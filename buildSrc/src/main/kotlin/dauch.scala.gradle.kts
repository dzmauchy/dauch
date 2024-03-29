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
	testImplementation(group = "org.scalatest", name = "scalatest-wordspec_3", version = "3.2.17")
	testImplementation(group = "org.scalatest", name = "scalatest-shouldmatchers_3", version = "3.2.17")
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

tasks.withType<ScalaCompile>().configureEach {
	scalaCompileOptions.apply {
		isForce = true
		isFailOnError = true
		isUnchecked = true
		additionalParameters = listOf(
			"-no-indent",
			"-release", "21"
		)

		forkOptions.apply {
			memoryMaximumSize = "2g"
		}
	}
}

scala {
	zincVersion = "1.9.6"
}