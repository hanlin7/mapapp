package com.yourname.mapscenes

import android.app.Application
import com.amap.api.maps.MapsInitializer
import com.yourname.mapscenes.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MapScenesApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // 高德地图隐私合规设置
        MapsInitializer.updatePrivacyShow(this, true, true)
        MapsInitializer.updatePrivacyAgree(this, true)

        // 启动 Koin 依赖注入
        startKoin {
            androidContext(this@MapScenesApplication)
            modules(appModule)
        }

        // 暂时移除数据库初始化，先解决编译问题
        // GlobalScope.launch {
        //     val database = AppDatabase.getInstance(this@MapScenesApplication)
        //     val repository = SceneRepository(
        //         database.sceneDao(),
        //         database.userMarkerDao()
        //     )
        //     repository.initializeSampleData()
        // }
    }
}