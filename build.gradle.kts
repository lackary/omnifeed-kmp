// Create a readable version number variable in the root directory
// The default value is "0.0.1", the actual version number will be set by the CI/CD process
val versionFilename = "VERSION.txt"
fun getVersionFromFile(): String {
    val versionFile = File(versionFilename)
    return if (versionFile.exists()) {
        versionFile.readText().trim()
    } else {
        "0.0.1"
    }
}

fun getGitVersion(): String {
    return try {
        // 執行 git describe --tags 來獲取最近的 tag (例如 v1.0.0 或 v1.0.0-2-gda23...)
        val process = ProcessBuilder("git", "describe", "--tags").start()
        val version = process.inputStream.bufferedReader().readText().trim()

        process.waitFor()

        // 檢查執行結果：必須 exit code 為 0 且有內容
        if (process.exitValue() == 0 && version.isNotEmpty()) {
            // 移除開頭的 'v' (如果有的話)，例如 v1.0.0 -> 1.0.0
            version.removePrefix("v")
        } else {
            println("version is empty")
            "" // 如果有 git 但沒 tag
        }
    } catch (e: Exception) {
        println("getGitVersion exception: ${e.message}")
        "" // 如果沒有 git 環境 (例如 CI 某些階段或單純下載 zip)
    }
}

val projectVersion: String by lazy {
    // 1. 優先嘗試從 Gradle Property 獲取 (由 Semantic com.google.firebase.appdistribution.gradle.models.uploadstatus.Release 傳入)
    val pNewVersion = project.providers.gradleProperty("newVersion").orNull

    // 2. 如果沒有 Property，再讀檔或讀 Git (本地開發用)
    // 注意：VERSION.txt 可能包含 +88，所以我們需要處理它
    val rawVersion = pNewVersion ?: getGitVersion().ifEmpty { getVersionFromFile() }

    // 判斷是否為 CI 環境
    val isCi = System.getenv("CI") == "true"

    if (isCi) {
        // 🟢 關鍵邏輯：在 CI 發布 Artifact 時，強制切除 '+' 後面的 Build Metadata
        // 這樣 Maven 發布出去的版本就是乾淨的 1.0.0，而不是 1.0.0+88
        rawVersion.substringBefore("+")
    } else {
        // 本地環境保留原始邏輯，方便 Debug
        rawVersion
    }
}

plugins {
    //trick: for the same plugin versions in all sub-modules
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose.hotReload) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.kotlin.compose.compiler) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.devtool.ksp) apply false
    alias(libs.plugins.gms.google.services) apply false
    alias(libs.plugins.kotlin.native.cocoapods) apply false
}

subprojects {
    //trick: for the same plugin versions in all sub-modules
    group = "io.lackstudio.omnifeed"
    version = projectVersion
}


// Inherit from DefaultTask or a more suitable abstract class
// Use @get:Input to mark these properties as task inputs
abstract class SetBuildVersionTask : DefaultTask() {

    // Use Property<String> to hold the version and build numbers, ensuring configuration cache compatibility
    @get:Input
    abstract val newVersion: Property<String>

    @get:Input
    abstract val buildNumber: Property<String>

    @get:OutputFile
    val versionFile = project.layout.projectDirectory.file(versionFilename)

    // Use @TaskAction to annotate the task's execution logic
    @TaskAction
    fun execute() {
        // Use .get() to retrieve the actual String value.
        val version = newVersion.get()
        val build = buildNumber.get()

        logger.lifecycle(">> newVersion (Semantic): $version")
        logger.lifecycle(">> buildNumber (CI): $build")

        // 使用 '+' 連接 Build Number (SemVer 標準)
        // 這樣 SDK 內部讀取 VERSION.txt 時能看到 1.0.0+88
        val internalVersion = if (build.isNotEmpty()) {
            "$version+$build"
        } else {
            version
        }

        logger.lifecycle(">> write INTERNAL version to ${versionFile.asFile}")
        versionFile.asFile.writeText(internalVersion)

        logger.lifecycle(">> Successfully set $versionFilename to: $internalVersion")
    }
}

// Register and configure the new task
tasks.register<SetBuildVersionTask>("setBuildVersion") {
    // Use providers.gradleProperty to safely get -P parameters
    // If the property doesn't exist, it returns an unset Property.
    val pNewVersion = project.providers.gradleProperty("newVersion")
    val pBuildNumber = project.providers.gradleProperty("buildNumber")

    // Use .set() to configure task properties.
    newVersion.set(pNewVersion)
    buildNumber.set(pBuildNumber)

    // Configure group and description
    group = "versioning"
    description = "Writes the project version and build number to $versionFilename."
}
