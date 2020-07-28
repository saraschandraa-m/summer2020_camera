package com.appstone.camera;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import io.fotoapparat.Fotoapparat;
import io.fotoapparat.characteristic.LensPosition;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.view.CameraView;

public class FotoAppartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto_appart);

        CameraView cameraView = findViewById(R.id.camera_view);


        Fotoapparat.with(this).into(cameraView).build();


    }
}