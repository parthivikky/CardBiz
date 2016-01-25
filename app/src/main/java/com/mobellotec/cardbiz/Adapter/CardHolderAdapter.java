package com.mobellotec.cardbiz.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobellotec.cardbiz.Model.GroupInfo;
import com.mobellotec.cardbiz.R;

import java.util.ArrayList;

/**
 * Created by Parthi on 20-Nov-15.
 */
public class CardHolderAdapter extends BaseAdapter {

    public ArrayList<GroupInfo> sharedContacts = new ArrayList<>();
    private Context context;

    public CardHolderAdapter(ArrayList<GroupInfo> sharedContacts, Context context) {
        this.sharedContacts = sharedContacts;
        this.context = context;
    }

    @Override
    public int getCount() {
        return sharedContacts.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        View view;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.card_holder_row, parent, false);
        }else{
            view = convertView;
        }
        viewHolder.personName = (TextView)view.findViewById(R.id.personName);
        viewHolder.companyName = (TextView)view.findViewById(R.id.companyName);
        viewHolder.personName.setText(sharedContacts.get(position).getName());
        viewHolder.companyName.setText(sharedContacts.get(position).getCompanyName());
        return view;
    }

    private class ViewHolder{
        TextView personName,companyName;
    }
}
