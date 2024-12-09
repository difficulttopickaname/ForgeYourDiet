package com.cs407.forgeyourdiet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation

class NutritionTipsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_nutrition_tips, container, false)

        // Find the TextViews for each tip title
        val tip1Title: TextView = view.findViewById(R.id.tip1Title)
        val tip2Title: TextView = view.findViewById(R.id.tip2Title)

        // Set click listeners to navigate to TipDetailFragment with an argument
        tip1Title.setOnClickListener {
            // Navigate to TipDetailFragment with tipId = 1 for "Counting Calories"
            val bundle = Bundle().apply {
                putInt("tipId", 1)
            }
            Navigation.findNavController(view).navigate(R.id.action_nutritionTipsFragment_to_tipDetailFragment, bundle)
        }

        tip2Title.setOnClickListener {
            // Navigate to TipDetailFragment with tipId = 2 for "Hydration Importance"
            val bundle = Bundle().apply {
                putInt("tipId", 2)
            }
            Navigation.findNavController(view).navigate(R.id.action_nutritionTipsFragment_to_tipDetailFragment, bundle)
        }

        return view
    }
}
