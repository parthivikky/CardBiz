package com.mobellotec.cardbiz.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobellotec.cardbiz.R;
import com.mobellotec.cardbiz.Utility.Helper;

import java.util.ArrayList;

/**
 * Created by Sai Sheshan on 01-Sep-15.
 */
public class CustomPagerAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<String> photoLists;
    private LayoutInflater inflater;

    public CustomPagerAdapter(Context context, ArrayList<String> photoLists) {
        this.context = context;
        this.photoLists = photoLists;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return photoLists.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((FrameLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = inflater.inflate(R.layout.pager_item, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.img_photo_view);
        TextView textView = (TextView) view.findViewById(R.id.no_card);
        if (!TextUtils.isEmpty(photoLists.get(position))) {
//            Drawable d = Helper.getBackgroundImageFromStorage(context, photoLists.get(position));
            if (Helper.rotateBitmap(context, photoLists.get(position)) != null)
                imageView.setImageBitmap(Helper.rotateBitmap(context, photoLists.get(position)));
            else
                textView.setVisibility(View.VISIBLE);
        } else
            textView.setVisibility(View.VISIBLE);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
