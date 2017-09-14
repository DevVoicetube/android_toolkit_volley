package com.volleypro;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.volleypro.enums.Method;
import com.volleypro.error.HttpError;
import com.volleypro.util.UtilVolley;

import java.io.File;
import java.util.HashMap;

/**
 * Created by tony1 on 1/18/2017.
 */

public class VolleyPro extends BaseVolleyPro {
    private String TAG = getClass().getSimpleName();
    private Context context;
    private Gson gson;
    private Option option;
    private RawOption rawOption;

    public VolleyPro(Context context) {
        super(context);
        this.context = context;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public VolleyPro request(final Method method, final String endpoint) {
        request(method, endpoint, null);
        return this;
    }

    public VolleyPro request(final Method method, final String endpoint, Option option) {
        this.option = option;
        HashMap<String, String> header = null;
        HashMap<String, String> parameters = null;
        String cachePath = null;
        long expiredDuration = 0;
        boolean forceUseCacheOnNoNetwork = false;

        if (option != null) {
            header = option.getHeader();
            parameters = option.getParameters();
            cachePath = option.getCachePath();
            expiredDuration = option.getExpiredDuration();
            forceUseCacheOnNoNetwork = option.isForceUseCacheOnNoNetwork();
        }

        boolean isNetworkAvailable = UtilVolley.isNetworkAvailable(context);
        boolean isCacheExist = UtilVolley.isFileExist(cachePath);
        boolean isCacheExpired = UtilVolley.isFileExpired(cachePath, expiredDuration);
        String cacheResult = UtilVolley.readFile(cachePath);

        if (isNetworkAvailable) {
            if (isCacheExist && !isCacheExpired) {
                log(method, endpoint, UtilVolley.SOURCE_CACHE);
                callOnSuccess(cacheResult);
            } else {
                log(method, endpoint, UtilVolley.SOURCE_NETWORK);
                request(method, endpoint, header, parameters, cachePath, cacheResult, forceUseCacheOnNoNetwork);
            }
        } else {
            if (isCacheExist && forceUseCacheOnNoNetwork) {
                log(method, endpoint, UtilVolley.SOURCE_CACHE);
                callOnSuccess(cacheResult);
            } else {
                log(method, endpoint, UtilVolley.SOURCE_NONE);
                callOnFailed(HttpError.Code.NETWORK_UNAVAILABLE, HttpError.Message.getMessage(HttpError.Code.NETWORK_UNAVAILABLE));
            }
        }
        return this;
    }

    public VolleyPro requestRaw(final Method method, final String endpoint, RawOption option) {
        this.rawOption = option;
        HashMap<String, String> header = null;
        String parameters = null;
        String cachePath = null;
        long expiredDuration = 0;
        boolean forceUseCacheOnNoNetwork = false;

        if (option != null) {
            header = option.getHeader();
            parameters = option.getParameters();
            cachePath = option.getCachePath();
            expiredDuration = option.getExpiredDuration();
            forceUseCacheOnNoNetwork = option.isForceUseCacheOnNoNetwork();
        }

        boolean isNetworkAvailable = UtilVolley.isNetworkAvailable(context);
        boolean isCacheExist = UtilVolley.isFileExist(cachePath);
        boolean isCacheExpired = UtilVolley.isFileExpired(cachePath, expiredDuration);
        String cacheResult = UtilVolley.readFile(cachePath);

        if (isNetworkAvailable) {
            if (isCacheExist && !isCacheExpired) {
                logRaw(method, endpoint, UtilVolley.SOURCE_CACHE);
                callOnSuccess(cacheResult);
            } else {
                logRaw(method, endpoint, UtilVolley.SOURCE_NETWORK);
                requestRaw(method, endpoint, header, parameters, cachePath, cacheResult, forceUseCacheOnNoNetwork);
            }
        } else {
            if (isCacheExist && forceUseCacheOnNoNetwork) {
                logRaw(method, endpoint, UtilVolley.SOURCE_CACHE);
                callOnSuccess(cacheResult);
            } else {
                logRaw(method, endpoint, UtilVolley.SOURCE_NONE);
                callOnFailed(HttpError.Code.NETWORK_UNAVAILABLE, HttpError.Message.getMessage(HttpError.Code.NETWORK_UNAVAILABLE));
            }
        }
        return this;
    }

    public VolleyPro requestFile(final Method method, final String endpoint, Option option) {
        this.option = option;
        HashMap<String, String> header = null;
        HashMap<String, String> parameters = null;
        String cachePath = null;
        long expiredDuration = 0;
        boolean forceUseCacheOnNoNetwork = false;
        boolean enableFileProgress = false;

        if (option != null) {
            header = option.getHeader();
            parameters = option.getParameters();
            cachePath = option.getCachePath();
            expiredDuration = option.getExpiredDuration();
            forceUseCacheOnNoNetwork = option.isForceUseCacheOnNoNetwork();
            enableFileProgress = option.isEnableFileProgress();
        } else {
            expiredDuration = 0;
            forceUseCacheOnNoNetwork = false;
        }

        if (cachePath == null) {
            Log.e(TAG, "requestFile\tcachePath is necessary");
            return this;
        }

        boolean isNetworkAvailable = UtilVolley.isNetworkAvailable(context);
        boolean isCacheExist = UtilVolley.isFileExist(cachePath);
        boolean isCacheExpired = UtilVolley.isFileExpired(cachePath, expiredDuration);
        File cacheResult = new File(cachePath);

        if (isNetworkAvailable) {
            if (isCacheExist && !isCacheExpired) {
                log(method, endpoint, UtilVolley.SOURCE_CACHE);
                callOnSuccess(cacheResult);
            } else {
                log(method, endpoint, UtilVolley.SOURCE_NETWORK);
                requestFile(method, endpoint, header, parameters, cachePath, cacheResult, forceUseCacheOnNoNetwork, enableFileProgress);
            }
        } else {
            if (isCacheExist && forceUseCacheOnNoNetwork) {
                log(method, endpoint, UtilVolley.SOURCE_CACHE);
                callOnSuccess(cacheResult);
            } else {
                log(method, endpoint, UtilVolley.SOURCE_NONE);
                callOnFailed(HttpError.Code.NETWORK_UNAVAILABLE, HttpError.Message.getMessage(HttpError.Code.NETWORK_UNAVAILABLE));
            }
        }
        return this;
    }

    private void log(final Method method, final String endpoint, String source) {
        if (!enableLog) {
            return;
        }
        HashMap<String, String> header = null;
        HashMap<String, String> parameters = null;
        String cachePath = null;
        long expiredDuration = 0;
        boolean forceUseCacheOnNoNetwork = false;

        if (option != null) {
            header = option.getHeader();
            parameters = option.getParameters();
            cachePath = option.getCachePath();
            expiredDuration = option.getExpiredDuration();
            forceUseCacheOnNoNetwork = option.isForceUseCacheOnNoNetwork();
        }

        Log.i(TAG, "request======================================");
        Log.i(TAG, String.format("%24s", "source : ") + source);
        Log.i(TAG, String.format("%24s", "method : ") + UtilVolley.getMethodName(method));
        Log.i(TAG, String.format("%24s", "endpoint : ") + endpoint);
        Log.i(TAG, String.format("%24s", "cachePath : ") + cachePath);
        Log.i(TAG, String.format("%24s", "expiredDuration : ") + expiredDuration);
        Log.i(TAG, String.format("%24s", "useCacheOnNoNetwork : ") + forceUseCacheOnNoNetwork);
        if (header != null) {
            for (String key : header.keySet()) {
                Log.i(TAG, String.format("%24s", "header : ") + String.format("%s : %s", key, header.get(key)));
            }
        }

        if (parameters != null) {
            for (String key : parameters.keySet()) {
                Log.i(TAG, String.format("%24s", "parameters : ") + String.format("%s : %s", key, parameters.get(key)));
            }
        }


        Log.i(TAG, "request======================================");
    }

    private void logRaw(final Method method, final String endpoint, String source) {
        if (!enableLog) {
            return;
        }
        HashMap<String, String> header = null;
        String parameters = null;
        String cachePath = null;
        long expiredDuration = 0;
        boolean forceUseCacheOnNoNetwork = false;

        if (rawOption != null) {
            header = rawOption.getHeader();
            parameters = rawOption.getParameters();
            cachePath = rawOption.getCachePath();
            expiredDuration = rawOption.getExpiredDuration();
            forceUseCacheOnNoNetwork = rawOption.isForceUseCacheOnNoNetwork();
        }

        Log.i(TAG, "request======================================");
        Log.i(TAG, String.format("%24s", "source : ") + source);
        Log.i(TAG, String.format("%24s", "method : ") + UtilVolley.getMethodName(method));
        Log.i(TAG, String.format("%24s", "endpoint : ") + endpoint);
        Log.i(TAG, String.format("%24s", "cachePath : ") + cachePath);
        Log.i(TAG, String.format("%24s", "expiredDuration : ") + expiredDuration);
        Log.i(TAG, String.format("%24s", "useCacheOnNoNetwork : ") + forceUseCacheOnNoNetwork);
        if (header != null) {
            for (String key : header.keySet()) {
                Log.i(TAG, String.format("%24s", "header : ") + String.format("%s : %s", key, header.get(key)));
            }
        }

        if (parameters != null) {
            Log.i(TAG, String.format("%24s", "raw : \n") + parameters);
        }


        Log.i(TAG, "request======================================");
    }

    public void cleanCache() {
        if (option == null) {
            return;
        }
        UtilVolley.deleteFile(option.getCachePath());
    }
}
