package ir.weblogestaan.sardabir.Classes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * Created by Windows on 8/18/15.
 */
public class User implements Serializable {
    public String uid,name,username,login,bio,email,image,follower_count,following_count,followed_subjects_count,renewser;

    public static User parseUser(JSONObject jsonReminder) throws JSONException, IllegalAccessException {
        User n = new User();
        if (jsonReminder == null || jsonReminder.has("uid") == false) {
            return n;
        }

        n.setID(jsonReminder.getString("uid"));
        for(Field f : n.getClass().getFields()) {
            if (jsonReminder.has(f.getName()))
                f.set(n,jsonReminder.get(f.getName()));
            else
                f.set(n,"empty");
        }

        return n;
    }



    public String getID() {
        return this.uid;
    }

    public void setID(String _id) {
        this.uid = _id;
    }

    public static String getMod()
    {
        return "54bd2d119dc5c1d2320aae0a60f30b80";
    }

    @Override
    public String toString(){
        return "Name : "+name+", usernmae : "+username+" and uID : "+uid
                + "image : "+image+", bio : "+bio;

    }

    public static void Login(User u)
    {
        PostParams.current_user = u;
    }

    public static User GetCurrentUser()
    {
        return PostParams.current_user;
    }
}
