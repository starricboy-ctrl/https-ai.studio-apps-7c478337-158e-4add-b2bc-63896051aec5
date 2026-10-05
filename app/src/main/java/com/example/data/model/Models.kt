package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class BoostType(val displayName: String, val iconName: String, val unitLabel: String) {
    VIEWS("Video Views", "play_arrow", "Views"),
    WATCH_HOURS("Watch Hours (4,000h)", "schedule", "Hours"),
    FOLLOWERS("Followers / Subscribers", "person_add", "Followers"),
    LIKES("Likes & Hearts", "favorite", "Likes")
}

enum class SocialPlatform(val displayName: String, val colorHex: Long) {
    YOUTUBE("YouTube", 0xFFFF0000),
    TIKTOK("TikTok", 0xFF00F2FE),
    INSTAGRAM("Instagram", 0xFFE1306C),
    X_TWITTER("X / Twitter", 0xFF1DA1F2),
    SHORTS("Shorts / Reels", 0xFFFF4500)
}

enum class CampaignStatus {
    ACTIVE,
    PAUSED,
    COMPLETED
}

@Entity(tableName = "campaigns")
data class Campaign(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val platform: SocialPlatform,
    val type: BoostType,
    val targetUrl: String,
    val targetCount: Int,
    val currentCount: Int = 0,
    val watchSecondsPerView: Int = 60, // e.g. 60s, 120s, 300s
    val watchHoursDelivered: Float = 0.0f,
    val coinsSpent: Int,
    val status: CampaignStatus = CampaignStatus.ACTIVE,
    val isTurbo: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "creator_tasks")
data class CreatorTask(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val creatorName: String,
    val platform: SocialPlatform,
    val durationSeconds: Int,
    val rewardCoins: Int,
    val likeBonusCoins: Int = 20,
    val subscribeBonusCoins: Int = 40,
    val isWatched: Boolean = false,
    val isLiked: Boolean = false,
    val isSubscribed: Boolean = false,
    val viewsCount: String = "12.4K",
    val category: String = "Gaming & Tech"
)

@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey
    val id: Int = 1,
    val coinBalance: Int = 450,
    val totalViewsBoosted: Int = 1240,
    val totalLikesBoosted: Int = 890,
    val totalFollowersBoosted: Int = 320,
    val totalWatchHoursBoosted: Float = 68.5f,
    val currentMonetizationWatchHours: Float = 1450.0f,
    val currentSubscribers: Int = 680,
    val dailyStreak: Int = 3,
    val lastCheckInDay: Long = 0,
    val channelName: String = "FEC Studios TV",
    val channelNiche: String = "Entertainment & Media",
    val luckySpinRemaining: Int = 1
)

data class TagItem(
    val tag: String,
    val searchVolume: String,
    val competition: String, // "Low", "Medium", "High"
    val score: Int // 0-100
)

data class TitleIdea(
    val title: String,
    val category: String, // "High Curiosity", "Extreme Value", "How-To Viral", "Algorithmic Mystery"
    val predictedCtr: String,
    val explanation: String
)

data class BestPostTime(
    val dayOfWeek: String,
    val bestWindow: String,
    val secondaryWindow: String,
    val audiencePeakScore: Int // 1-100
)

data class DailyQuest(
    val id: String,
    val title: String,
    val description: String,
    val rewardCoins: Int,
    val target: Int,
    val current: Int,
    val isClaimed: Boolean
)
