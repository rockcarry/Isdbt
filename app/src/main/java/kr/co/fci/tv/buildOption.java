package kr.co.fci.tv;
/**
 * Created by eddy.lee on 2015-09-25.
 */
public class buildOption {

    // Define Country and Mode
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

    // Define SW or H/W
    public final static int VIDEOCODEC_TYPE_MEDIACODEC = 0;
    public final static int VIDEOCODEC_TYPE_SWCODEC = 1;
    public final static int VIDEOCODEC_TYPE_AUTODETECT = 2;

    // Define MFD monitoring
    public final static int BB_MFD_MON_ON_EACHDIV = 0;
    public final static int BB_MFD_MON_ON_BROADCAST = 1;
    public final static int BB_MFD_MON_OFF = 2;

    // Define Recording format
    public final static int RECORDING_TYPE_MP4 = 0;
    public final static int RECORDING_TYPE_TS = 1;

    // Define UI Style
    public final static int GUI_STYLE = cReleaseOption.GUI_STYLE;

    public final static String CUSTOMER = cReleaseOption.CUSTOMER;

    //////////////////////////////////////////////////////////////////////////////////////////////////

    // Major build option
    public static int FCI_SOLUTION_MODE = cReleaseOption.FCI_SOLUTION_MODE;     // [[ solution switching mode 20170223
    public final static int VIDEO_CODEC_TYPE = cReleaseOption.VIDEO_CODEC_TYPE;
    public final static int RECORDING_TYPE = cReleaseOption.RECORDING_TYPE;

    // MFD monitoring option
    public static int BB_MFD_MON_MODE = cReleaseOption.BB_MFD_MON_MODE;

    // support Resolution Size
    public final static int SWCODEC_MAX_VIDEO_WIDTH = cReleaseOption.SWCODEC_MAX_VIDEO_WIDTH;
    public final static int SWCODEC_MAX_VIDEO_HEIGHT = cReleaseOption.SWCODEC_MAX_VIDEO_HEIGHT;

    //Max Recording file Size
    public final static int RECORDING_CLIP_SIZE = cReleaseOption.RECORDING_CLIP_SIZE; //mb

    // if RECORDING_FILE_SYSTEM_MODE =0 , RECORDING_SYSTEM_SETTING_SUPPORT_MODE  // for normal.
    // if RECORDING_FILE_SYSTEM_MODE =1 , RECORDING_INTEGRATION_PATH_MODE  // for techain

    public final static int RECORDING_FILE_SYSTEM_MODE = cReleaseOption.RECORDING_FILE_SYSTEM_MODE; //mb

    // For GUI
    public final static int CHANNLE_CHANGE_PROC_SHIFT_X = cReleaseOption.CHANNLE_CHANGE_PROC_SHIFT_X;
    public final static int CHANNLE_CHANGE_PROC_SHIFT_Y = cReleaseOption.CHANNLE_CHANGE_PROC_SHIFT_Y;
    public final static int TOAST_SHIFT_X = cReleaseOption.TOAST_SHIFT_X;
    public final static int TOAST_SHIFT_Y = cReleaseOption.TOAST_SHIFT_Y;

    //for mdpi (Apical)
    /*NCL_NAVI_SHIFT_X = 450;*/

    // for hdpi (TCL)
    /*NCL_NAVI_SHIFT_X = 500;*/

    //for xhdpi (Motolora, Techain)
    public final static int NCL_NAVI_SHIFT_X = 740;

    // for large-ldpi (Rishta)
    /*public final static int NCL_NAVI_SHIFT_X = 460;*/

    // for large-hdpi (Rishta, Techain)
    /*public final static int NCL_NAVI_SHIFT_X = 740;*/

    // for large-mdpi (WIT, YiHengKe)
    /*public final static int NCL_NAVI_SHIFT_X = 740;*/

    // function
    public final static boolean ADD_LOUD_SPEAKER = cReleaseOption.ADD_LOUD_SPEAKER;
    public final static boolean INTRO_ANIMATION = cReleaseOption.INTRO_ANIMATION;  //true = PNG animation intro, false = One Pic intro
    public final static boolean ADD_DEBUG_SCREEN = cReleaseOption.ADD_DEBUG_SCREEN;
    public final static boolean ADD_TS_CAPTURE = cReleaseOption.ADD_TS_CAPTURE;
    public final static boolean ADD_GINGA_NCL = cReleaseOption.ADD_GINGA_NCL;
    public final static boolean VIEW_PHY_CH = cReleaseOption.VIEW_PHY_CH;
    public final static boolean USE_REF_TIME = cReleaseOption.USE_REF_TIME;  //true = use SystemTime , false = use TSNetTime
    public final static boolean SKIP_AV_ERROR_DATA = cReleaseOption.SKIP_AV_ERROR_DATA; //true = no mosaic / false = mosaic allowed
    public final static boolean USE_MULTI_WINDOW = cReleaseOption.USE_MULTI_WINDOW;
    public final static boolean USE_CHAT_FUNCTION = cReleaseOption.USE_CHAT_FUNCTION;

    // File system path
    //public final static String PHONE_DRIVE_PATH = cReleaseOption.PHONE_DRIVE_PATH;
    //public final static String SECOND_DRIVE_PATH = cReleaseOption.SECOND_DRIVE_PATH;
    public final static String ROOT_RECORDED_PATH = cReleaseOption.ROOT_RECORDED_PATH;
    public final static String ROOT_CAPTURED_PATH = cReleaseOption.ROOT_CAPTURED_PATH;

    //////////////////////////////////////////////////////////////////////////////////////////////////
    public final static boolean RECORD_FUNCTION_USE = cReleaseOption.RECORD_FUNCTION_USE;   // This option include recording and recorded file with playback

    public final static boolean SETTING_LOCALE_USE = cReleaseOption.SETTING_LOCALE_USE;
    public final static boolean SETTING_RESTORE_USE = cReleaseOption.SETTING_RESTORE_USE;
    public final static boolean SETTING_PASSWORD_USE = cReleaseOption.SETTING_PASSWORD_USE;
    public final static boolean SETTING_PARENTAL_USE = cReleaseOption.SETTING_PARENTAL_USE;    // This option include parental switch and set age menu


    // Log on/off for Debugging
    public final static boolean FCI_SOLUTION_LOG_ON = false;
    // 0 : not support
    // 1 : Field capture mode for air or file
    // 2 : manual File mode
    // 3 : Automatic File mode
    public final static int LOG_CAPTURE_MODE = 0;
    public final static int REGISTER_DEBUG = 0;
}
