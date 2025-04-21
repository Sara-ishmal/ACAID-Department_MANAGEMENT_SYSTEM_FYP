package com.example.acaid.Adapters

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.acaid.Models.ResourceModel
import com.example.acaid.R

class ResourcesAdapter(private val list: MutableList<ResourceModel>, private val onItemClick: (ResourceModel) -> Unit): RecyclerView.Adapter<ResourcesAdapter.ResourcesViewHolder>(){
    class ResourcesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val fileIcon=itemView.findViewById<ImageView>(R.id.fileIcon)
        val fileName=itemView.findViewById<TextView>(R.id.fileName)
        val fileDate=itemView.findViewById<TextView>(R.id.fileDate)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResourcesViewHolder {
        val itemView= LayoutInflater.from(parent.context).inflate(R.layout.item_resource,parent,false)
       return ResourcesViewHolder(itemView)
    }

    override fun getItemCount(): Int=list.size

    override fun onBindViewHolder(holder: ResourcesViewHolder, position: Int) {
         val currentItem=list[position]
        holder.fileName.text=currentItem.title
        val timestamp = currentItem.timestamp
        if (timestamp != null) {
            val timeInMillis = timestamp.toDate().time
            val relativeTime = DateUtils.getRelativeTimeSpanString(
                timeInMillis,
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS
            )
            holder.fileDate.text = relativeTime
        } else {
            holder.fileDate.text = "No date"
        }

        when(currentItem.type){
            "PDF (.pdf)" -> holder.fileIcon.setImageResource(R.drawable.icons8_pdf_96)
            "PowerPoint (.ppt/.pptx)" -> holder.fileIcon.setImageResource(R.drawable.icons8_ppt_96)
            "Word Document (.doc/.docx)" -> holder.fileIcon.setImageResource(R.drawable.icons8_docs)
            "Image (.jpg/.png)" -> holder.fileIcon.setImageResource(R.drawable.icons8_image_file)
            "Text File (.txt)" -> holder.fileIcon.setImageResource(R.drawable.icons8_txt_file_100)
            "Any File" -> holder.fileIcon.setImageResource(R.drawable.any_doc)

        }
        holder.itemView.setOnClickListener {
            onItemClick(currentItem)
        }


    }
    fun updateList(newList: MutableList<ResourceModel>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

}