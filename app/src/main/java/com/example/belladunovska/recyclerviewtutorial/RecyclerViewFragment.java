package com.example.belladunovska.recyclerviewtutorial;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.GregorianCalendar;


public class RecyclerViewFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerViewAdapter mShoppingAdapter;

    private String[] mDataset;


    public static RecyclerViewFragment newInstance() {
        RecyclerViewFragment fragment = new RecyclerViewFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    public RecyclerViewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.myRecyclerView);
        mRecyclerView.setLayoutManager(mLayoutManager);
        ArrayList<GregorianCalendar> days = new ArrayList<GregorianCalendar>();
        days.add(0, new GregorianCalendar(2014, 1, 3));
        days.add(1, new GregorianCalendar(2010, 10, 30));
        days.add(2, new GregorianCalendar(2009, 5, 7));
        days.add(3, new GregorianCalendar(2013, 7, 5));
        days.add(4, new GregorianCalendar(2001, 10, 13));
        days.add(5, new GregorianCalendar(2003, 10, 23));

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(mRecyclerView,getActivity(), days);
        mRecyclerView.setAdapter(adapter);
    }


}
