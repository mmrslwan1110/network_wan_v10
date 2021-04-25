package com.example.network_wan_v10.ui.gallery.utils;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by xuhuan
 */
public class ProgressUtils {

    private static ProgressDialog progressDialog;

    public static void showProgress(Context context,String msg){
        progressDialog = ProgressDialog.show(context,null,msg,false,true);
    }

    public static void dismissProgress(){
        if(progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

}
