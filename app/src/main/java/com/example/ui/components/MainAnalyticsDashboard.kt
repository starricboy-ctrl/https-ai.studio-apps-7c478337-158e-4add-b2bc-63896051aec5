package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserStats
import com.example.ui.theme.AmberBright
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanBright
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.PinkNeon

enum class AnalyticsPeriod(val label: String, val multiplier: Float, val daysLabel: String) {
    SEVEN_DAYS("7D", 0.35f, "last 7 days"),
    TWENTY_EIGHT_DAYS("28D", 1.0f, "last 28 days"),
    NINETY_DAYS("90D", 2.8f, "last 90 days"),
    ALL_TIME("All", 4.2f, "all-time total")
}

@Composable
fun MainAnalyticsDashboard(
    userStats: UserStats,
    modifier: Modifier = Modifier,
    onBoostFollowersClick: () -> Unit = {},
    onBoostViewsClick: () -> Unit = {},
    onBoostWatchHoursClick: () -> Unit = {}
) {
    var selectedPeriod by remember { mutableStateOf(AnalyticsPeriod.TWENTY_EIGHT_DAYS) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("main_analytics_dashboard"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Section Header with Title & Period Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Channel Performance",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.2).sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(EmeraldSuccess.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "LIVE",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = EmeraldSuccess,
                            fontSize = 9.sp
                        )
                    }
                }
                Text(
                    text = "High-level metrics for FEC Studios TV (${selectedPeriod.daysLabel})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Period Selection Pills
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                AnalyticsPeriod.values().forEach { period ->
                    val isSelected = selectedPeriod == period
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) CyanNeon else Color.Transparent)
                            .clickable { selectedPeriod = period }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = period.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Responsive Animated Metric Values based on Period and Device Width
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val isExpanded = maxWidth >= 640.dp
            AnimatedContent(
                targetState = selectedPeriod,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "period_transition"
            ) { period ->
                val periodViews = (userStats.totalViewsBoosted * period.multiplier).toInt().coerceAtLeast(120)
                val periodFollowers = (userStats.totalFollowersBoosted * period.multiplier).toInt().coerceAtLeast(15)
                val periodWatchHours = (userStats.totalWatchHoursBoosted * period.multiplier).coerceAtLeast(4.5f)

                if (isExpanded) {
                    // Wide screen / Tablet layout: 3 cards side-by-side in a balanced row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        FollowersMetricCard(
                            followerCount = periodFollowers,
                            currentSubs = userStats.currentSubscribers,
                            growthPercent = "+%.1f%%".format(14.8f * (1f + (period.ordinal * 0.15f))),
                            sparklinePoints = listOf(10f, 15f, 14f, 22f, 26f, 35f, 44f),
                            onBoostClick = onBoostFollowersClick,
                            modifier = Modifier.weight(1f)
                        )
                        ViewsMetricCard(
                            viewsCount = periodViews,
                            growthPercent = "+%.1f%%".format(28.4f * (1f + (period.ordinal * 0.12f))),
                            avgDuration = "2m 45s",
                            sparklinePoints = listOf(15f, 18f, 25f, 22f, 34f, 42f, 58f),
                            onBoostClick = onBoostViewsClick,
                            modifier = Modifier.weight(1f)
                        )
                        WatchHoursMetricCard(
                            currentHours = userStats.currentMonetizationWatchHours,
                            boostedHours = periodWatchHours,
                            targetHours = 4000f,
                            onBoostClick = onBoostWatchHoursClick,
                            modifier = Modifier.weight(1.3f)
                        )
                    }
                } else {
                    // Standard Handheld Phone layout: 2 cards on top, prominent Watch Hours card below
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            FollowersMetricCard(
                                followerCount = periodFollowers,
                                currentSubs = userStats.currentSubscribers,
                                growthPercent = "+%.1f%%".format(14.8f * (1f + (period.ordinal * 0.15f))),
                                sparklinePoints = listOf(10f, 15f, 14f, 22f, 26f, 35f, 44f),
                                onBoostClick = onBoostFollowersClick,
                                modifier = Modifier.weight(1f)
                            )
                            ViewsMetricCard(
                                viewsCount = periodViews,
                                growthPercent = "+%.1f%%".format(28.4f * (1f + (period.ordinal * 0.12f))),
                                avgDuration = "2m 45s",
                                sparklinePoints = listOf(15f, 18f, 25f, 22f, 34f, 42f, 58f),
                                onBoostClick = onBoostViewsClick,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        WatchHoursMetricCard(
                            currentHours = userStats.currentMonetizationWatchHours,
                            boostedHours = periodWatchHours,
                            targetHours = 4000f,
                            onBoostClick = onBoostWatchHoursClick,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

/**
 * Material 3 Card displaying Followers & Subscriber Growth with trend sparkline and progress.
 */
@Composable
fun FollowersMetricCard(
    followerCount: Int,
    currentSubs: Int,
    growthPercent: String,
    sparklinePoints: List<Float>,
    onBoostClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.testTag("followers_analytics_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header with Icon & Trend Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(EmeraldSuccess.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = "Followers Icon",
                        tint = EmeraldSuccess,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(EmeraldSuccess.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = "Growth",
                            tint = EmeraldSuccess,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = growthPercent,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldSuccess
                        )
                    }
                }
            }

            // Metric Value & Label
            Column {
                Text(
                    text = "$followerCount",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Followers Gained",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Sparkline Graph
            MetricSparkline(
                dataPoints = sparklinePoints,
                lineColor = EmeraldSuccess,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(26.dp)
            )

            // Milestone progress towards 1k subscribers
            val subProgress = (currentSubs.toFloat() / 1000f).coerceIn(0f, 1f)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Partner Goal",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "$currentSubs / 1,000",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldSuccess,
                        fontSize = 10.sp
                    )
                }
                LinearProgressIndicator(
                    progress = { subProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = EmeraldSuccess,
                    trackColor = MaterialTheme.colorScheme.surface
                )
            }

            // Quick Boost Action Pill
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(EmeraldSuccess.copy(alpha = 0.12f))
                    .clickable(onClick = onBoostClick)
                    .minimumInteractiveComponentSize()
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.RocketLaunch,
                        contentDescription = "Boost",
                        tint = EmeraldSuccess,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Boost Followers",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldSuccess
                    )
                }
            }
        }
    }
}

