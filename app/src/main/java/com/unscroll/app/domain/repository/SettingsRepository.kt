package com.unscroll.app.domain.repository

import com.unscroll.app.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getUserProfile(): Flow<UserProfile>
    suspend fun updateUserName(name: String)
    suspend fun updateInterruptionInterval(intervalMinutes: Int)
    
    fun getTrackedPackageNames(): Flow<Set<String>>
    suspend fun setTrackedPackageNames(packages: Set<String>)
    suspend fun addTrackedPackage(packageName: String)
    suspend fun removeTrackedPackage(packageName: String)
    
    fun isOnboardingCompleted(): Flow<Boolean>
    suspend fun setOnboardingCompleted(completed: Boolean)
}
