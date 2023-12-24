package com.toptoppy.kotlinSpringBoot

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class KotlinSpringBootApplicationTests {

	@Test
	fun contextLoads() {
	}

}
