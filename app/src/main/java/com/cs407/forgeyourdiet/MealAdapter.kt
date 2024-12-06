package com.cs407.forgeyourdiet

// MealAdapter.kt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MealAdapter(
    private var mealList: List<Meal>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<MealAdapter.MealViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(meal: Meal)
    }

    private var filteredMealList = mealList.toMutableList()

    inner class MealViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mealName: TextView = view.findViewById(R.id.mealName)
        val mealCategory: TextView = view.findViewById(R.id.mealCategory)
        val mealCalories: TextView = view.findViewById(R.id.mealCalories)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.meal_item, parent, false)
        return MealViewHolder(view)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val meal = filteredMealList[position]
        holder.mealName.text = meal.name
        holder.mealCategory.text = meal.category
        holder.mealCalories.text = "${meal.calories} cal"

        // Set click listener on the item
        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(meal)
        }
    }

    override fun getItemCount(): Int = filteredMealList.size

    fun updateData(newList: List<Meal>) {
        mealList = newList
        filteredMealList = newList.toMutableList()
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        filteredMealList = if (query.isEmpty()) {
            mealList.toMutableList()
        } else {
            mealList.filter { it.name.contains(query, ignoreCase = true) }.toMutableList()
        }
        notifyDataSetChanged()
    }
}

