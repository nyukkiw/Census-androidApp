package com.example.census.repository

import com.example.census.data.UmatDao
import com.example.census.data.UmatEntity
import kotlinx.coroutines.flow.Flow

class UmatRepository(private val dao: UmatDao) {

    fun getAll(): Flow<List<UmatEntity>> = dao.getAll()

    fun search(query: String): Flow<List<UmatEntity>> = dao.search(query)

    suspend fun getById(id: Int): UmatEntity? = dao.getById(id)

    suspend fun insert(umat: UmatEntity) = dao.insert(umat)

    suspend fun update(umat: UmatEntity) = dao.update(umat)

    suspend fun delete(umat: UmatEntity) = dao.delete(umat)

    suspend fun getAllOnce(): List<UmatEntity> = dao.getAllOnce()

    suspend fun deleteAll() = dao.deleteAll()
}