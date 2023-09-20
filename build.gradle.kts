plugins {
	java
	jacoco
	id("org.springframework.boot") version "3.0.6"
	id("io.spring.dependency-management") version "1.1.0"
	id("org.jsonschema2pojo") version "1.2.1"
}

group = "faang.school"
version = "1.0"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	/**
	 * Spring boot starters
	 */
	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.cloud:spring-cloud-starter-openfeign:4.0.2")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

	/**
	 * Database
	 */
	implementation("org.liquibase:liquibase-core")
	implementation("redis.clients:jedis:4.3.2")
	runtimeOnly("org.postgresql:postgresql")

	/**
	 * Amazon S3
	 */
	implementation("com.amazonaws:aws-java-sdk-s3:1.12.481")

	/**
	 * Utils & Logging
	 */
	implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
	implementation("org.slf4j:slf4j-api:2.0.5")
	implementation("ch.qos.logback:logback-classic:1.4.6")
	implementation("org.projectlombok:lombok:1.18.26")
	annotationProcessor("org.projectlombok:lombok:1.18.26")
	implementation("org.mapstruct:mapstruct:1.5.3.Final")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.3.Final")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.2")
	implementation ("com.google.code.gson:gson:2.8.8")
	implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.13.0")
	implementation("net.coobird:thumbnailator:0.4.20")

	/**
	 * Test containers
	 */
	implementation(platform("org.testcontainers:testcontainers-bom:1.17.6"))
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:postgresql")
	testImplementation("com.redis.testcontainers:testcontainers-redis-junit-jupiter:1.4.6")

	/**
	 * Tests
	 */
	testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.2")
	testImplementation("org.assertj:assertj-core:3.24.2")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	/**
	 * Google Calendar API
	 */
	implementation("com.google.api-client:google-api-client:2.0.0")
	implementation("com.google.oauth-client:google-oauth-client-jetty:1.34.1")
	implementation("com.google.apis:google-api-services-calendar:v3-rev20220715-2.0.0")
}

jsonSchema2Pojo {
	setSource(files("src/main/resources/json"))
	targetDirectory = file("${project.buildDir}/generated-sources/js2p")
	targetPackage = "com.json.student"
	setSourceType("jsonschema")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

val test by tasks.getting(Test::class) { testLogging.showStandardStreams = true }

tasks.bootJar {
	archiveFileName.set("service.jar")
}
tasks.test {
	finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}
tasks.jacocoTestReport {
	dependsOn(tasks.test) // tests are required to run before generating the report
}
jacoco {
	toolVersion = "0.8.9"
	reportsDirectory.set(layout.buildDirectory.dir("customJacocoReportDir"))
}
tasks.jacocoTestReport {
	reports {
		xml.required.set(false)
		csv.required.set(false)
		html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
	}
    classDirectories.setFrom(files(classDirectories.files.map {
        fileTree(it).apply {
            exclude("school/faang/user_service/entity/**",
                "school/faang/user_service/dto/**",
                "school/faang/user_service/commonMessages/**",
                "school/faang/user_service/config/**",
                "school/faang/user_service/filter/**",
                "school/faang/user_service/exception/**",
                "school/faang/user_service/client/**",
                "school/faang/user_service/model/**",
                "school/faang/user_service/repository/**",
                "school/faang/user_service/util/**",
                "school/faang/user_service/UserServiceApplication.class",
                "com/json/student/**",)
        }
    }))
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            element = "CLASS"
            excludes = listOf("school.faang.projectservice.entity.**",
                "school.faang.user_service.dto.**",
                "school.faang.user_service.commonMessages.**",
                "school.faang.user_service.config.**",
                "school.faang.user_service.filter.**",
                "school.faang.user_service.exception.**",
                "school.faang.user_service.model.**",
                "school.faang.user_service.client.**",
                "school.faang.school.user_service.repository.**",
                "school.faang.school.user_service.util.**",
                "school.faang.user_service.UserServiceApplication",
                "com.json.student.**")
            limit {
                minimum = "0.8".toBigDecimal()
            }
        }
    }
}

