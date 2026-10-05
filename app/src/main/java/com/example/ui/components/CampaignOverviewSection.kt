package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BoostType
import com.example.data.model.Campaign
import com.example.data.model.CampaignStatus
import com.example.ui.theme.AmberBright
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanBright
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.PinkNeon

/**
 * 'Campaign Overview' dashboard section that displays active campaign status,
 * remaining budget, and current growth progress using a vertical list of Material 3 cards.
 */
@Composable
fun CampaignOverviewSection(
    campaigns: List<Campaign>,
    onToggleStatus: (Campaign) -> Unit,
    onDeleteCampaign: (Long) -> Unit,
    onTurboSpeed: (Campaign) -> Unit,
    onLaunchNewCampaign: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableIntStateOf(0) } // 0: Active, 1: All, 2: Completed

    val filteredCampaigns = remember(campaigns, selectedFilter) {
        when (selectedFilter) {
            0 -> campaigns.filter { it.status == CampaignStatus.ACTIVE }
            2 -> campaigns.filter { it.status == CampaignStatus.COMPLETED }
            else -> campaigns
        }
    }

    val activeCount = campaigns.count { it.status == CampaignStatus.ACTIVE }
    val totalRemainingBudget = campaigns
        .filter { it.status == CampaignStatus.ACTIVE }
        .sumOf { calculateRemainingBudget(it) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("campaign_overview_section"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Campaign Overview",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.2).sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "$activeCount active campaigns • $totalRemainingBudget coins remaining",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Quick Launch Button in Header
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(CyanNeon.copy(alpha = 0.15f))
                    .clickable(onClick = onLaunchNewCampaign)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("overview_new_campaign_btn")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Campaign",
                        tint = CyanBright,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "New Boost",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = CyanBright
                    )
                }
            }
        }

        // Budget Summary Banner (Aggregated across active campaigns)
        if (activeCount > 0) {
            CampaignBudgetSummaryBanner(
                activeCount = activeCount,
                totalRemainingBudget = totalRemainingBudget,
                totalAllocatedBudget = campaigns.filter { it.status == CampaignStatus.ACTIVE }.sumOf { it.coinsSpent }
            )
        }

        // Filter Chips Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedFilter == 0,
                onClick = { selectedFilter = 0 },
                label = { Text("Active ($activeCount)") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CyanNeon,
                    selectedLabelColor = Color.Black
                ),
                modifier = Modifier.testTag("filter_chip_active")
            )
            FilterChip(
                selected = selectedFilter == 1,
                onClick = { selectedFilter = 1 },
                label = { Text("All (${campaigns.size})") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CyanNeon,
                    selectedLabelColor = Color.Black
                ),
                modifier = Modifier.testTag("filter_chip_all")
            )
            FilterChip(
                selected = selectedFilter == 2,
                onClick = { selectedFilter = 2 },
                label = { Text("Completed (${campaigns.count { it.status == CampaignStatus.COMPLETED }})") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CyanNeon,
                    selectedLabelColor = Color.Black
                ),
                modifier = Modifier.testTag("filter_chip_completed")
            )
        }

        // Vertical List of Campaign Overview Cards
        if (filteredCampaigns.isEmpty()) {
            EmptyCampaignOverviewCard(
                filterState = selectedFilter,
                onLaunchClick = onLaunchNewCampaign
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                filteredCampaigns.forEach { campaign ->
                    CampaignOverviewCard(
                        campaign = campaign,
                        onToggleStatus = { onToggleStatus(campaign) },
                        onDelete = { onDeleteCampaign(campaign.id) },
                        onTurbo = { onTurboSpeed(campaign) }
                    )
                }
            }
        }
    }
}

/**
 * Material 3 Card displaying individual Campaign details:
 * - Active Campaign Status (Pulsing badge, Turbo indicator, Status Tag)
 * - Remaining Budget (Coins remaining vs initial budget, Spent fraction)
 * - Current Growth Progress (Delivered vs target count, Watch hours, Progress bar)
 */
