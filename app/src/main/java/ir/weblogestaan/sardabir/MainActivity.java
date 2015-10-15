package ir.weblogestaan.sardabir;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ir.weblogestaan.sardabir.Adapters.PostAdapter;
import ir.weblogestaan.sardabir.Classes.EndlessScrollListener;
import ir.weblogestaan.sardabir.Classes.Post;
import ir.weblogestaan.sardabir.Classes.PostParams;
import ir.weblogestaan.sardabir.Classes.Subject;
import ir.weblogestaan.sardabir.Classes.User;
import ir.weblogestaan.sardabir.Fragments.FeedFragment;
import ir.weblogestaan.sardabir.Network.HttpService;


public class MainActivity extends BaseActivity {

    public static String base_url = "http://weblogestaan.ir";
    public static Typeface typeFace;
    public ArrayList<Post> allPosts;
    public FeedFragment mMainPageFragment;
    public static ListView AllPostListView ;
    public static PostAdapter AllPostListViewArrayAdapter ;
    public static View ListFooter;
    private ProgressBar prgProgress;
    static int current_page;
    private boolean onLoading = false;

    public static boolean isLoaded, mustRefresh, prependData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prgProgress = (ProgressBar) findViewById(R.id.prgProgress);
        prgProgress.setVisibility(View.GONE);
        typeFace = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Yekan.ttf");
        isLoaded = false;
        onLoading = false;
        mustRefresh = false;

