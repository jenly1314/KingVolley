package com.king.http;

import com.android.volley.NetworkResponse;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

/**
 * ByteArrayLoadControler implements Volley Listener & ErrorListener
 * @author Jenly
 * @date 2015-10-9
 */
class ByteArrayLoadControler extends AbsLoadControler implements
		Listener<NetworkResponse>, ErrorListener {

	private LoadListener mOnLoadListener;

	private int mAction = 0;

	public ByteArrayLoadControler(LoadListener requestListener, int actionId) {
		this.mOnLoadListener = requestListener;
		this.mAction = actionId;
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		String errorMsg = null;
		if (error.getMessage() != null) {
			errorMsg = error.getMessage();
		} else {
			try {
				errorMsg = "Server Response Error ("
						+ error.networkResponse.statusCode + ")";
			} catch (Exception e) {
				errorMsg = "Server Response Error";
			}
		}
		this.mOnLoadListener.onError(errorMsg, getOriginUrl(), this.mAction);
	}

	@Override
	public void onResponse(NetworkResponse response) {
		this.mOnLoadListener.onSuccess(response.data, response.headers,
				getOriginUrl(), this.mAction);
	}
}