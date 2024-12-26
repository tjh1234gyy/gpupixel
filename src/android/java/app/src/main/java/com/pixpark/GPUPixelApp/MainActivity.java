package com.pixpark.GPUPixelApp;

import static android.widget.Toast.LENGTH_LONG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.pixpark.GPUPixelApp.databinding.ActivityMainBinding;
import com.pixpark.gpupixel.GPUPixel;
import com.pixpark.gpupixel.GPUPixelTargetRawOutput;
import com.pixpark.gpupixel.filter.BeautyFaceFilter;
import com.pixpark.gpupixel.filter.FaceReshapeFilter;
import com.pixpark.gpupixel.GPUPixelSourceCamera;
import com.pixpark.gpupixel.GPUPixelView;
import com.pixpark.gpupixel.filter.LipstickFilter;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 200;
    private static final String TAG = "GPUPixelDemo";

    private GPUPixelSourceCamera sourceCamera;
    private GPUPixelView surfaceView;
    private BeautyFaceFilter beautyFaceFilter;
    private FaceReshapeFilter faceReshapFilter;
    private LipstickFilter lipstickFilter;
    private GPUPixelTargetRawOutput targetRawOutput;
    private SeekBar smooth_seekbar;
    private SeekBar whiteness_seekbar;
    private SeekBar face_reshap_seekbar;
    private SeekBar big_eye_seekbar;
    private SeekBar lipstick_seekbar;
    private Button btn_switch_camera;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // get log path
        String path = getExternalFilesDir("gpupixel").getAbsolutePath();
        Log.i(TAG, path);

        GPUPixel.setContext(this);
        // 保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // preview
        surfaceView = binding.surfaceView;
        surfaceView.setMirror(true);

        smooth_seekbar = binding.smoothSeekbar;
        smooth_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                beautyFaceFilter.setSmoothLevel(progress / 10.0f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        whiteness_seekbar = binding.whitenessSeekbar;
        whiteness_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                beautyFaceFilter.setWhiteLevel(progress / 10.0f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        face_reshap_seekbar = binding.thinfaceSeekbar;
        face_reshap_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.e(TAG, "thinfaceSeekbar progress:" + progress);
                faceReshapFilter.setThinLevel(progress / 200.0f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        big_eye_seekbar = binding.bigeyeSeekbar;
        big_eye_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                faceReshapFilter.setBigeyeLevel(progress / 100.0f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        lipstick_seekbar = binding.lipstickSeekbar;
        lipstick_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                lipstickFilter.setBlendLevel(progress / 10.0f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btn_switch_camera = binding.btnSwitchCamera;
        btn_switch_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sourceCamera != null) {
                    sourceCamera.switchCamera();
                    surfaceView.setMirror(sourceCamera.currentCameraIsFacingFront());
                }
            }
        });

        //
        this.checkCameraPermission();

//        SurfaceView myView = (SurfaceView) binding.getRoot().findViewById(R.id.surface_my_view);
//        if (myView != null) {
//            myView.getHolder().addCallback(this);
//        }
    }

    public void startCameraFilter() {
        // 美颜滤镜
        beautyFaceFilter = new BeautyFaceFilter();
        faceReshapFilter = new FaceReshapeFilter();
        lipstickFilter = new LipstickFilter();
        targetRawOutput = new GPUPixelTargetRawOutput();

        // camera
        sourceCamera = new GPUPixelSourceCamera(this.getApplicationContext());

        //
        sourceCamera.addTarget(lipstickFilter);
        lipstickFilter.addTarget(faceReshapFilter);
        faceReshapFilter.addTarget(beautyFaceFilter);
        beautyFaceFilter.addTarget(surfaceView);
        Log.e("MainActivity", "targetRawOutput::getNativeClassID>>" + targetRawOutput.getNativeClassID());
        Log.e("MainActivity", "surfaceView::getNativeClassID>>" + surfaceView.getNativeClassID());
        beautyFaceFilter.addTarget(targetRawOutput);

        sourceCamera.setLandmarkCallbck(new GPUPixel.GPUPixelLandmarkCallback() {
            @Override
            public void onFaceLandmark(float[] landmarks) {
                faceReshapFilter.setFaceLandmark(landmarks);
                lipstickFilter.setFaceLandmark(landmarks);
            }
        });
//        targetRawOutput.setRawOutputI420Callback(new GPUPixel.GPUPixelRawOutputI420Callback() {
//            @Override
//            public void onRawOutputI420Data(byte[] data, int width, int height, long ts) {
//                Log.e("MainActivity", "onRawOutputData:yuv.length:" + data.length + ">>width:" + width + ">>height:" + height + ">>ts:" + ts);
//                String strData = "";
//                try {
//                    for (int i = 0; i < 32; ++i) {
//                        Byte b = data[i];
//                        strData += String.format("%h", b & 0xff) + '>';
//                    }
//                    strData += "Spacer:";
//                    for (int i = data.length - 1; i >= data.length - 32; --i) {
//                        Byte b = data[i];
//                        strData += String.format("%h", b & 0xff) + '>';
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                Log.d("MainActivity", "data:" + strData);
//            }
//        });
//        targetRawOutput.setRawOutputPixelsCallback(new GPUPixel.GPUPixelRawOutputPixelsCallback() {
//            @Override
//            public void onRawOutputPixelsData(byte[] data, int width, int height, long ts) {
//                Log.e("MainActivity", "onRawOutputPixelsData:pixels.length:" + data.length + ">>width:" + width + ">>height:" + height + ">>ts:" + ts);
//                String strData = "";
//                for (int i = 0; i < 32; ++i) {
//                    Byte b = data[i];
//                    strData += String.format("%h", b & 0xff) + '>';
//                }
//                strData += "Spacer:";
//                for (int i = data.length - 1; i >= data.length - 32; --i) {
//                    Byte b = data[i];
//                    strData += String.format("%h", b & 0xff) + '>';
//                }
//                Log.d("MainActivity", "Pixels data:" + strData);
//            }
//        });
        // set default value
        beautyFaceFilter.setSmoothLevel(0.5f);
        beautyFaceFilter.setWhiteLevel(0.4f);

    }

    public void checkCameraPermission() {
        // 检查相机权限是否已授予
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 如果未授予相机权限，向用户请求权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            startCameraFilter();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCameraFilter();
            } else {
                Toast.makeText(this, "No Camera permission!", LENGTH_LONG);
            }
        }
    }


    public void surfaceCreated(SurfaceHolder holder) {
        Log.e("MainActivity", "surfaceCreated");
        sourceCamera.setPreviewHolder(holder);
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }
}