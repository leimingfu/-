package com.Healthy.MainPage.ui.health;

import android.os.Bundle;
import android.os.Handler;
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



public class HealthFragment extends Fragment {
    TextView tv_loc;
    TextView tv_step;
    private Handler handler=new Handler();
    private Runnable runnable =new Runnable() {
        @Override
        public void run() {
            this.update();
            handler.postDelayed(this,3000);
        }
        void update(){
            if(MainActivity.Location!="1")
            {
                tv_loc.setText(MainActivity.Location);
                String s=String.valueOf(MainActivity.Step);
                tv_step.setText(s);
            }
        }
    };

    private HealthViewModel dashboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(HealthViewModel.class);
        View root = inflater.inflate(R.layout.fragment_health, container, false);
//        final TextView textView = root.findViewById(R.id.text_dashboard);
//        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        //Toast.makeText(getActivity(), "t1", Toast.LENGTH_LONG).show();

        tv_loc=(TextView)root.findViewById(R.id.tv_loc);
        tv_step=(TextView)root.findViewById(R.id.tv_step);
        handler.postDelayed(runnable,2000);
        return root;
    }
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Button button = (Button) getActivity().findViewById(R.id.btn2);
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Toast.makeText(getActivity(), "t2", Toast.LENGTH_LONG).show();
                TextView tv_loc=getView().findViewById(R.id.tv_loc);
                tv_loc.setText("Yunnan");
            }
        });
    }
    public void onDestroy(){
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }
}
