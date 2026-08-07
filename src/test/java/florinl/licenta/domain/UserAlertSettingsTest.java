package florinl.licenta.domain;

import static florinl.licenta.domain.UserAlertSettingsTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import florinl.licenta.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserAlertSettingsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserAlertSettings.class);
        UserAlertSettings userAlertSettings1 = getUserAlertSettingsSample1();
        UserAlertSettings userAlertSettings2 = new UserAlertSettings();
        assertThat(userAlertSettings1).isNotEqualTo(userAlertSettings2);

        userAlertSettings2.setId(userAlertSettings1.getId());
        assertThat(userAlertSettings1).isEqualTo(userAlertSettings2);

        userAlertSettings2 = getUserAlertSettingsSample2();
        assertThat(userAlertSettings1).isNotEqualTo(userAlertSettings2);
    }
}
