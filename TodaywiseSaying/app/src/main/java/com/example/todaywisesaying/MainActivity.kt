package com.example.todaywisesaying

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.absoluteValue

class MainActivity : AppCompatActivity() {

    private val viewpager: ViewPager2 by lazy {
        findViewById(R.id.Viewpager)
    }
    private val progressbar: ProgressBar by lazy {
        findViewById(R.id.Progressbar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initview()
        initdata()
    }

    private fun initview(){ // 화면으로 넘길때 여운을 주는 함수
        viewpager.setPageTransformer { page, position ->
            when{
                position.absoluteValue >= 1F -> {
                    page.alpha = 0F
                }
                position == 0F -> { // position이 중앙에 올때 알파값은 1
                    page.alpha = 1F // 알파값 1은 보여짐 0은 안보여짐
                }
                else -> { // 1에서0으로 점점 안보여지게함.
                    page.alpha = 1F - 2 * position.absoluteValue
                }
            }

        }
    }

    private fun initdata(){
        val remoteconfig = Firebase.remoteConfig
        remoteconfig.setConfigSettingsAsync(
            remoteConfigSettings {
                minimumFetchIntervalInSeconds = 0 ////앱이 들어올때마다 패치
            }
        )

        remoteconfig.fetchAndActivate().addOnCompleteListener {
            progressbar.visibility = View.GONE // 프로그래스 바 숨기기
            if(it.isSuccessful){ // 만약 구동이 성공한다면 아래 데이터들을 가져와라
                val quotes = parseQuotesJson(remoteconfig.getString("quotes"))
                val isNameReveald = remoteconfig.getBoolean("is_name_reveal")
                displayQuotesPager(quotes,isNameReveald)
            }
        }

    }

    private fun parseQuotesJson(Json: String):List<Quote> {

        val jsonArray = JSONArray(Json) // JSON STRING을  배열로 바꿔줌
        var jsonList = emptyList<JSONObject>() // 오브젝트 리스트
        for(index in 0 until jsonArray.length()){
            val jsonObject = jsonArray.getJSONObject(index) // jsonarray에서 인덱스 전달 object에
            jsonObject?.let { //null이 아니면은 리스트에 차례대로 붙여준다
                jsonList = jsonList + it
            }
        }

        return jsonList.map {
            Quote(
                quote = it.getString("quote"),
                name = it.getString("name")
            )
        }

    }

    private fun displayQuotesPager(quotes: List<Quote>,isNameRevealed: Boolean){
        val adapter = QuotesPagerAdapter(
            quote = quotes,
            isNameRevealed = isNameRevealed
        )
        viewpager.adapter = adapter
        viewpager.setCurrentItem(adapter.itemCount / 2, false) // int max의 중앙값으로 위치 시켜줌
    }

}