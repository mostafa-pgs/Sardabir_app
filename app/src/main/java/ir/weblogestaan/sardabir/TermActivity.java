package ir.weblogestaan.sardabir;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ListView;
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

import ir.weblogestaan.sardabir.Adapters.PostAdapter;
import ir.weblogestaan.sardabir.Classes.Post;
import ir.weblogestaan.sardabir.Classes.PostParams;
import ir.weblogestaan.sardabir.Classes.Subject;
import ir.weblogestaan.sardabir.Fragments.TermFragment;
import ir.weblogestaan.sardabir.Network.HttpService;

public class TermActivity extends BaseActivity {

    private Subject subject;
    private ImageButton btnActivitySubjects;
    public ArrayList<Post> allPosts;
    public static boolean changed = false;
    public static ListView AllPostListView ;
    public static PostAdapter AllPostListViewArrayAdapter ;
    public static boolean canLoad;
    public static View ListFooter;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term);
        subject = (Subject)getIntent().getSerializableExtra("subject");
        canLoad = true;
        Typeface typeFace = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Yekan.ttf");
        TextView txtHeaderTitle = (TextView) findViewById(R.id.txtHeaderTitle);
        txtHeaderTitle.setTypeface(typeFace);
        txtHeaderTitle.setText(Html.fromHtml("آخرین مطالب" + " " + "<span style=\"color:#0694ba\">" + subject.name + "</span>"));
        btnActivitySubjects = (ImageButton) findViewById(R.id.btnImgSubjects);
        btnActivitySubjects.setImageResource(R.mipmap.subjects_one);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
        changed = false;
        afterLogin();
        initHeader(new HeaderInitializer() {
            @Override
            public void init() {
                AllPostListView.smoothScrollToPosition(0);
                headerImgBtnFirst.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_rotate));
                prependData = true;
                LoadPosts(0, true);
            }
        });
        main_rule();
        setStatusbarColor();
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
        if (canLoad == false)
            return;
        String url = PostParams.getBaseUrl()+"/REST/get/feed?ok=BEZAR17&page="+page;
        HttpService p = new HttpService(getApplicationContext(),url, add);
        p.ls = session.BasePostParams();
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
                else {
                    if (prependData == false)
                        AddToList(jsonResp);
                    else {
                        headerImgBtnFirst.clearAnimation();
                        PrependToList(jsonResp);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setPostListAdapter(JSONObject jsonArray) throws JSONException {
        ArrayList<Post> u = ParsePosts(jsonArray);
        if(u.size()>0)
        {
            allPosts = u;
            SetMainPageFragment();
        } else {
            RemoveListFooter();
        }
    }

    private ArrayList<Post> ParsePosts(JSONObject jsonResp) throws JSONException
    {
        if(jsonResp == null)
            return new ArrayList<>();
        ArrayList<Post> u = new ArrayList<Post>();
        String result_type = jsonResp.getString("result_type");
        if (result_type.equals("boolean"))
            return u;
        JSONArray childs = jsonResp.getJSONArray("result");
        if (jsonResp.has("notif_count")){
            setNotifsCount(jsonResp.getString("notif_count"));
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
        canLoad = false;
        try
        {
            progressBar.setVisibility(View.GONE);
            AllPostListView.removeFooterView(ListFooter);
            Toast toast = Toast.makeText(getApplicationContext(), "مطلب  یافت نشد", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
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
}
