package com.Healthy.MainPage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;



public class InfoActivity extends AppCompatActivity {

    public static String age;
    public static String h;
    public static String w;
    public static String tel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
    }
    public void enter(View v){
        EditText et1=(EditText)findViewById(R.id.et_age);
        EditText et2=(EditText)findViewById(R.id.et_h);
        EditText et3=(EditText)findViewById(R.id.et_w);
        EditText et4=(EditText)findViewById(R.id.et_tel);
        et1.setText("1");
        et2.setText("1");
        et3.setText("1");
        et4.setText("15665160228");


        age=et1.getText().toString();
        h=et2.getText().toString();
        w=et3.getText().toString();
        tel=et4.getText().toString();
        //Toast.makeText(getApplicationContext(),"1",Toast.LENGTH_LONG).show();
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}

