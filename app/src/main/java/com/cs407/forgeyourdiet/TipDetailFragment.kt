package com.cs407.forgeyourdiet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class TipDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tip_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Back Button Navigation
        val backButton = view.findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        // Retrieve the tipId argument passed from the NutritionTipsFragment
        val tipId = arguments?.getInt("tipId", 1)

        // Find both sections in the layout
        val countingCaloriesSection = view.findViewById<View>(R.id.countingCaloriesSection)
        val hydrationImportanceSection = view.findViewById<View>(R.id.hydrationImportanceSection)

        // Show the correct section based on tipId
        when (tipId) {
            1 -> {
                countingCaloriesSection.visibility = View.VISIBLE
                hydrationImportanceSection.visibility = View.GONE
            }
            2 -> {
                countingCaloriesSection.visibility = View.GONE
                hydrationImportanceSection.visibility = View.VISIBLE
            }
            else -> {
                // Default case, hide all sections if tipId is invalid
                countingCaloriesSection.visibility = View.GONE
                hydrationImportanceSection.visibility = View.GONE
            }
        }
    }
}
