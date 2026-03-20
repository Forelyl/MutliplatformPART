package ua.kpi.ipze.part.database

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import java.io.File

actual fun getArtDatabaseBuilder(): RoomDatabase.Builder<ArtDatabase> {
    val currentJarSuperFolder = runCatching {
        File(
            ArtDatabase::class.java.protectionDomain.codeSource.location
                .toURI()
        ).parentFile
    }.getOrNull() ?: File(".")
    return Room.databaseBuilder<ArtDatabase>(
        name = File(currentJarSuperFolder, "art.db").absolutePath,
    ).setDriver(BundledSQLiteDriver())
}