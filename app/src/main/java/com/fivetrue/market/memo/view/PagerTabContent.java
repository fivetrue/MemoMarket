package com.fivetrue.market.memo.view;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * Created by ojin.kwon on 2016-04-26.
 */
public interface PagerTabContent {

    enum TabIcon{
        Text, Icon, TextWithIcon
    }

    String getTabTitle(Context context);
    Drawable getTabDrawable(Context context);

    TabIcon getIconState();
}
