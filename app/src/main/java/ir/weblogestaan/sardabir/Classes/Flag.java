package ir.weblogestaan.sardabir.Classes;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ir.weblogestaan.sardabir.Network.HttpService;

/**
 * Created by Windows on 8/18/15.
 */
public class Flag {
    Context context;
    String flag_type /*ms,renews,bookmark,...*/,flag_action/* flag / unflag*/, entity_id, uid,in_url_action;

    public Flag(Context c,String in_url, String flag_type,String flag, String entity_id) {
        this.flag_action = flag;
        this.entity_id = entity_id;
        this.uid = User.getMod();
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

        }
    }

    public List<NameValuePair> FlagPostParams()
    {
        List<NameValuePair> ls = new ArrayList<NameValuePair>();
        ls.add(new BasicNameValuePair("mod",this.uid));
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
