package florinl.licenta.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DailyMarketDataTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static DailyMarketData getDailyMarketDataSample1() {
        return new DailyMarketData().id(1L).symbol("symbol1");
    }

    public static DailyMarketData getDailyMarketDataSample2() {
        return new DailyMarketData().id(2L).symbol("symbol2");
    }

    public static DailyMarketData getDailyMarketDataRandomSampleGenerator() {
        return new DailyMarketData().id(longCount.incrementAndGet()).symbol(UUID.randomUUID().toString());
    }
}
