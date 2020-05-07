package com.Healthy.MainPage.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Trace;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.widget.Toast;

import com.Healthy.MainPage.MainActivity;
import com.Healthy.MainPage.R;

import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {
//    private final Timer timer=new Timer();
//    private TimerTask task;
    private Handler handler=new Handler();
    private Runnable runnable =new Runnable() {
        @Override
        public void run() {
            this.update();
            handler.postDelayed(this,2000);
        }
        void update(){
            if(MainActivity.Climate!="1"){
                wh=MainActivity.Climate+" "+MainActivity.Temperature+"℃";
                tv_whtip.setText(wh);
            }
        }
    };
    public String wh;
    TextView tv_whtip;
    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
//        final TextView textView = root.findViewById(R.id.text_home);
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        tv_whtip=(TextView)root.findViewById(R.id.tv_whtip);
        handler.postDelayed(runnable,2000);
        return root;
    }
    public void onDestroy(){
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Button button = (Button) getActivity().findViewById(R.id.btn1);
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Toast.makeText(getActivity(), "t1", Toast.LENGTH_LONG).show();
            }
        });
    }
}
