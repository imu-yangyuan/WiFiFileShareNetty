package com.yangyuan.wififileshareNio.reciver;

import android.content.Context;
import android.content.Intent;

import com.yangyuan.wififileshareNio.Base.BaseReciver;
import com.yangyuan.wififileshareNio.BaseApplication;


/***
 * 当程序通过APHelper执行了相应的开启或关闭AP的操作时，会发送此广播
 */
public class ApStateBroadcastReciver extends BaseReciver
{

	public static final int WIFI_AP_OPENING = 8;
	public static final int WIFI_AP_OPENED = 9;
	public static final int WIFI_AP_CLOSING = 10;
	public static final int WIFI_AP_CLOSED = 11;

	public static final int WIFI_AP_Operation_TimeOut = 12;

	private OnApStateChangListener apStateChangListener;

	public ApStateBroadcastReciver()
	{
		super(ApStateBroadcastReciver.class.getName());
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		if (apStateChangListener != null)
			switch (intent.getIntExtra("statu", 0))
			{
				case WIFI_AP_OPENING: apStateChangListener.onApOpening();
					break;
				case WIFI_AP_OPENED: apStateChangListener.onApOpened();
					break;
				case WIFI_AP_CLOSING: apStateChangListener.onApClosing();
					break;
				case WIFI_AP_CLOSED: apStateChangListener.onApClosed();
					break;
				default:
					break;
			}
	}

	public static void sendBroadcast(int state)
	{
		Intent intent = new Intent(ApStateBroadcastReciver.class.getName());
		intent.putExtra("statu", state);
		BaseApplication.getInstance().sendBroadcast(intent);
	}

	public void setApStateChangListener(OnApStateChangListener apStateChangListener)
	{
		this.apStateChangListener = apStateChangListener;
	}

	public interface OnApStateChangListener
	{
		void onApClosing();

		void onApOpening();

		void onApOpened();

		void onApClosed();
	}
}