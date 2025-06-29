package com.example.connectify.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.connectify.Fragment.CallsFragment;
import com.example.connectify.Fragment.ChatsFragment;
import com.example.connectify.Fragment.GroupsFragment;
import com.example.connectify.Fragment.StatusFragment;

public class ViewPagerMessageAdapter extends FragmentPagerAdapter {
    public ViewPagerMessageAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) return new ChatsFragment();
        else if (position ==1) return new GroupsFragment();
        else if(position == 2) return new StatusFragment();
        else return new CallsFragment();
    }

    @Override
    public int getCount() {
        return 4; // no of tabs
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) return "Chats";
        else if (position ==1) return "Groups";
        else if(position == 2) return "Status";
        else return "Calls";
    }
}
