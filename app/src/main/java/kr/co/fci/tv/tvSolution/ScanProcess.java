package kr.co.fci.tv.tvSolution;
/**
 * Created by eddy.lee on 2015-08-26.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import kr.co.fci.tv.MainActivity;
import kr.co.fci.tv.R;
import kr.co.fci.tv.TVEVENT;
import kr.co.fci.tv.util.TVlog;


public class ScanProcess {

    MaterialDialog scandialog;
    private static Context mContext;
    public final static int SHOW_PROGRESS_ON = 1;
    public final static int SHOW_PRORESS_OFF = 2;
    public final static int SHOW_PROGRESS_CLEAR = 3;
    private int preProgress=0;
    public boolean ScanOn = false;

    private static String TAG = "ScanProcess";

    public static ScanProcess instance;
    public static ScanProcess getInstance()
    {
        return instance;
    }


    public void showProgress(int _progress,int _found, float freqKHz, int _option)
    {
        if (ScanOn) {
            if (_option == SHOW_PROGRESS_ON) {

                if(scandialog.isCancelled())
                {
                    TVlog.i(TAG, " - call cancel -");
                    TVBridge.scanStop();
                    ScanOn = false;
                    /*
                    CustomToast toast = new CustomToast(mContext);
                    toast.showToast(mContext, _found  + " " + mContext.getString(R.string.channel_found), Toast.LENGTH_SHORT);
                    */
                    /*
                    Toast toast = Toast.makeText(mContext, _found  + " " + mContext.getString(R.string.channel_found), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 200);
                    toast.show();
                    */
                    return;
                }
                int increase = _progress - preProgress;
                scandialog.incrementProgress(increase);
                scandialog.setContent(_found + " " + mContext.getString(R.string.channel_found) + " (" + freqKHz / 1000 + mContext.getString(R.string.mega_hertz) + ")");
                //ToDo::scanned service name can be showed.
                //scandialog.setContent(_found + " " + mContext.getString(R.string.channel_found) + " (" + freqKHz / 1000 + mContext.getString(R.string.mega_hertz) + ")" + TVBridge.getCurScannedServiceName());
                preProgress = _progress;
                //scandialog.show();
            } else if (_option == SHOW_PRORESS_OFF) {
                /*
                CustomToast toast = new CustomToast(mContext);
                toast.showToast(mContext, _found  + " " + mContext.getString(R.string.channel_found), Toast.LENGTH_SHORT);
                */
                /*
                Toast toast = Toast.makeText(mContext, _found  + " " + mContext.getString(R.string.channel_found), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 200);
                toast.show();
                */
                scandialog.cancel();
            } else {
                TVBridge.scanStop();
                scandialog.cancel();
            }
        }
    }

    public ScanProcess(Context _context) {
        preProgress = 0;
        ScanOn = true;
        mContext = _context;
        scandialog = new MaterialDialog.Builder(_context)
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
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        ((MainActivity) mContext).sendEvent(TVEVENT.E_SCAN_CANCEL);
                    }
                })
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
//                      ((MainActivity) mContext).sendEvent(TVEVENT.E_SCAN_CANCEL);
                    }
                }).showListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        final MaterialDialog dialog = (MaterialDialog) dialogInterface;
                    }

                }).build();
        scandialog.getWindow().setGravity(Gravity.CENTER);
        scandialog.show();
        scandialog.setCanceledOnTouchOutside(false);

        // live add for hide bar at searching
        View decorView = scandialog.getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE ;
        decorView.setSystemUiVisibility(uiOptions);
    }

}
