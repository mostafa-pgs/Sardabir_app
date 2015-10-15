package ir.weblogestaan.sardabir.Classes;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Windows on 8/18/15.
 */
public class PostParams implements Serializable {

    public static String getBaseUrl()
    {
        return "http://weblogestaan.ir";
    }

    public static User current_user;

    public static int RENEWS_ACTIVITY_CODE = 14;
    public static int RENEWS_ACTIVITY_RESULT_SUCCESS_CODE = 12;
    public static int RENEWS_ACTIVITY_RESULT_ERROR_CODE = 13;


    public static String NODE_LAST_NID = "0";


    public static Integer USER_TYPE_LIKERS = 0;
    public static Integer USER_TYPE_FOLLOWERS = 1;
    public static Integer USER_TYPE_FOLLOWINGS = 2;

}
