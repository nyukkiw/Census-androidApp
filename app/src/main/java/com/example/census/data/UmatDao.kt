package com.example.census.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UmatDao {

    @Query("SELECT * FROM umat ORDER BY namaLengkap ASC")
    fun getAll(): Flow<List<UmatEntity>>

    @Query("""
        SELECT * FROM umat 
        WHERE namaLengkap LIKE '%' || :query || '%'
        OR nomorNIK LIKE '%' || :query || '%'
        OR nomorKK LIKE '%' || :query || '%'
        ORDER BY namaLengkap ASC
    """)
    fun search(query: String): Flow<List<UmatEntity>>

    @Query("SELECT * FROM umat WHERE id = :id")
    suspend fun getById(id: Int): UmatEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(umat: UmatEntity)

    @Update
    suspend fun update(umat: UmatEntity)

    @Delete
    suspend fun delete(umat: UmatEntity)

    @Query("DELETE FROM umat")
    suspend fun deleteAll()

    @Query("SELECT * FROM umat ORDER BY namaLengkap ASC")
    suspend fun getAllOnce(): List<UmatEntity>
}