package sg.vinova.easy_imgur.networking;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import sg.vinova.easy_imgur.base.Constant;
import sg.vinova.easy_imgur.utilities.LogUtility;
import sg.vinova.easy_imgur.utilities.TokenUtility;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class ImgurAPI {

	// TAG
	public static final String TAG = "ImgurAPI";

	private static RequestQueue mRequestQueue;

	// Instance object
	private static ImgurAPI imgurClient;

	// Imgur API url
	private static final String imgurClientUrl = "https://api.imgur.com/3";

	private String url;

	public static void init(Context context) {
		mRequestQueue = Volley.newRequestQueue(context);
	}

	public static RequestQueue getRequestQueue() {
		if (mRequestQueue != null) {
			return mRequestQueue;
		} else {
			throw new IllegalStateException("RequestQueue not initialized");
		}
	}

	public static ImgurAPI getClient() {
		if (imgurClient == null) {
			imgurClient = new ImgurAPI();
			imgurClient.url = imgurClientUrl;
		}
		return imgurClient;
	}

	public String getUrl(String path) {
		return this.url + "/" + path;
	}

	public static String generateFullUrl(String url,
			HashMap<String, String> params) {
		if (params.keySet().size() > 0) {
			url = url + "?";
			for (String key : params.keySet()) {
				url = url + key + "=" + params.get(key) + "&";
			}
		}
		LogUtility.e(TAG, "request url: " + url);
		return url;
	}

	public static void get(final Context mContext, String url, final HashMap<String, String> params,
			Response.Listener<JSONObject> listener,
			Response.ErrorListener errorListener) {
		RequestQueue mRequestQueue = getRequestQueue();
		JsonObjectRequest jsonRequest = new JsonObjectRequest(Method.GET,
				generateFullUrl(url, params), null, listener, errorListener){
			@Override
			public Map<String, String> getHeaders()
					throws AuthFailureError {
				HashMap<String, String> params = new HashMap<String, String>();
				if (TokenUtility.getUser(mContext) != null && !TextUtils.isEmpty(TokenUtility.getUser(mContext).getAccessToken())) {
					params.put("Authorization", "Bearer " + TokenUtility.getUser(mContext).getAccessToken());
				} else {
					params.put("Authorization", "Client-ID " + Constant.CLIENT_ID);
				}
		        return params;
			}
		};
		mRequestQueue.add(jsonRequest);
	}

	public static void getArray(String url,
			final HashMap<String, String> params,
			Response.Listener<JSONArray> listener,
			Response.ErrorListener errorListener) {
		RequestQueue mRequestQueue = getRequestQueue();
		JsonArrayRequest jsonRequest = new JsonArrayRequest(generateFullUrl(url, params), listener, errorListener);
		mRequestQueue.add(jsonRequest);
	}

	public static void post(String url, JSONObject params,
			Response.Listener<JSONObject> listener,
			Response.ErrorListener errorListener) {
		RequestQueue mRequestQueue = getRequestQueue();
		JsonObjectRequest jsonRequest = new JsonObjectRequest(Method.POST, url,
				params, listener, errorListener);
		mRequestQueue.add(jsonRequest);
		LogUtility.e(TAG, "post url: " + url);
	}
	
	public static void post(final Context mContext, String url, final HashMap<String, String> params,
			Response.Listener<JSONObject> listener,
			Response.ErrorListener errorListener) {
		RequestQueue mRequestQueue = getRequestQueue();
		JsonObjectRequest jsonRequest = new JsonObjectRequest(Method.POST,
				generateFullUrl(url, params), null, listener, errorListener){
			@Override
			public Map<String, String> getHeaders()
					throws AuthFailureError {
				HashMap<String, String> params = new HashMap<String, String>();
				if (TokenUtility.getUser(mContext) != null && !TextUtils.isEmpty(TokenUtility.getUser(mContext).getAccessToken())) {
					params.put("Authorization", "Bearer " + TokenUtility.getUser(mContext).getAccessToken());
					LogUtility.e(TAG, "Bearer " + TokenUtility.getUser(mContext).getAccessToken());
				} else {
					params.put("Authorization", "Client-ID " + Constant.CLIENT_ID);
					LogUtility.e(TAG, "Client-ID " + Constant.CLIENT_ID);
				}
		        return params;
			}
		};
		mRequestQueue.add(jsonRequest);
	}
	
	/**********************
	 ******* GALLERY ******
	 **********************/
	
	public void getAllGallery(Context mContext, String section, int page, 
			String sort, String window, boolean showViral,
			Response.Listener<JSONObject> listener,
			Response.ErrorListener errorListener){
		String url = "gallery/";
		url += section + "/";
		
		if (!TextUtils.isEmpty(sort)) {
			url += sort +"/";
		}
		if (section.equalsIgnoreCase(Constant.PARAM_TYPE_SECTION_TOP) && !TextUtils.isEmpty(window)) {
			url += window + "/";
		}
		url += page;
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(Constant.PARAM_SHOW_VIRAL, String.valueOf(showViral));
		
		ImgurAPI.get(mContext, getUrl(url), params, listener, errorListener);
	}
	
	/**	
	 * Get detail of a gallery
	 * @param galleryId
	 * @param listener
	 * @param errorListener
	 */
	public void getDetailGallery(Context mContext, String galleryId, Response.Listener<JSONObject> listener,
			Response.ErrorListener errorListener) {
		String url = getUrl("gallery/") + galleryId;
		
		HashMap<String, String> params = new HashMap<String, String>();
		
		ImgurAPI.get(mContext, url, params, listener, errorListener);
	}
	
	/**
	 * Favorite an album
	 * @param mContext
	 * @param galleryId
	 * @param listener
	 * @param errorListener
	 */
	public void favoriteAlbum(Context mContext, String galleryId, Response.Listener<JSONObject> listener,
			Response.ErrorListener errorListener) {
		String url = getUrl("album/") + galleryId + "/favorite";
		
		HashMap<String, String> params = new HashMap<String, String>();
		
		ImgurAPI.post(mContext, url, params, listener, errorListener);
	}
	
	/**
	 * Favorite an image
	 * @param mContext
	 * @param galleryId
	 * @param listener
	 * @param errorListener
	 */
	public void favoriteImage(Context mContext, String galleryId, Response.Listener<JSONObject> listener,
			Response.ErrorListener errorListener) {
		String url = getUrl("image/") + galleryId + "/favorite";
		
		HashMap<String, String> params = new HashMap<String, String>();
		
		ImgurAPI.post(mContext, url, params, listener, errorListener);
	}
}
