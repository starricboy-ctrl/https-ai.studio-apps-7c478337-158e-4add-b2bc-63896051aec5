package com.example.data.tools

import com.example.data.model.BestPostTime
import com.example.data.model.SocialPlatform
import com.example.data.model.TagItem
import com.example.data.model.TitleIdea

object GrowthEngine {

    fun generateViralTags(topic: String, platform: SocialPlatform): List<TagItem> {
        val cleanTopic = if (topic.isBlank()) "Creator Growth" else topic.trim()
        val baseWord = cleanTopic.lowercase()

        return listOf(
            TagItem("#${baseWord.replace(" ", "")}", "4.2M searches/mo", "Low", 96),
            TagItem("$cleanTopic viral tips", "1.8M searches/mo", "Low", 92),
            TagItem("how to boost $cleanTopic", "950K searches/mo", "Medium", 88),
            TagItem("$cleanTopic algorithm hack 2026", "620K searches/mo", "Low", 95),
            TagItem("$cleanTopic secret strategy", "430K searches/mo", "Low", 89),
            TagItem("best $cleanTopic for beginners", "810K searches/mo", "Medium", 84),
            TagItem("$cleanTopic growth case study", "290K searches/mo", "Low", 91),
            TagItem("$cleanTopic 100k views", "1.1M searches/mo", "Medium", 87),
            TagItem("viral ${platform.displayName.lowercase()} $baseWord", "2.3M searches/mo", "Low", 94),
            TagItem("${baseWord} monetization watch hours", "540K searches/mo", "Low", 90),
            TagItem("trending $baseWord", "3.6M searches/mo", "High", 79),
            TagItem("$cleanTopic full tutorial", "720K searches/mo", "Medium", 85)
        )
    }

    fun generateViralTitles(topic: String): List<TitleIdea> {
        val cleanTopic = if (topic.isBlank()) "My Channel" else topic.trim().replaceFirstChar { it.uppercase() }

        return listOf(
            TitleIdea(
                title = "I Tested $cleanTopic For 30 Days (The Algorithm Went CRAZY)",
                category = "Curiosity Gap & High Stakes",
                predictedCtr = "14.8% CTR (Very High)",
                explanation = "Creates intense curiosity and emotional transformation stakes that trigger immediate clicks."
            ),
            TitleIdea(
                title = "How to Master $cleanTopic in 10 Minutes (No One Talks About This)",
                category = "Extreme Value & Fast Payoff",
                predictedCtr = "12.5% CTR (High)",
                explanation = "Fast timeline with an exclusivity hook leads to high retention and high initial swipe-through."
            ),
            TitleIdea(
                title = "Stop Doing $cleanTopic Like This! (Huge Algorithm Mistake)",
                category = "Loss Aversion & Warning",
                predictedCtr = "13.9% CTR (Very High)",
                explanation = "Loss aversion is psychologically 2x more potent than gain seeking. Prevents scroll-past."
            ),
            TitleIdea(
                title = "The $cleanTopic Secret That 100K Creators Keep Hidden",
                category = "Insider Access & Authority",
                predictedCtr = "11.7% CTR (Above Average)",
                explanation = "Positions the viewer inside the elite circle of top algorithmic performers."
            ),
            TitleIdea(
                title = "From 0 to 100,000 with $cleanTopic: The 2026 Step-by-Step Blueprint",
                category = "Ultimate Guide & Proof",
                predictedCtr = "13.2% CTR (High)",
                explanation = "Actionable roadmap with clear numeric milestones attracts highly motivated binge-watchers."
            )
        )
    }

