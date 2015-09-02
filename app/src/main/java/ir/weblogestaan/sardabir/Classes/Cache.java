package ir.weblogestaan.sardabir.Classes;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.ArrayList;

import ir.weblogestaan.sardabir.R;

/**
 * Created by Windows on 8/18/15.
 */
public class Cache {
    public static ArrayList<Subject> subjects;
    public static DisplayImageOptions getImageDisplayOptions()
    {
        return new DisplayImageOptions.Builder()
                .cacheOnDisc(true)
                .showImageForEmptyUri(R.mipmap.placeholder)
                .showImageOnFail(R.mipmap.placeholder)
                .showImageOnLoading(R.mipmap.placeholder)
                .build();
    }
}
