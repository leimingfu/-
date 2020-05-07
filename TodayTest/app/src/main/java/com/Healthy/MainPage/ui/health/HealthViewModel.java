package com.Healthy.MainPage.ui.health;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class HealthViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HealthViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}