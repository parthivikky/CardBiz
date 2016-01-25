package com.mobellotec.cardbiz.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobellotec.cardbiz.Model.Group;
import com.mobellotec.cardbiz.R;
import com.mobellotec.cardbiz.Utility.DBHelper;

import java.util.List;

/**
 * Created by Sai Sheshan on 27-Aug-15.
 */
public class GroupAdapter extends ArrayAdapter<Group> {

    private Context context;
    private List<Group> groupList;
    private LayoutInflater inflater;
    private DBHelper dbHelper;

    public GroupAdapter(Context context, List<Group> groupList) {
        super(context, R.layout.group_row, groupList);
        this.context = context;
        this.groupList = groupList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.group_row, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.getGroupLayout().setBackgroundResource(R.drawable.group_row_border);
        holder.getGroupImage().setImageResource(R.drawable.group_icon);
        holder.getGroupName().setTextColor(Color.parseColor("#000000"));
        holder.getGroupName().setText(groupList.get(position).getGroupName());
        return convertView;
    }

    public class ViewHolder {
        private View rootView;
        private TextView txtGroupName;
        private LinearLayout groupLayout;
        private ImageView groupImage;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
        }

        public TextView getGroupName() {
            if (txtGroupName == null)
                txtGroupName = (TextView) rootView.findViewById(R.id.txt_group_name);
            return txtGroupName;
        }

        public LinearLayout getGroupLayout() {
            if (groupLayout == null)
                groupLayout = (LinearLayout) rootView.findViewById(R.id.group_layout);
            return groupLayout;
        }

        public ImageView getGroupImage() {
            if (groupImage == null)
                groupImage = (ImageView) rootView.findViewById(R.id.img_group_icon);
            return groupImage;
        }



    }
}
