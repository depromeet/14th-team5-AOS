package com.no5ing.bbibbi.data.datasource.local

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import com.no5ing.bbibbi.data.model.auth.AuthResult
import com.no5ing.bbibbi.data.model.member.MemberRealEmoji
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataStorage @Inject constructor(val context: Context) {
    private val spec = KeyGenParameterSpec.Builder(
        MasterKey.DEFAULT_MASTER_KEY_ALIAS,
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    )
        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        .setKeySize(MasterKey.DEFAULT_AES_GCM_MASTER_KEY_SIZE)
        .build()

    private val masterKey = MasterKey.Builder(context)
        .setKeyGenParameterSpec(spec)
        .build()

    private val preferences = EncryptedSharedPreferences.create(
        context,
        "Encrypted_Shared_Preferences",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        const val AUTH_RESULT_KEY = "auth_result_key"
        const val LANDING_SEEN_KEY = "landing_seen"
        const val REAL_EMOJI_KEY = "real_emoji"
        const val TEMPORARY_POST_URI = "temporary_post_uri"
        const val WIDGET_POPUP_PERIOD_KEY = "widget_popup_period"
        const val MISSION_WIDGET_PERIOD_KEY = "mission_widget_period"
        const val FAMILY_NAME_FEATURE_MAIN_KEY = "family_name_feature_main"
        const val FAMILY_NAME_FEATURE_FAMILY_KEY = "family_name_feature_family"
    }

    fun logOut() {
        Timber.d("[LocalDataSource] logged out")
        clearPreference()
    }

    private fun clearPreference() {
        Timber.d("[LocalDataSource] clear internal preferences")
        val editor = preferences.edit()

        editor.remove(AUTH_RESULT_KEY)
        //editor.remove(REGISTRATION_TOKEN_KEY)
        editor.apply()
        editor.commit()
    }

    fun getAuthTokens(): AuthResult? {
        val json = preferences.getString(AUTH_RESULT_KEY, null)
        return json?.let { Gson().fromJson(it, AuthResult::class.java) }
    }

    fun setAuthTokens(authResult: AuthResult) {
        val editor = preferences.edit()
        val json = Gson().toJson(authResult)

        editor.putString(AUTH_RESULT_KEY, json)
        editor.apply()
        editor.commit()
    }

    fun setLandingSeen() {
        val editor = preferences.edit()
        editor.putBoolean(LANDING_SEEN_KEY, true)
        editor.apply()
        editor.commit()
    }

    fun getLandingSeen(): Boolean {
        return preferences.getBoolean(LANDING_SEEN_KEY, false)
    }

    fun setRealEmojiList(realEmojiList: List<MemberRealEmoji>) {
        val editor = preferences.edit()
        editor.putString(REAL_EMOJI_KEY, Gson().toJson(realEmojiList))
        editor.apply()
        editor.commit()
    }

    fun getRealEmojiList(): List<MemberRealEmoji> {
        val json = preferences.getString(REAL_EMOJI_KEY, null)
        return json?.let {
            runCatching {
                Gson().fromJson(it, Array<MemberRealEmoji>::class.java).toList()
            }.getOrNull()
        } ?: emptyList()
    }

    fun setTemporaryUri(uri: String) {
        val editor = preferences.edit()
        editor.putString(TEMPORARY_POST_URI, uri)
        editor.apply()
        editor.commit()
    }

    fun clearTemporaryUri() {
        val editor = preferences.edit()
        editor.remove(TEMPORARY_POST_URI)
        editor.apply()
        editor.commit()
    }

    fun getTemporaryUri(): String? {
        return preferences.getString(TEMPORARY_POST_URI, null)
    }

    fun setWidgetPopupPeriod() {
        val editor = preferences.edit()
        editor.putLong(WIDGET_POPUP_PERIOD_KEY, System.currentTimeMillis())
        editor.apply()
        editor.commit()
    }

    fun getAndRemoveWidgetPopupPeriod(): Boolean {
        val period = preferences.getLong(WIDGET_POPUP_PERIOD_KEY, 0)
        if (period == 0L) {
            return false
        }
        val now = System.currentTimeMillis()
        val diff = now - period
        if (diff > 1000 * 60 * 60 * 24 * 3) {
            val editor = preferences.edit()
            editor.remove(WIDGET_POPUP_PERIOD_KEY)
            editor.apply()
            editor.commit()
            return true
        }
        return false
    }

    fun getLastWidgetPopupSeenDate(): LocalDate? {
        val date = preferences.getString(MISSION_WIDGET_PERIOD_KEY, null)
        return date?.let(LocalDate::parse)
    }

    fun setLastWidgetPopupSeenDate(date: LocalDate) {
        val editor = preferences.edit()
        editor.putString(MISSION_WIDGET_PERIOD_KEY, date.toString())
        editor.apply()
        editor.commit()
    }

    fun getFamilyAndMemberNameFeatureMain(): Boolean {
        return preferences.getBoolean(FAMILY_NAME_FEATURE_MAIN_KEY, true)
    }

    fun setFamilyAndMemberNameFeatureMain() {
        val editor = preferences.edit()
        editor.putBoolean(FAMILY_NAME_FEATURE_MAIN_KEY, false)
        editor.apply()
        editor.commit()
    }

    fun getFamilyAndMemberNameFeatureFamily(): Boolean {
        return preferences.getBoolean(FAMILY_NAME_FEATURE_FAMILY_KEY, true)
    }

    fun setFamilyAndMemberNameFeatureFamily() {
        val editor = preferences.edit()
        editor.putBoolean(FAMILY_NAME_FEATURE_FAMILY_KEY, false)
        editor.apply()
        editor.commit()
    }
}