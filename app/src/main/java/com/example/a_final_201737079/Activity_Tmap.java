package com.example.a_final_201737079;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapTapi;
import com.skt.Tmap.TMapView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.ArrayList;

public class Activity_Tmap extends Fragment {

    View view;
    Context context;

    LinearLayout linMapView;
    Button btnZoomIn, btnZoomOut, btnMyLocation, btnFind;
    TextView tvSearch;
    TMapView tMapView;
    TMapData tMapData;

    private String strData;

    // 티맵에서 POI 검색 결과를 저장할 리스트
    ArrayList<TMapPOIItem> poiResult;
    LocationManager locationManager;

    // 마커 처리를 위한 멤버 변수
    Bitmap rightButton;
    BitmapFactory.Options options;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_fragment_tmap, container, false);

        context = container.getContext();

        initView();
        initInstance();
        _strData();
        setEventListener();


        return view;
    }

    private void _strData() {

        if (getArguments() != null) {

            strData = getArguments().getString("Index");

            tvSearch.setText(strData);

            if (!strData.equals("")){
                searchPOI(strData);
            } else {
                tvSearch.setText("여기서 검색");
                Toast.makeText(getActivity(), "검색어를 정확히 입력해주세요!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void initView(){

        linMapView = view.findViewById(R.id.linMapView);
        btnZoomIn = view.findViewById(R.id.btnZoomIn);
        btnZoomOut = view.findViewById(R.id.btnZoomOut);
        btnMyLocation = view.findViewById(R.id.btnMyLocation);
        tvSearch = view.findViewById(R.id.tvSearch);
        btnFind = view.findViewById(R.id.btnFind);
        options = new BitmapFactory.Options();
        options.inSampleSize = 16;
        rightButton = BitmapFactory.decodeResource(getResources(), R.drawable.right_arrow, options);

    }

    // 필요 객체 변수 인스턴스화 작업
    public void initInstance() {

        tMapView = new TMapView(context);
        tMapView.setSKTMapApiKey("l7xxc08dc6bf2ab54206ba30f959b405908a");
        linMapView.addView(tMapView);
        tMapData = new TMapData();

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        poiResult = new ArrayList<>();

    }

    // 각 버튼 및 객체의 이벤트 리스너 설정 작업
    public void setEventListener() {

        btnZoomIn.setOnClickListener(listener);
        btnZoomOut.setOnClickListener(listener);
        tvSearch.setOnClickListener(listener);
        btnMyLocation.setOnClickListener(listener);
        btnFind.setOnClickListener(listener);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.tvSearch:

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Activity_Search activity_search = new Activity_Search();
                    fragmentTransaction.replace(android.R.id.content, activity_search);
                    fragmentTransaction.addToBackStack(null).commit();

                    break;

                case R.id.btnZoomIn:

                    tMapView.MapZoomIn();

                    break;


                case R.id.btnZoomOut:

                    tMapView.MapZoomOut();

                    break;

                case R.id.btnMyLocation:

                    strData = "";
                    try {
                        Toast.makeText(getContext(), "현재위치입니다. ", Toast.LENGTH_SHORT).show();
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
                        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, locationListener);
                    } catch (SecurityException e) {

                    }
                    break;
                case R.id.btnFind:

                    FragmentManager mManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction mTransaction = mManager.beginTransaction();
                    Activity_Findpaths activity_findpaths = new Activity_Findpaths();
                    mTransaction.replace(android.R.id.content, activity_findpaths);
                    mTransaction.addToBackStack(null).commit();
                    break;
            }
        }
    };

    // 위치 수신자 객체 설정
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            double lat = location.getLatitude();
            double lon = location.getLongitude();
            tMapView.setCenterPoint(lon, lat);
            tMapView.setLocationPoint(lon, lat);
            tMapView.setIconVisibility(true);

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };


    // 마커 설정 메서드
    private void setMarker(String name, double lat, double lon){
        TMapMarkerItem markerItem = new TMapMarkerItem();

        TMapPoint tMapPoint = new TMapPoint(lat, lon);

        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marker_icon);
        //markerItem.setIcon(bitmap); // 마커 아이콘 지정
        markerItem.setPosition(0.5f, 1.0f); // 마커의 중심점을 중앙, 하단으로 지정
        markerItem.setTMapPoint(tMapPoint);
        markerItem.setName(name); // 마커 타이틀 지정
        markerItem.setCanShowCallout(true);
        markerItem.setCalloutTitle(name);
        tMapView.addMarkerItem("name", markerItem); // 지도에 마커 추가
    }

    // 통합 검색 메서드
    private void searchPOI(String strData) {
        tMapData.findAllPOI(strData, 1, new TMapData.FindAllPOIListenerCallback() {
            @Override
            public void onFindAllPOI(ArrayList<TMapPOIItem> arrayList) {
                // 외부에서 사용하기 위한 검색 결과 복사

                try {
                    poiResult.addAll(arrayList);

                    tMapView.setCenterPoint(arrayList.get(0).getPOIPoint().getLongitude(),
                            arrayList.get(0).getPOIPoint().getLatitude(), true);

                    for (int i = 0; i < arrayList.size(); i++) {
                        TMapPOIItem item = (TMapPOIItem) arrayList.get(i);
                        Log.d("POI Name: ", item.getPOIName().toString() + ", " +
                                "Address: " + item.getPOIAddress().replace("null", "") + ", " +
                                "Point: " + item.getPOIPoint().toString() + "Contents : " + item.getPOIContent());

                        TMapMarkerItem markerItem = new TMapMarkerItem();
                        markerItem.setTMapPoint(item.getPOIPoint());
                        markerItem.setCalloutTitle(item.getPOIName());
                        markerItem.setCalloutSubTitle(item.getPOIAddress());
                        markerItem.setCanShowCallout(true);
                        markerItem.setCalloutRightButtonImage(rightButton);

                        //if (item.getPOIPoint().toString().equals(strPoint)) {
                            tMapView.addMarkerItem(item.getPOIName(), markerItem);
                        //}
                    }

                }catch (Exception e){
                    Handler mhandler = new Handler(Looper.getMainLooper());
                    mhandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                        }
                    },0);
                }
            }
        });
    }
}
