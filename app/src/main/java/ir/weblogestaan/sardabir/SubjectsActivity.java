package ir.weblogestaan.sardabir;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ir.weblogestaan.sardabir.Classes.Cache;
import ir.weblogestaan.sardabir.Classes.PostParams;
import ir.weblogestaan.sardabir.Classes.Session;
import ir.weblogestaan.sardabir.Classes.Subject;
import ir.weblogestaan.sardabir.Classes.User;
import ir.weblogestaan.sardabir.Fragments.SubjectsFragment;
import ir.weblogestaan.sardabir.Network.HttpService;

public class SubjectsActivity extends BaseActivity {

    private ImageButton btnActivitySubjects, btnActivityFeed;
    public static boolean changed = false;
    public static int selectedCount = 0;
    boolean isFromLogin = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects);
        Intent i = getIntent();
        isFromLogin  = i.getBooleanExtra("isFromLogin", false);
        Typeface typeFace = Typeface.createFromAsset(this.getAssets(), "fonts/Yekan.ttf");
        btnActivitySubjects = (ImageButton) findViewById(R.id.btnImgSubjects);
        btnActivityFeed = (ImageButton) findViewById(R.id.btnImgFeed);
        btnActivitySubjects.setImageResource(R.mipmap.subjects_s);
        btnActivityFeed.setImageResource(R.mipmap.feed);
        btnActivityFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MainActivity.mustRefresh = SubjectsActivity.changed;
                } catch (Exception e) {
                    Log.e("e", e.getMessage());
                }
                finish();
            }
        });
        changed = false;

        TextView txtHeaderTitle = (TextView) findViewById(R.id.txtHeaderTitle);
        if (isFromLogin) {
            CloseDrawer();
            txtHeaderTitle.setText("موضوعات مورد علاقه را دنبال کنید");
            imgBtnDrawer.setVisibility(View.INVISIBLE);
            headerImgBtnFirst.setImageResource(R.mipmap.ic_done);
            headerImgBtnFirst.setVisibility(View.VISIBLE);
            headerImgBtnFirst.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_on_click));
                    if (selectedCount <= 0) {
                        Toast t = Toast.makeText(getApplicationContext(), "حداقل یک موضوع را انتخاب کنید", Toast.LENGTH_LONG);
                        t.setGravity(Gravity.CENTER,0,0);
                        t.show();
                        return;
                    }
                    gotoMainActivity(true);
                }
            });
        }
        else {
            txtHeaderTitle.setText("موضوعات");
        }
        txtHeaderTitle.setTypeface(typeFace);
        setStatusbarColor();
        afterLogin();
        main_rule();
    }

    public void main_rule(){
        try{
            if(!isNetworkConnected())
            {
                finish();
                SubjectsActivity.this.startActivity(new Intent(this.getBaseContext(),NoNetActivity.class));
            }
            else
            {
                LoadSubjects(0);
            }
        }
        catch(Exception cx)
        {
            finish();
        }
    }

    public void LoadSubjects(int page) {
        if (Cache.subjects != null)
            SetSubjectsFragment(Cache.subjects);
        String url = PostParams.getBaseUrl()+"/REST/get/subject_all?ok=BEZAR17&page="+page;
        HttpService p = new HttpService(getApplicationContext(),url, false);
        p.ls = session.BasePostParams();
        new GetSubjectsTask().execute(p);
    }

    public class GetSubjectsTask extends AsyncTask<HttpService,Long,JSONObject>
    {
        boolean getToAdd = false;
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
                if(getToAdd == false)
                {
                    setPostListAdapter(jsonResp);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void setPostListAdapter(JSONObject jsonArray) throws JSONException {
        ArrayList<Subject> u = ParseSubjects(jsonArray);
        if(u.size()>0)
        {
            Cache.subjects = u;
            SetSubjectsFragment(u);
        }
    }

    private ArrayList<Subject> ParseSubjects(JSONObject jsonResp) throws JSONException
    {
        if(jsonResp == null)
            return new ArrayList<>();
        ArrayList<Subject> u = new ArrayList<Subject>();
        JSONArray childs = jsonResp.getJSONArray("result");
        String total = jsonResp.getString("total_posts");
        if (jsonResp.has("notif_count")){
            setNotifsCount(jsonResp.getString("notif_count"));
        }
        for(int i=0;i<childs.length();i++)
        {
            try{
                JSONObject maindata = childs.getJSONObject(i);
                Subject p = Subject.parseSubject(maindata);
                u.add(p);
            }
            catch(Exception cx)
            {
                u.add(new Subject());
                Log.e("Subject PARSE ERROR", cx.getMessage());
            }
        }
        return u;
    }

    public void  SetSubjectsFragment(ArrayList<Subject> all) {
        SubjectsFragment mSubjectsFragment = new SubjectsFragment();
        Bundle args = new Bundle();
        args.putSerializable("posts", all);

        mSubjectsFragment.setArguments(args);

        // Insert the fragment by replacing any existing fragment
        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, mSubjectsFragment)
                .commitAllowingStateLoss();
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
