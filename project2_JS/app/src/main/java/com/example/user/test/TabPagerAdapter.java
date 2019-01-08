package com.example.user.test;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

public class TabPagerAdapter extends FragmentStatePagerAdapter {

    private  int tabCount;
    private String user_id;
    private String user_name;

    //private Context context;

    public TabPagerAdapter(FragmentManager fm, int tabCount, String id, String name) {
        super(fm);
        this.tabCount = tabCount;
        this.user_id = id;
        this.user_name = name;

    }
    @Override
    public Fragment getItem(int position){

        switch(position) {
            case 0:
                Fragment1 tabFragment1 = new Fragment1();
                Bundle bundle1 = new Bundle();
                bundle1.putString("id",user_id);
                tabFragment1.setArguments(bundle1);
                return tabFragment1;

            case 1:
                Fragment3 tabFragment2 = new Fragment3();
                Bundle bundle2 = new Bundle();
                bundle2.putString("id", user_id);
                bundle2.putString("name",user_name);
                tabFragment2.setArguments(bundle2);
                return tabFragment2;

            case 2:
                Fragment2 tabFragment3 = new Fragment2();
                Bundle bundle3= new Bundle();
                bundle3.putString("id",user_id);
                tabFragment3.setArguments(bundle3);
                return tabFragment3;

            default :
                return null;
        }
    }

    @Override
    public int getCount(){
        return tabCount;
    }
}
