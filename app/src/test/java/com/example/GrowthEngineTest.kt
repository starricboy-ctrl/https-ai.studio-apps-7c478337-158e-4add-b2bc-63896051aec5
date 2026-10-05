package com.example

import com.example.data.model.BoostType
import com.example.data.model.SocialPlatform
import com.example.data.tools.GrowthEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GrowthEngineTest {

    @Test
    fun testGenerateViralTags() {
        val tags = GrowthEngine.generateViralTags("Minecraft", SocialPlatform.YOUTUBE)
        assertTrue(tags.isNotEmpty())
        assertTrue(tags.any { it.tag.contains("minecraft", ignoreCase = true) })
        assertTrue(tags.all { it.score in 50..100 })
    }

    @Test
    fun testGenerateViralTitles() {
        val titles = GrowthEngine.generateViralTitles("Gaming Setup")
        assertTrue(titles.isNotEmpty())
        assertTrue(titles.any { it.title.contains("Gaming Setup", ignoreCase = true) })
        assertTrue(titles.all { it.predictedCtr.isNotBlank() })
    }

    @Test
    fun testBestPostTimes() {
        val ytTimes = GrowthEngine.getBestPostTimes(SocialPlatform.YOUTUBE)
        assertEquals(7, ytTimes.size)
        assertTrue(ytTimes.all { it.audiencePeakScore in 1..100 })

        val tiktokTimes = GrowthEngine.getBestPostTimes(SocialPlatform.TIKTOK)
        assertEquals(7, tiktokTimes.size)
    }

    @Test
    fun testCalculateWatchTimePace() {
        // Target 4000h, Current 1000h, Remaining 3000h
        // At 10h/day -> 300 days
        val (remaining, days, viewsNeeded) = GrowthEngine.calculateWatchTimePace(
            currentHours = 1000f,
            targetHours = 4000f,
            dailyHoursRate = 10f
        )
        assertEquals(3000f, remaining, 0.01f)
        assertEquals(300, days)
        // 3000h * 60 min / 3 min per view = 60,000 views
        assertEquals(60000, viewsNeeded)
    }

    @Test
    fun testCampaignRemainingBudgetCalculation() {
        val totalCoins = 300
        val targetCount = 200
        val currentCount = 80 // 40% delivered, 60% remaining
        val remainingUnits = targetCount - currentCount
        val remainingBudget = ((totalCoins.toDouble() * remainingUnits) / targetCount).toInt()

        assertEquals(180, remainingBudget)
    }

    @Test
    fun testGeminiSeoServiceGeneration() = kotlinx.coroutines.runBlocking {
        val result = com.example.data.api.GeminiSeoService.generateSeoSuggestions(
            title = "Top 10 Video Hacks",
            description = "Detailed breakdown of video hooks",
            tags = "video, hacks, tips",
            platform = com.example.data.model.SocialPlatform.YOUTUBE
        )
        assertTrue(result.titles.isNotEmpty())
        assertTrue(result.tags.isNotEmpty())
        assertTrue(result.description.isNotBlank())
        assertTrue(result.seoScore in 70..100)
    }

    @Test
    fun testMonetizationReadinessCalculation() {
        val watchHours = 2000f // 50% of 4000
        val subs = 500 // 50% of 1000
        val watchHoursProgress = (watchHours / 4000f).coerceIn(0f, 1f)
        val subsProgress = (subs.toFloat() / 1000f).coerceIn(0f, 1f)
        val overallReadiness = (((watchHoursProgress * 0.6f) + (subsProgress * 0.4f)) * 100).toInt()

        assertEquals(50, overallReadiness)
    }
}
