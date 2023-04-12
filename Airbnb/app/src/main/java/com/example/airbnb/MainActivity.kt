package com.example.airbnb

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import com.naver.maps.map.widget.LocationButtonView
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), OnMapReadyCallback, Overlay.OnClickListener {

    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource

    private val mapView: MapView by lazy { findViewById(R.id.MapView) }
    private val viewpager : ViewPager2 by lazy { findViewById(R.id.houseViewpager) }
    private val recyclerView: RecyclerView by lazy { findViewById(R.id.recyclerView) }
    private val currentlocationbutton : LocationButtonView by lazy { findViewById(R.id.CurrentLocationButton) }
    private val bottomsheetTitleTextview: TextView by lazy { findViewById(R.id.bottomSheetTitleTextView) }

    private val viewPagerAdapter = HouseViewPagerAdapter(itemclicked = {
        val intent = Intent()
            .apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT,"지금 ${it.price}에 ${it.title}을 예약하세요!")
                type = "text/plain"
            }
        startActivity(intent)
    })

    private val recyclerViewAdapter = HouseListAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mapView.onCreate(savedInstanceState)

        mapView.getMapAsync(this)

        viewpager.adapter = viewPagerAdapter
        recyclerView.adapter = recyclerViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewpager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                val selectedHouseModel = viewPagerAdapter.currentList[position]
                val cameraUpdate = CameraUpdate.scrollTo(LatLng(selectedHouseModel.lat, selectedHouseModel.lng))
                    .animate(CameraAnimation.Easing)

                naverMap.moveCamera(cameraUpdate)
            }
        })

    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map

        naverMap.maxZoom = 18.0 // 확대
        naverMap.minZoom = 10.0 // 축소

        val cameraUpdate = CameraUpdate.scrollTo(LatLng(37.497885, 127.027512))
        naverMap.moveCamera(cameraUpdate) // 초기 위치 설정

        val uiSetting = naverMap.uiSettings // 현위치 받아오기
        uiSetting.isLocationButtonEnabled = false
        currentlocationbutton.map = naverMap

        // 로케이션 버튼
        locationSource = FusedLocationSource(this@MainActivity, LOCATION_PERMISSION_REQUEST_CODE)
        naverMap.locationSource = locationSource

//        val marker = Marker() // 마커
//        marker.position = LatLng(37.6154444,127.0341968)
//        marker.map = naverMap
//        marker.icon = MarkerIcons.BLACK
//        marker.iconTintColor = Color.RED

        getHouseListFromAPI()
    }

    private fun getHouseListFromAPI(){
        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io")
            .addConverterFactory(GsonConverterFactory.create()) //서버로부터 데이터를 받아와서 원하는 타입으로 데이터를 바꾸기 위해
            .build()

        retrofit.create(HouseService::class.java).also {
            it.getHouseLiset()
                .enqueue(object : Callback<HouseDto>{
                    override fun onResponse(call: Call<HouseDto>, response: Response<HouseDto>) {
                        if(response.isSuccessful.not()){
                            //실패처리에 대한 구현
                            return
                        }

                        response.body()?.let { dto ->
                            updateMaker(dto.items)
                            viewPagerAdapter.submitList(dto.items)
                            recyclerViewAdapter.submitList(dto.items)
                            bottomsheetTitleTextview.text = "${dto.items.size}개의 숙소"
                        }
                    }

                    override fun onFailure(call: Call<HouseDto>, t: Throwable) {
                        //실패처리에 대한 구현
                    }

                })
        }
    }
    private fun updateMaker(house: List<HouseModel>){
        house.forEach { house ->
            val marker = Marker()
            marker.position = LatLng(house.lat,house.lng)
            marker.onClickListener = this
            marker.map = naverMap
            marker.tag = house.id
            marker.icon = MarkerIcons.BLACK
            marker.iconTintColor = Color.RED
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode!= LOCATION_PERMISSION_REQUEST_CODE) {
            return
        }
        if(locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)){
            if(!locationSource.isActivated){
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    companion object {
       private  const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    override fun onClick(overlay: Overlay): Boolean {
        overlay.tag
        Log.d("onClick",overlay.tag.toString())

        val selectedModel = viewPagerAdapter.currentList.firstOrNull{
            it.id == overlay.tag
        }

        selectedModel?.let{
            val pos = viewPagerAdapter.currentList.indexOf(it)
            viewpager.currentItem = pos
        }

        return true
    }

}