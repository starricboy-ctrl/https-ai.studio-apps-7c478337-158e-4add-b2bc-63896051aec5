package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Campaign
import com.example.data.model.CreatorTask
import com.example.data.model.UserStats
import kotlinx.coroutines.flow.Flow

@Dao
interface BoostDao {
    // Campaigns
    @Query("SELECT * FROM campaigns ORDER BY createdAt DESC")
    fun getAllCampaigns(): Flow<List<Campaign>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCampaign(campaign: Campaign): Long

    @Update
    suspend fun updateCampaign(campaign: Campaign)

    @Delete
    suspend fun deleteCampaign(campaign: Campaign)

    @Query("DELETE FROM campaigns WHERE id = :id")
    suspend fun deleteCampaignById(id: Long)

    // Tasks (Community Pool)
    @Query("SELECT * FROM creator_tasks ORDER BY isWatched ASC, id ASC")
    fun getAllTasks(): Flow<List<CreatorTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<CreatorTask>)

    @Update
    suspend fun updateTask(task: CreatorTask)

    @Query("SELECT COUNT(*) FROM creator_tasks")
    suspend fun getTaskCount(): Int

    // User Stats
    @Query("SELECT * FROM user_stats WHERE id = 1 LIMIT 1")
    fun getUserStats(): Flow<UserStats?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserStats(stats: UserStats)

    @Update
    suspend fun updateUserStats(stats: UserStats)
}
