package ua.kpi.ipze.part.services.geogetter.view

import androidx.lifecycle.ViewModel


import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ua.kpi.ipze.part.services.geogetter.LocationManager

class LocationViewModel : ViewModel() {
    private val locationManager = LocationManager()

    private val _location = MutableStateFlow<String?>(null)
    val location: StateFlow<String?> = _location.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun fetchLocation() {
        viewModelScope.launch {
            _isLoading.value = true

            locationManager.getLocation { result ->
                _location.value = result
                _isLoading.value = false
            }
        }
    }
}