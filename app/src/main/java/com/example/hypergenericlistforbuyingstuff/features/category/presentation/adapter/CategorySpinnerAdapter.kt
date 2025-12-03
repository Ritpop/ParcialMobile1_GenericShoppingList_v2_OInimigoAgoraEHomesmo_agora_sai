package com.example.hypergenericlistforbuyingstuff.features.category.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.hypergenericlistforbuyingstuff.R
import com.example.hypergenericlistforbuyingstuff.models.Category

class CategorySpinnerAdapter(context: Context, categories: List<Category>) :
    ArrayAdapter<Category>(context, 0, categories) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.spinner_item_category, parent, false)

        val category = getItem(position)

        val emojiTextView = view.findViewById<TextView>(R.id.textViewCategoryEmoji)
        val nameTextView = view.findViewById<TextView>(R.id.textViewCategoryName)

        if (category != null) {
            emojiTextView.text = category.emoji
            nameTextView.text = category.name
        }

        return view
    }
}