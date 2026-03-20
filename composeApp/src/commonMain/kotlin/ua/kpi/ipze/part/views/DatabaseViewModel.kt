package ua.kpi.ipze.part.views

import androidx.lifecycle.ViewModel
import androidx.room.immediateTransaction
import androidx.room.useWriterConnection
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import ua.kpi.ipze.part.database.ArtDatabase
import ua.kpi.ipze.part.database.types.Layer
import ua.kpi.ipze.part.database.types.LayersList
import ua.kpi.ipze.part.database.types.Project
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.max

val Tag = DatabaseViewModel::class.simpleName ?: ""

class DatabaseViewModel : ViewModel() {

    private val initialized = AtomicBoolean(false)

    fun initialize(dao: ArtDatabase) {
        if (!initialized.compareAndSet(false, true)) {
            Logger.e(Tag, message = { "Got second init" })
            return
        }
        this.artDao = dao
    }

    lateinit var artDao: ArtDatabase

    // -------------------
    // get

    suspend fun getAllProjectsCollected(): List<Project> {
        return artDao.projectDao.getAllProjects().first()
    }

    fun getAllProjects(): Flow<List<Project>> {
        return artDao.projectDao.getAllProjects()
    }

    suspend fun getProjectWithLayers(id: Long): DatabaseProjectWithLayers? {
        val project = artDao.projectDao.getProject(id)
        if (project == null) {
            Logger.e(Tag, message = { "Failed to get project with layer (id: $id)" })
            return null
        }

        val layers = artDao.layerDao.getLayers(project.layers.layersList).first()
        if (layers.size != project.layers.layersList.size) {
            Logger.e(Tag, message = { "Failed to get project with layer (id: $id)" })
            return null
        }

        return DatabaseProjectWithLayers(project, layers)
    }

    // -------------------
    // insert

    suspend fun saveProject(project: Project, layers: List<Layer>): Long? {
        return try {
            artDao.useWriterConnection { transactor ->
                transactor.immediateTransaction {
                    val epochTime = System.currentTimeMillis()

                    Logger.d(Tag) { "Current time of insert: ${java.util.Date(epochTime)}" }


                    val layerIds = layers.map { layer ->
                        // upsert returns new id or -1 (if updated) - layer.id contains existing id or 0
                        // (if new or first layer id db)
                        max(artDao.layerDao.upsertLayer(layer), layer.id)
                    }
                    val projectNew = project.copy(
                        layers = LayersList(layersList = layerIds),
                        lastModified = epochTime
                    )
                    Logger.d(Tag, message = { "Prepared project to insert" })
                    val a = artDao.projectDao.upsertProject(projectNew)
                    Logger.d(Tag, message = { "ario" })
                    a
                }
            }
        } catch (e: Throwable) {
            Logger.e(Tag) { "saveProject failed: $e" }
            null
        }
    }


    // -------------------
    // delete
    suspend fun deleteProject(id: Long) {
        artDao.useWriterConnection { transactor ->
            transactor.immediateTransaction {
                val oldValues = getProjectWithLayers(id)
                if (oldValues == null) {
                    Logger.d(Tag, message = { "Failed to delete project (id: $id)" })
                    return@immediateTransaction
                }
                oldValues.layers.map { layer -> artDao.layerDao.deleteLayer(layer) }
                artDao.projectDao.deleteProject(oldValues.project)
            }
        }
    }


    // -------------------
    // update

    suspend fun renameProject(id: Long, newName: String) {
        val project = artDao.projectDao.getProject(id)
        if (project == null) {
            Logger.e(Tag, message = { "Failed to rename project (id: $id, newName: $newName)" })
            return
        }
        artDao.projectDao.updateProject(project.copy(name = newName))

    }
}

data class DatabaseProjectWithLayers(
    var project: Project,
    var layers: List<Layer>
)