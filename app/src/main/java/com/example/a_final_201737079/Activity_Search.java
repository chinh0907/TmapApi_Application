package com.example.a_final_201737079;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapView;

import java.util.ArrayList;

public class Activity_Search extends Fragment {

    View view;
    Context context;

    EditText edtSearch;
    Button btnSearch;
    ListView _listView;
    listAdapter mAdapter;

    TMapData tMapData;
    TMapView tMapView;

    String strData;
    String strName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.search_fragment, container, false);
        context = container.getContext();

        initView();
        initInstance();
        setEventListener();

        return view;

    }

    private void initView() {

        _listView = view.findViewById(R.id._listView);
        edtSearch = view.findViewById(R.id.edtSearch);
        set_listView();
        btnSearch = view.findViewById(R.id.btnSearch);

    }

    private void initInstance() {

        tMapView = new TMapView(context);
        tMapView.setSKTMapApiKey("l7xx3bb39c06e70b494087a3c2502cce77e5");
        tMapData = new TMapData();

    }

    private void setEventListener() {

        btnSearch.setOnClickListener(listener);

    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()){
                case R.id.btnSearch:

                    strData = edtSearch.getText().toString();

                    mAdapter = new listAdapter();

                    tMapData.findAllPOI(strData, new TMapData.FindAllPOIListenerCallback() {
                        @Override
                        public void onFindAllPOI(ArrayList poiItem) {
                            for(int i = 0; i < poiItem.size(); i++) {

                                TMapPOIItem  item = (TMapPOIItem) poiItem.get(i);
                                mAdapter.addItem(item.getPOIName().toString(), item.getPOIAddress().replace("null", "")
                                        , item.getPOIPoint().getLatitude(), item.getPOIPoint().getLongitude());

                            }

                            TmapThread thread = new TmapThread();
                            thread.start();
                        }
                    });

                    break;
            }

        }
    };

    private void set_listView (){
        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                strName = mAdapter.getItem(i).getTvTitle();

                setFragment();

            }
        });
    }

    public void setFragment(){

        Bundle bundle = new Bundle();
        bundle.putString("Index", strName);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        Activity_Tmap activity_tmap = new Activity_Tmap();
        activity_tmap.setArguments(bundle);
        transaction.replace(android.R.id.content, activity_tmap);
        fragmentManager.popBackStack();
        transaction.remove(Activity_Search.this).commit();

    }


    Handler handler = new Handler();

    public class UIUpdate implements Runnable {
        @Override
        public void run() {
            _listView.setAdapter(mAdapter);
        }
    }

    public class TmapThread extends Thread {
        @Override
        public void run() {
            handler.post(new UIUpdate());
        }
    }

}
