package kr.co.fci.tv.chat;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fci.tv.FCI_TV;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lme.dtv.lmedtvsdk;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import kr.co.fci.tv.ChatSubSurface;
import kr.co.fci.tv.MainActivity;
import kr.co.fci.tv.R;
import kr.co.fci.tv.SubSurfaceSet;
import kr.co.fci.tv.TVEVENT;
import kr.co.fci.tv.buildOption;
import kr.co.fci.tv.channelList.Channel;
import kr.co.fci.tv.emoji.Emojicon;
import kr.co.fci.tv.emoji.EmojiconEditText;
import kr.co.fci.tv.emoji.EmojiconGridView;
import kr.co.fci.tv.emoji.EmojiconsPopup;
import kr.co.fci.tv.saves.CommonStaticData;
import kr.co.fci.tv.saves.SharedPreference;
import kr.co.fci.tv.saves.TVProgram;
import kr.co.fci.tv.setting.InputDialog;
import kr.co.fci.tv.tvSolution.AudioOut;
import kr.co.fci.tv.tvSolution.CaptionDirectView;
import kr.co.fci.tv.tvSolution.FCI_TVi;
import kr.co.fci.tv.tvSolution.ScanProcess_chat;
import kr.co.fci.tv.tvSolution.SignalMonitor;
import kr.co.fci.tv.tvSolution.TVBridge;
import kr.co.fci.tv.util.CustomToast;
import kr.co.fci.tv.util.TVlog;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static java.lang.System.exit;
import static kr.co.fci.tv.TVEVENT.E_CHANNEL_CHANGE_TIMEOVER_CHAT;
import static kr.co.fci.tv.TVEVENT.E_SCAN_MONITOR_CHAT;
import static kr.co.fci.tv.TVEVENT.E_SIGNAL_NOTI_MSG_CHAT;

/**
 * Created by live.kim on 2017-03-24.
 */

public class ChatMainActivity extends AppCompatActivity implements View.OnClickListener, SurfaceHolder.Callback {

    private static String TAG = "ChatMainActivity ";

    CustomToast customToast = null;
    private long mLastClickTimeReturn = 0;
    private final static int DOUBLE_CLICK_TOLERANCE = 1000; // 3000;
    private static final int RC_SIGN_IN = 1001;

//  String[] arr_svcmodeswitch_jp;
    private TextView subTitleView_chat = null;
    private TextView superImposeView_chat = null;
    private final static int CAPTION_CLEAR_TIME_CHAT = 15000;
    private final static int SUPERIMPOSE_CLEAR_TIME_CHAT = 15000;
    //JAPAN_CAPTION[[
    private FrameLayout mCaptionLayout_chat = null;
    private FrameLayout mSuperimposeLayout_chat = null;
    private CaptionDirectView mCaptionView_chat;
    private CaptionDirectView mSuperimposeView_chat;
    private final int M_TYPE_CAPTION_SUBTITLE = 0;
    private final int M_TYPE_CAPTION_SUPERIMPOSE = 1;
    //]]JAPAN_CAPTION

    public static LinearLayout chat_ll_audioOnlyChannel;
    public static LinearLayout chat_ll_black;

    public Handler mHandler_chat;
    public ScanProcess_chat doScan_chat;

    public LinearLayout chat_ll_scan_progress;
    public ProgressBar chat_scan_progressBar;
    public Button chat_btn_scan_cancel;

    public static LinearLayout ll_chatAutoSearch;
    private ProgressBar progressing_autoSearch_chat;
    public static Button btn_return_chat;

    //private View chat_view;
    private static NetworkChangeReceiver mNetworkReceiver_Chat = null;
    public static SysBroadcastReceiver mSysReceiver_Chat = null;
    public static SysBroadcastReceiver mScreenOff_Chat = null;
    public static SysBroadcastReceiver mScreenOn_Chat = null;
    // usbdongle[[
    public static UsbDevice chat_currentUsbDevice = null;
    private static SysBroadcastReceiver mUsbReceiver = null;
    // ]]usbdongle

    WindowManager mWindowManager;

    //WindowManager.LayoutParams mParams;

    // Firebase - Realtime Database
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;

    // Firebase - Authentication
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;

    // Views
    public TextView mNetworkMsg;
    public ListView mCahtListView;
    //private EditText mEdtMessage;
    private SignInButton mBtnGoogleSignIn; // login button
    private Button mBtnGoogleSignOut; // logout button

    private TextView mTxtProfileInfo; // user information display
    private ImageView mImgProfile; // user profile image display

    // Values
    public ChatAdapter mChatAdapter;
    private String userName;

    public static boolean isChat = true;

    private static SurfaceHolder chatVideoSurfaceHolder = null;
    public static SurfaceView sv_chatView = null;
    public static SurfaceView svSub_chatView = null;
    public static SurfaceHolder chatVideoSurfaceHolderSub = null;
    private Surface mainSurface;

    public int frameWidthChat = 0;
    public int frameHeightChat = 0;

    private static Cursor mCursor_chat;
    //Uri mUri;
    //public int chat_mChannelIndex = CommonStaticData.lastCH;
    int AudioFormat=0x00;        // recording 0x60(HEAAV), 0x40(AAC)
    int VideoFormat=0x00;        // recording 0x04(H.264)
    int Scrambled = 1;             // 0: scramble ch, 1: free ch
    int mRemoteKey = 0;
    int mSvcNumber = 0;
    public LinearLayout chat_ll_age_limit;
    TextView chat_tv_age_limit_msg_10;
    TextView chat_tv_age_limit_msg_12;
    TextView chat_tv_age_limit_msg_14;
    TextView chat_tv_age_limit_msg_16;
    TextView chat_tv_age_limit_msg_18;
    LinearLayout chat_ll_scramble_msg;
    TextView chat_tv_scramble_title;
    TextView chat_tv_scramble_msg;
    private boolean SignalStatFlag =false;
    int signal_check_cnt = 0;
    private final static int BUTTON_CLICK_TIME = 100;
    private final static int NO_SIGNAL_MSG_TIME = 5000;  // live add
    private final static int SIGNAL_MONITER_TIME = 1000;  // live change from 1000 to 2000
    private final static int SIGNAL_MONITER_TIME_USB = 2000;  //
    private final static int CONTROLLER_HIDE_TIME = 7000;
    private SignalMonitor signalMoniter = null;
    // parent rate checking
    //private Boolean screenbl_enable = false;
    //private Boolean password_verify = false;
    private int chat_curr_rate;
    AudioOut audioOut;
    LinearLayout chat_ll_ch_info;
    private TextView chat_currChNo;
    private TextView chat_currRemoteNo;
    public TextView chat_currCH;
    //private CustomView chat_progressingChange;
    private ProgressBar chat_progressingChange;
    LinearLayout chat_controllerLayout;
    LinearLayout chat_status_bar;
    public Typeface mFont_chat, tf_chat;
    private View decorView;
    private int uiOptions;
    private int[] channelChangeProcLocation =null;

    //usbdongle[[
    UsbDevice mUsbDevice;
    UsbDeviceConnection mUsbConnection;
    UsbInterface mUsbInterface;
    String mUsbDeviceName;
    int mUsbFd = -1;
    int mPermissionRequested = 0;
    private final int USB_CHIP_TYPE_NONE = 0;
    private final int USB_CHIP_TYPE_NXP = 1;
    private final int USB_CHIP_TYPE_LME = 2;
    private final int USB_NXP_VENDOR_ID = 7510;
    private final int USB_NXP_PRODUCT_ID = 24896;
    private final int USB_LME_MODE_NONE = 0;
    private final int USB_LME_MODE_COLD = 1;
    private final int USB_LME_MODE_WARM = 2;
    private final int USB_LME_VENDOR_ID = 13124;
    private final int USB_LME_COLD_PRODUCT_ID = 4384;
    private final int USB_LME_WARM_FULLSEG_PRODUCT_ID = 9344;
    int mUsbChipType = USB_CHIP_TYPE_NONE;
    int mUsbLMEMode = USB_LME_MODE_NONE;
    boolean mUsbConnected = false;
    PendingIntent mPermissionIntent = null;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    lmedtvsdk mylme_sdk;
    //static private boolean TVON =false;

    ImageView chat_iv_scan;
    ImageView chat_iv_prev;
    ImageView chat_iv_next;
    ImageView chat_iv_max;
//  Button chat_btn_receiveMode;

    EmojiconEditText emojiconEditText;
    View rootView;
    ImageView emojiButton;
    ImageView submitButton;
    TextView txtEmojis;
    EmojiconsPopup popup;

    TextView tv_message;
    public static LinearLayout chat_changeChannelView =null;
    public static ImageView chat_channelChangeBG = null;
    public LinearLayout chat_ll_noSignal;
    TextView chat_programNotMsg;
    TextView chat_noSignal;
    TextView chat_noChannel;

    TextView chat_channel_search;
    public TextView chat_scan_found;
    TextView chat_loadingChannel;
    TextView tv_autoSearch_title_chat;
    TextView tv_autoSearch_msg_chat;

    TextView chat_room_title;

    RelativeLayout rl_ChType_chat;
    ImageView iv_ChType_chat;
    ImageView iv_ChFree_chat;

    public static ChatMainActivity instance;
    public static ChatMainActivity getInstance()
    {
        return instance;
    }

    public static Cursor chat_getCursor( ){
        return mCursor_chat;
    }

    public Handler Chat_Handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            TVEVENT event = TVEVENT.values()[msg.what];
            /*if (TVON==false)
            {
                TVlog.i(TAG, "---------------- TV OFF -------------------");
                return;
            }*/
            switch (event) {

                case E_CAPTION_NOTIFY_CHAT: {
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
                                        if (mCaptionView_chat != null) {
                                            mCaptionView_chat.setText(caption_info);
                                            mCaptionView_chat.invalidate();
                                        }
                                    }
                                    //]]JAPAN_CAPTION
                                    else {
                                        subTitleView_chat.setText(Html.fromHtml(caption_info));
                                    }

                                    removeEvent(TVEVENT.E_CAPTION_CLEAR_NOTIFY_CHAT);
                                    postEvent(TVEVENT.E_CAPTION_CLEAR_NOTIFY_CHAT, CAPTION_CLEAR_TIME_CHAT);
                                } else {
                                    //JAPAN_CAPTION[[
                                    if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                                        if (mCaptionView_chat != null) {
                                            mCaptionView_chat.setText("");
                                            mCaptionView_chat.invalidate();
                                        }
                                    }
                                    //]]JAPAN_CAPTION
                                    else {
                                        subTitleView_chat.setText(Html.fromHtml(""));
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;

                case E_CAPTION_CLEAR_NOTIFY_CHAT: {
                    //JAPAN_CAPTION[[
                    if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                        if (mCaptionView_chat != null) {
                            mCaptionView_chat.setText("");
                            mCaptionView_chat.invalidate();
                        }
                    }
                    //]]JAPAN_CAPTION
                    else {
                        subTitleView_chat.setText(Html.fromHtml(""));
                    }
                }
                break;

                case E_SUPERIMPOSE_NOTIFY_CHAT: {
                    try {
                        Bundle newSuperimpose = (Bundle) msg.obj;
                        String superimpose_info = newSuperimpose.getString("superimpose_info");

                        if (superimpose_info.length() > 0) {
                            //JAPAN_CAPTION[[
                            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                                if (mSuperimposeView_chat != null) {
                                    mSuperimposeView_chat.setText(superimpose_info);
                                    mSuperimposeView_chat.invalidate();
                                }
                            }
                            //]]JAPAN_CAPTION
                            else {
                                // live modify
                                if (CommonStaticData.superimposeSwitch == true) {
                                    superImposeView_chat.setVisibility(View.VISIBLE);
                                } else {
                                    superImposeView_chat.setVisibility(View.INVISIBLE);
                                }
                                //
                                superImposeView_chat.setText(Html.fromHtml(superimpose_info));
                            }

                            removeEvent(TVEVENT.E_SUPERIMPOSE_CLEAR_NOTIFY_CHAT);
                            postEvent(TVEVENT.E_SUPERIMPOSE_CLEAR_NOTIFY_CHAT, SUPERIMPOSE_CLEAR_TIME_CHAT);
                        } else {
                            //JAPAN_CAPTION[[
                            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                                if (mSuperimposeView_chat != null) {
                                    mSuperimposeView_chat.setText("");
                                    mSuperimposeView_chat.invalidate();
                                }
                            }
                            //]]JAPAN_CAPTION
                            else {
                                // live modify
                                if (CommonStaticData.superimposeSwitch == true) {
                                    superImposeView_chat.setVisibility(View.VISIBLE);
                                } else {
                                    superImposeView_chat.setVisibility(View.INVISIBLE);
                                }
                                //
                                superImposeView_chat.setText(Html.fromHtml(""));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

                case E_SUPERIMPOSE_CLEAR_NOTIFY_CHAT: {
                    //JAPAN_CAPTION[[
                    if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                        if (mSuperimposeView_chat != null) {
                            mSuperimposeView_chat.setText("");
                            mSuperimposeView_chat.invalidate();
                        }
                    }
                    //]]JAPAN_CAPTION
                    else {
                        superImposeView_chat.setText(Html.fromHtml(""));
                    }
                }
                break;

                case E_SCAN_COMPLETED_CHAT:
                    TVlog.i(TAG, "---------------- E_SCAN_COMPLETED_CHAT-------------------");
                    if (CommonStaticData.handoverMode > CommonStaticData.HANDOVER_MODE_OFF) {
                        if (CommonStaticData.handoverIndex != -1) {
                            MainActivity.getInstance().mChannelIndex = CommonStaticData.handoverIndex;
                            TVlog.e(TAG, "handover mode = " + CommonStaticData.handoverMode + " , channel index =  " + MainActivity.getInstance().mChannelIndex);
                        }
                        else {
                            CommonStaticData.handoverIndex = 0;
                        }
                    }
                    if (FCI_TVi.initiatedSol) {
                        channelChangeStartView(false);
                    }

                    if (CommonStaticData.scanCHnum > 0) {
                        MainActivity.isNoChannel = false;
                        chat_noChannel.setVisibility(View.INVISIBLE);
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
                        mCursor_chat = MainActivity.getCursor();
                        if (MainActivity.getInstance().mUri != null) {
                            if (mCursor_chat != null && mCursor_chat.isClosed() == false) {
                                mCursor_chat.close();
                                mCursor_chat = null;
                            }
                            mCursor_chat = getContentResolver().query(MainActivity.getInstance().mUri, CommonStaticData.PROJECTION, TVProgram.Programs.TYPE + "=?", CommonStaticData.selectionArgsTV, null);
                            MainActivity.setCursor(mCursor_chat);
                        }
                        if (mCursor_chat != null && mCursor_chat.getCount() > 0 && (mCursor_chat.getPosition() < mCursor_chat.getCount())) {
                            if (MainActivity.getInstance().mChannelIndex >= mCursor_chat.getCount()) {
                                MainActivity.getInstance().mChannelIndex = 0;
                            }
                            mCursor_chat.moveToPosition(MainActivity.getInstance().mChannelIndex);
                            mRemoteKey = mCursor_chat.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_REMOTE_KEY);
                            mSvcNumber = mCursor_chat.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_NUMBER);
                            if (CommonStaticData.isProcessingUpdate) {
                                if ((mRemoteKey != TVBridge.getLastRemoteKey()) || (mSvcNumber != TVBridge.getLastSvcID())) {
                                    int cursorCount = mCursor_chat.getCount();
                                    if (cursorCount >= TVBridge.getLastListCount()) { //service is increased or contents are changed.
                                        for (int i = 0; i < cursorCount; i++) {
                                            mCursor_chat.moveToPosition(i);
                                            if ((mCursor_chat.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_REMOTE_KEY) == TVBridge.getLastRemoteKey())
                                                    && (mCursor_chat.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_NUMBER) == TVBridge.getLastSvcID())) {
                                                if (MainActivity.getInstance().mChannelIndex != i) {
                                                    statusOfNeedToChange = NEED_TO_CHANGE_CHANNEL_CHANGE_INDEX;
                                                }
                                                MainActivity.getInstance().mChannelIndex = i;
                                                break;
                                            }
                                        }
                                    }
                                    else { //service is decreased.
                                        for (int i = 0; i < cursorCount; i++) {
                                            mCursor_chat.moveToPosition(i);
                                            if ((mCursor_chat.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_REMOTE_KEY) == TVBridge.getLastRemoteKey())
                                                    && (mCursor_chat.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV) == 0)) {
                                                if (MainActivity.getInstance().mChannelIndex != i) {
                                                    statusOfNeedToChange = NEED_TO_CHANGE_CHANNEL_CHANGE_INDEX;
                                                }
                                                MainActivity.getInstance().mChannelIndex = i;
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (CommonStaticData.handoverMode == CommonStaticData.HANDOVER_MODE_ON_UPDATE_LIST) {
                                    if (MainActivity.getInstance().mChannelIndex != CommonStaticData.handoverIndex && mCursor_chat.getCount() > CommonStaticData.handoverIndex) {
                                        MainActivity.getInstance().mChannelIndex = CommonStaticData.handoverIndex;
                                        mCursor_chat.moveToPosition(MainActivity.getInstance().mChannelIndex);
                                        TVlog.e(TAG, "handover: list reloaded & different index: channel index =  " + MainActivity.getInstance().mChannelIndex);
                                    }
                                    else {
                                        TVlog.e(TAG, "handover: list reloaded & same index: channel index =  " + MainActivity.getInstance().mChannelIndex);
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
                                    TVlog.e(TAG, "handover: same list & same index: channel index =  " + MainActivity.getInstance().mChannelIndex);
                                }
                            }

                            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                                switch (CommonStaticData.receivemode){
                                    case 0:     // 1seg
                                        if (mCursor_chat.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV)!=0) {
                                            for (int i=0; i < mCursor_chat.getCount(); i++) {
                                                mCursor_chat.moveToPosition(i);
                                                if (mCursor_chat.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV) == 0) {
                                                    MainActivity.getInstance().mChannelIndex = i;
                                                    break;
                                                }
                                            }
                                            if (MainActivity.getInstance().mChannelIndex==0){   // not found channel
                                                TVBridge.stop();
                                                channelChangeEndView(false);
                                                CustomToast toast = new CustomToast(getApplicationContext());
                                                toast.showToast(getApplicationContext(), getApplicationContext().getString(R.string.ch_change_fail), Toast.LENGTH_SHORT);
                                            }
                                        }
                                        break;
                                    case 1:     // fullseg
                                        if (mCursor_chat.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV)!=1) {
                                            for (int i=0; i < mCursor_chat.getCount(); i++) {
                                                mCursor_chat.moveToPosition(i);
                                                if (mCursor_chat.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV) == 1) {
                                                    MainActivity.getInstance().mChannelIndex = i;
                                                    break;
                                                }
                                            }
                                            if (MainActivity.getInstance().mChannelIndex==0) {      // not found channel
                                                TVBridge.stop();
                                                channelChangeEndView(false);
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

                            mCursor_chat.moveToPosition(MainActivity.getInstance().mChannelIndex);

                            int freq = Integer.parseInt(mCursor_chat.getString(CommonStaticData.COLUMN_INDEX_SERVICE_FREQ));
                            TVlog.i(TAG, " >>>>> current freq = " + freq);

                            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                                int channelNo = 13 + (int)((freq-473143)/6000);
                                chat_currChNo.setText(channelNo+"ch");
                            } else {
                                if (buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_ONESEG
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                                    // for Sri Lanka
                                    int channelNo = 13 + (int)((freq-474000)/8000);
                                    chat_currChNo.setText(channelNo+"ch");
                                } else {
                                    int channelNo = 14 + (int)((freq-473143)/6000);
                                    chat_currChNo.setText(channelNo+"ch");
                                }
                            }

