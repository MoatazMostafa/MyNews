package com.android.mynews.view.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.mynews.R
import com.android.mynews.models.Article
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class ArticlesAdapter(private val newsList: ArrayList<Article>,
                      private val listener: OnItemClickListener) :
    RecyclerView.Adapter<ArticlesAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.news_list_item,
            parent,
            false
        )
        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = newsList[position]
        holder.titleTextView.text= article.title
        holder.sourceTextView.text="${article.source?.name}   ${getDate(article.publishedAt!!)}"
        Picasso.get().load(article.urlToImage).into(holder.imageView)
    }

    override fun getItemCount(): Int {
       return newsList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
    View.OnClickListener{
        val titleTextView:TextView = itemView.findViewById(R.id.news_list_item_title)
        val sourceTextView:TextView = itemView.findViewById(R.id.news_list_item_source)
        val imageView:ImageView = itemView.findViewById(R.id.news_list_item_imageView)
        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(view: View?) {
            val position=adapterPosition
            if(position != RecyclerView.NO_POSITION)
                listener.onItemClick(position)
        }
    }
    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDate(publishedAt: String) :String{
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val outputFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a")
        val parsedDate = inputFormat.parse(publishedAt)
        return outputFormat.format(parsedDate)
    }
}