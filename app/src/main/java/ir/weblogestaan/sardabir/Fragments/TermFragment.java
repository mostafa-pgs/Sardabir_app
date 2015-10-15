package ir.weblogestaan.sardabir.Fragments;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ir.weblogestaan.sardabir.Adapters.PostAdapter;
import ir.weblogestaan.sardabir.Classes.Cache;
import ir.weblogestaan.sardabir.Classes.CircleImageView;
import ir.weblogestaan.sardabir.Classes.EndlessScrollListener;
import ir.weblogestaan.sardabir.Classes.Post;
import ir.weblogestaan.sardabir.Classes.PostParams;
import ir.weblogestaan.sardabir.Classes.Subject;
import ir.weblogestaan.sardabir.Classes.User;
import ir.weblogestaan.sardabir.MainActivity;
import ir.weblogestaan.sardabir.R;
import ir.weblogestaan.sardabir.TermActivity;


/**
 * Created by Windows on 1/3/15.
 */
public class TermFragment extends Fragment {
    public ArrayList<Post> posts;
    public Subject subject;
    public int total;

    public TermFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        posts = (ArrayList<Post>) getArguments().getSerializable("posts");
        subject = (Subject) getArguments().getSerializable("subject");
        total = 1000;
        View rootView = inflater.inflate(R.layout.feed_feragment, container, false);
        Typeface typeFace = Typeface.createFromAsset(container.getContext().getAssets(), "fonts/Yekan.ttf");
        ListView ls = (ListView) rootView.findViewById(R.id.lsFeed);
        View header = inflater.inflate(R.layout.term_header, null, false);
        RelativeLayout followersBox = (RelativeLayout)header.findViewById(R.id.followersBox);
        ImageView imgLogo = (ImageView) header.findViewById(R.id.btnImgTermLogo);
        TextView txtTermName = (TextView) header.findViewById(R.id.txtTermName);
        TextView txtViewCount = (TextView) header.findViewById(R.id.txtViewCount);
        TextView txtFollowerCount = (TextView) header.findViewById(R.id.txtFollowerCount);
        TextView txtTermDesc = (TextView) header.findViewById(R.id.txtTermDesc);
        txtTermName.setTypeface(typeFace);
        txtViewCount.setTypeface(typeFace);
        txtTermDesc.setTypeface(typeFace);
        txtFollowerCount.setTypeface(typeFace);
        txtTermName.setText(subject.name);
        txtTermDesc.setText(subject.desc);
        txtViewCount.setText((Integer.parseInt(subject.views)+1) + " بازدید");
        txtFollowerCount.setText(subject.follower_count + " دنبال کننده");
        if (subject != null) {
            if (subject.image != null) {
                String url = PostParams.getBaseUrl() + subject.image;
                Picasso.with(container.getContext()).load(url).placeholder(R.mipmap.placeholder).into(imgLogo);
            }
        }
        if (subject.followers.size() > 0) {
            for (int i=0; i<subject.followers.size(); i++) {
                final User u = subject.followers.get(i);
                CircleImageView userImage ;
                switch (i) {
                    case 0:
                        userImage = (CircleImageView)followersBox.findViewById(R.id.img0);
                        break;
                    case 1:
                        userImage = (CircleImageView)followersBox.findViewById(R.id.img1);
                        break;
                    case 2:
                        userImage = (CircleImageView)followersBox.findViewById(R.id.img2);
                        break;
                    case 3:
                        userImage = (CircleImageView)followersBox.findViewById(R.id.img3);
                        break;
                    case 4:
                        userImage = (CircleImageView)followersBox.findViewById(R.id.img4);
                        break;
                    default:
                        userImage = (CircleImageView)followersBox.findViewById(R.id.img0);
                        break;
                }
                try {
                    ImageLoader.getInstance().displayImage(u.image, userImage, Cache.getImageDisplayOptions());
                } catch (Exception e) {
                    userImage.setVisibility(View.GONE);
                }
                userImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        u.gotoProfileActivity(container.getContext());
                    }
                });
            }
        } else {
            followersBox.setVisibility(View.INVISIBLE);
        }
        ls.addHeaderView(header);

        PostAdapter m = new PostAdapter(container.getContext(), posts, false, false);
        ls.setAdapter(m);

        View footer = inflater.inflate(R.layout.main_list_view_footer, null,false);
        ls.addFooterView(footer);
        ls.setOnScrollListener(new EndlessScrollListener("TermActivity",container.getContext(), "", null,0, total));
        TermActivity.AllPostListViewArrayAdapter = m;
        TermActivity.AllPostListView = ls;
        TermActivity.ListFooter = footer;

        return rootView;
    }
}