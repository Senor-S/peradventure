package com.example.peradventure

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import java.io.IOException
import org.jsoup.nodes.Document

class MainActivity : AppCompatActivity() {

    private lateinit var searchField: EditText
    private lateinit var searchButton: Button
    private lateinit var resultsListView: ListView
    private lateinit var progressBar: ProgressBar

    private var urls: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchField = findViewById(R.id.searchField)
        searchButton = findViewById(R.id.searchButton)
        resultsListView = findViewById(R.id.resultsListView)
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.GONE

        searchButton.setOnClickListener {
            val query = searchField.text.toString()
            if (query.isNotEmpty()) {
                fetchPageTitles(query)
            } else {
                Toast.makeText(this, "Bitte gib einen Suchbegriff ein", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchPageTitles(query: String) {
        progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val perchanceLinks = fetchPerchanceLinks()
                val searchResultTitles = fetchPageTitles(perchanceLinks, query)
                updateResults(searchResultTitles)
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this@MainActivity, "Fehler beim Abrufen der Daten", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private suspend fun fetchPerchanceLinks(): List<String> {
        val doc = connectToPerchanceOrg()
        val links = doc.select("a[href]")
        val perchanceLinks = mutableListOf<String>()
        for (link in links) {
            val absUrl = link.absUrl("href")
            if (absUrl.startsWith("https://perchance.org/") && absUrl.isNotEmpty()) {
                perchanceLinks.add(absUrl)
            }
        }
        return perchanceLinks
    }

    private suspend fun connectToPerchanceOrg(): Document {
        return withContext(Dispatchers.IO) {
            Jsoup.connect("https://perchance.org/").get()
        }
    }

    private suspend fun fetchPageTitles(links: List<String>, query: String): List<String> {
        val searchResultTitles = mutableListOf<String>()
        for (url in links) {
            try {
                val subDoc = connectToUrl(url)
                val title = subDoc.title()
                if (title.contains(query, ignoreCase = true)) {
                    searchResultTitles.add(title)
                }
            } catch (e: Exception) {
                println("Error fetching or parsing URL: $url")
                e.printStackTrace()
            }
        }
        return searchResultTitles
    }

    private suspend fun connectToUrl(url: String): Document {
        return withContext(Dispatchers.IO) {
            Jsoup.connect(url).get()
        }
    }

    private fun updateResults(titles: List<String>) {
        val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1, titles)
        resultsListView.adapter = adapter
    }
}