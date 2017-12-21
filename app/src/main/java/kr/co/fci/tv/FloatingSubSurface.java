package kr.co.fci.tv;

import android.view.Surface;
import android.view.SurfaceHolder;

import kr.co.fci.tv.tvSolution.FCI_TVi;
import kr.co.fci.tv.util.TVlog;

/**
 * Created by eddy.lee on 2017-11-17.
 */


public class FloatingSubSurface implements SurfaceHolder.Callback {

    private static FloatingSubSurface floatingSubSurface = null;
    private static final String TAG = "FloatingSubSurface ";

    private static Surface surface = null;
    //private ScanProcess doScan;

    public static FloatingSubSurface getFloatingSubSurface() {
        if (null == floatingSubSurface) {
            synchronized (FloatingSubSurface.class) {
                if (null == floatingSubSurface) {
                    floatingSubSurface = new FloatingSubSurface();
                }
            }
        }
        return floatingSubSurface;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        TVlog.i(TAG, "Floating Sub surfaceCreated ******************************************************************** ");
        //MainActivity.getInstance().onStart_TV();
        FCI_TVi.setSubSurface(holder.getSurface());  //eddy strang
        surface = holder.getSurface();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        TVlog.i(TAG, " Sub surfaceChanged ");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        TVlog.i(TAG, " Sub surfaceDestroyed ");

/*
        if (CommonStaticData.scanningNow) {
            if (doScan != null) {
                doScan.showProgress(0, 0, 0, doScan.SHOW_PROGRESS_CLEAR);
            }

            if (!FloatingWindow.isFloating) {
                MainActivity.getInstance().SolutionStop();
            }
        }
*/
    }

    Surface getFloatingSurface() {
        return surface;
    }
}
