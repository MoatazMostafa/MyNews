package com.android.mynews.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.mynews.R
import com.android.mynews.models.Article
import com.android.mynews.view.adapters.ArticlesAdapter
import com.android.mynews.viewmodels.HomeViewModel
import kotlinx.android.synthetic.main.activity_home.*
import kotlin.collections.ArrayList

class HomeActivity : AppCompatActivity(),ArticlesAdapter.OnItemClickListener{
    private lateinit var homeVM: HomeViewModel
    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        homeVM = HomeViewModel(application)
        homeVM.selectedCountry=homeVM.preference.getCountry()   //Get Saved Country from SharedPreference

        setCountryTextView()
        getNew()

        home_news_recyclerList.layoutManager=LinearLayoutManager(this)  //Preparation RecyclerView
        home_news_recyclerList.setHasFixedSize(true)
        homeVM.requestStatus.observe(this,  //LiveData observe when ViewModel receive api response
            { if(it=="ok") {
                setArticlesAdapter(homeVM.articleList)  //Pass ArticleList to RecyclerView Adapter
                home_last_sync_textView.text = "Last Sync: ${homeVM.getCurrentTime()}" //Update last sync textView (only updated when data received online from api)
                homeVM.preference.saveArticleList(homeVM.articleList)  //Save data in SharedPreference
                homeVM.preference.saveLastSync(homeVM.getCurrentTime())
                }
                else showSavedPreference()  //Data didn't received Successfully
            })

        home_sync_imageView.setOnClickListener {
            home_progressBar.visibility=View.VISIBLE
            home_news_recyclerList.visibility=View.GONE
            getNew() // Re-Get data when sync button clicked
        }

        home_country_textView.setOnClickListener {
            showCountryAlertDialog()
        }

        home_search_editText.addTextChangedListener(object : TextWatcher {
            @SuppressLint("DefaultLocale")
            override fun afterTextChanged(s: Editable?) {
                if(home_search_editText.text.toString()=="")
                    homeVM.articleList = homeVM.originalList // When search bar is empty return list to it's origin
                else
                    homeVM.articleList = // If not Search by entered text in articles titles
                        homeVM.originalList.filter { it.title!!.toUpperCase().contains(home_search_editText.text.toString().toUpperCase()) } as ArrayList<Article>

                setArticlesAdapter(homeVM.articleList) // Set filtered list or original list to adapter
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                home_progressBar.visibility=View.VISIBLE
                home_news_recyclerList.visibility=View.GONE
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    override fun onItemClick(position: Int) {  // RecyclerView item click handler
        val article= homeVM.articleList[position]
        val intent = Intent(this, ArticleActivity::class.java)
        intent.putExtra("Article", article) // Pass Selected Article to ArticleActivity
        this.startActivity(intent)
    }

    private fun setArticlesAdapter(Articles:ArrayList<Article>){ //Pass list to adapter
        home_progressBar.visibility=View.GONE
        home_news_recyclerList.adapter = ArticlesAdapter(Articles, this)
        home_news_recyclerList.visibility=View.VISIBLE
    }

    @SuppressLint("SetTextI18n")
    private fun showSavedPreference() { // In Offline mode data from preference set to adapter
        if(homeVM.preference.getArticleList()!=null) {
            homeVM.articleList=homeVM.preference.getArticleList()!!
            homeVM.originalList = homeVM.articleList
            setArticlesAdapter(homeVM.articleList)
            home_last_sync_textView.text = "Last Sync: ${homeVM.preference.getLastSync()}" //Get last saved sync date
        }
    }

    private fun getNew(){
        if(homeVM.isNetworkAvailable(this)) //Check Internet connection
            homeVM.getNews() // Start of online scenario
        else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
            showSavedPreference() // Start of offline scenario
        }
    }

    private fun showCountryAlertDialog() { //Country selection from alert dialog
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)// User have to select and click ok
        builder.setTitle("Select Country")
        val countries =  arrayOf("USA","Egypt")  // USA news or Egyptian news
        builder.setSingleChoiceItems(countries,homeVM.selectedCountry){ _, selected ->
            if(homeVM.isNetworkAvailable(this))
                homeVM.selectedCountry=selected
        }
        builder.setPositiveButton("OK") { _, _ ->
            homeVM.preference.saveCountry(homeVM.selectedCountry)  //Save selection in preference
            setCountryTextView()
            getNew() // Re-Get data
         }

        val dialog = builder.create()
        dialog.show()
    }

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    private fun setCountryTextView() {
        if(homeVM.selectedCountry==0) {
            home_country_textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_flag_us, 0, 0, 0)
            home_country_textView.text="USA"
        }
        else if(homeVM.selectedCountry==1){
            home_country_textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_flag_eg, 0, 0, 0)
            home_country_textView.text="Egypt"
        }
    }
}