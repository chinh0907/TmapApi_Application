package com.example.a_final_201737079;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class Activity_CheckPermission extends AppCompatActivity {

    //허가받고자 하는 권한을 나열한 배열(권한 변경 시 수정할 부분)
    String[] asdf = {Manifest.permission.ACCESS_COARSE_LOCATION
            ,Manifest.permission.ACCESS_FINE_LOCATION
            ,Manifest.permission.INTERNET
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_check_permission);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int count = 0;

        //사용자의 OS 버전이 마시멜로우(23)이상인지 판별
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M) {
            //사용자의 단말에 권한 중 위치 가져오기(ACCESS_FINE_LOCATION)의 권한 허가 여부를 가져온다.
            //허가 -> PERMISSION_GRANTED
            //거부 -> PERMISSION_DENIED
            //현재 어플리케이션이 권한에 대해 거부되었는지 확인
            for(String qwer:asdf){
                if(checkSelfPermission(qwer)!= PackageManager.PERMISSION_DENIED){
                    count++;
                }
            }
            //모든 권한이 허가되어 있다면 바로 메인 실행
            if(count == asdf.length) {
                setFragment();
            }else{//아니라면 허가권 신청 실행
                requestPermissions(asdf, 1000);
            }
        }
        //버전이 이하라면 바로 실행
        else {
            setFragment();
        }
    }
    //요청에 대한 응답을 처리하는 부분
    //@param requestCode : 요청코드(1000)
    //@param permissions : 사용자가 요청한 권한들(개발자) String배열
    //@param grandResults : 권한에 대한 응답들(인덱스 별로 매칭)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int counta;
        //반환받은 요청이 0개 이상인지, 요청한 권한 코드가 일치하는지 확인
        if (grantResults.length > 0 && requestCode == 1000) {
            counta = 0;
            //답변받은 권한을 for문 돌려서 몇 개의 승인을 받았는지 확인
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    counta++;
                }
            }
            //모든 답변이 승인된 경우 메인 실행
            if (counta == grantResults.length) {
                setFragment();
                Toast.makeText(this, "권한 요청을 모두 승인하였습니다", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "권한 설정 실패!\n앱을 다시 설치하세요!\n앱 종료!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void setFragment(){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Activity_Tmap activity_tmap = new Activity_Tmap();
        fragmentTransaction.replace(android.R.id.content, activity_tmap);
        fragmentTransaction.commit();

    }
}

