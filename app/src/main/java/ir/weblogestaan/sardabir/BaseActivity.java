package ir.weblogestaan.sardabir;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import ir.weblogestaan.sardabir.Classes.PostParams;

public class BaseActivity extends Activity {

    private static final String TAG = "BaseActivity";

    private ImageView current_user_logo;
    private ImageButton btnImgNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        current_user_logo = (ImageView) findViewById(R.id.imgProfileImage);
        btnImgNotifications = (ImageButton) findViewById(R.id.btnImgNotifications);
        btnImgNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Notification", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setUserProfile()
    {
        String url =  (PostParams.current_user != null) ? PostParams.current_user.image : "" ;
        if (url != "" && url != null) {
            try {
                Picasso.with(this).load(url).placeholder(R.mipmap.user_placeholder).into(current_user_logo);
            } catch (Exception e) {
                current_user_logo.setImageResource(R.mipmap.user_placeholder);
            }
        }
    }

    protected void afterLogin()
    {
        setUserProfile();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        setUserProfile();
    }
}

