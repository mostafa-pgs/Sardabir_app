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
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ir.weblogestaan.sardabir.Classes.Cache;
import ir.weblogestaan.sardabir.Classes.Notif;
import ir.weblogestaan.sardabir.Classes.PostParams;
import ir.weblogestaan.sardabir.Classes.Subject;
import ir.weblogestaan.sardabir.Fragments.NotificationsFragment;
import ir.weblogestaan.sardabir.Fragments.SubjectsFragment;
import ir.weblogestaan.sardabir.Network.HttpService;

public class NotificationsActivity extends BaseActivity {

    private ImageButton btnActivitySubjects, btnActivityFeed;
    private FrameLayout content_frame;
    public static boolean changed = false;
    Typeface typeFace;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects);
        typeFace = Typeface.createFromAsset(this.getAssets(), "fonts/Yekan.ttf");
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
        btnActivitySubjects = (ImageButton) findViewById(R.id.btnImgSubjects);
        btnActivityFeed = (ImageButton) findViewById(R.id.btnImgFeed);
//        btnActivitySubjects.setImageResource(R.mipmap.subjects_s);
        btnActivityFeed.setImageResource(R.mipmap.feed);
        btnActivityFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MainActivity.mustRefresh = NotificationsActivity.changed;
                } catch (Exception e) {
                    Log.e("e", e.getMessage());
                }
                finish();
            }
        });
        content_frame = (FrameLayout) findViewById(R.id.content_frame);
        changed = false;

        TextView txtHeaderTitle = (TextView) findViewById(R.id.txtHeaderTitle);
        txtHeaderTitle.setText("اعلان ها");
        txtHeaderTitle.setTypeface(typeFace);
        afterLogin();
        setStatusbarColor();
        main_rule();
    }

    public void main_rule(){
        try{
            if(!isNetworkConnected())
            {
                finish();
                NotificationsActivity.this.startActivity(new Intent(this.getBaseContext(),NoNetActivity.class));
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
            SetNotificationsFragment(Cache.notifs);
        String url = PostParams.getBaseUrl()+"/REST/get/notifs?ok=BEZAR17&page="+page;
        HttpService p = new HttpService(getApplicationContext(),url, false);
        p.ls = session.BasePostParams();
        p.ls.add(new BasicNameValuePair("seen","2"));
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
        ArrayList<Notif.NotifBase> u = ParseNotifications(jsonArray);
        if(u.size()>0)
        {
            Cache.notifs = u;
            SetNotificationsFragment(u);
        } else {
            removeLoading();
        }
    }

    private void removeLoading()
    {
        if (content_frame != null) {
            ProgressBar progressBar = (ProgressBar)content_frame.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.GONE);
            Toast toast = Toast.makeText(getApplicationContext(), "موردی یافت نشد", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    private ArrayList<Notif.NotifBase> ParseNotifications(JSONObject jsonResp) throws JSONException
    {
        if(jsonResp == null)
            return new ArrayList<>();
        ArrayList<Notif.NotifBase> u = new ArrayList<>();
        JSONArray childs = jsonResp.getJSONArray("result");
        String total = jsonResp.getString("total_posts");
        for(int i=0;i<childs.length();i++) {
            Notif.NotifBase notif = new Notif().new NotifBase();
            try {
                JSONObject maindata = childs.getJSONObject(i);
                String type = maindata.getString("subject_name");
                if (type.equals("node")) {
                    notif = new Notif().new PostNotif();
                    notif = notif.parseNotif(maindata);
                    u.add(notif);
                } else if (type.equals("user")) {
                    notif = new Notif().new UserNotif();
                    notif = notif.parseNotif(maindata);
                    u.add(notif);
                }
            } catch (Exception cx) {
                u.add(notif);
                Log.e("Notif PARSE ERROR", cx.getMessage());
            }
        }
        return u;
    }

    public void  SetNotificationsFragment(ArrayList<Notif.NotifBase> all) {
        NotificationsFragment mSubjectsFragment = new NotificationsFragment();
        Bundle args = new Bundle();
        try {
            args.putSerializable("posts", all);
        }catch (Exception e) {
            e.printStackTrace();
        }

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
