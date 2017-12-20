package kr.co.fci.tv.tvSolution;


import android.content.Context;
import android.database.Cursor;
import android.text.format.Time;
import android.view.Surface;
import android.view.View;

import com.fci.tv.FCI_TV;
import com.fci.tv.NotifyMSG;

import java.io.File;
import java.io.IOException;

import kr.co.fci.tv.FloatingWindow;
import kr.co.fci.tv.MainActivity;
import kr.co.fci.tv.TVEVENT;
import kr.co.fci.tv.buildOption;
import kr.co.fci.tv.chat.ChatMainActivity;
import kr.co.fci.tv.saves.CommonStaticData;
import kr.co.fci.tv.util.TVlog;

import static kr.co.fci.tv.MainActivity.getExternalMounts;

public class FCI_TVi {


    private static String TAG = "FCI_TVi";
    private static FCI_TV itv= null;

    private static Context mContext;
    private static int mSequence;
    static ScanListManager _slmgr = null;

    private static Process process = null;
    // usbdongle[[
    private static int devUsbFd = -1;
    private static String devUsbName = null;
    private static int devOSVersion = 0;
    // ]]usbdongle
    //device init failed[[
    public static boolean initiatedSol = true;
    //]]device init failed

    public static int currentDualmode = FCI_TV.CHSTART_SINGLE;

    static FCI_TV getFCITV()
    {
        if(null == itv){
            synchronized (FCI_TV.class) {
                if(null == itv){
                    itv = new FCI_TV();
                }
            }
        }
        return itv;
    }

