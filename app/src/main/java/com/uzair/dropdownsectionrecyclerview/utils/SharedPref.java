package com.uzair.dropdownsectionrecyclerview.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref
{
    private static SharedPreferences mSharedPref;

    public static void init(Context context)
    {
        if(mSharedPref == null)
            mSharedPref = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
    }

    public static void storeType( String value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putString("type", value);
        prefsEditor.commit();
    }

    public static String getType() {

        return mSharedPref.getString("type","Product");
    }

    public static void setLanguage(String language)
    {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putString("lang", language);
        prefsEditor.commit();
    }

    public static String getLanguage()
    {
        return mSharedPref.getString("lang","en");
    }
}
