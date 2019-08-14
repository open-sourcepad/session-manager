package com.sourcepad.opensource.sessionmanager

import android.app.Activity

interface SessionEntry {

    fun getSessionEntryActivity(): Class<Activity>
}