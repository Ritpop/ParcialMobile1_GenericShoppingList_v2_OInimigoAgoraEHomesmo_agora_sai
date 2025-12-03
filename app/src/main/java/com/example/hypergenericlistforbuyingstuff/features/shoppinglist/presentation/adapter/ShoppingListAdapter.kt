package com.example.hypergenericlistforbuyingstuff.features.shoppinglist.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.hypergenericlistforbuyingstuff.R
import com.example.hypergenericlistforbuyingstuff.databinding.ItemShoppingListBinding
import com.example.hypergenericlistforbuyingstuff.models.ShoppingList

class ShoppingListAdapter(
    private var lists: List<ShoppingList>,
    private val onItemClick: (ShoppingList) -> Unit,
    private val onItemLongClick: (ShoppingList) -> Unit
) : RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListViewHolder {
        val binding =
            ItemShoppingListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShoppingListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShoppingListViewHolder, position: Int) {
        val list = lists[position]
        holder.bind(list)
        holder.itemView.setOnClickListener {
            onItemClick(list)
        }
        holder.itemView.setOnLongClickListener {
            onItemLongClick(list)
            true
        }
    }

    override fun getItemCount(): Int = lists.size

    fun updateLists(newLists: List<ShoppingList>) {
        lists = newLists
        notifyDataSetChanged()
    }

    class ShoppingListViewHolder(private val binding: ItemShoppingListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(list: ShoppingList) {
            binding.textViewListName.text = list.name

            if (!list.imagePath.isNullOrBlank()) {
                Glide.with(binding.root.context)
                    .load(list.imagePath)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.mipmap.ic_launcher)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(binding.imageViewList)
            } else {
                binding.imageViewList.setImageResource(R.mipmap.ic_launcher)
            }
        }
    }
}