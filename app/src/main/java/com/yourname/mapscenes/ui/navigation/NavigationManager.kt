package com.yourname.mapscenes.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yourname.mapscenes.ui.screens.MapScreen
import com.yourname.mapscenes.ui.screens.SceneDetailScreen
import com.yourname.mapscenes.ui.screens.UserMarkerDetailScreen
import com.yourname.mapscenes.viewmodel.MapViewModel

@Composable
fun NavigationManager(viewModel: MapViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "map"
    ) {
        composable("map") {
            MapScreen(
                viewModel = viewModel,
                onSceneSelected = { scene ->
                    navController.navigate("scene_detail/${scene.id}")
                },
                onUserMarkerSelected = { marker ->
                    navController.navigate("user_marker_detail/${marker.id}")
                }
            )
        }

        composable("scene_detail/{sceneId}") { backStackEntry ->
            val sceneId = backStackEntry.arguments?.getString("sceneId")
            val scene = viewModel.scenes.value.find { it.id == sceneId }

            if (scene != null) {
                SceneDetailScreen(
                    scene = scene,
                    onBack = { navController.popBackStack() },
                    onToggleFavorite = { id ->
                        viewModel.toggleFavorite(id)
                    }
                )
            } else {
                // 如果场景不存在，返回地图页面
                MapScreen(
                    viewModel = viewModel,
                    onSceneSelected = { scene ->
                        navController.navigate("scene_detail/${scene.id}")
                    },
                    onUserMarkerSelected = { marker ->
                        navController.navigate("user_marker_detail/${marker.id}")
                    }
                )
            }
        }

        composable("user_marker_detail/{markerId}") { backStackEntry ->
            val markerId = backStackEntry.arguments?.getString("markerId")
            val marker = viewModel.userMarkers.value.find { it.id == markerId }

            if (marker != null) {
                UserMarkerDetailScreen(
                    marker = marker,
                    onBack = { navController.popBackStack() },
                    onDelete = {
                        viewModel.deleteUserMarker(markerId ?: "")
                        navController.popBackStack()
                    }
                )
            } else {
                // 如果标记不存在，返回地图页面
                MapScreen(
                    viewModel = viewModel,
                    onSceneSelected = { scene ->
                        navController.navigate("scene_detail/${scene.id}")
                    },
                    onUserMarkerSelected = { marker ->
                        navController.navigate("user_marker_detail/${marker.id}")
                    }
                )
            }
        }
    }
}