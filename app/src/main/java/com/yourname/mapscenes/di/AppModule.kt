package com.yourname.mapscenes.di

import android.content.Context
import com.yourname.mapscenes.database.AppDatabase
import com.yourname.mapscenes.repository.SceneRepository
import com.yourname.mapscenes.viewmodel.MapViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { AppDatabase.getInstance(androidContext()) }
    single { get<AppDatabase>().sceneDao() }
    single { get<AppDatabase>().userMarkerDao() }
    single { SceneRepository(get(), get()) }
    viewModel { MapViewModel(get()) }
}