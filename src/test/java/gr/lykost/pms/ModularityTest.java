package gr.lykost.pms;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModularityTest {

    @Test
    void modulesAreAcyclicAndRespectTheirBoundaries() {
        ApplicationModules.of(PmsApplication.class).verify();
    }
}
