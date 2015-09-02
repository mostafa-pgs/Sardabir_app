package ir.weblogestaan.sardabir.Network;
import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import ir.weblogestaan.sardabir.MainActivity;

public class HttpService
{
    public String url = "";
    public boolean toAdd = false;
    public List<NameValuePair> ls;
    public Context context;
    public boolean isOnSplash = false;

    public HttpService(Context _context, String _url,boolean add)
    {
        this.url = _url;
        this.toAdd = add;
        this.context = _context;
    }

    public JSONObject RequestObject()
    {
        HttpEntity httpEntity = null;
        try
        {
            DefaultHttpClient httpClient = new DefaultHttpClient();  // Default HttpClient
            Log.e("Sent Url :  ", url);
            HttpGet httpGet = new HttpGet(url);

            HttpProtocolParams.setUseExpectContinue(httpClient.getParams(), false);
            HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 10000); //Timeout Limit
            HttpRequestRetryHandler retryHandler = new HttpRequestRetryHandler() {

                @Override
                public boolean retryRequest(IOException exception, int executionCount,HttpContext context) {
                    // retry a max of 5 times
                    if(executionCount >= 5){
                        return false;
                    }
                    if (exception instanceof ClientProtocolException){
                        return true;
                    }
                    return false;
                }

            };
            httpClient.setHttpRequestRetryHandler(retryHandler);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            httpEntity = httpResponse.getEntity();
        } catch (ClientProtocolException e) {
            Log.e("--- Protocol  : ", e.getMessage());
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        // Convert HttpEntity into JSON Array
        JSONObject jsonArr = null;
        if (httpEntity != null) {
            try {
                String entityResponse = EntityUtils.toString(httpEntity);
                Log.e("response was",entityResponse);
                jsonArr = new JSONObject(entityResponse);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        return jsonArr;
    }

    public JSONArray RequestArray()
    {
        HttpEntity httpEntity = null;
        try
        {
            DefaultHttpClient httpClient = new DefaultHttpClient();  // Default HttpClient
            Log.e("Sent Url :  ", url);
            HttpGet httpGet = new HttpGet(url);

            HttpProtocolParams.setUseExpectContinue(httpClient.getParams(), false);
            HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 10000); //Timeout Limit
            HttpRequestRetryHandler retryHandler = new HttpRequestRetryHandler() {

                @Override
                public boolean retryRequest(IOException exception, int executionCount,HttpContext context) {
                    // retry a max of 5 times
                    if(executionCount >= 5){
                        return false;
                    }
                    if (exception instanceof ClientProtocolException){
                        return true;
                    }
                    return false;
                }

            };

            httpClient.setHttpRequestRetryHandler(retryHandler);
            HttpResponse httpResponse = httpClient.execute(httpGet);

            httpEntity = httpResponse.getEntity();



        } catch (ClientProtocolException e) {
            // Signals error in http protocol
            Log.e("--- Protocol  : ", e.getMessage());
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        // Convert HttpEntity into JSON Array
        JSONArray jsonArr = null;
        if (httpEntity != null) {
            try {
                String entityResponse = EntityUtils.toString(httpEntity);
                Log.e("response was",entityResponse);
                jsonArr = new JSONArray(entityResponse);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return jsonArr;
    }

    public JSONArray postDataArray() {
        // Create a new HttpClient and Post Header
        HttpEntity httpEntity = null;
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(this.url);
        Log.e("POST Sent Url :  ", url);
        try {
            httppost.setEntity(new UrlEncodedFormEntity(this.ls));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            httpEntity = response.getEntity();

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JSONArray jsonArr = null;
        if (httpEntity != null) {
            try {
                String entityResponse = EntityUtils.toString(httpEntity);
                Log.e("POST Respons was : ", entityResponse);
                jsonArr = new JSONArray(entityResponse);
                return jsonArr;
            }catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jsonArr;
    }

    public JSONObject postDataObject() {
        // Create a new HttpClient and Post Header
        HttpEntity httpEntity = null;
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(this.url);
        Log.e("POST Sent Url :  ", url);
        try {
            if (this.ls != null)
                httppost.setEntity(new UrlEncodedFormEntity(this.ls));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            httpEntity = response.getEntity();

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JSONObject jsonObj = null;
        if (httpEntity != null) {
            try {
                String entityResponse = EntityUtils.toString(httpEntity);
                Log.e("POST Respons was : ", entityResponse);
                jsonObj = new JSONObject(entityResponse);
                return jsonObj;
            }catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jsonObj;
    }
}