package gr.lykost.pms.support;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ContainerBootTest extends AbstractIntegrationTest {

    @Test
    void applicationContextStartsAgainstMysqlContainer() {
        assertThat(MYSQL.isRunning()).isTrue();
        assertThat(mvc).isNotNull();
    }
}
