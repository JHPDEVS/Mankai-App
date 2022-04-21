package com.wdj.mankai.ui.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.wdj.mankai.R;
import com.wdj.mankai.ui.login.LoginActivity;
import com.wdj.mankai.ui.login.LoginRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences= getSharedPreferences("login_token", MODE_PRIVATE);
        String token = sharedPreferences.getString("login_token","");
        getUser(token); // 유저 정보 받아오기
    }

    private void getUser(String token) {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    System.out.println(response);
                    String userName = jsonObject.getString("name");
                    SharedPreferences sharedPreferences = getSharedPreferences("user_info",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("user_info",response);
                    editor.commit();
                    if(userName != null) {
                        System.out.println("유저 정보 받아옴");
                        Toast.makeText(MainActivity.this,response,Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(MainActivity.this,"토큰 만료 다시 로그인", Toast.LENGTH_SHORT).show();
                        System.out.println(response);
                    }
                } catch(JSONException err) {
                    err.printStackTrace();
                }
            }
        };
        UserRequest userRequest = new UserRequest(token,responseListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(userRequest);
    }
}