    private static void setCallback()
    {
        itv.setCallback(new FCI_TV.Callback() {
            public void Cb(int _result, int arg1, int arg2, String arg3, Object arg4 ){
                switch (_result) {

                    case FCI_TV.MSG_FIRST_VIDEO_START: {
                        TVlog.i(TAG, " MSG_FIRST_VIDEO_START ");

                        // live add
                        if (MainActivity.isMainActivity || MainActivity.isPlayBackActivity) {
                            TVBridge.firstVideoNoti();
                        } else if (FloatingWindow.isFloating) {
                            TVBridge.firstVideoNotiFloating();
                        } else if (ChatMainActivity.isChat) {
                            TVBridge.firstVideoNotiChat();
                        }

                        //TVBridge.firstVideoNoti();

                    }
                    break;

                    case FCI_TV.MSG_AUDIO_ONLY_MODE_START: {
                        TVlog.i(TAG, " MSG_AUDIO_ONLY_MODE_START ");

                        // live add
                        if (MainActivity.isMainActivity || MainActivity.isPlayBackActivity) {
                            TVBridge.firstAudioNoti();
                        } else if (FloatingWindow.isFloating) {
                            TVBridge.firstAudioNotiFloating();
                        } else if (ChatMainActivity.isChat) {
                            TVBridge.firstAudioNotiChat();
                        }

                        //TVBridge.firstVideoNoti();

                    }
                    break;

                    case FCI_TV.MSG_LOW_BUFFER: {

                        TVlog.i(TAG, " MSG_LOW_BUFFER ");
                        if(CommonStaticData.scanningNow == false && CommonStaticData.scanCHnum > 0) {
                            // live add

                            TVlog.i(TAG, " ==> MainActivity.isMainActivity = "+MainActivity.isMainActivity);
                            TVlog.i(TAG, " ==> ChatMainActivity.isChat = "+ChatMainActivity.isChat);
                            TVlog.i(TAG, " ==> FloatingWindow.isFloating = "+FloatingWindow.isFloating);

                            if (MainActivity.isMainActivity) {
                                TVBridge.noSignalNoti();
                            } else if (FloatingWindow.isFloating) {
                                TVBridge.noSignalNotiFloating();
                            } else if (ChatMainActivity.isChat) {
                                TVBridge.noSignalNotiChat();
                            }
                            //TVBridge.noSignalNoti();

                        }
                    }
                    break;
                    case FCI_TV.MSG_RECORDING_FILE_SIZE_ERROR: {

                        TVlog.i(TAG, " MSG_RECORDING_FILE_SIZE_ERROR ");
                        // Type 1 file size error
                        // Type 2 file unsupport type
                        TVBridge.sendEventToMainWithData(TVEVENT.E_RECORDING_FAIL, 1, 0, null);
                    }
                    break;

                    case FCI_TV.MSG_SCAN_NOTIFY: {


                        NotifyMSG notiMSG =null;
                        if(arg4!=null)
                        {
                            notiMSG = (NotifyMSG)arg4;
                        }
                        else
                        {
                            return;
                        }
                        int idx = (int)arg2;
                        String desc =(String) arg3;
                        byte segType = (byte)notiMSG.segType;
                        byte vidFormat = (byte)notiMSG.vidFormat;
                        byte audFormat = (byte)notiMSG.audFormat;
                        byte isFree = (byte)notiMSG.isFree;
                        byte bLast = (byte)notiMSG.bLast;
                        int remoteKey = (int)notiMSG.remoteKey;
                        int serviceID = (int)notiMSG.svcNumber;
                        int freqKHz = (int)notiMSG.setFreqKHz;

                        // TVlog.e(TAG, " MSG_SCAN_NOTIFY ");
                        if (FCI_TVi.initiatedSol) {
                            TVBridge.scanNoti(idx, desc, segType, vidFormat, audFormat, isFree, remoteKey, serviceID, freqKHz, bLast);
                        }
                    }
                    break;

                    case FCI_TV.MSG_SCANNIG_PROGRESS_NOTIFY: {

                        NotifyMSG notiMSG =null;
                        if(arg4!=null)
                        {
                            notiMSG = (NotifyMSG)arg4;
                        }
                        else
                        {
                            return;
                        }
                        int progressPercent = notiMSG.nProgress;
                        int found = (int)notiMSG.found;
                        int freqKHz = (int)notiMSG.freqKHz;
                        String svcName = (String) arg3;
                        if (FloatingWindow.isFloating) {
                            TVBridge.scanProgress_floating(progressPercent, found, freqKHz, svcName);
                        } else if (ChatMainActivity.isChat) {
                            TVBridge.scanProgress_chat(progressPercent, found, freqKHz, svcName);
                        } else {
                            TVBridge.scanProgress(progressPercent, found, freqKHz, svcName);
                        }

                    }
                    break;

                    case FCI_TV.MSG_HANDOVER_PROGRESS_NOTIFY: {

                        NotifyMSG notiMSG =null;
                        if(arg4!=null)
                        {
                            notiMSG = (NotifyMSG)arg4;
                        }
                        else
                        {
                            return;
                        }
                        int progressPercent = notiMSG.nProgress;
                        int found = (int)notiMSG.found;
                        int freqKHz = (int)notiMSG.freqKHz;
                        String svcName = (String) arg3;
                        TVBridge.handoverProgress(progressPercent, found, freqKHz, svcName);
                    }
                    break;

                    case FCI_TV.MSG_HANDOVER_SUCCESS_NOTIFY: {

                        int index = (int)arg1;
                        int total = (int)arg2;
                        int updateMode = (int)arg4;
                        TVBridge.handoverSuccess(index, total, updateMode);
                    }
                    break;

                    case FCI_TV.MSG_PROGRAM_NOT_AVAILABLE_NOTIFY: {

                        /*
                        TVlog.i(TAG, " ==> MainActivity.isMainActivity = "+MainActivity.isMainActivity);
                        TVlog.i(TAG, " ==> ChatMainActivity.isChat = "+ChatMainActivity.isChat);
                        TVlog.i(TAG, " ==> FloatingWindow.isFloating = "+FloatingWindow.isFloating);

                        if (MainActivity.isMainActivity) {
                            TVBridge.programNotAvailableNoti();
                        } else if (FloatingWindow.isFloating) {
                            TVBridge.programNotAvailableNotiFloating();
                        } else if (ChatMainActivity.isChat) {
                            TVBridge.programNotAvailableNotiChat();
                        }
                        */

                    }
                    break;

                    case FCI_TV.MSG_EWS_NOTIFY: {
                        int startEndFlag = (int)arg1;
                        int signalLevel = (int)arg2;
                        int [] areaCodes = null;
                        if (arg4 != null) {
                            areaCodes = (int[])arg4;
                        }
                        TVBridge.ewsInfoNoti(startEndFlag, signalLevel, areaCodes);
                    }
                    break;

                    case FCI_TV.MSG_EPG_UPDATE_NOTIFY: {
                        int epgType = arg1;
                        TVBridge.epgUpdateNoti(epgType);
                    }
                    break;

                    case FCI_TV.MSG_TS_PLAYBACK_ERROR_NOTIFY: {
                        int errorNum = arg1;
                        TVBridge.tsPlaybackErrorNoti(errorNum);
                    }

                    case FCI_TV.MSG_BCAS_CARD_READY: {
                        byte[] cardId = (byte[])arg4;
                        TVBridge.bcasCardReady(cardId);
                    }
                    break;

                    case FCI_TV.MSG_BCAS_CARD_REMOVED: {
                        TVBridge.bcasCardRemoved();
                    }
                    break;

                    case FCI_TV.MSG_UPDATANUMOF_ADUIO_NOTIFY: {

                    }
                    break;

                    case FCI_TV.MSG_UPDATANUMOF_CAPTION_NOFITY: {

                    }
                    break;

                    case FCI_TV.MSG_CAPTION_NOTIFY: {
                        String captionContents = (String)arg3;
                        //       TVlog.i(TAG, " MSG_CAPTION_NOTIFY ");
                        TVBridge.sendEventSubtitle(captionContents);
                    }
                    break;

                    case FCI_TV.MSG_CAPTION_NOTIFY_DIRECT: {
                        NotifyMSG notiMSG =null;
                        if(arg4!=null) {
                            notiMSG = (NotifyMSG)arg4;
                        }
                        else {
                            return;
                        }
                        byte[] capData = notiMSG.capData;
                        int capLen = notiMSG.capLen;
                        byte isClear = notiMSG.capClear;
                        byte isEnd = notiMSG.capEnd;
                        int[] capInfo = notiMSG.capInfo;

                        // TVlog.e(TAG, " MSG_CAPTION_NOTIFY_DIRECT ");
                        TVBridge.sendEventSubtitleDirect(capData, capLen, isClear, isEnd, capInfo);
                    }
                    break;

                    case FCI_TV.MSG_SUPERIMPOSE_NOTIFY: {
                        String superimposeContents = (String)arg3;
                        //TVlog.i(TAG, " MSG_SUPERIMPOSE_NOTIFY ");
                        TVBridge.sendEventSuperimpose(superimposeContents);
                    }
                    break;

                    case FCI_TV.MSG_SUPERIMPOSE_NOTIFY_DIRECT: {
                        NotifyMSG notiMSG =null;
                        if(arg4!=null) {
                            notiMSG = (NotifyMSG)arg4;
                        }
                        else {
                            return;
                        }
                        byte[] supData = notiMSG.capData;
                        int supLen = notiMSG.capLen;
                        byte isClear = notiMSG.capClear;
                        byte isEnd = notiMSG.capEnd;
                        int[] supInfo = notiMSG.capInfo;

                        // TVlog.e(TAG, " MSG_SUPERIMPOSE_NOTIFY_DIRECT ");
                        TVBridge.sendEventSuperimposeDirect(supData, supLen, isClear, isEnd, supInfo);
                    }
                    break;

                    case FCI_TV.MSG_START_REC_NOTIFY: {
                        TVBridge.recStartNotify();
                    }
                    break;

                    case FCI_TV.MSG_UNSUPPORT_REC_NOTIFY: {
                        TVlog.i(TAG, " MSG_UNSUPPORT_REC_NOTIFY ");
                        TVBridge.recErrorNotify(2);
                    }
                    break;

                    case FCI_TV.MSG_TS_INVALID_REC_NOTIFY: {
                        TVlog.i(TAG, " MSG_TS_INVALID_REC_NOTIFY ");
                        TVBridge.recErrorNotify(3);
                    }
                    break;

                    case FCI_TV.MSG_TS_VALID_REC_NOTIFY: {
                        TVlog.i(TAG, " MSG_TS_VALID_REC_NOTIFY ");
                        TVBridge.recOKNotify();
                    }
                    break;

                    case FCI_TV.MSG_NOT_READY_VIDEO_DECODER: {
                        TVlog.i(TAG, " MSG_NOT_READY_VIDEO_DECODER ");
                        TVBridge.noDecoderNotify();
                    }
                    break;

                    //ADD_GINGA_NCL[[
                    case FCI_TV.MSG_NCL_NOTIFY: {
                        byte[] dataNCL = null;
                        if (arg4 != null) {
                            dataNCL = (byte[]) arg4;
                        }
                        TVBridge.gingaNCLNoti(dataNCL);
                    }
                    break;
                    //]]ADD_GINGA_NCL

                    case FCI_TV.MSG_NOT_SUPPORT_VIDEO_RESOLUTION:
                        TVlog.i(TAG, " MSG_NOT_SUPPORT_VIDEO_RESOLUTION");
                        TVBridge.noSupportResolutionNotify();
                        break;
                    case FCI_TV.MSG_AUTODETECT_CODEC_ON_NOTIFY :
                        TVlog.i(TAG, " MSG_AUTODETECT_CODEC_ON_NOTIFY");
                        if (MainActivity.isMainActivity) {
                            TVBridge.subSurfaceViewON();
                        } else if (ChatMainActivity.isChat) {
                            TVBridge.subChatSurfaceViewON();
                        } else if (FloatingWindow.isFloating) {
                            TVBridge.subFloatingSurfaceViewON();
                        }
                        break;

                    case FCI_TV.MSG_AUTODETECT_CODEC_OFF_NOTIFY :
                        TVlog.i(TAG, " MSG_AUTODETECT_CODEC_OFF_NOTIFY");
                        if (MainActivity.isMainActivity) {
                            TVBridge.subSurfaceViewOff();
                        } else if (ChatMainActivity.isChat) {
                            TVBridge.subChatSurfaceViewOff();
                        } else if (FloatingWindow.isFloating) {
                            TVBridge.subFloatingSurfaceViewOff();
                        }
                        break;

                }

            }
        });
    }


