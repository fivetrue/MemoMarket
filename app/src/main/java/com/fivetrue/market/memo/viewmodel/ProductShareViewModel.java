package com.fivetrue.market.memo.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.fivetrue.market.memo.LL;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.model.Product;
import com.fivetrue.market.memo.share.kakao.ProductKakaoLinkItem;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.TemplateParams;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;

import java.util.List;

import io.reactivex.Completable;

/**
 * Created by kwonojin on 2017. 11. 21..
 */

public class ProductShareViewModel extends AndroidViewModel {

    private static final String TAG = "ProductShareViewModel";

    public ProductShareViewModel(@NonNull Application application) {
        super(application);
    }

    public Completable shareToKakaoTalk(String message, List<Product> products){
        return Completable.create(e -> {
            ProductKakaoLinkItem shareItem = new ProductKakaoLinkItem(message, products);

            LinkObject linkObject = LinkObject
                    .newBuilder()
                    .setAndroidExecutionParams(shareItem.getParameters())
                    .setMobileWebUrl(getApplication().getString(R.string.market_memo_kakao_link_url))
                    .setWebUrl(getApplication().getString(R.string.market_memo_kakao_link_url)).build();

            ContentObject contentObject = ContentObject
                    .newBuilder(shareItem.getTitle(), shareItem.getImageUrl(), linkObject)
                    .setDescrption(shareItem.getDescription()).build();

            TemplateParams params = FeedTemplate.newBuilder(contentObject).build();

            KakaoLinkService.getInstance().sendDefault(getApplication(), params, new ResponseCallback<KakaoLinkResponse>() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    if(LL.D)
                        Log.d(TAG, "onFailure() called with: errorResult = [" + errorResult + "]");
                    e.onError(errorResult.getException());
                }

                @Override
                public void onSuccess(KakaoLinkResponse result) {
                    // 템플릿 밸리데이션과 쿼터 체크가 성공적으로 끝남. 톡에서 정상적으로 보내졌는지 보장은 할 수 없다.
                    if(LL.D) Log.d(TAG, "onSuccess() called with: result = [" + result + "]");
                    e.onComplete();
                }
            });
        });
    }

}
