package com.android.mynews.view

import android.annotation.SuppressLint
import android.content.Intent
import android.gesture.Gesture
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.core.view.GestureDetectorCompat
import com.android.mynews.R
import com.android.mynews.models.Article
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_article.*
import java.text.SimpleDateFormat
import kotlin.math.abs


class ArticleActivity : AppCompatActivity(){
    lateinit var gestureDetectorCompat: GestureDetectorCompat
    var url=""
    @SuppressLint( "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)

        gestureDetectorCompat= GestureDetectorCompat(this,GestureListener()) // Swipe Gesture Detector
        val article=intent.getSerializableExtra("Article") as Article // Receive selected Article

        Picasso.get().load(article.urlToImage).into(article_imageView) // Set Image URL to ImageView
        article_title_textView.text=article.title // Set Article data
        article_source_textView.text="${article.source?.name}   ${getDate(article.publishedAt!!)}"
        article_description_textView.text=article.description
        article_author_textView.text="Author: ${article.author}"
        article_url_textView.text="Article Link: ${article.url}"
        url= article.url!! // catch url for share

        article_back_imageView.setOnClickListener {
            finish()
        }
        article_share_imageView.setOnClickListener{
            shareUrl(url) // Start Share url
        }

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean { //Swipe Event
        return if(gestureDetectorCompat.onTouchEvent(event)) {
            true
        }else super.onTouchEvent(event)
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDate(publishedAt: String) :String{  // Change timeStamp to normal date and time format
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val outputFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a")
        val parsedDate = inputFormat.parse(publishedAt)
        return outputFormat.format(parsedDate!!)
    }

    fun shareUrl(url:String){
        val intent = Intent()
        intent.action=Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT,url)
        intent.type="text/plain"
        startActivity(Intent.createChooser(intent,"Share:"))
    }

    inner class GestureListener:GestureDetector.SimpleOnGestureListener(){ // Swipe Listener
        override fun onFling(
            downEvent: MotionEvent?,
            moveEvent: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val diffX = moveEvent?.x?.minus(downEvent!!.x) ?: 0.0F
            val diffY = moveEvent?.y?.minus(downEvent!!.y) ?: 0.0F
            if(abs(diffX) <= abs(diffY)) { // Abs Delta X less than Abs Delta Y so swipe is vertically
               if(abs(diffY)>100&& abs(velocityY)>100){
                   if(diffY<=0) // Delta Y less than Zero so the Swipe is up
                       shareUrl(url)
               }
            }
            return super.onFling(downEvent, moveEvent, velocityX, velocityY)
        }
    }

}