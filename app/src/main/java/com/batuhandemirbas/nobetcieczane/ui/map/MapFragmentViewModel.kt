package com.batuhandemirbas.nobetcieczane.ui.map

import androidx.lifecycle.ViewModel
import com.batuhandemirbas.nobetcieczane.util.Constants
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MapUiState(
    val lat: Double? = null,
    val lon: Double? = null,
)

class MapViewModel : ViewModel() {

    val user = Constants.user
    val currentLocation = Point(user.Lat?:0.0, user.Lon?:0.0)

    // Expose screen UI state
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    // Handle business logic
    fun getUserLocation() {
        _uiState.update { currentState ->
            currentState.copy(
                lat = user.Lat,
                lon = user.Lon
            )
        }
    }
}