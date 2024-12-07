package com.cs407.forgeyourdiet

import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.cs407.forgeyourdiet.data.UserStatusRepository
import kotlinx.coroutines.launch
import kotlin.math.ceil

class MealDetailFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_meal_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
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

        val proteinData = Pair(roundedProtein, 100)
        val carbsData = Pair(roundedCarbs, 100)
        val fatData = Pair(roundedFat, 100)

        updateProgress(view.findViewById(R.id.proteinProgressBar), view.findViewById(R.id.proteinValue), proteinData)
        updateProgress(view.findViewById(R.id.carbsProgressBar), view.findViewById(R.id.carbsValue), carbsData)
        updateProgress(view.findViewById(R.id.fatProgressBar), view.findViewById(R.id.fatValue), fatData)

        setProgressBarColor(view.findViewById(R.id.proteinProgressBar), R.color.protein)
        setProgressBarColor(view.findViewById(R.id.carbsProgressBar), R.color.carbs)
        setProgressBarColor(view.findViewById(R.id.fatProgressBar), R.color.fat)

        val context = requireContext()
        val userStatusRepository = UserStatusRepository(context)

        // Assume the username is stored in SharedPreferences or passed as an argument
        val username = userViewModel.userState.value.username

        view.findViewById<Button>(R.id.button).setOnClickListener {
            // Print the values to the console
            println("Total Calories: $mealCalories")
            println("Fat: $mealFat")
            println("Carbs: $mealCarbs")
            println("Protein: $mealProtein")

            // Update the database with the new values
            lifecycleScope.launch {
                val userStatus = userStatusRepository.getStatusByUsername(username)
                if (userStatus != null) {
                    userStatusRepository.updateStatus(
                        username = username,
                        currentCalories = userStatus.currentCalories + (mealCalories ?: 0), // Add to currentCalories
                        currentProtein = userStatus.currentProtein + (mealProtein ?: 0),   // Add to currentProtein
                        currentCarbs = userStatus.currentCarbs + (mealCarbs ?: 0),         // Add to currentCarbs
                        currentFat = userStatus.currentFat + (mealFat ?: 0),               // Add to currentFat
                        calorieGoal = userStatus.calorieGoal,                              // Keep original goal
                        proteinGoal = userStatus.proteinGoal,                              // Keep original goal
                        carbsGoal = userStatus.carbsGoal,                                  // Keep original goal
                        fatGoal = userStatus.fatGoal                                       // Keep original goal
                    )
                    println("Database updated with new values for $username")
                    println("New Current Calories: ${userStatus.currentCalories + (mealCalories ?: 0)}")
                    println("New Current Protein: ${userStatus.currentProtein + (mealProtein ?: 0)}")
                    println("New Current Carbs: ${userStatus.currentCarbs + (mealCarbs ?: 0)}")
                    println("New Current Fat: ${userStatus.currentFat + (mealFat ?: 0)}")
                } else {
                    println("User status not found for username: $username")
                }
            }

        }
    }

    private fun updateProgress(progressBar: ProgressBar, valueTextView: TextView, data: Pair<Int?, Int>) {
        progressBar.max = data.second
        progressBar.progress = data.first!!
        valueTextView.text = "${data.first}/${data.second}"
    }

    private fun setProgressBarColor(progressBar: ProgressBar, colorResId: Int) {
        val layerDrawable = progressBar.progressDrawable as LayerDrawable
        val clipDrawable = layerDrawable.findDrawableByLayerId(android.R.id.progress)
        val color = ContextCompat.getColor(requireContext(), colorResId)
        clipDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
    }

}
