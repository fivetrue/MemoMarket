package com.fivetrue.market.memo.model.image;

/**
 * Created by kwonojin on 2017. 2. 21..
 */

public class Image {

    public String name;
    public String webSearchUrl;
    public String thumbnailUrl;
    public String datePublished;
    public String contentUrl;
    public String hostPageUrl;
    public String contentSize;
    public String encodingFormat;
    public String hostPageDisplayUrl;
    public int width;
    public int height;
    public ThumbnailSize thumbnail;
    public String imageInsightsToken;
    public String imageId;
    public String accentColor;

    public static final class ThumbnailSize {
        public int width;
        public int height;
    }

}
