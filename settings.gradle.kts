pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // 添加高德地图仓库
        //maven { url = uri("https://oss.sonatype.org/content/repositories/releases/") }
        //maven { url = uri("https://maven.aliyun.com/repository/public/") }
    }
}

rootProject.name = "MapScenes"
include(":app")

