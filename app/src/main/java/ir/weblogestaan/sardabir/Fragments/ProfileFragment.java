package ir.weblogestaan.sardabir.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import ir.weblogestaan.sardabir.Adapters.PostAdapter;
import ir.weblogestaan.sardabir.Adapters.RenewsAdapter;
import ir.weblogestaan.sardabir.Classes.EndlessScrollListener;
import ir.weblogestaan.sardabir.Classes.Post;
import ir.weblogestaan.sardabir.Classes.Renews;
import ir.weblogestaan.sardabir.Classes.Subject;
import ir.weblogestaan.sardabir.HotActivity;
import ir.weblogestaan.sardabir.ProfileActivity;
import ir.weblogestaan.sardabir.R;


/**
 * Created by Windows on 1/3/15.
 */
public class ProfileFragment extends Fragment {
    public ArrayList<Renews> posts;
    public int total, currentPage = 0;
    public static boolean isLoading = false;

    public ProfileFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        posts = (ArrayList<Renews>) getArguments().getSerializable("posts");
        total = 1000;
        View rootView = inflater.inflate(R.layout.profile_feragment, container, false);

        ListView ls = (ListView) rootView.findViewById(R.id.lsRenews);
        RenewsAdapter m = new RenewsAdapter(container.getContext(), posts, true);
        ls.setAdapter(m);
        View footer = inflater.inflate(R.layout.main_list_view_footer, container,false);
        ls.addFooterView(footer);
        ls.setOnScrollListener(new EndlessScrollListener("ProfileActivity",container.getContext(), "", null,0, total));
        ProfileActivity.AllPostListViewArrayAdapter = m;
        ProfileActivity.AllPostListView = ls;
        ProfileActivity.ListFooter = footer;

        return rootView;
    }
}