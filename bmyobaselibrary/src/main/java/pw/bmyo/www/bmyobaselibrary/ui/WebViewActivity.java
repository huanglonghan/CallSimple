package pw.bmyo.www.bmyobaselibrary.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import pw.bmyo.www.bmyobaselibrary.R;

/**
 * Created by huang on 2017/2/7.
 */
public class WebViewActivity extends pw.bmyo.www.bmyobaselibrary.source.BaseActivity {

    @BindView(R.id.web_content)
    WebView mWebView;
    @BindView(R.id.content_load_pb)
    ProgressBar mProgressBar;
    @BindView(R.id.left_back)
    ImageButton mLeftBack;
    @BindView(R.id.title_tv)
    TextView mTitleTv;
    @BindView(R.id.video_fullView)
    FrameLayout mVideoFullView;


    @Override
    protected int getContentLayoutId() {
        return R.layout.web_view_activity;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebSettings settings = mWebView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        settings.setAppCacheEnabled(true);// 设置启动缓存
        settings.setSupportZoom(false);// 不支持缩放
        settings.setBuiltInZoomControls(false);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setJavaScriptEnabled(true);
        settings.setBlockNetworkImage(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            settings.setAllowUniversalAccessFromFileURLs(true);
        }

        mWebView.setWebViewClient(new WebViewClient() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (mProgressBar != null)
                    mProgressBar.setVisibility(View.VISIBLE);
                settings.setBlockNetworkImage(true);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (mProgressBar != null)
                    mProgressBar.setVisibility(View.GONE);
                settings.setBlockNetworkImage(false);
            }

        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (mTitleTv != null)
                    mTitleTv.setText(title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (mProgressBar != null)
                    mProgressBar.setProgress(newProgress);
            }
        });
        mWebView.loadUrl(getIntent().getDataString());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @OnClick(R.id.left_back)
    public void onClick() {
        exit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null)
            mWebView.destroy();
    }
}
