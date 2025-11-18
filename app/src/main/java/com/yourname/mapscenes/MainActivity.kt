package com.yourname.mapscenes


import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize  // 添加这行导入
import com.yourname.mapscenes.ui.theme.MapScenesTheme
import com.yourname.mapscenes.viewmodel.MapViewModel
import org.koin.androidx.compose.koinViewModel

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.MapView
import com.amap.api.maps.UiSettings
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MyLocationStyle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import android.Manifest

class MainActivity : AppCompatActivity(), View.OnClickListener {

    // 地图核心对象（可空类型，Kotlin要求显式声明）
    private var mapView: MapView? = null
    private var aMap: AMap? = null
    private var uiSettings: UiSettings? = null

    // 定位相关对象
    private var locationClient: AMapLocationClient? = null
    private var locationOption: AMapLocationClientOption? = null

    // 权限请求码
    companion object {
        private const val REQUEST_LOCATION_PERMISSIONS = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // 高德地图隐私协议（必须在super.onCreate前调用，否则会报错）
        com.amap.api.services.core.ServiceSettings.updatePrivacyShow(this, true, true)
        com.amap.api.services.core.ServiceSettings.updatePrivacyAgree(this, true)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. 初始化地图View并绑定生命周期
        mapView = findViewById(R.id.map)
        mapView?.onCreate(savedInstanceState) // 关键：同步地图生命周期

        // 2. 初始化AMap实例
        initMap()

        // 3. 初始化定位功能
        initLocation()

        // 4. 绑定定位按钮点击事件
        findViewById<FloatingActionButton>(R.id.GetLocal).setOnClickListener(this)

        // 5. 检查权限并启动定位
        checkLocationPermissions()
    }

    /**
     * 初始化地图核心配置
     */
    private fun initMap() {
        // 获取AMap实例
        if (aMap == null) {
            aMap = mapView?.map
        }

        // 配置地图UI（缩放按钮、指南针等）
        uiSettings = aMap?.uiSettings
        uiSettings?.apply {
            isZoomControlsEnabled = true // 显示缩放按钮
            isCompassEnabled = true // 显示指南针
            isMyLocationButtonEnabled = false // 隐藏默认定位按钮（用自定义按钮）
        }

        // 配置定位蓝点样式
        val myLocationStyle = MyLocationStyle().apply {
            myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE) // 定位时移动到中心点
            interval(2000) // 定位间隔（毫秒）
            myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.gps)) // 自定义定位图标
            radiusFillColor(0) // 去除定位圆圈填充色
            strokeColor(0) // 去除定位圆圈边框色
        }
        aMap?.setMyLocationStyle(myLocationStyle)
        aMap?.isMyLocationEnabled = true // 显示定位蓝点
    }

    /**
     * 初始化定位客户端
     */
    private fun initLocation() {
        // 初始化定位客户端
        try {
            locationClient = AMapLocationClient(applicationContext)
        } catch (e: Exception) {
            Log.e("MapInit", "定位客户端初始化失败：${e.message}")
        }

        // 配置定位参数
        locationOption = AMapLocationClientOption().apply {
            locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy // 高精度模式
            isOnceLocationLatest = true // 获取最近3s内精度最高的定位结果
            isNeedAddress = true // 需要返回地址信息
            httpTimeOut = 20000 // 超时时间（毫秒）
            isLocationCacheEnable = false // 关闭缓存
        }
        locationClient?.setLocationOption(locationOption)

        // 设置定位回调（获取定位结果）
        locationClient?.setLocationListener(object : AMapLocationListener {
            override fun onLocationChanged(location: AMapLocation?) {
                location?.let {
                    if (it.errorCode == 0) {
                        // 定位成功：获取经纬度并移动地图到当前位置
                        val latLng = LatLng(it.latitude, it.longitude)
                        aMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f)) // 缩放级别17
                        Log.d("LocationSuccess", "地址：${it.address}，经纬度：${it.latitude}, ${it.longitude}")
                    } else {
                        // 定位失败
                        Log.e("LocationError", "错误码：${it.errorCode}，信息：${it.errorInfo}")
                        showToast("定位失败：${it.errorInfo}")
                    }
                }
            }
        })
    }

    /**
     * 检查定位权限（Android 6.0+需要动态申请）
     */
    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSIONS)
    private fun checkLocationPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (EasyPermissions.hasPermissions(this, *permissions)) {
            // 已有权限，启动定位
            locationClient?.startLocation()
        } else {
            // 无权限，申请权限
            EasyPermissions.requestPermissions(
                this,
                "需要定位权限才能显示您的位置",
                REQUEST_LOCATION_PERMISSIONS,
                *permissions
            )
        }
    }

    /**
     * 手动触发定位（点击按钮时调用）
     */
    private fun getCurrentLocation() {
        locationClient?.let {
            if (!it.isStarted) {
                it.startLocation()
            } else {
                // 已在定位中，重新触发一次定位
                it.stopLocation()
                it.startLocation()
            }
            showToast("正在获取当前位置...")
        } ?: showToast("定位客户端未初始化")
    }

    /**
     * 处理权限请求结果
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    /**
     * 点击事件（处理定位按钮点击）
     */
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.GetLocal -> getCurrentLocation() // 点击定位按钮
        }
    }

    /**
     * 地图生命周期管理（必须实现，否则地图会崩溃）
     */
    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
        // 停止定位并释放资源
        locationClient?.stopLocation()
        locationClient?.onDestroy()
    }

    /**
     * 简化Toast显示
     */
    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}