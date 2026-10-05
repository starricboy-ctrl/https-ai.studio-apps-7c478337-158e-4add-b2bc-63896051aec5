package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.SocialPlatform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class AiSeoSuggestionResult(
    val titles: List<AiTitleSuggestion>,
    val description: String,
    val tags: List<String>,
    val seoScore: Int,
    val strategyTip: String,
    val isFromGemini: Boolean = false,
    val errorMessage: String? = null
)

data class AiTitleSuggestion(
    val title: String,
    val hookType: String,
    val predictedCtr: String
)

object GeminiSeoService {
    private const val TAG = "GeminiSeoService"
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    private val client by lazy {
        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    suspend fun generateSeoSuggestions(
        title: String,
        description: String,
        tags: String,
        platform: SocialPlatform = SocialPlatform.YOUTUBE
    ): AiSeoSuggestionResult = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        // If no API key configured, use local intelligent generation
        if (apiKey.isNullOrBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.d(TAG, "No valid Gemini API key configured. Using local SEO engine.")
            return@withContext generateLocalSuggestions(title, description, tags, platform, isFallback = true)
        }

        try {
            val prompt = """
                You are a world-class YouTube and social media SEO algorithm engineer for FEC Studios TV.
                Analyze the following video details and generate viral, high-converting SEO optimizations.
                
                Input Video Title: $title
                Input Video Description: $description
                Input Tags/Keywords: $tags
                Target Platform: ${platform.displayName}
                
                Return a strictly valid JSON object matching this schema:
                {
                  "improvedTitles": [
                    {
                      "title": "Title 1 with high CTR hook",
                      "hookType": "Curiosity Gap",
                      "predictedCtr": "14.8%"
                    },
                    {
                      "title": "Title 2 with high emotional stake",
                      "hookType": "Extreme Challenge",
                      "predictedCtr": "16.2%"
                    },
                    {
                      "title": "Title 3 optimized for algorithm search",
                      "hookType": "Direct Search Match",
                      "predictedCtr": "13.5%"
                    }
                  ],
                  "optimizedDescription": "A complete 3-paragraph SEO description with relevant hook, value summary, chapter timestamps, and hashtags.",
                  "recommendedTags": ["tag1", "tag2", "tag3", "tag4", "tag5", "tag6", "tag7", "tag8"],
                  "seoScore": 94,
                  "strategyTip": "Actionable tip on thumbnail composition and first 30 seconds retention."
                }
                
                Only output the JSON object, no Markdown ticks.
            """.trimIndent()

            val requestBodyJson = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            val partObj = JSONObject().apply {
                                put("text", prompt)
                            }
                            put(partObj)
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)

                val genConfig = JSONObject().apply {
                    put("temperature", 0.7)
                    put("responseMimeType", "application/json")
                }
                put("generationConfig", genConfig)
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(requestBodyJson.toString().toRequestBody(mediaType))
                .build()

            val response = client.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.e(TAG, "Gemini API failed with HTTP ${response.code}: $responseString")
                return@withContext generateLocalSuggestions(
                    title, description, tags, platform,
                    isFallback = true,
                    error = "Gemini API HTTP ${response.code}: Please verify API key in Secrets panel."
                )
            }

            // Parse candidates
            val root = JSONObject(responseString)
            val candidates = root.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val rawText = parts?.optJSONObject(0)?.optString("text") ?: ""

            if (rawText.isBlank()) {
                return@withContext generateLocalSuggestions(title, description, tags, platform, isFallback = true)
            }

            // Parse returned JSON from model
            val cleanedJson = rawText
                .replace("```json", "")
                .replace("```", "")
                .trim()

            val parsedJson = JSONObject(cleanedJson)
            val titlesArray = parsedJson.optJSONArray("improvedTitles") ?: JSONArray()
            val titles = mutableListOf<AiTitleSuggestion>()
            for (i in 0 until titlesArray.length()) {
                val item = titlesArray.optJSONObject(i) ?: continue
                titles.add(
                    AiTitleSuggestion(
                        title = item.optString("title", "Viral Video Title"),
                        hookType = item.optString("hookType", "High CTR"),
                        predictedCtr = item.optString("predictedCtr", "14.0%")
                    )
                )
            }

