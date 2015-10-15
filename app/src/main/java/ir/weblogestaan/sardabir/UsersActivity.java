package ir.weblogestaan.sardabir;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
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
import ir.weblogestaan.sardabir.Classes.Subject;
import ir.weblogestaan.sardabir.Classes.User;
import ir.weblogestaan.sardabir.Fragments.SubjectsFragment;
import ir.weblogestaan.sardabir.Fragments.UsersFragment;
import ir.weblogestaan.sardabir.Network.HttpService;

public class UsersActivity extends BaseActivity {

    public static boolean changed = false;
    String entity_type = "";
    String entity_id = "";
    Integer subject = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        Intent i = getIntent();
        //isFromLogin  = i.getBooleanExtra("isFromLogin", false);
        entity_type = i.getStringExtra("entity_type");
        entity_id = i.getStringExtra("entity_id");
        subject = i.getIntExtra("subject", PostParams.USER_TYPE_LIKERS);
        Typeface typeFace = Typeface.createFromAsset(this.getAssets(), "fonts/Yekan.ttf");
        changed = false;

        TextView txtHeaderTitle = (TextView) findViewById(R.id.txtHeaderTitle);
        txtHeaderTitle.setText("");
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
                UsersActivity.this.startActivity(new Intent(this.getBaseContext(),NoNetActivity.class));
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
        String url = PostParams.getBaseUrl()+"/REST/get/ausers?ok=BEZAR17&page="+page;
        HttpService p = new HttpService(getApplicationContext(),url, false);
        p.ls = session.BasePostParams();
        p.ls.add(new BasicNameValuePair("entity_type",entity_type));
        p.ls.add(new BasicNameValuePair("entity_id",entity_id));
        p.ls.add(new BasicNameValuePair("subject",subject.toString()));
        new GetUsersTask().execute(p);
    }

    public class GetUsersTask extends AsyncTask<HttpService,Long,JSONObject>
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
        ArrayList<User> u = ParseSubjects(jsonArray);
        if(u.size()>0)
        {
            SetUsersFragment(u);
        }
    }

    private ArrayList<User> ParseSubjects(JSONObject jsonResp) throws JSONException
    {
        if(jsonResp == null)
            return new ArrayList<>();
        ArrayList<User> u = new ArrayList<>();
        JSONArray childs = jsonResp.getJSONArray("result");
        String total = jsonResp.getString("total_posts");
        if (jsonResp.has("notif_count")){
            setNotifsCount(jsonResp.getString("notif_count"));
        }
        for(int i=0;i<childs.length();i++)
        {
            try{
                JSONObject maindata = childs.getJSONObject(i);
                User p = User.parseUser(maindata);
                u.add(p);
            }
            catch(Exception cx)
            {
                Log.e("Subject PARSE ERROR", cx.getMessage());
            }
        }
        return u;
    }

    public void  SetUsersFragment(ArrayList<User> all) {
        UsersFragment mSubjectsFragment = new UsersFragment();
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
