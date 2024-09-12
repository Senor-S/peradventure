package com.example.peradventure


import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class WebViewActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        // Toolbar mit Zurück-Button einrichten
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Webseite anzeigen"

        // WebView initialisieren
        webView = findViewById(R.id.webView)

        // Einstellungen für WebView
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()  // Verhindert das Öffnen der Links im Browser

        // URL aus Intent abrufen
        val url = intent.getStringExtra("URL")
        if (url != null) {
            webView.loadUrl(url)
        }

        // Zurück-Button in der Toolbar
        toolbar.setNavigationOnClickListener {
            onBackPressed()  // Geht zum vorherigen Fenster zurück
        }
    }

    // Behandlung des Zurück-Buttons
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()  // Geht in der WebView zurück
        } else {
            super.onBackPressed()  // Schließt die Activity, wenn keine Seiten zum Zurückgehen verfügbar sind
        }
    }
}
