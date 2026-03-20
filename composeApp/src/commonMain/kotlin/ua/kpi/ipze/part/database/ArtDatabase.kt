package ua.kpi.ipze.part.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ua.kpi.ipze.part.database.converters.Converters
import ua.kpi.ipze.part.database.dao.LayerDao
import ua.kpi.ipze.part.database.dao.ProjectDao
import ua.kpi.ipze.part.database.types.Layer
import ua.kpi.ipze.part.database.types.Project

@Database(
    entities = [Project::class, Layer::class],
    version = 6,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ArtDatabase : RoomDatabase() {
    abstract val layerDao: LayerDao
    abstract val projectDao: ProjectDao
}