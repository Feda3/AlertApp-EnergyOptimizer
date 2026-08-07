package florinl.licenta.service;

import florinl.licenta.domain.DailyMarketData;
import florinl.licenta.domain.UserAlertSettings;
import florinl.licenta.domain.UserSignal;
import florinl.licenta.repository.DailyMarketDataRepository;
import florinl.licenta.repository.UserAlertSettingsRepository;
import florinl.licenta.repository.UserSignalRepository;
import java.io.ByteArrayInputStream;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap; // <--- NEW IMPORT
import java.util.List;
import java.util.Map; // <--- NEW IMPORT
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

@Service
@Transactional
public class AlertProcessingService {

    private final Logger log = LoggerFactory.getLogger(AlertProcessingService.class);

    private final DailyMarketDataRepository dailyMarketDataRepository;
    private final UserAlertSettingsRepository userAlertSettingsRepository;
    private final UserSignalRepository userSignalRepository;

    public AlertProcessingService(
        DailyMarketDataRepository dailyMarketDataRepository,
        UserAlertSettingsRepository userAlertSettingsRepository,
        UserSignalRepository userSignalRepository
    ) {
        this.dailyMarketDataRepository = dailyMarketDataRepository;
        this.userAlertSettingsRepository = userAlertSettingsRepository;
        this.userSignalRepository = userSignalRepository;
    }

    // @Scheduled(fixedRate = 60000)
    @Scheduled(cron = "0 0 10 * * ?")
    public void processDailyMarketData() {
        log.info("Starting daily market data fetch and alert processing...");

        List<DailyMarketData> todayData = fetchDatasheetFromWeb();
        todayData = dailyMarketDataRepository.saveAll(todayData);

        List<UserAlertSettings> allSettings = userAlertSettingsRepository.findAll();

        for (UserAlertSettings setting : allSettings) {
            if (Boolean.FALSE.equals(setting.getIsActive())) {
                log.info("Rule for {} is paused. Skipping.", setting.getSymbol());
                continue;
            }

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime startTime = (setting.getStartTime() != null && !setting.getStartTime().isEmpty())
                ? LocalTime.parse(setting.getStartTime(), timeFormatter)
                : null;
            LocalTime endTime = (setting.getEndTime() != null && !setting.getEndTime().isEmpty())
                ? LocalTime.parse(setting.getEndTime(), timeFormatter)
                : null;

            List<DailyMarketData> triggeredBlocks = todayData
                .stream()
                .filter(data -> data.getSymbol().startsWith(setting.getSymbol()))
                .filter(data -> {
                    String timeString = data.getSymbol().replace(setting.getSymbol(), "");

                    try {
                        LocalTime blockTime = LocalTime.parse(timeString, timeFormatter);
                        if (startTime != null && blockTime.isBefore(startTime)) return false;
                        if (endTime != null && blockTime.isAfter(endTime)) return false;
                    } catch (Exception e) {}

                    if (Boolean.TRUE.equals(setting.getTriggerIfGreater())) {
                        return data.getMetricValue() > setting.getThreshold();
                    } else {
                        return data.getMetricValue() < setting.getThreshold();
                    }
                })
                .sorted(Comparator.comparing(DailyMarketData::getSymbol))
                .toList();

            if (triggeredBlocks.isEmpty()) {
                continue;
            }

            LocalTime blockStart = null;
            LocalTime blockEnd = null;

            for (DailyMarketData block : triggeredBlocks) {
                String timeString = block.getSymbol().replace(setting.getSymbol(), "");
                LocalTime currentTime = LocalTime.parse(timeString, timeFormatter);

                if (blockStart == null) {
                    blockStart = currentTime;
                    blockEnd = currentTime;
                } else {
                    if (blockEnd.plusMinutes(15).equals(currentTime)) {
                        blockEnd = currentTime;
                    } else {
                        checkDurationAndSave(setting, blockStart, blockEnd, timeFormatter);
                        blockStart = currentTime;
                        blockEnd = currentTime;
                    }
                }
            }

            if (blockStart != null) {
                checkDurationAndSave(setting, blockStart, blockEnd, timeFormatter);
            }
        }
        log.info("Finished processing daily alerts.");
    }

