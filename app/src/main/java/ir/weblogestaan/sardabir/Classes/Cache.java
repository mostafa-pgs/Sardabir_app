package ir.weblogestaan.sardabir.Classes;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.ArrayList;

import ir.weblogestaan.sardabir.R;

public class Cache {
    public static ArrayList<Subject> subjects;
    public static ArrayList<Notif.NotifBase> notifs;
    public static DisplayImageOptions getImageDisplayOptions()
    {
        return new DisplayImageOptions.Builder()
                .cacheOnDisc(true)
                .showImageForEmptyUri(R.mipmap.user_placeholder)
                .showImageOnFail(R.mipmap.user_placeholder)
                .showImageOnLoading(R.mipmap.user_placeholder)
                .build();
    }
}
