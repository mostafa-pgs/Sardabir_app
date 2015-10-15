package ir.weblogestaan.sardabir.Classes;

import android.content.Context;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Field;
import ir.weblogestaan.sardabir.ProfileActivity;

/**
 * Created by Windows on 8/18/15.
 */
public class User implements Serializable {
    public String uid,name,username,login,bio,
            email,image,follower_count,following_count,
            followed_subjects_count,renewser,repost_count,is_followed, subjects_count;

    public static String mod;

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
                f.set(n,null);
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
        return mod; // 54bd2d119dc5c1d2320aae0a60f30b80
    }

    public static void setMod(String _mod)
    {
        mod = _mod;
    }

    @Override
    public String toString(){
        return "Name : "+name+", usernmae : "+username+" and uID : "+uid
                + "image : "+image+", bio : "+bio;

    }


    public String getName()
    {
        return this.name.equals("") ? this.username : this.name;
    }

    public void gotoProfileActivity(Context context)
    {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra("user", this);
        context.startActivity(intent);
    }
    public static void Login(User u)
    {
        PostParams.current_user = u;
    }

    public static void Logout()
    {
        PostParams.current_user = null;
    }

    public static User GetCurrentUser()
    {
        return PostParams.current_user;
    }
}
