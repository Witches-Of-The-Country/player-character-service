package com.dungeoneer;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@Disabled("Desabilitado temporariamente por falta de lógica de negócio")
class BackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
