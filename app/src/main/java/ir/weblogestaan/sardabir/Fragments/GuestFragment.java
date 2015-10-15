package ir.weblogestaan.sardabir.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import ir.weblogestaan.sardabir.Adapters.PostAdapter;
import ir.weblogestaan.sardabir.Classes.EndlessScrollListener;
import ir.weblogestaan.sardabir.Classes.Post;
import ir.weblogestaan.sardabir.Classes.Subject;
import ir.weblogestaan.sardabir.GuestActivity;
import ir.weblogestaan.sardabir.MainActivity;
import ir.weblogestaan.sardabir.R;


/**
 * Created by Windows on 1/3/15.
 */
public class GuestFragment extends Fragment {
    public ArrayList<Post> posts;
    public Subject subject;
    public int total = 0;

    public GuestFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        posts = (ArrayList<Post>) getArguments().getSerializable("posts");
        subject = (Subject) getArguments().getSerializable("cat");
        total = 1000;
        View rootView = inflater.inflate(R.layout.guest_feragment, container, false);

        ListView ls = (ListView) rootView.findViewById(R.id.lsFeed);
        PostAdapter m = new PostAdapter(container.getContext(), posts, false, true);
        ls.setAdapter(m);
        View footer = inflater.inflate(R.layout.main_list_view_footer, null,false);
        ls.addFooterView(footer);
        ls.setOnScrollListener(new EndlessScrollListener("GuestActivity", container.getContext(), "", null, 0, total));
        GuestActivity.AllPostListViewArrayAdapter = m;
        GuestActivity.AllPostListView = ls;
        GuestActivity.ListFooter = footer;

        return rootView;
    }
}