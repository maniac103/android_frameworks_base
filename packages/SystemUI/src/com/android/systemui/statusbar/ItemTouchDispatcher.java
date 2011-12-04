/*
* Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.systemui.statusbar;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.android.systemui.R;

public class ItemTouchDispatcher {
    private final GestureDetector mGestureDetector;
    private LatestItemContainer mItem;

    public ItemTouchDispatcher(final Context context) {
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float vX, float vY) {
                final ViewConfiguration vc = ViewConfiguration.get(context);
                int minDistance = vc.getScaledTouchSlop();
                int distance = (int) Math.abs(e2.getX() - e1.getX());
                if (distance > minDistance && Math.abs(vX) > Math.abs(vY)) {
                    mItem.finishSwipe(vX > 0);
                    return true;
                }
                return false;
            }
        });
    }

    public void setItem(LatestItemContainer item) {
        mItem = item;
    }

    public boolean needsInterceptTouch() {
        return mItem != null;
    }

    public boolean handleTouchEvent(MotionEvent event) {
        boolean handled = mGestureDetector.onTouchEvent(event);
        if (mItem != null) {
            mItem.dispatchTouchEvent(event);

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    mItem.stopSwipe();
                    mItem = null;
                    break;
            }
        }
        return handled;
    }
}
