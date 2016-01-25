package com.mobellotec.cardbiz.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobellotec.cardbiz.Model.Template;
import com.mobellotec.cardbiz.R;
import com.mobellotec.cardbiz.Utility.Helper;
import com.mobellotec.cardbiz.Utility.TemplateImageDownloader;

import java.io.File;
import java.util.List;

/**
 * Created by MobelloTech on 11-08-2015.
 */
public class FreeTemplateAdapter extends ArrayAdapter<Template> {

    private List<Template> templateList;
    private Context context;
    private String strImageUrl, strFrontImage, strBackImage, strName, strPrice, strFilePath;
    private applyTemplateListener listener;

    public interface applyTemplateListener {
        void onApplyTemplateListener(int position);
    }

    public void setApplyTemplateListener(applyTemplateListener listener) {
        this.listener = listener;
    }

    public FreeTemplateAdapter(Context context, List<Template> templateList) {
        super(context, R.layout.free_template_row, templateList);
        this.context = context;
        this.templateList = templateList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.free_template_row, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        try {
            strFilePath = templateList.get(position).getFilePath();
            strFrontImage = templateList.get(position).getFrontModel();
            strBackImage = templateList.get(position).getBackModel();
            strName = templateList.get(position).getName();
            strPrice = templateList.get(position).getPrice();
            viewHolder.getName().setText(strName);
//            viewHolder.getPrice().setText(strPrice);
            if (strFrontImage.length() > 0) {
                strImageUrl = Helper.removeBackslashChar(strFrontImage);
                String fileName = Helper.getFileNameFromUrl(strImageUrl);
                File file = new File(Helper.sdCardRoot + "/" + fileName);
                if (file.exists()) {
                    Drawable d = Helper.getBackgroundImageFromStorage(context, file.getAbsolutePath());
                    viewHolder.getImageView().setBackground(d);
                    viewHolder.getProgressBar().setVisibility(View.GONE);
                } else {
                    new TemplateImageDownloader(context, strImageUrl, fileName, viewHolder.getImageView(), viewHolder.getProgressBar(), new TemplateImageDownloader.ImageLoaderListener() {
                        @Override
                        public void onSuccess(String filePath, ImageView imageView, ProgressBar progressBar) {
                            Drawable d = Helper.getBackgroundImageFromStorage(context, filePath);
                            viewHolder.getImageView().setBackground(d);
                            viewHolder.getProgressBar().setVisibility(View.GONE);
                            notifyDataSetChanged();
                        }
                    }).execute();
                }
            }
            /*viewHolder.getPrice().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onApplyTemplateListener(position);
                    }
                }
            });*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    public class ViewHolder {
        private ImageView imageView;
        private TextView name, price;
        private View view;
        private ProgressBar progressBar;

        public ViewHolder(View view) {
            this.view = view;
        }

        public ImageView getImageView() {
            if (imageView == null)
                imageView = (ImageView) view.findViewById(R.id.imageView);
            return imageView;
        }

        public TextView getName() {
            if (name == null)
                name = (TextView) view.findViewById(R.id.template_name);
            return name;
        }

        /*public TextView getPrice() {
            if (price == null)
                price = (TextView) view.findViewById(R.id.price);
            return price;
        }*/

        public ProgressBar getProgressBar() {
            if (progressBar == null)
                progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
            return progressBar;
        }
    }
}
