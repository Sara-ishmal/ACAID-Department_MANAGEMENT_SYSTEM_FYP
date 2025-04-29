package com.example.acaid.Adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.acaid.Models.Event
import com.example.acaid.R

class EventAdapter(private val events: MutableList<Event>) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.eventTitle.text = event.title
        holder.eventDescription.text = event.description
        holder.eventDate.text = event.date
        holder.eventTime.text = event.time
        holder.eventLocation.text = event.location

        if (event.imageUrl.isNotEmpty()) {
            val imageBitmap = decodeBase64ToBitmap(event.imageUrl)
            holder.eventImage.setImageBitmap(imageBitmap)
        } else {
            holder.eventImage.setImageResource(R.drawable.img_unsplash)
        }
    }

    override fun getItemCount(): Int {
        return events.size
    }

    private fun decodeBase64ToBitmap(encodedImage: String): Bitmap {
        val decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventTitle: TextView = itemView.findViewById(R.id.eventTitle)
        val eventDescription: TextView = itemView.findViewById(R.id.eventDescription)
        val eventDate: TextView = itemView.findViewById(R.id.eventDate)
        val eventTime: TextView = itemView.findViewById(R.id.eventTime)
        val eventLocation: TextView = itemView.findViewById(R.id.eventLocation)
        val eventImage: ImageView = itemView.findViewById(R.id.eventImage)
    }
}
