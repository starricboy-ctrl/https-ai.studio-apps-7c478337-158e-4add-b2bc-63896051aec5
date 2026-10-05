package com.example.ui.screens.booster

import androidx.compose.animation.AnimatedVisibility
import com.example.ui.components.CampaignOverviewSection
import com.example.ui.components.CreatorAnalyticsReportSheet
import com.example.ui.components.MainAnalyticsDashboard
import com.example.ui.components.QuickCreatorActionFab
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.BoostType
import com.example.data.model.Campaign
import com.example.data.model.CampaignStatus
import com.example.data.model.SocialPlatform
import com.example.data.model.UserStats
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanBright
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.PinkNeon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoosterScreen(
    campaigns: List<Campaign>,
    userStats: UserStats,
    onCreateCampaign: (String, SocialPlatform, BoostType, String, Int, Int, Boolean) -> Boolean,
    onToggleStatus: (Campaign) -> Unit,
    onDeleteCampaign: (Long) -> Unit,
    onTurboSpeed: (Campaign) -> Unit,
    onNavigateToSeo: () -> Unit = {},
    onOpenCoinShop: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showCreateSheet by remember { mutableStateOf(false) }
    var showReportsSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            QuickCreatorActionFab(
                onCreateCampaign = { showCreateSheet = true },
                onViewReports = { showReportsSheet = true },
                onOpenAiSeo = onNavigateToSeo,
                onOpenCoinShop = onOpenCoinShop
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Hero Banner
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("booster_hero_banner")
                ) {
                    Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.img_boost_banner_1791150047615),
                            contentDescription = "Viral Booster Banner",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.Transparent,
                                            Color(0xCC090D16),
                                            Color(0xF0090D16)
                                        )
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(CyanNeon)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "COMMUNITY EXCHANGE ENGINE",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color.Black
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Accelerate Views, Likes & 4000h Watch Time",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // High-Level Analytics Dashboard utilizing Material 3 cards for Followers, Views, and Watch Hours
            item {
                MainAnalyticsDashboard(
                    userStats = userStats,
                    onBoostFollowersClick = { showCreateSheet = true },
                    onBoostViewsClick = { showCreateSheet = true },
                    onBoostWatchHoursClick = { showCreateSheet = true }
                )
            }

            // 'Campaign Overview' section: Displays active campaign status, remaining budget, and growth progress
            item {
                CampaignOverviewSection(
                    campaigns = campaigns,
                    onToggleStatus = onToggleStatus,
                    onDeleteCampaign = onDeleteCampaign,
                    onTurboSpeed = onTurboSpeed,
                    onLaunchNewCampaign = { showCreateSheet = true }
                )
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }

    if (showCreateSheet) {
        CreateCampaignBottomSheet(
            currentCoins = userStats.coinBalance,
            onDismiss = { showCreateSheet = false },
            onSubmit = { title, platform, type, url, count, watchSec, turbo ->
                val success = onCreateCampaign(title, platform, type, url, count, watchSec, turbo)
                if (success) showCreateSheet = false
            }
        )
    }

    if (showReportsSheet) {
        CreatorAnalyticsReportSheet(
            userStats = userStats,
            campaigns = campaigns,
            onDismiss = { showReportsSheet = false }
        )
    }
}

