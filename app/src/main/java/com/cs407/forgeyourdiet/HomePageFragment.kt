package com.cs407.forgeyourdiet

import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

class HomePageFragment : Fragment() {

    private lateinit var userStateViewModel: NutritionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userStateViewModel = ViewModelProvider(this).get(NutritionViewModel::class.java)

        // Initialize calorie circle view
        val calorieCircle = view.findViewById<View>(R.id.calorieCircle)
        val calorieValue = calorieCircle.findViewById<TextView>(R.id.currentCaloriesText)
        val calorieGoal = calorieCircle.findViewById<TextView>(R.id.calorieGoalText)
        //setProgressBarColor(calorieCircle, R.color.cal)

        // Set up protein bar
        val proteinContainer = view.findViewById<View>(R.id.proteinContainer)
        val proteinLabel = proteinContainer.findViewById<TextView>(R.id.nutrientLabel)
        val proteinValue = proteinContainer.findViewById<TextView>(R.id.nutrientValue)
        val proteinProgressBar = proteinContainer.findViewById<ProgressBar>(R.id.nutrientProgressBar)
        proteinLabel.text = getString(R.string.protein)
        setProgressBarColor(proteinContainer, R.color.protein)

        // Set up carbs bar
        val carbsContainer = view.findViewById<View>(R.id.carbsContainer)
        val carbsLabel = carbsContainer.findViewById<TextView>(R.id.nutrientLabel)
        val carbsValue = carbsContainer.findViewById<TextView>(R.id.nutrientValue)
        val carbsProgressBar = carbsContainer.findViewById<ProgressBar>(R.id.nutrientProgressBar)
        carbsLabel.text = getString(R.string.carbs)
        setProgressBarColor(carbsContainer, R.color.carbs)

        // Set up fat bar
        val fatContainer = view.findViewById<View>(R.id.fatContainer)
        val fatLabel = fatContainer.findViewById<TextView>(R.id.nutrientLabel)
        val fatValue = fatContainer.findViewById<TextView>(R.id.nutrientValue)
        val fatProgressBar = fatContainer.findViewById<ProgressBar>(R.id.nutrientProgressBar)
        fatLabel.text = getString(R.string.fat)
        setProgressBarColor(fatContainer, R.color.fat)

        // Observe ViewModel for data changes
        userStateViewModel.userProgress.observe(viewLifecycleOwner) { progress ->
            calorieValue.text = "${progress.currentCalories}"
            calorieGoal.text = "/ ${progress.calorieGoal} kcal"
            proteinProgressBar.progress = progress.currentProtein
            proteinValue.text = "${progress.currentProtein} / ${progress.proteinGoal} g"
            carbsProgressBar.progress = progress.currentCarbs
            carbsValue.text = "${progress.currentCarbs} / ${progress.carbGoal} g"
            fatProgressBar.progress = progress.currentFat
            fatValue.text = "${progress.currentFat} / ${progress.fatGoal} g"
        }

        // clean your fridge part
        val cleanYourFridge = view.findViewById<TextView>(R.id.cleanYourFridgeText)
        cleanYourFridge.setOnClickListener {
            findNavController().navigate(R.id.action_homepageFragment_to_cleanYourFridgeFragment)
        }
    }

    private fun setProgressBarColor(container: View, colorResId: Int) {
        val progressBar = container.findViewById<ProgressBar>(R.id.nutrientProgressBar)
        val layerDrawable = progressBar.progressDrawable as LayerDrawable
        val clipDrawable = layerDrawable.findDrawableByLayerId(android.R.id.progress)
        val color = ContextCompat.getColor(requireContext(), colorResId)
        clipDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
    }
}
