package kr.co.fci.tv.chat;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.List;
import java.util.Random;

import kr.co.fci.tv.MainActivity;
import kr.co.fci.tv.R;
import kr.co.fci.tv.SubSurfaceSet;
import kr.co.fci.tv.TVEVENT;
import kr.co.fci.tv.buildOption;
import kr.co.fci.tv.emoji.Emojicon;
import kr.co.fci.tv.emoji.EmojiconEditText;
import kr.co.fci.tv.emoji.EmojiconGridView;
import kr.co.fci.tv.emoji.EmojiconsPopup;
import kr.co.fci.tv.saves.CommonStaticData;
import kr.co.fci.tv.saves.TVProgram;
import kr.co.fci.tv.setting.InputDialog;
import kr.co.fci.tv.tvSolution.AudioOut;
import kr.co.fci.tv.tvSolution.FCI_TVi;
import kr.co.fci.tv.tvSolution.SignalMonitor;
import kr.co.fci.tv.tvSolution.TVBridge;
import kr.co.fci.tv.util.CustomToast;
import kr.co.fci.tv.util.TVlog;

import static android.view.View.GONE;
import static java.lang.System.exit;
import static kr.co.fci.tv.R.id.sv_chat;
import static kr.co.fci.tv.TVEVENT.E_CHANNEL_CHANGE_TIMEOVER_CHAT;
import static kr.co.fci.tv.TVEVENT.E_SCAN_MONITOR_CHAT;
import static kr.co.fci.tv.TVEVENT.E_SIGNAL_NOTI_MSG_CHAT;

/**
 * Created by live.kim on 2017-03-24.
 */

public class ChatMainActivity extends AppCompatActivity implements View.OnClickListener, SurfaceHolder.Callback {

    private static String TAG = "ChatMainActivity ";

    private static final int RC_SIGN_IN = 1001;

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

    public int frameWidthChat = 0;
    public int frameHeightChat = 0;

    private static Cursor mCursor;
    Uri mUri;
    public int mChannelIndex = CommonStaticData.lastCH;
    int AudioFormat=0x00;        // recording 0x60(HEAAV), 0x40(AAC)
    int VideoFormat=0x00;        // recording 0x04(H.264)
    int Scrambled = 1;             // 0: scramble ch, 1: free ch
    int mRemoteKey = 0;
    int mSvcNumber = 0;
    LinearLayout chat_ll_age_limit;
    TextView chat_tv_age_limit_msg_10;
    TextView chat_tv_age_limit_msg_12;
    TextView chat_tv_age_limit_msg_14;
    TextView chat_tv_age_limit_msg_16;
    TextView chat_tv_age_limit_msg_18;
    LinearLayout chat_ll_scramble_msg;
    me.grantland.widget.AutofitTextView chat_tv_scramble_title;
    me.grantland.widget.AutofitTextView chat_tv_scramble_msg;
    private boolean SignalStatFlag =false;
    int signal_check_cnt = 0;
    private final static int NO_SIGNAL_MSG_TIME = 10000;  // live add
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
    private TextView chat_loadingChannel;
    LinearLayout chat_controllerLayout;
    LinearLayout chat_status_bar;
    public Typeface mFont, tf;
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

    ImageButton button_prev;
    ImageButton button_next;
    ImageButton button_max;

    EmojiconEditText emojiconEditText;
    View rootView;
    ImageView emojiButton;
    ImageView submitButton;
    TextView txtEmojis;
    EmojiconsPopup popup;

    TextView tv_message;
    private LinearLayout chat_changeChannelView =null;
    private ImageView chat_channelChangeBG = null;
    private TextView chat_programNotMsg;
    private TextView chat_noSignal;

    TextView chat_room_title;

    public static ChatMainActivity instance;
    public static ChatMainActivity getInstance()
    {
        return instance;
    }


