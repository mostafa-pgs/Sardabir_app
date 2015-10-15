package ir.weblogestaan.sardabir;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import ir.weblogestaan.sardabir.Classes.Post;
import ir.weblogestaan.sardabir.Classes.PostParams;

public class LinkActivity extends BaseActivity {

    private ProgressBar prgLoading;
    public Post post;
    ImageButton btnBack;
    TextView txtLink;
    WebView webNewsContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link);
        Intent i = getIntent();
        post = (Post)i.getSerializableExtra("post");
        prgLoading = (ProgressBar) findViewById(R.id.prgPercent);
        prgLoading.setMax(100);
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_on_click));
                finish();
            }
        });
        txtLink = (TextView) findViewById(R.id.txtLink);
        txtLink.setText(post.link);
        webNewsContent = (WebView) findViewById(R.id.webView);
        webNewsContent.setWebViewClient(new NewWebViewClient());
        webNewsContent.setWebChromeClient(new MyWebViewClient());
        webNewsContent.getSettings().setJavaScriptEnabled(true);
        String url = PostParams.getBaseUrl() + "/node/" + post.nid;
        webNewsContent.loadUrl(url);
        prgLoading.setProgress(0);
        prgLoading.setVisibility(View.VISIBLE);
        setStatusbarColor();
    }
    private class MyWebViewClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            prgLoading.setProgress(newProgress);
            if (newProgress >= 98)
                prgLoading.setVisibility(View.GONE);
            super.onProgressChanged(view, newProgress);
        }
    }

    private class NewWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public void onStop()
    {
        try {
            if (webNewsContent != null) {
                webNewsContent.destroy();
                webNewsContent = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStop();
    }
}
