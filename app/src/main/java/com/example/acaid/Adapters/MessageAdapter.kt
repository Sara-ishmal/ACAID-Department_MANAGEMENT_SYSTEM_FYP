package com.example.acaid.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.acaid.Models.Message
import com.example.acaid.R

class MessageAdapter(
    private var messages: List<Message>,
    private val stdName: String,
    private val onDelete: (Message) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
    }

    inner class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageTextView: TextView = itemView.findViewById(R.id.messageTextView)
        private val timeTextView: TextView = itemView.findViewById(R.id.time)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

        fun bind(message: Message) {
            messageTextView.text = message.text
            timeTextView.text = convertTimestampToReadableFormat(message.time)
            deleteButton.visibility = View.GONE
            deleteButton.setOnClickListener { onDelete(message) }
        }
    }

    inner class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageTextView: TextView = itemView.findViewById(R.id.messageTextView)
        private val timeTextView: TextView = itemView.findViewById(R.id.time)
        private val userNameTextView: TextView = itemView.findViewById(R.id.userName)

        fun bind(message: Message) {
            messageTextView.text = message.text
            timeTextView.text = convertTimestampToReadableFormat(message.time)
            userNameTextView.text = message.name
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].name == stdName) VIEW_TYPE_SENT else VIEW_TYPE_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_send, parent, false)
            SentMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
            ReceivedMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is SentMessageViewHolder) {
            holder.bind(message)
        } else if (holder is ReceivedMessageViewHolder) {
            holder.bind(message)
        }
    }

    override fun getItemCount(): Int = messages.size

    fun updateMessages(newMessages: List<Message>) {
        messages = newMessages
        notifyDataSetChanged()
    }

    private fun convertTimestampToReadableFormat(timestamp: Long): String {
        val date = java.util.Date(timestamp)
        val format = java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault())
        return format.format(date)
    }
}