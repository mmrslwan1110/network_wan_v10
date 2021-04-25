package com.example.network_wan_v10.ui.home;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.network_wan_v10.MainActivity;
import com.example.network_wan_v10.R;

public class HomeFragment extends Fragment {


    private HomeViewModel homeViewModel;
    private LocationManager lm;
    private TextView tv_show;
    private String url = "https://restapi.amap.com/v3/staticmap?markers=mid,0xFF0000,W:116.37359,39.92437&key=ab4bb2ae722420d4df61cf29a6ceb838";
    private WebView webView;
    private ProgressDialog dialog;
    EvilTransform WGS84_GCJ02;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        tv_show = (TextView) root.findViewById(R.id.tv_show);
        lm = (LocationManager) getActivity(). getSystemService(Context.LOCATION_SERVICE);
        webView = (WebView) root.findViewById(R.id.webView);

        init();
        if (!isGpsAble(lm)) {
            Toast.makeText( getActivity(), "请打开Gps!", Toast.LENGTH_SHORT).show();
            openGps();
        }
        // 从gps获取最近的定位信息
        if (ActivityCompat.checkSelfPermission( getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        Location lc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        updateShow(lc);
        //设置间隔两秒获得一次gps定位信息
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 8, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // 当gps定位信息发生改变时,更新定位
                updateShow(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String provider) {
                // 当gpsLocationProvider可用时,更新定位
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                updateShow(lm.getLastKnownLocation(provider));
            }

            @Override
            public void onProviderDisabled(String s) {
                updateShow(null);
            }
        });


        return root;
    }


    // 定义更新显示的方法
    private void updateShow(Location location) {
        if (location != null) {
            StringBuilder sb = new StringBuilder();
            double Longitude, Latitude;
            double[] tude = {1, 2};
            String url = "https://restapi.amap.com/v3/staticmap?markers=mid,0xFF0000,", Longitudestr, Latitudestr;

            sb.append("当前gps位置定位信息:\n");
            sb.append("经度:" + location.getLongitude() + "\n");
            sb.append("维度:" + location.getLatitude() + "\n");
            sb.append("海拔:" + location.getAltitude() + "\n");
            sb.append("速度:" + location.getSpeed() + "\n");
            sb.append("方位:" + location.getBearing() + "\n");
            sb.append("时间:" + location.getTime() + "\n");
            sb.append("定位精度:" + location.getLongitude() + "\n");
            Longitude = Double.valueOf(location.getLongitude());
            Latitude = Double.valueOf(location.getLatitude());

            /*WGS84转GCJ-02*/
            tude = WGS84_GCJ02.transform(Latitude, Longitude);
            Latitude = tude[0];
            Longitude = tude[1];
            Longitudestr = String.valueOf(Longitude);
            Latitudestr = String.valueOf(Latitude);
            url = url + "W:" + Longitudestr + "," + Latitudestr + "&key=ab4bb2ae722420d4df61cf29a6ceb838";
            sb.append("GCJ-02:" + Latitude + "\n");
            sb.append("GCJ-02:" + Longitudestr + "\n");
            tv_show.setText(sb.toString());

            webView.loadUrl(url);


        } else
            tv_show.setText("未开启权限");
    }

    private boolean isGpsAble(LocationManager lm) {
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ? true : false;
    }

    // 打开设置界面让用户自己设置
    private void openGps() {
        Intent intent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
        startActivityForResult(intent, 0);
    }


    private void init() {
        // webView加载本地资源
        // webView.loadUrl("file:///android_asset/example.html");
        // webView加载web资源
        webView.loadUrl("https://restapi.amap.com/v3/staticmap?markers=mid,0xFF0000,W:116.37359,39.92437&key=ab4bb2ae722420d4df61cf29a6ceb838");
        // 覆盖WebView默认通过浏览器打开网页的行为，使网页可以在webView中打开
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 返回值是true时，控制网页在webView中打开，为false，则调用系统或第三方浏览器打开
                // return super.shouldOverrideUrlLoading(view, url);
                view.loadUrl(url);
                return true;
            }
            // WebViewClient帮助WebView处理一些页面的控制和请求通知
        });
        // 启用Javascript支持
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // WebView加载页面优先选择使用缓存加载
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        // 给用户显示网页加载的情况
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // newProgress 1-100之间的整数
                if (newProgress == 100) {
                    // 网页加载完毕，关闭进度对话框
                    closeDialog();
                } else {
                    // 网页加载中,打开进度对话框
                    openDialog(newProgress);
                }
            }

            private void closeDialog() {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                    dialog = null;
                }
            }

            private void openDialog(int newProgress) {
                if (dialog == null) {
                    dialog = new ProgressDialog(getActivity());
                    dialog.setTitle("正在加载");
                    dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    dialog.setProgress(newProgress);
                    dialog.show();
                } else {
                    dialog.setProgress(newProgress);
                }
            }
        });

    }





}