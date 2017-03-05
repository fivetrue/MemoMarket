package com.fivetrue.market.memo.model.image;

import java.util.List;

/**
 * Created by kwonojin on 2017. 2. 21..
 */

public class ImageEntry {
    private String _type;
    private String webSearchUrl;
    private int totalEstimatedMatches;
    private List<Image> value;

    public String getWebSearchUrl() {
        return webSearchUrl;
    }

    public void setWebSearchUrl(String webSearchUrl) {
        this.webSearchUrl = webSearchUrl;
    }

    public int getTotalEstimatedMatches() {
        return totalEstimatedMatches;
    }

    public void setTotalEstimatedMatches(int totalEstimatedMatches) {
        this.totalEstimatedMatches = totalEstimatedMatches;
    }

    public List<Image> getValue() {
        return value;
    }

    public void setValue(List<Image> value) {
        this.value = value;
    }
}
