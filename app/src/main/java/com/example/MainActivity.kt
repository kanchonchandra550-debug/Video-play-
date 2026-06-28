package com.example

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainContent()
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MainContent() {
    // Elegant deep background color to prevent flash of white during load
    val deepSpaceBg = Color(0xFF030712)
    var webViewRef: WebView? = remember { null }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(deepSpaceBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    webViewRef = this
                    
                    // Securely configure WebView settings
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.allowFileAccess = true
                    settings.useWideViewPort = true
                    settings.loadWithOverviewMode = true
                    settings.databaseEnabled = true
                    settings.mediaPlaybackRequiresUserGesture = false
                    
                    // Setup custom clients
                    webViewClient = object : WebViewClient() {
                        @Deprecated("Deprecated in Java")
                        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                            // Keep all navigation inside the WebView itself
                            return false
                        }
                    }
                    
                    webChromeClient = WebChromeClient()
                    
                    // Load our premium local HTML5 app from assets
                    loadUrl("file:///android_asset/index.html")
                }
            },
            update = {
                webViewRef = it
            }
        )
    }

    // Capture Android back gestures to navigate back inside the WebView history
    BackHandler(enabled = true) {
        webViewRef?.let { webView ->
            if (webView.canGoBack()) {
                webView.goBack()
            } else {
                // Let the system handle standard back exit
                webView.context as? ComponentActivity ?: return@let
                (webView.context as ComponentActivity).finish()
            }
        }
    }
}
