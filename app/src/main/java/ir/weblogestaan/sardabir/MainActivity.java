package ir.weblogestaan.sardabir;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

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
    public ArrayList<Subject> allCategories;
    public static ListView AllPostListView ;
    public static PostAdapter AllPostListViewArrayAdapter ;
    public static View ListFooter;
    private ProgressDialog progress;
    private ProgressBar prgProgress;
    static String searched_subject = "";
    static int current_page;
    private ImageButton btnActivitySubjects, btnActivityFeed;

    public static boolean isLoaded, mustRefresh;

    public void initNavigation()
    {
        btnActivitySubjects = (ImageButton) findViewById(R.id.btnImgSubjects);
        btnActivitySubjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.startActivity(new Intent(v.getContext(),SubjectsActivity.class));
                MainActivity.this.overridePendingTransition(0, 0);
            }
        });
        btnActivityFeed = (ImageButton) findViewById(R.id.btnImgFeed);
        btnActivityFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllPostListView.smoothScrollToPosition(0);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prgProgress = (ProgressBar) findViewById(R.id.prgProgress);
        prgProgress.setVisibility(View.GONE);
        typeFace = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Yekan.ttf");
        isLoaded = false;
        mustRefresh = false;
        initNavigation();
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
        String url = base_url+"/REST/get/feed?ok=BEZAR17&page="+page;
        HttpService p = new HttpService(getBaseContext(),url, add);
        p.ls = PostParams.BasePostParams();
        new GetPostsTask().execute(p);
    }

    public class GetPostsTask extends AsyncTask<HttpService,Long,JSONObject>
    {
        boolean getToAdd = false;
        protected void onPreExecute() {
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
            try {
                if(getToAdd == false)
                {
                    setPostListAdapter(jsonResp);
                }
                else
                    AddToList(jsonResp);
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
        }
    }

    private ArrayList<Post> ParsePosts(JSONObject jsonResp) throws JSONException, IllegalAccessException {
        if(jsonResp == null)
            return new ArrayList<Post>();
        ArrayList<Post> u = new ArrayList<Post>();
        JSONArray childs = jsonResp.getJSONArray("result");
        String total = jsonResp.getString("total_posts");
        Log.e("C Length", childs.length()+"");
        if (jsonResp.has("user") == true) {
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
                u.add(new Post());
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
            boolean b = AllPostListViewArrayAdapter.setData(AllPostListView,p);
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
                AllPostListView.removeFooterView(ListFooter);
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
        FeedFragment mMainPageFragment = new FeedFragment();
        Bundle args = new Bundle();
        args.putSerializable("posts", allPosts);

        mMainPageFragment.setArguments(args);

        // Insert the fragment by replacing any existing fragment
        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, mMainPageFragment)
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

    private void toggleProgress(boolean show)
    {
        if (prgProgress == null)
            return;

        if (show)
            prgProgress.setVisibility(View.VISIBLE);
        else
            prgProgress.setVisibility(View.GONE);
    }


}
