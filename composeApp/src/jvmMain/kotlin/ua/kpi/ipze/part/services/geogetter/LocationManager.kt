package ua.kpi.ipze.part.services.geogetter

actual class LocationManager {
    actual fun getLocation(onResult: (String) -> Unit) {
        onResult("")
    }
}