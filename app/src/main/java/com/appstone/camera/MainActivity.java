package com.appstone.camera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.camera2.CameraCaptureSession;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FrameLayout cameraFrame;
    private Camera camera;
    private ImageView mIvCaptureImage;

    private boolean isBackMode;

    Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            saveImageToDevice(bitmap);
//            camera.startPreview();
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
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
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

    private void readImagesFromDevice() {
        Uri imageURI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] proj = {MediaStore.Images.Media.DATA};

        ArrayList<String> images = new ArrayList<>();

        Cursor cursor = getApplicationContext().getContentResolver().query(imageURI, proj, null, null, null);

        if (cursor != null) {
            //for (initialization; condition; iteration)
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                String image = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                images.add(image);
            }

            Glide.with(MainActivity.this).load(images.get(0)).into(mIvCaptureImage);

            Toast.makeText(MainActivity.this, "Total Images " + images.size(), Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
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

        readImagesFromDevice();
    }

    private void saveImageToDevice(Bitmap image) {
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/CameraTest");

        if (!directory.exists()) {
            directory.mkdirs();
        }

        File imageName = new File(directory , "IMG_" + System.currentTimeMillis() + ".png");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageName);
            image.compress(Bitmap.CompressFormat.PNG, 70, fos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mIvCaptureImage.setImageBitmap(image);
        camera.startPreview();
    }
}