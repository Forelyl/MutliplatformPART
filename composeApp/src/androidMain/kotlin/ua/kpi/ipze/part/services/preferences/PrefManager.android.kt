package ua.kpi.ipze.part.services.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import ua.kpi.ipze.part.providers.GlobalApplicationContext

/**
 * @param fullFileName example: "file.preferences_pb"
 */
actual fun constructDataStore(fullFileName: String): DataStore<Preferences> {
    return constructDataStoreFromPath(
        GlobalApplicationContext.context
            .filesDir
            .resolve(fullFileName)
            .absolutePath
            .toPath()
    )
}