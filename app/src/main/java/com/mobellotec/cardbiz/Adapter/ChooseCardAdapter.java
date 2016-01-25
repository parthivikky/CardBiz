package com.mobellotec.cardbiz.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobellotec.cardbiz.Activity.OwnCardViewActivity;
import com.mobellotec.cardbiz.Model.Card;
import com.mobellotec.cardbiz.Model.ChooseCard;
import com.mobellotec.cardbiz.Model.GroupInfo;
import com.mobellotec.cardbiz.R;
import com.mobellotec.cardbiz.Utility.AppPreference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MobelloTech on 12-08-2015.
 */
public class ChooseCardAdapter extends ArrayAdapter<Card> {

    private Context context;
    private ArrayList<Card> cardInfo;
    private LayoutInflater inflater;

    public ChooseCardAdapter(Context context, ArrayList<Card> cardInfo) {
        super(context, R.layout.group_info_row, cardInfo);
        this.cardInfo = cardInfo;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.choose_card_row, parent, false);
        } else {
            view = convertView;
        }
        viewHolder.txtName = (TextView) view.findViewById(R.id.txt_name);
        viewHolder.imgInfo = (ImageView) view.findViewById(R.id.img_info);
        viewHolder.txtName.setText("Card " + (position + 1));

        viewHolder.imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OwnCardViewActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("card_id", cardInfo.get(position).getCardId());
                intent.putExtra("user_id", AppPreference.getString(context,AppPreference.USER_ID));
                context.startActivity(intent);
            }
        });
        return view;
    }

    public class ViewHolder {
        TextView txtName;
        ImageView imgInfo;
    }

}
