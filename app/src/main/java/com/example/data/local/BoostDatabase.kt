package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.model.Campaign
import com.example.data.model.CreatorTask
import com.example.data.model.UserStats

@Database(
    entities = [Campaign::class, CreatorTask::class, UserStats::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(BoostConverters::class)
abstract class BoostDatabase : RoomDatabase() {
    abstract fun boostDao(): BoostDao

    companion object {
        @Volatile
        private var INSTANCE: BoostDatabase? = null

        fun getInstance(context: Context): BoostDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BoostDatabase::class.java,
                    "boost_pulse.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
