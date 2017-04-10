package com.fivetrue.market.memo.utils;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by kwonojin on 2017. 4. 7..
 */

public class MediaScannerUtil {
    private Context mContext;

    private String mPath;

    private MediaScannerConnection mMediaScanner;
    private MediaScannerConnection.MediaScannerConnectionClient mMediaScannerClient;

    private PublishSubject<ScanData> mPublishSubject = PublishSubject.create();

    private static MediaScannerUtil sInstance;

    public static MediaScannerUtil getInstance(Context context) {
        if(sInstance == null){
            sInstance = new MediaScannerUtil(context);
        }
        return sInstance;
    }

    private MediaScannerUtil(Context context) {
        mContext = context;
    }

    public Observable<ScanData> mediaScanning(final String path) {
        if (mMediaScanner == null) {
            mMediaScannerClient = new MediaScannerConnection.MediaScannerConnectionClient() {

                @Override
                public void onMediaScannerConnected() {
                    mMediaScanner.scanFile(mPath, null); // 디렉토리
                    // 가져옴
                }

                @Override
                public void onScanCompleted(String s, Uri uri) {
                    mMediaScanner.disconnect();
                    mPublishSubject.onNext(new ScanData(s, uri));
                    mPublishSubject.publish();
                }

            };
            mMediaScanner = new MediaScannerConnection(mContext, mMediaScannerClient);
        }
        mPath = path;
        mMediaScanner.connect();
        return mPublishSubject;
    }

    public static final class ScanData {
        public final String path;
        public final Uri uri;
        public ScanData(String path, Uri uri){
            this.path = path;
            this.uri = uri;
        }

        @Override
        public String toString() {
            return "ScanData{" +
                    "path='" + path + '\'' +
                    ", uri=" + uri +
                    '}';
        }
    }
}