package florinl.licenta.domain;

import static florinl.licenta.domain.DailyMarketDataTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import florinl.licenta.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DailyMarketDataTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DailyMarketData.class);
        DailyMarketData dailyMarketData1 = getDailyMarketDataSample1();
        DailyMarketData dailyMarketData2 = new DailyMarketData();
        assertThat(dailyMarketData1).isNotEqualTo(dailyMarketData2);

        dailyMarketData2.setId(dailyMarketData1.getId());
        assertThat(dailyMarketData1).isEqualTo(dailyMarketData2);

        dailyMarketData2 = getDailyMarketDataSample2();
        assertThat(dailyMarketData1).isNotEqualTo(dailyMarketData2);
    }
}
