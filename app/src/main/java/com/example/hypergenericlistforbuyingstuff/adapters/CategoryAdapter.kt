package com.example.hypergenericlistforbuyingstuff.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hypergenericlistforbuyingstuff.databinding.ItemCategoryBinding
import com.example.hypergenericlistforbuyingstuff.models.Category

class CategoryAdapter(
    private var categories: List<Category>,
    private val onItemLongClick: (Category) -> Unit ) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding =
            ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category)
                holder.itemView.setOnLongClickListener {
            onItemLongClick(category)
            true         }
    }

    override fun getItemCount() = categories.size

    fun updateCategories(newCategories: List<Category>) {
        categories = newCategories
        notifyDataSetChanged()
    }

    class CategoryViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Category) {
            binding.textViewCategoryName.text = category.name
            binding.textViewCategoryEmoji.text = category.emoji
        }
    }
}