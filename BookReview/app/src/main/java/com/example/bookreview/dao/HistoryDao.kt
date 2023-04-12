package com.example.bookreview.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.bookreview.model.History


@Dao //DAO는 앱에서 데이터베이스의 데이터를 추가, 삭제, 업데이트 작업을 할 수 있는 메소드를 제공해주는 역할, 그 외에도 다양한 쿼리 사용 가능
interface HistoryDao{
    @Query("SELECT * FROM history")
    fun getAll():List<History> // 검색 기록을 가져오는 함수

    @Insert
    fun insertHistory(history: History) // 검색기록을 저장하는 함수

    @Query("DELETE FROM history WHERE keyword == :keyword")
    fun delete(keyword:String) // 기록에서 지워주는 함수
}