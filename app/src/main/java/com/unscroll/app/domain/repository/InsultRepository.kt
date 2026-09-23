package com.unscroll.app.domain.repository

import com.unscroll.app.domain.model.Insult
import kotlinx.coroutines.flow.Flow

interface InsultRepository {
    suspend fun getNextInsult(userName: String?): Insult
    fun getCurrentInsultIndex(): Flow<Int>
    suspend fun advanceInsultIndex()
}
