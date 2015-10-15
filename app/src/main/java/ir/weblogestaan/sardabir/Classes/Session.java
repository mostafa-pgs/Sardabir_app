package ir.weblogestaan.sardabir.Classes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.ListView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ir.weblogestaan.sardabir.LoginActivity;

/**
 * Created by Windows on 9/14/15.
 */
public class Session implements Serializable {
    Context context;
    protected static final String SESSION_KEY = "sardabir_app";

    public Session(Context _context)
    {
        this.context = _context;
    }

    public void StoreUserPass(String username,String pswd)
    {
        SharedPreferences.Editor e = context.getSharedPreferences(SESSION_KEY, Activity.MODE_PRIVATE).edit();
        e.putString("password", pswd);
        e.putString("username", username);
        e.apply();
        e.commit();
    }

    public void StoreMod(String mod)
    {
        SharedPreferences.Editor e = context.getSharedPreferences(SESSION_KEY, Activity.MODE_PRIVATE).edit();
        e.putString("mod", mod);
        e.apply();
        e.commit();
        User.setMod(mod);
    }

    public boolean hasUserPass()
    {
        String username = getUsername();
        String password = getPassword();
        if (username != "" && password != "") {
            return true;
        }
        return false;
    }


    public String getMod()
    {
        SharedPreferences savedSession = context.getSharedPreferences(SESSION_KEY, Activity.MODE_PRIVATE);
        return savedSession.getString("mod", "");
    }

    public String getUsername()
    {
        SharedPreferences savedSession = context.getSharedPreferences(SESSION_KEY, Activity.MODE_PRIVATE);
        return savedSession.getString("username", "");
    }

    public String getPassword()
    {
        SharedPreferences savedSession = context.getSharedPreferences(SESSION_KEY, Activity.MODE_PRIVATE);
        return savedSession.getString("password", "");
    }

    public String getVersion()
    {
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = "-1";
        if (info != null)
            version = info.versionName + "@" + info.versionCode;
        return version;
    }

    public List<NameValuePair> BasePostParams()
    {
        List<NameValuePair> ls = new ArrayList<>();
        ls.add(new BasicNameValuePair("mod", getMod()));
        if (PostParams.current_user != null)
            ls.add(new BasicNameValuePair("muid", PostParams.current_user.getID()));
        ls.add(new BasicNameValuePair("last_nid", PostParams.NODE_LAST_NID));
        ls.add(new BasicNameValuePair("version", getVersion()));
        return ls;
    }

    public List<NameValuePair> RegisterParams(String name, String email, String pass, String repeat) throws Exception {
        SardabirCrypt sc = new SardabirCrypt();
        List<NameValuePair> ls = new ArrayList<>();
        ls.add(new BasicNameValuePair("mail", sc.displayBytes(sc.encrypt(email),false)));
        ls.add(new BasicNameValuePair("username", sc.displayBytes(sc.encrypt(name),false)));
        ls.add(new BasicNameValuePair("pass", sc.displayBytes(sc.encrypt(pass), false)));
        ls.add(new BasicNameValuePair("repeat", sc.displayBytes(sc.encrypt(repeat),false)));
        return ls;
    }

    public List<NameValuePair> SuggestParams(String url, String desc) {
        List<NameValuePair> ls = new ArrayList<>();
        ls.add(new BasicNameValuePair("mod", getMod()));
        ls.add(new BasicNameValuePair("url", url));
        ls.add(new BasicNameValuePair("desc", desc));
        return ls;
    }

    public List<NameValuePair> ResetPassParams(String email) {
        List<NameValuePair> ls = new ArrayList<>();
        ls.add(new BasicNameValuePair("email", email));
        return ls;
    }

    public String EncodeUserPass()
    {
        SardabirCrypt m = new SardabirCrypt();
        String toCrypt = getUsername() + "~*~" + getPassword();
        try {
            return m.displayBytes(m.encrypt(toCrypt),false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String EncodeUserPass(String username,String pass)
    {
        SardabirCrypt m = new SardabirCrypt();
        String toCrypt = username + "~*~" + pass;
        try {
            return m.displayBytes(m.encrypt(toCrypt),false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
