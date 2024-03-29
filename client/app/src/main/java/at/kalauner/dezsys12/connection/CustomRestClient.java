package at.kalauner.dezsys12.connection;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * RestClient
 *
 * @author Paul Kalauner 5BHIT
 * @version 20160219.1
 */
public class CustomRestClient {
    private static final String BASE_URL = "https://young-beyond-42910.herokuapp.com/";
    // private static final String BASE_URL = "http://10.0.0.15:8080/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    static {
        // Long polling
        client.setTimeout(80000);
    }


    /**
     * Sends a get request
     *
     * @param url             url
     * @param params          request params
     * @param responseHandler callback method
     */
    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    /**
     * Sends a get request
     *
     * @param context         context
     * @param url             url
     * @param params          request params
     * @param responseHandler callback method
     */
    public static void get(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(context, getAbsoluteUrl(url), params, responseHandler);
    }

    /**
     * Sends a post request
     *
     * @param url             url
     * @param params          request params
     * @param responseHandler callback method
     */
    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }


    /**
     * Sends a post request with a JSON-Body
     *
     * @param context         context
     * @param url             url
     * @param entity          json body
     * @param responseHandler callback method
     */
    public static void postJson(Context context, String url, StringEntity entity, AsyncHttpResponseHandler responseHandler) {
        client.post(context, getAbsoluteUrl(url), entity, "application/json", responseHandler);
    }

    /**
     * Cancels all requests
     */
    public static void cancelRequests() {
        client.cancelAllRequests(true);
    }

    /**
     * returns the absoulute url
     *
     * @param relativeUrl endpoint
     * @return absolute url
     */
    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
