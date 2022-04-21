package com.wdj.mankai.ui.main;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;

import java.net.ResponseCache;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

public class UserRequest extends StringRequest {

    final static private String URL = "https://api.mankai.shop/api/user";
    private Map<String,String> map;

    public UserRequest(String ACCESS_TOKEN,Response.Listener<String> listener) {
        super(Method.GET,URL,listener,null);
        map = new HashMap<>();
        map.put("Authorization", "Bearer "+ACCESS_TOKEN);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return map;
    }

}