        TextView txtHeaderTitle = (TextView) findViewById(R.id.txtHeaderTitle);
        txtHeaderTitle.setText("آخرین مطالب");
        txtHeaderTitle.setTypeface(typeFace);
        initHeader(new HeaderInitializer() {
            @Override
            public void init() {
                AllPostListView.smoothScrollToPosition(0);
                headerImgBtnFirst.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_rotate));
                current_page = 0;
                mustRefresh = true;
                prependData = true;
                LoadPosts(0,true);
            }
        });
        setStatusbarColor();
        main_rule();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (mustRefresh) {
            if (AllPostListView != null && AllPostListView.getAdapter().getCount()>0)
                AllPostListView.smoothScrollToPosition(0);
            main_rule();
        }
    }

    public void main_rule(){
        try{
            if(!isNetworkConnected())
            {
                finish();
                MainActivity.this.startActivity(new Intent(this.getBaseContext(),NoNetActivity.class));
            }
            else
            {
                LoadPosts(0, false);
            }
        }
        catch(Exception cx)
        {
            finish();
        }
    }

    public void LoadPosts(int page,boolean add) {
        if (page == 0)
            PostParams.NODE_LAST_NID = "0";
        if (onLoading)
            return;
        String url = base_url+"/REST/get/feed?ok=BEZAR17&page="+page;
        HttpService p = new HttpService(getBaseContext(),url, add);
        p.ls = session.BasePostParams();
        new GetPostsTask().execute(p);
    }

    public class GetPostsTask extends AsyncTask<HttpService,Long,JSONObject>
    {
        boolean getToAdd = false;
        protected void onPreExecute() {
            onLoading = true;
            if (mustRefresh)
                toggleProgress(true);
        }

        @Override
        protected JSONObject doInBackground(HttpService... params) {
            getToAdd = params[0].toAdd;
            return params[0].postDataObject();
        }

        @Override
        protected void onPostExecute(JSONObject jsonResp)
        {
            onLoading = false;
            try {
                if(getToAdd == false)
                {
                    setPostListAdapter(jsonResp);
                }
                else {
                    if (prependData == false)
                        AddToList(jsonResp);
                    else {
                        headerImgBtnFirst.clearAnimation();
                        PrependToList(jsonResp);
                    }
                }
                if (mustRefresh) {
                    mustRefresh = false;
                    toggleProgress(mustRefresh);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void setPostListAdapter(JSONObject jsonArray) throws JSONException, IllegalAccessException {
        ArrayList<Post> u = ParsePosts(jsonArray);
        Log.e("U -Length", u.size()+"");
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

    private ArrayList<Post> ParsePosts(JSONObject jsonResp) throws JSONException, IllegalAccessException {
        if(jsonResp == null)
            return new ArrayList<Post>();
        ArrayList<Post> u = new ArrayList<Post>();
        JSONArray childs = jsonResp.getJSONArray("result");
        String total = jsonResp.getString("total_posts");
        if (jsonResp.has("notif_count")){
            Log.e("notif_co",jsonResp.getString("notif_count"));
            setNotifsCount(jsonResp.getString("notif_count"));
        }
        Log.e("C Length", childs.length()+"");
        if (jsonResp.has("user")) {
            try {
                User.Login(User.parseUser(jsonResp.getJSONObject("user")));
                afterLogin();
            }catch (Exception evb) {
                Log.e("LoginFailed",evb.getMessage());
            }
        }
        for(int i=0;i<childs.length();i++)
        {
            try{
                JSONObject maindata = childs.getJSONObject(i);

                Post p = Post.parsePost(maindata);
                u.add(p);
            }
            catch(Exception cx)
            {
                Log.e("POST PARSE ERROR", cx.getMessage());
            }
        }
        return u;
    }

    public void RemoveListFooter(){
        try
        {
            AllPostListView.removeFooterView(ListFooter);
        }
        catch(Exception ex) {
            return;
        }
    }

    private void AddToList(JSONObject jsonArray) {
        ArrayList<Post> p = null;
        try {
            p = ParsePosts(jsonArray);
        } catch (JSONException e) {
            p = new ArrayList<Post>();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if(p.isEmpty() == false)
        {
            boolean b = AllPostListViewArrayAdapter.setData(AllPostListView, p);
            if(b == false)
            {
                RemoveListFooter();
            }
            AllPostListViewArrayAdapter.notifyDataSetChanged();
        } else {
            RemoveListFooter();
        }
    }
    private void PrependToList(JSONObject jsonArray) {
        prependData  = false;
        ArrayList<Post> p = null;
        try {
            p = ParsePosts(jsonArray);
        } catch (JSONException e) {
            p = new ArrayList<>();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if(p.isEmpty() == false)
        {
            boolean b = AllPostListViewArrayAdapter.prependData(AllPostListView, p);
            if(b == false)
            {
                AllPostListView.removeFooterView(ListFooter);
            }
            AllPostListViewArrayAdapter.notifyDataSetChanged();
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
            AllPostListView.setOnScrollListener(new EndlessScrollListener("MainActivity",this, "", null,current_page, total));
        }
        catch (Exception e)
        {
            Log.e("a", e.getMessage());
        }
    }

    public void  SetMainPageFragment() {
        mMainPageFragment = new FeedFragment();
        Bundle args = new Bundle();
        args.putSerializable("posts", allPosts);

        mMainPageFragment.setArguments(args);

        // Insert the fragment by replacing any existing fragment
        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, mMainPageFragment)
                .commitAllowingStateLoss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PostParams.RENEWS_ACTIVITY_CODE && resultCode == PostParams.RENEWS_ACTIVITY_RESULT_SUCCESS_CODE) {
            Bundle bundle = data.getExtras();
            Post post = (Post)bundle.getSerializable("post");
            if (post != null && post.position != null) {
                post.reposted = "yes";
                AllPostListViewArrayAdapter.posts.set(Integer.parseInt(post.position), post);
                AllPostListViewArrayAdapter.notifyDataSetChanged();
            }
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

    private void toggleProgress(boolean show)
    {
        if (prgProgress == null)
            return;

        if (show)
            prgProgress.setVisibility(View.VISIBLE);
        else
            prgProgress.setVisibility(View.GONE);
    }

    /*boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "برای خروج یک بار دیگر بازگشت را لمس کنید", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }*/


}
