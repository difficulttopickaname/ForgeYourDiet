package com.cs407.forgeyourdiet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cs407.forgeyourdiet.data.UserProgress

class NutritionViewModel : ViewModel() {
    private val _userProgress = MutableLiveData<UserProgress>()
    val userProgress: LiveData<UserProgress> = _userProgress

    init {
        _userProgress.value = UserProgress()
    }

    fun updateCalories(newCalories: Int) {
        _userProgress.value = _userProgress.value?.copy(currentCalories = newCalories)
    }

    fun updateProtein(newProtein: Int) {
        _userProgress.value = _userProgress.value?.copy(currentProtein = newProtein)
    }

    fun updateCarbs(newCarbs: Int) {
        _userProgress.value = _userProgress.value?.copy(currentCarbs = newCarbs)
    }

    fun updateFat(newFat: Int) {
        _userProgress.value = _userProgress.value?.copy(currentFat = newFat)
    }
}
