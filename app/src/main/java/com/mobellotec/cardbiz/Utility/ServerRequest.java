package com.mobellotec.cardbiz.Utility;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.mobellotec.cardbiz.Model.Parameter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by MobelloTech on 13-07-2015.
 */
public class ServerRequest extends AsyncTask<Void, Void, String> {

    private static int EOFEXCEPTION_MAX_ATTEMPT = 3;
    private static int EOFEXCEPTION_RETRY_ATTEMPT = 0;

    public static int POST = 1;

    public static int GET = 2;

    private int type;

    private String web_url;

    private ArrayList<Parameter> parameters = new ArrayList<>();

    private RequestListener listener;

    private ErrorType error;

    private Context context;


    public interface RequestListener {
        void onRequestSuccess(String result);

        void onRequestError(ErrorType error);
    }

    public ServerRequest(Context context, int type, String url, ArrayList<Parameter> parameters, RequestListener listener) {
        this.context = context;
        this.type = type;
        this.parameters = parameters;
        this.listener = listener;
        this.web_url = Constants.BASE_URL + url;
    }

    @Override
    protected String doInBackground(Void... params) {

        return backgroundProcess();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result == null)
            listener.onRequestError(error);
        else
            listener.onRequestSuccess(result);
    }

    private String getPostDataString(ArrayList<Parameter> parameters) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Parameter parameter : parameters) {
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(parameter.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(parameter.getValue(), "UTF-8"));
        }
        return result.toString();
    }

    private String backgroundProcess(){

        if (!InternetDetector.isOnline(context)) {
            error = ErrorType.NETWORK_ERROR;
            return null;
        }
        String response = "";
        BufferedReader br;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(web_url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(15000);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setChunkedStreamingMode(0);
            if (type == ServerRequest.POST)
                connection.setRequestMethod("POST");
            else
                connection.setRequestMethod("Get");
            connection.connect();

            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(parameters));
            writer.flush();
            writer.close();
            os.close();
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
                br.close();
            }
            return response;
        }
        catch (EOFException e){
            if(EOFEXCEPTION_MAX_ATTEMPT > EOFEXCEPTION_RETRY_ATTEMPT) {
                EOFEXCEPTION_RETRY_ATTEMPT++;
                backgroundProcess();
            }
            e.printStackTrace();
        }catch (ConnectException e) {
            e.printStackTrace();
            error = ErrorType.SERVER_ERROR;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            error = ErrorType.SERVER_ERROR;
        }
        catch (IOException e) {
            e.printStackTrace();
            error = ErrorType.SERVER_ERROR;
        }
        finally {
            connection.disconnect();
        }
        return null;
    }
}
