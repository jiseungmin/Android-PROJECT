package com.example.bookreview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.bumptech.glide.Glide
import com.example.bookreview.databinding.ActivityDetailBinding
import com.example.bookreview.model.Book
import com.example.bookreview.model.Review

class DetailActivity:AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var db :AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "BookSearchDB"
        ).build()

        val bookModel = intent.getParcelableExtra<Book>("bookModel")


        binding.titleTextview.text=bookModel?.title.orEmpty()


        Glide.with(binding.converImageView.context)
            .load(bookModel?.coverSmallUrl.orEmpty())
            .into(binding.converImageView)

        binding.descriptionTextView.text=bookModel?.description.orEmpty()

        Thread {
            val review = db.reviewDao().getOne(bookModel?.id.orEmpty())
            runOnUiThread {
                binding.reviewEdittext.setText(review?.review.orEmpty())
            }
        }.start()

        binding.savebutton.setOnClickListener {
            Thread {
                db.reviewDao().saveReview(
                    Review(
                        bookModel?.id.orEmpty(),
                        binding.reviewEdittext.text.toString()
                    )
                )

            }.start()
        }
    }
}