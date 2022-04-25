package com.wdj.mankai.ui.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.wdj.mankai.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.acl.Group;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences= getSharedPreferences("login_token", MODE_PRIVATE);
        String token = sharedPreferences.getString("login_token","");
        getUser(token); // 유저 정보 받아오기

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_container,new HomeFragment()).commit();

        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.chat:
                        fragment = new ChatFragment();
                        break;
                    case R.id.group:
                        fragment = new GroupFragment();
                        break;
                    case R.id.mypage:
                        fragment = new MyPageFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container,fragment).commit();
                return true;
            }
        });
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