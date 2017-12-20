package kr.co.fci.tv.tvSolution;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import kr.co.fci.tv.R;
import kr.co.fci.tv.chat.ChatMainActivity;
import kr.co.fci.tv.util.TVlog;

/**
 * Created by live.kim on 2017-10-11.
 */

public class ScanProcess_chat {

    //static MaterialDialog scandialog_chat;
    private static Context mContext;
    public final static int SHOW_PROGRESS_ON_CHAT= 1;
    public final static int SHOW_PROGRESS_OFF_CHAT = 2;
    public final static int SHOW_PROGRESS_CLEAR_CHAT = 3;
    private static int preProgress_chat=0;
    public static boolean ScanOn_chat = false;

    private static String TAG = "ScanProcess_chat";

    public static ScanProcess_chat instance;

    public static ScanProcess_chat getInstance()
    {
        return instance;
    }

    public static void showProgress_chat(int _progress, int _found, float freqKHz, int _option)
    {
        if (ScanOn_chat) {
            if (_option == SHOW_PROGRESS_ON_CHAT) {

                if(ChatMainActivity.getInstance().chat_ll_scan_progress.getVisibility() == View.INVISIBLE)
                {
                    TVlog.i(TAG, " - call cancel -");
                    TVBridge.scanStop();
                    ScanOn_chat = false;
                    ChatMainActivity.getInstance().mHandler_chat.post(new ToastRunnable(_found  + " " + mContext.getString(R.string.channel_found)));
                    /*CustomToast toast = new CustomToast(mContext);
                    toast.showToast(mContext, _found  + " " + mContext.getString(R.string.channel_found), Toast.LENGTH_SHORT);*/
                    return;
                }
                TVlog.i(TAG, " - showProgress_chat -");
                int increase = _progress - preProgress_chat;
                ChatMainActivity.getInstance().chat_scan_found.setText(_found + " " + mContext.getString(R.string.channel_found) + "\n (" + freqKHz / 1000 + mContext.getString(R.string.mega_hertz) + ")");
                //ToDo::scanned service name can be showed.
                //scandialog.setContent(_found + " " + mContext.getString(R.string.channel_found) + " (" + freqKHz / 1000 + mContext.getString(R.string.mega_hertz) + ")" + TVBridge.getCurScannedServiceName());
                preProgress_chat = _progress;
                //scandialog.show();
            } else if (_option == SHOW_PROGRESS_OFF_CHAT) {
                /*CustomToast toast = new CustomToast(mContext);
                toast.showToast(mContext, _found  + " " + mContext.getString(R.string.channel_found), Toast.LENGTH_SHORT);*/
                ChatMainActivity.getInstance().mHandler_chat.post(new ToastRunnable(_found  + " " + mContext.getString(R.string.channel_found)));

                /*Toast toast = Toast.makeText(mContext, _found  + " " + mContext.getString(R.string.channel_found), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 200);
                toast.show();*/
                //scandialog_chat.cancel();
                ChatMainActivity.getInstance().chat_ll_scan_progress.setVisibility(View.INVISIBLE);

            } else {
                TVBridge.scanStop();
                //scandialog_chat.cancel();
                ChatMainActivity.getInstance().chat_ll_scan_progress.setVisibility(View.INVISIBLE);

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

    public ScanProcess_chat(Context _context) {
        preProgress_chat = 0;
        ScanOn_chat = true;
        mContext = _context;

        /*scandialog_chat = new MaterialDialog.Builder(_context)
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
                        ChatMainActivity.getInstance().sendEvent(TVEVENT.E_SCAN_CANCEL_CHAT);
                    }
                }).showListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        final MaterialDialog dialog = (MaterialDialog) dialogInterface;
                    }

                }).build();
        scandialog_chat.getWindow().setGravity(Gravity.CENTER);
        scandialog_chat.show();*/
    }

}
