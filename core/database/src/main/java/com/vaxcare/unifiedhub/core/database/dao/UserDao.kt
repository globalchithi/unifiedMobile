package com.vaxcare.unifiedhub.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.vaxcare.unifiedhub.core.database.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class UserDao {
    @Query("DELETE FROM Users")
    abstract suspend fun deleteAll()

    @Query("SELECT * FROM Users")
    abstract fun getAll(): Flow<List<UserEntity>>

    @Query("SELECT * FROM Users WHERE pin = :pin")
    abstract suspend fun getUserByPin(pin: String): UserEntity?

    @Insert
    abstract suspend fun insertAll(data: List<UserEntity>)
}
