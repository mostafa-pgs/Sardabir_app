package ir.weblogestaan.sardabir.Classes;
import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import ir.weblogestaan.sardabir.Network.HttpService;

/**
 * Created by Windows on 8/18/15.
 */
public class Renews implements Serializable{
    private static final long serialVersionUID = 1L;
    Context context;
    public String comment, id, flag_id, date_create, date_str_fa, like_count, liked, reposted, renews_count;
    public Post post;
    public User renewser;
    public RenewsResult callback;

    public interface RenewsResult extends Serializable {
        public void onError();
        public void onSuccess();
    }

    private String RESULT_KEY = "result";
    public Renews()
    {

    }

    public Renews(Context c,String _comment, Post p, User _renewser) {
        this.comment = _comment;
        this.post = p;
        this.renewser = _renewser;
        this.context = c;
    }

    public void Post()
    {
        SendFlagTask sf = new SendFlagTask();
        String url = PostParams.getBaseUrl()+"/REST/action/repost?ok=BEZAR17";
        HttpService p = new HttpService(context,url, false);
        p.ls = this.RenewsPostParams();
        sf.execute(p);
    }

    public void manageResult(JSONObject json) throws JSONException {
        if (json != null)
        {
            if (json.has(RESULT_KEY)) {
                String result = json.getString(RESULT_KEY);
                if (result.equals("true") || Boolean.parseBoolean(result) == true) {
                    onSuccess();
                    return;
                }
            }
        }
        onError();
    }

    public static Renews parseRenews(JSONObject jsonReminder) throws JSONException, IllegalAccessException {
        Renews n = new Renews();
        if (jsonReminder == null || jsonReminder.has("id") == false) {
            return n;
        }

        n.setID(jsonReminder.getString("id"));
        for(Field f : n.getClass().getFields()) {

            if (f.getName().equals("post"))
            {
                if (jsonReminder.has(f.getName()))
                    f.set(n,Post.parsePost(jsonReminder.getJSONObject(f.getName())));
                else
                    f.set(n,new Post());
                continue;
            }
            if (f.getName().equals("renewser"))
            {
                if (jsonReminder.has(f.getName()))
                    f.set(n,User.parseUser(jsonReminder.getJSONObject(f.getName())));
                else
                    f.set(n,new User());
                continue;
            }
            if (jsonReminder.has(f.getName())) {
                f.set(n, jsonReminder.get(f.getName()));
            }
            else
                f.set(n,null);
        }

        return n;
    }

    public void onError()
    {
        callback.onError();
    }

    public void onSuccess()
    {
        callback.onSuccess();
    }

    public class SendFlagTask extends AsyncTask<HttpService,Long,JSONObject> implements Serializable
    {
        protected void onPreExecute() {

        }

        @Override
        protected JSONObject doInBackground(HttpService... params) {
            return params[0].postDataObject();
        }

        @Override
        protected void onPostExecute(JSONObject jsonResp)
        {
            try {
                manageResult(jsonResp);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public List<NameValuePair> RenewsPostParams()
    {
        Session session = new Session(context);
        List<NameValuePair> ls = new ArrayList<NameValuePair>();
        ls.add(new BasicNameValuePair("mod",session.getMod()));
        ls.add(new BasicNameValuePair("comment",this.comment));
        ls.add(new BasicNameValuePair("nid",this.post.nid));
        ls.add(new BasicNameValuePair("uid",this.renewser.uid));
        ls.add(new BasicNameValuePair("flag","flag"));
        ls.add(new BasicNameValuePair("entity_type","renews"));
        ls.add(new BasicNameValuePair("entity_id", this.post.nid));
        return ls;
    }

    public String getID() {
        return this.id;
    }

    public void setID(String _id) {
        this.id = _id;
    }

    @Override
    public String toString()
    {
        return this.comment + ";;;" + this.liked;
    }
}
