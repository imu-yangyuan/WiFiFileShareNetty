package com.yangyuan.wififileshareNio.sendReciver;

import android.content.Context;
import android.content.Intent;

import com.yangyuan.wififileshareNio.Base.BaseReciver;
import com.yangyuan.wififileshareNio.BaseApplication;


/***
 * 当连接到制定的WIFI热点后，发送服务会发送出此广播
 */
public class ConnectToTargetWifiReciver extends BaseReciver
{
	private static final String INTENT_FILTER = ConnectToTargetWifiReciver.class.getName();
	private OnConnectToTargetWifiListener onConnectToTargetWifiListener;

	public ConnectToTargetWifiReciver()
	{
		super(INTENT_FILTER);
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		if(INTENT_FILTER.equals(intent.getAction()))
		{
			if(onConnectToTargetWifiListener != null)
			{
				String ssid = intent.getStringExtra("ssid");
				onConnectToTargetWifiListener.onConnectToTargetWifi(ssid);
			}
		}
	}

	public static void sendBroadcast(String ssid)
	{
		Intent intent = new Intent(INTENT_FILTER);
		intent.putExtra("ssid", ssid);
		BaseApplication.getInstance().sendBroadcast(intent);
	}

	public void setOnConnectToTargetWifiListener(OnConnectToTargetWifiListener onConnectToTargetWifiListener)
	{
		this.onConnectToTargetWifiListener = onConnectToTargetWifiListener;
	}

	public interface OnConnectToTargetWifiListener
	{
		void onConnectToTargetWifi(String ssid);
	}
}
