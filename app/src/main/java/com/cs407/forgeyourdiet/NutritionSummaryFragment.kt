package com.cs407.forgeyourdiet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cs407.forgeyourdiet.data.UserProgress
import org.w3c.dom.Text

class NutritionSummaryFragment: Fragment() {
    private lateinit var nutritionViewModel: NutritionViewModel
    private lateinit var caloriesBox: androidx.cardview.widget.CardView
    private lateinit var proteinBox: androidx.cardview.widget.CardView
    private lateinit var carbohydratesBox: androidx.cardview.widget.CardView
    private lateinit var fatBox: androidx.cardview.widget.CardView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.nutrition_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nutritionViewModel = ViewModelProvider(requireActivity()).get(NutritionViewModel::class.java)

        caloriesBox = view.findViewById(R.id.caloriesBox)
        proteinBox = view.findViewById(R.id.proteinBox)
        carbohydratesBox = view.findViewById(R.id.carbohydratesBox)
        fatBox = view.findViewById(R.id.fatBox)

        // Observe ViewModel for data changes
        nutritionViewModel.userProgress.observe(viewLifecycleOwner) { progress ->
            updateUI(progress)
        }
    }


    private fun updateUI(progress: UserProgress){
        caloriesBox.findViewById<TextView>(R.id.boxTitle).text = getString(R.string.calories)
        caloriesBox.findViewById<TextView>(R.id.boxValue).text = "${progress.currentCalories}"
        caloriesBox.findViewById<TextView>(R.id.boxDifference).text = formatDifference(progress.currentCalories - progress.calorieGoal)

        proteinBox.findViewById<TextView>(R.id.boxTitle).text = getString(R.string.protein)
        proteinBox.findViewById<TextView>(R.id.boxValue).text = "${progress.currentProtein}"
        proteinBox.findViewById<TextView>(R.id.boxDifference).text = formatDifference(progress.currentProtein - progress.proteinGoal)

        carbohydratesBox.findViewById<TextView>(R.id.boxTitle).text = getString(R.string.carbs)
        carbohydratesBox.findViewById<TextView>(R.id.boxValue).text = "${progress.currentCarbs}"
        carbohydratesBox.findViewById<TextView>(R.id.boxDifference).text = formatDifference(progress.currentCarbs - progress.carbGoal)

        fatBox.findViewById<TextView>(R.id.boxTitle).text = getString(R.string.fat)
        fatBox.findViewById<TextView>(R.id.boxValue).text = "${progress.currentFat}"
        fatBox.findViewById<TextView>(R.id.boxDifference).text = formatDifference(progress.currentFat - progress.fatGoal)

    }

    private fun formatDifference(difference: Int): String {
        return if (difference >= 0) {
            "+$difference"
        } else {
            "$difference"
        }
    }
}