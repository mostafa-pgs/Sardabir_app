package ir.weblogestaan.sardabir.Classes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by Windows on 1/22/15.
 */
public class Subject implements Serializable {
    public String tid,name,color,image,desc,is_followed,follower_count,views;
    public ArrayList<User> followers;
    public Subject()
    {

    }

    public static Subject parseSubject(JSONObject jsonReminder) throws JSONException, IllegalAccessException {
        if (jsonReminder == null || jsonReminder.has("tid") == false) {
            return new Subject();
        }

        Subject n = new Subject();
        n.setID(jsonReminder.getString("tid"));
        for(Field f : n.getClass().getFields()) {
            if (f.getName() == "followers")
            {
                ArrayList<User> fs = new ArrayList<>();
                if (jsonReminder.has(f.getName())) {
                    JSONArray followersJson = jsonReminder.getJSONArray(f.getName());
                    for (int i = 0; i < followersJson.length(); i++)
                        fs.add(User.parseUser(followersJson.getJSONObject(i)));
                }
                f.set(n, fs);
                continue;
            }
            if (jsonReminder.has(f.getName()))
                f.set(n,jsonReminder.get(f.getName()));
            else
                f.set(n,"");
        }

        return n;
    }

    public String getID() {
        return this.tid;
    }

    public void setID(String _id) {
        this.tid = _id;
    }

    @Override
    public String toString()
    {
        return this.name+", "+this.image;
    }
}
