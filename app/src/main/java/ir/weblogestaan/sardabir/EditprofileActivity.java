package ir.weblogestaan.sardabir;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.util.EncodingUtils;

import ir.weblogestaan.sardabir.Classes.Post;
import ir.weblogestaan.sardabir.Classes.PostParams;
import ir.weblogestaan.sardabir.Classes.Session;
import ir.weblogestaan.sardabir.Classes.User;

public class EditprofileActivity extends BaseActivity {

    private ProgressBar prgLoading;
    public User user;
    ImageButton btnBack;
    TextView txtLink;
    WebView webNewsContent;
    Typeface typeFace;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);
        typeFace = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Yekan.ttf");
        Intent i = getIntent();
        user = (User)i.getSerializableExtra("user");
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
        txtLink.setText("ویرایش پروفایل");
        txtLink.setTypeface(typeFace);
        webNewsContent = (WebView) findViewById(R.id.webView);
        webNewsContent.setWebViewClient(new NewWebViewClient());
        webNewsContent.setWebChromeClient(new MyWebViewClient());
        webNewsContent.getSettings().setJavaScriptEnabled(true);
        webNewsContent.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        webNewsContent.getSettings().setSupportMultipleWindows(false);
        String url = PostParams.getBaseUrl() + "/ep/" + user.uid;
        Session session = new Session(getApplicationContext());
        byte[] post = EncodingUtils.getBytes("efsi="+ session.getMod(), "BASE64");
        webNewsContent.postUrl(url, post);
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
