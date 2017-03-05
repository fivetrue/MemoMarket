package com.fivetrue.market.memo.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fivetrue.market.memo.LL;

/**
 * Created by ojin.kwon on 2016-11-18.
 */

public abstract class BaseFragment extends Fragment {

    private static final String TAG = "BaseFragment";

    public abstract String getTitle(Context context);

    public String getSubTitle(Context context){
        return null;
    }

    public abstract int getImageResource();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(LL.D)
            Log.d(TAG, getClass().getSimpleName() + " : onCreate() called with: savedInstanceState = [" + savedInstanceState + "]");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(LL.D) Log.d(TAG, getClass().getSimpleName() + " : onAttach() called with: context = [" + context + "]");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(LL.D) Log.d(TAG, getClass().getSimpleName() + " : onDetach() called");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(LL.D)
            Log.d(TAG, getClass().getSimpleName() + " : onCreateView() called with: inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(LL.D)
            Log.d(TAG, getClass().getSimpleName() + " : onActivityCreated() called with: savedInstanceState = [" + savedInstanceState + "]");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(LL.D) Log.d(TAG, getClass().getSimpleName() + " : onDestroy() called");
    }

    @Override
    public void onStart() {
        super.onStart();
        if(LL.D) Log.d(TAG, getClass().getSimpleName() + " : onStart() called");
    }

    @Override
    public void onStop() {
        super.onStop();
        if(LL.D) Log.d(TAG, getClass().getSimpleName() + " : onStop() called");
    }
}
