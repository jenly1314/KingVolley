package com.king.http;

import java.io.UnsupportedEncodingException;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
/**
 * GsonRequest
 * @author Jenly
 * @date 2015-10-9
 */
public class GsonRequest<T> extends Request<T> {

	private final Listener<T> mListener;

	private Gson mGson;

	private Class<T> mClass;

	public GsonRequest(String url, Class<T> cls, Listener<T> listener,
			ErrorListener errorlistener) {
		this(Method.GET, url, cls, listener, errorlistener);
	}

	public GsonRequest(int method, String url, Class<T> cls,
			Listener<T> listener, ErrorListener errorlistener) {
		super(method, url, errorlistener);
		this.mListener = listener;
		this.mClass = cls;
		mGson = new Gson();
	}

	@Override
	protected void deliverResponse(T response) {
		this.mListener.onResponse(response);
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			String jsonString = new String(response.data,HttpHeaderParser.parseCharset(response.headers));
			return Response.success(mGson.fromJson(jsonString, mClass),HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (Exception e) {
			return Response.error(new ParseError(e));
		}
		
	}

}
