package com.mobellotec.cardbiz.Utility;

import android.content.Context;

import com.mobellotec.cardbiz.Model.ApiStatus;

import org.acra.ACRA;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class BugReportSender implements ReportSender {

    private Context context;

    public BugReportSender(Context context, Exception errorLog) {
        this.context = context;
        ACRA.getErrorReporter().handleException(errorLog);
    }

    @Override
    public void send(Context context, CrashReportData errorContent) throws ReportSenderException {
        try {
            bugReport(errorContent.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bugReport(String crashReport) {
        RetrofitApi.ApiInterface apiInterface = RetrofitApi.getApiInterfaceInstance();
        Call<ApiStatus> reportCall = apiInterface.sendBugReport(
                AppPreference.getString(context, AppPreference.USER_ID),
                AppPreference.getString(context, AppPreference.FIRST_NAME) + " " + AppPreference.getString(context, AppPreference.LAST_NAME),
                crashReport, "Android");
        reportCall.enqueue(new Callback<ApiStatus>() {
            @Override
            public void onResponse(Response<ApiStatus> response, Retrofit retrofit) {
                if(response.isSuccess()){
                    ApiStatus apiStatus = response.body();
                    if(apiStatus.getStatus().equalsIgnoreCase(Constants.STATUS_SUCCESS)){
                        CommonClass.showMessageToast(context,"Bug report sent successfully");
                    }
                    else{
                        CommonClass.showMessageToast(context,"Bug report sending failed");
                    }
                }
                else
                    CommonClass.showMessageToast(context,"Bug report sending failed");
            }

            @Override
            public void onFailure(Throwable t) {
                CommonClass.showMessageToast(context,"Bug report sending failed");
            }
        });
    }
}
