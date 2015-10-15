package ir.weblogestaan.sardabir.Adapters;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;

import ir.weblogestaan.sardabir.Classes.CircleImageView;
import ir.weblogestaan.sardabir.Classes.Flag;
import ir.weblogestaan.sardabir.Classes.Post;
import ir.weblogestaan.sardabir.Classes.PostParams;
import ir.weblogestaan.sardabir.Classes.Renews;
import ir.weblogestaan.sardabir.Classes.ViewHolder;
import ir.weblogestaan.sardabir.LinkActivity;
import ir.weblogestaan.sardabir.MainActivity;
import ir.weblogestaan.sardabir.R;
import ir.weblogestaan.sardabir.RenewsActivity;
import ir.weblogestaan.sardabir.TermActivity;

public class RenewsAdapter extends BaseAdapter {

	private final Context context;
	private ArrayList<Renews> posts;
    private boolean isMinimal = false;


	public RenewsAdapter(Context context, ArrayList<Renews> values, boolean minimal) {
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
        if (convertView == null)
            convertView = inflater.inflate(R.layout.renews_item, parent, false);
        final Renews renews = posts.get(position);
        RelativeLayout imgHolder = ViewHolder.get(convertView,R.id.imgHolder);
        TextView txtTitle = ViewHolder.get(convertView,R.id.txtTitle);
        ImageView imageView = ViewHolder.get(convertView,R.id.postImage);
        ImageView imgRenewserLogo = ViewHolder.get(convertView,R.id.imgRenewserLogo);
        TextView txtRenewserName = ViewHolder.get(convertView, R.id.txtRenewserName);
        TextView txtRenewsComment = ViewHolder.get(convertView, R.id.txtRenewsComment);
        TextView txtLikeText = ViewHolder.get(convertView, R.id.txtLikeText);
        final TextView txtRenewsLikeCount = ViewHolder.get(convertView, R.id.txtRenewsLikeCount);

        TextView txtRenewsTime = ViewHolder.get(convertView, R.id.txtRenewsTime);
        ToggleButton btnImgLike = ViewHolder.get(convertView, R.id.btnImgLike);
        Typeface type = MainActivity.typeFace;
        Typeface typefaceRoya = Typeface.createFromAsset(context.getAssets(), "fonts/IRANSans.ttf");
        txtTitle.setTypeface(typefaceRoya);
        txtRenewserName.setTypeface(type);
        txtRenewsComment.setTypeface(type);
        txtRenewsTime.setTypeface(typefaceRoya);
        txtRenewsLikeCount.setTypeface(typefaceRoya);
        txtLikeText.setTypeface(typefaceRoya);

        final Post s = renews.post;

        txtRenewsComment.setText(renews.comment);
        txtRenewserName.setText(renews.renewser.getName());
        txtRenewsLikeCount.setText(renews.like_count);
        txtRenewsTime.setText(renews.date_str_fa);
        try {
            Picasso.with(context).load(renews.renewser.image).placeholder(R.mipmap.placeholder).into(imgRenewserLogo);
        } catch (Exception e) {
            imgRenewserLogo.setImageResource(R.mipmap.placeholder);
        }

        txtTitle.setText(Html.fromHtml(s.title));

        View.OnClickListener toRenewsActivityClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RenewsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("post", s);
                intent.putExtras(bundle);
                (v.getContext()).startActivity(intent);
            }
        };
        String url = MainActivity.base_url + s.image_url;
        imageView.setOnClickListener(toRenewsActivityClick);

        if (url != "" && url != null) {
            try {
                Picasso.with(context).load(url).placeholder(R.mipmap.placeholder).into(imageView);
            } catch (Exception e) {
                imageView.setImageResource(R.mipmap.placeholder);
                imgHolder.setVisibility(View.GONE);
            }
        } else {
            imageView.setImageResource(R.mipmap.placeholder);
            imgHolder.setVisibility(View.GONE);
        }
        btnImgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = ((ToggleButton) v).isChecked();
                Flag f;
                String newLikeCount = "";
                if (isChecked) {
                    f = new Flag(context, "like", "bookmarks", "flag", renews.id);
                    posts.get(position).liked = "yes";
                    newLikeCount = (Integer.parseInt(txtRenewsLikeCount.getText().toString()) + 1) + "";
                    posts.get(position).like_count = newLikeCount;
                    txtRenewsLikeCount.setText(newLikeCount);
                } else {
                    f = new Flag(context, "like", "bookmarks", "unflag", renews.id);
                    posts.get(position).liked = "no";
                    newLikeCount = (Integer.parseInt(txtRenewsLikeCount.getText().toString()) - 1) + "";
                    posts.get(position).like_count = newLikeCount;
                    txtRenewsLikeCount.setText(newLikeCount);
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
        btnImgLike.setChecked(posts.get(position).liked.equals("yes"));
        txtTitle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(arg0.getContext(), LinkActivity.class);
                intent.putExtra("post", s);
                arg0.getContext().startActivity(intent);
            }
        });
        return convertView;
	}

    @TargetApi(11)
    public boolean setData(ListView l,ArrayList<Renews> data) {
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

}
