package ir.weblogestaan.sardabir.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import ir.weblogestaan.sardabir.Adapters.NotifAdapter;
import ir.weblogestaan.sardabir.Adapters.SubjectAdapter;
import ir.weblogestaan.sardabir.Classes.Notif;
import ir.weblogestaan.sardabir.Classes.Subject;
import ir.weblogestaan.sardabir.R;


/**
 * Created by Windows on 1/3/15.
 */
public class NotificationsFragment extends Fragment {
    public ArrayList<Notif.NotifBase> posts;
    public Subject subject;
    public int total;

    public NotificationsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        posts = (ArrayList<Notif.NotifBase>) getArguments().getSerializable("posts");
        total = 1000;
        View rootView = inflater.inflate(R.layout.notifications_feragment, container, false);

        ListView ls = (ListView) rootView.findViewById(R.id.lsNotifs);
        NotifAdapter m = new NotifAdapter(container.getContext(),posts);
        ls.setAdapter(m);

        return rootView;
    }
}