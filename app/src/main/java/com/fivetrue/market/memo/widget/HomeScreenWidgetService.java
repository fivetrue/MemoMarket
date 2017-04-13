package com.fivetrue.market.memo.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by kwonojin on 2017. 4. 13..
 */

public class HomeScreenWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return(new HomeRemoteViewsFactory(this.getApplicationContext(),
                intent));
    }
}
