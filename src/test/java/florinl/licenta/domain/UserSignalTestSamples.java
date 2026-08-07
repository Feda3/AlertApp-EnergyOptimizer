package florinl.licenta.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class UserSignalTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static UserSignal getUserSignalSample1() {
        return new UserSignal().id(1L).summaryMessage("summaryMessage1");
    }

    public static UserSignal getUserSignalSample2() {
        return new UserSignal().id(2L).summaryMessage("summaryMessage2");
    }

    public static UserSignal getUserSignalRandomSampleGenerator() {
        return new UserSignal().id(longCount.incrementAndGet()).summaryMessage(UUID.randomUUID().toString());
    }
}
