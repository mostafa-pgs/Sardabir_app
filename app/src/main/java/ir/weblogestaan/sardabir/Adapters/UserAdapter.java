package ir.weblogestaan.sardabir.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ir.weblogestaan.sardabir.Classes.Flag;
import ir.weblogestaan.sardabir.Classes.PostParams;
import ir.weblogestaan.sardabir.Classes.Subject;
import ir.weblogestaan.sardabir.Classes.User;
import ir.weblogestaan.sardabir.MainActivity;
import ir.weblogestaan.sardabir.R;
import ir.weblogestaan.sardabir.SubjectsActivity;
import ir.weblogestaan.sardabir.TermActivity;

public class UserAdapter extends BaseAdapter {

	private final Context context;
	private ArrayList<User> users;

	public UserAdapter(Context context, ArrayList<User> values) {
		this.context = context;
		this.users = values;
	}

    @Override
    public int getCount()
    {
        return  0 + users.size();
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
        View rowView = inflater.inflate(R.layout.user_item, parent, false);
        TextView txtName = (TextView) rowView.findViewById(R.id.txtName);
        TextView txtCommon = (TextView) rowView.findViewById(R.id.txtCommon);
        TextView txtDesc = (TextView) rowView.findViewById(R.id.txtDesc);
        final ToggleButton btnFollow = (ToggleButton) rowView.findViewById(R.id.btnImgFollow);
        ImageView imgSubjectLogo = (ImageView) rowView.findViewById(R.id.imgSubjectLogo);
        Typeface type = Typeface.createFromAsset(context.getAssets(), "fonts/Yekan.ttf");
        txtName.setTypeface(type);
        txtCommon.setTypeface(type);
        txtDesc.setTypeface(type);
        btnFollow.setTypeface(type);

        final User s = users.get(position);
        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Flag f;
                if (btnFollow.isChecked()) {
                    f = new Flag(context, "friend","friend","flag", s.getID());
                    users.get(position).is_followed = "yes";
                    ((ToggleButton) v).setTextColor(Color.GRAY);
                    ((ToggleButton) v).setChecked(true);
                } else {
                    f = new Flag(context, "friend","friend","unflag", s.getID());
                    users.get(position).is_followed = "no";
                    ((ToggleButton) v).setTextColor(Color.WHITE);
                    ((ToggleButton) v).setChecked(false);
                }
                f.send();
                notifyDataSetChanged();
            }
        });
        btnFollow.setChecked(s.is_followed.equals("yes"));
        if (s.is_followed.equals("yes")) {
            btnFollow.setTextColor(Color.GRAY);
        }
        else
            btnFollow.setTextColor(Color.WHITE);
        txtName.setText(s.name);
        txtDesc.setText(s.bio);
        txtCommon.setVisibility(View.GONE);
        String url = PostParams.getBaseUrl() + s.image;
        imgSubjectLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                s.gotoProfileActivity(arg0.getContext());
            }
        });


        if (url != "" && url != null) {
            try {
                Picasso.with(context).load(url).placeholder(R.mipmap.placeholder).into(imgSubjectLogo);
            } catch (Exception e) {
                imgSubjectLogo.setVisibility(View.GONE);
            }
        }

        txtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

            }
        });

        return rowView;
	}
}
