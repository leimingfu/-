package com.Healthy.Login;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.Healthy.MainPage.InfoActivity;
import com.Healthy.MainPage.R;
import com.MySQL.Http.LoginPostService;
import com.MySQL.Http.RegisterPostService;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private EditText mAccount;                        //用户名编辑
    private EditText mPwd;                            //密码编辑
    private Button mRegisterButton;                   //注册按钮
    private Button mLoginButton;                      //登录按钮
    private CheckBox pwd_remember;                 //记住密码
    private String FLAG;

    NoteDBOpenHelper noteDBOpenHelper;
    List<Result> resultinfo=new ArrayList<>();


    Handler handler;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_log);
        FLAG = "False";
        //通过id找到相应的控件
        mAccount = (EditText) findViewById(R.id.login_edit_account);
        mPwd = (EditText) findViewById(R.id.login_edit_pwd);
        mRegisterButton = (Button) findViewById(R.id.login_btn_register);
        mLoginButton = (Button) findViewById(R.id.login_btn_login);
        pwd_remember = (CheckBox) findViewById(R.id.login_text_remember_pwd);
        noteDBOpenHelper=new NoteDBOpenHelper(this,"Result.db",null,1);
        getList();

        if(resultinfo.size() != 0){
            Result result = resultinfo.get(resultinfo.size() - 1);
            if(result.getPwd_remember().equals("True")){
                pwd_remember.setChecked(true);
                FLAG = "True";
                mAccount.setText(result.getUser());
                mPwd.setText(result.getPwd());
                if(isConnectingToInternet()){ //检查网络
                    if (mAccount.getText().toString().equals(""))
                        Toast.makeText(MainActivity.this, "请输入账号", Toast.LENGTH_SHORT).show();
                    if (mPwd.getText().toString().equals(""))
                        Toast.makeText(MainActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    else {
                        //启动登录Thread
                        dialog = new Dialog(MainActivity.this);
                        dialog.setTitle("正在登录，请稍后...");
                        dialog.setCancelable(false);
                        dialog.show();
                        new LoginPostThread(mAccount.getText().toString(),
                                mPwd.getText().toString()).start();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),
                            "网络未连接",Toast.LENGTH_SHORT).show();
                }
            }
        }

        mRegisterButton.setOnClickListener(mListener);                      //采用OnClickListener方法设置不同按钮按下之后的监听事件
        mLoginButton.setOnClickListener(mListener);
        pwd_remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    FLAG = "True";
                   // Toast.makeText(MainActivity.this, "选中", Toast.LENGTH_SHORT).show();
                }
                else {
                    FLAG = "False";
                   // Toast.makeText(MainActivity.this, "取消", Toast.LENGTH_SHORT).show();
                }
            }
        });



        //Handle,Msg返回成功信息，跳转到其他Activity
        handler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                dialog.dismiss();
                if (msg.what == 111) {  // 处理发送线程传回的消息
                    System.out.println("msg.obh = " + msg.obj.toString());
                    if(msg.obj.toString().equals("FAILED"))
                    {
                        Toast.makeText(MainActivity.this, "服务器未开启，请联系管理员", Toast.LENGTH_SHORT).show();
                    } else if(msg.obj.toString().equals("NOT_REGISTER"))
                    {
                        Toast.makeText(MainActivity.this, "账号未注册", Toast.LENGTH_SHORT).show();
                    } else if(msg.obj.toString().equals("LOGIN_FAILED"))
                    {
                        Toast.makeText(MainActivity.this, "账号密码不匹配", Toast.LENGTH_SHORT).show();
                    } else if(msg.obj.toString().equals("LOGIN_SUCCEEDED"))
                    {
                        //跳转
                        Toast.makeText(MainActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                        SQLiteDatabase db=noteDBOpenHelper.getWritableDatabase();
                        String username = mAccount.getText().toString();
                        Boolean User_Exits = false;
                        if(resultinfo.size() != 0){
                            Result result = null;
                            String user ;
                            for(int i = 0; i < resultinfo.size(); i++){
                                result = resultinfo.get(i);
                                user = result.getUser();
                                if(username.equals(user))
                                {
                                    User_Exits = true;
                                }
                                db.execSQL("update Noteinfo set pwd_remember=?where user=?",new Object[]{FLAG,user});
                                result = null;
                            }
                        }
                        if(User_Exits == false)
                        {
                            Date date = new Date();
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            String date_now = formatter.format(date);
                            db.execSQL("insert into Noteinfo (_id,user,pwd,pwd_remember,date,step) " + " values(?,?,?,?,?,?)",new Object[]{null,mAccount.getText().toString(),mPwd.getText().toString(),FLAG,date_now,"0"});
                        }
                        //Intent intent = new Intent(com.Healthy.Login.MainActivity.this,com.Healthy.MainPage.MainActivity.class) ;    //切换Login Activity至User Activity
//                        startActivity(intent);
//                        finish();
                        //打开信息Activity
                        Intent intent=new Intent(com.Healthy.Login.MainActivity.this,InfoActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        };

    }
    View.OnClickListener mListener = new View.OnClickListener() {                  //不同按钮按下的监听事件选择
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.login_btn_register:                            //登录界面的注册按钮
                    Intent intent_Login_to_Register = new Intent(com.Healthy.Login.MainActivity.this,com.Healthy.Login.Register.class) ;    //切换Login Activity至Register Activity
                    startActivity(intent_Login_to_Register);
                    finish();
                    break;
                case R.id.login_btn_login:                              //登录界面的登录按钮
                    if(isConnectingToInternet()){ //检查网络
                        if (mAccount.getText().toString().equals(""))
                            Toast.makeText(MainActivity.this, "请输入账号", Toast.LENGTH_SHORT).show();
                        if (mPwd.getText().toString().equals(""))
                            Toast.makeText(MainActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                        else {
                            //启动登录Thread
                            dialog = new Dialog(MainActivity.this);
                            dialog.setTitle("正在登录，请稍后...");
                            dialog.setCancelable(false);
                            dialog.show();
                            new LoginPostThread(mAccount.getText().toString(),
                                    mPwd.getText().toString()).start();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),
                                "网络未连接",Toast.LENGTH_SHORT).show();
                    }
                    break;

            }
        }
    };



    //登录Thread调用LoginPostService，返回Msg
    public class LoginPostThread extends Thread {
        public String id, password;
        public LoginPostThread(String id, String password) {
            this.id = id;
            this.password = password;
        }

        @Override
        public void run() {
            // 要发送的数据
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", id));
            params.add(new BasicNameValuePair("password", password));
            // 发送数据，获取对象
            String responseMsg  = LoginPostService.send(params);
            // 准备发送消息
            Message msg = handler.obtainMessage();
            // 设置消息默认值
            msg.what = 111;
            // 服务器返回信息的判断和处理
            msg.obj = responseMsg;
            handler.sendMessage(msg);
        }
    }




    private void getList() {
        SQLiteDatabase db=noteDBOpenHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from Noteinfo",null);
        while(cursor.moveToNext())
        {
            Result result=new Result();
            result.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            result.setUser(cursor.getString(cursor.getColumnIndex("user")));
            result.setPwd(cursor.getString(cursor.getColumnIndex("pwd")));
            result.setPwd_remember(cursor.getString(cursor.getColumnIndex("pwd_remember")));
            resultinfo.add(result);
            System.out.println(result.toString());
        }
        cursor.close();
        db.close();
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
