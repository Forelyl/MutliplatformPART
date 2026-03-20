package ua.kpi.ipze.part.router

import kotlinx.serialization.Serializable

@Serializable
data object LoginPageData

@Serializable
data object NewProjectPageData

@Serializable
data object GalleryPageData

@Serializable
data class EditorPageData(
    val historyLength: Int,
    val id: Long
)