package ua.kpi.ipze.part.database

import androidx.room.RoomDatabase
import ua.kpi.ipze.part.database.migrations.migration_4_5

expect fun getArtDatabaseBuilder(): RoomDatabase.Builder<ArtDatabase>

fun makeArtDatabase(): ArtDatabase = getArtDatabaseBuilder().addMigrations(migration_4_5)
    .fallbackToDestructiveMigration(dropAllTables = true).build()

object ArtDatabaseProvider {
    val database: ArtDatabase by lazy { makeArtDatabase() }
}

