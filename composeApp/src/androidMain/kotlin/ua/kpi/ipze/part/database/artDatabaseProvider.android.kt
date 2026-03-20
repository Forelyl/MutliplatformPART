package ua.kpi.ipze.part.database

import androidx.room.Room
import androidx.room.RoomDatabase
import ua.kpi.ipze.part.providers.GlobalApplicationContext

actual fun getArtDatabaseBuilder(): RoomDatabase.Builder<ArtDatabase> {
    val appContext = GlobalApplicationContext.context.applicationContext
    val dbFile = appContext.getDatabasePath("art.db")
    return Room.databaseBuilder<ArtDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}