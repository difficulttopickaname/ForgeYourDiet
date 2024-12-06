package com.cs407.forgeyourdiet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.cs407.forgeyourdiet.data.UserStatus
import com.cs407.forgeyourdiet.data.UserStatusRepository
import org.w3c.dom.Text

class NutritionSummaryFragment: Fragment() {
    private lateinit var nutritionViewModel: NutritionViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var caloriesBox: androidx.cardview.widget.CardView
    private lateinit var proteinBox: androidx.cardview.widget.CardView
    private lateinit var carbohydratesBox: androidx.cardview.widget.CardView
    private lateinit var fatBox: androidx.cardview.widget.CardView
    private lateinit var username: String
    private lateinit var logEntryTitle: TextView
    private lateinit var logEntryValue: EditText

    private var selectedNutrient: NutrientType = NutrientType.CALORIES

    private enum class NutrientType {
        NONE, CALORIES, PROTEIN, CARBOHYDRATES, FAT
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.nutrition_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = UserStatusRepository(requireContext())
        val factory = NutritionViewModelFactory(repository)

        nutritionViewModel = ViewModelProvider(this, factory).get(NutritionViewModel::class.java)
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        username = userViewModel.userState.value.username
        nutritionViewModel.loadUserStatus(username)

        caloriesBox = view.findViewById(R.id.caloriesBox)
        proteinBox = view.findViewById(R.id.proteinBox)
        carbohydratesBox = view.findViewById(R.id.carbohydratesBox)
        fatBox = view.findViewById(R.id.fatBox)

        logEntryTitle = view.findViewById(R.id.logCaloriesSection)
        logEntryValue = view.findViewById(R.id.caloriesEditText)

        titleImplement()
        // Observe ViewModel for data changes
        // TODO: change logic of observation to cache or other observation
        nutritionViewModel.userStatus.observe(viewLifecycleOwner) { userStatus ->
            if (userStatus != null) {
                updateUI(userStatus)
            }
        }

        caloriesBox.setOnClickListener { onNutrientSelected(NutrientType.CALORIES) }
        proteinBox.setOnClickListener { onNutrientSelected(NutrientType.PROTEIN) }
        carbohydratesBox.setOnClickListener { onNutrientSelected(NutrientType.CARBOHYDRATES) }
        fatBox.setOnClickListener { onNutrientSelected(NutrientType.FAT) }

        val macronutrientRadioGroup = view.findViewById<RadioGroup>(R.id.macronutrientRadioGroup)

        macronutrientRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.caloriesRadioButton -> onNutrientSelected(NutrientType.CALORIES)
                R.id.proteinRadioButton -> onNutrientSelected(NutrientType.PROTEIN)
                R.id.carbsRadioButton -> onNutrientSelected(NutrientType.CARBOHYDRATES)
                R.id.fatRadioButton -> onNutrientSelected(NutrientType.FAT)
            }
        }

        // reset button logic
        view.findViewById<View>(R.id.resetButton).setOnClickListener{
            logEntryValue.text = null
        }

        view.findViewById<View>(R.id.addFoodButton).setOnClickListener{
            findNavController().navigate(R.id.action_nutritionSummaryFragment_to_cleanYourFridgeFragment)
        }

        // save button logic
        view.findViewById<View>(R.id.saveButton).setOnClickListener {
            val enteredValue = logEntryValue.text.toString().toIntOrNull()
            if (enteredValue != null) {
                updateSelectedNutrient(enteredValue)
            } else {
                Log.e("LogEntry", "Invalid number entered")
            }
        }
    }

    private fun onNutrientSelected(nutrientType: NutrientType) {
        selectedNutrient = nutrientType
        when (nutrientType) {
            NutrientType.CALORIES -> logEntryTitle.text = getString(R.string.NutritionSummary_log_calories)
            NutrientType.PROTEIN -> logEntryTitle.text = getString(R.string.NutritionSummary_log_protein)
            NutrientType.CARBOHYDRATES -> logEntryTitle.text = getString(R.string.NutritionSummary_log_carbs)
            NutrientType.FAT -> logEntryTitle.text = getString(R.string.NutritionSummary_log_fat)
            else -> logEntryTitle.text = ""
        }
    }
    private fun updateSelectedNutrient(value: Int) {
        when (selectedNutrient) {
            NutrientType.CALORIES -> nutritionViewModel.updateCaloriesGoal(username, value)
            NutrientType.PROTEIN -> nutritionViewModel.updateProteinGoal(username, value)
            NutrientType.CARBOHYDRATES -> nutritionViewModel.updateCarbsGoal(username, value)
            NutrientType.FAT -> nutritionViewModel.updateFatGoal(username, value)
            else -> Log.e("LogEntry", "No nutrient selected")
        }
    }
    private fun titleImplement(){
        caloriesBox.findViewById<TextView>(R.id.boxTitle).text = getString(R.string.calories)
        proteinBox.findViewById<TextView>(R.id.boxTitle).text = getString(R.string.protein)
        carbohydratesBox.findViewById<TextView>(R.id.boxTitle).text = getString(R.string.carbs)
        fatBox.findViewById<TextView>(R.id.boxTitle).text = getString(R.string.fat)
    }

    private fun updateUI(progress: UserStatus){
        caloriesBox.findViewById<TextView>(R.id.boxValue).text = "${progress.currentCalories}"
        caloriesBox.findViewById<TextView>(R.id.boxDifference).text = formatDifference(progress.currentCalories - progress.calorieGoal)

        proteinBox.findViewById<TextView>(R.id.boxValue).text = "${progress.currentProtein}"
        proteinBox.findViewById<TextView>(R.id.boxDifference).text = formatDifference(progress.currentProtein - progress.proteinGoal)

        carbohydratesBox.findViewById<TextView>(R.id.boxValue).text = "${progress.currentCarbs}"
        carbohydratesBox.findViewById<TextView>(R.id.boxDifference).text = formatDifference(progress.currentCarbs - progress.carbsGoal)

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