package com.example.simplewebbrowser

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.widget.ContentLoadingProgressBar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : AppCompatActivity() {

    private val Webview: WebView by lazy {
        findViewById(R.id.WebView)
    }
    private val adressbar: EditText by lazy {
        findViewById(R.id.addressBar)
    }
    private val homebutton: ImageButton by lazy { 
        findViewById(R.id.homeButton)
    }
    private val gobutton: ImageButton by lazy {
        findViewById(R.id.forwordButton)
    }
    private val backbutton: ImageButton by lazy {
        findViewById(R.id.backButton)
    }
    private val refreshLayout: SwipeRefreshLayout by lazy {
        findViewById(R.id.refreshLatout)
    }
    private val progressBar: ContentLoadingProgressBar by lazy {
        findViewById(R.id.progressbar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        bindingView()
    }

    override fun onBackPressed() {
        if (Webview.canGoBack()) {
            Webview.goBack()
        } else {
            super.onBackPressed()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initViews() {
        Webview.apply {
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            settings.javaScriptEnabled = true
            loadUrl(DEFAULT_URL)
        }
    }

    private fun bindingView() {
        homebutton.setOnClickListener {
            Webview.loadUrl(DEFAULT_URL)
        }

        adressbar.setOnEditorActionListener { v, actionID, _ ->
            if (actionID == EditorInfo.IME_ACTION_DONE) {
                val loadingUrl = v.text.toString()
                if(URLUtil.isNetworkUrl(loadingUrl)){
                    Webview.loadUrl(loadingUrl)
                }else{
                    Webview.loadUrl("http://$loadingUrl")
                }
            }

            return@setOnEditorActionListener false
        }
        backbutton.setOnClickListener {
            Webview.goBack()
        }

        gobutton.setOnClickListener {
            Webview.goForward()
        }

        refreshLayout.setOnRefreshListener {
            Webview.reload()
        }
    }

    inner class WebViewClient : android.webkit.WebViewClient() { // inner을 써야만 상위클래스에 들어갈수있음.
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)

            progressBar.show()
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            refreshLayout.isRefreshing = false
            progressBar.hide()
            gobutton.isEnabled = Webview.canGoBack()
            backbutton.isEnabled = Webview.canGoForward()
            adressbar.setText(url)
        }
    }

    inner class WebChromeClient : android.webkit.WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)

            progressBar.progress = newProgress
        }
    }

    companion object {
        private const val DEFAULT_URL = "http://www.google.com"
    }
}