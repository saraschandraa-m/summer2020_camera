package com.appstone.camera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.camera2.CameraCaptureSession;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    FrameLayout cameraFrame;
    private Camera camera;
    private ImageView mIvCaptureImage;

    private boolean isBackMode;

    Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            mIvCaptureImage.setImageBitmap(bitmap);
            camera.startPreview();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraFrame = findViewById(R.id.camera_frame);

        ImageButton btnCapture = findViewById(R.id.btn_capture);
        ImageButton btnSwitch = findViewById(R.id.btn_switch_camera);
        mIvCaptureImage = findViewById(R.id.iv_catpure_image);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 1000);
            } else {
                initiateCamera(true);
            }
        } else {
            initiateCamera(true);
        }

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.takePicture(null, null, pictureCallback);
            }
        });

        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.stopPreview();
                boolean currentFocus = isBackMode == true ? false : true;
                initiateCamera(currentFocus);
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initiateCamera(true);
            } else {
                Toast.makeText(MainActivity.this, "User Denied Permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initiateCamera(boolean isBack) {
        try {
            isBackMode = isBack;
            int cameraID;
            if (isBack) {
                cameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
            } else {
                cameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
            }
            camera = Camera.open(cameraID);
            CameraSurfaceView surfaceView = new CameraSurfaceView(MainActivity.this, camera);
            cameraFrame.addView(surfaceView);
        } catch (Exception e) {

        }
    }
}