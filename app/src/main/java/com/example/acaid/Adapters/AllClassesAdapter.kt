package com.example.acaid.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.acaid.Models.AllClassesModel
import com.example.acaid.Models.AllStudentsModel
import com.example.acaid.R

class AllClassesAdapter(val list: MutableList<AllClassesModel>,
                        private val listener: OnClassClickListener): RecyclerView.Adapter<AllClassesAdapter.AllClassesViewHolder>() {
    class AllClassesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val className=itemView.findViewById<TextView>(R.id.className)
        val classStudents=itemView.findViewById<TextView>(R.id.classStudents)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllClassesViewHolder {
        val itemView=
            LayoutInflater.from(parent.context).inflate(R.layout.item_all_classes,parent,false)
       return AllClassesViewHolder(itemView)
    }

    override fun getItemCount(): Int=list.size

    override fun onBindViewHolder(holder: AllClassesViewHolder, position: Int) {
        val classItem = list[position]
        holder.className.text = classItem.className
        holder.classStudents.setOnClickListener {
            listener.onViewStudentsClicked(classItem.classId)
        }

    }

    interface OnClassClickListener {
        fun onViewStudentsClicked(classId: String)
    }


}
