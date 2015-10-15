package ir.weblogestaan.sardabir.Adapters;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


import java.text.DecimalFormat;
import java.util.ArrayList;

import ir.weblogestaan.sardabir.R;

public class ProfilePagerAdapter extends PagerAdapter {

    Context context;
    public ArrayList<Integer> pages;
    public String type;
    public ProfilePagerAdapter(Context c, ArrayList<Integer> values, String _type)
    {
        pages = values;
        context = c;
        type = _type;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int layout = pages.get(position);
        View view = inflater.inflate(layout, container, false);

        //TextView txtTitle = (TextView) view.findViewById(R.id.txtCardTitle);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        if (pages != null)
            return pages.size();
        return 0;
    }

    public void AddNewPoints(ArrayList<Integer> n)
    {
        pages.addAll(n);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }
}