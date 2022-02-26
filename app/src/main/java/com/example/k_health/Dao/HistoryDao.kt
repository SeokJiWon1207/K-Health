package com.example.k_health.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.k_health.food.data.models.History

// Room 1-2) DB에 접근해 질의를 수행할 Dao 파일 생성.
// Query를 메소드로 작성해줘야한다.
@Dao
interface HistoryDao {
    // history 테이블에서 'keyword'의 데이터를 중복없이 조회
    @Query("SELECT DISTINCT * FROM history")
    suspend fun getAll(): List<History>

    @Insert(onConflict = REPLACE)
    suspend fun insertHistory(history: History)

    // history 테이블에서 'keyword'에 해당하는 데이터를 삭제
    @Query("DELETE FROM history WHERE keyword = :keyword")
    suspend fun delete(keyword: String)
}