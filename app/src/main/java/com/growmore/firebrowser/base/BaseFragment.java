package com.growmore.firebrowser.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;


public class BaseFragment extends Fragment {
    private BaseActivity mActivity;
    public BaseFragment() {
    }
    public static BaseFragment newInstance() {
        BaseFragment fragment = new BaseFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout root = new LinearLayout(getActivity());
        return root;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    public FragmentActivity getContext() {
        if (getActivity() != null) {
            return getActivity();
        }

        return mActivity;
    }

    public void showMessages(int resID){
        Toast.makeText(getContext(), getResources().getText(resID), Toast.LENGTH_SHORT).show();
    }
    public void showMessages(String str){
        Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
    }

}