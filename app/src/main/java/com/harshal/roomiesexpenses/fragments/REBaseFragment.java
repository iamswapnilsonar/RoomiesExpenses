package com.harshal.roomiesexpenses.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.harshal.roomiesexpenses.listener.FragmentChangedListener;
import com.harshal.roomiesexpenses.utils.Util;

/**
 * Created by ceroroot on 31/5/16.
 */
public class REBaseFragment extends Fragment {
    protected AppCompatActivity mContext;
    protected FragmentChangedListener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = (AppCompatActivity) getActivity();
    }

    public void setListener(FragmentChangedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(listener!=null)
            listener.onFragmentChanged(getArguments().getInt(Util.ARG_SECTION_NUMBER));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(mContext!=null)
            hideKeyboard(mContext);
    }

    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        // check if no view has focus:
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;
        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}
