package com.yourname.mapscenes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourname.mapscenes.model.Scene
import com.yourname.mapscenes.model.SceneType
import com.yourname.mapscenes.model.UserMarker
import com.yourname.mapscenes.model.MarkerType
import com.yourname.mapscenes.repository.SceneRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MapViewModel(private val sceneRepository: SceneRepository) : ViewModel() {

    private val _scenes = MutableStateFlow<List<Scene>>(emptyList())
    val scenes: StateFlow<List<Scene>> = _scenes.asStateFlow()

    private val _selectedScene = MutableStateFlow<Scene?>(null)
    val selectedScene: StateFlow<Scene?> = _selectedScene.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filteredScenes = MutableStateFlow<List<Scene>>(emptyList())
    val filteredScenes: StateFlow<List<Scene>> = _filteredScenes.asStateFlow()

    private val _selectedType = MutableStateFlow<SceneType?>(null)
    val selectedType: StateFlow<SceneType?> = _selectedType.asStateFlow()

    private val _userMarkers = MutableStateFlow<List<UserMarker>>(emptyList())
    val userMarkers: StateFlow<List<UserMarker>> = _userMarkers.asStateFlow()

    private val _selectedUserMarker = MutableStateFlow<UserMarker?>(null)
    val selectedUserMarker: StateFlow<UserMarker?> = _selectedUserMarker.asStateFlow()

    private val _isAddingMarker = MutableStateFlow(false)
    val isAddingMarker: StateFlow<Boolean> = _isAddingMarker.asStateFlow()

    private val _tempMarkerPosition = MutableStateFlow<com.amap.api.maps.model.LatLng?>(null)
    val tempMarkerPosition: StateFlow<com.amap.api.maps.model.LatLng?> = _tempMarkerPosition.asStateFlow()

    init {
        loadScenes()
        loadUserMarkers()
    }

    // 用户标记相关方法
    fun startAddingMarker() {
        _isAddingMarker.value = true
    }

    fun cancelAddingMarker() {
        _isAddingMarker.value = false
        _tempMarkerPosition.value = null
    }

    fun setTempMarkerPosition(latLng: com.amap.api.maps.model.LatLng) {
        _tempMarkerPosition.value = latLng
    }

    fun addUserMarker(name: String, description: String, markerType: MarkerType, tags: List<String> = emptyList()) {
        val position = _tempMarkerPosition.value
        if (position != null) {
            viewModelScope.launch {
                val marker = UserMarker(
                    name = name,
                    description = description,
                    latitude = position.latitude,
                    longitude = position.longitude,
                    markerType = markerType,
                    tags = tags
                )
                sceneRepository.addUserMarker(marker)
                _isAddingMarker.value = false
                _tempMarkerPosition.value = null
                loadUserMarkers()
            }
        }
    }

    fun selectUserMarker(marker: UserMarker) {
        _selectedUserMarker.value = marker
    }

    fun clearSelectedUserMarker() {
        _selectedUserMarker.value = null
    }

    fun deleteUserMarker(markerId: String) {
        viewModelScope.launch {
            sceneRepository.deleteUserMarker(markerId)
            loadUserMarkers()
            _selectedUserMarker.value = null
        }
    }

    private fun loadUserMarkers() {
        viewModelScope.launch {
            try {
                val markers = sceneRepository.getUserMarkers()
                _userMarkers.value = markers
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadScenes() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val scenesList = sceneRepository.getScenes()
                _scenes.value = scenesList
                _filteredScenes.value = scenesList
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectScene(scene: Scene) {
        _selectedScene.value = scene
    }

    fun clearSelectedScene() {
        _selectedScene.value = null
    }

    fun addScene(scene: Scene) {
        viewModelScope.launch {
            sceneRepository.addScene(scene)
            loadScenes()
        }
    }

    fun searchScenes(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            if (query.isBlank()) {
                _filteredScenes.value = _scenes.value
            } else {
                val results = sceneRepository.searchScenes(query)
                _filteredScenes.value = results
            }
        }
    }

    fun filterByType(type: SceneType?) {
        _selectedType.value = type
        viewModelScope.launch {
            if (type == null) {
                _filteredScenes.value = _scenes.value
            } else {
                // 获取 Flow 的第一个值
                val results = sceneRepository.getScenesByType(type).first()
                _filteredScenes.value = results
            }
        }
    }

    fun toggleFavorite(sceneId: String) {
        viewModelScope.launch {
            sceneRepository.toggleFavorite(sceneId)
            loadScenes()
        }
    }
}