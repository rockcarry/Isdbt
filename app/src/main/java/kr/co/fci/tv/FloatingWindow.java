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
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import static kr.co.fci.tv.MainActivity.mCursor;
import static kr.co.fci.tv.R.id.ll_controller;
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

    //처음 이미지를 선택했을 때, 이미지의 X,Y 값과 클릭 지점 간의 거리
    private float offsetX;
    private float offsetY;

    // 드래그시 좌표 저장
    int posX1=0, posX2=0, posY1=0, posY2=0;

    // 핀치시 두좌표간의 거리 저장
    float oldDist = 1f;
    float newDist = 1f;

    // 드래그 모드인지 핀치줌 모드인지 구분
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
    LinearLayout ll_scan;
    ImageView iv_scan;

    LinearLayout ll_max;
    LinearLayout ll_close;
    ImageView iv_max;
    ImageView iv_close;

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

    public int frameWidthFloating  = 0;
    public int frameHeightFloating = 0;

    //private static Cursor mCursor_floating;

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

    me.grantland.widget.AutofitTextView floating_age_limit_title;
    me.grantland.widget.AutofitTextView floating_age_limit_msg;

    me.grantland.widget.AutofitTextView tv_scramble_title;
    me.grantland.widget.AutofitTextView tv_scramble_msg;

    me.grantland.widget.AutofitTextView floating_channel_search;
    public me.grantland.widget.AutofitTextView floating_scan_found;
    me.grantland.widget.AutofitTextView floating_loadingChannel;
    me.grantland.widget.AutofitTextView tv_autoSearch_title_floating;
    me.grantland.widget.AutofitTextView tv_autoSearch_msg_floating;

    private LinearLayout floating_changeChannelView =null;
    private ImageView floating_channelChangeBG = null;
    //private CustomView floating_progressingChange;
    private ProgressBar floating_progressingChange;
    //private TextView floating_loadingChannel;
    private int[] channelChangeProcLocation =null;


    private boolean SignalStatFlag =false;
    int signal_check_cnt = 0;
    //private ImageView channelChangeBG = null;
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

    public int floating_mChannelIndex = CommonStaticData.lastCH;

    WindowManager mWindowManager;

    public static kr.co.fci.tv.FloatingWindow instance;
    public static kr.co.fci.tv.FloatingWindow getInstance()
    {
        return instance;
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

                case E_SCAN_COMPLETED_FLOATING:
                    TVlog.i(TAG, " >>>>> E_SCAN_COMPLETED_FLOATING");
                    channelChangeStartView(false);
                    floating_ll_scan_progress.setVisibility(View.INVISIBLE);

                    TVlog.i(TAG, "---------------- E_SCAN_COMPLETED_FLOATING-------------------");
                    if (CommonStaticData.handoverMode > 0) {
                        if (CommonStaticData.handoverIndex != -1) {
                            floating_mChannelIndex = CommonStaticData.handoverIndex;
                            TVlog.e(TAG, "handover mode = " + CommonStaticData.handoverMode + " , channel index =  " + floating_mChannelIndex);
                        }
                        else {
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
                        //mCursor_floating = MainActivity.getCursor();
                        if (MainActivity.getInstance().mUri != null) {
                            MainActivity.getInstance().mCursor = getContentResolver().query(MainActivity.getInstance().mUri, CommonStaticData.PROJECTION, TVProgram.Programs.TYPE + "=?", CommonStaticData.selectionArgsTV, null);
                        }
                        if (MainActivity.getInstance().mCursor.getCount() > 0 && (MainActivity.getInstance().mCursor.getPosition() < MainActivity.getInstance().mCursor.getCount())) {
                            if (floating_mChannelIndex >= MainActivity.getInstance().mCursor.getCount()) {
                                floating_mChannelIndex = 0;
                            }
                            MainActivity.getInstance().mCursor.moveToPosition(floating_mChannelIndex);
                            mRemoteKey = MainActivity.getInstance().mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_REMOTE_KEY);
                            mSvcNumber = MainActivity.getInstance().mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_NUMBER);
                            if (CommonStaticData.isProcessingUpdate) {
                                if ((mRemoteKey != TVBridge.getLastRemoteKey()) || (mSvcNumber != TVBridge.getLastSvcID())) {
                                    int cursorCount = MainActivity.getInstance().mCursor.getCount();
                                    if (cursorCount >= TVBridge.getLastListCount()) { //service is increased or contents are changed.
                                        for (int i = 0; i < cursorCount; i++) {
                                            MainActivity.getInstance().mCursor.moveToPosition(i);
                                            if ((MainActivity.getInstance().mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_REMOTE_KEY) == TVBridge.getLastRemoteKey())
                                                    && (MainActivity.getInstance().mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_NUMBER) == TVBridge.getLastSvcID())) {
                                                if (floating_mChannelIndex != i) {
                                                    statusOfNeedToChange = NEED_TO_CHANGE_CHANNEL_CHANGE_INDEX;
                                                }
                                                floating_mChannelIndex = i;
                                                break;
                                            }
                                        }
                                    } else { //service is decreased.
                                        for (int i = 0; i < cursorCount; i++) {
                                            MainActivity.getInstance().mCursor.moveToPosition(i);
                                            if ((MainActivity.getInstance().mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_REMOTE_KEY) == TVBridge.getLastRemoteKey())
                                                    && (MainActivity.getInstance().mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV) == 0)) {
                                                if (floating_mChannelIndex != i) {
                                                    statusOfNeedToChange = NEED_TO_CHANGE_CHANNEL_CHANGE_INDEX;
                                                }
                                                floating_mChannelIndex = i;
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (CommonStaticData.handoverMode == 2) {
                                    if (floating_mChannelIndex != CommonStaticData.handoverIndex && mCursor.getCount() > CommonStaticData.handoverIndex) {
                                        floating_mChannelIndex = CommonStaticData.handoverIndex;
                                        mCursor.moveToPosition(floating_mChannelIndex);
                                        TVlog.e(TAG, "handover: list reloaded & different index: channel index =  " + floating_mChannelIndex);
                                    } else {
                                        TVlog.e(TAG, "handover: list reloaded & same index: channel index =  " + floating_mChannelIndex);
                                    }
                                    statusOfNeedToChange = NEED_TO_CHANGE_CHANNEL_CHANGE_INDEX;
                                    CommonStaticData.handoverMode = 0;
                                }
                                CommonStaticData.isProcessingUpdate = false;
                            } else {
                                statusOfNeedToChange = NEED_TO_CHANGE_CHANNEL_FIRST_LOAD;
                                if (CommonStaticData.handoverMode == 1) {
                                    CommonStaticData.handoverMode = 0;
                                    TVlog.e(TAG, "handover: same list & same index: channel index =  " + floating_mChannelIndex);
                                }
                            }

                            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                                switch (CommonStaticData.receivemode) {
                                    case 0:     // 1seg
                                        if (MainActivity.getInstance().mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV)!=0) {
                                            for (int i=0; i < MainActivity.getInstance().mCursor.getCount(); i++) {
                                                MainActivity.getInstance().mCursor.moveToPosition(i);
                                                if (MainActivity.getInstance().mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV) == 0) {
                                                    floating_mChannelIndex = i;
                                                    break;
                                                }
                                            }
                                            if (floating_mChannelIndex == 0) {   // not found channel
                                                TVBridge.stop();
                                                //channelChangeEndViewFloating(false);
                                                //viewToastMSG(getResources().getString(R.string.ch_change_fail), false);
                                                CustomToast toast1 = new CustomToast(getApplicationContext());
                                                toast1.showToast(getApplicationContext(), getApplicationContext().getString(R.string.ch_change_fail), Toast.LENGTH_SHORT);
                                            }
                                        }
                                        break;
                                    case 1:     // fullseg
                                        if (MainActivity.getInstance().mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV)!=1) {
                                            for (int i=0; i < MainActivity.getInstance().mCursor.getCount(); i++) {
                                                MainActivity.getInstance().mCursor.moveToPosition(i);
                                                if (MainActivity.getInstance().mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV) == 1) {
                                                    floating_mChannelIndex = i;
                                                    break;
                                                }
                                            }
                                            if (floating_mChannelIndex == 0) {      // not found channel
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

                            MainActivity.getInstance().mCursor.moveToPosition(floating_mChannelIndex);

                            int freq = Integer.parseInt(MainActivity.getInstance().mCursor.getString(CommonStaticData.COLUMN_INDEX_SERVICE_FREQ));
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
                            String channelName = MainActivity.getInstance().mCursor.getString(CommonStaticData.COLUMN_INDEX_SERVICE_NAME);
                            TVlog.i(TAG, " >>>>> channelName = "+ channelName);
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

                            // live add
                            //updateCurEPGNameNDuration();

                            AudioFormat = MainActivity.getInstance().mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_AUDFORM);
                            VideoFormat = MainActivity.getInstance().mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_VIDFORM);
                            Scrambled = MainActivity.getInstance().mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_FREE);
                            mRemoteKey = MainActivity.getInstance().mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_REMOTE_KEY);
                            mSvcNumber = MainActivity.getInstance().mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_NUMBER);

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
                                        TVBridge.serviceID_start(0);
                                        postEvent(TVEVENT.E_AUTO_CHANGE_CHANNEL_TEST_FLOATING, 20 * 1000);
                                    }else {

                                        TVBridge.serviceID_start(floating_mChannelIndex);
                                    }
                                } else {
                                    floating_changeChannelView.setVisibility(View.INVISIBLE);
                                    floating_channelChangeBG.setVisibility(View.INVISIBLE);
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
                            floating_changeChannelView.setVisibility(View.INVISIBLE);
                            floating_noChannel.setVisibility(VISIBLE);

                            if (sv_floatingView != null) {
                                sv_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
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

                    if (sv_floatingView != null) {
                        sv_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
                    }

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
                    TVBridge.scan_floating();
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
                                doScan_floating.showProgress_floating(progress_floating, found_floating, freqKHz_floating+8000, doScan_floating.SHOW_PROGRESS_ON);
                            } else {
                                doScan_floating.showProgress_floating(progress_floating, found_floating, freqKHz_floating+6000, doScan_floating.SHOW_PROGRESS_ON);
                            }
                            CommonStaticData.scanCHnum = found_floating;
                            //MainActivity.getInstance().mChannelIndex = 0;
                            floating_mChannelIndex = 0;
                        } else if (progress_floating >= 97 && progress_floating < 100) {
                            doScan_floating.showProgress_floating(progress_floating, found_floating, freqKHz_floating, doScan_floating.SHOW_PROGRESS_ON);
                            CommonStaticData.scanCHnum = found_floating;
                            //MainActivity.getInstance().mChannelIndex = 0;
                            floating_mChannelIndex = 0;
                        } else {
                            doScan_floating.showProgress_floating(progress_floating, found_floating, freqKHz_floating, doScan_floating.SHOW_PROGRESS_OFF);
                            //CommonStaticData.scanCHnum = found;
                        }
                    }

                    break;

                case E_SCAN_CANCEL_FLOATING:
                    TVlog.i(TAG, "---------------- E_SCAN_CANCEL-------------------");
                    doScan_floating.showProgress_floating(0, 0, 473143, doScan_floating.SHOW_PROGRESS_OFF);
                    floating_ll_scan_progress.setVisibility(View.INVISIBLE);
                    TVBridge.scanStop();
                    if (CommonStaticData.handoverMode == 1) {
                        sendEvent(TVEVENT.E_SCAN_COMPLETED_FLOATING);
                    }
                    break;

                case E_FIRSTVIDEO_FLOATING:
                {
                    TVlog.i(TAG, " >>>>> E_FIRSTVIDEO_FLOATING");
                    removeEvent(TVEVENT.E_BADSIGNAL_CHECK_FLOATING);
                    removeEvent(E_SIGNAL_NOTI_MSG_FLOATING);
                    removeEvent(E_NOSIGNAL_SHOW_FLOATING);
                    removeEvent(E_CHANNEL_CHANGE_TIMEOVER_FLOATING);

                    CommonStaticData.badSignalFlag = false;
                    CommonStaticData.encryptFlag = false;
                    CommonStaticData.ageLimitFlag = false;

                    if (floating_changeChannelView.getVisibility() == VISIBLE) {
                        floating_changeChannelView.setVisibility(View.INVISIBLE);
                    }

                    if (sv_floatingView != null) {
                        sv_floatingView.setBackgroundColor(getResources().getColor(R.color.transparent));
                    }
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
                                if (sv_floatingView != null) {
                                    sv_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
                                }
                                //if (tv_scramble_title.getVisibility() == View.INVISIBLE) {
                                tv_scramble_title.setVisibility(View.INVISIBLE);
                                floating_noSignal.setVisibility(View.VISIBLE);
                                floating_programNotMsg.setVisibility(View.VISIBLE);
                                //}

                                if (floating_changeChannelView.getVisibility() == View.VISIBLE) {
                                    floating_changeChannelView.setVisibility(View.INVISIBLE);
                                }

                                signal_check_cnt = 0;
                                if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB) {
                                    if (CommonStaticData.autoSearch == 0 && CommonStaticData.scanCHnum > 0) {
                                        MainActivity.getInstance().postEvent(TVEVENT.E_SCAN_HANDOVER_START, HANDOVER_TIME);     // 3sec
                                    }
                                }
                            } else {
                                if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB) {
                                    if (CommonStaticData.autoSearch == 0 && CommonStaticData.scanCHnum > 0) {
                                        MainActivity.getInstance().postEvent(TVEVENT.E_SCAN_HANDOVER_START, HANDOVER_TIME);     // 3sec
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
                                if (sv_floatingView != null) {
                                    sv_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
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
                                    }
                                }
                            } else {
                                if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB) {
                                    if (CommonStaticData.autoSearch == 0 && CommonStaticData.scanCHnum > 0) {
                                        MainActivity.getInstance().postEvent(TVEVENT.E_SCAN_HANDOVER_START, HANDOVER_TIME);     // 3sec
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
                        }

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

                case E_RATING_MONITOR_FLOATING:
                    floating_curr_rate = FCI_TVi.GetCurProgramRating();  // curr_rate 2~6, PG_Rate 1~5
                    TVlog.i("justin", " ====> E_RATING_MONITOR_FLOATING :: floating_curr_rate " + floating_curr_rate + " , Set PG-rate" + CommonStaticData.PG_Rate);
                    if (buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL
                            || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_FILE
                            || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_ONESEG) {
                        if (CommonStaticData.ageLimitFlag) {
                            //TVBridge.stop();
                            if (sv_floatingView != null) {
                                sv_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
                            }
                            floating_age_limit_title.setVisibility(View.VISIBLE);
                            floating_age_limit_msg.setVisibility(View.VISIBLE);
                            floating_noSignal.setVisibility(View.INVISIBLE);
                            floating_programNotMsg.setVisibility(View.INVISIBLE);
                            tv_scramble_title.setVisibility(View.INVISIBLE);
                            tv_scramble_msg.setVisibility(View.INVISIBLE);
                            floating_changeChannelView.setVisibility(View.INVISIBLE);
                            FCI_TVi.setVolume(0.0f);
                        } else {
                            if (sv_floatingView != null) {
                                sv_floatingView.setBackgroundColor(getResources().getColor(R.color.transparent));
                            }
                            floating_age_limit_title.setVisibility(View.INVISIBLE);
                            floating_age_limit_msg.setVisibility(View.INVISIBLE);
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
                    if (MainActivity.getInstance().mCursor != null) {
                        TVlog.i(TAG, " >>>>> E_CHANNEL_NAME_UPDATE_FLOATING");
                        floating_mChannelIndex = CommonStaticData.lastCH;
                        MainActivity.getInstance().mCursor.moveToPosition(floating_mChannelIndex);
                        if (CommonStaticData.scanCHnum != 0) {

                            int freq = Integer.parseInt(MainActivity.getInstance().mCursor.getString(CommonStaticData.COLUMN_INDEX_SERVICE_FREQ));
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

                            String channelName = MainActivity.getInstance().mCursor.getString(CommonStaticData.COLUMN_INDEX_SERVICE_NAME);
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

                            //chat_currCH.setText(mCursor_floating.getString(CommonStaticData.COLUMN_INDEX_SERVICE_NAME));
                            AudioFormat = MainActivity.getInstance().mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_AUDFORM);
                            VideoFormat = MainActivity.getInstance().mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_VIDFORM);
                            Scrambled = MainActivity.getInstance().mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_FREE);
                            mRemoteKey = MainActivity.getInstance().mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_REMOTE_KEY);
                            mSvcNumber = MainActivity.getInstance().mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_NUMBER);

                            TVlog.i (TAG, " >>>>> Scrambled = "+String.valueOf(Scrambled));

                            if (Scrambled == 0) {
                                sendEvent(TVEVENT.E_BADSIGNAL_CHECK_FLOATING, 2, 0, null);
                            }
                        }
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
        if (sv_floatingView != null) {
            sv_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
        }

        instance = this;
        floatingVideoSurfaceHolder = sv_floatingView.getHolder();

        int h = sv_floatingView.getMeasuredHeight();
        int w = sv_floatingView.getMeasuredWidth();

        floatingVideoSurfaceHolder.setFixedSize(w, h);
        floatingVideoSurfaceHolder.addCallback(this);

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

        floating_age_limit_title = (me.grantland.widget.AutofitTextView) floating_view.findViewById(R.id.floating_age_limit_title);
        floating_age_limit_title.setVisibility(View.INVISIBLE);

        floating_age_limit_msg = (me.grantland.widget.AutofitTextView) floating_view.findViewById(R.id.floating_age_limit_msg);
        floating_age_limit_msg.setVisibility(View.INVISIBLE);

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
        if (CommonStaticData.badSignalFlag) {
            if (sv_floatingView != null) {
                sv_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
            }
            if (floating_noSignal.getVisibility() == View.INVISIBLE) {
                floating_noSignal.setVisibility(View.VISIBLE);
            }
            if (floating_programNotMsg.getVisibility() == View.INVISIBLE) {
                floating_programNotMsg.setVisibility(View.VISIBLE);
            }
        } else if (!CommonStaticData.badSignalFlag) {
            if (sv_floatingView != null) {
                sv_floatingView.setBackgroundColor(getResources().getColor(R.color.transparent));
            }
            if (floating_noSignal.getVisibility() == View.VISIBLE) {
                floating_noSignal.setVisibility(View.INVISIBLE);
            }
            if (floating_programNotMsg.getVisibility() == View.VISIBLE) {
                floating_programNotMsg.setVisibility(View.INVISIBLE);
            }
        }

        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
            if (CommonStaticData.encryptFlag) {
                if (sv_floatingView != null) {
                    sv_floatingView.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
                if (tv_scramble_msg.getVisibility() == View.VISIBLE) {
                    tv_scramble_title.setVisibility(View.INVISIBLE);
                    tv_scramble_msg.setVisibility(View.INVISIBLE);
                }
            }
        } else {
            if (CommonStaticData.encryptFlag) {
                if (sv_floatingView != null) {
                    sv_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
                }
                if (tv_scramble_msg.getVisibility() == View.INVISIBLE) {
                    tv_scramble_title.setVisibility(View.VISIBLE);
                    tv_scramble_msg.setVisibility(View.VISIBLE);
                }
            } else if (!CommonStaticData.encryptFlag) {
                if (sv_floatingView != null) {
                    sv_floatingView.setBackgroundColor(getResources().getColor(R.color.transparent));
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
                if (sv_floatingView != null) {
                    sv_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
                }
                floating_age_limit_title.setVisibility(View.VISIBLE);
                floating_age_limit_msg.setVisibility(View.VISIBLE);
            } else if (!CommonStaticData.ageLimitFlag) {
                if (sv_floatingView != null) {
                    sv_floatingView.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
                floating_age_limit_title.setVisibility(View.INVISIBLE);
                floating_age_limit_msg.setVisibility(View.INVISIBLE);
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

        floating_channelChangeBG = (ImageView) floating_view.findViewById(R.id.floating_imageView_bg);

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
        signalMoniter = new SignalMonitor(signalImage_floating);

        currChNo_floating = (TextView) floating_view.findViewById(R.id.tv_ch_no_floating);
        //currChNo_floating.setText(MainActivity.getInstance().currChNo.getText());
        tf_floating = Typeface.createFromAsset(getAssets(), "fonts/digital7.ttf");
        currChNo_floating.setTypeface(tf_floating);
        currChNo_floating.setTextSize(18);

        if (buildOption.VIEW_PHY_CH) {
            currChNo_floating.setVisibility(View.VISIBLE);
        } else {
            currChNo_floating.setVisibility(View.GONE);
        }

        currRemoteNo_floating = (TextView) floating_view.findViewById(R.id.tv_remote_no_floating);
        //currRemoteNo_floating.setText(MainActivity.getInstance().currRemoteNo.getText());
        currCH_floating = (TextView) floating_view.findViewById(R.id.servicename_floating);
        //currCH_floating.setText(MainActivity.getInstance().currCH.getText());
        //if (buildOption.GUI_STYLE == 1) {
        //currCH_floating.setSelected(true);
        //}

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

        channelLayout_floating = (LinearLayout) floating_view.findViewById(R.id.channelLayout_floating);
        channelLayout_floating.setVisibility(View.INVISIBLE);

        ch_up_floating = (ImageView) floating_view.findViewById(R.id.ch_up_floating);
        ch_up_floating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // justin DB
                CommonStaticData.passwordVerifyFlag = false;
                CommonStaticData.ageLimitFlag = false;
                channelChangeStartView(false);
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
                floating_age_limit_title.setVisibility(View.INVISIBLE);
                floating_age_limit_msg.setVisibility(View.INVISIBLE);
                TVBridge.AVStartPlus();
            }
        });

        ch_down_floating = (ImageView) floating_view.findViewById(R.id.ch_down_floating);
        ch_down_floating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonStaticData.passwordVerifyFlag = false;
                CommonStaticData.ageLimitFlag = false;
                channelChangeStartView(false);
                // live add
                if (tv_scramble_title.getVisibility() == View.VISIBLE) {
                    tv_scramble_title.setVisibility(View.INVISIBLE);
                }
                if (tv_scramble_msg.getVisibility() == View.VISIBLE) {
                    tv_scramble_msg.setVisibility(View.INVISIBLE);
                }
                floating_age_limit_title.setVisibility(View.INVISIBLE);
                floating_age_limit_msg.setVisibility(View.INVISIBLE);
                TVBridge.AVStartMinus();
            }
        });

        ll_controller_floating = (LinearLayout) floating_view.findViewById(ll_controller);
        ll_controller_floating.setVisibility(View.INVISIBLE);

        ll_scan = (LinearLayout) floating_view.findViewById(R.id.ll_scan);
        ll_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // live add
                if (sv_floatingView != null) {
                    sv_floatingView.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
                /*
                if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                    if (svSub_flolating!= null)
                    {
                        svSub_flolating.setBackgroundColor(getResources().getColor(R.color.transparent));
                    }
                }*/

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

        iv_scan = (ImageView) floating_view.findViewById(R.id.iv_scan);
        iv_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // live add
                if (sv_floatingView != null) {
                    sv_floatingView.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
                /*
                if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                    if (svSub_flolating!= null)
                    {
                        svSub_flolating.setBackgroundColor(getResources().getColor(R.color.transparent));
                    }
                }*/
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


        ll_max = (LinearLayout) floating_view.findViewById(R.id.ll_max);
        ll_max.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CommonStaticData.returnMainFromFloating = true;
                MainActivity.isMainActivity = true;
                isFloating = false;
                ChatMainActivity.isChat = false;
                TVlog.i(TAG, " >>>>> CommonStaticData.returnMainFromFloating = "+CommonStaticData.returnMainFromFloating);

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
                //
                editor.commit();

                AudioManager am = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                am.abandonAudioFocus(afChangeListener);



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

        ll_close = (LinearLayout) floating_view.findViewById(R.id.ll_close);
        ll_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (floating_view != null) {
                    mWindowManager.removeView(floating_view);
                    mWindowManager = null;
                }
                stopSelf();
                exit(0);
            }
        });

        iv_max = (ImageView) floating_view.findViewById(R.id.iv_max);
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
                //
                editor.commit();

                AudioManager am = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                am.abandonAudioFocus(afChangeListener);




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

        if (iv_close != null) {
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

        try {
            if (floating_view != null) {
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
        floating_changeChannelView.setVisibility(View.VISIBLE);  //live add
        } else {
            if (currChNo_floating != null && currCH_floating != null) {
                currChNo_floating.setText("- -ch");
                currRemoteNo_floating.setText("- - -");
                currCH_floating.setText(R.string.no_channel_title);
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
            floating_age_limit_title.setVisibility(View.INVISIBLE);
            floating_age_limit_msg.setVisibility(View.INVISIBLE);
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
                floating_age_limit_title.setVisibility(View.VISIBLE);
                floating_age_limit_msg.setVisibility(View.VISIBLE);
            } else {
                floating_changeChannelView.setVisibility(View.INVISIBLE);
                floating_age_limit_title.setVisibility(View.INVISIBLE);
                floating_age_limit_msg.setVisibility(View.INVISIBLE);
            }
            floating_noSignal.setVisibility(View.INVISIBLE);
            floating_programNotMsg.setVisibility(View.INVISIBLE);
            tv_scramble_title.setVisibility(View.INVISIBLE);
            tv_scramble_msg.setVisibility(View.INVISIBLE);
        }
        /*
        if (!CommonStaticData.badSignalFlag) {
            notifyFirstVideoFloating();
        }*/

        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
            postEvent(TVEVENT.E_SIGNAL_MONITER_FLOATING, SIGNAL_MONITER_TIME_USB);
        } else {
            postEvent(TVEVENT.E_SIGNAL_MONITER_FLOATING, SIGNAL_MONITER_TIME);
        }

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


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        TVlog.i(TAG, " ===== FloatingWindow surfaceCreated =====");

        MainActivity.isMainActivity = false;
        isFloating = true;
        ChatMainActivity.isChat = false;

        MainActivity.getInstance().onStart_TV();

        if (sv_floatingView != null) {
            FCI_TVi.setSuface(holder.getSurface());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        TVlog.i(TAG, " ===== FloatingWindow surfaceChanged =====");
        if (frameWidthFloating ==0 || frameHeightFloating ==0) {

            frameWidthFloating =width;
            frameHeightFloating=height;
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
            doScan_floating.showProgress_floating(0, 0, 0, ScanProcess_floating.SHOW_PROGRESS_CLEAR);
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
        //MainActivity.getInstance().sendEvent(TVEVENT.E_FIRSTVIDEO);
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
                //    TVBridge.serviceID_start(TVBridge.getCurrentChannel());

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

    public  void sendEvent(TVEVENT _Event, int _arg1, int _arg2, Object _obj) {
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

    private void channelChangeStartView(boolean _cas)
    {
        if (ll_floatingAutoSearch.getVisibility() == VISIBLE) {
            ll_floatingAutoSearch.setVisibility(View.INVISIBLE);
        }
        if (floating_noChannel.getVisibility() == VISIBLE) {
            floating_noChannel.setVisibility(View.INVISIBLE);
        }
        floating_changeChannelView.setVisibility(View.VISIBLE);
        floating_channelChangeBG.setVisibility(View.VISIBLE);
        if (floating_progressingChange != null) {
        floating_progressingChange.setVisibility(View.VISIBLE);
        }
        if (_cas == false) {
            floating_loadingChannel.setVisibility(View.VISIBLE);
        } else {   // call from playback
            floating_loadingChannel.setVisibility(GONE);
        }
    }

    private void channelChangeEndView(boolean _keepBG)
    {
        if (_keepBG ==false)
        {
            floating_channelChangeBG.setVisibility(View.INVISIBLE);
        }
        floating_changeChannelView.setVisibility(View.INVISIBLE);
    }

    public void envSet_JP_floating() {
        if (mFont_floating != null && currCH_floating != null) {
            mFont_floating = Typeface.createFromAsset(getAssets(), "wlcmaru2004emoji.ttf");
            currCH_floating.setTypeface(mFont_floating);
        }
    }

    public void envSet_Normal() {
        if (mFont_floating != null && currCH_floating != null) {
            mFont_floating = Typeface.DEFAULT;
            currCH_floating.setTypeface(mFont_floating);
        }
    }

    public void scanNotify_floating(int idx, String desc, byte type, byte vFormat, byte aFormat, byte iFree, int remoteKey, int svcNum, int freqKHz, byte bLast) {
        ContentValues values = new ContentValues();
        if (bLast==2) {
            if (MainActivity.getInstance().mCursor != null) {
                int cursorCount = MainActivity.getInstance().mCursor.getCount();
                if (cursorCount > 0 && cursorCount > MainActivity.getInstance().mCursor.getPosition()) {
                    TVBridge.setLastRemoteKey(MainActivity.getInstance().mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_REMOTE_KEY));
                    TVBridge.setLastSvcID(MainActivity.getInstance().mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_NUMBER));
                    TVBridge.setLastListCount(cursorCount);
                    TVBridge.setLastFreq(MainActivity.getInstance().mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_FREQ));
                    CommonStaticData.isProcessingUpdate = true;
                } else {
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
