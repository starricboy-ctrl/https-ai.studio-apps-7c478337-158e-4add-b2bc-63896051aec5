package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.BoostDatabase
import com.example.data.model.BoostType
import com.example.data.model.Campaign
import com.example.data.model.CampaignStatus
import com.example.data.model.CreatorTask
import com.example.data.model.SocialPlatform
import com.example.data.model.UserStats
import com.example.data.repository.BoostRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

class BoostViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BoostRepository
    val campaigns: StateFlow<List<Campaign>>
    val tasks: StateFlow<List<CreatorTask>>
    val userStats: StateFlow<UserStats>

    // Exchange Watch Player State
    private val _activePlayerTask = MutableStateFlow<CreatorTask?>(null)
    val activePlayerTask: StateFlow<CreatorTask?> = _activePlayerTask.asStateFlow()

    private val _watchTimerSeconds = MutableStateFlow(0)
    val watchTimerSeconds: StateFlow<Int> = _watchTimerSeconds.asStateFlow()

    private val _isPlayerRunning = MutableStateFlow(false)
    val isPlayerRunning: StateFlow<Boolean> = _isPlayerRunning.asStateFlow()

    private val _isAutoPlayEnabled = MutableStateFlow(true)
    val isAutoPlayEnabled: StateFlow<Boolean> = _isAutoPlayEnabled.asStateFlow()

    private val _celebrationReward = MutableSharedFlow<String>()
    val celebrationReward = _celebrationReward.asSharedFlow()

    private var timerJob: Job? = null
    private var campaignDeliveryJob: Job? = null

    init {
        val db = BoostDatabase.getInstance(application)
        repository = BoostRepository(db.boostDao())

        campaigns = repository.allCampaigns
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        tasks = repository.allTasks
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        userStats = repository.userStats
            .filterNotNull()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                UserStats()
            )

        viewModelScope.launch {
            repository.initializeDefaultsIfNeeded()
            startCampaignDeliverySimulation()
        }
    }

    // Set or switch active video in watch pool
    fun selectTaskForWatch(task: CreatorTask) {
        timerJob?.cancel()
        _activePlayerTask.value = task
        _watchTimerSeconds.value = task.durationSeconds
        _isPlayerRunning.value = true
        startTimer(task)
    }

    private fun startTimer(task: CreatorTask) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_watchTimerSeconds.value > 0 && _isPlayerRunning.value) {
                delay(1000)
                _watchTimerSeconds.value -= 1
            }

            if (_watchTimerSeconds.value <= 0) {
                _isPlayerRunning.value = false
                val current = userStats.value
                repository.markTaskWatched(task, current)
                _celebrationReward.emit("+${task.rewardCoins} Boost Coins Earned!")

                if (_isAutoPlayEnabled.value) {
                    delay(1500)
                    playNextTask()
                }
            }
        }
    }

    fun togglePlayerPlayback() {
        val currentTask = _activePlayerTask.value ?: return
        if (_isPlayerRunning.value) {
            _isPlayerRunning.value = false
            timerJob?.cancel()
        } else {
            _isPlayerRunning.value = true
            startTimer(currentTask)
        }
    }

    fun toggleAutoPlay() {
        _isAutoPlayEnabled.value = !_isAutoPlayEnabled.value
    }

    fun playNextTask() {
        val currentList = tasks.value
        val unwatched = currentList.filter { !it.isWatched }
        val next = unwatched.firstOrNull { it.id != _activePlayerTask.value?.id }
            ?: currentList.firstOrNull { it.id != _activePlayerTask.value?.id }

        next?.let { selectTaskForWatch(it) }
    }

    fun likeCurrentTask() {
        val task = _activePlayerTask.value ?: return
        if (task.isLiked) return
        viewModelScope.launch {
            repository.markTaskLiked(task, userStats.value)
            _celebrationReward.emit("+${task.likeBonusCoins} Like Bonus Added!")
            _activePlayerTask.value = task.copy(isLiked = true)
        }
    }

    fun subscribeCurrentTask() {
        val task = _activePlayerTask.value ?: return
        if (task.isSubscribed) return
        viewModelScope.launch {
            repository.markTaskSubscribed(task, userStats.value)
            _celebrationReward.emit("+${task.subscribeBonusCoins} Subscriber Bonus Added!")
            _activePlayerTask.value = task.copy(isSubscribed = true)
        }
    }

    // Campaign Actions
    fun createCampaign(
        title: String,
        platform: SocialPlatform,
        type: BoostType,
        url: String,
        targetCount: Int,
        watchSeconds: Int,
        isTurbo: Boolean
    ): Boolean {
        val costPerUnit = when (type) {
            BoostType.VIEWS -> if (watchSeconds > 60) (watchSeconds / 60) * 2 else 2
            BoostType.WATCH_HOURS -> 5
            BoostType.FOLLOWERS -> 4
            BoostType.LIKES -> 2
        }
        val totalCost = targetCount * costPerUnit * (if (isTurbo) 2 else 1)

        val current = userStats.value
        if (current.coinBalance < totalCost) return false

        val newCampaign = Campaign(
            title = title.ifBlank { "${platform.displayName} ${type.displayName} Boost" },
            platform = platform,
            type = type,
            targetUrl = url.ifBlank { "https://${platform.name.lowercase()}.com/creator" },
            targetCount = targetCount,
            currentCount = 0,
            watchSecondsPerView = watchSeconds,
            watchHoursDelivered = 0f,
            coinsSpent = totalCost,
            status = CampaignStatus.ACTIVE,
            isTurbo = isTurbo
        )

        viewModelScope.launch {
            repository.createCampaign(newCampaign, current)
            _celebrationReward.emit("Boost Campaign Launched! 🚀")
        }
        return true
    }

    fun toggleCampaignStatus(campaign: Campaign) {
        val updatedStatus = if (campaign.status == CampaignStatus.ACTIVE) {
            CampaignStatus.PAUSED
        } else {
            CampaignStatus.ACTIVE
        }
        viewModelScope.launch {
            repository.updateCampaign(campaign.copy(status = updatedStatus))
        }
    }

    fun deleteCampaign(campaignId: Long) {
        viewModelScope.launch {
            repository.deleteCampaign(campaignId)
        }
    }

    fun turboSpeedCampaign(campaign: Campaign) {
        if (campaign.isTurbo) return
        val turboCost = 50
        val current = userStats.value
        if (current.coinBalance >= turboCost) {
            viewModelScope.launch {
                val updatedStats = current.copy(coinBalance = current.coinBalance - turboCost)
                repository.updateUserStats(updatedStats)
                repository.updateCampaign(campaign.copy(isTurbo = true))
                _celebrationReward.emit("Turbo Speed Activated (2X Delivery Rate)!")
            }
        }
    }

    // Daily Bonus & Lucky Spin
    fun claimDailyStreak(): Boolean {
        val current = userStats.value
        val today = System.currentTimeMillis() / (1000 * 60 * 60 * 24)
        if (current.lastCheckInDay == today) return false

        val reward = (current.dailyStreak + 1) * 35
        val updated = current.copy(
            coinBalance = current.coinBalance + reward,
            dailyStreak = current.dailyStreak + 1,
            lastCheckInDay = today
        )
        viewModelScope.launch {
            repository.updateUserStats(updated)
            _celebrationReward.emit("Streak Reward: +$reward Coins!")
        }
        return true
    }

    fun spinLuckyWheel(): Int {
        val current = userStats.value
        if (current.luckySpinRemaining <= 0) return 0

        val outcomes = listOf(50, 100, 150, 200, 250, 500)
        val wonAmount = outcomes[Random.nextInt(outcomes.size)]

        val updated = current.copy(
            coinBalance = current.coinBalance + wonAmount,
            luckySpinRemaining = 0
        )
        viewModelScope.launch {
            repository.updateUserStats(updated)
            _celebrationReward.emit("JACKPOT! +$wonAmount Coins Won!")
        }
        return wonAmount
    }

    fun addManualCoins(amount: Int) {
        viewModelScope.launch {
            repository.addCoins(amount, userStats.value)
            _celebrationReward.emit("+$amount Coins Added!")
        }
    }

    fun updateMonetizationProgress(addedHours: Float, addedSubs: Int) {
        val current = userStats.value
        val updated = current.copy(
            currentMonetizationWatchHours = (current.currentMonetizationWatchHours + addedHours).coerceAtMost(4000f),
            currentSubscribers = (current.currentSubscribers + addedSubs).coerceAtMost(1000)
        )
        viewModelScope.launch {
            repository.updateUserStats(updated)
        }
    }

    // Dynamic campaign delivery simulation (makes active campaigns live and breathing)
    private fun startCampaignDeliverySimulation() {
        campaignDeliveryJob?.cancel()
        campaignDeliveryJob = viewModelScope.launch {
            while (true) {
                delay(12000) // update every 12 seconds
                val activeList = campaigns.value.filter { it.status == CampaignStatus.ACTIVE }
                for (campaign in activeList) {
                    if (campaign.currentCount < campaign.targetCount) {
                        val increment = if (campaign.isTurbo) Random.nextInt(2, 5) else Random.nextInt(1, 3)
                        val newCount = (campaign.currentCount + increment).coerceAtMost(campaign.targetCount)
                        val additionalHours = (increment * campaign.watchSecondsPerView) / 3600f
                        val newWatchHours = campaign.watchHoursDelivered + additionalHours
                        val newStatus = if (newCount >= campaign.targetCount) CampaignStatus.COMPLETED else CampaignStatus.ACTIVE

                        val updated = campaign.copy(
                            currentCount = newCount,
                            watchHoursDelivered = newWatchHours,
                            status = newStatus
                        )
                        repository.updateCampaign(updated)

                        // Update user stats
                        val stats = userStats.value
                        val newStats = when (campaign.type) {
                            BoostType.VIEWS -> stats.copy(
                                totalViewsBoosted = stats.totalViewsBoosted + increment,
                                totalWatchHoursBoosted = stats.totalWatchHoursBoosted + additionalHours,
                                currentMonetizationWatchHours = (stats.currentMonetizationWatchHours + additionalHours).coerceAtMost(4000f)
                            )
                            BoostType.WATCH_HOURS -> stats.copy(
                                totalWatchHoursBoosted = stats.totalWatchHoursBoosted + additionalHours,
                                currentMonetizationWatchHours = (stats.currentMonetizationWatchHours + additionalHours).coerceAtMost(4000f)
                            )
                            BoostType.FOLLOWERS -> stats.copy(
                                totalFollowersBoosted = stats.totalFollowersBoosted + increment,
                                currentSubscribers = (stats.currentSubscribers + increment).coerceAtMost(1000)
                            )
                            BoostType.LIKES -> stats.copy(
                                totalLikesBoosted = stats.totalLikesBoosted + increment
                            )
                        }
                        repository.updateUserStats(newStats)
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        campaignDeliveryJob?.cancel()
    }
}
