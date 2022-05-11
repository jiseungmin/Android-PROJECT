package com.example.bookreviewapp

import BookAdapter
import HistoryAdapter
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.bookreviewapp.api.BookAPI
import com.example.bookreviewapp.databinding.ActivityMainBinding
import com.example.bookreviewapp.model.History
import com.example.bookreviewapp.model.SearchBooksDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: BookAdapter
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var service: BookAPI
    private lateinit var db: AppDatabase

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "historyDB"
        )
            .build()

        adapter = BookAdapter(clickListener = {
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("bookModel", it)
            startActivity(intent)
        })
        historyAdapter = HistoryAdapter(historyDeleteClickListener = {
            deleteSearchKeyword(it)
        })


        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(BookAPI::class.java)
//        service.getBestSeller(getString(R.string.interpark_apikey))
//            .enqueue(object: Callback<BestSellerDto> {
//                override fun onFailure(call: Call<BestSellerDto>, t: Throwable) {
//
//                }
//
//                override fun onResponse(call: Call<BestSellerDto>, response: Response<BestSellerDto>) {
//                    if (response.isSuccessful.not()) {
//                        return
//                    }
//
//                    response.body()?.let {
//                        adapter.submitList(it.books)
//                    }
//                }
//
//            })
//


        binding.bookRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.bookRecyclerView.adapter = adapter

        binding.SearchEditText.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                search(binding.SearchEditText.text.toString())
                return@setOnKeyListener true
            }
            return@setOnKeyListener false

        }

        binding.SearchEditText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                showHistoryView()
            }

            return@setOnTouchListener false
        }


        binding.historyRecyclerview.adapter = historyAdapter
        binding.historyRecyclerview.layoutManager = LinearLayoutManager(this)


    }

    private fun search(text: String) {


        service.getBooksByName(
            getString(R.string.naver_id),
            getString(R.string.naver_secret_key),
            text
        )
            .enqueue(object : Callback<SearchBooksDto> {
                override fun onFailure(call: Call<SearchBooksDto>, t: Throwable) {
                    hideHistoryView()
                }

                override fun onResponse(
                    call: Call<SearchBooksDto>,
                    response: Response<SearchBooksDto>
                ) {

                    hideHistoryView()
                    saveSearchKeyword(text)

                    if (response.isSuccessful.not()) {
                        return
                    }

                    response.body()
                        ?.let {
                            adapter.submitList(it.books)
                        }
                }

            })
    }

    private fun showHistoryView() {
        Thread(Runnable {
            db.historyDao()
                .getAll()
                .reversed()
                .run {
                    runOnUiThread {
                        binding.historyRecyclerview.isVisible = true
                        historyAdapter.submitList(this)
                    }
                }

        }).start()

    }

    private fun hideHistoryView() {
        binding.historyRecyclerview.isVisible = false
    }

    private fun saveSearchKeyword(keyword: String) {
        Thread(Runnable {
            db.historyDao()
                .insertHistory(History(null, keyword))
        }).start()
    }

    private fun deleteSearchKeyword(keyword: String) {
        Thread(Runnable {
            db.historyDao()
                .delete(keyword)
            showHistoryView()
        }).start()
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val BASE_URL = "https://openapi.naver.com/"
    }
}