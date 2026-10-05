package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AppTab
import com.example.ui.components.BoostBottomNavigation
import com.example.ui.components.BoostNavigationRail
import com.example.ui.components.CoinGrantDialog
import com.example.ui.components.TopGrowthBar
import com.example.ui.screens.booster.BoosterScreen
import com.example.ui.screens.exchange.ExchangeScreen
import com.example.ui.screens.profile.ProfileQuestsScreen
import com.example.ui.screens.seotools.SeoStudioScreen
import com.example.ui.screens.watchtracker.WatchHoursTrackerScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.BoostViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContent()
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: BoostViewModel = viewModel()) {
    val campaigns by viewModel.campaigns.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val userStats by viewModel.userStats.collectAsStateWithLifecycle()

    val activeTask by viewModel.activePlayerTask.collectAsStateWithLifecycle()
    val watchTimerSeconds by viewModel.watchTimerSeconds.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlayerRunning.collectAsStateWithLifecycle()
    val isAutoPlayEnabled by viewModel.isAutoPlayEnabled.collectAsStateWithLifecycle()

    var currentTab by remember { mutableStateOf(AppTab.BOOSTER) }
    var showCoinShop by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    // Listen for celebration alerts
    LaunchedEffect(Unit) {
        viewModel.celebrationReward.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Back handling: If on secondary tab, back button returns to Booster tab
    BackHandler(enabled = currentTab != AppTab.BOOSTER) {
        currentTab = AppTab.BOOSTER
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isExpandedLayout = maxWidth >= 720.dp

        if (isExpandedLayout) {
            // Adaptive Tablet / Foldable / Desktop layout: Side Navigation Rail + Centered Content
            Row(modifier = Modifier.fillMaxSize()) {
                BoostNavigationRail(
                    currentTab = currentTab,
                    onTabSelected = { currentTab = it }
                )

                Scaffold(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    containerColor = MaterialTheme.colorScheme.background,
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    topBar = {
                        TopGrowthBar(
                            userStats = userStats,
                            onAddCoinsClicked = { showCoinShop = true },
                            onStreakClicked = { viewModel.claimDailyStreak() }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Box(modifier = Modifier.widthIn(max = 1100.dp)) {
                            TabContent(
                                currentTab = currentTab,
                                campaigns = campaigns,
                                userStats = userStats,
                                activeTask = activeTask,
                                watchTimerSeconds = watchTimerSeconds,
                                isPlaying = isPlaying,
                                isAutoPlayEnabled = isAutoPlayEnabled,
                                viewModel = viewModel,
                                onNavigateToBooster = { currentTab = AppTab.BOOSTER },
                                onNavigateToSeo = { currentTab = AppTab.SEO_TOOLS },
                                onOpenCoinShop = { showCoinShop = true }
                            )
                        }
                    }
                }
            }
        } else {
            // Standard Handheld Mobile layout: Bottom Navigation
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background,
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    TopGrowthBar(
                        userStats = userStats,
                        onAddCoinsClicked = { showCoinShop = true },
                        onStreakClicked = { viewModel.claimDailyStreak() }
                    )
                },
                bottomBar = {
                    BoostBottomNavigation(
                        currentTab = currentTab,
                        onTabSelected = { currentTab = it }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    TabContent(
                        currentTab = currentTab,
                        campaigns = campaigns,
                        userStats = userStats,
                        activeTask = activeTask,
                        watchTimerSeconds = watchTimerSeconds,
                        isPlaying = isPlaying,
                        isAutoPlayEnabled = isAutoPlayEnabled,
                        viewModel = viewModel,
                        onNavigateToBooster = { currentTab = AppTab.BOOSTER },
                        onNavigateToSeo = { currentTab = AppTab.SEO_TOOLS },
                        onOpenCoinShop = { showCoinShop = true }
                    )
                }
            }
        }
    }

    if (showCoinShop) {
        CoinGrantDialog(
            currentCoins = userStats.coinBalance,
            onGrantCoins = { amount -> viewModel.addManualCoins(amount) },
            onDismiss = { showCoinShop = false }
        )
    }
}

@Composable
private fun TabContent(
    currentTab: AppTab,
    campaigns: List<com.example.data.model.Campaign>,
    userStats: com.example.data.model.UserStats,
    activeTask: com.example.data.model.CreatorTask?,
    watchTimerSeconds: Int,
    isPlaying: Boolean,
    isAutoPlayEnabled: Boolean,
    viewModel: BoostViewModel,
    onNavigateToBooster: () -> Unit,
    onNavigateToSeo: () -> Unit,
    onOpenCoinShop: () -> Unit
) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = currentTab,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "tab_transition",
        modifier = Modifier.fillMaxSize()
    ) { tab ->
        when (tab) {
            AppTab.BOOSTER -> {
                BoosterScreen(
                    campaigns = campaigns,
                    userStats = userStats,
                    onCreateCampaign = { title, platform, type, url, count, watchSec, turbo ->
                        viewModel.createCampaign(
                            title,
                            platform,
                            type,
                            url,
                            count,
                            watchSec,
                            turbo
                        )
                    },
                    onToggleStatus = { campaign ->
                        viewModel.toggleCampaignStatus(campaign)
                    },
                    onDeleteCampaign = { id ->
                        viewModel.deleteCampaign(id)
                    },
                    onTurboSpeed = { campaign ->
                        viewModel.turboSpeedCampaign(campaign)
                    },
                    onNavigateToSeo = onNavigateToSeo,
                    onOpenCoinShop = onOpenCoinShop
                )
            }

            AppTab.EXCHANGE -> {
                ExchangeScreen(
                    tasks = tasks,
                    userStats = userStats,
                    activeTask = activeTask,
                    watchSecondsRemaining = watchTimerSeconds,
                    isPlaying = isPlaying,
                    isAutoPlayEnabled = isAutoPlayEnabled,
                    onSelectTask = { task -> viewModel.selectTaskForWatch(task) },
                    onTogglePlay = { viewModel.togglePlayerPlayback() },
                    onToggleAutoPlay = { viewModel.toggleAutoPlay() },
                    onPlayNext = { viewModel.playNextTask() },
                    onLikeTask = { viewModel.likeCurrentTask() },
                    onSubscribeTask = { viewModel.subscribeCurrentTask() },
                    onClaimStreak = { viewModel.claimDailyStreak() },
                    onSpinWheel = { viewModel.spinLuckyWheel() }
                )
            }

            AppTab.WATCH_HOURS -> {
                WatchHoursTrackerScreen(
                    userStats = userStats,
                    onUpdateStats = { addedHours, addedSubs ->
                        viewModel.updateMonetizationProgress(addedHours, addedSubs)
                    },
                    onNavigateToBooster = onNavigateToBooster
                )
            }

            AppTab.SEO_TOOLS -> {
                SeoStudioScreen()
            }

            AppTab.COMMUNITY -> {
                ProfileQuestsScreen(
                    userStats = userStats,
                    onClaimQuest = { coins -> viewModel.addManualCoins(coins) },
                    onOpenCoinShop = onOpenCoinShop
                )
            }
        }
    }
}
