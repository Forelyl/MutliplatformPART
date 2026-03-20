package ua.kpi.ipze.part.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection


val migration_4_5 = object : Migration(4, 5) {
    override fun migrate(connection: SQLiteConnection) {
        connection.prepare("""DELETE FROM project WHERE layers LIKE '%-1%'""").use { it.step() }
    }
}