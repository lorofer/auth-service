import com.google.protobuf.gradle.*

plugins {
	java
	id("org.springframework.boot") version "4.1.1"
	id("io.spring.dependency-management") version "1.1.7"
	id("com.google.protobuf") version "0.9.6"
}

group = "be4"
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
	// postgresql
	runtimeOnly("org.postgresql:postgresql")

	// spring
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-grpc-server")

	// grpc
	runtimeOnly("io.grpc:grpc-netty-shaded:1.84.0")
	implementation("io.grpc:grpc-protobuf:1.84.0")
	implementation("io.grpc:grpc-stub:1.84.0")

	// jwt
	implementation("io.jsonwebtoken:jjwt-api:0.13.0")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.13.0")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.13.0")

	// lombok
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
}

sourceSets {
	main {
		proto {
			srcDir("../proto")
		}
	}
}

protobuf {
	plugins {
		id("grpc")
	}
}