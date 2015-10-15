package ir.weblogestaan.sardabir;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends BaseActivity {

    private ImageView imgAppLogo;
    private TextView txtAppName, txtAppDesc,txtError, txtForgetPass;
    private static ProgressBar progressBar;
    private Button btnLogin, btnRegister, btnAnonymos;
    private EditText EditTxtUsername, EditTxtPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Intent i = getIntent();
        boolean isFromRegister = i.getBooleanExtra("fromRegister",false);
        Typeface typeFace = Typeface.createFromAsset(this.getAssets(), "fonts/Yekan.ttf");
        progressBar = (ProgressBar) findViewById(R.id.prgProgress);
        imgAppLogo = (ImageView) findViewById(R.id.imgAppLogo);
        txtAppName = (TextView) findViewById(R.id.txtAppName);
        txtAppDesc = (TextView) findViewById(R.id.txtAppDesc);
        txtForgetPass = (TextView) findViewById(R.id.txtForgetPass);
        txtError = (TextView) findViewById(R.id.txtError);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnAnonymos = (Button) findViewById(R.id.btnAnonymos);
        EditTxtUsername = (EditText) findViewById(R.id.EditTxtEmail);
        EditTxtPassword = (EditText) findViewById(R.id.editTxtPassword);

        if (isFromRegister) {
            txtError.setTextColor(Color.GREEN);
            txtError.setText("حساب کاربری شما با موفقیت ایجاد شد.");
            txtError.setVisibility(View.VISIBLE);
        } else {
            txtError.setVisibility(View.GONE);
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_on_click));
                if (!isNetworkConnected())
                {
                    Toast.makeText(getApplicationContext(),"عدم دسترسی به اینترنت",Toast.LENGTH_LONG).show();
                    return;
                }
                txtError.setVisibility(View.INVISIBLE);
                String username = EditTxtUsername.getText().toString();
                String password = EditTxtPassword.getText().toString();
                StoreUserPass(username, password);
                if (hasUserPass()) {
                    progressBar.setVisibility(View.VISIBLE);
                    CheckLogin(new LoginResult() {
                        @Override
                        public void onError() {
                            progressBar.setVisibility(View.INVISIBLE);
                            txtError.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onSuccess() {
                        }
                    });
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    txtError.setVisibility(View.VISIBLE);
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_on_click));
                goToRegisterPage(false);
            }
        });

        txtForgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_on_click));
                gotoResetPage(false);
            }
        });

        btnAnonymos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_on_click));
                gotoGuestPage(false);
            }
        });

        txtAppDesc.setTypeface(typeFace);
        txtAppName.setTypeface(typeFace);
        txtError.setTypeface(typeFace);
        btnLogin.setTypeface(typeFace);
        btnRegister.setTypeface(typeFace);
        btnAnonymos.setTypeface(typeFace);
        EditTxtPassword.setTypeface(typeFace);
        EditTxtUsername.setTypeface(typeFace);
        txtForgetPass.setTypeface(typeFace);
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
