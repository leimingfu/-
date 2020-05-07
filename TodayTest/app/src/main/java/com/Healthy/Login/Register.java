package com.Healthy.Login;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.Healthy.MainPage.R;
import com.MySQL.Http.RegisterPostService;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;


public class Register extends AppCompatActivity {
    private EditText mAccount;                        //用户名编辑
    private EditText mPwd;                            //密码编辑
    private EditText mPwdCheck;                       //密码编辑
    private Button mSureButton;                       //确定按钮
    private Button mCancelButton;                     //取消按钮

    Handler handler;
    static int REGISTER_FAILED = 0;
    static int REGISTER_SUCCEEDED = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAccount = (EditText) findViewById(R.id.resetpwd_edit_name);
        mPwd = (EditText) findViewById(R.id.resetpwd_edit_pwd_old);
        mPwdCheck = (EditText) findViewById(R.id.resetpwd_edit_pwd_new);

        mSureButton = (Button) findViewById(R.id.register_btn_sure);
        mCancelButton = (Button) findViewById(R.id.register_btn_cancel);

        mSureButton.setOnClickListener(m_register_Listener);      //注册界面两个按钮的监听事件
        mCancelButton.setOnClickListener(m_register_Listener);


        //Handle,Msg返回成功信息，跳转到其他Activity
        handler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 222) {  // 处理发送线程传回的消息
                    if(msg.obj.toString().equals("ALREADY_REGISTER")){
                        Toast.makeText(Register.this, "账号已注册", Toast.LENGTH_SHORT).show();
                    } else if(msg.obj.toString().equals("REGISTER_FAILED")){
                        Toast.makeText(Register.this, "注册失败", Toast.LENGTH_SHORT).show();
                    } else if(msg.obj.toString().equals("FAILED")){
                        Toast.makeText(Register.this, "服务器未开启，请联系管理员", Toast.LENGTH_SHORT).show();
                    } else if(msg.obj.toString().equals("REGISTER_SUCCEEDED")){
                        Toast.makeText(Register.this, "注册成功", Toast.LENGTH_SHORT).show();
                        Intent intent_Register_to_Login = new Intent(com.Healthy.Login.Register.this,com.Healthy.MainPage.InfoActivity.class) ;    //切换User Activity至Login Activity
                        startActivity(intent_Register_to_Login);
                        finish();
                    }
                }
            }
        };

    }
    View.OnClickListener m_register_Listener = new View.OnClickListener() {    //不同按钮按下的监听事件选择
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.register_btn_sure:                       //确认按钮的监听事件
                    if(isConnectingToInternet()){ //检查网络

                        if(mAccount.getText().toString().equals("")){
                            Toast.makeText(Register.this, "请输入账号", Toast.LENGTH_SHORT).show();
                            return;
                        }if(mPwd.getText().toString().equals("")){
                            Toast.makeText(Register.this, "请输入密码", Toast.LENGTH_SHORT).show();
                            return;
                        }if(mPwdCheck.getText().toString().equals("")){
                            Toast.makeText(Register.this, "请输入确认账号", Toast.LENGTH_SHORT).show();
                            return;
                        }if(mPwd.getText().toString().equals(mPwdCheck.getText().toString()) == false){
                            Toast.makeText(Register.this, "两次密码不一致，请检查", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        new RegisterPostThread(mAccount.getText().toString(),mPwd.getText().toString()).start();
                    }else{
                        Toast.makeText(getApplicationContext(),
                                "网络未连接",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.register_btn_cancel:                     //取消按钮的监听事件,由注册界面返回登录界面
                    Intent intent_Register_to_Login = new Intent(com.Healthy.Login.Register.this,com.Healthy.Login.MainActivity.class) ;    //切换User Activity至Login Activity
                    startActivity(intent_Register_to_Login);
                    finish();
                    break;
            }
        }
    };


    //注册Thread调用RegisterPostService，返回Msg
    public class RegisterPostThread extends Thread {
        public String username, password;

        public RegisterPostThread(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        public void run() {
            // 要发送的数据
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("password", password));
            // 发送数据，获取对象
            String responseMsg = RegisterPostService.send(params);
            Log.i("tag", "RegisterActivity: responseInt = " + responseMsg);
            // 准备发送消息
            Message msg = handler.obtainMessage();
            // 设置消息默认值
            msg.what = 222;
            msg.obj = responseMsg;
            handler.sendMessage(msg);
        }
    }

    // 检测网络状态
    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
        }
        return false;
    }



}
