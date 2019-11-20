package com.example.mlfacedetector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.mlfacedetector.fragments.FragmentCaptureOnFaceDetect;
import com.example.mlfacedetector.fragments.FragmentDetectFace;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnDetct,btnCapture;
    private FragmentTransaction ft;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        btnCapture = findViewById(R.id.btn_capture);
        btnDetct = findViewById(R.id.btn_detect);
        ft = getSupportFragmentManager().beginTransaction();
        btnDetct.setOnClickListener(this);
        btnCapture.setOnClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_detect:
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA},
                            50); }else{
                ft.replace(R.id.frag_holder, new FragmentDetectFace());
                ft.commit();}
                break;
            case R.id.btn_capture:
                ft.replace(R.id.frag_holder,new FragmentCaptureOnFaceDetect());
                ft.commit();
                break;
        }

    }
}
