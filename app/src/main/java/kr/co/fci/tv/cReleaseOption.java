package kr.co.fci.tv;

/**
 * Created by eddy.lee on 2016-05-27.
 */
public class cReleaseOption {
    public final static String CUSTOMER= "FCI COMMON";
    public final static int JAPAN = 0;
    public final static int BRAZIL = 1;
    public final static int PHILIPPINES = 2;
    public final static int SRILANKA = 3;
    public final static int JAPAN_ONESEG = 4;
    public final static int BRAZIL_ONESEG = 5;
    public final static int PHILIPPINES_ONESEG = 6;
    public final static int SRILANKA_ONESEG = 7;
    public final static int JAPAN_USB = 8;
    public final static int BRAZIL_USB = 9;
    public final static int PHILIPPINES_USB = 10;
    public final static int SRILANKA_USB = 11;
    public final static int JAPAN_FILE = 12;
    public final static int BRAZIL_FILE = 13;
    public final static int PHILIPPINES_FILE = 14;

    public final static int VIDEOCODEC_TYPE_MEDIACODEC = 0;
    public final static int VIDEOCODEC_TYPE_SWCODEC = 1;
    public final static int VIDEOCODEC_TYPE_AUTODETECT = 2;

    public final static int SWCODEC_MAX_VIDEO_WIDTH = 1920;
    public final static int SWCODEC_MAX_VIDEO_HEIGHT = 1088;

    public final static int RECORDING_TYPE_MP4 = 0;
    public final static int RECORDING_TYPE_TS = 1;

    // Must be select for you target and contury
    public final static int FCI_SOLUTION_MODE = JAPAN;

    public final static int VIDEO_CODEC_TYPE = VIDEOCODEC_TYPE_MEDIACODEC;
    public final static int RECORDING_TYPE = RECORDING_TYPE_TS;

    public final static int GUI_STYLE_DEFAULT = 0;
    public final static int GUI_STYLE_1 = 1;
    public final static int GUI_STYLE_2 = 2;
    public final static int GUI_STYLE_3 = 3;


    //for xhdpi (Motolora, Techain)
    public final static int CHANNLE_CHANGE_PROC_SHIFT_X = 0;
    public final static int CHANNLE_CHANGE_PROC_SHIFT_Y = 0;
    //ADD_GINGA_NCL[[
    public final static int TOAST_SHIFT_X = 0; // HW_NaviKey=>0, SW_NaviKey=>50
    public final static int TOAST_SHIFT_Y = 0;
    //]]ADD_GINGA_NCL


    public final static boolean FCI_SOLUTION_LOG_ON = false;
    public final static boolean ADD_LOUD_SPEAKER = true;
    public final static boolean INTRO_ANIMATION = false;  //true = PNG animation intro, false = One Pic intro
    public final static boolean ADD_DEBUG_SCREEN = false;
    public final static boolean ADD_TS_CAPTURE = false;
    public final static boolean ADD_GINGA_NCL = false;
    public final static boolean VIEW_PHY_CH = true;
    public final static boolean SKIP_AV_ERROR_DATA = true; //true = no mosaic / false = mosaic allowed
    public final static boolean USE_MULTI_WINDOW = true;

    public final static boolean USE_CHAT_FUNCTION = false;

    public final static boolean USE_REF_TIME = false;  //true = use SystemTime , false = use TSNetTime

    public final static boolean RECORD_FUNCTION_USE = true;   // This option include recording and recorded file with playback

    public final static boolean SETTING_LOCALE_USE = false;
    public final static boolean SETTING_RESTORE_USE = true;
    public final static boolean SETTING_PASSWORD_USE = true;
    public final static boolean SETTING_PARENTAL_USE = true;    // This option include parental switch and set age menu

    public final static int RECORDING_CLIP_SIZE = 1800; //mb
    public final static int RECORDING_FILE_SYSTEM_MODE = 0; //mb

    // for FCI TV
    //public final static String PHONE_DRIVE_PATH = "/mnt/sdcard/";
    //public final static String SECOND_DRIVE_PATH = "/storage/sdcard1/";
    public final static String ROOT_RECORDED_PATH = "MobileTV";

    public final static String ROOT_CAPTURED_PATH = "Pictures/MobileTV";
    public final static int LOG_CAPTURE_MODE = 0;
    public final static int GUI_STYLE = 0;
}
