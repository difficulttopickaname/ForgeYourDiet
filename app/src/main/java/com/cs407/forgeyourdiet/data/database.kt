package com.cs407.forgeyourdiet.data

import androidx.room.Entity
import androidx.room.PrimaryKey

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import org.mindrot.jbcrypt.BCrypt


@Entity(tableName = "users")
data class User(
    @PrimaryKey val username: String,
    val passwordHash: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): User?

}


@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "forge_your_diet_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}


class AuthRepository(context: Context) {
    private val userDao = AppDatabase.getDatabase(context).userDao()

    suspend fun registerUser(username: String,  password: String): Boolean {
        if (userDao.getUserByUsername(username) != null ) {
            return false
        }

        // Hash the password
        val passwordHash = BCrypt.hashpw(password, BCrypt.gensalt())

        // Create and insert new user
        val newUser = User(username, passwordHash)
        userDao.insertUser(newUser)
        return true
    }

    suspend fun loginUser(username: String, password: String): Boolean {
        val user = userDao.getUserByUsername(username) ?: return false
        return BCrypt.checkpw(password, user.passwordHash)
    }
}