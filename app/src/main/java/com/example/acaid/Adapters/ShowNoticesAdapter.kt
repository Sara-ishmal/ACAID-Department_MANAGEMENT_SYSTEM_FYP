package com.example.acaid.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.acaid.Models.Notice
import com.example.acaid.R
import java.text.SimpleDateFormat
import java.util.Locale

class ShowNoticesAdapter(val list: MutableList<Notice>): RecyclerView.Adapter<ShowNoticesAdapter.ShowNoticesViewHolder>() {
    class ShowNoticesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val noticeText=itemView.findViewById<TextView>(R.id.noticeText)
        val noticeTimestamp=itemView.findViewById<TextView>(R.id.noticeTimestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowNoticesViewHolder {
        val itemView= LayoutInflater.from(parent.context).inflate(R.layout.item_show_notice,parent,false)
        return ShowNoticesViewHolder(itemView)
    }

    override fun getItemCount(): Int =list.size

    override fun onBindViewHolder(holder: ShowNoticesViewHolder, position: Int) {
        val currentItem = list[position]
        holder.noticeText.text = currentItem.notice

        val timestamp = currentItem.timestamp?.toDate()
        val formattedTimestamp = formatTimestamp(timestamp)
        holder.noticeTimestamp.text = formattedTimestamp
    }

    private fun formatTimestamp(date: java.util.Date?): String {
        return if (date != null) {
            val formatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            formatter.format(date)
        } else {
            "Unknown date"
        }
    }

}