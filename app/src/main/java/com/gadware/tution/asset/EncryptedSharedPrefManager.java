package com.gadware.tution.asset;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import androidx.annotation.NonNull;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;


import java.io.IOException;
import java.security.GeneralSecurityException;

import static androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV;
import static androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM;

public class EncryptedSharedPrefManager {

    @NonNull

    private static final String SHARED_PREF_NAME = "my_en_sh_pre.txt";

    public static EncryptedSharedPrefManager mInstance;
     Context mCtx;

    @NonNull

    public static MasterKey masterKey;


    KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(
            MasterKey.DEFAULT_MASTER_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(MasterKey.DEFAULT_AES_GCM_MASTER_KEY_SIZE)
            .build();

    {
        try {
            masterKey = new MasterKey.Builder(mCtx)
                    .setKeyGenParameterSpec(spec)
                    .build();
        } catch (GeneralSecurityException | IOException generalSecurityException) {
            generalSecurityException.printStackTrace();
        }
    }


 //        try {
//
//            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(mCtx, SHARED_PREF_NAME, masterKey, AES256_SIV, AES256_GCM);
//        } catch (GeneralSecurityException | IOException e) {
//            e.printStackTrace();
//        }

    public EncryptedSharedPrefManager(@org.jetbrains.annotations.NotNull Context mCtx) {
        this.mCtx = mCtx;
    }


    public static synchronized EncryptedSharedPrefManager getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new EncryptedSharedPrefManager(mCtx);
        }
        return mInstance;
    }


    public void saveData(String key, String value) throws GeneralSecurityException, IOException {

        SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(mCtx, SHARED_PREF_NAME, masterKey, AES256_SIV, AES256_GCM);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();

    }

    public String getData(String key) throws GeneralSecurityException, IOException {
        SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(mCtx, SHARED_PREF_NAME, masterKey, AES256_SIV, AES256_GCM);
        return sharedPreferences.getString(key, "null");
    }

    public void removeData(String key) throws GeneralSecurityException, IOException {
        SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(mCtx, SHARED_PREF_NAME, masterKey, AES256_SIV, AES256_GCM);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public void clearAllData() throws GeneralSecurityException, IOException {
        SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(mCtx, SHARED_PREF_NAME, masterKey, AES256_SIV, AES256_GCM);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
