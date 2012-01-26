package ru.yandex.money.droid.preferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author dvmelnikov
 */

public class Prefs {

//    private
    private Context context;

    public Prefs(Context context) {
        this.context = context;
    }

    public void write(String name, String value) {
        SharedPreferences sp = context.getSharedPreferences(LibConsts.PREFERENCES,
                Context.MODE_PRIVATE);
        sp.edit().putString(name, value).commit();
    }

    public String read(String name) {
        return read(name, "");
    }
    
    public String read(String name, String defValue) {
        SharedPreferences sp = context.getSharedPreferences(LibConsts.PREFERENCES,
                Context.MODE_PRIVATE);
        return sp.getString(name, defValue);
    }
}
