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
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ir.weblogestaan.sardabir.Adapters.PostAdapter;
import ir.weblogestaan.sardabir.Classes.Post;
import ir.weblogestaan.sardabir.Classes.PostParams;
import ir.weblogestaan.sardabir.Classes.Subject;
import ir.weblogestaan.sardabir.Fragments.TermFragment;
import ir.weblogestaan.sardabir.Network.HttpService;

public class TermActivity extends BaseActivity {

    private Subject subject;
    private ImageButton btnActivitySubjects, btnActivityFeed;
    public ArrayList<Post> allPosts;
    public static boolean changed = false;
    public static ListView AllPostListView ;
    public static PostAdapter AllPostListViewArrayAdapter ;
    public static View ListFooter;
    public static boolean isLoaded, mustRefresh;
    static int current_page;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term);
        subject = (Subject)getIntent().getSerializableExtra("subject");
        Typeface typeFace = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Yekan.ttf");
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
        changed = false;
        afterLogin();
        main_rule();
    }

    public void main_rule(){
        try{
            if(!isNetworkConnected())
            {
                finish();
                TermActivity.this.startActivity(new Intent(this.getBaseContext(),NoNetActivity.class));
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
        String url = PostParams.getBaseUrl()+"/REST/get/feed?ok=BEZAR17&page="+page;
        HttpService p = new HttpService(getApplicationContext(),url, add);
        p.ls = PostParams.BasePostParams();
        p.ls.add(new BasicNameValuePair("tid",subject.tid));
        new GetPostsTask().execute(p);
    }

    public class GetPostsTask extends AsyncTask<HttpService,Long,JSONObject>
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
            }
        }
    }

    private void setPostListAdapter(JSONObject jsonArray) throws JSONException {
        ArrayList<Post> u = ParsePosts(jsonArray);
        Log.e("U -Length", u.size()+"");
        if(u.size()>0)
        {
            allPosts = u;
            SetMainPageFragment();
        }
    }

    private ArrayList<Post> ParsePosts(JSONObject jsonResp) throws JSONException
    {
        if(jsonResp == null)
            return new ArrayList<Post>();
        ArrayList<Post> u = new ArrayList<Post>();
        JSONArray childs = jsonResp.getJSONArray("result");
        String total = jsonResp.getString("total_posts");
        Log.e("C Length", childs.length()+"");
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
        ArrayList<Post> p;
        try {
            p = ParsePosts(jsonArray);
        } catch (JSONException e) {
            p = new ArrayList<Post>();
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

    public void  SetMainPageFragment() {
        TermFragment mMainPageFragment = new TermFragment();
        Bundle args = new Bundle();
        args.putSerializable("posts", allPosts);
        args.putSerializable("subject", subject);

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
}
