package com.fivetrue.market.memo.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;

/**
 * Created by kwonojin on 2017. 11. 20..
 */

public class TestViewModel extends AndroidViewModel {

    private static final String FORMAT = "%s is a good day for a hike.";
    private MutableLiveData<String> dayOfWeek = new MutableLiveData<>();

    public TestViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<String> getLiveData(){
        return dayOfWeek;
    }

    public void setDay(String day){
        dayOfWeek.postValue(String.format(FORMAT, day));
    }


}
