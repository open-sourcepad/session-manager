package com.sourcepad.opensource.sessionmanager

import android.accounts.Account
import android.accounts.AccountManager
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.core.content.edit
import androidx.preference.PreferenceManager


@Suppress("unused")
open class SessionManager(context: Context) {
    private val accountType: String = context.getString(R.string.account_type)
    private val accountManager: AccountManager = AccountManager.get(context)
    private val preference: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun getCurrentAccount(): Account? {
        val accounts = accountManager.getAccountsByType(accountType)
        if (accounts.isNotEmpty()) {
            return accounts[0]
        }
        return null
    }

    fun getAuthToken(): String? {
        val account = getCurrentAccount()
        return account?.let {
            accountManager.peekAuthToken(
                getCurrentAccount(),
                AccountAuthenticator.AUTHTOKEN_TYPE_FULL_ACCESS
            )
        }
    }

    fun<T> getData(){
        preference
    }


    fun save(user: String, password: String?, authToken: String) {
        if (getCurrentAccount() != null) {
            end()
        }
        accountManager.addAccountExplicitly(Account(user, accountType), password, null)
        accountManager.setAuthToken(
            getCurrentAccount(),
            AccountAuthenticator.AUTHTOKEN_TYPE_FULL_ACCESS,
            authToken
        )
    }


    fun <T> saveUserData(data: T, key: String) {
        when (data) {

            is Int -> {
                preference.edit {
                    putInt(key, data)
                    commit()
                }
            }
            is Double -> {

            }
            is Long -> {

            }
            is String -> {

            }

            is Float -> {

            }
        }
    }

    fun getUserData(key: String, defaultData: String? = null): String? {
        return accountManager.getUserData(getCurrentAccount(), key) ?: defaultData
    }

    fun getLong(key: String): Long {
        return accountManager.getUserData(getCurrentAccount(), key)?.toLongOrNull() ?: 0L
    }

    fun getInt(key: String): Int {
        return accountManager.getUserData(getCurrentAccount(), key)?.toIntOrNull() ?: 0
    }

    fun end() {

        if (getCurrentAccount() != null) {
            accountManager.invalidateAuthToken(accountType, getAuthToken())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                accountManager.removeAccountExplicitly(getCurrentAccount())
            } else {
                @Suppress("DEPRECATION")
                accountManager.removeAccount(getCurrentAccount(), null, null)
            }
        }

    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun isLoggedIn(): Boolean {
        return getAuthToken() != null
    }

    fun checkUserAuth(activity: Activity) {
        if (!isLoggedIn()) {
            accountManager.addAccount(
                accountType, AccountAuthenticator.AUTHTOKEN_TYPE_FULL_ACCESS, null, null, activity,
                null, null
            )
            activity.finish()
        }
    }


}