package com.mobellotec.cardbiz.Utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.mobellotec.cardbiz.Model.Parameter;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by MobelloTech on 27-07-2015.
 */
public class FileServerRequest extends AsyncTask<Void, Void, String> {

    public static int POST = 1;

    public static int GET = 2;

    private int type;

    private String web_url;

    private ArrayList<Parameter> parameters = new ArrayList<>();

    private ArrayList<Parameter> filePaths = new ArrayList<>();

    private FileRequestListener listener;

    private ErrorType error;

    private Context context;


    public interface FileRequestListener {
        public void onRequestSuccess(String result);

        public void onRequestError(ErrorType error);
    }

    public FileServerRequest(Context context, int type, String url, ArrayList<Parameter> parameters, ArrayList<Parameter> filePaths, FileRequestListener listener) {
        this.context = context;
        this.type = type;
        this.parameters = parameters;
        this.filePaths = filePaths;
        this.listener = listener;
        this.web_url = Constants.BASE_URL + url;
    }


    @Override
    protected String doInBackground(Void... params) {

        if (!InternetDetector.isOnline(context)) {
            error = ErrorType.NETWORK_ERROR;
            return null;
        }

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        String lineStart = twoHyphens + boundary + lineEnd;
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        int serverResponseCode = 0;
        Bitmap mIcon11 = null;
        String response = "";
        try {
            URL url = new URL(web_url);

            // Open a HTTP  connection to  the URL
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept-Encoding", "");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            for (int i = 0; i < parameters.size(); i++) {
                conn.setRequestProperty(parameters.get(i).getKey(), parameters.get(i).getValue());
            }

            for (int i = 0; i < filePaths.size(); i++) {
                if (filePaths.get(i).getValue().length() > 0) {
                    conn.setRequestProperty(filePaths.get(i).getKey(), CommonClass.getFileNameFromPath(filePaths.get(i).getValue()));
                } else {
                    conn.setRequestProperty(filePaths.get(i).getKey(), filePaths.get(i).getValue());
                }
            }

            dos = new DataOutputStream(conn.getOutputStream());
            for (int i = 0; i < parameters.size(); i++) {
                dos.writeBytes(lineStart);
                dos.writeBytes("Content-Disposition: form-data; name=\"" + parameters.get(i).getKey() + "\"" + lineEnd + lineEnd
                        + parameters.get(i).getValue() + lineEnd);
                //dos.writeBytes(lineEnd);
            }
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            for (int i = 0; i < filePaths.size(); i++) {
                if (filePaths.get(i).getValue().length() > 0) {
                    dos.writeBytes(lineStart);
                    dos.writeBytes("Content-Disposition: form-data; name=\"" + filePaths.get(i).getKey() + "\";filename=\"" + CommonClass.getFileNameFromPath(filePaths.get(i).getValue()) + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);

                    File file = new File(filePaths.get(i).getValue());
                    FileInputStream fileInputStream = new FileInputStream(file);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }
                    //close the streams //
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(lineStart);
                    fileInputStream.close();

                }
            }

            // Responses from the server (code and message)
            serverResponseCode = conn.getResponseCode();

            String serverResponseMessage = conn.getResponseMessage();

            if (serverResponseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            }
            dos.flush();
            dos.close();
            conn.disconnect();
            return response;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            error = ErrorType.SERVER_ERROR;
        } catch (IOException e) {
            e.printStackTrace();
            error = ErrorType.SERVER_ERROR;
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result == null)
            listener.onRequestError(error);
        else
            listener.onRequestSuccess(result);
    }
}
