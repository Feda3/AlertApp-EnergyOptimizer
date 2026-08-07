package florinl.licenta.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class UserAlertSettingsTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static UserAlertSettings getUserAlertSettingsSample1() {
        return new UserAlertSettings().id(1L).symbol("symbol1").startTime("startTime1").endTime("endTime1").minDurationMinutes(1);
    }

    public static UserAlertSettings getUserAlertSettingsSample2() {
        return new UserAlertSettings().id(2L).symbol("symbol2").startTime("startTime2").endTime("endTime2").minDurationMinutes(2);
    }

    public static UserAlertSettings getUserAlertSettingsRandomSampleGenerator() {
        return new UserAlertSettings()
            .id(longCount.incrementAndGet())
            .symbol(UUID.randomUUID().toString())
            .startTime(UUID.randomUUID().toString())
            .endTime(UUID.randomUUID().toString())
            .minDurationMinutes(intCount.incrementAndGet());
    }
}
