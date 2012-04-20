/*
 * Copyright (C) 2006 The Android Open Source Project
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

package com.android.internal.telephony.gsm;

import android.os.AsyncResult;
import android.os.Handler;
import android.os.Message;
import android.os.RegistrantList;
import android.util.Log;

import com.android.internal.telephony.CommandsInterface;
import com.android.internal.telephony.PhoneFactory;

/**
 * {@hide}
 */
public class AtCmdDelegate {
    private static final String LOG_TAG = "AtCmdDelegate";
    private static final boolean DBG = true;

    private static final int EVENT_NETWORK_STATE_CHANGED = 1;
    private static final int EVENT_UNSOLICITED_OEM_RAW = 2;

    static AtCmdDelegate sInstance;

    CommandsInterface mCM;
    Handler mNetworkStateHandler;
    RegistrantList mNetworkStateRegistrants = new RegistrantList();
    RegistrantList mOemUnsolicitedRegistrants = new RegistrantList();

    public static AtCmdDelegate instance() {
        if (sInstance == null) {
            sInstance = new AtCmdDelegate();
        }

        return sInstance;
    }

    AtCmdDelegate() {
        mNetworkStateHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                AsyncResult ar;

                if (DBG) {
                    Log.d(LOG_TAG, "Handle message " + msg.what);
                }

                switch (msg.what) {
                    case EVENT_NETWORK_STATE_CHANGED:
                        ar = (AsyncResult) msg.obj;
                        mNetworkStateRegistrants.notifyRegistrants(ar);
                        break;
                    case EVENT_UNSOLICITED_OEM_RAW:
                        ar = (AsyncResult) msg.obj;
                        mOemUnsolicitedRegistrants.notifyRegistrants(ar);
                        break;
                }
            }
        };

        mCM = PhoneFactory.getDefaultPhone().getCommandsInterface();
        mCM.registerForNetworkStateChanged(mNetworkStateHandler, EVENT_NETWORK_STATE_CHANGED, null);
        mCM.setOnUnsolOemHookRaw(mNetworkStateHandler, EVENT_UNSOLICITED_OEM_RAW, null);
    }

    public void getGPRSRegistrationState(Message msg) {
        mCM.getGPRSRegistrationState(msg);
    }

    public void getRegistrationState(Message msg) {
        mCM.getRegistrationState(msg);
    }

    public void registerForNetworkStateChanged(Handler h, int what, Object obj) {
        mNetworkStateRegistrants.addUnique(h, what, obj);
    }

    public void unregisterForNetworkStateChanged(Handler h) {
        mNetworkStateRegistrants.remove(h);
    }

    public void registerForOemUnsol(Handler h, int what, Object obj) {
        mOemUnsolicitedRegistrants.addUnique(h, what, obj);
    }

    public void unregisterForOemUnsol(Handler h) {
        mOemUnsolicitedRegistrants.remove(h);
    }
}
