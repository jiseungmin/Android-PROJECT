package com.example.locationbasedapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.locationbasedapp.MapActivity.Companion.SEARCH_RESULT_EXTRA_KEY
import com.example.locationbasedapp.Utillity.RetrofitUtil
import com.example.locationbasedapp.databinding.ActivityMainBinding
import com.example.locationbasedapp.model.LocationLatLngEntity
import com.example.locationbasedapp.model.SearchResultEntity
import com.example.locationbasedapp.response.Poi
import com.example.locationbasedapp.response.Pois
import kotlinx.coroutines.*
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: SearchRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        job = Job()

        initAdapter()
        initview()
        bindViews()
        initData()
    }

    private fun bindViews() = with(binding) {
        SearchButton.setOnClickListener {
            searchKeyword(SearchBarInputView.text.toString())
        }
    }

    private fun searchKeyword(KeywordString: String) {
        launch(coroutineContext) {
            try {
                withContext(Dispatchers.IO) {
                    val response = RetrofitUtil.apiService.getSearchLocation(
                        keyword = KeywordString
                    )
                    if (response.isSuccessful) {
                        val body = response.body()
                        withContext(Dispatchers.Main) {
                            body?.let { searchResponse ->
                                setData(searchResponse.searchPoiInfo.pois)

                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@MainActivity, "검색하는 과정에서 에러가 발생했습니다. : ${e.message}", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun initview() = with(binding) {
        emptyResultTextView.isVisible = false
        RecyclerView.adapter = adapter
    }

    private fun initAdapter() {
        adapter = SearchRecyclerAdapter()
    }

    private fun initData() {
        adapter.notifyDataSetChanged()
    }

    private fun setData(pois: Pois) {
        val datalist = pois.poi.map {
            SearchResultEntity(
                name = it.name ?: "",
                fulladress = makeMainAdress(it),
                locationLatng = LocationLatLngEntity(it.noorLat, it.noorLon)
            )
        }
        adapter.setSearchResultList(datalist){
            startActivity(
                Intent(this, MapActivity::class.java).apply {
                    putExtra(SEARCH_RESULT_EXTRA_KEY, it)
                })
        }
    }

    private fun makeMainAdress(poi: Poi): String =
        if (poi.secondNo?.trim().isNullOrEmpty()) {
            (poi.upperAddrName?.trim() ?: "") + " " +
                    (poi.middleAddrName?.trim() ?: "") + " " +
                    (poi.lowerAddrName?.trim() ?: "") + " " +
                    (poi.detailAddrName?.trim() ?: "") + " " +
                    poi.firstNo?.trim()
        } else {
            (poi.upperAddrName?.trim() ?: "") + " " +
                    (poi.middleAddrName?.trim() ?: "") + " " +
                    (poi.lowerAddrName?.trim() ?: "") + " " +
                    (poi.detailAddrName?.trim() ?: "") + " " +
                    (poi.firstNo?.trim() ?: "") + " " +
                    poi.secondNo?.trim()
        }


}