package ir.weblogestaan.sardabir;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import ir.weblogestaan.sardabir.Classes.Post;
import ir.weblogestaan.sardabir.Classes.PostParams;
import ir.weblogestaan.sardabir.Classes.Session;
import ir.weblogestaan.sardabir.Network.HttpService;

public class RessetpassActivity extends BaseActivity implements BaseActivity.LoginResult,Serializable {

    public transient Post post;
    EditText editTextAddress;
    Button btnSubmit;
    ProgressBar progressBar;
    TextView txtRules, txtError, txtLink, txtSiteAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ressetpass);
        Typeface typeFace = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Yekan.ttf");
        ImageButton btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_on_click));
                finish();
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.prgProgress);
        txtError = (TextView) findViewById(R.id.txtError);
        editTextAddress = (EditText) findViewById(R.id.editTextAddress);
        txtRules = (TextView) findViewById(R.id.txtRules);
        txtLink = (TextView) findViewById(R.id.txtLink);
        txtSiteAddress = (TextView) findViewById(R.id.txtSiteAddress);
        txtSiteAddress.setTypeface(typeFace);
        editTextAddress.setTypeface(typeFace);
        txtRules.setTypeface(typeFace);
        txtLink.setTypeface(typeFace);
        txtError.setTypeface(typeFace);
        txtLink.setText("بازیابی رمز عبور");
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setTypeface(typeFace);
        final RessetpassActivity that = this;
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_on_click));
                String siteAddress = editTextAddress.getText().toString();
                if (siteAddress.equals(""))
                {
                    showError("آدرس ایمیل را وارد کنید");
                    return;
                }
                Send(siteAddress, that);
            }
        });
        setStatusbarColor();
    }


    public void showError(String text)
    {
        if (txtError != null) {
            txtError.setText(text);
            txtError.setVisibility(View.VISIBLE);
        }
    }

    public boolean Send(String siteAddress, LoginResult registerResult)
    {
        String url = PostParams.getBaseUrl()+"/REST/action/resetpswd?ok=BEZAR17";
        HttpService p = new HttpService(getApplicationContext() ,url, false);
        ResetTask lt = new ResetTask();
        lt.loginResult = registerResult;
        Session session = new Session(getApplicationContext());
        try {
            p.ls = session.ResetPassParams(siteAddress);
        }
        catch (Exception e) {
            return false;
        }
        lt.execute(p);
        return true;
    }

    public class ResetTask extends AsyncTask<HttpService,Long,JSONObject> implements Serializable
    {
        boolean getToAdd = false;
        LoginResult loginResult = null;
        protected void onPreExecute() {
            btnSubmit.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(HttpService... params) {
            getToAdd = params[0].toAdd;
            return params[0].postDataObject();
        }

        @Override
        protected void onPostExecute(JSONObject jsonResp)
        {
            btnSubmit.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            try {
                ParseResult(jsonResp,loginResult);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    protected boolean ParseResult(JSONObject jsonResp, LoginResult loginResult) throws JSONException
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

    @Override
    public void onError() {
        Toast.makeText(this,"مشکلی در ارسال وجود دارد...",Toast.LENGTH_LONG).show();
        btnSubmit.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSuccess() {
        Toast t = Toast.makeText(this, "ایمیل ارسال شد. لطفا پوشه اسپم را هم چک کنید", Toast.LENGTH_LONG);
        t.setGravity(Gravity.CENTER, 0, 0);
        t.show();
        finish();
    }

}
