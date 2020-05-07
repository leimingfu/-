package com.Healthy.MainPage.ui.person;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class PersonViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PersonViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}