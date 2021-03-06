/*
 * Copyright 2017 Tua Rua Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Additional Terms
 * No part, or derivative of this Air Native Extensions's code is permitted
 * to be sold as the basis of a commercially packaged Air Native Extension which
 * undertakes the same purpose as this software. That is, a WebView for Windows,
 * OSX and/or iOS and/or Android.
 * All Rights Reserved. Tua Rua Ltd.
 */


package com.tuarua.webviewane

import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.webkit.WebView
import com.adobe.fre.FREContext
import com.adobe.fre.FREObject
import com.tuarua.frekotlin.*
import com.tuarua.frekotlin.display.FreBitmapDataKotlin
import com.tuarua.frekotlin.geom.Rect
import java.util.ArrayList

typealias FREArgv = ArrayList<FREObject>

@Suppress("unused", "UNUSED_PARAMETER", "UNCHECKED_CAST")
class KotlinController : FreKotlinMainController {
    private var isAdded: Boolean = false
    private var scaleFactor: Double = 1.0
    private var webViewController: WebViewController? = null
    private var capturedBitmapData: Bitmap? = null

    fun isSupported(ctx: FREContext, argv: FREArgv): FREObject? {
        return true.toFREObject()
    }

    fun init(ctx: FREContext, argv: FREArgv): FREObject? {
        argv.takeIf { argv.size > 4 } ?: return FreArgException("init")
        try {
            val initialUrl = String(argv[0])
            val viewPort = Rect(argv[1])
            val settings = Settings(argv[2])
            val sf = Double(argv[3])
            if (sf != null) {
                scaleFactor = sf
            }
            val backgroundColorFre = argv[4]
            val backgroundColor = backgroundColorFre.toColor(true)
            webViewController = WebViewController(ctx, initialUrl, scaleViewPort(viewPort),
                    settings, backgroundColor)

        } catch (e: FreException) {
            Log.e(TAG, e.message)
            return e.getError(Thread.currentThread().stackTrace)
        }
        return null
    }

    fun clearCache(ctx: FREContext, argv: FREArgv): FREObject? {
        webViewController?.clearCache()
        return null
    }

    fun zoomIn(ctx: FREContext, argv: FREArgv): FREObject? {
        webViewController?.zoomIn()
        return null
    }

    fun zoomOut(ctx: FREContext, argv: FREArgv): FREObject? {
        webViewController?.zoomOut()
        return null
    }

    fun setViewPort(ctx: FREContext, argv: FREArgv): FREObject? {
        argv.takeIf { argv.size > 0 } ?: return FreArgException("setViewPort")
        val viewPortFre = Rect(argv[0])
        webViewController?.viewPort = scaleViewPort(viewPortFre)
        return null
    }

    fun setVisible(ctx: FREContext, argv: FREArgv): FREObject? {
        argv.takeIf { argv.size > 0 } ?: return FreArgException("setVisible")
        val visible = Boolean(argv[0]) == true
        if (!isAdded) {
            webViewController?.add()
            isAdded = true
        }
        webViewController?.visible = visible

        return null
    }

    fun loadHTMLString(ctx: FREContext, argv: FREArgv): FREObject? {
        argv.takeIf { argv.size > 0 } ?: return FreArgException("loadHTMLString")
        val data = String(argv[0]) ?: return FreConversionException("data")
        webViewController?.loadHTMLString(data)
        return null
    }

    fun load(ctx: FREContext, argv: FREArgv): FREObject? {
        argv.takeIf { argv.size > 0 } ?: return FreArgException("load")
        val url = String(argv[0]) ?: return FreConversionException("url")
        webViewController?.loadUrl(url)
        return null
    }

    fun loadFileURL(ctx: FREContext, argv: FREArgv): FREObject? {
        argv.takeIf { argv.size > 0 } ?: return FreArgException("loadFileURL")
        val url = String(argv[0]) ?: return FreConversionException("url")
        webViewController?.loadFileURL(url)
        return null
    }

    fun reload(ctx: FREContext, argv: FREArgv): FREObject? {
        webViewController?.reload()
        return null
    }

    fun go(ctx: FREContext, argv: FREArgv): FREObject? {
        argv.takeIf { argv.size > 0 } ?: return FreArgException("go")
        val offset = Int(argv[0]) ?: return FreConversionException("offset")
        webViewController?.go(offset)
        return null
    }

    fun goBack(ctx: FREContext, argv: FREArgv): FREObject? {
        webViewController?.goBack()
        return null
    }

    fun goForward(ctx: FREContext, argv: FREArgv): FREObject? {
        webViewController?.goForward()
        return null
    }

