package ir.weblogestaan.sardabir.Adapters;

import java.io.Serializable;
import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;
import ir.weblogestaan.sardabir.Classes.Flag;
import ir.weblogestaan.sardabir.Classes.Post;
import ir.weblogestaan.sardabir.Classes.PostParams;
import ir.weblogestaan.sardabir.Classes.ViewHolder;
import ir.weblogestaan.sardabir.LinkActivity;
import ir.weblogestaan.sardabir.MainActivity;
import ir.weblogestaan.sardabir.R;
import ir.weblogestaan.sardabir.RenewsActivity;
import ir.weblogestaan.sardabir.TermActivity;

public class PostAdapter extends BaseAdapter implements Serializable, OnClickListener{

	private final Context context;
	public ArrayList<Post> posts;
    private boolean isMinimal = false;
    private boolean isLocked = false;

	public PostAdapter(Context context, ArrayList<Post> values, boolean minimal, boolean locked) {
		this.context = context;
		this.posts = values;
        this.isMinimal = minimal;
        this.isLocked = locked;
	}

    @Override
    public int getCount()
    {
        return  0 + posts.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.post_item, parent, false);
        RelativeLayout imgHolder = ViewHolder.get(convertView,R.id.imgHolder);
        RelativeLayout toMinimal = ViewHolder.get(convertView,R.id.toMinimal);
        TextView txtTitle = ViewHolder.get(convertView,R.id.txtTitle);
        TextView txtTime = ViewHolder.get(convertView,R.id.txtTime);
        TextView txtSummary = ViewHolder.get(convertView,R.id.txtSummary);
        final ImageView imageView = ViewHolder.get(convertView,R.id.postImage);
        TextView txtBlogName = ViewHolder.get(convertView,R.id.txtBlogName);
        TextView txtSubject = ViewHolder.get(convertView,R.id.txtBlogSubjectName);
        Button btnReadMore = ViewHolder.get(convertView,R.id.btnReadMore);
        ToggleButton btnLike = ViewHolder.get(convertView,R.id.btnImgLike);
        ToggleButton btnImgRepost = ViewHolder.get(convertView,R.id.btnImgRepost);
        ImageButton btnImageShare = ViewHolder.get(convertView,R.id.btnImgShare);
        Typeface type = Typeface.createFromAsset(context.getAssets(), "fonts/Yekan.ttf");
        txtTitle.setTypeface(type);
        txtSubject.setTypeface(type);
        txtTime.setTypeface(type);
        txtBlogName.setTypeface(type);
        btnReadMore.setTypeface(type);
        final Post s = posts.get(position);

        txtTitle.setText(Html.fromHtml(s.title));
        txtBlogName.setText("از " + s.blog_name);
        txtTime.setText(s.date_str_fa);
        //btnBlog.setText(s.blog_address.replace("http://","").replace("https://", "").toUpperCase().replace("/",""));
        txtSummary.setEllipsize(TextUtils.TruncateAt.END);
        txtSummary.setText(Html.fromHtml(s.body));
        txtSummary.setTypeface(type);
        if(s.subject != null) {
            txtSubject.setText(s.subject.name);
            if (!isLocked)
                txtSubject.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), TermActivity.class);
                        intent.putExtra("subject", s.subject);
                        v.getContext().startActivity(intent);
                    }
                });
            else
                txtSubject.setOnClickListener(this);
        }
        btnImageShare.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_on_click));
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, GetShareMessage(s));
                sendIntent.setType("text/plain");
                view.getContext().startActivity(Intent.createChooser(sendIntent, "Share..."));
            }
        });

        if (!isLocked) {
            btnLike.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_on_click));
                    boolean isChecked = ((ToggleButton) v).isChecked();
                    Flag f;
                    if (isChecked) {
                        f = new Flag(context, "like", "bookmarks", "flag", s.nid);
                        posts.get(position).liked = "yes";
                    } else {
                        f = new Flag(context, "like", "bookmarks", "unflag", s.nid);
                        posts.get(position).liked = "no";
                    }
                    f.callback = new Flag.FlagResult() {
                        @Override
                        public void onError() {
                            Toast.makeText(context, "خطا: دسترسی به اینترنت", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onSuccess() {
                            notifyDataSetChanged();
                        }
                    };
                    f.send();
                }
            });
        } else {
            btnLike.setOnClickListener(this);
        }
        btnLike.setChecked(s.liked.equals("yes"));

        if (!isLocked) {
            btnImgRepost.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (((ToggleButton) v).isChecked()) {
                        Intent intent = new Intent(v.getContext(), RenewsActivity.class);
                        Bundle bundle = new Bundle();
                        posts.get(position).position = "" + position;
                        bundle.putSerializable("post", posts.get(position));
                        intent.putExtras(bundle);
                        ((Activity) v.getContext()).startActivityForResult(intent, PostParams.RENEWS_ACTIVITY_CODE);
                        ((ToggleButton) v).setChecked(false);
                    } else {
                        Flag f;
                        f = new Flag(context, "repost", "renews", "unflag", s.nid);
                        posts.get(position).reposted = "no";
                        f.send();
                        notifyDataSetChanged();
                    }
                }
            });
        } else {
            btnImgRepost.setOnClickListener(this);
        }
        btnImgRepost.setChecked(s.reposted.equals("yes"));

        String url = MainActivity.base_url + s.image_url;
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
            }
        });

        btnReadMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_on_click));
                Intent intent = new Intent(v.getContext(), LinkActivity.class);
                intent.putExtra("post", posts.get(position));
                v.getContext().startActivity(intent);
            }
        });

        if (s.image_url.equals("")) {
            imageView.setVisibility(View.GONE);
        } else {
            try {
                imageView.setVisibility(View.VISIBLE);
                Picasso.with(context).load(url).placeholder(R.mipmap.placeholder).into(imageView);
            } catch (Exception e) {
                imageView.setVisibility(View.GONE);
            }
        }

        txtTitle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(arg0.getContext(), LinkActivity.class);
                intent.putExtra("post", posts.get(position));
                arg0.getContext().startActivity(intent);
            }
        });
        if (isMinimal == true) {
            toMinimal.setVisibility(View.GONE);
        } else {
            PostParams.NODE_LAST_NID = s.nid;
        }
//        convertView.setTag(s);
        return convertView;
	}

    @TargetApi(11)
    public boolean setData(ListView l,ArrayList<Post> data) {
        if(data.size() == 0)
        {
            return false;
        }
        if (data != null) {
            posts.addAll(data);
            notifyDataSetChanged();
        }
        return true;
    }
    @TargetApi(11)
    public boolean prependData(ListView l,ArrayList<Post> data) {
        if(data.size() == 0)
        {
            return false;
        }
        if (data != null) {
            posts.clear();
            posts.addAll(data);
            notifyDataSetChanged();
        }
        return true;
    }

    private String GetShareMessage(Post s){
        String share = "";
        share += s.title;
        share += "   " + s.link +" via @sardabir_app";
        return share;
    }

    @Override
    public void onClick(View v) {
        if (v instanceof ToggleButton)
            ((ToggleButton) v).setChecked(false);
        Toast t = Toast.makeText(context, "برای استفاده از این امکان باید وارد برنامه شوید", Toast.LENGTH_LONG);
        t.setGravity(Gravity.CENTER, 0, 0);
        t.show();
    }
}
