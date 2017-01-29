package com.fivetrue.market.memo.firebase;

import android.util.Log;

import com.google.firebase.database.ServerValue;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kwonojin on 2016. 10. 19..
 */

public abstract class FirebaseData {

    private static final String TAG = "FirebaseData";

    public long updateTime;

    public Map<String, Object> getValues(){
        Field[] fields = getClass().getDeclaredFields();
        Map<String, Object> map = new HashMap<>();
        for(Field f : fields){
            if(!java.lang.reflect.Modifier.isStatic(f.getModifiers())){
                try {
                    Object object = f.get(this);
                    if(object instanceof FirebaseData){
                        map.put(f.getName(), ((FirebaseData) object).getValues());
                    }else{
                        map.put(f.getName(), object);
                    }
                } catch (IllegalAccessException e) {
                    Log.e(TAG, "error convert to Map", e);
                }
            }
        }
        map.put("updateTime", ServerValue.TIMESTAMP);
        return map;
    };

    public long getUpdateTime(){
        return updateTime;
    }
}