    private void checkDurationAndSave(UserAlertSettings setting, LocalTime start, LocalTime end, DateTimeFormatter formatter) {
        long totalMinutes = Duration.between(start, end.plusMinutes(15)).toMinutes();

        if (setting.getMinDurationMinutes() == null || totalMinutes >= setting.getMinDurationMinutes()) {
            saveGroupedSignal(setting, start, end, formatter);
        } else {
            log.info("Block {} - {} discarded. Only {} mins long.", start.format(formatter), end.format(formatter), totalMinutes);
        }
    }

    private List<DailyMarketData> fetchDatasheetFromWeb() {
        List<DailyMarketData> results = new ArrayList<>();

        try {
            ZoneId bucharestZone = ZoneId.of("Europe/Bucharest");
            ZonedDateTime nowBucharest = ZonedDateTime.now(bucharestZone);

            ZonedDateTime startOfDayUtc = nowBucharest.toLocalDate().atStartOfDay(bucharestZone).withZoneSameInstant(ZoneOffset.UTC);
            ZonedDateTime endOfDayUtc = startOfDayUtc.plusDays(1);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
            String periodStart = startOfDayUtc.format(formatter);
            String periodEnd = endOfDayUtc.format(formatter);

            String apiToken = "ff7fd400-5a24-4b7c-a1dd-a20930c28062";
            String domainRO = "10YRO-TEL------P";
            String url = String.format(
                "https://web-api.tp.entsoe.eu/api?securityToken=%s&documentType=A44&in_Domain=%s&out_Domain=%s&periodStart=%s&periodEnd=%s",
                apiToken,
                domainRO,
                domainRO,
                periodStart,
                periodEnd
            );

            log.info("Fetching Day-Ahead Prices for RO from ENTSO-E...");
            RestTemplate restTemplate = new RestTemplate();
            String xmlResponse = restTemplate.getForObject(url, String.class);

            List<Double> marketPrices = parseEntsoeXmlForHourlyPrices(xmlResponse);
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            // NEW: Use a LinkedHashMap to instantly crush any duplicate ENTSO-E times
            Map<String, DailyMarketData> uniqueBlocks = new LinkedHashMap<>();

            for (int i = 0; i < marketPrices.size(); i++) {
                LocalTime time = LocalTime.MIDNIGHT.plusMinutes(i * 15);
                String timeString = time.format(timeFormatter);
                String symbol = "RO_POWER_" + timeString;

                if (!uniqueBlocks.containsKey(symbol)) {
                    DailyMarketData timeBlockData = new DailyMarketData();
                    timeBlockData.setFetchDate(LocalDate.now(bucharestZone));
                    timeBlockData.setSymbol(symbol);
                    timeBlockData.setMetricValue(marketPrices.get(i));
                    uniqueBlocks.put(symbol, timeBlockData);
                }
            }
            results.addAll(uniqueBlocks.values());

            log.info("Successfully saved {} unique 15-minute prices for RO.", results.size());
        } catch (Exception e) {
            log.error("Failed to fetch ENTSO-E data: {}", e.getMessage());
        }

        return results;
    }

    private List<Double> parseEntsoeXmlForHourlyPrices(String xml) throws Exception {
        List<Double> prices = new ArrayList<>();
        if (xml == null || xml.isEmpty()) return prices;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes("UTF-8"));
        Document doc = builder.parse(input);

        NodeList priceNodes = doc.getElementsByTagName("price.amount");

        for (int i = 0; i < priceNodes.getLength(); i++) {
            prices.add(Double.parseDouble(priceNodes.item(i).getTextContent()));
        }

        return prices;
    }

    private void saveGroupedSignal(UserAlertSettings setting, LocalTime start, LocalTime end, DateTimeFormatter formatter) {
        UserSignal groupedSignal = new UserSignal();
        groupedSignal.setSignalDate(LocalDate.now());
        groupedSignal.setAction(setting.getAction());
        groupedSignal.setSetting(setting);
        groupedSignal.setUser(setting.getUser());

        // NEW: Always add 15 minutes to the display end time so it covers the full block perfectly
        LocalTime displayEnd = end.plusMinutes(15);
        String timeDisplay = start.format(formatter) + " - " + displayEnd.format(formatter);

        String message = String.format("Alert: Price met your threshold of %s EUR. Time period: %s", setting.getThreshold(), timeDisplay);

        groupedSignal.setSummaryMessage(message);
        userSignalRepository.save(groupedSignal);

        log.info("Saved GROUPED signal for User: {} | Time: {}", setting.getUser().getLogin(), timeDisplay);
    }
}