            val desc = parsedJson.optString(
                "optimizedDescription",
                description.ifBlank { "In this video from FEC Studios TV, we dive deep into the ultimate breakdown. Watch until the end for the unexpected reveal!" }
            )

            val tagsArray = parsedJson.optJSONArray("recommendedTags") ?: JSONArray()
            val tagsList = mutableListOf<String>()
            for (i in 0 until tagsArray.length()) {
                val t = tagsArray.optString(i)
                if (t.isNotBlank()) tagsList.add(t)
            }

            val seoScore = parsedJson.optInt("seoScore", 92)
            val tip = parsedJson.optString("strategyTip", "Keep your first 30 seconds fast-paced to maintain >65% audience retention.")

            AiSeoSuggestionResult(
                titles = if (titles.isNotEmpty()) titles else generateLocalTitles(title),
                description = desc,
                tags = if (tagsList.isNotEmpty()) tagsList else generateLocalTags(tags, platform),
                seoScore = seoScore,
                strategyTip = tip,
                isFromGemini = true
            )
        } catch (e: Throwable) {
            Log.e(TAG, "Error invoking Gemini API", e)
            generateLocalSuggestions(
                title, description, tags, platform,
                isFallback = true,
                error = e.localizedMessage
            )
        }
    }

    private fun generateLocalSuggestions(
        title: String,
        description: String,
        tags: String,
        platform: SocialPlatform,
        isFallback: Boolean = false,
        error: String? = null
    ): AiSeoSuggestionResult {
        val query = title.ifBlank { "Viral Content & Growth" }
        val generatedTitles = generateLocalTitles(query)
        val generatedTags = generateLocalTags(if (tags.isNotBlank()) tags else query, platform)

        val generatedDesc = """
            🔥 Welcome to FEC Studios TV! In this exclusive video, we break down $query.
            
            ⏱️ CHAPTER TIMESTAMPS:
            0:00 - Introduction & The Big Problem
            1:15 - Key Insights & Breakdown
            3:45 - The Turning Point You Won't Believe
            6:10 - Final Verdict & Creator Tips
            
            🔔 Subscribe to FEC Studios TV for more viral content: https://youtube.com/@FECStudiosTV
            👍 Leave a like if this helped you, and comment your favorite part below!
            
            #${query.replace(" ", "")} #FECStudiosTV #ViralGrowth #CreatorTips #Algorithm2026
        """.trimIndent()

        val score = 88 + (query.length % 11)

        return AiSeoSuggestionResult(
            titles = generatedTitles,
            description = generatedDesc,
            tags = generatedTags,
            seoScore = score,
            strategyTip = "Pair these tags with a bright thumbnail with 3 or fewer focal points and bold contrasting text.",
            isFromGemini = !isFallback,
            errorMessage = error
        )
    }

    private fun generateLocalTitles(query: String): List<AiTitleSuggestion> {
        val clean = query.trim().ifBlank { "This Creator Secret" }
        return listOf(
            AiTitleSuggestion(
                title = "I Tried $clean For 30 Days (Here's What Happened)",
                hookType = "Curiosity Gap",
                predictedCtr = "16.8%"
            ),
            AiTitleSuggestion(
                title = "Why Everyone is WRONG About $clean (Algorithmic Truth)",
                hookType = "Contrarian Stance",
                predictedCtr = "15.4%"
            ),
            AiTitleSuggestion(
                title = "$clean: The Ultimate 2026 Guide Before It's Too Late!",
                hookType = "Urgency / High Stakes",
                predictedCtr = "14.1%"
            )
        )
    }

    private fun generateLocalTags(tagsInput: String, platform: SocialPlatform): List<String> {
        val baseWords = tagsInput.split(",", " ", "\n")
            .map { it.trim().lowercase() }
            .filter { it.length > 2 }

        val standard = listOf(
            "fec studios tv",
            "viral tips 2026",
            "watch time booster",
            "algorithm hacks",
            "monetization guide",
            "video seo",
            "trending shorts",
            "creator economy"
        )

        val result = mutableListOf<String>()
        baseWords.forEach { w ->
            result.add(w)
            result.add("$w tips")
            result.add("$w tutorial")
            result.add("best $w 2026")
        }
        result.addAll(standard)
        return result.distinct().take(12)
    }
}
