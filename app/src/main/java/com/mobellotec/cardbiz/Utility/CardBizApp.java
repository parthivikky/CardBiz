package com.mobellotec.cardbiz.Utility;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.mobellotec.cardbiz.R;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

/**
 * Created by Parthi on 29-Dec-15.
 */
@ReportsCrashes(
        customReportContent = { ReportField.BRAND,ReportField.PHONE_MODEL,ReportField.ANDROID_VERSION,
                ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME,  ReportField.STACK_TRACE,
                ReportField.CRASH_CONFIGURATION, ReportField.USER_CRASH_DATE},
        reportType = HttpSender.Type.FORM,
        httpMethod = HttpSender.Method.POST,
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_report_message)
public class CardBizApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
