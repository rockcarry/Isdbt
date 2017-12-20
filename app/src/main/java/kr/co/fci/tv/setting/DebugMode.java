package kr.co.fci.tv.setting;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import kr.co.fci.tv.MainActivity;
import kr.co.fci.tv.R;
import kr.co.fci.tv.TVEVENT;
import kr.co.fci.tv.util.CustomToast;
import kr.co.fci.tv.util.TVlog;

/**
 * Created by eddy.lee on 2016-07-12.
 */
public class DebugMode {

    private String TAG= "DebugMode";

    static final int RUNNING_PRESSED_NUM =10;
    static boolean debugModeOn =false;
    static boolean logCaptureModeOn=false;

    static int debugModePressed =0;
    static int logCaptureModePressed =0;

    static long lastDebugModePressedTime;
    static long lastLogCapturePressedTime;

    static CustomToast toast;
    static Context context;


    private static DebugMode debugMode = null;
    public static DebugMode getDebugMode() {
        if (null == debugMode) {
            synchronized (DebugMode.class) {
                if (null == debugMode) {
                    debugMode = new DebugMode();
                }
            }
        }
        return debugMode;
    }

    void setContext(Context _context)
    {
        context=_context;
        toast = new CustomToast(context);
    }

    public boolean checkingDebugOn()
    {
        return debugModeOn;
    }


    void ShowToast(String _str)
    {
        toast.showToast(context, _str , Toast.LENGTH_LONG);
    }

    void PressDebugMode()
    {

        if(debugModeOn == false)
        {

            if(debugModePressed==0)
            {
                lastDebugModePressedTime =System.currentTimeMillis();
            }

            if( debugModePressed >0) {

                if( (System.currentTimeMillis()-lastDebugModePressedTime) <500)
                {

                    TVlog.i(TAG, "debugModePressed = " + debugModePressed);

                    if(RUNNING_PRESSED_NUM-debugModePressed ==0)
                    {
                        String toastMSG = " Activated RF/BB Debug mode ";
                        lastDebugModePressedTime=System.currentTimeMillis();
                        ShowToast(toastMSG);
                        debugModeOn =true;
                    }else
                    {
                        String toastMSG = "You are " + (RUNNING_PRESSED_NUM-debugModePressed) + " step away from displaying RF/BB Debug mode";
                        lastDebugModePressedTime=System.currentTimeMillis();
                        ShowToast(toastMSG);
                    }

                }else
                {
                    TVlog.i(TAG, "debugMode Reset  " );
                    debugModePressed=0;
                    return;
                }

            }
            debugModePressed++;

        }else
        {
            String toastMSG = "Please stop to press this button. \n Already activated RF/BB Debug mode";
            lastDebugModePressedTime=System.currentTimeMillis();
            ShowToast(toastMSG);
        }
    }

    void PressLogCaptureMode()
    {

        if(logCaptureModeOn == false)
        {

            if(logCaptureModePressed==0)
            {
                lastLogCapturePressedTime =System.currentTimeMillis();
            }

            if( logCaptureModePressed >0) {

                if( (System.currentTimeMillis()-lastLogCapturePressedTime) <500)
                {

                    TVlog.i(TAG, "logCaptureModePressed = " + logCaptureModePressed);

                    if(RUNNING_PRESSED_NUM-logCaptureModePressed ==0)
                    {
                        String toastMSG = " Activated log capture mode ";
                        lastLogCapturePressedTime=System.currentTimeMillis();
                        ShowToast(toastMSG);
                        logCaptureModeOn =true;
                        MainActivity.getInstance().sendEvent(TVEVENT.E_LOG_CAPTURE_MOD_ON);

                    }else
                    {
                        String toastMSG = "You are " + (RUNNING_PRESSED_NUM-logCaptureModePressed) + " step away from Log capture mode";
                        lastLogCapturePressedTime=System.currentTimeMillis();
                        ShowToast(toastMSG);
                    }

                }else
                {
                    TVlog.i(TAG, "logCaptureMode Reset  " );
                    logCaptureModePressed=0;
                    return;
                }

            }
            logCaptureModePressed++;

        }else
        {
            String toastMSG = "Please stop to press this button. \n Already activated Log capture mode";
            lastLogCapturePressedTime=System.currentTimeMillis();
            ShowToast(toastMSG);
        }

    }

}
