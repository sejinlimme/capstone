package com.cookandroid.couselingaiscreen;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder;
    private Camera camera = null;

    private int mCameraID;
    private Camera.CameraInfo mCameraInfo;
    private int mDisplayOrientation;

    public CameraSurfaceView(Context context) {
        super(context);

        init(context);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init(Context context) {
        holder = getHolder();
        holder.addCallback(this);

        mCameraID = 1;

        mDisplayOrientation = ((Activity)context).getWindowManager().getDefaultDisplay().getRotation();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open(mCameraID);

        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraID, cameraInfo);

        mCameraInfo = cameraInfo;

        //하드웨어 카메라를 통해 들어온 영상이 홀더 쪽으로 전달되고 홀더에 의해 서피스뷰에 표시

        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            Log.e("CameraSurfaceView",
                    "Failed to set camera preview", e);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        int orientation = calculatePreviewOrientation(mCameraInfo, mDisplayOrientation);
        camera.setDisplayOrientation(orientation);
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    public boolean capture(Camera.PictureCallback callback) {
        if(camera != null) {
            camera.takePicture(null, null, callback);
            return true;
        } else {
            return false;
        }
    }

    public int calculatePreviewOrientation(Camera.CameraInfo info, int rotation) {
        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }

        return result;
    }
}
