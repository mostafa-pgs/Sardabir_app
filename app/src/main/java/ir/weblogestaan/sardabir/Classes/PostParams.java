package ir.weblogestaan.sardabir.Classes;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Windows on 8/18/15.
 */
public class PostParams {

    public static String getBaseUrl()
    {
        return "http://weblogestaan.ir";
    }

    public static User current_user;

    public static List<NameValuePair> BasePostParams()
    {
        List<NameValuePair> ls = new ArrayList<NameValuePair>();
        ls.add(new BasicNameValuePair("mod", User.getMod()));
        return ls;
    }
}
