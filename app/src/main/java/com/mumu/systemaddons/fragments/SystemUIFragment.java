package com.mumu.systemaddons.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by leonardo on 17-10-26.
 */

public class SystemUIFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        TextView text = new TextView(getContext());
        text.setText(getClass().getSimpleName());
        text.setGravity(Gravity.CENTER);
        return text;
    }
}
