package com.fivetrue.market.memo.viewmodeltest;

import android.app.Application;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.fivetrue.market.memo.viewmodel.TestViewModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

/**
 * Created by kwonojin on 2017. 11. 20..
 */
@RunWith(AndroidJUnit4.class)
public class TestViewModelTest{

    private static final String TAG = "TestViewModelTest";

    Context context;
    TestViewModel viewModel;

    @Before
    public void prepare(){
        context = InstrumentationRegistry.getTargetContext();
        viewModel = new TestViewModel((Application) context.getApplicationContext());
    }

    @Test
    public void test(){
        AppCompatActivity activity = Mockito.mock(AppCompatActivity.class);
        viewModel.getLiveData().observe(activity, s -> {
            Log.d(TAG, "LIVE DATA TEST : " + s);
        });

        viewModel.setDay("Thursday");
        viewModel.setDay("Friday");
    }


}