    public static void saveLogcatToFile(int serviceID) {

        String rootPath;

        if(getExternalMounts().size() != 0) {  // external SD
            //rootPath = buildOption.SECOND_DRIVE_PATH+"FCI_LOG";
            rootPath = MainActivity.getInstance().getExternalSDPath()+"FCI_LOG";

        }else                          // phone
        {
            //rootPath = buildOption.PHONE_DRIVE_PATH+"FCI_LOG";
            rootPath = MainActivity.getInstance().getInternalSDPath()+"FCI_LOG";
        }

        File dir_exist = new File(rootPath);
        if (!dir_exist.exists()) {
            dir_exist.mkdirs();
            TVlog.i(TAG, "==== make new folder ====  "+rootPath);
        }


        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();

        String localDate= today.year+ "-" +
                (today.month + 1) + "-" +
                today.monthDay + "_" +
                today.format("%H:%M:%S").replaceAll(":", "-");


        TVlog.i(TAG, "local data =  " + localDate);
        String fileName = "FCITV_Log_v"+buildInformation.RELEASE_VERSION+"_"+localDate+".txt";
        File outputFile = new File(rootPath+"/",fileName);

        TVlog.i(TAG, "saveLogcatToFile outputFile = " + outputFile);

        try{
            //  @SuppressWarnings("unused");
            process = Runtime.getRuntime().exec("logcat -f "+outputFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void startLogCaptue(int serviceID )
    {
        StopLogcapture();
        String rootPath;

        if(getExternalMounts().size() != 0) {  // external SD
            //rootPath = buildOption.SECOND_DRIVE_PATH+"FCI_LOG";
            rootPath = MainActivity.getInstance().getExternalSDPath()+"FCI_LOG";

        }else                          // phone
        {
            //rootPath = buildOption.PHONE_DRIVE_PATH+"FCI_LOG";
            rootPath = MainActivity.getInstance().getInternalSDPath()+"FCI_LOG";
        }

        File dir_exist = new File(rootPath);
        if (!dir_exist.exists()) {
            dir_exist.mkdirs();
            TVlog.i(TAG, "==== make new folder ====  "+rootPath);
        }

        Cursor cursor = MainActivity.getCursor();
        cursor.moveToPosition(serviceID);
        String channelName = cursor.getString(CommonStaticData.COLUMN_INDEX_SERVICE_NAME);

        TVlog.i("Log Capture","Service ID = " + serviceID+ " Name  ");

        channelName = channelName.substring(4);
        channelName = channelName.replaceAll("[^a-zA-Z0-9]", "");

        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();

        String localDate= today.year+ "-" +
                (today.month + 1) + "-" +
                today.monthDay + "_" +
                today.format("%H:%M:%S").replaceAll(":", "-");

        String fileName = "FCITV_Log_"+Integer.toString(serviceID)+"_"+channelName+"_"+localDate+".txt";
        File outputFile = new File(rootPath+"/",fileName);
        TVlog.i(TAG, "=============== save Logcat To File outputFile = " + outputFile);

        try{
            //  @SuppressWarnings("unused");
            process = Runtime.getRuntime().exec("logcat -f "+outputFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void StopLogcapture()
    {
        if(process!=null)
        {
            TVlog.i(TAG, "=============== Stop Log capture =======================" );
            process.destroy();
            process=null;
        }
    }
    public static void setSuface(Surface _sur) {

        if(itv == null) itv = getFCITV();
        if(itv != null && _sur !=null)
        {
            if (buildOption.VIDEO_CODEC_TYPE != buildOption.VIDEOCODEC_TYPE_MEDIACODEC) {
                itv.setMaxResolution(buildOption.SWCODEC_MAX_VIDEO_WIDTH, buildOption.SWCODEC_MAX_VIDEO_HEIGHT);
            }
            itv.SelectCodec(buildOption.VIDEO_CODEC_TYPE);
            itv.setSurface(_sur);
        }
    }

    public static void setSubSurface(Surface _sur) {

        if(itv == null) itv = getFCITV();
        if(itv != null && _sur !=null)
        {

            itv.setSubSurface(_sur);

        }

    }


    public static int init(String _name ,String _forceRecPath, int _param1, String _param2, int _param3) {
        int rcode = 0;
        if(itv == null) itv = getFCITV();
        // eddy MW mode change

/*
        STANDARD_JAPAN = 0;
        STANDARD_BRAZIL = 1;
        STANDARD_PHILIPPINES = 2;
        STANDARD_JAPAN_ONESEG = 3;
        STANDARD_BRAZIL_ONESEG = 4;
        STANDARD_PHILIPPINES_ONESEG = 5;
        STANDARD_JAPAN_USB = 6;
        STANDARD_BRAZIL_USB = 7;
        STANDARD_PHILIPPINES_USB = 8;
        STANDARD_JAPAN_ONESEG_USB = 9;
        STANDARD_BRAZIL_ONESEG_USB = 10;
        STANDARD_PHILIPPINES_ONESEG_USB = 11;
        STANDARD_JAPAN_FILE = 12;
        STANDARD_BRAZIL_FILE = 13;
        STANDARD_PHILIPPINES_FILE = 14;
*/


        itv.ChangeMWMode(buildOption.FCI_SOLUTION_MODE);
        if(buildOption.FCI_SOLUTION_LOG_ON ==false)
        {
            itv.setLogLevel(FCI_TV.NO_LOG);
        }

        setCallback();
        _slmgr = new ScanListManager();
        // usbdongle[[
        devUsbFd = _param1;
        devUsbName = _param2;
        devOSVersion = _param3;
        //]]usbdongle
        rcode = itv.init(_name, devUsbFd, devUsbName, devOSVersion);
//device init failed[[
        if (rcode != 0) {
            initiatedSol = false;
            if (buildOption.FCI_SOLUTION_MODE != buildOption.JAPAN_FILE &&
                    buildOption.FCI_SOLUTION_MODE != buildOption.BRAZIL_FILE &&
                    buildOption.FCI_SOLUTION_MODE != buildOption.PHILIPPINES_FILE) {

                if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB) {
                    itv.ChangeMWMode(buildOption.JAPAN_FILE);
                }
                else if (buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB) {
                    itv.ChangeMWMode(buildOption.PHILIPPINES_FILE);
                }
                else {
                    itv.ChangeMWMode(buildOption.BRAZIL_FILE);
                }
                itv.init(_name, devUsbFd, devUsbName, devOSVersion);
            }
        } else {
            initiatedSol = true;
        }

//]]device init failed
        if( buildOption.RECORDING_FILE_SYSTEM_MODE==0) {
            itv.setRecordingMode(buildOption.RECORDING_CLIP_SIZE, FCI_TV.RECORDING_SYSTEM_SETTING_SUPPORT_MODE, _forceRecPath);
        }else
        {
            itv.setRecordingMode(buildOption.RECORDING_CLIP_SIZE, FCI_TV.RECORDING_INTEGRATION_PATH_MODE, _forceRecPath);
        }


        setLogModuleOn(FCI_TV.PLAYER_CONTROLLER_DEBUG_LOG, false);
        setLogModuleOn(FCI_TV.PLAYER_SYNC_DEBUG_LOG, false);
        setLogModuleOn(FCI_TV.PLAYER_ADUIO_DEBUG_LOG, false);

//         if(buildOption.PHILIPPINES == buildOption.FCI_SOLUTION_MODE) {
//
//             TVlog.i(TAG, " PHILIPPINES register resetting ");
/////////////// RF
//            devRegWriteByte(0x0f7e, (byte) 0xff);
//            devRegWriteByte(0x0f7f, (byte) 0xff);
//            devRegWriteByte(0x0fb3, (byte) 0x07);
//            devRegWriteByte(0x0fb5, (byte) 0x07);
//            devRegWriteByte(0x0f1f, (byte) 0x79);
//            devRegWriteByte(0x0f19, (byte) 0x33);
//
/////////////// BB
//
//             devRegWriteByte(0x2502, (byte) 0x20);
//             devRegWriteByte(0x2548, (byte) 0x10);
//             devRegWriteByte(0x2535, (byte) 0x10);
//             devRegWriteByte(0x417f, (byte) 0xc);
//             devRegWriteByte(0x3032, (byte) 0x0);
//             devRegWriteByte(0x257C, (byte) 0x1);
//             devRegWriteByte(0x2505, (byte) 0x32);
//             devRegWriteByte(0x250e, (byte) 0x32);
//             devRegWriteByte(0x2532, (byte) 0x32);
//             devRegWriteByte(0x2550, (byte) 0x0);
//
//        }



        return rcode;
    }


    public static void deInit() {
        if (itv != null) {
            itv.deInit();
        }
        if(buildOption.LOG_CAPTURE_MODE ==2 || buildOption.LOG_CAPTURE_MODE ==3) {
            StopLogcapture();
        }
        itv= null;
        _slmgr = null;
    }



    public static void surfaceClear()
    {

        if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
            if (MainActivity.isMainActivity)
            {
                TVBridge.sendEventToMain(TVEVENT.E_STOP_NOTIFY);

            }else if (FloatingWindow.isFloating)
            {
                FloatingWindow.getInstance().sendEvent(TVEVENT.E_FLOATING_STOP_NOTIFY);

            }else if (ChatMainActivity.isChat)
            {
                ChatMainActivity.getInstance().sendEvent(TVEVENT.E_SUPERIMPOSE_CLEAR_NOTIFY_CHAT);
            }

        }


    }

    public static void  subSurfaceViewOnOff(int _switchMode)
    {
        if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
            if (MainActivity.isMainActivity)
            {
                if (_switchMode == FCI_TV.CHSTART_SINGLE || _switchMode == FCI_TV.CHSTART_DUAL_F_SEG) {

                    TVlog.i(TAG, "[MainActivity] Sub INVISIBLE " + _switchMode);
                    if (MainActivity.getInstance().svSub != null) {
                    MainActivity.getInstance().svSub.setVisibility(View.INVISIBLE);
                    }
                } else if (_switchMode == FCI_TV.CHSTART_DUAL_O_SEG) {
                    TVlog.i(TAG, "[MainActivity] Sub VISIBLE " + _switchMode);
                    if (MainActivity.getInstance().svSub != null) {
                    MainActivity.getInstance().svSub.setVisibility(View.VISIBLE);
                }
                }
            }else if (FloatingWindow.isFloating)
            {
                if (_switchMode == FCI_TV.CHSTART_SINGLE || _switchMode == FCI_TV.CHSTART_DUAL_F_SEG) {

                    TVlog.i(TAG, "[FloatingWindow] Sub INVISIBLE " + _switchMode);
                    if (FloatingWindow.getInstance().svSub_floatingView != null) {
                    FloatingWindow.getInstance().svSub_floatingView.setVisibility(View.INVISIBLE);
                    }

                } else if (_switchMode == FCI_TV.CHSTART_DUAL_O_SEG) {
                    TVlog.i(TAG, "[FloatingWindow] Sub VISIBLE " + _switchMode);
                    if (FloatingWindow.getInstance().svSub_floatingView != null) {
                    FloatingWindow.getInstance().svSub_floatingView.setVisibility(View.VISIBLE);
                }
                }

            }else if (ChatMainActivity.isChat)
            {
                if (_switchMode == FCI_TV.CHSTART_SINGLE || _switchMode == FCI_TV.CHSTART_DUAL_F_SEG) {
                    TVlog.i(TAG, "[ChatMainActivity] Sub INVISIBLE " + _switchMode);
                    if (ChatMainActivity.getInstance().svSub_chatView != null) {
                    ChatMainActivity.getInstance().svSub_chatView.setVisibility(View.INVISIBLE);
                    }
                } else if (_switchMode == FCI_TV.CHSTART_DUAL_O_SEG) {
                    TVlog.i(TAG, "[ChatMainActivity] Sub VISIBLE " + _switchMode);
                    if (ChatMainActivity.getInstance().svSub_chatView != null) {
                    ChatMainActivity.getInstance().svSub_chatView.setVisibility(View.VISIBLE);
                }
            }
            }


        }else
        {
            TVlog.e(TAG, " Not support Sub surface view control " + _switchMode);
        }

    }



    public static int getDualMode()
    {
        return currentDualmode;
    }

    public static void setDualMode(int _mode)
    {

        TVlog.e(TAG, " Set DualMode  from " + currentDualmode + " to "+_mode);
        currentDualmode = _mode;
    }

    public static void AVStart(int _ID, int _switchMode) {
        if (itv != null) {
            if(buildOption.LOG_CAPTURE_MODE ==2 || buildOption.LOG_CAPTURE_MODE ==3) {
                startLogCaptue(_ID);
            }

            subSurfaceViewOnOff(_switchMode);
            itv.AVStart(_ID, _switchMode);
            setDualMode(_switchMode);

        }
    }


    public static void AVStop() {
        if (itv != null) {
            if (MainActivity.isMainActivity) {
                TVBridge.sendEventToMain(TVEVENT.E_CAPTION_CLEAR_NOTIFY);
                TVBridge.sendEventToMain(TVEVENT.E_SUPERIMPOSE_CLEAR_NOTIFY);
            } else if (FloatingWindow.isFloating) {
                FloatingWindow.getInstance().sendEvent(TVEVENT.E_CAPTION_CLEAR_NOTIFY_FLOATING);
                FloatingWindow.getInstance().sendEvent(TVEVENT.E_SUPERIMPOSE_CLEAR_NOTIFY_FLOATING);
            } else if (ChatMainActivity.isChat) {
                ChatMainActivity.getInstance().sendEvent(TVEVENT.E_CAPTION_CLEAR_NOTIFY_CHAT);
                ChatMainActivity.getInstance().sendEvent(TVEVENT.E_SUPERIMPOSE_CLEAR_NOTIFY_CHAT);
            }
            itv.AVStop();
            surfaceClear();

        }
    }


    public static int AVSwitch(int _ID, int _mode) {
        if (itv != null) {

            int retSwitch;

            if (_mode != FCI_TV.CHSTART_DUAL_F_SEG && _mode != FCI_TV.CHSTART_DUAL_O_SEG) {
                TVlog.e(TAG, " AVSwitch switch mode invalid! (_mode=" + _mode + ")");
                return 0;
            }

            if( currentDualmode  == _mode)
            {
                subSurfaceViewOnOff(_mode);
            }

            retSwitch = itv.AVSwitch(_ID, _mode);
            if (retSwitch > 0) {
                setDualMode(_mode);

                if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                        (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {

                    if (MainActivity.isMainActivity) {

                        TVBridge.sendEventToMain(TVEVENT.E_CAPTION_CLEAR_NOTIFY);
                        TVBridge.sendEventToMain(TVEVENT.E_SUPERIMPOSE_CLEAR_NOTIFY);
                        MainActivity.getInstance().sendEvent(TVEVENT.E_FIRSTVIDEO);

                    } else if (FloatingWindow.isFloating) {
                        FloatingWindow.getInstance().sendEvent(TVEVENT.E_CAPTION_CLEAR_NOTIFY_FLOATING);
                        FloatingWindow.getInstance().sendEvent(TVEVENT.E_SUPERIMPOSE_CLEAR_NOTIFY_FLOATING);
                        FloatingWindow.getInstance().sendEvent(TVEVENT.E_FIRSTVIDEO_FLOATING);
                    } else if (ChatMainActivity.isChat) {

                        ChatMainActivity.getInstance().sendEvent(TVEVENT.E_CAPTION_CLEAR_NOTIFY_CHAT);
                        ChatMainActivity.getInstance().sendEvent(TVEVENT.E_SUPERIMPOSE_CLEAR_NOTIFY_CHAT);
                        ChatMainActivity.getInstance().sendEvent(TVEVENT.E_FIRSTVIDEO_CHAT);

                    } else {

                        TVlog.e(TAG, "  not support .. mode   ");
                    }

                } else {

                    TVlog.e(TAG, " AVSwitch not support .. this is not japan  ");

                }
                return retSwitch;
            }else
            {

                TVlog.e(TAG, " AVSwitch error...  recovery Surface  ");
                subSurfaceViewOnOff(currentDualmode);
            }
            return 0;

        }

        return 0;
    }


    public static void RecStart(Context _con,int _ID, String _fileName,boolean _background) {
        if (itv != null) {
            if (buildOption.RECORDING_TYPE == buildOption.RECORDING_TYPE_TS) {
                itv.TSRecStart(_fileName);
            }
            else { //(buildOption.RECORDING_TYPE == buildOption.RECORDING_TYPE_MP4)
                itv.RecStart(_con, _ID, _fileName, _background, devUsbFd, devUsbName, devOSVersion);
            }
        }
        else
        {
            itv=getFCITV();
            itv.ChangeMWMode(buildOption.FCI_SOLUTION_MODE);
            itv.init(_con.getPackageName(),devUsbFd, devUsbName, devOSVersion);
            if (buildOption.RECORDING_TYPE == buildOption.RECORDING_TYPE_TS) {
                itv.TSRecStart(_fileName);
            }
            else { //(buildOption.RECORDING_TYPE == buildOption.RECORDING_TYPE_MP4)
                itv.RecStart(_con, _ID, _fileName, _background, devUsbFd, devUsbName, devOSVersion);
            }
        }
    }

    public static void RecStop(boolean _background) {
        if (itv != null) {
            if (buildOption.RECORDING_TYPE == buildOption.RECORDING_TYPE_TS) {
                itv.TSRecStop();
            }
            else { //(buildOption.RECORDING_TYPE == buildOption.RECORDING_TYPE_MP4)
                itv.RecStop(_background);
            }
        }
        else
        {
            itv=getFCITV();
            if (buildOption.RECORDING_TYPE == buildOption.RECORDING_TYPE_TS) {
                itv.TSRecStop();
            }
            else { //(buildOption.RECORDING_TYPE == buildOption.RECORDING_TYPE_MP4)
                itv.RecStop(_background);
            }
        }
    }

    public static void setAudioMode(int _mode)
    {
        if (itv != null) {
            itv.setAudioMode(_mode);
        }
    }

    public static int getAudioMode()
    {
        if (itv != null) {
            return itv.getAudioMode();
        }
        else {
            return 0; //default:stereo
        }
    }
    /**
     * stop to record audio and video from TS stream
     */
    public static void setLogLevel(int _level)
    {
        if (itv != null) {
            itv.setLogLevel(_level);
        }
    }
    public static void setLogModuleOn(int _module,boolean _isOn)
    {
        if (itv != null) {
            itv.setLogModuleOn(_module, _isOn);
        }
    }

    public static boolean AddScanList(ScanList scanlist) {
        if (_slmgr != null) {
            return _slmgr.AddScanList(scanlist);
        }
        else {
            return false;
        }
    }
    public static void RemoveAllChannel()
    {
        if (_slmgr != null) {
            _slmgr.RemoveAllChannel();
        }
    }

    public static int ScanStart() {
        if (itv != null) {

            return itv.ScanStart();
        }
        else {
            return -1; //failure
        }
    }

    public static int ScanStart(int[] _freqCh) {
        if (itv != null) {
            return itv.ScanStart(_freqCh);
        }
        else {
            return -1; //failure
        }
    }

    public static int ScanStart(byte _opMode) {
        if (itv != null) {
            return itv.ScanStart(_opMode);
        }
        else {
            return -1; //failure
        }
    }

    public static int ScanStop() {
        if (itv != null) {
            return itv.ScanStop();
        }
        else {
            return -1; //failure
        }
    }

    public static int IsScanning() {
        if (itv != null) {
            return itv.IsScanning();
        }
        else {
            return 0; //not scanning state
        }
    }

    public static int LoadScanList() {
        if (itv != null) {
            return itv.LoadScanList();
        }
        else {
            return 1; //failure;
        }
    }

    public static int GetSignal() {
        if (itv != null) {
            return itv.GetSignal();
        }
        else {
            return 0;
        }
    }

    public static int[] GetMoreSignalVal() {
        if (itv != null) {
            return itv.GetMoreSignalVal();
        } else {
            int[] ret = {0, 0, 0};
            return ret;
        }
    }

    public static int[] GetMoreSignalVal2() {
        if (itv != null) {
            return itv.GetMoreSignalVal2();
        } else {
            int[] ret = {10000, 10000, 10000, 10000, -120};
            return ret;
        }
    }

    public static int devRegWriteByte(int regAddress, byte setValue) {
        if (itv != null) {
            return itv.RegWriteByte(regAddress, setValue);
        }
        else {
            return -1;
        }
    }

    public static int devRegReadByte(int regAddress) {
        if (itv != null) {
            return itv.RegReadByte(regAddress);
        }
        else {
            return -1;
        }
    }

    public static int[] GetTSNetTime() {
        if (itv != null) {
            return itv.GetTSNetTime();
        }
        else {
            int[] ret = {0, 0, 0, 0, 0, 0};
            return ret;
        }
    }

    public static String GetServiceName() {
        if (itv != null) {
            return itv.GetServiceName();
        }
        else {
            return "";
        }
    }

    public static int GetEPGCount(int day, int serviceIdx) {
        if (itv != null) {
            return itv.GetEPGCount(day, serviceIdx);
        }
        else {
            return 0;
        }
    }

    public static String GetEPGTS(int day, int index) {
        if (itv != null) {
            return itv.GetEPGTS(day, index);
        }
        else {
            return "";
        }
    }

    public static int[] GetEPGStartTimeNDuration(int day, int index) {
        if (itv != null) {
            return itv.GetEPGStartTimeNDuration(day, index);
        }
        else {
            int[] ret = {0, 0, 0, 0, 0, 0, 0, 0};
            return ret;
        }
    }

    public static String GetEPGTitle(int day, int index) {
        if (itv != null){
            return itv.GetEPGTitle(day, index);
        }
        else {
            return "";
        }
    }

    public  static String GetEPGDesc(int day, int index) {
        if (itv != null) {
            return itv.GetEPGDesc(day, index);
        }
        else {
            return "";
        }
    }

    public static int GetAudioNum() {
        if (itv != null) {
            return itv.GetAudioNum();
        }
        else {
            return 1;
        }
    }

    public static int GetVideoNum() {
        if (itv != null) {
            return itv.GetVideoNum();
        }
        else {
            return 1;
        }
    }

    public static int GetCurAudioChannelNum() {
        if (itv != null) {
            return itv.GetCurAudioChannelNum();
        }
        else {
            return 2; //stereo mode
        }
    }

    //dualdecode[[
    public static int GetCurAudioChannelNumSub() {
        if (itv != null) {
            return itv.GetCurAudioChannelNumSub();
        }
        else {
            return 2; //stereo mode
        }
    }

    /**
     * function that gets the paired service index and whether it's fullseg or not.
     * param => index: channel index
     * return =>
     * int[0]: 1st return array value - paired service index
     * int[1]: 2nd return array value - whether it's fullseg or not
     * int[2]: 3rd return array value - index for having same video pid in case of non paired service
     * int[3]: 4th return array value - main index among indices having same frequency
     * int[4]: 5th return array value - index of 1-seg service whether it is paired or not
     * int[5]: 6th return array value - whether it's audio only service or not
     */
    public static int[] GetPairNSegInfoOfCHIndex(int index) {
        if (itv != null) {
            return itv.GetPairNSegInfoOfCHIndex(index);
        }
        else {
            int[] ret = {-1, -1, -1, -1, -1, -1};
            return ret;
        }
    }

    public static int GetCurPlayMode() {
        if (itv != null) {
            return itv.GetCurPlayMode();
        }
        else {
            return -1; //failed
        }
    }

    public static boolean IsBCASInitialized() {
        if (itv != null) {
            return itv.IsBCASInitialized();
        }
        else {
            return false;
        }
    }
    //]]dualdecode

    public static String GetAudioInfo(int index) {
        if (itv != null) {
            return itv.GetAudioInfo(index);
        }
        else {
            return "";
        }
    }

    public static int SelectAudioLanguage(int index) {
        if (itv != null) {
            return itv.SelectAudioLanguage(index);
        }
        else {
            return -1; //failure
        }
    }

    public static int SelectVideoTrack(int index) {
        if (itv != null) {
            return itv.SelectVideoTrack(index);
        }
        else {
            return -1; //failure
        }
    }

    public static int GetSubtitleNum() {
        if (itv != null) {
            return itv.GetSubtitleNum();
        }
        else {
            return 0;
        }
    }

    public static String GetSubtitleInfo(int index) {
        if (itv != null) {
            return itv.GetSubtitleInfo(index);
        }
        else {
            return "";
        }
    }

    public static int SelectCaption(int index) {
        if (itv != null) {
            return itv.SelectCaption(index);
        }
        else {
            return -1;
        }
    }

    public static int GetSuperimposeNum() {
        if (itv != null) {
            return itv.GetSuperimposeNum();
        }
        else {
            return 0;
        }
    }

    public static String GetSuperimposeInfo(int index) {
        if (itv != null) {
            return itv.GetSuperimposeInfo(index);
        }
        else {
            return "";
        }
    }

    public static int SelectSuperimpose(int index) {
        if (itv != null) {
            return itv.SelectSuperimpose(index);
        }
        else {
            return -1;
        }
    }

    public static int GetCurProgramRating() {
        if (itv != null) {
            return itv.GetCurProgramRating();
        }
        else {
            return 1;
        }
    }

    public static int playFrequency(long freqKHz) {
        if (itv != null) {
            return itv.playFrequency(freqKHz);
        }
        else {
            return -1;
        }
    }

    public static long startPlayback(String fileName) {
        boolean nRet = true;
        if (itv != null) {
            nRet = itv.TSPlayBackStart(fileName);
            if (nRet == true) {
                return 0;
            }
            else {
                return -1;
            }
        }
        else {
            return -1;
        }
    }

    public static boolean stopPlayback()
    {
        if (itv != null) {
            return itv.TSPlayBackStop();
        }
        else {
            return false;
        }
    }

    public static boolean pausePlayback()
    {
        if (itv != null) {
            return itv.TSPlayBackPause();
        }
        else {
            return false;
        }
    }

    public static boolean resumePlayback()
    {
        if (itv != null) {
            return itv.TSPlayBackResume();
        }
        else {
            return false;
        }
    }

    public static boolean seekPlayback(int value)
    {
        if (itv != null) {
            return itv.TSPlayBackSeek(value);
        }
        else {
            return false;
        }
    }
    public static int currentPlayback()
    {
        if (itv != null) {
            return itv.TSPlayBackGetCurrent();
        }
        else {
            return -1;
        }
    }


    public static int TSPlayerBackGetOffSetUnit()
    {
        if (itv != null) {
            return itv.TSPlayerBackGetOffSetUnit();
        }
        else {
            return -1;
        }
    }

    public static int TSPlayBackGetDuration(String _fileName)
    {
        if (itv != null) {
            return itv.TSPlayBackGetDuration(_fileName);
        }
        else {
            return -1;
        }
    }


    public static int startDumpTS(int sizeTodump, String passWord) {
        if (itv != null) {
            return itv.startDumpTS(sizeTodump, passWord);
        }
        else {
            return -1;
        }
    }

    public static int stopDumpTS() {
        if (itv != null) {
            return itv.stopDumpTS();
        }
        else {
            return -1;
        }
    }

    public static int bcasTest()
    {
        if (itv != null) {
            return itv.bcasTest();
        }
        else {
            return -1;
        }
    }

    public static int setSkipAVErrorData(byte onSkip)
    {
        if (itv != null) {
            if (onSkip == 0) { //disable
                itv.setSkipAVErrorData((byte)0);
            }
            else {
                itv.setSkipAVErrorData((byte)1);
            }
            return 0;
        }
        else {
            return -1;
        }
    }

    public final static int ISDBT_MODE_NONE = 0;
    public final static int ISDBT_MODE_FULLSEG = 1;
    public final static int ISDBT_MODE_ONESEG = 2;
    public final static int ISDBT_MODE_FILE = 3;
    public static int GetISDBMode()
    {
        if (itv != null) {
            int isdbMode = itv.GetISDBMode();
            if(isdbMode == ISDBT_MODE_FULLSEG || isdbMode == ISDBT_MODE_FILE) {
                return ISDBT_MODE_FULLSEG;
            }
            else if (isdbMode == ISDBT_MODE_ONESEG) {
                return ISDBT_MODE_ONESEG;
            }
            else {
                return ISDBT_MODE_NONE;
            }
        }
        else {
            return ISDBT_MODE_NONE;
        }
    }

    //ADD_GINGA_NCL[[
    public static int enableGingaNCL() {
        if (itv != null) {
            return itv.enableGingaNCL();
        }
        else {
            return -1;
        }
    }

    public static int disableGingaNCL() {
        if (itv != null) {
            return itv.disableGingaNCL();
        }
        else {
            return -1;
        }
    }
    //]]ADD_GINGA_NCL

    //JAPAN_CAPTION[[
    public static byte[] getPngFromAribPng(byte[] _rawData) {
        if (itv != null) {
            return itv.getPngFromAribPng(_rawData);
        }
        else {
            return null;
        }
    }
    //]]JAPAN_CAPTION

    public static void setVolume(float _volume) {
        if (itv != null) {
            itv.setVolume(_volume);             // 0= mute, 1=play
        }
    }

    public static int DoCapture(String var1) {
        if (itv != null) {
            return itv.DoCapture(var1);
        } else {
            return 0;
        }
    }

}
