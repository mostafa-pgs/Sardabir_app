package ir.weblogestaan.sardabir;

import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import ir.weblogestaan.sardabir.Classes.PostParams;
import ir.weblogestaan.sardabir.Classes.Session;
import ir.weblogestaan.sardabir.Network.HttpService;

public class RegisterActivity extends BaseActivity {

    private ImageView imgAppLogo;
    private TextView txtAppName, txtAppDesc,txtError, txtForgetPass;
    private static ProgressBar progressBar;
    private Button btnLogin, btnRegister, btnAnonymos;
    private EditText EditTxtEmail, EditTxtName, EditTxtPassword, editTextRepeatPswd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Typeface typeFace = Typeface.createFromAsset(this.getAssets(), "fonts/Yekan.ttf");
        progressBar = (ProgressBar) findViewById(R.id.prgProgress);
        imgAppLogo = (ImageView) findViewById(R.id.imgAppLogo);
        txtAppName = (TextView) findViewById(R.id.txtAppName);
        txtAppDesc = (TextView) findViewById(R.id.txtAppDesc);
        txtError = (TextView) findViewById(R.id.txtError);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnAnonymos = (Button) findViewById(R.id.btnAnonymos);
        EditTxtEmail = (EditText) findViewById(R.id.EditTxtEmail);
        EditTxtName = (EditText) findViewById(R.id.editTextName);
        EditTxtPassword = (EditText) findViewById(R.id.editTxtPassword);
        editTextRepeatPswd = (EditText) findViewById(R.id.editTextRepeatPswd);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_on_click));
                if (!isNetworkConnected()) {
                    showError("عدم دسترسی به اینترنت");
                    return;
                }
                txtError.setVisibility(View.INVISIBLE);
                String email = EditTxtEmail.getText().toString();
                String name = EditTxtName.getText().toString();
                String password = EditTxtPassword.getText().toString();
                String repeat = editTextRepeatPswd.getText().toString();

                if (name.equals("")) {
                    showError("نام کاربری را وارد کنید");
                    return;
                }

                if (!isEmailValid(email)) {
                    showError("آدرس ایمیل معتبر نیست");
                    return;
                }

                if (password.length() < 6) {
                    showError("کلمه عبور باید حداقل ۶ حرف باشد");
                    return;
                }

                if (!password.equals(repeat)) {
                    showError("کلمه عبور با تکرار آن مطابقت ندارد");
                    return;
                }

                RegisterUser(getApplicationContext(), name, email, password, repeat, new LoginResult() {
                    @Override
                    public void onError() {
                        txtError.setText("مشکلی در ثبت نام وجود دارد");
                    }

                    @Override
                    public void onSuccess() {
                        goToLoginPage(true, true);
                    }
                });
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_on_click));
                goToLoginPage(true, false);
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
        EditTxtName.setTypeface(typeFace);
        editTextRepeatPswd.setTypeface(typeFace);
        EditTxtEmail.setTypeface(typeFace);
    }

    public void showError(String text)
    {
        if (txtError != null) {
            txtError.setText(text);
            txtError.setVisibility(View.VISIBLE);
        }
    }

    public boolean RegisterUser(Context c,String name, String email, String pass, String repeat, BaseActivity.LoginResult registerResult)
    {
        String url = PostParams.getBaseUrl()+"/REST/set/user?ok=BEZAR17";
        HttpService p = new HttpService(c ,url, false);
        RegisterTask lt = new RegisterTask();
        lt.loginResult = registerResult;
        Session session = new Session(c);
        try {
            p.ls = session.RegisterParams(name, email, pass, repeat);
        }
        catch (Exception e) {
            return false;
        }
        lt.execute(p);
        return true;
    }

    public class RegisterTask extends AsyncTask<HttpService,Long,JSONObject> implements Serializable
    {
        boolean getToAdd = false;
        BaseActivity.LoginResult loginResult = null;
        protected void onPreExecute() {

        }

        @Override
        protected JSONObject doInBackground(HttpService... params) {
            getToAdd = params[0].toAdd;
            return params[0].postDataObject();
        }

        @Override
        protected void onPostExecute(JSONObject jsonResp)
        {
            try {
                ParseResult(jsonResp,loginResult);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    protected boolean ParseResult(JSONObject jsonResp, BaseActivity.LoginResult loginResult) throws JSONException
    {
        if(jsonResp == null) {
            if (loginResult != null)
                loginResult.onError();
            return false;
        }

        String resultType = jsonResp.getString("result_type");
        String result = jsonResp.getString("result");
        if (resultType.equals("boolean")) {
            if (result.equals("true") && Boolean.parseBoolean(result) == true) {
                if (loginResult != null)
                    loginResult.onSuccess();
            }
            return false;
        } else if (resultType.equals("string")) {
            showError(result);
        }
        return false;
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


    /**
     * method is used for checking valid email id format.
     *
     * @param email
     * @return boolean true for valid false for invalid
     */
    public static boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
