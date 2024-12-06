package com.cs407.forgeyourdiet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlin.math.ceil

class MealDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_meal_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve arguments
        val mealName = arguments?.getString("mealName")
        val mealCategory = arguments?.getString("mealCategory")
        val mealFat = arguments?.getInt("mealFat")
        val mealCarbs = arguments?.getInt("mealCarbs")
        val mealProtein = arguments?.getInt("mealProtein")
        val mealCalories = arguments?.getInt("mealCalories")
        val ingredients = arguments?.getStringArrayList("ingredients") ?: listOf()

        // Set the text on the UI elements
        view.findViewById<TextView>(R.id.mealTitle)?.text = mealName
        view.findViewById<TextView>(R.id.mealDescription)?.text = mealCategory
        view.findViewById<TextView>(R.id.mealCalories)?.text = "$mealCalories cal"
        view.findViewById<TextView>(R.id.mealRecipe)?.text = ingredients.joinToString(separator = "\n") { "- $it" }
        // Round up the values
        val roundedProtein = mealProtein?.let { ceil(it.toDouble()).toInt() }
        val roundedCarbs = mealCarbs?.let { ceil(it.toDouble()).toInt() }
        val roundedFat = mealFat?.let { ceil(it.toDouble()).toInt() }

// Use the rounded values
        val proteinData = Pair(roundedProtein, 100)
        val carbsData = Pair(roundedCarbs, 100)
        val fatData = Pair(roundedFat, 10)

        updateProgress(view.findViewById(R.id.proteinProgressBar), view.findViewById(R.id.proteinValue), proteinData)
        updateProgress(view.findViewById(R.id.carbsProgressBar), view.findViewById(R.id.carbsValue), carbsData)
        updateProgress(view.findViewById(R.id.fatProgressBar), view.findViewById(R.id.fatValue), fatData)
    }

    private fun updateProgress(progressBar: ProgressBar, valueTextView: TextView, data: Pair<Int?, Int>) {
        progressBar.max = data.second
        progressBar.progress = data.first!!
        valueTextView.text = "${data.first}/${data.second}"
    }
}
