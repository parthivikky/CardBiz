package com.mobellotec.cardbiz.Adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mobellotec.cardbiz.R;

/**
 * Created by MobelloTech on 14-07-2015.
 */
public class TutotialAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private int[] drawables;

    public TutotialAdapter(Context context, int[] drawables) {
        this.context = context;
        this.drawables = drawables;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return drawables.length;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ViewHolder holder = new ViewHolder();
        holder.imageView = (ImageView) layoutInflater.inflate(R.layout.row_tutorial,container,false);
        holder.imageView.setImageResource(drawables[position]);
        container.addView(holder.imageView);
        return holder.imageView;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    private class ViewHolder{
        ImageView imageView;
    }
}
