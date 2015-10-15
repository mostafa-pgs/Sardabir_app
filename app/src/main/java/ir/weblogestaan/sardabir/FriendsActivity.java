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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ir.weblogestaan.sardabir.Adapters.RenewsAdapter;
import ir.weblogestaan.sardabir.Classes.EndlessScrollListener;
import ir.weblogestaan.sardabir.Classes.Flag;
import ir.weblogestaan.sardabir.Classes.PostParams;
import ir.weblogestaan.sardabir.Classes.Renews;
import ir.weblogestaan.sardabir.Classes.User;
import ir.weblogestaan.sardabir.Fragments.FriendsFragment;
import ir.weblogestaan.sardabir.Network.HttpService;


public class FriendsActivity extends BaseActivity {

    public static Typeface typeFace;
    public ArrayList<Renews> allPosts;
    public static ListView AllPostListView ;
    public static RenewsAdapter AllPostListViewArrayAdapter ;
    public static View ListFooter;
    static int current_page;
    public static boolean isLoaded, mustRefresh , canLoad;
    public User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        isLoaded = false;
        canLoad = true;
        typeFace = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Yekan.ttf");
        TextView txtHeaderTitle = (TextView) findViewById(R.id.txtHeaderTitle);
        txtHeaderTitle.setText("از دوستان...");
        txtHeaderTitle.setTypeface(typeFace);
        initHeader(new HeaderInitializer() {
            @Override
            public void init() {
                AllPostListView.smoothScrollToPosition(0);
                headerImgBtnFirst.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_rotate));
                current_page = 0;
                mustRefresh = true;
                prependData = true;
                LoadProfile(0, true);
            }
        });
        setStatusbarColor();
        main_rule();
    }

    public View.OnClickListener onUnfollow = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Flag f = new Flag(getApplicationContext(),"friend","friend","unflag",user.getID());
            f.send();
            v.setOnClickListener(onFollow);
        }
    };

    public View.OnClickListener onFollow = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Flag f = new Flag(getApplicationContext(),"friend","friend","unflag",user.getID());
            f.send();
            v.setOnClickListener(onUnfollow);
        }
    };

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    public void main_rule(){
        try{
            if(!isNetworkConnected())
            {
                finish();
                FriendsActivity.this.startActivity(new Intent(this.getBaseContext(),NoNetActivity.class));
            }
            else
            {
                LoadProfile(0, false);
            }
        }
        catch(Exception cx)
        {
            finish();
        }
    }


    public void LoadProfile(int page,boolean add) {
        if (canLoad == false)
            return;
        String url = PostParams.getBaseUrl() +"/REST/get/timeline?ok=BEZAR17&page="+page;
        HttpService p = new HttpService(getBaseContext(),url, add);
        p.ls = session.BasePostParams();
        new GetTimelineTask().execute(p);
    }

    public class GetTimelineTask extends AsyncTask<HttpService,Long,JSONObject>
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
                else
                    AddToList(jsonResp);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public void  SetMainPageFragment() {
        FriendsFragment mMainPageFragment = new FriendsFragment();
        Bundle args = new Bundle();
        args.putSerializable("posts", allPosts);

        mMainPageFragment.setArguments(args);

        // Insert the fragment by replacing any existing fragment
        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, mMainPageFragment)
                .commitAllowingStateLoss();
    }

    private void setPostListAdapter(JSONObject jsonArray) throws JSONException, IllegalAccessException {
        ArrayList<Renews> u = ParsePosts(jsonArray);
        if(u.size()>0)
        {
            allPosts = u;
            if(isLoaded == false)
            {
                SetMainPageFragment();
                isLoaded = true;
            }
            else
                RenewMainPageFragment();
        } else {
            RemoveListFooter();
        }
    }

    public void RenewMainPageFragment()
    {
        try {
            int total = 100;
            boolean b = AllPostListViewArrayAdapter.setData(AllPostListView, allPosts);
            if (b == false) {
                RemoveListFooter();
            }
            AllPostListViewArrayAdapter.notifyDataSetChanged();
            View footer = ListFooter;
            AllPostListView.addFooterView(footer);
            AllPostListView.setOnScrollListener(new EndlessScrollListener("FriendsActivity",this, "", null,current_page, total));
        }
        catch (Exception e)
        {
            Log.e("a", e.getMessage());
        }
    }

    private void AddToList(JSONObject jsonArray) {
        ArrayList<Renews> p = null;
        try {
            p = ParsePosts(jsonArray);
        } catch (JSONException e) {
            p = new ArrayList<Renews>();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if(p.isEmpty() == false)
        {
            boolean b = AllPostListViewArrayAdapter.setData(AllPostListView,p);
            if(b == false)
            {
                RemoveListFooter();
            }
            AllPostListViewArrayAdapter.notifyDataSetChanged();
        }
    }

    private ArrayList<Renews> ParsePosts(JSONObject jsonResp) throws JSONException, IllegalAccessException {
        if(jsonResp == null)
            return new ArrayList<>();
        ArrayList<Renews> u = new ArrayList<>();
        String result_type = jsonResp.getString("result_type");
        if (result_type.equals("boolean"))
            return u;
        JSONArray childs = jsonResp.getJSONArray("result");
        String total = jsonResp.getString("total_posts");
        if (jsonResp.has("notif_count")){
            setNotifsCount(jsonResp.getString("notif_count"));
        }
        for(int i=0;i<childs.length();i++)
        {
            try{
                JSONObject maindata = childs.getJSONObject(i);

                Renews p = Renews.parseRenews(maindata);
                u.add(p);
            }
            catch(Exception cx)
            {
                u.add(new Renews());
                Log.e("Renews PARSE ERROR", cx.getMessage());
            }
        }
        return u;
    }
    public void RemoveListFooter(){
        canLoad = false;
        try
        {
            AllPostListView.removeFooterView(ListFooter);
            Toast toast = Toast.makeText(getApplicationContext(), "مطلب بیشتری یافت نشد", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        catch(Exception ex) {
            return;
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
