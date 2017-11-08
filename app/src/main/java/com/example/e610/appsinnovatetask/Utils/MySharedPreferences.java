package com.example.e610.appsinnovatetask.Utils;

import android.content.Context;
import android.content.SharedPreferences;



public class  MySharedPreferences {




    static Context context;
    static String FileName;
    static SharedPreferences sharedPref;
    static SharedPreferences.Editor editor;

    public static void setUpMySharedPreferences(Context context_,String FileName_){
         context=context_;
        FileName=FileName_;
        sharedPref = context.getSharedPreferences(FileName, Context.MODE_PRIVATE);
        editor=sharedPref.edit();
    }
    public static void SaveAccessToken(String key,String value){
        //key -> userID
        editor.putString(key,value);
        editor.commit();
    }

    public static String getAccessToken(String key){

        String UserSetting=sharedPref.getString(key,"");

        return UserSetting;
    }

    public static boolean IsFirstTime(){
        String check=sharedPref.getString("FirstTime","");

        if(check.equals("yes"))
            return false;
         return true;
    }

    public static void FirstTime(){
        editor.putString("FirstTime","yes");
        editor.commit();
    }

    public void Clear(){
        editor.clear();
        editor.commit();
    }

}