    public Handler Chat_Handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            TVEVENT event = TVEVENT.values()[msg.what];
            /*if(TVON==false)
            {
                TVlog.i(TAG, "---------------- TV OFF -------------------");
                return;
            }*/
            switch (event) {

                case E_SCAN_COMPLETED_CHAT:
                    TVlog.i(TAG, " >>>>> E_SCAN_COMPLETED_CHAT");
                    channelChangeStartView(false);  // live remove

                    TVlog.i(TAG, "---------------- E_SCAN_COMPLETED-------------------");
                    CommonStaticData.scanningNow = false;
                    if(CommonStaticData.scanCHnum > 0) {
                        final int NEED_TO_CHANGE_CHANNEL_NO = 0;
                        final int NEED_TO_CHANGE_CHANNEL_FIRST_LOAD = 1;
                        final int NEED_TO_CHANGE_CHANNEL_CHANGE_INDEX = 2;
                        int statusOfNeedToChange = NEED_TO_CHANGE_CHANNEL_NO;
                        mCursor = MainActivity.getCursor();
                        if (mUri != null) {
                            mCursor = getContentResolver().query(mUri, CommonStaticData.PROJECTION, TVProgram.Programs.TYPE + "=?", CommonStaticData.selectionArgsTV, null);
                        }
                        if (mCursor != null && mCursor.getCount() > 0 && (mCursor.getPosition() < mCursor.getCount())) {
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
                                CommonStaticData.isProcessingUpdate = false;
                            }
                            else {
                                statusOfNeedToChange = NEED_TO_CHANGE_CHANNEL_FIRST_LOAD;
                            }

                            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                                switch (CommonStaticData.receivemode){
                                    case 0:     // 1seg
                                        if(mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV)!=0) {
                                            for (int i=0; i < mCursor.getCount(); i++) {
                                                mCursor.moveToPosition(i);
                                                if (mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV) == 0) {
                                                    mChannelIndex = i;
                                                    break;
                                                }
                                            }
                                            if(mChannelIndex==0){   // not found channel
                                                TVBridge.stop();
                                                channelChangeEndView(false);
                                                CustomToast toast = new CustomToast(getApplicationContext());
                                                toast.showToast(getApplicationContext(), getApplicationContext().getString(R.string.ch_change_fail), Toast.LENGTH_SHORT);
                                            }
                                        }
                                        break;
                                    case 1:     // fullseg
                                        if(mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV)!=1) {
                                            for (int i=0; i < mCursor.getCount(); i++) {
                                                mCursor.moveToPosition(i);
                                                if (mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV) == 1) {
                                                    mChannelIndex = i;
                                                    break;
                                                }
                                            }
                                            if(mChannelIndex==0) {      // not found channel
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

                            mCursor.moveToPosition(mChannelIndex);

                            int freq = Integer.parseInt(mCursor.getString(CommonStaticData.COLUMN_INDEX_SERVICE_FREQ));
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
                            String channelName = mCursor.getString(CommonStaticData.COLUMN_INDEX_SERVICE_NAME);
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
                            //

                            AudioFormat = mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_AUDFORM);
                            VideoFormat = mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_VIDFORM);
                            Scrambled = mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_FREE);
                            mRemoteKey = mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_REMOTE_KEY);
                            mSvcNumber = mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_NUMBER);

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

                                    /*if(isCheckingPlayback())
                                    {
                                        TVlog.i(TAG, " playback running  ");
                                        break;
                                    }*/
                                    if(buildOption.LOG_CAPTURE_MODE==3)
                                    {
                                        TVBridge.serviceID_start(0);
                                        postEvent(TVEVENT.E_AUTO_CHANGE_CHANNEL_TEST_CHAT, 20 * 1000);
                                    }else {

                                        TVBridge.serviceID_start(mChannelIndex);
                                    }
                                } else {
                                    chat_changeChannelView.setVisibility(View.INVISIBLE);
                                    chat_channelChangeBG.setVisibility(View.INVISIBLE);
                                    chat_progressingChange.setVisibility(View.INVISIBLE);
                                    chat_loadingChannel.setVisibility(View.INVISIBLE);
                                }
                            } else {

                                TVlog.i(TAG, " =====  screen off =========");
                                MainActivity.getInstance().SolutionStop();
                            }
                        } else {
                            //postEvent(TVEVENT.E_CHLIST_UPDATE_CHAT, 0);
                        }
                        chat_noSignal.setVisibility(View.INVISIBLE);
                        chat_programNotMsg.setVisibility(View.INVISIBLE);

                    } else {

                        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                        boolean isScreenOn;
                        if (Build.VERSION.SDK_INT <= 19) {
                            isScreenOn = pm.isScreenOn();
                        } else {
                            isScreenOn = pm.isInteractive();
                        }

                        if(isScreenOn)
                        {

                            TVlog.i(TAG, " =====  no scan =========");
                            if (chat_currChNo != null && chat_currCH != null) {
                                chat_currChNo.setText("- -ch");
                                chat_currRemoteNo.setText("- - -");
                                chat_currCH.setText(R.string.no_channel_title);
                            }
                            chat_changeChannelView.setVisibility(View.INVISIBLE);

                            if (sv_chatView != null) {
                                sv_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                            }
                            if(buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                                if (svSub_chatView != null) {
                                    svSub_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                                }
                            }

                            if (chat_tv_scramble_title.getVisibility() == View.INVISIBLE) {
                                CommonStaticData.badSignalFlag = true;
                                CommonStaticData.encryptFlag = false;
                                CommonStaticData.ageLimitFlag = false;
                                chat_noSignal.setVisibility(View.VISIBLE);
                                chat_programNotMsg.setVisibility(View.VISIBLE);
                            }

                            /*if(isCheckingPlayback())
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


                case E_FIRSTVIDEO_CHAT:
                {
                    TVlog.i(TAG, " >>>>> E_FIRSTVIDEO_CHAT");
                    removeEvent(TVEVENT.E_BADSIGNAL_CHECK_CHAT);
                    removeEvent(E_SIGNAL_NOTI_MSG_CHAT);
                    removeEvent(TVEVENT.E_NOSIGNAL_SHOW_CHAT);
                    removeEvent(E_CHANNEL_CHANGE_TIMEOVER_CHAT);

                    if (buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL
                            || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_ONESEG
                            || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_FILE) {
                        chat_curr_rate = FCI_TVi.GetCurProgramRating();
                        if((chat_curr_rate >= (CommonStaticData.PG_Rate+1) && (CommonStaticData.PG_Rate!=0))
                                && (CommonStaticData.passwordVerifyFlag == false)
                                && (CommonStaticData.ratingsetSwitch == true)) {
                            CommonStaticData.ageLimitFlag = true;
                        } else {
                            CommonStaticData.ageLimitFlag = false;
                        }
                        sendEvent(TVEVENT.E_RATING_MONITOR_CHAT);
                    }

                    if (chat_programNotMsg.getVisibility() == View.VISIBLE) {
                        chat_noSignal.setVisibility(View.INVISIBLE);
                        chat_programNotMsg.setVisibility(View.INVISIBLE);
                    }

                    if (chat_ll_scramble_msg.getVisibility() == View.VISIBLE) {
                        chat_ll_scramble_msg.setVisibility(View.INVISIBLE);
                    }

                    InputDialog.nosignalNotiClear();
                    SignalStatFlag = false;
                    CommonStaticData.tuneTimeOver = false;
                    channelChangeEndView(false);

                }
                break;

                case E_CHANNEL_CHANGE_TIMEOVER_CHAT:
                    TVlog.i(TAG, " >>>>> E_CHANNEL_CHANGE_TIMEOVER_CHAT");
                    if(CommonStaticData.tuneTimeOver==true && CommonStaticData.scanningNow==false ) {

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

                case E_BADSIGNAL_CHECK_CHAT:
                    TVlog.i(TAG, " >>>>> E_BADSIGNAL_CHECK_CHAT");
                    int stat = (int)msg.arg1;
                    /*if (isPlayBackActivity) {
                        break;
                    }*/
                    switch (stat){
                        case 1: // low buffer
                            if(SignalStatFlag==false) {
                                TVlog.i(TAG, " >>>>> E_BADSIGNAL_CHECK_CHAT CASE1");
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
                            channelChangeEndView(true);
                            /*if (isChannelListViewOn) {
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
                            if (sv_chatView != null) {
                                sv_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                            }
                            if(buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                                if (svSub_chatView != null) {
                                    svSub_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                                }
                            }
                            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                                //chat_ll_scramble_msg.setVisibility(View.VISIBLE);
                            } else {
                                chat_ll_scramble_msg.setVisibility(View.VISIBLE);
                            }

                            break;
                        case 3: //RF signal bad
                            TVlog.i(TAG, " >>>>> E_BADSIGNAL_CHECK_CHAT CASE3");
                            //if(CommonStaticData.scanningNow==false && CommonStaticData.scanCHnum > 0) {
                            if(CommonStaticData.scanningNow==false) {

                                //new InputDialog(instance, InputDialog.TYPE_SIGNALSTAT_NOTI, null, null, null);

                                // live add
                                CommonStaticData.badSignalFlag = true;
                                CommonStaticData.encryptFlag = false;
                                CommonStaticData.ageLimitFlag = false;

                                // live add
                                if (sv_chatView != null) {
                                    sv_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                                }
                                if(buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                                    if (svSub_chatView != null) {
                                        svSub_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                                    }
                                }

                                if (chat_tv_scramble_title.getVisibility() == View.INVISIBLE) {
                                    chat_noSignal.setVisibility(View.VISIBLE);
                                    chat_programNotMsg.setVisibility(View.VISIBLE);
                                }
                                signal_check_cnt = 0;
                            } else {
                                removeEvent(TVEVENT.E_BADSIGNAL_CHECK_CHAT);
                                //MainActivity.getInstance().removeEvent(TVEVENT.E_BADSIGNAL_CHECK);
                            }
                            break;
                        case 4: //program not available
                            TVlog.i(TAG, " >>>>> E_BADSIGNAL_CHECK_CHAT CASE4");
                            if(CommonStaticData.scanningNow==false && CommonStaticData.scanCHnum > 0) {
                                //channelChangeEndViewMulti(false);
                                /*CustomToast toast7 = new CustomToast(getApplicationContext());
                                toast7.showToast(getApplicationContext(),
                                        getApplicationContext().getString(R.string.no_signal_msg)+"\n"+
                                                getApplicationContext().getString(R.string.program_not_available), Toast.LENGTH_SHORT);*/

                                // live add
                                CommonStaticData.badSignalFlag = true;
                                CommonStaticData.encryptFlag = false;
                                CommonStaticData.ageLimitFlag = false;
                                if (sv_chatView != null) {
                                    sv_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                                }
                                if(buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                                    if (svSub_chatView != null) {
                                        svSub_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                                    }
                                }

                                if (chat_changeChannelView.getVisibility() == View.VISIBLE) {
                                    chat_changeChannelView.setVisibility(View.INVISIBLE);
                                }

                                if (chat_tv_scramble_title.getVisibility() == View.INVISIBLE) {
                                    chat_noSignal.setVisibility(View.VISIBLE);
                                    chat_programNotMsg.setVisibility(View.VISIBLE);
                                }
                            }
                            else {
                                removeEvent(TVEVENT.E_BADSIGNAL_CHECK_CHAT);
                                //MainActivity.getInstance().removeEvent(TVEVENT.E_BADSIGNAL_CHECK);
                            }
                            break;
                    }
                    break;

                case E_SCAN_MONITOR_CHAT:
                    TVlog.i(TAG, " >>>>> E_SCAN_MONITOR_CHAT");
                    if(CommonStaticData.scanCHnum != 0) {
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
                    if(signalMoniter!=null)
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

                            if((chat_curr_rate >= (CommonStaticData.PG_Rate+1) && (CommonStaticData.PG_Rate!=0))
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
                    if(CommonStaticData.scanningNow==false) {
                        CustomToast toast8 = new CustomToast(getApplicationContext());
                        toast8.showToast(getApplicationContext(), getApplicationContext().getString(R.string.signal_weak), Toast.LENGTH_SHORT);
                        SignalStatFlag = false;
                        postEvent(TVEVENT.E_NOSIGNAL_SHOW_CHAT, NO_SIGNAL_MSG_TIME);     // 10sec
                        //MainActivity.getInstance().postEvent(TVEVENT.E_NOSIGNAL_SHOW, NO_SIGNAL_MSG_TIME);
                    }
                    break;
                case E_NOSIGNAL_SHOW_CHAT:
                    TVlog.i(TAG, " >>>>> E_NOSIGNAL_SHOW_CHAT");
                    if(CommonStaticData.scanningNow==false) {
                        sendEvent(TVEVENT.E_BADSIGNAL_CHECK_CHAT, 3, 0, null);
                        //MainActivity.getInstance().sendEvent(TVEVENT.E_BADSIGNAL_CHECK, 3, 0, null);
                    }
                    break;

                case E_CHAT_SURFACE_SUB_ONOFF: {
                    int onoff = (int) msg.arg1;
                    if(onoff==1)
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
                    //if(screenbl_enable.equals(false) && password_verify.equals(false)) {
                    if(CommonStaticData.ageLimitFlag) {
                        //TVBridge.stop();
                        if (sv_chatView != null) {
                            sv_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                        }
                        if(buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                            if (svSub_chatView != null) {
                                svSub_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                            }
                        }
                        if (chat_curr_rate == 2) {
                            chat_tv_age_limit_msg_10.setVisibility(View.VISIBLE);
                            chat_tv_age_limit_msg_12.setVisibility(View.INVISIBLE);
                            chat_tv_age_limit_msg_14.setVisibility(View.INVISIBLE);
                            chat_tv_age_limit_msg_16.setVisibility(View.INVISIBLE);
                            chat_tv_age_limit_msg_18.setVisibility(View.INVISIBLE);
                        } else if (chat_curr_rate == 3) {
                            chat_tv_age_limit_msg_10.setVisibility(View.INVISIBLE);
                            chat_tv_age_limit_msg_12.setVisibility(View.VISIBLE);
                            chat_tv_age_limit_msg_14.setVisibility(View.INVISIBLE);
                            chat_tv_age_limit_msg_16.setVisibility(View.INVISIBLE);
                            chat_tv_age_limit_msg_18.setVisibility(View.INVISIBLE);
                        } else if (chat_curr_rate == 4) {
                            chat_tv_age_limit_msg_10.setVisibility(View.INVISIBLE);
                            chat_tv_age_limit_msg_12.setVisibility(View.INVISIBLE);
                            chat_tv_age_limit_msg_14.setVisibility(View.VISIBLE);
                            chat_tv_age_limit_msg_16.setVisibility(View.INVISIBLE);
                            chat_tv_age_limit_msg_18.setVisibility(View.INVISIBLE);
                        } else if (chat_curr_rate == 5) {
                            chat_tv_age_limit_msg_10.setVisibility(View.INVISIBLE);
                            chat_tv_age_limit_msg_12.setVisibility(View.INVISIBLE);
                            chat_tv_age_limit_msg_14.setVisibility(View.INVISIBLE);
                            chat_tv_age_limit_msg_16.setVisibility(View.VISIBLE);
                            chat_tv_age_limit_msg_18.setVisibility(View.INVISIBLE);
                        } else if (chat_curr_rate == 6) {
                            chat_tv_age_limit_msg_10.setVisibility(View.INVISIBLE);
                            chat_tv_age_limit_msg_12.setVisibility(View.INVISIBLE);
                            chat_tv_age_limit_msg_14.setVisibility(View.INVISIBLE);
                            chat_tv_age_limit_msg_16.setVisibility(View.INVISIBLE);
                            chat_tv_age_limit_msg_18.setVisibility(View.VISIBLE);
                        } else {
                            chat_tv_age_limit_msg_10.setVisibility(View.INVISIBLE);
                            chat_tv_age_limit_msg_12.setVisibility(View.INVISIBLE);
                            chat_tv_age_limit_msg_14.setVisibility(View.INVISIBLE);
                            chat_tv_age_limit_msg_16.setVisibility(View.INVISIBLE);
                            chat_tv_age_limit_msg_18.setVisibility(View.INVISIBLE);
                        }
                        chat_ll_age_limit.setVisibility(View.VISIBLE);
                        chat_noSignal.setVisibility(View.INVISIBLE);
                        chat_programNotMsg.setVisibility(View.INVISIBLE);
                        chat_ll_scramble_msg.setVisibility(View.INVISIBLE);
                        chat_changeChannelView.setVisibility(View.INVISIBLE);
                        TVlog.i("chat_justin", " ====> screenbl_enabled stop !!!");
                        FCI_TVi.setVolume(0.0f);
                    } else {
                        if (sv_chatView != null) {
                            sv_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
                        }
                        if(buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                            if (svSub_chatView != null) {
                                svSub_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
                            }
                        }
                        chat_ll_age_limit.setVisibility(View.INVISIBLE);
                        FCI_TVi.setVolume(1.0f);
                    }
                    break;

                case   E_CONFIRMED_PASSWORD_CHAT:
                {
                    CommonStaticData.passwordVerifyFlag = true;
                    CommonStaticData.ageLimitFlag = false;
                    //TVBridge.serviceID_start(mChannelIndex);
                    sendEvent(TVEVENT.E_RATING_MONITOR_CHAT);
                    if (sv_chatView != null) {
                        sv_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
                    }
                    if(buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                        if (svSub_chatView != null) {
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

                    if(buildOption.LOG_CAPTURE_MODE ==3)
                    {
                        int currentChannel = TVBridge.getCurrentChannel();

                        TVlog.i(TAG, " E_AUTO_CHANGE_CHANNEL_TEST  currentID = " + currentChannel + " ChannelCount = "+CommonStaticData.scanCHnum );
                        if(currentChannel < (CommonStaticData.scanCHnum -1)) {
                            CommonStaticData.passwordVerifyFlag = false;
                            //CommonStaticData.screenBlockFlag = false;
                            CommonStaticData.ageLimitFlag = false;
                            //sendEvent(TVEVENT.E_CAPTION_CLEAR_NOTIFY);
                            //sendEvent(TVEVENT.E_SUPERIMPOSE_CLEAR_NOTIFY);

                            channelChangeStartView(false);  // live remove
                            chat_changeChannelView.setVisibility(View.VISIBLE);
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
                    if (mCursor != null) {
                        mChannelIndex=CommonStaticData.lastCH;
                        mCursor.moveToPosition(mChannelIndex);
                        if(CommonStaticData.scanCHnum != 0) {

                            int freq = Integer.parseInt(mCursor.getString(CommonStaticData.COLUMN_INDEX_SERVICE_FREQ));
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

                            String channelName = mCursor.getString(CommonStaticData.COLUMN_INDEX_SERVICE_NAME);
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

                            //chat_currCH.setText(mCursor.getString(CommonStaticData.COLUMN_INDEX_SERVICE_NAME));
                            AudioFormat = mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_AUDFORM);
                            VideoFormat = mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_VIDFORM);
                            Scrambled = mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_FREE);
                            mRemoteKey = mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_REMOTE_KEY);
                            mSvcNumber = mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_NUMBER);

                            TVlog.i (TAG, " >>>>> Scrambled = "+String.valueOf(Scrambled));

                            if(Scrambled == 0) {
                                sendEvent(TVEVENT.E_BADSIGNAL_CHECK_CHAT, 2, 0, null);
                            }
                        }
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

        removeStatusBar(false);

        ImageView signalImage = (ImageView) findViewById(R.id.chat_dtv_signal);
        //signalMoniter = new SignalMonitor(signalImage);

        sv_chatView = (SurfaceView) findViewById(sv_chat);
        sv_chatView.setBackgroundColor(getResources().getColor(R.color.black));

        if(buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
            svSub_chatView =(SurfaceView) findViewById(R.id.svSub_chat);
        }

        instance = ChatMainActivity.this;
        chatVideoSurfaceHolder = sv_chatView.getHolder();

        if(buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
            if (svSub_chatView != null) {
                chatVideoSurfaceHolderSub = svSub_chatView.getHolder();
            }
        }

        int h = sv_chatView.getMeasuredHeight();
        int w = sv_chatView.getMeasuredWidth();

        chatVideoSurfaceHolder.setFixedSize(w, h);
        chatVideoSurfaceHolder.addCallback(this);
        if(buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
            if (svSub_chatView != null) {
                chatVideoSurfaceHolderSub.setFixedSize(w, h);
                chatVideoSurfaceHolderSub.addCallback(SubSurfaceSet.getSubSurfaceSet());
            }
        }

        chat_noSignal = (me.grantland.widget.AutofitTextView) findViewById(R.id.chat_no_signal_msg);
        if (Build.VERSION.SDK_INT <= 19) {
            chat_noSignal.setTextColor(getResources().getColor(R.color.white));
        }
        chat_noSignal.setVisibility(View.INVISIBLE);

        chat_programNotMsg = (me.grantland.widget.AutofitTextView) findViewById(R.id.chat_program_not_msg);
        if (Build.VERSION.SDK_INT <= 19) {
            chat_programNotMsg.setTextColor(getResources().getColor(R.color.white));
        }
        chat_programNotMsg.setVisibility(View.INVISIBLE);

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
        chat_tv_scramble_title = (me.grantland.widget.AutofitTextView) findViewById(R.id.chat_tv_scramble_title);
        //chat_tv_scramble_title.setVisibility(View.INVISIBLE);
        chat_tv_scramble_msg = (me.grantland.widget.AutofitTextView) findViewById(R.id.chat_tv_scramble_msg);
        //chat_tv_scramble_msg.setVisibility(View.INVISIBLE);


        TVlog.i(TAG, " >>>>> CommonStaticData.badSignaltFlag= "+CommonStaticData.badSignalFlag+
                ", CommonStaticData.encryptFlag = "+CommonStaticData.encryptFlag);

        // live add
        if (CommonStaticData.badSignalFlag) {
            if (sv_chatView != null) {
                sv_chatView.setBackgroundColor(getResources().getColor(R.color.black));
            }
            if(buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                if (svSub_chatView != null) {
                    svSub_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                }
            }
            if (chat_programNotMsg.getVisibility() == View.INVISIBLE) {
                chat_noSignal.setVisibility(View.VISIBLE);
                chat_programNotMsg.setVisibility(View.VISIBLE);
            }
        } else if (!CommonStaticData.badSignalFlag) {
            if (sv_chatView != null) {
                sv_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
            }
            if(buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                if (svSub_chatView != null) {
                    svSub_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
            }
            if (chat_programNotMsg.getVisibility() == View.VISIBLE) {
                chat_noSignal.setVisibility(View.INVISIBLE);
                chat_programNotMsg.setVisibility(View.INVISIBLE);
            }

        }

        if (CommonStaticData.encryptFlag) {
            if (sv_chatView != null) {
                sv_chatView.setBackgroundColor(getResources().getColor(R.color.black));
            }
            if(buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                if (svSub_chatView != null) {
                    svSub_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                }
            }
            if (chat_tv_scramble_msg.getVisibility() == View.INVISIBLE) {
                chat_tv_scramble_title.setVisibility(View.VISIBLE);
                chat_tv_scramble_msg.setVisibility(View.VISIBLE);
            }

        } else if (!CommonStaticData.encryptFlag) {
            if (sv_chatView != null) {
                sv_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
            }
            if(buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                if (svSub_chatView != null) {
                    svSub_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
            }
            if (chat_tv_scramble_msg.getVisibility() == View.VISIBLE) {
                chat_tv_scramble_title.setVisibility(View.INVISIBLE);
                chat_tv_scramble_msg.setVisibility(View.INVISIBLE);
            }
        }

        if (buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL
                || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_ONESEG
                || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_FILE) {
            if (CommonStaticData.ageLimitFlag) {
                if (sv_chatView != null) {
                    sv_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                }
                if(buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                    if (svSub_chatView != null) {
                        svSub_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                    }
                }
                if (chat_curr_rate == 2) {
                    chat_tv_age_limit_msg_10.setVisibility(View.VISIBLE);
                    chat_tv_age_limit_msg_12.setVisibility(View.INVISIBLE);
                    chat_tv_age_limit_msg_14.setVisibility(View.INVISIBLE);
                    chat_tv_age_limit_msg_16.setVisibility(View.INVISIBLE);
                    chat_tv_age_limit_msg_18.setVisibility(View.INVISIBLE);
                } else if (chat_curr_rate == 3) {
                    chat_tv_age_limit_msg_10.setVisibility(View.INVISIBLE);
                    chat_tv_age_limit_msg_12.setVisibility(View.VISIBLE);
                    chat_tv_age_limit_msg_14.setVisibility(View.INVISIBLE);
                    chat_tv_age_limit_msg_16.setVisibility(View.INVISIBLE);
                    chat_tv_age_limit_msg_18.setVisibility(View.INVISIBLE);
                } else if (chat_curr_rate == 4) {
                    chat_tv_age_limit_msg_10.setVisibility(View.INVISIBLE);
                    chat_tv_age_limit_msg_12.setVisibility(View.INVISIBLE);
                    chat_tv_age_limit_msg_14.setVisibility(View.VISIBLE);
                    chat_tv_age_limit_msg_16.setVisibility(View.INVISIBLE);
                    chat_tv_age_limit_msg_18.setVisibility(View.INVISIBLE);
                } else if (chat_curr_rate == 5) {
                    chat_tv_age_limit_msg_10.setVisibility(View.INVISIBLE);
                    chat_tv_age_limit_msg_12.setVisibility(View.INVISIBLE);
                    chat_tv_age_limit_msg_14.setVisibility(View.INVISIBLE);
                    chat_tv_age_limit_msg_16.setVisibility(View.VISIBLE);
                    chat_tv_age_limit_msg_18.setVisibility(View.INVISIBLE);
                } else if (chat_curr_rate == 6) {
                    chat_tv_age_limit_msg_10.setVisibility(View.INVISIBLE);
                    chat_tv_age_limit_msg_12.setVisibility(View.INVISIBLE);
                    chat_tv_age_limit_msg_14.setVisibility(View.INVISIBLE);
                    chat_tv_age_limit_msg_16.setVisibility(View.INVISIBLE);
                    chat_tv_age_limit_msg_18.setVisibility(View.VISIBLE);
                } else {
                    chat_tv_age_limit_msg_10.setVisibility(View.INVISIBLE);
                    chat_tv_age_limit_msg_12.setVisibility(View.INVISIBLE);
                    chat_tv_age_limit_msg_14.setVisibility(View.INVISIBLE);
                    chat_tv_age_limit_msg_16.setVisibility(View.INVISIBLE);
                    chat_tv_age_limit_msg_18.setVisibility(View.INVISIBLE);
                }
                chat_ll_age_limit.setVisibility(View.VISIBLE);
            } else {
                if (sv_chatView != null) {
                    sv_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
                if(buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                    if (svSub_chatView != null) {
                        svSub_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
                    }
                }
                chat_ll_age_limit.setVisibility(View.INVISIBLE);
            }
        }

        if (CommonStaticData.badSignalFlag || CommonStaticData.encryptFlag || CommonStaticData.ageLimitFlag) {
            if (sv_chatView != null) {
                sv_chatView.setBackgroundColor(getResources().getColor(R.color.black));
            }
            if(buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                if (svSub_chatView != null) {
                    svSub_chatView.setBackgroundColor(getResources().getColor(R.color.black));
                }
            }
        } else {
            if (sv_chatView != null) {
                sv_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
            }
            if(buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                if (svSub_chatView != null) {
                    svSub_chatView.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
            }
        }


        chat_ll_ch_info = (LinearLayout) findViewById(R.id.chat_ll_ch_info);
        chat_channelChangeBG = (ImageView) findViewById(R.id.chat_imageView_bg);

        chat_currChNo = (TextView) findViewById(R.id.chat_tv_ch_no);
        tf = Typeface.createFromAsset(getAssets(), "fonts/digital7.ttf");
        chat_currChNo.setTypeface(tf);
        chat_currChNo.setTextSize(18);

        if (buildOption.VIEW_PHY_CH) {
            chat_currChNo.setVisibility(View.VISIBLE);
        } else {
            chat_currChNo.setVisibility(GONE);
        }

        chat_currRemoteNo = (TextView) findViewById(R.id.chat_tv_remote_no);
        chat_currCH = (TextView) findViewById(R.id.chat_servicename);
        //if (buildOption.GUI_STYLE == 1) {
        chat_currCH.setSelected(true);
        //}

        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
            //use free font
            mFont = Typeface.createFromAsset(getAssets(), "wlcmaru2004emoji.ttf");
            chat_currCH.setTypeface(mFont);
        }

        signalMoniter = new SignalMonitor(signalImage);

        chat_changeChannelView = (LinearLayout) findViewById(R.id.chat_progressBarCircularIndeterminate);
        chat_changeChannelView.setVisibility(View.INVISIBLE);

        //chat_progressingChange = (CustomView) findViewById(R.id.chat_progressing_channel);
        chat_progressingChange = (ProgressBar) findViewById(R.id.chat_progressing_channel);
        chat_progressingChange.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);

        chat_loadingChannel = (TextView) findViewById(R.id.chat_loading_channel);

        chat_status_bar = (LinearLayout) findViewById(R.id.chat_status_bar);
        if (chat_status_bar != null) {
            chat_status_bar.setVisibility(View.VISIBLE);
        }

        chat_controllerLayout = (LinearLayout) findViewById(R.id.chat_controllerLayout);


        button_prev = (ImageButton) findViewById(R.id.button_prev);
        button_prev.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    button_prev.setScaleX(0.5f);
                    button_prev.setScaleY(0.5f);
                    button_prev.setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    button_prev.setScaleX(1.0f);
                    button_prev.setScaleY(1.0f);
                    button_prev.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);

                    ChatMainActivity.isChat = true;
                    MainActivity.isMainActivity = false;

                    CommonStaticData.passwordVerifyFlag = false;
                    CommonStaticData.ageLimitFlag = false;

                    channelChangeStartView(false);
                    // live add
                    if (chat_ll_scramble_msg.getVisibility() == View.VISIBLE) {
                        chat_ll_scramble_msg.setVisibility(View.INVISIBLE);
                    }
                    //
                    chat_ll_age_limit.setVisibility(View.INVISIBLE);
                    TVBridge.AVStartMinus();

                    initViews();
                    initFirebaseDatabase();
                    updateProfile();
                }
                return false;
            }
        });

        button_next = (ImageButton) findViewById(R.id.button_next);
        button_next.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    button_next.setScaleX(0.5f);
                    button_next.setScaleY(0.5f);
                    button_next.setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    button_next.setScaleX(1.0f);
                    button_next.setScaleY(1.0f);
                    button_next.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);

                    ChatMainActivity.isChat = true;
                    MainActivity.isMainActivity = false;

                    CommonStaticData.passwordVerifyFlag = false;
                    CommonStaticData.ageLimitFlag = false;

                    channelChangeStartView(false);
                    chat_changeChannelView.setVisibility(View.VISIBLE);
                    // live add
                    if (chat_ll_scramble_msg.getVisibility() == View.VISIBLE) {
                        chat_ll_scramble_msg.setVisibility(View.INVISIBLE);
                    }
                    chat_ll_age_limit.setVisibility(View.INVISIBLE);
                    //
                    TVBridge.AVStartPlus();

                    initViews();
                    initFirebaseDatabase();
                    updateProfile();
                }
                return false;
            }
        });

        button_max = (ImageButton) findViewById(R.id.button_max);
        button_max.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    button_max.setScaleX(0.5f);
                    button_max.setScaleY(0.5f);
                    button_max.setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    button_max.setScaleX(1.0f);
                    button_max.setScaleY(1.0f);
                    button_max.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);

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
                    //
                    editor.commit();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                return false;
            }
        });

        postEvent(TVEVENT.E_HIDE_CHAT_CONTROLER, CONTROLLER_HIDE_TIME);

        chat_room_title = (TextView) findViewById(R.id.chat_room_title);

        initViews();
        initFirebaseDatabase();
        initFirebaseAuth();
        initValues();

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
            mCahtListView.setVisibility(View.VISIBLE);
            mNetworkMsg.setVisibility(View.GONE);
            /*mChatAdapter = new ChatAdapter(this, 0);
            mCahtListView.setAdapter(mChatAdapter);*/
        } else {
            mCahtListView.setVisibility(View.GONE);
            mNetworkMsg.setVisibility(View.VISIBLE);
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
        mCursor = MainActivity.getCursor();
        String channelName = mCursor.getString(CommonStaticData.COLUMN_INDEX_SERVICE_NAME);
        String[] split_channelName = channelName.split(" ");
        String str = "";
        for (int i = 1; i < split_channelName.length; i++) {
            str += split_channelName[i];
            if (i < split_channelName.length - 1) {
                str += " ";
            }
        }

        mDatabaseReference = mFirebaseDatabase.getReference("("+getApplicationContext().getResources().getString(R.string.app_name)+") "+str);

        //chat_room_title.setText(getApplicationContext().getString(R.string.chat_room_name)+" "+str);
        chat_room_title.setText(getApplicationContext().getString(R.string.chat_room_name)+" ("+getApplicationContext().getResources().getString(R.string.app_name)+") "+str);

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
            mBtnGoogleSignIn.setVisibility(View.VISIBLE);
            mBtnGoogleSignOut.setVisibility(GONE);
            mTxtProfileInfo.setVisibility(GONE);
            mImgProfile.setVisibility(GONE);

            //submitButton.setVisibility(View.GONE);
            rootView.setVisibility(View.GONE);
            tv_message.setVisibility(View.VISIBLE);

            mChatAdapter.setEmail(null);
            mChatAdapter.notifyDataSetChanged();
        } else {
            // login state
            mBtnGoogleSignIn.setVisibility(GONE);
            mBtnGoogleSignOut.setVisibility(View.VISIBLE);
            mTxtProfileInfo.setVisibility(View.VISIBLE);
            mImgProfile.setVisibility(View.VISIBLE);

            //submitButton.setVisibility(View.VISIBLE);
            rootView.setVisibility(View.VISIBLE);
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

        MainActivity.isMainActivity = false;
        ChatMainActivity.isChat = true;

        if((chat_curr_rate >= (CommonStaticData.PG_Rate+1) && (CommonStaticData.PG_Rate!=0))
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

        //isChat = false;

        removeEvent(TVEVENT.E_HIDE_CHAT_CONTROLER);

        chat_controllerLayout.setVisibility(View.INVISIBLE);
        chat_status_bar.setVisibility(View.INVISIBLE);

        super.onPause();
    }

    @Override
    protected void onDestroy() {

        TVlog.i(TAG, "== onDestroy ==");

        ChatMainActivity.isChat = false;

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
        mDatabaseReference.removeEventListener(mChildEventListener);
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

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        TVlog.i(TAG, " ===== ChatWindow surfaceCreated =====");

        MainActivity.getInstance().onStart_TV();

        if (sv_chatView != null) {
            FCI_TVi.setSuface(holder.getSurface());
        }

        MainActivity.isMainActivity = false;
        ChatMainActivity.isChat = true;
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
        if(frameWidthChat == 0 || frameHeightChat == 0) {
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

        if (!CommonStaticData.returnMainFromChat) {
            MainActivity.getInstance().SolutionStop();
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
        //MainActivity.getInstance().sendEvent(TVEVENT.E_FIRSTVIDEO);
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
                if(isRunningInForeground())
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
                            if(CommonStaticData.settingActivityShow == true){
                                SettingActivity.getInstance().onBackPressed();
                            }else if(CommonStaticData.epgActivityShow == true){
                                EPGActivity.getInstance().onBackPressed();
                            }else if(CommonStaticData.recordedFileActivityShow == true){
                                RecordedFileListActivity.getInstance().onBackPressed();
                            }else if(CommonStaticData.playBackActivityShow == true){
                                //PlayBackActivity.getInstance().onBackPressed();
                                PlayBackActivity.getInstance().closeActivity();
                            }else if(CommonStaticData.channelMainActivityShow == true){
                                ChannelMainActivity.getInstance().onBackPressed();
                            }else if(CommonStaticData.openActivityShow == true){
                                OpenActivity.getInstance().onBackPressed();
                            }else if(CommonStaticData.aboutActivityShow == true){
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
                            ChatMainActivity.getInstance().mNetworkMsg.setVisibility(View.VISIBLE);
                        } else {
                            //ChatMainActivity.getInstance().updateProfile();
                            ChatMainActivity.getInstance().mCahtListView.setVisibility(View.VISIBLE);
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

    private void channelChangeStartView(boolean _cas)
    {
        if (chat_programNotMsg.getVisibility() == View.VISIBLE) {
            chat_noSignal.setVisibility(View.INVISIBLE);
            chat_programNotMsg.setVisibility(View.INVISIBLE);
        }
        if (chat_ll_scramble_msg.getVisibility() == View.VISIBLE) {
            chat_ll_scramble_msg.setVisibility(View.INVISIBLE);
        }

        chat_changeChannelView.setVisibility(View.VISIBLE);
        chat_channelChangeBG.setVisibility(View.VISIBLE);
        chat_progressingChange.setVisibility(View.VISIBLE);
        if(_cas == false) {
            chat_loadingChannel.setVisibility(View.VISIBLE);
        }
        else{   // call from playback
            chat_loadingChannel.setVisibility(GONE);
        }

        /*if (chat_ll_scramble_msg.getVisibility() == View.VISIBLE) {
            chat_ll_scramble_msg.setVisibility(View.INVISIBLE);
        }*/
    }

    private void channelChangeEndView(boolean _keepBG)
    {
        if(_keepBG ==false)
        {
            chat_channelChangeBG.setVisibility(View.INVISIBLE);
        }
        chat_changeChannelView.setVisibility(View.INVISIBLE);
    }

    private void hideChatController() {
        //if(chat_controllerLayout.isShown()) {


        TVlog.i(TAG, "== hideController ==");
        /*uiOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);*/

        chat_status_bar.setVisibility(View.INVISIBLE);
        chat_controllerLayout.setVisibility(View.INVISIBLE);

        //}
    }

    private void showChatController(){

        chat_status_bar.setVisibility(View.VISIBLE);
        chat_controllerLayout.setVisibility(View.VISIBLE);

        /*if(buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 2 || buildOption.GUI_STYLE == 3) {
            chat_status_bar.setVisibility(View.VISIBLE);
        }*/
        postEvent(TVEVENT.E_HIDE_CHAT_CONTROLER, CONTROLLER_HIDE_TIME);

    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_UP:
                if (chat_controllerLayout.getVisibility() == View.VISIBLE) {
                    hideChatController();
                } else {
                    showChatController();
                }
                return true;

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
        if(buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
            if (svSub_chatView !=null) {
                if(_onoff) {
                    TVlog.i(TAG, "= Sub Chat surface visible = ");
                    svSub_chatView.setVisibility(View.VISIBLE);
                } else {
                    TVlog.i(TAG, "= Sub Chat surface invisible = ");
                    svSub_chatView.setVisibility(View.INVISIBLE);
                }
            }

        } else {
            TVlog.e(TAG, "No AUTODETECT , but setChatSubSurfaceVisible call Error ");
        }
    }

}
