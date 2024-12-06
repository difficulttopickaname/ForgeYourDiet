package com.cs407.forgeyourdiet

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cs407.forgeyourdiet.data.UserStatus
import com.cs407.forgeyourdiet.data.UserStatusRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NutritionViewModel(private val repository: UserStatusRepository) : ViewModel() {

    private val _userStatus = MutableLiveData<UserStatus?>()
    val userStatus: LiveData<UserStatus?> = _userStatus

    fun loadUserStatus(username: String) {
        viewModelScope.launch {
            val status = repository.getStatusByUsername(username)
            if (status != null) {
                Log.i("loadUserStatus", "Loaded UserStatus: $status")
                _userStatus.postValue(status)
            } else {
                Log.i("loadUserStatus", "No UserStatus found for username: $username")
                _userStatus.postValue(null)
            }
            _userStatus.postValue(status)
        }
    }

    fun updateUserStatus(
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
        viewModelScope.launch {
            repository.updateStatus(
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
            loadUserStatus(username)
        }
    }


    fun upsertUserStatus(userStatus: UserStatus) {
        viewModelScope.launch {
            repository.upsertStatus(userStatus)
            loadUserStatus(userStatus.username)
        }
    }
}

class NutritionViewModelFactory(private val repository: UserStatusRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NutritionViewModel::class.java)) {
            return NutritionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

data class UserState(
    val username: String = ""
)

class UserViewModel : ViewModel() {
    private val _userState = MutableStateFlow(UserState())
    val userState = _userState.asStateFlow()

    fun setUser(state: UserState) {
        _userState.update {
            state
        }
    }
}
