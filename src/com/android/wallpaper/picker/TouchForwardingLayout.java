/*
 * Copyright (C) 2020 The Android Open Source Project
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
package com.android.wallpaper.picker;

import android.annotation.StringRes;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat;

/** A frame layout that listens to touch events and routes them to another view. */
public class TouchForwardingLayout extends FrameLayout {

    private View mView;
    private boolean mForwardingEnabled;
    private GestureDetector mGestureDetector;

    public TouchForwardingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGestureDetector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        return performClick();
                    }
                });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mGestureDetector.onTouchEvent(ev);
        if (mView != null && mForwardingEnabled) {
            mView.dispatchTouchEvent(ev);
        }
        return true;
    }

    /** Set the view that the touch events are routed to */
    public void setTargetView(View view) {
        mView = view;
    }

    public void setForwardingEnabled(boolean forwardingEnabled) {
        mForwardingEnabled = forwardingEnabled;
    }

    /**
     * Sets an Accessibility ACTION_CLICK to describe the TouchForwardingLayout onClick action.
     * @param actionDescriptionRes The String resource describing the talkback double-tap action.
     */
    public void setOnClickAccessibilityDescription(@StringRes int actionDescriptionRes) {
        ViewCompat.setAccessibilityDelegate(this, new AccessibilityDelegateCompat() {
            @Override
            public void onInitializeAccessibilityNodeInfo(View host,
                    AccessibilityNodeInfoCompat info) {
                super.onInitializeAccessibilityNodeInfo(host, info);
                CharSequence description = host.getResources().getString(actionDescriptionRes);
                AccessibilityActionCompat clickAction = new AccessibilityActionCompat(
                        AccessibilityNodeInfoCompat.ACTION_CLICK, description);
                info.addAction(clickAction);
            }
        });
    }
}
