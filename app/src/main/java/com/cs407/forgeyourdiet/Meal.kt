package com.cs407.forgeyourdiet

// Meal.kt
data class Meal(
    val name: String,
    val category: String,
    val calories: Int,
    val ingredients: List<String>, // Normalized ingredients for filtering
    val originalIngredients: List<String> // Original ingredients for display
)
