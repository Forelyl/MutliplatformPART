package ua.kpi.ipze.part.services.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import ua.kpi.ipze.part.utils.filesystem.ApplicationFolder
import java.io.File

/**
 * @param fullFileName example: "file.preferences_pb"
 */
actual fun constructDataStore(fullFileName: String): DataStore<Preferences> =
    constructDataStoreFromPath(
        File(ApplicationFolder, fullFileName).absolutePath.toPath()
    )
