package ua.kpi.ipze.part.utils.filesystem

import java.io.File

private object ApplicationFolderMarker

val ApplicationFolder by lazy {
    runCatching {
        File(
            ApplicationFolderMarker::class.java.protectionDomain.codeSource.location
                .toURI()
        ).parentFile
    }.getOrElse {
        val appName =
            ApplicationFolderMarker::class.java.packageName.split(".").take(4).joinToString("-")
        File(System.getProperty("java.io.tmpdir"), appName)
    }
}