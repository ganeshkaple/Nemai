package com.scleroid.nemai.volley_support;

import org.json.JSONObject;

/**
 * Created by Nanostuffs on 25-05-2015.
 */
public interface VolleyCompleteListener {


    void onTaskCompleted(JSONObject response, int statusCode);

    void onTaskFailed(String error, int statusCode);


}
