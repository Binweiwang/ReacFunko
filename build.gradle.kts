plugins {
    id("java")
}
group = "org.example"
version = "1.0-SNAPSHOT"
repositories {
    mavenCentral()
}
dependencies {
    // Project Reactor
    implementation("io.projectreactor:reactor-core:3.5.10")
    // R2DBC
    implementation("io.r2dbc:r2dbc-h2:1.0.0.RELEASE")
    implementation("io.r2dbc:r2dbc-pool:1.0.0.RELEASE")
    // Para nuestros test
    testImplementation(platform("org.junit:junit-bom:5.9.2"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    // Logger
    implementation("ch.qos.logback:logback-classic:1.4.11")
    // Lombook para generar código, poner todo esto para que funcione
    implementation("org.projectlombok:lombok:1.18.28")
    testImplementation("org.projectlombok:lombok:1.18.28")
    annotationProcessor("org.projectlombok:lombok:1.18.28")
    // Ibatis lo usaremos para leer los scripts SQL desde archivos
    implementation("org.mybatis:mybatis:3.5.13")
    // H2, solo usa una
    implementation("com.h2database:h2:2.1.214")
    // https://mvnrepository.com/artifact/com.google.code.gson/gson
    implementation("com.google.code.gson:gson:2.10.1")
    // Mockito para nuestros test con JUnit 5
    testImplementation("org.mockito:mockito-junit-jupiter:5.5.0")
    testImplementation("org.mockito:mockito-core:5.5.0")
}

tasks.test {
    useJUnitPlatform()
}