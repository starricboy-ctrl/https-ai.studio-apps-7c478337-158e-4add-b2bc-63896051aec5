package com.example.data.repository

import com.example.data.local.BoostDao
import com.example.data.model.BoostType
import com.example.data.model.Campaign
import com.example.data.model.CampaignStatus
import com.example.data.model.CreatorTask
import com.example.data.model.SocialPlatform
import com.example.data.model.UserStats
import kotlinx.coroutines.flow.Flow

class BoostRepository(private val dao: BoostDao) {

    val allCampaigns: Flow<List<Campaign>> = dao.getAllCampaigns()
    val allTasks: Flow<List<CreatorTask>> = dao.getAllTasks()
    val userStats: Flow<UserStats?> = dao.getUserStats()

    suspend fun initializeDefaultsIfNeeded() {
        if (dao.getTaskCount() == 0) {
            val initialTasks = listOf(
                CreatorTask(
                    title = "How I Built a 100k Subscriber Tech Channel in 6 Months",
                    creatorName = "TechVibe Studio",
                    platform = SocialPlatform.YOUTUBE,
                    durationSeconds = 60,
                    rewardCoins = 60,
                    likeBonusCoins = 25,
                    subscribeBonusCoins = 50,
                    viewsCount = "48.2K",
                    category = "Tech & Creative"
                ),
                CreatorTask(
                    title = "Epic Minecraft Hardcore World - Day 100 Mega Castle Tour",
                    creatorName = "PixelMaster Gaming",
                    platform = SocialPlatform.YOUTUBE,
                    durationSeconds = 90,
                    rewardCoins = 90,
                    likeBonusCoins = 30,
                    subscribeBonusCoins = 60,
                    viewsCount = "125K",
                    category = "Gaming"
                ),
                CreatorTask(
                    title = "10 Viral TikTok Hooks That Guarantee 1 Million Views",
                    creatorName = "Alex Growth Hacks",
                    platform = SocialPlatform.TIKTOK,
                    durationSeconds = 45,
                    rewardCoins = 45,
                    likeBonusCoins = 20,
                    subscribeBonusCoins = 40,
                    viewsCount = "89.6K",
                    category = "Marketing & Viral"
                ),
                CreatorTask(
                    title = "5-Minute High Intensity Fat Burning HIIT Routine for Beginners",
                    creatorName = "FitLife Pulse",
                    platform = SocialPlatform.SHORTS,
                    durationSeconds = 60,
                    rewardCoins = 60,
                    likeBonusCoins = 25,
                    subscribeBonusCoins = 50,
                    viewsCount = "31.4K",
                    category = "Fitness & Health"
                ),
                CreatorTask(
                    title = "Cinematic Street Photography Secrets in Tokyo Rain",
                    creatorName = "Lens & Horizon",
                    platform = SocialPlatform.INSTAGRAM,
                    durationSeconds = 45,
                    rewardCoins = 45,
                    likeBonusCoins = 20,
                    subscribeBonusCoins = 45,
                    viewsCount = "64.1K",
                    category = "Photography & Art"
                ),
                CreatorTask(
                    title = "Ultimate Beginner Guide to YouTube Monetization & Watch Time",
                    creatorName = "Creator Academy 360",
                    platform = SocialPlatform.YOUTUBE,
                    durationSeconds = 120,
                    rewardCoins = 120,
                    likeBonusCoins = 35,
                    subscribeBonusCoins = 75,
                    viewsCount = "210K",
                    category = "Education"
                )
            )
            dao.insertTasks(initialTasks)

            // Initial active campaign to show the boost engine in action
            val defaultCampaign = Campaign(
                title = "My Channel Premiere - 4000h Watch Booster",
                platform = SocialPlatform.YOUTUBE,
                type = BoostType.WATCH_HOURS,
                targetUrl = "https://youtube.com/watch?v=sample101",
                targetCount = 200,
                currentCount = 84,
                watchSecondsPerView = 120,
                watchHoursDelivered = 2.8f,
                coinsSpent = 300,
                status = CampaignStatus.ACTIVE,
                isTurbo = true
            )
            dao.insertCampaign(defaultCampaign)

            // Initial user stats
            val initialStats = UserStats(
                id = 1,
                coinBalance = 750,
                totalViewsBoosted = 840,
                totalLikesBoosted = 520,
                totalFollowersBoosted = 185,
                totalWatchHoursBoosted = 28.5f,
                currentMonetizationWatchHours = 1420.0f,
                currentSubscribers = 680,
                dailyStreak = 4,
                channelName = "FEC Studios TV",
                channelNiche = "Entertainment & Media"
            )
            dao.insertUserStats(initialStats)
        }
    }

    suspend fun createCampaign(campaign: Campaign, currentStats: UserStats): Boolean {
        if (currentStats.coinBalance < campaign.coinsSpent) return false

        // Deduct coins and update stats
        val updatedStats = currentStats.copy(
            coinBalance = currentStats.coinBalance - campaign.coinsSpent
        )
        dao.updateUserStats(updatedStats)
        dao.insertCampaign(campaign)
        return true
    }

    suspend fun updateCampaign(campaign: Campaign) {
        dao.updateCampaign(campaign)
    }

    suspend fun deleteCampaign(campaignId: Long) {
        dao.deleteCampaignById(campaignId)
    }

    suspend fun markTaskWatched(task: CreatorTask, currentStats: UserStats) {
        if (task.isWatched) return
        val updatedTask = task.copy(isWatched = true)
        dao.updateTask(updatedTask)

        val updatedStats = currentStats.copy(
            coinBalance = currentStats.coinBalance + task.rewardCoins
        )
        dao.updateUserStats(updatedStats)
    }

    suspend fun markTaskLiked(task: CreatorTask, currentStats: UserStats) {
        if (task.isLiked) return
        val updatedTask = task.copy(isLiked = true)
        dao.updateTask(updatedTask)

        val updatedStats = currentStats.copy(
            coinBalance = currentStats.coinBalance + task.likeBonusCoins
        )
        dao.updateUserStats(updatedStats)
    }

    suspend fun markTaskSubscribed(task: CreatorTask, currentStats: UserStats) {
        if (task.isSubscribed) return
        val updatedTask = task.copy(isSubscribed = true)
        dao.updateTask(updatedTask)

        val updatedStats = currentStats.copy(
            coinBalance = currentStats.coinBalance + task.subscribeBonusCoins
        )
        dao.updateUserStats(updatedStats)
    }

    suspend fun updateUserStats(stats: UserStats) {
        dao.updateUserStats(stats)
    }

    suspend fun addCoins(amount: Int, currentStats: UserStats) {
        val updatedStats = currentStats.copy(
            coinBalance = currentStats.coinBalance + amount
        )
        dao.updateUserStats(updatedStats)
    }
}
