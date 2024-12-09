package com.cs407.forgeyourdiet

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.InputStreamReader
import com.opencsv.CSVReader
import java.io.File
import java.io.IOException
import kotlin.math.roundToInt

class CleanYourFridgeFragment : Fragment(), MealAdapter.OnItemClickListener {

    private lateinit var mealAdapter: MealAdapter
    // Replace the placeholder with a mutable list
    private val userIngredients = mutableListOf("")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loadUserIngredients()
        return inflater.inflate(R.layout.fragment_clean_your_fridge, container, false)
    }

    private fun loadUserIngredients() {
        try {
            // Access the file where ingredients are stored
            val file = File(requireContext().filesDir, "ingredientsAdded.txt")

            // Check if the file exists before reading
            if (file.exists()) {
                val ingredients = file.readLines() // Read all lines from the file
                userIngredients.addAll(ingredients) // Add each ingredient to the list
                Log.d("CleanYourFridgeFragment", "Ingredients loaded: $ingredients")
            } else {
                Log.d("CleanYourFridgeFragment", "File does not exist: ingredientsAdded.txt")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error loading ingredients", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val button = view.findViewById<TextView>(R.id.cleanYourFridgeTitle)
        val recyclerView = view.findViewById<RecyclerView>(R.id.dishRecyclerView)
        val searchEditText = view.findViewById<EditText>(R.id.searchDishesEditText)

        // Set up RecyclerView
        mealAdapter = MealAdapter(emptyList(), this)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = mealAdapter

        // Load initial data and filter dishes when the button is clicked
        button.setOnClickListener {
            val dishes = readMealsFromCsv(requireContext())
            val filteredDishes = filterDishesByIngredients(dishes, userIngredients)
            println("filteredDishes: $filteredDishes")
            mealAdapter.updateData(filteredDishes)
        }

        // Set up the search functionality
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mealAdapter.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onItemClick(meal: Meal) {
        val bundle = Bundle().apply {
            putString("mealName", meal.name)
            putString("mealCategory", meal.category)
            putInt("mealCalories", meal.calories)
            putStringArrayList(
                "ingredients",
                ArrayList(meal.originalIngredients)
            ) // Pass original ingredients
            putInt("mealFat",meal.fat)
            putInt("mealCarbs",meal.carbs)
            putInt("mealProtein",meal.protein)
        }
        findNavController().navigate(
            R.id.action_cleanYourFridgeFragment_to_mealDetailFragment,
            bundle
        )
    }


    private fun readMealsFromCsv(context: Context): List<Meal> {
        val mealList = mutableListOf<Meal>()
        try {
            val inputStream = context.assets.open("meals.csv")
            val reader = CSVReader(InputStreamReader(inputStream))

            reader.readNext() // Skip header
            reader.forEach { row ->
                if (row.size >= 24) { // Ensure there are enough columns
                    val name = row[0]
                    val category = row[1]
                    val calories = row[22].toDoubleOrNull()?.roundToInt() ?: 0
                    val fat = row[23].toDoubleOrNull()?.roundToInt() ?: 0
                    val carbs = row[24].toDoubleOrNull()?.roundToInt() ?: 0
                    val protein = row[25].toDoubleOrNull()?.roundToInt() ?: 0
                    val originalIngredients = row.slice(3..21).filter { it.isNotBlank() }
                    val normalizedIngredients = originalIngredients.map {
                        it.replace(Regex("\\s*\\(.*?\\)"), "").trim().lowercase()
                    }

                    val meal =
                        Meal(name, category, calories,fat,carbs,protein, normalizedIngredients, originalIngredients)
                    mealList.add(meal)

                    // Print the meal to the console
                    println("Meal: $meal")
                }
            }
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mealList
    }

    private fun filterDishesByIngredients(
        dishes: List<Meal>,
        userIngredients: List<String>
    ): List<Meal> {
        val normalizedUserIngredients =
            userIngredients.map { it.lowercase() } // Normalize user ingredients to lowercase
        return dishes
            .map { meal ->
                // Normalize meal ingredients and calculate match rate
                val matchRate = meal.ingredients.count { ingredient ->
                    normalizedUserIngredients.contains(ingredient)
                }
                Pair(meal, matchRate) // Pair the meal with its match rate
            }
            .filter { it.second > 0 } // Keep only meals with at least one match
            .sortedByDescending { it.second } // Sort by match rate in descending order
            .map { it.first } // Extract the sorted meals
    }
}

//class CleanYourFridgeFragment : Fragment() , MealAdapter.OnItemClickListener{
//
//    private lateinit var mealAdapter: MealAdapter
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_clean_your_fridge, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val button = view.findViewById<TextView>(R.id.cleanYourFridgeTitle)
//        val recyclerView = view.findViewById<RecyclerView>(R.id.dishRecyclerView)
//        val searchEditText = view.findViewById<EditText>(R.id.searchDishesEditText)
//
//        // Set up RecyclerView
//        mealAdapter = MealAdapter(emptyList(),this)
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//        recyclerView.adapter = mealAdapter
//
//        // Load initial data when the button is clicked
//        button.setOnClickListener {
//            val dishes = readMealsFromCsv(requireContext())
//            mealAdapter.updateData(dishes)
//        }
//
//        // Set up the search functionality
//        searchEditText.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                mealAdapter.filter(s.toString())
//            }
//
//            override fun afterTextChanged(s: Editable?) {}
//        })
//    }
//
//    override fun onItemClick(meal: Meal) {
//        val bundle = Bundle().apply {
//            putString("mealName", meal.name)
//            putString("mealCategory", meal.category)
//            putInt("mealCalories", meal.calories)
//            putStringArrayList("ingredients", ArrayList(meal.ingredients)) // Pass ingredients as an ArrayList
//        }
//        findNavController().navigate(R.id.action_cleanYourFridgeFragment_to_mealDetailFragment, bundle)
//
//    }
//
//    private fun readMealsFromCsv(context: Context): List<Meal> {
//        val mealList = mutableListOf<Meal>()
//        try {
//            val inputStream = context.assets.open("meals.csv")
//            val reader = CSVReader(InputStreamReader(inputStream))
//
//            reader.readNext() // Skip header
//            reader.forEach { row ->
//                if (row.size >= 24) { // Ensure there are enough columns
//                    val name = row[0]
//                    val category = row[1]
//                    val calories = row[22].toDoubleOrNull()?.roundToInt() ?: 0
//                    val ingredients = row.slice(3..23).filter { it.isNotBlank() } // Extract and clean up ingredients
//                    mealList.add(Meal(name, category, calories, ingredients))
//                }
//            }
//            reader.close()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        return mealList
//    }
//
//}
