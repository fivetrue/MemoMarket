package com.fivetrue.market.memo.ui.fragment.transition;

import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.TransitionSet;

public class ImageTransition extends TransitionSet {

    public ImageTransition() {
        setOrdering(ORDERING_TOGETHER);
        addTransition(new ChangeImageTransform());
    }
}