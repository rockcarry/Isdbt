package kr.co.fci.tv;

import android.view.SurfaceHolder;

import kr.co.fci.tv.chat.ChatMainActivity;
import kr.co.fci.tv.saves.CommonStaticData;
import kr.co.fci.tv.tvSolution.FCI_TVi;
import kr.co.fci.tv.tvSolution.ScanProcess;
import kr.co.fci.tv.util.TVlog;

/**
 * Created by eddy.lee on 2017-02-24.
 */

public class SubSurfaceSet implements SurfaceHolder.Callback {

    private static SubSurfaceSet subSurfaceSet = null;
    private static final String TAG = "SubSurfaceSet ";

    private ScanProcess doScan;

    public static SubSurfaceSet getSubSurfaceSet() {
        if (null == subSurfaceSet) {
            synchronized (SubSurfaceSet.class) {
                if (null == subSurfaceSet) {
                    subSurfaceSet = new SubSurfaceSet();
                }
            }
        }
        return subSurfaceSet;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        TVlog.i(TAG, " Sub surfaceCreated ");
        MainActivity.getInstance().onStart_TV();
        FCI_TVi.setSubSurface(holder.getSurface());
        //notifyFirstVideo();

        MainActivity.isMainActivity = true;
        FloatingWindow.isFloating = false;
        ChatMainActivity.isChat = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        TVlog.i(TAG, " Sub surfaceChanged ");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        TVlog.i(TAG, " Sub surfaceDestroyed ");

        if (CommonStaticData.scanningNow) {
            if (doScan != null) {
                doScan.showProgress(0, 0, 0, doScan.SHOW_PROGRESS_CLEAR);
            }

            if (!FloatingWindow.isFloating) {
                MainActivity.getInstance().SolutionStop();
            }
        }
    }
}
