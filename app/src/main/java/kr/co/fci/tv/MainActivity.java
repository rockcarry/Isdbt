package kr.co.fci.tv;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.storage.StorageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.format.Time;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.fci.tv.FCI_TV;
import com.lme.dtv.lmedtvsdk;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import kr.co.fci.tv.activity.AboutActivity;
import kr.co.fci.tv.activity.OpenActivity;
import kr.co.fci.tv.channelList.Channel;
import kr.co.fci.tv.channelList.ChannelListAdapter;
import kr.co.fci.tv.channelList.ChannelMainActivity;
import kr.co.fci.tv.chat.ChatMainActivity;
import kr.co.fci.tv.epgInfo.EPGActivity;
import kr.co.fci.tv.gesture.VerticalSeekBar;
import kr.co.fci.tv.recording.PlayBackActivity;
import kr.co.fci.tv.recording.RecordedFileListActivity;
import kr.co.fci.tv.recording.thumbNailUpdate;
import kr.co.fci.tv.saves.CommonStaticData;
import kr.co.fci.tv.saves.SharedPreference;
import kr.co.fci.tv.saves.TVProgram;
import kr.co.fci.tv.setting.DebugMode;
import kr.co.fci.tv.setting.InputDialog;
import kr.co.fci.tv.setting.SettingActivity;
import kr.co.fci.tv.tvSolution.AudioOut;
import kr.co.fci.tv.tvSolution.CaptionDirectView;
import kr.co.fci.tv.tvSolution.FCI_TVi;
import kr.co.fci.tv.tvSolution.ScanProcess;
import kr.co.fci.tv.tvSolution.SignalMonitor;
import kr.co.fci.tv.tvSolution.TVBridge;
import kr.co.fci.tv.tvSolution.buildInformation;
import kr.co.fci.tv.util.CustomToast;
import kr.co.fci.tv.util.TVlog;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Environment.getExternalStorageDirectory;
import static kr.co.fci.tv.TVEVENT.E_CAPTION_CLEAR_NOTIFY;
import static kr.co.fci.tv.TVEVENT.E_EWS_RECEIVED;
import static kr.co.fci.tv.TVEVENT.E_RATING_MONITOR;
import static kr.co.fci.tv.TVEVENT.E_SCAN_MONITOR;
import static kr.co.fci.tv.TVEVENT.E_SCAN_START;
import static kr.co.fci.tv.TVEVENT.E_SIGNAL_NOTI_MSG;

public class MainActivity extends Activity implements SurfaceHolder.Callback {

    private static String TAG = "FCITvApp ";
    private static String TAG_Debug = "Debugging ";

    public static int lastIndex = -1;

    public static LinearLayout ll_audioOnlyChannel;
    public static LinearLayout ll_black;

    int svcmodeswitch_selected = 2;
    String[] arr_svcmodeswitch_jp;

    /*
    public static boolean badSignalFlag = false;
    public static boolean encryptFlag = false;
    */

    public static int pxWidth;
    public static int pxHeight;

    private final static int BUTTON_CLICK_TIME = 100;
    private final static int CONTROLLER_HIDE_TIME = 7000;
    private final static int SIGNAL_MONITER_TIME = 1000;  // live change from 1000 to 2000
    private final static int SIGNAL_MONITER_TIME_USB = 2000;  // live add
    private final static int RECORDING_UPDATE_TIME = 200;
    private final static int CAPTION_CLEAR_TIME = 15000;
    private final static int SUPERIMPOSE_CLEAR_TIME = 15000;
    private final static int _TIME = 7000;
    private final static int SCALEMODE_NORMAL =0;
    private final static int SCALEMODE_16_9 = 1;
    private final static int SCALEMODE_4_3 = 2;
    private final static int DOUBLE_CLICK_TOLERANCE = 1000;   // 3000;

    private final static int GESTURE_HIDE_TIME = 3000;  // live add
    private final static int NO_SIGNAL_MSG_TIME = 5000;  // live add
    public final static int HANDOVER_COUNT = 10;  // live add
    private static boolean setDefaultChannel = false;  // live add

    static float density;
    static float densityDpi;
    public static String dpiName = "";
    public static String screenSize = "";

    // onoff flag
    private static boolean captureOn = true;
    private static boolean SurfaceRotationOn = false;
    public static boolean toggleTest = false;
    private static boolean isLoading = false;
    private boolean isRec = false;
    private boolean mIsRecStarted = false;
    private boolean captureFlag = false;
    static private boolean TVON = false;
    private boolean SignalStatFlag =false;
    private int signal_check_cnt = 0;
    public static boolean isChannelListViewOn = false;
    public static boolean dumpTVLog = false;

    // capture
    private static final String devicePath = "/sdcard/mobileTV/capture_";

    private  String capturePath;

    public static SurfaceView sv = null;
    public static SurfaceView svSub = null;
    public static SurfaceHolder videoSurfaceHolder = null;
    public static SurfaceHolder videoSurfaceHolderSub = null;
    private TextView subTitleView = null;
    private TextView superImposeView = null;
    private TextView debugScreen;

    public int frameWidth = 0;
    public int frameHeight =0;
    public int mChannelIndex = 0;

    private int screenWidth;
    private int screenHeight;
    private int currentVideoMode = CommonStaticData.currentScaleMode;

    private int AudioFormat= 0x00;        // recording 0x60(HEAAV), 0x40(AAC)
    private int VideoFormat= 0x00;        // recording 0x04(H.264)
    public  int Scrambled  = 1;           // 0: scramble ch, 1: free ch
    private int mRemoteKey = 0;
    private int mSvcNumber = 0;

    //Broadcast Event
    private SysBroadcastReceiver mSysReceiver = null;
    private SysBroadcastReceiver mScreenOff = null;
    // usbdongle[[
    private SysBroadcastReceiver mUsbReceiver = null;
    private lmedtvsdk mylme_sdk;
    // ]]usbdongle

    private boolean is_wired_headset;
    private boolean is_shown_ui;

    String  recordingFileName;
    String captureFileName;     // for myphone
    String record_channelName;
    // event
    private static final int CAPTION_NOTIFY_MSG = 2100;
    private long recStartTime;

    //public GestureDetector mGestureDetector = null;
    private GestureDetector mGestureDetector;
    public TextView currChNo;
    public TextView currRemoteNo;
    public TextView currCH;
    public TextView currProgram;
    public TextView currDuration;
    private LinearLayout ll_recTimeview;
    private TextView recTimeview;
    public static LinearLayout changeChannelView =null;
    public static ProgressBar progressingChange;
    public static TextView loadingChannel;
    private TextView loadingFilePlay;
    public static ImageView channelChangeBG = null;

    public static LinearLayout ll_mainAutoSearch;
    public static Button btn_return;

    public static LinearLayout ll_noSignal;    // for Myphone
    public static TextView programNotMsg;
    public static TextView noSignal;    // for Myphone

    public static LinearLayout ll_noChannel;

    public static LinearLayout ll_file_play_mode;    // for File play mode
    public static LinearLayout ll_file_play_mode_usb;    // for File play mode in USB dongle

    public static LinearLayout ll_age_limit;    // for parental rating
    TextView tv_age_limit_msg_10;
    TextView tv_age_limit_msg_12;
    TextView tv_age_limit_msg_14;
    TextView tv_age_limit_msg_16;
    TextView tv_age_limit_msg_18;

    public LinearLayout ll_scramble_msg;
    public TextView tv_scramble_title;
    public TextView tv_scramble_msg;

    private int[] channelChangeProcLocation =null;
    Toast mMainToast =null;

    //View decorView;
    RelativeLayout earphoneLayout;
    LinearLayout channelLayout;
    LinearLayout controllerLayout;
    LinearLayout status_bar;
    LinearLayout ll_ch_menu;
    LinearLayout ll_ch_info;

    public LinearLayout ll_chat;
    public ImageView iv_chat;

    public LinearLayout ll_multiWindow;
    public ImageView iv_multiWindow;

    LinearLayout ll_uiLock;
    ImageView iv_uiLock;
    LinearLayout ll_uiLocked;
    ImageView iv_uiLocked;
    public boolean isUiLocked = false;

    ImageButton earphone;
    ImageButton speaker;
    ImageButton ch_up;

    ImageButton ch_CherryMenu;
    ImageButton ch_MyphoneMenu;
    ImageButton ch_down;
    ImageButton scanB;
    ImageButton listB;
    ImageButton leftB;
    ImageButton rightB;
    ImageButton epgB;
    ImageButton scaleB;
    ImageButton recB;
    ImageButton recfileB;
    ImageButton favB;
    ImageButton setB;
    ImageButton captureB;

//  Button receiveModeB;

    //prevent double click
    private long mLastClickTimeCHMenu = 0;
    private long mLastClickTimeScan = 0;
    private long mLastClickTimeList = 0;
    private long mLastClickTimeEPG = 0;
    private long mLastClickTimeScale = 0;
    private long mLastClickTimeRec = 0;
    private long mLastClickTimeRecF = 0;
    private long mLastClickTimeSet = 0;
    private long mLastClickTimeReturn = 0;
    private long mLastClickTimeChat = 0;
    private long mLastClickTimeFloating = 0;

    private AudioOut audioOut;
    private PopupWindow settingWindow;
    private ScanProcess doScan;
    private ActionBar actionBar;
    private SignalMonitor signalMoniter =null;

    // parent rate checking  justin 20170523
    //public Boolean screenbl_enable = false;
    //public Boolean password_verify = false;
    public int curr_rate;

    // EPG display by justin
    public static Cursor mCursor;
    public static Uri mUri;

    // ADD_GINGA_NCL[[
    private boolean mRunDemuxThread = true;
    //]]ADD_GINGA_NCL

    //JAPAN_CAPTION[[
    public Typeface mFont, tf;
    private FrameLayout mCaptionLayout = null;
    private FrameLayout mSuperimposeLayout = null;
    private CaptionDirectView mCaptionView;
    private CaptionDirectView mSuperimposeView;
    private final int M_TYPE_CAPTION_SUBTITLE = 0;
    private final int M_TYPE_CAPTION_SUPERIMPOSE = 1;
    //]]JAPAN_CAPTION
    /////////////////////////

    //////// Gesture for Volume, Brightness and Channel List
    private boolean mAudio = false;
    private boolean mBrightnessChanged = false;
    // private boolean mIsTouchFlag=false;
    private boolean mIsTouchCHList=false;
    private boolean mEnableChannellist = false;
    private int mSurfaceYDisplayRange;
    private float mTouchY, mTouchX, mVol;

    private float mLastMotionX = 0;
    private float mLastMotionY = 0;

    private boolean mIsFirstBrightnessGesture = true;
    private boolean mEnableBrightnessGesture = false;
    private boolean isLocked = false;

    private VerticalSeekBar mVerticalBrightProgress;
    private VerticalSeekBar mVerticalVolumeProgress;

    private RelativeLayout volumebarLayout;
    private RelativeLayout brightbarLayout;

    ImageView img_volumebar, img_volumebar_off;
    ////////////////////////////////////

    // justin add
    private boolean selectionLanduageSet = false; // caption and superimpose language set from floating

    public static final int DIALOG_SCANMODE = 0;

    static final int DIALOG_AREA = 15;

    static final int DIALOG_PREFECTURE_0 = 21;
    static final int DIALOG_PREFECTURE_1 = 22;
    static final int DIALOG_PREFECTURE_2 = 23;
    static final int DIALOG_PREFECTURE_3 = 24;
    static final int DIALOG_PREFECTURE_4 = 25;
    static final int DIALOG_PREFECTURE_5 = 26;
    static final int DIALOG_PREFECTURE_6 = 27;
    static final int DIALOG_PREFECTURE_7 = 28;

    static final int DIALOG_LOCALITY_00 = 31;
    static final int DIALOG_LOCALITY_01 = 32;
    static final int DIALOG_LOCALITY_02 = 33;
    static final int DIALOG_LOCALITY_03 = 34;
    static final int DIALOG_LOCALITY_04 = 35;
    static final int DIALOG_LOCALITY_05 = 36;
    static final int DIALOG_LOCALITY_06 = 37;

    static final int DIALOG_LOCALITY_10 = 38;
    static final int DIALOG_LOCALITY_11 = 39;
    static final int DIALOG_LOCALITY_12 = 40;
    static final int DIALOG_LOCALITY_13 = 41;
    static final int DIALOG_LOCALITY_14 = 42;
    static final int DIALOG_LOCALITY_15 = 43;
    static final int DIALOG_LOCALITY_16 = 44;

    static final int DIALOG_LOCALITY_20 = 45;
    static final int DIALOG_LOCALITY_21 = 46;
    static final int DIALOG_LOCALITY_22 = 47;
    static final int DIALOG_LOCALITY_23 = 48;
    static final int DIALOG_LOCALITY_24 = 49;
    static final int DIALOG_LOCALITY_25 = 50;

    static final int DIALOG_LOCALITY_30 = 51;
    static final int DIALOG_LOCALITY_31 = 52;
    static final int DIALOG_LOCALITY_32 = 53;
    static final int DIALOG_LOCALITY_33 = 54;

    static final int DIALOG_LOCALITY_40 = 55;
    static final int DIALOG_LOCALITY_41 = 56;
    static final int DIALOG_LOCALITY_42 = 57;
    static final int DIALOG_LOCALITY_43 = 58;
    static final int DIALOG_LOCALITY_44 = 59;
    static final int DIALOG_LOCALITY_45 = 60;

    static final int DIALOG_LOCALITY_50 = 61;
    static final int DIALOG_LOCALITY_51 = 62;
    static final int DIALOG_LOCALITY_52 = 63;
    static final int DIALOG_LOCALITY_53 = 64;
    static final int DIALOG_LOCALITY_54 = 65;

    static final int DIALOG_LOCALITY_60 = 66;
    static final int DIALOG_LOCALITY_61 = 67;
    static final int DIALOG_LOCALITY_62 = 68;
    static final int DIALOG_LOCALITY_63 = 69;

    static final int DIALOG_LOCALITY_70 = 70;
    static final int DIALOG_LOCALITY_71 = 71;
    static final int DIALOG_LOCALITY_72 = 72;
    static final int DIALOG_LOCALITY_73 = 73;
    static final int DIALOG_LOCALITY_74 = 74;
    static final int DIALOG_LOCALITY_75 = 75;
    static final int DIALOG_LOCALITY_76 = 76;
    static final int DIALOG_LOCALITY_77 = 77;

    static final int DIALOG_NOT_SUPPORT_RESOLUTION = 78;

    public static final int DIALOG_SCAN_RESTORE = 79;    // justin add

    int dialog_scanmode_selected;

    int dialog_area_selected;

    int dialog_prefecture0_selected;
    int dialog_prefecture1_selected;
    int dialog_prefecture2_selected;
    int dialog_prefecture3_selected;
    int dialog_prefecture4_selected;
    int dialog_prefecture5_selected;
    int dialog_prefecture6_selected;
    int dialog_prefecture7_selected;

    int dialog_locality00_selected;
    int dialog_locality01_selected;
    int dialog_locality02_selected;
    int dialog_locality03_selected;
    int dialog_locality04_selected;
    int dialog_locality05_selected;
    int dialog_locality06_selected;
    int dialog_locality10_selected;
    int dialog_locality11_selected;
    int dialog_locality12_selected;
    int dialog_locality13_selected;
    int dialog_locality14_selected;
    int dialog_locality15_selected;
    int dialog_locality16_selected;
    int dialog_locality20_selected;
    int dialog_locality21_selected;
    int dialog_locality22_selected;
    int dialog_locality23_selected;
    int dialog_locality24_selected;
    int dialog_locality25_selected;
    int dialog_locality30_selected;
    int dialog_locality31_selected;
    int dialog_locality32_selected;
    int dialog_locality33_selected;
    int dialog_locality40_selected;
    int dialog_locality41_selected;
    int dialog_locality42_selected;
    int dialog_locality43_selected;
    int dialog_locality44_selected;
    int dialog_locality45_selected;
    int dialog_locality50_selected;
    int dialog_locality51_selected;
    int dialog_locality52_selected;
    int dialog_locality53_selected;
    int dialog_locality54_selected;
    int dialog_locality60_selected;
    int dialog_locality61_selected;
    int dialog_locality62_selected;
    int dialog_locality63_selected;
    int dialog_locality70_selected;
    int dialog_locality71_selected;
    int dialog_locality72_selected;
    int dialog_locality73_selected;
    int dialog_locality74_selected;
    int dialog_locality75_selected;
    int dialog_locality76_selected;
    int dialog_locality77_selected;

    ////////////////////////////////////
    public int isdbMode = 0;
    public String strISDBMode = "";
    ////////////////////////////////////

    public static String cardStr = "";
    public static int is_inserted_card = 2;
    TextView bcas_card_insert_msg;
    ///////////////////////////////////

    /////// justin test
    private View decorView;
    private int uiOptions;
    ////////

    static public boolean isMainActivity = false;
    static public boolean isPlayBackActivity = false;
    static public boolean isNoChannel = false;
    static public boolean floatingFromMain = false;

    MaterialDialog bb_fail_dialog;
    static public boolean isBBFail = false;

    MaterialDialog needNetworkConnectDialog;

    // boolean isWarmReset = false;
    public static boolean withoutUSB = false;

    int is_inserted_card_false_count = 0;
    private Runnable mRunnable;

    public static MainActivity instance;
    public static MainActivity getInstance()
    {
        return instance;
    }

    public static Cursor getCursor( ){
        return mCursor;
    }

    public static void setCursor(Cursor _cursor){
        if (_cursor != null) {
            mCursor = _cursor;
        }
    }

    CustomToast customToast = null;

    public RelativeLayout rl_ChType;
    ImageView iv_ChType;
    ImageView iv_ChFree;

    public Handler TVUI_Handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            TVEVENT event = TVEVENT.values()[msg.what];
            if (TVON == false)
            {
                TVlog.i(TAG, "---------------- TV OFF -------------------");
                return;
            }
            switch (event) {
                case E_CAPTION_NOTIFY: {
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
                                        if (mCaptionView != null) {
                                            mCaptionView.setText(caption_info);
                                            mCaptionView.invalidate();
                                        }
                                    }
                                    //]]JAPAN_CAPTION
                                    else {
                                        subTitleView.setText(Html.fromHtml(caption_info));
                                    }

                                    removeEvent(TVEVENT.E_CAPTION_CLEAR_NOTIFY);
                                    postEvent(TVEVENT.E_CAPTION_CLEAR_NOTIFY, CAPTION_CLEAR_TIME);
                                } else {
                                    //JAPAN_CAPTION[[
                                    if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                                        if (mCaptionView != null) {
                                            mCaptionView.setText("");
                                            mCaptionView.invalidate();
                                        }
                                    }
                                    //]]JAPAN_CAPTION
                                    else {
                                        subTitleView.setText(Html.fromHtml(""));
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
                case E_CAPTION_CLEAR_NOTIFY: {
                    //JAPAN_CAPTION[[
                    if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                        if (mCaptionView != null) {
                            mCaptionView.setText("");
                            mCaptionView.invalidate();
                        }
                    }
                    //]]JAPAN_CAPTION
                    else {
                        subTitleView.setText(Html.fromHtml(""));
                    }
                }
                break;

                case E_SUPERIMPOSE_NOTIFY: {
                    try {
                        Bundle newSuperimpose = (Bundle) msg.obj;
                        if (newSuperimpose != null) {
                            String superimpose_info = newSuperimpose.getString("superimpose_info");

                            if (superimpose_info.length() > 0) {
                                //JAPAN_CAPTION[[
                                if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                                    if (mSuperimposeView != null) {
                                        mSuperimposeView.setText(superimpose_info);
                                        mSuperimposeView.invalidate();
                                    }
                                }
                                //]]JAPAN_CAPTION
                                else {
                                    // live modify
                                    if (CommonStaticData.superimposeSwitch == true) {
                                        superImposeView.setVisibility(View.VISIBLE);
                                    } else {
                                        superImposeView.setVisibility(View.INVISIBLE);
                                    }
                                    //
                                    superImposeView.setText(Html.fromHtml(superimpose_info));
                                }

                                removeEvent(TVEVENT.E_SUPERIMPOSE_CLEAR_NOTIFY);
                                postEvent(TVEVENT.E_SUPERIMPOSE_CLEAR_NOTIFY, SUPERIMPOSE_CLEAR_TIME);
                            } else {
                                //JAPAN_CAPTION[[
                                if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                                    if (mSuperimposeView != null) {
                                        mSuperimposeView.setText("");
                                        mSuperimposeView.invalidate();
                                    }
                                }
                                //]]JAPAN_CAPTION
                                else {
                                    // live modify
                                    if (CommonStaticData.superimposeSwitch == true) {
                                        superImposeView.setVisibility(View.VISIBLE);
                                    } else {
                                        superImposeView.setVisibility(View.INVISIBLE);
                                    }
                                    //
                                    superImposeView.setText(Html.fromHtml(""));
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

                case E_SUPERIMPOSE_CLEAR_NOTIFY: {
                    //JAPAN_CAPTION[[
                    if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                        if (mSuperimposeView != null) {
                            mSuperimposeView.setText("");
                            mSuperimposeView.invalidate();
                        }
                    }
                    //]]JAPAN_CAPTION
                    else {
                        superImposeView.setText(Html.fromHtml(""));
                    }
                }
                break;

                case E_CHANNEL_LIST_AV_STARTED: {
                    TVlog.i(TAG, "---------------- E_CHANNEL_LIST_AV_STARTED-------------------");

                    CommonStaticData.passwordVerifyFlag = false;
                    CommonStaticData.ageLimitFlag = false;

                    channelChangeStartView(false);
                }
                break;
                case E_STOP_NOTIFY: {
                    TVlog.i(TAG, ">>>>> E_STOP_NOTIFY");
                    if (sv != null && sv.isShown()) {
                        sv.setBackgroundColor(getResources().getColor(R.color.black));
                    }
                    if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                        if (svSub != null && svSub.isShown()) {
                            svSub.setBackgroundColor(getResources().getColor(R.color.black));
                        }
                    } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                            (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                    buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                    buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                        if (svSub != null && svSub.isShown()) {
                            svSub.setBackgroundColor(getResources().getColor(R.color.black));
                        }
                    }
                }
                break;
                case E_FIRSTVIDEO: {
                    TVlog.i(TAG, ">>>>> E_FIRSTVIDEO");
                    if (CommonStaticData.isSwitched == false) {
                        removeEvent(TVEVENT.E_BADSIGNAL_CHECK);
                        removeEvent(E_SIGNAL_NOTI_MSG);
                        removeEvent(TVEVENT.E_NOSIGNAL_SHOW);
                        removeEvent(TVEVENT.E_CHANNEL_CHANGE_TIMEOVER);

                        CommonStaticData.isBadSignalFlag = false;
                        CommonStaticData.badSignalFlag = false;
                        CommonStaticData.encryptFlag = false;
                        CommonStaticData.ageLimitFlag = false;
                    }
                    CommonStaticData.isSwitched = false;

                    if (sv != null && sv.isShown()) {
                        sv.setBackgroundColor(getResources().getColor(R.color.transparent));
                    }
                    if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                        if (svSub != null && svSub.isShown()) {
                            svSub.setBackgroundColor(getResources().getColor(R.color.transparent));
                        }
                    } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                            (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                    buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                    buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                        if (svSub != null && svSub.isShown()) {
                            svSub.setBackgroundColor(getResources().getColor(R.color.transparent));
                        }
                    }

                    if (CommonStaticData.isAudioChannel) {
                        if (ll_black != null) {
                            ll_black.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (ll_black != null) {
                            ll_black.setVisibility(View.INVISIBLE);
                        }
                    }

                    if (ll_mainAutoSearch.getVisibility() == View.VISIBLE) {
                        ll_mainAutoSearch.setVisibility(View.INVISIBLE);
                    }

                    if (FCI_TVi.initiatedSol) {
                        if (ll_noChannel.getVisibility() == View.VISIBLE) {
                            ll_noChannel.setVisibility(View.INVISIBLE);
                        }
                        if (ll_noSignal.getVisibility() == View.VISIBLE) {
                            ll_noSignal.setVisibility(View.INVISIBLE);
                        }
                        if (ll_scramble_msg.getVisibility() == View.VISIBLE) {
                            ll_scramble_msg.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                            if (ll_file_play_mode_usb.getVisibility() == View.VISIBLE) {
                                ll_file_play_mode_usb.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            if (ll_file_play_mode.getVisibility() == View.VISIBLE) {
                                ll_file_play_mode.setVisibility(View.INVISIBLE);
                            }
                        }
                    }

                    if (buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL
                            || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_ONESEG
                            || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_FILE) {
                        curr_rate = FCI_TVi.GetCurProgramRating();
                        if ((curr_rate >= (CommonStaticData.PG_Rate + 1) && (CommonStaticData.PG_Rate != 0))
                                && (CommonStaticData.passwordVerifyFlag == false)
                                && (CommonStaticData.ratingsetSwitch == true)) {
                            CommonStaticData.ageLimitFlag = true;
                        } else {
                            CommonStaticData.ageLimitFlag = false;
                        }
                        sendEvent(E_RATING_MONITOR);
                    }

                    // justin add
                    if (selectionLanduageSet) {
                        if (CommonStaticData.captionSelect != 0) {
                            FCI_TVi.SelectCaption(CommonStaticData.captionSelect);
                        }
                        if (CommonStaticData.superimposeSelect != 0) {
                            FCI_TVi.SelectSuperimpose(CommonStaticData.superimposeSelect);
                        }
                        selectionLanduageSet = false;
                    }

                    InputDialog.nosignalNotiClear();
                    SignalStatFlag = false;
                    CommonStaticData.tuneTimeOver = false;
                    channelChangeEndView(false);

                    FCI_TVi.subSurfaceViewOnOff(FCI_TVi.getDualMode());

                }
                break;

                case E_FIRSTAUDIO: {
                    TVlog.i(TAG, " === E_FIRSTAUDIO ===");
                    if (CommonStaticData.isSwitched == false) {
                        removeEvent(TVEVENT.E_BADSIGNAL_CHECK);
                        removeEvent(E_SIGNAL_NOTI_MSG);
                        removeEvent(TVEVENT.E_NOSIGNAL_SHOW);
                        removeEvent(TVEVENT.E_CHANNEL_CHANGE_TIMEOVER);

                        CommonStaticData.isBadSignalFlag = false;
                        CommonStaticData.badSignalFlag = false;
                        CommonStaticData.encryptFlag = false;
                        CommonStaticData.ageLimitFlag = false;
                    }
                    CommonStaticData.isSwitched = false;

                    if (sv != null && sv.isShown()) {
                        sv.setBackgroundColor(getResources().getColor(R.color.transparent));
                    }
                    if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                        if (svSub != null && svSub.isShown()) {
                            svSub.setBackgroundColor(getResources().getColor(R.color.transparent));
                        }
                    } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                            (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                    buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                    buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                        if (svSub != null && svSub.isShown()) {
                            svSub.setBackgroundColor(getResources().getColor(R.color.transparent));
                        }
                    }

                    if (CommonStaticData.isAudioChannel) {
                        if (ll_black != null) {
                            ll_black.setVisibility(View.VISIBLE);
                        }
                        if (ll_audioOnlyChannel != null) {
                            ll_audioOnlyChannel.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (ll_black != null) {
                            ll_black.setVisibility(View.INVISIBLE);
                        }
                        if (ll_audioOnlyChannel != null) {
                            ll_audioOnlyChannel.setVisibility(View.INVISIBLE);
                        }
                    }

                    if (ll_mainAutoSearch.getVisibility() == View.VISIBLE) {
                        ll_mainAutoSearch.setVisibility(View.INVISIBLE);
                    }

                    if (FCI_TVi.initiatedSol) {
                        if (ll_noChannel.getVisibility() == View.VISIBLE) {
                            ll_noChannel.setVisibility(View.INVISIBLE);
                        }
                        if (ll_noSignal.getVisibility() == View.VISIBLE) {
                            ll_noSignal.setVisibility(View.INVISIBLE);
                        }
                        if (ll_scramble_msg.getVisibility() == View.VISIBLE) {
                            ll_scramble_msg.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                            if (ll_file_play_mode_usb.getVisibility() == View.VISIBLE) {
                                ll_file_play_mode_usb.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            if (ll_file_play_mode.getVisibility() == View.VISIBLE) {
                                ll_file_play_mode.setVisibility(View.INVISIBLE);
                            }
                        }
                    }

                    if (buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL
                            || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_ONESEG
                            || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_FILE) {
                        curr_rate = FCI_TVi.GetCurProgramRating();
                        if ((curr_rate >= (CommonStaticData.PG_Rate + 1) && (CommonStaticData.PG_Rate != 0))
                                && (CommonStaticData.passwordVerifyFlag == false)
                                && (CommonStaticData.ratingsetSwitch == true)) {
                            CommonStaticData.ageLimitFlag = true;
                        } else {
                            CommonStaticData.ageLimitFlag = false;
                        }
                        sendEvent(E_RATING_MONITOR);
                    }


                    InputDialog.nosignalNotiClear();
                    SignalStatFlag = false;
                    CommonStaticData.tuneTimeOver = false;
                    channelChangeEndView(false);
                }
                break;

                case E_HIDE_CONTROLER:
                    hideController();
                    break;

                case E_SHOW_CONTROLER:
                    // mIsTouchFlag =true;
                    showController();
                    break;

                case E_HIDE_GESTURE:
                    volumebarLayout.setVisibility(View.INVISIBLE);
                    brightbarLayout.setVisibility(View.INVISIBLE);
                    mBrightnessChanged = false;
                    mAudio = false;
                    break;

                case E_SHOW_CHANNELLIST:
                    if (mIsTouchCHList == true) {
                        MainActivity.isMainActivity = true;
                        Intent intent_ch = new Intent(MainActivity.this, ChannelMainActivity.class);
                        startActivity(intent_ch);
                        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);  // live add
                        mIsTouchCHList = false;
                    }
                    break;


                case E_SCAN_PROCESS:
                    int progress = (int) msg.arg1;
                    int found = (int) msg.arg2;
                    int freqKHz = 0;
                    if (msg.obj != null) {
                        freqKHz = (int) msg.obj;
                    }
                    //   TVlog.e(TAG, "E_SCAN_PROCESS " + progress + " % " + found + " found");
                    if (progress < 97) {
                        if (buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA
                                || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_ONESEG
                                || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                            doScan.showProgress(progress, found, freqKHz + 8000, doScan.SHOW_PROGRESS_ON);
                        } else {
                            doScan.showProgress(progress, found, freqKHz + 6000, doScan.SHOW_PROGRESS_ON);
                        }
                        CommonStaticData.scanCHnum = found;
                        mChannelIndex = 0;
                    } else if (progress >= 97 && progress < 100) {
                        doScan.showProgress(progress, found, freqKHz, doScan.SHOW_PROGRESS_ON);
                        CommonStaticData.scanCHnum = found;
                        mChannelIndex = 0;
                    } else {
                        doScan.showProgress(progress, found, freqKHz, doScan.SHOW_PRORESS_OFF);
                        //CommonStaticData.scanCHnum = found;
                    }
                    break;

                case E_SCAN_CANCEL:
                    TVlog.i(TAG, "---------------- E_SCAN_CANCEL-------------------");
                    TVBridge.scanStop();
                    if (CommonStaticData.handoverMode == CommonStaticData.HANDOVER_MODE_ON_NORMAL) {
                        sendEvent(TVEVENT.E_SCAN_COMPLETED);
                    }
                    break;

                case E_SCAN_COMPLETED:
                    if (CommonStaticData.handoverMode > CommonStaticData.HANDOVER_MODE_OFF) {
                        if (CommonStaticData.handoverIndex != -1) {
                            mChannelIndex = CommonStaticData.handoverIndex;
                            TVlog.e(TAG, "handover mode = " + CommonStaticData.handoverMode + " , channel index =  " + mChannelIndex);
                        }
                        else {
                            CommonStaticData.handoverIndex = 0;
                        }
                    }
                    if (FCI_TVi.initiatedSol) {
                        channelChangeStartView(false);
                    }

                    TVlog.i(TAG, "---------------- E_SCAN_COMPLETED-------------------");

                    if (CommonStaticData.scanCHnum > 0) {

                        //postEvent(TVEVENT.E_SIGNAL_MONITER, SIGNAL_MONITER_TIME);  //live remove

                        isNoChannel = false;
                        ll_noChannel.setVisibility(View.INVISIBLE);

                        if (CommonStaticData.handoverMode == CommonStaticData.HANDOVER_MODE_OFF && CommonStaticData.scanningNow == true) {
                            int num_Fullseg = 0;
                            int num_1seg = 0;

                            for (int i = 0; i < CommonStaticData.scanCHnum; i++) {
                                int[] info = FCI_TVi.GetPairNSegInfoOfCHIndex(i);
                                int isFullseg = info[1];
                                if (isFullseg == 0) {  //1-seg
                                    num_1seg++;
                                } else {
                                    num_Fullseg++;
                                }
                            }
                            CustomToast toast = new CustomToast(getApplicationContext());
                            toast.showToast(getApplicationContext(), CommonStaticData.scanCHnum  + " " + getApplicationContext().getString(R.string.channel_found)
                                    +"\n"+getApplicationContext().getString(R.string.type_HD)+" : "+num_Fullseg
                                    +"\n"+getApplicationContext().getString(R.string.type_SD)+" : "+num_1seg, Toast.LENGTH_LONG);
                        }

                        CommonStaticData.scanningNow = false;

                        final int NEED_TO_CHANGE_CHANNEL_NO = 0;
                        final int NEED_TO_CHANGE_CHANNEL_FIRST_LOAD = 1;
                        final int NEED_TO_CHANGE_CHANNEL_CHANGE_INDEX = 2;
                        int statusOfNeedToChange = NEED_TO_CHANGE_CHANNEL_NO;

                        if (mCursor != null && mCursor.isClosed() == false) {
                            mCursor.close();
                            mCursor = null;
                            TVlog.e(TAG, "cursor closed!!! ????????????? !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                        }
                        mCursor = getContentResolver().query(mUri, CommonStaticData.PROJECTION, TVProgram.Programs.TYPE + "=?", CommonStaticData.selectionArgsTV, null);
                        if (mCursor.getCount() > 0 && (mCursor.getPosition() < mCursor.getCount())) {
                            if (mChannelIndex >= mCursor.getCount()) {
                                mChannelIndex = 0;
                            }
                            mCursor.moveToPosition(mChannelIndex);
                            mRemoteKey = mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_REMOTE_KEY);
                            mSvcNumber = mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_NUMBER);
                            if (CommonStaticData.isProcessingUpdate) {
                                if ((mRemoteKey != TVBridge.getLastRemoteKey()) || (mSvcNumber != TVBridge.getLastSvcID())) {
                                    int cursorCount = mCursor.getCount();
                                    if (cursorCount >= TVBridge.getLastListCount()) { //service is increased or contents are changed.
                                        for (int i = 0; i < cursorCount; i++) {
                                            mCursor.moveToPosition(i);
                                            if ((mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_REMOTE_KEY) == TVBridge.getLastRemoteKey())
                                                    && (mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_NUMBER) == TVBridge.getLastSvcID())) {
                                                if (mChannelIndex != i) {
                                                    statusOfNeedToChange = NEED_TO_CHANGE_CHANNEL_CHANGE_INDEX;
                                                }
                                                mChannelIndex = i;
                                                break;
                                            }
                                        }
                                    }
                                    else { //service is decreased.
                                        for (int i = 0; i < cursorCount; i++) {
                                            mCursor.moveToPosition(i);
                                            if ((mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_REMOTE_KEY) == TVBridge.getLastRemoteKey())
                                                    && (mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV) == 0)) {
                                                if (mChannelIndex != i) {
                                                    statusOfNeedToChange = NEED_TO_CHANGE_CHANNEL_CHANGE_INDEX;
                                                }
                                                mChannelIndex = i;
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (CommonStaticData.handoverMode == CommonStaticData.HANDOVER_MODE_ON_UPDATE_LIST) {
                                    if (mChannelIndex != CommonStaticData.handoverIndex && mCursor.getCount() > CommonStaticData.handoverIndex) {
                                        mChannelIndex = CommonStaticData.handoverIndex;
                                        mCursor.moveToPosition(mChannelIndex);
                                        TVlog.e(TAG, "handover: list reloaded & different index: channel index =  " + mChannelIndex);
                                    }
                                    else {
                                        TVlog.e(TAG, "handover: list reloaded & same index: channel index =  " + mChannelIndex);
                                    }
                                    statusOfNeedToChange = NEED_TO_CHANGE_CHANNEL_CHANGE_INDEX;
                                    CommonStaticData.handoverMode = CommonStaticData.HANDOVER_MODE_OFF;
                                }
                                CommonStaticData.isProcessingUpdate = false;
                            }
                            else {
                                statusOfNeedToChange = NEED_TO_CHANGE_CHANNEL_FIRST_LOAD;
                                if (CommonStaticData.handoverMode == CommonStaticData.HANDOVER_MODE_ON_NORMAL) {
                                    CommonStaticData.handoverMode = CommonStaticData.HANDOVER_MODE_OFF;
                                    TVlog.e(TAG, "handover: same list & same index: channel index =  " + mChannelIndex);
                                }
                            }

                            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                                switch (CommonStaticData.receivemode){
                                    case 0:     // 1seg
                                        if (mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV)!=0) {
                                            for (int i=0; i < mCursor.getCount(); i++) {
                                                mCursor.moveToPosition(i);
                                                if (mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV) == 0) {
                                                    mChannelIndex = i;
                                                    break;
                                                }
                                            }
                                            if (CommonStaticData.scanCHnum == 0){   // not found channel
                                                TVBridge.stop();
                                                channelChangeEndView(false);
                                                //viewToastMSG(getResources().getString(R.string.ch_change_fail), false);
                                                CustomToast toast = new CustomToast(getApplicationContext());
                                                toast.showToast(getApplicationContext(), getApplicationContext().getString(R.string.ch_change_fail), Toast.LENGTH_SHORT);
                                            }
                                        }
                                        break;
                                    case 1:     // fullseg
                                        if (mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV)!=1) {
                                            for (int i=0; i < mCursor.getCount(); i++) {
                                                mCursor.moveToPosition(i);
                                                if (mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV) == 1) {
                                                    mChannelIndex = i;
                                                    break;
                                                }
                                            }
                                            if (CommonStaticData.scanCHnum == 0) {   // not found channel
                                                TVBridge.stop();
                                                channelChangeEndView(false);
                                                //viewToastMSG(getResources().getString(R.string.ch_change_fail), false);
                                                CustomToast toast = new CustomToast(getApplicationContext());
                                                toast.showToast(getApplicationContext(), getApplicationContext().getString(R.string.ch_change_fail), Toast.LENGTH_SHORT);
                                            }
                                        }
                                        break;
                                    case 2:     // auto
                                    case 3:     // off
                                        break;

                                }
                            }

                            TVlog.i(TAG, " >>>>> CommonStaticData.returnMainFromChat = "+CommonStaticData.returnMainFromChat);
                            TVlog.i(TAG, " >>>>> CommonStaticData.returnMainFromFloating = "+CommonStaticData.returnMainFromFloating);

                            if (CommonStaticData.returnMainFromChat) {
                                mChannelIndex = CommonStaticData.lastCH;
                                CommonStaticData.returnMainFromChat = false;
                            } else if (CommonStaticData.returnMainFromFloating) {
                                mChannelIndex = CommonStaticData.lastCH;
                                CommonStaticData.returnMainFromFloating = false;
                                selectionLanduageSet = true; // justin add
                            } else {
                                if (buildOption.CUSTOMER.contains("Myphone")) {
                                    if (setDefaultChannel) {
                                        for (int i = 0; i < mCursor.getCount(); i++) {
                                            mCursor.moveToPosition(i);
                                            String channelName = mCursor.getString(CommonStaticData.COLUMN_INDEX_SERVICE_NAME);
                                            if (channelName.contains("PTV")) {  // default channel
                                                mChannelIndex = i;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }

                            mCursor.moveToPosition(mChannelIndex);

                            int freq = Integer.parseInt(mCursor.getString(CommonStaticData.COLUMN_INDEX_SERVICE_FREQ));
                            TVlog.i(TAG, " >>>>> current freq = " + freq);

                            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                                int channelNo = 13 + (int)((freq-473143)/6000);
                                currChNo.setText(channelNo+"ch");
                            } else {
                                if (buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_ONESEG
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                                    // for Sri Lanka
                                    int channelNo = 13 + (int)((freq-474000)/8000);
                                    currChNo.setText(channelNo+"ch");
                                } else {
                                    int channelNo = 14 + (int)((freq-473143)/6000);
                                    currChNo.setText(channelNo+"ch");
                                }
                            }

                            //String channelName = channel.getName();
                            String channelName = mCursor.getString(CommonStaticData.COLUMN_INDEX_SERVICE_NAME);
                            TVlog.i(TAG, " >>>>> channelName = "+ channelName);
                            TVlog.i(TAG, " >>> CommonStaticData.lastCH = "+CommonStaticData.lastCH);
                            String[] split_channelName = channelName.split(" ");

                            // live modify 20170104
                            currRemoteNo.setText(split_channelName[0]);
                            String str = "";
                            for (int i = 1; i < split_channelName.length; i++) {
                                str += split_channelName[i];
                                if (i < split_channelName.length - 1) {
                                    str += " ";
                                }
                            }
                            currCH.setText(str);
                            //
                            int type = (int) mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV);
                            int free = (int) mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_FREE);
                            if (type == 0) { // if 1seg
                                if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                                    iv_ChType.setBackgroundResource(R.drawable.jp_1seg);
                                    iv_ChFree.setVisibility(View.GONE);
                                } else {
                                    iv_ChType.setBackgroundResource(R.drawable.tv_icon_1seg);
                                    if (free == 0) {
                                        iv_ChFree.setVisibility(View.VISIBLE);
                                    } else {
                                        iv_ChFree.setVisibility(View.GONE);
                                    }
                                }
                            } else if (type == 1) { // if fullseg
                                if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                                    iv_ChType.setBackgroundResource(R.drawable.jp_fullseg);
                                    iv_ChFree.setVisibility(View.GONE);
                                } else {
                                    iv_ChType.setBackgroundResource(R.drawable.tv_icon_fullseg);
                                    if (free == 0) {
                                        iv_ChFree.setVisibility(View.VISIBLE);
                                    } else {
                                        iv_ChFree.setVisibility(View.GONE);
                                    }
                                }
                            }
                            rl_ChType.setVisibility(View.VISIBLE);

                            // live add
                            sendEvent(TVEVENT.E_UPDATE_EPG_NAME_AND_DURATION);

                            AudioFormat = mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_AUDFORM);
                            VideoFormat = mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_VIDFORM);
                            Scrambled = mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_FREE);
                            mRemoteKey = mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_REMOTE_KEY);
                            mSvcNumber = mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_NUMBER);

                            removeEvent(E_SCAN_MONITOR);

                            TVlog.i (TAG, " >>>>> Scrambled1 = "+String.valueOf(Scrambled));

                            if (Scrambled == 0) {
                                sendEvent(TVEVENT.E_BADSIGNAL_CHECK, 2, 0, null);
                            }

                            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                            boolean isScreenOn;
                            if (SDK_INT <= 19) {
                                isScreenOn = pm.isScreenOn();
                            } else {
                                isScreenOn = pm.isInteractive();
                            }
                            if (isScreenOn) {
                                if (statusOfNeedToChange == NEED_TO_CHANGE_CHANNEL_FIRST_LOAD || statusOfNeedToChange == NEED_TO_CHANGE_CHANNEL_CHANGE_INDEX) {
                                    TVlog.i(TAG, " scan completed " + statusOfNeedToChange);

                                    // checking TS playback running...

                                    if (isCheckingPlayback())
                                    {
                                        TVlog.i(TAG, " playback running  ");
                                        break;
                                    }
                                    if (buildOption.LOG_CAPTURE_MODE==3)
                                    {
                                        TVBridge.dualAV_start(0, true);
                                        postEvent(TVEVENT.E_AUTO_CHANGE_CHANNEL_TEST, 20 * 1000);
                                    } else {
                                        int[] info = FCI_TVi.GetPairNSegInfoOfCHIndex(mChannelIndex);
                                        int isAudioOnly = info[5];
                                        if (isAudioOnly == 1) {
                                            CommonStaticData.isAudioChannel = true;
                                            channelChangeEndView(false);
                                            if (ll_black != null) {
                                                ll_black.setVisibility(View.VISIBLE);
                                            }
                                            if (ll_audioOnlyChannel != null) {
                                                ll_audioOnlyChannel.setVisibility(View.VISIBLE);
                                            }
                                        } else {
                                            CommonStaticData.isAudioChannel = false;
                                            if (ll_black != null) {
                                                ll_black.setVisibility(View.INVISIBLE);
                                            }
                                            if (ll_audioOnlyChannel != null) {
                                                ll_audioOnlyChannel.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                        TVBridge.dualAV_start(mChannelIndex, true);
                                    }
                                } else {
                                    channelChangeEndView(false);
                                }
                            } else {
                                TVlog.i(TAG, " =====  screen off =========");
                                SolutionStop();
                            }
                        } else {
                            postEvent(TVEVENT.E_CHLIST_UPDATE, 0);
                        }
                        ll_noSignal.setVisibility(View.INVISIBLE);
                    } else {
                        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                        boolean isScreenOn;
                        if (SDK_INT <= 19) {
                            isScreenOn = pm.isScreenOn();
                        } else {
                            isScreenOn = pm.isInteractive();
                        }

                        if (isScreenOn)
                        {
                            TVlog.i(TAG, " =====  no scan =========");
                            if (currChNo != null && currCH != null&& currRemoteNo != null ) {
                                currChNo.setText("- -ch");
                                currRemoteNo.setText("- - -");
                                currCH.setText(R.string.no_channel_title);
                            }

                            if (rl_ChType != null) {
                                rl_ChType.setVisibility(View.GONE);
                            }

                            if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 )  {        // bug update
                                currProgram.setText("- - -");
                                currDuration.setText("--:--~--:--");
                            }

                            changeChannelView.setVisibility(View.INVISIBLE);
                            ll_noChannel.setVisibility(View.VISIBLE);
                            isNoChannel = true;

                            if (FCI_TVi.initiatedSol) {
                                //if (ll_scramble_msg.getVisibility() == View.INVISIBLE) {
                                // live add
                                CommonStaticData.badSignalFlag = false;
                                CommonStaticData.encryptFlag = false;
                                CommonStaticData.ageLimitFlag = false;
                                //
                                ll_scramble_msg.setVisibility(View.INVISIBLE);
                                //}
                            }

                            if (isCheckingPlayback())
                            {
                                TVlog.i(TAG, " playback running  ");
                                break;
                            }

                            if (buildOption.ADD_TS_CAPTURE != true) {
                                new InputDialog(instance, InputDialog.TYPE_TV_NOCHANNELLIST, null, null, null);
                            }
                        } else {
                            TVlog.i(TAG, " =====  no scan and screen off =========");
                            SolutionStop();
                        }
                    }
                    CommonStaticData.loadingNow = false;

                    if (buildOption.GUI_STYLE == 2 || buildOption.GUI_STYLE == 3) {
                        setDefaultChannel = true;  // live add
                    }
                    break;

                case E_SCAN_START:
                    TVlog.i(TAG, " >>>>> E_SCAN_START ");
                    recordingStop(true);
                    TVBridge.scanStop();  //live add
                    TVBridge.stop();
                    // channel index initialize
                    CommonStaticData.lastCH = 0;
                    CommonStaticData.scanCHnum = 0; // justin garbage service name during scan stop #120
                    CommonStaticData.captionSelect = 0;    // justin add
                    CommonStaticData.superimposeSelect = 0;
                    CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                    editor.putInt(CommonStaticData.lastChannelKey, CommonStaticData.lastCH);
                    editor.commit();
                    CommonStaticData.scanningNow = true;
                    removeEvent(E_SIGNAL_NOTI_MSG);
                    removeEvent(TVEVENT.E_BADSIGNAL_CHECK);
                    removeEvent(TVEVENT.E_CHANNEL_LIST_ENCRYPTED);
                    SignalMonitor.handover_counter = 0;

                    if (ll_mainAutoSearch.getVisibility() == View.VISIBLE) {
                        ll_mainAutoSearch.setVisibility(View.INVISIBLE);
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

                    Intent intent = getIntent();
                    mUri = intent.getData();
                    if (mUri == null) {
                        mUri = TVProgram.Programs.CONTENT_URI;
                        intent.setData(mUri);
                    }
                    getContentResolver().delete(mUri, null, null);  // justin DB
                    TVlog.i(TAG, " >>>>> press Scan  ");
                    doScan = new ScanProcess(instance);
                    TVBridge.scan();
                    break;

                case E_REGION_SCAN_START:
                    int[] regionCh = (int[]) msg.obj;
                    if (regionCh != null) {
                        TVlog.i(TAG, " >>>>> E_REGION_SCAN_START regionCh length = "+ regionCh.length);
                        recordingStop(true);
                        TVBridge.scanStop();  //live add
                        TVBridge.stop();
                        CommonStaticData.lastCH = 0;
                        CommonStaticData.scanCHnum = 0; // justin garbage service name during scan stop #120
                        CommonStaticData.captionSelect = 0;    // justin add
                        CommonStaticData.superimposeSelect = 0;
                        CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor1 = CommonStaticData.settings.edit();
                        editor1.putInt(CommonStaticData.lastChannelKey, CommonStaticData.lastCH);
                        editor1.commit();
                        CommonStaticData.scanningNow = true;
                        removeEvent(E_SIGNAL_NOTI_MSG);
                        removeEvent(TVEVENT.E_BADSIGNAL_CHECK);
                        removeEvent(TVEVENT.E_CHANNEL_LIST_ENCRYPTED);

                        SharedPreference sharedPreference2 = new SharedPreference();
                        if (sharedPreference2 != null) {
                            ArrayList<Channel> favorites = sharedPreference2.getFavorites(getApplicationContext());
                            if (favorites != null) {
                                for (int i = 0 ; i < favorites.size() ; i++) {
                                    sharedPreference2.removeFavorite(getApplicationContext(), favorites.get(i));
                                }
                            }
                        }

                        Intent intent2 = getIntent();
                        mUri = intent2.getData();
                        if (mUri == null) {
                            mUri = TVProgram.Programs.CONTENT_URI;
                            intent2.setData(mUri);
                        }
                        getContentResolver().delete(mUri, null, null);  // justin DB
                        TVlog.i(TAG, " >>>>> press Scan  ");
                        doScan = new ScanProcess(instance);
                        TVBridge.scan(regionCh);
                    }

                    break;

                case E_CHLIST_REMOVE:
                    TVlog.i(TAG, " >>>>> E_CHLIST_REMOVE ");
                    channelChangeBG.setVisibility(View.VISIBLE);
                    recordingStop(true);
                    TVBridge.removeChannelDB();
                    SetVideoScale(CommonStaticData.currentScaleMode);
                    FCI_TVi.setAudioMode(1); // justin add for default set to stereo main

                    SharedPreference sharedPreferenceRM = new SharedPreference();
                    if (sharedPreferenceRM != null) {
                        ArrayList<Channel> favorites = sharedPreferenceRM.getFavorites(getApplicationContext());
                        if (favorites != null) {
                            for (int i = 0 ; i < favorites.size() ; i++) {
                                sharedPreferenceRM.removeFavorite(getApplicationContext(), favorites.get(i));
                            }
                        }
                    }
                    getContentResolver().delete(mUri, null, null);  // justin DB
                    CommonStaticData.scanCHnum =0;
                    if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 )  {
                        currChNo.setText("- -ch");
                    } else {
                        currChNo.setText("CH - -");
                    }
                    currRemoteNo.setText("- - -");
                    if (rl_ChType != null) {
                        rl_ChType.setVisibility(View.GONE);
                    }
                    currCH.setText(R.string.no_channel_title);
                    currProgram.setText("- - -");
                    currDuration.setText("--:--~--:--");
                    break;

                case E_SCAN_MONITOR:
                    if (CommonStaticData.scanCHnum != 0) {
                        removeEvent(E_SCAN_MONITOR);
                    } else {
                        if (buildOption.ADD_TS_CAPTURE != true) {
                            new InputDialog(instance, InputDialog.TYPE_TV_NOCHANNELLIST, null, null, null);
                        }
                        postEvent(E_SCAN_MONITOR, CONTROLLER_HIDE_TIME * 2);
                    }
                    break;

                case E_RATING_MONITOR:
                    TVlog.e("main_justin", " E_RATING_MONITOR ====> CommonStaticData.ageLimitFlag " + CommonStaticData.ageLimitFlag);
                    curr_rate = FCI_TVi.GetCurProgramRating();
                    if (!CommonStaticData.passwordVerifyFlag
                            && CommonStaticData.ageLimitFlag
                            && curr_rate >= (CommonStaticData.PG_Rate+1)
                            && (CommonStaticData.PG_Rate!=0)) {
                        if (sv != null && sv.isShown()) {
                            sv.setBackgroundColor(getResources().getColor(R.color.black));
                        }

                        if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                            if (svSub != null && svSub.isShown()) {
                                svSub.setBackgroundColor(getResources().getColor(R.color.black));
                            }
                        } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                                (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                            if (svSub != null && svSub.isShown()) {
                                svSub.setBackgroundColor(getResources().getColor(R.color.black));
                            }
                        }

                        if (curr_rate == 2) {
                            tv_age_limit_msg_10.setVisibility(View.VISIBLE);
                            tv_age_limit_msg_12.setVisibility(View.INVISIBLE);
                            tv_age_limit_msg_14.setVisibility(View.INVISIBLE);
                            tv_age_limit_msg_16.setVisibility(View.INVISIBLE);
                            tv_age_limit_msg_18.setVisibility(View.INVISIBLE);
                        } else if (curr_rate == 3) {
                            tv_age_limit_msg_10.setVisibility(View.INVISIBLE);
                            tv_age_limit_msg_12.setVisibility(View.VISIBLE);
                            tv_age_limit_msg_14.setVisibility(View.INVISIBLE);
                            tv_age_limit_msg_16.setVisibility(View.INVISIBLE);
                            tv_age_limit_msg_18.setVisibility(View.INVISIBLE);
                        } else if (curr_rate == 4) {
                            tv_age_limit_msg_10.setVisibility(View.INVISIBLE);
                            tv_age_limit_msg_12.setVisibility(View.INVISIBLE);
                            tv_age_limit_msg_14.setVisibility(View.VISIBLE);
                            tv_age_limit_msg_16.setVisibility(View.INVISIBLE);
                            tv_age_limit_msg_18.setVisibility(View.INVISIBLE);
                        } else if (curr_rate == 5) {
                            tv_age_limit_msg_10.setVisibility(View.INVISIBLE);
                            tv_age_limit_msg_12.setVisibility(View.INVISIBLE);
                            tv_age_limit_msg_14.setVisibility(View.INVISIBLE);
                            tv_age_limit_msg_16.setVisibility(View.VISIBLE);
                            tv_age_limit_msg_18.setVisibility(View.INVISIBLE);
                        } else if (curr_rate == 6) {
                            tv_age_limit_msg_10.setVisibility(View.INVISIBLE);
                            tv_age_limit_msg_12.setVisibility(View.INVISIBLE);
                            tv_age_limit_msg_14.setVisibility(View.INVISIBLE);
                            tv_age_limit_msg_16.setVisibility(View.INVISIBLE);
                            tv_age_limit_msg_18.setVisibility(View.VISIBLE);
                        } else {
                            tv_age_limit_msg_10.setVisibility(View.INVISIBLE);
                            tv_age_limit_msg_12.setVisibility(View.INVISIBLE);
                            tv_age_limit_msg_14.setVisibility(View.INVISIBLE);
                            tv_age_limit_msg_16.setVisibility(View.INVISIBLE);
                            tv_age_limit_msg_18.setVisibility(View.INVISIBLE);
                        }

                        ll_age_limit.setVisibility(View.VISIBLE);

                        if (subTitleView != null) {
                            subTitleView.setVisibility(View.INVISIBLE);
                        }

                        ll_noSignal.setVisibility(View.INVISIBLE);
                        ll_scramble_msg.setVisibility(View.INVISIBLE);
                        changeChannelView.setVisibility(View.INVISIBLE);

                        TVlog.e("main_justin", " ====> screenbl_enabled stop !!!" );
                        FCI_TVi.setVolume(0.0f);
                    } else {
                        if (sv != null && sv.isShown()) {
                            sv.setBackgroundColor(getResources().getColor(R.color.transparent));
                        }

                        if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                            if (svSub != null && svSub.isShown()) {
                                svSub.setBackgroundColor(getResources().getColor(R.color.transparent));
                            }
                        } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                                (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                            if (svSub != null && svSub.isShown()) {
                                svSub.setBackgroundColor(getResources().getColor(R.color.transparent));
                            }
                        }
                        ll_age_limit.setVisibility(View.INVISIBLE);

                        if (CommonStaticData.captionSwitch) {
                            if (subTitleView != null) {
                                subTitleView.setVisibility(View.VISIBLE);
                            }
                        }
                        FCI_TVi.setVolume(1.0f);
                    }
                    break;

                case E_CHANNEL_CHANGE_FAIL:
                    channelChangeEndView(false);
                    CustomToast toast6 = new CustomToast(getApplicationContext());
                    toast6.showToast(getApplicationContext(), getApplicationContext().getString(R.string.ch_change_fail), Toast.LENGTH_SHORT);
                    break;

                case E_SIGNAL_MONITER:
                    if (signalMoniter != null)
                    {
                        int segType;

                        signalMoniter.getSignal();

                        if (buildOption.ADD_DEBUG_SCREEN) {
                            checkDebugInfo();
                        }

                        /*
                        if (buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL
                                || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_ONESEG
                                || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                                || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_FILE) {
                            // justin 20170526
                            curr_rate = FCI_TVi.GetCurProgramRating();  // curr_rate 2~6, PG_Rate 1~5
                            TVlog.e("justin", " ====> currRate " + curr_rate + " , Set PG-rate" + CommonStaticData.PG_Rate);
                            if ((curr_rate >= (CommonStaticData.PG_Rate+1) && (CommonStaticData.PG_Rate!=0))
                                    && (CommonStaticData.passwordVerifyFlag == false)
                                    && (CommonStaticData.ratingsetSwitch == true)) {
                                CommonStaticData.ageLimitFlag = true;
                            } else {
                                CommonStaticData.ageLimitFlag = false;
                            }
                            sendEvent(TVEVENT.E_RATING_MONITOR);
                        }*/

                        removeEvent(TVEVENT.E_SIGNAL_MONITER);  //live add
                        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                                || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB
                                || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                            postEvent(TVEVENT.E_SIGNAL_MONITER, SIGNAL_MONITER_TIME_USB);
                        } else {
                            postEvent(TVEVENT.E_SIGNAL_MONITER, SIGNAL_MONITER_TIME);
                        }
                    }
                    break;

                case E_BADSIGNAL_CHECK:
                    int stat = (int)msg.arg1;
                    if (isPlayBackActivity) {
                        break;
                    }
                    switch (stat){
                        case 1: // low buffer
                            if (SignalStatFlag==false) {
                            TVlog.i("live", " === E_BADSIGNAL_CHECK case 1 ===");
                                postEvent(E_SIGNAL_NOTI_MSG, NO_SIGNAL_MSG_TIME);     // 10sec
                                if (ll_mainAutoSearch.getVisibility() == View.VISIBLE) {
                                    ll_mainAutoSearch.setVisibility(View.INVISIBLE);
                                }
                                SignalStatFlag = true;
                                CommonStaticData.badSignalFlag = true;  // live add
                                CommonStaticData.encryptFlag = false;  // live add
                                CommonStaticData.ageLimitFlag = false;
                            }
                            break;
                        case 2: // scramble channel
                            //channelChangeEndView(true);  //live0919
                            TVlog.i("live", " === E_BADSIGNAL_CHECK case 2 ===");
                            if (ll_mainAutoSearch.getVisibility() == View.VISIBLE) {
                                ll_mainAutoSearch.setVisibility(View.INVISIBLE);
                            }
                            if (isChannelListViewOn) {
                                if (buildOption.FCI_SOLUTION_MODE !=buildOption.JAPAN && buildOption.FCI_SOLUTION_MODE !=buildOption.JAPAN_ONESEG
                                        && buildOption.FCI_SOLUTION_MODE != buildOption.JAPAN_USB
                                        && buildOption.FCI_SOLUTION_MODE != buildOption.JAPAN_FILE) {
                                    ChannelMainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_LIST_ENCRYPTED);
                                }
                            } else {
                                //new InputDialog(instance, InputDialog.TYPE_SCRAMBLE_NOTI, null, null, null);
                                CommonStaticData.encryptFlag = true;
                                CommonStaticData.badSignalFlag = false;  // live add
                                CommonStaticData.ageLimitFlag = false;
                                if (ll_noSignal.getVisibility() == View.VISIBLE) {
                                    ll_noSignal.setVisibility(View.INVISIBLE);
                                }
                                if (ll_audioOnlyChannel.getVisibility() == View.VISIBLE) {
                                    ll_audioOnlyChannel.setVisibility(View.INVISIBLE);
                                }
                                if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                                    //ll_scramble_msg.setVisibility(View.VISIBLE);
                                } else {
                                    channelChangeEndView(true);
                                    ll_scramble_msg.setVisibility(View.VISIBLE);
                                }
                            }
                            break;
                        case 3: //RF signal bad
                            TVlog.i("live", " === E_BADSIGNAL_CHECK case 3 ===");
                            CommonStaticData.badSignalFlag = true;
                            CommonStaticData.encryptFlag = false;
                            CommonStaticData.ageLimitFlag = false;
                            if (ll_mainAutoSearch.getVisibility() == View.VISIBLE) {
                                ll_mainAutoSearch.setVisibility(View.INVISIBLE);
                            }
                            if (CommonStaticData.scanningNow==false && CommonStaticData.scanCHnum > 0) {
                                if (sv != null && sv.isShown()) {
                                    sv.setBackgroundColor(getResources().getColor(R.color.black));
                                }
                                if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                                    if (svSub != null && svSub.isShown()) {
                                        svSub.setBackgroundColor(getResources().getColor(R.color.black));
                                    }
                                } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                                        (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                                buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                                buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                                    if (svSub != null && svSub.isShown()) {
                                        svSub.setBackgroundColor(getResources().getColor(R.color.black));
                                    }
                                }

                                ll_scramble_msg.setVisibility(View.INVISIBLE);
                                ll_noSignal.setVisibility(View.VISIBLE);
                                if (ll_audioOnlyChannel.getVisibility() == View.VISIBLE) {
                                    ll_audioOnlyChannel.setVisibility(View.INVISIBLE);
                                }

                                signal_check_cnt = 0;
                                if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG) {
                                    if (CommonStaticData.scanCHnum > 0) {
                                        CommonStaticData.isBadSignalFlag = true;
                                    }
                                }
                            } else {
                                removeEvent(TVEVENT.E_BADSIGNAL_CHECK);
                            }
                            break;
                        case 4: //program not available
                            TVlog.i("live", " === E_BADSIGNAL_CHECK case 4 ===");
                            CommonStaticData.badSignalFlag = true;
                            CommonStaticData.encryptFlag = false;
                            CommonStaticData.ageLimitFlag = false;
                            if (ll_mainAutoSearch.getVisibility() == View.VISIBLE) {
                                ll_mainAutoSearch.setVisibility(View.INVISIBLE);
                            }
                            if (CommonStaticData.scanningNow==false && CommonStaticData.scanCHnum > 0) {
                                channelChangeEndView(false);
                                /*
                                CustomToast toast7 = new CustomToast(getApplicationContext());
                                toast7.showToast(getApplicationContext(),
                                        getApplicationContext().getString(R.string.no_signal_msg)+"\n"+
                                                getApplicationContext().getString(R.string.program_not_available), Toast.LENGTH_SHORT);
                                */


                                if (sv != null && sv.isShown()) {
                                    sv.setBackgroundColor(getResources().getColor(R.color.black));
                                }
                                if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                                    if (svSub != null && svSub.isShown()) {
                                        svSub.setBackgroundColor(getResources().getColor(R.color.black));
                                    }
                                } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                                        (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                                buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                                buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                                    if (svSub != null && svSub.isShown())  {
                                        svSub.setBackgroundColor(getResources().getColor(R.color.black));
                                    }
                                }

                                ll_scramble_msg.setVisibility(View.INVISIBLE);
                                ll_noSignal.setVisibility(View.VISIBLE);
                                if (ll_audioOnlyChannel.getVisibility() == View.VISIBLE) {
                                    ll_audioOnlyChannel.setVisibility(View.INVISIBLE);
                                }
                                //}
                                if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG) {
                                    if (CommonStaticData.scanCHnum > 0) {
                                        CommonStaticData.isBadSignalFlag = true;
                                    }
                                }
                            }
                            else {
                                removeEvent(TVEVENT.E_BADSIGNAL_CHECK);
                            }
                            break;
                    }
                    break;

                case E_EWS_RECEIVED:
                {
                    int startEndFlag = (int) msg.arg1;
                    int signalLevel = (int) msg.arg2;
                    int[] areaCodes = (int[]) msg.obj;

                    if (isChannelListViewOn) {
                        ChannelMainActivity.getInstance().sendEvent(E_EWS_RECEIVED, msg.arg1, msg.arg2, msg.obj);
                    }
                    else {
                        new InputDialog(instance, InputDialog.TYPE_EWS_NOTIFY, (int) startEndFlag, (int) signalLevel, (int[]) areaCodes);
                    }
                }
                break;

                case E_BCAS_CARD_READY:
                {
                    if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                        is_inserted_card_false_count = 0;
                        is_inserted_card = 1;
                        TVlog.i(TAG, " >>>>> is_inserted_card = "+is_inserted_card);
                        bcas_card_insert_msg.setVisibility(View.INVISIBLE);
                        byte[] cardId = (byte[]) msg.obj;
                        if (cardId != null) {
                            cardStr = String.format("%c%c%c%c %c%c%c%c %c%c%c%c %c%c%c%c %c%c%c%c",
                                    cardId[0], cardId[1], cardId[2], cardId[3],
                                    cardId[4], cardId[5], cardId[6], cardId[7],
                                    cardId[8], cardId[9], cardId[10], cardId[11],
                                    cardId[12], cardId[13], cardId[14], cardId[15],
                                    cardId[16], cardId[17], cardId[18], cardId[19]);
                        }

                        TVlog.i(TAG, "BCAS card Id=" + cardStr);

                        if (SettingActivity.getInstance() != null) {
                            SettingActivity.getInstance().bcas_update();
                        }
                    }
                }
                break;

                case E_BCAS_CARD_REMOVED:
                {
                    if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                        is_inserted_card = 0;
                        is_inserted_card_false_count =  is_inserted_card_false_count + 1;
                        TVlog.i(TAG, " >>>>> is_inserted_card = " + is_inserted_card);
                        if (is_inserted_card_false_count > 1) {
                            if (ChannelListAdapter.getInstance() != null) {
                                final Channel channel = (Channel) ChannelListAdapter.getInstance().getItem(mChannelIndex);
                                if (channel != null) {
                                    int type = (int) channel.getType();
                                    if (type == 0) {  //1-seg
                                        bcas_card_insert_msg.setVisibility(View.INVISIBLE);
                                    } else {
                                        bcas_card_insert_msg.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }

                        if (SettingActivity.getInstance() != null) {
                            SettingActivity.getInstance().bcas_update();
                        }

                        if (CommonStaticData.receivemode == CommonStaticData.RECEIVE_MODE_AUTO && CommonStaticData.scanCHnum > 0 && CommonStaticData.scanningNow == false) {
                            MainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_SWITCHING, 0, 0, null);
                            //CommonStaticData.receivemode = 0;
                        }

                        //TVlog.i(TAG, "BCAS card removed <<<");
                    }
                }
                break;

                case E_SIGNAL_NOTI_MSG:
                    if (CommonStaticData.scanningNow==false) {
                        TVlog.i("live", " === E_SIGNAL_NOTI_MSG ===");
                        CustomToast toast8 = new CustomToast(getApplicationContext());
                        toast8.showToast(getApplicationContext(), getApplicationContext().getString(R.string.signal_weak), Toast.LENGTH_SHORT);
                        SignalStatFlag = false;
                        postEvent(TVEVENT.E_NOSIGNAL_SHOW, NO_SIGNAL_MSG_TIME);     // 5sec
                    }
                    break;
                case E_NOSIGNAL_SHOW:
                    if (CommonStaticData.scanningNow==false) {
                        TVlog.i("live", " === E_NOSIGNAL_SHOW ===");
                        channelChangeEndView(false);
                        sendEvent(TVEVENT.E_BADSIGNAL_CHECK, 3, 0, null);
                        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG) {
                            if (CommonStaticData.scanCHnum > 0) {
                                CommonStaticData.isBadSignalFlag = true;
                            }
                        }
                    }
                    break;
                case E_EPG_UPDATE:
                    int epgType = msg.arg1;
                    if (CommonStaticData.scanCHnum > 0  && CommonStaticData.loadingNow==false && EPGActivity.epgContext != null) {
                        ((EPGActivity) EPGActivity.epgContext).UpdateEPGList(epgType);
                        postEvent(TVEVENT.E_EPG_UPDATE, ((EPGActivity) EPGActivity.epgContext).EPG_UPDATE_PERIOD, ((EPGActivity) EPGActivity.epgContext).EPG_UPDATE_TYPE_PERIODIC);
                    }
                    if (epgType == EPG_UPDATE_TYPE_PF) { //PF EPG update
                        sendEvent(TVEVENT.E_UPDATE_EPG_NAME_AND_DURATION);
                    }
                    break;

                case E_EPGTITLE_UPDATE:  //live
                    if (CommonStaticData.scanCHnum > 0  && CommonStaticData.loadingNow==false && EPGActivity.epgContext != null) {
                        ((EPGActivity) EPGActivity.epgContext).UpdateEPGTitle();
                        TVlog.i(TAG, " >>>>> E_EPGTITLE_UPDATE && UpdateEPGTitle() call!!");
                        //postEvent(TVEVENT.E_EPGTITLE_UPDATE, 3000);
                    }
                    break;

                case E_CHLIST_UPDATE:   //live
                    if (CommonStaticData.chListFragment != null && CommonStaticData.loadingNow==false) {
                        CommonStaticData.chListFragment.UpdateChannelList();
                    }
                    break;

                case E_SURFACE_RESIZE:
                {
                    Rect rec = (Rect) msg.obj;
                    android.view.ViewGroup.LayoutParams lp = sv.getLayoutParams();
                    sv.setX(rec.left);
                    sv.setY(rec.top);
                    lp.height = rec.bottom-rec.top;
                    lp.width  = rec.right -rec.left;
                    TVlog.i(TAG, "A/V EPG mode : X =" +rec.left + " Y = " +rec.top + " W = " + lp.width + " H = "+ lp.height);
                    sv.setLayoutParams(lp);

                    if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                        if (svSub != null) {
                            svSub.setX(rec.left);
                            svSub.setY(rec.top);
                            svSub.setLayoutParams(lp);
                        }
                    } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                            (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                    buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                    buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                        if (svSub != null) {
                            svSub.setX(rec.left);
                            svSub.setY(rec.top);
                            svSub.setLayoutParams(lp);
                        }
                    }
                }
                break;

                case E_SURFACE_ORIRESIZE:
                {
                    sv.setX(0);
                    sv.setY(0);
                    CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                    int scaleMode = CommonStaticData.settings.getInt(CommonStaticData.currentScaleModeKey, 0);  // normal
                    SetVideoScale(scaleMode);
                    TVlog.i(TAG, " Return A/V full mode");
                    if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                        if (svSub != null) {
                            svSub.setX(0);
                            svSub.setY(0);
                        }
                    } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                            (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                    buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                    buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                        if (svSub != null) {
                            svSub.setX(0);
                            svSub.setY(0);
                        }
                    }
                }
                break;

                case E_SURFACE_SUB_ONOFF: {
                    int onoff = (int) msg.arg1;
                    if (onoff == 1)
                    {
                        TVlog.i(TAG, " E_SURFACE_SUB_ON  On ") ;
                        setSubSurfaceVisible(true);
                    } else {
                        TVlog.i(TAG, " E_SURFACE_SUB_ON  Off ") ;
                        setSubSurfaceVisible(false);
                    }
                }
                break;

                case E_RECORDING_START :
                    // time update
                    TVlog.i(TAG, " E_RECORDING_START");
                    mIsRecStarted=true;
                    recStartTime=System.currentTimeMillis();
                    sendEvent(TVEVENT.E_RECORDING_TIME_UPDATE);
                    break;

                case E_RECORDING_TIME_UPDATE :
                    if (isRec)
                    {
                        long elapsed;
                        elapsed = ((System.currentTimeMillis() - recStartTime) / 1000);
                        String display = String.format("%02d:%02d:%02d", elapsed / 3600, (elapsed % 3600) / 60, (elapsed % 60));
                        if (recTimeview != null) recTimeview.setText(display);
                        TVlog.i(TAG, " recording time = " + display);

                        postEvent(TVEVENT.E_RECORDING_TIME_UPDATE, RECORDING_UPDATE_TIME);
                    }
                    break;

                case E_RECORDING_FAIL:
                {
                    ll_recTimeview.setVisibility(View.INVISIBLE);
                    recTimeview.setText("");
                    int errorType = (int) msg.arg1;
                    isRec = false;
                    TVlog.i(TAG, "=E_RECORDING_FAIL  errorType = " + errorType);
                    recB.setImageResource(R.drawable.rec_color_f);
                    if (buildOption.GUI_STYLE == 2 || buildOption.GUI_STYLE == 3) {
                        rightB.setVisibility(View.VISIBLE);
                        leftB.setVisibility(View.VISIBLE);
                    }
                    if (1 == errorType) {
                        CustomToast toast9 = new CustomToast(getApplicationContext());
                        toast9.showToast(getApplicationContext(), getApplicationContext().getString(R.string.recordFileSizeError), Toast.LENGTH_SHORT);
                    } else if (2 == errorType) {
                        CustomToast toast10 = new CustomToast(getApplicationContext());
                        toast10.showToast(getApplicationContext(), getApplicationContext().getString(R.string.recordNotSupportFormat), Toast.LENGTH_SHORT);
                    } else if (3 == errorType) {
                        CustomToast toast11 = new CustomToast(getApplicationContext());
                        toast11.showToast(getApplicationContext(), getApplicationContext().getString(R.string.recorded_fail_too_shot), Toast.LENGTH_SHORT);
                    }
                }
                break;

                case E_RECORDING_OK:
                {
                    if (buildOption.GUI_STYLE != 2) {
                        File chkFile = new File(recordingFileName);
                        boolean fileExist = chkFile.exists();

                        if (fileExist && (buildOption.RECORDING_TYPE == buildOption.RECORDING_TYPE_TS)) {
                            CustomToast toast15 = new CustomToast(getApplicationContext());
                            toast15.showToast(getApplicationContext(), getApplicationContext().getString(R.string.recorded) + " : " + recordingFileName, Toast.LENGTH_SHORT);
                        }
                    }
                }
                break;

                case E_BATTERY_LIMITED_CHECK:
                {
                    float level = getBatteryLevel();
                    int setting = CommonStaticData.battMonitorSet;
                    float settingLevel = 0;

                    TVlog.i(TAG, " bettey level  = "+level+ " Setting = "+ setting);

                    // setting 0 = off ,1 =50, 2= 30, 3, 15, 4=0

                    switch (setting)
                    {
                        case 0:settingLevel = 0;  break;
                        case 1:settingLevel = 50; break;
                        case 2:settingLevel = 30; break;
                        case 3:settingLevel = 15; break;
                        case 4:settingLevel = 10; break;
                    }
                    if (settingLevel > level) {
                        //pop_up
                        InputDialog dig = new InputDialog(instance, InputDialog.TYPE_BATTERY_NOTIFY,(int)level, null, null);
                    } else {
                        if (setting !=0) {
                            postEvent(TVEVENT.E_BATTERY_LIMITED_CHECK, 5000);
                        }
                    }

                }
                break;
                case E_CONFIRMED_PASSWORD:
                {
                    CommonStaticData.ageLimitFlag = false;
                    sendEvent(E_RATING_MONITOR);
                    //TVBridge.serviceID_start(mChannelIndex);
                }
                break;

                case E_SLEEP_TIMER:
                {
                    new InputDialog(instance,InputDialog.TYPE_SLEEP_NOTIFY, null, null, null);
                }
                break;

                case E_SLEEP_TIMER_EXPIRED:
                case E_TERMINATE:
                {
                    TVTerminate();
                }
                break;

                case E_CHANNEL_NAME_UPDATE:
                {
                    mChannelIndex = CommonStaticData.lastCH;
                    if (mCursor != null) {      // elliot
                        mCursor.moveToPosition(mChannelIndex);
                    }
                    if (CommonStaticData.scanCHnum > 0) {
                        if (mCursor != null) {
                            int freq = Integer.parseInt(mCursor.getString(CommonStaticData.COLUMN_INDEX_SERVICE_FREQ));
                            TVlog.i(TAG, " >>>>> current freq = " + freq);

                            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                                int channelNo = 13 + (int)((freq-473143)/6000);
                                currChNo.setText(channelNo+"ch");
                            } else {
                                if (buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_ONESEG
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                                    // for Sri Lanka
                                    int channelNo = 13 + (int)((freq-474000)/8000);
                                    currChNo.setText(channelNo+"ch");
                                } else {
                                    int channelNo = 14 + (int)((freq-473143)/6000);
                                    currChNo.setText(channelNo+"ch");
                                }
                            }

                            String channelName = mCursor.getString(CommonStaticData.COLUMN_INDEX_SERVICE_NAME);
                            String[] split_channelName = channelName.split(" ");

                            // live modify 20170104
                            currRemoteNo.setText(split_channelName[0]);
                            String str = "";
                            for (int i = 1; i < split_channelName.length; i++) {
                                str += split_channelName[i];
                                if (i < split_channelName.length - 1) {
                                    str += " ";
                                }
                            }
                            currCH.setText(str);
                            //
                            int type = (int) mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV);
                            int free = (int) mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_FREE);
                            if (type == 0) { // if 1seg
                                if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                                    iv_ChType.setBackgroundResource(R.drawable.jp_1seg);
                                    iv_ChFree.setVisibility(View.GONE);
                                } else {
                                    iv_ChType.setBackgroundResource(R.drawable.tv_icon_1seg);
                                    if (free == 0) {
                                        iv_ChFree.setVisibility(View.VISIBLE);
                                    } else {
                                        iv_ChFree.setVisibility(View.GONE);
                                    }
                                }
                            } else if (type == 1) { // if fullseg
                                if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                                    iv_ChType.setBackgroundResource(R.drawable.jp_fullseg);
                                    iv_ChFree.setVisibility(View.GONE);
                                } else {
                                    iv_ChType.setBackgroundResource(R.drawable.tv_icon_fullseg);
                                    if (free == 0) {
                                        iv_ChFree.setVisibility(View.VISIBLE);
                                    } else {
                                        iv_ChFree.setVisibility(View.GONE);
                                    }
                                }
                            }

                            sendEvent(TVEVENT.E_UPDATE_EPG_NAME_AND_DURATION);

                            AudioFormat = mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_AUDFORM);
                            VideoFormat = mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_VIDFORM);
                            Scrambled = mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_FREE);
                            mRemoteKey = mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_REMOTE_KEY);
                            mSvcNumber = mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_NUMBER);

                            if (isChannelListViewOn) {
                                sendEvent(TVEVENT.E_CHLIST_UPDATE);
                            }

                            ActivityManager am = (ActivityManager)getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
                            ComponentName componentName = am.getRunningTasks(1).get(0).topActivity;
                            if (componentName.getClassName().contains("EPGActivity")) {
                                if (EPGActivity.EActivity != null) {
                                    EPGActivity epgActivity = (EPGActivity) EPGActivity.EActivity;
                                    epgActivity.finish();
                                }
                                if ((buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) && (mUsbChipType == USB_CHIP_TYPE_NONE)) {          // justin add
                                    CustomToast toast = new CustomToast(getApplicationContext());
                                    toast.showToast(getApplicationContext(), getApplicationContext().getString(R.string.usb_dongle_not_attached), Toast.LENGTH_SHORT);
                                } else {
                                    Intent intentA = new Intent(MainActivity.this, EPGActivity.class);
                                    intentA.putExtra("curIndex", mChannelIndex);
                                    startActivity(intentA);
                                }
                            }

                            TVlog.i (TAG, " >>>>> Scrambled2 = "+String.valueOf(Scrambled));

                            if (Scrambled == 0) {
                                sendEvent(TVEVENT.E_BADSIGNAL_CHECK, 2, 0, null);
                            }
                        }
                    }
                }
                break;

                case E_MAINACTIVITY_VIEW_TOASTS:
                {
                    String viewText=null;
                    if (msg.obj != null) {
                        viewText = (String) msg.obj;
                    }
                    viewToastMSG(viewText, true);
                }
                break;

                case E_NO_DECODER_NOTIFY:
                {
                    CustomToast toast11 = new CustomToast(getApplicationContext());
                    toast11.showToast(getApplicationContext(), getApplicationContext().getString(R.string.no_decoder), Toast.LENGTH_SHORT);
                }
                break;

                case E_AUTO_CHANGE_CHANNEL_TEST:
                {
                    if (buildOption.LOG_CAPTURE_MODE == 3)
                    {
                        int currentChannel = TVBridge.getCurrentChannel();

                        TVlog.i(TAG, " E_AUTO_CHANGE_CHANNEL_TEST  currentID = " + currentChannel + " ChannelCount = "+CommonStaticData.scanCHnum );
                        if (currentChannel < (CommonStaticData.scanCHnum -1)) {
                            CommonStaticData.passwordVerifyFlag = false;
                            CommonStaticData.ageLimitFlag = false;
                            //CommonStaticData.screenBlockFlag = false;
                            sendEvent(E_CAPTION_CLEAR_NOTIFY);
                            sendEvent(TVEVENT.E_SUPERIMPOSE_CLEAR_NOTIFY);

                            channelChangeStartView(false);
                            //changeChannelView.setVisibility(View.VISIBLE);  live0919

                            TVBridge.AVStartPlus();

                            postEvent(TVEVENT.E_AUTO_CHANGE_CHANNEL_TEST,20*1000);
                        } else {
                            TVlog.i(TAG, " E_AUTO_CHANGE_CHANNEL_TEST  END ~~~~~~~~~~~");
                            TVTerminate();
                        }
                    }
                }
                break;
                case E_CHANNEL_CHANGE_TIMEOVER:
                    if (CommonStaticData.tuneTimeOver==true && CommonStaticData.scanningNow==false && CommonStaticData.scanCHnum > 0) {
                        //TVBridge.stop();
                        //sendEvent(TVEVENT.E_CHANNEL_CHANGE_FAIL);
                        sendEvent(TVEVENT.E_BADSIGNAL_CHECK, 4, 0, null);
                    }
                    break;

                case E_SCAN_HANDOVER_START:
                    if (CommonStaticData.handoverMode == CommonStaticData.HANDOVER_MODE_OFF && CommonStaticData.scanningNow == false) {  // handover not running
                        TVlog.i(TAG, " >>>>> E_SCAN_HANDOVER_START ");
                        CommonStaticData.handoverMode = CommonStaticData.HANDOVER_MODE_ON_NORMAL;
                        CommonStaticData.handoverIndex = mChannelIndex;
                        CommonStaticData.scanningNow = true;  //live add
                        CommonStaticData.fromFindFail = false;  //live add
                        CommonStaticData.captionSelect = 0;    // justin add
                        CommonStaticData.superimposeSelect = 0;

                        if (ChannelMainActivity.CActivity != null) {
                            ChannelMainActivity channelMainActivity = (ChannelMainActivity) ChannelMainActivity.CActivity;
                            channelMainActivity.finish();
                        }
                        if (EPGActivity.EActivity != null) {
                            EPGActivity epgActivity = (EPGActivity) EPGActivity.EActivity;
                            epgActivity.finish();
                        }
                        if (SettingActivity.SActivity != null) {
                            SettingActivity settingActivity = (SettingActivity) SettingActivity.SActivity;
                            settingActivity.finish();
                        }
                        recordingStop(true);
                        TVBridge.stop();
                        SharedPreferences.Editor editor6 = CommonStaticData.settings.edit();
                        editor6.putInt(CommonStaticData.lastChannelKey, CommonStaticData.lastCH);
                        editor6.commit();

                        if (isMainActivity) {
                            removeEvent(TVEVENT.E_SIGNAL_NOTI_MSG);
                            removeEvent(TVEVENT.E_BADSIGNAL_CHECK);
                            removeEvent(TVEVENT.E_CHANNEL_LIST_ENCRYPTED);

                            //removeEvent(TVEVENT.E_BADSIGNAL_CHECK);
                            //removeEvent(E_SIGNAL_NOTI_MSG);
                            removeEvent(TVEVENT.E_NOSIGNAL_SHOW);
                            removeEvent(TVEVENT.E_CHANNEL_CHANGE_TIMEOVER);
                            sendEvent(TVEVENT.E_CAPTION_CLEAR_NOTIFY);
                            sendEvent(TVEVENT.E_SUPERIMPOSE_CLEAR_NOTIFY);
                            if (sv != null && sv.isShown()) {
                                sv.setBackgroundColor(getResources().getColor(R.color.black));
                            }
                            if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                                if (svSub != null && svSub.isShown()) {
                                    svSub.setBackgroundColor(getResources().getColor(R.color.black));
                                }
                            } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                                    (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                            buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                            buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                                if (svSub != null && svSub.isShown()) {
                                    svSub.setBackgroundColor(getResources().getColor(R.color.black));
                                }
                            }
                            channelChangeEndView(false);
                            ll_noSignal.setVisibility(View.INVISIBLE);
                            ll_audioOnlyChannel.setVisibility(View.INVISIBLE);
                            hideController();
                            ll_mainAutoSearch.setVisibility(View.VISIBLE);

                            //doScan = new ScanProcess(instance);
                            TVBridge.scan((byte) 0);
                            //doScan.showProgress(1, 0, 473143, doScan.SHOW_PRGRESS_ON);
                        } else if (FloatingWindow.isFloating) {
                            removeEvent(TVEVENT.E_SIGNAL_NOTI_MSG_FLOATING);
                            removeEvent(TVEVENT.E_BADSIGNAL_CHECK_FLOATING);
                            //removeEvent(TVEVENT.E_CHANNEL_LIST_ENCRYPTED);

                            //removeEvent(TVEVENT.E_BADSIGNAL_CHECK);
                            //removeEvent(E_SIGNAL_NOTI_MSG);
                            removeEvent(TVEVENT.E_NOSIGNAL_SHOW_FLOATING);
                            removeEvent(TVEVENT.E_CHANNEL_CHANGE_TIMEOVER_FLOATING);
                            FloatingWindow.getInstance().sendEvent(TVEVENT.E_CAPTION_CLEAR_NOTIFY_FLOATING);
                            FloatingWindow.getInstance().sendEvent(TVEVENT.E_SUPERIMPOSE_CLEAR_NOTIFY_FLOATING);
                            if (FloatingWindow.sv_floatingView != null && FloatingWindow.sv_floatingView.isShown()) {
                                FloatingWindow.sv_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
                            }
                            if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                                if (FloatingWindow.svSub_floatingView != null && FloatingWindow.svSub_floatingView.isShown()) {
                                    FloatingWindow.svSub_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
                                }
                            } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                                    (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                            buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                            buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                                if (FloatingWindow.svSub_floatingView != null && FloatingWindow.svSub_floatingView.isShown()) {
                                    FloatingWindow.svSub_floatingView.setBackgroundColor(getResources().getColor(R.color.black));
                                }
                            }
                            FloatingWindow.getInstance().channelChangeEndView(false);
                            FloatingWindow.getInstance().floating_noSignal.setVisibility(View.INVISIBLE);
                            FloatingWindow.getInstance().floating_programNotMsg.setVisibility(View.INVISIBLE);
                            FloatingWindow.getInstance().floating_ll_audioOnlyChannel.setVisibility(View.INVISIBLE);
                            FloatingWindow.getInstance().ll_floatingAutoSearch.setVisibility(View.VISIBLE);

                            //doScan = new ScanProcess(instance);
                            TVBridge.scan((byte) 0);
                            //doScan.showProgress(1, 0, 473143, doScan.SHOW_PRGRESS_ON);
                        } else if (ChatMainActivity.isChat) {
                            removeEvent(TVEVENT.E_SIGNAL_NOTI_MSG_CHAT);
                            removeEvent(TVEVENT.E_BADSIGNAL_CHECK_CHAT);
                            removeEvent(TVEVENT.E_NOSIGNAL_SHOW_CHAT);
                            removeEvent(TVEVENT.E_CHANNEL_CHANGE_TIMEOVER_CHAT);
                            ChatMainActivity.getInstance().sendEvent(TVEVENT.E_CAPTION_CLEAR_NOTIFY_CHAT);
                            ChatMainActivity.getInstance().sendEvent(TVEVENT.E_SUPERIMPOSE_CLEAR_NOTIFY_CHAT);
                            if (ChatMainActivity.sv_chatView != null && ChatMainActivity.sv_chatView.isShown()) {
                                ChatMainActivity.sv_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                            }
                            if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                                if (ChatMainActivity.svSub_chatView != null && ChatMainActivity.svSub_chatView.isShown()) {
                                    svSub.setBackgroundColor(getResources().getColor(R.color.black));
                                }
                            } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                                    (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                            buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                            buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                                if (ChatMainActivity.svSub_chatView != null && ChatMainActivity.svSub_chatView.isShown()) {
                                    ChatMainActivity.svSub_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                                }
                            }
                            ChatMainActivity.getInstance().channelChangeEndView(false);
                            ChatMainActivity.getInstance().chat_ll_noSignal.setVisibility(View.INVISIBLE);
                            ChatMainActivity.getInstance().chat_ll_audioOnlyChannel.setVisibility(View.INVISIBLE);
                            ChatMainActivity.getInstance().hideChatController();
                            ChatMainActivity.getInstance().ll_chatAutoSearch.setVisibility(View.VISIBLE);
                            TVBridge.scan((byte) 0);
                        }
                    }
                    break;

                case E_SCAN_HANDOVER_PROCESS :
                    if (CommonStaticData.handoverMode == CommonStaticData.HANDOVER_MODE_ON_NORMAL) {  // handover is running
                        TVlog.i(TAG, "---------------- E_SCAN_HANDOVER_PROCESS -------------------");
                        int progress2 = (int) msg.arg1;
                        int found2 = (int) msg.arg2;
                        int freqKHz2 = 0;
                        if (msg.obj != null) {
                            freqKHz2 = (int) msg.obj;
                        }
                        //doScan.showProgress(progress2, found2, freqKHz2, doScan.SHOW_PRGRESS_ON);
                    }
                    break;

                case E_SCAN_HANDOVER_SUCCESS :
                    if (CommonStaticData.handoverMode == CommonStaticData.HANDOVER_MODE_ON_NORMAL) {
                        TVlog.i(TAG, "---------------- E_SCAN_HANDOVER_SUCCESS -------------------");
                        int index = (int) msg.arg1;
                        int total = (int) msg.arg2;
                        int updateMode = (int) msg.obj;
                        if (mCursor != null) {
                            TVlog.e(TAG, "E_SCAN_HANDOVER_SUCESS: index =  " + index + ", total = " + total + ", cursor count = " + mCursor.getCount() + ", update mode = " + updateMode);
                            //CommonStaticData.scanCHnum = total;
                            if (index == -1) {
                                CommonStaticData.handoverIndex = -1;
                            } else {
                                CommonStaticData.handoverIndex = index;
                                CommonStaticData.lastCH = index;  //live
                            }
                            if (total != mCursor.getCount() || updateMode > 0) {
                                CommonStaticData.handoverMode = CommonStaticData.HANDOVER_MODE_ON_UPDATE_LIST;  // list will be updated
                                TVlog.e(TAG, "handoverMode to 2");
                            }
                        }

                        if (CommonStaticData.handoverMode == CommonStaticData.HANDOVER_MODE_ON_NORMAL) {
                            if (MainActivity.isMainActivity) {
                                sendEvent(TVEVENT.E_SCAN_COMPLETED);
                            } else if (ChatMainActivity.isChat) {
                                ChatMainActivity.getInstance().sendEvent(TVEVENT.E_SCAN_COMPLETED_CHAT);
                            } else if (FloatingWindow.isFloating) {
                                FloatingWindow.getInstance().sendEvent(TVEVENT.E_SCAN_COMPLETED_FLOATING);
                            }
                        }

                        //doScan.showProgress(100, 1, 0, doScan.SHOW_PRORESS_OFF);
                    }
                    break;

                case E_CHANNEL_SWITCHING:
                    TVlog.i("live", " ==== E_CHANNEL_SWITCHING ====");
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

                    info = FCI_TVi.GetPairNSegInfoOfCHIndex(mChannelIndex);
                    pairIndex = info[0];
                    isFullseg = info[1];
                    channelMainIndex = info[3];
                    oneSegIndex = info[4];

                    TVlog.i("live", " >>> cur Index = " + mChannelIndex  + ", isFullseg = " + isFullseg + ", pairIndex = " + pairIndex
                            + ", channelMainIndex = " + channelMainIndex + ", oneSegIndex = " + oneSegIndex);
                    if (mCursor != null) {
                        if (mCursor.getCount() > pairIndex && pairIndex != -1) {
                            orgPos = mCursor.getPosition();
                            mCursor.moveToPosition(pairIndex);
                            info = FCI_TVi.GetPairNSegInfoOfCHIndex(pairIndex);
                            if (info[1] == tomove) {
                                findFail = 0;
                            } else {
                                mCursor.moveToPosition(orgPos);
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
                            if (oneSegIndex != -1) {
                                lastIndex = mChannelIndex;
                                mChannelIndex = oneSegIndex;
                                TVlog.i("live", " >>> changed to O-seg index 1 = " + mChannelIndex);
                                CommonStaticData.fromFindFail = true;
                                TVBridge.serviceID_start(mChannelIndex);
                            } else {
                                TVlog.i(TAG,  " >>> There is no channel for switching!!");
                                break;
                            }
                        } else if (isFullseg == 0 && tomove == 1){  //O-seg->F-seg
                            TVlog.i("live", " >>> CommonStaticData.fromFindFail = "+CommonStaticData.fromFindFail);
                            if (channelMainIndex != -1) {
                                if (CommonStaticData.fromFindFail == true) {
                                    mChannelIndex = lastIndex;
                                    TVlog.i("live", " >>> changed to F-seg index 2 = " + mChannelIndex);
                                    CommonStaticData.fromFindFail = false;
                                    TVBridge.serviceID_start(mChannelIndex);
                                } else {
                                    mChannelIndex = pairIndex;
                                    TVlog.i("live", " >>> changed to F-seg index 3 = " + mChannelIndex);
                                    CommonStaticData.fromFindFail = false;
                                    TVBridge.serviceID_start(mChannelIndex);
                                }
                            } else {
                                TVlog.i(TAG,  " >>> There is no channel for switching!!");
                                break;
                            }
                        }
                    } else {
                        mChannelIndex = pairIndex;
                        if (tomove == 0) { // to O-seg
                            mChannelIndex = oneSegIndex;
                            TVlog.i("live", " >>> changed to 1-seg index 4 =" + mChannelIndex);
                            CommonStaticData.fromFindFail = false;
                            TVBridge.serviceID_start(mChannelIndex);
                        } else if (tomove == 1) { // to F-seg
                            TVlog.i("live", " >>> CommonStaticData.fromFindFail = "+CommonStaticData.fromFindFail);
                            if (CommonStaticData.fromFindFail == true) {
                                mChannelIndex = lastIndex;
                                TVlog.i("live", " >>> changed to F-seg index 5 = " + mChannelIndex);
                                CommonStaticData.fromFindFail = false;
                                TVBridge.serviceID_start(mChannelIndex);
                            } else {
                                mChannelIndex = pairIndex;
                                TVlog.i("live", " >>> changed to F-seg index 6 = " + mChannelIndex);
                                CommonStaticData.fromFindFail = false;
                                TVBridge.serviceID_start(mChannelIndex);
                            }
                        }
                    }
                    break;

                //ADD_GINGA_NCL[[
                case E_INTERACTIVE_ENABLE: {
                    if (buildOption.ADD_GINGA_NCL == true) {
                        if (mRunDemuxThread == false) {
                            FCI_TVi.enableGingaNCL();
                            restartNCLDemux();
                        }
                    }
                }
                break;

                case E_INTERACTIVE_DISABLE: {
                    if (buildOption.ADD_GINGA_NCL == true) {
                        if (mRunDemuxThread == true) {
                            resetNCL();
                            FCI_TVi.disableGingaNCL();
                        }
                    }
                }
                break;
                //]]ADD_GINGA_NCL

                case E_NOT_SUPPORT_RESOLUTION:
                {
                    /*CustomToast toasttmp = new CustomToast(getApplicationContext());
                    toasttmp.showToast(getApplicationContext(), getApplicationContext().getString(R.string.not_support_resolution), Toast.LENGTH_SHORT);*/

                    showDialog(DIALOG_NOT_SUPPORT_RESOLUTION);

                    recordingStop(true);
                }
                break;

                case E_DEBUG_SCREEN_DISPLAY:
                {
                    if (DebugMode.getDebugMode().checkingDebugOn()) {
                        String testMsg = (String) msg.obj;
                        DebugScreenDisplay(testMsg);
                    }
                }
                break;

                case E_LOG_CAPTURE_MOD_ON:
                {
                    dumpTVLog=true;
                    TVlog.i(TAG, " ===========================");
                    TVlog.i(TAG, " Force LOG_CAPTURE_MODE on ");
                    TVlog.i(TAG, " ===========================");
                    if (isStoragePermissionGranted() == true) {  // for Android M Permission
                        saveLogcatToFile(instance);
                    } else {
                        CustomToast toast = new CustomToast(getApplicationContext());
                        toast.showToast(getApplicationContext(), "Permission is needed to save FCI_TV_log!", Toast.LENGTH_SHORT);
                    }
                }
                break;
                case  E_FINISH_NOTIFY:
                {

                    TVlog.i(TAG, " ===========================");
                    TVlog.i(TAG, "  Call Main Activity finish ");
                    TVlog.i(TAG, " ===========================");
                    if (SDK_INT >= 21) {
                        finishAndRemoveTask();
                    } else {
                        finish();
                    }
                }
                break;
                case E_UPDATE_EPG_NAME_AND_DURATION:
                {
                    final int EPG_TIME_NOT_DEFINED = 0xFF;
                    final String EPG_TIME_STR_NOT_DEFINED = "??:??";
                    final String EPG_TIME_STR_FORMAT_SPECIFIER = "%02d:%02d";
                    final String EPG_TIME_STR_SEPARATOR = "~";
                    final int EPG_UPDATE_TYPE_PF = 0;
                    final int EPG_UPDATE_TYPE_SCH = 1;

                    TVlog.i("live", " >>> E_UPDATE_EPG_NAME_AND_DURATION");
                    if (CommonStaticData.scanCHnum > 0) {
                        String progName = FCI_TVi.GetServiceName();
                        TVlog.i("live", " >>> progName = "+progName);
                        if (progName.length() != 0) {
                            //duration
                            String epgStartNDuration = null;
                            String epgStartTime = null;
                            String epgEndTime = null;
                            int mCurWeekDay = 0;
                            //int EPGIndex;
                            int startYear, startMonth, startDay, startHour, startMin, endYear, endMonth, endDay, endHour, endMin, tmpHour, tmpMin;
                            int date[] = FCI_TVi.GetTSNetTime();
                            mCurWeekDay = MainActivity.DayFun(date[0], date[1], date[2]);
                            FCI_TVi.GetEPGCount(mCurWeekDay, mChannelIndex);
                            int[] startNDuration = FCI_TVi.GetEPGStartTimeNDuration(mCurWeekDay, 0);

                            if (startNDuration != null) {
                                startYear = startNDuration[0];
                                startMonth = startNDuration[1];
                                startDay = startNDuration[2];
                                startHour = startNDuration[3];
                                startMin = startNDuration[4];
                                tmpHour = startNDuration[6];
                                tmpMin = startNDuration[7];
                                //start time
                                if (startHour == EPG_TIME_NOT_DEFINED || startMin == EPG_TIME_NOT_DEFINED) {
                                    epgStartTime = EPG_TIME_STR_NOT_DEFINED;
                                } else {
                                    epgStartTime = String.format(EPG_TIME_STR_FORMAT_SPECIFIER, startHour, startMin);
                                }
                                //end time
                                if (tmpHour == EPG_TIME_NOT_DEFINED || tmpMin == EPG_TIME_NOT_DEFINED) { //duration not defined.
                                    epgEndTime = EPG_TIME_STR_NOT_DEFINED;
                                } else {
                                    endMin = startMin + startNDuration[7];
                                    if (endMin >= 60) {
                                        endHour = 1;
                                        endMin = endMin - 60;
                                    } else {
                                        endHour = 0;
                                    }
                                    endHour = endHour + startHour + startNDuration[6];
                                    if (endHour >= 24) {
                                        endHour = endHour % 24;
                                    }
                                    epgEndTime = String.format(EPG_TIME_STR_FORMAT_SPECIFIER, endHour, endMin);
                                }

                                epgStartNDuration = epgStartTime + EPG_TIME_STR_SEPARATOR + epgEndTime;

                                if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 )  {
                                    //program name
                                    currProgram.setText(progName);
                                    currDuration.setText(epgStartNDuration);
                                }
                                ActivityManager am = (ActivityManager)getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
                                ComponentName componentName = am.getRunningTasks(1).get(0).topActivity;
                                if (componentName.getClassName().contains("EPGActivity")) {
                                    if (EPGActivity.getInstance() != null) {
                                        int EPGCount = FCI_TVi.GetEPGCount(mCurWeekDay, CommonStaticData.lastCH);
                                        if (EPGCount == 0) {
                                            EPGActivity.getInstance().curEpgPosition.setText("0");
                                        } else {    // justin no epg text clear
                                            EPGActivity.getInstance().curEpgPosition.setText("1");
                                        }
                                    }
                                }
                            }
                        } else {
                            if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 )  {
                                currProgram.setText("- - -");
                                currDuration.setText("--:--~--:--");
                            }
                            removeEvent(TVEVENT.E_UPDATE_EPG_NAME_AND_DURATION);
                            postEvent(TVEVENT.E_UPDATE_EPG_NAME_AND_DURATION, SIGNAL_MONITER_TIME*5);
                        }
                    } else {
                        ActivityManager am = (ActivityManager)getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
                        ComponentName componentName = am.getRunningTasks(1).get(0).topActivity;
                        if (componentName.getClassName().contains("EPGActivity")) {
                            if (EPGActivity.getInstance() != null) {
                                EPGActivity.getInstance().tv_phyChNo.setText("--");
                                EPGActivity.getInstance().tv_remoteNo.setText("---");
                                EPGActivity.getInstance().mTopBarText.setText("- - -");
                                EPGActivity.getInstance().curEpgPosition.setText("0");
                            }
                        }
                    }
                }
                break;
            }

            super.handleMessage(msg);
        }
    };

    private static Process processLog = null;
    public void saveLogcatToFile(Context context) {

        String rootPath;

        if (buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
            rootPath = MainActivity.getInstance().getInternalSDPath()+"FCI_LOG";
        } else {
            if (SDK_INT < Build.VERSION_CODES.N) {
                if (getExternalMounts().size() != 0) {  // external SD
                    //rootPath = buildOption.SECOND_DRIVE_PATH+"FCI_LOG";
                    rootPath = MainActivity.getInstance().getExternalSDPath()+"FCI_LOG";

                } else { // phone
                    //rootPath = buildOption.PHONE_DRIVE_PATH+"FCI_LOG";
                    rootPath = MainActivity.getInstance().getInternalSDPath()+"FCI_LOG";
                }
            } else {
                rootPath = MainActivity.getInstance().getInternalSDPath()+"FCI_LOG";
            }
        }

        File dir_exist = new File(rootPath);
        if (!dir_exist.exists()) {
            dir_exist.mkdirs();
            TVlog.i(TAG, "==== make new folder ====  "+rootPath);
        }

        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();

        String localDate = today.year+ "-" +
                (today.month + 1) + "-" +
                today.monthDay + "_" +
                today.format("%H:%M:%S").replaceAll(":", "-");


        TVlog.i(TAG, "local data =  " + localDate);
        String fileName = "FCITV_Log_v"+buildInformation.RELEASE_VERSION+"_"+localDate+".txt";
        File outputFile = new File(rootPath+"/",fileName);

        TVlog.i(TAG, "saveLogcatToFile outputFile = " + outputFile);

        try {
            // @SuppressWarnings("unused");
            processLog = Runtime.getRuntime().exec("logcat -f "+outputFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        instance.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + fileName)));
    }

    public void viewToastMSG(String _viewText,boolean _isLong)
    {
        if (mMainToast ==null )
        {
            mMainToast = Toast.makeText(instance, _viewText, Toast.LENGTH_SHORT);
        }

        if (true == _isLong) {
            mMainToast.setDuration(Toast.LENGTH_LONG);
        } else {
            mMainToast.setDuration(Toast.LENGTH_SHORT);
            //   mMainToast = Toast.makeText(instance, _viewText, Toast.LENGTH_LONG);
        }
        mMainToast.setText(_viewText);
        mMainToast.setGravity(Gravity.CENTER, 0, buildOption.TOAST_SHIFT_Y);  // for tablet
        //mMainToast.setGravity(Gravity.CENTER, 0, 200); // for normal
        mMainToast.show();
    }

    public static int DayFun( int y, int m, int d ) {
        int c;
        if ( m <= 2 )
        {
            m += 12;
            y-- ;
        }
        c = y / 100;
        y %= 100;
        int w = ( y+(y/4)+(c/4)-2*c+(26*(m+1)/10)+d-1) % 7;
        if ( w <= 0 )
            w += 7;
        return w;
    }

    public void sendEvent(TVEVENT _Event) {
        int m;
        m = _Event.ordinal();
        Message msg = TVUI_Handler.obtainMessage(m);
        TVUI_Handler.sendMessage(msg);
    }

    public void sendEvent(TVEVENT _Event, int[] regionCh) {
        int m;
        m = _Event.ordinal();
        Message msg = TVUI_Handler.obtainMessage(m);
        msg.obj = (int[])regionCh;
        TVUI_Handler.sendMessage(msg);
    }

    public void sendEvent(TVEVENT _Event, int _arg1, int _arg2, Object _obj) {
        int m;
        m = _Event.ordinal();
        Message msg = TVUI_Handler.obtainMessage(m);
        msg.arg1 = _arg1;
        msg.arg2 = _arg2;
        msg.obj = _obj;
        TVUI_Handler.sendMessage(msg);
    }

    public void postEvent(TVEVENT _Event,int _time )
    {
        int m;
        m = _Event.ordinal();
        Message msg = TVUI_Handler.obtainMessage(m);
        TVUI_Handler.sendEmptyMessageDelayed(m, _time);
    }

    public void postEvent(TVEVENT _Event, int _time, int _arg1)
    {
        int m;
        m = _Event.ordinal();
        Message msg = TVUI_Handler.obtainMessage(m);
        msg.arg1 = _arg1;

        TVUI_Handler.sendMessageDelayed(msg, _time);
    }

    public void removeEvent(TVEVENT _Event)
    {
        int m;
        m = _Event.ordinal();
        Message msg = TVUI_Handler.obtainMessage(m);
        TVUI_Handler.removeMessages(m);
    }

    public void sendSubtitle(String capContents) {
        Bundle caption = new Bundle();
        caption.putString("caption_info", capContents);
        caption.putString("clear", "");
        sendEvent(TVEVENT.E_CAPTION_NOTIFY, 0, 0, caption);
    }

    public void sendSuperimpose(String superContents) {
        Bundle superimpose = new Bundle();
        superimpose.putString("superimpose_info", superContents);
        superimpose.putString("clear", "");
        sendEvent(TVEVENT.E_SUPERIMPOSE_NOTIFY, 0, 0, superimpose);
    }

    //JAPAN_CAPTION[[
    public void sendSubtitleDirect(byte[] capData, int capLen, byte isClear, byte isEnd, int[] capInfo) {
        if (MainActivity.isMainActivity) {
            if (mCaptionView != null && mCursor != null) {
                mCaptionView.renderCaptionDirect(capData, capLen, isClear, isEnd, capInfo, mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV));
            }
        }
    }

    public void sendSuperimposeDirect(byte[] supData, int supLen, byte isClear, byte isEnd, int[] supInfo) {
        if (MainActivity.isMainActivity) {
            if (mSuperimposeView != null && mCursor != null) {
                mSuperimposeView.renderCaptionDirect(supData, supLen, isClear, isEnd, supInfo, mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV));
            }
        }
    }
    //]]JAPAN_CAPTION

    public void notifyFirstVideo() {
        sendEvent(TVEVENT.E_FIRSTVIDEO);
    }

    public void notifyFirstAudio() {
        sendEvent(TVEVENT.E_FIRSTAUDIO);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        TVlog.i(TAG, "==========surfaceCreated   ======================");
        //usbdongle[[
        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB)
        {
            TVlog.i("FCIISDBT::", "usb device chip=" + mUsbChipType);
            if (mUsbChipType == USB_CHIP_TYPE_LME) {
                int loopCount = 0;
                while (mUsbLMEMode != USB_LME_MODE_WARM && loopCount++ < 10) {
                    TVlog.i("FCIISDBT::", "LME dongle not warm mode ...");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException var5) {
                        var5.printStackTrace();
                    }
                }
            }
        }
        //]]usbdongle

        onStart_TV();

        if (sv != null) {
            FCI_TVi.setSuface(holder.getSurface());
        }

//      if (CommonStaticData.returnMainFromChat) {
//          notifyFirstVideo();
//      } // eliot

        isMainActivity = true;
        FloatingWindow.isFloating = false;
        ChatMainActivity.isChat = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        if (frameWidth == 0 || frameHeight == 0) {

            frameWidth = width;
            frameHeight= height;
        }
        TVlog.i(TAG, "===== surfaceChanged============ width = " + width + " height =" + height);
        getCurrentDeviceResolution();
        getCurrentDeviceDpi();
        getCurrentDeviceSize();
        TVlog.i(TAG, "  >>>>>>>>>>>>>>>>>>> device's width : " + frameWidth + ", height : " + frameHeight
                + ", density : " + density
                + ", densityDpi : " + densityDpi
                + ", dpiName : " + dpiName
                + ", screen size : " + screenSize);

        CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
        int scaleMode = CommonStaticData.settings.getInt(CommonStaticData.currentScaleModeKey, 0);  // normal
        SetVideoScale(scaleMode);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        TVlog.i(TAG, "==========surfaceDestroyed   ======================");
        if (CommonStaticData.scanningNow) {
            if (doScan != null) {
                doScan.showProgress(0, 0, 0, doScan.SHOW_PROGRESS_CLEAR);
            }
        }

        /*
        if (!ChatMainActivity.isChat) {
            SolutionStop();
        }*/

        //SolutionStop();
        removeEvent(TVEVENT.E_SIGNAL_MONITER);
        if (!FloatingWindow.isFloating) {
            SolutionStop();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // live add
        /*
        SharedPreferences.Editor editor = CommonStaticData.settings.edit();
        editor.putInt(CommonStaticData.lastChannelKey, CommonStaticData.lastCH);
        editor.putInt(CommonStaticData.scanedChannelsKey, CommonStaticData.scanCHnum);
        editor.putBoolean(CommonStaticData.loudSpeakerKey, CommonStaticData.loudSpeaker);
        editor.putBoolean(CommonStaticData.badSignalFlagKey, CommonStaticData.badSignalFlag);
        editor.putBoolean(CommonStaticData.encryptFlagKey, CommonStaticData.encryptFlag);
        CommonStaticData.returnMainFromChat = false;
        CommonStaticData.returnMainFromFloating = false;
        editor.putBoolean(CommonStaticData.returnMainFromChatKey, CommonStaticData.returnMainFromChat);
        editor.putBoolean(CommonStaticData.returnMainFromFloatingKey, CommonStaticData.returnMainFromFloating);
        editor.commit();
        */
        //

        TVlog.i(TAG, "=== onConfigurationChanged is called !!! ===");
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            TVlog.i(TAG, "=== Configuration.ORIENTATION_PORTRAIT !!! ===");
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            TVlog.i(TAG, "=== Configuration.ORIENTATION_LANDSCAPE !!! ===");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (buildOption.GUI_STYLE == 2 || buildOption.GUI_STYLE == 3) {
            AudioManager audMgr = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
            View decorView = getWindow().getDecorView();
            uiOptions =  View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE;
            decorView.setSystemUiVisibility(uiOptions);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

            removeEvent(TVEVENT.E_HIDE_GESTURE);
            if (brightbarLayout.getVisibility() == View.VISIBLE) {
                brightbarLayout.setVisibility(View.INVISIBLE);
            }

            switch (keyCode) {
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    // TVlog.i(TAG, "=== KEYCODE_VOLUME_DOWN*********  ");
                    int voldn = audMgr.getStreamVolume(AudioManager.STREAM_MUSIC);
                    if (voldn > 0) {
                        audMgr.setStreamVolume(AudioManager.STREAM_MUSIC, voldn - 1, 0);
                    }
                    mAudio = true;
                    ShowVolume();
                    postEvent(TVEVENT.E_HIDE_GESTURE, 1000);
                    return true;

                case KeyEvent.KEYCODE_VOLUME_UP:
                    int volup = audMgr.getStreamVolume(AudioManager.STREAM_MUSIC);
                    if (volup < 16) {
                        audMgr.setStreamVolume(AudioManager.STREAM_MUSIC, volup + 1, 0);
                    }
                    mAudio = true;
                    ShowVolume();
                    postEvent(TVEVENT.E_HIDE_GESTURE, 1000);
                    return true;

                default:
                    return super.onKeyDown(keyCode, event);
            }
        }
        //TVlog.i(TAG, "=== KEYCODE_NOT_Valid *********  ");
        //return false;
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = mGestureDetector.onTouchEvent(event) ;
        //TVlog.i(TAG, "===onTouchEvent *********= "+ result + " controllerLayout.isShown() = "+ controllerLayout.isShown());
        if (!isUiLocked) {
            if (!result) {
                if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1)  {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            uiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                            decorView.setSystemUiVisibility(uiOptions);
                            return true;

                        case MotionEvent.ACTION_UP:
                            //if (!controllerLayout.isShown()) {
                            if (ll_mainAutoSearch.getVisibility() != View.VISIBLE) {
                                if (buildOption.CUSTOMER.contains("NEXELL")) {
                                    if (controllerLayout.getVisibility() != View.VISIBLE) {

                                        TVlog.i(TAG, "===TOuch outside the dialog *********=");
                                        //  showController();
                                        // [ nexell touch 20170223
                                        controllerLayout.setVisibility(View.VISIBLE);
                                        status_bar.setVisibility(View.VISIBLE);
                                        channelLayout.setVisibility(View.VISIBLE);
                                        // ]
                                        postEvent(TVEVENT.E_HIDE_CONTROLER, CONTROLLER_HIDE_TIME);
                                    }
                                    // [ nexell touch 20170223
                                    else {
                                        controllerLayout.setVisibility(View.INVISIBLE);
                                        status_bar.setVisibility(View.INVISIBLE);
                                        channelLayout.setVisibility(View.INVISIBLE);

                                    }
                                } else {
                                    if (controllerLayout.getVisibility() != View.VISIBLE) {

                                        TVlog.i(TAG, "===TOuch outside the dialog *********=");
                                        //  showController();
                                        postEvent(TVEVENT.E_HIDE_CONTROLER, CONTROLLER_HIDE_TIME);
                                    }
                                }
                            }
                            return true;

                        default:
                            return super.onTouchEvent(event);

                    }
                } else {
                    DisplayMetrics screen = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(screen);
                    if (mSurfaceYDisplayRange == 0) {
                        mSurfaceYDisplayRange = Math.min(screen.widthPixels, screen.heightPixels);
                    }

                    float x_changed = event.getRawX() - mTouchX;
                    float y_changed = event.getRawY() - mTouchY;

                    float coefy = Math.abs(y_changed / x_changed);
                    float coefx = Math.abs(x_changed / y_changed);
                    float xgesturesize = ((x_changed / screen.xdpi) * 2.54f);

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            TVlog.i(TAG, "=== MotionEvent.ACTION_DOWN *********= ");

                            mLastMotionX = event.getX();
                            mLastMotionY = event.getY();

                            mTouchY = event.getRawY();
                            mVol = CurrentVol();
                            //mIsAudioOrBrightnessChanged = false;

                            mTouchX = event.getRawX();
                            uiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                            decorView.setSystemUiVisibility(uiOptions);
                            return true;

                        case MotionEvent.ACTION_MOVE:
                            //TVlog.i(TAG, "=== ACTION_MOVE ********* y=  " + coefy +", x = "+coefx);

                            final float x = event.getX();
                            final float y = event.getY();
                            final int deltaX = Math.abs((int) (mLastMotionX - x));
                            final int deltaY = Math.abs((int) (mLastMotionY - y));

                            // TVlog.i(TAG, "=== ACTION_MOVE ****deltaX=  " + deltaX+", deltaY = "+deltaY);

                            if (coefy > 4) {
                                mEnableBrightnessGesture = true;
                                removeEvent(TVEVENT.E_HIDE_GESTURE);

                                if (mEnableBrightnessGesture && (mTouchX < (screen.widthPixels / 2))) {       // left brightness
                                    TVlog.i(TAG, "=== ACTION_MOVE Brightness *********  ");
                                    //sendEvent(TVEVENT.E_HIDE_CONTROLER);
                                    volumebarLayout.setVisibility(View.INVISIBLE);
                                    doBrightnessTouch(y_changed);
                                }

                                if (mEnableBrightnessGesture && (mTouchX > (screen.widthPixels / 2))) {       // right volume
                                    TVlog.i(TAG, "=== ACTION_MOVE Volume *********  ");
                                    //sendEvent(TVEVENT.E_HIDE_CONTROLER);
                                    brightbarLayout.setVisibility(View.INVISIBLE);
                                    doVolumeTouch(y_changed);
                                }
                            }

                            //if (xgesturesize > 2 && deltaX < 300 && (mTouchX < (screen.widthPixels / 2))) {       // channel list call
                            if (xgesturesize > 2 && (mTouchX < (screen.widthPixels / 2))) {       // channel list call
                                if ((buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) && (mUsbChipType == USB_CHIP_TYPE_NONE)) {          // justin add
                                    if (event.getAction() == MotionEvent.ACTION_UP) {
                                        CustomToast toast12 = new CustomToast(getApplicationContext());
                                        toast12.showToast(getApplicationContext(), getApplicationContext().getString(R.string.usb_dongle_not_attached), Toast.LENGTH_SHORT);
                                    }
                                } else {
                                    if (mIsTouchCHList == false && mEnableChannellist == false) {
                                        TVlog.i(TAG, "=== ACTION_MOVE Channel List *********  ");
                                        mIsTouchCHList = true;
                                        mEnableChannellist = true;
                                        sendEvent(TVEVENT.E_HIDE_GESTURE);
                                        postEvent(TVEVENT.E_SHOW_CHANNELLIST, 100);
                                    }
                                }
                            }
                            return true;

                        case MotionEvent.ACTION_UP:
                            TVlog.i(TAG, "=== MotionEvent.ACTION_UP *********= ");
                            if (mEnableBrightnessGesture == true) {
                                mEnableBrightnessGesture = false;
                                postEvent(TVEVENT.E_HIDE_GESTURE, GESTURE_HIDE_TIME);
                                //InputDialog dig = new InputDialog(this, InputDialog.TYPE_RECOVER_FOCUS, null, null, null);  // make to get focus
                            }
                            postEvent(TVEVENT.E_HIDE_CONTROLER, CONTROLLER_HIDE_TIME);
                            mEnableChannellist = false;
                            return false;

                        case MotionEvent.ACTION_CANCEL:
                            TVlog.i(TAG, "=== MotionEvent.ACTION_CANCEL *********= ");
                            return false;

                        case MotionEvent.ACTION_OUTSIDE:
                            TVlog.i(TAG, "=== MotionEvent.ACTION_OUTSIDE *********= ");
                            return false;

                        default:
                            return super.onTouchEvent(event);
                    }
                }
            }
        } else {
            if (!result) {
                if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            uiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                            decorView.setSystemUiVisibility(uiOptions);
                            return true;

                        case MotionEvent.ACTION_UP:
                            if (ll_uiLocked.getVisibility() != View.VISIBLE) {

                                TVlog.i(TAG, "===TOuch outside the dialog *********=");
                                //showController();
                                postEvent(TVEVENT.E_HIDE_CONTROLER, CONTROLLER_HIDE_TIME);
                            }
                            return true;

                        default:
                            return super.onTouchEvent(event);
                    }
                } else {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            uiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                            decorView.setSystemUiVisibility(uiOptions);
                            return true;

                        case MotionEvent.ACTION_UP:
                            postEvent(TVEVENT.E_HIDE_CONTROLER, CONTROLLER_HIDE_TIME);
                            return false;

                        default:
                            return super.onTouchEvent(event);
                    }
                }
            }
        }

        return false;
    }

    public void removeStatusBar(boolean remove){
        if (remove){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    public void setSubSurfaceVisible(boolean _onoff) {
        if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
            if (svSub != null) {
                if (_onoff) {
                    TVlog.i(TAG, "= Sub surface visible = ");
                    svSub.setVisibility(View.VISIBLE);
                    //sv.setVisibility(View.INVISIBLE);
                } else {
                    TVlog.i(TAG, "= Sub surface invisible = ");
                    svSub.setVisibility(View.INVISIBLE);
                    //sv.setVisibility(View.VISIBLE);
                }
            }
        } else {
            TVlog.e(TAG, "No AUTODETECT , but setSubSurfaceVisible call Error ");
        }
    }

    public void SetVideoScale(int _mode)
    {
        android.view.ViewGroup.LayoutParams lp = sv.getLayoutParams();
        if (SCALEMODE_16_9 == _mode ) {
            TVlog.i(TAG, "current video mode --> 16:9");
            lp.width = (int) frameWidth;
            lp.height = (int) frameWidth * 9 / 16;

            int positionY = frameHeight-  lp.height;
            if (positionY > 0) {
                sv.setX(positionY / 2);
                if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                    if (svSub != null)
                    {
                        svSub.setX(positionY / 2);
                    }
                } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                        (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                    if (svSub!= null)
                    {
                        svSub.setX(positionY / 2);
                    }
                }
            }
            currentVideoMode = SCALEMODE_16_9;
        } else if (SCALEMODE_4_3 == _mode) {

            lp.width = (int) frameHeight * 4 / 3;
            lp.height = (int) frameHeight;
            int positionX = frameWidth-  lp.width;
            if (  positionX>0) {
                sv.setX(positionX / 2);
                if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                    if (svSub != null)
                    {
                        svSub.setX(positionX / 2);
                    }
                } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                        (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                    if (svSub!= null)
                    {
                        svSub.setX(positionX / 2);
                    }
                }
            }
            TVlog.i(TAG, "current video mode --> 4:3");
            currentVideoMode = SCALEMODE_4_3;
        } else {
            TVlog.i(TAG, "current video mode--> Normal");

            lp.width = (int) frameWidth;
            lp.height = (int) frameHeight;
            sv.setX(0);
            sv.setY(0);

            if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                if (svSub != null) {
                    svSub.setX(0);
                    svSub.setY(0);
                }
            } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                    (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                            buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                            buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                if (svSub!= null) {
                    svSub.setX(0);
                    svSub.setY(0);
                }
            }

            currentVideoMode = SCALEMODE_NORMAL;
        }

        sv.setLayoutParams(lp);

        if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
            if (svSub != null)
            {
                svSub.setLayoutParams(lp);
            }
        } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
            if (svSub!= null)
            {
                svSub.setLayoutParams(lp);
            }
        }
    }

    public recordAndCapturePath getCurrentRecordingPath()
    {
        String rootPath;
        String retPath;
        File dir_exist = null;
        String path;

        if (buildOption.RECORDING_FILE_SYSTEM_MODE == 0) {
            if (buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                rootPath = getInternalSDPath() + buildOption.ROOT_RECORDED_PATH;
                TVlog.i(TAG, " >>>>> getCurrentRecordingPath() internal_path = "+rootPath);
                path = "Phone Storage";
                TVlog.i(TAG, " >>>>> root dir is => " + rootPath);
                retPath = rootPath + "/";
                File rootCheck = new File(rootPath);
                if (!rootCheck.exists()) {
                    rootCheck.mkdir();
                    TVlog.i(TAG, " >>>>> root make");
                }
            } else {
                if (SDK_INT < Build.VERSION_CODES.N) {
                    if (getExternalMounts().size() != 0) {
                        //SD card MOUNTED
                        TVlog.i(TAG, " >>>>> sdcard mounted");
                        int counter = 0;
                        for (int i = 0; i < getExternalSDPath().length(); i++) {
                            if (getExternalSDPath().charAt(i) == '/') {
                                counter++;
                            }
                        }
                        if (counter <= 2) {
                            rootPath = getExternalSDPath() + "/" + buildOption.ROOT_RECORDED_PATH;
                        } else {
                            rootPath = getExternalSDPath() + buildOption.ROOT_RECORDED_PATH;
                        }
                        TVlog.i(TAG, " >>>>> getCurrentRecordingPath() external_path = "+rootPath);
                        path = "SD";
                    } else {
                        //SD card UNUMOUNTED
                        TVlog.i(TAG, " >>>>> sdcard unmounted");
                        rootPath = getInternalSDPath() + buildOption.ROOT_RECORDED_PATH;
                        TVlog.i(TAG, " >>>>> getCurrentRecordingPath() internal_path = "+rootPath);
                        path = "Phone Storage";
                    }
                    TVlog.i(TAG, " >>>>> root dir is => " + rootPath);
                    retPath = rootPath + "/";
                    File rootCheck = new File(rootPath);
                    if (!rootCheck.exists()) {
                        rootCheck.mkdir();
                        TVlog.i(TAG, " >>>>> root make");
                    }
                } else {
                    rootPath = getInternalSDPath() + buildOption.ROOT_RECORDED_PATH;
                    TVlog.i(TAG, " >>>>> getCurrentRecordingPath() internal_path = "+rootPath);
                    path = "Phone Storage";
                    TVlog.i(TAG, " >>>>> root dir is => " + rootPath);
                    retPath = rootPath + "/";
                    File rootCheck = new File(rootPath);
                    if (!rootCheck.exists()) {
                        rootCheck.mkdir();
                        TVlog.i(TAG, " >>>>> root make");
                    }
                }
            }
        } else {
            //SD card UNMOUNTED
            TVlog.i(TAG, " >>>>> sdcard unmounted");
            rootPath = "" + Environment.getRootDirectory().getAbsolutePath() + "/" + buildOption.ROOT_RECORDED_PATH+"/";
            path = "Phone Storage";
        }
        recordAndCapturePath ret = new recordAndCapturePath(path, retPath);
        return ret;
    }

    public class recordAndCapturePath
    {
        public String pathName;
        public String fullPath;

        recordAndCapturePath(String _pathName ,String _fullPath) {

            pathName = _pathName;
            fullPath = _fullPath;
        }
    }

    public recordAndCapturePath getCurrentCapturePath() {

        String rootPath = "";
        String retPath;
        String path;

        if (buildOption.RECORDING_FILE_SYSTEM_MODE == 0) {
            if (buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                rootPath = getInternalSDPath() + buildOption.ROOT_CAPTURED_PATH;
                TVlog.i(TAG, " >>>>> getCurrentCapturePath() internal_path = "+rootPath);
                path = "Phone Storage";
                TVlog.i(TAG, " >>>>> root dir is => " + rootPath);
                retPath = rootPath + "/";
                File rootCheck = new File(rootPath);
                if (!rootCheck.exists()) {
                    rootCheck.mkdir();
                    TVlog.i(TAG, " >>>>> root make");
                }
            } else {
                if (SDK_INT < Build.VERSION_CODES.N) {
                    TVlog.i(TAG,  " ==> getExternalMounts().size() ="+getExternalMounts().size());
                    if (getExternalMounts().size() != 0) {
                        //SD card MOUNTED
                        TVlog.i(TAG, " >>>>> sdcard mounted");
                        int counter = 0;
                        for (int i = 0; i < getExternalSDPath().length(); i++) {
                            if (getExternalSDPath().charAt(i) == '/') {
                                counter++;
                            }
                        }
                        if (counter <= 2) {
                            rootPath = getExternalSDPath() + "/" + buildOption.ROOT_CAPTURED_PATH;
                        } else {
                            rootPath = getExternalSDPath() + buildOption.ROOT_CAPTURED_PATH;
                        }
                        TVlog.i(TAG, " >>>>> getCurrentCapturePath() external_path = "+rootPath);
                        path = "SD";
                    } else {
                        //SD card UNUMOUNTED
                        TVlog.i(TAG, " >>>>> sdcard unmounted");
                        rootPath = getInternalSDPath() + buildOption.ROOT_CAPTURED_PATH;
                        TVlog.i(TAG, " >>>>> getCurrentCapturePath() internal_path = "+rootPath);
                        path = "Phone Storage";
                    }
                    TVlog.i(TAG, " >>>>> root dir is => " + rootPath);
                    retPath = rootPath + "/";
                    File rootCheck = new File(rootPath);
                    if (!rootCheck.exists()) {
                        rootCheck.mkdir();
                        TVlog.i(TAG, " >>>>> root make");
                    }
                } else {
                    rootPath = getInternalSDPath() + buildOption.ROOT_CAPTURED_PATH;
                    TVlog.i(TAG, " >>>>> getCurrentCapturePath() internal_path = "+rootPath);
                    path = "Phone Storage";
                    TVlog.i(TAG, " >>>>> root dir is => " + rootPath);
                    retPath = rootPath + "/";
                    File rootCheck = new File(rootPath);
                    if (!rootCheck.exists()) {
                        rootCheck.mkdir();
                        TVlog.i(TAG, " >>>>> root make");
                    }
                }
            }
        } else {
            //SD card UNMOUNTED
            TVlog.i(TAG, " >>>>> sdcard unmounted");
            rootPath = getInternalSDPath() + "/" + buildOption.ROOT_CAPTURED_PATH;
            path = "Phone Storage";
        }
        recordAndCapturePath ret = new recordAndCapturePath(path, retPath);
        return ret;
    }

    public String getInternalSDPath() {

        String internalSDPath = "";

        if (buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
            internalSDPath = getExternalStorageDirectory()+"/";
        } else {
            if (SDK_INT < Build.VERSION_CODES.N) {
                if (getStoragePath(getApplicationContext(), false) != null) {
                    internalSDPath = getStoragePath(getApplicationContext(), false).toString()+"/";
                } else {
                    internalSDPath = getExternalStorageDirectory()+"/";
                }
            } else {
                internalSDPath = getExternalStorageDirectory()+"/";
            }
        }

        TVlog.i(TAG, " >>>>> internalSDPath = "+internalSDPath);
        return internalSDPath;
    }

    public String getExternalSDPath() {
        StorageHelper.StorageVolume storageVolume = new StorageHelper().getStorage(StorageHelper.StorageVolume.Type.EXTERNAL);
        if (storageVolume != null) {
            return storageVolume.file.getPath()+"/";
        } else {
            String externalSDPath = System.getenv("SECONDARY_STORAGE");
            if (externalSDPath == null || externalSDPath.isEmpty()) {
                externalSDPath = "/storage/extSdCard/";
            }
            return externalSDPath;
        }
    }

    boolean isCheckingPlayback()
    {
        if (buildOption.RECORDING_TYPE_TS == buildOption.RECORDING_TYPE)
        {
            if (MainActivity.isPlayBackActivity==true) {
                CommonStaticData.loadingNow=false;
                TVlog.i(TAG, " =====  PlayBackActivity  running =========");
                channelChangeStartView(true);  // justin add for start from power key or home key

                if (PlayBackActivity.getInstance() != null)
                {
                    PlayBackActivity.getInstance().Start();
                    return true;
                } else {
                    TVlog.i(TAG, " Playback running...but start fail...");
                }
            }
        }
        return false;
    }

    @Override
    protected void onDestroy(){
        TVlog.i(TAG, "==== onDestroy ======");
        if (mSysReceiver != null) {
            unregisterReceiver(mSysReceiver);
        }

        if (mScreenOff != null) {
            unregisterReceiver(mScreenOff);
        }

        // usbdongle[[
        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
            unregisterReceiver(mUsbReceiver);

            if (FloatingWindow.isFloating == false) {
                //for A33 tablet type::LME dongle can't be re-detected after normal finish()
                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                CommonStaticData.countIntro = 0;
                editor.putInt(CommonStaticData.countIntroKey, CommonStaticData.countIntro);
                editor.commit();

                android.os.Process.killProcess(android.os.Process.myPid()); // elliot
            }
        }
        // ]]usbdongle

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        TVlog.i(TAG, "==== onBackPressed======");
        InputDialog dig = new InputDialog(MainActivity.this, InputDialog.TYPE_TV_TERMINATE, null, null, null);  // live modify
    }

    public void recordingStop(boolean _isOtherEvent)
    {
        String[] filename = null;

        if (isRec)
        {
            TVlog.i(TAG, "==== recordingStop======");
            recB.setImageResource(R.drawable.rec_color_f);
            if (buildOption.GUI_STYLE == 2 || buildOption.GUI_STYLE == 3){
                rightB.setVisibility(View.VISIBLE);
                leftB.setVisibility(View.VISIBLE);
            }
            isRec = false;
            ll_recTimeview.setVisibility(View.INVISIBLE);
            recTimeview.setText("");
            FCI_TVi.RecStop(false);
            if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 )  {
                thumbNailUpdate.getThhumbNailUpdateTask().sendEvent(TVEVENT.E_UPDATE_THUMBNAIL, 1000);
            }

            instance.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.parse("file://" + recordingFileName)));

            if (mIsRecStarted)
            {
                if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 )  {
                    File chkFile = new File(recordingFileName);
                    boolean fileExist = chkFile.exists();

                    if (fileExist && (buildOption.RECORDING_TYPE == buildOption.RECORDING_TYPE_MP4)) {
                        CustomToast toast15 = new CustomToast(getApplicationContext());
                        toast15.showToast(getApplicationContext(), getApplicationContext().getString(R.string.recorded) + " : " + recordingFileName, Toast.LENGTH_SHORT);
                    }
                } else {
                    recordAndCapturePath filePath = getCurrentRecordingPath();

                    if (recordingFileName != null) {
                        filename = recordingFileName.split("/");
                        for (int i=0 ; i < filename.length; i++) {
                            TVlog.i(TAG, " >>>>> filename[] = "+filename[i]);
                        }
                    }

                    if (filePath.pathName.equalsIgnoreCase("SD")) {

                        CustomToast toast = new CustomToast(this);
                        //toast.showToast(this, this.getString(R.string.recorded) + " : " + recordingFileName, Toast.LENGTH_SHORT);
                        toast.showToast(this, this.getString(R.string.recorded_to_file) + "\n"
                                + this.getString(R.string.sd_storage_record) + "\n"
                                + filename[filename.length-1], Toast.LENGTH_LONG);
                    } else {
                        CustomToast toast = new CustomToast(this);
                        //toast.showToast(this, this.getString(R.string.recorded) + " : " + recordingFileName, Toast.LENGTH_SHORT);
                        toast.showToast(this, this.getString(R.string.recorded_to_file) + "\n"
                                + this.getString(R.string.phone_storage_record) + "\n"
                                + filename[filename.length-1], Toast.LENGTH_LONG);
                    }
                }
            } else {
                if (_isOtherEvent ==false) {
                    CustomToast toast16 = new CustomToast(getApplicationContext());
                    toast16.showToast(getApplicationContext(), getApplicationContext().getString(R.string.recorded_fail_too_shot), Toast.LENGTH_SHORT);
                }
            }
        }
    }

    public void TVTerminate()
    {
        if (CommonStaticData.settingActivityShow == true) {
            SettingActivity.getInstance().onBackPressed();
        } else if (CommonStaticData.epgActivityShow == true) {
            EPGActivity.getInstance().onBackPressed();
        } else if (CommonStaticData.recordedFileActivityShow == true) {
            RecordedFileListActivity.getInstance().onBackPressed();
        } else if (CommonStaticData.playBackActivityShow == true) {
            //PlayBackActivity.getInstance().onBackPressed();
            PlayBackActivity.getInstance().closeActivity();
        } else if (CommonStaticData.channelMainActivityShow == true) {
            ChannelMainActivity.getInstance().onBackPressed();
        } else if (CommonStaticData.openActivityShow == true) {
            OpenActivity.getInstance().onBackPressed();
        } else if (CommonStaticData.aboutActivityShow == true) {
            AboutActivity.getInstance().onBackPressed();
        }
        SolutionStop();
        TVlog.i(TAG, "==== call finish ======");
        finish();
    }

    public void SolutionStop() {
        TVlog.i(TAG, "==== SolutionStop ======");
        if (TVON==false)
        {
            TVlog.i(TAG, "==== already Stop ======");
            return;
        }
        TVBridge.stop();
        InputDialog.nosignalNotiClear();
        recordingStop(true);

        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG) {
            if (CommonStaticData.handoverMode > CommonStaticData.HANDOVER_MODE_OFF) {
                if (MainActivity.isMainActivity) {
                    ll_mainAutoSearch.setVisibility(View.INVISIBLE);
                } else if (FloatingWindow.isFloating) {
                    FloatingWindow.getInstance().ll_floatingAutoSearch.setVisibility(View.INVISIBLE);
                } else if (ChatMainActivity.isChat) {
                    ChatMainActivity.getInstance().ll_chatAutoSearch.setVisibility(View.INVISIBLE);
                }
                TVBridge.scanStop();
            }
        }

        if (CommonStaticData.scanningNow) {
            if (doScan != null) {
                if (MainActivity.isMainActivity) {
                    if (doScan != null) {
                        doScan.showProgress(0, 0, 0, doScan.SHOW_PROGRESS_CLEAR);
                    }
                } else if (FloatingWindow.isFloating) {
                    if (FloatingWindow.getInstance().doScan_floating != null) {
                        FloatingWindow.getInstance().doScan_floating.showProgress_floating(0, 0, 0, FloatingWindow.getInstance().doScan_floating.SHOW_PROGRESS_CLEAR_FLOATING);
                    }
                } else if (ChatMainActivity.isChat) {
                    if (ChatMainActivity.getInstance().doScan_chat != null) {
                        ChatMainActivity.getInstance().doScan_chat.showProgress_chat(0, 0, 0, ChatMainActivity.getInstance().doScan_chat.SHOW_PROGRESS_CLEAR_CHAT);
                    }
                }
            }
        }

        TVON = false;

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
        editor.putBoolean(CommonStaticData.mainPasswordVerifyFlagKey, CommonStaticData.mainPasswordVerifyFlag);
        //editor.putBoolean(CommonStaticData.screenBlockFlagKey, CommonStaticData.screenBlockFlag);
        CommonStaticData.returnMainFromChat = false;
        CommonStaticData.returnMainFromFloating = false;
        editor.putBoolean(CommonStaticData.returnMainFromChatKey, CommonStaticData.returnMainFromChat);
        editor.putBoolean(CommonStaticData.returnMainFromFloatingKey, CommonStaticData.returnMainFromFloating);
        editor.putInt(CommonStaticData.currentScaleModeKey, CommonStaticData.currentScaleMode);
        //
        editor.commit();

        if (audioOut != null) {
            audioOut.audioModeReturn();
        }

        if ((buildOption.LOG_CAPTURE_MODE ==1) ||  (dumpTVLog ==true))
        {
            TVlog.i(TAG, "==== LOG_CAPTURE_MODE  OFF ======");
            if (processLog != null)
            {
                processLog.destroy();
                processLog=null;
            }
        }
        FCI_TVi.deInit();

        if (buildOption.CUSTOMER.contains("NEXELL")) {
            propSet("deinterlace.mode", "0");
        }

        //usbdongle[[
        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
            closeUsbDevice();
        }
        //]]usbdongle
        TVlog.i(TAG, "==== SolutionStop end ======");
    }

    public static boolean getTVon()
    {
        return TVON;
    }

    @Override
    protected void onResume() {
        // send broadcast to make music paused
        sendBroadcast(new Intent("com.android.music.musicservicecommand.pause"));

        /*
        if (isWarmReset && CommonStaticData.scanCHnum < 1) {
            sendEvent(TVEVENT.E_SCAN_COMPLETED);
            isWarmReset = false;
        }*/

        isMainActivity = true;
        FloatingWindow.isFloating = false;
        ChatMainActivity.isChat = false;
        CommonStaticData.isBadSignalFlag = false;
        // live add
        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE || buildOption.RECORD_FUNCTION_USE == false) {
            recB.setVisibility(View.GONE);
            recB.setEnabled(false);
        } else {
            recB.setVisibility(View.VISIBLE);
            recB.setEnabled(true);
        }
        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE || buildOption.RECORD_FUNCTION_USE == false) {
            recfileB.setVisibility(View.GONE);
            recfileB.setEnabled(false);
        } else {
            recfileB.setVisibility(View.VISIBLE);
            recfileB.setEnabled(true);
        }
        //

        if ((mUsbChipType != USB_CHIP_TYPE_NONE) && (CommonStaticData.scanCHnum < 1)){
            isBBFail = false;
            sendEvent(TVEVENT.E_SCAN_COMPLETED);
        }
        TVlog.i(TAG, " ==> CommonStaticData.returnMainFromChat = "+CommonStaticData.returnMainFromChat);

        if (CommonStaticData.returnMainFromChat) {
            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                postEvent(TVEVENT.E_SIGNAL_MONITER, SIGNAL_MONITER_TIME_USB);
            } else {
                postEvent(TVEVENT.E_SIGNAL_MONITER, SIGNAL_MONITER_TIME);
            }
            sendEvent(TVEVENT.E_CHANNEL_NAME_UPDATE);
            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                if (CommonStaticData.encryptFlag) {
                    //changeChannelView.setVisibility(View.INVISIBLE);
                    //ll_scramble_msg.setVisibility(View.VISIBLE);
                }
            } else {
                if (CommonStaticData.encryptFlag) {
                    changeChannelView.setVisibility(View.INVISIBLE);
                    ll_scramble_msg.setVisibility(View.VISIBLE);
                }
            }

            TVlog.i(TAG, " ==> CommonStaticData.ageLimitFlag = "+CommonStaticData.ageLimitFlag);

        } else if (CommonStaticData.returnMainFromFloating) {
            //mCursor = getmCursor_floating();
            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                postEvent(TVEVENT.E_SIGNAL_MONITER, SIGNAL_MONITER_TIME_USB);
            } else {
                postEvent(TVEVENT.E_SIGNAL_MONITER, SIGNAL_MONITER_TIME);
            }
            sendEvent(TVEVENT.E_CHANNEL_NAME_UPDATE);
            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                if (CommonStaticData.encryptFlag) {
                    //changeChannelView.setVisibility(View.INVISIBLE);
                    //ll_scramble_msg.setVisibility(View.VISIBLE);
                }
            } else {
                if (CommonStaticData.encryptFlag) {
                    changeChannelView.setVisibility(View.INVISIBLE);
                    ll_scramble_msg.setVisibility(View.VISIBLE);
                }
            }

            TVlog.i(TAG, " ==> CommonStaticData.ageLimitFlag = "+CommonStaticData.ageLimitFlag);
        }

        // usbdongle[[
        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
            TVlog.i("FCIISDBT::", "onResume() call seupUsbDevice() 1111");
            mPermissionRequested = 0;
            setupUsbDevice();
            TVlog.i("FCIISDBT::", "onResume() call seupUsbDevice() 2222");
            if (mUsbFd == -1) {
                TVlog.i("FCIISDBT::", "usb dongle closed ! (by device not attached)");
                // live chage toast message to dialog
                CustomToast toast = new CustomToast(getApplicationContext());
                toast.showToast(getApplicationContext(), getApplicationContext().getString(R.string.usb_dongle_not_attached), Toast.LENGTH_LONG);
                ll_noChannel.setVisibility(View.INVISIBLE);
                ll_noSignal.setVisibility(View.INVISIBLE);
                ll_audioOnlyChannel.setVisibility(View.INVISIBLE);
                ll_file_play_mode_usb.setVisibility(View.VISIBLE);
                //
            }
            else {
                UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
                if (manager != null && mUsbDevice != null) {
                    if (manager.hasPermission(mUsbDevice) != true) {
                        TVlog.i("FCIISDBT::", "usb dongle closed ! (by permission not granted)");
                        CustomToast toast = new CustomToast(getApplicationContext());
                        toast.showToast(getApplicationContext(), getApplicationContext().getString(R.string.usb_permission_not_granted), Toast.LENGTH_LONG);
                    }
                    else {
                        openUsbDevice(mUsbDevice);
                        if (bb_fail_dialog != null) {
                            if (bb_fail_dialog.getView().getVisibility() == View.VISIBLE) {
                                bb_fail_dialog.dismiss();
                            }
                        }
                        if (ll_file_play_mode_usb.getVisibility() == View.VISIBLE) {
                            ll_file_play_mode_usb.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
        }
//device init failed[[
        if (FCI_TVi.initiatedSol == false &&
                buildOption.FCI_SOLUTION_MODE != buildOption.JAPAN_FILE &&
                buildOption.FCI_SOLUTION_MODE != buildOption.BRAZIL_FILE &&
                buildOption.FCI_SOLUTION_MODE != buildOption.PHILIPPINES_FILE) {

            FCI_TVi.AVStop();
            FCI_TVi.deInit();

            TVlog.i(TAG, "onResume():: solution to be re-initialed...");

            if (buildOption.GUI_STYLE == 0 || buildOption.GUI_STYLE == 1 )  {
                FCI_TVi.init(this.getPackageName(), null, mUsbFd, mUsbDeviceName, getVersionForDongle());
            }
            else {
                recordAndCapturePath FixedRecPath = getCurrentRecordingPath();
                FCI_TVi.init(this.getPackageName(),FixedRecPath.fullPath, mUsbFd, mUsbDeviceName, getVersionForDongle());
            }
        }

        // power button
        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
            //  CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
            //  CommonStaticData.countIntro = CommonStaticData.settings.getInt(CommonStaticData.countIntroKey, 0);
            //  SharedPreferences.Editor editor = CommonStaticData.settings.edit();
            if (CommonStaticData.countIntro == 2) {
                CommonStaticData.countIntro = 3;
                //    editor.putInt(CommonStaticData.countIntroKey, CommonStaticData.countIntro);
                //   editor.commit();
                //TVlog.i(TAG, "elliot 2222: "+CommonStaticData.countIntro);
            }
            else {
                //TVlog.i(TAG, "elliot 2223: "+CommonStaticData.countIntro);
            }
        }
        // ]]usbdongle

        int player_bright = CommonStaticData.brightness;
        //WindowManager.LayoutParams lp = mWindow.getAttributes();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = player_bright;
        getWindow().setAttributes(lp);

        TVlog.i(TAG, "== onResume ==");
        if (changeChannelView != null) {
            if (channelChangeProcLocation != null) {
                changeChannelView.setX(channelChangeProcLocation[0]);
                changeChannelView.setY(channelChangeProcLocation[1]);
            }
        }

        /*
        if (ll_scramble_msg.getVisibility() == View.VISIBLE || noSignal.getVisibility() == View.VISIBLE) {
            ll_multiWindow.setVisibility(View.GONE);
        } else {
            ll_multiWindow.setVisibility(View.VISIBLE);
        }*/

        /*
        if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_SWCODEC) {
            if (buildOption.USE_MULTI_WINDOW) {
                if (changeChannelView.getVisibility() == View.VISIBLE) {
                        ll_multiWindow.setVisibility(View.GONE);
                    } else if (ll_scramble_msg.getVisibility() == View.VISIBLE) {
                        ll_multiWindow.setVisibility(View.GONE);
                } else {
                    ll_multiWindow.setVisibility(View.VISIBLE);
                }
            }
        }*/

        View decorView = getWindow().getDecorView();
//      int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE;
        uiOptions =  View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE;    // justin
        //TVlog.i("decorView", "onResume uiOptions = "+ uiOptions);
        decorView.setSystemUiVisibility(uiOptions);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        TVlog.i(TAG, "== setSystemUiVisibility onResume ==");
//        }
        super.onResume();
    }

    @Override
    protected void onPause() {

        //isMainActivity = false;
        TVlog.i(TAG, "== onPause ==");
        removeEvent(TVEVENT.E_HIDE_CONTROLER);
        removeEvent(TVEVENT.E_HIDE_GESTURE);
        controllerLayout.setVisibility(View.INVISIBLE);
        if (isUiLocked) {
            ll_uiLocked.setVisibility(View.INVISIBLE);
        }

        // live add
        /*
        if (ll_multiWindow.getVisibility() == View.VISIBLE) {
            ll_multiWindow.setVisibility(View.GONE);
        }*/
        //if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_SWCODEC) {
        if (buildOption.USE_MULTI_WINDOW) {
            ll_multiWindow.setVisibility(View.GONE);
        }
        //}

        //ll_chat.setVisibility(View.INVISIBLE);
        if (buildOption.USE_CHAT_FUNCTION) {
            if (ll_chat.getVisibility() == View.VISIBLE) {
                ll_chat.setVisibility(View.GONE);
            }
        }

        if (volumebarLayout.getVisibility() == View.VISIBLE) {
            volumebarLayout.setVisibility(View.INVISIBLE);
        }
        if (brightbarLayout.getVisibility() == View.VISIBLE) {
            brightbarLayout.setVisibility(View.INVISIBLE);
        }

        if (buildOption.ADD_LOUD_SPEAKER) {
            if (is_wired_headset) {
                earphoneLayout.setVisibility(View.VISIBLE);
            } else {
                earphoneLayout.setVisibility(View.INVISIBLE);
            }
        }

        if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 ) {
            channelLayout.setVisibility(View.INVISIBLE);
        }
        //if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 2 || buildOption.GUI_STYLE == 3) {
        status_bar.setVisibility(View.INVISIBLE);
        //}
        CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = CommonStaticData.settings.edit();
        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
            //   CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
            //   CommonStaticData.countIntro = CommonStaticData.settings.getInt(CommonStaticData.countIntroKey, 0);
            if (CommonStaticData.countIntro == 1) {
                CommonStaticData.countIntro = 2;
                //TVlog.i(TAG, "elliot 1111: "+CommonStaticData.countIntro);
            }
            else {
                CommonStaticData.countIntro = 3;
                //TVlog.i(TAG, "elliot 1112: "+CommonStaticData.countIntro);
            }
            editor.putInt(CommonStaticData.countIntroKey, CommonStaticData.countIntro);
            editor.commit();
        }
        //editor.putBoolean(CommonStaticData.parentalcontrolSwitchKey, CommonStaticData.ratingsetSwitch);
        editor.putBoolean(CommonStaticData.passwordVerifyFlagKey, CommonStaticData.passwordVerifyFlag);
        editor.putBoolean(CommonStaticData.mainPasswordVerifyFlagKey, CommonStaticData.mainPasswordVerifyFlag);
        editor.putBoolean(CommonStaticData.ageLimitFlagKey, CommonStaticData.ageLimitFlag);
        editor.putInt(CommonStaticData.receivemodeSwitchKey, CommonStaticData.receivemode);
        editor.commit();
        removeStatusBar(true);
        super.onPause();
    }

    public class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch(state){
                case TelephonyManager.CALL_STATE_IDLE:
                    TVlog.i("MyPhoneStateListener", " >>>>> CALL_STATE_IDLE "+state);
                    if (TVON == false) {
                        // usbdongle[[
                        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                            TVlog.i("FCIISDBT::", "onResume() call seupUsbDevice() 1111");
                            mPermissionRequested = 0;
                            setupUsbDevice();
                            TVlog.i("FCIISDBT::", "onResume() call seupUsbDevice() 2222");
                            if (mUsbFd == -1) {
                                TVlog.i("FCIISDBT::", "usb dongle closed ! (by device not attached)");
                                //viewToastMSG("USB device not attached & TV close...", true);
//device init failed[[

                                // live chage toast message to dialog
                                CustomToast toast = new CustomToast(getApplicationContext());
                                toast.showToast(getApplicationContext(), getApplicationContext().getString(R.string.usb_dongle_not_attached), Toast.LENGTH_LONG);
                                ll_noChannel.setVisibility(View.INVISIBLE);
                                ll_noSignal.setVisibility(View.INVISIBLE);
                                ll_audioOnlyChannel.setVisibility(View.INVISIBLE);
                                ll_file_play_mode_usb.setVisibility(View.VISIBLE);
                                //
//]]device init failed
                            }
                            else {
                                UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
                                if (manager != null && mUsbDevice != null) {
                                    if (manager.hasPermission(mUsbDevice) != true) {
                                        TVlog.i("FCIISDBT::", "usb dongle closed ! (by permission not granted)");
//device init failed[[
                                        CustomToast toast = new CustomToast(getApplicationContext());
                                        toast.showToast(getApplicationContext(), getApplicationContext().getString(R.string.usb_permission_not_granted), Toast.LENGTH_LONG);
//]]device init failed
                                    } else {
                                        openUsbDevice(mUsbDevice);
                                        if (bb_fail_dialog != null) {
                                            if (bb_fail_dialog.getView().getVisibility() == View.VISIBLE) {
                                                bb_fail_dialog.dismiss();
                                            }
                                        }
                                        if (ll_file_play_mode_usb.getVisibility() == View.VISIBLE) {
                                            ll_file_play_mode_usb.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                }
                            }
                        }
                        if (!CommonStaticData.ageLimitFlag) {
                            MainActivity.isMainActivity = true;
                            onStart_TV();
                        }
                    }
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    TVlog.i("MyPhoneStateListener", " >>>>> CALL_STATE_RINGING "+state);
                    FCI_TVi.setVolume(0.0f);
                    SolutionStop();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //TVlog.i("MyPhoneStateListener", " >>>>> CALL_STATE_OFFHOOK "+state);
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    //usbdongle[[
    UsbDevice mUsbDevice;
    UsbDeviceConnection mUsbConnection;
    UsbInterface mUsbInterface;
    String mUsbDeviceName;
    int mUsbFd = -1;
    int mPermissionRequested = 0;
    public final int USB_CHIP_TYPE_NONE = 0;
    public final int USB_CHIP_TYPE_NXP = 1;
    public final int USB_CHIP_TYPE_LME = 2;
    public final int USB_CHIP_TYPE_ITE = 3;
    public final int USB_NXP_VENDOR_ID = 7510;
    public final int USB_NXP_PRODUCT_ID = 24896;
    public final int USB_LME_MODE_NONE = 0;
    public final int USB_LME_MODE_COLD = 1;
    public final int USB_LME_MODE_WARM = 2;
    public final int USB_LME_VENDOR_ID = 13124;
    public final int USB_LME_COLD_PRODUCT_ID = 4384;
    public final int USB_LME_WARM_FULLSEG_PRODUCT_ID = 9344;
    public final int USB_ITE_VENDOR_ID = 1165;
    public final int USB_ITE_PRODUCT_ID = 37638;
    int mUsbChipType = USB_CHIP_TYPE_NONE;
    int mUsbLMEMode = USB_LME_MODE_NONE;
    boolean mUsbConnected = false;
    PendingIntent mPermissionIntent = null;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";


    private String F_GetDeviceName() {
        return mUsbDeviceName;
    }

    private int F_GetDeviceFD() {
        int fd = -1;
        UsbManager manager = (UsbManager)getSystemService(Context.USB_SERVICE);
        UsbDevice foundDevice = null;
        mUsbDeviceName = null;

        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        String usbDeviceString = deviceList.toString();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        TVlog.i("FCIISDBT::", "usb dev list: " + usbDeviceString);

        while (deviceIterator.hasNext()) {
            UsbDevice device = (UsbDevice)deviceIterator.next();
            TVlog.i("FCIISDBT:: name=", device.getDeviceName() + ", vendor=" + Integer.toHexString(device.getVendorId()) + ", product=" + Integer.toHexString(device.getProductId()));
            if (device.getVendorId() == USB_LME_VENDOR_ID) {
                foundDevice = device;
                break;
            }
        }

        int checkCount = 0;
        final int CHECK_USB_SYNC_TIME_INTERVAL = 9001; // about 4 seconds

        if (foundDevice != null) {
/*
        if (!manager.hasPermission(foundDevice)) {
            manager.requestPermission(foundDevice, mPermissionIntent);
            TVlog.i("FCIISDBT::", "usb permission requested!");

            while (!manager.hasPermission(foundDevice)) {
                synchronized (manager) {
                    if (checkCount++ < 3000) {
                        TVlog.d("FCIISDBT::", "usb permission requested (checkCount=" + checkCount + ")");
                    }
                }
            }
        }
*/
            if (!manager.hasPermission(foundDevice)) {
                while (!manager.hasPermission(foundDevice)) {
                    synchronized (manager) {
                        if (checkCount++ % CHECK_USB_SYNC_TIME_INTERVAL == 0) { // debug message
                            TVlog.i("FCIISDBT::", "usb permission checking...");
                        }
                        if (checkCount == CHECK_USB_SYNC_TIME_INTERVAL*2 && mPermissionRequested == 0) { // permission request
                            manager.requestPermission(foundDevice, mPermissionIntent);
                            mPermissionRequested = 2;
                            TVlog.i("FCIISDBT::", "usb permission requested");
                        }
                    }
                    if (checkCount > CHECK_USB_SYNC_TIME_INTERVAL*5 && mPermissionRequested == 2) { // time out : abnormal
                        TVlog.i("FCIISDBT::", "usb permission checking...time out: abnormal");
                        break;
                    }
                    if (checkCount > CHECK_USB_SYNC_TIME_INTERVAL*8 && mPermissionRequested == 1) { // time out : normal
                        TVlog.i("FCIISDBT::", "usb permission checking...time out: normal");
                        break;
                    }
                }
            }

            if (manager.hasPermission(foundDevice)) {
                UsbDeviceConnection connection = manager.openDevice(foundDevice);
                if (connection != null) {
                    fd = connection.getFileDescriptor();
                    mUsbDevice = foundDevice;
                    mUsbDeviceName = foundDevice.getDeviceName();
                    mUsbConnection = connection;
                    mUsbInterface = foundDevice.getInterface(0);
                    TVlog.i("FCIISDBT::", "UsbManager openDevice OK!");
                } else {
                    TVlog.e("FCIISDBT::", "UsbManager openDevice failed!");
                }
            } else {
                TVlog.e("FCIISDBT::", "UsbManager permission denied!");
            }
        } else {
            TVlog.e("FCIISDBT::", "UsbManager can't get valid device");
        }

        TVlog.i("FCIISDBT::", "DTV Device prob " + fd);
        return fd;
    }

    private int LME_OpenUSBDongle(int nStandard) {
        int fd = F_GetDeviceFD();
        String devName = F_GetDeviceName();
        int productId = 0;
        if (mUsbDevice != null) {
            productId = mUsbDevice.getProductId();
        }
        if (fd > 0) {
            if (mPermissionRequested == 0) {
                mPermissionRequested = 1;
            }
            mUsbConnected = true;
            if (productId == USB_LME_COLD_PRODUCT_ID) {
                TVlog.i("FCIISDBT::", "Detect LME USB Colde Mode.");
                mylme_sdk.probdevice(fd, nStandard);
                mylme_sdk.deletedevice(fd);
                mUsbLMEMode = USB_LME_MODE_COLD;
                mUsbConnected = false;
            } else if (productId == USB_LME_WARM_FULLSEG_PRODUCT_ID) {
                TVlog.i("FCIISDBT::", "Detect LME USB Warm Mode.");
                mUsbFd = fd;
                mUsbDeviceName = devName;
                mUsbLMEMode = USB_LME_MODE_WARM;
            } else {
                TVlog.e("FCIISDBT::", "Detect LME USB: not supported product id = "+productId);
                mUsbFd = fd;
                mUsbDeviceName = devName;
                //mUsbLMEMode = USB_LME_MODE_WARM;
                mUsbLMEMode = USB_LME_MODE_COLD;
            }
        } else {
            mUsbConnected = false;
            mUsbLMEMode = USB_LME_MODE_NONE;
        }
        return mUsbLMEMode;
    }

    private void setupUsbDevice() {
        int checkCount = 0;
        UsbManager manager = (UsbManager)getSystemService(Context.USB_SERVICE);
        UsbDevice foundDevice = null;

        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();        // load Device List

        if (mUsbConnected) {
            TVlog.i("FCIISDBT::", "usb dongle already connected");
            return;
        }
        if (deviceList == null) {
            Toast.makeText(getApplicationContext(), "No devices detected!", Toast.LENGTH_SHORT).show();
            TVlog.i("FCIISDBT::", "No list of usb devices!");
            mUsbDevice = null;
            mUsbDeviceName = null;
            mUsbFd = -1;
            mUsbChipType = USB_CHIP_TYPE_NONE;
            return;
        }
        String usbDeviceString = deviceList.toString();
        mUsbChipType = USB_CHIP_TYPE_NONE;
        TVlog.i("FCIISDBT::", "usb dev list: " + usbDeviceString);
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
            UsbDevice dev = deviceIterator.next();
            int vendorId = 0;
            int productId = 0;
            if (dev != null) {
                vendorId = dev.getVendorId();
                productId = dev.getProductId();
                if (vendorId == USB_NXP_VENDOR_ID && productId == USB_NXP_PRODUCT_ID) {
                    TVlog.i("FCIISDBT::", "FCI TV dongle(NXP) found");
                    mUsbChipType = USB_CHIP_TYPE_NXP;
                    withoutUSB = false;
                    foundDevice = dev;
                    TVlog.i("FCIISDBT::", "NXP: vendorId="+vendorId+", productId="+productId);
                    break;
                } else if (vendorId == USB_ITE_VENDOR_ID && productId == USB_ITE_PRODUCT_ID) {
                    TVlog.i("FCIISDBT::", "FCI TV dongle(ITE) found");
                    mUsbChipType = USB_CHIP_TYPE_ITE;
                    withoutUSB = false;
                    foundDevice = dev;
                    TVlog.i("FCIISDBT::", "ITE: vendorId="+vendorId+", productId="+productId);
                    break;
                } else if (vendorId == USB_LME_VENDOR_ID) {
                    TVlog.i("FCIISDBT::", "FCI TV dongle(LME) found!");
                    mUsbChipType = USB_CHIP_TYPE_LME;
                    withoutUSB = false;
                    foundDevice = dev;
                    TVlog.i("FCIISDBT::", "LME: vendorId="+vendorId+", productId="+productId);
                    break;
                }
            }
        }

        if (foundDevice == null) {
            TVlog.i("FCIISDBT::", "No device detected!");
            mUsbFd = -1;
            mUsbDeviceName = null;
            mUsbDevice = null;
            return;
        }

        checkCount = 0;

        if (mUsbChipType == USB_CHIP_TYPE_NXP || mUsbChipType == USB_CHIP_TYPE_ITE) {
            while (!manager.hasPermission(foundDevice)) {
                synchronized (manager) {
                    if (checkCount++ < 3000) {
                        manager.requestPermission(foundDevice, mPermissionIntent);
                        TVlog.d("FCIISDBT::", "usb permission requested (checkCount=" + checkCount + ")");
                    }
                }
            }

            if (manager.hasPermission(foundDevice)) {
                openUsbDevice(foundDevice);
            }
        } else if (mUsbChipType == USB_CHIP_TYPE_LME) {
            openUsbDevice(foundDevice);
        }
    }

    private void openUsbDevice(UsbDevice device) {

        UsbManager manager = (UsbManager)getSystemService(Context.USB_SERVICE);

        if (manager == null) {
            TVlog.e("FCIISDBT::", "can't get usbmanager handle frome system");
            return;
        }
        if (mUsbConnected) {
            TVlog.i("FCIISDBT::", "usb device already connected");
            return;
        }
        mUsbDevice = device;

        if (mUsbChipType == USB_CHIP_TYPE_NXP || mUsbChipType == USB_CHIP_TYPE_ITE) {
            mUsbInterface = device.getInterface(0);
            mUsbDeviceName = device.getDeviceName();
            TVlog.i("FCIISDBT::", "usb device name: " + mUsbDeviceName);
            if (mUsbChipType == USB_CHIP_TYPE_NXP) {
                mUsbDeviceName = "nxp" + mUsbDeviceName;
                TVlog.i("FCIISDBT::", "usb device name: " + mUsbDeviceName);
            } else if (mUsbChipType == USB_CHIP_TYPE_ITE) {
                mUsbDeviceName = "ite" + mUsbDeviceName;
                TVlog.i("FCIISDBT::", "usb device name: " + mUsbDeviceName);
            }
            mUsbConnection = manager.openDevice(device);
            mUsbConnection.claimInterface(mUsbInterface, true);

            if (mUsbConnection != null) {
                mUsbFd = mUsbConnection.getFileDescriptor();
                if (mUsbFd >= 0) {
                    mUsbConnected = true;
                    TVlog.i("FCIISDBT::", "usb device opened and connected. fd: " + mUsbFd);
                }
            } else {
                TVlog.i("FCIISDBT::", "usb device opened but not connected!");
                mUsbConnected = false;
            }
        }
        else if (mUsbChipType == USB_CHIP_TYPE_LME) {
            while (LME_OpenUSBDongle(0) == USB_LME_MODE_COLD) {
                TVlog.i("FCIISDBT::", "setupUsbDevice() ...LME dongle process");
                try {
                    Thread.sleep(3000L);
                } catch (InterruptedException var5) {
                    var5.printStackTrace();
                }
            }
        }
    }

    private void closeUsbDevice() {
        if (mUsbConnection != null) {
            if (mUsbChipType == USB_CHIP_TYPE_NXP || mUsbChipType == USB_CHIP_TYPE_ITE) {
                mUsbConnection.releaseInterface(mUsbInterface);
            } else if (mUsbChipType == USB_CHIP_TYPE_LME) {
                mylme_sdk.deletedevice(0);
            }
            mUsbConnection.close();
        }
        mUsbConnected = false;
        mUsbInterface = null;
        mUsbConnection = null;
        mUsbDevice = null;
    }

    private int getVersionForDongle() {
        boolean version = false;
        String v = Build.VERSION.RELEASE;
        String v1 = v.substring(0, 1);
        String v2 = v.substring(2, 3);
        int version1 = Integer.parseInt(v1) * 10 + Integer.parseInt(v2);
        TVlog.i(TAG, "Android Version: " + version1);
        return version1;
    }
    // ]]usbdongle

    // @Override
    public void onStart_TV() {

        TVlog.DeviceInfo(instance);

        if ((buildOption.LOG_CAPTURE_MODE ==1) || (dumpTVLog==true))
        {

            TVlog.i(TAG, " ===========================");
            TVlog.i(TAG, " LOG_CAPTURE_MODE ON");
            TVlog.i(TAG, " ===========================");
            if (isStoragePermissionGranted() == true) {  // for Android M Permission
                saveLogcatToFile(this);
            } else {
                CustomToast toast = new CustomToast(getApplicationContext());
                toast.showToast(getApplicationContext(), "Permission is needed to save FCI_TV_log!", Toast.LENGTH_SHORT);
            }
        }
        TVlog.i(TAG, " ===========================");
        TVlog.i(TAG, " Building date  = "+ buildInformation.RELEASE_DATE);
        TVlog.i(TAG, " TV App version  = "+ buildInformation.RELEASE_VERSION);
        TVlog.i(TAG, " Constructor  = "+ buildInformation.RELEASE_CONSTRUCTOR);
        TVlog.i(TAG, " Solution version  = "+ buildInformation.RELEASE_SOL_VERSION);
        TVlog.i(TAG, " Customer  = "+ cReleaseOption.CUSTOMER);
        TVlog.i(TAG, " ===========================");

        TVlog.i(TAG, "==== onStart ======");

        // [[ eddy 160706
        if (TVON==true)
        {
            TVlog.i(TAG, "==== already init  ======");
            return;
        }
        // ]] eddy 160706
        TVON = true;

        checkOnStart();

        if (buildOption.CUSTOMER.contains("NEXELL")) {
            propSet("deinterlace.mode", "10");
        }

/*
        isLoading = true;
*/
        //start loading channel DB
        CommonStaticData.loadingNow = true;
        TVBridge.setContext(this);

        TVlog.i(TAG, " ==> onStart_TV() CommonStaticData.isChat = "+ChatMainActivity.isChat);

        if (ChatMainActivity.isChat) {
            // usbdongle[[
            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                    buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB ||
                    buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB ||
                    buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                TVlog.i("ChatMainActivity::", "onResume() call seupUsbDevice() 1111");
                mPermissionRequested = 0;
                setupUsbDevice();
                TVlog.i("ChatMainActivity::", "onResume() call seupUsbDevice() 2222");
                if (mUsbFd == -1) {
                    TVlog.i("ChatMainActivity::", "usb dongle closed ! (by device not attached)");
                    //viewToastMSG("USB device not attached & TV close...", true);
                    CustomToast toast = new CustomToast(getApplicationContext());
                    toast.showToast(getApplicationContext(), "USB dongle was not attached.\nTV app was terminated.", Toast.LENGTH_LONG);
                    finish();
                } else {
                    UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
                    if (manager != null && mUsbDevice != null) {
                        if (manager.hasPermission(mUsbDevice) != true) {
                            TVlog.i("ChatMainActivity::", "usb dongle closed ! (by permission not granted)");
                            //viewToastMSG("USB permission not granted & TV close...", true);
                            CustomToast toast = new CustomToast(getApplicationContext());
                            toast.showToast(getApplicationContext(), "USB permission was not granted.\nTV app was terminated.", Toast.LENGTH_LONG);
                            finish();
                        }
                        else {
                            openUsbDevice(mUsbDevice);
                        }
                    }
                }
            }
            // ]]usbdongle
        }

        if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1) {
            if (FCI_TVi.init(this.getPackageName(), null, mUsbFd, mUsbDeviceName, getVersionForDongle()) != 0) {
                isBBFail = true;
                TVlog.i("FCIISDBT::", "usb fd="+mUsbFd+", usb dev="+mUsbDeviceName);
                if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                    bb_fail_dialog = new MaterialDialog.Builder(MainActivity.this)
                            .theme(Theme.LIGHT)
                            .iconRes(R.drawable.ic_info_outline_gray_48dp)
                            .title(R.string.alert)
                            .titleColor(getResources().getColor(R.color.black))
                            .content(R.string.usb_dongle_not_attach_msg)
                            .negativeText(R.string.continue_msg)
                            .negativeColor(getResources().getColor(R.color.blue3))
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    bb_fail_dialog.dismiss();
                                    if (ll_noChannel.getVisibility() == View.VISIBLE) {
                                        ll_noChannel.setVisibility(View.INVISIBLE);
                                    }
                                    if (ll_noSignal.getVisibility() == View.VISIBLE) {
                                        ll_noSignal.setVisibility(View.INVISIBLE);
                                    }
                                    if (ll_audioOnlyChannel.getVisibility() == View.VISIBLE) {
                                        ll_audioOnlyChannel.setVisibility(View.INVISIBLE);
                                    }
                                    if (ll_file_play_mode_usb.getVisibility() == View.INVISIBLE) {
                                        ll_file_play_mode_usb.setVisibility(View.VISIBLE);
                                    }
                                    // isWarmReset = true;
                                    withoutUSB = true;
                                }
                            })
                            .positiveText(R.string.exit)
                            .positiveColor(getResources().getColor(R.color.blue3))
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    sendEvent(TVEVENT.E_TERMINATE);
                                }
                            })
                            .build();
                    bb_fail_dialog.getWindow().setGravity(Gravity.CENTER);
                    bb_fail_dialog.show();
                    bb_fail_dialog.setCanceledOnTouchOutside(false);
                } else {
                    bb_fail_dialog = new MaterialDialog.Builder(MainActivity.this)
                            .theme(Theme.LIGHT)
                            .iconRes(R.drawable.ic_info_outline_gray_48dp)
                            .title(R.string.alert)
                            .titleColor(getResources().getColor(R.color.black))
                            .content(R.string.bb_init_fail_msg)
                            .negativeText(R.string.continue_msg)
                            .negativeColor(getResources().getColor(R.color.blue3))
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    bb_fail_dialog.dismiss();
                                    if (ll_noChannel.getVisibility() == View.VISIBLE) {
                                        ll_noChannel.setVisibility(View.INVISIBLE);
                                    }
                                    if (ll_noSignal.getVisibility() == View.VISIBLE) {
                                        ll_noSignal.setVisibility(View.INVISIBLE);
                                    }
                                    if (ll_audioOnlyChannel.getVisibility() == View.VISIBLE) {
                                        ll_audioOnlyChannel.setVisibility(View.INVISIBLE);
                                    }
                                    if (ll_file_play_mode.getVisibility() == View.INVISIBLE) {
                                        ll_file_play_mode.setVisibility(View.VISIBLE);
                                    }
                                }
                            })
                            .positiveText(R.string.exit)
                            .positiveColor(getResources().getColor(R.color.blue3))
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    sendEvent(TVEVENT.E_TERMINATE);
                                }
                            })
                            .build();
                    bb_fail_dialog.getWindow().setGravity(Gravity.CENTER);
                    bb_fail_dialog.show();
                    bb_fail_dialog.setCanceledOnTouchOutside(false);
                }
            }
        } else {
            getCurrentCapturePath();
            recordAndCapturePath FixedRecPath=  getCurrentRecordingPath();

            if (FCI_TVi.init(this.getPackageName(),FixedRecPath.fullPath, mUsbFd, mUsbDeviceName, getVersionForDongle()) != 0) {
                isBBFail = true;
                if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                    bb_fail_dialog = new MaterialDialog.Builder(MainActivity.this)
                            .theme(Theme.LIGHT)
                            .iconRes(R.drawable.ic_info_outline_gray_48dp)
                            .title(R.string.alert)
                            .titleColor(getResources().getColor(R.color.black))
                            .content(R.string.usb_dongle_not_attach_msg)
                            .negativeText(R.string.continue_msg)
                            .negativeColor(getResources().getColor(R.color.blue3))
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    bb_fail_dialog.dismiss();
                                    if (ll_noChannel.getVisibility() == View.VISIBLE) {
                                        ll_noChannel.setVisibility(View.INVISIBLE);
                                    }
                                    if (ll_noSignal.getVisibility() == View.VISIBLE) {
                                        ll_noSignal.setVisibility(View.INVISIBLE);
                                    }
                                    if (ll_audioOnlyChannel.getVisibility() == View.VISIBLE) {
                                        ll_audioOnlyChannel.setVisibility(View.INVISIBLE);
                                    }
                                    if (ll_file_play_mode_usb.getVisibility() == View.INVISIBLE) {
                                        ll_file_play_mode_usb.setVisibility(View.VISIBLE);
                                    }
                                    //   isWarmReset = true;
                                    withoutUSB = true;
                                }
                            })
                            .positiveText(R.string.exit)
                            .positiveColor(getResources().getColor(R.color.blue3))
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    sendEvent(TVEVENT.E_TERMINATE);
                                }
                            })
                            .build();
                    bb_fail_dialog.getWindow().setGravity(Gravity.CENTER);
                    bb_fail_dialog.show();
                    bb_fail_dialog.setCanceledOnTouchOutside(false);
                } else {
                    bb_fail_dialog = new MaterialDialog.Builder(MainActivity.this)
                            .theme(Theme.LIGHT)
                            .iconRes(R.drawable.ic_info_outline_gray_48dp)
                            .title(R.string.alert)
                            .titleColor(getResources().getColor(R.color.black))
                            .content(R.string.bb_init_fail_msg)
                            .negativeText(R.string.continue_msg)
                            .negativeColor(getResources().getColor(R.color.blue3))
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    bb_fail_dialog.dismiss();
                                    if (ll_noChannel.getVisibility() == View.VISIBLE) {
                                        ll_noChannel.setVisibility(View.INVISIBLE);
                                    }
                                    if (ll_noSignal.getVisibility() == View.VISIBLE) {
                                        ll_noSignal.setVisibility(View.INVISIBLE);
                                    }
                                    if (ll_audioOnlyChannel.getVisibility() == View.VISIBLE) {
                                        ll_audioOnlyChannel.setVisibility(View.INVISIBLE);
                                    }
                                    if (ll_file_play_mode.getVisibility() == View.INVISIBLE) {
                                        ll_file_play_mode.setVisibility(View.VISIBLE);
                                    }
                                }
                            })
                            .positiveText(R.string.exit)
                            .positiveColor(getResources().getColor(R.color.blue3))
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    sendEvent(TVEVENT.E_TERMINATE);
                                }
                            })
                            .build();
                    bb_fail_dialog.getWindow().setGravity(Gravity.CENTER);
                    bb_fail_dialog.show();
                    bb_fail_dialog.setCanceledOnTouchOutside(false);
                }
            }
        }

        // showController();

        if (buildOption.SKIP_AV_ERROR_DATA == false) {
            FCI_TVi.setSkipAVErrorData((byte)0);
            TVlog.i(TAG, "==== AV error data allowed ====");
        } else {
            FCI_TVi.setSkipAVErrorData((byte)1);
            TVlog.i(TAG, "==== No AV error data allowed ====");
        }

        // live add
        isdbMode = FCI_TVi.GetISDBMode();
        if (isdbMode == FCI_TVi.ISDBT_MODE_FULLSEG) {
            strISDBMode = "ISDBT Fullseg";
        }
        else if (isdbMode == FCI_TVi.ISDBT_MODE_ONESEG) {
            strISDBMode = "ISDBT Oneseg";
        }
        else {
            strISDBMode = "ISDBT None";
        }
        TVlog.i(TAG, "==== onStart  end====(mode: "+strISDBMode+")");
        //

        if (isMainActivity) {
            removeEvent(TVEVENT.E_SIGNAL_MONITER_FLOATING);
            removeEvent(TVEVENT.E_SIGNAL_MONITER_CHAT);
            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                postEvent(TVEVENT.E_SIGNAL_MONITER, SIGNAL_MONITER_TIME_USB);
            } else {
                postEvent(TVEVENT.E_SIGNAL_MONITER, SIGNAL_MONITER_TIME);
            }
        } else if (ChatMainActivity.isChat) {
            removeEvent(TVEVENT.E_SIGNAL_MONITER);
            removeEvent(TVEVENT.E_SIGNAL_MONITER_FLOATING);
            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                if (ChatMainActivity.getInstance() != null) {
                    ChatMainActivity.getInstance().postEvent(TVEVENT.E_SIGNAL_MONITER_CHAT, SIGNAL_MONITER_TIME_USB);
                }
            } else {
                if (ChatMainActivity.getInstance() != null) {
                    ChatMainActivity.getInstance().postEvent(TVEVENT.E_SIGNAL_MONITER_CHAT, SIGNAL_MONITER_TIME);
                }
            }
        } else if (FloatingWindow.isFloating) {
            removeEvent(TVEVENT.E_SIGNAL_MONITER);
            removeEvent(TVEVENT.E_SIGNAL_MONITER_CHAT);
            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                if (FloatingWindow.getInstance() != null) {
                    FloatingWindow.getInstance().postEvent(TVEVENT.E_SIGNAL_MONITER_FLOATING, SIGNAL_MONITER_TIME_USB);
                }
            } else {
                if (FloatingWindow.getInstance() != null) {
                    FloatingWindow.getInstance().postEvent(TVEVENT.E_SIGNAL_MONITER_FLOATING, SIGNAL_MONITER_TIME);
                }
            }
        }

        /*
        if (ChatMainActivity.isChat) {
            ChatMainActivity.getInstance().sendEvent(TVEVENT.E_SIGNAL_MONITER_CHAT);
        } else {
        sendEvent(TVEVENT.E_SIGNAL_MONITER);
        }*/

        if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 )  {
            getCurrentRecordingPath();
            thumbNailUpdate.getThhumbNailUpdateTask().sendEvent(TVEVENT.E_UPDATE_THUMBNAIL, 0);
        }

        if (buildOption.ADD_GINGA_NCL == true) {
            //ToDo::Ginga
            //Initialize Ginga view

            if (CommonStaticData.interactiveSwitch==true) {
                FCI_TVi.enableGingaNCL();
            }
            else {
                FCI_TVi.disableGingaNCL();
            }
            TVlog.i(TAG, "==== ginga initialized end ====");
        }
        else {
            FCI_TVi.disableGingaNCL();
        }

        // super.onStart();
    }

    // [ Nexell de-intelace 20170120
    private void propSet(String key, String value) {
        try {
            Class clazz = Class.forName("android.os.SystemProperties");
            if (clazz == null)
                return;

            Class[] params = {String.class, String.class};
            Method method = clazz.getDeclaredMethod("set", params);
            if (method == null)
                return;

            Object[] args = {key, value};
            method.invoke(null, args);
        } catch (Exception e) {
            TVlog.i("PSTTSET ", "Exception during reflection: " + e.getMessage());
        }
    }
    // ]

    FrameLayout debugScreenLayout;
    TextView debugScreenTextView;
    Button bPlus;
    Button bMinus;
    Button bRed;
    Button bGreen;
    Button bBlue;
    int debugScreenTextViewSize=20;

    EditText eResiter;
    EditText eResiterValue;
    TextView debugScreenRRBB;
    Button bRegisterWrite;
    Button bRegisterRead;

    void DebugScreenDisplay(String _msg)
    {
        debugScreenLayout.setVisibility(View.VISIBLE);
        debugScreenTextView.setText(_msg);
    }

    void DebugScreenInit()
    {
        debugScreenLayout = (FrameLayout)findViewById(R.id.debugScreenFrameLayout);

        debugScreenTextView = (TextView)findViewById(R.id.textviewDebug);
        bPlus =(Button) findViewById(R.id.debugButtonPlus);
        bMinus =(Button) findViewById(R.id.debugButtonMinus);
        bRed =(Button) findViewById(R.id.debugButtonRed);
        bGreen =(Button) findViewById(R.id.debugButtonGreen);
        bBlue =(Button) findViewById(R.id.debugButtonBlue);

        debugScreenRRBB =(TextView)findViewById(R.id.textViewRRBB);
        debugScreenRRBB.setMovementMethod(new ScrollingMovementMethod());
        bRegisterWrite = (Button) findViewById(R.id.debugButtonWrite);
        bRegisterRead = (Button) findViewById(R.id.debugButtonRead);
        eResiter=(EditText)findViewById(R.id.editRegister);
        eResiterValue=(EditText)findViewById(R.id.editRegisterValue);

        bPlus.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                debugScreenTextViewSize+=5;
                if (debugScreenTextViewSize>=100)
                {
                    debugScreenTextViewSize=100;
                }
                TVlog.i(TAG_Debug, "bPlus Pressed size = " + debugScreenTextViewSize);
                debugScreenTextView.setTextSize(debugScreenTextViewSize);
                debugScreenRRBB.setTextSize(debugScreenTextViewSize);
            }
        });
        bMinus.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                debugScreenTextViewSize-=5;
                if (debugScreenTextViewSize <=5)
                {
                    debugScreenTextViewSize=5;
                }
                TVlog.i(TAG_Debug, "bMinus Pressed size = " + debugScreenTextViewSize);
                debugScreenTextView.setTextSize(debugScreenTextViewSize);
                debugScreenRRBB.setTextSize(debugScreenTextViewSize);
            }
        });
        bRed.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                bPlus.setTextColor(Color.RED);
                bMinus.setTextColor(Color.RED);
                debugScreenTextView.setTextColor(Color.RED);
                debugScreenRRBB.setTextColor(Color.RED);
                TVlog.i(TAG_Debug, "bRed Pressed ");
            }
        });
        bGreen.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                bPlus.setTextColor(Color.GREEN);
                bMinus.setTextColor(Color.GREEN);
                debugScreenTextView.setTextColor(Color.GREEN);
                debugScreenRRBB.setTextColor(Color.GREEN);
                TVlog.i(TAG_Debug, "bGreen Pressed ");
            }
        });
        bBlue.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                bPlus.setTextColor(Color.BLUE);
                bMinus.setTextColor(Color.BLUE);
                debugScreenTextView.setTextColor(Color.BLUE);
                debugScreenRRBB.setTextColor(Color.BLUE);
                TVlog.i(TAG_Debug, "bBlue Pressed ");
            }
        });

        bRegisterWrite.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                String editReg = eResiter.getText().toString();
                String editRegval = eResiterValue.getText().toString();

                if ( editReg.length()==0)
                {
                    CustomToast toast = new CustomToast(getApplicationContext());
                    toast.showToast(getApplicationContext(), "Please put register address " , Toast.LENGTH_LONG);
                    return;
                }
                if ( editRegval.length()==0)
                {
                    CustomToast toast = new CustomToast(getApplicationContext());
                    toast.showToast(getApplicationContext(), "Please put register value " , Toast.LENGTH_LONG);
                    return;
                }

                debugScreenRRBB.append("Write : 0x" + editReg + " = 0x" + editRegval + "\n");

                TVlog.i(TAG_Debug, "editReg length =  " + editReg.length() + " editRegval length = " + editRegval.length());
                Integer.parseInt(editReg, 16);
                FCI_TVi.devRegWriteByte(Integer.parseInt(editReg, 16), (byte) Integer.parseInt(editRegval, 16));

                final int scrollAmount = debugScreenRRBB.getLayout().getLineTop(debugScreenRRBB.getLineCount()) - debugScreenRRBB.getHeight();
                if (scrollAmount > 0)
                    debugScreenRRBB.scrollTo(0, scrollAmount);
                else
                    debugScreenRRBB.scrollTo(0, 0);
            }
        });

        bRegisterRead.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                String editReg = eResiter.getText().toString();

                if (editReg.length()==0)
                {
                    CustomToast toast = new CustomToast(getApplicationContext());
                    toast.showToast(getApplicationContext(), "Please put register address " , Toast.LENGTH_LONG);
                    return;
                }

                String value = SignalMonitor.hex(FCI_TVi.devRegReadByte(Integer.parseInt(editReg, 16)));

                debugScreenRRBB.append("Read : 0x" + editReg + " = " + value + "\n");
                final int scrollAmount = debugScreenRRBB.getLayout().getLineTop(debugScreenRRBB.getLineCount()) - debugScreenRRBB.getHeight();
                if (scrollAmount > 0)
                    debugScreenRRBB.scrollTo(0, scrollAmount);
                else
                    debugScreenRRBB.scrollTo(0, 0);
            }
        });


        debugScreenTextView.setTextSize(debugScreenTextViewSize);
        debugScreenTextView.setTextColor(Color.RED);

        debugScreenRRBB.append("== Current Register State == \n");
        debugScreenRRBB.setTextSize(debugScreenTextViewSize);
        debugScreenRRBB.setTextColor(Color.RED);

        debugScreenLayout.setVisibility(View.INVISIBLE);
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE;    // justin
        decorView.setSystemUiVisibility(uiOptions);

        //TVlog.i(TAG, "== setSystemUiVisibility onWindowFocusChanged ==");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isMainActivity = true;
        ChatMainActivity.isChat = false;
        FloatingWindow.isFloating = false;

        // [[ solution switching mode 20170223
        CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
        CommonStaticData.solutionMode = CommonStaticData.settings.getInt(CommonStaticData.solutionModeKey, cReleaseOption.FCI_SOLUTION_MODE);
        buildOption.FCI_SOLUTION_MODE = CommonStaticData.solutionMode;
        // ]]

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        pxWidth = displayMetrics.widthPixels;
        pxHeight = displayMetrics.heightPixels;
        TVlog.i(TAG, " >>>>> pxWidth = "+pxWidth+ ", pxHeight = "+pxHeight);

        int player_bright = CommonStaticData.brightness;
        //WindowManager.LayoutParams lp = mWindow.getAttributes();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = player_bright;
        getWindow().setAttributes(lp);

        isStoragePermissionGranted();  // for Android M Permission
        //if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_SWCODEC) {
        if (buildOption.USE_MULTI_WINDOW) {
            overlay_permission();
        }
        //}

        instance = this;
        TVlog.i(TAG, "==== onCreate ======");

        customToast = new CustomToast(getApplicationContext());

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        decorView = getWindow().getDecorView(); // justin
        uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE ;

        decorView.setSystemUiVisibility(uiOptions);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.layout_port);

        Intent intent = getIntent();
        mUri = intent.getData();
        if (mUri == null) {
            mUri = TVProgram.Programs.CONTENT_URI;
            intent.setData(mUri);
        }

        // Broadcast Receiver Register

        mSysReceiver = new SysBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_HEADSET_PLUG);

        registerReceiver(mSysReceiver, filter);

        //live add
        mScreenOff = new SysBroadcastReceiver();
        IntentFilter off_filter = new IntentFilter();
        off_filter.addAction(Intent.ACTION_SCREEN_OFF);
        // [[ eddy 160706
        off_filter.addAction(Intent.ACTION_USER_PRESENT);
        // ]] eddy 160706
        off_filter.setPriority(999);
        registerReceiver(mScreenOff, off_filter);

        // justin phone status debug 20161110
        MyPhoneStateListener phoneStateListener = new MyPhoneStateListener();
        TelephonyManager telephony = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        //

        removeStatusBar(false);

        // usbdongle[[
        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
            //LME dongle
            mylme_sdk = new lmedtvsdk(this);
            mUsbReceiver = new SysBroadcastReceiver();
            IntentFilter usb_filter = new IntentFilter(ACTION_USB_PERMISSION);
            mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
            //mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_ONE_SHOT);
            usb_filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
            registerReceiver(mUsbReceiver, usb_filter);
        }
        // ]]usbdongle

        if (isRunningOnEmulator())
        {
            //emul
            capturePath = getExternalStorageDirectory().toString()+"/MobileTV";
        } else {
            capturePath = devicePath;
        }

        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener (new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {

                if (!isUiLocked) {
                    if ((visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0) {

                        //  showController();
                        if (MainActivity.isMainActivity ==true) {

                            if (ll_mainAutoSearch.getVisibility() != View.VISIBLE) {
                                removeEvent(TVEVENT.E_HIDE_CONTROLER);

                                controllerLayout.setVisibility(View.VISIBLE);

                                if (buildOption.USE_MULTI_WINDOW) {
                                    if (changeChannelView.getVisibility() == View.VISIBLE) {
                                        ll_multiWindow.setVisibility(View.GONE);
                                    } else {
                                        ll_multiWindow.setVisibility(View.VISIBLE);
                                    }
                                }

                                if (buildOption.USE_CHAT_FUNCTION) {
                                    ll_chat.setVisibility(View.VISIBLE);
                                } else {
                                    ll_chat.setVisibility(View.GONE);
                                }

                                if (buildOption.ADD_LOUD_SPEAKER) {
                                    if (is_wired_headset) {
                                        earphoneLayout.setVisibility(View.VISIBLE);
                                    } else {
                                        earphoneLayout.setVisibility(View.INVISIBLE);
                                    }
                                }

                                if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 )  {
                                    channelLayout.setVisibility(View.VISIBLE);
                                }

                                status_bar.setVisibility(View.VISIBLE);
                                if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                                    CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
//                                  int receiveMode = CommonStaticData.settings.getInt(CommonStaticData.receivemodeSwitchKey, CommonStaticData.RECEIVE_MODE_AUTO);  // auto
//                                  receiveModeB.setText(arr_svcmodeswitch_jp[receiveMode]);
                                }

                                postEvent(TVEVENT.E_HIDE_CONTROLER, CONTROLLER_HIDE_TIME);
                                removeStatusBar(false);

                                // live add
                                sendEvent(TVEVENT.E_UPDATE_EPG_NAME_AND_DURATION);

                                if (CommonStaticData.settingActivityShow || CommonStaticData.epgActivityShow || CommonStaticData.channelMainActivityShow || CommonStaticData.recordedFileActivityShow) {
                                    removeEvent(TVEVENT.E_HIDE_CONTROLER);
                                    controllerLayout.setVisibility(View.INVISIBLE);

                                    if (buildOption.USE_MULTI_WINDOW) {
                                        ll_multiWindow.setVisibility(View.GONE);
                                    }

                                    //ll_chat.setVisibility(View.INVISIBLE);
                                    if (buildOption.USE_CHAT_FUNCTION) {
                                        ll_chat.setVisibility(View.GONE);
                                    }

                                    if (buildOption.ADD_LOUD_SPEAKER) {
                                        earphoneLayout.setVisibility(View.INVISIBLE);
                                    }
                                    if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 )  {
                                        channelLayout.setVisibility(View.INVISIBLE);
                                    }
                                    status_bar.setVisibility(View.INVISIBLE);
                                    removeStatusBar(true);
                                }
                            }

                        } else {
                            removeEvent(TVEVENT.E_HIDE_CONTROLER);
                            controllerLayout.setVisibility(View.INVISIBLE);

                            if (buildOption.USE_MULTI_WINDOW) {
                                ll_multiWindow.setVisibility(View.GONE);
                            }

                            //ll_chat.setVisibility(View.INVISIBLE);
                            if (buildOption.USE_CHAT_FUNCTION) {
                                ll_chat.setVisibility(View.GONE);
                            }

                            if (buildOption.ADD_LOUD_SPEAKER) {
                                earphoneLayout.setVisibility(View.INVISIBLE);
                            }
                            if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 )  {
                                channelLayout.setVisibility(View.INVISIBLE);
                            }
                            status_bar.setVisibility(View.INVISIBLE);
                            removeStatusBar(true);
                        }

                    } else {
                        removeEvent(TVEVENT.E_HIDE_CONTROLER);
                        controllerLayout.setVisibility(View.INVISIBLE);

                        if (buildOption.USE_MULTI_WINDOW) {
                            ll_multiWindow.setVisibility(View.GONE);
                        }

                        //ll_chat.setVisibility(View.INVISIBLE);
                        if (buildOption.USE_CHAT_FUNCTION) {
                            ll_chat.setVisibility(View.GONE);
                        }

                        if (buildOption.ADD_LOUD_SPEAKER) {
                            earphoneLayout.setVisibility(View.INVISIBLE);
                        }
                        if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 ) {
                            channelLayout.setVisibility(View.INVISIBLE);
                        }
                        status_bar.setVisibility(View.INVISIBLE);
                        removeStatusBar(true);
                    }
                } else {
                    if ((visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0) {
                        if (MainActivity.isMainActivity) {
                            removeEvent(TVEVENT.E_HIDE_CONTROLER);
                            ll_uiLocked.setVisibility(View.VISIBLE);
                            postEvent(TVEVENT.E_HIDE_CONTROLER, CONTROLLER_HIDE_TIME);
                            removeStatusBar(false);
                        } else {
                            removeEvent(TVEVENT.E_HIDE_CONTROLER);
                            ll_uiLocked.setVisibility(View.INVISIBLE);
                            removeStatusBar(true);
                        }
                    } else {
                        removeEvent(TVEVENT.E_HIDE_CONTROLER);
                        ll_uiLocked.setVisibility(View.INVISIBLE);
                        removeStatusBar(true);
                    }
                }
            }
        });

        int display_mode = getResources().getConfiguration().orientation;

        if (SurfaceRotationOn == false)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);  // live
        }

        if (buildOption.GUI_STYLE == 2 || buildOption.GUI_STYLE == 3) {
            setDefaultChannel = true;  // live add
        }

        ll_mainAutoSearch = (LinearLayout) findViewById(R.id.ll_mainAutoSearch);
        ll_mainAutoSearch.setVisibility(View.INVISIBLE);
        btn_return = (Button) findViewById(R.id.btn_return);
        if (btn_return != null) {
            btn_return.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideController();
                    if (SystemClock.elapsedRealtime() - mLastClickTimeReturn < DOUBLE_CLICK_TOLERANCE){
                        return;
                    }
                    mLastClickTimeReturn = SystemClock.elapsedRealtime();
                    ll_mainAutoSearch.setVisibility(View.INVISIBLE);
                    sendEvent(TVEVENT.E_SCAN_CANCEL);
                }
            });
        }

        ll_noSignal = (LinearLayout) findViewById(R.id.ll_noSignal);
        ll_noSignal.setVisibility(View.INVISIBLE);

        noSignal = (TextView) findViewById(R.id.no_signal_msg);
        if (SDK_INT <= 19) {
            noSignal.setTextColor(getResources().getColor(R.color.white));
        }
        //noSignal.setVisibility(View.INVISIBLE);

        programNotMsg = (TextView) findViewById(R.id.program_not_msg);
        if (SDK_INT <= 19) {
            programNotMsg.setTextColor(getResources().getColor(R.color.white));
        }
        //programNotMsg.setVisibility(View.INVISIBLE);

        ll_noChannel = (LinearLayout) findViewById(R.id.ll_noChannel);

        // live add
        ll_file_play_mode = (LinearLayout) findViewById(R.id.ll_file_play_mode);
        ll_file_play_mode_usb = (LinearLayout) findViewById(R.id.ll_file_play_mode_usb);
        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
            if (FCI_TVi.initiatedSol) {
                ll_file_play_mode.setVisibility(View.INVISIBLE);
                ll_file_play_mode_usb.setVisibility(View.INVISIBLE);
            } else {
                ll_file_play_mode.setVisibility(View.INVISIBLE);
                ll_file_play_mode_usb.setVisibility(View.VISIBLE);
            }
        } else {
            if (FCI_TVi.initiatedSol) {
                ll_file_play_mode_usb.setVisibility(View.INVISIBLE);
                ll_file_play_mode.setVisibility(View.INVISIBLE);
            } else {
                ll_file_play_mode_usb.setVisibility(View.INVISIBLE);
                ll_file_play_mode.setVisibility(View.VISIBLE);
            }
        }

        //

        ll_age_limit = (LinearLayout) findViewById(R.id.ll_age_limit);
        ll_age_limit.setVisibility(View.INVISIBLE);

        tv_age_limit_msg_10 = (TextView) findViewById(R.id.tv_age_limit_msg_10);
        tv_age_limit_msg_10.setVisibility(View.INVISIBLE);
        tv_age_limit_msg_12 = (TextView) findViewById(R.id.tv_age_limit_msg_12);
        tv_age_limit_msg_12.setVisibility(View.INVISIBLE);
        tv_age_limit_msg_14 = (TextView) findViewById(R.id.tv_age_limit_msg_14);
        tv_age_limit_msg_14.setVisibility(View.INVISIBLE);
        tv_age_limit_msg_16 = (TextView) findViewById(R.id.tv_age_limit_msg_16);
        tv_age_limit_msg_16.setVisibility(View.INVISIBLE);
        tv_age_limit_msg_18 = (TextView) findViewById(R.id.tv_age_limit_msg_18);
        tv_age_limit_msg_18.setVisibility(View.INVISIBLE);

        Button button_enter_password = (Button) findViewById(R.id.button_enter_password);
        if (button_enter_password != null) {
            button_enter_password.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String saved  = CommonStaticData.settings.getString(CommonStaticData.passwordKey, null);
                    TVlog.i(TAG, " DIALOG_PASSWORD  String = " + saved);
                    if (saved == null || saved.length() == 0) {
                        InputDialog dig = new InputDialog(MainActivity.this, InputDialog.TYPE_NEW_PASSWORD, null, null, null);
                    } else {
                        InputDialog dig = new InputDialog(MainActivity.this, InputDialog.TYPE_ENTER_PASSWORD, null, null, null);
                    }
                }
            });
        }


        ll_chat = (LinearLayout) findViewById(R.id.ll_chat);
        iv_chat = (ImageView) findViewById(R.id.iv_chat);

        if (buildOption.USE_CHAT_FUNCTION) {
            ll_chat.setVisibility(View.VISIBLE);
        } else {
            ll_chat.setVisibility(View.GONE);
        }

        ll_chat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    iv_chat.setScaleX(0.8f);
                    iv_chat.setScaleY(0.8f);
                    iv_chat.setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    try {
                        Thread.sleep(BUTTON_CLICK_TIME);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    iv_chat.setScaleX(1.0f);
                    iv_chat.setScaleY(1.0f);
                    iv_chat.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
                }
                return false;
            }
        });

        ll_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                        || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB
                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                        || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) && (mUsbChipType == USB_CHIP_TYPE_NONE)) {          // justin add
                    // if (event.getAction() == MotionEvent.ACTION_UP) {
                    CustomToast toast12 = new CustomToast(getApplicationContext());
                    toast12.showToast(getApplicationContext(), getApplicationContext().getString(R.string.usb_dongle_not_attached), Toast.LENGTH_SHORT);
                    // }
                } else {
                    if (SystemClock.elapsedRealtime() - mLastClickTimeChat < DOUBLE_CLICK_TOLERANCE){
                        return;
                    }
                    mLastClickTimeChat = SystemClock.elapsedRealtime();
                    if (CommonStaticData.scanCHnum > 0) {
                        if (isRec) {
                            recordingStop(false);
                        }
                        if (!isOnline()) {
                            needNetworkConnectDialog = new MaterialDialog.Builder(MainActivity.this)
                                    .theme(Theme.LIGHT)
                                    .iconRes(R.drawable.ic_info_outline_gray_48dp)
                                    .title(R.string.alert)
                                    .titleColor(getResources().getColor(R.color.black))
                                    .content(R.string.need_network_connect_msg)
                                    .negativeText(R.string.ok)
                                    .negativeColor(getResources().getColor(R.color.blue3))
                                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            needNetworkConnectDialog.dismiss();
                                        }
                                    })
                                    .build();
                            needNetworkConnectDialog.getWindow().setGravity(Gravity.CENTER);
                            needNetworkConnectDialog.show();
                            needNetworkConnectDialog.setCanceledOnTouchOutside(false);
                        } else {
                            removeEvent(TVEVENT.E_BADSIGNAL_CHECK);
                            removeEvent(E_SIGNAL_NOTI_MSG);
                            removeEvent(TVEVENT.E_NOSIGNAL_SHOW);
                            removeEvent(TVEVENT.E_CHANNEL_CHANGE_TIMEOVER);

                            //removeEvent(TVEVENT.E_SIGNAL_MONITER);

                            isMainActivity = false;
                            ChatMainActivity.isChat = true;
                            FloatingWindow.isFloating = false;

                            CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                            editor.putInt(CommonStaticData.lastChannelKey, CommonStaticData.lastCH);
                            editor.putBoolean(CommonStaticData.badSignalFlagKey, CommonStaticData.badSignalFlag);
                            editor.putBoolean(CommonStaticData.encryptFlagKey, CommonStaticData.encryptFlag);
                            editor.putBoolean(CommonStaticData.ageLimitFlagKey, CommonStaticData.ageLimitFlag);
                            editor.putBoolean(CommonStaticData.passwordVerifyFlagKey, CommonStaticData.passwordVerifyFlag);
                            editor.putBoolean(CommonStaticData.mainPasswordVerifyFlagKey, CommonStaticData.mainPasswordVerifyFlag);
                            //editor.putBoolean(CommonStaticData.screenBlockFlagKey, CommonStaticData.screenBlockFlag);
                            editor.putBoolean(CommonStaticData.returnMainFromChatKey, CommonStaticData.returnMainFromChat);
                            editor.putBoolean(CommonStaticData.returnMainFromFloatingKey, CommonStaticData.returnMainFromFloating);
                            editor.putInt(CommonStaticData.currentScaleModeKey, CommonStaticData.currentScaleMode);

                            editor.commit();

                            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                    buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB ||
                                    buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB ||
                                    buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                                if (mUsbDevice != null) {
                                    ChatMainActivity.chat_currentUsbDevice = mUsbDevice;
                                } else {
                                    TVlog.i(TAG, " >>>>> mUsbDevice is null !!!");
                                }
                            }

                            //20170526
                            //CommonStaticData.screenBlockFlag = false;

                            Intent intent = new Intent(MainActivity.this, ChatMainActivity.class);
                            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            //android.os.Process.killProcess(android.os.Process.myPid());   // live
                        }
                    } else {
                        isMainActivity = false;
                        ChatMainActivity.isChat = true;
                        FloatingWindow.isFloating = false;

                        CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                        editor.putInt(CommonStaticData.lastChannelKey, CommonStaticData.lastCH);
                        editor.putBoolean(CommonStaticData.badSignalFlagKey, CommonStaticData.badSignalFlag);
                        editor.putBoolean(CommonStaticData.encryptFlagKey, CommonStaticData.encryptFlag);
                        editor.putBoolean(CommonStaticData.ageLimitFlagKey, CommonStaticData.ageLimitFlag);
                        editor.putBoolean(CommonStaticData.passwordVerifyFlagKey, CommonStaticData.passwordVerifyFlag);
                        editor.putBoolean(CommonStaticData.mainPasswordVerifyFlagKey, CommonStaticData.mainPasswordVerifyFlag);
                        editor.putBoolean(CommonStaticData.returnMainFromChatKey, CommonStaticData.returnMainFromChat);
                        editor.putBoolean(CommonStaticData.returnMainFromFloatingKey, CommonStaticData.returnMainFromFloating);
                        editor.putInt(CommonStaticData.currentScaleModeKey, CommonStaticData.currentScaleMode);
                        editor.commit();

                        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                            if (mUsbDevice != null) {
                                ChatMainActivity.chat_currentUsbDevice = mUsbDevice;
                            } else {
                                TVlog.i(TAG, " >>>>> mUsbDevice is null !!!");
                            }
                        }
                        Intent intent = new Intent(MainActivity.this, ChatMainActivity.class);
                        startActivity(intent);
                        new InputDialog(instance, InputDialog.TYPE_TV_NOCHANNELLIST, null, null, null);
                    }
                }
            }
        });

        //if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_SWCODEC) {
        ll_multiWindow = (LinearLayout) findViewById(R.id.ll_multiWindow);
        if (buildOption.USE_CHAT_FUNCTION  == false) {
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            param.topMargin = 0;
            ll_multiWindow.setLayoutParams(param);
        } else {
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            param.topMargin = 40;
            ll_multiWindow.setLayoutParams(param);
        }
        iv_multiWindow = (ImageView) findViewById(R.id.iv_multiWindow);

        if (buildOption.USE_MULTI_WINDOW) {
            ll_multiWindow.setVisibility(View.VISIBLE);
        } else {
            ll_multiWindow.setVisibility(View.GONE);
        }

        ll_multiWindow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    iv_multiWindow.setScaleX(0.8f);
                    iv_multiWindow.setScaleY(0.8f);
                    iv_multiWindow.setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    try {
                        Thread.sleep(BUTTON_CLICK_TIME);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    iv_multiWindow.setScaleX(1.0f);
                    iv_multiWindow.setScaleY(1.0f);
                    iv_multiWindow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
                }
                return false;
            }
        });

        ll_multiWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                        || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB
                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                        || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) && (mUsbChipType == USB_CHIP_TYPE_NONE)) {          // justin add
                    // if (event.getAction() == MotionEvent.ACTION_UP) {
                    CustomToast toast12 = new CustomToast(getApplicationContext());
                    toast12.showToast(getApplicationContext(), getApplicationContext().getString(R.string.usb_dongle_not_attached), Toast.LENGTH_SHORT);
                    // }
                } else {
                    if (SystemClock.elapsedRealtime() - mLastClickTimeFloating < DOUBLE_CLICK_TOLERANCE) {
                        return;
                    }
                    mLastClickTimeFloating = SystemClock.elapsedRealtime();
                    if (CommonStaticData.scanCHnum > 0) {
                        //TVUI_Handler.removeCallbacks(mRunnable);
                        if (isRec) {
                            recordingStop(false);
                        }
                        removeEvent(TVEVENT.E_BADSIGNAL_CHECK);
                        removeEvent(TVEVENT.E_SIGNAL_NOTI_MSG);
                        CommonStaticData.isBadSignalFlag = false;
                        removeEvent(TVEVENT.E_NOSIGNAL_SHOW);
                        removeEvent(TVEVENT.E_CHANNEL_CHANGE_TIMEOVER);

                        isMainActivity = false;
                        ChatMainActivity.isChat = false;
                        FloatingWindow.isFloating = true;

                        CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                        editor.putInt(CommonStaticData.lastChannelKey, CommonStaticData.lastCH);
                        editor.putBoolean(CommonStaticData.badSignalFlagKey, CommonStaticData.badSignalFlag);
                        editor.putBoolean(CommonStaticData.encryptFlagKey, CommonStaticData.encryptFlag);
                        editor.putBoolean(CommonStaticData.ageLimitFlagKey, CommonStaticData.ageLimitFlag);
                        editor.putBoolean(CommonStaticData.passwordVerifyFlagKey, CommonStaticData.passwordVerifyFlag);
                        editor.putBoolean(CommonStaticData.mainPasswordVerifyFlagKey, CommonStaticData.mainPasswordVerifyFlag);
                        //editor.putBoolean(CommonStaticData.screenBlockFlagKey, CommonStaticData.screenBlockFlag);
                        editor.putBoolean(CommonStaticData.returnMainFromChatKey, CommonStaticData.returnMainFromChat);
                        editor.putBoolean(CommonStaticData.returnMainFromFloatingKey, CommonStaticData.returnMainFromFloating);
                        editor.putInt(CommonStaticData.currentScaleModeKey, CommonStaticData.currentScaleMode);
                        editor.commit();

                        //CommonStaticData.screenBlockFlag = false;

                        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                            if (mUsbDevice != null) {
                                FloatingWindow.currentUsbDevice = mUsbDevice;
                            }
                        }
                        floatingFromMain = true;

                        startService(new Intent(MainActivity.this,FloatingWindow.class));

                        if (isServiceRunningCheck()) {
                            TVlog.i(TAG, " >>>>> FloatingWindow Service is running!! later finish");
                            //moveTaskToBack(true);
                        }
                    } else {
                        isMainActivity = false;
                        ChatMainActivity.isChat = false;
                        FloatingWindow.isFloating = true;

                        CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                        editor.putInt(CommonStaticData.lastChannelKey, CommonStaticData.lastCH);
                        editor.putBoolean(CommonStaticData.badSignalFlagKey, CommonStaticData.badSignalFlag);
                        editor.putBoolean(CommonStaticData.encryptFlagKey, CommonStaticData.encryptFlag);
                        editor.putBoolean(CommonStaticData.ageLimitFlagKey, CommonStaticData.ageLimitFlag);
                        editor.putBoolean(CommonStaticData.passwordVerifyFlagKey, CommonStaticData.passwordVerifyFlag);
                        editor.putBoolean(CommonStaticData.mainPasswordVerifyFlagKey, CommonStaticData.mainPasswordVerifyFlag);
                        editor.putBoolean(CommonStaticData.returnMainFromChatKey, CommonStaticData.returnMainFromChat);
                        editor.putBoolean(CommonStaticData.returnMainFromFloatingKey, CommonStaticData.returnMainFromFloating);
                        editor.putInt(CommonStaticData.currentScaleModeKey, CommonStaticData.currentScaleMode);
                        editor.commit();

                        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                            if (mUsbDevice != null) {
                                FloatingWindow.currentUsbDevice = mUsbDevice;
                            }
                        }
                        floatingFromMain = true;

                        startService(new Intent(MainActivity.this,FloatingWindow.class));

                        if (isServiceRunningCheck()) {
                            TVlog.i(TAG, " >>>>> FloatingWindow Service is running!!  scanCHnum = 0");

                        }

                        new InputDialog(instance, InputDialog.TYPE_TV_NOCHANNELLIST, null, null, null);
                    }
                }
            }
        });

        ll_uiLock = (LinearLayout) findViewById(R.id.ll_uiLock);
        iv_uiLock = (ImageView) findViewById(R.id.iv_uiLock);

        ll_uiLock.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    iv_uiLock.setScaleX(0.8f);
                    iv_uiLock.setScaleY(0.8f);
                    iv_uiLock.setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    try {
                        Thread.sleep(BUTTON_CLICK_TIME);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    iv_uiLock.setScaleX(1.0f);
                    iv_uiLock.setScaleY(1.0f);
                    iv_uiLock.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
                }
                return false;
            }
        });

        ll_uiLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideController();
                isUiLocked = true;
                ll_uiLocked.setVisibility(View.VISIBLE);
            }
        });

        ll_uiLocked = (LinearLayout) findViewById(R.id.ll_uiLocked);
        iv_uiLocked = (ImageView) findViewById(R.id.iv_uiLocked);

        ll_uiLocked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_uiLocked.setVisibility(View.GONE);
                isUiLocked = false;
                showController();
            }
        });

        // live add
        ll_scramble_msg = (LinearLayout) findViewById(R.id.ll_scramble_msg);
        tv_scramble_title = (TextView) findViewById(R.id.tv_scramble_title);
        tv_scramble_msg = (TextView) findViewById(R.id.tv_scramble_msg);
        if (SDK_INT <= 19) {
            tv_scramble_title.setTextColor(getResources().getColor(R.color.white));
            tv_scramble_msg.setTextColor(getResources().getColor(R.color.white));
        }
        ll_scramble_msg.setVisibility(View.INVISIBLE);
        //

        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB) {
            bcas_card_insert_msg = (TextView) findViewById(R.id.bcas_card_insert_msg);
            bcas_card_insert_msg.setVisibility(View.INVISIBLE);
            if (SDK_INT <= 19) {
                if (bcas_card_insert_msg.getVisibility() == View.VISIBLE) {
                    bcas_card_insert_msg.setTextColor(getResources().getColor(R.color.white));
                }
            }
            /*
            if (is_inserted_card) {
                bcas_card_insert_msg.setVisibility(View.INVISIBLE);
            } else {
                bcas_card_insert_msg.setVisibility(View.VISIBLE);
                blink();
            }*/
        }

        // debugScreen

        DebugScreenInit();

        channelChangeBG=(ImageView)findViewById(R.id.imageView_bg);
        ImageView signalImage = (ImageView)findViewById(R.id.dtv_signal);

        currChNo = (TextView) findViewById(R.id.tv_ch_no);
        tf = Typeface.createFromAsset(getAssets(), "fonts/digital7.ttf");
        currChNo.setTypeface(tf);
        currChNo.setTextSize(18);

        if (buildOption.VIEW_PHY_CH) {
            currChNo.setVisibility(View.VISIBLE);
        } else {
            currChNo.setVisibility(View.GONE);
        }

        rl_ChType = (RelativeLayout) findViewById(R.id.rl_ChType);
        iv_ChType = (ImageView) findViewById(R.id.iv_ChType);
        iv_ChFree = (ImageView) findViewById(R.id.iv_ChFree);

        if (CommonStaticData.scanCHnum > 0) {
            if (mCursor != null) {
                int type = (int) mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV);
                int free = (int) mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_FREE);
                if (type == 0) { // if 1seg
                    if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                        iv_ChType.setBackgroundResource(R.drawable.jp_1seg);
                        iv_ChFree.setVisibility(View.GONE);
                    } else {
                        iv_ChType.setBackgroundResource(R.drawable.tv_icon_1seg);
                        if (free == 0) {
                            iv_ChFree.setVisibility(View.VISIBLE);
                        } else {
                            iv_ChFree.setVisibility(View.GONE);
                        }
                    }
                } else if (type == 1) { // if fullseg
                    if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                        iv_ChType.setBackgroundResource(R.drawable.jp_fullseg);
                        iv_ChFree.setVisibility(View.GONE);
                    } else {
                        iv_ChType.setBackgroundResource(R.drawable.tv_icon_fullseg);
                        if (free == 0) {
                            iv_ChFree.setVisibility(View.VISIBLE);
                        } else {
                            iv_ChFree.setVisibility(View.GONE);
                        }
                    }
                }
            }
        } else {
            rl_ChType.setVisibility(View.GONE);
        }

        currRemoteNo = (TextView) findViewById(R.id.tv_remote_no);
        currCH = (TextView)findViewById(R.id.servicename);
        currCH.setSelected(true);

        CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
        CommonStaticData.scanCHnum = CommonStaticData.settings.getInt(CommonStaticData.scanedChannelsKey, 0);
        if (CommonStaticData.scanCHnum > 0) {
            ll_noChannel.setVisibility(View.INVISIBLE);
        } else {
            ll_noChannel.setVisibility(View.VISIBLE);
            if (currChNo != null && currCH != null && currRemoteNo != null) {
                currChNo.setText("- -ch");
                currRemoteNo.setText("- - -");
                currCH.setText(R.string.no_channel_title);
            }
            if (rl_ChType != null) {
                rl_ChType.setVisibility(View.GONE);
            }
            if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 )  {
                if (currProgram != null && currDuration != null) {
                    currProgram.setText("- - -");
                    currDuration.setText("--:--~--:--");
                }
            }
        }

        if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 )  {
            currProgram = (TextView) findViewById(R.id.tv_programname);
            currDuration = (TextView) findViewById(R.id.tv_duration);
        }

        /*
        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
            //use free font
            mFont = Typeface.createFromAsset(getAssets(), "wlcmaru2004emoji.ttf");
            currCH.setTypeface(mFont);
            if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 ) {
                currProgram.setTypeface(mFont);
            }
        }*/

        signalMoniter = new SignalMonitor(signalImage);

        sv = (SurfaceView) findViewById(R.id.surfaceView);
        videoSurfaceHolder = sv.getHolder();

        if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
            svSub =(SurfaceView) findViewById(R.id.surfaceView2);
            if (svSub != null) {
                videoSurfaceHolderSub = svSub.getHolder();
            }
        }
        //dualdecode[[
        else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
            svSub =(SurfaceView) findViewById(R.id.surfaceView2);
            if (svSub != null) {
                videoSurfaceHolderSub = svSub.getHolder();
            }
        }
        //]]dualdecode

        int h = sv.getMeasuredHeight();
        int w = sv.getMeasuredWidth();

        videoSurfaceHolder.setFixedSize(w, h);
        videoSurfaceHolder.addCallback(this);
        if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
            if (svSub != null) {
                videoSurfaceHolderSub.setFixedSize(w, h);
                videoSurfaceHolderSub.addCallback(SubSurfaceSet.getSubSurfaceSet());
            }
        }
        //dualdecode[[
        else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
            if (svSub != null) {
                videoSurfaceHolderSub.setFixedSize(w, h);
                videoSurfaceHolderSub.addCallback(SubSurfaceSet.getSubSurfaceSet());
            }
        }
        //]]dualdecode

        ll_audioOnlyChannel = (LinearLayout) findViewById(R.id.ll_audioOnlyChannel);
        if (ll_audioOnlyChannel != null) {
            ll_audioOnlyChannel.setVisibility(View.INVISIBLE);
        }

        ll_black = (LinearLayout) findViewById(R.id.ll_black);
        if (CommonStaticData.isAudioChannel == true) {
            if (ll_black != null) {
                ll_black.setVisibility(View.VISIBLE);
            }
        } else {
            if (ll_black != null) {
                ll_black.setVisibility(View.INVISIBLE);
            }
        }

        scanB=(ImageButton)findViewById(R.id.button_scan);
        scanB.setPadding(0, 10, 0, 10);
        scanB.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    scanB.setScaleX(0.8f);
                    scanB.setScaleY(0.8f);
                    scanB.setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        try {
                            Thread.sleep(BUTTON_CLICK_TIME);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        scanB.setScaleX(1.0f);
                        scanB.setScaleY(1.0f);
                        scanB.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
                }
                return false;
            }
        });

        scanB.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if ((buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                        || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB
                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                        || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) && (mUsbChipType == USB_CHIP_TYPE_NONE)) {          // justin add
                    CustomToast toast12 = new CustomToast(getApplicationContext());
                    toast12.showToast(getApplicationContext(), getApplicationContext().getString(R.string.usb_dongle_not_attached), Toast.LENGTH_SHORT);
                } else {
                    // mis-clicking prevention, using threshold of 1500 ms
                    if (SystemClock.elapsedRealtime() - mLastClickTimeScan < DOUBLE_CLICK_TOLERANCE){
                        return;
                    }
                    mLastClickTimeScan = SystemClock.elapsedRealtime();

                    // live add
                    if (sv != null && sv.isShown()) {
                        sv.setBackgroundColor(getResources().getColor(R.color.transparent));
                    }
                    if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                        if (svSub != null && svSub.isShown()) {
                            svSub.setBackgroundColor(getResources().getColor(R.color.transparent));
                        }
                    } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                            (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                    buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                    buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                        if (svSub != null && svSub.isShown()) {
                            svSub.setBackgroundColor(getResources().getColor(R.color.transparent));
                        }
                        }

                        if (ll_noSignal.getVisibility() == View.VISIBLE) {
                            ll_noSignal.setVisibility(View.INVISIBLE);
                        }
                    if (ll_audioOnlyChannel.getVisibility() == View.VISIBLE) {
                        ll_audioOnlyChannel.setVisibility(View.INVISIBLE);
                    }
                        if (ll_scramble_msg.getVisibility() == View.VISIBLE) {
                            ll_scramble_msg.setVisibility(View.INVISIBLE);
                        }

                        if (buildOption.GUI_STYLE == 2 || buildOption.GUI_STYLE == 3) {
                            setDefaultChannel = true;  // live add
                    }
                    if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                        showDialog(DIALOG_SCANMODE);
                    } else {
                        sendEvent(E_SCAN_START);
                    }
                }
            }
        });

        listB = (ImageButton)findViewById(R.id.button_list);
        listB.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    listB.setScaleX(0.8f);
                    listB.setScaleY(0.8f);
                    listB.setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    try {
                            Thread.sleep(BUTTON_CLICK_TIME);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        listB.setScaleX(1.0f);
                        listB.setScaleY(1.0f);
                        listB.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
                }
                return false;
            }
        });
        listB.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if ((buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                        || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB
                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                        || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) && (mUsbChipType == USB_CHIP_TYPE_NONE)) {          // justin add
                    CustomToast toast12 = new CustomToast(getApplicationContext());
                    toast12.showToast(getApplicationContext(), getApplicationContext().getString(R.string.usb_dongle_not_attached), Toast.LENGTH_SHORT);
                } else {
                    hideController();
                    // mis-clicking prevention, using threshold of 1500 ms
                    if (SystemClock.elapsedRealtime() - mLastClickTimeList < DOUBLE_CLICK_TOLERANCE){
                        return;
                    }
                    mLastClickTimeList = SystemClock.elapsedRealtime();
                    MainActivity.isMainActivity=true;
                    setChangeProcMemory();
                        setChangeProcShift();
                    Intent intent = new Intent(MainActivity.this, ChannelMainActivity.class);
                    startActivity(intent);
                }
            }
        });

        if (buildOption.GUI_STYLE == 2 || buildOption.GUI_STYLE == 3){
        leftB = (ImageButton)findViewById(R.id.button_down);
        leftB.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        leftB.setScaleX(0.8f);
                        leftB.setScaleY(0.8f);
                        leftB.setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        try {
                            Thread.sleep(BUTTON_CLICK_TIME);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        leftB.setScaleX(1.0f);
                        leftB.setScaleY(1.0f);
                        leftB.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
                    }
                    return false;
                }
            });
            leftB.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    if ((buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) && (mUsbChipType == USB_CHIP_TYPE_NONE)) {          // justin add
                        CustomToast toast12 = new CustomToast(getApplicationContext());
                        toast12.showToast(getApplicationContext(), getApplicationContext().getString(R.string.usb_dongle_not_attached), Toast.LENGTH_SHORT);
                    } else {
                        CommonStaticData.passwordVerifyFlag = false;
                        CommonStaticData.ageLimitFlag = false;
                        channelChangeStartView(false);
                        if (ll_scramble_msg.getVisibility() == View.VISIBLE) {
                            ll_scramble_msg.setVisibility(View.INVISIBLE);
                        }

                        ll_age_limit.setVisibility(View.INVISIBLE);

                        TVBridge.AVStartMinus();
                    }
                }
            });
        }

        if (buildOption.GUI_STYLE == 2 || buildOption.GUI_STYLE == 3){
            rightB=(ImageButton)findViewById(R.id.button_up);
            rightB.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        rightB.setScaleX(0.8f);
                        rightB.setScaleY(0.8f);
                        rightB.setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        try {
                            Thread.sleep(BUTTON_CLICK_TIME);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        rightB.setScaleX(1.0f);
                        rightB.setScaleY(1.0f);
                        rightB.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
                    }
                    return false;
                }
            });
            rightB.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    if ((buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) && (mUsbChipType == USB_CHIP_TYPE_NONE)) {          // justin add
                        CustomToast toast12 = new CustomToast(getApplicationContext());
                        toast12.showToast(getApplicationContext(), getApplicationContext().getString(R.string.usb_dongle_not_attached), Toast.LENGTH_SHORT);
                    } else {
                        // justin DB
                        CommonStaticData.passwordVerifyFlag = false;
                        CommonStaticData.ageLimitFlag = false;
                        channelChangeStartView(false);
                        changeChannelView.setVisibility(View.VISIBLE);
                        if (ll_mainAutoSearch.getVisibility() == View.VISIBLE) {
                            ll_mainAutoSearch.setVisibility(View.INVISIBLE);
                        }
                        // live add
                        if (ll_scramble_msg.getVisibility() == View.VISIBLE) {
                            ll_scramble_msg.setVisibility(View.INVISIBLE);
                        }

                        ll_age_limit.setVisibility(View.INVISIBLE);
                        TVBridge.AVStartPlus();
                    }
                }
            });
        }

        epgB = (ImageButton) findViewById(R.id.button_epg);
        epgB.setPadding(0, 10, 0, 10);
        epgB.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    epgB.setScaleX(0.8f);
                    epgB.setScaleY(0.8f);
                    epgB.setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    try {
                        Thread.sleep(BUTTON_CLICK_TIME);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    epgB.setScaleX(1.0f);
                    epgB.setScaleY(1.0f);
                    epgB.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
                }
                return false;
            }
        });
        epgB.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if ((buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                        || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB
                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                        || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) && (mUsbChipType == USB_CHIP_TYPE_NONE)) {          // justin add
                    CustomToast toast12 = new CustomToast(getApplicationContext());
                    toast12.showToast(getApplicationContext(), getApplicationContext().getString(R.string.usb_dongle_not_attached), Toast.LENGTH_SHORT);
                } else {
                    hideController();
                    // mis-clicking prevention, using threshold of 1500 ms
                    if (SystemClock.elapsedRealtime() - mLastClickTimeEPG < DOUBLE_CLICK_TOLERANCE){
                        return;
                    }
                    mLastClickTimeEPG = SystemClock.elapsedRealtime();
                    MainActivity.isMainActivity=true;
                    setChangeProcMemory();
                    setChangeProcShift();
                    Intent intent = new Intent(MainActivity.this, EPGActivity.class);
                    intent.putExtra("curIndex", mChannelIndex);
                    startActivity(intent);
                }
            }
        });

        scaleB = (ImageButton) findViewById(R.id.button_scale);
        scaleB.setPadding(0, 10, 0, 10);
        scaleB.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    scaleB.setScaleX(0.8f);
                    scaleB.setScaleY(0.8f);
                    scaleB.setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    try {
                            Thread.sleep(BUTTON_CLICK_TIME);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        scaleB.setScaleX(1.0f);
                        scaleB.setScaleY(1.0f);
                        scaleB.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
                }
                return false;
            }
        });
        scaleB.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if ((buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                        || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB
                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                        || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) && (mUsbChipType == USB_CHIP_TYPE_NONE)) {          // justin add
                    CustomToast toast12 = new CustomToast(getApplicationContext());
                    toast12.showToast(getApplicationContext(), getApplicationContext().getString(R.string.usb_dongle_not_attached), Toast.LENGTH_SHORT);
                } else {
                    CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                    android.view.ViewGroup.LayoutParams lp = sv.getLayoutParams();
                    if (currentVideoMode == SCALEMODE_NORMAL) {
                        TVlog.i(TAG, "video mode : Normal --> 16:9");
                        SetVideoScale(SCALEMODE_16_9);
                        customToast.showToast(getApplicationContext(), getApplicationContext().getString(R.string.scale_16_9), Toast.LENGTH_SHORT);
                        CommonStaticData.currentScaleMode = 1;
                        editor.putInt(CommonStaticData.currentScaleModeKey, CommonStaticData.currentScaleMode);
                        editor.commit();

                    } else if (currentVideoMode == SCALEMODE_16_9) {
                        TVlog.i(TAG, "video mode : 16:9 --> 4:3");
                        SetVideoScale(SCALEMODE_4_3);
                        customToast.showToast(getApplicationContext(), getApplicationContext().getString(R.string.scale_4_3), Toast.LENGTH_SHORT);
                        CommonStaticData.currentScaleMode = 2;
                        editor.putInt(CommonStaticData.currentScaleModeKey, CommonStaticData.currentScaleMode);
                        editor.commit();
                    } else {
                        TVlog.i(TAG, "video mode : 4:3 --> Normal");
                        SetVideoScale(SCALEMODE_NORMAL);
                        customToast.showToast(getApplicationContext(), getApplicationContext().getString(R.string.scale_normal), Toast.LENGTH_SHORT);
                        CommonStaticData.currentScaleMode = 0;
                        editor.putInt(CommonStaticData.currentScaleModeKey, CommonStaticData.currentScaleMode);
                        editor.commit();
                    }
                }
            }
        });

        captureB = (ImageButton) findViewById(R.id.button_capture);
        if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_SWCODEC) {
            captureB.setVisibility(View.VISIBLE);
        } else {
            captureB.setVisibility(View.GONE);
        }

        if (captureB.getVisibility() == View.VISIBLE) {
            captureB.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (ll_age_limit.getVisibility() == View.INVISIBLE) {
                            if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_SWCODEC) {
                                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                captureB.setScaleX(0.8f);
                                captureB.setScaleY(0.8f);
                                    captureB.setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                                    try {
                                        Thread.sleep(BUTTON_CLICK_TIME);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    captureB.setScaleX(1.0f);
                                    captureB.setScaleY(1.0f);
                                    captureB.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
                            }
                        }
                    }
                    return false;
                }
            });

            captureB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ll_age_limit.getVisibility() == View.INVISIBLE) {
                        if ((buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                                || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB
                                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) && (mUsbChipType == USB_CHIP_TYPE_NONE)) {          // justin add
                            CustomToast toast12 = new CustomToast(getApplicationContext());
                            toast12.showToast(getApplicationContext(), getApplicationContext().getString(R.string.usb_dongle_not_attached), Toast.LENGTH_SHORT);
                        } else {
                            if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_SWCODEC) {

                                if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_SWCODEC) {
                                    if (isStoragePermissionGranted() == true && mCursor != null) { // for Android M Permission
                                        //mLastClickTimeRec = SystemClock.elapsedRealtime();
                                        //if (isRec == false) {  // available capture on recording
                                        // change rec image
                                        String channelName = mCursor.getString(CommonStaticData.COLUMN_INDEX_SERVICE_NAME);
                                        int serviceID = mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_ID);
                                        channelName = channelName.substring(4);
                                        channelName = channelName.replaceAll("[^a-zA-Z0-9]", "");


                                        Date now = new Date();
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());

                                        recordAndCapturePath filePath = getCurrentCapturePath();

                                        captureFileName = filePath.fullPath + sdf.format(now) + "_" + channelName + ".png";

                                        TVlog.i(TAG, " Capture Name = " + captureFileName);
                                        status_bar.setVisibility(View.INVISIBLE);
                                        controllerLayout.setVisibility(View.INVISIBLE);

                                        //live
                                        //if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_SWCODEC) {
                                        if (buildOption.USE_MULTI_WINDOW) {
                                            ll_multiWindow.setVisibility(View.GONE);
                                        }
                                        //}

                                        //ll_chat.setVisibility(View.INVISIBLE);
                                        if (buildOption.USE_CHAT_FUNCTION) {
                                            ll_chat.setVisibility(View.GONE);
                                        }

                                        int isCaptured = FCI_TVi.DoCapture(captureFileName);

                                        instance.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                                                Uri.parse("file://" + captureFileName)));

                                        //int isCaptured = FCI_TVi.DoCapture(captureFileName);

                                        if (isCaptured == 1) {
                                            if (buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                                                    || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB
                                                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                                    || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                                                CustomToast toast = new CustomToast(getApplicationContext());
                                                toast.showToast(getApplicationContext(), getApplicationContext().getString(R.string.captured_to_file) + "\n"
                                                        + getApplicationContext().getString(R.string.phone_storage_capture) + "\n"
                                                        + sdf.format(now) + "_" + channelName + ".png", Toast.LENGTH_SHORT);
                                            } else {
                                                if (SDK_INT < Build.VERSION_CODES.N) {
                                                    if (getExternalMounts().size() != 0) {

                                                        CustomToast toast = new CustomToast(getApplicationContext());
                                                        toast.showToast(getApplicationContext(), getApplicationContext().getString(R.string.captured_to_file) + "\n"
                                                                + getApplicationContext().getString(R.string.sd_storage_capture) + "\n"
                                                                + sdf.format(now) + "_" + channelName + ".png", Toast.LENGTH_SHORT);
                                                    } else {
                                                        CustomToast toast = new CustomToast(getApplicationContext());
                                                        toast.showToast(getApplicationContext(), getApplicationContext().getString(R.string.captured_to_file) + "\n"
                                                                + getApplicationContext().getString(R.string.phone_storage_capture) + "\n"
                                                                + sdf.format(now) + "_" + channelName + ".png", Toast.LENGTH_SHORT);
                                                    }
                                                } else {
                                                    CustomToast toast = new CustomToast(getApplicationContext());
                                                    toast.showToast(getApplicationContext(), getApplicationContext().getString(R.string.captured_to_file) + "\n"
                                                            + getApplicationContext().getString(R.string.phone_storage_capture) + "\n"
                                                            + sdf.format(now) + "_" + channelName + ".png", Toast.LENGTH_SHORT);
                                                }
                                            }

                                        } else {
                                            CustomToast toast = new CustomToast(getApplicationContext());
                                            toast.showToast(getApplicationContext(), getApplicationContext().getString(R.string.capture_fail), Toast.LENGTH_SHORT);
                                        }
                                    } else {
                                        CustomToast toast = new CustomToast(getApplicationContext());
                                        toast.showToast(getApplicationContext(), getApplicationContext().getString(R.string.permission_for_capture), Toast.LENGTH_SHORT);
                                    }

                                }
                            }
                        }
                    } else if (ll_age_limit.getVisibility() == View.VISIBLE) {
                        CustomToast toast = new CustomToast(getApplicationContext());
                        toast.showToast(getApplicationContext(), getApplicationContext().getString(R.string.age_limit_title), Toast.LENGTH_SHORT);
                    }
                }
            });
        }

        ll_recTimeview = (LinearLayout) findViewById(R.id.ll_rectTimeView);
        ll_recTimeview.setVisibility(View.INVISIBLE);

        recTimeview = (TextView)findViewById(R.id.rectTimeView);
        if (SDK_INT <= 19) {
            recTimeview.setTextColor(getResources().getColor(R.color.white));
        }
        recB = (ImageButton)findViewById(R.id.button_rec);
        recB.setPadding(0, 10, 0, 10);
        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE || buildOption.RECORD_FUNCTION_USE == false) {
            recB.setVisibility(View.GONE);
            recB.setEnabled(false);
        } else {
            recB.setVisibility(View.VISIBLE);
            recB.setEnabled(true);
        }

        recB.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (ll_age_limit.getVisibility() == View.INVISIBLE) {
                    if ((buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) && (mUsbChipType == USB_CHIP_TYPE_NONE)) {          // justin add
                        //if (event.getAction() == MotionEvent.ACTION_UP) {
                        CustomToast toast12 = new CustomToast(getApplicationContext());
                        toast12.showToast(getApplicationContext(), getApplicationContext().getString(R.string.usb_dongle_not_attached), Toast.LENGTH_SHORT);
                        // }
                    } else {
                        if (changeChannelView.getVisibility() != View.VISIBLE) {
                            // mis-clicking prevention, using threshold of 1500 ms
                            if (SystemClock.elapsedRealtime() - mLastClickTimeRec < DOUBLE_CLICK_TOLERANCE) {
                                return;
                            }

                            if (isStoragePermissionGranted() == true) { // for Android M Permission
                                mLastClickTimeRec = SystemClock.elapsedRealtime();
                                if (isRec == false) {

                                    if ((AudioFormat == 0x08 || AudioFormat == 0x40 || AudioFormat == 0x20 || AudioFormat == 0x60) || VideoFormat == 0x04) {  // live add AudioFormat == 0x20 (MP2)

                                        if (isRec == false) {
                                            // change rec image
                                            String channelName = mCursor.getString(CommonStaticData.COLUMN_INDEX_SERVICE_NAME);
                                            int serviceID = mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_ID);
                                            channelName = channelName.substring(4);
                                            channelName = channelName.replaceAll("[^a-zA-Z0-9]", "");

                                            mIsRecStarted = false;
                                            recordAndCapturePath filePath = getCurrentRecordingPath();

                                            if (buildOption.GUI_STYLE == 2 || buildOption.GUI_STYLE == 3) {
                                                Date now = new Date();
                                                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());

                                                if (buildOption.RECORDING_TYPE == buildOption.RECORDING_TYPE_TS) {
                                                    //recordingFileName = getCurrentRecordingPath() + channelName + "_" +
                                                    recordingFileName = filePath.fullPath + sdf.format(now) + "_" + channelName + ".tv";
                                                } else {
                                                    recordingFileName = filePath.fullPath + sdf.format(now) + "_" + channelName + ".mp4";
                                                }

                                            } else {
                                                Time today = new Time(Time.getCurrentTimezone());
                                                today.setToNow();


                                                if (buildOption.RECORDING_TYPE == buildOption.RECORDING_TYPE_TS) {
                                                    //recordingFileName = getCurrentRecordingPath() + channelName + "_" +
                                                    recordingFileName = filePath.fullPath + channelName + "_" +
                                                            today.year + "-" +
                                                            (today.month + 1) + "-" +
                                                            today.monthDay + "_" +
                                                            today.format("%H:%M:%S").replaceAll(":", "-") +
                                                            ".tv";
                                                } else { //(buildOption.RECORDING_TYPE == buildOption.RECORDING_TYPE_MP4)
                                                    //recordingFileName = getCurrentRecordingPath() + channelName + " " +
                                                    recordingFileName = filePath.fullPath + channelName + "_" +
                                                            today.year + "-" +
                                                            (today.month + 1) + "-" +
                                                            today.monthDay + "_" +
                                                            today.format("%H:%M:%S").replaceAll(":", "-") +
                                                            ".mp4";
                                                }
                                            }

                                            TVlog.i(TAG, " Recording Name = " + recordingFileName);
                                            FCI_TVi.RecStart(instance, serviceID, recordingFileName, false);
                                            isRec = true;
                                            ll_recTimeview.setVisibility(View.VISIBLE);
                                            recTimeview.setText("");
                                            recB.setImageResource(R.drawable.ic_stop_red);

                                            if (buildOption.GUI_STYLE == 2 || buildOption.GUI_STYLE == 3) {
                                                rightB.setVisibility(View.INVISIBLE);
                                                leftB.setVisibility(View.INVISIBLE);
                                            }

                                            //live
                                            //if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_SWCODEC) {
                                            if (buildOption.USE_MULTI_WINDOW) {
                                                ll_multiWindow.setVisibility(View.GONE);
                                            }
                                            //}
                                        } else {
                                            recordingStop(false);

                                        }
                                    } else {
                                        new InputDialog(instance, InputDialog.TYPE_NOT_SUPPORT_RECORD, null, null, null);
                                    }
                                } else {
                                    recordingStop(false);
                                }
                            } else {
                                CustomToast toast = new CustomToast(getApplicationContext());
                                toast.showToast(getApplicationContext(), getApplicationContext().getString(R.string.permission_need_recording), Toast.LENGTH_SHORT);
                            }
                        } else {
                            CustomToast toast = new CustomToast(getApplicationContext());
                            toast.showToast(getApplicationContext(), getApplicationContext().getString(R.string.recordNotSupportFormat), Toast.LENGTH_SHORT);
                        }
                    }
                } else if (ll_age_limit.getVisibility() == View.VISIBLE) {
                    CustomToast toast = new CustomToast(getApplicationContext());
                    toast.showToast(getApplicationContext(), getApplicationContext().getString(R.string.age_limit_title), Toast.LENGTH_SHORT);
                }
            }
        });


        recfileB = (ImageButton)findViewById(R.id.button_recfile);
        recfileB.setPadding(0, 10, 0, 10);
        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE || buildOption.RECORD_FUNCTION_USE == false) {
            recfileB.setVisibility(View.GONE);
            recfileB.setEnabled(false);
        } else {
            recfileB.setVisibility(View.VISIBLE);
            recfileB.setEnabled(true);
        }
        recfileB.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    recfileB.setScaleX(0.8f);
                    recfileB.setScaleY(0.8f);
                    recfileB.setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    try {
                        Thread.sleep(BUTTON_CLICK_TIME);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    recfileB.setScaleX(1.0f);
                    recfileB.setScaleY(1.0f);
                    recfileB.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
                }
                return false;
            }
        });
        recfileB.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                hideController();
                // mis-clicking prevention, using threshold of 1500 ms
                if (SystemClock.elapsedRealtime() - mLastClickTimeRecF < DOUBLE_CLICK_TOLERANCE){
                    return;
                }
                mLastClickTimeRecF = SystemClock.elapsedRealtime();
                if (isStoragePermissionGranted() == true) {  // for Android M Permission
                    mLastClickTimeRecF = SystemClock.elapsedRealtime();
                        Intent intent = new Intent(MainActivity.this, RecordedFileListActivity.class);
                        MainActivity.isMainActivity = true;
                        isPlayBackActivity = true;
                        startActivity(intent);
                    } else {
                        CustomToast toast = new CustomToast(getApplicationContext());
                        toast.showToast(getApplicationContext(), getApplicationContext().getString(R.string.permission_need_play), Toast.LENGTH_SHORT);
                }
            }
        });

        setB=(ImageButton)findViewById(R.id.button_set);
        setB.setPadding(0, 10, 0, 10);
        setB.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    setB.setScaleX(0.8f);
                    setB.setScaleY(0.8f);
                    setB.setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    try {
                        Thread.sleep(BUTTON_CLICK_TIME);
                    } catch (Exception e) {
                            e.printStackTrace();
                        }
                        setB.setScaleX(1.0f);
                    setB.setScaleY(1.0f);
                    setB.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
                }
                return false;
            }
        });
        setB.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if ((buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                        || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB
                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                        || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) && (mUsbChipType == USB_CHIP_TYPE_NONE)) {          // justin add
                    CustomToast toast12 = new CustomToast(getApplicationContext());
                    toast12.showToast(getApplicationContext(), getApplicationContext().getString(R.string.usb_dongle_not_attached), Toast.LENGTH_SHORT);
                } else {
                    // mis-clicking prevention, using threshold of 1500 ms
                    if (SystemClock.elapsedRealtime() - mLastClickTimeSet < DOUBLE_CLICK_TOLERANCE){
                        return;
                    }
                    mLastClickTimeSet = SystemClock.elapsedRealtime();
                    hideController();
                    setChangeProcMemory();
                    setChangeProcShift();
                    Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                    startActivity(intent);
                }
            }
        });

        /*
        arr_svcmodeswitch_jp = getResources().getStringArray(R.array.svcmode_switch_jp);
        receiveModeB = (Button) findViewById(R.id.button_mode);
        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
            CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
            int receiveMode = CommonStaticData.settings.getInt(CommonStaticData.receivemodeSwitchKey, CommonStaticData.RECEIVE_MODE_AUTO);  // auto
            receiveModeB.setText(arr_svcmodeswitch_jp[receiveMode]);
        }

        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
            if (setB != null) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)setB.getLayoutParams();
                params.rightMargin=10;
                setB.setLayoutParams(params);
            }

            receiveModeB.setVisibility(View.VISIBLE);

            CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
            CommonStaticData.scanCHnum = CommonStaticData.settings.getInt(CommonStaticData.scanedChannelsKey, 0);
            receiveModeB.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        receiveModeB.setScaleX(0.8f);
                        receiveModeB.setScaleY(0.8f);
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        receiveModeB.setScaleX(1.0f);
                        receiveModeB.setScaleY(1.0f);
                    }
                    return false;
                }
            });
            receiveModeB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (CommonStaticData.scanCHnum > 0) {
                        int isChanged = 0;
                        int[] info = FCI_TVi.GetPairNSegInfoOfCHIndex(CommonStaticData.lastCH);
                        int isPaired = 0;
                        int pairedIndex = info[0];
                        int segInfo = info[1];
                        int mainIndex = info[3];
                        int oneSegIndex = info[4];
                        if (pairedIndex >= 0) {
                            isPaired = 1;
                        }

                        CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = CommonStaticData.settings.edit();

                        if (CommonStaticData.receivemode == CommonStaticData.RECEIVE_MODE_FULLSEG) {  //fullseg --> 1seg
                            if (oneSegIndex != -1) {
                                CommonStaticData.receivemode = CommonStaticData.RECEIVE_MODE_1SEG;
                                MainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_SWITCHING, 0, 0, null);
                                receiveModeB.setText(arr_svcmodeswitch_jp[0]);
                                editor.putInt(CommonStaticData.receivemodeSwitchKey, CommonStaticData.receivemode);
                                editor.commit();
                            } else {
                                CommonStaticData.receivemode = CommonStaticData.RECEIVE_MODE_AUTO;  // fullseg --> auto
                                receiveModeB.setText(arr_svcmodeswitch_jp[2]);
                                editor.putInt(CommonStaticData.receivemodeSwitchKey, CommonStaticData.receivemode);
                                editor.commit();
                                customToast.showToast(getApplicationContext(), getApplicationContext().getString(R.string.switch_fail_SD), Toast.LENGTH_SHORT);
                            }

                        } else if (CommonStaticData.receivemode == CommonStaticData.RECEIVE_MODE_1SEG) {  //1seg --> auto
                            CommonStaticData.receivemode = CommonStaticData.RECEIVE_MODE_AUTO;
                            receiveModeB.setText(arr_svcmodeswitch_jp[2]);
                            editor.putInt(CommonStaticData.receivemodeSwitchKey, CommonStaticData.receivemode);
                            editor.commit();
                        } else if (CommonStaticData.receivemode == CommonStaticData.RECEIVE_MODE_AUTO) {  //auto --> off
                            CommonStaticData.receivemode = CommonStaticData.RECEIVE_MODE_OFF;
                            receiveModeB.setText(arr_svcmodeswitch_jp[3]);
                            editor.putInt(CommonStaticData.receivemodeSwitchKey, CommonStaticData.receivemode);
                            editor.commit();
                        } else if (CommonStaticData.receivemode == CommonStaticData.RECEIVE_MODE_OFF) {  //off --> fullseg
                            if (mainIndex != -1) {
                                CommonStaticData.receivemode = CommonStaticData.RECEIVE_MODE_FULLSEG;
                                MainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_SWITCHING, 1, 0, null);
                                receiveModeB.setText(arr_svcmodeswitch_jp[1]);
                                editor.putInt(CommonStaticData.receivemodeSwitchKey, CommonStaticData.receivemode);
                                editor.commit();
                            } else {
                                CommonStaticData.receivemode = CommonStaticData.RECEIVE_MODE_1SEG;  // off --> 1seg
                                receiveModeB.setText(arr_svcmodeswitch_jp[0]);
                                editor.putInt(CommonStaticData.receivemodeSwitchKey, CommonStaticData.receivemode);
                                editor.commit();
                                customToast.showToast(getApplicationContext(), getApplicationContext().getString(R.string.switch_fail_HD), Toast.LENGTH_SHORT);
                            }
                        }
                    } else {
                        customToast.showToast(getApplicationContext(), getApplicationContext().getString(R.string.no_channel_tip), Toast.LENGTH_SHORT);
                    }
                }
            });
        } else {
            if (setB != null) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)setB.getLayoutParams();
                params.rightMargin=40;
                setB.setLayoutParams(params);
            }
            if (receiveModeB != null) {
                receiveModeB.setVisibility(View.GONE);
            }
        }*/

        subTitleView = (TextView)findViewById(R.id.subTitleView);

        superImposeView = (TextView)findViewById(R.id.superImposeView);
        // live add
        if (CommonStaticData.superimposeSwitch == true) {
            superImposeView.setVisibility(View.VISIBLE);
        } else {
            superImposeView.setVisibility(View.INVISIBLE);
        }
        //

        debugScreen = (TextView)findViewById(R.id.debugScreenView);

        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
            // [[ solution switching mode 20170223
            envSet_JP();
            //]]
        }
        else {
            envSet_Normal();
        }

        changeChannelView = (LinearLayout) findViewById(R.id.progressBarCircularIndeterminate);
        changeChannelView.setVisibility(View.INVISIBLE);

        //progressingChange=(CustomView)findViewById(R.id.progressing_channel);
        progressingChange = (ProgressBar)findViewById(R.id.progressing_channel);
        progressingChange.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);

        loadingChannel = (TextView)findViewById(R.id.loading_channel);
        loadingFilePlay = (TextView)findViewById(R.id.loading_fileplay);    // justin

        controllerLayout = (LinearLayout) findViewById(R.id.controllerLayout);

        //  controllerLayout.setVisibility(View.VISIBLE);  //eddy remove

        // live add for Revoview 7inch myT1 model
        if (buildOption.GUI_STYLE == 3) {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            int size = Math.round(42 * dm.density);
            RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) controllerLayout.getLayoutParams();
            param.setMargins(0,0,0,size);
            controllerLayout.setLayoutParams(param);
        }

        //if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 2 || buildOption.GUI_STYLE == 3) {
        status_bar = (LinearLayout) findViewById(R.id.status_bar);
        if (status_bar != null) {
            status_bar.setVisibility(View.VISIBLE);
        }
        //}
        earphoneLayout = (RelativeLayout) findViewById(R.id.earphoneLayout);
        earphoneLayout.setVisibility(View.INVISIBLE);
        if (buildOption.ADD_LOUD_SPEAKER) {
            if (is_wired_headset) {
                earphoneLayout.setVisibility(View.VISIBLE);
            } else {
                earphoneLayout.setVisibility(View.INVISIBLE);
            }

            earphone = (ImageButton) findViewById(R.id.earphone);
            earphone.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {

                    earphone.setVisibility(View.INVISIBLE);
                    speaker.setVisibility(View.VISIBLE);

                    if (speaker.getVisibility() == View.VISIBLE) {
                        ObjectAnimator animator = ObjectAnimator.ofFloat(speaker, "rotationX", 0, 360);
                        animator.setDuration(1000);
                        animator.start();
                    }

                    audioOut.setSpeakerMode(true);
                    CommonStaticData.loudSpeaker = true;

                }
            });

            speaker = (ImageButton) findViewById(R.id.speaker);
            speaker.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {

                    speaker.setVisibility(View.INVISIBLE);
                    earphone.setVisibility(View.VISIBLE);

                    if (earphone.getVisibility() == View.VISIBLE) {
                        ObjectAnimator animator = ObjectAnimator.ofFloat(earphone, "rotationX", 0, 360);
                        animator.setDuration(1000);
                        animator.start();
                    }

                    audioOut.setSpeakerMode(false);
                    CommonStaticData.loudSpeaker = false;
                }
            });
        }

        //////////// Gesture seekbar
        mVerticalVolumeProgress = (VerticalSeekBar) findViewById(R.id.seekbar_vol);
        mVerticalBrightProgress = (VerticalSeekBar) findViewById(R.id.seekbar_bri);

        volumebarLayout = (RelativeLayout) findViewById(R.id.volumebar);
        volumebarLayout.setVisibility(View.INVISIBLE);

        img_volumebar = (ImageView) findViewById(R.id.img_volumebar);
        img_volumebar_off = (ImageView) findViewById(R.id.img_volumebar_off);

        brightbarLayout = (RelativeLayout) findViewById(R.id.brightbar);
        brightbarLayout.setVisibility(View.INVISIBLE);

        //CommonStaticData.brightness = CommonStaticData.settings.getInt(CommonStaticData.brightnessKey,15);
        player_bright = CommonStaticData.brightness;
        if (player_bright < 1){
            player_bright =1;
        } else if (player_bright > 15){
            player_bright = 15;
        }
        WindowManager.LayoutParams plp = this.getWindow().getAttributes();
        plp.screenBrightness = (float) player_bright / 15;
        this.getWindow().setAttributes(plp);

        mVerticalBrightProgress.setMax(15);
        mVerticalBrightProgress.setProgress(player_bright);

        if (mVerticalBrightProgress != null){
            mVerticalBrightProgress.setOnSeekBarChangeListener(new VerticalSeekBar.OnSeekBarChangeListener(){

                @Override
                public void onProgressChanged(kr.co.fci.tv.gesture.VerticalSeekBar seekBar, int progress, boolean fromUser) {
                    if (!fromUser) {
                        return;
                    }
                    CommonStaticData.brightness = progress;

                    CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                    editor.putInt(CommonStaticData.brightnessKey, CommonStaticData.brightness);
                    editor.commit();
                    setBrightness(progress);
                }

                @Override
                public void onStartTrackingTouch(kr.co.fci.tv.gesture.VerticalSeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(kr.co.fci.tv.gesture.VerticalSeekBar seekBar) {

                }
            });
        }

        final AudioManager audMgr = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
        //is_wired_headset_on = audMgr.isWiredHeadsetOn();
        int vol = audMgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        int max = audMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        if (mVerticalVolumeProgress != null){
            mVerticalVolumeProgress.setMax(max);
            mVerticalVolumeProgress.setProgress(vol);
            mVerticalVolumeProgress.setOnSeekBarChangeListener(new VerticalSeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(kr.co.fci.tv.gesture.VerticalSeekBar seekBar, int progress, boolean fromUser) {
                    if (!fromUser) {
                        return;
                    }
                    audMgr.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);  // live add
                }

                @Override
                public void onStartTrackingTouch(kr.co.fci.tv.gesture.VerticalSeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(kr.co.fci.tv.gesture.VerticalSeekBar seekBar) {

                }
            });
        }
        ///////////////////////
        if (buildOption.GUI_STYLE == 2 || buildOption.GUI_STYLE == 3){
            ll_ch_menu = (LinearLayout) findViewById(R.id.ll_ch_menu);
            ll_ch_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) && (mUsbChipType == USB_CHIP_TYPE_NONE)) {          // justin add
                        // if (event.getAction() == MotionEvent.ACTION_UP) {
                        CustomToast toast12 = new CustomToast(getApplicationContext());
                        toast12.showToast(getApplicationContext(), getApplicationContext().getString(R.string.usb_dongle_not_attached), Toast.LENGTH_SHORT);
                        //  }
                    } else {
                        // mis-clicking prevention, using threshold of 1500 ms
                        if (SystemClock.elapsedRealtime() - mLastClickTimeCHMenu < DOUBLE_CLICK_TOLERANCE) {
                            return;
                        }
                        mLastClickTimeCHMenu = SystemClock.elapsedRealtime();
                        TVlog.i(TAG, " >>>>> Channel menu is clicked!!");
                        MainActivity.isMainActivity = true;
                        setChangeProcMemory();
                        isChannelListViewOn = true;
                        setChangeProcShift();

                        Intent intent = new Intent(MainActivity.this, ChannelMainActivity.class);
                        startActivity(intent);
                        hideController();
                        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);  // live add
                    }
                }
            });

            ll_ch_info = (LinearLayout) findViewById(R.id.ll_ch_info);
            ll_ch_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) && (mUsbChipType == USB_CHIP_TYPE_NONE)) {          // justin add
                        // if (event.getAction() == MotionEvent.ACTION_UP) {
                        CustomToast toast12 = new CustomToast(getApplicationContext());
                        toast12.showToast(getApplicationContext(), getApplicationContext().getString(R.string.usb_dongle_not_attached), Toast.LENGTH_SHORT);
                        // }
                    } else {
                        // mis-clicking prevention, using threshold of 1500 ms
                        if (SystemClock.elapsedRealtime() - mLastClickTimeCHMenu < DOUBLE_CLICK_TOLERANCE) {
                            return;
                        }
                        mLastClickTimeCHMenu = SystemClock.elapsedRealtime();
                        TVlog.i(TAG, " >>>>> Channel menu is clicked!!");
                        MainActivity.isMainActivity = true;
                        setChangeProcMemory();
                        isChannelListViewOn = true;
                        setChangeProcShift();

                        Intent intent = new Intent(MainActivity.this, ChannelMainActivity.class);
                        startActivity(intent);
                        hideController();
                        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);  // live add
                    }
                }
            });
        }

        if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 ) {
            channelLayout = (LinearLayout) findViewById(R.id.channelLayout);
            channelLayout.setVisibility(View.VISIBLE);
        }

        if (buildOption.GUI_STYLE == 0 || buildOption.GUI_STYLE == 1) {
            ch_up = (ImageButton) findViewById(R.id.button_up);
            ch_up.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        ch_up.setScaleX(0.8f);
                        ch_up.setScaleY(0.8f);
                        ch_up.setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        try {
                            Thread.sleep(BUTTON_CLICK_TIME);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ch_up.setScaleX(1.0f);
                        ch_up.setScaleY(1.0f);
                        ch_up.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
                    }
                    return false;
                }
            });

            ch_up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) && (mUsbChipType == USB_CHIP_TYPE_NONE)) {          // justin add
                        CustomToast toast12 = new CustomToast(getApplicationContext());
                        toast12.showToast(getApplicationContext(), getApplicationContext().getString(R.string.usb_dongle_not_attached), Toast.LENGTH_SHORT);
                    } else {
                        // mis-clicking prevention, using threshold of 1500 ms
                        if (SystemClock.elapsedRealtime() - mLastClickTimeCHMenu < 500) {
                            return;
                        }
                        mLastClickTimeCHMenu = SystemClock.elapsedRealtime();
                        // justin DB
                        CommonStaticData.passwordVerifyFlag = false;
                        CommonStaticData.ageLimitFlag = false;
                        //channelChangeStartView(false);

                        //changeChannelView.setVisibility(View.VISIBLE);

                        sendEvent(E_CAPTION_CLEAR_NOTIFY);
                        sendEvent(TVEVENT.E_SUPERIMPOSE_CLEAR_NOTIFY);

                        if (ll_mainAutoSearch.getVisibility() == View.VISIBLE) {
                            ll_mainAutoSearch.setVisibility(View.INVISIBLE);
                        }

                        // live add
                        if (ll_scramble_msg.getVisibility() == View.VISIBLE) {
                            ll_scramble_msg.setVisibility(View.INVISIBLE);
                        }

                        ll_age_limit.setVisibility(View.INVISIBLE);

                        TVBridge.AVStartMinus();
                    }
                }
            });
        }

        if (buildOption.GUI_STYLE == 0 || buildOption.GUI_STYLE == 1) {
            ch_CherryMenu = (ImageButton) findViewById(R.id.button_tv);
            ch_CherryMenu.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if ((buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) && (mUsbChipType == USB_CHIP_TYPE_NONE)) {          // justin add
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            CustomToast toast12 = new CustomToast(getApplicationContext());
                            toast12.showToast(getApplicationContext(), getApplicationContext().getString(R.string.usb_dongle_not_attached), Toast.LENGTH_SHORT);
                        }
                    } else {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            ch_CherryMenu.setScaleX(0.8f);
                            ch_CherryMenu.setScaleY(0.8f);
                            ch_CherryMenu.setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
                            try {
                                Thread.sleep(BUTTON_CLICK_TIME);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            ch_CherryMenu.setScaleX(1.0f);
                            ch_CherryMenu.setScaleY(1.0f);
                            ch_CherryMenu.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);

                            hideController();

                            MainActivity.isMainActivity = true;
                            setChangeProcMemory();
                            isChannelListViewOn = true;
                            setChangeProcShift();

                            Intent intent = new Intent(MainActivity.this, ChannelMainActivity.class);

                            startActivity(intent);
                        }
                    }
                    return false;
                }
            });
        } else if (buildOption.GUI_STYLE == 2 || buildOption.GUI_STYLE == 3) {
            ch_MyphoneMenu = (ImageButton) findViewById(R.id.button_tv);
            ch_MyphoneMenu.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if ((buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) && (mUsbChipType == USB_CHIP_TYPE_NONE)) {          // justin add
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            CustomToast toast12 = new CustomToast(getApplicationContext());
                            toast12.showToast(getApplicationContext(), getApplicationContext().getString(R.string.usb_dongle_not_attached), Toast.LENGTH_SHORT);
                        }
                    } else {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            ch_MyphoneMenu.setScaleX(0.8f);
                            ch_MyphoneMenu.setScaleY(0.8f);
                            ch_MyphoneMenu.setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
                            try {
                                Thread.sleep(BUTTON_CLICK_TIME);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            ch_MyphoneMenu.setScaleX(1.0f);
                            ch_MyphoneMenu.setScaleY(1.0f);
                            ch_MyphoneMenu.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);

                            MainActivity.isMainActivity=true;
                            setChangeProcMemory();
                            isChannelListViewOn=true;
                            setChangeProcShift();

                            Intent intent = new Intent(MainActivity.this, ChannelMainActivity.class);

                            startActivity(intent);

                            hideController();
                            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);  // live add
                        }
                    }
                    return false;
                }
            });
        }

        if (buildOption.GUI_STYLE == 0 || buildOption.GUI_STYLE == 1) {
            ch_down = (ImageButton) findViewById(R.id.button_down);
            ch_down.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        ch_down.setScaleX(0.8f);
                        ch_down.setScaleY(0.8f);
                        ch_down.setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        try {
                            Thread.sleep(BUTTON_CLICK_TIME);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ch_down.setScaleX(1.0f);
                        ch_down.setScaleY(1.0f);
                        ch_down.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
                    }
                    return false;
                }
            });
            ch_down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) && (mUsbChipType == USB_CHIP_TYPE_NONE)) {          // justin add
                        CustomToast toast12 = new CustomToast(getApplicationContext());
                        toast12.showToast(getApplicationContext(), getApplicationContext().getString(R.string.usb_dongle_not_attached), Toast.LENGTH_SHORT);
                    } else {
                        // mis-clicking prevention, using threshold of 1500 ms
                        if (SystemClock.elapsedRealtime() - mLastClickTimeCHMenu < 500) {
                            return;
                        }
                        mLastClickTimeCHMenu = SystemClock.elapsedRealtime();
                        CommonStaticData.passwordVerifyFlag = false;
                        CommonStaticData.ageLimitFlag = false;
                        //channelChangeStartView(false);

                        sendEvent(E_CAPTION_CLEAR_NOTIFY);
                        sendEvent(TVEVENT.E_SUPERIMPOSE_CLEAR_NOTIFY);

                        // live add
                        if (ll_scramble_msg.getVisibility() == View.VISIBLE) {
                            ll_scramble_msg.setVisibility(View.INVISIBLE);
                        }

                        ll_age_limit.setVisibility(View.INVISIBLE);

                        TVBridge.AVStartPlus();
                    }
                }
            });
        }

        if (SurfaceRotationOn ==true) {
            sv.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View arg0, MotionEvent arg1) {

                    if (toggleTest == false) // portrait --> landscape
                    {
                        TVlog.i(TAG, " >>>>> to landscape mode ");
                        DisplayMetrics displaymetrics = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

                        frameHeight = sv.getMeasuredHeight();
                        frameWidth = sv.getMeasuredWidth();

                        Display display = getWindowManager().getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);
                        int width = size.x;
                        int height = size.y;

                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);  // live

                        android.view.ViewGroup.LayoutParams lp = sv.getLayoutParams();
                        lp.width = (int) height;
                        lp.height = (int) width;
                        sv.setLayoutParams(lp);
                        if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                            if (svSub != null)
                            {
                                svSub.setLayoutParams(lp);
                            }
                        }
                        toggleTest = true;
                    } else {
                        TVlog.i(TAG, " >>>>> to portrait  mode ");
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        android.view.ViewGroup.LayoutParams lp = sv.getLayoutParams();
                        lp.width = (int) frameWidth;
                        lp.height = (int) frameHeight;
                        sv.setLayoutParams(lp);
                        if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                            if (svSub != null)
                            {
                                svSub.setLayoutParams(lp);
                            }
                        }
                        toggleTest = false;
                    }

                    return false;
                }
            });
        }

        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener()) {
            @Override
            public boolean onTouchEvent(MotionEvent ev) {
                return super.onTouchEvent(ev);
            }
        };

        postEvent(TVEVENT.E_HIDE_CONTROLER, CONTROLLER_HIDE_TIME);  // live add

    }

    // [[ solution switching mode 20170223
    public void envSet_JP() {

        mFont = Typeface.createFromAsset(getAssets(), "wlcmaru2004emoji.ttf");
        currCH.setTypeface(mFont);
        if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 ) {
            currProgram.setTypeface(mFont);
            currProgram.setPadding(0, 5, 0, 0);
        }


        //use free font
        subTitleView.setTypeface(mFont);
        superImposeView.setTypeface(mFont);
        debugScreen.setTypeface(mFont);


        //JAPAN_CAPTION[[
        Display displayCap = getWindowManager().getDefaultDisplay();
        Point sizeCap = new Point();
        displayCap.getRealSize(sizeCap);
        int capWidth = sizeCap.x;
        int capHeight = sizeCap.y;
        //TVlog.e(TAG, "caption real width="+capWidth+", caption real height="+capHeight);

        //caption
        mCaptionLayout = (FrameLayout)findViewById(R.id.frameLayout);
        mCaptionView = new CaptionDirectView(this, mCaptionLayout, capWidth, capHeight, mFont, M_TYPE_CAPTION_SUBTITLE);

        //superimpose
        mSuperimposeLayout = (FrameLayout)findViewById(R.id.frameLayout);
        mSuperimposeView = new CaptionDirectView(this, mSuperimposeLayout, capWidth, capHeight, mFont, M_TYPE_CAPTION_SUPERIMPOSE);

        //caption
        mCaptionView.setVisibility(View.VISIBLE);
        mCaptionLayout.addView(mCaptionView);
        setContentView(mCaptionLayout);

        //superimpose
        mSuperimposeLayout.addView(mSuperimposeView);
        setContentView(mSuperimposeLayout);
        mSuperimposeView.setVisibility(View.VISIBLE);

        //]]JAPAN_CAPTION
    }

    public void envSet_Normal() {
        mFont = Typeface.DEFAULT;
        currCH.setTypeface(mFont);
        if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 ) {
            currProgram.setTypeface(mFont);
        }

        subTitleView.setTypeface(mFont);
        superImposeView.setTypeface(mFont);
        debugScreen.setTypeface(mFont);

        if (buildOption.ADD_GINGA_NCL == true) {
        }
    }


    private final int EPG_TIME_NOT_DEFINED = 0xFF;
    private final String EPG_TIME_STR_NOT_DEFINED = "??:??";
    private final String EPG_TIME_STR_FORMAT_SPECIFIER = "%02d:%02d";
    private final String EPG_TIME_STR_SEPARATOR = "~";
    private final int EPG_UPDATE_TYPE_PF = 0;
    private final int EPG_UPDATE_TYPE_SCH = 1;

    private void updateCurEPGNameNDuration()
    {
        TVlog.i("live", " >>> updateCurEPGNameNDuration()");
        String progName = FCI_TVi.GetServiceName();
        TVlog.i("live", " >>> progName = "+progName);

        if (progName.length() != 0) {
            //duration
            String epgStartNDuration = null;
            String epgStartTime = null;
            String epgEndTime = null;
            int mCurWeekDay = 0;
            //int EPGIndex;
            int startYear, startMonth, startDay, startHour, startMin, endYear, endMonth, endDay, endHour, endMin, tmpHour, tmpMin;
            int date[] = FCI_TVi.GetTSNetTime();
            mCurWeekDay = MainActivity.DayFun(date[0], date[1], date[2]);
            FCI_TVi.GetEPGCount(mCurWeekDay, mChannelIndex);
            int[] startNDuration = FCI_TVi.GetEPGStartTimeNDuration(mCurWeekDay, 0);

            if (startNDuration != null) {
                startYear = startNDuration[0];
                startMonth = startNDuration[1];
                startDay = startNDuration[2];
                startHour = startNDuration[3];
                startMin = startNDuration[4];
                tmpHour = startNDuration[6];
                tmpMin = startNDuration[7];
                //start time
                if (startHour == EPG_TIME_NOT_DEFINED || startMin == EPG_TIME_NOT_DEFINED) {
                    epgStartTime = EPG_TIME_STR_NOT_DEFINED;
                } else {
                    epgStartTime = String.format(EPG_TIME_STR_FORMAT_SPECIFIER, startHour, startMin);
                }
                //end time
                if (tmpHour == EPG_TIME_NOT_DEFINED || tmpMin == EPG_TIME_NOT_DEFINED) { //duration not defined.
                    epgEndTime = EPG_TIME_STR_NOT_DEFINED;
                } else {
                    endMin = startMin + startNDuration[7];
                    if (endMin >= 60) {
                        endHour = 1;
                        endMin = endMin - 60;
                    } else {
                        endHour = 0;
                    }
                    endHour = endHour + startHour + startNDuration[6];
                    if (endHour >= 24) {
                        endHour = endHour % 24;
                    }
                    epgEndTime = String.format(EPG_TIME_STR_FORMAT_SPECIFIER, endHour, endMin);
                }

                epgStartNDuration = epgStartTime + EPG_TIME_STR_SEPARATOR + epgEndTime;

                if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 )  {
                    //program name
                    currProgram.setText(progName);
                    currDuration.setText(epgStartNDuration);
                }

            }
        } else {
            if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 )  {
                currProgram.setText("- - -");
                currDuration.setText("--:--~--:--");
            }
        }
    }

    private void getCurrentDeviceResolution() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        density = displayMetrics.density;
        densityDpi = displayMetrics.densityDpi;
    }

    private void getCurrentDeviceDpi() {

        if (densityDpi <= 120) {
            dpiName = "ldpi";
        } else if (120 < densityDpi && densityDpi <= 160) {
            dpiName = "mdpi";
        } else if (160 < densityDpi && densityDpi <= 240) {
            dpiName = "hdpi";
        } else if (240 < densityDpi && densityDpi <= 320) {
            dpiName = "xhdpi";
        } else if (320 < densityDpi && densityDpi <= 480) {
            dpiName = "xxhdpi";
        } else if (480 < densityDpi && densityDpi <= 640) {
            dpiName = "xxxhdpi";
        }
    }

    private void getCurrentDeviceSize() {

        boolean isSmallScreen = (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL;
        boolean isNormalScreen = (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL;
        boolean isLargeScreen = (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE;
        boolean isXLargeScreen = (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE;

        if (isSmallScreen) {
            screenSize = "small";
        } else if (isNormalScreen) {
            screenSize = "normal";
        } else if (isLargeScreen) {
            screenSize = "large";
        } else if (isXLargeScreen) {
            screenSize ="xlarge";
        }
    }

    private void setChangeProcMemory()
    {
        if (channelChangeProcLocation ==null)
        {
            channelChangeProcLocation= new int[2];
            changeChannelView.getLocationInWindow(channelChangeProcLocation);
            TVlog.i(TAG, "== channelChangeProcLocation x =" + channelChangeProcLocation[0] + " y =" + channelChangeProcLocation[1]);
            if (channelChangeProcLocation[0]==0 || channelChangeProcLocation[1]==0)
            {
                channelChangeProcLocation = null;
            }
        }
    }

    private void setChangeProcShift()
    {
        if (MainActivity.isMainActivity==true) {

            TVlog.i(TAG, "== channelChangeStartView  channelListViewOn false ==");
            if (changeChannelView != null) {
                if (channelChangeProcLocation != null) {
                    changeChannelView.setX(channelChangeProcLocation[0]);
                    changeChannelView.setY(channelChangeProcLocation[1]);
                }
            }
        } else {
            TVlog.i(TAG, "== channelChangeStartView  channelListViewOn true ==");
            if (changeChannelView != null) {
                if (channelChangeProcLocation != null) {
                    changeChannelView.setX(channelChangeProcLocation[0] + buildOption.CHANNLE_CHANGE_PROC_SHIFT_X);
                    changeChannelView.setY(channelChangeProcLocation[1] + buildOption.CHANNLE_CHANGE_PROC_SHIFT_Y);
                }
            }
        }
    }

    public void channelChangeStartView(boolean _cas)
    {
        recTimeview.setText("");
        ll_recTimeview.setVisibility(View.INVISIBLE);
        recTimeview.setVisibility(View.INVISIBLE);

        setChangeProcMemory();
        setChangeProcShift();

        if (ll_noChannel.getVisibility() == View.VISIBLE) {
            ll_noChannel.setVisibility(View.INVISIBLE);
        }

        if (ll_noSignal.getVisibility() == View.VISIBLE) {
            ll_noSignal.setVisibility(View.INVISIBLE);
        }
        if (ll_scramble_msg.getVisibility() == View.VISIBLE) {
            ll_scramble_msg.setVisibility(View.INVISIBLE);
        }

        if (ll_mainAutoSearch.getVisibility() == View.VISIBLE) {
            ll_mainAutoSearch.setVisibility(View.INVISIBLE);
        }

        changeChannelView.setVisibility(View.VISIBLE);
        channelChangeBG.setVisibility(View.VISIBLE);
        progressingChange.setVisibility(View.VISIBLE);
        if (_cas == false) {
            loadingChannel.setVisibility(View.VISIBLE);
            loadingFilePlay.setVisibility(View.GONE);
        } else {   // call from playback
            loadingChannel.setVisibility(View.GONE);
            loadingFilePlay.setVisibility(View.VISIBLE);
        }

         if (buildOption.USE_MULTI_WINDOW) {
             if (changeChannelView.getVisibility() == View.VISIBLE) {
                 ll_multiWindow.setVisibility(View.GONE);
             } else {
                 ll_multiWindow.setVisibility(View.VISIBLE);
             }
         }
    }


    public void channelChangeEndView(boolean _keepBG)
    {
        if (_keepBG == false) {
            channelChangeBG.setVisibility(View.INVISIBLE);
        }
        recTimeview.setVisibility(View.VISIBLE);
        changeChannelView.setVisibility(View.INVISIBLE);
    }

    public int getChannelChangView()
    {
        int visual = changeChannelView.getVisibility();

        if (visual == 0)
        {
            TVlog.i(TAG, "Viewing Ch change view ");
            return 0;
        } else {
            TVlog.i(TAG, "No Viewing  Ch change view ");
            return 1;
        }
    }

    public void hideController() {
        //if (controllerLayout.isShown()) {

        if (!isUiLocked) {
            TVlog.i(TAG, "== hideController ==");
            uiOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            decorView.setSystemUiVisibility(uiOptions);

            //if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 2  || buildOption.GUI_STYLE == 3) {
            status_bar.setVisibility(View.INVISIBLE);
            //}
            controllerLayout.setVisibility(View.INVISIBLE);
            if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 ) {
                channelLayout.setVisibility(View.INVISIBLE);
            }
            if (buildOption.ADD_LOUD_SPEAKER) {
                earphoneLayout.setVisibility(View.INVISIBLE);
            }

            //if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_SWCODEC) {
            if (buildOption.USE_MULTI_WINDOW) {
                if (ll_multiWindow.getVisibility() == View.VISIBLE) {
                    ll_multiWindow.setVisibility(View.GONE);
                }
            }
            //}

            //ll_chat.setVisibility(View.INVISIBLE);
            if (buildOption.USE_CHAT_FUNCTION) {
                if (ll_chat.getVisibility() == View.VISIBLE) {
                    ll_chat.setVisibility(View.GONE);
                }
            }
        } else {
            uiOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            decorView.setSystemUiVisibility(uiOptions);
            ll_uiLocked.setVisibility(View.INVISIBLE);
        }
        //}
    }

    private void showController() {
        if (!isUiLocked) {
            status_bar.setVisibility(View.VISIBLE);

            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
//              receiveModeB.setText(arr_svcmodeswitch_jp[CommonStaticData.receivemode]);
            }

            controllerLayout.setVisibility(View.VISIBLE);
            if (buildOption.ADD_LOUD_SPEAKER) {
                if (is_wired_headset) {
                    earphoneLayout.setVisibility(View.VISIBLE);
                } else {
                    earphoneLayout.setVisibility(View.INVISIBLE);
                }
            }
            if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 )  {
                channelLayout.setVisibility(View.VISIBLE);
            }

            if (buildOption.USE_MULTI_WINDOW) {
                if (changeChannelView.getVisibility() == View.VISIBLE) {
                    ll_multiWindow.setVisibility(View.GONE);
                } else {
            ll_multiWindow.setVisibility(View.VISIBLE);
                }
            }

            if (buildOption.USE_CHAT_FUNCTION) {
                ll_chat.setVisibility(View.VISIBLE);
            } else {
                ll_chat.setVisibility(View.GONE);
            }
            postEvent(TVEVENT.E_HIDE_CONTROLER, CONTROLLER_HIDE_TIME);
        } else {
            ll_uiLocked.setVisibility(View.VISIBLE);
            postEvent(TVEVENT.E_HIDE_CONTROLER, CONTROLLER_HIDE_TIME);
        }
    }

    private void getScreenSize()
    {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
    }

    private void showSetting() {
        View popupView = getLayoutInflater().inflate(R.layout.activity_setting, null);
        settingWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        settingWindow.setAnimationStyle(-1);
        settingWindow.showAtLocation(popupView, Gravity.TOP, 0, 0);
        getScreenSize();
        settingWindow.update(0, 0, screenWidth, screenHeight);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        /*getMenuInflater().inflate(R.menu.menu_main, menu);*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    // justin for DB
    public void scanNotify(int idx, String desc, byte type, byte vFormat, byte aFormat, byte iFree, int remoteKey, int svcNum, int freqKHz, byte bLast) {
        ContentValues values = new ContentValues();
        if (bLast == 2) {
            if (mCursor != null) {
                int cursorCount = mCursor.getCount();
                if (cursorCount > 0 && cursorCount > mCursor.getPosition()) {
                    TVBridge.setLastRemoteKey(mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_REMOTE_KEY));
                    TVBridge.setLastSvcID(mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_NUMBER));
                    TVBridge.setLastListCount(cursorCount);
                    TVBridge.setLastFreq(mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_FREQ));
                    CommonStaticData.isProcessingUpdate = true;
                }
                else {
                    if (cursorCount > 0) {
                        //TVlog.i("FCIISDBT::", ">>> exception: current pos invalid >>> pos="+mCursor.getPosition()+"of count="+cursorCount);
                    }
                }
            }
            CommonStaticData.loadingNow = true;
            getContentResolver().delete(mUri,null,null);    // channel DB delete
        } else if (bLast == 1) {
            postEvent(TVEVENT.E_CHLIST_UPDATE, 0);
            postEvent(TVEVENT.E_EPGTITLE_UPDATE, 0);
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
            if (mUri != null) {
                getContentResolver().insert(mUri, values);
            }
        }
    }

    //check emulator
    public static boolean isRunningOnEmulator()
    {
        boolean result =//
                Build.FINGERPRINT.startsWith("generic")//
                        ||Build.FINGERPRINT.startsWith("unknown")//
                        ||Build.MODEL.contains("google_sdk")//
                        ||Build.MODEL.contains("Emulator")//
                        ||Build.MODEL.contains("Android SDK built for x86");
        if (result)
            return true;
        result|=Build.BRAND.startsWith("generic")&&Build.DEVICE.startsWith("generic");
        if (result)
            return true;
        result|="google_sdk".equals(Build.PRODUCT);
        return result;
    }

    float getBatteryLevel() {
        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        // Error checking that probably isn't needed but I added just in case.
        if (level == -1 || scale == -1) {
            return 50.0f;
        }
        //  return 40.0f; //for test
        return ((float)level / (float)scale) * 100.0f;
    }

    public void checkOnStart() {

        if (mUri != null) {
            getContentResolver().delete(mUri, null, null);
        }

        /*
        if (buildOption.USE_ANALYTICS_FUNCTION) {
            GoogleAnalytics.getInstance(this).reportActivityStart(this);
        }*/

        // justin for DB load
        CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);

        // sharedpreference init check justin
        CommonStaticData.versionName = CommonStaticData.settings.getString(CommonStaticData.versionNameKey, "");
        if (!CommonStaticData.versionName.equals(buildInformation.RELEASE_VERSION)){
            SharedPreferences.Editor editor = CommonStaticData.settings.edit();
            editor.clear();
            editor.commit();
        }
        CommonStaticData.captionSwitch = CommonStaticData.settings.getBoolean(CommonStaticData.captionSwitchKey, true);
        CommonStaticData.superimposeSwitch = CommonStaticData.settings.getBoolean(CommonStaticData.superimposeSwitchKey, true);
        CommonStaticData.lastCH = CommonStaticData.settings.getInt(CommonStaticData.lastChannelKey, 0);
        CommonStaticData.scanCHnum = CommonStaticData.settings.getInt(CommonStaticData.scanedChannelsKey, 0);
        CommonStaticData.PG_Rate = CommonStaticData.settings.getInt(CommonStaticData.parentalRatingKey, 0);
        CommonStaticData.PassWord = CommonStaticData.settings.getString(CommonStaticData.passwordKey, null);
        CommonStaticData.ratingsetSwitch = CommonStaticData.settings.getBoolean(CommonStaticData.parentalcontrolSwitchKey, true);
        CommonStaticData.loudSpeaker = CommonStaticData.settings.getBoolean(CommonStaticData.loudSpeakerKey, false);
        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) { // Japan
            CommonStaticData.localeSet = 10;
        } else if (buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_ONESEG
                || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_FILE){
            CommonStaticData.localeSet = 4;

        } else if (buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_ONESEG
                || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) { // Brazil

            // CommonStaticData.localeSet = CommonStaticData.settings.getInt(CommonStaticData.localeSetKey, 16);  // for Sri Lanka
            CommonStaticData.localeSet = 16;

        } else if (buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_ONESEG
                || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_FILE) { // Philippines
            CommonStaticData.localeSet = 15;

        } else {
            CommonStaticData.localeSet = 4;
        }
        CommonStaticData.brightness = CommonStaticData.settings.getInt(CommonStaticData.brightnessKey, 15);

        // live add
        if (strISDBMode.equalsIgnoreCase("ISDBT Oneseg")) {
            CommonStaticData.receivemode = CommonStaticData.settings.getInt(CommonStaticData.receivemodeSwitchKey, CommonStaticData.RECEIVE_MODE_1SEG); // 1seg
        } else {
            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB) {
                CommonStaticData.receivemode = CommonStaticData.settings.getInt(CommonStaticData.receivemodeSwitchKey, CommonStaticData.RECEIVE_MODE_AUTO);  // auto
            } else {
                CommonStaticData.receivemode = CommonStaticData.settings.getInt(CommonStaticData.receivemodeSwitchKey, CommonStaticData.RECEIVE_MODE_OFF);  // off
            }
        }
        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG) {
            CommonStaticData.autoSearch = CommonStaticData.settings.getInt(CommonStaticData.autoSearchSwitchKey, 0);
        } else {
            CommonStaticData.autoSearch = CommonStaticData.settings.getInt(CommonStaticData.autoSearchSwitchKey, 1);
        }
        //

        CommonStaticData.badSignalFlag = CommonStaticData.settings.getBoolean(CommonStaticData.badSignalFlagKey, false);
        CommonStaticData.encryptFlag = CommonStaticData.settings.getBoolean(CommonStaticData.encryptFlagKey, false);
        CommonStaticData.ageLimitFlag = CommonStaticData.settings.getBoolean(CommonStaticData.ageLimitFlagKey, false);
        CommonStaticData.passwordVerifyFlag = CommonStaticData.settings.getBoolean(CommonStaticData.passwordVerifyFlagKey, false);
        CommonStaticData.mainPasswordVerifyFlag = CommonStaticData.settings.getBoolean(CommonStaticData.mainPasswordVerifyFlagKey, false);
        //CommonStaticData.screenBlockFlag = CommonStaticData.settings.getBoolean(CommonStaticData.screenBlockFlagKey, false);

        CommonStaticData.returnMainFromChat = CommonStaticData.settings.getBoolean(CommonStaticData.returnMainFromChatKey, false);
        CommonStaticData.returnMainFromFloating = CommonStaticData.settings.getBoolean(CommonStaticData.returnMainFromFloatingKey, false);
        CommonStaticData.currentScaleMode = CommonStaticData.settings.getInt(CommonStaticData.currentScaleModeKey, 0);

        // to keep caption language set
        if (CommonStaticData.returnMainFromFloating == true ) {
            CommonStaticData.captionSelect = CommonStaticData.settings.getInt(CommonStaticData.captionSetKey,0);
            CommonStaticData.superimposeSelect = CommonStaticData.settings.getInt(CommonStaticData.superimposeSetKey,0);
        } else {
            CommonStaticData.captionSelect = 0;
            CommonStaticData.superimposeSelect = 0;
        }
        /*
        if (CommonStaticData.scanCHnum == 0) {
            CommonStaticData.onesegCh = CommonStaticData.fullsegCh = new int [255];
        } else {
            CommonStaticData.onesegCh = CommonStaticData.fullsegCh = new int [CommonStaticData.scanCHnum];
        }*/
        if (buildOption.ADD_GINGA_NCL == true) {
            CommonStaticData.interactiveSwitch = CommonStaticData.settings.getBoolean(CommonStaticData.interactiveKey, true);
        }

        // battery check continue
        CommonStaticData.battMonitorSet=CommonStaticData.settings.getInt(CommonStaticData.definedbattMonitorKey,0);
        if (CommonStaticData.battMonitorSet > 0) {
            postEvent(TVEVENT.E_BATTERY_LIMITED_CHECK, 5000);
        }

        mChannelIndex = CommonStaticData.lastCH;

        audioOut = new AudioOut(this);

        if (buildOption.ADD_LOUD_SPEAKER) {
            if (CommonStaticData.loudSpeaker) {
                earphone.setVisibility(View.INVISIBLE);
                speaker.setVisibility(View.VISIBLE);
            } else {
                earphone.setVisibility(View.VISIBLE);
                speaker.setVisibility(View.INVISIBLE);
            }
            audioOut.setSpeakerMode(CommonStaticData.loudSpeaker);
        }
        else {
            CommonStaticData.loudSpeaker = true;
            audioOut.setSpeakerMode(CommonStaticData.loudSpeaker);
        }

        // sleep timer init for none set
        CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = CommonStaticData.settings.edit();
        CommonStaticData.sleeptime = 0;
        editor.putInt(CommonStaticData.sleeptimerSwitchKey, CommonStaticData.sleeptime);
        editor.putInt(CommonStaticData.localeSetKey, CommonStaticData.localeSet);   // [[ solution switching mode 20170223
        CommonStaticData.versionName = buildInformation.RELEASE_VERSION;        // add by justin
        editor.putString(CommonStaticData.versionNameKey, CommonStaticData.versionName);
        editor.commit();
    }

    private class SysBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive (Context _ctx, Intent _intent) {
            String action =_intent.getAction();

            if (action.equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = _intent.getIntExtra("state", -1);

                switch(state) {
                    case 0: //disconnected
                        is_wired_headset = false;
                        TVlog.i("SysBroadcastReceiver", " >>>>> Headset is unpluged!!");
                        break;
                    case 1: //connected
                        is_wired_headset = true;
                        TVlog.i("SysBroadcastReceiver", " >>>>> Headset is pluged!!");
                        if (audioOut != null) {
                            if (buildOption.ADD_LOUD_SPEAKER) {
                                if (CommonStaticData.loudSpeaker) {
                                    earphone.setVisibility(View.INVISIBLE);
                                    speaker.setVisibility(View.VISIBLE);
                                } else {
                                    earphone.setVisibility(View.VISIBLE);
                                    speaker.setVisibility(View.INVISIBLE);
                                }
                                audioOut.setSpeakerMode(CommonStaticData.loudSpeaker);
                            } else {
                                audioOut.setSpeakerMode(CommonStaticData.loudSpeaker);
                            }
                            break;
                        }
                    default:
                        break;
                }
            }

            if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                TVlog.i(TAG, " >>>>> Intent.ACTION_SCREEN_OFF");
                SolutionStop();
            }
            // [[ eddy 160706
            if (action.equals(Intent.ACTION_USER_PRESENT)) {
                TVlog.i(TAG, " >>>>> Intent.ACTION_USER_PRESENT");
                if (isRunningInForeground())
                {
                    TVlog.i(TAG, " TV running fore ground");
                    if (buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL
                            || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_ONESEG
                            || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_FILE) {
                        if (CommonStaticData.ageLimitFlag == false) {
                            MainActivity.isMainActivity = true;
                            onStart_TV();
                        } else {
                            ll_age_limit.setVisibility(View.VISIBLE);
                        }
                    } else {
                        MainActivity.isMainActivity = true;
                        onStart_TV();
                    }
                }
            }
            // ]] eddy 160706

            // usbdongle[[
            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                    buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB ||
                    buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB ||
                    buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                if (ACTION_USB_PERMISSION.equals(action)) {
                    synchronized (this) {
                        UsbDevice device = (UsbDevice) _intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                        if (_intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            if (device != null) {
                                //call method to set up device communication
                                if (mUsbConnected == false) {
                                    openUsbDevice(device);
                                    mUsbConnected = true;
                                    TVlog.i("FCIISDBT::", "usb dongle opened !!!");
                                }
                            } else {
                                TVlog.i("FCIISDBT::", "usb dongle device not found !!!");
                            }
                        } else {
                            TVlog.i("FCIISDBT::", " usb permission denied for dongle !!!: " + device);
                        }
                    }
                }

                if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                    TVlog.i("FCIISDBT::", "usb dongle detached !: UsbChip="+mUsbChipType+", LME mode="+mUsbLMEMode);
                    if (mUsbChipType == USB_CHIP_TYPE_LME && mUsbLMEMode == USB_LME_MODE_COLD) {
                        TVlog.i("FCIISDBT::", "usb dongle detached info: LME dongle, Cold mode");
                        return;
                    }
                    UsbDevice device = (UsbDevice) _intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device != null) {
                        if (mUsbDevice != null) {
                            if (mUsbDevice.equals(device)){
                                // call your method that cleans up and closes communication with the device
                                if (mUsbChipType == USB_CHIP_TYPE_NXP || mUsbChipType == USB_CHIP_TYPE_ITE || (mUsbChipType == USB_CHIP_TYPE_LME && mUsbLMEMode == USB_LME_MODE_WARM)) {
                                    TVlog.i("FCIISDBT::", "usb dongle closed ! (by device detached)");
                                    //viewToastMSG("USB Dongle was detached!\n" + "TV app is terminated.", true);
                                    CustomToast toast = new CustomToast(getApplicationContext());
                                    toast.showToast(getApplicationContext(), getApplicationContext().getString(R.string.usb_detached_app_terminated), Toast.LENGTH_LONG);
                                    sendEvent(TVEVENT.E_TERMINATE);
                                }
                            }
                        }
                    }
                }

                if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action) && mUsbConnected == false) {
                    TVlog.i("FCIISDBT::", "usb dongle attached !!!");
                }
            }
            // ]]usbdongle
        }
    }

    // [[ eddy 160706
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
    // ]] eddy 160706
    public void checkDebugInfo() {
        if (buildOption.ADD_DEBUG_SCREEN) {
            String svcName;
            String strtmp;
            String chName = "";
            String strAudio;
            String strNetTime;
            float freq = 0;
            int signal;
            int rating;
            int age;
            int audioChannel;
            int more[];
            int netTime[];

            if (mCursor != null) {
                if (mCursor.getCount() > 0) {
                    chName = mCursor.getString(CommonStaticData.COLUMN_INDEX_SERVICE_NAME);
                    freq = (float)mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_FREQ);
                }
            }
            svcName = FCI_TVi.GetServiceName();
            signal = FCI_TVi.GetSignal();
            more = FCI_TVi.GetMoreSignalVal();
            netTime = FCI_TVi.GetTSNetTime();
            rating = FCI_TVi.GetCurProgramRating();

            strNetTime = String.format("%04d.%02d.%02d %02d:%02d", netTime[0], netTime[1], netTime[2], netTime[3], netTime[4]);

            switch (rating) {
                case 2: age = 10; break;
                case 3: age = 12; break;
                case 4: age = 14; break;
                case 5: age = 16; break;
                case 6: age = 18; break;
                default: age = 0; break;
            }

            audioChannel = FCI_TVi.GetCurAudioChannelNum();
            switch (audioChannel) {
                case 0: strAudio = getApplicationContext().getString(R.string.audio_dual);
                    break;
                case 1: strAudio = getApplicationContext().getString(R.string.audio_mono);
                    break;
                case 2: strAudio = getApplicationContext().getString(R.string.audio_stereo);
                    break;
                case 6: strAudio = getApplicationContext().getString(R.string.audio_5_1);
                    break;
                default: strAudio = "";
                    break;
            }
            String strFNet = String.format("Net time: [%s]\n", strNetTime);
            String strFFreq = String.format("Freq.: [%3.3f MHz]\n", freq/1000);
            String strFSig = String.format("Signal: [C/N:%d, BER:%d, PER:%d, RSSI:%d]\n", signal, more[0], more[1], more[2]);
            String strFName = String.format("Name(CH+Svc): [%s | %s]\n", chName, svcName);
            String strFAud = String.format("Audio channel: [%s]\n", strAudio);
            String strFRate = "Rating: [All]";
            if (age != 0) {
                strFRate = String.format("Rating: [%d]", age);
            }
            strtmp = strFNet + strFFreq + strFSig + strFName + strFAud + strFRate;
            debugScreen.setText(strtmp);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_SCAN_RESTORE:
                MaterialDialog restoreScanDialog =  new MaterialDialog.Builder(MainActivity.this)
                        //.iconRes(R.drawable.ic_search_grey600_48dp)
                        .theme(Theme.LIGHT)
                        .title(R.string.scan_channel)
                        .titleColor(getResources().getColor(R.color.button_color))
                        .items(R.array.scan_mode)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_scanmode_selected = which;
                                if (dialog_scanmode_selected == 0) {
                                    sendEvent(E_SCAN_START);
                                } else if (dialog_scanmode_selected == 1) {
                                    showDialog(DIALOG_AREA);
                                }
                                try {
                                    removeDialog(DIALOG_SCAN_RESTORE);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .build();
                restoreScanDialog.getWindow().setGravity(Gravity.CENTER);
                restoreScanDialog.show();
                restoreScanDialog.setCanceledOnTouchOutside(false);
                break;
            case DIALOG_SCANMODE:
                MaterialDialog scanmodeDialog =  new MaterialDialog.Builder(MainActivity.this)
                        //.iconRes(R.drawable.ic_search_grey600_48dp)
                        .theme(Theme.LIGHT)
                        .title(R.string.scan_channel)
                        .titleColor(getResources().getColor(R.color.button_color))
                        .items(R.array.scan_mode)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_scanmode_selected = which;
                                if (dialog_scanmode_selected == 0) {
                                    sendEvent(E_SCAN_START);
                                } else if (dialog_scanmode_selected == 1) {
                                    showDialog(DIALOG_AREA);
                                }
                                try {
                                    removeDialog(DIALOG_SCANMODE);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                scanmodeDialog.getWindow().setGravity(Gravity.CENTER);
                scanmodeDialog.show();
                break;

            case DIALOG_AREA:
                MaterialDialog areaDialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_area)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.area)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_area_selected = which;
                                CommonStaticData.areaSet = String.valueOf(dialog_area_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.areaSet = " + CommonStaticData.areaSet);
                                if (dialog_area_selected == 0) {
                                    try {
                                        removeDialog(DIALOG_AREA);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_PREFECTURE_0);
                                } else if (dialog_area_selected == 1) {
                                    try {
                                        removeDialog(DIALOG_AREA);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_PREFECTURE_1);
                                } else if (dialog_area_selected == 2) {
                                    try {
                                        removeDialog(DIALOG_AREA);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_PREFECTURE_2);
                                } else if (dialog_area_selected == 3) {
                                    try {
                                        removeDialog(DIALOG_AREA);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_PREFECTURE_3);
                                } else if (dialog_area_selected == 4) {
                                    try {
                                        removeDialog(DIALOG_AREA);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_PREFECTURE_4);
                                } else if (dialog_area_selected == 5) {
                                    try {
                                        removeDialog(DIALOG_AREA);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_PREFECTURE_5);
                                } else if (dialog_area_selected == 6) {
                                    try {
                                        removeDialog(DIALOG_AREA);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_PREFECTURE_6);
                                } else if (dialog_area_selected == 7) {
                                    try {
                                        removeDialog(DIALOG_AREA);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_PREFECTURE_7);
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.areaKey, CommonStaticData.areaSet);
                                editor.commit();
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                areaDialog.getWindow().setGravity(Gravity.CENTER);
                areaDialog.show();
                break;


            case DIALOG_PREFECTURE_0:
                MaterialDialog prefecture0Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_prefecture)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.prefecture_0)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_prefecture0_selected = which;
                                CommonStaticData.prefectureSet = "0" + "/" + String.valueOf(dialog_prefecture0_selected);    // justin db save
                                if (dialog_prefecture0_selected == 0) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_0);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_00);
                                } else if (dialog_prefecture0_selected == 1) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_0);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_01);
                                } else if (dialog_prefecture0_selected == 2) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_0);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_02);
                                } else if (dialog_prefecture0_selected == 3) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_0);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_03);
                                } else if (dialog_prefecture0_selected == 4) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_0);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_04);
                                } else if (dialog_prefecture0_selected == 5) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_0);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_05);
                                } else if (dialog_prefecture0_selected == 6) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_0);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_06);
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.prefectureKey, CommonStaticData.prefectureSet);
                                editor.commit();
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                prefecture0Dialog.getWindow().setGravity(Gravity.CENTER);
                prefecture0Dialog.show();
                break;


            case DIALOG_PREFECTURE_1:
                MaterialDialog prefecture1Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_prefecture)
                        .titleColor(getResources().getColor(R.color.button_color))
                        .items(R.array.prefecture_1)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_prefecture1_selected = which;
                                CommonStaticData.prefectureSet = "1" + "/" + String.valueOf(dialog_prefecture1_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.prefectureSet = " + CommonStaticData.prefectureSet);
                                if (dialog_prefecture1_selected == 0) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_1);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_10);
                                } else if (dialog_prefecture1_selected == 1) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_1);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_11);
                                } else if (dialog_prefecture1_selected == 2) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_1);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_12);
                                } else if (dialog_prefecture1_selected == 3) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_1);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_13);
                                } else if (dialog_prefecture1_selected == 4) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_1);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_14);
                                } else if (dialog_prefecture1_selected == 5) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_1);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_15);
                                } else if (dialog_prefecture1_selected == 6) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_1);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_16);
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.prefectureKey, CommonStaticData.prefectureSet);
                                editor.commit();
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.button_color))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.button_color))
                        .build();
                prefecture1Dialog.getWindow().setGravity(Gravity.CENTER);
                prefecture1Dialog.show();
                break;

            case DIALOG_PREFECTURE_2:
                MaterialDialog prefecture2Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_prefecture)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.prefecture_2)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_prefecture2_selected = which;
                                CommonStaticData.prefectureSet = "2" + "/" + String.valueOf(dialog_prefecture2_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.prefectureSet = " + CommonStaticData.prefectureSet);
                                if (dialog_prefecture2_selected == 0) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_2);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_20);
                                } else if (dialog_prefecture2_selected == 1) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_2);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_21);
                                } else if (dialog_prefecture2_selected == 2) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_2);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_22);
                                } else if (dialog_prefecture2_selected == 3) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_2);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_23);
                                } else if (dialog_prefecture2_selected == 4) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_2);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_24);
                                } else if (dialog_prefecture2_selected == 5) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_2);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_25);
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.prefectureKey, CommonStaticData.prefectureSet);
                                editor.commit();
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                prefecture2Dialog.getWindow().setGravity(Gravity.CENTER);
                prefecture2Dialog.show();
                break;

            case DIALOG_PREFECTURE_3:
                MaterialDialog prefecture3Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_prefecture)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.prefecture_3)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_prefecture3_selected = which;
                                CommonStaticData.prefectureSet = "3" + "/" + String.valueOf(dialog_prefecture3_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.prefectureSet = " + CommonStaticData.prefectureSet);
                                if (dialog_prefecture3_selected == 0) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_3);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_30);
                                } else if (dialog_prefecture3_selected == 1) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_3);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_31);
                                } else if (dialog_prefecture3_selected == 2) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_3);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_32);
                                } else if (dialog_prefecture3_selected == 3) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_3);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_33);
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.prefectureKey, CommonStaticData.prefectureSet);
                                editor.commit();
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                prefecture3Dialog.getWindow().setGravity(Gravity.CENTER);
                prefecture3Dialog.show();
                break;


            case DIALOG_PREFECTURE_4:
                MaterialDialog prefecture4Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_prefecture)
                        .titleColor(getResources().getColor(R.color.button_color))
                        .items(R.array.prefecture_4)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_prefecture4_selected = which;
                                CommonStaticData.prefectureSet = "4" + "/" + String.valueOf(dialog_prefecture4_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.prefectureSet = " + CommonStaticData.prefectureSet);
                                if (dialog_prefecture4_selected == 0) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_4);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_40);
                                } else if (dialog_prefecture4_selected == 1) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_4);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_41);
                                } else if (dialog_prefecture4_selected == 2) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_4);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_42);
                                } else if (dialog_prefecture4_selected == 3) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_4);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_43);
                                } else if (dialog_prefecture4_selected == 4) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_4);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_44);
                                } else if (dialog_prefecture4_selected == 5) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_4);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_45);
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.prefectureKey, CommonStaticData.prefectureSet);
                                editor.commit();
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                prefecture4Dialog.getWindow().setGravity(Gravity.CENTER);
                prefecture4Dialog.show();
                break;


            case DIALOG_PREFECTURE_5:
                MaterialDialog prefecture5Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_prefecture)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.prefecture_5)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_prefecture5_selected = which;
                                CommonStaticData.prefectureSet = "5" + "/" + String.valueOf(dialog_prefecture5_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.prefectureSet = " + CommonStaticData.prefectureSet);
                                if (dialog_prefecture5_selected == 0) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_5);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_50);
                                } else if (dialog_prefecture5_selected == 1) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_5);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_51);
                                } else if (dialog_prefecture5_selected == 2) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_5);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_52);
                                } else if (dialog_prefecture5_selected == 3) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_5);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_53);
                                } else if (dialog_prefecture5_selected == 4) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_5);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_54);
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.prefectureKey, CommonStaticData.prefectureSet);
                                editor.commit();
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                prefecture5Dialog.getWindow().setGravity(Gravity.CENTER);
                prefecture5Dialog.show();
                break;


            case DIALOG_PREFECTURE_6:
                MaterialDialog prefecture6Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_prefecture)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.prefecture_6)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_prefecture6_selected = which;
                                CommonStaticData.prefectureSet = "6" + "/" + String.valueOf(dialog_prefecture6_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.prefectureSet = " + CommonStaticData.prefectureSet);
                                if (dialog_prefecture6_selected == 0) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_6);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_60);
                                } else if (dialog_prefecture6_selected == 1) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_6);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_61);
                                } else if (dialog_prefecture6_selected == 2) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_6);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_62);
                                } else if (dialog_prefecture6_selected == 3) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_6);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_63);
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.prefectureKey, CommonStaticData.prefectureSet);
                                editor.commit();
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                prefecture6Dialog.getWindow().setGravity(Gravity.CENTER);
                prefecture6Dialog.show();
                break;


            case DIALOG_PREFECTURE_7:
                MaterialDialog prefecture7Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_prefecture)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.prefecture_7)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_prefecture7_selected = which;
                                CommonStaticData.prefectureSet = "7" + "/" + String.valueOf(dialog_prefecture7_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.prefectureSet = " + CommonStaticData.prefectureSet);
                                if (dialog_prefecture7_selected == 0) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_7);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_70);
                                } else if (dialog_prefecture7_selected == 1) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_7);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_71);
                                } else if (dialog_prefecture7_selected == 2) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_7);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_72);
                                } else if (dialog_prefecture7_selected == 3) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_7);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_73);
                                } else if (dialog_prefecture7_selected == 4) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_7);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_74);
                                } else if (dialog_prefecture7_selected == 5) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_7);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_75);
                                } else if (dialog_prefecture7_selected == 6) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_7);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_76);
                                } else if (dialog_prefecture7_selected == 7) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_7);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_77);
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.prefectureKey, CommonStaticData.prefectureSet);
                                editor.commit();
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                prefecture7Dialog.getWindow().setGravity(Gravity.CENTER);
                prefecture7Dialog.show();
                break;

            case DIALOG_LOCALITY_00:
                MaterialDialog locality00Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_00)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality00_selected = which;
                                CommonStaticData.localitySet = "0/0" + "/" + String.valueOf(dialog_locality00_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_00);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                removeDialog(DIALOG_LOCALITY_00);
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality00_selected == 0) {
                                    int[] regionCh = {13, 14, 15, 19, 21, 23, 25, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality00_selected == 1) {
                                    int[] regionCh = {14, 15, 17, 18, 19, 23, 25, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality00_selected == 2) {
                                    int[] regionCh = {13, 14, 15, 19, 21, 23, 25, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality00_selected == 3) {
                                    int[] regionCh = {16, 20, 22, 24, 26, 31, 33, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality00_selected == 4) {
                                    int[] regionCh = {29, 31, 33, 36, 41, 43, 45, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality00_selected == 5) {
                                    int[] regionCh = {13, 15, 17, 19, 21, 23, 25, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality00_selected == 6) {
                                    int[] regionCh = {13, 14, 16, 18, 20, 22, 24, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality00Dialog.getWindow().setGravity(Gravity.CENTER);
                locality00Dialog.show();
                break;

            case DIALOG_LOCALITY_01:
                MaterialDialog locality01Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_01)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality01_selected = which;
                                CommonStaticData.localitySet = "0/1" + "/" + String.valueOf(dialog_locality01_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_01);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality01_selected == 0) {
                                    int[] regionCh = {13, 16, 28, 30, 32, 0, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality01_selected == 1) {
                                    int[] regionCh = {14, 18, 20, 22, 24, 0, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality01Dialog.getWindow().setGravity(Gravity.CENTER);
                locality01Dialog.show();
                break;


            case DIALOG_LOCALITY_02:
                MaterialDialog locality02Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_02)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality02_selected = which;
                                CommonStaticData.localitySet = "0/2" + "/" + String.valueOf(dialog_locality02_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_02);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality02_selected == 0) {
                                    int[] regionCh = {13, 14, 16, 18, 20, 22, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality02_selected == 1) {
                                    int[] regionCh = {15, 17, 19, 21, 24, 30, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality02_selected == 2) {
                                    int[] regionCh = {15, 23, 27, 29, 37, 43, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality02_selected == 3) {
                                    int[] regionCh = {15, 17, 19, 21, 23, 50, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality02_selected == 4) {
                                    int[] regionCh = {13, 14, 15, 16, 17, 18, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality02_selected == 5) {
                                    int[] regionCh = {13, 14, 16, 18, 20, 22, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality02Dialog.getWindow().setGravity(Gravity.CENTER);
                locality02Dialog.show();
                break;


            case DIALOG_LOCALITY_03:
                MaterialDialog locality03Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.button_color))
                        .items(R.array.locality_03)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality03_selected = which;
                                CommonStaticData.localitySet = "0/3" + "/" + String.valueOf(dialog_locality03_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_03);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality03_selected == 0) {
                                    int[] regionCh = {13, 17, 19, 21, 24, 28, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality03_selected == 1) {
                                    int[] regionCh = {14, 17, 19, 21, 24, 28, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality03_selected == 2) {
                                    int[] regionCh = {13, 15, 23, 25, 27, 30, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality03_selected == 3) {
                                    int[] regionCh = {16, 18, 20, 22, 26, 30, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality03Dialog.getWindow().setGravity(Gravity.CENTER);
                locality03Dialog.show();
                break;


            case DIALOG_LOCALITY_04:
                MaterialDialog locality04Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_04)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality04_selected = which;
                                CommonStaticData.localitySet = "0/4" + "/" + String.valueOf(dialog_locality04_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_04);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality04_selected == 0) {
                                    int[] regionCh = {21, 29, 35, 48, 50, 0, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality04_selected == 1) {
                                    int[] regionCh = {19, 23, 25, 26, 33, 0, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality04Dialog.getWindow().setGravity(Gravity.CENTER);
                locality04Dialog.show();
                break;


            case DIALOG_LOCALITY_05:
                MaterialDialog locality05Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_05)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality05_selected = which;
                                CommonStaticData.localitySet = "0/5" + "/" + String.valueOf(dialog_locality05_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_05);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality05_selected == 0) {
                                    int[] regionCh = {13, 14, 16, 18, 20, 22, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality05_selected == 1) {
                                    int[] regionCh = {24, 28, 32, 34, 37, 40, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality05_selected == 2) {
                                    int[] regionCh = {16, 18, 20, 27, 32, 34, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality05_selected == 3) {
                                    int[] regionCh = {15, 17, 19, 21, 23, 33, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality05Dialog.getWindow().setGravity(Gravity.CENTER);
                locality05Dialog.show();
                break;


            case DIALOG_LOCALITY_06:
                MaterialDialog locality06Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_06)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality06_selected = which;
                                CommonStaticData.localitySet = "0/6" + "/" + String.valueOf(dialog_locality06_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_06);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality06_selected == 0) {
                                    int[] regionCh = {14, 15, 25, 26, 27, 29, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality06_selected == 1) {
                                    int[] regionCh = {14, 16, 18, 20, 22, 30, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality06_selected == 2) {
                                    int[] regionCh = {13, 16, 17, 21, 26, 28, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality06Dialog.getWindow().setGravity(Gravity.CENTER);
                locality06Dialog.show();
                break;


            case DIALOG_LOCALITY_10:
                MaterialDialog locality10Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_10)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality10_selected = which;
                                CommonStaticData.localitySet = "1/0" + "/" + String.valueOf(dialog_locality10_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_10);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality10_selected == 0) {
                                    int[] regionCh = {13, 14, 15, 17, 18, 19, 20, 28, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality10Dialog.getWindow().setGravity(Gravity.CENTER);
                locality10Dialog.show();
                break;


            case DIALOG_LOCALITY_11:
                MaterialDialog locality11Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_11)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality11_selected = which;
                                CommonStaticData.localitySet = "1/1" + "/" + String.valueOf(dialog_locality11_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_11);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality11_selected == 0) {
                                    int[] regionCh = {15, 17, 18, 28, 29, 34, 35, 39, 47, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality11Dialog.getWindow().setGravity(Gravity.CENTER);
                locality11Dialog.show();
                break;


            case DIALOG_LOCALITY_12:
                MaterialDialog locality12Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_12)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality12_selected = which;
                                CommonStaticData.localitySet = "1/2" + "/" + String.valueOf(dialog_locality12_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_12);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality12_selected == 0) {
                                    int[] regionCh = {19, 28, 33, 36, 37, 39, 42, 43, 45, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality12Dialog.getWindow().setGravity(Gravity.CENTER);
                locality12Dialog.show();
                break;


            case DIALOG_LOCALITY_13:
                MaterialDialog locality13Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_13)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality13_selected = which;
                                CommonStaticData.localitySet = "1/3" + "/" + String.valueOf(dialog_locality13_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_13);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality13_selected == 0) {
                                    int[] regionCh = {21, 22, 23, 24, 25, 26, 27, 28, 32, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality13Dialog.getWindow().setGravity(Gravity.CENTER);
                locality13Dialog.show();
                break;


            case DIALOG_LOCALITY_14:
                MaterialDialog locality14Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_14)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality14_selected = which;
                                CommonStaticData.localitySet = "1/4" + "/" + String.valueOf(dialog_locality14_selected);    // justin db save
                                try {
                                    removeDialog(DIALOG_LOCALITY_14);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality14_selected == 0) {
                                    int[] regionCh = {21, 22, 23, 24, 25, 26, 27, 28, 30, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality14Dialog.getWindow().setGravity(Gravity.CENTER);
                locality14Dialog.show();
                break;


            case DIALOG_LOCALITY_15:
                MaterialDialog locality15Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_15)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality15_selected = which;
                                CommonStaticData.localitySet = "1/5" + "/" + String.valueOf(dialog_locality15_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_15);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality15_selected == 0) {
                                    int[] regionCh = {16, 21, 22, 23, 24, 25, 26, 27, 28, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality15Dialog.getWindow().setGravity(Gravity.CENTER);
                locality15Dialog.show();
                break;


            case DIALOG_LOCALITY_16:
                MaterialDialog locality16Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_16)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality16_selected = which;
                                CommonStaticData.localitySet = "1/6" + "/" + String.valueOf(dialog_locality16_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_16);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality16_selected == 0) {
                                    int[] regionCh = {18, 21, 22, 23, 24, 25, 26, 27, 28, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality16_selected == 1) {
                                    int[] regionCh = {18, 19, 21, 22, 23, 24, 25, 26, 28, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality16Dialog.getWindow().setGravity(Gravity.CENTER);
                locality16Dialog.show();
                break;


            case DIALOG_LOCALITY_20:
                MaterialDialog locality20Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_20)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality20_selected = which;
                                CommonStaticData.localitySet = "2/0" + "/" + String.valueOf(dialog_locality20_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_20);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality20_selected == 0) {
                                    int[] regionCh = {13, 15, 17, 19, 23, 26, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality20Dialog.getWindow().setGravity(Gravity.CENTER);
                locality20Dialog.show();
                break;


            case DIALOG_LOCALITY_21:
                MaterialDialog locality21Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_21)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality21_selected = which;
                                CommonStaticData.localitySet = "2/1" + "/" + String.valueOf(dialog_locality21_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_21);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality21_selected == 0) {
                                    int[] regionCh = {18, 22, 24, 27, 28, 0, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality21Dialog.getWindow().setGravity(Gravity.CENTER);
                locality21Dialog.show();
                break;


            case DIALOG_LOCALITY_22:
                MaterialDialog locality22Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_22)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality22_selected = which;
                                CommonStaticData.localitySet = "2/2" + "/" + String.valueOf(dialog_locality22_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_22);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality22_selected == 0) {
                                    int[] regionCh = {13, 14, 15, 16, 17, 23, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality22_selected == 1) {
                                    int[] regionCh = {20, 30, 39, 42, 44, 52, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality22_selected == 2) {
                                    int[] regionCh = {14, 16, 25, 31, 33, 37, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality22Dialog.getWindow().setGravity(Gravity.CENTER);
                locality22Dialog.show();
                break;


            case DIALOG_LOCALITY_23:
                MaterialDialog locality23Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_23)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality23_selected = which;
                                CommonStaticData.localitySet = "2/3" + "/" + String.valueOf(dialog_locality23_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_23);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality23_selected == 0) {
                                    int[] regionCh = {19, 20, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality23Dialog.getWindow().setGravity(Gravity.CENTER);
                locality23Dialog.show();
                break;


            case DIALOG_LOCALITY_24:
                MaterialDialog locality24Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_24)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality24_selected = which;
                                CommonStaticData.localitySet = "2/4" + "/" + String.valueOf(dialog_locality24_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_24);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality24_selected == 0) {
                                    int[] regionCh = {21, 23, 25, 27, 0, 0, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality24Dialog.getWindow().setGravity(Gravity.CENTER);
                locality24Dialog.show();
                break;


            case DIALOG_LOCALITY_25:
                MaterialDialog locality25Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_25)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality25_selected = which;
                                CommonStaticData.localitySet = "2/5" + "/" + String.valueOf(dialog_locality25_selected);    // justin db save
                                try {
                                    removeDialog(DIALOG_LOCALITY_25);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality25_selected == 0) {
                                    int[] regionCh = {13, 14, 15, 16, 17, 18, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality25_selected == 1) {
                                    int[] regionCh = {33, 35, 36, 46, 48, 49, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality25_selected == 2) {
                                    int[] regionCh = {38, 41, 47, 49, 51, 53, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality25Dialog.getWindow().setGravity(Gravity.CENTER);
                locality25Dialog.show();
                break;


            case DIALOG_LOCALITY_30:
                MaterialDialog locality30Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_30)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality30_selected = which;
                                CommonStaticData.localitySet = "3/0" + "/" + String.valueOf(dialog_locality30_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_30);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality30_selected == 0) {
                                    int[] regionCh = {13, 18, 19, 21, 22, 29, 30, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality30_selected == 1) {
                                    int[] regionCh = {14, 15, 16, 17, 29, 30, 31, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality30_selected == 2) {
                                    int[] regionCh = {14, 15, 16, 17, 24, 31, 32, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality30Dialog.getWindow().setGravity(Gravity.CENTER);
                locality30Dialog.show();
                break;


            case DIALOG_LOCALITY_31:
                MaterialDialog locality31Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_31)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality31_selected = which;
                                CommonStaticData.localitySet = "3/1" + "/" + String.valueOf(dialog_locality31_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_31);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality31_selected == 0) {
                                    int[] regionCh = {13, 15, 17, 18, 19, 20, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality31_selected == 1) {
                                    int[] regionCh = {13, 20, 21, 22, 23, 25, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality31Dialog.getWindow().setGravity(Gravity.CENTER);
                locality31Dialog.show();
                break;


            case DIALOG_LOCALITY_32:
                MaterialDialog locality32Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_32)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality32_selected = which;
                                CommonStaticData.localitySet = "3/2" + "/" + String.valueOf(dialog_locality32_selected);    // justin db save
                                try {
                                    removeDialog(DIALOG_LOCALITY_32);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality32_selected == 0) {
                                    int[] regionCh = {13, 18, 19, 20, 21, 22, 23, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality32Dialog.getWindow().setGravity(Gravity.CENTER);
                locality32Dialog.show();
                break;


            case DIALOG_LOCALITY_33:
                MaterialDialog locality33Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_33)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality33_selected = which;
                                CommonStaticData.localitySet = "3/3" + "/" + String.valueOf(dialog_locality33_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_33);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality33_selected == 0) {
                                    int[] regionCh = {18, 19, 21, 22, 27, 28, 44, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality33_selected == 1) {
                                    int[] regionCh = {13, 14, 15, 16, 17, 24, 29, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality33Dialog.getWindow().setGravity(Gravity.CENTER);
                locality33Dialog.show();
                break;


            case DIALOG_LOCALITY_40:
                MaterialDialog locality40Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_40)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality40_selected = which;
                                CommonStaticData.localitySet = "4/0" + "/" + String.valueOf(dialog_locality40_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_40);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality40_selected == 0) {
                                    int[] regionCh = {13, 15, 16, 17, 20, 26, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality40_selected == 1) {
                                    int[] regionCh = {15, 16, 17, 26, 29, 31, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality40Dialog.getWindow().setGravity(Gravity.CENTER);
                locality40Dialog.show();
                break;


            case DIALOG_LOCALITY_41:
                MaterialDialog locality41Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_41)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality41_selected = which;
                                CommonStaticData.localitySet = "4/1" + "/" + String.valueOf(dialog_locality41_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_41);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality41_selected == 0) {
                                    int[] regionCh = {13, 14, 15, 16, 17, 23, 25, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality41Dialog.getWindow().setGravity(Gravity.CENTER);
                locality41Dialog.show();
                break;


            case DIALOG_LOCALITY_42:
                MaterialDialog locality42Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_42)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality42_selected = which;
                                CommonStaticData.localitySet = "4/2" + "/" + String.valueOf(dialog_locality42_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_42);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality42_selected == 0) {
                                    int[] regionCh = {13, 14, 15, 16, 17, 18, 24, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality42Dialog.getWindow().setGravity(Gravity.CENTER);
                locality42Dialog.show();
                break;


            case DIALOG_LOCALITY_43:
                MaterialDialog locality43Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_43)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality43_selected = which;
                                CommonStaticData.localitySet = "4/3" + "/" + String.valueOf(dialog_locality43_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_43);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality43_selected == 0) {
                                    int[] regionCh = {13, 14, 15, 16, 17, 22, 26, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality43Dialog.getWindow().setGravity(Gravity.CENTER);
                locality43Dialog.show();
                break;


            case DIALOG_LOCALITY_44:
                MaterialDialog locality44Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_44)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality44_selected = which;
                                CommonStaticData.localitySet = "4/4" + "/" + String.valueOf(dialog_locality44_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_44);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality44_selected == 0) {
                                    int[] regionCh = {13, 14, 15, 16, 17, 29, 31, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality44Dialog.getWindow().setGravity(Gravity.CENTER);
                locality44Dialog.show();
                break;


            case DIALOG_LOCALITY_45:
                MaterialDialog locality45Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_45)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality45_selected = which;
                                CommonStaticData.localitySet = "4/5" + "/" + String.valueOf(dialog_locality45_selected);    // justin db save
                                try {
                                    removeDialog(DIALOG_LOCALITY_45);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality45_selected == 0) {
                                    int[] regionCh = {13, 14, 15, 16, 17, 20, 23, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality45Dialog.getWindow().setGravity(Gravity.CENTER);
                locality45Dialog.show();
                break;


            case DIALOG_LOCALITY_50:
                MaterialDialog locality50Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_50)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality50_selected = which;
                                CommonStaticData.localitySet = "5/0" + "/" + String.valueOf(dialog_locality50_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_50);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality50_selected == 0) {
                                    int[] regionCh = {20, 29, 31, 36, 38, 0, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality50_selected == 1) {
                                    int[] regionCh = {20, 26, 41, 43, 45, 0, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality50_selected == 2) {
                                    int[] regionCh = {27, 29, 31, 36, 38, 0, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality50Dialog.getWindow().setGravity(Gravity.CENTER);
                locality50Dialog.show();
                break;


            case DIALOG_LOCALITY_51:
                MaterialDialog locality51Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_51)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality51_selected = which;
                                CommonStaticData.localitySet = "5/1" + "/" + String.valueOf(dialog_locality51_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_51);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality51_selected == 0) {
                                    int[] regionCh = {19, 21, 41, 43, 45, 0, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality51_selected == 1) {
                                    int[] regionCh = {22, 23, 31, 35, 37, 0, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality51_selected == 2) {
                                    int[] regionCh = {20, 21, 33, 38, 44, 0, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality51Dialog.getWindow().setGravity(Gravity.CENTER);
                locality51Dialog.show();
                break;


            case DIALOG_LOCALITY_52:
                MaterialDialog locality52Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_52)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality52_selected = which;
                                CommonStaticData.localitySet = "5/2" + "/" + String.valueOf(dialog_locality52_selected);    // justin db save
                                try {
                                    removeDialog(DIALOG_LOCALITY_52);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality52_selected == 0) {
                                    int[] regionCh = {18, 20, 21, 27, 30, 32, 45, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality52_selected == 1) {
                                    int[] regionCh = {13, 14, 15, 16, 17, 19, 22, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality52Dialog.getWindow().setGravity(Gravity.CENTER);
                locality52Dialog.show();
                break;


            case DIALOG_LOCALITY_53:
                MaterialDialog locality53Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_53)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality53_selected = which;
                                CommonStaticData.localitySet = "5/3" + "/" + String.valueOf(dialog_locality53_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_53);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality53_selected == 0) {
                                    int[] regionCh = {14, 15, 18, 19, 22, 23, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality53_selected == 1) {
                                    int[] regionCh = {16, 17, 28, 29, 42, 44, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality53Dialog.getWindow().setGravity(Gravity.CENTER);
                locality53Dialog.show();
                break;

            case DIALOG_LOCALITY_54:
                MaterialDialog locality54Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_54)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality54_selected = which;
                                CommonStaticData.localitySet = "5/4" + "/" + String.valueOf(dialog_locality54_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_54);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality54_selected == 0) {
                                    int[] regionCh = {13, 16, 18, 20, 26, 0, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality54_selected == 1) {
                                    int[] regionCh = {13, 16, 18, 20, 26, 0, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality54_selected == 2) {
                                    int[] regionCh = {38, 39, 40, 41, 42, 0, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality54Dialog.getWindow().setGravity(Gravity.CENTER);
                locality54Dialog.show();
                break;


            case DIALOG_LOCALITY_60:
                MaterialDialog locality60Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_60)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality60_selected = which;
                                CommonStaticData.localitySet = "6/0" + "/" + String.valueOf(dialog_locality60_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_60);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality60_selected == 0) {
                                    int[] regionCh = {31, 34, 40, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality60Dialog.getWindow().setGravity(Gravity.CENTER);
                locality60Dialog.show();
                break;


            case DIALOG_LOCALITY_61:
                MaterialDialog locality61Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_61)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality61_selected = which;
                                CommonStaticData.localitySet = "6/1" + "/" + String.valueOf(dialog_locality61_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_61);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality61_selected == 0) {
                                    int[] regionCh = {13, 15, 17, 18, 21, 24, 27, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality61_selected == 1) {
                                    int[] regionCh = {13, 15, 17, 18, 21, 24, 28, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality61Dialog.getWindow().setGravity(Gravity.CENTER);
                locality61Dialog.show();
                break;


            case DIALOG_LOCALITY_62:
                MaterialDialog locality62Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_62)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality62_selected = which;
                                CommonStaticData.localitySet = "6/2" + "/" + String.valueOf(dialog_locality62_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_62);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality62_selected == 0) {
                                    int[] regionCh = {13, 16, 17, 20, 21, 27, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality62_selected == 1) {
                                    int[] regionCh = {13, 19, 20, 21, 23, 29, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality62_selected == 2) {
                                    int[] regionCh = {39, 41, 43, 47, 49, 51, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality62Dialog.getWindow().setGravity(Gravity.CENTER);
                locality62Dialog.show();
                break;


            case DIALOG_LOCALITY_63:
                MaterialDialog locality63Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_63)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality63_selected = which;
                                CommonStaticData.localitySet = "6/3" + "/" + String.valueOf(dialog_locality63_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_63);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality63_selected == 0) {
                                    int[] regionCh = {13, 15, 17, 19, 21, 0, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality63_selected == 1) {
                                    int[] regionCh = {16, 18, 20, 23, 24, 0, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality63_selected == 2) {
                                    int[] regionCh = {25, 26, 27, 28, 29, 0, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality63Dialog.getWindow().setGravity(Gravity.CENTER);
                locality63Dialog.show();
                break;


            case DIALOG_LOCALITY_70:
                MaterialDialog locality70Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_70)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality70_selected = which;
                                CommonStaticData.localitySet = "7/0" + "/" + String.valueOf(dialog_locality70_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_70);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality70_selected == 0) {
                                    int[] regionCh = {22, 26, 28, 30, 31, 32, 34, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality70_selected == 1) {
                                    int[] regionCh = {27, 29, 30, 31, 32, 40, 42, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality70_selected == 2) {
                                    int[] regionCh = {13, 17, 21, 26, 29, 30, 31, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality70_selected == 3) {
                                    int[] regionCh = {18, 20, 22, 23, 24, 26, 28, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality70Dialog.getWindow().setGravity(Gravity.CENTER);
                locality70Dialog.show();
                break;


            case DIALOG_LOCALITY_71:
                MaterialDialog locality71Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_71)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality71_selected = which;
                                CommonStaticData.localitySet = "7/1" + "/" + String.valueOf(dialog_locality71_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_71);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality71_selected == 0) {
                                    int[] regionCh = {25, 33, 44, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality71Dialog.getWindow().setGravity(Gravity.CENTER);
                locality71Dialog.show();
                break;


            case DIALOG_LOCALITY_72:
                MaterialDialog locality72Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_72)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality72_selected = which;
                                CommonStaticData.localitySet = "7/2" + "/" + String.valueOf(dialog_locality72_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_72);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality72_selected == 0) {
                                    int[] regionCh = {13, 14, 15, 18, 19, 20, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality72_selected == 1) {
                                    int[] regionCh = {16, 22, 34, 38, 40, 42, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality72Dialog.getWindow().setGravity(Gravity.CENTER);
                locality72Dialog.show();
                break;


            case DIALOG_LOCALITY_73:
                MaterialDialog locality73Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_73)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality73_selected = which;
                                CommonStaticData.localitySet = "7/3" + "/" + String.valueOf(dialog_locality73_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_73);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality73_selected == 0) {
                                    int[] regionCh = {24, 28, 41, 42, 47, 49, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality73_selected == 1) {
                                    int[] regionCh = {20, 26, 27, 30, 31, 40, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality73Dialog.getWindow().setGravity(Gravity.CENTER);
                locality73Dialog.show();
                break;


            case DIALOG_LOCALITY_74:
                MaterialDialog locality74Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_74)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality74_selected = which;
                                CommonStaticData.localitySet = "7/4" + "/" + String.valueOf(dialog_locality74_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_74);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality74_selected == 0) {
                                    int[] regionCh = {14, 15, 22, 32, 34, 0, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality74_selected == 1) {
                                    int[] regionCh = {14, 15, 22, 25, 34, 0, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality74Dialog.getWindow().setGravity(Gravity.CENTER);
                locality74Dialog.show();
                break;


            case DIALOG_LOCALITY_75:
                MaterialDialog locality75Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_75)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality75_selected = which;
                                CommonStaticData.localitySet = "7/5" + "/" + String.valueOf(dialog_locality75_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_75);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality75_selected == 0) {
                                    int[] regionCh = {13, 14, 15, 16, 0, 0, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality75_selected == 1) {
                                    int[] regionCh = {43, 44, 45, 46, 0, 0, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality75Dialog.getWindow().setGravity(Gravity.CENTER);
                locality75Dialog.show();
                break;


            case DIALOG_LOCALITY_76:
                MaterialDialog locality76Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_76)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality76_selected = which;
                                CommonStaticData.localitySet = "7/6" + "/" + String.valueOf(dialog_locality76_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_76);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality76_selected == 0) {
                                    int[] regionCh = {18, 29, 34, 36, 40, 42, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality76_selected == 1) {
                                    int[] regionCh = {17, 22, 41, 43, 47, 49, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality76_selected == 2) {
                                    int[] regionCh = {20, 22, 24, 37, 39, 41, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality76_selected == 3) {
                                    int[] regionCh = {13, 14, 15, 19, 21, 25, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality76Dialog.getWindow().setGravity(Gravity.CENTER);
                locality76Dialog.show();
                break;


            case DIALOG_LOCALITY_77:
                MaterialDialog locality77Dialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_77)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality77_selected = which;
                                CommonStaticData.localitySet = "7/7" + "/" + String.valueOf(dialog_locality77_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_77);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality77_selected == 0) {
                                    int[] regionCh = {13, 14, 15, 16, 17, 0, 0, 0, 0, 0, 0, 0};
                                    sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality77Dialog.getWindow().setGravity(Gravity.CENTER);
                locality77Dialog.show();
                break;

            case DIALOG_NOT_SUPPORT_RESOLUTION:
                MaterialDialog notSupportResolutionDialog = new MaterialDialog.Builder(MainActivity.this)
                        .theme(Theme.LIGHT)
                        .iconRes(R.drawable.ic_info_outline_gray_48dp)
                        .title(R.string.alert)
                        .titleColor(getResources().getColor(R.color.black))
                        .content(R.string.not_support_resolution)
                        .contentColor(getResources().getColor(R.color.black))
                        .positiveText(R.string.ok)
                        .positiveColor(getResources().getColor(R.color.blue3))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                try {
                                    removeDialog(DIALOG_NOT_SUPPORT_RESOLUTION);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                Intent intent_ch = new Intent(MainActivity.this, ChannelMainActivity.class);
                                startActivity(intent_ch);
                                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);  // live add
                            }
                        })
                        .cancelable(false)
                        .build();
                notSupportResolutionDialog.getWindow().setGravity(Gravity.CENTER);
                notSupportResolutionDialog.show();
                break;
        }
        return null;
    }

    // for Android M Permission

    public  boolean isStoragePermissionGranted() {
        if (SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                TVlog.i(TAG, " >>>>> Permission is granted");
                return true;
            } else {
                TVlog.i(TAG, " >>>>> Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            TVlog.i(TAG, " >>>>> Permission is granted");
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            if (grantResults[0]== PackageManager.PERMISSION_GRANTED){
                TVlog.i(TAG, " >>>>> Permission: " + permissions[0] + "was " + grantResults[0]);
                //resume tasks needing this permission
            }
        }
    }

    private int CurrentVol(){
        AudioManager audMgr = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
        int vol = audMgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        return vol;
    }

    private void doVolumeTouch(float y_changed){
        AudioManager audMgr = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);

        int max = audMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int delta = -(int)((y_changed/mSurfaceYDisplayRange)*max);
        int vol = (int) Math.min(Math.max(mVol +delta, 0), max);

        ShowVolume();

        if (delta != 0){
            audMgr.setStreamVolume(AudioManager.STREAM_MUSIC, vol, 0);
            //mIsAudioOrBrightnessChanged = true;
            mAudio = true;
        }
    }

    private void ShowVolume(){
        volumebarLayout.setVisibility(View.VISIBLE);
        mVerticalVolumeProgress.setProgress(CurrentVol());

        AudioManager audMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int vol = audMgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (vol == 0) {
            img_volumebar_off.setVisibility(View.VISIBLE);
            img_volumebar.setVisibility(View.INVISIBLE);
        } else {
            img_volumebar.setVisibility(View.VISIBLE);
            img_volumebar_off.setVisibility(View.INVISIBLE);
        }

        // live add
        mVerticalVolumeProgress.setOnSeekBarChangeListener(new VerticalSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(VerticalSeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    img_volumebar_off.setVisibility(View.VISIBLE);
                    img_volumebar.setVisibility(View.INVISIBLE);
                } else {
                    img_volumebar.setVisibility(View.VISIBLE);
                    img_volumebar_off.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onStartTrackingTouch(VerticalSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(VerticalSeekBar seekBar) {
                AudioManager audMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                audMgr.setStreamVolume(AudioManager.STREAM_MUSIC, mVerticalVolumeProgress.getProgress(), 0);
                if (mVerticalVolumeProgress.getProgress() == 0) {
                    img_volumebar_off.setVisibility(View.VISIBLE);
                    img_volumebar.setVisibility(View.INVISIBLE);
                } else {
                    img_volumebar.setVisibility(View.VISIBLE);
                    img_volumebar_off.setVisibility(View.INVISIBLE);
                }
            }
        });
        //
    }

    private void initBrightnessTouch(){
        CommonStaticData.brightness = CommonStaticData.settings.getInt(CommonStaticData.brightnessKey, 15);
        int player_bright = CommonStaticData.brightness;
        //WindowManager.LayoutParams lp = mWindow.getAttributes();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = player_bright;
        getWindow().setAttributes(lp);
        mIsFirstBrightnessGesture = false;
    }

    private void doBrightnessTouch(float y_changed){
        if (mIsFirstBrightnessGesture)
            initBrightnessTouch();

        //mIsAudioOrBrightnessChanged = true;

        float delta = -y_changed / mSurfaceYDisplayRange * 0.07f;

        //WindowManager.LayoutParams lp = mWindow.getAttributes();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = Math.min(Math.max(lp.screenBrightness + delta, 0.01f), 1);

        CommonStaticData.brightness = CommonStaticData.settings.getInt(CommonStaticData.brightnessKey, 15);
        int player_bright = CommonStaticData.brightness;

        if (player_bright < 1){
            player_bright = 1;
        } else if (player_bright > 15){
            player_bright = 15;
        }

        ShowBright();

        if (player_bright != Math.round(lp.screenBrightness * 15)){
            getWindow().setAttributes(lp);
            mBrightnessChanged =  true;     // justin
            CommonStaticData.brightness = Math.round(lp.screenBrightness * 15);
            CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = CommonStaticData.settings.edit();
            editor.putInt(CommonStaticData.brightnessKey, CommonStaticData.brightness);
            editor.commit();
        }
    }

    private void ShowBright(){
        brightbarLayout.setVisibility(View.VISIBLE);
        CommonStaticData.brightness = CommonStaticData.settings.getInt(CommonStaticData.brightnessKey, 15);
        int player_bright = CommonStaticData.brightness;
        mVerticalBrightProgress.setProgress(player_bright);
    }

    private void setBrightness(int vlaue){
        if (vlaue < 1) {
            vlaue = 1;
        } else if (vlaue > 15) {
            vlaue = 15;
        }

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.screenBrightness = (float) vlaue / 15;
        getWindow().setAttributes(params);
    }

    private void blink(){
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeToBlink = 1000;    //in milissegunds
                try {Thread.sleep(timeToBlink);}catch (Exception e) {}
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        TextView txt = (TextView) findViewById(R.id.bcas_card_insert_msg);
                        if (txt.getVisibility() == View.VISIBLE){
                            txt.setVisibility(View.INVISIBLE);
                        } else {
                            txt.setVisibility(View.VISIBLE);
                        }
                        blink();
                    }
                });
            }
        }).start();
    }

    //ADD_GINGA_NCL[[
    public void resetNCL() {
        if (buildOption.ADD_GINGA_NCL == true) {
            stopNCLDemux();
            //ToDo::Ginga
            //reset Ginga view & channel
        }
    }

    public void restartNCLDemux() {
        if (buildOption.ADD_GINGA_NCL == true) {
            stopNCLDemux();
            //ToDo::Ginga
            //reset Ginga View & channel

            // start a thread to get the TS data.
            startNCLDemux();
        }
    }

    public void startNCLDemux() {
        if (buildOption.ADD_GINGA_NCL==true) {
            mRunDemuxThread = true;
        }
    }

    public void stopNCLDemux() {
        if (buildOption.ADD_GINGA_NCL==true) {
            mRunDemuxThread = false;
        }
    }

    public void onAppStart() {
        if (buildOption.ADD_GINGA_NCL==true) {
            TVlog.i(TAG, "onAppStart");
        }
    }

    public void onAppStop() {
        if (buildOption.ADD_GINGA_NCL==true) {
            TVlog.i(TAG,"onAppStop ");
        }
    }

    public void SetVideoLayout(Rect in_rect) {
        if (buildOption.ADD_GINGA_NCL==true) {
        }
    }

    public void NotifyDownloadInfo(int status, int percent) {
        if (buildOption.ADD_GINGA_NCL==true) {
        }
    }

    public void NotifyDocumentDone() {
        // NCL Contents is ready to show.
        if (buildOption.ADD_GINGA_NCL==true) {
        }
    }

    public void processGingaNCL(byte[] TSdata)
    {
        //TVlog.i(TAG, "[processGingaNCL] TSdata: ");
        //for (int i=0; i < 188; i++) {
        //  TVlog.i(TAG, TSdata[i] + " ");
        //}
        if (buildOption.ADD_GINGA_NCL
                && (buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_ONESEG
                || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_FILE
                || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_ONESEG
                || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB)) {
            final int TS_PACKET_SIZE = 188; // It should be 188 bytes.
            byte[] buffer = new byte[TS_PACKET_SIZE];

            if (mRunDemuxThread == true && CommonStaticData.interactiveSwitch == true) {
                if (TSdata[0] == 0x47) // 0x47 = Sync Byte
                {
                    //ToDo::Ginga
                    //Process Ginga NCL TS here.
                    //TVlog.i(TAG, "ginga ts in+ ");
/*
                    System.arraycopy(TSdata, 0, buffer, 0, TS_PACKET_SIZE);
*/
                }
            }
        }
    }
    //]]ADD_GINGA_NCL

    public void overlay_permission() {
        if (SDK_INT >= Build.VERSION_CODES.M) {
            if (!android.provider.Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1234);
            } else {
                TVlog.i(TAG, " >>>>>> SYSTEM_ALERT_WINDOW permission already granted... ");
            }
        }

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1234) {
            if (SDK_INT >= Build.VERSION_CODES.M) {
                if (android.provider.Settings.canDrawOverlays(this)) {
                    TVlog.i(TAG, " >>>>>> SYSTEM_ALERT_WINDOW permission granted... ");
                } else {
                    // SYSTEM_ALERT_WINDOW permission not granted...
                    Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    startActivity(intent);
                    TVlog.i(TAG, " >>>>>> SYSTEM_ALERT_WINDOW permission not granted... ");
                }
            }
        }
    }

    public boolean isServiceRunningCheck() {
        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("kr.co.fci.tv.FloatingWindow".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static HashSet<String> getExternalMounts() {
        final HashSet<String> out = new HashSet<String>();
        String reg = "(?i).*vold.*(vfat|ntfs|exfat|fat32|ext3|ext4).*rw.*";
        String s = "";
        try {
            final Process process = new ProcessBuilder().command("mount").redirectErrorStream(true).start();
            process.waitFor();
            final InputStream is = process.getInputStream();
            final byte[] buffer = new byte[1024];
            while (is.read(buffer) != -1) {
                s = s + new String(buffer);
            }
            is.close();
        } catch (final Exception e) {
            e.printStackTrace();
        }

        // parse output
        final String[] lines = s.split("\n");
        for (String line : lines) {
            if (!line.toLowerCase(Locale.US).contains("asec")) {
                if (line.matches(reg)) {
                    String[] parts = line.split(" ");
                    for (String part : parts) {
                        if (part.startsWith("/"))
                            if (!part.toLowerCase(Locale.US).contains("vold"))
                                out.add(part);
                    }
                }
            }
        }
        TVlog.i(TAG, " >>>>> getExternalMounts() = "+out);
        return out;
    }

    public static String getStoragePath(Context mContext, boolean is_removale) {

        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (is_removale == removable) {
                    return path;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
    public File getUsbDrive() {
        File parent;
        parent = new File("/storage");
        try {
            for (File f : parent.listFiles()) {
                if (f.exists() && f.getName().toLowerCase().contains("usb") && f.canExecute()) {
                    return f;
                }
            }
        } catch (Exception e) {
        }
        parent = new File("/mnt/sdcard/usbStorage");
        if (parent.exists() && parent.canExecute())
            return (parent);
        parent = new File("/mnt/sdcard/usb_storage");
        if (parent.exists() && parent.canExecute())
            return parent;

        return null;
    }*/

    // // [[ solution switching mode 20170223
    public void reStart_TV(){
        FCI_TVi.AVStop();
        if (mCaptionView != null) {
            mCaptionView.setText("");
            mCaptionView.invalidate();
        }
        if (mSuperimposeView != null) {
            mSuperimposeView.setText("");
            mSuperimposeView.invalidate();
        }
        if (subTitleView != null)
        {
            subTitleView.setText(Html.fromHtml(""));
        }

        FCI_TVi.deInit();

        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
            envSet_JP();
        } else {
            envSet_Normal();
        }

        if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1)  {
            FCI_TVi.init(this.getPackageName(), null, mUsbFd, mUsbDeviceName, getVersionForDongle());
        } else {
            recordAndCapturePath FixedRecPath=  getCurrentRecordingPath();
            FCI_TVi.init(this.getPackageName(),FixedRecPath.fullPath, mUsbFd, mUsbDeviceName, getVersionForDongle());
        }
    }
    // ]]

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /*
    public String[] getExternalStorageDirectories() {

        List<String> results = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //Method 1 for KitKat & above
            File[] externalDirs = getExternalFilesDirs(null);

            for (File file : externalDirs) {
                String path = file.getPath().split("/Android")[0];

                boolean addPath = false;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    addPath = Environment.isExternalStorageRemovable(file);
                }
                else {
                    addPath = Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(file));
                }

                if (addPath){
                    results.add(path);
                }
            }
        }

        if (results.isEmpty()) { //Method 2 for all versions
            // better variation of: http://stackoverflow.com/a/40123073/5002496
            String output = "";
            try {
                final Process process = new ProcessBuilder().command("mount | grep /dev/block/vold")
                        .redirectErrorStream(true).start();
                process.waitFor();
                final InputStream is = process.getInputStream();
                final byte[] buffer = new byte[1024];
                while (is.read(buffer) != -1) {
                    output = output + new String(buffer);
                }
                is.close();
            } catch (final Exception e) {
                e.printStackTrace();
            }
            if (!output.trim().isEmpty()) {
                String devicePoints[] = output.split("\n");
                for(String voldPoint: devicePoints) {
                    results.add(voldPoint.split(" ")[2]);
                }
            }
        }

        //Below few lines is to remove paths which may not be external memory card, like OTG (feel free to comment them out)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < results.size(); i++) {
                if (!results.get(i).toLowerCase().matches(".*[0-9a-f]{4}[-][0-9a-f]{4}")) {
                    Log.d(TAG, results.get(i) + " might not be extSDcard");
                    results.remove(i--);
                }
            }
        } else {
            for (int i = 0; i < results.size(); i++) {
                if (!results.get(i).toLowerCase().contains("ext") && !results.get(i).toLowerCase().contains("sdcard")) {
                    Log.d(TAG, results.get(i)+" might not be extSDcard");
                    results.remove(i--);
                }
            }
        }

        String[] storageDirectories = new String[results.size()];
        for(int i=0; i<results.size(); ++i) storageDirectories[i] = results.get(i);

        return storageDirectories;
    }*/
}

