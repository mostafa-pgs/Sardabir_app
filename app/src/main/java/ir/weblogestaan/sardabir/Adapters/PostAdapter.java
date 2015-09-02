package ir.weblogestaan.sardabir.Adapters;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import ir.weblogestaan.sardabir.Classes.Flag;
import ir.weblogestaan.sardabir.Classes.Post;
import ir.weblogestaan.sardabir.Classes.Subject;
import ir.weblogestaan.sardabir.LinkActivity;
import ir.weblogestaan.sardabir.MainActivity;
import ir.weblogestaan.sardabir.R;
import ir.weblogestaan.sardabir.RenewsActivity;
import ir.weblogestaan.sardabir.TermActivity;

public class PostAdapter extends BaseAdapter {

	private final Context context;
	private ArrayList<Post> posts;
    private boolean isMinimal = false;


	public PostAdapter(Context context, ArrayList<Post> values, boolean minimal) {
		this.context = context;
		this.posts = values;
        this.isMinimal = minimal;
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
        View rowView = convertView ;
        if (rowView == null)
            rowView = inflater.inflate(R.layout.post_item, parent, false);
        RelativeLayout imgHolder = (RelativeLayout) rowView.findViewById(R.id.imgHolder);
        RelativeLayout toMinimal = (RelativeLayout) rowView.findViewById(R.id.toMinimal);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txtTitle);
        TextView txtTime = (TextView) rowView.findViewById(R.id.txtTime);
        TextView txtSummary = (TextView) rowView.findViewById(R.id.txtSummary);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.postImage);
        TextView txtBlogName = (TextView) rowView.findViewById(R.id.txtBlogName);
        TextView txtSubject = (TextView) rowView.findViewById(R.id.txtBlogSubjectName);
        Button btnReadMore = (Button) rowView.findViewById(R.id.btnReadMore);
        ToggleButton btnLike = (ToggleButton) rowView.findViewById(R.id.btnImgLike);
        ToggleButton btnImgRepost = (ToggleButton) rowView.findViewById(R.id.btnImgRepost);
        ImageButton btnImageShare = (ImageButton) rowView.findViewById(R.id.btnImgShare);
        Typeface type = MainActivity.typeFace;
        txtTitle.setTypeface(type);
        txtSubject.setTypeface(type);
        txtTime.setTypeface(type);
        txtBlogName.setTypeface(type);
        btnReadMore.setTypeface(type);
        final Post s = posts.get(position);

        txtTitle.setText(s.title);
        txtBlogName.setText("از " + s.blog_name);
        txtTime.setText(s.date_str_fa);
        //btnBlog.setText(s.blog_address.replace("http://","").replace("https://", "").toUpperCase().replace("/",""));
        txtSummary.setEllipsize(TextUtils.TruncateAt.END);
        txtSummary.setText(s.body);
        txtSummary.setTypeface(type);
        txtSubject.setText(s.subject.name);
        txtSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), TermActivity.class);
                intent.putExtra("subject", s.subject);
                v.getContext().startActivity(intent);
            }
        });
        btnImageShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, GetShareMessage(s));
                sendIntent.setType("text/plain");
                view.getContext().startActivity(Intent.createChooser(sendIntent, "Share..."));
            }
        });

        btnLike.setChecked(s.liked.equals("yes"));
        btnLike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Flag f;
                if (isChecked) {
                    f = new Flag(context, "like", "bookmarks", "flag", s.nid);
                    posts.get(position).liked = "yes";
                } else {
                    f = new Flag(context, "like", "bookmarks", "unflag", s.nid);
                    posts.get(position).liked = "no";
                }
                Log.e("Flag", f.toString());
                f.send();
                notifyDataSetChanged();
            }
        });

        btnImgRepost.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                /*Flag f;
                if (isChecked) {
                    f = new Flag(context,"repost","renews","flag",s.nid);
                    posts.get(position).reposted = "yes";
                } else {
                    f = new Flag(context,"repost","renews","unflag",s.nid);
                    posts.get(position).reposted = "no";
                }
                Log.e("Flag",f.toString());
                f.send();
                notifyDataSetChanged();*/
            }
        });
        btnImgRepost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((ToggleButton)v).isChecked() == true) {
                    Intent intent = new Intent(v.getContext(), RenewsActivity.class);
                    intent.putExtra("post", posts.get(position));
                    v.getContext().startActivity(intent);
                    ((ToggleButton)v).setChecked(false);
                } else {
                    Flag f;
                    f = new Flag(context,"repost","renews","unflag",s.nid);
                    posts.get(position).reposted = "no";
                    f.send();
                    notifyDataSetChanged();
                }
            }
        });
        btnImgRepost.setChecked(s.reposted.equals("yes"));

        String url = MainActivity.base_url + s.image_url;
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
            }
        });

        btnReadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), LinkActivity.class);
                intent.putExtra("post", posts.get(position));
                v.getContext().startActivity(intent);
            }
        });

        if (url != "" && url != null) {
            try {
                Picasso.with(context).load(url).placeholder(R.mipmap.placeholder).into(imageView);
            } catch (Exception e) {
                imageView.setImageResource(R.mipmap.placeholder);
            }
        } else {
            imgHolder.setVisibility(View.GONE);
        }

        txtTitle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(arg0.getContext(), LinkActivity.class);
                intent.putExtra("post", posts.get(position));
                arg0.getContext().startActivity(intent);
            }
        });
        if (isMinimal == true) {
            toMinimal.setVisibility(View.GONE);
        }
        rowView.setTag(s);
        return rowView;
	}

    @TargetApi(11)
    public boolean setData(ListView l,ArrayList<Post> data) {
        if(data.size() == 0)
        {
            return false;
        }
        if (data != null) {
            posts.addAll(data);
        }
        return true;
    }


    private String GetShareMessage(Post s){
        String share = "";
        share += s.title;
        share += "   " + s.link +" via @sardabir_app";
        return share;
    }
}
