package com.yourname.mapscenes.ui.screens

import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.amap.api.maps.AMap
import com.amap.api.maps.MapView
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MarkerOptions
import com.yourname.mapscenes.model.Scene
import com.yourname.mapscenes.model.UserMarker
import com.yourname.mapscenes.ui.components.*
import com.yourname.mapscenes.viewmodel.MapViewModel
import androidx.compose.runtime.collectAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel,
    onSceneSelected: (Scene) -> Unit,
    onUserMarkerSelected: (UserMarker) -> Unit = {} // 添加用户标记选择回调
) {
    // 状态收集
    val scenes by viewModel.scenes.collectAsState()
    val filteredScenes by viewModel.filteredScenes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()

    // 用户标记相关状态
    val isAddingMarker by viewModel.isAddingMarker.collectAsState()
    val tempMarkerPosition by viewModel.tempMarkerPosition.collectAsState()
    val userMarkers by viewModel.userMarkers.collectAsState()

    var showAddMarkerDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("途景Map - 探索小众景点") },
                actions = {
                    // 添加标记按钮
                    IconButton(
                        onClick = {
                            viewModel.startAddingMarker()
                        }
                    ) {
                        Icon(Icons.Default.AddLocation, contentDescription = "添加标记")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 地图视图
            AMapView(
                modifier = Modifier.fillMaxSize(),
                scenes = filteredScenes,
                userMarkers = userMarkers,
                isAddingMarker = isAddingMarker,
                tempMarkerPosition = tempMarkerPosition,
                onSceneSelected = { scene ->
                    onSceneSelected(scene) // 景点点击跳转到详情页
                },
                onMapLongClick = { latLng ->
                    if (isAddingMarker) {
                        viewModel.setTempMarkerPosition(latLng)
                        showAddMarkerDialog = true
                    }
                },
                onUserMarkerSelected = { marker ->
                    onUserMarkerSelected(marker) // 用户标记点击也跳转
                }
            )

            // 搜索和筛选栏
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { viewModel.searchScenes(it) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                FilterChips(
                    selectedType = selectedType,
                    onTypeSelected = { viewModel.filterByType(it) }
                )
            }

            // 添加标记提示
            if (isAddingMarker) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(bottom = 100.dp)
                ) {
                    Card(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "长按地图选择位置",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // 加载指示器
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // 移除了 UserMarkerDetailSheet，因为现在都跳转到详情页面
        }
    }

    // 添加标记对话框
    if (showAddMarkerDialog) {
        AddMarkerDialog(
            onDismiss = {
                showAddMarkerDialog = false
                viewModel.cancelAddingMarker()
            },
            onConfirm = { name, description, type, tags ->
                viewModel.addUserMarker(name, description, type, tags)
                showAddMarkerDialog = false
            }
        )
    }
}

@Composable
fun AMapView(
    modifier: Modifier = Modifier,
    scenes: List<Scene>,
    userMarkers: List<UserMarker> = emptyList(),
    isAddingMarker: Boolean = false,
    tempMarkerPosition: LatLng? = null,
    onSceneSelected: (Scene) -> Unit,
    onMapLongClick: (LatLng) -> Unit = {},
    onUserMarkerSelected: (UserMarker) -> Unit = {},
) {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            onCreate(Bundle())
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier
    ) { view ->
        view.getMap()?.let { amap ->
            setupMap(
                amap,
                scenes,
                userMarkers,
                isAddingMarker,
                tempMarkerPosition,
                onSceneSelected,
                onMapLongClick,
                onUserMarkerSelected
            )
        }
    }
}

private fun setupMap(
    amap: AMap,
    scenes: List<Scene>,
    userMarkers: List<UserMarker>,
    isAddingMarker: Boolean,
    tempMarkerPosition: LatLng?,
    onSceneSelected: (Scene) -> Unit,
    onMapLongClick: (LatLng) -> Unit,
    onUserMarkerSelected: (UserMarker) -> Unit
) {
    // 设置地图类型：普通地图
    amap.mapType = AMap.MAP_TYPE_NORMAL

    // 设置初始位置
    val initialPosition = LatLng(39.9042, 116.4074)
    amap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialPosition, 10f))

    // 清除所有现有标记
    amap.clear()

    // 添加场景标记点
    scenes.forEach { scene ->
        val scenePosition = LatLng(scene.latitude, scene.longitude)
        val markerOptions = MarkerOptions()
            .position(scenePosition)
            .title(scene.name)
            .snippet(scene.description)

        val marker = amap.addMarker(markerOptions)

        // 设置标记点击监听
        amap.setOnMarkerClickListener { clickedMarker ->
            if (clickedMarker == marker) {
                val correspondingScene = scenes.find {
                    it.latitude == clickedMarker.position.latitude &&
                            it.longitude == clickedMarker.position.longitude
                }
                correspondingScene?.let { onSceneSelected(it) }
                true
            } else {
                false
            }
        }
    }

    // 添加用户标记点
    userMarkers.forEach { userMarker ->
        val markerPosition = LatLng(userMarker.latitude, userMarker.longitude)
        val markerOptions = MarkerOptions()
            .position(markerPosition)
            .title(userMarker.name)
            .snippet(userMarker.markerType.displayName)

        val marker = amap.addMarker(markerOptions)

        // 设置用户标记点击监听
        amap.setOnMarkerClickListener { clickedMarker ->
            if (clickedMarker == marker) {
                val correspondingMarker = userMarkers.find {
                    it.latitude == clickedMarker.position.latitude &&
                            it.longitude == clickedMarker.position.longitude
                }
                correspondingMarker?.let { onUserMarkerSelected(it) }
                true
            } else {
                false
            }
        }
    }

    // 添加临时标记（预览位置）
    tempMarkerPosition?.let { position ->
        val tempMarkerOptions = MarkerOptions()
            .position(position)
            .title("新标记位置")
            .snippet("点击确认添加")

        amap.addMarker(tempMarkerOptions)
    }

    // 设置长按监听
    amap.setOnMapLongClickListener { latLng ->
        if (isAddingMarker) {
            onMapLongClick(latLng)
        }
    }

    // 启用各种控件
    amap.uiSettings.isMyLocationButtonEnabled = true
    amap.uiSettings.isZoomControlsEnabled = true
    amap.uiSettings.isCompassEnabled = true
    amap.uiSettings.isScaleControlsEnabled = true
}