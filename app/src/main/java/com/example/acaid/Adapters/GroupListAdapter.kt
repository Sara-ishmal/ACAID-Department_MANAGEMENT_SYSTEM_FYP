package com.example.acaid.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.acaid.Models.GroupListModel
import com.example.acaid.R

class GroupListAdapter(
    private val list: MutableList<GroupListModel>,
    private val onGroupClick: (GroupListModel) -> Unit
) : RecyclerView.Adapter<GroupListAdapter.GroupViewHolder>() {

    class GroupViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val profilePicture = itemView.findViewById<com.google.android.material.imageview.ShapeableImageView>(R.id.groupDp)
        val groupName = itemView.findViewById<TextView>(R.id.tvGroupName)
        val groupSubtitle = itemView.findViewById<TextView>(R.id.tvGroupSubtitle)
        val date = itemView.findViewById<TextView>(R.id.date)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_group_list, parent, false)
        return GroupViewHolder(itemView)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val currentItem = list[position]
        holder.groupName.text = currentItem.groupName
        holder.groupSubtitle.text = currentItem.groupSubtitle
        holder.date.text = currentItem.date
        holder.profilePicture.setImageResource(R.drawable.group)
        holder.itemView.setOnClickListener {
            onGroupClick(currentItem)
        }


    }
    fun filterList(filteredGroupList: List<GroupListModel>) {
        list.clear()
        list.addAll(filteredGroupList)
        notifyDataSetChanged()
    }



}