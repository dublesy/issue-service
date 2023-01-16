dependencies {
    //implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    runtimeOnly("io.netty:netty-resolver-dns-native-macos")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.11.3")

    kapt("org.springframework.boot:spring-boot-configuration-processor")

    implementation("at.favre.lib:bcrypt:0.9.0")
    runtimeOnly("io.r2dbc:r2dbc-h2")





}
