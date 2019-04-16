package com.yangyuan.wififileshareNio.UI;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yangyuan.wififileshareNio.Base.BaseActivity;
import com.yangyuan.wififileshareNio.R;
import com.yangyuan.wififileshareNio.Trans.SendService;
import com.yangyuan.wififileshareNio.UI.UIUtils.CircleImageView;
import com.yangyuan.wififileshareNio.Utils.ApNameUtil;
import com.yangyuan.wififileshareNio.bean.ApNameInfo;
import com.yangyuan.wififileshareNio.config.AppConfig;
import com.yangyuan.wififileshareNio.sendReciver.ConnectToTargetWifiReciver;
import com.yangyuan.wififileshareNio.sendReciver.ScanReciverResultReciver;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by yangy on 2017/3/1.
 */

public class FindSharingActivity extends BaseActivity implements View.OnClickListener, ScanReciverResultReciver.OnScanReciverResultAvilableListener, ConnectToTargetWifiReciver.OnConnectToTargetWifiListener
{

    private LinearLayout[] layouts = new LinearLayout[10];
    private TextView infoText = null;
    private View scanResultView;
    private int hasAddCount = 0;
    private ScanReciverResultReciver scanReciverResultReciver = new ScanReciverResultReciver();
    private ConnectToTargetWifiReciver connectToTargetWifiReciver = new ConnectToTargetWifiReciver();


    private SendService.SendActionBinder binder;
    private ServiceConnection serviceConnection;
    private ApNameInfo info;
    private boolean hasReSize = false;

    private static final int MSG_SCAN_AP = 1;
    private static final int MSG_ENABLE_VIEW = 2;
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if (msg.what == MSG_SCAN_AP)
            {
                binder.scanAP();//扫描热点
            } else if (msg.what == MSG_ENABLE_VIEW)
            {
                if (scanResultView != null)
                    scanResultView.setEnabled(true);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_reciver);
        initView();
        scanReciverResultReciver.setOnScanReciverResultAvilableListener(this);
        scanReciverResultReciver.registerSelf();
        connectToTargetWifiReciver.setOnConnectToTargetWifiListener(this);
        connectToTargetWifiReciver.registerSelf();
        serviceConnection = new ServiceConnection()
        {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder binder)
            {
                FindSharingActivity.this.binder = ((SendService.SendActionBinder) binder);
                FindSharingActivity.this.binder.preparedTranSend();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName)
            {
            }
        };
        binderService();
    }

    private void initView()
    {
        scanResultView = findViewById(R.id.scanResult);


        layouts[0] = null;
        layouts[1] = (LinearLayout) scanResultView.findViewById(R.id.v1);
        layouts[2] = (LinearLayout) scanResultView.findViewById(R.id.v2);
        layouts[3] = (LinearLayout) scanResultView.findViewById(R.id.v3);
        layouts[4] = (LinearLayout) scanResultView.findViewById(R.id.v4);
        layouts[5] = null;
        layouts[6] = (LinearLayout) scanResultView.findViewById(R.id.v6);
        layouts[7] = (LinearLayout) scanResultView.findViewById(R.id.v7);
        layouts[8] = (LinearLayout) scanResultView.findViewById(R.id.v8);
        layouts[9] = (LinearLayout) scanResultView.findViewById(R.id.v9);
        infoText = (TextView) findViewById(R.id.tv_info);
        infoText.setText(R.string.initing);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        ((TextView) findViewById(R.id.tv_title)).setText(R.string.choseRevicer);

        ((CircleImageView)findViewById(R.id.iv_photo)).setImageResource(AppConfig.getPhotoResorce());
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && !hasReSize)
        {
            hasReSize = true;
            View view = findViewById(R.id.scanResult);
            int width = view.getWidth();
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.height = (int) (width * 1.1);
            view.setLayoutParams(params);
        }
    }

    @Override
    protected void onDestroy()
    {
        connectToTargetWifiReciver.unRegisterSelf();
        scanReciverResultReciver.unRegisterSelf();
        unbindService(serviceConnection);
        super.onDestroy();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onStop()
    {
        scanReciverResultReciver.unRegisterSelf();
        super.onStop();
    }

    private void clearItem(ArrayList<ApNameInfo> scanInfo)
    {
        for (int i = 1; i < 10; i++)
        {
            if (layouts[i] == null)
                continue;
            int count = layouts[i].getChildCount();
            if (count <= 0)
                continue;
            ApNameInfo info = (ApNameInfo) layouts[i].getChildAt(0).getTag();
            if (scanInfo.contains(info))
            {
                scanInfo.remove(info);
            } else
            {
                layouts[i].removeAllViews();
                hasAddCount--;
            }
        }
    }

    private void addItem(ApNameInfo info)
    {
        if (hasAddCount >= 8)
            return;
        int max = 9;
        int min = 1;
        Random random = new Random();
        int i = random.nextInt(max) % (max - min + 1) + min;
        while (layouts[i] == null || layouts[i].getChildCount() > 0)
        {
            i = random.nextInt(max) % (max - min + 1) + min;
        }
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.scan_result_item, null);
        layouts[i].addView(view);
        ((ImageView) view.findViewById(R.id.ivPhoto)).setImageResource(AppConfig.getPhotoResorce(info.getPhotoId()));
        ((TextView) view.findViewById(R.id.tvName)).setText(info.getName());
        view.setOnClickListener(this);
        view.setTag(info);
        hasAddCount++;
    }

    @Override
    public void onClick(View v)
    {
        info = (ApNameInfo) v.getTag();
        binder.connectionSSID(ApNameUtil.encodeApName(info));
        infoText.setText(String.format(getString(R.string.linking), info.getName()));
        scanResultView.setEnabled(false);
        handler.sendEmptyMessageDelayed(MSG_ENABLE_VIEW, 8000);
    }


    @Override
    public void onScanReciverResultAviable(ArrayList<ApNameInfo> infos)
    {
        if (scanResultView.isEnabled())
            infoText.setText(R.string.scaning);//只有enable的时候才可以改变
        if (hasAddCount > 0)
            clearItem(infos);

        if (infos.size() > 0)
        {
            for (int i = 0; i < infos.size(); i++)
            {

                ApNameInfo info = infos.get(i);
                System.out.println(info.getName());
                addItem(info);
            }
        }
        handler.removeMessages(MSG_SCAN_AP);
        handler.sendEmptyMessageDelayed(MSG_SCAN_AP, 5000);//5秒新数据未到达就进行一次扫描
    }

    private void binderService()
    {
        Intent intent = new Intent(this, SendService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    //连接目标wifi
    @Override
    public void onConnectToTargetWifi(String ssid)
    {
        startService(new Intent(this, SendService.class));
        Intent intent = new Intent(this, ShowShareFileInfoActivity.class);
        startActivity(intent);
        scanReciverResultReciver.unRegisterSelf();
        handler.removeMessages(MSG_SCAN_AP);
        this.finish();
    }
    private String intToIp(int i)
    {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
    }


}
