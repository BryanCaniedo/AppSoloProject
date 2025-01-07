package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class MyAdapter(private val wordList: ArrayList<dataWord>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_word,parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return wordList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = wordList[position]

        holder.someWordName.text = currentItem.word

    }

    class MyViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){

     val someWordName : TextView = itemView.findViewById(R.id.textViewWord)

    }

}


