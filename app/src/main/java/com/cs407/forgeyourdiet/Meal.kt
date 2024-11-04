package com.cs407.forgeyourdiet

// Meal.kt
data class Meal(
    val name: String,
    val category: String,
    val calories: Int,
    val ingredients: List<String> // New property for ingredients
)
