plugins {
	scala
}

group = "org.dzmauchy"
version = "0.1"

repositories {
	mavenCentral()
}

dependencies {
	implementation(group = "org.scala-lang", name = "scala3-library_3", version = "3.3.1")
	testImplementation(group = "org.scalatestplus", name = "junit-5-10_3", version = "3.2.17.0")
	testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-params", version = "5.10.1")
	testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = "5.10.1")
	testRuntimeOnly(group = "org.junit.platform", name = "junit-platform-launcher", version = "1.10.1")
}

tasks.named<Test>("test") {
	useJUnitPlatform {
		includeEngines("scalatest")
	}
	testLogging {
		events("passed", "skipped", "failed")
	}
}