                            //String channelName = channel.getName();
                            String channelName = mCursor_chat.getString(CommonStaticData.COLUMN_INDEX_SERVICE_NAME);
                            TVlog.i(TAG, " >>>>> channelName = "+ channelName);
                            String[] split_channelName = channelName.split(" ");

                            // live modify 20170104
                            chat_currRemoteNo.setText(split_channelName[0]);
                            String str = "";
                            for (int i = 1; i < split_channelName.length; i++) {
                                str += split_channelName[i];
                                if (i < split_channelName.length - 1) {
                                    str += " ";
                                }
                            }
                            chat_currCH.setText(str);

                            if (chat_room_title != null) {
                                chat_room_title.setText(getApplicationContext().getString(R.string.chat_room_name) + " (" + getApplicationContext().getResources().getString(R.string.app_name) + ") " + str);
                            }
                            //

                            int type = (int) mCursor_chat.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV);
                            int free = (int) mCursor_chat.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_FREE);
                            if (type == 0) { // if 1seg
                                if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                                    iv_ChType_chat.setBackgroundResource(R.drawable.jp_1seg);
                                    iv_ChFree_chat.setVisibility(View.GONE);
                                } else {
                                    iv_ChType_chat.setBackgroundResource(R.drawable.tv_icon_1seg);
                                    if (free == 0) {
                                        iv_ChFree_chat.setVisibility(View.VISIBLE);
                                    } else {
                                        iv_ChFree_chat.setVisibility(View.GONE);
                                    }
                                }
                            } else if (type == 1) { // if fullseg
                                if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                                    iv_ChType_chat.setBackgroundResource(R.drawable.jp_fullseg);
                                    iv_ChFree_chat.setVisibility(View.GONE);
                                } else {
                                    iv_ChType_chat.setBackgroundResource(R.drawable.tv_icon_fullseg);
                                    if (free == 0) {
                                        iv_ChFree_chat.setVisibility(View.VISIBLE);
                                    } else {
                                        iv_ChFree_chat.setVisibility(View.GONE);
                                    }
                                }
                            }
                            rl_ChType_chat.setVisibility(View.VISIBLE);

                            MainActivity.getInstance().sendEvent(TVEVENT.E_UPDATE_EPG_NAME_AND_DURATION);

                            AudioFormat = mCursor_chat.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_AUDFORM);
                            VideoFormat = mCursor_chat.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_VIDFORM);
                            Scrambled = mCursor_chat.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_FREE);
                            mRemoteKey = mCursor_chat.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_REMOTE_KEY);
                            mSvcNumber = mCursor_chat.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_NUMBER);

                            removeEvent(E_SCAN_MONITOR_CHAT);
                            //MainActivity.getInstance().removeEvent(TVEVENT.E_SCAN_MONITOR);

                            TVlog.i (TAG, " >>>>> Scrambled = "+String.valueOf(Scrambled));

                            if (Scrambled == 0) {
                                sendEvent(TVEVENT.E_BADSIGNAL_CHECK_CHAT, 2, 0, null);
                                //MainActivity.getInstance().sendEvent(TVEVENT.E_BADSIGNAL_CHECK, 2, 0, null);
                            }

                            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                            boolean isScreenOn;
                            if (Build.VERSION.SDK_INT <= 19) {
                                isScreenOn = pm.isScreenOn();
                            } else {
                                isScreenOn = pm.isInteractive();
                            }
                            TVlog.i (TAG, " >>>>> isScreenOn = "+isScreenOn);
                            if (isScreenOn) {
                                if (statusOfNeedToChange == NEED_TO_CHANGE_CHANNEL_FIRST_LOAD || statusOfNeedToChange == NEED_TO_CHANGE_CHANNEL_CHANGE_INDEX) {
                                    TVlog.i(TAG, " scan completed " + statusOfNeedToChange);

                                    // checking TS playback running...

                                    /*if (isCheckingPlayback())
                                    {
                                        TVlog.i(TAG, " playback running  ");
                                        break;
                                    }*/
                                    if (buildOption.LOG_CAPTURE_MODE==3)
                                    {
                                        //TVBridge.serviceID_start(0);
                                        TVBridge.dualAV_start(0, true);
                                        postEvent(TVEVENT.E_AUTO_CHANGE_CHANNEL_TEST_CHAT, 20 * 1000);
                                    } else {
                                        //TVBridge.serviceID_start(MainActivity.getInstance().mChannelIndex);
                                        int[] info = FCI_TVi.GetPairNSegInfoOfCHIndex(MainActivity.getInstance().mChannelIndex);
                                        int isAudioOnly = info[5];
                                        if (isAudioOnly == 1) {
                                            CommonStaticData.isAudioChannel = true;
                                            channelChangeEndView(false);
                                            if (chat_ll_black != null) {
                                                chat_ll_black.setVisibility(View.VISIBLE);
                                            }
                                            if (chat_ll_audioOnlyChannel != null) {
                                                chat_ll_audioOnlyChannel.setVisibility(View.VISIBLE);
                                            }
                                        } else {
                                            CommonStaticData.isAudioChannel = false;
                                            if (chat_ll_black != null) {
                                                chat_ll_black.setVisibility(View.INVISIBLE);
                                            }
                                            if (chat_ll_audioOnlyChannel != null) {
                                                chat_ll_audioOnlyChannel.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                        TVBridge.dualAV_start(MainActivity.getInstance().mChannelIndex, true);
                                    }
                                } else {
                                    channelChangeEndView(false);
                                }
                            } else {

                                TVlog.i(TAG, " =====  screen off =========");
                                MainActivity.getInstance().SolutionStop();
                            }
                        } else {
                            //postEvent(TVEVENT.E_CHLIST_UPDATE_CHAT, 0);
                        }
                        chat_noChannel.setVisibility(View.INVISIBLE);
                        chat_ll_noSignal.setVisibility(View.INVISIBLE);
                    } else {

                        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                        boolean isScreenOn;
                        if (Build.VERSION.SDK_INT <= 19) {
                            isScreenOn = pm.isScreenOn();
                        } else {
                            isScreenOn = pm.isInteractive();
                        }

                        if (isScreenOn)
                        {

                            TVlog.i(TAG, " =====  no scan =========");
                            if (chat_currChNo != null && chat_currCH != null) {
                                chat_currChNo.setText("- -ch");
                                chat_currRemoteNo.setText("- - -");
                                chat_currCH.setText(R.string.no_channel_title);
                            }

                            if (chat_room_title != null) {
                                chat_room_title.setText(getApplicationContext().getString(R.string.chat_room_name) + " (" + getApplicationContext().getResources().getString(R.string.app_name) + ") ");
                            }

                            if (rl_ChType_chat != null) {
                                rl_ChType_chat.setVisibility(View.GONE);
                            }

                            MainActivity.isNoChannel = true;

                            chat_changeChannelView.setVisibility(View.INVISIBLE);
                            chat_noChannel.setVisibility(VISIBLE);

                            if (FCI_TVi.initiatedSol) {
                                CommonStaticData.badSignalFlag = false;
                                CommonStaticData.encryptFlag = false;
                                CommonStaticData.ageLimitFlag = false;
                                chat_ll_scramble_msg.setVisibility(View.INVISIBLE);
                            }

                            /*if (isCheckingPlayback())
                            {
                                TVlog.i(TAG, " playback running  ");
                                break;
                            }*/

                            if (buildOption.ADD_TS_CAPTURE != true) {
                                new InputDialog(instance, InputDialog.TYPE_TV_NOCHANNELLIST, null, null, null);
                            }
                        } else {
                            TVlog.i(TAG, " =====  no scan and screen off =========");
                            MainActivity.getInstance().SolutionStop();
                        }
                    }
                    CommonStaticData.loadingNow = false;

                    /*if (buildOption.GUI_STYLE == 2 || buildOption.GUI_STYLE == 3) {
                        setDefaultChannel = false;  // live add
                    }*/

                    break;

                case E_SCAN_START_CHAT:
                    TVlog.i(TAG, " >>>>> E_SCAN_START_CHAT ");
                    TVBridge.scanStop();
                    TVBridge.stop();
                    // channel index initialize
                    CommonStaticData.lastCH = 0;
                    CommonStaticData.captionSelect = 0; // justin add
                    CommonStaticData.superimposeSelect = 0;
                    CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                    editor.putInt(CommonStaticData.lastChannelKey, CommonStaticData.lastCH);
                    editor.commit();
                    CommonStaticData.scanningNow = true;
                    removeEvent(TVEVENT.E_SIGNAL_NOTI_MSG_CHAT);
                    removeEvent(TVEVENT.E_BADSIGNAL_CHECK_CHAT);

                    if (sv_chatView != null && sv_chatView.isShown()) {
                        sv_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                    }
                    if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                        if (svSub_chatView != null && svSub_chatView.isShown()) {
                            svSub_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                        }
                    } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                            (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                    buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                    buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                        if (svSub_chatView != null && svSub_chatView.isShown()) {
                            svSub_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                        }
                    }

                    sendEvent(TVEVENT.E_CAPTION_CLEAR_NOTIFY_CHAT);
                    sendEvent(TVEVENT.E_SUPERIMPOSE_CLEAR_NOTIFY_CHAT);

                    if (ll_chatAutoSearch.getVisibility() == VISIBLE) {
                        ll_chatAutoSearch.setVisibility(View.INVISIBLE);
                    }
                    if (chat_noChannel.getVisibility() == VISIBLE) {
                        chat_noChannel.setVisibility(View.INVISIBLE);
                    }

                    if (chat_changeChannelView.getVisibility() == VISIBLE) {
                        chat_changeChannelView.setVisibility(View.INVISIBLE);
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
                    chat_ll_scan_progress.setVisibility(VISIBLE);
                    doScan_chat = new ScanProcess_chat(instance);
                    TVBridge.scan();
                    break;

                case E_SCAN_PROCESS_CHAT :
                    int progress_chat = (int)msg.arg1;
                    int found_chat = (int)msg.arg2;
                    int freqKHz_chat = 0;
                    if (msg.obj != null) {
                        freqKHz_chat = (int)msg.obj;
                    }
                    //   TVlog.e(TAG, "E_SCAN_PROCESS " + progress + " % " + found + " found");
                    if (doScan_chat != null) {
                        if (progress_chat < 97) {
                            if (buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA
                                    || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_ONESEG
                                    || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                                doScan_chat.showProgress_chat(progress_chat, found_chat, freqKHz_chat+8000, doScan_chat.SHOW_PROGRESS_ON_CHAT);
                            } else {
                                doScan_chat.showProgress_chat(progress_chat, found_chat, freqKHz_chat+6000, doScan_chat.SHOW_PROGRESS_ON_CHAT);
                            }
                            CommonStaticData.scanCHnum = found_chat;
                            //MainActivity.getInstance().mChannelIndex = 0;
                            MainActivity.getInstance().mChannelIndex = 0;
                        } else if (progress_chat >= 97 && progress_chat < 100) {
                            doScan_chat.showProgress_chat(progress_chat, found_chat, freqKHz_chat, doScan_chat.SHOW_PROGRESS_ON_CHAT);
                            CommonStaticData.scanCHnum = found_chat;
                            //MainActivity.getInstance().mChannelIndex = 0;
                            MainActivity.getInstance().mChannelIndex = 0;
                        } else {
                            doScan_chat.showProgress_chat(progress_chat, found_chat, freqKHz_chat, doScan_chat.SHOW_PROGRESS_OFF_CHAT);
                            //CommonStaticData.scanCHnum = found;
                        }
                    }

                    break;

                case E_SCAN_CANCEL_CHAT:
                    TVlog.i(TAG, "---------------- E_SCAN_CANCEL-------------------");
                    if (buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA
                            || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_ONESEG
                            || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                        doScan_chat.showProgress_chat(0, 0, 474000, doScan_chat.SHOW_PROGRESS_OFF_CHAT);
                    } else {
                        doScan_chat.showProgress_chat(0, 0, 473143, doScan_chat.SHOW_PROGRESS_OFF_CHAT);
                    }
                    chat_ll_scan_progress.setVisibility(View.INVISIBLE);
                    TVBridge.scanStop();
                    if (CommonStaticData.handoverMode == CommonStaticData.HANDOVER_MODE_ON_NORMAL) {
                        sendEvent(TVEVENT.E_SCAN_COMPLETED_CHAT);
                    }
                    break;

                case E_CHANNEL_LIST_AV_STARTED_CHAT: {
                    TVlog.i(TAG, "---------------- E_CHANNEL_LIST_AV_STARTED_CHAT-------------------");

                    //CommonStaticData.screenBlockFlag = false;    // justin 20170523
                    CommonStaticData.passwordVerifyFlag = false;
                    CommonStaticData.ageLimitFlag = false;

                    //           controllerLayout.setVisibility(View.VISIBLE);
                    channelChangeStartView(false);
                }

                case E_CHAT_STOP_NOTIFY: {
                    TVlog.i(TAG, ">>>>> E_CHAT_STOP_NOTIFY");
                    if (sv_chatView != null && sv_chatView.isShown()) {
                        sv_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                    }
                    if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                        if (svSub_chatView != null && svSub_chatView.isShown()) {
                            svSub_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                        }
                    } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                            (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                    buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                    buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                        if (svSub_chatView != null && svSub_chatView.isShown()) {
                            svSub_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                        }
                    }
                }
                break;

                case E_FIRSTVIDEO_CHAT:
                {
                    TVlog.i(TAG, " >>>>> E_FIRSTVIDEO_CHAT");
                    if (CommonStaticData.isSwitched == false) {
                        removeEvent(TVEVENT.E_BADSIGNAL_CHECK_CHAT);
                        removeEvent(E_SIGNAL_NOTI_MSG_CHAT);
                        removeEvent(TVEVENT.E_NOSIGNAL_SHOW_CHAT);
                        removeEvent(E_CHANNEL_CHANGE_TIMEOVER_CHAT);

                        CommonStaticData.isBadSignalFlag = false;
                        CommonStaticData.badSignalFlag = false;
                        CommonStaticData.encryptFlag = false;
                        CommonStaticData.ageLimitFlag = false;
                    }
                    CommonStaticData.isSwitched = false;

                    if (chat_changeChannelView.getVisibility() == VISIBLE) {
                        chat_changeChannelView.setVisibility(View.INVISIBLE);
                    }
                    channelChangeEndView(false);

                    if (sv_chatView != null && sv_chatView.isShown()) {
                        sv_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
                    }
                    if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                        if (svSub_chatView != null && svSub_chatView.isShown()) {
                            svSub_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
                        }
                    } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                            (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                    buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                    buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                        if (svSub_chatView != null && svSub_chatView.isShown()) {
                            svSub_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
                        }
                    }

                    if (CommonStaticData.isAudioChannel) {
                        if (chat_ll_black != null) {
                            chat_ll_black.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (chat_ll_black != null) {
                            chat_ll_black.setVisibility(View.INVISIBLE);
                        }
                    }

                    if (CommonStaticData.scanCHnum > 0) {
                        if (mCursor_chat != null) {
                            String channelName = mCursor_chat.getString(CommonStaticData.COLUMN_INDEX_SERVICE_NAME);
                            String[] split_channelName = channelName.split(" ");
                            String str = "";
                            for (int i = 1; i < split_channelName.length; i++) {
                                str += split_channelName[i];
                                if (i < split_channelName.length - 1) {
                                    str += " ";
                                }
                            }
                            chat_room_title.setText(getApplicationContext().getString(R.string.chat_room_name) + " (" + getApplicationContext().getResources().getString(R.string.app_name) + ") " + str);
                        }
                    } else {
                        chat_room_title.setText(getApplicationContext().getString(R.string.chat_room_name) + " (" + getApplicationContext().getResources().getString(R.string.app_name) + ") ");
                    }

                    if (ll_chatAutoSearch.getVisibility() == View.VISIBLE) {
                        ll_chatAutoSearch.setVisibility(View.INVISIBLE);
                    }

                    if (buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL
                            || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_ONESEG
                            || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_FILE) {
                        chat_curr_rate = FCI_TVi.GetCurProgramRating();
                        if ((chat_curr_rate >= (CommonStaticData.PG_Rate+1) && (CommonStaticData.PG_Rate!=0))
                                && (CommonStaticData.passwordVerifyFlag == false)
                                && (CommonStaticData.ratingsetSwitch == true)) {
                            CommonStaticData.ageLimitFlag = true;
                        } else {
                            CommonStaticData.ageLimitFlag = false;
                        }
                        sendEvent(TVEVENT.E_RATING_MONITOR_CHAT);
                    }
                    chat_noChannel.setVisibility(View.INVISIBLE);
                    chat_ll_noSignal.setVisibility(View.INVISIBLE);
                    chat_ll_scramble_msg.setVisibility(View.INVISIBLE);

                    InputDialog.nosignalNotiClear();
                    SignalStatFlag = false;
                    CommonStaticData.tuneTimeOver = false;
                    channelChangeEndView(false);

                    FCI_TVi.subSurfaceViewOnOff(FCI_TVi.getDualMode());
                }
                break;

                case E_FIRSTAUDIO_CHAT:
                {
                    TVlog.i(TAG, " >>>>> E_FIRSTAUDIO_CHAT");
                    if (CommonStaticData.isSwitched == false) {
                        removeEvent(TVEVENT.E_BADSIGNAL_CHECK_CHAT);
                        removeEvent(E_SIGNAL_NOTI_MSG_CHAT);
                        removeEvent(TVEVENT.E_NOSIGNAL_SHOW_CHAT);
                        removeEvent(E_CHANNEL_CHANGE_TIMEOVER_CHAT);

                        CommonStaticData.isBadSignalFlag = false;
                        CommonStaticData.badSignalFlag = false;
                        CommonStaticData.encryptFlag = false;
                        CommonStaticData.ageLimitFlag = false;
                    }
                    CommonStaticData.isSwitched = false;

                    if (chat_changeChannelView.getVisibility() == VISIBLE) {
                        chat_changeChannelView.setVisibility(View.INVISIBLE);
                    }
                    channelChangeEndView(false);

                    if (sv_chatView != null && sv_chatView.isShown()) {
                        sv_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
                    }
                    if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                        if (svSub_chatView != null && svSub_chatView.isShown()) {
                            svSub_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
                        }
                    } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                            (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                    buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                    buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                        if (svSub_chatView != null && svSub_chatView.isShown()) {
                            svSub_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
                        }
                    }

                    if (CommonStaticData.isAudioChannel) {
                        if (chat_ll_black != null) {
                            chat_ll_black.setVisibility(View.VISIBLE);
                        }
                        if (chat_ll_audioOnlyChannel != null) {
                            chat_ll_audioOnlyChannel.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (chat_ll_black != null) {
                            chat_ll_black.setVisibility(View.INVISIBLE);
                        }
                        if (chat_ll_audioOnlyChannel != null) {
                            chat_ll_audioOnlyChannel.setVisibility(View.INVISIBLE);
                        }
                    }

                    if (CommonStaticData.scanCHnum > 0) {
                        if (mCursor_chat != null) {
                            String channelName = mCursor_chat.getString(CommonStaticData.COLUMN_INDEX_SERVICE_NAME);
                            String[] split_channelName = channelName.split(" ");
                            String str = "";
                            for (int i = 1; i < split_channelName.length; i++) {
                                str += split_channelName[i];
                                if (i < split_channelName.length - 1) {
                                    str += " ";
                                }
                            }
                            chat_room_title.setText(getApplicationContext().getString(R.string.chat_room_name) + " (" + getApplicationContext().getResources().getString(R.string.app_name) + ") " + str);
                        }
                    } else {
                        chat_room_title.setText(getApplicationContext().getString(R.string.chat_room_name) + " (" + getApplicationContext().getResources().getString(R.string.app_name) + ") ");
                    }

                    if (ll_chatAutoSearch.getVisibility() == View.VISIBLE) {
                        ll_chatAutoSearch.setVisibility(View.INVISIBLE);
                    }

                    if (buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL
                            || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_ONESEG
                            || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_FILE) {
                        chat_curr_rate = FCI_TVi.GetCurProgramRating();
                        if ((chat_curr_rate >= (CommonStaticData.PG_Rate+1) && (CommonStaticData.PG_Rate!=0))
                                && (CommonStaticData.passwordVerifyFlag == false)
                                && (CommonStaticData.ratingsetSwitch == true)) {
                            CommonStaticData.ageLimitFlag = true;
                        } else {
                            CommonStaticData.ageLimitFlag = false;
                        }
                        sendEvent(TVEVENT.E_RATING_MONITOR_CHAT);
                    }
                    chat_noChannel.setVisibility(View.INVISIBLE);
                    chat_ll_noSignal.setVisibility(View.INVISIBLE);
                    chat_ll_scramble_msg.setVisibility(View.INVISIBLE);

                    InputDialog.nosignalNotiClear();
                    SignalStatFlag = false;
                    CommonStaticData.tuneTimeOver = false;
                    channelChangeEndView(false);

                    FCI_TVi.subSurfaceViewOnOff(FCI_TVi.getDualMode());
                }
                break;

                case E_CHANNEL_CHANGE_TIMEOVER_CHAT:
                    TVlog.i(TAG, " >>>>> E_CHANNEL_CHANGE_TIMEOVER_CHAT");
                    if (CommonStaticData.tuneTimeOver==true && CommonStaticData.scanningNow==false) {

                        //TVBridge.stop();
                        //sendEvent(TVEVENT.E_CHANNEL_CHANGE_FAIL);
                        sendEvent(TVEVENT.E_BADSIGNAL_CHECK_CHAT, 4, 0, null);
                        //MainActivity.getInstance().sendEvent(TVEVENT.E_BADSIGNAL_CHECK, 4, 0, null);
                    }
                    break;

                case E_CHANNEL_CHANGE_FAIL_CHAT:
                    channelChangeEndView(false);
                    CustomToast toast6 = new CustomToast(getApplicationContext());
                    toast6.showToast(getApplicationContext(), getApplicationContext().getString(R.string.ch_change_fail), Toast.LENGTH_SHORT);
                    break;

                case E_CHANNEL_SWITCHING_CHAT:
                    TVlog.i("live", " ==== E_CHANNEL_SWITCHING_CHAT ====");
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
                                if (mCursor_chat != null) {
                        if (mCursor_chat.getCount() > pairIndex && pairIndex != -1) {
                            orgPos = mCursor_chat.getPosition();
                            mCursor_chat.moveToPosition(pairIndex);
                            info = FCI_TVi.GetPairNSegInfoOfCHIndex(pairIndex);
                            if (info[1] == tomove) {
                                findFail = 0;
                            } else {
                                mCursor_chat.moveToPosition(orgPos);
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
                                MainActivity.getInstance().lastIndex = MainActivity.getInstance().mChannelIndex;
                                MainActivity.getInstance().mChannelIndex = oneSegIndex;
                                TVlog.i("live", " >>> changed to O-seg index 1 = " + MainActivity.getInstance().mChannelIndex);
                                CommonStaticData.fromFindFail = true;
                                TVBridge.serviceID_start(MainActivity.getInstance().mChannelIndex);
                            } else {
                                TVlog.i(TAG,  " >>> There is no channel for switching!!");
                                break;
                            }
                        } else if (isFullseg == 0 && tomove == 1){  //O-seg->F-seg
                            TVlog.i("live", " >>> CommonStaticData.fromFindFail = "+CommonStaticData.fromFindFail);
                            if (channelMainIndex != -1) {
                                if (CommonStaticData.fromFindFail == true) {
                                    MainActivity.getInstance().mChannelIndex = MainActivity.getInstance().lastIndex;
                                    TVlog.i("live", " >>> changed to F-seg index 2 = " + MainActivity.getInstance().mChannelIndex);
                                    CommonStaticData.fromFindFail = false;
                                    TVBridge.serviceID_start(MainActivity.getInstance().mChannelIndex);
                                } else {
                                    MainActivity.getInstance().mChannelIndex = pairIndex;
                                    TVlog.i("live", " >>> changed to F-seg index 3 = " + MainActivity.getInstance().mChannelIndex);
                                    CommonStaticData.fromFindFail = false;
                                    TVBridge.serviceID_start(MainActivity.getInstance().mChannelIndex);
                                }
                            } else {
                                TVlog.i(TAG,  " >>> There is no channel for switching!!");
                                break;
                            }
                        }
                    } else {
                        MainActivity.getInstance().mChannelIndex = pairIndex;
                        if (tomove == 0) { // to O-seg
                            MainActivity.getInstance().mChannelIndex = oneSegIndex;
                            TVlog.i("live", " >>> changed to 1-seg index 4 =" + MainActivity.getInstance().mChannelIndex);
                            CommonStaticData.fromFindFail = false;
                            TVBridge.serviceID_start(MainActivity.getInstance().mChannelIndex);
                        } else if (tomove == 1) { // to F-seg
                            TVlog.i("live", " >>> CommonStaticData.fromFindFail = "+CommonStaticData.fromFindFail);
                            if (CommonStaticData.fromFindFail == true) {
                                MainActivity.getInstance().mChannelIndex = MainActivity.getInstance().lastIndex;
                                TVlog.i("live", " >>> changed to F-seg index 5 = " + MainActivity.getInstance().mChannelIndex);
                                CommonStaticData.fromFindFail = false;
                                TVBridge.serviceID_start(MainActivity.getInstance().mChannelIndex);
                            } else {
                                MainActivity.getInstance().mChannelIndex = pairIndex;
                                TVlog.i("live", " >>> changed to F-seg index 6 = " + MainActivity.getInstance().mChannelIndex);
                                CommonStaticData.fromFindFail = false;
                                TVBridge.serviceID_start(MainActivity.getInstance().mChannelIndex);
                            }
                        }
                    }
                    break;

                case E_BADSIGNAL_CHECK_CHAT:
                    TVlog.i(TAG, " >>>>> E_BADSIGNAL_CHECK_CHAT");
                    int stat = (int)msg.arg1;
                    /*
                    if (isPlayBackActivity) {
                        break;
                    }*/
                    switch (stat){
                        case 1: // low buffer
                            if (SignalStatFlag==false) {
                                TVlog.i(TAG, " >>>>> E_BADSIGNAL_CHECK_CHAT CASE1");
                                if (ll_chatAutoSearch.getVisibility() == View.VISIBLE) {
                                    ll_chatAutoSearch.setVisibility(View.INVISIBLE);
                                }
                                postEvent(E_SIGNAL_NOTI_MSG_CHAT, NO_SIGNAL_MSG_TIME);     // 10sec
                                //MainActivity.getInstance().postEvent(TVEVENT.E_SIGNAL_NOTI_MSG, NO_SIGNAL_MSG_TIME);
                                SignalStatFlag = true;
                                CommonStaticData.badSignalFlag = true;  // live add
                                CommonStaticData.encryptFlag = false;  // live add
                                CommonStaticData.ageLimitFlag = false;  // live add
                            }
                            break;
                        case 2: // scramble channel
                            TVlog.i(TAG, " >>>>> E_BADSIGNAL_CHECK_CHAT CASE2");
                            if (ll_chatAutoSearch.getVisibility() == View.VISIBLE) {
                                ll_chatAutoSearch.setVisibility(View.INVISIBLE);
                            }
                            //channelChangeEndView(true);
                            /*
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
                                if (sv_chatView != null) {
                                    sv_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                                }
                                chat_tv_scramble_title.setVisibility(View.VISIBLE);
                                chat_tv_scramble_msg.setVisibility(View.VISIBLE);
                            }*/
                            CommonStaticData.encryptFlag = true;
                            CommonStaticData.badSignalFlag = false;  // live add
                            CommonStaticData.ageLimitFlag = false;
                            chat_noChannel.setVisibility(View.INVISIBLE);
                            if (chat_ll_noSignal.getVisibility() == VISIBLE) {
                                chat_ll_noSignal.setVisibility(View.INVISIBLE);
                            }

                            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                                /*
                                if (sv_chatView != null) {
                                    sv_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
                                }
                                if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                                    if (svSub_chatView != null) {
                                        svSub_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
                                    }
                                }*/
                                //chat_ll_scramble_msg.setVisibility(View.VISIBLE);
                            } else {
                                if (sv_chatView != null && sv_chatView.isShown()) {
                                    sv_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                                }
                                if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                                    if (svSub_chatView != null && svSub_chatView.isShown()) {
                                        svSub_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                                    }
                                } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                                        (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                                buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                                buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                                    if (svSub_chatView != null && svSub_chatView.isShown()) {
                                        svSub_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                                    }
                                }
                                channelChangeEndView(true);
                                chat_ll_scramble_msg.setVisibility(VISIBLE);
                            }

                            break;
                        case 3: //RF signal bad
                            TVlog.i(TAG, " >>>>> E_BADSIGNAL_CHECK_CHAT CASE3");
                            if (ll_chatAutoSearch.getVisibility() == View.VISIBLE) {
                                ll_chatAutoSearch.setVisibility(View.INVISIBLE);
                            }
                            if (CommonStaticData.scanningNow==false && CommonStaticData.scanCHnum > 0) {
                                //new InputDialog(instance, InputDialog.TYPE_SIGNALSTAT_NOTI, null, null, null);

                                // live add
                                CommonStaticData.badSignalFlag = true;
                                CommonStaticData.encryptFlag = false;
                                CommonStaticData.ageLimitFlag = false;

                                // live add
                                if (sv_chatView != null && sv_chatView.isShown()) {
                                    sv_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                                }
                                if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                                    if (svSub_chatView != null && svSub_chatView.isShown()) {
                                        svSub_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                                    }
                                } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                                        (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                                buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                                buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                                    if (svSub_chatView != null && svSub_chatView.isShown()) {
                                        svSub_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                                    }
                                }
                                chat_noChannel.setVisibility(View.INVISIBLE);
                                chat_ll_noSignal.setVisibility(VISIBLE);
                                if (chat_changeChannelView.getVisibility() == View.VISIBLE) {
                                    chat_changeChannelView.setVisibility(View.INVISIBLE);
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
                                removeEvent(TVEVENT.E_BADSIGNAL_CHECK_CHAT);
                            }
                            break;
                        case 4: //program not available
                            TVlog.i(TAG, " >>>>> E_BADSIGNAL_CHECK_CHAT CASE4");
                            if (ll_chatAutoSearch.getVisibility() == View.VISIBLE) {
                                ll_chatAutoSearch.setVisibility(View.INVISIBLE);
                            }
                            if (CommonStaticData.scanningNow==false && CommonStaticData.scanCHnum > 0) {
                                //channelChangeEndViewMulti(false);
                                /*CustomToast toast7 = new CustomToast(getApplicationContext());
                                toast7.showToast(getApplicationContext(),
                                        getApplicationContext().getString(R.string.no_signal_msg)+"\n"+
                                                getApplicationContext().getString(R.string.program_not_available), Toast.LENGTH_SHORT);*/

                                // live add
                                CommonStaticData.badSignalFlag = true;
                                CommonStaticData.encryptFlag = false;
                                CommonStaticData.ageLimitFlag = false;
                                if (sv_chatView != null && sv_chatView.isShown()) {
                                    sv_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                                }
                                if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                                    if (svSub_chatView != null && svSub_chatView.isShown()) {
                                        svSub_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                                    }
                                } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                                        (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                                buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                                buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                                    if (svSub_chatView != null && svSub_chatView.isShown()) {
                                        svSub_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                                    }
                                }

                                if (chat_changeChannelView.getVisibility() == VISIBLE) {
                                    chat_changeChannelView.setVisibility(View.INVISIBLE);
                                }
                                chat_noChannel.setVisibility(View.INVISIBLE);
                                chat_ll_noSignal.setVisibility(VISIBLE);
                                if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG) {
                                    if (CommonStaticData.scanCHnum > 0) {
                                        CommonStaticData.isBadSignalFlag = true;
                                    }
                                }
                            }
                            else {
                                removeEvent(TVEVENT.E_BADSIGNAL_CHECK_CHAT);
                            }
                            break;
                    }
                    break;

                case E_SCAN_MONITOR_CHAT:
                    TVlog.i(TAG, " >>>>> E_SCAN_MONITOR_CHAT");
                    if (CommonStaticData.scanCHnum != 0) {
                        removeEvent(E_SCAN_MONITOR_CHAT);
                        //MainActivity.getInstance().removeEvent(TVEVENT.E_SCAN_MONITOR);
                    }
                    else{
                        if (buildOption.ADD_TS_CAPTURE != true) {
                            new InputDialog(instance, InputDialog.TYPE_TV_NOCHANNELLIST, null, null, null);
                        }
                        postEvent(E_SCAN_MONITOR_CHAT, CONTROLLER_HIDE_TIME * 2);
                        //MainActivity.getInstance().postEvent(TVEVENT.E_SCAN_MONITOR, CONTROLLER_HIDE_TIME * 2);
                    }
                    break;

                case E_SIGNAL_MONITER_CHAT:
                    TVlog.i(TAG, " >>>>> E_SIGNAL_MONITER_CHAT");
                    if (signalMoniter!=null)
                    {
                        int segType;

                        signalMoniter.getSignal();

                        if (buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL
                                || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_ONESEG
                                || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                                || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_FILE) {
                            // justin 20170526
                            chat_curr_rate = FCI_TVi.GetCurProgramRating();  // curr_rate 2~6, PG_Rate 1~5
                            TVlog.e("justin", " ====> chat_currRate " + chat_curr_rate + " , Set PG-rate" + CommonStaticData.PG_Rate);

                            if ((chat_curr_rate >= (CommonStaticData.PG_Rate+1) && (CommonStaticData.PG_Rate!=0))
                                    && (CommonStaticData.passwordVerifyFlag == false)
                                    && (CommonStaticData.ratingsetSwitch == true)) {
                                CommonStaticData.ageLimitFlag = true;
                            } else {
                                CommonStaticData.ageLimitFlag = false;
                            }
                            sendEvent(TVEVENT.E_RATING_MONITOR_CHAT);
                        }

                        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                                || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB
                                || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                            postEvent(TVEVENT.E_SIGNAL_MONITER_CHAT, SIGNAL_MONITER_TIME_USB);
                        } else {
                            postEvent(TVEVENT.E_SIGNAL_MONITER_CHAT, SIGNAL_MONITER_TIME);
                        }

                    }
                    break;

                case E_SIGNAL_NOTI_MSG_CHAT:
                    TVlog.i(TAG, " >>>>> E_SIGNAL_NOTI_MSG_CHAT");
                    if (CommonStaticData.scanningNow==false) {
                        CustomToast toast8 = new CustomToast(getApplicationContext());
                        toast8.showToast(getApplicationContext(), getApplicationContext().getString(R.string.signal_weak), Toast.LENGTH_SHORT);
                        SignalStatFlag = false;
                        postEvent(TVEVENT.E_NOSIGNAL_SHOW_CHAT, NO_SIGNAL_MSG_TIME);     // 10sec
                        //MainActivity.getInstance().postEvent(TVEVENT.E_NOSIGNAL_SHOW, NO_SIGNAL_MSG_TIME);
                    }
                    break;
                case E_NOSIGNAL_SHOW_CHAT:
                    TVlog.i(TAG, " >>>>> E_NOSIGNAL_SHOW_CHAT");
                    if (CommonStaticData.scanningNow==false) {
                        TVlog.i("live", " === E_NOSIGNAL_SHOW ===");
                        channelChangeEndView(false);
                        sendEvent(TVEVENT.E_BADSIGNAL_CHECK_CHAT, 3, 0, null);
                        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG) {
                            if (CommonStaticData.scanCHnum > 0) {
                                CommonStaticData.isBadSignalFlag = true;
                            }
                        }
                    }
                    break;

                case E_CHAT_SURFACE_SUB_ONOFF: {
                    int onoff = (int) msg.arg1;
                    if (onoff==1)
                    {
                        TVlog.i(TAG, " E_CHAT_SURFACE_SUB_ON  On ") ;
                        setChatSubSurfaceVisible(true);
                    }else
                    {
                        TVlog.i(TAG, " E_CHAT_SURFACE_SUB_ON  Off ") ;
                        setChatSubSurfaceVisible(false);
                    }
                }
                break;


                case E_RATING_MONITOR_CHAT:
                    chat_curr_rate = FCI_TVi.GetCurProgramRating();  // curr_rate 2~6, PG_Rate 1~5
                    TVlog.i("chat_justin ", "password_verify "+CommonStaticData.passwordVerifyFlag);
                    //if (screenbl_enable.equals(false) && password_verify.equals(false)) {
                    if (CommonStaticData.ageLimitFlag) {
                        //TVBridge.stop();
                        if (sv_chatView != null && sv_chatView.isShown()) {
                            sv_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                        }
                        if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                            if (svSub_chatView != null && svSub_chatView.isShown()) {
                                svSub_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                            }
                        } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                                (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                            if (svSub_chatView != null && svSub_chatView.isShown()) {
                                svSub_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                            }
                        }
                        if (chat_curr_rate == 2) {
                            chat_tv_age_limit_msg_10.setVisibility(VISIBLE);
                            chat_tv_age_limit_msg_12.setVisibility(View.INVISIBLE);
                            chat_tv_age_limit_msg_14.setVisibility(View.INVISIBLE);
                            chat_tv_age_limit_msg_16.setVisibility(View.INVISIBLE);
                            chat_tv_age_limit_msg_18.setVisibility(View.INVISIBLE);
                        } else if (chat_curr_rate == 3) {
                            chat_tv_age_limit_msg_10.setVisibility(View.INVISIBLE);
                            chat_tv_age_limit_msg_12.setVisibility(VISIBLE);
                            chat_tv_age_limit_msg_14.setVisibility(View.INVISIBLE);
                            chat_tv_age_limit_msg_16.setVisibility(View.INVISIBLE);
                            chat_tv_age_limit_msg_18.setVisibility(View.INVISIBLE);
                        } else if (chat_curr_rate == 4) {
                            chat_tv_age_limit_msg_10.setVisibility(View.INVISIBLE);
                            chat_tv_age_limit_msg_12.setVisibility(View.INVISIBLE);
                            chat_tv_age_limit_msg_14.setVisibility(VISIBLE);
                            chat_tv_age_limit_msg_16.setVisibility(View.INVISIBLE);
                            chat_tv_age_limit_msg_18.setVisibility(View.INVISIBLE);
                        } else if (chat_curr_rate == 5) {
                            chat_tv_age_limit_msg_10.setVisibility(View.INVISIBLE);
                            chat_tv_age_limit_msg_12.setVisibility(View.INVISIBLE);
                            chat_tv_age_limit_msg_14.setVisibility(View.INVISIBLE);
                            chat_tv_age_limit_msg_16.setVisibility(VISIBLE);
                            chat_tv_age_limit_msg_18.setVisibility(View.INVISIBLE);
                        } else if (chat_curr_rate == 6) {
                            chat_tv_age_limit_msg_10.setVisibility(View.INVISIBLE);
                            chat_tv_age_limit_msg_12.setVisibility(View.INVISIBLE);
                            chat_tv_age_limit_msg_14.setVisibility(View.INVISIBLE);
                            chat_tv_age_limit_msg_16.setVisibility(View.INVISIBLE);
                            chat_tv_age_limit_msg_18.setVisibility(VISIBLE);
                        } else {
                            chat_tv_age_limit_msg_10.setVisibility(View.INVISIBLE);
                            chat_tv_age_limit_msg_12.setVisibility(View.INVISIBLE);
                            chat_tv_age_limit_msg_14.setVisibility(View.INVISIBLE);
                            chat_tv_age_limit_msg_16.setVisibility(View.INVISIBLE);
                            chat_tv_age_limit_msg_18.setVisibility(View.INVISIBLE);
                        }
                        chat_ll_age_limit.setVisibility(VISIBLE);
                        chat_noChannel.setVisibility(View.INVISIBLE);
                        chat_ll_noSignal.setVisibility(View.INVISIBLE);
                        chat_ll_scramble_msg.setVisibility(View.INVISIBLE);
                        chat_changeChannelView.setVisibility(View.INVISIBLE);
                        TVlog.i("chat_justin", " ====> screenbl_enabled stop !!!");
                        FCI_TVi.setVolume(0.0f);
                    } else {
                        if (sv_chatView != null && sv_chatView.isShown()) {
                            sv_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
                        }
                        if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                            if (svSub_chatView != null && svSub_chatView.isShown()) {
                                svSub_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
                            }
                        } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                                (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                            if (svSub_chatView != null && svSub_chatView.isShown()) {
                                svSub_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
                            }
                        }
                        chat_ll_age_limit.setVisibility(View.INVISIBLE);
                        if (CommonStaticData.captionSwitch) {
                            if (subTitleView_chat != null) {
                                subTitleView_chat.setVisibility(View.VISIBLE);
                            }
                        }
                        FCI_TVi.setVolume(1.0f);
                    }
                    break;

                case E_CONFIRMED_PASSWORD_CHAT:
                {
                    CommonStaticData.passwordVerifyFlag = true;
                    CommonStaticData.ageLimitFlag = false;
                    //TVBridge.serviceID_start(MainActivity.getInstance().mChannelIndex);
                    sendEvent(TVEVENT.E_RATING_MONITOR_CHAT);
                    if (sv_chatView != null && sv_chatView.isShown()) {
                        sv_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
                    }
                    if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                        if (svSub_chatView != null && svSub_chatView.isShown()) {
                            svSub_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
                        }
                    } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                            (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                    buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                    buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                        if (svSub_chatView != null && svSub_chatView.isShown()) {
                            svSub_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
                        }
                    }
                }
                break;

                /*case E_HIDE_TITLE :
                    hideTitle();
                    break;
                case E_SHOW_TITLE :
                    // mIsTouchFlag =true;
                    showTitle();
                    break;*/

                case E_AUTO_CHANGE_CHANNEL_TEST_CHAT:
                    TVlog.i(TAG, " >>>>> E_AUTO_CHANGE_CHANNEL_TEST_CHAT");
                {

                    if (buildOption.LOG_CAPTURE_MODE ==3)
                    {
                        int currentChannel = TVBridge.getCurrentChannel();

                        TVlog.i(TAG, " E_AUTO_CHANGE_CHANNEL_TEST  currentID = " + currentChannel + " ChannelCount = "+CommonStaticData.scanCHnum );
                        if (currentChannel < (CommonStaticData.scanCHnum -1)) {
                            CommonStaticData.passwordVerifyFlag = false;
                            //CommonStaticData.screenBlockFlag = false;
                            CommonStaticData.ageLimitFlag = false;
                            //sendEvent(TVEVENT.E_CAPTION_CLEAR_NOTIFY);
                            //sendEvent(TVEVENT.E_SUPERIMPOSE_CLEAR_NOTIFY);

                            channelChangeStartView(false);  // live remove
                            //chat_changeChannelView.setVisibility(VISIBLE);
                            TVBridge.AVStartPlus();

                            postEvent(TVEVENT.E_AUTO_CHANGE_CHANNEL_TEST_CHAT,20*1000);
                        } else
                        {
                            TVlog.i(TAG, " E_AUTO_CHANGE_CHANNEL_TEST  END ~~~~~~~~~~~");
                            MainActivity.getInstance().TVTerminate();
                        }
                    }

                }

                case E_CHANNEL_NAME_UPDATE_CHAT:
                {
                    mCursor_chat = MainActivity.getCursor();
                    if (mCursor_chat != null) {
                        MainActivity.getInstance().mChannelIndex=CommonStaticData.lastCH;
                        mCursor_chat.moveToPosition(MainActivity.getInstance().mChannelIndex);
                        if (CommonStaticData.scanCHnum > 0) {
                            //live add
                            chat_noChannel.setVisibility(View.INVISIBLE);
                            if (sv_chatView != null && sv_chatView.isShown()) {
                                sv_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
                            }
                            if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                                if (svSub_chatView != null && svSub_chatView.isShown()) {
                                    svSub_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
                                }
                            } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                                    (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                            buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                            buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                                if (svSub_chatView != null && svSub_chatView.isShown()) {
                                    svSub_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
                                }
                            }
                            //

                            int freq = Integer.parseInt(mCursor_chat.getString(CommonStaticData.COLUMN_INDEX_SERVICE_FREQ));
                            TVlog.i(TAG, " >>>>> current freq = " + freq);

                            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                                int channelNo = 13 + (int)((freq-473143)/6000);
                                chat_currChNo.setText(channelNo+"ch");
                            } else {
                                if (buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_ONESEG
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                                    // for Sri Lanka
                                    int channelNo = 13 + (int)((freq-474000)/8000);
                                    chat_currChNo.setText(channelNo+"ch");
                                } else {
                                    int channelNo = 14 + (int)((freq-473143)/6000);
                                    chat_currChNo.setText(channelNo+"ch");
                                }
                            }

                            String channelName = mCursor_chat.getString(CommonStaticData.COLUMN_INDEX_SERVICE_NAME);
                            String[] split_channelName = channelName.split(" ");

                            // live modify 20170104
                            chat_currRemoteNo.setText(split_channelName[0]);
                            String str = "";
                            for (int i = 1; i < split_channelName.length; i++) {
                                str += split_channelName[i];
                                if (i < split_channelName.length - 1) {
                                    str += " ";
                                }
                            }
                            chat_currCH.setText(str);
                            //

                            int type = (int) mCursor_chat.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV);
                            int free = (int) mCursor_chat.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_FREE);
                            if (type == 0) { // if 1seg
                                if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                                    iv_ChType_chat.setBackgroundResource(R.drawable.jp_1seg);
                                    iv_ChFree_chat.setVisibility(View.GONE);
                                } else {
                                    iv_ChType_chat.setBackgroundResource(R.drawable.tv_icon_1seg);
                                    if (free == 0) {
                                        iv_ChFree_chat.setVisibility(View.VISIBLE);
                                    } else {
                                        iv_ChFree_chat.setVisibility(View.GONE);
                                    }
                                }
                            } else if (type == 1) { // if fullseg
                                if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                                    iv_ChType_chat.setBackgroundResource(R.drawable.jp_fullseg);
                                    iv_ChFree_chat.setVisibility(View.GONE);
                                } else {
                                    iv_ChType_chat.setBackgroundResource(R.drawable.tv_icon_fullseg);
                                    if (free == 0) {
                                        iv_ChFree_chat.setVisibility(View.VISIBLE);
                                    } else {
                                        iv_ChFree_chat.setVisibility(View.GONE);
                                    }
                                }
                            }
                            rl_ChType_chat.setVisibility(View.VISIBLE);

                            MainActivity.getInstance().sendEvent(TVEVENT.E_UPDATE_EPG_NAME_AND_DURATION);

                            //chat_currCH.setText(mCursor_chat.getString(CommonStaticData.COLUMN_INDEX_SERVICE_NAME));
                            AudioFormat = mCursor_chat.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_AUDFORM);
                            VideoFormat = mCursor_chat.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_VIDFORM);
                            Scrambled = mCursor_chat.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_FREE);
                            mRemoteKey = mCursor_chat.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_REMOTE_KEY);
                            mSvcNumber = mCursor_chat.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_NUMBER);

                            TVlog.i (TAG, " >>>>> Scrambled = "+String.valueOf(Scrambled));

                            if (Scrambled == 0) {
                                sendEvent(TVEVENT.E_BADSIGNAL_CHECK_CHAT, 2, 0, null);
                            }
                        }
                    }

                }
                break;

                case E_CHAT_SURFACE_CREATED:
                {

                    TVlog.i(TAG, " E_CHAT_SURFACE_CREATED");
                    Surface subSurface = ChatSubSurface.getChatSubSurface().getChatSurface();
                    if (subSurface !=null) {
                        setDualSurface(subSurface);

                    } else {
                        TVlog.i(TAG, " Retry Create Surface  later Start TV");
                        postEvent(TVEVENT.E_CHAT_SURFACE_CREATED, 100);
                    }
                }
                break;

                case E_HIDE_CHAT_CONTROLER :
                    hideChatController();
                    break;

                case E_SHOW_CHAT_CONTROLER :
                    // mIsTouchFlag =true;
                    showChatController();
                    break;
            }
            super.handleMessage(msg);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TVlog.i(TAG, " ===== ChatWindow onCreate() =====");
        customToast = new CustomToast(getApplicationContext());

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        ChatMainActivity.isChat = true;
        MainActivity.isMainActivity = false;

        // live add
        mNetworkReceiver_Chat = new NetworkChangeReceiver();
        IntentFilter network_filter = new IntentFilter();
        network_filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkReceiver_Chat, network_filter);


        mSysReceiver_Chat = new SysBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        //filter.addAction(Intent.ACTION_HEADSET_PLUG);

        registerReceiver(mSysReceiver_Chat, filter);

        //live add
        mScreenOff_Chat = new SysBroadcastReceiver();
        IntentFilter off_filter = new IntentFilter();
        off_filter.addAction(Intent.ACTION_SCREEN_OFF);
        // [[ eddy 160706
        off_filter.addAction(Intent.ACTION_USER_PRESENT);
        // ]] eddy 160706
        //off_filter.setPriority(999);
        registerReceiver(mScreenOff_Chat, off_filter);

        //live add
        mScreenOn_Chat = new SysBroadcastReceiver();
        IntentFilter on_filter = new IntentFilter();
        on_filter.addAction(Intent.ACTION_SCREEN_ON);
        // [[ eddy 160706
        on_filter.addAction(Intent.ACTION_USER_PRESENT);
        // ]] eddy 160706
        //on_filter.setPriority(999);
        registerReceiver(mScreenOn_Chat, on_filter);

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

        setContentView(R.layout.activity_chat_main);
        /*LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        chat_view = inflater.inflate(R.layout.activity_chat_main, null);*/

        mHandler_chat = new Handler();

        removeStatusBar(false);

        ImageView signalImage_chat = (ImageView) findViewById(R.id.chat_dtv_signal_chat);
        signalMoniter = new SignalMonitor(signalImage_chat);

        sv_chatView = (SurfaceView) findViewById(R.id.sv_chat);
        if (sv_chatView != null && sv_chatView.isShown()) {
            sv_chatView.setBackgroundColor(getResources().getColor(R.color.black));
        }

        if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
            svSub_chatView =(SurfaceView) findViewById(R.id.svSub_chat);
            if (svSub_chatView != null && svSub_chatView.isShown()) {
                svSub_chatView.setBackgroundColor(getResources().getColor(R.color.black));
            }
        } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {

            svSub_chatView =(SurfaceView) findViewById(R.id.svSub_chat);
            if (svSub_chatView != null && svSub_chatView.isShown()) {
                svSub_chatView.setBackgroundColor(getResources().getColor(R.color.black));
            }
        }

        instance = ChatMainActivity.this;
        chatVideoSurfaceHolder = sv_chatView.getHolder();

        if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
            if (svSub_chatView != null) {
                chatVideoSurfaceHolderSub = svSub_chatView.getHolder();
            }
        } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
            if (svSub_chatView != null) {
                chatVideoSurfaceHolderSub = svSub_chatView.getHolder();
            }
        }

        int h = sv_chatView.getMeasuredHeight();
        int w = sv_chatView.getMeasuredWidth();

        chatVideoSurfaceHolder.setFixedSize(w, h);
        chatVideoSurfaceHolder.addCallback(this);
        if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
            if (svSub_chatView != null) {
                chatVideoSurfaceHolderSub.setFixedSize(w, h);
                chatVideoSurfaceHolderSub.addCallback(SubSurfaceSet.getSubSurfaceSet());
            }
        } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
            if (svSub_chatView != null) {
                chatVideoSurfaceHolderSub.setFixedSize(w, h);
                chatVideoSurfaceHolderSub.addCallback(ChatSubSurface.getChatSubSurface());
            }
        }

        chat_ll_audioOnlyChannel = (LinearLayout) findViewById(R.id.chat_ll_audioOnlyChannel);
        if (chat_ll_audioOnlyChannel != null) {
            chat_ll_audioOnlyChannel.setVisibility(View.INVISIBLE);
        }

        chat_ll_black = (LinearLayout) findViewById(R.id.chat_ll_black);
        if (CommonStaticData.isAudioChannel == true) {
            if (chat_ll_black != null) {
                chat_ll_black.setVisibility(View.VISIBLE);
            }
        } else {
            if (chat_ll_black != null) {
                chat_ll_black.setVisibility(View.INVISIBLE);
            }
        }

        ll_chatAutoSearch = (LinearLayout) findViewById(R.id.ll_chatAutoSearch);
        ll_chatAutoSearch.setVisibility(View.INVISIBLE);

        progressing_autoSearch_chat = (ProgressBar) findViewById(R.id.progressing_autoSearch_chat);
        progressing_autoSearch_chat.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);

        btn_return_chat = (Button) findViewById(R.id.btn_return_chat);
        if (btn_return_chat != null) {
            btn_return_chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideChatController();
                    if (SystemClock.elapsedRealtime() - mLastClickTimeReturn < DOUBLE_CLICK_TOLERANCE){
                        return;
                    }
                    mLastClickTimeReturn = SystemClock.elapsedRealtime();
                    ll_chatAutoSearch.setVisibility(View.INVISIBLE);
                    sendEvent(TVEVENT.E_SCAN_CANCEL_CHAT);

                }
            });
        }

        chat_ll_noSignal = (LinearLayout) findViewById(R.id.chat_ll_noSignal);
        //chat_ll_noSignal.setVisibility(View.INVISIBLE);

        chat_noChannel = (TextView) findViewById(R.id.chat_noChannel);
        if (MainActivity.isNoChannel) {
            chat_noChannel.setVisibility(VISIBLE);
        }

        chat_ll_age_limit = (LinearLayout) findViewById(R.id.chat_ll_age_limit);
        chat_ll_age_limit.setVisibility(View.INVISIBLE);

        chat_tv_age_limit_msg_10 = (TextView) findViewById(R.id.chat_tv_age_limit_msg_10);
        chat_tv_age_limit_msg_10.setVisibility(View.INVISIBLE);
        chat_tv_age_limit_msg_12 = (TextView) findViewById(R.id.chat_tv_age_limit_msg_12);
        chat_tv_age_limit_msg_12.setVisibility(View.INVISIBLE);
        chat_tv_age_limit_msg_14 = (TextView) findViewById(R.id.chat_tv_age_limit_msg_14);
        chat_tv_age_limit_msg_14.setVisibility(View.INVISIBLE);
        chat_tv_age_limit_msg_16 = (TextView) findViewById(R.id.chat_tv_age_limit_msg_16);
        chat_tv_age_limit_msg_16.setVisibility(View.INVISIBLE);
        chat_tv_age_limit_msg_18 = (TextView) findViewById(R.id.chat_tv_age_limit_msg_18);
        chat_tv_age_limit_msg_18.setVisibility(View.INVISIBLE);

        Button button_enter_password = (Button) findViewById(R.id.chat_button_enter_password);
        if (button_enter_password != null) {
            button_enter_password.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String saved  = CommonStaticData.settings.getString(CommonStaticData.passwordKey, null);
                    TVlog.i(TAG, " DIALOG_PASSWORD  String = " + saved);
                    if (saved == null || saved.length() == 0) {
                        InputDialog dig = new InputDialog(ChatMainActivity.this, InputDialog.TYPE_NEW_PASSWORD, null, null, null);
                    } else {
                        InputDialog dig = new InputDialog(ChatMainActivity.this, InputDialog.TYPE_ENTER_PASSWORD, null, null, null);
                    }
                }
            });
        }

        chat_ll_scramble_msg = (LinearLayout) findViewById(R.id.chat_ll_scramble_msg);
        chat_tv_scramble_title = (TextView) findViewById(R.id.chat_tv_scramble_title);
        //chat_tv_scramble_title.setVisibility(View.INVISIBLE);
        chat_tv_scramble_msg = (TextView) findViewById(R.id.chat_tv_scramble_msg);
        //chat_tv_scramble_msg.setVisibility(View.INVISIBLE);

        chat_channel_search = (TextView) findViewById(R.id.chat_channel_search);
        chat_scan_found = (TextView) findViewById(R.id.chat_scan_found);
        chat_loadingChannel = (TextView) findViewById(R.id.chat_loading_channel);
        tv_autoSearch_title_chat = (TextView) findViewById(R.id.tv_autoSearch_title_chat);
        tv_autoSearch_msg_chat = (TextView) findViewById(R.id.tv_autoSearch_msg_chat);

        TVlog.i(TAG, " >>>>> CommonStaticData.badSignaltFlag= "+CommonStaticData.badSignalFlag+
                ", CommonStaticData.encryptFlag = "+CommonStaticData.encryptFlag);

        // live add
        if (CommonStaticData.badSignalFlag == true) {
            if (sv_chatView != null && sv_chatView.isShown()) {
                sv_chatView.setBackgroundColor(getResources().getColor(R.color.black));
            }
            if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                if (svSub_chatView != null && svSub_chatView.isShown()) {
                    svSub_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                }
            } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                    (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                            buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                            buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                if (svSub_chatView != null && svSub_chatView.isShown()) {
                    svSub_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                }
            }
            if (chat_ll_noSignal.getVisibility() == View.INVISIBLE) {
                chat_ll_noSignal.setVisibility(VISIBLE);
            }
        } else if (CommonStaticData.badSignalFlag == false) {
            if (sv_chatView != null && sv_chatView.isShown()) {
                sv_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
            }
            if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                if (svSub_chatView != null && svSub_chatView.isShown()) {
                    svSub_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
            } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                    (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                            buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                            buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                if (svSub_chatView != null && svSub_chatView.isShown()) {
                    svSub_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
            }
            if (chat_ll_noSignal.getVisibility() == VISIBLE) {
                chat_ll_noSignal.setVisibility(View.INVISIBLE);
            }

        }

        if (CommonStaticData.encryptFlag == true) {
            if (sv_chatView != null && sv_chatView.isShown()) {
                sv_chatView.setBackgroundColor(getResources().getColor(R.color.black));
            }
            if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                if (svSub_chatView != null && svSub_chatView.isShown()) {
                    svSub_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                }
            } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                    (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                            buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                            buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                if (svSub_chatView != null && svSub_chatView.isShown()) {
                    svSub_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                }
            }
            if (chat_tv_scramble_msg.getVisibility() == View.INVISIBLE) {
                chat_tv_scramble_title.setVisibility(VISIBLE);
                chat_tv_scramble_msg.setVisibility(VISIBLE);
            }

        } else if (CommonStaticData.encryptFlag == false) {
            if (sv_chatView != null && sv_chatView.isShown()) {
                sv_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
            }
            if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                if (svSub_chatView != null) {
                    svSub_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
            } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                    (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                            buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                            buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                if (svSub_chatView != null && svSub_chatView.isShown()) {
                    svSub_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
            }
            if (chat_tv_scramble_msg.getVisibility() == VISIBLE) {
                chat_tv_scramble_title.setVisibility(View.INVISIBLE);
                chat_tv_scramble_msg.setVisibility(View.INVISIBLE);
            }
        }

        if (buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL
                || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_ONESEG
                || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_FILE) {
            if (CommonStaticData.ageLimitFlag == true) {
                if (sv_chatView != null && sv_chatView.isShown()) {
                    sv_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                }
                if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                    if (svSub_chatView != null && svSub_chatView.isShown()) {
                        svSub_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                    }
                } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                        (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                    if (svSub_chatView != null && svSub_chatView.isShown()) {
                        svSub_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                    }
                }
                if (chat_curr_rate == 2) {
                    chat_tv_age_limit_msg_10.setVisibility(VISIBLE);
                    chat_tv_age_limit_msg_12.setVisibility(View.INVISIBLE);
                    chat_tv_age_limit_msg_14.setVisibility(View.INVISIBLE);
                    chat_tv_age_limit_msg_16.setVisibility(View.INVISIBLE);
                    chat_tv_age_limit_msg_18.setVisibility(View.INVISIBLE);
                } else if (chat_curr_rate == 3) {
                    chat_tv_age_limit_msg_10.setVisibility(View.INVISIBLE);
                    chat_tv_age_limit_msg_12.setVisibility(VISIBLE);
                    chat_tv_age_limit_msg_14.setVisibility(View.INVISIBLE);
                    chat_tv_age_limit_msg_16.setVisibility(View.INVISIBLE);
                    chat_tv_age_limit_msg_18.setVisibility(View.INVISIBLE);
                } else if (chat_curr_rate == 4) {
                    chat_tv_age_limit_msg_10.setVisibility(View.INVISIBLE);
                    chat_tv_age_limit_msg_12.setVisibility(View.INVISIBLE);
                    chat_tv_age_limit_msg_14.setVisibility(VISIBLE);
                    chat_tv_age_limit_msg_16.setVisibility(View.INVISIBLE);
                    chat_tv_age_limit_msg_18.setVisibility(View.INVISIBLE);
                } else if (chat_curr_rate == 5) {
                    chat_tv_age_limit_msg_10.setVisibility(View.INVISIBLE);
                    chat_tv_age_limit_msg_12.setVisibility(View.INVISIBLE);
                    chat_tv_age_limit_msg_14.setVisibility(View.INVISIBLE);
                    chat_tv_age_limit_msg_16.setVisibility(VISIBLE);
                    chat_tv_age_limit_msg_18.setVisibility(View.INVISIBLE);
                } else if (chat_curr_rate == 6) {
                    chat_tv_age_limit_msg_10.setVisibility(View.INVISIBLE);
                    chat_tv_age_limit_msg_12.setVisibility(View.INVISIBLE);
                    chat_tv_age_limit_msg_14.setVisibility(View.INVISIBLE);
                    chat_tv_age_limit_msg_16.setVisibility(View.INVISIBLE);
                    chat_tv_age_limit_msg_18.setVisibility(VISIBLE);
                } else {
                    chat_tv_age_limit_msg_10.setVisibility(View.INVISIBLE);
                    chat_tv_age_limit_msg_12.setVisibility(View.INVISIBLE);
                    chat_tv_age_limit_msg_14.setVisibility(View.INVISIBLE);
                    chat_tv_age_limit_msg_16.setVisibility(View.INVISIBLE);
                    chat_tv_age_limit_msg_18.setVisibility(View.INVISIBLE);
                }
                chat_ll_age_limit.setVisibility(VISIBLE);
            } else if (CommonStaticData.ageLimitFlag == false) {
                if (sv_chatView != null && sv_chatView.isShown()) {
                    sv_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
                if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                    if (svSub_chatView != null && svSub_chatView.isShown()) {
                        svSub_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
                    }
                } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                        (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                    if (svSub_chatView != null && svSub_chatView.isShown()) {
                        svSub_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
                    }
                }
                chat_ll_age_limit.setVisibility(View.INVISIBLE);
            }
        }

        if (CommonStaticData.badSignalFlag || CommonStaticData.encryptFlag || CommonStaticData.ageLimitFlag) {
            if (sv_chatView != null && sv_chatView.isShown()) {
                sv_chatView.setBackgroundColor(getResources().getColor(R.color.black));
            }
            if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                if (svSub_chatView != null && svSub_chatView.isShown()) {
                    svSub_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                }
            } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                    (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                            buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                            buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                if (svSub_chatView != null && svSub_chatView.isShown()) {
                    svSub_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                }
            }
        } else {
            if (sv_chatView != null && sv_chatView.isShown()) {
                sv_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
            }
            if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                if (svSub_chatView != null && svSub_chatView.isShown()) {
                    svSub_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
            } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                    (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                            buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                            buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                if (svSub_chatView != null && svSub_chatView.isShown()) {
                    svSub_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
            }
        }


        chat_ll_ch_info = (LinearLayout) findViewById(R.id.chat_ll_ch_info);
        chat_channelChangeBG = (ImageView) findViewById(R.id.chat_imageView_bg);

        chat_currChNo = (TextView) findViewById(R.id.chat_tv_ch_no);
        tf_chat = MainActivity.getInstance().tf;
        chat_currChNo.setTypeface(tf_chat);
        chat_currChNo.setTextSize(18);

        if (buildOption.VIEW_PHY_CH) {
            chat_currChNo.setVisibility(VISIBLE);
        } else {
            chat_currChNo.setVisibility(GONE);
        }

        rl_ChType_chat = (RelativeLayout) findViewById(R.id.rl_ChType_chat);
        iv_ChType_chat = (ImageView) findViewById(R.id.iv_ChType_chat);
        iv_ChFree_chat = (ImageView) findViewById(R.id.iv_ChFree_chat);

        if (CommonStaticData.scanCHnum > 0) {
            if (mCursor_chat != null) {
                int type = (int) mCursor_chat.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV);
                int free = (int) mCursor_chat.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_FREE);
                if (type == 0) { // if 1seg
                    if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                        iv_ChType_chat.setBackgroundResource(R.drawable.jp_1seg);
                        iv_ChFree_chat.setVisibility(View.GONE);
                    } else {
                        iv_ChType_chat.setBackgroundResource(R.drawable.tv_icon_1seg);
                        if (free == 0) {
                            iv_ChFree_chat.setVisibility(View.VISIBLE);
                        } else {
                            iv_ChFree_chat.setVisibility(View.GONE);
                        }
                    }
                } else if (type == 1) { // if fullseg
                    if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                        iv_ChType_chat.setBackgroundResource(R.drawable.jp_fullseg);
                        iv_ChFree_chat.setVisibility(View.GONE);
                    } else {
                        iv_ChType_chat.setBackgroundResource(R.drawable.tv_icon_fullseg);
                        if (free == 0) {
                            iv_ChFree_chat.setVisibility(View.VISIBLE);
                        } else {
                            iv_ChFree_chat.setVisibility(View.GONE);
                        }
                    }
                }
                rl_ChType_chat.setVisibility(View.VISIBLE);
            }
        } else {
            rl_ChType_chat.setVisibility(View.GONE);
        }

        chat_currRemoteNo = (TextView) findViewById(R.id.chat_tv_remote_no);
        chat_currCH = (TextView) findViewById(R.id.chat_servicename);
        //if (buildOption.GUI_STYLE == 1) {
        chat_currCH.setSelected(true);
        //}

        subTitleView_chat =(TextView) findViewById(R.id.chat_subTitleView);

        superImposeView_chat = (TextView) findViewById(R.id.chat_superImposeView);
        // live add
        if (CommonStaticData.superimposeSwitch == true) {
            superImposeView_chat.setVisibility(View.VISIBLE);
        } else {
            superImposeView_chat.setVisibility(View.INVISIBLE);
        }
        //


        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
            // [[ solution switching mode 20170223
            envSet_JP_chat();

            //]]
        } else {
            envSet_Normal_chat();
        }

        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
            // use free font
            mFont_chat = MainActivity.getInstance().mFont;
            chat_currCH.setTypeface(mFont_chat);
        }

        chat_ll_scan_progress = (LinearLayout) findViewById(R.id.chat_ll_scan_progress);
        if (chat_ll_scan_progress != null) {
            chat_ll_scan_progress.setVisibility(View.INVISIBLE);
        }

        chat_scan_progressBar = (ProgressBar) findViewById(R.id.chat_scan_progressBar);
        chat_scan_progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
        chat_btn_scan_cancel = (Button) findViewById(R.id.chat_btn_scan_cancel);
        if (chat_btn_scan_cancel != null) {
            chat_btn_scan_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideChatController();
                    chat_ll_scan_progress.setVisibility(View.INVISIBLE);
                    sendEvent(TVEVENT.E_SCAN_CANCEL_CHAT);
                }
            });
        }

        chat_changeChannelView = (LinearLayout) findViewById(R.id.chat_progressBarCircularIndeterminate);
        chat_changeChannelView.setVisibility(View.INVISIBLE);

        //chat_progressingChange = (CustomView) findViewById(R.id.chat_progressing_channel);
        chat_progressingChange = (ProgressBar) findViewById(R.id.chat_progressing_channel);
        chat_progressingChange.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);

        chat_status_bar = (LinearLayout) findViewById(R.id.chat_status_bar);
        if (chat_status_bar != null) {
            chat_status_bar.setVisibility(VISIBLE);
        }

        chat_controllerLayout = (LinearLayout) findViewById(R.id.chat_controllerLayout);

        chat_iv_scan = (ImageView) findViewById(R.id.chat_iv_scan);
        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
            if (chat_iv_scan != null) {
                chat_iv_scan.setBackgroundResource(R.drawable.btn_selector);
            }
        } else {
            if (chat_iv_scan != null) {
                chat_iv_scan.setBackgroundResource(R.color.transparent);
            }
        }
        chat_iv_scan.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    chat_iv_scan.setScaleX(0.5f);
                    chat_iv_scan.setScaleY(0.5f);
                    chat_iv_scan.setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    try {
                        Thread.sleep(BUTTON_CLICK_TIME);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    chat_iv_scan.setScaleX(1.0f);
                    chat_iv_scan.setScaleY(1.0f);
                    chat_iv_scan.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
                }
                return false;
            }
        });
        chat_iv_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // live add
                if (sv_chatView != null && sv_chatView.isShown()) {
                    sv_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
                if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                    if (svSub_chatView!= null && svSub_chatView.isShown())
                    {
                        svSub_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
                    }
                } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                        (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                    if (svSub_chatView != null && sv_chatView.isShown()) {
                        sv_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
                    }
                }
                hideChatController();
                chat_noChannel.setVisibility(View.INVISIBLE);
                if (chat_ll_noSignal.getVisibility() == View.VISIBLE) {
                    chat_ll_noSignal.setVisibility(View.INVISIBLE);
                }
                if (chat_tv_scramble_msg.getVisibility() == View.VISIBLE) {
                    chat_tv_scramble_title.setVisibility(View.INVISIBLE);
                    chat_tv_scramble_msg.setVisibility(View.INVISIBLE);
                }
                sendEvent(TVEVENT.E_SCAN_START_CHAT);
            }
        });


        chat_iv_prev = (ImageView) findViewById(R.id.chat_iv_prev);
        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
            if (chat_iv_prev != null) {
                chat_iv_prev.setBackgroundResource(R.drawable.btn_selector);
            }
        }
        chat_iv_prev.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    chat_iv_prev.setScaleX(0.5f);
                    chat_iv_prev.setScaleY(0.5f);
                    chat_iv_prev.setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    try {
                        Thread.sleep(BUTTON_CLICK_TIME);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    chat_iv_prev.setScaleX(1.0f);
                    chat_iv_prev.setScaleY(1.0f);
                    chat_iv_prev.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
                }
                return false;
            }
        });
        chat_iv_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatMainActivity.isChat = true;
                MainActivity.isMainActivity = false;

                CommonStaticData.passwordVerifyFlag = false;
                CommonStaticData.ageLimitFlag = false;

                channelChangeStartView(false);
                hideChatController();
                chat_changeChannelView.setVisibility(VISIBLE);

                sendEvent(TVEVENT.E_CAPTION_CLEAR_NOTIFY_CHAT);
                sendEvent(TVEVENT.E_SUPERIMPOSE_CLEAR_NOTIFY_CHAT);
                // live add
                if (ll_chatAutoSearch.getVisibility() == VISIBLE) {
                    ll_chatAutoSearch.setVisibility(View.INVISIBLE);
                }
                if (chat_ll_scramble_msg.getVisibility() == VISIBLE) {
                    chat_ll_scramble_msg.setVisibility(View.INVISIBLE);
                }
                //
                chat_ll_age_limit.setVisibility(View.INVISIBLE);
                TVBridge.AVStartMinus();

                initViews();
                initFirebaseDatabase();
                updateProfile();
            }
        });

        chat_iv_next = (ImageView) findViewById(R.id.chat_iv_next);
        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
            if (chat_iv_next != null) {
                chat_iv_next.setBackgroundResource(R.drawable.btn_selector);
            }
        }
        chat_iv_next.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    chat_iv_next.setScaleX(0.5f);
                    chat_iv_next.setScaleY(0.5f);
                    chat_iv_next.setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    try {
                        Thread.sleep(BUTTON_CLICK_TIME);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    chat_iv_next.setScaleX(1.0f);
                    chat_iv_next.setScaleY(1.0f);
                    chat_iv_next.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
                }
                return false;
            }
        });
        chat_iv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatMainActivity.isChat = true;
                MainActivity.isMainActivity = false;

                CommonStaticData.passwordVerifyFlag = false;
                CommonStaticData.ageLimitFlag = false;

                channelChangeStartView(false);
                hideChatController();
                chat_changeChannelView.setVisibility(VISIBLE);

                sendEvent(TVEVENT.E_CAPTION_CLEAR_NOTIFY_CHAT);
                sendEvent(TVEVENT.E_SUPERIMPOSE_CLEAR_NOTIFY_CHAT);
                // live add
                if (ll_chatAutoSearch.getVisibility() == VISIBLE) {
                    ll_chatAutoSearch.setVisibility(View.INVISIBLE);
                }
                if (chat_ll_scramble_msg.getVisibility() == VISIBLE) {
                    chat_ll_scramble_msg.setVisibility(View.INVISIBLE);
                }
                chat_ll_age_limit.setVisibility(View.INVISIBLE);
                //
                TVBridge.AVStartPlus();

                initViews();
                initFirebaseDatabase();
                updateProfile();
            }
        });

        chat_iv_max = (ImageView) findViewById(R.id.chat_iv_max);
        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
            if (chat_iv_max != null) {
                chat_iv_max.setBackgroundResource(R.drawable.btn_selector);
            }
        }
        chat_iv_max.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    chat_iv_max.setScaleX(0.5f);
                    chat_iv_max.setScaleY(0.5f);
                    chat_iv_max.setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    try {
                        Thread.sleep(BUTTON_CLICK_TIME);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    chat_iv_max.setScaleX(1.0f);
                    chat_iv_max.setScaleY(1.0f);
                    chat_iv_max.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
                }
                return false;
            }
        });
        chat_iv_max.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //usbdongle[[
                if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) { // for re-entering NXP dongle
                    /*
                    if (MainActivity.getInstance() != null) {
                        MainActivity.getInstance().TVTerminate();
                    }*/
                }
                //]]usbdongle

                AudioManager am = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                am.abandonAudioFocus(AudioOut.afChangeListener);

                MainActivity.isMainActivity = true;
                ChatMainActivity.isChat = false;

                CommonStaticData.returnMainFromChat = true;
                TVlog.i(TAG, " >>>>> CommonStaticData.returnMainFromChat = "+CommonStaticData.returnMainFromChat);

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
                editor.putBoolean(CommonStaticData.returnMainFromChatKey, CommonStaticData.returnMainFromChat);
                //
                editor.commit();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        /*
        arr_svcmodeswitch_jp = getResources().getStringArray(R.array.svcmode_switch_jp);
        chat_btn_receiveMode = (Button) findViewById(R.id.chat_btn_receiveMode);
        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
            if (chat_btn_receiveMode != null) {
                chat_btn_receiveMode.setBackgroundResource(R.drawable.btn_selector);
                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                int receiveMode = CommonStaticData.settings.getInt(CommonStaticData.receivemodeSwitchKey, CommonStaticData.RECEIVE_MODE_AUTO);  // auto
                TVlog.i("live", " >>> receiveMode = "+receiveMode);
                chat_btn_receiveMode.setText(arr_svcmodeswitch_jp[receiveMode]);
                chat_btn_receiveMode.setVisibility(View.VISIBLE);
                chat_btn_receiveMode.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            chat_btn_receiveMode.setScaleX(0.8f);
                            chat_btn_receiveMode.setScaleY(0.8f);
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
                            chat_btn_receiveMode.setScaleX(1.0f);
                            chat_btn_receiveMode.setScaleY(1.0f);
                        }
                        return false;
                    }
                });
                chat_btn_receiveMode.setOnClickListener(new View.OnClickListener() {
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
                                    chat_btn_receiveMode.setText(arr_svcmodeswitch_jp[0]);
                                    editor.putInt(CommonStaticData.receivemodeSwitchKey, CommonStaticData.receivemode);
                                    editor.commit();
                                } else {
                                    CommonStaticData.receivemode = CommonStaticData.RECEIVE_MODE_AUTO;  // fullseg --> auto
                                    chat_btn_receiveMode.setText(arr_svcmodeswitch_jp[2]);
                                    editor.putInt(CommonStaticData.receivemodeSwitchKey, CommonStaticData.receivemode);
                                    editor.commit();
                                    customToast.showToast(getApplicationContext(), getApplicationContext().getString(R.string.switch_fail_SD), Toast.LENGTH_SHORT);
                                }
                            } else if (CommonStaticData.receivemode == CommonStaticData.RECEIVE_MODE_1SEG) {  //1seg --> auto
                                CommonStaticData.receivemode = CommonStaticData.RECEIVE_MODE_AUTO;
                                chat_btn_receiveMode.setText(arr_svcmodeswitch_jp[2]);
                                editor.putInt(CommonStaticData.receivemodeSwitchKey, CommonStaticData.receivemode);
                                editor.commit();
                            } else if (CommonStaticData.receivemode == CommonStaticData.RECEIVE_MODE_AUTO) {   //auto --> off
                                CommonStaticData.receivemode = CommonStaticData.RECEIVE_MODE_OFF;
                                chat_btn_receiveMode.setText(arr_svcmodeswitch_jp[3]);
                                editor.putInt(CommonStaticData.receivemodeSwitchKey, CommonStaticData.receivemode);
                                editor.commit();
                            } else if (CommonStaticData.receivemode == CommonStaticData.RECEIVE_MODE_OFF) {  //off --> fullseg
                                if (mainIndex != -1) {
                                    CommonStaticData.receivemode = CommonStaticData.RECEIVE_MODE_FULLSEG;
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_SWITCHING, 1, 0, null);
                                    chat_btn_receiveMode.setText(arr_svcmodeswitch_jp[1]);
                                    editor.putInt(CommonStaticData.receivemodeSwitchKey, CommonStaticData.receivemode);
                                    editor.commit();
                                } else {
                                    CommonStaticData.receivemode = CommonStaticData.RECEIVE_MODE_1SEG;  // off --> 1seg
                                    chat_btn_receiveMode.setText(arr_svcmodeswitch_jp[0]);
                                    editor.putInt(CommonStaticData.receivemodeSwitchKey, CommonStaticData.receivemode);
                                    editor.commit();
                                    customToast.showToast(getApplicationContext(), getApplicationContext().getString(R.string.switch_fail_HD), Toast.LENGTH_SHORT);
                                }

                            }
                        } else {
                            CustomToast toast = new CustomToast(getApplicationContext());
                            toast.showToast(getApplicationContext(), getApplicationContext().getString(R.string.no_channel_tip), Toast.LENGTH_SHORT);
                        }
                    }
                });
            }
        } else {
            chat_btn_receiveMode.setVisibility(View.GONE);
        }*/

        postEvent(TVEVENT.E_HIDE_CHAT_CONTROLER, CONTROLLER_HIDE_TIME);

        chat_room_title = (TextView) findViewById(R.id.chat_room_title);

        if (CommonStaticData.scanCHnum > 0) {
            if (mCursor_chat != null) {
                String channelName = mCursor_chat.getString(CommonStaticData.COLUMN_INDEX_SERVICE_NAME);
                String[] split_channelName = channelName.split(" ");
                String str = "";
                for (int i = 1; i < split_channelName.length; i++) {
                    str += split_channelName[i];
                    if (i < split_channelName.length - 1) {
                        str += " ";
                    }
                }
                chat_room_title.setText(getApplicationContext().getString(R.string.chat_room_name) + " (" + getApplicationContext().getResources().getString(R.string.app_name) + ") " + str);
            }
        } else {
            chat_room_title.setText(getApplicationContext().getString(R.string.chat_room_name) + " (" + getApplicationContext().getResources().getString(R.string.app_name) + ") ");
        }

        initViews();
        if (CommonStaticData.scanCHnum > 0) {
            initFirebaseDatabase();
        }
        initFirebaseAuth();
        initValues();

        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
            postEvent(TVEVENT.E_SIGNAL_MONITER_CHAT, SIGNAL_MONITER_TIME_USB);
        } else {
            postEvent(TVEVENT.E_SIGNAL_MONITER_CHAT, SIGNAL_MONITER_TIME);
        }

        sendEvent(TVEVENT.E_CHANNEL_NAME_UPDATE_CHAT);
        sendEvent(TVEVENT.E_BADSIGNAL_CHECK_CHAT);

        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
            // [[ solution switching mode 20170223
            envSet_JP_chat();
            //]]
        } else {
            envSet_Normal_chat();
        }
    }

    private void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId) {
        iconToBeChanged.setImageResource(drawableResourceId);
    }

    private void initViews() {
        mNetworkMsg = (TextView) findViewById(R.id.network_message);
        mCahtListView = (ListView) findViewById(R.id.list_message);
        mChatAdapter = new ChatAdapter(this, 0);
        mCahtListView.setAdapter(mChatAdapter);
        if (isOnline()) {
            mCahtListView.setVisibility(VISIBLE);
            mNetworkMsg.setVisibility(View.GONE);
            /*mChatAdapter = new ChatAdapter(this, 0);
            mCahtListView.setAdapter(mChatAdapter);*/
        } else {
            mCahtListView.setVisibility(View.GONE);
            mNetworkMsg.setVisibility(VISIBLE);
        }

        mBtnGoogleSignIn = (SignInButton) findViewById(R.id.btn_google_signin);
        mBtnGoogleSignOut = (Button) findViewById(R.id.btn_google_signout);
        mBtnGoogleSignIn.setOnClickListener(this);
        mBtnGoogleSignOut.setOnClickListener(this);

        mTxtProfileInfo = (TextView) findViewById(R.id.txt_profile_info);
        mImgProfile = (ImageView) findViewById(R.id.img_profile);

        emojiconEditText = (EmojiconEditText) findViewById(R.id.emojicon_edit_text);
        rootView = findViewById(R.id.root_view);
        emojiButton = (ImageView) findViewById(R.id.emoji_btn);
        submitButton = (ImageView) findViewById(R.id.submit_btn);
        txtEmojis = (TextView) findViewById(R.id.txtEmojis);
        tv_message = (TextView) findViewById(R.id.tv_message);
        if (tv_message != null) {
            tv_message.setText(R.string.chat_sign_in);
        }

        // Give the topmost view of your activity layout hierarchy. This will be used to measure soft keyboard height
        popup = new EmojiconsPopup(rootView, this);

        //Will automatically set size according to the soft keyboard size
        popup.setSizeForSoftKeyboard();

        //If the emoji popup is dismissed, change emojiButton to smiley icon
        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                changeEmojiKeyboardIcon(emojiButton, R.drawable.smiley);
            }
        });

        //If the text keyboard closes, also dismiss the emoji popup
        popup.setOnSoftKeyboardOpenCloseListener(new EmojiconsPopup.OnSoftKeyboardOpenCloseListener() {

            @Override
            public void onKeyboardOpen(int keyBoardHeight) {

            }

            @Override
            public void onKeyboardClose() {
                if (popup.isShowing())
                    popup.dismiss();
            }
        });

        //On emoji clicked, add it to edittext
        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                if (emojiconEditText == null || emojicon == null) {
                    return;
                }

                int start = emojiconEditText.getSelectionStart();
                int end = emojiconEditText.getSelectionEnd();
                if (start < 0) {
                    emojiconEditText.append(emojicon.getEmoji());
                } else {
                    emojiconEditText.getText().replace(Math.min(start, end),
                            Math.max(start, end), emojicon.getEmoji(), 0,
                            emojicon.getEmoji().length());
                }
            }
        });

        //On backspace clicked, emulate the KEYCODE_DEL key event
        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {
                KeyEvent event = new KeyEvent(
                        0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                emojiconEditText.dispatchKeyEvent(event);
            }
        });

        // To toggle between text keyboard and emoji keyboard keyboard(Popup)
        emojiButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //If popup is not showing => emoji keyboard is not visible, we need to show it
                if (!popup.isShowing()) {

                    //If keyboard is visible, simply show the emoji popup
                    if (popup.isKeyBoardOpen()) {
                        popup.showAtBottom();
                        changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_action_keyboard);
                    }

                    //else, open the text keyboard first and immediately after that show the emoji popup
                    else {
                        emojiconEditText.setFocusableInTouchMode(true);
                        emojiconEditText.requestFocus();
                        popup.showAtBottomPending();
                        final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(emojiconEditText, InputMethodManager.SHOW_IMPLICIT);
                        changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_action_keyboard);
                    }
                }

                //If popup is showing, simply dismiss it to show the undelying text keyboard
                else {
                    popup.dismiss();
                }
            }
        });

        //On submit, add the edittext text to listview and clear the edittext
        submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String newText = emojiconEditText.getText().toString();
                txtEmojis.setText(newText);
                Toast.makeText(ChatMainActivity.this, newText + "", Toast.LENGTH_SHORT).show();
                emojiconEditText.getText().clear();


            }
        });

        //mEdtMessage = (EditText) findViewById(edit_message);
        findViewById(R.id.submit_btn).setOnClickListener(this);

    }

    private void initFirebaseDatabase() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        //mDatabaseReference = mFirebaseDatabase.getReference("message");
        //if (mCursor_chat != null) {
        if (CommonStaticData.scanCHnum > 0) {
            if (mCursor_chat != null) {
                String channelName = mCursor_chat.getString(CommonStaticData.COLUMN_INDEX_SERVICE_NAME);
                String[] split_channelName = channelName.split(" ");
                String str = "";
                for (int i = 1; i < split_channelName.length; i++) {
                    str += split_channelName[i];
                    if (i < split_channelName.length - 1) {
                        str += " ";
                    }
                }
                mDatabaseReference = mFirebaseDatabase.getReference("(" + getApplicationContext().getResources().getString(R.string.app_name) + ") " + str);
                //chat_room_title.setText(getApplicationContext().getString(R.string.chat_room_name) + " (" + getApplicationContext().getResources().getString(R.string.app_name) + ") " + str);
                mChildEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        ChatData chatData = dataSnapshot.getValue(ChatData.class);
                        chatData.firebaseKey = dataSnapshot.getKey();
                        mChatAdapter.add(chatData);
                        mCahtListView.smoothScrollToPosition(mChatAdapter.getCount());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        String firebaseKey = dataSnapshot.getKey();
                        int count = mChatAdapter.getCount();
                        for (int i = 0; i < count; i++) {
                            if (mChatAdapter.getItem(i).firebaseKey.equals(firebaseKey)) {
                                mChatAdapter.remove(mChatAdapter.getItem(i));
                                break;
                            }
                        }
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                };
                mDatabaseReference.addChildEventListener(mChildEventListener);
            }

        } else {
            mDatabaseReference = mFirebaseDatabase.getReference("(" + getApplicationContext().getResources().getString(R.string.app_name) + ") ");
            //chat_room_title.setText(getApplicationContext().getString(R.string.chat_room_name) + " (" + getApplicationContext().getResources().getString(R.string.app_name) + ") " + str);
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    ChatData chatData = dataSnapshot.getValue(ChatData.class);
                    chatData.firebaseKey = dataSnapshot.getKey();
                    mChatAdapter.add(chatData);
                    mCahtListView.smoothScrollToPosition(mChatAdapter.getCount());
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    String firebaseKey = dataSnapshot.getKey();
                    int count = mChatAdapter.getCount();
                    for (int i = 0; i < count; i++) {
                        if (mChatAdapter.getItem(i).firebaseKey.equals(firebaseKey)) {
                            mChatAdapter.remove(mChatAdapter.getItem(i));
                            break;
                        }
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mDatabaseReference.addChildEventListener(mChildEventListener);
        }

        //}
    }

    private void initFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                updateProfile();
            }
        };
    }

    private void initValues() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            userName = "Guest" + new Random().nextInt(5000);
        } else {
            userName = user.getDisplayName();
        }
    }

    public void updateProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            // not login state (cannot send message)
            mBtnGoogleSignIn.setVisibility(VISIBLE);
            mBtnGoogleSignOut.setVisibility(GONE);
            mTxtProfileInfo.setVisibility(GONE);
            mImgProfile.setVisibility(GONE);

            //submitButton.setVisibility(View.GONE);
            rootView.setVisibility(View.GONE);
            tv_message.setVisibility(VISIBLE);

            mChatAdapter.setEmail(null);
            mChatAdapter.notifyDataSetChanged();
        } else {
            // login state
            mBtnGoogleSignIn.setVisibility(GONE);
            mBtnGoogleSignOut.setVisibility(VISIBLE);
            mTxtProfileInfo.setVisibility(VISIBLE);
            mImgProfile.setVisibility(VISIBLE);

            //submitButton.setVisibility(View.VISIBLE);
            rootView.setVisibility(VISIBLE);
            tv_message.setVisibility(GONE);

            userName = user.getDisplayName(); // set nick name
            String email = user.getEmail();
            StringBuilder profile = new StringBuilder();
            profile.append(userName).append("\n").append(user.getEmail());
            mTxtProfileInfo.setText(profile);
            TVlog.i(TAG, " >>>>> mTxtProfileInfo = "+profile);
            mChatAdapter.setEmail(email);
            mChatAdapter.notifyDataSetChanged();
            Picasso.with(this).load(user.getPhotoUrl()).into(mImgProfile);

        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(ChatMainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                updateProfile();
            }
        }
    }

    private void signOut() {
        mAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        updateProfile();
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onResume() {

        TVlog.i(TAG, " ===== onResume() =====");

        MainActivity.isMainActivity = false;
        ChatMainActivity.isChat = true;
        CommonStaticData.isBadSignalFlag = false;

        if ((chat_curr_rate >= (CommonStaticData.PG_Rate+1) && (CommonStaticData.PG_Rate!=0))
                && (CommonStaticData.passwordVerifyFlag == false)
                && (CommonStaticData.ratingsetSwitch == true)) {
            CommonStaticData.ageLimitFlag = true;
            //CommonStaticData.screenBlockFlag = false;
            //sendEvent(TVEVENT.E_RATING_MONITOR_CHAT);
        }

        //notifyFirstVideoChat();

        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
            postEvent(TVEVENT.E_SIGNAL_MONITER_CHAT, SIGNAL_MONITER_TIME_USB);
        } else {
            postEvent(TVEVENT.E_SIGNAL_MONITER_CHAT, SIGNAL_MONITER_TIME);
        }
        sendEvent(TVEVENT.E_CHANNEL_NAME_UPDATE_CHAT);
        sendEvent(TVEVENT.E_BADSIGNAL_CHECK_CHAT);

        removeStatusBar(false);

        int player_bright = CommonStaticData.brightness;
        //WindowManager.LayoutParams lp = mWindow.getAttributes();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = player_bright;
        getWindow().setAttributes(lp);

        TVlog.i(TAG, "== onResume ==");
        if (chat_changeChannelView != null) {
            if (channelChangeProcLocation != null) {
                chat_changeChannelView.setX(channelChangeProcLocation[0]);
                chat_changeChannelView.setY(channelChangeProcLocation[1]);
            }
        }

        /*View decorView = getWindow().getDecorView();
        uiOptions =  View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE;    // justin
        decorView.setSystemUiVisibility(uiOptions);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        TVlog.i(TAG, "== setSystemUiVisibility onResume ==");

        super.onResume();

    }

    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch(state){
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

    @Override
    protected void onPause() {
        TVlog.i(TAG, "== onPause ==");

        removeEvent(TVEVENT.E_HIDE_CHAT_CONTROLER);

        chat_controllerLayout.setVisibility(View.INVISIBLE);
        chat_status_bar.setVisibility(View.INVISIBLE);

        CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = CommonStaticData.settings.edit();
        editor.putBoolean(CommonStaticData.ageLimitFlagKey, CommonStaticData.ageLimitFlag);
        editor.putInt(CommonStaticData.receivemodeSwitchKey, CommonStaticData.receivemode);
        editor.commit();

        removeStatusBar(true);
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        TVlog.i(TAG, "== onDestroy ==");

        ChatMainActivity.isChat = false;
        removeEvent(TVEVENT.E_CHAT_SURFACE_CREATED);

        /*try {
            if (mWindowManager != null) {
                if (chat_view != null) {
                    mWindowManager.removeView(chat_view);
                    mWindowManager = null;
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }*/

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
        editor.putBoolean(CommonStaticData.returnMainFromChatKey, CommonStaticData.returnMainFromChat);
        editor.putInt(CommonStaticData.receivemodeSwitchKey, CommonStaticData.receivemode);
        //
        editor.commit();

        try {

            if (mNetworkReceiver_Chat != null) {
                unregisterReceiver(mNetworkReceiver_Chat);
            }

            if (mSysReceiver_Chat != null) {
                unregisterReceiver(mSysReceiver_Chat);
            }

            if (mScreenOff_Chat != null) {
                unregisterReceiver(mScreenOff_Chat);
            }

            if (mScreenOn_Chat != null) {
                unregisterReceiver(mScreenOn_Chat);
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
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        super.onDestroy();
        if (mChildEventListener != null) {
            mDatabaseReference.removeEventListener(mChildEventListener);
        }
    }

    @Override
    public void onBackPressed() {

        //usbdongle[[
        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) { // for re-entering NXP dongle
                    /*if (MainActivity.getInstance() != null) {
                        MainActivity.getInstance().TVTerminate();
                    }*/
        }
        //]]usbdongle

        AudioManager am = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        am.abandonAudioFocus(AudioOut.afChangeListener);

        MainActivity.isMainActivity = true;
        ChatMainActivity.isChat = false;

        CommonStaticData.returnMainFromChat = true;
        TVlog.i(TAG, " >>>>> CommonStaticData.returnMainFromChat = "+CommonStaticData.returnMainFromChat);

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
        editor.putBoolean(CommonStaticData.returnMainFromChatKey, CommonStaticData.returnMainFromChat);
        editor.putInt(CommonStaticData.receivemodeSwitchKey, CommonStaticData.receivemode);
        //
        editor.commit();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_btn:
                String message = emojiconEditText.getText().toString();
                if (!TextUtils.isEmpty(message)) {
                    emojiconEditText.setText("");
                    ChatData chatData = new ChatData();
                    chatData.userName = userName;
                    chatData.message = message;
                    chatData.time = System.currentTimeMillis();
                    chatData.userEmail = mAuth.getCurrentUser().getEmail(); // user email address
                    chatData.userPhotoUrl = mAuth.getCurrentUser().getPhotoUrl().toString(); // usser profile image address
                    mDatabaseReference.push().setValue(chatData);
                }
                break;
            case R.id.btn_google_signin:
                signIn();
                break;
            case R.id.btn_google_signout:
                signOut();
                break;
        }
    }

    void setDualSurface(Surface _subSurface)
    {
        TVlog.i(TAG, " setDualSurface  ---------------------------------------");
        //   MainActivity.getInstance().onStart_TV();

        FCI_TVi.setSuface(mainSurface);
        //  FCI_TVi.setSubSurface(_subSurface);

        int mode = FCI_TVi.getDualMode();

        if (mode == FCI_TV.CHSTART_DUAL_O_SEG) {

            TVlog.i(TAG, "onStartCommand One-SEG mode ");
            svSub_chatView.setVisibility(View.VISIBLE);
        } else {
            TVlog.i(TAG, "onStartCommand Noamel mode ");
            svSub_chatView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        TVlog.i(TAG, " ===== ChatWindow surfaceCreated =====");

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


        /*MainActivity.getInstance().onStart_TV();

        if (sv_chatView != null) {
            FCI_TVi.setSuface(holder.getSurface());
        }
        if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
            if (sv_chatView != null && svSub_chatView != null) {
                FCI_TVi.setSubSurface(holder.getSurface());
            }
        } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
            if (sv_chatView != null && svSub_chatView != null) {
                FCI_TVi.setSubSurface(holder.getSurface());
            }
        }*/

        MainActivity.isMainActivity = false;
        ChatMainActivity.isChat = true;

        if (MainActivity.getInstance() != null) {
            MainActivity.getInstance().onStart_TV();
        }

        if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {

            TVlog.i(TAG, " JAPAN , MEDIA, CHAT Create Surface");

            Surface subSurface = ChatSubSurface.getChatSubSurface().getChatSurface();
            mainSurface =holder.getSurface();
            if (subSurface !=null) {
                setDualSurface(subSurface);
            } else {
                TVlog.i(TAG, " JAPAN , MEDIA, CHAT Create Surface  later Start TV");
                postEvent(TVEVENT.E_CHAT_SURFACE_CREATED, 100);
            }
        } else {
            //  MainActivity.getInstance().onStart_TV();
            if (sv_chatView!= null) {
                FCI_TVi.setSuface(holder.getSurface());
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        TVlog.i(TAG, "=== onConfigurationChanged is called !!! ===");
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            TVlog.i(TAG, "=== Configuration.ORIENTATION_PORTRAIT !!! ===");
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            TVlog.i(TAG, "=== Configuration.ORIENTATION_LANDSCAPE !!! ===");
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        TVlog.i(TAG, " ===== ChatWindow surfaceChanged =====");
        if (frameWidthChat == 0 || frameHeightChat == 0) {
            frameWidthChat = width;
            frameHeightChat = height;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        TVlog.i(TAG, " ===== ChatWindow SurfaceDestroyed!!! =====");
        removeEvent(TVEVENT.E_BADSIGNAL_CHECK_CHAT);
        removeEvent(E_SIGNAL_NOTI_MSG_CHAT);
        removeEvent(TVEVENT.E_NOSIGNAL_SHOW_CHAT);
        removeEvent(TVEVENT.E_CHANNEL_CHANGE_TIMEOVER_CHAT);
        removeEvent(TVEVENT.E_SIGNAL_MONITER_CHAT);

        /*if (!CommonStaticData.returnMainFromChat) {
            MainActivity.getInstance().SolutionStop();
        }*/
        if (CommonStaticData.scanningNow) {
            doScan_chat.showProgress_chat(0, 0, 0, ScanProcess_chat.SHOW_PROGRESS_CLEAR_CHAT);
        }
        /*if (audioOut != null) {
            audioOut.audioModeReturn();
        }*/
        /*try {
            if (mWindowManager != null) {
                if (chat_view != null) {
                    mWindowManager.removeView(chat_view);
                    mWindowManager = null;
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }*/
    }

    public static SurfaceView getSurfaceView() {
        if (sv_chatView == null) {
            TVlog.i (TAG, " >>>>> ChatWindow Service not started!!");
        }
        return sv_chatView;
    }


    public void notifyFirstVideoChat() {
        sendEvent(TVEVENT.E_FIRSTVIDEO_CHAT);
    }

    public void notifyFirstAudioChat() {
        sendEvent(TVEVENT.E_FIRSTAUDIO_CHAT);
    }

    private class SysBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive (Context _ctx, Intent _intent) {
            String action = _intent.getAction();

            if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                TVlog.i(TAG, " >>>>> Intent.ACTION_SCREEN_OFF");
                MainActivity.getInstance().SolutionStop();
            }

            if (action.equals(Intent.ACTION_USER_PRESENT)) {
                TVlog.i(TAG, " >>>>> Intent.ACTION_USER_PRESENT");
                if (isRunningInForeground())
                {
                    TVlog.i(TAG, " TV running fore ground");
                    if (!CommonStaticData.ageLimitFlag) {
                        ChatMainActivity.isChat = true;
                        MainActivity.getInstance().onStart_TV();
                    }
                }
            }

            // usbdongle[[
            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                    buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB ||
                    buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB ||
                    buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                    TVlog.i("ChatMainActivity::", "usb dongle detached !");
                    UsbDevice device = (UsbDevice) _intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device != null && chat_currentUsbDevice != null && chat_currentUsbDevice.equals(device)) {
                        // call your method that cleans up and closes communication with the device
                        /*if (chat_view != null) {
                            mWindowManager.removeView(chat_view);
                            mWindowManager = null;
                        }*/
                        //stopSelf();
                        finish();
                        exit(0);
                    }
                }
            }
            // ]]usbdongle

            // usbdongle[[
            /*if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
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
                    TVlog.i("ChatMainActivity::", "usb dongle detached !: UsbChip="+mUsbChipType+", LME mode="+mUsbLMEMode);
                    if (mUsbChipType == USB_CHIP_TYPE_LME && mUsbLMEMode == USB_LME_MODE_COLD) {
                        TVlog.i("ChatMainActivity::", "usb dongle detached info: LME dongle, Cold mode");
                        return;
                    }
                    UsbDevice device = (UsbDevice) _intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device != null && mUsbDevice.equals(device)) {
                        // call your method that cleans up and closes communication with the device
                        if (mUsbChipType == USB_CHIP_TYPE_NXP || (mUsbChipType == USB_CHIP_TYPE_LME && mUsbLMEMode == USB_LME_MODE_WARM)) {
                            if (CommonStaticData.settingActivityShow == true){
                                SettingActivity.getInstance().onBackPressed();
                            }else if (CommonStaticData.epgActivityShow == true){
                                EPGActivity.getInstance().onBackPressed();
                            }else if (CommonStaticData.recordedFileActivityShow == true){
                                RecordedFileListActivity.getInstance().onBackPressed();
                            }else if (CommonStaticData.playBackActivityShow == true){
                                //PlayBackActivity.getInstance().onBackPressed();
                                PlayBackActivity.getInstance().closeActivity();
                            }else if (CommonStaticData.channelMainActivityShow == true){
                                ChannelMainActivity.getInstance().onBackPressed();
                            }else if (CommonStaticData.openActivityShow == true){
                                OpenActivity.getInstance().onBackPressed();
                            }else if (CommonStaticData.aboutActivityShow == true){
                                AboutActivity.getInstance().onBackPressed();
                            }
                            TVlog.i("ChatMainActivity::", "usb dongle closed ! (by device detached)");
                            //viewToastMSG("USB Dongle was detached!\n" + "TV app is terminated.", true);
                            CustomToast toast = new CustomToast(getApplicationContext());
                            toast.showToast(getApplicationContext(), "USB Dongle was detached!\nTV app is terminated.", Toast.LENGTH_LONG);
                            sendEvent(TVEVENT.E_TERMINATE);
                        }
                    }
                }

                if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action) && mUsbConnected == false) {
                    TVlog.i("ChatMainActivity::", "usb dongle attached !!!");
                }
            }*/
            // ]]usbdongle
        }
    }

    private class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {

            String action = intent.getAction();

            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

                String status = NetworkUtil.getConnectivityStatusString(context);

                //Toast.makeText(context, status, Toast.LENGTH_LONG).show();
                CustomToast toast = new CustomToast(getApplicationContext());
                toast.showToast(context, status, Toast.LENGTH_LONG);

                if (ChatMainActivity.getInstance() != null) {
                    if (ChatMainActivity.getInstance().mCahtListView != null && ChatMainActivity.getInstance().mNetworkMsg != null) {
                        if (status.equals("Not connected to Internet")) {
                            ChatMainActivity.getInstance().mCahtListView.setVisibility(View.GONE);
                            ChatMainActivity.getInstance().mNetworkMsg.setVisibility(VISIBLE);
                        } else {
                            //ChatMainActivity.getInstance().updateProfile();
                            ChatMainActivity.getInstance().mCahtListView.setVisibility(VISIBLE);
                            ChatMainActivity.getInstance().mNetworkMsg.setVisibility(View.GONE);
                            /*ChatMainActivity.getInstance().mChatAdapter = new ChatAdapter(context, 0);
                              ChatMainActivity.getInstance().mCahtListView.setAdapter(ChatMainActivity.getInstance().mChatAdapter);*/
                            //ChatMainActivity.getInstance().mCahtAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
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

    public void sendEvent(TVEVENT _Event) {
        int m;
        m = _Event.ordinal();
        Message msg = Chat_Handler.obtainMessage(m);
        Chat_Handler.sendMessage(msg);
    }

    public void sendEvent(TVEVENT _Event, int[] regionCh) {
        int m;
        m = _Event.ordinal();
        Message msg = Chat_Handler.obtainMessage(m);
        msg.obj = (int[])regionCh;
        Chat_Handler.sendMessage(msg);
    }

    public  void sendEvent(TVEVENT _Event, int _arg1, int _arg2, Object _obj) {
        int m;
        m = _Event.ordinal();
        Message msg = Chat_Handler.obtainMessage(m);
        msg.arg1 = _arg1;
        msg.arg2 = _arg2;
        msg.obj = _obj;
        Chat_Handler.sendMessage(msg);
    }

    public void postEvent(TVEVENT _Event,int _time )
    {
        int m;
        m = _Event.ordinal();
        Message msg = Chat_Handler.obtainMessage(m);
        Chat_Handler.sendEmptyMessageDelayed(m, _time);
    }

    public void postEvent(TVEVENT _Event, int _time, int _arg1)
    {
        int m;
        m = _Event.ordinal();
        Message msg = Chat_Handler.obtainMessage(m);
        msg.arg1 = _arg1;

        Chat_Handler.sendMessageDelayed(msg, _time);
    }

    public void removeEvent(TVEVENT _Event)
    {
        int m;
        m = _Event.ordinal();
        Message msg = Chat_Handler.obtainMessage(m);
        Chat_Handler.removeMessages(m);
    }

    public void channelChangeStartView(boolean _cas)
    {
        if (ll_chatAutoSearch.getVisibility() == VISIBLE) {
            ll_chatAutoSearch.setVisibility(View.INVISIBLE);
        }
        if (chat_noChannel.getVisibility() == VISIBLE) {
            chat_noChannel.setVisibility(View.INVISIBLE);
        }
        if (chat_ll_noSignal.getVisibility() == VISIBLE) {
            chat_ll_noSignal.setVisibility(View.INVISIBLE);
        }
        if (chat_ll_scramble_msg.getVisibility() == VISIBLE) {
            chat_ll_scramble_msg.setVisibility(View.INVISIBLE);
        }

        chat_changeChannelView.setVisibility(VISIBLE);
        chat_channelChangeBG.setVisibility(VISIBLE);
        if (chat_progressingChange != null) {
            chat_progressingChange.setVisibility(VISIBLE);
        }
        if (_cas == false) {
            chat_loadingChannel.setVisibility(VISIBLE);
        }
        else{   // call from playback
            chat_loadingChannel.setVisibility(GONE);
        }

        /*if (chat_ll_scramble_msg.getVisibility() == View.VISIBLE) {
            chat_ll_scramble_msg.setVisibility(View.INVISIBLE);
        }*/
    }

    public void channelChangeEndView(boolean _keepBG)
    {
        if (_keepBG ==false)
        {
            chat_channelChangeBG.setVisibility(View.INVISIBLE);
        }
        chat_changeChannelView.setVisibility(View.INVISIBLE);
    }

    public void hideChatController() {
        //if (chat_controllerLayout.isShown()) {


        TVlog.i(TAG, "== hideChatController ==");
        /*uiOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);*/

        chat_status_bar.setVisibility(View.INVISIBLE);
        chat_controllerLayout.setVisibility(View.INVISIBLE);

        //}
    }

    public int getChatChannelChangView()
    {
        int visual = chat_changeChannelView.getVisibility();

        if (visual == 0)
        {

            TVlog.i(TAG, "Viewing Ch change view ");
            return 0;
        }else
        {
            TVlog.i(TAG, "No Viewing  Ch change view ");
            return 1;
        }

    }

    private void showChatController(){
        if (CommonStaticData.handoverMode == CommonStaticData.HANDOVER_MODE_ON_NORMAL) {

        } else {
            chat_status_bar.setVisibility(VISIBLE);
            chat_controllerLayout.setVisibility(VISIBLE);

            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                /*
                int receiveMode = CommonStaticData.settings.getInt(CommonStaticData.receivemodeSwitchKey, CommonStaticData.RECEIVE_MODE_AUTO);  // auto
                if (chat_btn_receiveMode != null) {
                    chat_btn_receiveMode.setText(arr_svcmodeswitch_jp[receiveMode]);
                }*/
            }
            postEvent(TVEVENT.E_HIDE_CHAT_CONTROLER, CONTROLLER_HIDE_TIME);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_UP:
                if (ll_chatAutoSearch.getVisibility() != VISIBLE) {
                    if (chat_controllerLayout.getVisibility() == VISIBLE) {
                        hideChatController();
                    } else {
                        showChatController();
                    }
                }
                return false;

            default:
                return super.onTouchEvent(event);

        }

        //return super.onTouchEvent(event);
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void removeStatusBar(boolean remove) {
        if (remove) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    public void setChatSubSurfaceVisible(boolean _onoff) {
        if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
            if (svSub_chatView !=null) {
                if (_onoff) {
                    TVlog.i(TAG, "= Sub Chat surface visible = ");
                    svSub_chatView.setVisibility(VISIBLE);
                } else {
                    TVlog.i(TAG, "= Sub Chat surface invisible = ");
                    svSub_chatView.setVisibility(View.INVISIBLE);
                }
            }
        } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
            if (svSub_chatView !=null) {
                if (_onoff) {
                    TVlog.i(TAG, "= Sub Chat surface visible = ");
                    svSub_chatView.setVisibility(VISIBLE);
                } else {
                    TVlog.i(TAG, "= Sub Chat surface invisible = ");
                    svSub_chatView.setVisibility(View.INVISIBLE);
                }
            }
        } else {
            TVlog.e(TAG, "No AUTODETECT , but setChatSubSurfaceVisible call Error ");
        }
    }

    public void sendSubtitle(String capContents) {
        Bundle caption = new Bundle();
        caption.putString("caption_info", capContents);
        caption.putString("clear", "");
        sendEvent(TVEVENT.E_CAPTION_NOTIFY_CHAT, 0, 0, caption);
    }

    public  void sendSuperimpose(String superContents) {
        Bundle superimpose = new Bundle();
        superimpose.putString("superimpose_info", superContents);
        superimpose.putString("clear", "");
        sendEvent(TVEVENT.E_SUPERIMPOSE_NOTIFY_CHAT, 0, 0, superimpose);
    }

    //JAPAN_CAPTION[[
    public void sendSubtitleDirect(byte[] capData, int capLen, byte isClear, byte isEnd, int[] capInfo) {
        if (ChatMainActivity.isChat) {
            if (mCaptionView_chat != null && mCursor_chat != null) {
                mCaptionView_chat.renderCaptionDirect(capData, capLen, isClear, isEnd, capInfo, mCursor_chat.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV));
            }
        }
    }

    public void sendSuperimposeDirect(byte[] supData, int supLen, byte isClear, byte isEnd, int[] supInfo) {
        if (ChatMainActivity.isChat) {
            if (mSuperimposeView_chat != null && mCursor_chat != null) {
                mSuperimposeView_chat.renderCaptionDirect(supData, supLen, isClear, isEnd, supInfo, mCursor_chat.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV));
            }
        }
    }
    //]]JAPAN_CAPTION

    public void scanNotify_chat(int idx, String desc, byte type, byte vFormat, byte aFormat, byte iFree, int remoteKey, int svcNum, int freqKHz, byte bLast) {
        TVlog.i("live", " ===== scanNotify_chat() =====");
        ContentValues values = new ContentValues();
        if (bLast==2) {
            if (MainActivity.getCursor() != null) {
                int cursorCount = mCursor_chat.getCount();
                if (cursorCount > 0 && cursorCount > mCursor_chat.getPosition()) {
                    TVBridge.setLastRemoteKey(mCursor_chat.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_REMOTE_KEY));
                    TVBridge.setLastSvcID(mCursor_chat.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_NUMBER));
                    TVBridge.setLastListCount(cursorCount);
                    TVBridge.setLastFreq(mCursor_chat.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_FREQ));
                    CommonStaticData.isProcessingUpdate = true;
                }
                else {
                    if (cursorCount > 0) {
                        //TVlog.i("FCIISDBT::", ">>> exception: current pos invalid >>> pos="+mCursor_chat.getPosition()+"of count="+cursorCount);
                    }
                }
            }
            CommonStaticData.loadingNow = true;
            getContentResolver().delete(MainActivity.getInstance().mUri,null,null);    // channel DB delete
        } else if (bLast == 1) {
            //postEvent(TVEVENT.E_CHLIST_UPDATE_CHAT, 0);
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

    public void envSet_JP_chat() {
        if (MainActivity.getInstance() != null) {
            mFont_chat = MainActivity.getInstance().mFont;
        }

        //use free font
        if (subTitleView_chat != null) {
            subTitleView_chat.setTypeface(mFont_chat);
        }
        if (superImposeView_chat != null) {
            superImposeView_chat.setTypeface(mFont_chat);
        }

        //JAPAN_CAPTION[[
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
                    capHeight =  MainActivity.getInstance().frameWidth / 3;
                }
            }
        }

        TVlog.i("live", ">>> caption real width ="+capWidth+", caption real height="+capHeight);

        //caption
        mCaptionLayout_chat = (FrameLayout)findViewById(R.id.chat_frameLayout);
        mCaptionView_chat = new CaptionDirectView(this, mCaptionLayout_chat, capWidth, capHeight, mFont_chat, M_TYPE_CAPTION_SUBTITLE);

        //superimpose
        mSuperimposeLayout_chat = (FrameLayout) findViewById(R.id.chat_frameLayout);
        mSuperimposeView_chat = new CaptionDirectView(this, mSuperimposeLayout_chat, capWidth, capHeight, mFont_chat, M_TYPE_CAPTION_SUPERIMPOSE);

        //caption
        mCaptionView_chat.setVisibility(View.VISIBLE);
        mCaptionLayout_chat.addView(mCaptionView_chat);
        //setContentView(mCaptionView_chat);

        //superimpose
        mSuperimposeView_chat.setVisibility(View.VISIBLE);
        mSuperimposeLayout_chat.addView(mSuperimposeView_chat);
        //setContentView(mSuperimposeLayout_chat);
        //]]JAPAN_CAPTION
    }

    public void envSet_Normal_chat() {
        mFont_chat = Typeface.DEFAULT;
        chat_currCH.setTypeface(mFont_chat);
        /*
        if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 ) {
            chat_currProgram.setTypeface(mFont_chat);
        }*/

        subTitleView_chat.setTypeface(mFont_chat);
        superImposeView_chat.setTypeface(mFont_chat);
    }
    //]]
}
