package ir.weblogestaan.sardabir;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import ir.weblogestaan.sardabir.Adapters.PostAdapter;
import ir.weblogestaan.sardabir.Classes.Post;
import ir.weblogestaan.sardabir.Classes.PostParams;
import ir.weblogestaan.sardabir.Classes.Renews;
import ir.weblogestaan.sardabir.Classes.Session;
import ir.weblogestaan.sardabir.Classes.User;
import ir.weblogestaan.sardabir.Network.HttpService;

public class SuggestActivity extends BaseActivity implements BaseActivity.LoginResult,Serializable {

    public transient Post post;
    EditText editTextAddress, editTextDesc;
    Button btnSubmit;
    ProgressBar progressBar;
    TextView txtRules, txtError, txtLink, txtSiteAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest);
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
        editTextDesc = (EditText) findViewById(R.id.editTextDesc);
        txtRules = (TextView) findViewById(R.id.txtRules);
        txtLink = (TextView) findViewById(R.id.txtLink);
        txtSiteAddress = (TextView) findViewById(R.id.txtSiteAddress);
        txtSiteAddress.setTypeface(typeFace);
        editTextDesc.setTypeface(typeFace);
        editTextAddress.setTypeface(typeFace);
        txtRules.setTypeface(typeFace);
        txtLink.setTypeface(typeFace);
        txtError.setTypeface(typeFace);
        txtLink.setText("پیشنهاد سایت");
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setTypeface(typeFace);
        final SuggestActivity that = this;
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_on_click));
                String siteAddress = editTextAddress.getText().toString();
                String desc = editTextDesc.getText().toString();
                if (siteAddress.equals(""))
                {
                    showError("آدرس سایت را وارد کنید");
                    return;
                }
                Send(siteAddress, desc, that);
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

    public boolean Send(String siteAddress, String desc, BaseActivity.LoginResult registerResult)
    {
        String url = PostParams.getBaseUrl()+"/REST/set/suggest?ok=BEZAR17";
        HttpService p = new HttpService(getApplicationContext() ,url, false);
        SuggestTask lt = new SuggestTask();
        lt.loginResult = registerResult;
        Session session = new Session(getApplicationContext());
        try {
            p.ls = session.SuggestParams(siteAddress, desc);
        }
        catch (Exception e) {
            return false;
        }
        lt.execute(p);
        return true;
    }

    public class SuggestTask extends AsyncTask<HttpService,Long,JSONObject> implements Serializable
    {
        boolean getToAdd = false;
        BaseActivity.LoginResult loginResult = null;
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

    @Override
    public void onError() {
        Toast.makeText(this,"مشکلی در ارسال وجود دارد...",Toast.LENGTH_LONG).show();
        btnSubmit.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSuccess() {
        Toast t = Toast.makeText(this, "ممنون", Toast.LENGTH_LONG);
        t.setGravity(Gravity.CENTER, 0, 0);
        t.show();
        finish();
    }

}
