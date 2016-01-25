package com.mobellotec.cardbiz.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.mobellotec.cardbiz.Model.Contact;
import com.mobellotec.cardbiz.R;
import com.mobellotec.cardbiz.Utility.Constants;
import com.mobellotec.cardbiz.Utility.DBHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by MobelloTech on 12-08-2015.
 */
public class ContactAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private Context context;
    public ArrayList<Contact> contacts;
    private LayoutInflater inflater;
    private TextView invites;
    private DBHelper dbHelper;

    public ContactAdapter(Context context, TextView invites) {
        dbHelper = new DBHelper(context);
        this.contacts = dbHelper.getContact();
        this.context = context;
        this.invites = invites;
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Object getItem(int position) {
        return contacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.contact_row, parent, false);
        } else {
            view = convertView;
        }
        viewHolder.name = (TextView) view.findViewById(R.id.name);
        viewHolder.checkBox = (CheckBox) view.findViewById(R.id.checkbox);
        viewHolder.name.setText(contacts.get(position).getName());
        viewHolder.checkBox.setChecked(contacts.get(position).getIsSelected() == 1);

        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contacts.get(position).getIsSelected() == 1) {
                    contacts.get(position).setIsSelected(0);
                    dbHelper.updateContactSelected(contacts.get(position).getId(),0);
                }
                else {
                    contacts.get(position).setIsSelected(1);
                    dbHelper.updateContactSelected(contacts.get(position).getId(),1);
                }
                invites.setText("Invite(" + dbHelper.getSelectedContact().size() + ")");
                notifyDataSetChanged();
            }
        });
        return view;
    }

    @Override
    public View getHeaderView(int position, View view, ViewGroup viewGroup) {
        HeaderViewHolder holder;
        if (view == null) {
            holder = new HeaderViewHolder();
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.header_item, viewGroup, false);
            holder.txtHeader = (TextView) view.findViewById(R.id.txt_header);
            view.setTag(holder);
        } else {
            holder = (HeaderViewHolder) view.getTag();
        }
        CharSequence headerChar = contacts.get(position).getName().subSequence(0, 1);
        holder.txtHeader.setText(headerChar + " (" + getHeaderCount(headerChar.toString()) + ")");
        return view;
    }

    private String getHeaderCount(String headerChar) {
        int count = 0;
        for (int i = 0; i < contacts.size(); i++) {
            if (String.valueOf(contacts.get(i).getName().charAt(0)).equalsIgnoreCase(headerChar)) {
                count++;
            }
        }
        return String.valueOf(count);
    }

    @Override
    public long getHeaderId(int position) {
        return contacts.get(position).getName().subSequence(0, 1).charAt(0);
    }


    public class ViewHolder {
        CheckBox checkBox;
        TextView name;
    }

    public class HeaderViewHolder {
        TextView txtHeader;
    }
}
