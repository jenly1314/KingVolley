package com.king.http;

import java.util.Map;

/**
 * LoadListener special for ByteArrayLoadControler
 * @author Jenly
 * @date 2015-10-8
 */
public interface LoadListener {
	
	void onStart(String url);

	void onSuccess(byte[] data, Map<String, String> headers, String url, int actionId);

	void onError(String errorMsg, String url, int actionId);
}
