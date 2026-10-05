package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.BoostType
import com.example.data.model.CampaignStatus
import com.example.data.model.SocialPlatform

class BoostConverters {
    @TypeConverter
    fun fromBoostType(value: BoostType): String = value.name

    @TypeConverter
    fun toBoostType(value: String): BoostType = runCatching { BoostType.valueOf(value) }.getOrDefault(BoostType.VIEWS)

    @TypeConverter
    fun fromSocialPlatform(value: SocialPlatform): String = value.name

    @TypeConverter
    fun toSocialPlatform(value: String): SocialPlatform = runCatching { SocialPlatform.valueOf(value) }.getOrDefault(SocialPlatform.YOUTUBE)

    @TypeConverter
    fun fromCampaignStatus(value: CampaignStatus): String = value.name

    @TypeConverter
    fun toCampaignStatus(value: String): CampaignStatus = runCatching { CampaignStatus.valueOf(value) }.getOrDefault(CampaignStatus.ACTIVE)
}
