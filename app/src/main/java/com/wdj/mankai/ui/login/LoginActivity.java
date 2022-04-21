package com.wdj.mankai.ui.login;

import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.wdj.mankai.R;
import com.wdj.mankai.ui.login.LoginViewModel;
import com.wdj.mankai.ui.login.LoginViewModelFactory;
import com.wdj.mankai.databinding.ActivityLoginBinding;
import com.wdj.mankai.ui.main.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;

    EditText emailEditText ;
    EditText passwordEditText;
    TextView emailValidationText;
    TextView passwordValidationText;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        emailValidationText = findViewById(R.id.emailValidation);
        passwordValidationText = findViewById(R.id.passwordValidation);
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;
        getToken(); // 토큰 불러옴
        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    emailEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                System.out.println(emailEditText.getText().toString() + passwordEditText.getText().toString());
            }
        });



        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
                System.out.println(emailEditText.getText().toString() + passwordEditText.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(emailEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(emailEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                login();
            }
        });
    }
    private void getToken() {
        SharedPreferences sharedPreferences= getSharedPreferences("login_token", MODE_PRIVATE);
        String token = sharedPreferences.getString("login_token","");
        if(token.isEmpty()) {
            Toast.makeText(LoginActivity.this,"저장된 토큰이 없습니다 로그인해주세요 " ,Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(LoginActivity.this,"토근 불러오기 완료" + token,Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class); // 여기에 클래스명 바꿔주면 됨
            startActivity(intent); 
        }

    }
    private void login() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int statusCode = jsonObject.getInt("status");
                    if(statusCode == 200) {
                        System.out.println("로그인 성공");
                        String token = jsonObject.getString("token");
                        String toastMessage = "로그인 성공 토큰 값 : " + token;
                        Toast.makeText(LoginActivity.this,toastMessage, Toast.LENGTH_SHORT).show();

                        SharedPreferences sharedPreferences = getSharedPreferences("login_token",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("login_token",token);
                        editor.commit();
                        Toast.makeText(LoginActivity.this,"토큰 저장 완료",Toast.LENGTH_SHORT).show();
                    } else if(statusCode == 400){
                        System.out.println("validation error");
                        emailValidationText.setText(jsonObject.getString("email"));
                        passwordValidationText.setText(jsonObject.getString("password"));
                        System.out.println(response);
                    } else if(statusCode == 401){
                        Toast.makeText(LoginActivity.this,"아이디와 비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show();
                      
                    } else {
                        Toast.makeText(LoginActivity.this,"서버하고 연결실패", Toast.LENGTH_SHORT).show();
                    }
                } catch(JSONException err) {
                    err.printStackTrace();
                }
            }
        };
        LoginRequest loginRequest = new LoginRequest(email,password,responseListener);
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        queue.add(loginRequest);
    }
    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }


}