package com.mobellotec.cardbiz.Utility;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sai Sheshan on 20-Aug-15.
 */
public class MultipleImageDownloader extends AsyncTask<Void, Void, ArrayList<String>> {

    private Context context;
    private ImageLoaderListener listener;
    private List<String> url;
    private ArrayList<String> fileName = new ArrayList<String>();
    private ArrayList<String> filePath = new ArrayList<String>();
    private ArrayList<File> file = new ArrayList<File>();


    public MultipleImageDownloader(Context context, ArrayList<String> url, ImageLoaderListener listener) {
        this.context = context;
        this.listener = listener;
        this.url = url;
    }

    public interface ImageLoaderListener {
        public void onSuccess(ArrayList<String> filePath);
    }

    @Override
    protected ArrayList<String> doInBackground(Void... params) {
        try {
            if (!Helper.sdCardRoot.exists()) {
                Helper.sdCardRoot.mkdir();
            }
            for (int i = 0; i < url.size(); i++) {
                fileName.add(Helper.getFileNameFromUrl(url.get(i)));
                file.add(new File(Helper.sdCardRoot + "/" + fileName.get(i)));
                if (file.get(i).exists()) {
                    filePath.add(file.get(i).getAbsolutePath());
                } else {
                    URL imageUrl = new URL(url.get(i));
                    HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestProperty("Content-Length", String.valueOf(url.get(i).length()));
                    OutputStream os = connection.getOutputStream();
                    os.write(url.get(i).getBytes());
                    InputStream in = connection.getInputStream();
                    FileOutputStream fileOutputStream = new FileOutputStream(file.get(i));
                    byte[] buffer = new byte[1024];
                    long total = 0;
                    int count = 0;
                    while ((count = in.read(buffer)) != -1) {
                        total += count;
                        fileOutputStream.write(buffer, 0, count);
                    }
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    filePath.add(file.get(i).getAbsolutePath());
                }
            }
            return filePath;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return filePath;
        } catch (IOException e) {
            e.printStackTrace();
            return filePath;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<String> filePath) {
        super.onPostExecute(filePath);
        listener.onSuccess(filePath);
    }
}
