package ir.weblogestaan.sardabir.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import ir.weblogestaan.sardabir.Adapters.SubjectAdapter;
import ir.weblogestaan.sardabir.Adapters.UserAdapter;
import ir.weblogestaan.sardabir.Classes.Subject;
import ir.weblogestaan.sardabir.Classes.User;
import ir.weblogestaan.sardabir.R;


/**
 * Created by Windows on 1/3/15.
 */
public class UsersFragment extends Fragment {
    public ArrayList<User> posts;
    public Subject subject;
    public int total;

    public UsersFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        posts = (ArrayList<User>) getArguments().getSerializable("posts");
        total = 1000;
        View rootView = inflater.inflate(R.layout.users_feragment, container, false);

        ListView ls = (ListView) rootView.findViewById(R.id.lsSubjects);
        UserAdapter m = new UserAdapter(container.getContext(),posts);
        ls.setAdapter(m);

        return rootView;
    }
}