    fun stopLoading(ctx: FREContext, argv: FREArgv): FREObject? {
        webViewController?.stopLoading()
        return null
    }

    fun reloadFromOrigin(ctx: FREContext, argv: FREArgv): FREObject? {
        webViewController?.reload()
        return null
    }

    fun allowsMagnification(ctx: FREContext, argv: FREArgv): FREObject? {
        return true.toFREObject()
    }

    fun showDevTools(ctx: FREContext, argv: FREArgv): FREObject? {
        WebView.setWebContentsDebuggingEnabled(true)
        return null
    }

    fun closeDevTools(ctx: FREContext, argv: FREArgv): FREObject? {
        WebView.setWebContentsDebuggingEnabled(false)
        return null
    }

    fun callJavascriptFunction(ctx: FREContext, argv: FREArgv): FREObject? {
        argv.takeIf { argv.size > 1 } ?: return FreArgException("callJavascriptFunction")
        val js = String(argv[0]) ?: return FreConversionException("js")
        val callback = String(argv[1])
        webViewController?.evaluateJavascript(js, callback)
        return null
    }

    fun evaluateJavaScript(ctx: FREContext, argv: FREArgv): FREObject? {
        argv.takeIf { argv.size > 1 } ?: return FreArgException("evaluateJavaScript")
        val js = String(argv[0]) ?: return FreConversionException("js")
        val callback = String(argv[1])
        webViewController?.evaluateJavascript(js, callback)
        return null
    }

    fun getCurrentTab(ctx: FREContext, argv: FREArgv): FREObject? {
        val ret = 0
        return ret.toFREObject()
    }

    fun getTabDetails(ctx: FREContext, argv: FREArgv): FREObject? {
        try {
            val o = FREObject("Vector.<com.tuarua.webview.TabDetails>")
            val vecTabs = FREArray(o)
            vecTabs.length = 1
            val currentTabFre = FREObject("com.tuarua.webview.TabDetails", 0,
                    webViewController?.url ?: ""
                    , webViewController?.title ?: ""
                    , webViewController?.isLoading ?: false
                    , webViewController?.canGoBack ?: false
                    , webViewController?.canGoForward ?: false
                    , webViewController?.progress ?: 0.0
            )
            vecTabs[0] = currentTabFre
            return vecTabs
        } catch (e: FreException) {
            Log.e(TAG, e.message)
        }
        return null
    }

    fun capture(ctx: FREContext, argv: FREArgv): FREObject? {
        argv.takeIf { argv.size > 0 } ?: return FreArgException("capture")
        val cropTo = scaleViewPort(Rect(argv[0]))
        capturedBitmapData = webViewController?.capture(cropTo)
        sendEvent(WebViewEvent.ON_CAPTURE_COMPLETE, "")
        return null
    }

    fun getCapturedBitmapData(ctx: FREContext, argv: FREArgv): FREObject? {
        val cbmd = capturedBitmapData ?: return null
        val bmd = FreBitmapDataKotlin(cbmd)
        return bmd.rawValue
    }

    fun backForwardList(ctx: FREContext, argv: FREArgv): FREObject? = null
    fun setCurrentTab(ctx: FREContext, argv: FREArgv): FREObject? = null
    fun addTab(ctx: FREContext, argv: FREArgv): FREObject? = null
    fun closeTab(ctx: FREContext, argv: FREArgv): FREObject? = null
    fun injectScript(ctx: FREContext, argv: FREArgv): FREObject? = null
    fun print(ctx: FREContext, argv: FREArgv): FREObject? = null
    fun printToPdf(ctx: FREContext, argv: FREArgv): FREObject? = null
    fun focus(ctx: FREContext, argv: FREArgv): FREObject? = null
    fun onFullScreen(ctx: FREContext, argv: FREArgv): FREObject? = null
    fun shutDown(ctx: FREContext, argv: FREArgv): FREObject? = null

    private fun scaleViewPort(rect: Rect?): Rect {
        if (rect == null) {
            return Rect(0, 0, 0, 0)
        }
        return Rect(
                (rect.x * scaleFactor).toInt(),
                (rect.y * scaleFactor).toInt(),
                (rect.width * scaleFactor).toInt(),
                (rect.height * scaleFactor).toInt())
    }

    fun getOsVersion(ctx: FREContext, argv: FREArgv): FREObject? {
        return intArrayOf(Build.VERSION.SDK_INT, 0, 0).toFREArray()
    }

    override fun dispose() {
        webViewController?.dispose()
        webViewController = null
    }

    @Suppress("PropertyName")
    override val TAG: String
        get() = this::class.java.simpleName
    private var _context: FREContext? = null
    override var context: FREContext?
        get() = _context
        set(value) {
            _context = value
        }

}