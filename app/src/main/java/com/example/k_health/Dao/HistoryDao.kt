package com.example.k_health.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.k_health.model.History

// Room 1-2) DB에 접근해 질의를 수행할 Dao 파일 생성.
// Query를 메소드로 작성해줘야한다.
@Dao
interface HistoryDao {

    @Query("SELECT * FROM history")
    fun getAll(): List<History>

    @Insert
    fun insertHistory(history: History)

    // history table에서 'keyword'에 해당하는 데이터를 삭제
    @Query("DELETE FROM history WHERE keyword = :keyword")
    fun delete(keyword: String)
}