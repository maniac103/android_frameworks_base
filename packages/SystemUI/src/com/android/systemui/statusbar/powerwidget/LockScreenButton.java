package com.android.systemui.statusbar.powerwidget;

import com.android.systemui.R;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.Intent;
import android.view.View;

public class LockScreenButton extends PowerButton {
    private KeyguardLock mLock = null;
    private boolean mDisabledLockscreen = false;

    public LockScreenButton() { mType = BUTTON_LOCKSCREEN; }

    @Override
    protected void updateState(Context context) {
        if (!mDisabledLockscreen) {
            mIcon = R.drawable.stat_lock_screen_on;
            mState = STATE_ENABLED;
        } else {
            mIcon = R.drawable.stat_lock_screen_off;
            mState = STATE_DISABLED;
        }
    }

    @Override
    protected void setupButton(View view) {
        super.setupButton(view);

        if (view == null && mDisabledLockscreen) {
            mLock.reenableKeyguard();
            mLock = null;
        } else if (view != null && mDisabledLockscreen) {
            ensureKeyguardLock(view.getContext());
            mLock.disableKeyguard();
        }
    }

    @Override
    protected void toggleState(Context context) {
        ensureKeyguardLock(context);
        if (!mDisabledLockscreen) {
            mLock.disableKeyguard();
            mDisabledLockscreen = true;
        } else {
            mLock.reenableKeyguard();
            mDisabledLockscreen = false;
        }
    }

    @Override
    protected boolean handleLongClick(Context context) {
        Intent intent = new Intent("android.settings.SECURITY_SETTINGS");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        return true;
    }

    private void ensureKeyguardLock(Context context) {
        if (mLock == null) {
            KeyguardManager keyguardManager = (KeyguardManager)
                    context.getSystemService(Context.KEYGUARD_SERVICE);
            mLock = keyguardManager.newKeyguardLock("PowerWidget");
        }
    }
}

