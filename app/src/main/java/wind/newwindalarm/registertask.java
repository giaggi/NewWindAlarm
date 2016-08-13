package wind.newwindalarm;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by giacomo on 04/07/2015.
 */
public class registertask extends AsyncTask<Object, Boolean, Boolean> {


    public static final int POST_REGISTERDEVICE = 1;
    public static final int POST_REGISTERUSER = 2;
    private int postType;

    public AsyncRegisterResponse delegate = null;//Call back interface
    private ProgressDialog dialog;
    private Activity activity;
    private boolean error = false;
    private String errorMessage = "";
    int regId;
    String response = "";

    public registertask(Activity activity, AsyncRegisterResponse asyncResponse, int postType) {

        this.postType = postType;
        this.activity = activity;
        delegate = asyncResponse;//Assigning call back interfacethrough constructor
    }

    @Override
    protected Boolean doInBackground(Object... params) {

        URL myUrl = null;
        HttpURLConnection conn = null;

        String serverURL = AlarmPreferences.getServerUrl(activity);//getServerURL();

        try {

            myUrl = new URL(serverURL + "/register");
            conn = (HttpURLConnection) myUrl.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(60000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            String postData = "";

            if (postType == POST_REGISTERDEVICE) {
                String regId = (String) params[0];
                String name = (String) params[1];
                String personId = (String) params[2];

                postData = URLEncoder.encode("registerdevice", "UTF-8") + "=" +
                        URLEncoder.encode("true", "UTF-8") + "&" +
                        URLEncoder.encode("regId", "UTF-8") + "=" +
                        URLEncoder.encode(regId, "UTF-8") + "&" +
                        URLEncoder.encode("name", "UTF-8") + "=" +
                        URLEncoder.encode(name, "UTF-8") + "&" +
                        URLEncoder.encode("personid", "UTF-8") + "=" +
                        URLEncoder.encode(personId, "UTF-8") + "&";

            } else if (postType == POST_REGISTERUSER) {

                String personId = (String) params[0];
                String personName = (String) params[1];
                String personEmail = (String) params[2];
                Uri personPhoto = (Uri) params[3];
                String authCode = (String) params[4];

                postData = URLEncoder.encode("registeruser", "UTF-8") + "=" +
                        URLEncoder.encode("true", "UTF-8") + "&" +
                        URLEncoder.encode("personId", "UTF-8") + "=" +
                        URLEncoder.encode(personId, "UTF-8") + "&"+
                        URLEncoder.encode("personName", "UTF-8") + "=" +
                        URLEncoder.encode(personName, "UTF-8") + "&"+
                        /*URLEncoder.encode("personEmail", "UTF-8") + "=" +
                        URLEncoder.encode(personEmail, "UTF-8") + "&"+
                        URLEncoder.encode("personPhoto", "UTF-8") + "=" +
                        URLEncoder.encode(personPhoto.toString(), "UTF-8") + "&"+*/
                        URLEncoder.encode("authCode", "UTF-8") + "=" +
                        URLEncoder.encode(authCode, "UTF-8") + "&";

            }
            OutputStream os = conn.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            bufferedWriter.write(postData);
            bufferedWriter.flush();
            bufferedWriter.close();

            InputStream inputStream = conn.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                response += line;
            }
            bufferedReader.close();
            inputStream.close();
            conn.disconnect();
            os.close();


        } catch (
                MalformedURLException e
                )

        {
            e.printStackTrace();
            return false;
        } catch (
                UnsupportedEncodingException e
                )

        {
            e.printStackTrace();
            return false;
        } catch (
                ProtocolException e
                )

        {
            e.printStackTrace();
            return false;
        } catch (
                SocketTimeoutException e
                )

        {
            e.printStackTrace();
            return false;
        } catch (
                IOException e
                )

        {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    protected void onPreExecute() {
    }

    protected void onProgressUpdate(Integer... progress) {
        // setProgressPercent(progress[0]);
    }

    protected void onPostExecute(Boolean res) {
        delegate.processFinish(response, error, errorMessage);
    }

}
