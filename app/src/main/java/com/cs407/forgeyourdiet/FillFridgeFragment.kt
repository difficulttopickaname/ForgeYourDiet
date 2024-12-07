package com.cs407.forgeyourdiet

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import java.io.*

class FillFridgeFragment : Fragment() {

    private lateinit var searchEditText: EditText
    private lateinit var suggestionsListView: ListView
    private lateinit var ingredientsListView: ListView
    private lateinit var addButton: Button
    private val ingredientsList = mutableListOf<String>()
    private val userIngredients = mutableListOf<String>()
    private lateinit var ingredientsAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fill_your_fridge, container, false)

        // Initialize views
        searchEditText = view.findViewById(R.id.searchIngredientsEditText)
        suggestionsListView = view.findViewById(R.id.suggestionsListView)
        ingredientsListView = view.findViewById(R.id.userIngredientsListView)
        addButton = view.findViewById(R.id.addButton)

        // Set up the adapter for the ListView
        ingredientsAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            userIngredients
        )
        ingredientsListView.adapter = ingredientsAdapter

        // Load ingredients from CSV
        loadIngredients()

        // Add TextWatcher to handle input changes
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    showSuggestions(query)
                } else {
                    suggestionsListView.adapter = null // Clear suggestions
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // No action needed
            }
        })

        // Handle adding selected ingredient to the fridge
        addButton.setOnClickListener {
            val selectedIngredient = searchEditText.text.toString().trim()
            if (selectedIngredient.isNotEmpty()) {
                addToFridge(selectedIngredient)
                saveIngredientToFile(selectedIngredient) // Save to file
            } else {
                Toast.makeText(requireContext(), "Please enter an ingredient", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun loadIngredients() {
        try {
            val inputStream = requireContext().assets.open("ingredients.csv")
            val reader = BufferedReader(InputStreamReader(inputStream))
            reader.useLines { lines ->
                lines.drop(1).forEach { line -> // Skip the header if present
                    val parts = line.split(",")
                    if (parts.isNotEmpty()) {
                        val ingredient = parts[0].trim() // Extract only the first column (ingredient name)
                        ingredientsList.add(ingredient)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error loading ingredients", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showSuggestions(query: String) {
        val suggestions = ingredientsList.filter {
            it.contains(query, ignoreCase = true)
        }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, suggestions)
        suggestionsListView.adapter = adapter

        // Handle click on suggestion
        suggestionsListView.setOnItemClickListener { _, _, position, _ ->
            val selected = suggestions[position]
            searchEditText.setText(selected) // Set the selected item in the search bar
        }
    }

    private fun addToFridge(ingredient: String) {
        // Add the ingredient to the list and notify the adapter
        if (!userIngredients.contains(ingredient)) {
            userIngredients.add(ingredient)
            ingredientsAdapter.notifyDataSetChanged()
        }

        // Clear the search bar
        searchEditText.text.clear()

        // Show a confirmation toast
        Toast.makeText(requireContext(), "$ingredient added to your fridge", Toast.LENGTH_SHORT).show()
    }

    private fun saveIngredientToFile(ingredient: String) {
        try {
            // Save the file to the app's internal storage (writable directory)
            val file = File(requireContext().filesDir, "ingredientsAdded.txt")

            // Avoid duplications in the file
            val existingIngredients = if (file.exists()) file.readLines() else emptyList()
            if (!existingIngredients.contains(ingredient)) {
                file.appendText("$ingredient\n")
            }

            // Log the file path and contents for debugging
            Log.d("FillFridgeFragment", "File path: ${file.absolutePath}")
            Log.d("FillFridgeFragment", "Current file contents:\n${file.readText()}")

            Toast.makeText(requireContext(), "Saved to: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error saving ingredient", Toast.LENGTH_SHORT).show()
        }
    }
}
