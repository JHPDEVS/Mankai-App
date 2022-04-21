package com.wdj.mankai.ui.login;

import java.net.ResponseCache;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

public class LoginRequest extends StringRequest {

    final static  private String URL = "https://api.mankai.shop/api/login";

    private Map<String,String> map;

    public LoginRequest(String email , String password , Response.Listener<String> listener) {
        super(Method.POST,URL,listener,null);

        map = new HashMap<>();
        map.put("email", email);
        map.put("password",password);
    }

}
