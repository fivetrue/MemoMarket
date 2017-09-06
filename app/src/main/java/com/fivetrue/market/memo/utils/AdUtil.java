package com.fivetrue.market.memo.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.view.ViewGroup;

import com.fivetrue.market.memo.LL;
import com.fivetrue.market.memo.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;

/**
 * Created by kwonojin on 2017. 4. 15..
 */

public class AdUtil extends AdListener {

    private static final String TAG = "AdUtil";

    public static final String AD_PRODUCT_ADD = "product_add";
    public static final String AD_PRODUCT_CHECK_OUT = "product_check_out";

    private static final int REFRESH_COUNT = 2;

    private Map<String, AdView> mAdViewMap = new HashMap<>();
    private AdRequest.Builder mAdRequestBuilder;
    private Context mContext;

    private static AdUtil sInstance;

    public static void init(Context context){
        sInstance = new AdUtil(context);

    }

    public static AdUtil getInstance(){
        return sInstance;
    }

    private AdUtil(Context context){
        mContext = context;
        DataManager.getInstance(context).getGeoLocation()
                .subscribe(geoLocation -> {
                    if(LL.D) Log.d(TAG, "loadAd: geoLocation : " + geoLocation);
                    if(geoLocation != null){
                        Location location = new Location(LocationManager.NETWORK_PROVIDER);
                        location.setAccuracy(geoLocation.getAccuracy());
                        location.setLatitude(geoLocation.getLocation().getLat());
                        location.setLongitude(geoLocation.getLocation().getLng());
                        setLocation(location);
                    }
                }, throwable -> {
                    Log.e(TAG, "fail getGeoLocation ", throwable);
                    setLocation(null);
                });
    }

    private void setLocation(Location location){
        mAdRequestBuilder = new AdRequest.Builder();
        if(location != null){
            mAdRequestBuilder.setLocation(location);
        }
        mAdRequestBuilder.setGender(AdRequest.GENDER_FEMALE)
                .addKeyword("Baby")
                .addKeyword("아기")
                .addKeyword("집")
                .addKeyword("House")
                .addKeyword("옷")
                .addKeyword("Cloth")
                .addKeyword("유행")
                .addKeyword("Trend")
                .addKeyword("Fashion")
                .addKeyword("패션")
                .addKeyword("보육")
                .addKeyword("Baby Care");

//        mAdViewMap.put(AD_PRODUCT_ADD, makeAdView(mContext.getString(R.string.admob_product_add_bottom), AdSize.MEDIUM_RECTANGLE));
//        mAdViewMap.put(AD_PRODUCT_CHECK_OUT, makeAdView(mContext.getString(R.string.admob_product_checkout_bottom), AdSize.MEDIUM_RECTANGLE));
        mAdViewMap.put(AD_PRODUCT_ADD, makeAdView(mContext.getString(R.string.admob_product_add_bottom), AdSize.LARGE_BANNER));
        mAdViewMap.put(AD_PRODUCT_CHECK_OUT, makeAdView(mContext.getString(R.string.admob_product_checkout_bottom), AdSize.LARGE_BANNER));
        mAdViewMap.get(AD_PRODUCT_ADD).loadAd(mAdRequestBuilder.build());
        mAdViewMap.get(AD_PRODUCT_CHECK_OUT).loadAd(mAdRequestBuilder.build());
    }

    private AdView makeAdView(String unitId, AdSize adSize){
        AdView adView = new AdView(mContext);
        adView.setAdSize(adSize);
        adView.setAdUnitId(unitId);
        adView.setAdListener(this);
        return adView;
    }

    public void addAdView(ViewGroup parent, String adType, boolean refresh){
        AdView adView =  mAdViewMap.get(adType);
        if(adView != null && parent != null){
            Integer count = (Integer) adView.getTag();
            if((count != null && count > REFRESH_COUNT) || refresh){
                adView.loadAd(mAdRequestBuilder.build());
                adView.setTag(0);
            }
            if(count == null){
                count = 1;
            }
            count ++;
            adView.setTag(count);
            if(adView.getParent() == null){
                parent.addView(adView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }else{
                try{
                    ViewGroup viewGroup = (ViewGroup) adView.getParent();
                    viewGroup.removeView(adView);
                }catch (Exception e){
                    Log.e(TAG, "addAdView: ", e);
                    TrackingUtil.getInstance().report(e);
                }
            }
        }
    }

    public void detachAdView(String adType){
        AdView view = mAdViewMap.get(adType);
        if(view != null && view.getParent() != null){
            ViewGroup parent = (ViewGroup) view.getParent();
            parent.removeView(view);
        }
    }

    @Override
    public void onAdClosed() {
        super.onAdClosed();
        Log.i(TAG, "onAdClosed: ");
    }

    @Override
    public void onAdFailedToLoad(int i) {
        super.onAdFailedToLoad(i);
        Log.i(TAG, "onAdFailedToLoad: " + i);
    }

    @Override
    public void onAdLeftApplication() {
        super.onAdLeftApplication();
        Log.i(TAG, "onAdLeftApplication: ");
    }

    @Override
    public void onAdOpened() {
        super.onAdOpened();
        Log.i(TAG, "onAdOpened: ");
    }

    @Override
    public void onAdLoaded() {
        super.onAdLoaded();
        Log.i(TAG, "onAdLoaded: ");
    }

    public Observable<AdRequest.Builder> getAdRequestBuilder(){
        return Observable.create(e -> {
            if(mAdRequestBuilder != null){
                e.onNext(mAdRequestBuilder);
                e.onComplete();
            }else{
                DataManager.getInstance(mContext).getGeoLocation()
                        .subscribe(geoLocation -> {
                            if(LL.D) Log.d(TAG, "loadAd: geoLocation : " + geoLocation);
                            if(geoLocation != null){
                                Location location = new Location(LocationManager.NETWORK_PROVIDER);
                                location.setAccuracy(geoLocation.getAccuracy());
                                location.setLatitude(geoLocation.getLocation().getLat());
                                location.setLongitude(geoLocation.getLocation().getLng());
                                setLocation(location);
                                e.onNext(mAdRequestBuilder);
                                e.onComplete();
                            }
                        }, throwable -> {
                            Log.e(TAG, "fail getGeoLocation ", throwable);
                            setLocation(null);
                            e.onNext(mAdRequestBuilder);
                            e.onComplete();
                        });
            }
        });
    }
}
