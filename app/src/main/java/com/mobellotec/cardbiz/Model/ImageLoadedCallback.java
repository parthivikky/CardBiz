package com.mobellotec.cardbiz.Model;

import android.view.View;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;

/**
 * Created by Rajesh on 06-Aug-15.
 */
public class ImageLoadedCallback implements Callback {


    private ProgressBar progressBar;
    public ImageLoadedCallback(ProgressBar progressBar) {
        this.progressBar=progressBar;
    }

    @Override
    public void onSuccess() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onError() {
        progressBar.setVisibility(View.GONE);
    }
}
