package kr.co.fci.tv;

/**
 * Created by eddy.lee on 2015-08-26.
 */
public enum TVEVENT {
    E_INTRO_MAIN_ACVITIVY_CALL,
    E_FIRSTVIDEO,
    E_CAPTION_NOTIFY,
    E_CAPTION_CLEAR_NOTIFY,
    E_SUPERIMPOSE_NOTIFY,
    E_SUPERIMPOSE_CLEAR_NOTIFY,
    E_HIDE_CONTROLER,
    E_SHOW_CONTROLER,
    E_NOT_SUPPORT_RESOLUTION,

    E_HIDE_PLAYBACK,
    E_TSPLAYBACK_FIRSTVIDEO,
    E_TSPLAYBACK_START,
    E_TSPLAYBACK_SEEK,
    E_TSPLAYBACK_STOP,
    E_TSPLAYBACK_PAUSE,
    E_TSPLAYBACK_RESUME,
    E_TS_PLAYBACK_CURRENT_POS,
    E_TSPLAYBACK_TERMINATE,
    E_TSPLAYBACK_ERROR,

    E_SCAN_PROCESS,
    E_SCAN_CANCEL,
    E_SCAN_COMPLETED,
    E_SCAN_START,
    E_SCAN_HANDOVER_START,
    E_SCAN_HANDOVER_PROCESS,
    E_SCAN_HANDOVER_SUCCESS,
    E_SCAN_MONITOR,
    E_REGION_SCAN_START,

    E_CHANNEL_CHANGE_FAIL,
    E_SIGNAL_MONITER,
    E_BADSIGNAL_CHECK,
    E_SIGNAL_NOTI_MSG,
    E_NOSIGNAL_SHOW,
    E_EWS_RECEIVED,

    E_BCAS_CARD_READY,
    E_BCAS_CARD_REMOVED,

    E_EPG_UPDATE,   // justin
    E_RATING_MONITOR,
    E_EPGTITLE_UPDATE,  //live
    E_CHLIST_UPDATE, //live
    E_CHLIST_REMOVE,

	E_SURFACE_RESIZE,
    E_SURFACE_ORIRESIZE,
    E_SURFACE_SUB_ONOFF,


    E_RECORDING_START,
    E_RECORDING_TIME_UPDATE,
    E_RECORDING_FAIL,
    E_RECORDING_OK,

    E_BATTERY_LIMITED_CHECK,

    E_CONFIRMED_PASSWORD,
    E_SLEEP_TIMER,
    E_SLEEP_TIMER_EXPIRED,
    E_TERMINATE,
    E_CHANNEL_NAME_UPDATE,

    E_RECORDED_FILE_DELETE,
    E_RECORDED_FILE_EDIT,
    E_RECORDED_FILE_OK,
    E_RECORDED_FILE_MOVIEPLAYER,

    E_UPDATE_THUMBNAIL,
    E_CHANNEL_LIST_ENCRYPTED,
    E_CHANNEL_LIST_AV_STARTED,
    E_CHANNEL_LIST_REMOVED,
    E_SETTING_EXIT,

    E_MAINACTIVITY_VIEW_TOASTS,
    E_NO_DECODER_NOTIFY,

    E_AUTO_CHANGE_CHANNEL_TEST,
    E_CHANNEL_CHANGE_TIMEOVER,
    E_CHANNEL_SWITCHING,

    E_INTERACTIVE_ENABLE,
    E_INTERACTIVE_DISABLE,

 	E_HIDE_GESTURE,
    E_SHOW_CHANNELLIST,
    E_HIDE_CHANNELLIST,
    E_BCAS_TESTING_DIALOG,
    E_BCAS_TEST_COMPLETE_DIALOG,
    E_DEBUG_SCREEN_DISPLAY,
    E_LOG_CAPTURE_MOD_ON,

    /*E_SCAN_COMPLETED_MULTI,
    E_FIRSTVIDEO_MULTI,
    E_BADSIGNAL_CHECK_MULTI,
    E_SCAN_MONITOR_MULTI,
    E_SIGNAL_MONITER_MULTI,
    E_SIGNAL_NOTI_MSG_MULTI,
    E_NOSIGNAL_SHOW_MULTI,
    E_RATING_MONITOR_MULTI,
    E_AUTO_CHANGE_CHANNEL_TEST_MULTI,
    E_CHANNEL_CHANGE_TIMEOVER_MULTI,*/
    //E_HIDE_TITLE,
    //E_SHOW_TITLE

    E_SCAN_COMPLETED_FLOATING,
    E_SCAN_START_FLOATING,
    E_SCAN_PROCESS_FLOATING,
    E_SCAN_CANCEL_FLOATING,
    E_FIRSTVIDEO_FLOATING,
    E_BADSIGNAL_CHECK_FLOATING,
    E_SCAN_MONITOR_FLOATING,
    E_SIGNAL_MONITER_FLOATING,
    E_SIGNAL_NOTI_MSG_FLOATING,
    E_NOSIGNAL_SHOW_FLOATING,
    E_RATING_MONITOR_FLOATING,
    E_AUTO_CHANGE_CHANNEL_TEST_FLOATING,
    E_CHANNEL_CHANGE_TIMEOVER_FLOATING,
    E_CHANNEL_CHANGE_FAIL_FLOATING,
    E_CHANNEL_NAME_UPDATE_FLOATING,
    E_HIDE_FLOATING_CONTROLLER,
    E_SHOW_FLOATING_CONTROLLER,

    E_SOLUTION_MODE_SWITCHING,

    E_SCAN_COMPLETED_CHAT,
    E_FIRSTVIDEO_CHAT,
    E_BADSIGNAL_CHECK_CHAT,
    E_SCAN_MONITOR_CHAT,
    E_SIGNAL_MONITER_CHAT,
    E_SIGNAL_NOTI_MSG_CHAT,
    E_NOSIGNAL_SHOW_CHAT,
    E_RATING_MONITOR_CHAT,
    E_AUTO_CHANGE_CHANNEL_TEST_CHAT,
    E_CHANNEL_CHANGE_TIMEOVER_CHAT,
    E_CHANNEL_CHANGE_FAIL_CHAT,
    E_CHANNEL_NAME_UPDATE_CHAT,
    E_HIDE_CHAT_CONTROLER,
    E_SHOW_CHAT_CONTROLER,
    E_CHAT_SURFACE_SUB_ONOFF,
    E_CONFIRMED_PASSWORD_CHAT,  // 20170526

}
