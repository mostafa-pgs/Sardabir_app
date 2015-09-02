package ir.weblogestaan.sardabir.Classes;
import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ir.weblogestaan.sardabir.Network.HttpService;

/**
 * Created by Windows on 8/18/15.
 */
public class Renews {
    Context context;
    String comment;
    Post post;
    User renewser;
    public RenewsResult callback;

    public interface RenewsResult {
        public void onError();
        public void onSuccess();
    }

    private String RESULT_KEY = "result";
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
                }
            }
        }
        onError();
    }

    public void onError()
    {
        callback.onError();
    }

    public void onSuccess()
    {
        callback.onSuccess();
    }

    public class SendFlagTask extends AsyncTask<HttpService,Long,JSONObject>
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
        List<NameValuePair> ls = new ArrayList<NameValuePair>();
        ls.add(new BasicNameValuePair("mod",User.getMod()));
        ls.add(new BasicNameValuePair("comment",this.comment));
        ls.add(new BasicNameValuePair("nid",this.post.nid));
        ls.add(new BasicNameValuePair("uid",this.renewser.uid));
        return ls;
    }

    @Override
    public String toString()
    {
        return this.comment;
    }
}
