package kr.co.fci.tv;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fci.tv.FCI_TV;

import java.util.ArrayList;
import java.util.List;

import kr.co.fci.tv.channelList.Channel;
import kr.co.fci.tv.channelList.ChannelMainActivity;
import kr.co.fci.tv.chat.ChatMainActivity;
import kr.co.fci.tv.saves.CommonStaticData;
import kr.co.fci.tv.saves.SharedPreference;
import kr.co.fci.tv.saves.TVProgram;
import kr.co.fci.tv.setting.InputDialog;
import kr.co.fci.tv.tvSolution.AudioOut;
import kr.co.fci.tv.tvSolution.CaptionDirectView;
import kr.co.fci.tv.tvSolution.FCI_TVi;
import kr.co.fci.tv.tvSolution.ScanProcess_floating;
import kr.co.fci.tv.tvSolution.SignalMonitor;
import kr.co.fci.tv.tvSolution.TVBridge;
import kr.co.fci.tv.util.CustomToast;
import kr.co.fci.tv.util.TVlog;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static java.lang.System.exit;
import static kr.co.fci.tv.MainActivity.HANDOVER_TIME;
import static kr.co.fci.tv.MainActivity.isChannelListViewOn;
import static kr.co.fci.tv.R.id.ll_controller;
import static kr.co.fci.tv.TVEVENT.E_CAPTION_CLEAR_NOTIFY_FLOATING;
import static kr.co.fci.tv.TVEVENT.E_CHANNEL_CHANGE_TIMEOVER_FLOATING;
import static kr.co.fci.tv.TVEVENT.E_HIDE_FLOATING_CONTROLLER;
import static kr.co.fci.tv.TVEVENT.E_NOSIGNAL_SHOW_FLOATING;
import static kr.co.fci.tv.TVEVENT.E_SCAN_MONITOR_FLOATING;
import static kr.co.fci.tv.TVEVENT.E_SIGNAL_NOTI_MSG_FLOATING;
import static kr.co.fci.tv.tvSolution.AudioOut.afChangeListener;


/**
 * Created by live.kim on 2016-11-04.
 */

public class FloatingWindow extends Service implements SurfaceHolder.Callback , AudioManager.OnAudioFocusChangeListener {

    private static AudioManager am;
    private AudioOut audioOut_floating;
    public Typeface mFont_floating, tf_floating;
    String[] arr_svcmodeswitch_jp;
    private TextView subTitleView_floating = null;
    private TextView superImposeView_floating = null;
    private final static int CAPTION_CLEAR_TIME_FLOATING = 15000;
    private final static int SUPERIMPOSE_CLEAR_TIME_FLOATING = 15000;
    //JAPAN_CAPTION[[
    private FrameLayout mCaptionLayout_floating = null;
    private FrameLayout mSuperimposeLayout_floating = null;
    private CaptionDirectView mCaptionView_floating;
    private CaptionDirectView mSuperimposeView_floating;
    private final int M_TYPE_CAPTION_SUBTITLE = 0;
    private final int M_TYPE_CAPTION_SUPERIMPOSE = 1;
    //]]JAPAN_CAPTION

    public static LinearLayout floating_ll_audioOnlyChannel;

    public static LinearLayout floating_ll_black;

    public Handler mHandler_floating;

    public ScanProcess_floating doScan_floating;

    public LinearLayout floating_ll_scan_progress;
    public ProgressBar floating_scan_progressBar;
    //public TextView floating_scan_found;
    public Button floating_btn_scan_cancel;

    boolean mHasAudioFocus = false;

    DisplayMetrics displayMetrics;
    WindowManager windowManager;
    Point size;
    int floatingpxWidth;
    int floatingpxHeight;

    int currentDistance;
    int lastDistance = -1;

    private float X;
    private float Y;

    private float Width;
    private float Height;

    private float offsetX;
    private float offsetY;

    int posX1=0, posX2=0, posY1=0, posY2=0;

    float oldDist = 1f;
    float newDist = 1f;

    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;


    private static String TAG = "FloatingWindow";
    private View floating_view;
    public static boolean isFloating = false;

    LinearLayout status_bar_floating;
    private TextView currChNo_floating;
    private TextView currRemoteNo_floating;
    public TextView currCH_floating;

    LinearLayout channelLayout_floating;
    ImageView ch_up_floating;
    ImageView ch_down_floating;

    LinearLayout ll_controller_floating;
    ImageView iv_scan;
    ImageView iv_max;
    ImageView iv_close;
//  Button btn_receiveMode;

    public static SysBroadcastReceiver mSysReceiver_Floating = null;
    public static SysBroadcastReceiver mScreenOff_Floating = null;
    public static SysBroadcastReceiver mScreenOn_Floating = null;
    // usbdongle[[
    public static UsbDevice currentUsbDevice = null;
    private static SysBroadcastReceiver mUsbReceiver = null;
    // ]]usbdongle

    WindowManager.LayoutParams mParams;

    private static SurfaceHolder floatingVideoSurfaceHolder = null;
    public static SurfaceView sv_floatingView = null;
    private static SurfaceHolder floatingVideoSurfaceHolderSub = null;
    public static SurfaceView svSub_floatingView = null;

    public int frameWidthFloating  = 0;
    public int frameHeightFloating = 0;

    private static Cursor mCursor_floating;

    private Surface mainSurface;
    //Uri mUri;

    int AudioFormat= 0x00;         // recording 0x60(HEAAV), 0x40(AAC)
    int VideoFormat= 0x00;         // recording 0x04(H.264)
    int Scrambled  = 1;            // 0: scramble ch, 1: free ch
    int mRemoteKey = 0;
    int mSvcNumber = 0;

    public static LinearLayout ll_floatingAutoSearch;
    private ProgressBar progressing_autoSearch_floating;
    public static Button btn_return_floating;

    me.grantland.widget.AutofitTextView floating_programNotMsg;
    me.grantland.widget.AutofitTextView floating_noSignal;

    me.grantland.widget.AutofitTextView floating_noChannel;

    //private LinearLayout changeChannelView =null;
    //private CustomView progressingChange;
    //private TextView loadingChannel;

    boolean is_wired_headset;

    public LinearLayout floating_ll_age_limit;
    me.grantland.widget.AutofitTextView floating_age_limit_title;
    me.grantland.widget.AutofitTextView floating_age_limit_msg;

    me.grantland.widget.AutofitTextView tv_scramble_title;
    me.grantland.widget.AutofitTextView tv_scramble_msg;

    me.grantland.widget.AutofitTextView floating_channel_search;
    public me.grantland.widget.AutofitTextView floating_scan_found;
    public static me.grantland.widget.AutofitTextView floating_loadingChannel;
    me.grantland.widget.AutofitTextView tv_autoSearch_title_floating;
    me.grantland.widget.AutofitTextView tv_autoSearch_msg_floating;

    public static LinearLayout floating_changeChannelView =null;
    public static ImageView floating_channelChangeBG = null;
    //private CustomView floating_progressingChange;
    public static ProgressBar floating_progressingChange;
    //private TextView floating_loadingChannel;
    private int[] channelChangeProcLocation =null;


    private boolean SignalStatFlag =false;
    int signal_check_cnt = 0;
    //private ImageView channelChangeBG = null;
    private final static int BUTTON_CLICK_TIME = 500;
    private final static int NO_SIGNAL_MSG_TIME = 5000;  // live add
    private final static int SIGNAL_MONITER_TIME = 1000;  // live change from 1000 to 2000
    private final static int SIGNAL_MONITER_TIME_USB = 2000;  // live change from 1000 to 2000
    private final static int CONTROLLER_HIDE_TIME = 7000;

    //public static boolean isChannelListViewOn =false;
    private SignalMonitor signalMoniter =null;
    //ImageView signalImage;

    // parent rate checking
    //  private Boolean screenbl_enable = false;    // justin 20170523
    //  private Boolean password_verify = false;
    private int floating_curr_rate;

//  public int floating_mChannelIndex = CommonStaticData.lastCH;

    WindowManager mWindowManager;

    RelativeLayout rl_ChType_floating;
    ImageView iv_ChType_floating;
    ImageView iv_ChFree_floating;

    public static kr.co.fci.tv.FloatingWindow instance;
    public static kr.co.fci.tv.FloatingWindow getInstance()
    {
        return instance;
    }

    public static Cursor floating_getCursor( ) {
        return mCursor_floating;
    }

    public FloatingWindow() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        //return null;
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public Handler Floating_Handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            TVEVENT event = TVEVENT.values()[msg.what];
            /*
            if (TVON==false)
            {
                TVlog.i(TAG, "---------------- TV OFF -------------------");
                return;
            }*/
            switch (event) {
                case E_CAPTION_NOTIFY_FLOATING: {
                    if (CommonStaticData.captionSwitch == true) {

                        try {
                            Bundle newCaption = (Bundle) msg.obj;
                            if (newCaption != null) {
                                String caption_info = newCaption.getString("caption_info");

                                if (caption_info.length() > 0) {
                                    //JAPAN_CAPTION[[
                                    if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                                        if (mCaptionView_floating != null) {
                                            mCaptionView_floating.setText(caption_info);
                                            mCaptionView_floating.invalidate();
                                        }
                                    }
                                    //]]JAPAN_CAPTION
                                    else {
                                        subTitleView_floating.setText(Html.fromHtml(caption_info));
                                    }

                                    removeEvent(TVEVENT.E_CAPTION_CLEAR_NOTIFY_FLOATING);
                                    postEvent(TVEVENT.E_CAPTION_CLEAR_NOTIFY_FLOATING, CAPTION_CLEAR_TIME_FLOATING);
                                } else {
                                    //JAPAN_CAPTION[[
                                    if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                                        if (mCaptionView_floating != null) {
                                            mCaptionView_floating.setText("");
                                            mCaptionView_floating.invalidate();
                                        }
                                    }
                                    //]]JAPAN_CAPTION
                                    else {
                                        subTitleView_floating.setText(Html.fromHtml(""));
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;

                case E_CAPTION_CLEAR_NOTIFY_FLOATING: {
                    //JAPAN_CAPTION[[
                    if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                        if (mCaptionView_floating != null) {
                            mCaptionView_floating.setText("");
                            mCaptionView_floating.invalidate();
                        }
                    }
                    //]]JAPAN_CAPTION
                    else {
                        subTitleView_floating.setText(Html.fromHtml(""));
                    }
                }
                break;

                case E_SUPERIMPOSE_NOTIFY_FLOATING: {
                    try {
                        Bundle newSuperimpose = (Bundle) msg.obj;
                        String superimpose_info = newSuperimpose.getString("superimpose_info");

                        if (superimpose_info.length() > 0) {
                            //JAPAN_CAPTION[[
                            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                                if (mSuperimposeView_floating != null) {
                                    mSuperimposeView_floating.setText(superimpose_info);
                                    mSuperimposeView_floating.invalidate();
                                }
                            }
                            //]]JAPAN_CAPTION
                            else {
                                // live modify
                                if (CommonStaticData.superimposeSwitch == true) {
                                    superImposeView_floating.setVisibility(View.VISIBLE);
                                } else {
                                    superImposeView_floating.setVisibility(View.INVISIBLE);
                                }
                                //
                                superImposeView_floating.setText(Html.fromHtml(superimpose_info));
                            }

                            removeEvent(TVEVENT.E_SUPERIMPOSE_CLEAR_NOTIFY_FLOATING);
                            postEvent(TVEVENT.E_SUPERIMPOSE_CLEAR_NOTIFY_FLOATING, SUPERIMPOSE_CLEAR_TIME_FLOATING);
                        } else {
                            //JAPAN_CAPTION[[
                            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                                if (mSuperimposeView_floating != null) {
                                    mSuperimposeView_floating.setText("");
                                    mSuperimposeView_floating.invalidate();
                                }
                            }
                            //]]JAPAN_CAPTION
                            else {
                                // live modify
                                if (CommonStaticData.superimposeSwitch == true) {
                                    superImposeView_floating.setVisibility(View.VISIBLE);
                                } else {
                                    superImposeView_floating.setVisibility(View.INVISIBLE);
                                }
                                //
                                superImposeView_floating.setText(Html.fromHtml(""));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

                case E_SUPERIMPOSE_CLEAR_NOTIFY_FLOATING: {
                    //JAPAN_CAPTION[[
                    if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                        if (mSuperimposeView_floating != null) {
                            mSuperimposeView_floating.setText("");
                            mSuperimposeView_floating.invalidate();
                        }
                    }
                    //]]JAPAN_CAPTION
                    else {
                        superImposeView_floating.setText(Html.fromHtml(""));
                    }
                }
                break;

                case E_SCAN_COMPLETED_FLOATING:
                    TVlog.i(TAG, " >>>>> E_SCAN_COMPLETED_FLOATING");
                    channelChangeStartView(false);
                    floating_ll_scan_progress.setVisibility(View.INVISIBLE);

                    TVlog.i(TAG, "---------------- E_SCAN_COMPLETED_FLOATING-------------------");
                    if (CommonStaticData.handoverMode > 0) {
                        if (CommonStaticData.handoverIndex != -1) {
                            MainActivity.getInstance().mChannelIndex = CommonStaticData.handoverIndex;
                            TVlog.e(TAG, "handover mode = " + CommonStaticData.handoverMode + " , channel index =  " + MainActivity.getInstance().mChannelIndex);
                        } else {
                            CommonStaticData.handoverIndex = 0;
                        }
                    }
                    CommonStaticData.scanningNow = false;
                    if (CommonStaticData.scanCHnum > 0) {
                        floating_noChannel.setVisibility(View.INVISIBLE);
                        final int NEED_TO_CHANGE_CHANNEL_NO = 0;
                        final int NEED_TO_CHANGE_CHANNEL_FIRST_LOAD = 1;
                        final int NEED_TO_CHANGE_CHANNEL_CHANGE_INDEX = 2;
                        int statusOfNeedToChange = NEED_TO_CHANGE_CHANNEL_NO;
                        mCursor_floating = MainActivity.getCursor();
                        if (MainActivity.getInstance().mUri != null) {
                            if (mCursor_floating != null && mCursor_floating.isClosed() == false) {
                                mCursor_floating.close();
                                mCursor_floating = null;
                            }
                            mCursor_floating = getContentResolver().query(MainActivity.getInstance().mUri, CommonStaticData.PROJECTION, TVProgram.Programs.TYPE + "=?", CommonStaticData.selectionArgsTV, null);
                            MainActivity.setCursor(mCursor_floating);
                        }
                        if (mCursor_floating.getCount() > 0 && (mCursor_floating.getPosition() < mCursor_floating.getCount())) {
                            if (MainActivity.getInstance().mChannelIndex >= mCursor_floating.getCount()) {
                                MainActivity.getInstance().mChannelIndex = 0;
                            }
                            mCursor_floating.moveToPosition(MainActivity.getInstance().mChannelIndex);
                            mRemoteKey = mCursor_floating.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_REMOTE_KEY);
                            mSvcNumber = mCursor_floating.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_NUMBER);
                            if (CommonStaticData.isProcessingUpdate) {
                                if ((mRemoteKey != TVBridge.getLastRemoteKey()) || (mSvcNumber != TVBridge.getLastSvcID())) {
                                    int cursorCount = mCursor_floating.getCount();
                                    if (cursorCount >= TVBridge.getLastListCount()) { //service is increased or contents are changed.
                                        for (int i = 0; i < cursorCount; i++) {
                                            mCursor_floating.moveToPosition(i);
                                            if ((mCursor_floating.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_REMOTE_KEY) == TVBridge.getLastRemoteKey())
                                                    && (mCursor_floating.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_NUMBER) == TVBridge.getLastSvcID())) {
                                                if (MainActivity.getInstance().mChannelIndex != i) {
                                                    statusOfNeedToChange = NEED_TO_CHANGE_CHANNEL_CHANGE_INDEX;
                                                }
                                                MainActivity.getInstance().mChannelIndex = i;
                                                break;
                                            }
                                        }
                                    } else { //service is decreased.
                                        for (int i = 0; i < cursorCount; i++) {
                                            mCursor_floating.moveToPosition(i);
                                            if ((mCursor_floating.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_REMOTE_KEY) == TVBridge.getLastRemoteKey())
                                                    && (mCursor_floating.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV) == 0)) {
                                                if (MainActivity.getInstance().mChannelIndex != i) {
                                                    statusOfNeedToChange = NEED_TO_CHANGE_CHANNEL_CHANGE_INDEX;
                                                }
                                                MainActivity.getInstance().mChannelIndex = i;
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (CommonStaticData.handoverMode == 2) {
                                    if (MainActivity.getInstance().mChannelIndex != CommonStaticData.handoverIndex && mCursor_floating.getCount() > CommonStaticData.handoverIndex) {
                                        MainActivity.getInstance().mChannelIndex = CommonStaticData.handoverIndex;
                                        mCursor_floating.moveToPosition(MainActivity.getInstance().mChannelIndex);
                                        TVlog.e(TAG, "handover: list reloaded & different index: channel index =  " + MainActivity.getInstance().mChannelIndex);
                                    } else {
                                        TVlog.e(TAG, "handover: list reloaded & same index: channel index =  " + MainActivity.getInstance().mChannelIndex);
                                    }
                                    statusOfNeedToChange = NEED_TO_CHANGE_CHANNEL_CHANGE_INDEX;
                                    CommonStaticData.handoverMode = 0;
                                }
                                CommonStaticData.isProcessingUpdate = false;
                            } else {
                                statusOfNeedToChange = NEED_TO_CHANGE_CHANNEL_FIRST_LOAD;
                                if (CommonStaticData.handoverMode == 1) {
                                    CommonStaticData.handoverMode = 0;
                                    TVlog.e(TAG, "handover: same list & same index: channel index =  " + MainActivity.getInstance().mChannelIndex);
                                }
                            }

                            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                                switch (CommonStaticData.receivemode) {
                                    case 0:     // 1seg
                                        if (mCursor_floating.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV)!=0) {
                                            for (int i=0; i < mCursor_floating.getCount(); i++) {
                                                mCursor_floating.moveToPosition(i);
                                                if (mCursor_floating.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV) == 0) {
                                                    MainActivity.getInstance().mChannelIndex = i;
                                                    break;
                                                }
                                            }
                                            if (MainActivity.getInstance().mChannelIndex == 0) {   // not found channel
                                                TVBridge.stop();
                                                //channelChangeEndViewFloating(false);
                                                //viewToastMSG(getResources().getString(R.string.ch_change_fail), false);
                                                CustomToast toast1 = new CustomToast(getApplicationContext());
                                                toast1.showToast(getApplicationContext(), getApplicationContext().getString(R.string.ch_change_fail), Toast.LENGTH_SHORT);
                                            }
                                        }
                                        break;
                                    case 1:     // fullseg
                                        if (mCursor_floating.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV)!=1) {
                                            for (int i=0; i < mCursor_floating.getCount(); i++) {
                                                mCursor_floating.moveToPosition(i);
                                                if (mCursor_floating.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV) == 1) {
                                                    MainActivity.getInstance().mChannelIndex = i;
                                                    break;
                                                }
                                            }
                                            if (MainActivity.getInstance().mChannelIndex==0) {      // not found channel
                                                TVBridge.stop();
                                                //channelChangeEndViewFloating(false);
                                                //viewToastMSG(getResources().getString(R.string.ch_change_fail), false);
                                                CustomToast toast2 = new CustomToast(getApplicationContext());
                                                toast2.showToast(getApplicationContext(), getApplicationContext().getString(R.string.ch_change_fail), Toast.LENGTH_SHORT);
                                            }
                                        }
                                        break;
                                    case 2:     // auto
                                    case 3:     // off
                                        break;
                                }
                            }

