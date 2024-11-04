package com.cs407.forgeyourdiet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

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
        val mealCalories = arguments?.getInt("mealCalories")
        val ingredients = arguments?.getStringArrayList("ingredients") ?: listOf()

        // Set the text on the UI elements
        view.findViewById<TextView>(R.id.mealTitle)?.text = mealName
        view.findViewById<TextView>(R.id.mealDescription)?.text = mealCategory
        view.findViewById<TextView>(R.id.mealCalories)?.text = "$mealCalories cal"
        view.findViewById<TextView>(R.id.mealRecipe)?.text = ingredients.joinToString(separator = "\n") { "- $it" }
    }
}
