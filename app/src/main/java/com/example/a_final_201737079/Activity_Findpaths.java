package com.example.a_final_201737079;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class Activity_Findpaths extends Fragment {

    View view;
    Context context;

    LinearLayout linMapView, nodList;
    Button btnReturn, btn_MyLocation, btn_Info;
    EditText edt_Mylocation, edt_location;

    String input_Mylocation;
    String input_Location;
    String input_Start;
    String input_End;

    String info_Road;

    ListView _listView;
    listData _listData;
    listAdapter mAdapter;

    List<String> info_list;
    ListView list_item;
    ArrayAdapter<String> adapter;

    private boolean display_listView = false;
    private boolean startLocation_finish = false;

    TMapData tMapData;
    TMapView tMapView;

    LocationManager locationManager;
    double lat;
    double lon;

    TMapPoint StartPoint;
    TMapPoint EndPoint;

    final static int PATH_PEDESTIAN = 0;
    final static int PATH_CAR = 1;
    final static int PATH_BUS = 2;

    private Element root;


    Button btnPeople;
    Button btnBus;
    Button btnCar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.findpaths_fragment, container, false);
        context = container.getContext();

        list_item = (ListView)view.findViewById(R.id.list_item);
        info_list = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, info_list);
        list_item.setAdapter(adapter);



        initView();
        initInstance();
        setEventListener();

        return view;

    }

    private void initView() {

        linMapView = view.findViewById(R.id.linMapView);

        _listView = view.findViewById(R.id._listView);
        btn_Info = view.findViewById(R.id.btn_Info);
        nodList = view.findViewById(R.id.nodList);

        edt_Mylocation = view.findViewById(R.id.edt_Mylocation);
        edt_location = view.findViewById(R.id.edt_location);

        btn_MyLocation = view.findViewById(R.id.btn_MyLocation);
        btnReturn = view.findViewById(R.id.btnReturn);

        btnPeople = view.findViewById(R.id.btnPeople);
        btnBus = view.findViewById(R.id.btnBus);
        btnCar = view.findViewById(R.id.btnCar);

    }

    private void initInstance() {

        tMapView = new TMapView(context);
        tMapView.setSKTMapApiKey("l7xxc08dc6bf2ab54206ba30f959b405908a");
        linMapView.addView(tMapView);
        tMapData = new TMapData();

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

    }

    private void setEventListener() {

        set_edt();
        set_list();

        btn_MyLocation.setOnClickListener(listener);
        btnReturn.setOnClickListener(listener);
        btnPeople.setOnClickListener(listener);
        btnCar.setOnClickListener(listener);
        btnBus.setOnClickListener(listener);
        btn_Info.setOnClickListener(listener);

    }

    private void set_edt(){

        edt_Mylocation.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                mAdapter = new listAdapter();

                if (charSequence == null) return;


                if (!display_listView) {
                    startLocation_finish = false;
                    linMapView.setVisibility(View.INVISIBLE);
                    nodList.setVisibility(View.INVISIBLE);
                    btn_Info.setVisibility(View.INVISIBLE);
                    _listView.setVisibility(View.VISIBLE);
                    display_listView = true;
                }

                mAdapter.clearItem();


                if (charSequence.toString().length() < 2) {

                    mAdapter.notifyDataSetChanged();
                    return;
                }

                input_Mylocation = charSequence.toString();

                tMapData.findAllPOI(input_Mylocation, new TMapData.FindAllPOIListenerCallback() {
                    @Override
                    public void onFindAllPOI(ArrayList poiItem) {
                        for (int i = 0; i < poiItem.size(); i++) {

                            TMapPOIItem item = (TMapPOIItem) poiItem.get(i);
                            mAdapter.addItem(item.getPOIName().toString(), item.getPOIAddress().replace("null", "")
                                    , item.getPOIPoint().getLatitude(), item.getPOIPoint().getLongitude());

                        }

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        _listView.setAdapter(mAdapter);
                                    }
                                });
                            }
                        }).start();

                    }

                });

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edt_location.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence == null) return;

                mAdapter = new listAdapter();

                if (!display_listView) {
                    startLocation_finish = true;
                    linMapView.setVisibility(View.INVISIBLE);
                    nodList.setVisibility(View.INVISIBLE);
                    btn_Info.setVisibility(View.INVISIBLE);
                    _listView.setVisibility(View.VISIBLE);
                    display_listView = true;
                }


                try {

                    if (charSequence.toString().length() < 2) {
                        mAdapter.notifyDataSetChanged();
                        return;
                    }

                } catch (Exception e) {

                }

                mAdapter.clearItem();

                input_Location = charSequence.toString();

                tMapData.findAllPOI(input_Location, new TMapData.FindAllPOIListenerCallback() {
                    @Override
                    public void onFindAllPOI(ArrayList poiItem) {
                        for (int i = 0; i < poiItem.size(); i++) {

                            TMapPOIItem item = (TMapPOIItem) poiItem.get(i);

                            mAdapter.addItem(item.getPOIName().toString(), item.getPOIAddress().replace("null", "")
                                    , item.getPOIPoint().getLatitude(), item.getPOIPoint().getLongitude());

                        }

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        _listView.setAdapter(mAdapter);
                                    }
                                });
                            }
                        }).start();

                    }
                });

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void set_list(){
        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                tMapData.findAllPOI(mAdapter.getItem(i).getTvTitle(), 1, new TMapData.FindAllPOIListenerCallback() {
                    @Override
                    public void onFindAllPOI(ArrayList<TMapPOIItem> arrayList) {

                        TMapPOIItem item = (TMapPOIItem) arrayList.get(0);

                        if (!startLocation_finish) {
                            StartPoint = new TMapPoint(Double.parseDouble(mAdapter.getItem(i).getLat()), Double.parseDouble(mAdapter.getItem(i).getLon()));
                            input_Start = mAdapter.getItem(i).getTvTitle();
                            edt_Mylocation.setText(input_Start);
                            Log.d("위도 경도:", StartPoint.toString());


                        } else {
                            EndPoint = new TMapPoint(Double.parseDouble(mAdapter.getItem(i).getLat()), Double.parseDouble(mAdapter.getItem(i).getLon()));
                            input_End = mAdapter.getItem(i).getTvTitle();
                            edt_location.setText(input_End);
                            Log.d("위도 경도:", EndPoint.toString());
                        }

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        linMapView.setVisibility(View.VISIBLE);
                                        _listView.setVisibility(View.INVISIBLE);
                                        display_listView = false;

                                    }
                                });
                            }
                        }).start();
                    }
                });
            }
        });
    }


    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {

                case R.id.btn_MyLocation:

                    try {
                        Toast.makeText(getContext(), "현재위치버튼을 3번 클릭해주세요. ", Toast.LENGTH_SHORT).show();
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
                        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, locationListener);


                    } catch (SecurityException e) {

                    }

                    tMapData.convertGpsToAddress(lat, lon, new TMapData.ConvertGPSToAddressListenerCallback() {
                        @Override
                        public void onConvertToGPSToAddress(String s) {

                            input_Start = s;

                        }
                    });
                    edt_Mylocation.setText(input_Start);
                    StartPoint = new TMapPoint(lat, lon);

                    startLocation_finish = true;
                    linMapView.setVisibility(View.VISIBLE);
                    _listView.setVisibility(View.INVISIBLE);
                    display_listView = false;

                    break;

                case R.id.btnReturn:

                    if (!startLocation_finish) {

                        Toast.makeText(getContext(), "경로를 검색해주세요. ", Toast.LENGTH_SHORT).show();

                    }else {

                        TMapPoint Temp = StartPoint;
                        StartPoint = EndPoint;
                        EndPoint = Temp;

                        edt_Mylocation.setText(input_End);
                        edt_location.setText(input_Start);

                        String strTemp = input_Start;
                        input_Start = input_End;
                        input_End = strTemp;

                        startLocation_finish = true;
                        linMapView.setVisibility(View.VISIBLE);
                        _listView.setVisibility(View.INVISIBLE);
                        display_listView = false;

                    }

                    break;

                case R.id.btnPeople:
                    if (!startLocation_finish) {

                        Toast.makeText(getContext(), "경로를 검색해주세요. ", Toast.LENGTH_SHORT).show();

                    }else {

                        btn_Info.setVisibility(View.VISIBLE);

                        FindCarmera();
                        Find_Road(PATH_PEDESTIAN);
                        Road_Info(PATH_PEDESTIAN);

                    }
                    break;

                case R.id.btnBus:
                    if (!startLocation_finish) {

                        Toast.makeText(getContext(), "경로를 검색해주세요. ", Toast.LENGTH_SHORT).show();

                    }else {
                        btn_Info.setVisibility(View.VISIBLE);

                        FindCarmera();
                        Find_Road(PATH_BUS);
                        Road_Info(PATH_BUS);

                    }
                    break;
                case R.id.btnCar:
                    if (!startLocation_finish) {

                        Toast.makeText(getContext(), "경로를 검색해주세요. ", Toast.LENGTH_SHORT).show();

                    }else {
                        btn_Info.setVisibility(View.VISIBLE);
                        FindCarmera();
                        Find_Road(PATH_CAR);
                        Road_Info(PATH_CAR);


                    }

                    break;

                case R.id.btn_Info:

                    Toast.makeText(getContext(), "해당 길안내입니다.", Toast.LENGTH_SHORT).show();

                    linMapView.setVisibility(View.INVISIBLE);
                    nodList.setVisibility(View.VISIBLE);


            }

        }
    };

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            lat = location.getLatitude();
            lon = location.getLongitude();

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

    public void FindCarmera() {

        TMapPoint tMapPoint_instance = new TMapPoint((StartPoint.getLatitude() + EndPoint.getLatitude())/2 ,
                (StartPoint.getLongitude() + EndPoint.getLongitude())/2);
        tMapView.setCenterPoint(tMapPoint_instance.getLongitude(),tMapPoint_instance.getLatitude(),true);
        tMapView.setZoomLevel(14);
    }

    public void Find_Road(int type) {

        Enum<TMapData.TMapPathType> pathType = null;
        if (type == 0 || type == 2) {
            pathType = TMapData.TMapPathType.PEDESTRIAN_PATH;
        }
        else if (type == 1) {
            pathType = TMapData.TMapPathType.CAR_PATH;
        }

        tMapData.findPathDataWithType((TMapData.TMapPathType) pathType, StartPoint, EndPoint, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine tMapPolyLine) {

                tMapView.addTMapPath(tMapPolyLine);
            }
        });


    }

    public void Road_Info(int type){

        Enum<TMapData.TMapPathType> pathType = null;
        if (type == 0 || type == 2) {
            pathType = TMapData.TMapPathType.PEDESTRIAN_PATH;
        }
        else if (type == 1) {
            pathType = TMapData.TMapPathType.CAR_PATH;
        }

        info_list.clear();

        tMapData.findPathDataAllType((TMapData.TMapPathType) pathType, StartPoint, EndPoint, new TMapData.FindPathDataAllListenerCallback() {
            @Override
            public void onFindPathDataAll(Document document) {
                root = document.getDocumentElement();

                if (type == 0 || type == 2) {
                    NodeList nodeListPlacemark = root.getElementsByTagName("Placemark");

                    for (int i = 0; i < nodeListPlacemark.getLength(); i++) {
                        NodeList nodeListItem = nodeListPlacemark.item(i).getChildNodes();

                        for (int j = 0; j < nodeListItem.getLength(); j++) {
                            if (nodeListItem.item(j).getNodeName().equals("description")) {
                                Log.d("debug", nodeListItem.item(j).getTextContent().trim());
                                info_Road = nodeListItem.item(j).getTextContent().trim();

                                info_list.add(nodeListItem.item(j).getTextContent());

                            }
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            adapter.notifyDataSetChanged();

                                        }
                                    });
                                }
                            }).start();
                        }
                    }
                }
                else if (type == 1){

                    NodeList nodeListPlacemark = root.getElementsByTagName("Placemark");

                    for (int i = 0; i < nodeListPlacemark.getLength(); i++) {
                        NodeList nodeListItem = nodeListPlacemark.item(i).getChildNodes();

                        for (int j = 0; j < nodeListItem.getLength(); j++) {

                            if (nodeListItem.item(j).getNodeName().equals("description")) {
                                Log.d("debug", nodeListItem.item(j).getTextContent().trim());
                                info_Road = nodeListItem.item(j).getTextContent().trim();

                                info_list.add(nodeListItem.item(j).getTextContent());

                            }
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            adapter.notifyDataSetChanged();

                                        }
                                    });
                                }
                            }).start();
                        }
                    }

                }

            }
        });

    }
}
