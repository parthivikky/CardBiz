package com.mobellotec.cardbiz.Utility;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Sai Sheshan on 20-Aug-15.
 */
public class TemplatesDownload extends AsyncTask<Void, Void, String> {

    private Context context;
    private ImageLoaderListener listener;
    private String url, filePath, fileName;
    private File file;
    private FrameLayout frameLayout;
    private ProgressBar progressBar;


    public TemplatesDownload(Context context, String url, String fileName, FrameLayout frameLayout, ProgressBar progressBar, ImageLoaderListener listener) {
        this.context = context;
        this.listener = listener;
        this.fileName = fileName;
        this.url = url;
        this.frameLayout = frameLayout;
        this.progressBar = progressBar;
    }

    public TemplatesDownload(Context context, String fileName, String url, ImageLoaderListener listener) {
        this.context = context;
        this.fileName = fileName;
        this.url = url;
        this.listener = listener;
    }

    public interface ImageLoaderListener {
        void onSuccess(String filePath, FrameLayout frameLayout, ProgressBar progressBar);
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            if (!Helper.sdCardRoot.exists()) {
                Helper.sdCardRoot.mkdir();
            }
            URL imageUrl = new URL(url);
            fileName = Helper.getFileNameFromUrl(url);
            file = new File(Helper.sdCardRoot + "/" + fileName);
            if (file.exists()) {
                return filePath = file.getAbsolutePath();
            } else {
                HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Content-Length", String.valueOf(url.length()));
                OutputStream os = connection.getOutputStream();
                os.write(url.getBytes());
                InputStream in = connection.getInputStream();
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                long total = 0;
                int count = 0;
                while ((count = in.read(buffer)) != -1) {
                    total += count;
                    fileOutputStream.write(buffer, 0, count);
                }
                fileOutputStream.flush();
                fileOutputStream.close();
                return filePath = file.getAbsolutePath();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return filePath = null;
        } catch (IOException e) {
            e.printStackTrace();
            return filePath = null;
        }
    }

    @Override
    protected void onPostExecute(String filePath) {
        super.onPostExecute(filePath);
        listener.onSuccess(filePath, frameLayout, progressBar);
    }
}
