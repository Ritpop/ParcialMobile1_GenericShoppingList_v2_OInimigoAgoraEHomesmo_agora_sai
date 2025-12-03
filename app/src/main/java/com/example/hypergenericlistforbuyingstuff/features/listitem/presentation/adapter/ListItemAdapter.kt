package com.example.hypergenericlistforbuyingstuff.features.listitem.presentation.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hypergenericlistforbuyingstuff.databinding.ItemCategoryHeaderBinding
import com.example.hypergenericlistforbuyingstuff.databinding.ItemListItemBinding
import com.example.hypergenericlistforbuyingstuff.models.GroupedListItem
import com.example.hypergenericlistforbuyingstuff.models.ListItem

class ListItemAdapter(
    private var items: List<GroupedListItem>,
    private val onCheckboxClick: (ListItem) -> Unit,
    private val onItemLongClick: (ListItem) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var categoryEmojis: Map<String, String> = emptyMap()

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    fun setCategoryMap(map: Map<String, String>) {
        categoryEmojis = map
        notifyDataSetChanged()
    }

    class CategoryHeaderViewHolder(val binding: ItemCategoryHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(header: GroupedListItem.Header) {
            binding.textViewCategoryName.text = header.categoryName
            binding.textViewCategoryEmoji.text = header.emoji
        }
    }

    class ListItemViewHolder(val binding: ItemListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: ListItem,
            onCheckboxClick: (ListItem) -> Unit,
            emojiMap: Map<String, String>
        ) {
            binding.textViewItemName.text = item.name
            val details = "${item.quantity} ${item.unit}"
            binding.textViewItemDetails.text = details

            binding.checkBoxItem.setOnCheckedChangeListener(null)
            binding.checkBoxItem.isChecked = item.isChecked

            val emoji = emojiMap[item.category] ?: "📦"
            binding.textViewCategoryEmoji.text = emoji

            if (item.isChecked) {
                binding.textViewItemName.paintFlags =
                    binding.textViewItemName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                binding.textViewItemDetails.paintFlags =
                    binding.textViewItemDetails.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                binding.root.alpha = 0.5f
            } else {
                binding.textViewItemName.paintFlags =
                    binding.textViewItemName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                binding.textViewItemDetails.paintFlags =
                    binding.textViewItemDetails.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                binding.root.alpha = 1.0f
            }

            binding.checkBoxItem.setOnClickListener {
                onCheckboxClick(item)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is GroupedListItem.Header -> TYPE_HEADER
            is GroupedListItem.Item -> TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER -> {
                val binding = ItemCategoryHeaderBinding.inflate(inflater, parent, false)
                CategoryHeaderViewHolder(binding)
            }
            TYPE_ITEM -> {
                val binding = ItemListItemBinding.inflate(inflater, parent, false)
                ListItemViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val currentItem = items[position]) {
            is GroupedListItem.Header -> {
                (holder as CategoryHeaderViewHolder).bind(currentItem)
            }
            is GroupedListItem.Item -> {
                (holder as ListItemViewHolder).bind(
                    currentItem.listItem,
                    onCheckboxClick,
                    categoryEmojis
                )
                holder.itemView.setOnLongClickListener {
                    onItemLongClick(currentItem.listItem)
                    true
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<GroupedListItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}