package com.fivetrue.market.memo.data;

import android.content.Context;
import android.util.Log;

import com.fivetrue.market.memo.LL;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.model.Image;
import com.fivetrue.market.memo.model.remote.GeoLocation;
import com.fivetrue.market.memo.model.image.GoogleImage;
import com.fivetrue.market.memo.net.NetworkServiceProvider;
import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by kwonojin on 2017. 2. 23..
 */

public class NetworkData implements Repository {

    private static final String TAG = "NetworkData";

    private Context mContext;

    private static NetworkData sInstance;

    public static NetworkData getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new NetworkData(context);
        }
        return sInstance;
    }

    private NetworkData(Context context) {
        mContext = context;
    }

    public Observable<GeoLocation> getGeoLocation() {
        return NetworkServiceProvider.getInstance()
                .getGoogleApiService().getGeoLocation(mContext.getString(R.string.market_google_api_key))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread());
    }

    public Observable<List<Image>> findImages(String q, int count) {
        return Observable.create((ObservableOnSubscribe<List<Image>>) e -> {
            if(LL.D)
                Log.d(TAG, "findImages() called with: q = [" + q + "], count = [" + count + "]");
            String keyword = q.replaceAll(" ", "+");
            Log.i(TAG, "findImages: keyword =" + keyword);
            try {
                String googleUrl = "https://www.google.co.kr/search?tbm=isch&q=" + keyword + "&ijn="+count;
                if(LL.D) Log.d(TAG, "findImage: googleUrl = " + googleUrl);
                Document doc = Jsoup.connect(googleUrl).timeout(10 * 1000).get();
                Elements elements = doc.select("div.rg_meta");
                Observable.fromIterable(elements)
                        .map(element-> {
                            String json = element.childNode(0).toString().trim();
                            GoogleImage image = new Gson().fromJson(json, GoogleImage.class);
                            return (Image)image;
                        }).toList().subscribe(images -> {
                    e.onNext(images);
                });

            } catch (Exception e1) {
                Log.e(TAG, "findImage: ", e1);
                e.onError(e1);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread());
    }
}
