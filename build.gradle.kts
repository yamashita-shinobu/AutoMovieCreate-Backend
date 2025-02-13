import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.3.2"
	id("io.spring.dependency-management") version "1.1.6"
	id("org.jetbrains.kotlin.kapt") version "1.2.71"
	kotlin("jvm") version "1.9.24"
	kotlin("plugin.spring") version "1.9.24"
}

group = "com.example"
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
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.squareup.okhttp3:okhttp:4.9.2")
	implementation("com.squareup.okhttp3:logging-interceptor:4.9.2")
	implementation("com.google.code.gson:gson:2.8.9")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("mysql:mysql-connector-java:8.0.32")
	runtimeOnly("com.mysql:mysql-connector-j")
	implementation("org.seasar.doma.boot:doma-spring-boot-starter:1.7.0")
	kapt ("org.seasar.doma:doma:2.29.0")
	implementation("org.seasar.doma:doma:2.29.0")
	//implementation("javax.sound.sampled:sampled:1.0.0")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation(files("libs/opencv-4100.jar"))
	implementation("org.springframework.boot:spring-boot-starter-logging")
//	compileOnly("org.projectlombok:lombok")
//	kapt ("org.projectlombok:lombok")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
	implementation("com.worksap.nlp:sudachi:0.5.3")
	implementation("org.json:json:20210307")  // 最新のバージョンを確認してください
	implementation("org.jsoup:jsoup:1.15.4") // 最新版を指定
 	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.0")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

val compileKotlin: KotlinCompile by tasks

kapt {
	arguments {
		arg("doma.resources.dir", compileKotlin.destinationDirectory.get())
	}
}

tasks.register("copyDomaResources",Sync::class){
	from("src/main/resources")
	into(compileKotlin.destinationDirectory.get())
	include("doma.compile.config")
	include("META-INF/**/*.sql")
	include("META-INF/**/*.script")
}

tasks.withType<KotlinCompile> {
	dependsOn(tasks.getByName("copyDomaResources"))
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "21"
	}
}


tasks.withType<Test> {
	useJUnitPlatform()
}
