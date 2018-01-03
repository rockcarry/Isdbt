package kr.co.fci.tv.recording;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fci.tv.FCI_TV;

import java.util.List;

import kr.co.fci.tv.MainActivity;
import kr.co.fci.tv.R;
import kr.co.fci.tv.TVEVENT;
import kr.co.fci.tv.buildOption;
import kr.co.fci.tv.gesture.VerticalSeekBar;
import kr.co.fci.tv.saves.CommonStaticData;
import kr.co.fci.tv.tvSolution.FCI_TVi;
import kr.co.fci.tv.tvSolution.TVBridge;
import kr.co.fci.tv.util.CustomToast;
import kr.co.fci.tv.util.TVlog;

/**
 * Created by sungho.kim on 2016-03-16.
 */
public class PlayBackActivity extends Activity {
    private static String TAG = "Playback ";

    private static int STATE_PLAY =0;
    private static int STATE_PAUSE =1;
    private static int STATE_IDLE =2;

    private int currentState = STATE_IDLE;

    ImageButton resume;
    ImageButton pause;
    ImageButton undo;
    ImageButton nextlist;
    ImageButton prelist;
    SeekBar playbackseekbar;
    TextView start;
    TextView stop;

    LinearLayout ll_paused;
    ProgressBar progressingChange;

    private static int currentindex;
    private static long duration;
    private static int curOffset =0;
    private static int OffsetUint=0;
    private static int currentSeekbar=0;

    static public boolean isPlaying = false;  // TS playback core


    private final static int PLAYBACK_HIDE_TIME = 5000;
    public GestureDetector mGestureDetector;

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
    ////////////////////////////////////

    /////// justin test
    private int uiOptions;
    ////////
    private final static int CONTROLLER_HIDE_TIME = 7000;
    private final static int GESTURE_HIDE_TIME = 3000;  // live add



    List<RecordedFile> recordedFiles;
    RecordedFile currentRecordFile;
    String sendFilename;
    String nextFilename;
    String preFilename;
    String playFile;

    boolean isFirstI=false;

    LinearLayout controlplayback;
    LinearLayout ll_playbacktitle;
    TextView playbacktitle;

    boolean cotrollerToggle =false;

    static boolean seekTouch =false;
    static boolean pauseUIOn =true;

    public static PlayBackActivity instance;
    public static PlayBackActivity getInstance()
    {
        return instance;
    }

