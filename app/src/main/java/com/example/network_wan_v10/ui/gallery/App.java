package com.example.network_wan_v10.ui.gallery;

import android.app.Application;

import com.example.network_wan_v10.ui.gallery.utils.BluetoothUtils;
import com.example.network_wan_v10.ui.gallery.utils.CrashHandler;

import org.xutils.x;


/**
 * Created by lenovo on 2016/7/10 0010.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init(this);
        BluetoothUtils.getInstance(this);
        x.Ext.init(this);
    }
}
