package pw.bmyo.www.bmyobaselibrary.widget;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;

import pw.bmyo.www.bmyobaselibrary.source.MPMessage;
import pw.bmyo.www.bmyobaselibrary.source.NotProguard;
import pw.bmyo.www.bmyobaselibrary.utils.Utils;
import pw.bmyo.www.bmyobaselibrary.ui.BaseActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by huang on 2016/12/31.
 */

public class BMContentWebView extends WebView {

    private static String format;
    private static final String RES_FILE_NAME = "content.html";
    private ArrayList<String> mUrls = new ArrayList<>();
    private LoadCallback mCallback;

    public BMContentWebView(Context context) {
        super(context);
        init();
    }

    public BMContentWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) {
            return;
        }
        init();
    }

    public BMContentWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode()) {
            return;
        }
        init();
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface", "AddJavascriptInterface"})
    private void init() {
        WebSettings settings = getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        settings.setAppCacheEnabled(true);// 设置启动缓存
        settings.setSupportZoom(false);// 不支持缩放
        settings.setBuiltInZoomControls(false);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setJavaScriptEnabled(true);
        settings.setBlockNetworkImage(true);
        setFocusable(false);

        addJavascriptInterface(this, "content");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            settings.setAllowUniversalAccessFromFileURLs(true);
        }
        setWebViewClient(new WebViewClient() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                //requestHandle(request.getUrl().toString());
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //requestHandle(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                settings.setBlockNetworkImage(false);
                addListener();
                if (mCallback == null)
                    throw new RuntimeException("loadCallBack is null");
                mCallback.loadComplete();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                settings.setBlockNetworkImage(true);
            }
        });

        setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                Log.d(url, message);
                return false;
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (mCallback == null)
                    throw new RuntimeException("loadCallBack is null");
                mCallback.loading(newProgress);
            }


        });

    }

    public void loadData(String data) {
        Observable.just(RES_FILE_NAME)
                .map((str) -> {
                    if (format == null)
                        format = Utils.getAssetsFileAscii(getContext(), str);
                    return format;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((str) -> {
                    if (format == null) return;
                    String content = String.format(str, data);
                    loadData(content, "text/html; charset=UTF-8", null);
                });
    }

    public void addListener() {
        // 这段js函数的功能就是，遍历所有的img节点，并添加onclick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
        // 如要点击一张图片在弹出的页面查看所有的图片集合,则获取的值应该是个图片数组
        loadUrl("javascript:(function(){" +
                "   var objs = document.getElementsByTagName(\"img\");" +
                "   if(objs.length <= 0) return;" +
                "   var arrayObj = new Array(); " +
                "   for(var i = 0; i < objs.length; i++)" +
                "   {" +
                //      "objs[i].onclick=function(){alert(this.getAttribute(\"src\"));}" +
                "       objs[i].onclick = function(){" +
                "            content.imageClickListener(this.getAttribute(\"src\"));" +
                "       }; " +
                "       arrayObj.push(objs[i].src);" +
                "   } " +
                "   if(arrayObj.length > 0)" +
                "       content.allImageUrls(arrayObj);" +
                "})()");

        // 遍历所有的a节点,将节点里的属性传递过去(属性自定义,用于页面跳转)
        loadUrl("javascript:(function(){" +
                "   var objs = document.getElementsByTagName(\"a\");" +
                "   if(objs.length <= 0) return;" +
                "   for(var i = 0; i < objs.length;i++)" +
                "   {" +
                "       if(objs[i].href.length <= 0 ) {" +
                "             objs[i].href = \"javascript:;\";" +
                "       }"+
                "       objs[i].onclick = function() {" +
                "             content.textLinkClickListener(this.getAttribute(\"href\"));" +
                "       };" +
                "   }" +
                "})()");
    }

    @NotProguard
    @JavascriptInterface
    public void allImageUrls(String[] urls) {
        if (mUrls.size() > 0) {
            mUrls.clear();
        }
        Collections.addAll(mUrls, urls);
    }

    @NotProguard
    @JavascriptInterface
    public void imageClickListener(String url) {
        if (mUrls.size() > 0)
            EventBus.getDefault().post(new MPMessage(BaseActivity.MSG_TAG_IMAGE_VIEW, mUrls.indexOf(url), mUrls));
    }

    @NotProguard
    @JavascriptInterface
    public void textLinkClickListener(String href) {
        if (href.contentEquals("javascript:;")) return;
        requestHandle(href);
    }

    private void requestHandle(String url) {
        // 优酷视频跳转浏览器播放
        if (url.startsWith("http://v.youku.com/")) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addCategory("android.intent.category.BROWSABLE");
            Uri content_url = Uri.parse(url);
            intent.setData(content_url);
            getContext().startActivity(intent);
            return;

            // 电话、短信、邮箱
        } else if (url.startsWith(WebView.SCHEME_TEL) || url.startsWith("sms:") || url.startsWith(WebView.SCHEME_MAILTO)) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                getContext().startActivity(intent);
            } catch (ActivityNotFoundException ignored) {
            }
            return;
        }
        EventBus.getDefault().post(new MPMessage(BaseActivity.MSG_TAG_WEB_VIEW, url));
    }

    public void setCallback(LoadCallback callback) {
        mCallback = callback;
    }
}
