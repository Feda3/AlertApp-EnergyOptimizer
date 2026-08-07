package florinl.licenta.domain;

import static florinl.licenta.domain.UserAlertSettingsTestSamples.*;
import static florinl.licenta.domain.UserSignalTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import florinl.licenta.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserSignalTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserSignal.class);
        UserSignal userSignal1 = getUserSignalSample1();
        UserSignal userSignal2 = new UserSignal();
        assertThat(userSignal1).isNotEqualTo(userSignal2);

        userSignal2.setId(userSignal1.getId());
        assertThat(userSignal1).isEqualTo(userSignal2);

        userSignal2 = getUserSignalSample2();
        assertThat(userSignal1).isNotEqualTo(userSignal2);
    }

    @Test
    void settingTest() {
        UserSignal userSignal = getUserSignalRandomSampleGenerator();
        UserAlertSettings userAlertSettingsBack = getUserAlertSettingsRandomSampleGenerator();

        userSignal.setSetting(userAlertSettingsBack);
        assertThat(userSignal.getSetting()).isEqualTo(userAlertSettingsBack);

        userSignal.setting(null);
        assertThat(userSignal.getSetting()).isNull();
    }
}
