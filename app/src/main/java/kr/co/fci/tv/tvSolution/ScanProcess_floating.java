package kr.co.fci.tv.tvSolution;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import kr.co.fci.tv.FloatingWindow;
import kr.co.fci.tv.R;
import kr.co.fci.tv.util.TVlog;

import static kr.co.fci.tv.tvSolution.ScanProcess.SHOW_PRGRESS_ON;

/**
 * Created by live.kim on 2017-09-18.
 */

public class ScanProcess_floating {

    //static MaterialDialog scandialog_floating;
    private static Context mContext;
    public final static int SHOW_PROGRESS_ON= 1;
    public final static int SHOW_PROGRESS_OFF = 2;
    public final static int SHOW_PROGRESS_CLEAR = 3;
    private static int preProgress_floating=0;
    public static boolean ScanOn_floating = false;

    private static String TAG = "ScanProcess_floating";

    public static ScanProcess_floating instance;

    public static ScanProcess_floating getInstance()
    {
        return instance;
    }

    public static void showProgress_floating(int _progress, int _found, float freqKHz, int _option)
    {
        if (ScanOn_floating) {
            if (_option == SHOW_PRGRESS_ON) {

                if(FloatingWindow.getInstance().floating_ll_scan_progress.getVisibility() == View.INVISIBLE)
                {
                    TVlog.i(TAG, " - call cancel -");
                    TVBridge.scanStop();
                    ScanOn_floating = false;
                    FloatingWindow.getInstance().mHandler_floating.post(new ToastRunnable(_found  + " " + mContext.getString(R.string.channel_found)));
                    /*CustomToast toast = new CustomToast(mContext);
                    toast.showToast(mContext, _found  + " " + mContext.getString(R.string.channel_found), Toast.LENGTH_SHORT);*/
                    return;
                }
                TVlog.i(TAG, " - showProgress_floating -");
                int increase = _progress - preProgress_floating;
                FloatingWindow.getInstance().floating_scan_found.setText(_found + " " + mContext.getString(R.string.channel_found) + "\n (" + freqKHz / 1000 + mContext.getString(R.string.mega_hertz) + ")");
                //ToDo::scanned service name can be showed.
                //scandialog.setContent(_found + " " + mContext.getString(R.string.channel_found) + " (" + freqKHz / 1000 + mContext.getString(R.string.mega_hertz) + ")" + TVBridge.getCurScannedServiceName());
                preProgress_floating = _progress;
                //scandialog.show();
            } else if (_option == SHOW_PROGRESS_OFF) {
                /*CustomToast toast = new CustomToast(mContext);
                toast.showToast(mContext, _found  + " " + mContext.getString(R.string.channel_found), Toast.LENGTH_SHORT);*/
                FloatingWindow.getInstance().mHandler_floating.post(new ToastRunnable(_found  + " " + mContext.getString(R.string.channel_found)));

                /*Toast toast = Toast.makeText(mContext, _found  + " " + mContext.getString(R.string.channel_found), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 200);
                toast.show();*/
                //scandialog_floating.cancel();
                FloatingWindow.getInstance().floating_ll_scan_progress.setVisibility(View.INVISIBLE);

            } else {
                TVBridge.scanStop();
                //scandialog_floating.cancel();
                FloatingWindow.getInstance().floating_ll_scan_progress.setVisibility(View.INVISIBLE);

            }
        }
    }

    //private final Handler mHandler = new Handler();


    public static class ToastRunnable implements Runnable {
        String mText;
        public ToastRunnable(String text) {
            mText = text;
        } @Override public void run(){
            Toast.makeText(mContext, mText, Toast.LENGTH_SHORT).show();
        }
    }

    public ScanProcess_floating(Context _context) {
        preProgress_floating = 0;
        ScanOn_floating = true;
        mContext = _context;

        /*scandialog_floating = new MaterialDialog.Builder(_context)
                .theme(Theme.LIGHT)
                .iconRes(R.drawable.ic_search_grey600_48dp)
                .title(R.string.channel_search)
                .titleColor(mContext.getResources().getColor(R.color.black))
                .content(R.string.channel_found)
                .contentColor(mContext.getResources().getColor(R.color.black))
                .contentGravity(GravityEnum.START)
                .progress(false, 100, false)
                .widgetColorRes(R.color.blue3)
                .negativeText(R.string.cancel)
                .negativeColor(mContext.getResources().getColor(R.color.blue3))
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        FloatingWindow.getInstance().sendEvent(TVEVENT.E_SCAN_CANCEL_FLOATING);
                    }
                }).showListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        final MaterialDialog dialog = (MaterialDialog) dialogInterface;
                    }

                }).build();
        scandialog_floating.getWindow().setGravity(Gravity.CENTER);
        scandialog_floating.show();*/
    }

}
