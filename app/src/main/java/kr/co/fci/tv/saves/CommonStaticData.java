/********************************************************************************************************************/
/**
 *  @skip   $Id: CommonStaticData.java 1969 2017-12-06 07:56:53Z live.kim $
 *  @file   CommonStaticData.java
 *  @brief  Common Static Data define.
 *  @date   2015/05/21 FCI elliot create.
 *
 *  ALL Rights Reserved, Copyright(C) FCI 2015
 */
/********************************************************************************************************************/
package kr.co.fci.tv.saves;

import android.app.Activity;
import android.content.SharedPreferences;
import android.text.format.Time;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;

import kr.co.fci.tv.channelList.ChannelListFragment;
import kr.co.fci.tv.channelList.FavoriteListFragment;
import kr.co.fci.tv.saves.TVProgram.Programs;



public class CommonStaticData {
    public static Time sysTm = new Time();

    public static ArrayList<Integer> freqPointArrayList = new ArrayList<Integer>();
    public static ArrayList<Integer> bandWidthArrayList = new ArrayList<Integer>();
    public static int totalFreqCount = 56; // 56
    public static int scanMode = 0;
    public static int scanFreq = 0;
    public static int scanLNBFreq = 0;
    public static int scanBandWidth = 0;

    public static int symRate = 0;
    public static int polMode = 0;
    public static int s22kMode = 0;
//  public static boolean bolScanning = false;
//  public static boolean bolCancelScan = false;

    public static SharedPreferences settings = null;

    public static boolean bolEPGScan = true; // false;
    public static int bufferId = 0;
    /**************************PREF KEY*********************************/
//  public static final String mSharedPreferencesName = "GlobalSettings";
    public static final String mSharedPreferencesName = "com.android.isdbt_preferences";
    public static final String hasChannelKey = "hasChannel";
    public static final String scrSizeModeKey = "scrSizeMode";
    public static final String serviceTVNumKey = "serviceTVNum";
    public static final String serviceRadioNumKey = "serviceRadioNum";
    public static final String serviceTVFavFlagKey = "serviceTVFav";

    public static final String areaSetKey = "areaSet";
    public static final String orderSetKey = "orderSet";
    public static final String timeZoneSetKey = "timeZone";
    public static final String scanedChannelsKey = "scanedChannels";

    /// setting menu item ////
    public static final String audiolangSwitchKey = "audiolangSwitchKey";      // 1
    public static final String captionSwitchKey = "captionSwitchKey";          // 2
    public static final String sleeptimerSwitchKey = "sleeptimerSwitchKey";          // 3
    public static final String storageSwitchKey = "storageSwitchKey";          // 4
    public static final String automodeSwitchKey = "automodeSwitchKey";        // 5
    public static final String handoverSwitchKey = "handoverSwitchKey";        // 6
    public static final String recordingSwitchKey = "recordingSwitchKey";      // 7
    public static final String parentalcontrolSwitchKey = "parentalcontrolSwitchKey";  // 9
    public static final String parentalRatingKey = "parentalRatingKey";                // 10
    public static final String passwordKey = "passwordKey";            // 11
    public static final String definedbattMonitorKey = "definedbattMonitorKey";     // 12
    public static final String usagecollectKey = "uasgecollectKey";     // 13
    public static final String interactiveKey = "interactiveKey";       // 14
    public static final String initializeKey = "initializeKey";         // 15
    public static final String aboutthisKey = "aboutthisKey";           // 16
    public static final String audiomodeSwitchKey = "audiomodeSwitchKey";           // 17
    public static final String audiotrackSwitchKey = "audiotrackSwitchKey";
    public static final String videotrackSwitchKey = "videotrackSwitchKey";
    public static final String scaleSwitchKey = "scaleSwitchKey";           // 16
    public static final String brightnessKey = "brightnessKey";
    public static final String transparentKey = "transparentKey";

    public static final String ewsSwitchKey = "ewsSwitch";
    public static final String lastChannelKey = "lastChannelKey";

