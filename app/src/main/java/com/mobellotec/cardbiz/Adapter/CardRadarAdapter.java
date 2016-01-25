package com.mobellotec.cardbiz.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobellotec.cardbiz.Model.CardRadar;
import com.mobellotec.cardbiz.R;

import java.util.List;

/**
 * Created by MobelloTech on 12-08-2015.
 */
public class CardRadarAdapter extends ArrayAdapter<CardRadar> {

    private Context context;
    private List<CardRadar> cardRadars;
    private LayoutInflater inflater;

    public CardRadarAdapter(Context context, List<CardRadar> cardRadars) {
        super(context, R.layout.card_radar_row, cardRadars);
        this.cardRadars = cardRadars;
        this.context = context;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.card_radar_row, parent, false);
        } else {
            view = convertView;
        }
        viewHolder.name = (TextView) view.findViewById(R.id.name);
        viewHolder.photo = (ImageView) view.findViewById(R.id.photo);
//        viewHolder.name.setText(cardRadars.get(position).getgeme());
        return view;
    }


    public class ViewHolder {
        ImageView photo;
        TextView name;
    }

}
