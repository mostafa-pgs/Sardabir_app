package ir.weblogestaan.sardabir;

import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SplashActivity extends BaseActivity {

    private ImageView imgAppLogo;
    private TextView txtAppName, txtAppDesc,txtError;
    private static ProgressBar progressBar;
    private ImageButton btnReload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Typeface typeFace = Typeface.createFromAsset(this.getAssets(), "fonts/Yekan.ttf");
        progressBar = (ProgressBar) findViewById(R.id.prgProgress);
        imgAppLogo = (ImageView) findViewById(R.id.imgAppLogo);
        txtAppName = (TextView) findViewById(R.id.txtAppName);
        txtAppDesc = (TextView) findViewById(R.id.txtAppDesc);
        txtError = (TextView) findViewById(R.id.txtError);
        txtAppDesc.setTypeface(typeFace);
        txtAppName.setTypeface(typeFace);
        txtError.setTypeface(typeFace);
        btnReload = (ImageButton) findViewById(R.id.btnImgRelaod);
        btnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_rotate));
                main_rule();
            }
        });
        main_rule();
    }

    public void main_rule(){
        try{
            if(!isNetworkConnected())
            {
                progressBar.setVisibility(View.INVISIBLE);
                txtError.setVisibility(View.VISIBLE);
                btnReload.setVisibility(View.VISIBLE);
                btnReload.clearAnimation();
            }
            else
            {
                //progressBar.setVisibility(View.VISIBLE);
                txtError.setVisibility(View.GONE);
                btnReload.setVisibility(View.GONE);
                CheckLogin(null);
            }
        }
        catch(Exception cx)
        {
            finish();
        }
    }

    /* Helper Functions */
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            return false;
        } else
            return true;
    }
}
