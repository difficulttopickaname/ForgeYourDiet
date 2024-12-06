package com.cs407.forgeyourdiet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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

        titleImplement()
        // Observe ViewModel for data changes
        // TODO: change logic of observation to cache or other observation
        nutritionViewModel.userStatus.observe(viewLifecycleOwner) { userStatus ->
            if (userStatus != null) {
                Log.i("good", "good user status")
                updateUI(userStatus)
            }
            else{
                Log.i("e", "null user status")
            }
        }
        nutritionViewModel.loadUserStatus(userViewModel.userState.value.username)
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