@Composable
fun MetricMiniCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(tint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun CampaignCard(
    campaign: Campaign,
    onToggleStatus: () -> Unit,
    onDelete: () -> Unit,
    onTurbo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = if (campaign.targetCount > 0) {
        campaign.currentCount.toFloat() / campaign.targetCount
    } else 0f

    val isCompleted = campaign.status == CampaignStatus.COMPLETED
    val isActive = campaign.status == CampaignStatus.ACTIVE

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("campaign_card_${campaign.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = if (campaign.isTurbo && isActive) {
            androidx.compose.foundation.BorderStroke(1.5.dp, AmberGold)
        } else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Platform badge & Type
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(campaign.platform.colorHex).copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = campaign.platform.displayName,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(campaign.platform.colorHex)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = campaign.type.displayName,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Turbo badge or status
                if (campaign.isTurbo) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AmberGold)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
                            Text("2X TURBO", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold), color = Color.Black)
                        }
                    }
                } else if (isCompleted) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(EmeraldSuccess.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Completed", style = MaterialTheme.typography.labelSmall, color = EmeraldSuccess)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Campaign Title
            Text(
                text = campaign.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (isCompleted) EmeraldSuccess else CyanNeon,
                trackColor = MaterialTheme.colorScheme.surface
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Delivery Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Delivered: ${campaign.currentCount} / ${campaign.targetCount} ${campaign.type.unitLabel}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (campaign.type == BoostType.WATCH_HOURS || campaign.type == BoostType.VIEWS) {
                        Text(
                            text = "+%.1f Watch Hours generated".format(campaign.watchHoursDelivered),
                            style = MaterialTheme.typography.labelSmall,
                            color = AmberGold
                        )
                    }
                }

                // Controls
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isActive && !campaign.isTurbo) {
                        TextButton(
                            onClick = onTurbo,
                            modifier = Modifier.testTag("turbo_btn_${campaign.id}")
                        ) {
                            Icon(Icons.Default.Bolt, contentDescription = null, tint = AmberGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Turbo", color = AmberGold, style = MaterialTheme.typography.labelMedium)
                        }
                    }

                    if (!isCompleted) {
                        IconButton(
                            onClick = onToggleStatus,
                            modifier = Modifier.testTag("toggle_status_${campaign.id}")
                        ) {
                            Icon(
                                imageVector = if (isActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isActive) "Pause" else "Resume",
                                tint = if (isActive) MaterialTheme.colorScheme.onSurfaceVariant else CyanNeon
                            )
                        }
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.testTag("delete_campaign_${campaign.id}")
                    ) {
                        Icon(
                            Icons.Default.DeleteOutline,
                            contentDescription = "Delete Campaign",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyCampaignCard(filterTab: Int, onLaunchClicked: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(CyanNeon.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.RocketLaunch,
                    contentDescription = null,
                    tint = CyanNeon,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "No Active Campaigns",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Launch a growth campaign to boost video views, watch time towards 4,000h, subscribers, and likes through the creator exchange pool.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onLaunchClicked,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyanNeon),
                modifier = Modifier.testTag("empty_launch_button")
            ) {
                Text("Launch My First Boost", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCampaignBottomSheet(
    currentCoins: Int,
    onDismiss: () -> Unit,
    onSubmit: (String, SocialPlatform, BoostType, String, Int, Int, Boolean) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedPlatform by remember { mutableStateOf(SocialPlatform.YOUTUBE) }
    var selectedType by remember { mutableStateOf(BoostType.VIEWS) }
    var title by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var targetCount by remember { mutableIntStateOf(100) }
    var watchSeconds by remember { mutableIntStateOf(60) }
    var isTurbo by remember { mutableStateOf(false) }

    val costPerUnit = when (selectedType) {
        BoostType.VIEWS -> if (watchSeconds > 60) (watchSeconds / 60) * 2 else 2
        BoostType.WATCH_HOURS -> 5
        BoostType.FOLLOWERS -> 4
        BoostType.LIKES -> 2
    }
    val totalCoinCost = targetCount * costPerUnit * (if (isTurbo) 2 else 1)
    val hasEnoughCoins = currentCoins >= totalCoinCost

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Launch Growth Campaign",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(AmberGold.copy(alpha = 0.2f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "$currentCoins Coins Available",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = AmberGold
                        )
                    }
                }
            }

            // Platform Selection
            item {
                Text(
                    text = "1. Select Platform",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SocialPlatform.values().forEach { platform ->
                        val isSelected = selectedPlatform == platform
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) Color(platform.colorHex).copy(alpha = 0.25f)
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) Color(platform.colorHex) else Color.Transparent,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedPlatform = platform }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = platform.displayName,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) Color(platform.colorHex) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Objective / Boost Type Selection
            item {
                Text(
                    text = "2. Select Boost Objective",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    BoostType.values().forEach { type ->
                        val isSelected = selectedType == type
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) CyanNeon.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, CyanNeon) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedType = type }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = when (type) {
                                        BoostType.VIEWS -> Icons.Default.PlayArrow
                                        BoostType.WATCH_HOURS -> Icons.Default.Schedule
                                        BoostType.FOLLOWERS -> Icons.Default.TrendingUp
                                        BoostType.LIKES -> Icons.Default.Favorite
                                    },
                                    contentDescription = null,
                                    tint = if (isSelected) CyanNeon else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = type.displayName,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = when (type) {
                                            BoostType.VIEWS -> "Boost views with customizable retention duration"
                                            BoostType.WATCH_HOURS -> "Target high duration watch time towards 4,000h requirement"
                                            BoostType.FOLLOWERS -> "Real creator peer subscriptions and channel followers"
                                            BoostType.LIKES -> "Instant engagement signal to trigger algorithm recommendations"
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Title & URL Input
            item {
                Text(
                    text = "3. Video / Channel Details",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Campaign or Video Title") },
                    placeholder = { Text("e.g. My Best Hardcore Gameplay") },
                    modifier = Modifier.fillMaxWidth().testTag("input_campaign_title"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanNeon,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("URL Link (Video or Profile)") },
                    placeholder = { Text("https://${selectedPlatform.displayName.lowercase()}.com/...") },
                    modifier = Modifier.fillMaxWidth().testTag("input_campaign_url"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanNeon,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
            }

            // Target Count Selector
            item {
                Text(
                    text = "4. Target Quantity (${selectedType.unitLabel})",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(50, 100, 250, 500, 1000).forEach { count ->
                        val isSelected = targetCount == count
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) CyanNeon else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { targetCount = count }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$count",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Watch Duration Per View (If Views or Watch Hours)
            if (selectedType == BoostType.VIEWS || selectedType == BoostType.WATCH_HOURS) {
                item {
                    Text(
                        text = "Retention Watch Duration Per View",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(60, 120, 180, 300).forEach { sec ->
                            val isSelected = watchSeconds == sec
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) AmberGold else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { watchSeconds = sec }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${sec}s",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Turbo Delivery Mode
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(AmberGold.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Bolt, contentDescription = null, tint = AmberGold)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Turbo Delivery (2X Speed)",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Prioritizes your video at top of exchange pool",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Switch(
                            checked = isTurbo,
                            onCheckedChange = { isTurbo = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = AmberGold
                            )
                        )
                    }
                }
            }

            // Total Cost & Launch Button
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Total Campaign Cost",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$totalCoinCost Coins",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                            color = if (hasEnoughCoins) AmberGold else PinkNeon
                        )
                    }

                    if (!hasEnoughCoins) {
                        Text(
                            text = "Need ${totalCoinCost - currentCoins} more coins",
                            style = MaterialTheme.typography.labelSmall,
                            color = PinkNeon
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        onSubmit(
                            title,
                            selectedPlatform,
                            selectedType,
                            url,
                            targetCount,
                            watchSeconds,
                            isTurbo
                        )
                    },
                    enabled = hasEnoughCoins,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanNeon,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_launch_campaign_button")
                ) {
                    Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (hasEnoughCoins) "Confirm & Launch Campaign 🚀" else "Insufficient Coins",
                        color = if (hasEnoughCoins) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}
