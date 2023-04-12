package com.example.bookreview

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.bookreview.adapter.BookAdapter
import com.example.bookreview.adapter.HistoryAdapter
import com.example.bookreview.api.BookService
import com.example.bookreview.databinding.ActivityMainBinding
import com.example.bookreview.model.History
import com.example.bookreview.model.SearchBooksDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: BookAdapter
    private lateinit var Historyadapter: HistoryAdapter
    private lateinit var bookService: BookService
    private lateinit var db :AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBookrecyclerView()
        initHistoryrecyclerView()
        initSearchEditText()

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "BookSearchDB"
        ).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://openapi.naver.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        bookService = retrofit.create(BookService::class.java)
    }

    private fun search(keyword: String) {
        bookService.getBooksByName(
            getString(R.string.apikey),
            getString(R.string.serchkey),
            keyword
        )
            .enqueue(object : Callback<SearchBooksDto> {
                override fun onResponse(
                    call: Call<SearchBooksDto>,
                    response: Response<SearchBooksDto>
                ) { //  api를 받아 왔을때
                    // TODO 성공 처리
                    HidehistoryView()
                    SaveSearchKeyword(keyword)
                    if (response.isSuccessful.not()) {
                        return
                    }
                    response.body()?.let {
                        adapter.submitList(it.books)
                    }
                }

                override fun onFailure(
                    call: Call<SearchBooksDto>,
                    t: Throwable
                ) { //  api를 받아 오지 못했을때
                    // TODO 실패 처리
                    HidehistoryView()
                }


            })
    }

    private fun initBookrecyclerView() {
        adapter = BookAdapter(itemClickedListener = {
            val intent = Intent(this,DetailActivity::class.java)
            intent.putExtra("bookModel", it)
            startActivity(intent)
        })
        binding.BookRecylerView.layoutManager = LinearLayoutManager(this)
        binding.BookRecylerView.adapter = adapter
    }

    private fun initHistoryrecyclerView() {
        Historyadapter = HistoryAdapter(historyDeleteClickedListner = {
            deleteSearchKeyword(it)
        })
        binding.historyRecylerview.layoutManager = LinearLayoutManager(this)
        binding.historyRecylerview.adapter = Historyadapter
        initSearchEditText()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initSearchEditText(){
        binding.searchEditText.setOnKeyListener { _, keyCord, event -> //// edittext에서 입력을 받았을때 처리
            if (keyCord == KeyEvent.KEYCODE_ENTER && event.action == MotionEvent.ACTION_DOWN) {
                search(binding.searchEditText.text.toString())
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
        binding.searchEditText.setOnTouchListener{ _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                ShowhistoryView()
            }
            return@setOnTouchListener false
        }
    }

    private fun ShowhistoryView(){
        Thread {
            val keywords = db.historyDao().getAll().reversed()
            runOnUiThread{
                binding.historyRecylerview.isVisible = true
                Historyadapter.submitList(keywords.orEmpty())
            }
        }.start()
        binding.historyRecylerview.isVisible = true
    }

    private fun HidehistoryView(){
        binding.historyRecylerview.isVisible = false
    }


    private fun SaveSearchKeyword(keyword: String){
        Thread {
            db.historyDao().insertHistory(History(null,keyword))
        }.start()
    }
    private fun deleteSearchKeyword(keyword: String){
        Thread {
            db.historyDao().delete(keyword)
            ShowhistoryView()
        }.start()
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val Apikey = ""
    }
}