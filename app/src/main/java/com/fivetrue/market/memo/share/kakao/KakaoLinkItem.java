package com.fivetrue.market.memo.share.kakao;

import com.fivetrue.market.memo.share.ShareItem;

import java.util.Map;

/**
 * Created by kwonojin on 2017. 12. 12..
 */

public interface KakaoLinkItem extends ShareItem {

    String LINK_URL = "${SCRAP_REQUEST_URL}";
    String LINK_HOST = "${SCRAP_HOST}";
    String LINK_TITLE = "${SCRAP_TITLE}";
    String LINK_DESCRIPTION = "${SCRAP_DESCRIPTION}";
    String LINK_IMAGE = "${SCRAP_IMAGE}";
    String LINK_IMAGE_WIDTH = "${SCRAP_IMAGE_WIDTH}";
    String LINK_IMAGE_HEIGHT = "${SCRAP_IMAGE_HEIGHT}";
    String LINK_DURATION = "${SCRAP_IMAGE_HEIGHT}";

}
