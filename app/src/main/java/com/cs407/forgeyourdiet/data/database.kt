package com.cs407.forgeyourdiet.data

import androidx.room.Entity
import androidx.room.PrimaryKey

import android.content.Context
import android.util.Log
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

@Entity(
    tableName = "user_status",
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = User::class,
            parentColumns = ["username"],
            childColumns = ["username"],
            onDelete = androidx.room.ForeignKey.CASCADE // Delete stats if user is deleted
        )
    ],
    indices = [androidx.room.Index("username")] // For faster querying
)
data class UserStatus(
    @PrimaryKey val id: Int = 0, // Use a single row for simplicity
    val username: String,        // Foreign key to User table
    var currentCalories: Int = 0,
    var currentProtein: Int = 0,
    var currentCarbs: Int = 0,
    var currentFat: Int = 0,
    var calorieGoal: Int = 0,
    var proteinGoal: Int = 0,
    var carbsGoal: Int = 0,
    var fatGoal: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis() // Timestamp for tracking updates
)

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): User?

}

@Dao
interface UserStatusDao {
    // Insert or update user status
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStatus(status: UserStatus)

    // Fetch status for a specific user
    @Query("SELECT * FROM user_status WHERE username = :username")
    suspend fun getStatusByUsername(username: String): UserStatus?

    // Update specific stats
    @Query("""
        UPDATE user_status 
        SET 
            currentCalories = :currentCalories, 
            currentProtein = :currentProtein, 
            currentCarbs = :currentCarbs, 
            currentFat = :currentFat, 
            calorieGoal = :calorieGoal, 
            proteinGoal = :proteinGoal, 
            carbsGoal = :carbsGoal, 
            fatGoal = :fatGoal 
        WHERE username = :username
    """)
    suspend fun updateStatus(
        username: String,
        currentCalories: Int,
        currentProtein: Int,
        currentCarbs: Int,
        currentFat: Int,
        calorieGoal: Int,
        proteinGoal: Int,
        carbsGoal: Int,
        fatGoal: Int
    )

    // Delete user stats (optional, if needed)
    @Query("DELETE FROM user_status WHERE username = :username")
    suspend fun deleteStatus(username: String)
}

@Database(entities = [User::class, UserStatus::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun userStatusDao(): UserStatusDao

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

class UserStatusRepository(context: Context) {
    private val userStatusDao = AppDatabase.getDatabase(context).userStatusDao()

    // Insert or update status
    suspend fun upsertStatus(status: UserStatus) {
        userStatusDao.upsertStatus(status)
    }
    // Get status for a user
    suspend fun getStatusByUsername(username: String): UserStatus? {
        Log.i("UserStatusRepository", "Querying database for UserStatus with username: $username")
        return userStatusDao.getStatusByUsername(username)
    }

// Update specific stats, including current and goal values
    suspend fun updateStatus(
        username: String,
        currentCalories: Int,
        currentProtein: Int,
        currentCarbs: Int,
        currentFat: Int,
        calorieGoal: Int,
        proteinGoal: Int,
        carbsGoal: Int,
        fatGoal: Int
    ) {
        userStatusDao.updateStatus(
            username = username,
            currentCalories = currentCalories,
            currentProtein = currentProtein,
            currentCarbs = currentCarbs,
            currentFat = currentFat,
            calorieGoal = calorieGoal,
            proteinGoal = proteinGoal,
            carbsGoal = carbsGoal,
            fatGoal = fatGoal
        )
    }

    suspend fun deleteStatus(username: String) {
        userStatusDao.deleteStatus(username)
    }
}

class AuthRepository(context: Context) {
    private val userDao = AppDatabase.getDatabase(context).userDao()
    private val userStatusDao = AppDatabase.getDatabase(context).userStatusDao()

    suspend fun registerUser(username: String,  password: String): Boolean {
        if (userDao.getUserByUsername(username) != null ) {
            return false
        }

        // Hash the password
        val passwordHash = BCrypt.hashpw(password, BCrypt.gensalt())

        // Create and insert new user
        val newUser = User(username, passwordHash)
        userDao.insertUser(newUser)
        val initialUserStatus = UserStatus(
            username = username,
            currentCalories = 0,
            currentProtein = 0,
            currentCarbs = 0,
            currentFat = 0,
            calorieGoal = 2000, // Default goals, adjust as needed
            proteinGoal = 100,
            carbsGoal = 300,
            fatGoal = 70,
            lastUpdated = System.currentTimeMillis()
        )
        userStatusDao.upsertStatus(initialUserStatus)
        return true
    }

    suspend fun loginUser(username: String, password: String): Boolean {
        val user = userDao.getUserByUsername(username) ?: return false
        return BCrypt.checkpw(password, user.passwordHash)
    }
}