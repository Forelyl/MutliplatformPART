package ua.kpi.ipze.part.services.geogetter

expect class LocationManager() {
    fun getLocation(onResult: (String) -> Unit)
}