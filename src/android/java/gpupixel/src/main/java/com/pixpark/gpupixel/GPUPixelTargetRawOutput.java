package com.pixpark.gpupixel;

import android.util.Log;

public class GPUPixelTargetRawOutput implements GPUPixelTarget {

    protected long mNativeClassID = 0;
    private GPUPixel.GPUPixelRawOutputI420Callback rawOutputCallback;
    private GPUPixel.GPUPixelRawOutputPixelsCallback rawOutputPixelsCallback;
    private Object object_this;

    public GPUPixelTargetRawOutput() {
        object_this = this;

        if (mNativeClassID != 0) return;
        GPUPixel.getInstance().runOnDraw(new Runnable() {
            @Override
            public void run() {
                mNativeClassID = GPUPixel.nativeTargetRawOutputNew();
                Log.e("GPUPixelTargetRawOutput", "NativeClassID>>" + mNativeClassID);
            }
        });
    }

    @Override
    public long getNativeClassID() {
        return mNativeClassID;
    }

    public void setRawOutputI420Callback(GPUPixel.GPUPixelRawOutputI420Callback rawOutputCallback) {
        this.rawOutputCallback = rawOutputCallback;

        GPUPixel.getInstance().runOnDraw(new Runnable() {
            @Override
            public void run() {
                GPUPixel.nativeSetRawOutputI420Callback(object_this, mNativeClassID);
            }
        });
    }

    public void setRawOutputPixelsCallback(GPUPixel.GPUPixelRawOutputPixelsCallback rawOutputPixelsCallback) {
        this.rawOutputPixelsCallback = rawOutputPixelsCallback;

        GPUPixel.getInstance().runOnDraw(new Runnable() {
            @Override
            public void run() {
                GPUPixel.nativeSetRawOutputPixelsCallback(object_this, mNativeClassID);
            }
        });
    }

    public void onRawOutputI420Data(byte[] yuv, int width, int height, long ts) {
        if (rawOutputCallback != null) {
            rawOutputCallback.onRawOutputI420Data(yuv, width, height, ts);
        }
    }

    public void onRawOutputPixelsData(byte[] yuv, int width, int height, long ts) {
        if (rawOutputPixelsCallback != null) {
            rawOutputPixelsCallback.onRawOutputPixelsData(yuv, width, height, ts);
        }
    }
}
