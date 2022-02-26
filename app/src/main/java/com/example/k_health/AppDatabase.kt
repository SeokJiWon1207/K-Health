package com.example.k_health

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.k_health.Dao.HistoryDao
import com.example.k_health.food.data.models.History

// 1-3) History Entity 모델을 베이스로 하고 History의 메소드를 가지고 있는 Database생성
@Database(entities = [History::class],version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}

fun getAppDatabase(context: Context): AppDatabase {

    val migration_1_2 = object : Migration(1,2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE `REVIEW` (`id` INTEGER, `review` TEXT," + "PRIMARY KEY(`id`))")
        }
    }

    return Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "FoodSearchDB"
    )
        .addMigrations(migration_1_2)
        .build()
}