                            /*
                            if (buildOption.CUSTOMER.contains("Myphone")) {
                                if (setDefaultChannel) {
                                    for (int i = 0; i < mCursor.getCount(); i++) {
                                        mCursor.moveToPosition(i);
                                        String channelName = mCursor.getString(CommonStaticData.COLUMN_INDEX_SERVICE_NAME);
                                        if (channelName.contains("ABS")) {  // default channel
                                            floating_mChannelIndex = i;
                                            break;
                                        }
                                    }
                                }
                            }*/

                            mCursor_floating.moveToPosition(MainActivity.getInstance().mChannelIndex);

                            int freq = Integer.parseInt(mCursor_floating.getString(CommonStaticData.COLUMN_INDEX_SERVICE_FREQ));
                            TVlog.i(TAG, " >>>>> current freq = " + freq);

                            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                                int channelNo = 13 + (int)((freq-473143)/6000);
                                currChNo_floating.setText(channelNo+"ch");
                            } else {
                                int channelNo = 14 + (int)((freq-473143)/6000);
                                currChNo_floating.setText(channelNo+"ch");
                            }

                            //String channelName = channel.getName();
                            String channelName = mCursor_floating.getString(CommonStaticData.COLUMN_INDEX_SERVICE_NAME);
                            TVlog.i(TAG, " >>>>> channelName = "+ channelName);
                            TVlog.i(TAG, " >>> CommonStaticData.lastCH = "+CommonStaticData.lastCH);
                            String[] split_channelName = channelName.split(" ");

                            // live modify 20170104
                            currRemoteNo_floating.setText(split_channelName[0]);
                            String str = "";
                            for (int i = 1; i < split_channelName.length; i++) {
                                str += split_channelName[i];
                                if (i < split_channelName.length - 1) {
                                    str += " ";
                                }
                            }
                            currCH_floating.setText(str);
                            int type = (int) mCursor_floating.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV);
                            int free = (int) mCursor_floating.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_FREE);
                            if (type == 0) { // if 1seg
                                if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                                    iv_ChType_floating.setBackgroundResource(R.drawable.jp_1seg);
                                    iv_ChType_floating.setScaleX(0.8f);
                                    iv_ChType_floating.setScaleY(0.8f);
                                    iv_ChFree_floating.setVisibility(View.GONE);
                                } else {
                                    iv_ChType_floating.setBackgroundResource(R.drawable.tv_icon_1seg);
                                    if (free == 0) {
                                        iv_ChFree_floating.setVisibility(View.VISIBLE);
                                    } else {
                                        iv_ChFree_floating.setVisibility(View.GONE);
                                    }
                                }
                            } else if (type == 1) { // if fullseg
                                if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                                    iv_ChType_floating.setBackgroundResource(R.drawable.jp_fullseg);
                                    iv_ChType_floating.setScaleX(0.8f);
                                    iv_ChType_floating.setScaleY(0.8f);
                                    iv_ChFree_floating.setVisibility(View.GONE);
                                } else {
                                    iv_ChType_floating.setBackgroundResource(R.drawable.tv_icon_fullseg);
                                    if (free == 0) {
                                        iv_ChFree_floating.setVisibility(View.VISIBLE);
                                    } else {
                                        iv_ChFree_floating.setVisibility(View.GONE);
                                    }
                                }
                            }
                            rl_ChType_floating.setVisibility(VISIBLE);
                            //

                            // live add
                            //updateCurEPGNameNDuration();

                            AudioFormat = mCursor_floating.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_AUDFORM);
                            VideoFormat = mCursor_floating.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_VIDFORM);
                            Scrambled   = mCursor_floating.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_FREE);
                            mRemoteKey  = mCursor_floating.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_REMOTE_KEY);
                            mSvcNumber  = mCursor_floating.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_NUMBER);

                            removeEvent(E_SCAN_MONITOR_FLOATING);
                            //MainActivity.getInstance().removeEvent(TVEVENT.E_SCAN_MONITOR);

                            if (Scrambled == 0) {
                                sendEvent(TVEVENT.E_BADSIGNAL_CHECK_FLOATING, 2, 0, null);
                                //MainActivity.getInstance().sendEvent(TVEVENT.E_BADSIGNAL_CHECK, 2, 0, null);
                            }

                            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                            boolean isScreenOn;
                            if (Build.VERSION.SDK_INT <= 19) {
                                isScreenOn = pm.isScreenOn();
                            } else {
                                isScreenOn = pm.isInteractive();
                            }
                            if (isScreenOn) {
                                if (statusOfNeedToChange == NEED_TO_CHANGE_CHANNEL_FIRST_LOAD || statusOfNeedToChange == NEED_TO_CHANGE_CHANNEL_CHANGE_INDEX) {
                                    TVlog.i(TAG, " scan completed " + statusOfNeedToChange);

                                    // checking TS playback running...
                                    /*
                                    if (isCheckingPlayback())
                                    {
                                        TVlog.i(TAG, " playback running  ");
                                        break;
                                    }*/
                                    if (buildOption.LOG_CAPTURE_MODE==3)
                                    {
                                        //TVBridge.serviceID_start(0);
                                        TVBridge.dualAV_start(0, true);
                                        postEvent(TVEVENT.E_AUTO_CHANGE_CHANNEL_TEST_FLOATING, 20 * 1000);
                                    } else {
                                        //TVBridge.serviceID_start(MainActivity.getInstance().mChannelIndex);
                                        int[] info = FCI_TVi.GetPairNSegInfoOfCHIndex(MainActivity.getInstance().mChannelIndex);
                                        int isAudioOnly = info[5];
                                        if (isAudioOnly == 1) {
                                            CommonStaticData.isAudioChannel = true;
                                            channelChangeEndView(false);
                                            if (floating_ll_black != null) {
                                                floating_ll_black.setVisibility(View.VISIBLE);
                                            }
                                            if (floating_ll_audioOnlyChannel != null) {
                                                floating_ll_audioOnlyChannel.setVisibility(View.VISIBLE);
                                            }
                                        } else {
                                            CommonStaticData.isAudioChannel = false;
                                            if (floating_ll_black != null) {
                                                floating_ll_black.setVisibility(View.INVISIBLE);
                                            }
                                            if (floating_ll_audioOnlyChannel != null) {
                                                floating_ll_audioOnlyChannel.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                        TVBridge.dualAV_start(MainActivity.getInstance().mChannelIndex, true);
                                    }
                                } else {
                                    floating_changeChannelView.setVisibility(View.INVISIBLE);
                                    if (CommonStaticData.isAudioChannel == true) {
                                        if (floating_ll_black != null) {
                                            floating_ll_black.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        if (floating_ll_black != null) {
                                            floating_ll_black.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                    floating_progressingChange.setVisibility(View.INVISIBLE);
                                    floating_loadingChannel.setVisibility(View.INVISIBLE);
                                }
                            } else {
                                TVlog.i(TAG, " =====  screen off =========");
                                MainActivity.getInstance().SolutionStop();
                            }
                        } else {
                            //postEvent(TVEVENT.E_CHLIST_UPDATE_MULTI, 0);
                        }
                        floating_noSignal.setVisibility(View.INVISIBLE);
                        floating_programNotMsg.setVisibility(View.INVISIBLE);
                    } else {
                        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                        boolean isScreenOn;
                        if (Build.VERSION.SDK_INT <= 19) {
                            isScreenOn = pm.isScreenOn();
                        } else {
                            isScreenOn = pm.isInteractive();
                        }

                        if (isScreenOn) {
                            TVlog.i(TAG, " =====  no scan =========");
                            if (currChNo_floating != null && currCH_floating != null) {
                                currChNo_floating.setText("- -ch");
                                currRemoteNo_floating.setText("- - -");
                                currCH_floating.setText(R.string.no_channel_title);
                            }

                            if (rl_ChType_floating != null) {
                                rl_ChType_floating.setVisibility(View.GONE);
                            }

                            floating_changeChannelView.setVisibility(View.INVISIBLE);
                            floating_noChannel.setVisibility(VISIBLE);

                            if (sv_floatingView != null && sv_floatingView.isShown()) {
                                sv_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
                            }
                            if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                                if (svSub_floatingView != null && svSub_floatingView.isShown()) {
                                    svSub_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
                                }
                            } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                                    (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                            buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                            buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                                if (svSub_floatingView != null && svSub_floatingView.isShown()) {
                                    svSub_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
                                }
                            }
                            if (tv_scramble_title.getVisibility() == View.INVISIBLE) {
                                CommonStaticData.badSignalFlag = false;
                                CommonStaticData.encryptFlag = false;
                                CommonStaticData.ageLimitFlag = false;
                            }

                            /*
                            if (isCheckingPlayback())
                            {
                                TVlog.i(TAG, " playback running  ");
                                break;
                            }*/

                            /*
                            if (buildOption.ADD_TS_CAPTURE != true) {
                                new InputDialog(instance, InputDialog.TYPE_TV_NOCHANNELLIST, null, null, null);
                            }*/
                        } else {
                            TVlog.i(TAG, " =====  no scan and screen off =========");
                            MainActivity.getInstance().SolutionStop();
                        }
                    }
                    CommonStaticData.loadingNow = false;

                    /*
                    if (buildOption.GUI_STYLE == 2 || buildOption.GUI_STYLE == 3) {
                        setDefaultChannel = false;  // live add
                    }*/
                    break;

                case E_SCAN_START_FLOATING:
                    TVlog.i(TAG, " >>>>> E_SCAN_START ");
                    TVBridge.scanStop();
                    TVBridge.stop();
                    // channel index initialize
                    CommonStaticData.lastCH = 0;
                    CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                    editor.putInt(CommonStaticData.lastChannelKey, CommonStaticData.lastCH);
                    editor.commit();
                    CommonStaticData.scanningNow = true;
                    removeEvent(TVEVENT.E_SIGNAL_NOTI_MSG_FLOATING);
                    removeEvent(TVEVENT.E_BADSIGNAL_CHECK_FLOATING);
                    removeEvent(TVEVENT.E_SCAN_HANDOVER_START);

                    if (sv_floatingView != null && sv_floatingView.isShown()) {
                        sv_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
                    }
                    if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                        if (svSub_floatingView != null && svSub_floatingView.isShown()) {
                            svSub_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
                        }
                    } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                            (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                    buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                    buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                        if (svSub_floatingView != null && svSub_floatingView.isShown()) {
                            svSub_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
                        }
                    }
                    sendEvent(TVEVENT.E_CAPTION_CLEAR_NOTIFY_FLOATING);
                    sendEvent(TVEVENT.E_SUPERIMPOSE_CLEAR_NOTIFY_FLOATING);

                    if (ll_floatingAutoSearch.getVisibility() == View.VISIBLE) {
                        ll_floatingAutoSearch.setVisibility(View.INVISIBLE);
                    }
                    if (floating_noChannel.getVisibility() == VISIBLE) {
                        floating_noChannel.setVisibility(View.INVISIBLE);
                    }

                    if (floating_changeChannelView.getVisibility() == VISIBLE) {
                        floating_changeChannelView.setVisibility(View.INVISIBLE);
                    }

                    SharedPreference sharedPreference = new SharedPreference();
                    if (sharedPreference != null) {
                        ArrayList<Channel> favorites = sharedPreference.getFavorites(getApplicationContext());
                        if (favorites != null) {
                            for (int i = 0 ; i < favorites.size() ; i++) {
                                sharedPreference.removeFavorite(getApplicationContext(), favorites.get(i));
                            }
                        }
                    }

                    Intent intent = MainActivity.getInstance().getIntent();
                    MainActivity.getInstance().mUri = intent.getData();
                    if (MainActivity.getInstance().mUri == null) {
                        MainActivity.getInstance().mUri = TVProgram.Programs.CONTENT_URI;
                        intent.setData(MainActivity.getInstance().mUri);
                    }
                    getContentResolver().delete(MainActivity.getInstance().mUri, null, null);  // justin DB
                    TVlog.i(TAG, " >>>>> press Scan  ");
                    floating_ll_scan_progress.setVisibility(View.VISIBLE);
                    doScan_floating = new ScanProcess_floating(instance);
                    TVBridge.scan();
                    break;

                case E_SCAN_PROCESS_FLOATING :
                    int progress_floating = (int)msg.arg1;
                    int found_floating = (int)msg.arg2;
                    int freqKHz_floating = 0;
                    if (msg.obj != null) {
                        freqKHz_floating = (int)msg.obj;
                    }
                    //   TVlog.e(TAG, "E_SCAN_PROCESS " + progress + " % " + found + " found");
                    if (doScan_floating != null) {
                        if (progress_floating < 97) {
                            if (buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA
                                    || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_ONESEG
                                    || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                                doScan_floating.showProgress_floating(progress_floating, found_floating, freqKHz_floating+8000, doScan_floating.SHOW_PROGRESS_ON_FLOATING);
                            } else {
                                doScan_floating.showProgress_floating(progress_floating, found_floating, freqKHz_floating+6000, doScan_floating.SHOW_PROGRESS_ON_FLOATING);
                            }
                            CommonStaticData.scanCHnum = found_floating;
                            //MainActivity.getInstance().mChannelIndex = 0;
                            MainActivity.getInstance().mChannelIndex = 0;
                        } else if (progress_floating >= 97 && progress_floating < 100) {
                            doScan_floating.showProgress_floating(progress_floating, found_floating, freqKHz_floating, doScan_floating.SHOW_PROGRESS_ON_FLOATING);
                            CommonStaticData.scanCHnum = found_floating;
                            //MainActivity.getInstance().mChannelIndex = 0;
                            MainActivity.getInstance().mChannelIndex = 0;
                        } else {
                            doScan_floating.showProgress_floating(progress_floating, found_floating, freqKHz_floating, doScan_floating.SHOW_PROGRESS_OFF_FLOATING);
                            found_floating = 0;
                            //CommonStaticData.scanCHnum = found;
                        }
                    }
                    break;

                case E_SCAN_CANCEL_FLOATING:
                    TVlog.i(TAG, "---------------- E_SCAN_CANCEL-------------------");
                    doScan_floating.showProgress_floating(0, 0, 473143, doScan_floating.SHOW_PROGRESS_OFF_FLOATING);
                    floating_ll_scan_progress.setVisibility(View.INVISIBLE);
                    TVBridge.scanStop();
                    if (CommonStaticData.handoverMode == 1) {
                        sendEvent(TVEVENT.E_SCAN_COMPLETED_FLOATING);
                    }
                    break;

                case E_CHANNEL_LIST_AV_STARTED_FLOATING: {
                    TVlog.i(TAG, "---------------- E_CHANNEL_LIST_AV_STARTED_FLOATING-------------------");

                    //CommonStaticData.screenBlockFlag = false;    // justin 20170523
                    CommonStaticData.passwordVerifyFlag = false;
                    CommonStaticData.ageLimitFlag = false;

                    //controllerLayout.setVisibility(View.VISIBLE);
                    channelChangeStartView(false);
                }
                break;

                case E_FLOATING_STOP_NOTIFY: {
                    TVlog.i(TAG, ">>>>> E_FLOATING_STOP_NOTIFY");
                    sv_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
                    svSub_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
                }
                break;

                case E_FIRSTVIDEO_FLOATING:
                {
                    TVlog.i(TAG, " >>>>> E_FIRSTVIDEO_FLOATING");
                    removeEvent(TVEVENT.E_BADSIGNAL_CHECK_FLOATING);
                    removeEvent(E_SIGNAL_NOTI_MSG_FLOATING);
                    removeEvent(E_NOSIGNAL_SHOW_FLOATING);
                    removeEvent(E_CHANNEL_CHANGE_TIMEOVER_FLOATING);
                    sendEvent(TVEVENT.E_CHANNEL_NAME_UPDATE_FLOATING);

                    CommonStaticData.badSignalFlag = false;
                    CommonStaticData.encryptFlag = false;
                    CommonStaticData.ageLimitFlag = false;

                    if (floating_changeChannelView.getVisibility() == VISIBLE) {
                        floating_changeChannelView.setVisibility(View.INVISIBLE);
                    }
                    channelChangeEndView(false);

                    if (sv_floatingView != null && sv_floatingView.isShown()) {
                        sv_floatingView.setBackgroundColor(getResources().getColor(R.color.transparent));
                    }
                    if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                        if (svSub_floatingView != null && svSub_floatingView.isShown()) {
                            svSub_floatingView.setBackgroundColor(getResources().getColor(R.color.transparent));
                        }
                    } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                            (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                    buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                    buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                        if (svSub_floatingView != null && svSub_floatingView.isShown()) {
                            svSub_floatingView.setBackgroundColor(getResources().getColor(R.color.transparent));
                        }
                    }

                    if (CommonStaticData.isAudioChannel) {
                        if (floating_ll_black != null) {
                            floating_ll_black.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (floating_ll_black != null) {
                            floating_ll_black.setVisibility(View.INVISIBLE);
                        }
                    }

                    floating_noChannel.setVisibility(View.INVISIBLE);
                    floating_noSignal.setVisibility(View.INVISIBLE);
                    floating_programNotMsg.setVisibility(View.INVISIBLE);

                    if (ll_floatingAutoSearch.getVisibility() == View.VISIBLE) {
                        ll_floatingAutoSearch.setVisibility(View.INVISIBLE);
                    }

                    if (buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL
                            || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_ONESEG
                            || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_FILE) {
                        floating_curr_rate = FCI_TVi.GetCurProgramRating();
                        if ((floating_curr_rate >= (CommonStaticData.PG_Rate+1) && (CommonStaticData.PG_Rate!=0))
                                && (CommonStaticData.passwordVerifyFlag == false)
                                && (CommonStaticData.ratingsetSwitch == true)) {
                            CommonStaticData.ageLimitFlag = true;
                        } else {
                            CommonStaticData.ageLimitFlag = false;
                        }
                        sendEvent(TVEVENT.E_RATING_MONITOR_FLOATING);
                    }

                    /*
                    if (floating_programNotMsg.getVisibility() == View.VISIBLE) {
                        floating_noSignal.setVisibility(View.INVISIBLE);
                        floating_programNotMsg.setVisibility(View.INVISIBLE);
                    }*/

                    if (tv_scramble_title.getVisibility() == VISIBLE) {
                        tv_scramble_title.setVisibility(View.INVISIBLE);
                    }
                    if (tv_scramble_msg.getVisibility() == VISIBLE) {
                        tv_scramble_msg.setVisibility(View.INVISIBLE);
                    }

                    InputDialog.nosignalNotiClear();
                    SignalStatFlag = false;
                    CommonStaticData.tuneTimeOver = false;
                    channelChangeEndView(false);

                    FCI_TVi.subSurfaceViewOnOff(FCI_TVi.getDualMode());
                }
                break;

                case E_FIRSTAUDIO_FLOATING:
                {
                    TVlog.i(TAG, " >>>>> E_FIRSTAUDIO_FLOATING");
                    removeEvent(TVEVENT.E_BADSIGNAL_CHECK_FLOATING);
                    removeEvent(E_SIGNAL_NOTI_MSG_FLOATING);
                    removeEvent(E_NOSIGNAL_SHOW_FLOATING);
                    removeEvent(E_CHANNEL_CHANGE_TIMEOVER_FLOATING);
                    sendEvent(TVEVENT.E_CHANNEL_NAME_UPDATE_FLOATING);

                    CommonStaticData.badSignalFlag = false;
                    CommonStaticData.encryptFlag = false;
                    CommonStaticData.ageLimitFlag = false;

                    if (floating_changeChannelView.getVisibility() == VISIBLE) {
                        floating_changeChannelView.setVisibility(View.INVISIBLE);
                    }
                    channelChangeEndView(false);

                    if (sv_floatingView != null && sv_floatingView.isShown()) {
                        sv_floatingView.setBackgroundColor(getResources().getColor(R.color.transparent));
                    }
                    if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                        if (svSub_floatingView != null && svSub_floatingView.isShown()) {
                            svSub_floatingView.setBackgroundColor(getResources().getColor(R.color.transparent));
                        }
                    } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                            (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                    buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                    buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                        if (svSub_floatingView != null && svSub_floatingView.isShown()) {
                            svSub_floatingView.setBackgroundColor(getResources().getColor(R.color.transparent));
                        }
                    }

                    if (CommonStaticData.isAudioChannel) {
                        if (floating_ll_black != null) {
                            floating_ll_black.setVisibility(View.VISIBLE);
                        }
                        if (floating_ll_audioOnlyChannel != null) {
                            floating_ll_audioOnlyChannel.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (floating_ll_black != null) {
                            floating_ll_black.setVisibility(View.INVISIBLE);
                        }
                        if (floating_ll_audioOnlyChannel != null) {
                            floating_ll_audioOnlyChannel.setVisibility(View.INVISIBLE);
                        }
                    }

                    floating_noChannel.setVisibility(View.INVISIBLE);
                    floating_noSignal.setVisibility(View.INVISIBLE);
                    floating_programNotMsg.setVisibility(View.INVISIBLE);

                    if (ll_floatingAutoSearch.getVisibility() == View.VISIBLE) {
                        ll_floatingAutoSearch.setVisibility(View.INVISIBLE);
                    }

                    if (buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL
                            || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_ONESEG
                            || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_FILE) {
                        floating_curr_rate = FCI_TVi.GetCurProgramRating();
                        if ((floating_curr_rate >= (CommonStaticData.PG_Rate+1) && (CommonStaticData.PG_Rate!=0))
                                && (CommonStaticData.passwordVerifyFlag == false)
                                && (CommonStaticData.ratingsetSwitch == true)) {
                            CommonStaticData.ageLimitFlag = true;
                        } else {
                            CommonStaticData.ageLimitFlag = false;
                        }
                        sendEvent(TVEVENT.E_RATING_MONITOR_FLOATING);
                    }

                    if (tv_scramble_title.getVisibility() == VISIBLE) {
                        tv_scramble_title.setVisibility(View.INVISIBLE);
                    }
                    if (tv_scramble_msg.getVisibility() == VISIBLE) {
                        tv_scramble_msg.setVisibility(View.INVISIBLE);
                    }

                    InputDialog.nosignalNotiClear();
                    SignalStatFlag = false;
                    CommonStaticData.tuneTimeOver = false;
                    channelChangeEndView(false);

                    FCI_TVi.subSurfaceViewOnOff(FCI_TVi.getDualMode());
                }
                break;

                case E_CHANNEL_CHANGE_TIMEOVER_FLOATING:
                    TVlog.i(TAG, " >>>>> E_CHANNEL_CHANGE_TIMEOVER_FLOATING");
                    if (CommonStaticData.tuneTimeOver==true && CommonStaticData.scanningNow==false) {
                        //TVBridge.stop();
                        //sendEvent(TVEVENT.E_CHANNEL_CHANGE_FAIL);
                        sendEvent(TVEVENT.E_BADSIGNAL_CHECK_FLOATING, 4, 0, null);
                        //MainActivity.getInstance().sendEvent(TVEVENT.E_BADSIGNAL_CHECK, 4, 0, null);
                    }
                    break;

                case E_CHANNEL_CHANGE_FAIL_FLOATING:
                    channelChangeEndView(false);
                    CustomToast toast3 = new CustomToast(getApplicationContext());
                    toast3.showToast(getApplicationContext(), getApplicationContext().getString(R.string.ch_change_fail), Toast.LENGTH_SHORT);
                    break;

                case E_CHANNEL_SWITCHING_FLOATING:
                    TVlog.i("live", " ==== E_CHANNEL_SWITCHING_FLOATING ====");
                    int tomove = (int)msg.arg1;
                    int[] info;
                    int pairIndex = -1;
                    int isFullseg = -1;
                    int findFail = 1;
                    int orgPos = 0;
                    int[] info1;
                    int findpos1 = -1;

                    int channelMainIndex = -1;
                    int oneSegIndex = -1;

                    info = FCI_TVi.GetPairNSegInfoOfCHIndex(MainActivity.getInstance().mChannelIndex);
                    pairIndex = info[0];
                    isFullseg = info[1];
                    channelMainIndex = info[3];
                    oneSegIndex = info[4];

                    TVlog.i("live", " >>> cur Index = " + MainActivity.getInstance().mChannelIndex  + ", isFullseg = " + isFullseg + ", pairIndex = " + pairIndex
                            + ", channelMainIndex = " + channelMainIndex + ", oneSegIndex = " + oneSegIndex);
                    if (mCursor_floating != null) {
                        if (mCursor_floating.getCount() > pairIndex && pairIndex != -1) {
                            orgPos = mCursor_floating.getPosition();
                            mCursor_floating.moveToPosition(pairIndex);
                            info = FCI_TVi.GetPairNSegInfoOfCHIndex(pairIndex);
                            if (info[1] == tomove) {
                                findFail = 0;
                            } else {
                                mCursor_floating.moveToPosition(orgPos);
                                if (isFullseg == tomove) {
                                    //same index
                                    break;
                                }
                                findFail = 1;
                            }
                        }
                    } else {
                        findFail = 1;
                    }
                    TVlog.i("live", " >>> findFail = "+findFail);
                    if (findFail == 1) {
                        if (isFullseg == 1 && tomove == 0) {  //F-seg->O-seg
                            MainActivity.lastIndex = MainActivity.getInstance().mChannelIndex;
                            MainActivity.getInstance().mChannelIndex = oneSegIndex;
                            TVlog.i("live", " changed to O-seg index 1 = " + MainActivity.getInstance().mChannelIndex);
                            CommonStaticData.fromFindFail = true;
                            TVBridge.serviceID_start(MainActivity.getInstance().mChannelIndex);
                        } else if (isFullseg == 0 && tomove == 1) {  //O-seg->F-seg
                            TVlog.i("live", " >>> CommonStaticData.fromFindFail = "+CommonStaticData.fromFindFail);
                            if (CommonStaticData.fromFindFail == true) {
                                MainActivity.getInstance().mChannelIndex = MainActivity.lastIndex;
                                TVlog.i("live", " changed to F-seg index 2 = " + MainActivity.getInstance().mChannelIndex);
                                CommonStaticData.fromFindFail = false;
                                TVBridge.serviceID_start(MainActivity.getInstance().mChannelIndex);
                            } else {
                                MainActivity.getInstance().mChannelIndex = pairIndex;
                                TVlog.i("live", " changed to F-seg index 3 = " + MainActivity.getInstance().mChannelIndex);
                                CommonStaticData.fromFindFail = false;
                                TVBridge.serviceID_start(MainActivity.getInstance().mChannelIndex);
                            }
                        }
                    } else {
                        MainActivity.getInstance().mChannelIndex = pairIndex;
                        if (tomove == 0) { // to O-seg
                            MainActivity.getInstance().mChannelIndex = oneSegIndex;
                            TVlog.i("live", "changed to 1-seg index 4 =" + MainActivity.getInstance().mChannelIndex);
                            CommonStaticData.fromFindFail = false;
                            TVBridge.serviceID_start(MainActivity.getInstance().mChannelIndex);
                        } else if (tomove == 1) { // to F-seg
                            TVlog.i("live", " >>> CommonStaticData.fromFindFail = "+CommonStaticData.fromFindFail);
                            if (CommonStaticData.fromFindFail == true) {
                                MainActivity.getInstance().mChannelIndex = MainActivity.lastIndex;
                                TVlog.i("live", " changed to F-seg index 5 = " + MainActivity.getInstance().mChannelIndex);
                                CommonStaticData.fromFindFail = false;
                                TVBridge.serviceID_start(MainActivity.getInstance().mChannelIndex);
                            } else {
                                MainActivity.getInstance().mChannelIndex = pairIndex;
                                TVlog.i("live", " changed to F-seg index 6 = " + MainActivity.getInstance().mChannelIndex);
                                CommonStaticData.fromFindFail = false;
                                TVBridge.serviceID_start(MainActivity.getInstance().mChannelIndex);
                            }
                        }
                    }
                    break;

                case E_BADSIGNAL_CHECK_FLOATING:
                    TVlog.i(TAG, " >>>>> E_BADSIGNAL_CHECK_FLOATING");
                    int stat = (int)msg.arg1;
                    /*
                    if (isPlayBackActivity) {
                        break;
                    }*/
                    switch (stat) {
                        case 1: // low buffer
                            if (SignalStatFlag==false) {
                                TVlog.i(TAG, " >>>>> E_BADSIGNAL_CHECK_FLOATING CASE1");
                                if (ll_floatingAutoSearch.getVisibility() == View.VISIBLE) {
                                    ll_floatingAutoSearch.setVisibility(View.INVISIBLE);
                                }
                                postEvent(E_SIGNAL_NOTI_MSG_FLOATING, NO_SIGNAL_MSG_TIME);     // 10sec
                                //MainActivity.getInstance().postEvent(TVEVENT.E_SIGNAL_NOTI_MSG, NO_SIGNAL_MSG_TIME);
                                SignalStatFlag = true;
                                CommonStaticData.badSignalFlag = true;  // live add
                                CommonStaticData.encryptFlag = false;  // live add
                                CommonStaticData.ageLimitFlag = false;  // live add
                            }
                            break;
                        case 2: // scramble channel
                            TVlog.i(TAG, " >>>>> E_BADSIGNAL_CHECK_FLOATING CASE2");
                            if (ll_floatingAutoSearch.getVisibility() == View.VISIBLE) {
                                ll_floatingAutoSearch.setVisibility(View.INVISIBLE);
                            }
                            //channelChangeEndViewMulti(true);
                            if (isChannelListViewOn) {
                                if (buildOption.FCI_SOLUTION_MODE !=buildOption.JAPAN && buildOption.FCI_SOLUTION_MODE !=buildOption.JAPAN_ONESEG
                                        && buildOption.FCI_SOLUTION_MODE != buildOption.JAPAN_USB
                                        && buildOption.FCI_SOLUTION_MODE !=buildOption.JAPAN_FILE) {
                                    ChannelMainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_LIST_ENCRYPTED);
                                }
                            } else {
                                //new InputDialog(instance, InputDialog.TYPE_SCRAMBLE_NOTI, null, null, null);
                                CommonStaticData.encryptFlag = true;
                                CommonStaticData.badSignalFlag = false;  // live add
                                CommonStaticData.ageLimitFlag = false;  // live add

                                if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                                    //ll_scramble_msg.setVisibility(View.VISIBLE);
                                    tv_scramble_title.setVisibility(View.INVISIBLE);
                                    tv_scramble_msg.setVisibility(View.INVISIBLE);
                                } else {
                                    channelChangeEndView(true);
                                    floating_noSignal.setVisibility(View.INVISIBLE);
                                    floating_programNotMsg.setVisibility(View.INVISIBLE);
                                    tv_scramble_title.setVisibility(View.VISIBLE);
                                    tv_scramble_msg.setVisibility(View.VISIBLE);
                                }
                            }
                            break;
                        case 3: //RF signal bad
                            TVlog.i(TAG, " >>>>> E_BADSIGNAL_CHECK_FLOATING CASE3");
                            if (ll_floatingAutoSearch.getVisibility() == View.VISIBLE) {
                                ll_floatingAutoSearch.setVisibility(View.INVISIBLE);
                            }
                            if (CommonStaticData.scanningNow==false && CommonStaticData.scanCHnum > 0) {
                                //if (CommonStaticData.scanningNow==false) {

                                //new InputDialog(instance, InputDialog.TYPE_SIGNALSTAT_NOTI, null, null, null);

                                // live add

                                CommonStaticData.badSignalFlag = true;
                                CommonStaticData.encryptFlag = false;
                                CommonStaticData.ageLimitFlag = false;

                                // live add
                                if (sv_floatingView != null && sv_floatingView.isShown()) {
                                    sv_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
                                }
                                if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                                    if (svSub_floatingView != null && svSub_floatingView.isShown()) {
                                        svSub_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
                                    }
                                } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                                        (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                                buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                                buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                                    if (svSub_floatingView != null && svSub_floatingView.isShown()) {
                                        svSub_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
                                    }
                                }
                                //if (tv_scramble_title.getVisibility() == View.INVISIBLE) {
                                tv_scramble_title.setVisibility(View.INVISIBLE);
                                floating_noSignal.setVisibility(View.VISIBLE);
                                floating_programNotMsg.setVisibility(View.VISIBLE);
                                //}

                                if (floating_ll_audioOnlyChannel.getVisibility() == View.VISIBLE) {
                                    floating_ll_audioOnlyChannel.setVisibility(View.INVISIBLE);
                                }

                                if (floating_changeChannelView.getVisibility() == View.VISIBLE) {
                                    floating_changeChannelView.setVisibility(View.INVISIBLE);
                                }

                                signal_check_cnt = 0;
                                if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB) {
                                    if (CommonStaticData.autoSearch == 0 && CommonStaticData.scanCHnum > 0) {
                                        MainActivity.getInstance().postEvent(TVEVENT.E_SCAN_HANDOVER_START, HANDOVER_TIME);     // 3sec
                                        sendEvent(TVEVENT.E_CAPTION_CLEAR_NOTIFY_FLOATING);
                                        sendEvent(TVEVENT.E_SUPERIMPOSE_CLEAR_NOTIFY_FLOATING);
                                    }
                                }
                            } else {
                                if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB) {
                                    if (CommonStaticData.autoSearch == 0 && CommonStaticData.scanCHnum > 0) {
                                        MainActivity.getInstance().postEvent(TVEVENT.E_SCAN_HANDOVER_START, HANDOVER_TIME);     // 3sec
                                        sendEvent(TVEVENT.E_CAPTION_CLEAR_NOTIFY_FLOATING);
                                        sendEvent(TVEVENT.E_SUPERIMPOSE_CLEAR_NOTIFY_FLOATING);
                                    } else {
                                        removeEvent(TVEVENT.E_BADSIGNAL_CHECK_FLOATING);
                                    }
                                } else {
                                    removeEvent(TVEVENT.E_BADSIGNAL_CHECK_FLOATING);
                                }
                                //removeEvent(TVEVENT.E_BADSIGNAL_CHECK_FLOATING);
                                //MainActivity.getInstance().removeEvent(TVEVENT.E_BADSIGNAL_CHECK);
                            }
                            break;
                        case 4: //program not available
                            TVlog.i(TAG, " >>>>> E_BADSIGNAL_CHECK_FLOATING CASE4");
                            if (ll_floatingAutoSearch.getVisibility() == View.VISIBLE) {
                                ll_floatingAutoSearch.setVisibility(View.INVISIBLE);
                            }
                            if (CommonStaticData.scanningNow==false && CommonStaticData.scanCHnum > 0) {
                                //channelChangeEndViewMulti(false);
                                /*
                                CustomToast toast7 = new CustomToast(getApplicationContext());
                                toast7.showToast(getApplicationContext(),
                                        getApplicationContext().getString(R.string.no_signal_msg)+"\n"+
                                                getApplicationContext().getString(R.string.program_not_available), Toast.LENGTH_SHORT);
                                */

                                // live add
                                CommonStaticData.badSignalFlag = true;
                                CommonStaticData.encryptFlag = false;
                                CommonStaticData.ageLimitFlag = false;
                                if (sv_floatingView != null && sv_floatingView.isShown()) {
                                    sv_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
                                }
                                if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                                    if (svSub_floatingView != null && svSub_floatingView.isShown()) {
                                        svSub_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
                                    }
                                } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                                        (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                                buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                                buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                                    if (svSub_floatingView != null && svSub_floatingView.isShown()) {
                                        svSub_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
                                    }
                                }

                                if (floating_changeChannelView.getVisibility() == View.VISIBLE) {
                                    floating_changeChannelView.setVisibility(View.INVISIBLE);
                                }

                                //if (tv_scramble_title.getVisibility() == View.INVISIBLE) {
                                tv_scramble_title.setVisibility(View.INVISIBLE);
                                floating_noSignal.setVisibility(View.VISIBLE);
                                floating_programNotMsg.setVisibility(View.VISIBLE);
                                //}
                                if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB) {
                                    if (CommonStaticData.autoSearch == 0 && CommonStaticData.scanCHnum > 0) {
                                        MainActivity.getInstance().postEvent(TVEVENT.E_SCAN_HANDOVER_START, HANDOVER_TIME);     // 3sec
                                        sendEvent(TVEVENT.E_CAPTION_CLEAR_NOTIFY_FLOATING);
                                        sendEvent(TVEVENT.E_SUPERIMPOSE_CLEAR_NOTIFY_FLOATING);
                                    }
                                }
                            } else {
                                if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB) {
                                    if (CommonStaticData.autoSearch == 0 && CommonStaticData.scanCHnum > 0) {
                                        MainActivity.getInstance().postEvent(TVEVENT.E_SCAN_HANDOVER_START, HANDOVER_TIME);     // 3sec
                                        sendEvent(TVEVENT.E_CAPTION_CLEAR_NOTIFY_FLOATING);
                                        sendEvent(TVEVENT.E_SUPERIMPOSE_CLEAR_NOTIFY_FLOATING);
                                    } else {
                                        removeEvent(TVEVENT.E_BADSIGNAL_CHECK_FLOATING);
                                    }
                                } else {
                                    removeEvent(TVEVENT.E_BADSIGNAL_CHECK_FLOATING);
                                }
                                //MainActivity.getInstance().removeEvent(TVEVENT.E_BADSIGNAL_CHECK);
                            }
                            break;
                    }
                    break;

                case E_SCAN_MONITOR_FLOATING:
                    TVlog.i(TAG, " >>>>> E_SCAN_MONITOR_FLOATING");
                    if (CommonStaticData.scanCHnum != 0) {
                        removeEvent(E_SCAN_MONITOR_FLOATING);
                        //MainActivity.getInstance().removeEvent(TVEVENT.E_SCAN_MONITOR);
                    } else {
                        if (buildOption.ADD_TS_CAPTURE != true) {
                            new InputDialog(instance, InputDialog.TYPE_TV_NOCHANNELLIST, null, null, null);
                        }
                        postEvent(E_SCAN_MONITOR_FLOATING, CONTROLLER_HIDE_TIME * 2);
                        //MainActivity.getInstance().postEvent(TVEVENT.E_SCAN_MONITOR, CONTROLLER_HIDE_TIME * 2);
                    }
                    break;

                case E_SIGNAL_MONITER_FLOATING:
                    TVlog.i(TAG, " >>>>> E_SIGNAL_MONITER_FLOATING");
                    if (signalMoniter != null)
                    {
                        int segType;

                        signalMoniter.getSignal();

                        /*
                        if (buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL
                                || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_ONESEG
                                || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                                || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_FILE) {
                            floating_curr_rate = FCI_TVi.GetCurProgramRating();
                            if (CommonStaticData.ageLimitFlag) {
                                if (sv_floatingView != null) {
                                    sv_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
                                }
                                floating_age_limit_title.setVisibility(View.VISIBLE);
                                floating_age_limit_msg.setVisibility(View.VISIBLE);

                                FCI_TVi.setVolume(0.0f);
                            } else {
                                if (sv_floatingView != null) {
                                    sv_floatingView.setBackgroundColor(getResources().getColor(R.color.transparent));
                                }
                                floating_age_limit_title.setVisibility(View.INVISIBLE);
                                floating_age_limit_msg.setVisibility(View.INVISIBLE);
                                FCI_TVi.setVolume(1.0f);
                            }
                        }*/

                        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                                || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB
                                || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                            postEvent(TVEVENT.E_SIGNAL_MONITER_FLOATING, SIGNAL_MONITER_TIME_USB);
                        } else {
                            postEvent(TVEVENT.E_SIGNAL_MONITER_FLOATING, SIGNAL_MONITER_TIME);
                        }
                        //MainActivity.getInstance().sendEvent(TVEVENT.E_RATING_MONITOR);
                        //MainActivity.getInstance().postEvent(TVEVENT.E_SIGNAL_MONITER, SIGNAL_MONITER_TIME);
                    }
                    break;

                case E_SIGNAL_NOTI_MSG_FLOATING:
                    TVlog.i(TAG, " >>>>> E_SIGNAL_NOTI_MSG_FLOATING");
                    if (CommonStaticData.scanningNow==false) {
                        CustomToast toast4 = new CustomToast(getApplicationContext());
                        toast4.showToast(getApplicationContext(), getApplicationContext().getString(R.string.signal_weak), Toast.LENGTH_SHORT);
                        SignalStatFlag = false;
                        postEvent(E_NOSIGNAL_SHOW_FLOATING, NO_SIGNAL_MSG_TIME);     // 5sec
                        //MainActivity.getInstance().postEvent(TVEVENT.E_NOSIGNAL_SHOW, NO_SIGNAL_MSG_TIME);
                    }
                    break;

                case E_NOSIGNAL_SHOW_FLOATING:
                    TVlog.i(TAG, " >>>>> E_NOSIGNAL_SHOW_FLOATING");
                    if (CommonStaticData.scanningNow==false) {
                        sendEvent(TVEVENT.E_BADSIGNAL_CHECK_FLOATING, 3, 0, null);
                        //MainActivity.getInstance().sendEvent(TVEVENT.E_BADSIGNAL_CHECK, 3, 0, null);
                    }
                    break;

                case E_FLOATING_SURFACE_SUB_ONOFF: {
                    int onoff = (int) msg.arg1;
                    if (onoff==1)
                    {
                        TVlog.i(TAG, " E_FLOATING_SURFACE_SUB_ON  On ") ;
                        setFloatingSubSurfaceVisible(true);
                    } else {
                        TVlog.i(TAG, " E_FLOATING_SURFACE_SUB_ON  Off ") ;
                        setFloatingSubSurfaceVisible(false);
                    }
                }
                break;

                case E_RATING_MONITOR_FLOATING:
                    floating_curr_rate = FCI_TVi.GetCurProgramRating();  // curr_rate 2~6, PG_Rate 1~5
                    TVlog.i("justin", " ====> E_RATING_MONITOR_FLOATING :: floating_curr_rate " + floating_curr_rate + " , Set PG-rate" + CommonStaticData.PG_Rate);
                    if (buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL
                            || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_FILE
                            || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_ONESEG) {
                        if (CommonStaticData.ageLimitFlag) {
                            //TVBridge.stop();
                            if (sv_floatingView != null && sv_floatingView.isShown()) {
                                sv_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
                            }
                            if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                                if (svSub_floatingView != null && svSub_floatingView.isShown()) {
                                    svSub_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
                                }
                            } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                                    (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                            buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                            buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                                if (svSub_floatingView != null && svSub_floatingView.isShown()) {
                                    svSub_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
                                }
                            }
                            floating_ll_age_limit.setVisibility(View.VISIBLE);
                            floating_noSignal.setVisibility(View.INVISIBLE);
                            floating_programNotMsg.setVisibility(View.INVISIBLE);
                            tv_scramble_title.setVisibility(View.INVISIBLE);
                            tv_scramble_msg.setVisibility(View.INVISIBLE);
                            floating_changeChannelView.setVisibility(View.INVISIBLE);
                            FCI_TVi.setVolume(0.0f);
                        } else {
                            if (sv_floatingView != null && sv_floatingView.isShown()) {
                                sv_floatingView.setBackgroundColor(getResources().getColor(R.color.transparent));
                            }
                            if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                                if (svSub_floatingView != null && svSub_floatingView.isShown()) {
                                    svSub_floatingView.setBackgroundColor(getResources().getColor(R.color.transparent));
                                }
                            } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                                    (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                            buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                            buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                                if (svSub_floatingView != null && svSub_floatingView.isShown()) {
                                    svSub_floatingView.setBackgroundColor(getResources().getColor(R.color.transparent));
                                }
                            }
                            floating_ll_age_limit.setVisibility(View.INVISIBLE);
                            if (CommonStaticData.captionSwitch) {
                                if (subTitleView_floating != null) {
                                    subTitleView_floating.setVisibility(View.VISIBLE);
                                }
                            }
                            FCI_TVi.setVolume(1.0f);
                        }
                    }
                    break;

                /*
                case E_HIDE_TITLE :
                    hideTitle();
                    break;
                case E_SHOW_TITLE :
                    // mIsTouchFlag =true;
                    showTitle();
                    break;
                */

                case E_AUTO_CHANGE_CHANNEL_TEST_FLOATING:
                    TVlog.i(TAG, " >>>>> E_AUTO_CHANGE_CHANNEL_TEST_FLOATING");
                {

                    if (buildOption.LOG_CAPTURE_MODE ==3)
                    {
                        int currentChannel = TVBridge.getCurrentChannel();

                        TVlog.i(TAG, " E_AUTO_CHANGE_CHANNEL_TEST  currentID = " + currentChannel + " ChannelCount = "+CommonStaticData.scanCHnum );
                        if (currentChannel < (CommonStaticData.scanCHnum -1)) {
                            CommonStaticData.passwordVerifyFlag = false;
                            CommonStaticData.ageLimitFlag = false;
                            //CommonStaticData.screenBlockFlag = false;

                            //sendEvent(TVEVENT.E_CAPTION_CLEAR_NOTIFY);
                            //sendEvent(TVEVENT.E_SUPERIMPOSE_CLEAR_NOTIFY);

                            //channelChangeStartViewMulti(false);  // live remove
                            //changeChannelView.setVisibility(View.VISIBLE);
                            TVBridge.AVStartPlus();

                            postEvent(TVEVENT.E_AUTO_CHANGE_CHANNEL_TEST_FLOATING,20*1000);
                        } else {
                            TVlog.i(TAG, " E_AUTO_CHANGE_CHANNEL_TEST  END ~~~~~~~~~~~");
                            MainActivity.getInstance().TVTerminate();
                        }
                    }
                }

                case E_CHANNEL_NAME_UPDATE_FLOATING:
                {
                    mCursor_floating = MainActivity.getCursor();
                    if (mCursor_floating != null && mCursor_floating.isClosed() == false) {
                        TVlog.i(TAG, " >>>>> E_CHANNEL_NAME_UPDATE_FLOATING");
                        if (MainActivity.floatingFromMain) {
                            channelChangeEndView(false);
                            MainActivity.floatingFromMain = false;
                        }
                        MainActivity.getInstance().mChannelIndex = CommonStaticData.lastCH;
                        mCursor_floating.moveToPosition(MainActivity.getInstance().mChannelIndex);
                        if (CommonStaticData.scanCHnum != 0) {

                            int freq = Integer.parseInt(mCursor_floating.getString(CommonStaticData.COLUMN_INDEX_SERVICE_FREQ));
                            TVlog.i(TAG, " >>>>> current freq = " + freq);

                            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                                int channelNo = 13 + (int)((freq-473143)/6000);
                                currChNo_floating.setText(channelNo+"ch");
                            } else {
                                if (buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_ONESEG
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                                    // for Sri Lanka
                                    int channelNo = 13 + (int)((freq-474000)/8000);
                                    currChNo_floating.setText(channelNo+"ch");
                                } else {
                                    int channelNo = 14 + (int)((freq-473143)/6000);
                                    currChNo_floating.setText(channelNo+"ch");
                                }
                            }

                            String channelName = mCursor_floating.getString(CommonStaticData.COLUMN_INDEX_SERVICE_NAME);
                            String[] split_channelName = channelName.split(" ");

                            // live modify 20170104
                            currRemoteNo_floating.setText(split_channelName[0]);
                            String str = "";
                            for (int i = 1; i < split_channelName.length; i++) {
                                str += split_channelName[i];
                                if (i < split_channelName.length - 1) {
                                    str += " ";
                                }
                            }
                            currCH_floating.setText(str);
                            //

                            int type = (int) mCursor_floating.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV);
                            int free = (int) mCursor_floating.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_FREE);
                            if (type == 0) { // if 1seg
                                if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                                    iv_ChType_floating.setBackgroundResource(R.drawable.jp_1seg);
                                    iv_ChType_floating.setScaleX(0.8f);
                                    iv_ChType_floating.setScaleY(0.8f);
                                    iv_ChFree_floating.setVisibility(View.GONE);
                                } else {
                                    iv_ChType_floating.setBackgroundResource(R.drawable.tv_icon_1seg);
                                    if (free == 0) {
                                        iv_ChFree_floating.setVisibility(View.VISIBLE);
                                    } else {
                                        iv_ChFree_floating.setVisibility(View.GONE);
                                    }
                                }
                            } else if (type == 1) { // if fullseg
                                if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                                    iv_ChType_floating.setBackgroundResource(R.drawable.jp_fullseg);
                                    iv_ChType_floating.setScaleX(0.8f);
                                    iv_ChType_floating.setScaleY(0.8f);
                                    iv_ChFree_floating.setVisibility(View.GONE);
                                } else {
                                    iv_ChType_floating.setBackgroundResource(R.drawable.tv_icon_fullseg);
                                    if (free == 0) {
                                        iv_ChFree_floating.setVisibility(View.VISIBLE);
                                    } else {
                                        iv_ChFree_floating.setVisibility(View.GONE);
                                    }
                                }
                            }

                            //chat_currCH.setText(mCursor_floating.getString(CommonStaticData.COLUMN_INDEX_SERVICE_NAME));
                            AudioFormat = mCursor_floating.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_AUDFORM);
                            VideoFormat = mCursor_floating.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_VIDFORM);
                            Scrambled = mCursor_floating.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_FREE);
                            mRemoteKey = mCursor_floating.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_REMOTE_KEY);
                            mSvcNumber = mCursor_floating.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_NUMBER);

                            TVlog.i (TAG, " >>>>> Scrambled = "+String.valueOf(Scrambled));

                            if (Scrambled == 0) {
                                sendEvent(TVEVENT.E_BADSIGNAL_CHECK_FLOATING, 2, 0, null);
                            }
                        }
                    }
                }
                break;

                case E_FLOATING_SURFACE_CREATED:
                {

                    TVlog.i(TAG, " E_FLOATING_SURFACE_CREATED");
                    Surface subSurface = FloatingSubSurface.getFloatingSubSurface().getFloatingSurface();
                    if (subSurface !=null) {
                        setDualSurface(subSurface);

                    } else {
                        TVlog.i(TAG, " Retry Create Surface  later Start TV");
                        postEvent(TVEVENT.E_FLOATING_SURFACE_CREATED, 100);
                    }

                }
                break;

                case E_HIDE_FLOATING_CONTROLLER :
                    hideFloatingController();
                    break;

                case E_SHOW_FLOATING_CONTROLLER :
                    // mIsTouchFlag =true;
                    showFloatingController();
                    break;
            }
            super.handleMessage(msg);
        }
    };


    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        TVlog.i(TAG, " ===== FloatingWindow onCreate() =====");

        isFloating = true;
        MainActivity.isMainActivity = false;
        ChatMainActivity.isChat = false;

        //audioOut_floating = new AudioOut(this);

        mSysReceiver_Floating = new SysBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_HEADSET_PLUG);

        registerReceiver(mSysReceiver_Floating, filter);

        //live add
        mScreenOff_Floating = new SysBroadcastReceiver();
        IntentFilter off_filter = new IntentFilter();
        off_filter.addAction(Intent.ACTION_SCREEN_OFF);
        // [[ eddy 160706
        off_filter.addAction(Intent.ACTION_USER_PRESENT);
        // ]] eddy 160706
        //off_filter.setPriority(999);
        registerReceiver(mScreenOff_Floating, off_filter);

        //live add
        mScreenOn_Floating = new SysBroadcastReceiver();
        IntentFilter on_filter = new IntentFilter();
        on_filter.addAction(Intent.ACTION_SCREEN_ON);
        // [[ eddy 160706
        on_filter.addAction(Intent.ACTION_USER_PRESENT);
        // ]] eddy 160706
        //on_filter.setPriority(999);
        registerReceiver(mScreenOn_Floating, on_filter);

        // justin phone status debug 20161110
        MyPhoneStateListener phoneStateListener = new MyPhoneStateListener();
        TelephonyManager telephony = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        //

        // usbdongle[[
        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
            mUsbReceiver = new SysBroadcastReceiver();
            IntentFilter usb_filter = new IntentFilter();
            usb_filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
            registerReceiver(mUsbReceiver, usb_filter);
        }
        // ]]usbdongle


        //FloatingWindow.this.mParams = new WindowManager.LayoutParams();

        /*
        if (MainActivity.dpiName.contains("xhdpi")) {
            this.mParams = new WindowManager.LayoutParams(
                    MainActivity.getInstance().frameWidth/2,
                    MainActivity.getInstance().frameHeight/2,
                    WindowManager.LayoutParams.TYPE_TOAST,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                    PixelFormat.TRANSLUCENT);
        } else if (MainActivity.dpiName.contains("hdpi")) {
            mParams = new WindowManager.LayoutParams(
                    MainActivity.getInstance().frameWidth/2,
                    MainActivity.getInstance().frameHeight/2,
                    WindowManager.LayoutParams.TYPE_TOAST,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                    PixelFormat.TRANSLUCENT);
        } else if (MainActivity.dpiName.contains("mdpi")) {
            mParams = new WindowManager.LayoutParams(
                    MainActivity.getInstance().frameWidth/2,
                    MainActivity.getInstance().frameHeight/2,
                    WindowManager.LayoutParams.TYPE_TOAST,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                    PixelFormat.TRANSLUCENT);
        }*/

        if ((MainActivity.dpiName.contains("mdpi")) || (MainActivity.dpiName.contains("ldpi")) || (MainActivity.screenSize.contains("large"))) {
            if (MainActivity.getInstance() != null) {
                if (MainActivity.getInstance().frameHeight != 0 && MainActivity.getInstance().frameWidth != 0) {
                    this.mParams = new WindowManager.LayoutParams(
                            MainActivity.getInstance().frameWidth/2,
                            MainActivity.getInstance().frameHeight/2,
                            WindowManager.LayoutParams.TYPE_PHONE,
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                            PixelFormat.TRANSLUCENT);
                }
            }
            if (mParams != null) {
                mParams.gravity = Gravity.LEFT | Gravity.TOP;
                if (MainActivity.getInstance() != null) {
                    mParams.x = MainActivity.getInstance().frameWidth/2;
                    mParams.y = MainActivity.getInstance().frameHeight/2;
                }
            }
        } else {
            if (MainActivity.getInstance() != null) {
                if (MainActivity.getInstance().frameHeight != 0 && MainActivity.getInstance().frameWidth != 0) {
                    this.mParams = new WindowManager.LayoutParams(
                            MainActivity.getInstance().frameHeight,
                            (3 * MainActivity.getInstance().frameHeight) / 4,
                            WindowManager.LayoutParams.TYPE_PHONE,
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                            PixelFormat.TRANSLUCENT);
                }
            }
            if (mParams != null) {
                mParams.gravity = Gravity.LEFT | Gravity.TOP;
            }
        }


        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        /*
        displayMetrics = new DisplayMetrics();
        windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        floatingpxHeight = displayMetrics.heightPixels;
        floatingpxWidth = displayMetrics.widthPixels;
        */

        /*
        final WindowManager.LayoutParams parameters = new WindowManager.LayoutParams(
                600, 400, WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        parameters.gravity = Gravity.CENTER | Gravity.CENTER;
        parameters.x = 0;
        parameters.y = 0;
        */

        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        floating_view = inflater.inflate(R.layout.multi_window, null);

        final PopupWindow popupWindow = new PopupWindow(floating_view, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.update();

        //ViewHolder viewHolder = new ViewHolder();
        //floating_view.setTag(viewHolder);


        /*
        floating_view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        floatingpxHeight = floating_view.getMeasuredHeight();
        floatingpxWidth = floating_view.getMeasuredWidth();
        */

        sv_floatingView = (SurfaceView) floating_view.findViewById(R.id.sv_multiview);
        if (sv_floatingView != null && sv_floatingView.isShown()) {
            sv_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
        }
        if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
            svSub_floatingView =(SurfaceView) floating_view.findViewById(R.id.svSub_floating);
            if (svSub_floatingView != null && svSub_floatingView.isShown()) {
                svSub_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
            }
        } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {

            svSub_floatingView =(SurfaceView) floating_view.findViewById(R.id.svSub_floating);
            if (svSub_floatingView != null && svSub_floatingView.isShown()) {
                svSub_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
            }
        }

        instance = FloatingWindow.this;
        floatingVideoSurfaceHolder = sv_floatingView.getHolder();

        if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
            if (svSub_floatingView != null) {
                floatingVideoSurfaceHolderSub = svSub_floatingView.getHolder();
            }
        } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
            if (svSub_floatingView != null) {
                floatingVideoSurfaceHolderSub = svSub_floatingView.getHolder();
            }
        }

        int h = sv_floatingView.getMeasuredHeight();
        int w = sv_floatingView.getMeasuredWidth();

        floatingVideoSurfaceHolder.setFixedSize(w, h);
        floatingVideoSurfaceHolder.addCallback(this);
        if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
            if (svSub_floatingView != null) {
                floatingVideoSurfaceHolderSub.setFixedSize(w, h);
                floatingVideoSurfaceHolderSub.addCallback(SubSurfaceSet.getSubSurfaceSet());
            }
        } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
            if (svSub_floatingView != null) {
                floatingVideoSurfaceHolderSub.setFixedSize(w, h);
                floatingVideoSurfaceHolderSub.addCallback(FloatingSubSurface.getFloatingSubSurface());
            }
        }

        floating_channelChangeBG = (ImageView) floating_view.findViewById(R.id.floating_imageView_bg);

        floating_ll_audioOnlyChannel = (LinearLayout) floating_view.findViewById(R.id.floating_ll_audioOnlyChannel);
        if (floating_ll_audioOnlyChannel != null) {
            floating_ll_audioOnlyChannel.setVisibility(View.INVISIBLE);
        }

        floating_ll_black = (LinearLayout) floating_view.findViewById(R.id.floating_ll_black);
        if (CommonStaticData.isAudioChannel == true) {
            floating_ll_black.setVisibility(View.VISIBLE);
        } else {
            floating_ll_black.setVisibility(View.INVISIBLE);
        }

        ll_floatingAutoSearch = (LinearLayout) floating_view.findViewById(R.id.ll_floatingAutoSearch);
        ll_floatingAutoSearch.setVisibility(View.INVISIBLE);

        progressing_autoSearch_floating = (ProgressBar) floating_view.findViewById(R.id.progressing_autoSearch_floating);
        progressing_autoSearch_floating.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);

        btn_return_floating = (Button) floating_view.findViewById(R.id.btn_return_floating);
        if (btn_return_floating != null) {
            btn_return_floating.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideFloatingController();
                    ll_floatingAutoSearch.setVisibility(View.INVISIBLE);
                    sendEvent(TVEVENT.E_SCAN_CANCEL_FLOATING);

                }
            });
        }

        floating_noSignal = (me.grantland.widget.AutofitTextView) floating_view.findViewById(R.id.no_signal_msg);
        if (Build.VERSION.SDK_INT <= 19) {
            floating_noSignal.setTextColor(getResources().getColor(R.color.white));
        }
        floating_noSignal.setVisibility(View.INVISIBLE);

        floating_programNotMsg = (me.grantland.widget.AutofitTextView) floating_view.findViewById(R.id.program_not_msg);
        if (Build.VERSION.SDK_INT <= 19) {
            floating_programNotMsg.setTextColor(getResources().getColor(R.color.white));
        }
        floating_programNotMsg.setVisibility(View.INVISIBLE);

        floating_noChannel = (me.grantland.widget.AutofitTextView) floating_view.findViewById(R.id.floating_noChannel);
        if (MainActivity.isNoChannel) {
            floating_noChannel.setVisibility(VISIBLE);
        }

        floating_ll_age_limit = (LinearLayout) floating_view.findViewById(R.id.floating_ll_age_limit);
        floating_ll_age_limit.setVisibility(View.INVISIBLE);

        floating_age_limit_title = (me.grantland.widget.AutofitTextView) floating_view.findViewById(R.id.floating_age_limit_title);
        floating_age_limit_msg = (me.grantland.widget.AutofitTextView) floating_view.findViewById(R.id.floating_age_limit_msg);

        if (Build.VERSION.SDK_INT <= 19) {
            floating_age_limit_title.setTextColor(getResources().getColor(R.color.white));
            floating_age_limit_msg.setTextColor(getResources().getColor(R.color.white));
        }

        tv_scramble_title = (me.grantland.widget.AutofitTextView) floating_view.findViewById(R.id.tv_scramble_title);
        tv_scramble_title.setVisibility(View.INVISIBLE);
        tv_scramble_msg = (me.grantland.widget.AutofitTextView) floating_view.findViewById(R.id.tv_scramble_msg);
        tv_scramble_msg.setVisibility(View.INVISIBLE);
        if (Build.VERSION.SDK_INT <= 19) {
            tv_scramble_title.setTextColor(getResources().getColor(R.color.white));
            tv_scramble_msg.setTextColor(getResources().getColor(R.color.white));
        }

        floating_channel_search = (me.grantland.widget.AutofitTextView) floating_view.findViewById(R.id.floating_channel_search);
        floating_scan_found = (me.grantland.widget.AutofitTextView) floating_view.findViewById(R.id.floating_scan_found);
        floating_loadingChannel = (me.grantland.widget.AutofitTextView) floating_view.findViewById(R.id.floating_loading_channel);
        tv_autoSearch_title_floating = (me.grantland.widget.AutofitTextView) floating_view.findViewById(R.id.tv_autoSearch_title_floating);
        tv_autoSearch_msg_floating = (me.grantland.widget.AutofitTextView) floating_view.findViewById(R.id.tv_autoSearch_msg_floating);

        TVlog.i(TAG, " >>>>> CommonStaticData.badSignaltFlag= "+CommonStaticData.badSignalFlag+
                ", CommonStaticData.encryptFlag = "+CommonStaticData.encryptFlag);

        // live add
        if (CommonStaticData.badSignalFlag == true) {
            if (sv_floatingView != null && sv_floatingView.isShown()) {
                sv_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
            }
            if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                if (svSub_floatingView != null && svSub_floatingView.isShown()) {
                    svSub_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
                }
            } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                    (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                            buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                            buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                if (svSub_floatingView != null && svSub_floatingView.isShown()) {
                    svSub_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
                }
            }
            if (floating_noSignal.getVisibility() == View.INVISIBLE) {
                floating_noSignal.setVisibility(View.VISIBLE);
            }
            if (floating_programNotMsg.getVisibility() == View.INVISIBLE) {
                floating_programNotMsg.setVisibility(View.VISIBLE);
            }
        } else if (CommonStaticData.badSignalFlag == false) {
            if (sv_floatingView != null && sv_floatingView.isShown()) {
                sv_floatingView.setBackgroundColor(getResources().getColor(R.color.transparent));
            }
            if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                if (svSub_floatingView != null && svSub_floatingView.isShown()) {
                    svSub_floatingView.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
            } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                    (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                            buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                            buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                if (svSub_floatingView != null && svSub_floatingView.isShown()) {
                    svSub_floatingView.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
            }
            if (floating_noSignal.getVisibility() == View.VISIBLE) {
                floating_noSignal.setVisibility(View.INVISIBLE);
            }
            if (floating_programNotMsg.getVisibility() == View.VISIBLE) {
                floating_programNotMsg.setVisibility(View.INVISIBLE);
            }

            if (CommonStaticData.isAudioChannel == true) {
                if (floating_ll_black != null) {
                    floating_ll_black.setVisibility(View.VISIBLE);
                }
                floating_ll_audioOnlyChannel.setVisibility(View.VISIBLE);
            } else {
                if (floating_ll_black != null) {
                    floating_ll_black.setVisibility(View.INVISIBLE);
                }
                floating_ll_audioOnlyChannel.setVisibility(View.INVISIBLE);
            }
        }

        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
            if (CommonStaticData.encryptFlag == true) {
                if (sv_floatingView != null && sv_floatingView.isShown()) {
                    sv_floatingView.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
                if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                    if (svSub_floatingView != null && svSub_floatingView.isShown()) {
                        svSub_floatingView.setBackgroundColor(getResources().getColor(R.color.transparent));
                    }
                } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                        (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                    if (svSub_floatingView != null && svSub_floatingView.isShown()) {
                        svSub_floatingView.setBackgroundColor(getResources().getColor(R.color.transparent));
                    }
                }
                if (tv_scramble_msg.getVisibility() == View.VISIBLE) {
                    tv_scramble_title.setVisibility(View.INVISIBLE);
                    tv_scramble_msg.setVisibility(View.INVISIBLE);
                }
                if (CommonStaticData.isAudioChannel == true) {
                    if (floating_ll_black != null) {
                        floating_ll_black.setVisibility(View.VISIBLE);
                    }
                    floating_ll_audioOnlyChannel.setVisibility(View.VISIBLE);
                } else {
                    if (floating_ll_black != null) {
                        floating_ll_black.setVisibility(View.INVISIBLE);
                    }
                    floating_ll_audioOnlyChannel.setVisibility(View.INVISIBLE);
                }
            } else {
                if (CommonStaticData.isAudioChannel == true) {
                    if (floating_ll_black != null) {
                        floating_ll_black.setVisibility(View.VISIBLE);
                    }
                    floating_ll_audioOnlyChannel.setVisibility(View.VISIBLE);
                } else {
                    if (sv_floatingView != null && sv_floatingView.isShown()) {
                        sv_floatingView.setBackgroundColor(getResources().getColor(R.color.transparent));
                    }
                    if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                        if (svSub_floatingView != null && svSub_floatingView.isShown()) {
                            svSub_floatingView.setBackgroundColor(getResources().getColor(R.color.transparent));
                        }
                    } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                            (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                    buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                    buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                        if (svSub_floatingView != null && svSub_floatingView.isShown()) {
                            svSub_floatingView.setBackgroundColor(getResources().getColor(R.color.transparent));
                        }
                    }
                    if (floating_ll_black != null) {
                        floating_ll_black.setVisibility(View.INVISIBLE);
                    }
                    floating_ll_audioOnlyChannel.setVisibility(View.INVISIBLE);
                }
            }
        } else {
            if (CommonStaticData.encryptFlag == true) {
                if (sv_floatingView != null && sv_floatingView.isShown()) {
                    sv_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
                }
                if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                    if (svSub_floatingView != null && svSub_floatingView.isShown()) {
                        svSub_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
                    }
                } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                        (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                    if (svSub_floatingView != null && svSub_floatingView.isShown()) {
                        svSub_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
                    }
                }
                if (tv_scramble_msg.getVisibility() == View.INVISIBLE) {
                    tv_scramble_title.setVisibility(View.VISIBLE);
                    tv_scramble_msg.setVisibility(View.VISIBLE);
                }
            } else if (CommonStaticData.encryptFlag == false) {
                if (sv_floatingView != null && sv_floatingView.isShown()) {
                    sv_floatingView.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
                if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                    if (svSub_floatingView != null && svSub_floatingView.isShown()) {
                        svSub_floatingView.setBackgroundColor(getResources().getColor(R.color.transparent));
                    }
                } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                        (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                    if (svSub_floatingView != null && svSub_floatingView.isShown()) {
                        svSub_floatingView.setBackgroundColor(getResources().getColor(R.color.transparent));
                    }
                }
                if (tv_scramble_msg.getVisibility() == View.VISIBLE) {
                    tv_scramble_title.setVisibility(View.INVISIBLE);
                    tv_scramble_msg.setVisibility(View.INVISIBLE);
                }
            }
        }

        if (buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL
                || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_ONESEG
                || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_FILE) {
            if (CommonStaticData.ageLimitFlag) {
                if (sv_floatingView != null && sv_floatingView.isShown()) {
                    sv_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
                }
                if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                    if (svSub_floatingView != null && svSub_floatingView.isShown()) {
                        svSub_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
                    }
                } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                        (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                    if (svSub_floatingView != null && svSub_floatingView.isShown()) {
                        svSub_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
                    }
                }
                floating_ll_age_limit.setVisibility(View.VISIBLE);
            } else if (!CommonStaticData.ageLimitFlag) {
                if (sv_floatingView != null && sv_floatingView.isShown()) {
                    sv_floatingView.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
                if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                    if (svSub_floatingView != null && svSub_floatingView.isShown()) {
                        svSub_floatingView.setBackgroundColor(getResources().getColor(R.color.transparent));
                    }
                } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                        (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                    if (svSub_floatingView != null && svSub_floatingView.isShown()) {
                        svSub_floatingView.setBackgroundColor(getResources().getColor(R.color.transparent));
                    }
                }
                floating_ll_age_limit.setVisibility(View.INVISIBLE);
            }
        }

        floating_ll_scan_progress = (LinearLayout) floating_view.findViewById(R.id.floating_ll_scan_progress);
        floating_ll_scan_progress.setVisibility(View.INVISIBLE);

        //floating_scan_found = (TextView) floating_view.findViewById(R.id.floating_scan_found);
        floating_scan_progressBar = (ProgressBar) floating_view.findViewById(R.id.floating_scan_progressBar);
        floating_scan_progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
        floating_btn_scan_cancel = (Button) floating_view.findViewById(R.id.floating_btn_scan_cancel);
        if (floating_btn_scan_cancel != null) {
            floating_btn_scan_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideFloatingController();
                    floating_ll_scan_progress.setVisibility(View.INVISIBLE);
                    sendEvent(TVEVENT.E_SCAN_CANCEL_FLOATING);
                }
            });
        }

        floating_changeChannelView = (LinearLayout) floating_view.findViewById(R.id.floating_progressBarCircularIndeterminate);
        floating_changeChannelView.setVisibility(View.INVISIBLE);

        //floating_progressingChange = (CustomView) floating_view.findViewById(R.id.floating_progressing_channel);
        floating_progressingChange = (ProgressBar) floating_view.findViewById(R.id.floating_progressing_channel);
        floating_progressingChange.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);

        //floating_loadingChannel = (TextView) floating_view.findViewById(R.id.floating_loading_channel);

        status_bar_floating = (LinearLayout) floating_view.findViewById(R.id.status_bar_floating);
        if (status_bar_floating != null) {
            status_bar_floating.setVisibility(View.INVISIBLE);
        }

        ImageView signalImage_floating = (ImageView) floating_view.findViewById(R.id.dtv_signal_floating);
        signalImage_floating.setScaleX(0.8f);
        signalImage_floating.setScaleY(0.8f);
        signalMoniter = new SignalMonitor(signalImage_floating);

        currChNo_floating = (TextView) floating_view.findViewById(R.id.tv_ch_no_floating);
        //currChNo_floating.setText(MainActivity.getInstance().currChNo.getText());
        if (MainActivity.getInstance() != null) {
            tf_floating = MainActivity.getInstance().tf;
        }
        currChNo_floating.setTypeface(tf_floating);
        currChNo_floating.setTextSize(18);

        if (buildOption.VIEW_PHY_CH) {
            currChNo_floating.setVisibility(View.VISIBLE);
        } else {
            currChNo_floating.setVisibility(View.GONE);
        }

        rl_ChType_floating = (RelativeLayout) floating_view.findViewById(R.id.rl_ChType_floating);
        iv_ChType_floating = (ImageView) floating_view.findViewById(R.id.iv_ChType_floating);
        iv_ChFree_floating = (ImageView) floating_view.findViewById(R.id.iv_ChFree_floating);

        if (CommonStaticData.scanCHnum > 0) {
            if (mCursor_floating != null) {
                int type = (int) mCursor_floating.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV);
                int free = (int) mCursor_floating.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_FREE);
                if (type == 0) { // if 1seg
                    if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                        iv_ChType_floating.setBackgroundResource(R.drawable.jp_1seg);
                        iv_ChType_floating.setScaleX(0.8f);
                        iv_ChType_floating.setScaleY(0.8f);
                        iv_ChType_floating.setVisibility(View.GONE);
                    } else {
                        iv_ChType_floating.setBackgroundResource(R.drawable.tv_icon_1seg);
                        if (free == 0) {
                            iv_ChFree_floating.setVisibility(View.VISIBLE);
                        } else {
                            iv_ChFree_floating.setVisibility(View.GONE);
                        }
                    }
                } else if (type == 1) { // if fullseg
                    if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                        iv_ChType_floating.setBackgroundResource(R.drawable.jp_fullseg);
                        iv_ChType_floating.setScaleX(0.8f);
                        iv_ChType_floating.setScaleY(0.8f);
                        iv_ChType_floating.setVisibility(View.GONE);
                    } else {
                        iv_ChType_floating.setBackgroundResource(R.drawable.tv_icon_fullseg);
                        if (free == 0) {
                            iv_ChFree_floating.setVisibility(View.VISIBLE);
                        } else {
                            iv_ChFree_floating.setVisibility(View.GONE);
                        }
                    }
                }
                rl_ChType_floating.setVisibility(View.VISIBLE);
            }
        } else {
            rl_ChType_floating.setVisibility(View.GONE);
        }

        currRemoteNo_floating = (TextView) floating_view.findViewById(R.id.tv_remote_no_floating);
        //currRemoteNo_floating.setText(MainActivity.getInstance().currRemoteNo.getText());
        currCH_floating = (TextView) floating_view.findViewById(R.id.servicename_floating);
        //currCH_floating.setText(MainActivity.getInstance().currCH.getText());
        //if (buildOption.GUI_STYLE == 1) {
        //currCH_floating.setSelected(true);
        //}

        channelLayout_floating = (LinearLayout) floating_view.findViewById(R.id.channelLayout_floating);
        channelLayout_floating.setVisibility(View.INVISIBLE);

        ch_up_floating = (ImageView) floating_view.findViewById(R.id.ch_up_floating);
        ch_up_floating.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ch_up_floating.setScaleX(0.5f);
                    ch_up_floating.setScaleY(0.5f);
                    ch_up_floating.setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    ch_up_floating.setScaleX(1.0f);
                    ch_up_floating.setScaleY(1.0f);
                    ch_up_floating.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
                }
                return false;
            }
        });
        ch_up_floating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // justin DB
                CommonStaticData.passwordVerifyFlag = false;
                CommonStaticData.ageLimitFlag = false;
                //channelChangeStartView(false);
                if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                    if (CommonStaticData.receivemode == 2 || CommonStaticData.receivemode == 3) {
                        int[] info = FCI_TVi.GetPairNSegInfoOfCHIndex(CommonStaticData.lastCH);
                        int isPaired = 0;
                        int pairedIndex = info[0];
                        TVlog.i("live", " >>> ch_up :: pairedIndex = "+pairedIndex+", CommonStaticData.lastCH = "+CommonStaticData.lastCH);
                        if (pairedIndex == CommonStaticData.lastCH-1) {
                            isPaired = 1;
                        } else {
                            isPaired = 0;
                        }

                        TVlog.i("live", " >>> ch_up :: isPaired = "+isPaired);

                        if (isPaired == 1) {
                            channelChangeEndView(false);
                            //changeChannelView.setVisibility(View.INVISIBLE);
                        } else if (isPaired == 0) {
                            channelChangeStartView(false);
                            //changeChannelView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        channelChangeStartView(false);
                    }
                } else {
                    channelChangeStartView(false);
                }
                sendEvent(E_CAPTION_CLEAR_NOTIFY_FLOATING);
                sendEvent(TVEVENT.E_SUPERIMPOSE_CLEAR_NOTIFY_FLOATING);

                if (ll_floatingAutoSearch.getVisibility() == VISIBLE) {
                    ll_floatingAutoSearch.setVisibility(View.INVISIBLE);
                }
                //changeChannelView.setVisibility(View.VISIBLE);
                // live add

                if (tv_scramble_title.getVisibility() == View.VISIBLE) {
                    tv_scramble_title.setVisibility(View.INVISIBLE);
                }
                if (tv_scramble_msg.getVisibility() == View.VISIBLE) {
                    tv_scramble_msg.setVisibility(View.INVISIBLE);
                }
                floating_ll_age_limit.setVisibility(View.INVISIBLE);
                TVBridge.AVStartMinus();
            }
        });

        ch_down_floating = (ImageView) floating_view.findViewById(R.id.ch_down_floating);
        ch_down_floating.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ch_down_floating.setScaleX(0.5f);
                    ch_down_floating.setScaleY(0.5f);
                    ch_down_floating.setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    ch_down_floating.setScaleX(1.0f);
                    ch_down_floating.setScaleY(1.0f);
                    ch_down_floating.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
                }
                return false;
            }
        });
        ch_down_floating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonStaticData.passwordVerifyFlag = false;
                CommonStaticData.ageLimitFlag = false;
                //channelChangeStartView(false);
                if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                    if (CommonStaticData.receivemode == 2 || CommonStaticData.receivemode == 3) {
                        int[] info = FCI_TVi.GetPairNSegInfoOfCHIndex(CommonStaticData.lastCH);
                        int isPaired = 0;
                        int pairedIndex = info[0];
                        TVlog.i("live", " >>> ch_down :: pairedIndex = "+pairedIndex+", CommonStaticData.lastCH = "+CommonStaticData.lastCH);
                        if (pairedIndex == CommonStaticData.lastCH+1) {
                            isPaired = 1;
                        } else {
                            isPaired = 0;
                        }
                        TVlog.i("live", " >>> ch_down :: isPaired = "+isPaired);

                        if (isPaired == 1) {
                            channelChangeEndView(false);
                        } else if (isPaired == 0) {
                            channelChangeStartView(false);
                            //changeChannelView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        channelChangeStartView(false);
                    }

                } else {
                    channelChangeStartView(false);
                }
                sendEvent(E_CAPTION_CLEAR_NOTIFY_FLOATING);
                sendEvent(TVEVENT.E_SUPERIMPOSE_CLEAR_NOTIFY_FLOATING);
                // live add
                if (ll_floatingAutoSearch.getVisibility() == VISIBLE) {
                    ll_floatingAutoSearch.setVisibility(View.INVISIBLE);
                }
                if (tv_scramble_title.getVisibility() == View.VISIBLE) {
                    tv_scramble_title.setVisibility(View.INVISIBLE);
                }
                if (tv_scramble_msg.getVisibility() == View.VISIBLE) {
                    tv_scramble_msg.setVisibility(View.INVISIBLE);
                }
                floating_ll_age_limit.setVisibility(View.INVISIBLE);
                TVBridge.AVStartPlus();
            }
        });

        ll_controller_floating = (LinearLayout) floating_view.findViewById(ll_controller);
        ll_controller_floating.setVisibility(View.INVISIBLE);

        iv_scan = (ImageView) floating_view.findViewById(R.id.iv_scan);
        iv_scan.setPadding(0, 10, 0, 10);
        iv_scan.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    iv_scan.setScaleX(0.8f);
                    iv_scan.setScaleY(0.8f);
                    iv_scan.setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {

                    iv_scan.setScaleX(1.0f);
                    iv_scan.setScaleY(1.0f);
                    iv_scan.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
                }
                return false;
            }
        });
        iv_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Thread.sleep(BUTTON_CLICK_TIME);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // live add
                if (sv_floatingView != null && sv_floatingView.isShown()) {
                    sv_floatingView.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
                if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                    if (svSub_floatingView != null && svSub_floatingView.isShown()) {
                        svSub_floatingView.setBackgroundColor(getResources().getColor(R.color.transparent));
                    }
                } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                        (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                    if (svSub_floatingView != null && svSub_floatingView.isShown()) {
                        svSub_floatingView.setBackgroundColor(getResources().getColor(R.color.transparent));
                    }
                }
                hideFloatingController();

                if (floating_noSignal.getVisibility() == View.VISIBLE) {
                    floating_noSignal.setVisibility(View.INVISIBLE);
                }
                if (floating_programNotMsg.getVisibility() == View.VISIBLE) {
                    floating_programNotMsg.setVisibility(View.INVISIBLE);
                }

                if (tv_scramble_msg.getVisibility() == View.VISIBLE) {
                    tv_scramble_title.setVisibility(View.INVISIBLE);
                    tv_scramble_msg.setVisibility(View.INVISIBLE);
                }

                sendEvent(TVEVENT.E_SCAN_START_FLOATING);
            }
        });

        iv_max = (ImageView) floating_view.findViewById(R.id.iv_max);
        iv_max.setPadding(0, 10, 0, 10);
        iv_max.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    iv_max.setScaleX(0.8f);
                    iv_max.setScaleY(0.8f);
                    iv_max.setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    iv_max.setScaleX(1.0f);
                    iv_max.setScaleY(1.0f);
                    iv_max.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
                }
                return false;
            }
        });
        iv_max.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CommonStaticData.returnMainFromFloating = true;
                TVlog.i(TAG, " >>>>> CommonStaticData.returnMainFromFloating = "+CommonStaticData.returnMainFromFloating);

                MainActivity.isMainActivity = true;
                isFloating = false;
                ChatMainActivity.isChat = false;

                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                editor.putInt(CommonStaticData.lastChannelKey, CommonStaticData.lastCH);
                editor.putInt(CommonStaticData.scanedChannelsKey, CommonStaticData.scanCHnum);
                editor.putBoolean(CommonStaticData.loudSpeakerKey, CommonStaticData.loudSpeaker);

                // live add
                editor.putBoolean(CommonStaticData.badSignalFlagKey, CommonStaticData.badSignalFlag);
                editor.putBoolean(CommonStaticData.encryptFlagKey, CommonStaticData.encryptFlag);
                editor.putBoolean(CommonStaticData.ageLimitFlagKey, CommonStaticData.ageLimitFlag);
                editor.putBoolean(CommonStaticData.passwordVerifyFlagKey, CommonStaticData.passwordVerifyFlag);
                //editor.putBoolean(CommonStaticData.screenBlockFlagKey, CommonStaticData.screenBlockFlag);
                editor.putBoolean(CommonStaticData.returnMainFromFloatingKey, CommonStaticData.returnMainFromFloating);
                editor.putInt(CommonStaticData.receivemodeSwitchKey, CommonStaticData.receivemode);
                //
                editor.commit();

                AudioManager am = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                am.abandonAudioFocus(afChangeListener);
                MainActivity.floatingFromMain = false;

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
                if (floating_view != null) {
                    mWindowManager.removeView(floating_view);
                    mWindowManager = null;
                }
                stopSelf();
                exit(0);
            }
        });

        iv_close = (ImageView) floating_view.findViewById(R.id.iv_close);
        iv_close.setPadding(0, 10, 0, 10);
        if (iv_close != null) {
            iv_close.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        iv_close.setScaleX(0.8f);
                        iv_close.setScaleY(0.8f);
                        iv_close.setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        iv_close.setScaleX(1.0f);
                        iv_close.setScaleY(1.0f);
                        iv_close.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
                    }
                    return false;
                }
            });
            iv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isFloating = false;
                    if (floating_view != null) {
                        mWindowManager.removeView(floating_view);
                        mWindowManager = null;
                    }
                    stopSelf();
                    exit(0);
                }
            });
        }

        /*
        arr_svcmodeswitch_jp = getResources().getStringArray(R.array.svcmode_switch_jp);
        btn_receiveMode = (Button) floating_view.findViewById(R.id.button_receiveMode);
        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
            btn_receiveMode.setVisibility(View.VISIBLE);
        } else {
            btn_receiveMode.setVisibility(View.GONE);
        }
        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
            CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
           int receiveMode = CommonStaticData.settings.getInt(CommonStaticData.receivemodeSwitchKey, 2);  // auto
            TVlog.i("live", " >>> receiveMode = "+receiveMode);
            btn_receiveMode.setText(arr_svcmodeswitch_jp[receiveMode]);
        }

        btn_receiveMode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn_receiveMode.setScaleX(0.8f);
                    btn_receiveMode.setScaleY(0.8f);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn_receiveMode.setScaleX(1.0f);
                    btn_receiveMode.setScaleY(1.0f);
                }
                return false;
            }
        });
        btn_receiveMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int isChanged = 0;
                int[] info = FCI_TVi.GetPairNSegInfoOfCHIndex(CommonStaticData.lastCH);
                int isPaired = 0;
                int pairedIndex = info[0];
                int segInfo = info[1];
                if (pairedIndex >= 0) {
                    isPaired = 1;
                }

                if (CommonStaticData.receivemode == 1) {  //fullseg --> 1seg
                    CommonStaticData.receivemode = 0;
                    //if (isChanged == 1) {
                    if (CommonStaticData.scanCHnum > 0) {
                    sendEvent(TVEVENT.E_CHANNEL_SWITCHING_FLOATING, 0, 0, null);
                    } else {
                        CustomToast toast = new CustomToast(getApplicationContext());
                        toast.showToast(getApplicationContext(), getApplicationContext().getString(R.string.no_channel_tip), Toast.LENGTH_SHORT);
                    }
                    //}
                    btn_receiveMode.setText(arr_svcmodeswitch_jp[0]);
                } else if (CommonStaticData.receivemode == 0) {  //1seg --> auto
                    CommonStaticData.receivemode = 2;
                    //if (isChanged == 1) {
                    if (CommonStaticData.scanCHnum > 0) {
                    if (isPaired == 1) {
                        if (segInfo == 1) { //F-seg
                            FCI_TVi.AVStart(CommonStaticData.lastCH, FCI_TV.CHSTART_DUAL_F_SEG);
                        } else { //O-seg
                            FCI_TVi.AVStart(CommonStaticData.lastCH, FCI_TV.CHSTART_DUAL_O_SEG);
                        }
                    } else {
                        FCI_TVi.AVStart(CommonStaticData.lastCH, FCI_TV.CHSTART_SINGLE);
                    }
                    } else {
                        CustomToast toast = new CustomToast(getApplicationContext());
                        toast.showToast(getApplicationContext(), getApplicationContext().getString(R.string.no_channel_tip), Toast.LENGTH_SHORT);
                    }
                    btn_receiveMode.setText(arr_svcmodeswitch_jp[2]);
                } else if (CommonStaticData.receivemode == 2) {   //auto --> off
                    CommonStaticData.receivemode = 3;
                    //if (isChanged == 1) {
                    if (CommonStaticData.scanCHnum > 0) {
                    if (isPaired == 1) {
                        if (segInfo == 1) { //F-seg
                            FCI_TVi.AVStart(CommonStaticData.lastCH, FCI_TV.CHSTART_DUAL_F_SEG);
                        } else {
                            FCI_TVi.AVStart(CommonStaticData.lastCH, FCI_TV.CHSTART_DUAL_O_SEG);
                        }
                    } else {
                        FCI_TVi.AVStart(CommonStaticData.lastCH, FCI_TV.CHSTART_SINGLE);
                    }
                    } else {
                        CustomToast toast = new CustomToast(getApplicationContext());
                        toast.showToast(getApplicationContext(), getApplicationContext().getString(R.string.no_channel_tip), Toast.LENGTH_SHORT);
                    }
                    //}
                    btn_receiveMode.setText(arr_svcmodeswitch_jp[3]);
                } else if (CommonStaticData.receivemode == 3) {  //off --> fullseg
                    CommonStaticData.receivemode = 1;
                    //if (isChanged == 1) {
                    if (CommonStaticData.scanCHnum > 0) {
                    sendEvent(TVEVENT.E_CHANNEL_SWITCHING_FLOATING, 1, 0, null);
                    } else {
                        CustomToast toast = new CustomToast(getApplicationContext());
                        toast.showToast(getApplicationContext(), getApplicationContext().getString(R.string.no_channel_tip), Toast.LENGTH_SHORT);
                    }
                    //}
                    btn_receiveMode.setText(arr_svcmodeswitch_jp[1]);
                }
            }
        });
        */

        subTitleView_floating = (TextView) floating_view.findViewById(R.id.floating_subTitleView);

        superImposeView_floating = (TextView) floating_view.findViewById(R.id.floating_superImposeView);
        // live add
        if (CommonStaticData.superimposeSwitch == true) {
            superImposeView_floating.setVisibility(View.VISIBLE);
        } else {
            superImposeView_floating.setVisibility(View.INVISIBLE);
        }

        try {
            if (floating_view != null && mParams != null) {
            mWindowManager.addView(floating_view, mParams);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            stopSelf();
        }

        /*
        floating_view.setOnTouchListener(new View.OnTouchListener() {
            WindowManager.LayoutParams updatedParameters = mParams;
            double x;
            double y;
            double pressedX;
            double pressedY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {

                    case MotionEvent.ACTION_UP:
                        if (ll_controller.getVisibility() != View.VISIBLE) {
                            ll_controller.setVisibility(View.VISIBLE);
                            postEvent(TVEVENT.E_HIDE_FLOATING_CONTROLLER, CONTROLLER_HIDE_TIME);
                        } else {
                            ll_controller.setVisibility(View.INVISIBLE);
                        }

                        break;

                    case MotionEvent.ACTION_DOWN:
                        x = updatedParameters.x;
                        y = updatedParameters.y;

                        pressedX = event.getRawX();
                        pressedY = event.getRawY();

                        break;

                    case MotionEvent.ACTION_MOVE:
                        updatedParameters.x = (int) (x + (event.getRawX() - pressedX));
                        updatedParameters.y = (int) (y + (event.getRawY() - pressedY));

                        mWindowManager.updateViewLayout(floating_view, updatedParameters);


                    default:
                        break;
                }

                return false;
            }
        });*/

        //scaleGestureDetector = new ScaleGestureDetector(this, new simpleOnScaleGestureListener());


        floating_view.setOnTouchListener(new View.OnTouchListener() {

            WindowManager.LayoutParams updatedParameters = mParams;

            double x;
            double y;
            double pressedX;
            double pressedY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int act = event.getAction();

                switch (act & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:    //첫번째 손가락 터치
                        x = updatedParameters.x;
                        y = updatedParameters.y;

                        pressedX = event.getRawX();
                        pressedY = event.getRawY();

                        mode = DRAG;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (false && event.getPointerCount() >= 2) {
                            float xLength = event.getX(1) - event.getX(0);
                            float yLength = event.getY(1) - event.getY(0);
                            currentDistance = (int) Math.sqrt(xLength * xLength + yLength * yLength);
                            if (lastDistance < 0) {
                                lastDistance = currentDistance;
                                break;
                            } else if (currentDistance - lastDistance > 5) {
                                //SIZE-UP
                                ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) floating_view.getLayoutParams();
                                if (lp.width < MainActivity.getInstance().pxHeight) {
                                    lp.height = (int) ((float) floating_view.getMeasuredHeight() * 1.2f);
                                    lp.width = (int) ((float) floating_view.getMeasuredWidth() * 1.2f);
                                } else if (lp.width >= MainActivity.getInstance().pxHeight) {
                                    lp.width = MainActivity.getInstance().frameHeight;
                                    lp.height = (3*MainActivity.getInstance().frameHeight)/4;
                                }
                                floating_view.setLayoutParams(lp);
                            } else if (currentDistance - lastDistance < -5) {
                                //SIZE-DOWN
                                ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) floating_view.getLayoutParams();
                                if (lp.width > (MainActivity.getInstance().frameHeight/2)) {
                                    lp.height = (int) ((float) floating_view.getMeasuredHeight() * 0.8f);
                                    lp.width = (int) ((float) floating_view.getMeasuredWidth() * 0.8f);
                                } else if (lp.width <= (MainActivity.getInstance().frameHeight/2)) {
                                    lp.width = MainActivity.getInstance().frameHeight/2;
                                    lp.height = ((3*MainActivity.getInstance().frameHeight)/4)/2;
                                }
                                floating_view.setLayoutParams(lp);
                            }
                            lastDistance = currentDistance;
                        } else {
                            //DRAG
                            updatedParameters.x = (int) (x + (event.getRawX() - pressedX));
                            updatedParameters.y = (int) (y + (event.getRawY() - pressedY));
                            mWindowManager.updateViewLayout(floating_view, updatedParameters);
                        }
                        break;

                    case MotionEvent.ACTION_UP:    // 첫번째 손가락을 떼었을 경우
                        if (ll_floatingAutoSearch.getVisibility() != VISIBLE) {
                            if (ll_controller_floating.getVisibility() != View.VISIBLE) {
                                status_bar_floating.setVisibility(View.VISIBLE);
                                channelLayout_floating.setVisibility(View.VISIBLE);
                                ll_controller_floating.setVisibility(View.VISIBLE);
                                postEvent(TVEVENT.E_HIDE_FLOATING_CONTROLLER, CONTROLLER_HIDE_TIME);
                            } else {
                                status_bar_floating.setVisibility(View.INVISIBLE);
                                channelLayout_floating.setVisibility(View.INVISIBLE);
                                ll_controller_floating.setVisibility(View.INVISIBLE);
                            }
                        }
                        break;

                    case MotionEvent.ACTION_POINTER_UP:  // 두번째 손가락을 떼었을 경우
                        mode = NONE;
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        //두번째 손가락 터치(손가락 2개를 인식하였기 때문에 핀치 줌으로 판별)
                        mode = ZOOM;
                        newDist = spacing(event);
                        oldDist = spacing(event);

                        break;
                    case MotionEvent.ACTION_CANCEL:
                    default:
                        break;
                }
                return true;
            }
        });

        // live add
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(getResources().getString(R.string.app_name))
                .setTicker(getResources().getString(R.string.app_name))
                .setContentText(getResources().getString(R.string.app_name)+" is running")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_service_notification)
                .setColor(getResources().getColor(R.color.blue3));

        Notification cur_notification = builder.build();
        startForeground(1, cur_notification);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub

        TVlog.i(TAG, " ===== FloatingWindow onStartCommand() =====");

        mHandler_floating = new Handler();

        MainActivity.isMainActivity = false;
        isFloating = true;
        ChatMainActivity.isChat = false;

        if (CommonStaticData.scanCHnum > 0) {
            if (rl_ChType_floating != null) {
                rl_ChType_floating.setVisibility(View.VISIBLE);
            }
        } else {
            if (currChNo_floating != null && currCH_floating != null) {
                currChNo_floating.setText("- -ch");
                currRemoteNo_floating.setText("- - -");
                currCH_floating.setText(R.string.no_channel_title);
            }
            if (rl_ChType_floating != null) {
                rl_ChType_floating.setVisibility(View.GONE);
            }
        }

        if (MainActivity.getInstance() != null) {
            if ((MainActivity.getInstance().mUsbChipType != MainActivity.getInstance().USB_CHIP_TYPE_NONE) && (CommonStaticData.scanCHnum < 1)) {
                MainActivity.getInstance().isBBFail = false;
                sendEvent(TVEVENT.E_SCAN_COMPLETED_FLOATING);
            }
        }

        if (buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL
                || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_ONESEG
                || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_FILE) {
            if ((floating_curr_rate >= (CommonStaticData.PG_Rate+1) && (CommonStaticData.PG_Rate!=0))
                    && (CommonStaticData.passwordVerifyFlag == false)
                    && (CommonStaticData.ratingsetSwitch == true)) {
                CommonStaticData.ageLimitFlag = true;
            }
        }

        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
            postEvent(TVEVENT.E_SIGNAL_MONITER_FLOATING, SIGNAL_MONITER_TIME_USB);
        } else {
            postEvent(TVEVENT.E_SIGNAL_MONITER_FLOATING, SIGNAL_MONITER_TIME);
        }

        if (CommonStaticData.badSignalFlag) {
            floating_changeChannelView.setVisibility(View.INVISIBLE);
            floating_noSignal.setVisibility(View.VISIBLE);
            floating_programNotMsg.setVisibility(View.VISIBLE);
            tv_scramble_title.setVisibility(View.INVISIBLE);
            tv_scramble_msg.setVisibility(View.INVISIBLE);
            floating_ll_age_limit.setVisibility(View.INVISIBLE);
        } else if (CommonStaticData.encryptFlag) {
            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                floating_changeChannelView.setVisibility(View.INVISIBLE);
                tv_scramble_title.setVisibility(View.INVISIBLE);
                tv_scramble_msg.setVisibility(View.INVISIBLE);
            } else {
                floating_changeChannelView.setVisibility(View.INVISIBLE);
                tv_scramble_title.setVisibility(View.VISIBLE);
                tv_scramble_msg.setVisibility(View.VISIBLE);
            }
        } else if (CommonStaticData.ageLimitFlag) {
            if (buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL
                    || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_FILE) {
                floating_changeChannelView.setVisibility(View.INVISIBLE);
                floating_ll_age_limit.setVisibility(View.VISIBLE);
            } else {
                floating_changeChannelView.setVisibility(View.INVISIBLE);
                floating_ll_age_limit.setVisibility(View.INVISIBLE);
            }
            floating_noSignal.setVisibility(View.INVISIBLE);
            floating_programNotMsg.setVisibility(View.INVISIBLE);
            tv_scramble_title.setVisibility(View.INVISIBLE);
            tv_scramble_msg.setVisibility(View.INVISIBLE);
        }
        /*
        if (!CommonStaticData.badSignalFlag) {
            notifyFirstVideoFloating();
        }

        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
            postEvent(TVEVENT.E_SIGNAL_MONITER_FLOATING, SIGNAL_MONITER_TIME_USB);
        } else {
            postEvent(TVEVENT.E_SIGNAL_MONITER_FLOATING, SIGNAL_MONITER_TIME);
        }*/

        sendEvent(TVEVENT.E_CHANNEL_NAME_UPDATE_FLOATING);
        sendEvent(TVEVENT.E_BADSIGNAL_CHECK_FLOATING);

        if (floating_changeChannelView != null) {
            if (channelChangeProcLocation != null) {
                floating_changeChannelView.setX(channelChangeProcLocation[0]);
                floating_changeChannelView.setY(channelChangeProcLocation[1]);
            }
        }

        return START_STICKY;
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    @Override
    public void onDestroy() {
        TVlog.i(TAG, " ===== FloatingWindow onDestroy() =====");

        isFloating = false;
        removeEvent(TVEVENT.E_SIGNAL_MONITER_FLOATING);
        removeEvent(TVEVENT.E_FLOATING_SURFACE_CREATED);
        if (mWindowManager != null) {
            if (floating_view != null) {
                try {
                    mWindowManager.removeView(floating_view);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    stopSelf();
                }
                mWindowManager = null;
            }
        }

        if (mSysReceiver_Floating != null) {
            unregisterReceiver(mSysReceiver_Floating);
        }

        if (mScreenOff_Floating != null) {
            unregisterReceiver(mScreenOff_Floating);
        }

        if (mScreenOn_Floating != null) {
            unregisterReceiver(mScreenOn_Floating);
        }

        // usbdongle[[
        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
            if (mUsbReceiver != null) {
                unregisterReceiver(mUsbReceiver);
            }
        }
        // ]]usbdongle

        super.onDestroy();
        floating_view = null;
        stopForeground(true);
        stopSelf();
    }


    void setDualSurface(Surface _subSurface)
    {
        TVlog.i(TAG, " setDualSurface  ---------------------------------------");
        FCI_TVi.setSuface(mainSurface);

        int mode = FCI_TVi.getDualMode();

        if (mode == FCI_TV.CHSTART_DUAL_O_SEG) {

            TVlog.i(TAG, "onStartCommand One-SEG mode ");
            svSub_floatingView.setVisibility(View.VISIBLE);
        } else {
            TVlog.i(TAG, "onStartCommand Noamel mode ");
            svSub_floatingView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        TVlog.i(TAG, " ===== FloatingWindow surfaceCreated =====");

        MainActivity.isMainActivity = false;
        isFloating = true;
        ChatMainActivity.isChat = false;

        if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {

            TVlog.i(TAG, " JAPAN , MEDIA, FLOATING Create Surface");

            Surface subSurface = FloatingSubSurface.getFloatingSubSurface().getFloatingSurface();
            mainSurface =holder.getSurface();
            if (subSurface != null) {
                setDualSurface(subSurface);
            } else {
                TVlog.i(TAG, " JAPAN , MEDIA, FLOATING Create Surface  later Start TV");
                postEvent(TVEVENT.E_FLOATING_SURFACE_CREATED, 100);
            }
        } else {
            //  MainActivity.getInstance().onStart_TV();
            if (sv_floatingView != null) {
                FCI_TVi.setSuface(holder.getSurface());
            }
        }
        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
            // [[ solution switching mode 20170223
            envSet_JP_floating();
            //]]
        } else {
            envSet_Normal();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        TVlog.i(TAG, " ===== FloatingWindow surfaceChanged =====");
        if (frameWidthFloating ==0 || frameHeightFloating ==0) {

            frameWidthFloating = width;
            frameHeightFloating= height;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        TVlog.i(TAG, " ===== FloatingWindow SurfaceDestroyed!!! =====");
        removeEvent(TVEVENT.E_BADSIGNAL_CHECK_FLOATING);
        removeEvent(E_SIGNAL_NOTI_MSG_FLOATING);
        removeEvent(E_NOSIGNAL_SHOW_FLOATING);
        removeEvent(TVEVENT.E_CHANNEL_CHANGE_TIMEOVER_FLOATING);
        removeEvent(TVEVENT.E_SIGNAL_MONITER_FLOATING);

        if (CommonStaticData.scanningNow) {
            doScan_floating.showProgress_floating(0, 0, 0, ScanProcess_floating.SHOW_PROGRESS_CLEAR_FLOATING);
        }

        //MainActivity.getInstance().SolutionStop();
        if (mWindowManager != null) {
            if (floating_view != null) {
                mWindowManager.removeView(floating_view);
                mWindowManager = null;
            }
        }
    }


    public static SurfaceView getSurfaceView() {
        if (sv_floatingView == null) {
            TVlog.i (TAG, " >>>>> FloatingWindow Service not started!!");
        }
        return sv_floatingView;
    }


    public void notifyFirstVideoFloating() {
        sendEvent(TVEVENT.E_FIRSTVIDEO_FLOATING);
    }

    public void notifyFirstAudioFloating() {
        sendEvent(TVEVENT.E_FIRSTAUDIO_FLOATING);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) { //pause
            TVlog.i(TAG, "AUDIOFOCUS_LOSS_TRANSIENT Pause >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            //TVBridge.stop();
            FCI_TVi.setVolume(0.0f);
        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) { //unmute
            // Resume playback
            TVlog.i(TAG, "AUDIOFOCUS_GAIN Resume >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
            if (CommonStaticData.scanningNow == false && CommonStaticData.scanCHnum > 0) {
                // TVBridge.serviceID_start(TVBridge.getCurrentChannel());
                FCI_TVi.setVolume(1.0f);

                if (am != null) {
                    int result = am.requestAudioFocus(afChangeListener, am.STREAM_MUSIC, am.AUDIOFOCUS_GAIN);

                }
            }
        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) { //mute
            TVlog.i(TAG, "AUDIOFOCUS_LOSS stop >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
            am.abandonAudioFocus(afChangeListener);
            FCI_TVi.setVolume(1.0f);
            //TVBridge.stop();
        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
        }
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch(state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    // TVlog.i("MyPhoneStateListener", " >>>>> CALL_STATE_IDLE "+state);
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    TVlog.i("MyPhoneStateListener", " >>>>> CALL_STATE_RINGING "+state);
                    FCI_TVi.setVolume(0.0f);
                    MainActivity.getInstance().SolutionStop();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //TVlog.i("MyPhoneStateListener", " >>>>> CALL_STATE_OFFHOOK "+state);
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    private class SysBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive (Context _ctx, Intent _intent) {
            String action = _intent.getAction();

            if (action.equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = _intent.getIntExtra("state", -1);

                switch (state) {
                    case 0: //disconnected
                        is_wired_headset = false;
                        TVlog.i("SysBroadcastReceiver", " >>>>> Headset is unpluged!!");
                        break;
                    case 1: //connected
                        is_wired_headset = true;
                        TVlog.i("SysBroadcastReceiver", " >>>>> Headset is pluged!!");
                        if (audioOut_floating != null) {
                            if (buildOption.ADD_LOUD_SPEAKER) {
                                audioOut_floating.setSpeakerMode(CommonStaticData.loudSpeaker);
                            } else {
                                audioOut_floating.setSpeakerMode(CommonStaticData.loudSpeaker);
                            }
                            break;
                        }
                    default:
                        break;
                }
            }

            if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                TVlog.i(TAG, " >>>>> Intent.ACTION_SCREEN_OFF");
                //MainActivity.getInstance().SolutionStop();
                if (floating_view != null) {
                    mWindowManager.removeView(floating_view);
                    mWindowManager = null;
                }
                stopSelf();
                exit(0);
            }

            /*
            if (action.equals(Intent.ACTION_SCREEN_ON)) {
                TVlog.i(TAG, " >>>>> Intent.ACTION_SCREEN_ON");
                MainActivity.getInstance().onStart_TV();
            }*/

            if (action.equals(Intent.ACTION_USER_PRESENT)) {
                TVlog.i(TAG, " >>>>> Intent.ACTION_USER_PRESENT1");
                if (isRunningInForeground())
                {
                    TVlog.i(TAG, " TV running fore ground");
                    if (!CommonStaticData.ageLimitFlag) {
                        MainActivity.getInstance().onStart_TV();
                    }
                }
                //MainActivity.getInstance().onStart_TV();
            }

            // usbdongle[[
            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                    buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB ||
                    buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB ||
                    buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                    TVlog.i("FCIISDBT::", "usb dongle detached !");
                    UsbDevice device = (UsbDevice) _intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device != null && currentUsbDevice != null && currentUsbDevice.equals(device)) {
                        // call your method that cleans up and closes communication with the device
                        if (floating_view != null) {
                            mWindowManager.removeView(floating_view);
                            mWindowManager = null;
                        }
                        stopSelf();
                        exit(0);
                    }
                }
            }
            // ]]usbdongle
        }
    }

    protected boolean isRunningInForeground() {
        ActivityManager manager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = manager.getRunningTasks(1);
        if (tasks.isEmpty()) {
            return false;
        }
        String topActivityName = tasks.get(0).topActivity.getPackageName();
        return topActivityName.equalsIgnoreCase(getPackageName());
    }

    /*
    private void channelChangeEndViewFloating(boolean _keepBG)
    {
        if (_keepBG ==false)
        {
            channelChangeBG.setVisibility(View.INVISIBLE);
        }
        //recTimeview.setVisibility(View.VISIBLE);
        //changeChannelView.setVisibility(View.INVISIBLE);
    }*/

    private void hideFloatingController() {
        //if (controllerLayout.isShown()) {
        TVlog.i(TAG, "== hideController ==");
        status_bar_floating.setVisibility(View.INVISIBLE);
        channelLayout_floating.setVisibility(View.INVISIBLE);
        ll_controller_floating.setVisibility(View.INVISIBLE);
        //}
    }

    private void showFloatingController() {
        if (CommonStaticData.handoverMode == 1) {
        } else {
            status_bar_floating.setVisibility(View.VISIBLE);
            channelLayout_floating.setVisibility(View.VISIBLE);
            ll_controller_floating.setVisibility(View.VISIBLE);

            /*
            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                int receiveMode = CommonStaticData.settings.getInt(CommonStaticData.receivemodeSwitchKey, 2);  // auto
                btn_receiveMode.setText(arr_svcmodeswitch_jp[receiveMode]);
            }*/

            postEvent(E_HIDE_FLOATING_CONTROLLER, CONTROLLER_HIDE_TIME);
        }
        //MainActivity.getInstance().postEvent(E_HIDE_FLOATING_CONTROLLER, CONTROLLER_HIDE_TIME);
    }

    public void sendEvent(TVEVENT _Event) {
        int m;
        m = _Event.ordinal();
        Message msg = Floating_Handler.obtainMessage(m);
        Floating_Handler.sendMessage(msg);
    }

    public void sendEvent(TVEVENT _Event, int[] regionCh) {
        int m;
        m = _Event.ordinal();
        Message msg = Floating_Handler.obtainMessage(m);
        msg.obj = (int[])regionCh;
        Floating_Handler.sendMessage(msg);
    }

    public void sendEvent(TVEVENT _Event, int _arg1, int _arg2, Object _obj) {
        int m;
        m = _Event.ordinal();
        Message msg = Floating_Handler.obtainMessage(m);
        msg.arg1 = _arg1;
        msg.arg2 = _arg2;
        msg.obj = _obj;
        Floating_Handler.sendMessage(msg);
    }

    public void postEvent(TVEVENT _Event,int _time )
    {
        int m;
        m = _Event.ordinal();
        Message msg = Floating_Handler.obtainMessage(m);
        Floating_Handler.sendEmptyMessageDelayed(m, _time);
    }

    public void postEvent(TVEVENT _Event, int _time, int _arg1)
    {
        int m;
        m = _Event.ordinal();
        Message msg = Floating_Handler.obtainMessage(m);
        msg.arg1 = _arg1;

        Floating_Handler.sendMessageDelayed(msg, _time);
    }

    public void removeEvent(TVEVENT _Event)
    {
        int m;
        m = _Event.ordinal();
        Message msg = Floating_Handler.obtainMessage(m);
        Floating_Handler.removeMessages(m);
    }

    /*
    static class ViewHolder {
        SurfaceView sv_floatingView;
        SurfaceHolder floatingVideoSurfaceHolder;
    }*/

    public void channelChangeStartView(boolean _cas)
    {
        if (ll_floatingAutoSearch.getVisibility() == VISIBLE) {
            ll_floatingAutoSearch.setVisibility(View.INVISIBLE);
        }
        if (floating_noChannel.getVisibility() == VISIBLE) {
            floating_noChannel.setVisibility(View.INVISIBLE);
        }
        if (floating_noSignal.getVisibility() == VISIBLE) {
            floating_noSignal.setVisibility(View.INVISIBLE);
        }
        if (floating_programNotMsg.getVisibility() == VISIBLE) {
            floating_programNotMsg.setVisibility(View.INVISIBLE);
        }
        floating_changeChannelView.setVisibility(View.VISIBLE);
        floating_channelChangeBG.setVisibility(View.VISIBLE);
        if (floating_progressingChange != null) {
        floating_progressingChange.setVisibility(View.VISIBLE);
        }
        if (_cas == false) {
            floating_loadingChannel.setVisibility(View.VISIBLE);
        }
        else{   // call from playback
            floating_loadingChannel.setVisibility(GONE);
        }
    }

    public void channelChangeEndView(boolean _keepBG)
    {
        if (_keepBG ==false) {
            floating_channelChangeBG.setVisibility(View.INVISIBLE);
        }
        floating_changeChannelView.setVisibility(View.INVISIBLE);
    }

    public int getFloatingChannelChangView()
    {
        int visual = floating_changeChannelView.getVisibility();

        if (visual == 0)
        {

            TVlog.i(TAG, "Viewing Ch change view ");
            return 0;
        } else
        {
            TVlog.i(TAG, "No Viewing  Ch change view ");
            return 1;
        }

    }

    public void envSet_JP_floating() {

        mFont_floating = MainActivity.getInstance().mFont;
        currCH_floating.setTypeface(mFont_floating);

        //use free font
        if (subTitleView_floating != null) {
            subTitleView_floating.setTypeface(mFont_floating);
        }
        if (superImposeView_floating != null) {
            superImposeView_floating.setTypeface(mFont_floating);
        }

        //JAPAN_CAPTION[[
        /*Display displayCap = mWindowManager.getDefaultDisplay();
        Point sizeCap = new Point();
        displayCap.getRealSize(sizeCap);
        int capWidth = sizeCap.x;
        int capHeight = sizeCap.y;*/
        if (floating_view != null) {
            //ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) floating_view.getLayoutParams();
            int capWidth = 0;
            int capHeight = 0;
            if ((MainActivity.dpiName.contains("mdpi")) || (MainActivity.dpiName.contains("ldpi")) || (MainActivity.screenSize.contains("large"))) {
                if (MainActivity.getInstance() != null) {
                    if (MainActivity.getInstance().frameHeight != 0 && MainActivity.getInstance().frameWidth != 0) {
                        capWidth = MainActivity.getInstance().frameWidth/2;
                        capHeight = MainActivity.getInstance().frameHeight/2;
                    }
                }
            } else {
                if (MainActivity.getInstance() != null) {
                    if (MainActivity.getInstance().frameHeight != 0 && MainActivity.getInstance().frameWidth != 0) {
                        capWidth = MainActivity.getInstance().frameHeight;
                        capHeight =  (3 * MainActivity.getInstance().frameHeight) / 4;
                    }
                }
            }

            TVlog.i("live", ">>> caption real width ="+capWidth+", caption real height="+capHeight);

            //caption
            mCaptionLayout_floating = (FrameLayout) floating_view.findViewById(R.id.frameLayout_floating);
            mCaptionView_floating = new CaptionDirectView(this, mCaptionLayout_floating, capWidth, capHeight, mFont_floating, M_TYPE_CAPTION_SUBTITLE);

            //superimpose
            mSuperimposeLayout_floating = (FrameLayout) floating_view.findViewById(R.id.frameLayout_floating);
            mSuperimposeView_floating = new CaptionDirectView(this, mSuperimposeLayout_floating, capWidth, capHeight, mFont_floating, M_TYPE_CAPTION_SUPERIMPOSE);

            //caption
            mCaptionView_floating.setVisibility(View.VISIBLE);
            mCaptionLayout_floating.addView(mCaptionView_floating);
            //setContentView(mCaptionLayout);

            //superimpose
            mSuperimposeLayout_floating.addView(mSuperimposeView_floating);
            //setContentView(mSuperimposeLayout);
            mSuperimposeView_floating.setVisibility(View.VISIBLE);

            //]]JAPAN_CAPTION
        }
    }

    public void envSet_Normal() {
        mFont_floating = Typeface.DEFAULT;
        currCH_floating.setTypeface(mFont_floating);
        subTitleView_floating.setTypeface(mFont_floating);
        superImposeView_floating.setTypeface(mFont_floating);
    }

    public void setFloatingSubSurfaceVisible(boolean _onoff) {
        if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
            if (svSub_floatingView !=null) {
                if (_onoff) {
                    TVlog.i(TAG, "= Sub Floating surface visible = ");
                    svSub_floatingView.setVisibility(VISIBLE);
                } else {
                    TVlog.i(TAG, "= Sub Floating surface invisible = ");
                    svSub_floatingView.setVisibility(View.INVISIBLE);
                }
            }
        } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
            if (svSub_floatingView !=null) {
                if (_onoff) {
                    TVlog.i(TAG, "= Sub Floating surface visible = ");
                    svSub_floatingView.setVisibility(VISIBLE);
                } else {
                    TVlog.i(TAG, "= Sub Floating surface invisible = ");
                    svSub_floatingView.setVisibility(View.INVISIBLE);
                }
            }
        } else {
            TVlog.e(TAG, "No AUTODETECT , but setFloatingSubSurfaceVisible call Error ");
        }
    }

    public void sendSubtitle(String capContents) {
        Bundle caption = new Bundle();
        caption.putString("caption_info", capContents);
        caption.putString("clear", "");
        sendEvent(TVEVENT.E_CAPTION_NOTIFY_FLOATING, 0, 0, caption);
    }

    public void sendSuperimpose(String superContents) {
        Bundle superimpose = new Bundle();
        superimpose.putString("superimpose_info", superContents);
        superimpose.putString("clear", "");
        sendEvent(TVEVENT.E_SUPERIMPOSE_NOTIFY_FLOATING, 0, 0, superimpose);
    }

    //JAPAN_CAPTION[[
    public void sendSubtitleDirect(byte[] capData, int capLen, byte isClear, byte isEnd, int[] capInfo) {
        if (FloatingWindow.isFloating) {
            if (mCaptionView_floating != null && mCursor_floating != null) {
                mCaptionView_floating.renderCaptionDirect(capData, capLen, isClear, isEnd, capInfo, mCursor_floating.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV));
            }
        }
    }

    public void sendSuperimposeDirect(byte[] supData, int supLen, byte isClear, byte isEnd, int[] supInfo) {
        if (FloatingWindow.isFloating) {
            if (mSuperimposeView_floating != null && mCursor_floating != null) {
                mSuperimposeView_floating.renderCaptionDirect(supData, supLen, isClear, isEnd, supInfo, mCursor_floating.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV));
            }
        }
    }
    //]]JAPAN_CAPTION

    public void scanNotify_floating(int idx, String desc, byte type, byte vFormat, byte aFormat, byte iFree, int remoteKey, int svcNum, int freqKHz, byte bLast) {
        ContentValues values = new ContentValues();
        if (bLast==2) {
            if (mCursor_floating != null) {
                int cursorCount = mCursor_floating.getCount();
                if (cursorCount > 0 && cursorCount > mCursor_floating.getPosition()) {
                    TVBridge.setLastRemoteKey(mCursor_floating.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_REMOTE_KEY));
                    TVBridge.setLastSvcID(mCursor_floating.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_NUMBER));
                    TVBridge.setLastListCount(cursorCount);
                    TVBridge.setLastFreq(mCursor_floating.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_FREQ));
                    CommonStaticData.isProcessingUpdate = true;
                }
                else {
                    if (cursorCount > 0) {
                        //TVlog.i("FCIISDBT::", ">>> exception: current pos invalid >>> pos="+mCursor.getPosition()+"of count="+cursorCount);
                    }
                }
            }
            CommonStaticData.loadingNow = true;
            getContentResolver().delete(MainActivity.getInstance().mUri,null,null);    // channel DB delete
        } else if (bLast == 1) {
            //postEvent(TVEVENT.E_CHLIST_UPDATE_FLOATING, 0);
            //postEvent(TVEVENT.E_EPGTITLE_UPDATE, 0);
        } else {
            values.put(TVProgram.Programs.TYPE, 1); //service type TV
            values.put(TVProgram.Programs.SERVICEID, idx);
            values.put(TVProgram.Programs.SERVICENAME, desc);
            values.put(TVProgram.Programs.FREQ, freqKHz);
            values.put(TVProgram.Programs.FREE, iFree);
            values.put(TVProgram.Programs.FAV, (CommonStaticData.serviceTVFavFlag >> idx) & 1);
            values.put(TVProgram.Programs.MTV, type); //if Fullseg or Oneseg
            values.put(TVProgram.Programs.VIDFORM, vFormat);
            values.put(TVProgram.Programs.AUDFORM, aFormat);
            values.put(TVProgram.Programs.REMOTEKEY, remoteKey);
            values.put(TVProgram.Programs.SVCNUM, svcNum);
            if (MainActivity.getInstance().mUri != null) {
                getContentResolver().insert(MainActivity.getInstance().mUri, values);
            }
        }
    }
}
