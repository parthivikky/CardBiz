package com.mobellotec.cardbiz.Utility;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Patterns;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by MobelloTech on 13-07-2015.
 */
public class CommonClass {

    private static int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    public static ProgressDialog progressDialog;
    private static ProgressHUD progressHUD;

    public static boolean isValidEmail(String email) {
        if (email == null)
            return false;
        else
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static final String convertMd5(final String key) {
        try {
            if (key != null) {
                MessageDigest digest = MessageDigest.getInstance("MD5");
                digest.update(key.getBytes());
                byte messageDigest[] = digest.digest();

                StringBuffer hexString = new StringBuffer();
                for (int i = 0; i < messageDigest.length; i++) {
                    String h = Integer.toHexString(0xFF & messageDigest[i]);
                    while (h.length() < 2)
                        h = "0" + h;
                    hexString.append(h);
                }
                return hexString.toString();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getDeviceId(Activity context) {
        TelephonyManager telephonyManager = null;
        String deviceId = null;
        if (context != null) {
            telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.M){
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.READ_PHONE_STATE)) {

                    }else{
                        ActivityCompat.requestPermissions(context,
                                new String[]{Manifest.permission.READ_PHONE_STATE},
                                MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                        deviceId = telephonyManager.getDeviceId();
                    }
                }
            }else
                deviceId = telephonyManager.getDeviceId();
        }
        return deviceId;
    }

    public static void showProgress(Context context) {
        if (context != null) {
            progressHUD = ProgressHUD.show(context, "Loading...", true, true);
            progressHUD.setCancelable(false);
        }
    }

    public static void dismissProgress() {
        progressHUD.dismiss();
    }

    public static void showErrorToast(Context context, ErrorType errorType) {
        if (context != null)
            if (errorType == ErrorType.NETWORK_ERROR)
                Toast.makeText(context, "Please check your internet connection.", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context, "Server Problem. Please try again.", Toast.LENGTH_LONG).show();
    }

    public static void showMessageToast(Context context, String msg) {
        if (context != null)
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void showMessageToast(Context context, int message) {
        if (context != null)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }


    public static String getFilenamefromPath(String path) {
        File f = new File(path);
        return f.getName();
    }

    public static String getFileNameFromPath(String path) {
        if (path.length() <= 0)
            return "";
        return path.substring(path.lastIndexOf("/") + 1);
    }

    public static byte[] getBytes(Bitmap bitmap) {
        if (bitmap == null)
            return null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    public static Bitmap getBitmapFromUrl(String userImage) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(userImage);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            InputStream inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static String getMimeType() {
        String type = null;
        String extension = "jpg";
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static String getFileExtensionFromUrl(String url) {
        int dotPos = url.lastIndexOf('.');
        if (0 <= dotPos) {
            return (url.substring(dotPos + 1)).toLowerCase();
        }
        return "";
    }

}