    public static final String loudSpeakerKey = "loudSpeakerKey";
    public static final String localeSetKey = "localeSetKey";
    public static final String captionSetKey = "captionSetKey";
    public static final String superimposeSwitchKey = "superimposeSwitchKey";
    public static final String superimposeSetKey = "superimposeSetKey";
    public static final String receivemodeSwitchKey = "receivemodeSwitchKey";
    public static final String autoSearchSwitchKey = "autoSearchSwitchKey";

    public static final String areaKey = "areaKey";
    public static final String prefectureKey = "prefectureKey";
    public static final String localityKey = "localityKey";
    public static final String versionNameKey = "versionNameKey";

    public static final String badSignalFlagKey = "badSignalFlagKey";
    public static final String encryptFlagKey = "encryptFlagKey";
    public static final String ageLimitFlagKey = "ageLimitFlagKey";
    public static final String passwordVerifyFlagKey = "passwordVerifyFlag";
    public static final String mainPasswordVerifyFlagKey = "mainPasswordVerifyFlag";
//  public static final String screenBlockFlagKey = "screenBlockFlagKey";

    public static final String solutionModeKey = "solutionModeKey"; // TCL Brazil Philippine system switching 20161117

    public static final String countIntroKey = "countIntroKey";

    public static final String returnMainFromChatKey = "returnMainFromChatKey";
    public static final String returnMainFromFloatingKey = "returnMainFromFloatingKey";

    public static final String currentScaleModeKey = "currentScaleModeKey";

    /*******************************************************************/

//  public final static int BASE_FREQ = 473143;

    /* Menu ID */
    public final static byte MENU_ID_TV = 0;
    public final static byte MENU_ID_FAVORITE = 1;
    public final static byte MENU_ID_SCAN = 2;
    public final static byte MENU_ID_SETUP = 3;
    public final static byte MENU_ID_RADIO = 4;
    public final static byte MENU_ID_EPG = 5;

    /*
     * Service Type
     */
    public final static String SERVICE_TYPE_ALL   = "0"; /* all services */
    public final static String SERVICE_TYPE_TV    = "1"; /* TV services */
    public final static String SERVICE_TYPE_RADIO = "2"; /* radio services */
    public final static String SERVICE_TYPE_OTHER = "4"; /* data/other services */

    public final static String[] selectionArgsTV = new String[] {
        SERVICE_TYPE_TV
    };
    public final static String[] selectionArgsRadio = new String[] {
        SERVICE_TYPE_RADIO
    };
    public final static String[] selectionArgsFav = new String[] {
        "1"
    };

    /*
     * Service Number
     */
    public static int serviceTVNum = 0;
    public static int serviceRadioNum = 0;

    //favorite flag for serviceTV
    public static int serviceTVFavFlag = 0;
    /**
     * The columns we are interested in from the database
     */
    public static final String[] PROJECTION = new String[] {
        Programs._ID, // 0
        Programs.SERVICEID, // 1
        Programs.SERVICENAME, // 2
        Programs.FREQ, // 3
        Programs.FREE, // 4
        Programs.TYPE, //5
        Programs.FAV, //6
        Programs.MTV, //7
        Programs.VIDFORM, //8
        Programs.AUDFORM, //9
        Programs.REMOTEKEY, //10
        Programs.SVCNUM, //11
    };

    /**
     * The columns index in the database::same as TVProgram.java field
     */
    public static final int COLUMN_INDEX_SERVICE_ID = 1;
    public static final int COLUMN_INDEX_SERVICE_NAME = 2;
    public static final int COLUMN_INDEX_SERVICE_FREQ = 3;
    public static final int COLUMN_INDEX_SERVICE_FREE = 4;
    public static final int COLUMN_INDEX_SERVICE_TYPE = 5;
    public static final int COLUMN_INDEX_SERVICE_FAV = 6;
    public static final int COLUMN_INDEX_SERVICE_MTV = 7;
    public static final int COLUMN_INDEX_SERVICE_VIDFORM = 8;
    public static final int COLUMN_INDEX_SERVICE_AUDFORM = 9;
    public static final int COLUMN_INDEX_SERVICE_REMOTE_KEY = 10;
    public static final int COLUMN_INDEX_SERVICE_NUMBER = 11;

