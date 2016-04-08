package com.king.http;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.Map;

import android.app.Activity;

import com.king.http.RequestManager.RequestListener;
import com.king.utils.LogUtils;

/**
 * RequestListener Holder to avoid memory leak!
 * @author Jenly
 * @date 2015-10-9
 */
public class RequestListenerHolder implements LoadListener {

	private static final String CHARSET_UTF_8 = "UTF-8";

	private WeakReference<RequestListener> mRequestListenerRef;

	private RequestListener mRequestListener;

	public RequestListenerHolder(RequestListener requestListener) {
		if (requestListener instanceof Activity) {
			this.mRequestListenerRef = new WeakReference<RequestListener>(
					requestListener);
		} else {
			this.mRequestListener = requestListener;
		}
	}

	@Override
	public void onStart(String url) {
		if (mRequestListenerRef != null) {
			RequestListener requestListener = mRequestListenerRef.get();
			if (requestListener != null) {
				requestListener.onRequest(url);
				return;
			}
		}

		if (this.mRequestListener != null) {
			this.mRequestListener.onRequest(url);
		}
	}

	@Override
	public void onSuccess(byte[] data, Map<String, String> headers, String url,
			int actionId) {
		String parsed = null;
		try {
			parsed = new String(data, CHARSET_UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		LogUtils.i("Response:" + parsed);

		if (mRequestListenerRef != null) {
			RequestListener requestListener = mRequestListenerRef.get();
			if (requestListener != null) {
				requestListener.onSuccess(parsed, headers, url, actionId);
				return;
			}
		}

		if (this.mRequestListener != null) {
			this.mRequestListener.onSuccess(parsed, headers, url, actionId);
		}
	}

	@Override
	public void onError(String errorMsg, String url, int actionId) {
		LogUtils.w("onError:(actionId|0x"+Integer.toHexString(actionId)+") "+ errorMsg);
		if (mRequestListenerRef != null) {
			RequestListener requestListener = mRequestListenerRef.get();
			if (requestListener != null) {
				requestListener.onError(errorMsg, url, actionId);
				return;
			}
		}

		if (this.mRequestListener != null) {
			this.mRequestListener.onError(errorMsg, url, actionId);
		}
	}
}