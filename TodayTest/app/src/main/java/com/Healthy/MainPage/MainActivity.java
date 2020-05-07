package com.Healthy.MainPage;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.Healthy.Login.NoteDBOpenHelper;
import com.Healthy.Login.Result;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.Healthy.MainPage.R;
import MainPage.lib.ISportStepInterface;
import com.Healthy.MainPage.lib.TodayStepManager;
import com.Healthy.MainPage.lib.TodayStepService;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import interfaces.heweather.com.interfacesmodule.bean.Lang;
import interfaces.heweather.com.interfacesmodule.bean.Unit;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.Now;
import interfaces.heweather.com.interfacesmodule.view.HeConfig;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;


public class MainActivity extends AppCompatActivity implements SensorEventListener {









    public static int Step=0;   //步数
    public static String Climate="1";   //气候
    public static String Temperature;  //温度
    public static String Location="1";     //定位





    public int Old_Step = 0;






/**
 *
 *初始化数据,从数据库读取数据
 *
 */

    NoteDBOpenHelper noteDBOpenHelper;
    List<Result> resultinfo=new ArrayList<>();









    //计步部分
    private static String TAG = "MainActivity_log";

    private static final int REFRESH_STEP_WHAT = 0;

    //循环取当前时刻的步数中间的间隔时间
    private long TIME_INTERVAL_REFRESH = 3000;

    private Handler mDelayHandler = new Handler(new TodayStepCounterCall());
    private int mStepSum;

    private ISportStepInterface iSportStepInterface;

//    private TextView mStepArrayTextView;
//
//    private TextView timeTextView;


    private SensorManager sensorManager;

    private Sensor sensor;


    public boolean flag_zt;



    //天气
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new MyAMapLocationListener();
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    String Long_Lat;   //经纬度
    public TextView weather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_main2);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_health, R.id.navigation_person)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        noteDBOpenHelper=new NoteDBOpenHelper(this,"Result.db",null,1);

        //获取数据库数据
        getList();

        try{
            String date_db = resultinfo.get(resultinfo.size() - 1).getDate();

            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String date_now = formatter.format(date);
            if(!date_db.equals(date_now)){
                SQLiteDatabase db=noteDBOpenHelper.getReadableDatabase();
                db.execSQL("insert into Noteinfo (_id,user,pwd,pwd_remember,date,step) " + " values(?,?,?,?,?,?)",new Object[]{null,resultinfo.get(resultinfo.size() - 1).getUser(),resultinfo.get(resultinfo.size() - 1).getPwd(),resultinfo.get(resultinfo.size() - 1).getPwd_remember(),date_now,"0"});
                db.close();
                Old_Step = 0;
            } else
                Old_Step = Integer.parseInt(resultinfo.get(resultinfo.size() - 1).getStep());

        } catch (Exception e) {
            e.printStackTrace();
        }


        //calll_quanxian();//电话权限申请
        //定位权限
        getPermissionMethod();
        flag_zt = false;

        //初始化计步模块
        TodayStepManager.startTodayStepService(getApplication());
        Log.e("Service111","开始绑定1");

//        timeTextView = (TextView) findViewById(R.id.timeTextView);
//        mStepArrayTextView = (TextView) findViewById(R.id.stepArrayTextView);

        //开启计步Service，同时绑定Activity进行aidl通信
        Intent intent = new Intent(this, TodayStepService.class);
        startService(intent);
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                //Activity和Service通过aidl进行通信
                iSportStepInterface = ISportStepInterface.Stub.asInterface(service);
                try {
                    mStepSum = iSportStepInterface.getCurrentTimeSportStep();
                    updateStepCount();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                mDelayHandler.sendEmptyMessageDelayed(REFRESH_STEP_WHAT, TIME_INTERVAL_REFRESH);

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Context.BIND_AUTO_CREATE);



        //计时器
        mhandmhandlele.post(timeRunable);


        //跌落部分
        getSensorManager();

        Log.e("Service111","绑定完成");

        //天气
        HeConfig.init("HE2003171619431835", "f908fa75816b477a98b4b5e51cf64933");
        HeConfig.switchToFreeServerNode();
        //weather = (TextView) findViewById(R.id.Weather);
        init();
        //打开主界面
