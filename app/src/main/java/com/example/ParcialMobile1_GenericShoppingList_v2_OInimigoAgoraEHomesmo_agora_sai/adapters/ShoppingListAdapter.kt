package com.example.ParcialMobile1_GenericShoppingList_v2_OInimigoAgoraEHomesmo_agora_sai.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ParcialMobile1_GenericShoppingList_v2_OInimigoAgoraEHomesmo_agora_sai.R
import com.example.ParcialMobile1_GenericShoppingList_v2_OInimigoAgoraEHomesmo_agora_sai.databinding.ItemShoppingListBinding
import com.example.ParcialMobile1_GenericShoppingList_v2_OInimigoAgoraEHomesmo_agora_sai.models.ShoppingList

class ShoppingListAdapter(
    private var lists: List<ShoppingList>,
    private val onItemClick: (ShoppingList) -> Unit,     private val onItemLongClick: (ShoppingList) -> Unit ) : RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder>() {

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
            true         }
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
                try {
                    val uri = Uri.parse(list.imagePath)
                    binding.imageViewList.setImageURI(uri)
                } catch (e: Exception) {
                    binding.imageViewList.setImageResource(R.mipmap.ic_launcher)
                }
            } else {
                binding.imageViewList.setImageResource(R.mipmap.ic_launcher)
            }
        }
    }
}