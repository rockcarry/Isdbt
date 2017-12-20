package kr.co.fci.tv;

import android.view.Surface;
import android.view.SurfaceHolder;

import kr.co.fci.tv.tvSolution.FCI_TVi;
import kr.co.fci.tv.util.TVlog;

/**
 * Created by live.kim on 2017-11-20.
 */

public class ChatSubSurface implements SurfaceHolder.Callback {

    private static ChatSubSurface chatSubSurface = null;
    private static final String TAG = "ChatSubSurface ";

    private static Surface surface =null;
    //private ScanProcess doScan;

    public static ChatSubSurface getChatSubSurface() {
        if (null == chatSubSurface) {
            synchronized (ChatSubSurface.class) {
                if (null == chatSubSurface) {
                    chatSubSurface = new ChatSubSurface();
                }
            }
        }
        return chatSubSurface;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        TVlog.i(TAG, "Chat Sub surfaceCreated ******************************************************************** ");
        //MainActivity.getInstance().onStart_TV();
        FCI_TVi.setSubSurface(holder.getSurface());  //eddy strang
        surface =holder.getSurface();

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

    public Surface getChatSurface()
    {
        return surface;
    }

}