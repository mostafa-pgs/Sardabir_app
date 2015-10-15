package ir.weblogestaan.sardabir;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

import ir.weblogestaan.sardabir.Adapters.PostAdapter;
import ir.weblogestaan.sardabir.Classes.Post;
import ir.weblogestaan.sardabir.Classes.PostParams;
import ir.weblogestaan.sardabir.Classes.Renews;
import ir.weblogestaan.sardabir.Classes.User;

public class RenewsActivity extends BaseActivity implements Renews.RenewsResult,Serializable {

    public transient Post post;
    EditText editTextComment;
    ImageButton btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renews);
        Typeface typeFace = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Yekan.ttf");
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        post = (Post)bundle.getSerializable("post");
        ArrayList<Post> p = new ArrayList<>();
        p.add(post);
        ListView lsPostItem = (ListView) findViewById(R.id.lsPostItem);
        lsPostItem.setAdapter(new PostAdapter(this, p, true, false));
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
        btnSend = (ImageButton) findViewById(R.id.btnImgSend);
        final RenewsActivity that = this;
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Renews r = new Renews(v.getContext(),editTextComment.getText().toString(),post,User.GetCurrentUser());
                r.callback = that;
                Toast.makeText(v.getContext(),"در حال ارسال ...",Toast.LENGTH_SHORT).show();
                r.Post();
                btnSend.setVisibility(View.GONE);
            }
        });
        setStatusbarColor();
    }

    @Override
    public void onError() {
        Toast.makeText(this,"مشکلی در ارسال وجود دارد...",Toast.LENGTH_LONG).show();
        btnSend.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSuccess() {
        Toast.makeText(this,"با موفقیت ارسال شد",Toast.LENGTH_LONG).show();
        Intent intent = getIntent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("post", post);
        intent.putExtras(bundle);
        setResult(PostParams.RENEWS_ACTIVITY_RESULT_SUCCESS_CODE, intent);
        finish();
    }
}