    fun getBestPostTimes(platform: SocialPlatform): List<BestPostTime> {
        return when (platform) {
            SocialPlatform.YOUTUBE -> listOf(
                BestPostTime("Monday", "2:00 PM – 4:00 PM", "7:00 PM – 9:00 PM", 82),
                BestPostTime("Tuesday", "2:00 PM – 4:00 PM", "8:00 PM – 10:00 PM", 85),
                BestPostTime("Wednesday", "3:00 PM – 5:00 PM", "7:30 PM – 9:30 PM", 89),
                BestPostTime("Thursday", "2:00 PM – 5:00 PM", "8:00 PM – 10:00 PM", 94),
                BestPostTime("Friday", "12:00 PM – 3:00 PM", "6:00 PM – 9:00 PM", 98),
                BestPostTime("Saturday", "9:00 AM – 11:00 AM", "4:00 PM – 7:00 PM", 96),
                BestPostTime("Sunday", "10:00 AM – 1:00 PM", "5:00 PM – 8:00 PM", 92)
            )
            SocialPlatform.TIKTOK, SocialPlatform.SHORTS -> listOf(
                BestPostTime("Monday", "6:00 AM – 10:00 AM", "7:00 PM – 11:00 PM", 88),
                BestPostTime("Tuesday", "9:00 AM – 12:00 PM", "8:00 PM – 11:30 PM", 91),
                BestPostTime("Wednesday", "7:00 AM – 9:00 AM", "6:00 PM – 10:00 PM", 90),
                BestPostTime("Thursday", "10:00 AM – 1:00 PM", "7:00 PM – 11:00 PM", 95),
                BestPostTime("Friday", "5:00 AM – 8:00 AM", "3:00 PM – 9:00 PM", 99),
                BestPostTime("Saturday", "11:00 AM – 2:00 PM", "7:00 PM – 10:00 PM", 97),
                BestPostTime("Sunday", "7:00 AM – 10:00 AM", "4:00 PM – 9:00 PM", 93)
            )
            SocialPlatform.INSTAGRAM -> listOf(
                BestPostTime("Monday", "11:00 AM – 1:00 PM", "7:00 PM – 9:00 PM", 84),
                BestPostTime("Tuesday", "10:00 AM – 2:00 PM", "6:30 PM – 8:30 PM", 87),
                BestPostTime("Wednesday", "11:00 AM – 2:00 PM", "7:00 PM – 9:00 PM", 92),
                BestPostTime("Thursday", "11:00 AM – 1:00 PM", "8:00 PM – 10:00 PM", 93),
                BestPostTime("Friday", "10:00 AM – 12:00 PM", "5:00 PM – 7:30 PM", 95),
                BestPostTime("Saturday", "9:00 AM – 11:00 AM", "6:00 PM – 8:00 PM", 88),
                BestPostTime("Sunday", "10:00 AM – 1:00 PM", "5:00 PM – 7:00 PM", 86)
            )
            SocialPlatform.X_TWITTER -> listOf(
                BestPostTime("Monday", "8:00 AM – 10:00 AM", "12:00 PM – 1:00 PM", 85),
                BestPostTime("Tuesday", "9:00 AM – 11:00 AM", "1:00 PM – 3:00 PM", 90),
                BestPostTime("Wednesday", "9:00 AM – 12:00 PM", "5:00 PM – 6:00 PM", 94),
                BestPostTime("Thursday", "8:00 AM – 11:00 AM", "2:00 PM – 4:00 PM", 93),
                BestPostTime("Friday", "8:00 AM – 10:00 AM", "12:00 PM – 2:00 PM", 89),
                BestPostTime("Saturday", "10:00 AM – 12:00 PM", "3:00 PM – 5:00 PM", 78),
                BestPostTime("Sunday", "11:00 AM – 1:00 PM", "6:00 PM – 8:00 PM", 80)
            )
        }
    }

    fun calculateWatchTimePace(
        currentHours: Float,
        targetHours: Float = 4000f,
        dailyHoursRate: Float
    ): Triple<Float, Int, Int> {
        val remainingHours = (targetHours - currentHours).coerceAtLeast(0f)
        val rate = if (dailyHoursRate <= 0.1f) 5.5f else dailyHoursRate
        val daysToMonetization = (remainingHours / rate).toInt()
        val viewsNeededAt3MinAverage = ((remainingHours * 60) / 3f).toInt()
        return Triple(remainingHours, daysToMonetization, viewsNeededAt3MinAverage)
    }
}