/**
 * Material 3 Card displaying Video Views with retention duration and surge chart.
 */
@Composable
fun ViewsMetricCard(
    viewsCount: Int,
    growthPercent: String,
    avgDuration: String,
    sparklinePoints: List<Float>,
    onBoostClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.testTag("views_analytics_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header with Icon & Trend Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CyanNeon.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Views Icon",
                        tint = CyanBright,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(CyanNeon.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = "Views Velocity",
                            tint = CyanBright,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = growthPercent,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = CyanBright
                        )
                    }
                }
            }

            // Metric Value & Label
            Column {
                Text(
                    text = if (viewsCount >= 1000) "%.1fk".format(viewsCount / 1000f) else "$viewsCount",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Video Views Boosted",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Sparkline Graph
            MetricSparkline(
                dataPoints = sparklinePoints,
                lineColor = CyanNeon,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(26.dp)
            )

            // Avg retention indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Avg Retention",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
                Text(
                    text = avgDuration,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = CyanBright,
                    fontSize = 10.sp
                )
            }

            // Quick Boost Action Pill
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(CyanNeon.copy(alpha = 0.12f))
                    .clickable(onClick = onBoostClick)
                    .minimumInteractiveComponentSize()
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Boost Views",
                        tint = CyanBright,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Boost Views",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = CyanBright
                    )
                }
            }
        }
    }
}

/**
 * Material 3 Card displaying 4,000 Public Watch Hours progress for monetization qualification.
 */
@Composable
fun WatchHoursMetricCard(
    currentHours: Float,
    boostedHours: Float,
    targetHours: Float = 4000f,
    onBoostClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = (currentHours / targetHours).coerceIn(0f, 1f)
    val remainingHours = (targetHours - currentHours).coerceAtLeast(0f)

    OutlinedCard(
        modifier = modifier.testTag("watch_hours_analytics_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, AmberGold.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row: Badge & Monetization Goal Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(AmberGold.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.HourglassTop,
                            contentDescription = "Watch Hours",
                            tint = AmberBright,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "Public Watch Hours",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "YouTube Partner Program (4,000h Goal)",
                            style = MaterialTheme.typography.labelSmall,
                            color = AmberBright
                        )
                    }
                }

                // Completion Percentage Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(AmberGold.copy(alpha = 0.2f))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "%.1f%%".format(progress * 100),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = AmberBright
                    )
                }
            }

            // Big Numbers & Remaining Hours
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "%.1f".format(currentHours),
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.5).sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = " / 4,000 hrs",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                        )
                    }

                    Text(
                        text = "+%.1fh generated via active booster pool".format(boostedHours),
                        style = MaterialTheme.typography.bodySmall,
                        color = CyanBright
                    )
                }

                // Remaining Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "%.0fh needed".format(remainingHours),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Full-Width Progress Indicator
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = AmberGold,
                trackColor = MaterialTheme.colorScheme.surface
            )

            // Footer Row: Pace estimation & Boost Action Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "At current pace: ~${(remainingHours / 12.5f).toInt()} days to monetization",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(AmberGold)
                        .clickable(onClick = onBoostClick)
                        .minimumInteractiveComponentSize()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.RocketLaunch,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Boost Hours",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

/**
 * Lightweight smooth Canvas sparkline curve for trend visualization.
 */
@Composable
fun MetricSparkline(
    dataPoints: List<Float>,
    lineColor: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        if (dataPoints.size < 2) return@Canvas

        val maxVal = dataPoints.maxOrNull() ?: 1f
        val minVal = dataPoints.minOrNull() ?: 0f
        val range = (maxVal - minVal).coerceAtLeast(1f)

        val w = size.width
        val h = size.height
        val stepX = w / (dataPoints.size - 1)

        val path = Path()
        val fillPath = Path()

        dataPoints.forEachIndexed { index, value ->
            val x = index * stepX
            val normalizedY = 1f - ((value - minVal) / range)
            val y = normalizedY * (h - 6f) + 3f

            if (index == 0) {
                path.moveTo(x, y)
                fillPath.moveTo(x, h)
                fillPath.lineTo(x, y)
            } else {
                val prevX = (index - 1) * stepX
                val prevY = (1f - ((dataPoints[index - 1] - minVal) / range)) * (h - 6f) + 3f
                val cX = (prevX + x) / 2f
                path.cubicTo(cX, prevY, cX, y, x, y)
                fillPath.cubicTo(cX, prevY, cX, y, x, y)
            }

            if (index == dataPoints.size - 1) {
                fillPath.lineTo(x, h)
                fillPath.close()
            }
        }

        // Draw gradient fill below curve
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                listOf(lineColor.copy(alpha = 0.25f), Color.Transparent)
            )
        )

        // Draw stroke line
        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
        )

        // Draw ending dot
        val lastX = w
        val lastY = (1f - ((dataPoints.last() - minVal) / range)) * (h - 6f) + 3f
        drawCircle(
            color = lineColor,
            radius = 3.5.dp.toPx(),
            center = Offset(lastX, lastY)
        )
    }
}
