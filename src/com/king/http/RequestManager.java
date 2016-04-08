package com.king.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.Volley;
import com.king.utils.LogUtils;

/**
 * RequestManager
 * @author Jenly
 * @date 2015-10-9
 */
public class RequestManager {
	
	public static String DEFAULT_ENCODING = "UTF-8";
	
    public static final String URL_AND_PARA_SEPARATOR = "?";
    
    public static final String PARAMETERS_SEPARATOR   = "&";

	private static final int TIMEOUT_COUNT = 15 * 1000;

	private static final int RETRY_TIMES = 1;

	private volatile static RequestManager INSTANCE = null;

	private RequestQueue mRequestQueue = null;

	/**
	 * Request Result Callback
	 */
	public interface RequestListener {

		void onRequest(String url);

		void onSuccess(String response, Map<String, String> headers,
				String url, int actionId);

		void onError(String errorMsg, String url, int actionId);
	}

	private RequestManager() {
		
	}

	public void init(Context context) {
		this.mRequestQueue = Volley.newRequestQueue(context);
	}

	/**
	 * SingleTon
	 * 
	 * @return
	 */
	public static RequestManager getInstance() {
		if (null == INSTANCE) {
			synchronized (RequestManager.class) {
				if (null == INSTANCE) {
					INSTANCE = new RequestManager();
				}
			}
		}
		return INSTANCE;
	}
	
	/**
	 * 
	 * @param url 
	 * @param params
	 * @return
	 */
	public static String getParamsUrl(String url,Map<String,String> params){
		
		if(TextUtils.isEmpty(url) || null == params)
			return url;
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		for(Map.Entry<String, String> entry: params.entrySet()){
			list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			
		}
		
		if(list.size()>0){
			if(url.contains(URL_AND_PARA_SEPARATOR)){
				return new StringBuffer().append(url).append(PARAMETERS_SEPARATOR).append(URLEncodedUtils.format(list, DEFAULT_ENCODING)).toString();
			}
			return new StringBuffer().append(url).append(URL_AND_PARA_SEPARATOR).append(URLEncodedUtils.format(list, DEFAULT_ENCODING)).toString();
		}
		
		return url;
	}

	/**
	 * 
	 * @return
	 */
	public RequestQueue getRequestQueue() {
		return this.mRequestQueue;
	}

	/**
	 * default get method
	 * 
	 * @param url
	 * @param requestListener
	 * @param actionId
	 * @return
	 */
	public LoadControler get(String url, RequestListener requestListener,
			int actionId) {
		return this.get(url, requestListener, true, actionId);
	}
	
	/**
	 * 
	 * @param url
	 * @param requestListener
	 * @param shouldCache
	 * @param actionId
	 * @return
	 */
	public LoadControler get(String url, RequestListener requestListener,
			boolean shouldCache, int actionId) {
		return get(url, null, requestListener, shouldCache, actionId);
	}

	/**
	 * 
	 * @param url
	 * @param data
	 * @param requestListener
	 * @param actionId
	 * @return
	 */
	public LoadControler get(String url,Map<String,String> data, RequestListener requestListener,int actionId) {
		return get(url, data, requestListener, false, TIMEOUT_COUNT, RETRY_TIMES, actionId);
	}
	
	/**
	 * 
	 * @param url
	 * @param data
	 * @param requestListener
	 * @param shouldCache
	 * @param actionId
	 * @return
	 */
	public LoadControler get(String url,Map<String,String> data, RequestListener requestListener,
			boolean shouldCache, int actionId) {
		return get(url, data, requestListener, shouldCache, TIMEOUT_COUNT, RETRY_TIMES, actionId);
	}
	
	/**
	 * 
	 * @param url
	 * @param data
	 * @param requestListener
	 * @param shouldCache
	 * @param timeoutCount
	 * @param retryTimes
	 * @param actionId
	 * @return
	 */
	public LoadControler get(String url, Map<String,String> data,
			final RequestListener requestListener, boolean shouldCache,
			int timeoutCount, int retryTimes, int actionId) {
		return request(Method.GET, getParamsUrl(url, data), data, null, requestListener,
				shouldCache, timeoutCount, retryTimes, actionId);
	}

	

