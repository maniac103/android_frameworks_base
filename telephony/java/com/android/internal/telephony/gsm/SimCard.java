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

import android.os.Message;
import android.util.Log;

import com.android.internal.telephony.IccCard;

/**
 * {@hide}
 */
public final class SimCard extends IccCard {

    SimCard(GSMPhone phone) {
        super(phone, "GSM", true);

        mPhone.mCM.registerForSIMLockedOrAbsent(mHandler, EVENT_ICC_LOCKED_OR_ABSENT, null);
        mPhone.mCM.registerForOffOrNotAvailable(mHandler, EVENT_RADIO_OFF_OR_NOT_AVAILABLE, null);
        mPhone.mCM.registerForSIMReady(mHandler, EVENT_ICC_READY, null);
        updateStateProperty();
    }

    @Override
    public void dispose() {
        //Unregister for all events
        mPhone.mCM.unregisterForSIMLockedOrAbsent(mHandler);
        mPhone.mCM.unregisterForOffOrNotAvailable(mHandler);
        mPhone.mCM.unregisterForSIMReady(mHandler);
    }

    @Override
    public String getServiceProviderName () {
        return ((GSMPhone)mPhone).mSIMRecords.getServiceProviderName();
    }

    public void iccIO (int command, int fileid, String path, int p1, int p2, int p3,
            String data, String pin2, Message response) {
        mPhone.mCM.iccIO(command, fileid, path, p1, p2, p3, data, pin2, response);
    }
}
