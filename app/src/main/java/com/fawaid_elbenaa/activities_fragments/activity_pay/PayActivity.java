package com.fawaid_elbenaa.activities_fragments.activity_pay;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.fawaid_elbenaa.R;
import com.fawaid_elbenaa.databinding.ActivityWebViewBinding;
import com.fawaid_elbenaa.language.Language;

import io.paperdb.Paper;

public class PayActivity extends AppCompatActivity {
    private ActivityWebViewBinding binding;
    private String url ="";
    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase,Paper.book().read("lang","ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_web_view);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        url = intent.getStringExtra("url");

    }

    private void initView() {
  setUpWebView();

    }

    private void setUpWebView() {
        binding.progBar.setVisibility(View.GONE);
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        binding.webView.getSettings().setBuiltInZoomControls(false);
        binding.webView.loadUrl(url);
        binding.webView.setWebViewClient(new WebViewClient() {
                                             @Override
                                             public void onPageStarted(WebView view, String url, Bitmap favicon) {
                                                 super.onPageStarted(view, url, favicon);
                                             }

                                             @Override
                                             public void onPageFinished(WebView view, String url) {
                                                 binding.progBar.setVisibility(View.GONE);

                                                 if (url.contains("success")) {
                                                     setResult(RESULT_OK);
                                                     finish();
                                                     Toast.makeText(PayActivity.this, getString(R.string.suc), Toast.LENGTH_SHORT).show();

                                                 }
                                             }

                                             @Override
                                             public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                                                 super.onReceivedError(view, request, error);
                                                 binding.progBar.setVisibility(View.GONE);

                                                 binding.webView.setVisibility(View.INVISIBLE);
                                             }

                                             @Override
                                             public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                                                 super.onReceivedHttpError(view, request, errorResponse);
                                                 binding.progBar.setVisibility(View.GONE);

                                                 binding.webView.setVisibility(View.INVISIBLE);
                                             }
                                         }

        );
    }

}