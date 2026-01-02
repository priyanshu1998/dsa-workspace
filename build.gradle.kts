plugins {
    id("java")
    id("com.diffplug.spotless").version("8.1.0")
}

group = "dev.priyanshu"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

spotless {
    java {
        // Use the default importOrder configuration
        importOrder()

        removeUnusedImports()
        forbidWildcardImports()
        forbidModuleImports()

        // Cleanthat will refactor your code, but it may break your style: apply it before your formatter
        cleanthat()          // has its own section below

        // Choose one of these formatters.
        googleJavaFormat()   // has its own section below

        formatAnnotations()  // fixes formatting of type annotations, see below
    }
}