	/**
	 * default post method
	 * 
	 * @param url
	 * @param data
	 *            String, Map<String, String> or RequestMap(with file)
	 * @param requestListener
	 * @param actionId
	 * @return
	 */
	public LoadControler post(final String url, Object data,
			final RequestListener requestListener, int actionId) {
		return this.post(url, data, requestListener, false, TIMEOUT_COUNT,
				RETRY_TIMES, actionId);
	}

	/**
	 * 
	 * @param url
	 * @param data
	 *            String, Map<String, String> or RequestMap(with file)
	 * @param requestListener
	 * @param shouldCache
	 * @param timeoutCount
	 * @param retryTimes
	 * @param actionId
	 * @return
	 */
	public LoadControler post(final String url, Object data,
			final RequestListener requestListener, boolean shouldCache,
			int timeoutCount, int retryTimes, int actionId) {
		return request(Method.POST, url, data, null, requestListener,
				shouldCache, timeoutCount, retryTimes, actionId);
	}

	/**
	 * request
	 * 
	 * @param method
	 *            mainly Method.POST and Method.GET
	 * @param url
	 *            target url
	 * @param data
	 *            request params
	 * @param headers
	 *            request headers
	 * @param requestListener
	 *            request callback
	 * @param shouldCache
	 *            useCache
	 * @param timeoutCount
	 *            reqeust timeout count
	 * @param retryTimes
	 *            reqeust retry times
	 * @param actionId
	 *            request id
	 * @return
	 */
	private LoadControler request(int method, final String url, Object data,
			final Map<String, String> headers,
			final RequestListener requestListener, boolean shouldCache,
			int timeoutCount, int retryTimes, int actionId) {
		LogUtils.v("Url:"+ url);
		if(data!=null)
			LogUtils.d("Params:"+data);
		if(TextUtils.isEmpty(url))
			throw new NullPointerException("Url is null");
		
		return this.sendRequest(method, url, data, headers,
				new RequestListenerHolder(requestListener), shouldCache,
				timeoutCount, retryTimes, actionId);
	}

	/**
	 * @param method
	 * @param url
	 * @param data
	 * @param headers
	 * @param requestListener
	 * @param shouldCache
	 * @param timeoutCount
	 * @param retryTimes
	 * @param actionId
	 * @return
	 */
	private LoadControler sendRequest(int method, final String url, Object data,
			final Map<String, String> headers,
			final LoadListener requestListener, boolean shouldCache,
			int timeoutCount, int retryTimes, int actionId) {
		if (requestListener == null)
			throw new NullPointerException("RequestListener is null");

		final ByteArrayLoadControler loadControler = new ByteArrayLoadControler(
				requestListener, actionId);

		Request<?> request = null;
		if (data != null && data instanceof RequestMap) {// force POST and No Cache
			request = new ByteArrayRequest(Method.POST, url, data,
					loadControler, loadControler);
			request.setShouldCache(false);
		} else {
			request = new ByteArrayRequest(method, url, data, loadControler,
					loadControler);
			request.setShouldCache(shouldCache);
		}
		if (headers != null && !headers.isEmpty()) {// add headers if not empty
			try {
				request.getHeaders().putAll(headers);
			} catch (AuthFailureError e) {
				e.printStackTrace();
			}
		}

		RetryPolicy retryPolicy = new DefaultRetryPolicy(timeoutCount,
				retryTimes, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
		request.setRetryPolicy(retryPolicy);

		loadControler.bindRequest(request);

		if (this.mRequestQueue == null)
			throw new NullPointerException("RequestQueue is null");
		requestListener.onStart(url);
		this.mRequestQueue.add(request);

		return loadControler;
	}

}
