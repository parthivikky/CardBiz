package com.mobellotec.cardbiz.Utility;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobellotec.cardbiz.Model.Card;
import com.mobellotec.cardbiz.Model.Group;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Sai Sheshan on 18-Aug-15.
 */
public class Helper {
    public static JSONArray contactArray;
    public static ArrayList<Card> contactCardDetails;
    public static ArrayList<Card> contactTextDetails;
    public static List<Group> groupList;
    public static ArrayList<String> photoLists;
    public static String appLocalPath = Environment.getExternalStorageDirectory() + "/CardBiz";
    public static File sdCardRoot = new File(appLocalPath);
    public static List<Context> listActivity = new ArrayList<Context>();

    public static String removeNewLine(String value) {
        if (value.length() > 0) {
            value = value.replaceAll("[\n\r]", "");
        }
        return value;
    }

    public static String removeBackslashChar(String s) {
        if(s != null) {
            char c = '\\';
            StringBuffer r = new StringBuffer(s.length());
            r.setLength(s.length());
            int current = 0;
            for (int i = 0; i < s.length(); i++) {
                char cur = s.charAt(i);
                if (cur != c) r.setCharAt(current++, cur);
            }
            return r.toString();
        }
        return  null;
    }

    public static Drawable getBackgroundImage(Context context, String backgroundImage) {
        AssetManager manager = context.getResources().getAssets();
        Bitmap bitmap = null;

        try {
            InputStream inputStream = manager.open(backgroundImage);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeStream(inputStream, null, options);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    public static Drawable getBackgroundImageFromStorage(Context context, String backgroundImage) {
        BitmapDrawable bitmapDrawable = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(backgroundImage, options);
            if (context != null && bitmap != null)
                bitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bitmapDrawable;
    }

    public static boolean isNetworkAvailable(Context context) {
        if(context != null) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] info = manager.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static String getFileNameFromUrl(String value) {
        if (value.length() > 0)
            return value.substring(value.lastIndexOf('/') + 1);
        else
            return value;
    }

    public static void hideKeyBoard(View view, Context context) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static boolean isValidEmail(String emailAddress) {
        return !TextUtils.isEmpty(emailAddress) && Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (phone.length() < 8 || phone.length() > 13) {
            return false;
        } else {
            return true;
        }
    }

    public static Bitmap getBitmap(String backgroundImage) {
        Bitmap bitmap = null;
        File file = new File(backgroundImage);
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(file), null, o);

            //The new size we want to scale to
            final int REQUIRED_WIDTH = 300;
            final int REQUIRED_HIGHT = 190;
            //Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_WIDTH && o.outHeight / scale / 2 >= REQUIRED_HIGHT)
                scale *= 2;

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            bitmap = BitmapFactory.decodeStream(new FileInputStream(file), null, o2);
            /*BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeFile(backgroundImage, options);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Drawable convertBitmapToDrawable(Context context, Bitmap bitmap) {
        if (bitmap != null)
            return new BitmapDrawable(context.getResources(), bitmap);
        return null;
    }

    public static byte[] convertBitmapToByteArray(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream.toByteArray();
        }
        return null;
    }

    public static Bitmap convertByteArrayToBitmap(byte[] bytes) {
        if (bytes != null)
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return null;
    }

    public static Bitmap rotateBitmap(Context context, String imagePath) {
        try {
            Bitmap bmRotated = null;
            if(context != null && imagePath != null) {
                ExifInterface exif = new ExifInterface(imagePath);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);
                Matrix matrix = new Matrix();
                Bitmap d = new BitmapDrawable(context.getResources(), imagePath).getBitmap();
                if(d != null) {
                    int nh = (int) (d.getHeight() * (512.0 / d.getWidth()));
                    Bitmap bitmap = Bitmap.createScaledBitmap(d, 512, nh, true);
                    switch (orientation) {
                        case ExifInterface.ORIENTATION_NORMAL:
                            return bitmap;
                        case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                            matrix.setScale(-1, 1);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            matrix.setRotate(180);
                            break;
                        case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                            matrix.setRotate(180);
                            matrix.postScale(-1, 1);
                            break;
                        case ExifInterface.ORIENTATION_TRANSPOSE:
                            matrix.setRotate(90);
                            matrix.postScale(-1, 1);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            matrix.setRotate(90);
                            break;
                        case ExifInterface.ORIENTATION_TRANSVERSE:
                            matrix.setRotate(-90);
                            matrix.postScale(-1, 1);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            matrix.setRotate(-90);
                            break;
                        default:
                            return bitmap;
                    }
                    bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    bitmap.recycle();
                }
            }
            return bmRotated;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File getOutputMediaFile() {
        if (!Helper.sdCardRoot.exists()) {
            Helper.sdCardRoot.mkdir();
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile = new File(Helper.sdCardRoot.getAbsolutePath(),
                "IMG_" + timeStamp + ".jpeg");
        return mediaFile;
    }

    public static String removePlusFromMobile(String mobile) {
        if (mobile != null || !TextUtils.isEmpty(mobile)) {
            String[] arrMobile = mobile.split("\\+");
            if (arrMobile.length > 0)
                return arrMobile[1];
        }
        return mobile;
    }

    public static String getCountryCode(Context context){
        if(context != null) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String countryIso = telephonyManager.getSimCountryIso();
            return Iso2Phone.getPhone(countryIso);
        }
        return  null;
    }
}
