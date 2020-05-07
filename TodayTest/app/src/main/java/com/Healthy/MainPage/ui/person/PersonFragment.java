package com.Healthy.MainPage.ui.person;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;

import com.Healthy.MainPage.InfoActivity;
import com.Healthy.MainPage.R;

import java.text.DecimalFormat;



public class PersonFragment extends Fragment {
    TextView tv_h;
    TextView tv_w;
    TextView tv_tel;
    private PersonViewModel notificationsViewModel;
    public double bmi=0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(PersonViewModel.class);
        View root = inflater.inflate(R.layout.fragment_person, container, false);
//        final TextView textView = root.findViewById(R.id.text_notifications);
//        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        tv_h=(TextView)root.findViewById(R.id.tv_h);
        tv_w=(TextView)root.findViewById(R.id.tv_w);
        tv_tel=(TextView)root.findViewById(R.id.tv_p);
        tv_h.setText(InfoActivity.h);
        tv_w.setText(InfoActivity.w);
        String s=InfoActivity.tel;
        String s1=s.substring(3,7);
        String s2=s.replace(s1,"****");
        tv_tel.setText(s2);
        return root;
    }
    public void onResume(){
        super.onResume();
        String h=tv_h.getText().toString();
        String w=tv_w.getText().toString();
        double a=Double.parseDouble(h);
        double b=Double.parseDouble(w);
        bmi=b/(a*a);
        TextView tv_bmi=getView().findViewById(R.id.tv_bmi);
        DecimalFormat df=new DecimalFormat("#0.00");
        String ans=df.format(bmi);
        tv_bmi.setText(ans);
    }
}