//        Intent intent1 =new Intent(this,Main2Activity.class);
//        startActivity(intent1);




    }

    class TodayStepCounterCall implements Handler.Callback {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_STEP_WHAT: {
                    //每隔500毫秒获取一次计步数据刷新UI
                    if (null != iSportStepInterface) {
                        int step = 0;
                        try {
                            step = iSportStepInterface.getCurrentTimeSportStep();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        if (mStepSum != step) {
                            mStepSum = step;
                            updateStepCount();
                        }
                    }
                    mDelayHandler.sendEmptyMessageDelayed(REFRESH_STEP_WHAT, TIME_INTERVAL_REFRESH);

                    break;
                }
            }
            return false;
        }
    }

    private void updateStepCount() {


        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date_now = formatter.format(date);
        String date_db = resultinfo.get(resultinfo.size() - 1).getDate();
        if(!date_db.equals(date_now)){
            SQLiteDatabase db=noteDBOpenHelper.getReadableDatabase();
            db.execSQL("update Noteinfo set step=?where date=?",new Object[]{mStepSum + Old_Step,date_db});
            db.execSQL("insert into Noteinfo (_id,user,pwd,pwd_remember,date,step) " + " values(?,?,?,?,?,?)",new Object[]{null,resultinfo.get(resultinfo.size() - 1).getUser(),resultinfo.get(resultinfo.size() - 1).getPwd(),resultinfo.get(resultinfo.size() - 1).getPwd_remember(),date_now,"0"});
            db.close();
            Old_Step = 0;
            mStepSum = 0;
        }
        Log.e(TAG, "updateStepCount : " + mStepSum);
        Step = mStepSum + Old_Step;
    }



    /*****************计时器*******************/
    private Runnable timeRunable = new Runnable() {
        @Override
        public void run() {

            currentSecond = currentSecond + 1000;
//            timeTextView.setText(getFormatHMS(currentSecond));
            if (!isPause) {
                //递归调用本runable对象，实现每隔一秒一次执行任务
                mhandmhandlele.postDelayed(this, 1000);
            }
        }
    };
    //计时器
    private Handler mhandmhandlele = new Handler();
    private boolean isPause = false;//是否暂停
    private long currentSecond = 0;//当前毫秒数
