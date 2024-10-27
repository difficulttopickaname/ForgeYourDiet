package com.cs407.forgeyourdiet.data

data class UserProgress(
    var calorieGoal: Int = 2000,       // Target calories
    var currentCalories: Int = 1000,      // Consumed calories

    var proteinGoal: Int = 100,        // Target protein in grams
    var currentProtein: Int = 50,       // Consumed protein in grams

    var carbGoal: Int = 300,           // Target carbs in grams
    var currentCarbs: Int = 60,         // Consumed carbs in grams

    var fatGoal: Int = 70,             // Target fat in grams
    var currentFat: Int = 20            // Consumed fat in grams
)