@Composable
fun CampaignOverviewCard(
    campaign: Campaign,
    onToggleStatus: () -> Unit,
    onDelete: () -> Unit,
    onTurbo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = if (campaign.targetCount > 0) {
        (campaign.currentCount.toFloat() / campaign.targetCount).coerceIn(0f, 1f)
    } else 0f

    val remainingCount = (campaign.targetCount - campaign.currentCount).coerceAtLeast(0)
    val remainingBudget = calculateRemainingBudget(campaign)
    val spentBudget = (campaign.coinsSpent - remainingBudget).coerceAtLeast(0)

    val isActive = campaign.status == CampaignStatus.ACTIVE
    val isCompleted = campaign.status == CampaignStatus.COMPLETED
    val isPaused = campaign.status == CampaignStatus.PAUSED

    // Pulsing animation for active campaigns
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_status")
    val statusPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "status_alpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("campaign_overview_card_${campaign.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = if (campaign.isTurbo && isActive) {
            androidx.compose.foundation.BorderStroke(1.5.dp, AmberGold)
        } else null
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row: Platform & Status Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Platform & Boost Type Pills
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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

                    Text(
                        text = campaign.type.displayName,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Campaign Status Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.testTag("campaign_status_${campaign.id}")
                ) {
                    if (campaign.isTurbo && isActive) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AmberGold)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = "Turbo",
                                    tint = Color.Black,
                                    modifier = Modifier.size(11.dp)
                                )
                                Text(
                                    text = "2X TURBO",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                    color = Color.Black,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }

                    // Main Status Indicator
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                when {
                                    isActive -> EmeraldSuccess.copy(alpha = 0.15f)
                                    isPaused -> AmberGold.copy(alpha = 0.15f)
                                    else -> CyanNeon.copy(alpha = 0.15f)
                                }
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Pulsing dot for active status
                            if (isActive) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldSuccess.copy(alpha = statusPulseAlpha))
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                            }
                            Text(
                                text = when {
                                    isActive -> "ACTIVE"
                                    isPaused -> "PAUSED"
                                    else -> "COMPLETED"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = when {
                                    isActive -> EmeraldSuccess
                                    isPaused -> AmberBright
                                    else -> CyanBright
                                }
                            )
                        }
                    }
                }
            }

            // Campaign Title
            Text(
                text = campaign.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Current Growth Progress Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("campaign_growth_progress_${campaign.id}"),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Current Growth Progress",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "%.1f%%".format(progress * 100),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = if (isCompleted) EmeraldSuccess else CyanBright
                    )
                }

                // Growth Progress Bar
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (isCompleted) EmeraldSuccess else CyanNeon,
                    trackColor = MaterialTheme.colorScheme.surface
                )

                // Counts & Delivered Details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${campaign.currentCount} / ${campaign.targetCount} ${campaign.type.unitLabel} delivered",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isCompleted) "Completed" else "$remainingCount remaining",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (campaign.type == BoostType.WATCH_HOURS || campaign.type == BoostType.VIEWS) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Watch Hours Added: +%.1fh".format(campaign.watchHoursDelivered),
                            style = MaterialTheme.typography.labelSmall,
                            color = AmberGold
                        )
                        Text(
                            text = "${campaign.watchSecondsPerView}s / view retention",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Remaining Budget Section
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("campaign_remaining_budget_${campaign.id}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AmberGold.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = "Budget",
                                tint = AmberBright,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Remaining Budget",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$remainingBudget Coins",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = if (remainingBudget > 0) AmberBright else EmeraldSuccess
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Allocated Budget",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${campaign.coinsSpent} Coins ($spentBudget spent)",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Action Buttons Row: Turbo Speed, Pause/Resume, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isActive && !campaign.isTurbo) {
                    TextButton(
                        onClick = onTurbo,
                        modifier = Modifier.testTag("turbo_btn_${campaign.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Turbo",
                            tint = AmberGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Activate 2X Turbo",
                            color = AmberGold,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
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
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete Campaign",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * Summary banner showcasing total allocated vs remaining budget across campaigns.
 */
@Composable
fun CampaignBudgetSummaryBanner(
    activeCount: Int,
    totalRemainingBudget: Int,
    totalAllocatedBudget: Int,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier.fillMaxWidth()
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
                        .clip(CircleShape)
                        .background(AmberGold.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Budget Pool",
                        tint = AmberGold,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Active Budget Pool",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$totalRemainingBudget Coins Remaining",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = AmberBright
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Total Allocated",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$totalAllocatedBudget Coins",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun EmptyCampaignOverviewCard(
    filterState: Int,
    onLaunchClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(CyanNeon.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.RocketLaunch,
                    contentDescription = null,
                    tint = CyanNeon,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if (filterState == 2) "No Completed Campaigns Yet" else "No Active Campaigns in Pool",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Launch a growth campaign to boost video views, 4,000h watch time, subscribers, or likes through our creator community pool.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onLaunchClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyanNeon),
                modifier = Modifier.testTag("empty_overview_launch_btn")
            ) {
                Text("Launch Growth Boost 🚀", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * Calculates remaining budget in coins based on delivery ratio.
 */
private fun calculateRemainingBudget(campaign: Campaign): Int {
    if (campaign.targetCount <= 0) return 0
    val remainingUnits = (campaign.targetCount - campaign.currentCount).coerceAtLeast(0)
    return ((campaign.coinsSpent.toDouble() * remainingUnits) / campaign.targetCount).toInt()
}
