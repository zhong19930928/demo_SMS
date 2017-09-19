package com.example.caojl.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int REQUEST_PERMISSION_CODE = 1;
    private EditText phone_number,code;
    private Button btn_sendmessage,submint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        phone_number = (EditText) findViewById(R.id.phone_number);
        code = (EditText) findViewById(R.id.code);
        btn_sendmessage = (Button) findViewById(R.id.btn_sendmessage);
        submint = (Button) findViewById(R.id.submint);
        submint.setOnClickListener(this);
        btn_sendmessage.setOnClickListener(this);
        //SMSSDK.registerEventHandler(handler);

        SMSSDK.registerEventHandler( handler);
    }


    EventHandler handler =new EventHandler(){
        @Override
        public void afterEvent(int event, int result, Object data) {
            {
                if (result ==SMSSDK.RESULT_COMPLETE){
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,"验证成功",Toast.LENGTH_SHORT).show();
//                                本地存储注册的号码
//                                SharedPreferencesUtils.createSharePreferences(MainActivity.this, Config.LOGIN_MESSAGE,phoneS, MD5Tools.MD5(Password1));
                                startActivity(new Intent(MainActivity.this,MainActivity.class));
                                finish();
                            }
                        });
                    }else if(event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        //获取验证码成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,"验证码已发送",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }else{
                    ((Throwable)data).printStackTrace();
                    Throwable throwable = (Throwable) data;
                    try {
                        JSONObject obj = new JSONObject(throwable.getMessage());
                        final String des = obj.optString("detail");
                        if (!TextUtils.isEmpty(des)){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (des.equals("invalid validation code")){
                                        Toast.makeText(MainActivity.this,"验证码无效",Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(MainActivity.this,"手机号已注册",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_sendmessage:
                SMSSDK.getVerificationCode("86",phone_number.getText().toString());
                break;
            case R.id.submint:
                SMSSDK.submitVerificationCode("86",phone_number.getText().toString(),code.getText().toString());
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(handler);
    }
}
