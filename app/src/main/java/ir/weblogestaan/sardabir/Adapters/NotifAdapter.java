package ir.weblogestaan.sardabir.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ir.weblogestaan.sardabir.Classes.Cache;
import ir.weblogestaan.sardabir.Classes.CircleImageView;
import ir.weblogestaan.sardabir.Classes.Flag;
import ir.weblogestaan.sardabir.Classes.Notif;
import ir.weblogestaan.sardabir.Classes.PostParams;
import ir.weblogestaan.sardabir.Classes.Subject;
import ir.weblogestaan.sardabir.MainActivity;
import ir.weblogestaan.sardabir.R;
import ir.weblogestaan.sardabir.SubjectsActivity;
import ir.weblogestaan.sardabir.TermActivity;

public class NotifAdapter extends BaseAdapter {

	private final Context context;
	private ArrayList<Notif.NotifBase> posts;

	public NotifAdapter(Context context, ArrayList<Notif.NotifBase> values) {
		this.context = context;
		this.posts = values;
	}

    @Override
    public int getCount()
    {
        if (posts != null)
            return  0 + posts.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private int lastPosition = -1;
    @Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.notif_item, parent, false);
        LinearLayout notifHolder = (LinearLayout) rowView.findViewById(R.id.notifHolder);
        TextView txtName = (TextView) rowView.findViewById(R.id.txtName);
        TextView txtText = (TextView) rowView.findViewById(R.id.txtText);
        ImageView imgNotifType = (ImageView) rowView.findViewById(R.id.imgNotifType);
        CircleImageView imgUserLogo = (CircleImageView) rowView.findViewById(R.id.imgUserLogo);
        Typeface type = MainActivity.typeFace;
        txtName.setTypeface(type);
        txtText.setTypeface(type);

        final Notif.NotifBase s = posts.get(position);

        if (s.seen.equals("0"))
            notifHolder.setBackgroundColor(Color.CYAN);

        if (s.subject_name.equals("node"))
            imgNotifType.setImageResource(R.mipmap.notiflike);
        else if (s.subject_name.equals("user"))
            imgNotifType.setImageResource(R.mipmap.greentick);

        txtName.setText(s.by.getName());
        txtText.setText(Html.fromHtml(s.text));
        imgUserLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s.by.gotoProfileActivity(context);
            }
        });
        String url = s.by.image;
        try {
            ImageLoader.getInstance().displayImage(url, imgUserLogo, Cache.getImageDisplayOptions());
        } catch (Exception e) {
            imgUserLogo.setVisibility(View.GONE);
        }

        txtName.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

            }
        });


        /*Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        rowView.startAnimation(animation);
        lastPosition = position;*/

        return rowView;
	}
}
