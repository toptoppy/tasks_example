import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.2.1"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.21"
	kotlin("plugin.spring") version "1.9.21"
	kotlin("plugin.jpa") version "1.9.21"
}

group = "com.toptoppy"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	// swagger ui
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
	// for logger
	implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
	implementation("ch.qos.logback:logback-classic:1.4.7")
	// postgres driver
	runtimeOnly("org.postgresql:postgresql:42.5.4")
	// test
	testImplementation("com.h2database:h2:2.1.214")
	testImplementation("org.springframework.boot:spring-boot-starter-test:3.1.0")
	testImplementation("com.ninja-squad:springmockk:4.0.2") // mockk
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
