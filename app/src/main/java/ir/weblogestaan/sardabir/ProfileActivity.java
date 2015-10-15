package ir.weblogestaan.sardabir;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import ir.weblogestaan.sardabir.Adapters.PostAdapter;
import ir.weblogestaan.sardabir.Adapters.RenewsAdapter;
import ir.weblogestaan.sardabir.Classes.CircleImageView;
import ir.weblogestaan.sardabir.Classes.EndlessScrollListener;
import ir.weblogestaan.sardabir.Classes.Flag;
import ir.weblogestaan.sardabir.Classes.Pager;
import ir.weblogestaan.sardabir.Classes.Post;
import ir.weblogestaan.sardabir.Classes.PostParams;
import ir.weblogestaan.sardabir.Classes.Renews;
import ir.weblogestaan.sardabir.Classes.Subject;
import ir.weblogestaan.sardabir.Classes.User;
import ir.weblogestaan.sardabir.Fragments.FeedFragment;
import ir.weblogestaan.sardabir.Fragments.ProfileFragment;
import ir.weblogestaan.sardabir.Network.HttpService;


public class ProfileActivity extends BaseActivity {

    public static Typeface typeFace;
    public ArrayList<Renews> allPosts;
    public static ListView AllPostListView ;
    public static RenewsAdapter AllPostListViewArrayAdapter ;
    public static View ListFooter;
    static int current_page;
    public static boolean isLoaded, canLoad;
    public User user;
    private ProgressBar progressBar;
    private FrameLayout content_frame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        isLoaded = false;
        canLoad = true;
        typeFace = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Yekan.ttf");
        Intent i = getIntent();
        user = (User) i.getExtras().getSerializable("user");
        CircleImageView imgLogo = (CircleImageView) findViewById(R.id.imgUserLogo);
        TextView txtFollowersCount = (TextView) findViewById(R.id.txtFollowerCount);
        TextView txtUsername = (TextView) findViewById(R.id.txtUsername);
        TextView txtBio = (TextView) findViewById(R.id.txtBio);
        TextView txtFollowingCount = (TextView) findViewById(R.id.txtFollowingCount);
        TextView txtFollowerText = (TextView) findViewById(R.id.txtFollowerText);
        TextView txtFollowingText = (TextView) findViewById(R.id.txtFollowingText);
        Button btnFollowEdit = (Button) findViewById(R.id.btnFollowOrEdit);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        content_frame = (FrameLayout) findViewById(R.id.content_frame);
        txtFollowersCount.setTypeface(typeFace);
        txtFollowingCount.setTypeface(typeFace);
        txtUsername.setTypeface(typeFace);
        txtBio.setTypeface(typeFace);
        txtFollowerText.setTypeface(typeFace);
        txtFollowingText.setTypeface(typeFace);
        if (user != null)
        {
            txtBio.setText(Html.fromHtml(user.bio));
            txtUsername.setText(user.getName());
            txtFollowersCount.setText(user.follower_count);
            txtFollowingCount.setText(user.following_count);
            String url =  user.image;
            if (url != "" && url != null) {
                try {
                    Picasso.with(this).load(url).placeholder(R.mipmap.user_placeholder).into(imgLogo);
                } catch (Exception e) {
                    imgLogo.setImageResource(R.mipmap.user_placeholder);
                }
            }
            btnFollowEdit.setTypeface(typeFace);
            if (user.uid.equals(PostParams.current_user.uid)) {
                //btnFollowEdit.setBackgroundColor(Color.LTGRAY);
                btnFollowEdit.setText("ویرایش");
                btnFollowEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_on_click));
                        /*Intent intent = new Intent(v.getContext(), EditprofileActivity.class);
                        intent.putExtra("user", user);
                        v.getContext().startActivity(intent);*/
                        String url = PostParams.getBaseUrl() + "/user/" + user.uid + "/edit?vstyle";
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(browserIntent);
                    }
                });
            } else {
                if (user.is_followed.equals("yes")) {
                    btnFollowEdit.setText("دنبال نمیکنم");
                    btnFollowEdit.setOnClickListener(onUnfollow);
                } else {
                    btnFollowEdit.setText("دنبال کنید");
                    btnFollowEdit.setOnClickListener(onFollow);
                }
            }
            main_rule();
        }
        setStatusbarColor();
    }

    public View.OnClickListener onUnfollow = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Flag f = new Flag(getApplicationContext(),"friend","friend","unflag",user.getID());
            f.send();
            ((ToggleButton)v).setText("دنبال نمیکنم");
            v.setOnClickListener(onFollow);
        }
    };

    public View.OnClickListener onFollow = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Flag f = new Flag(getApplicationContext(),"friend","friend","unflag",user.getID());
            f.send();
            ((ToggleButton)v).setText("دنبال کنید");
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
                ProfileActivity.this.startActivity(new Intent(this.getBaseContext(),NoNetActivity.class));
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
        String url = PostParams.getBaseUrl() +"/REST/get/user_posts?ok=BEZAR17&page="+page;
        HttpService p = new HttpService(getBaseContext(),url, add);
        List<NameValuePair> ls = new ArrayList<>();
        ls.add(new BasicNameValuePair("puid",user.getID()));
        ls.add(new BasicNameValuePair("cuid", PostParams.current_user.getID()));
        p.ls = ls;
        new GetProfileTask().execute(p);
    }

    public class GetProfileTask extends AsyncTask<HttpService,Long,JSONObject>
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
        ProfileFragment mMainPageFragment = new ProfileFragment();
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
                AllPostListView.removeFooterView(ListFooter);
            }
            AllPostListViewArrayAdapter.notifyDataSetChanged();
            View footer = ListFooter;
            AllPostListView.addFooterView(footer);
            AllPostListView.setOnScrollListener(new EndlessScrollListener("ProfileActivity",this, "", null,current_page, total));
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
            p = new ArrayList<>();
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
        } else {
            RemoveListFooter();
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
                Log.e("Renews PARSE ERROR", cx.getMessage());
            }
        }
        return u;
    }
    public void RemoveListFooter(){
        canLoad = false;
        try
        {
            progressBar.setVisibility(View.GONE);
            content_frame.setBackgroundResource(R.drawable.nothing_bg);
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

    private void gotoBrowser()
    {
        String url = PostParams.getBaseUrl() + "/user/"+user.uid+"/edit";
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
        startActivity(browserIntent);
    }


}
