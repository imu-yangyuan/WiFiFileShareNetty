package com.yangyuan.wififileshareNio.sendReciver;

import android.content.Context;
import android.content.Intent;

import com.yangyuan.wififileshareNio.Base.BaseReciver;
import com.yangyuan.wififileshareNio.BaseApplication;

/**
 * Created by Cfun on 2015/4/30.
 */

/***
 * 当文件被选择或者被取消时，Fragment会发送此广播
 */
public class FileChoseChangedReciver extends BaseReciver
{
	private OnFileChoseChangedListener onFileChoseChangedListener;

	public FileChoseChangedReciver()
	{
		super(FileChoseChangedReciver.class.getName());
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		if (intent.getAction().equals(INTENT_FILTER))
		{
			if (onFileChoseChangedListener != null)
			{
				boolean increse = intent.getBooleanExtra("increse", false);
				onFileChoseChangedListener.onFileChoseChang(increse);
			}

		}
	}

	public interface OnFileChoseChangedListener
	{
		void onFileChoseChang(boolean increse);
	}

	public void setOnFileChoseChangedListener(OnFileChoseChangedListener onFileChoseChangedListener)
	{
		this.onFileChoseChangedListener = onFileChoseChangedListener;
	}

	public static void sendBroadcast(boolean increse)
	{
		Intent intent = new Intent(FileChoseChangedReciver.class.getName());
		intent.putExtra("increse", increse);
		//BaseApplication把Intent.increse传过去
		BaseApplication.getInstance().sendBroadcast(intent);
	}
}
