package ir.weblogestaan.sardabir.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import ir.weblogestaan.sardabir.Adapters.RenewsAdapter;
import ir.weblogestaan.sardabir.Classes.EndlessScrollListener;
import ir.weblogestaan.sardabir.Classes.Renews;
import ir.weblogestaan.sardabir.FriendsActivity;
import ir.weblogestaan.sardabir.R;


/**
 * Created by Windows on 1/3/15.
 */
public class FriendsFragment extends Fragment {
    public ArrayList<Renews> posts;
    public int total, currentPage = 0;
    public static boolean isLoading = false;

    public FriendsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        posts = (ArrayList<Renews>) getArguments().getSerializable("posts");
        total = 1000;
        View rootView = inflater.inflate(R.layout.friends_feragment, container, false);

        ListView ls = (ListView) rootView.findViewById(R.id.lsRenews);
        RenewsAdapter m = new RenewsAdapter(container.getContext(), posts, true);
        ls.setAdapter(m);
        View footer = inflater.inflate(R.layout.main_list_view_footer, container, false);
        ls.addFooterView(footer);
        ls.setOnScrollListener(new EndlessScrollListener("FriendsActivity",container.getContext(), "", null,0, total));
        FriendsActivity.AllPostListViewArrayAdapter = m;
        FriendsActivity.AllPostListView = ls;
        FriendsActivity.ListFooter = footer;

        return rootView;
    }
}