    public Handler PlaybackHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            TVEVENT event = TVEVENT.values()[msg.what];
            switch(event)
            {
                case E_TSPLAYBACK_START:
                {
                    MainActivity.getInstance().sendEvent(TVEVENT.E_HIDE_CONTROLER);
                    int offset = (int)msg.arg1;
                    String recordedFile = (String)msg.obj;
                    TVlog.i(TAG, " E_TSPLAYBACK_START =  " + offset + " isPlaying = " + isPlaying );
                    duration =(long) FCI_TVi.TSPlayBackGetDuration(recordedFile);
                    TVlog.i(TAG, " startPlayback  duration = "+duration);
                    FCI_TVi.startPlayback(recordedFile);
                    playFile = recordedFile;
                    isPlaying = true;
                    setState(STATE_PLAY);
                    isFirstI=false;
                }
                break;

                case E_TSPLAYBACK_SEEK:
                {
                    int offset = (int)msg.arg1;
                    TVlog.i(TAG, " E_TSPLAYBACK_SEEK =  " + offset);
                    FCI_TVi.seekPlayback(offset);
                    isPlaying = true;
                    setState(STATE_PLAY);
                    isFirstI=false;
                }
                break;

                case E_TSPLAYBACK_STOP:
                {
                    removeEvent(TVEVENT.E_TS_PLAYBACK_CURRENT_POS);
                    TVlog.i(TAG, " E_TSPLAYBACK_STOP ");
                    isPlaying = false;
                    FCI_TVi.stopPlayback();
                    isFirstI=false;
                }
                break;

                case E_TSPLAYBACK_PAUSE:
                {
                    removeEvent(TVEVENT.E_TS_PLAYBACK_CURRENT_POS);
                    TVlog.i(TAG, " E_TSPLAYBACK_PAUSE ");
                    FCI_TVi.pausePlayback();
                    setState(STATE_PAUSE);

                }
                break;

                case  E_TSPLAYBACK_RESUME:
                {
                    removeEvent(TVEVENT.E_TS_PLAYBACK_CURRENT_POS);
                    TVlog.i(TAG, " E_TSPLAYBACK_RESUME ");
                    FCI_TVi.resumePlayback();
                    setState(STATE_PLAY);
                    postEvent(TVEVENT.E_TS_PLAYBACK_CURRENT_POS, 100);
                }
                break;

                case E_HIDE_PLAYBACK :
                {
                    TVlog.i(TAG, " hideplayback ");
                    ViewController(false);
                }
                break;


                case E_TS_PLAYBACK_CURRENT_POS:
                {
                    boolean isTVOn = MainActivity.getTVon();
                    if (isPlaying && isTVOn) {
                        long currentPro = (long)FCI_TVi.currentPlayback();
                        //TVlog.i(TAG, "currentPro = "+currentPro+ "  duration = "+duration+ " OffsetUint = "+OffsetUint );
                        if (currentPro <= 0 || currentPro > OffsetUint) {
                            TVlog.i(TAG, "not ready start  =  " +currentPro);
                            postEvent(TVEVENT.E_TS_PLAYBACK_CURRENT_POS, 100);
                            return;
                        }
                        if (currentPro == 0) {
                            TVlog.i(TAG, "currentPro = 0 ");
                            currentPro=1;
                        }
                        curOffset=(int)currentPro;
                        DisplayDuration(currentPro,true);
                        postEvent(TVEVENT.E_TS_PLAYBACK_CURRENT_POS, 100);
                        if (curOffset == playbackseekbar.getMax()) {
                            TVlog.i(TAG, "MAX progress = "+curOffset);
                        }
                    }
                }
                break;

                case E_TSPLAYBACK_TERMINATE:
                {
                    MainActivity.getInstance().sendEvent(TVEVENT.E_HIDE_CONTROLER);
                    removeEvent(TVEVENT.E_TS_PLAYBACK_CURRENT_POS);
                    setState(STATE_IDLE);

                    TVlog.i(TAG, " E_TSPLAYBACK_TERMINATE  = ");
                    removeEvent(TVEVENT.E_AUTO_CHANGE_CHANNEL_TEST);
                    isPlaying = false;
                    duration  = 0;
                    curOffset = 0;
                    isFirstI  = false;
                    pauseUIOn = true;
                    ViewPause(true);
                    FCI_TVi.stopPlayback();
                    currentSeekbar = 0;
                    finish();

                    Intent myIntent = new Intent(PlayBackActivity.this, RecordedFileListActivity.class);
                    startActivity(myIntent);

                    MainActivity.isPlayBackActivity = false;
                    MainActivity.isMainActivity = true;

                    if (CommonStaticData.scanCHnum > 0) {
                        MainActivity.getInstance().changeChannelView.setVisibility(View.VISIBLE);
                        MainActivity.getInstance().channelChangeStartView(false);
                        TVBridge.serviceID_start(CommonStaticData.lastCH);
                        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                            if (MainActivity.ll_file_play_mode_usb.getVisibility() == View.VISIBLE) {
                                MainActivity.ll_file_play_mode_usb.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            if (MainActivity.ll_file_play_mode.getVisibility() == View.VISIBLE) {
                                MainActivity.ll_file_play_mode.setVisibility(View.INVISIBLE);
                            }
                        }

                    } else {
                        if (MainActivity.getInstance().sv != null && MainActivity.getInstance().sv.isShown()) {
                            MainActivity.getInstance().sv.setBackgroundColor(getResources().getColor(R.color.black));
                        }
                        if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                            if (MainActivity.getInstance().svSub != null && MainActivity.getInstance().svSub.isShown()) {
                                MainActivity.getInstance().svSub.setBackgroundColor(getResources().getColor(R.color.black));
                            }
                        } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                                (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                            if (MainActivity.getInstance().svSub != null && MainActivity.getInstance().svSub.isShown()) {
                                MainActivity.getInstance().svSub.setBackgroundColor(getResources().getColor(R.color.black));
                            }
                        }
                        if (FCI_TVi.initiatedSol) {
                            MainActivity.getInstance().ll_noChannel.setVisibility(View.VISIBLE);
                            if (MainActivity.getInstance().currChNo != null && MainActivity.getInstance().currCH != null && MainActivity.getInstance().currRemoteNo != null) {
                                MainActivity.getInstance().currChNo.setText("- -ch");
                                MainActivity.getInstance().currRemoteNo.setText("- - -");
                                MainActivity.getInstance().currCH.setText(R.string.no_channel_title);
                            }

                            if (MainActivity.getInstance().rl_ChType != null) {
                                MainActivity.getInstance().rl_ChType.setVisibility(View.GONE);
                            }
                            MainActivity.getInstance().ll_noSignal.setVisibility(View.INVISIBLE);

                            if (MainActivity.getInstance().ll_scramble_msg.getVisibility() == View.VISIBLE) {
                                MainActivity.getInstance().ll_scramble_msg.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            MainActivity.getInstance().ll_noChannel.setVisibility(View.INVISIBLE);
                            MainActivity.getInstance().ll_noSignal.setVisibility(View.INVISIBLE);
                            if (MainActivity.getInstance().ll_scramble_msg.getVisibility() == View.VISIBLE) {
                                MainActivity.getInstance().ll_scramble_msg.setVisibility(View.INVISIBLE);
                            }
                            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                    buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB ||
                                    buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB ||
                                    buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                                MainActivity.ll_file_play_mode_usb.setVisibility(View.VISIBLE);
                            } else {
                                MainActivity.ll_file_play_mode.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
                break;

                case E_TSPLAYBACK_FIRSTVIDEO:
                {
                    TVlog.i(TAG, " ------ E_TSPLAYBACK_FIRSTVIDEO  ---------- ");
                    isFirstI=true;
                    if (isPlaying == true) {
                        postEvent(TVEVENT.E_TS_PLAYBACK_CURRENT_POS, 100);
                        ViewSeekBar(true);
                    }
                }
                break;

                case E_TSPLAYBACK_FIRSTAUDIO:
                {
                    TVlog.i(TAG, " ------ E_TSPLAYBACK_FIRSTAUDIO  ---------- ");
                    isFirstI=true;
                    if (isPlaying == true) {
                        postEvent(TVEVENT.E_TS_PLAYBACK_CURRENT_POS, 100);
                        ViewSeekBar(true);
                    }
                }
                break;

                case E_TSPLAYBACK_ERROR:
                {
                    removeEvent(TVEVENT.E_TS_PLAYBACK_CURRENT_POS);
                    removeEvent(TVEVENT.E_TSPLAYBACK_ERROR);
                    int errorNum = (int)msg.arg1;
                    String errorMsg = null;
                    TVlog.i(TAG, " ------ E_TSPLAYBACK_ERROR  ----------: "+errorNum);
                    CustomToast toast = new CustomToast(getApplicationContext());
                    switch (errorNum) {
                        case FCI_TV.SOL_ERR_PLAYBACK_FILE_INVALID_NOT_188:
                        case FCI_TV.SOL_ERR_PLAYBACK_FILE_NOT_TS_FORMAT:
                            errorMsg = getApplicationContext().getString(R.string.playbackNotSupportedFormat);
                            break;
                        case FCI_TV.SOL_ERR_FILE_OPEN:
                        case FCI_TV.SOL_ERR_FILE_READ:
                            errorMsg = getApplicationContext().getString(R.string.playbackFileOpError);
                            break;
                        default:
                            errorMsg = getApplicationContext().getString(R.string.playbackCommonError);
                            break;
                    }
                    if (errorMsg != null) {
                        toast.showToast(getApplicationContext(), errorMsg + "[" + errorNum + "]", Toast.LENGTH_LONG);
                    }
                }
                break;

                case E_HIDE_GESTURE :
                    volumebarLayout.setVisibility(View.INVISIBLE);
                    brightbarLayout.setVisibility(View.INVISIBLE);
                    mBrightnessChanged = false;
                    mAudio = false;
                    break;

            }
            super.handleMessage(msg);
        }};

    public void sendEvent(TVEVENT _Event, int _arg1, Object _obj) {
        int m;
        m = _Event.ordinal();
        Message msg = PlaybackHandler.obtainMessage(m);
        msg.arg1 = _arg1;
        msg.obj = _obj;
        PlaybackHandler.sendMessage(msg);
    }

    public void postEvent(TVEVENT _Event,int _time )
    {
        int m;
        m = _Event.ordinal();
        Message msg = PlaybackHandler.obtainMessage(m);
        PlaybackHandler.sendEmptyMessageDelayed(m, _time);
    }

    public void sendEvent(TVEVENT _Event) {
        int m;
        m = _Event.ordinal();
        Message msg = PlaybackHandler.obtainMessage(m);
        PlaybackHandler.sendMessage(msg);
    }
    public void removeEvent(TVEVENT _Event)
    {
        int m;
        m = _Event.ordinal();
        Message msg = PlaybackHandler.obtainMessage(m);
        PlaybackHandler.removeMessages(m);
    }

    private void setState(int _state)
    {
        currentState = _state;
        TVlog.i(TAG, " setState = " + currentState);
    }

    public  void closeActivity(){
        CommonStaticData.playBackActivityShow = false;   // justin add for dongle detached
        finish();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        CommonStaticData.playBackActivityShow = false;   // justin add for dongle detached
        TVlog.i(TAG, " onBackPressed");
        sendEvent(TVEVENT.E_TSPLAYBACK_TERMINATE);
    }

    @Override
    protected void onPause() {

        TVlog.i(TAG, " =onPause  curOffset =  " +curOffset +" State = "  + currentState);

        CommonStaticData.playBackActivityShow = false;   // justin add for dongle detached

        if (pause.getVisibility() != View.VISIBLE) {
            pauseUIOn = false;
        } else {
            pauseUIOn = true;
        }
        isPlaying=false;
        removeEvent(TVEVENT.E_TS_PLAYBACK_CURRENT_POS);
        super.onPause();
    }



    public void Start() {
        TVlog.i(TAG, "RealStart  curOffset  = " + curOffset + " isPlaying = "+ isPlaying + " Current state = " +currentState);

        recordedFiles = RecordedFileListActivity.getInstance().getRecordFileList();
        currentRecordFile = recordedFiles.get(currentindex);
        sendFilename = currentRecordFile.getFilePath() + currentRecordFile.getFileName();
        if (curOffset == 0) {
            sendEvent(TVEVENT.E_TSPLAYBACK_START, 0, sendFilename);
        } else {
            sendEvent(TVEVENT.E_TSPLAYBACK_SEEK, curOffset, sendFilename);
        }

        if (pause.getVisibility() != View.VISIBLE) {
            TVlog.i(TAG, "now pause");
            sendEvent(TVEVENT.E_TSPLAYBACK_PAUSE, curOffset, sendFilename);
        }

        // live add  // eddy move to up .
        if (currentState == STATE_PAUSE) {
            currentSeekbar= playbackseekbar.getProgress();
            DisplayDuration(currentSeekbar, true);
            MainActivity.getInstance().changeChannelView.setVisibility(View.INVISIBLE);

            // live add
            if (MainActivity.getInstance().sv != null && MainActivity.getInstance().sv.isShown()) {
                MainActivity.getInstance().sv.setBackgroundColor(getResources().getColor(R.color.transparent));
            }
            if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                if (MainActivity.getInstance().svSub != null && MainActivity.getInstance().svSub.isShown()) {
                    MainActivity.getInstance().svSub.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
            } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                    (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                            buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                            buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                if (MainActivity.getInstance().svSub != null && MainActivity.getInstance().svSub.isShown()) {
                    MainActivity.getInstance().svSub.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
            }

            if (MainActivity.getInstance().ll_noChannel.getVisibility() == View.VISIBLE) {
                MainActivity.getInstance().ll_noChannel.setVisibility(View.INVISIBLE);
            }
            if (MainActivity.getInstance().ll_noSignal.getVisibility() == View.VISIBLE) {
                MainActivity.getInstance().ll_noSignal.setVisibility(View.INVISIBLE);
            }
            if (MainActivity.getInstance().ll_scramble_msg.getVisibility() == View.VISIBLE) {
                MainActivity.getInstance().ll_scramble_msg.setVisibility(View.INVISIBLE);
            }

            if (FCI_TVi.initiatedSol) {
            } else {
                if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                    if (MainActivity.getInstance().ll_file_play_mode_usb.getVisibility() == View.VISIBLE) {
                        MainActivity.getInstance().ll_file_play_mode_usb.setVisibility(View.INVISIBLE);
                    }
                } else {
                    if (MainActivity.getInstance().ll_file_play_mode.getVisibility() == View.VISIBLE) {
                        MainActivity.getInstance().ll_file_play_mode.setVisibility(View.INVISIBLE);
                    }
                }
            }

            ll_paused.setVisibility(View.VISIBLE);

            //controlplayback.setVisibility(View.VISIBLE);
            //ll_playbacktitle.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {

        MainActivity.isMainActivity = false;
        MainActivity.isPlayBackActivity = true;
        super.onResume();

        CommonStaticData.playBackActivityShow = true;   // justin add for dongle detached

        if (FCI_TVi.initiatedSol) {
            if (MainActivity.getInstance().ll_noChannel.getVisibility() == View.VISIBLE) {
                MainActivity.getInstance().ll_noChannel.setVisibility(View.INVISIBLE);
            }
            if (MainActivity.getInstance().ll_noSignal.getVisibility() == View.VISIBLE) {
                MainActivity.getInstance().ll_noSignal.setVisibility(View.INVISIBLE);
            }

            if (MainActivity.getInstance().ll_scramble_msg.getVisibility() == View.VISIBLE) {
                MainActivity.getInstance().ll_scramble_msg.setVisibility(View.INVISIBLE);
            }
        } else {
            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                    buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB ||
                    buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB ||
                    buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                if (MainActivity.getInstance().ll_file_play_mode_usb.getVisibility() == View.VISIBLE) {
                    MainActivity.getInstance().ll_file_play_mode_usb.setVisibility(View.INVISIBLE);
                }
            } else {
                if (MainActivity.getInstance().ll_file_play_mode.getVisibility() == View.VISIBLE) {
                    MainActivity.getInstance().ll_file_play_mode.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        int player_bright = CommonStaticData.brightness;
        //WindowManager.LayoutParams lp = mWindow.getAttributes();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = player_bright;
        getWindow().setAttributes(lp);

        instance = this;
        MainActivity.isPlayBackActivity = true;

        CommonStaticData.playBackActivityShow = true;   // justin add for dongle detached

        if (MainActivity.getInstance().ll_noChannel.getVisibility() == View.VISIBLE) {
            MainActivity.getInstance().ll_noChannel.setVisibility(View.INVISIBLE);
        }

        if (MainActivity.getInstance().ll_noSignal.getVisibility() == View.VISIBLE) {
            MainActivity.getInstance().ll_noSignal.setVisibility(View.INVISIBLE);
        }

        if (MainActivity.getInstance().ll_scramble_msg.getVisibility() == View.VISIBLE) {
            MainActivity.getInstance().ll_scramble_msg.setVisibility(View.INVISIBLE);
        }

        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
            if (MainActivity.getInstance().ll_file_play_mode_usb.getVisibility() == View.VISIBLE) {
                MainActivity.getInstance().ll_file_play_mode_usb.setVisibility(View.INVISIBLE);
            }
        } else {
            if (MainActivity.getInstance().ll_file_play_mode.getVisibility() == View.VISIBLE) {
                MainActivity.getInstance().ll_file_play_mode.setVisibility(View.INVISIBLE);
            }
        }

        setState(STATE_IDLE);
        setContentView(R.layout.playback_layout);
        TVlog.i(TAG, "  = onCreate  =  curOffset = "+ curOffset +" duration = "+duration);


        Intent intent = getIntent();
        int recordedindex = intent.getExtras().getInt("fileindex");
        TVlog.i(TAG, "fileindex = " + recordedindex);
        currentindex = recordedindex ;

        recordedFiles= RecordedFileListActivity.getInstance().getRecordFileList();
        currentRecordFile = recordedFiles.get(currentindex);
        sendFilename = currentRecordFile.getFilePath() + currentRecordFile.getFileName();

        ll_paused = (LinearLayout) findViewById(R.id.ll_paused);
        progressingChange = (ProgressBar)findViewById(R.id.progressing_file);
        progressingChange.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
        ll_paused.setVisibility(View.INVISIBLE);

        ll_playbacktitle = (LinearLayout) findViewById(R.id.ll_playbacktitle);

        playbacktitle = (TextView) findViewById(R.id.recordedtitle);
        playbacktitle.setText(currentRecordFile.getFileName());

        pause = (ImageButton)findViewById(R.id.playbackpause) ;
        resume = (ImageButton)findViewById(R.id.playbackresume) ;
        controlplayback = (LinearLayout) findViewById(R.id.playbackcontrol);
        undo = (ImageButton)findViewById(R.id.backofplayback) ;
        nextlist = (ImageButton)findViewById(R.id.nextlist) ;
        prelist = (ImageButton)findViewById(R.id.prelist) ;
        playbackseekbar = (SeekBar) findViewById(R.id.playbackseekbar);
        start = (TextView)findViewById(R.id.starttime);
        stop = (TextView)findViewById(R.id.stoptime);
        // eddy_TSRec
        String displayinit =String.format("%02d:%02d:%02d", 0,0,0);
        start.setText(displayinit);
        stop.setText(displayinit);

        if (OffsetUint == 0) {
            OffsetUint = FCI_TVi.TSPlayerBackGetOffSetUnit();
        }
        TVlog.i(TAG, " OffsetUint = " + OffsetUint);

        playbackseekbar.setMax(OffsetUint);

        nextlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int maxsize = recordedFiles.size();
                sendEvent(TVEVENT.E_TSPLAYBACK_STOP);
                ViewSeekBar(false);
                TVlog.i(TAG, "Next ++ maxsize  =" + maxsize);
                playbackseekbar.setProgress(0);

                if (ll_paused.getVisibility() == View.VISIBLE) {
                    ll_paused.setVisibility(View.INVISIBLE);
                }

                MainActivity.getInstance().channelChangeStartView(true);  // justin
                MainActivity.getInstance().changeChannelView.setVisibility(View.VISIBLE);
                currentindex++;
                if (currentindex >= maxsize) {
                    currentindex = 0;
                }
                currentRecordFile = recordedFiles.get(currentindex);
                nextFilename = currentRecordFile.getFilePath() + currentRecordFile.getFileName();
                playbacktitle.setText(currentRecordFile.getFileName());
                sendEvent(TVEVENT.E_TSPLAYBACK_START, 0, nextFilename);

                isPlaying = true;
                ViewPause(true);
            }
        });

        prelist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int maxsize = recordedFiles.size();
                sendEvent(TVEVENT.E_TSPLAYBACK_STOP);
                playbackseekbar.setProgress(0);
                ViewSeekBar(false);

                if (ll_paused.getVisibility() == View.VISIBLE) {
                    ll_paused.setVisibility(View.INVISIBLE);
                }

                //MainActivity.getInstance().channelChangeStartView(false);
                MainActivity.getInstance().channelChangeStartView(true);    // justin
                MainActivity.getInstance().changeChannelView.setVisibility(View.VISIBLE);
                TVlog.i(TAG, "Next -- maxsize = " + maxsize);
                currentindex--;
                if (currentindex == 0) {
                    currentindex = 0 ;
                } else if (currentindex < 0) {
                    currentindex = maxsize -1;
                }
                currentRecordFile = recordedFiles.get(currentindex);
                preFilename = currentRecordFile.getFilePath() + currentRecordFile.getFileName();
                playbacktitle.setText(currentRecordFile.getFileName());
                sendEvent(TVEVENT.E_TSPLAYBACK_START, 0, preFilename);

                isPlaying = true;
                ViewPause(true);

            }
        });

        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TVlog.i(TAG, "== undo = " );
                CommonStaticData.playBackActivityShow = false;   // justin add for dongle detached
                MainActivity.getInstance().sendEvent(TVEVENT.E_HIDE_CONTROLER);
                sendEvent(TVEVENT.E_TSPLAYBACK_TERMINATE);
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // isPlaying = false;
                TVlog.i(TAG, "== pause = " );
                sendEvent(TVEVENT.E_TSPLAYBACK_PAUSE);
                ViewPause(false);
                removeEvent(TVEVENT.E_HIDE_PLAYBACK);  // live add
            }
        });

        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // isPlaying = true;
                // int currentSetP = playbackseekbar.getProgress();
                TVlog.i(TAG, "== Press Resume== " + currentSeekbar + " curOffset = "+curOffset);
                if (isPlaying) {
                    TVlog.i(TAG, "core is playing " +currentSeekbar );
                    if (currentSeekbar!=curOffset)
                    {
                        TVlog.i(TAG, " Resume with moved progress " );
                        //curOffset=currentSeekbar;
                        currentSeekbar=curOffset;
                        sendEvent(TVEVENT.E_TSPLAYBACK_SEEK, curOffset, playFile);
                    } else {
                        TVlog.i(TAG, " Resume with same progress " );
                        sendEvent(TVEVENT.E_TSPLAYBACK_RESUME);
                    }
                } else {
                    TVlog.i(TAG, "core is not playing " +currentSeekbar );
                    sendEvent(TVEVENT.E_TSPLAYBACK_SEEK, curOffset, playFile);
                }
                ViewPause(true);
                postEvent(TVEVENT.E_HIDE_PLAYBACK, PLAYBACK_HIDE_TIME);
                if (ll_paused.getVisibility() == View.VISIBLE) {
                    ll_paused.setVisibility(View.INVISIBLE);
                }
            }
        });

        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener()) {
            @Override
            public boolean onTouchEvent(MotionEvent ev) {
                return super.onTouchEvent(ev);
            }
        };


        playbackseekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (seekTouch) {
                    DisplayDuration(progress, false);
                } else {
                    if (progress == playbackseekbar.getMax()) {
                        TVlog.i(TAG, "progress max will be terminated");
                        sendEvent(TVEVENT.E_TSPLAYBACK_TERMINATE);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                seekTouch = true;
                TVlog.i(TAG, "onStartTrackingTouch");
                sendEvent(TVEVENT.E_TSPLAYBACK_PAUSE);
                removeEvent(TVEVENT.E_HIDE_PLAYBACK);

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                currentSeekbar= playbackseekbar.getProgress();
                if (pause.getVisibility() == View.VISIBLE) {
                    TVlog.i(TAG, "current playing ~~ seek start");
                    sendEvent(TVEVENT.E_TSPLAYBACK_SEEK, currentSeekbar, playFile);
                    postEvent(TVEVENT.E_HIDE_PLAYBACK, PLAYBACK_HIDE_TIME);

                }
                seekTouch = false;
                // curOffset = (int) currentPro;
                DisplayDuration(currentSeekbar, false);
                TVlog.i(TAG, "onStopTrackingTouch  curOffset = "+curOffset);
            }
        });

        //////////// Gesture seekbar
        mVerticalVolumeProgress = (VerticalSeekBar) findViewById(R.id.seekbar_vol);
        mVerticalBrightProgress = (VerticalSeekBar) findViewById(R.id.seekbar_bri);

        volumebarLayout = (RelativeLayout) findViewById(R.id.volumebar);
        volumebarLayout.setVisibility(View.INVISIBLE);
        brightbarLayout = (RelativeLayout) findViewById(R.id.brightbar);
        brightbarLayout.setVisibility(View.INVISIBLE);

        //  CommonStaticData.brightness = CommonStaticData.settings.getInt(CommonStaticData.brightnessKey,15);
        player_bright = CommonStaticData.brightness;
        if (player_bright < 1) {
            player_bright =1;
        } else if (player_bright > 15) {
            player_bright = 15;
        }
        WindowManager.LayoutParams plp = this.getWindow().getAttributes();
        plp.screenBrightness = (float) player_bright / 15;
        this.getWindow().setAttributes(plp);

        mVerticalBrightProgress.setMax(15);
        mVerticalBrightProgress.setProgress(player_bright);

        if (mVerticalBrightProgress != null) {
            mVerticalBrightProgress.setOnSeekBarChangeListener(new VerticalSeekBar.OnSeekBarChangeListener() {

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

        if (mVerticalVolumeProgress != null) {
            mVerticalVolumeProgress.setMax(max);
            mVerticalVolumeProgress.setProgress(vol);
            mVerticalVolumeProgress.setOnSeekBarChangeListener(new VerticalSeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(kr.co.fci.tv.gesture.VerticalSeekBar seekBar, int progress, boolean fromUser) {
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

        ViewController(false);
        if (pauseUIOn) {
            ViewPause(true);
        } else {
            ViewPause(false);
            ViewController(true);
        }
        Start();
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
        if (!result) {

            if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1) {

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        return true;
                    case MotionEvent.ACTION_UP:

                        if (cotrollerToggle==false)toggleController(true);
                        else {
                            toggleController(false);
                        }
                        return true;

                    default:
                        return super.onTouchEvent(event);
                }
            } else {
                DisplayMetrics screen = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(screen);
                if (mSurfaceYDisplayRange == 0)
                    mSurfaceYDisplayRange = Math.min(screen.widthPixels, screen.heightPixels);

                float x_changed = event.getRawX() - mTouchX;
                float y_changed = event.getRawY() - mTouchY;

                float coefy = Math.abs(y_changed / x_changed);
                float coefx = Math.abs(x_changed / y_changed);
                float xgesturesize = ((x_changed / screen.xdpi) * 2.54f);

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        TVlog.i(TAG, "=== MotionEvent.ACTION_DOWN *********= ");

                        mLastMotionX = event.getX();
                        mLastMotionY = event.getY();

                        mTouchY = event.getRawY();
                        mVol = CurrentVol();
                        //mIsAudioOrBrightnessChanged = false;

                        mTouchX = event.getRawX();
                        if (mAudio != true && mBrightnessChanged != true) {
                            /*
                            if (mIsTouchFlag == true) {
                                mIsTouchFlag = false;
                                sendEvent(TVEVENT.E_HIDE_CONTROLER);
                            } else {
                                removeEvent(TVEVENT.E_HIDE_CONTROLER);  // add justin
                                sendEvent(TVEVENT.E_SHOW_CONTROLER);
                            }*/
                            /*
                            uiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                            View decorView = getWindow().getDecorView();
                            decorView.setSystemUiVisibility(uiOptions);
                            */
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                        TVlog.i(TAG, "=== MotionEvent.ACTION_UP *********= ");
                        if (mEnableBrightnessGesture == true) {
                            mEnableBrightnessGesture = false;
                            postEvent(TVEVENT.E_HIDE_GESTURE, GESTURE_HIDE_TIME);
                            //InputDialog dig = new InputDialog(this, InputDialog.TYPE_RECOVER_FOCUS, null, null, null);  // make to get focus
                        }
                        mEnableChannellist = false;

                        if (pause.getVisibility() == View.VISIBLE) {
                            if (cotrollerToggle==false) {
                                toggleController(true);
                            } else {
                                toggleController(false);
                            }
                        }
                        return true;

                    default:
                        return super.onTouchEvent(event);

                    case MotionEvent.ACTION_MOVE:
                        //TVlog.i(TAG, "=== ACTION_MOVE ********* y=  " + coefy +", x = "+coefx);
                        final float x = event.getX();
                        final float y = event.getY();
                        final int deltaX = Math.abs((int) (mLastMotionX - x));
                        final int deltaY = Math.abs((int) (mLastMotionY - y));

                        //TVlog.i(TAG, "=== ACTION_MOVE ****deltaX=  " + deltaX+", deltaY = "+deltaY);
                        if (coefy > 4) {
                            mEnableBrightnessGesture = true;
                            removeEvent(TVEVENT.E_HIDE_GESTURE);

                            if (mEnableBrightnessGesture && (mTouchX < (screen.widthPixels / 2))) {       // left brightness
                                TVlog.i(TAG, "=== ACTION_MOVE Brightness *********  ");
                                //sendEvent(TVEVENT.E_HIDE_PLAYBACK);
                                volumebarLayout.setVisibility(View.INVISIBLE);
                                doBrightnessTouch(y_changed);
                            }

                            if (mEnableBrightnessGesture && (mTouchX > (screen.widthPixels / 2))) {       // right volume
                                TVlog.i(TAG, "=== ACTION_MOVE Volume *********  ");
                                //sendEvent(TVEVENT.E_HIDE_PLAYBACK);
                                brightbarLayout.setVisibility(View.INVISIBLE);
                                doVolumeTouch(y_changed);
                            }
                        }
                        return true;

                    case MotionEvent.ACTION_CANCEL:
                        TVlog.i(TAG, "=== MotionEvent.ACTION_CANCEL *********= ");
                        return false;

                    case MotionEvent.ACTION_OUTSIDE:
                        TVlog.i(TAG, "=== MotionEvent.ACTION_OUTSIDE *********= ");
                        return false;
                }
            }
        }
        return false;
    }

    private void DisplayDuration(long _currentPosition ,boolean _isSet)
    {
        long ProDura = _currentPosition * duration;
        long currentMiliSec = ProDura / OffsetUint;

        if (_isSet) playbackseekbar.setProgress((int)_currentPosition);
        String displayCurrent = String.format("%02d:%02d:%02d", ((currentMiliSec / (1000 * 60 * 60)) % 24), ((currentMiliSec / (1000 * 60)) % 60), ((currentMiliSec / 1000) % 60));
        String displayEnd = String.format("%02d:%02d:%02d", ((duration / (1000 * 60 * 60)) % 24), ((duration / (1000 * 60)) % 60), ((duration / 1000) % 60));
        start.setText(displayCurrent);
        stop.setText(displayEnd);
    }


    void toggleController(boolean _toggle)
    {
        cotrollerToggle=_toggle;
        removeEvent(TVEVENT.E_HIDE_PLAYBACK);
        if (cotrollerToggle == true) {
            ViewController(true);
            postEvent(TVEVENT.E_HIDE_PLAYBACK, PLAYBACK_HIDE_TIME);
        } else {
            ViewController(false);
        }
        if (isFirstI == false) {
            ViewSeekBar(false);
        } else {
            ViewSeekBar(true);
        }
    }

    private void ViewPause(boolean _isView)
    {
        if (pause==null || resume == null) {
            TVlog.e(TAG, "ViewPause no image ");
            return;
        }

        if (_isView == true) {
            pause.setVisibility(View.VISIBLE);
            resume.setVisibility(View.INVISIBLE);
        } else {
            pause.setVisibility(View.INVISIBLE);
            resume.setVisibility(View.VISIBLE);
        }
    }

    private void ViewSeekBar(boolean _isView)
    {
        if (playbackseekbar == null || start == null || stop == null) {
            TVlog.e(TAG, "ViewSeekBar no image ");
            return;
        }
        if (_isView == true) {
            playbackseekbar.setVisibility(View.VISIBLE);
            start.setVisibility(View.VISIBLE);
            stop.setVisibility(View.VISIBLE);
        } else {
            playbackseekbar.setVisibility(View.INVISIBLE);
            start.setVisibility(View.INVISIBLE);
            stop.setVisibility(View.INVISIBLE);
        }
    }

    private void ViewController(boolean _isView)
    {
        if (controlplayback==null || ll_playbacktitle == null) {
            TVlog.e(TAG, "ViewController no image ");
            return;
        }
        if (_isView == true ) {
            controlplayback.setVisibility(View.VISIBLE);
            ll_playbacktitle.setVisibility(View.VISIBLE);
        } else {
            controlplayback.setVisibility(View.INVISIBLE);
            ll_playbacktitle.setVisibility(View.INVISIBLE);
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

    @Override
    protected void onDestroy() {
        if (!FCI_TVi.initiatedSol) {
            MainActivity.sv.setBackgroundResource(R.color.black);
        }
        super.onDestroy();
    }
}

