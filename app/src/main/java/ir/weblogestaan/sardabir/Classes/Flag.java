package ir.weblogestaan.sardabir.Classes;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ir.weblogestaan.sardabir.Network.HttpService;

/**
 * Created by Windows on 8/18/15.
 */
public class Flag implements Serializable{
    Context context;
    String flag_type /*ms,renews,bookmark,...*/,flag_action/* flag / unflag*/, entity_id, uid,in_url_action;
    public FlagResult callback;

    public interface FlagResult extends Serializable{
        public void onError();
        public void onSuccess();
    }

    public Flag(Context c,String in_url, String flag_type,String flag, String entity_id) {
        this.flag_action = flag;
        this.entity_id = entity_id;
        this.uid = "";
        this.flag_type = flag_type;
        this.context = c;
        this.in_url_action = in_url;
    }

    public void send()
    {
        SendFlagTask sf = new SendFlagTask();
        String url = PostParams.getBaseUrl()+"/REST/action/"+in_url_action+"?ok=BEZAR17";
        HttpService p = new HttpService(context,url, false);
        p.ls = this.FlagPostParams();
        sf.execute(p);
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
            Result(jsonResp);
        }
    }

    protected void Result(JSONObject json)
    {
        if (json != null && callback != null)
        {
            if (json.has("result"))
            {
                String result;
                try {
                    result = json.getString("result");
                } catch (JSONException e) {
                    result = "false";
                }
                if (Boolean.parseBoolean(result))
                    callback.onSuccess();
                else
                    callback.onError();
            }
        }
    }

    public List<NameValuePair> FlagPostParams()
    {
        Session session = new Session(context);
        List<NameValuePair> ls = new ArrayList<>();
        ls.add(new BasicNameValuePair("mod",session.getMod()));
        ls.add(new BasicNameValuePair("flag",this.flag_action));
        ls.add(new BasicNameValuePair("entity_type",this.flag_type));
        ls.add(new BasicNameValuePair("entity_id",this.entity_id));
        return ls;
    }

    @Override
    public String toString()
    {
        return this.flag_type+" :: "+flag_action+" :: "+entity_id;
    }
}
