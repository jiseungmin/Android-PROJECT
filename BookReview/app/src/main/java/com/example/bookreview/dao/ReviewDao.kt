package com.example.bookreview.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bookreview.model.Review

@Dao
interface  ReviewDao {
    @Query("SELECT * FROM Review WHERE isbn = :isbn")
    fun getOne(isbn: String): Review

    @Insert(onConflict = OnConflictStrategy.REPLACE) //원래 리뷰가 있을때 새롭게 다시 저장
    fun saveReview(review: Review)

}