/*****************计时器*******************/

    /**
     * 根据毫秒返回时分秒
     *
     * @param time
     * @return
     */
    public static String getFormatHMS(long time) {
        time = time / 1000;//总秒数
        int s = (int) (time % 60);//秒
        int m = (int) (time / 60);//分
        int h = (int) (time / 3600);//秒
        return String.format("%02d:%02d:%02d", h, m, s);
    }


    public boolean flag_tele;


    @Override
    public void onSensorChanged(SensorEvent event) {
        //Sensor 发生变化时,在次通过event.values获取数据
        float x = event.values[0];
        float y = event.values[0];
        float z = event.values[0];
        if((x>15 || y>15 || z>15) && flag_zt == false){
            flag_zt = true;

           // Toast.makeText(this,"欢迎使用摇一摇" + ", x = " + x + ", y = " + y + ", z = " + z,Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("我们将在10秒后拨打紧急联系人");
            flag_tele = false;
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    flag_tele = true;
                }
            });
            final AlertDialog dlg = builder.create();
            dlg.show();

            final Timer t = new Timer();
            t.schedule(new TimerTask() {
                public void run() {
                    dlg.dismiss();
                    flag_zt = false;
                    if(flag_tele == false)
                    {
                        call_phone("15665160228");
                    }
                }
            }, 10000);

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void getSensorManager() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        /**
         * 传入的参数决定传感器的类型
         * Senor.TYPE_ACCELEROMETER: 加速度传感器
         * Senor.TYPE_LIGHT:光照传感器
         * Senor.TYPE_GRAVITY:重力传感器
         * SenorManager.getOrientation(); //方向传感器
         */
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(sensorManager != null){
            //一般在Resume方法中注册
            /**
             * 第三个参数决定传感器信息更新速度
             * SensorManager.SENSOR_DELAY_NORMAL:一般
             * SENSOR_DELAY_FASTEST:最快
             * SENSOR_DELAY_GAME:比较快,适合游戏
             * SENSOR_DELAY_UI:慢
             */
            sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(sensorManager != null){
            //解除注册
            sensorManager.unregisterListener(this,sensor);
        }
    }




    private void init() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(false);

        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //关闭缓存机制
        mLocationOption.setLocationCacheEnable(false);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();

    }

    // 应用内动态定位权限请求
    private void getPermissionMethod() {
        List<String> permissionList = new ArrayList<>();

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.CALL_PHONE);
        }

        // Log.i(TAG, "getPermissionMethod: permissionListSize:"+permissionList.size());
        if (!permissionList.isEmpty()){ //权限列表不是空
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this,permissions,1);
        }else{
            // Log.i(TAG, "getPermissionMethod: requestLocation !permissionList.isEmpty()里");
            //requestLocation();
        }
    }


    //动态获取拨打电话权限
    public void calll_quanxian()
    {
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            //首先判断否获取了权限
            if (ActivityCompat.shouldShowRequestPermissionRationale( this,Manifest.permission.CALL_PHONE)) {
                //让用户手动授权
                Toast.makeText(this, "请授权！", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }else{
                ActivityCompat.requestPermissions( this,new String[]{Manifest.permission.CALL_PHONE},1);
            }
        }
    }



    private class MyAMapLocationListener implements AMapLocationListener {

        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            System.out.println("开始这里");
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    double Longitudeld = aMapLocation.getLongitude();
                    double Latitudeld = aMapLocation.getLatitude();



                    //将具体位置赋值给Location
                    Location = aMapLocation.getCountry()+aMapLocation.getProvince()+aMapLocation.getCity();



                    System.out.println("经度：" + Longitudeld + ", 纬度：" + Latitudeld);
                    System.out.println("速度：" + aMapLocation.getSpeed() );
                    //Toast.makeText(MainActivity_log.this,"经度：" + Longitudeld + ", 纬度：" + Latitudeld, Toast.LENGTH_SHORT).show();
                    Log.e("位置：", aMapLocation.getAddress());
                    mLocationClient.stopLocation();//停止定位
                    NumberFormat nf = NumberFormat.getInstance();
                    String tmp = nf.format(Longitudeld);
                    Long_Lat = tmp + ',';
                    tmp = nf.format(Latitudeld);
                    Long_Lat = Long_Lat + tmp;
                    System.out.println("Long_Lat:" + Long_Lat);
                    GetWeather();
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Toast.makeText(MainActivity.this,"定位失败，请检查是否打开GPS", Toast.LENGTH_SHORT).show();
                    Log.e("AmapError", "location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }
        }
    }

    public void GetWeather()
    {
        /**
         * 实况天气
         * 实况天气即为当前时间点的天气状况以及温湿风压等气象指数，具体包含的数据：体感温度、
         * 实测温度、天气状况、风力、风速、风向、相对湿度、大气压强、降水量、能见度等。
         *
         * @param context  上下文
         * @param location 地址详解
         * @param lang     多语言，默认为简体中文，海外城市默认为英文
         * @param unit     单位选择，公制（m）或英制（i），默认为公制单位
         * @param listener 网络访问回调接口
         */
        HeWeather.getWeatherNow(MainActivity.this, Long_Lat, Lang.CHINESE_SIMPLIFIED , Unit.METRIC , new HeWeather.OnResultWeatherNowBeanListener() {
            public static final String TAG="he_feng_now";

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "Weather Now onError: ", e);
                System.out.println("Weather Now Error:"+new Gson());
            }

            @Override
            public void onSuccess(Now dataObject) {
                Log.i(TAG, " Weather Now onSuccess: " + new Gson().toJson(dataObject));
                String jsonData = new Gson().toJson(dataObject);
                String tianqi = null,wendu = null, tianqicode = null;
                if (dataObject.getStatus().equals("ok")){
                    String JsonNow = new Gson().toJson(dataObject.getNow());
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(JsonNow);
                        tianqi = jsonObject.getString("cond_txt");
                        wendu = jsonObject.getString("tmp");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    System.out.println("和风天气有错误");
                    Toast.makeText(MainActivity.this,"有错误",Toast.LENGTH_SHORT).show();
                    return;
                }
                System.out.println("当前天气:"+tianqi);
                System.out.println("当前温度:"+wendu);
                System.out.println("jsonDate:" + jsonData);
                Climate = tianqi;
                Temperature = wendu;
                //weather.setText(jsonData);
//                tv1.setText("当前天气:"+tianqi);
//                tv2.setText("当前温度:"+wendu);
//                tv3.setText(jsonData);
            }
        });
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
            result.setDate(cursor.getString(cursor.getColumnIndex("date")));
            result.setStep(cursor.getString(cursor.getColumnIndex("step")));
            resultinfo.add(result);
            System.out.println(result.toString());
        }
        cursor.close();
        db.close();
    }






    //调用拨打电话
    public void call_phone(String phone) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phone));
        startActivity(intent);
    }



    @Override
    public void onDestroy(){
        super.onDestroy();

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date_now = formatter.format(date);
        SQLiteDatabase db=noteDBOpenHelper.getReadableDatabase();
        db.execSQL("update Noteinfo set step=?where date=?",new Object[]{Step,date_now});
        db.close();



    }















}
