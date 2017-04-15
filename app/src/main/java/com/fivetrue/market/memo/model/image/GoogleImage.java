package com.fivetrue.market.memo.model.image;

/**
 * Created by kwonojin on 2017. 2. 21..
 */


public class GoogleImage {

    public int cr;
    public String id;
    public String isu;
    public boolean itg;
    public String ity;
    public int oh;
    public String ou;
    public int ow;
    public String pt;
    public String rid;
    public String ru;
    public String s;
    public String st;
    public int th;
    public String tu;
    public String tw;

    public String getImageUrl(){
        return tu;
    }

    @Override
    public String toString() {
        return "GoogleImage{" +
                "cr=" + cr +
                ", id='" + id + '\'' +
                ", isu='" + isu + '\'' +
                ", itg=" + itg +
                ", ity='" + ity + '\'' +
                ", oh=" + oh +
                ", ou='" + ou + '\'' +
                ", ow=" + ow +
                ", pt='" + pt + '\'' +
                ", rid='" + rid + '\'' +
                ", ru='" + ru + '\'' +
                ", s='" + s + '\'' +
                ", st='" + st + '\'' +
                ", th=" + th +
                ", tu='" + tu + '\'' +
                ", tw='" + tw + '\'' +
                '}';
    }
}
