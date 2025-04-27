plugins {
	kotlin("jvm") version Version.Plugin.KOTLIN
	kotlin("plugin.spring") version Version.Plugin.KOTLIN
	kotlin("plugin.jpa") version Version.Plugin.KOTLIN
	kotlin("plugin.noarg") version Version.Plugin.KOTLIN
	id("org.springframework.boot") version Version.Plugin.SPRING_BOOT
	id("io.spring.dependency-management") version Version.Plugin.SPRING_DEPENDENCY_MANAGEMENT
}

group = "com.sync"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
//	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j")

	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${Version.SPRING_DOC}")
	implementation("net.logstash.logback:logstash-logback-encoder:${Version.LOGSTASH}")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

tasks {
	compileKotlin {
		kotlinOptions {
			freeCompilerArgs += "-Xjsr305-strict"
			jvmTarget = "21"
		}
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.register("localBootRun") {
	group = "application"
	doFirst {
		tasks.bootRun {
			args("--spring.profiles.active=local")
		}
	}
	finalizedBy("bootRun")
}