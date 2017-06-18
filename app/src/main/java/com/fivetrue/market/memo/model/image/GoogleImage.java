package com.fivetrue.market.memo.model.image;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kwonojin on 2017. 2. 21..
 */


public class GoogleImage implements Parcelable {

    public int cr;
    public String id;
    public String isu;
    public String itg;
    public String ity;
    public int oh;
    public String ou;
    public int ow;
    public String pt;
    public String rid;
    public String ru;
    public String s;
    public String st;
    public String tu;
    public int th;
    public int tw;

    protected GoogleImage(Parcel in) {
        cr = in.readInt();
        id = in.readString();
        isu = in.readString();
        itg = in.readString();
        ity = in.readString();
        oh = in.readInt();
        ou = in.readString();
        ow = in.readInt();
        pt = in.readString();
        rid = in.readString();
        ru = in.readString();
        s = in.readString();
        st = in.readString();
        tu = in.readString();
        th = in.readInt();
        tw = in.readInt();
    }

    public static final Creator<GoogleImage> CREATOR = new Creator<GoogleImage>() {
        @Override
        public GoogleImage createFromParcel(Parcel in) {
            return new GoogleImage(in);
        }

        @Override
        public GoogleImage[] newArray(int size) {
            return new GoogleImage[size];
        }
    };

    public String getId(){
        return id;
    }

    public String getSiteUrl(){
        return isu;
    }

    public String getPageUrl(){
        return ru;
    }

    public String getImageMimeType(){
        return ity;
    }

    public int getOriginalWidth(){
        return ow;
    }

    public int getOriginalHeight(){
        return oh;
    }

    public String getOriginalImageUrl(){
        return ou;
    }

    public String getPageText(){
        return pt;
    }

    public String getSubject(){
        return s;
    }

    public String getSiteTitle(){
        return st;
    }

    public String getRid(){
        return rid;
    }

    public String getThumbnailUrl(){
        return tu;
    }

    public int getThumbnailWidth(){
        return tw;
    }

    public int getThumbnailHeight(){
        return th;
    };

    public boolean isGif(){
        return ity != null && ity.equalsIgnoreCase("GIF");
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(cr);
        parcel.writeString(id);
        parcel.writeString(isu);
        parcel.writeString(itg);
        parcel.writeString(ity);
        parcel.writeInt(oh);
        parcel.writeString(ou);
        parcel.writeInt(ow);
        parcel.writeString(pt);
        parcel.writeString(rid);
        parcel.writeString(ru);
        parcel.writeString(s);
        parcel.writeString(st);
        parcel.writeString(tu);
        parcel.writeInt(th);
        parcel.writeInt(tw);
    }
}
