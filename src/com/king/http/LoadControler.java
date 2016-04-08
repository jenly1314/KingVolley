package com.king.http;

import com.android.volley.Request;

/**
 * LoadControler for Request
 * @author Jenly
 * @date 2015-10-9
 */
public interface LoadControler {
	void cancel();
}

/**
 * Abstract LoaderControler that implements LoadControler
 * 
 */
class AbsLoadControler implements LoadControler {
	
	protected Request<?> mRequest;

	public void bindRequest(Request<?> request) {
		this.mRequest = request;
	}

	@Override
	public void cancel() {
		if (this.mRequest != null) {
			this.mRequest.cancel();
		}
	}

	protected String getOriginUrl() {
		
		return this.mRequest.getUrl();
	}
}