    public static final int DVB_FE_TYPE_QPSK = 0;
    public static final int DVB_FE_TYPE_QAM = 1;
    public static final int DVB_FE_TYPE_OFDM = 2;
    public static final int DVB_FE_TYPE_ATSC = 3;
    public static final int DVB_FE_TYPE_ISDB_ONESEG = 4;
    public static final int DVB_FE_TYPE_ISDB_FULLSEG = 5;

    public static boolean bolHasChannel = false;

    public static boolean bolScanLCN = false;

    /* set full screen */
    public static void setFullScreen(Activity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);

        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // activity.getWindow().setFlags(WindowManager.LayoutParams.TYPE_STATUS_BAR,
        // WindowManager.LayoutParams.TYPE_STATUS_BAR);
    }

    public static int subtitleNum = 0;
    public static int subtitleCheckedItem = 0;

    public static int audioNum = 0;
    public static int audioCheckedItem = 0;

    public static final int subW = 720;
    public static final int subH = 576;

    public static boolean captionSwitch = true;
    public static boolean interactiveSwitch = false;
    public static boolean ratingsetSwitch = true;
    public static boolean scanningNow = false;
    public static int handoverMode = 0;
    public static final int HANDOVER_MODE_OFF = 0;
    public static final int HANDOVER_MODE_ON_NORMAL = 1;
    public static final int HANDOVER_MODE_ON_UPDATE_LIST = 2;
    public static int handoverIndex = 0;
    public static boolean loudSpeaker = false;
    public static boolean loadingNow = false;
    public static boolean settingActivityShow = false;
    // justin add for dongle depatched
    public static boolean channelMainActivityShow = false;
    public static boolean epgActivityShow = false;
    public static boolean playBackActivityShow = false;
    public static boolean recordedFileActivityShow = false;
    public static boolean openActivityShow = false;
    public static boolean aboutActivityShow = false;
    ///
    public static boolean superimposeSwitch = true;
    public static boolean tuneTimeOver = false;

   // public static int audioSelect = 0;
    public static int lastCH = 0;
    public static int lastRemoteKey = 0;
    public static int lastSvcID = 0;
    public static int lastCursorCount = 0;
    public static int lastFreq = 0;
    public static boolean isProcessingUpdate = false;
    public static int scanCHnum = 0;
    public static String PassWord;
    public static int battMonitorSet = 0;
    public static int scaleSet = 0;
    public static int audiomodeSet = 1;
    public static int audiotrackSet = 0;
    public static int videotrackSet = 0;
    public static int PG_Rate = 0;
    public static int storage = 0;
    public static int sleeptime = 0;
    public static int brightness = 50;
    public static int transparent = 50;
    public static int localeSet = 4;
    public static int captionSelect = 0;
    public static int superimposeSelect = 0 ;
    public static int receivemode = 2;
    public static final int RECEIVE_MODE_1SEG = 0;
    public static final int RECEIVE_MODE_FULLSEG = 1;
    public static final int RECEIVE_MODE_AUTO = 2;
    public static final int RECEIVE_MODE_OFF = 3;
    public static int solutionMode = 1;    // TCL Brazil Philippine system switching 20161117
    public static int autoSearch = 0;  //Auto

    public static String areaSet = "1";
    public static String prefectureSet = "1/5";
    public static String localitySet = "1/5/0";

    public static int[] onesegCh;
    public static int[] fullsegCh;

    public static ChannelListFragment chListFragment = null;
    public static FavoriteListFragment favListFragment = null;

    public static String versionName = "";

    public static boolean badSignalFlag = false;
    public static boolean encryptFlag = false;
    public static boolean ageLimitFlag = false;
    public static boolean passwordVerifyFlag = false;
    public static boolean mainPasswordVerifyFlag = false;
//  public static boolean screenBlockFlag = false;

    public static int countIntro = 0;

    public static boolean returnMainFromChat = false;
    public static boolean returnMainFromFloating = false;

    public static boolean isAudioChannel = false;
    public static boolean fromFindFail = false;

    public static boolean isBadSignalFlag = false;
    public static boolean isSwitched = false;

    public static int currentScaleMode = 0;
}
