package com.fivetrue.market.memo.ui.fragment.transition;

import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;

public class MoveTransition extends TransitionSet {

    public MoveTransition() {
        setOrdering(ORDERING_TOGETHER);
        addTransition(new ChangeBounds());
    }
}