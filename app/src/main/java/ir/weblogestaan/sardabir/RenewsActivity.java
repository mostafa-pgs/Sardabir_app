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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.picasso.Picasso;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ir.weblogestaan.sardabir.Adapters.PostAdapter;
import ir.weblogestaan.sardabir.Classes.Post;
import ir.weblogestaan.sardabir.Classes.PostParams;
import ir.weblogestaan.sardabir.Classes.Renews;
import ir.weblogestaan.sardabir.Classes.Subject;
import ir.weblogestaan.sardabir.Classes.User;
import ir.weblogestaan.sardabir.Fragments.TermFragment;
import ir.weblogestaan.sardabir.Network.HttpService;

public class RenewsActivity extends ActionBarActivity implements Renews.RenewsResult {

    public Post post;
    EditText editTextComment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renews);
        Typeface typeFace = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Yekan.ttf");
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
        Intent i = getIntent();
        post = (Post)i.getSerializableExtra("post");
        ArrayList<Post> p = new ArrayList<>();
        p.add(post);
        ListView lsPostItem = (ListView) findViewById(R.id.lsPostItem);
        lsPostItem.setAdapter(new PostAdapter(this, p, true));
        ImageView imgUser = (ImageView) findViewById(R.id.imgUserLogo);
        String url =  (PostParams.current_user != null) ? PostParams.current_user.image : "" ;
        if (url != "" && url != null) {
            try {
                Picasso.with(this).load(url).placeholder(R.mipmap.user_placeholder).into(imgUser);
            } catch (Exception e) {
                imgUser.setImageResource(R.mipmap.user_placeholder);
            }
        }
        ImageButton btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editTextComment = (EditText) findViewById(R.id.editTxtComment);
        editTextComment.setTypeface(typeFace);
        ImageButton btnSend = (ImageButton) findViewById(R.id.btnImgSend);
        final RenewsActivity that = this;
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Renews r = new Renews(v.getContext(),editTextComment.getText().toString(),post,User.GetCurrentUser());
                r.callback = that;
                r.Post();
            }
        });
    }

    @Override
    public void onError() {
        Toast.makeText(this,"مشکلی در ارسال وجود دارد...",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSuccess() {
        Toast.makeText(this,"با موفقیت ارسال شد",Toast.LENGTH_LONG).show();
        setResult(1001);
        finish();
    }
}
