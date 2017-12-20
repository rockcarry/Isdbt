package kr.co.fci.tv.tvSolution;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.fci.tv.FCI_TV;

import kr.co.fci.tv.FloatingWindow;
import kr.co.fci.tv.MainActivity;
import kr.co.fci.tv.TVEVENT;
import kr.co.fci.tv.buildOption;
import kr.co.fci.tv.chat.ChatMainActivity;
import kr.co.fci.tv.recording.PlayBackActivity;
import kr.co.fci.tv.saves.CommonStaticData;
import kr.co.fci.tv.util.TVlog;

import static kr.co.fci.tv.MainActivity.getCursor;

/**
 * Created by eddy.lee on 2015-06-17.
 */
public class TVBridge {

    private static String TAG = "TVBridge";
    private static int currentChid =0;
    private static boolean isScanning  = false;
    private static int progressPercent;
    private static int found=0;

    private static String scanServiceName = null;

    private static Context mContext;

    private static final int MAX_REGION_SCAN_CH_NUM = 12;

    public static int getCurrentChannel()
    {
        return CommonStaticData.lastCH;
    }

    public static void setLastRemoteKey(int _remoteKey) {
        CommonStaticData.lastRemoteKey = _remoteKey;
    }

    public static int getLastRemoteKey() {
        return CommonStaticData.lastRemoteKey;
    }

    public static void setLastSvcID(int _svcNum) {
        CommonStaticData.lastSvcID = _svcNum;
    }

    public static int getLastSvcID() {
        return CommonStaticData.lastSvcID;
    }

    public static void setLastListCount(int _cursorCount) {
        CommonStaticData.lastCursorCount = _cursorCount;
    }

    public static int getLastListCount() {
        return CommonStaticData.lastCursorCount;
    }

    public static void setLastFreq(int _Freq) {
        CommonStaticData.lastFreq = _Freq;
    }

    public static int getLastFreq() {
        return CommonStaticData.lastFreq;
    }

    public static void removeChannelDB(){
        stop();
        FCI_TVi.RemoveAllChannel();
        currentChid=0;
        progressPercent=0;
        found=0;
    }


    public static int getCurrentch()
    {
        return currentChid;
    }

    public static void scan()
    {
        stop();
        TVlog.i(TAG, "do scan...");
        FCI_TVi.RemoveAllChannel();

        currentChid=0;
        progressPercent=0;
        found=0;
        isScanning = true;

        int nRet = FCI_TVi.ScanStart();
        if (nRet != 0) {
            TVlog.i(TAG, "Channel Scan(All) Error...");
        }
    }

    public static void scan(int[] _freqCh)
    {
        if (_freqCh.length < MAX_REGION_SCAN_CH_NUM) {
            TVlog.i(TAG, "Channel Scan(Region) Error...incorrect array number");
            return;
        }

        stop();
        TVlog.i(TAG, "do scan (region)...");
        FCI_TVi.RemoveAllChannel();

        currentChid=0;
        progressPercent=0;
        found=0;
        isScanning = true;

        int nRet = FCI_TVi.ScanStart(_freqCh);
        if (nRet != 0) {
            TVlog.i(TAG, "Channel Scan(Region) Error...");
        }
    }

    public static void scan(byte _opMode)
    {
        stop();
        TVlog.i(TAG, "do scan (handover)...");
/*
        FCI_TVi.RemoveAllChannel();

        currentChid=0;
        progressPercent=0;
        found=0;
        isScanning = true;
*/

        int nRet = FCI_TVi.ScanStart(_opMode);
        if (nRet != 0) {
            TVlog.i(TAG, "Channel Scan(Handover) Error...");
        }
    }

    public static void scanStop()
    {
        FCI_TVi.ScanStop();
    }

    private static void updateChannelInfo(int _index)
    {
        /*MainActivity.getInstance().removeEvent(TVEVENT.E_SIGNAL_NOTI_MSG);
        MainActivity.getInstance().removeEvent(TVEVENT.E_BADSIGNAL_CHECK);
        MainActivity.getInstance().removeEvent(TVEVENT.E_CHANNEL_LIST_ENCRYPTED);
        CommonStaticData.lastCH=_index;
        SharedPreferences.Editor editor = CommonStaticData.settings.edit();
        editor.putInt(CommonStaticData.lastChannelKey, CommonStaticData.lastCH);
        editor.putInt(CommonStaticData.audiomodeSwitchKey, 0);
        editor.putInt(CommonStaticData.audiotrackSwitchKey, 0);
        editor.putInt(CommonStaticData.videotrackSwitchKey, 0);
        editor.putInt(CommonStaticData.captionSetKey, 0);
        editor.putInt(CommonStaticData.superimposeSetKey, 0);
        editor.commit();

        MainActivity.getInstance().recordingStop(true);
        MainActivity.getCursor().moveToPosition(CommonStaticData.lastCH);
        MainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_NAME_UPDATE);*/

        if (MainActivity.isMainActivity) {
            MainActivity.getInstance().removeEvent(TVEVENT.E_SIGNAL_NOTI_MSG);
            MainActivity.getInstance().removeEvent(TVEVENT.E_BADSIGNAL_CHECK);
            MainActivity.getInstance().removeEvent(TVEVENT.E_CHANNEL_LIST_ENCRYPTED);
            CommonStaticData.lastCH=_index;
            CommonStaticData.settings = mContext.getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = CommonStaticData.settings.edit();
            editor.putInt(CommonStaticData.lastChannelKey, CommonStaticData.lastCH);
            editor.putInt(CommonStaticData.audiomodeSwitchKey, 0);
            editor.putInt(CommonStaticData.audiotrackSwitchKey, 0);
            editor.putInt(CommonStaticData.videotrackSwitchKey, 0);
            editor.putInt(CommonStaticData.captionSetKey, 0);
            editor.putInt(CommonStaticData.superimposeSetKey, 0);
            editor.commit();

            MainActivity.getInstance().recordingStop(true);
            getCursor().moveToPosition(CommonStaticData.lastCH);
            MainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_NAME_UPDATE);
        } else if (ChatMainActivity.isChat) {
            ChatMainActivity.getInstance().removeEvent(TVEVENT.E_SIGNAL_NOTI_MSG_CHAT);
            ChatMainActivity.getInstance().removeEvent(TVEVENT.E_BADSIGNAL_CHECK_CHAT);
            //MainActivity.getInstance().removeEvent(TVEVENT.E_CHANNEL_LIST_ENCRYPTED);
            CommonStaticData.lastCH=_index;
            CommonStaticData.settings = mContext.getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = CommonStaticData.settings.edit();
            editor.putInt(CommonStaticData.lastChannelKey, CommonStaticData.lastCH);
            editor.putInt(CommonStaticData.audiomodeSwitchKey, 0);
            editor.putInt(CommonStaticData.audiotrackSwitchKey, 0);
            editor.putInt(CommonStaticData.videotrackSwitchKey, 0);
            editor.putInt(CommonStaticData.captionSetKey, 0);
            editor.putInt(CommonStaticData.superimposeSetKey, 0);
            editor.commit();

            //MainActivity.getInstance().recordingStop(true);
            if (ChatMainActivity.chat_getCursor() != null) {
                ChatMainActivity.chat_getCursor().moveToPosition(CommonStaticData.lastCH);
            }
            ChatMainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_NAME_UPDATE_CHAT);
        } else if (FloatingWindow.isFloating) {
            FloatingWindow.getInstance().removeEvent(TVEVENT.E_SIGNAL_NOTI_MSG_FLOATING);
            FloatingWindow.getInstance().removeEvent(TVEVENT.E_BADSIGNAL_CHECK_FLOATING);
            //MainActivity.getInstance().removeEvent(TVEVENT.E_CHANNEL_LIST_ENCRYPTED);
            CommonStaticData.lastCH=_index;
            CommonStaticData.settings = mContext.getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = CommonStaticData.settings.edit();
            editor.putInt(CommonStaticData.lastChannelKey, CommonStaticData.lastCH);
            editor.putInt(CommonStaticData.audiomodeSwitchKey, 0);
            editor.putInt(CommonStaticData.audiotrackSwitchKey, 0);
            editor.putInt(CommonStaticData.videotrackSwitchKey, 0);
            editor.putInt(CommonStaticData.captionSetKey, 0);
            editor.putInt(CommonStaticData.superimposeSetKey, 0);
            editor.commit();

            //MainActivity.getInstance().recordingStop(true);
            if (FloatingWindow.floating_getCursor() != null) {
                FloatingWindow.floating_getCursor().moveToPosition(CommonStaticData.lastCH);
            }
            FloatingWindow.getInstance().sendEvent(TVEVENT.E_CHANNEL_NAME_UPDATE_FLOATING);
        }
    }

    //dualdecode[[
    public static boolean dualAV_stop(int _paired)
    {
        int playMode = FCI_TVi.GetCurPlayMode();

        if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
            if (MainActivity.isMainActivity) {
                if (_paired == 0 || (playMode != FCI_TV.BB_PLAYMODE_DUAL_F_SEG && playMode != FCI_TV.BB_PLAYMODE_DUAL_O_SEG)
                        || (MainActivity.getInstance().getChannelChangView() == 0)) {
                    FCI_TVi.AVStop();
                    TVlog.i(">>>FCIISDBT>>>", "AV stoppend normally!");
                    return true;
                } else {
                    return false;
                }
            } else if (FloatingWindow.isFloating) {
                if (_paired == 0 || (playMode != FCI_TV.BB_PLAYMODE_DUAL_F_SEG && playMode != FCI_TV.BB_PLAYMODE_DUAL_O_SEG)
                        || (FloatingWindow.getInstance().getFloatingChannelChangView() == 0)) {
                    FCI_TVi.AVStop();
                    TVlog.i(">>>FCIISDBT>>>", "AV stoppend normally!");
                    return true;
                } else {
                    return false;
                }
            } else if (ChatMainActivity.isChat) {
                if (_paired == 0 || (playMode != FCI_TV.BB_PLAYMODE_DUAL_F_SEG && playMode != FCI_TV.BB_PLAYMODE_DUAL_O_SEG)
                        || (ChatMainActivity.getInstance().getChatChannelChangView() == 0)) {
                    FCI_TVi.AVStop();
                    TVlog.i(">>>FCIISDBT>>>", "AV stoppend normally!");
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            FCI_TVi.AVStop();
            TVlog.i(">>>FCIISDBT>>>", "AV stoppend normally!");
            return true;
        }
        FCI_TVi.AVStop();
        TVlog.i(">>>FCIISDBT>>>", "AV stoppend normally!");
        return true;
    }

    public static void dualAV_start(int _svcId, boolean _stopNormal) {
        int[] cur_info = FCI_TVi.GetPairNSegInfoOfCHIndex(_svcId);
        int cur_isPaired = 0;
        int cur_pairedIndex = 0;
        int cur_isFullseg = cur_info[1];
        int cur_mainIndex = cur_info[3];
        int cur_oneSegIndex = cur_info[4];
        int cur_isAudioOnly = cur_info[5];

        if (cur_info[0] >= 0) {
            cur_isPaired = 1;
            cur_pairedIndex = cur_info[0];
        }

        TVlog.i(">>>FCIISDBT>>>", " >>> cur_isPaired = " + cur_isPaired
                + ", cur_pairedIndex = " + cur_pairedIndex
                + ", cur_isFullseg = " + cur_isFullseg
                + ", cur_mainIndex = " + cur_mainIndex
                + ", cur_oneSegIndex = " + cur_oneSegIndex
                + ", cur_isAudioOnly = " + cur_isAudioOnly);

        TVlog.i(">>>FCIISDBT>>>", " >>> MainActivity.getInstance().mChannelIndex = " + MainActivity.getInstance().mChannelIndex);
        int[] last_info = FCI_TVi.GetPairNSegInfoOfCHIndex(MainActivity.getInstance().mChannelIndex);
        int last_isFullSeg = last_info[1];
        int last_mainIndex = last_info[3];
        int last_oneSegIndex = last_info[4];
        TVlog.i(">>>FCIISDBT>>>", " >>> last_isFullSeg = " + last_isFullSeg + ", last_mainIndex = " + last_mainIndex + ", last_oneSegIndex = " + last_oneSegIndex);

        if (cur_isAudioOnly == 1) {
            CommonStaticData.tuneTimeOver = false;
            if (MainActivity.isMainActivity) {
                CommonStaticData.isAudioChannel = true;
                MainActivity.getInstance().channelChangeEndView(false);
                if (MainActivity.ll_black != null) {
                    MainActivity.ll_black.setVisibility(View.VISIBLE);
                }
                if (MainActivity.ll_audioOnlyChannel != null) {
                    MainActivity.ll_audioOnlyChannel.setVisibility(View.VISIBLE);
                }
                MainActivity.getInstance().removeEvent(TVEVENT.E_CHANNEL_CHANGE_TIMEOVER);  // in after 20sec
            } else if (ChatMainActivity.isChat) {
                CommonStaticData.isAudioChannel = true;
                ChatMainActivity.getInstance().channelChangeEndView(false);
                if (ChatMainActivity.chat_ll_black != null) {
                    ChatMainActivity.chat_ll_black.setVisibility(View.VISIBLE);
                }
                if (ChatMainActivity.chat_ll_audioOnlyChannel != null) {
                    ChatMainActivity.chat_ll_audioOnlyChannel.setVisibility(View.VISIBLE);
                }
                ChatMainActivity.getInstance().removeEvent(TVEVENT.E_CHANNEL_CHANGE_TIMEOVER_CHAT);  // in after 20sec
            } else if (FloatingWindow.isFloating) {
                CommonStaticData.isAudioChannel = true;
                FloatingWindow.getInstance().channelChangeEndView(false);
                if (FloatingWindow.floating_ll_black != null) {
                    FloatingWindow.floating_ll_black.setVisibility(View.VISIBLE);
                }
                if (FloatingWindow.floating_ll_audioOnlyChannel != null) {
                    FloatingWindow.floating_ll_audioOnlyChannel.setVisibility(View.VISIBLE);
                }
                FloatingWindow.getInstance().removeEvent(TVEVENT.E_CHANNEL_CHANGE_TIMEOVER_FLOATING);  // in after 20sec
            }
        } else {
            CommonStaticData.isAudioChannel = false;
            CommonStaticData.tuneTimeOver = true;
            if (MainActivity.isMainActivity) {
                if (MainActivity.ll_black != null) {
                    MainActivity.ll_black.setVisibility(View.INVISIBLE);
                }
                if (MainActivity.ll_audioOnlyChannel != null) {
                    MainActivity.ll_audioOnlyChannel.setVisibility(View.INVISIBLE);
                }
                MainActivity.getInstance().postEvent(TVEVENT.E_CHANNEL_CHANGE_TIMEOVER, 20 * 1000);  // in after 20sec
            } else if (ChatMainActivity.isChat) {
                if (ChatMainActivity.chat_ll_black != null) {
                    ChatMainActivity.chat_ll_black.setVisibility(View.INVISIBLE);
                }
                if (ChatMainActivity.chat_ll_audioOnlyChannel != null) {
                    ChatMainActivity.chat_ll_audioOnlyChannel.setVisibility(View.INVISIBLE);
                }
                ChatMainActivity.getInstance().postEvent(TVEVENT.E_CHANNEL_CHANGE_TIMEOVER_CHAT, 20 * 1000);  // in after 20sec
            } else if (FloatingWindow.isFloating) {
                if (FloatingWindow.floating_ll_black != null) {
                    FloatingWindow.floating_ll_black.setVisibility(View.INVISIBLE);
                }
                if (FloatingWindow.floating_ll_audioOnlyChannel != null) {
                    FloatingWindow.floating_ll_audioOnlyChannel.setVisibility(View.INVISIBLE);
                }
                FloatingWindow.getInstance().postEvent(TVEVENT.E_CHANNEL_CHANGE_TIMEOVER_FLOATING, 20 * 1000);  // in after 20sec
            }
        }


        if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
            TVlog.i(">>>FCIISDBT>>>", "cur_isPaired = "+cur_isPaired+", _stopNormal = "+_stopNormal+", cur_isFullseg = "+cur_isFullseg);
            if (cur_isPaired == 1) {
                if (_stopNormal == false) {
                    if (cur_isFullseg == 1) {
                        int retSwitch  = FCI_TVi.AVSwitch(_svcId, FCI_TV.CHSTART_DUAL_F_SEG);
                        if (MainActivity.isMainActivity) {
                            if (MainActivity.ll_black != null) {
                                MainActivity.ll_black.setVisibility(View.VISIBLE);
                            }
                        } else if (FloatingWindow.isFloating) {
                            if (FloatingWindow.floating_ll_black != null) {
                                FloatingWindow.floating_ll_black.setVisibility(View.INVISIBLE);
                            }
                        } else if (ChatMainActivity.isChat) {
                            if (ChatMainActivity.chat_ll_black != null) {
                                ChatMainActivity.chat_ll_black.setVisibility(View.VISIBLE);
                            }
                        }

                        //to do if retSwitch <= 0 , switch fail
                        // 0 is player error, it may be no configuration.
                        //  if retSwitch  is smaller than 0 , it is BB error
                        if (retSwitch <= 0) {
                            FCI_TVi.AVStop();
                            FCI_TVi.AVStart(_svcId, FCI_TV.CHSTART_DUAL_F_SEG);
                            if (MainActivity.isMainActivity) {
                                if (MainActivity.ll_black != null) {
                                    MainActivity.ll_black.setVisibility(View.VISIBLE);
                                }
                                if (cur_isAudioOnly != 1) {
                                MainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_LIST_AV_STARTED);
                                }
                            } else if (FloatingWindow.isFloating) {
                                if (FloatingWindow.floating_ll_black != null) {
                                    FloatingWindow.floating_ll_black.setVisibility(View.INVISIBLE);
                                }
                                if (cur_isAudioOnly != 1) {
                                FloatingWindow.getInstance().sendEvent(TVEVENT.E_CHANNEL_LIST_AV_STARTED_FLOATING);
                                }
                            } else if (ChatMainActivity.isChat) {
                                if (ChatMainActivity.chat_ll_black != null) {
                                    ChatMainActivity.chat_ll_black.setVisibility(View.VISIBLE);
                                }
                                if (cur_isAudioOnly != 1) {
                                ChatMainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_LIST_AV_STARTED_CHAT);
                            }
                        }
                        }
                        TVlog.i(">>>FCIISDBT>>>", "AV switched to F-seg 1!  error is " + retSwitch);
                    } else {
                        int retSwitch = FCI_TVi.AVSwitch(_svcId, FCI_TV.CHSTART_DUAL_O_SEG);
                            if (MainActivity.isMainActivity) {
                            if (MainActivity.ll_black != null) {
                                MainActivity.ll_black.setVisibility(View.VISIBLE);
                            }
                            } else if (FloatingWindow.isFloating) {
                            if (FloatingWindow.floating_ll_black != null) {
                                FloatingWindow.floating_ll_black.setVisibility(View.INVISIBLE);
                            }
                            } else if (ChatMainActivity.isChat) {
                            if (ChatMainActivity.chat_ll_black != null) {
                                ChatMainActivity.chat_ll_black.setVisibility(View.VISIBLE);
                            }
                            }
                        //to do if retSwitch <= 0 , switch fail
                        // 0 is player error, it may be no configuration.
                        //  if retSwitch  is smallor than 0 , it is BB error
                        if (retSwitch <= 0) {
                            FCI_TVi.AVStop();
                            FCI_TVi.AVStart(_svcId, FCI_TV.CHSTART_DUAL_O_SEG);
                            if (MainActivity.isMainActivity) {
                                if (MainActivity.ll_black != null) {
                                    MainActivity.ll_black.setVisibility(View.VISIBLE);
                                }
                                if (cur_isAudioOnly != 1) {
                                MainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_LIST_AV_STARTED);
                                }
                            } else if (FloatingWindow.isFloating) {
                                if (FloatingWindow.floating_ll_black != null) {
                                    FloatingWindow.floating_ll_black.setVisibility(View.INVISIBLE);
                                }
                                if (cur_isAudioOnly != 1) {
                                FloatingWindow.getInstance().sendEvent(TVEVENT.E_CHANNEL_LIST_AV_STARTED_FLOATING);
                                }
                            } else if (ChatMainActivity.isChat) {
                                if (ChatMainActivity.chat_ll_black != null) {
                                    ChatMainActivity.chat_ll_black.setVisibility(View.VISIBLE);
                                }
                                if (cur_isAudioOnly != 1) {
                                ChatMainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_LIST_AV_STARTED_CHAT);
                            }
                        }
                        }
                        TVlog.i(">>>FCIISDBT>>>", "AV switched to O-seg 2! error is " + retSwitch);
                    }
                } else {
                    if (cur_isFullseg == 1) {  //to F-seg
                            FCI_TVi.AVStop();
                            FCI_TVi.AVStart(_svcId, FCI_TV.CHSTART_DUAL_F_SEG);
                        //FCI_TVi.AVStart(_svcId, FCI_TV.CHSTART_SINGLE);
                        if (MainActivity.isMainActivity) {
                            if (MainActivity.ll_black != null) {
                                MainActivity.ll_black.setVisibility(View.VISIBLE);
                        }
                            if (cur_isAudioOnly != 1) {
                            MainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_LIST_AV_STARTED);
                            }
                        } else if (FloatingWindow.isFloating) {
                            if (FloatingWindow.floating_ll_black != null) {
                                FloatingWindow.floating_ll_black.setVisibility(View.INVISIBLE);
                            }
                            if (cur_isAudioOnly != 1) {
                            FloatingWindow.getInstance().sendEvent(TVEVENT.E_CHANNEL_LIST_AV_STARTED_FLOATING);
                            }
                        } else if (ChatMainActivity.isChat) {
                            if (ChatMainActivity.chat_ll_black != null) {
                                ChatMainActivity.chat_ll_black.setVisibility(View.VISIBLE);
                            }
                            if (cur_isAudioOnly != 1) {
                            ChatMainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_LIST_AV_STARTED_CHAT);
                        }
                        }
                        TVlog.i(">>>FCIISDBT>>>", "AV started single F-seg! 1");
                    } else {  //to O-seg
                        FCI_TVi.AVStop();
                        FCI_TVi.AVStart(_svcId, FCI_TV.CHSTART_DUAL_O_SEG);
                            TVlog.i(">>>FCIISDBT>>>", "AV started dual O-seg! 2");
                        //FCI_TVi.AVStart(_svcId, FCI_TV.CHSTART_SINGLE);
                        if (MainActivity.isMainActivity) {
                            if (MainActivity.ll_black != null) {
                                MainActivity.ll_black.setVisibility(View.VISIBLE);
                            }
                            if (cur_isAudioOnly != 1) {
                            MainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_LIST_AV_STARTED);
                            }
                        } else if (FloatingWindow.isFloating) {
                            if (FloatingWindow.floating_ll_black != null) {
                                FloatingWindow.floating_ll_black.setVisibility(View.INVISIBLE);
                            }
                            if (cur_isAudioOnly != 1) {
                            FloatingWindow.getInstance().sendEvent(TVEVENT.E_CHANNEL_LIST_AV_STARTED_FLOATING);
                            }
                        } else if (ChatMainActivity.isChat) {
                            if (ChatMainActivity.chat_ll_black != null) {
                                ChatMainActivity.chat_ll_black.setVisibility(View.VISIBLE);
                            }
                            if (cur_isAudioOnly != 1) {
                            ChatMainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_LIST_AV_STARTED_CHAT);
                        }
                        }

                    }
                }
            } else {
                //TVlog.i(">>>FCIISDBT>>>", "last_mainIndex = "+last_mainIndex+", cur_mainIndex = "+cur_mainIndex);

                /*if (last_mainIndex == cur_mainIndex) {
                    if (_stopNormal == false) {

                    } else {

                    }
                    if (last_isFullSeg == 0 && last_mainIndex != -1) {
                        if (cur_isFullseg == 1) {  // to F-seg
                            int retSwitch = FCI_TVi.AVSwitch(_svcId, FCI_TV.CHSTART_DUAL_F_SEG);

                            //FCI_TVi.AVSwitch(_svcId, FCI_TV.CHSTART_DUAL_F_SEG);
                        //to do if retSwitch <= 0 , switch fail
                        // 0 is player error, it may be no configuration.
                        //  if retSwitch  is smallor than 0 , it is BB error
                        if (retSwitch <= 0) {
                                FCI_TVi.AVStop();
                                FCI_TVi.AVStart(_svcId, FCI_TV.CHSTART_DUAL_F_SEG);
                            if (MainActivity.isMainActivity) {
                                    if (MainActivity.ll_black != null) {
                                        MainActivity.ll_black.setVisibility(View.VISIBLE);
                                    }
                            } else if (FloatingWindow.isFloating) {
                                    if (FloatingWindow.floating_ll_black != null) {
                                        FloatingWindow.floating_ll_black.setVisibility(View.INVISIBLE);
                                    }
                            } else if (ChatMainActivity.isChat) {
                                    if (ChatMainActivity.chat_ll_black != null) {
                                        ChatMainActivity.chat_ll_black.setVisibility(View.VISIBLE);
                                    }
                            }
                            }
                            TVlog.i(">>>FCIISDBT>>>", "AV switched dual F-seg! 3");
                        } else {  //to O-seg
                            int retSwitch = FCI_TVi.AVSwitch(_svcId, FCI_TV.CHSTART_DUAL_O_SEG);
                            //FCI_TVi.AVSwitch(_svcId, FCI_TV.CHSTART_DUAL_O_SEG);
                            //to do if retSwitch <= 0 , switch fail
                            // 0 is player error, it may be no configuration.
                            //  if retSwitch  is smallor than 0 , it is BB error
                            if (retSwitch <= 0) {
                            FCI_TVi.AVStop();
                            FCI_TVi.AVStart(_svcId, FCI_TV.CHSTART_DUAL_O_SEG);
                                if (MainActivity.isMainActivity) {
                                    if (MainActivity.ll_black != null) {
                                        MainActivity.ll_black.setVisibility(View.VISIBLE);
                                    }
                                } else if (FloatingWindow.isFloating) {
                                    if (FloatingWindow.floating_ll_black != null) {
                                        FloatingWindow.floating_ll_black.setVisibility(View.INVISIBLE);
                                    }
                                } else if (ChatMainActivity.isChat) {
                                    if (ChatMainActivity.chat_ll_black != null) {
                                        ChatMainActivity.chat_ll_black.setVisibility(View.VISIBLE);
                        }
                                }
                            }
                            TVlog.i(">>>FCIISDBT>>>", "AV switched dual O-seg! 4");
                    }
                } else {
                        if (cur_isFullseg == 1) {  // to F-seg
                            FCI_TVi.AVStop();
                        FCI_TVi.AVStart(_svcId, FCI_TV.CHSTART_DUAL_F_SEG);
                            if (MainActivity.isMainActivity) {
                                if (MainActivity.ll_black != null) {
                                    MainActivity.ll_black.setVisibility(View.VISIBLE);
                                }
                            } else if (FloatingWindow.isFloating) {
                                if (FloatingWindow.floating_ll_black != null) {
                                    FloatingWindow.floating_ll_black.setVisibility(View.INVISIBLE);
                                }
                            } else if (ChatMainActivity.isChat) {
                                if (ChatMainActivity.chat_ll_black != null) {
                                    ChatMainActivity.chat_ll_black.setVisibility(View.VISIBLE);
                                }
                            }
                            MainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_LIST_AV_STARTED);
                            TVlog.i(">>>FCIISDBT>>>", "AV started dual F-seg! 5");
                        } else {  //to O-seg
                        FCI_TVi.AVStart(_svcId, FCI_TV.CHSTART_DUAL_O_SEG);
                            if (MainActivity.isMainActivity) {
                                if (MainActivity.ll_black != null) {
                                    MainActivity.ll_black.setVisibility(View.VISIBLE);
                                }
                            } else if (FloatingWindow.isFloating) {
                                if (FloatingWindow.floating_ll_black != null) {
                                    FloatingWindow.floating_ll_black.setVisibility(View.INVISIBLE);
                                }
                            } else if (ChatMainActivity.isChat) {
                                if (ChatMainActivity.chat_ll_black != null) {
                                    ChatMainActivity.chat_ll_black.setVisibility(View.VISIBLE);
                                }
                            }
                            MainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_LIST_AV_STARTED);
                            TVlog.i(">>>FCIISDBT>>>", "AV started dual O-seg! 6");
                    }
                }
            } else {
                    if (cur_isFullseg == 1) {  // to F-seg
                        FCI_TVi.AVStop();
                        FCI_TVi.AVStart(_svcId, FCI_TV.CHSTART_DUAL_F_SEG);
                        if (MainActivity.isMainActivity) {
                            if (MainActivity.ll_black != null) {
                                MainActivity.ll_black.setVisibility(View.VISIBLE);
                            }
                        } else if (FloatingWindow.isFloating) {
                            if (FloatingWindow.floating_ll_black != null) {
                                FloatingWindow.floating_ll_black.setVisibility(View.INVISIBLE);
                            }
                        } else if (ChatMainActivity.isChat) {
                            if (ChatMainActivity.chat_ll_black != null) {
                                ChatMainActivity.chat_ll_black.setVisibility(View.VISIBLE);
                            }
                        }
                        MainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_LIST_AV_STARTED);
                        TVlog.i(">>>FCIISDBT>>>", "AV started dual F-seg! 7");
                    } else {  //to O-seg
                        FCI_TVi.AVStop();
                        FCI_TVi.AVStart(_svcId, FCI_TV.CHSTART_DUAL_O_SEG);
                        if (MainActivity.isMainActivity) {
                            if (MainActivity.ll_black != null) {
                                MainActivity.ll_black.setVisibility(View.VISIBLE);
                            }
                        } else if (FloatingWindow.isFloating) {
                            if (FloatingWindow.floating_ll_black != null) {
                                FloatingWindow.floating_ll_black.setVisibility(View.INVISIBLE);
                }
                        } else if (ChatMainActivity.isChat) {
                            if (ChatMainActivity.chat_ll_black != null) {
                                ChatMainActivity.chat_ll_black.setVisibility(View.VISIBLE);
                            }
                        }
                        MainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_LIST_AV_STARTED);
                        TVlog.i(">>>FCIISDBT>>>", "AV started dual O-seg! 8");
                    }
                }*/



                /*if (cur_isFullseg == 1) {  // to F-seg
                    FCI_TVi.AVStop();
                    FCI_TVi.AVStart(_svcId, FCI_TV.CHSTART_DUAL_F_SEG);
                    if (MainActivity.isMainActivity) {
                        if (MainActivity.ll_black != null) {
                            MainActivity.ll_black.setVisibility(View.VISIBLE);
                        }
                        if (cur_isAudioOnly != 1) {
                        MainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_LIST_AV_STARTED);
                        }
                    } else if (FloatingWindow.isFloating) {
                        if (FloatingWindow.floating_ll_black != null) {
                            FloatingWindow.floating_ll_black.setVisibility(View.INVISIBLE);
                        }
                        if (cur_isAudioOnly != 1) {
                        FloatingWindow.getInstance().sendEvent(TVEVENT.E_CHANNEL_LIST_AV_STARTED_FLOATING);
                        }
                    } else if (ChatMainActivity.isChat) {
                        if (ChatMainActivity.chat_ll_black != null) {
                            ChatMainActivity.chat_ll_black.setVisibility(View.VISIBLE);
                        }
                        if (cur_isAudioOnly != 1) {
                        ChatMainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_LIST_AV_STARTED_CHAT);
                    }
                    }
                    TVlog.i(">>>FCIISDBT>>>", "AV started dual F-seg! 7");
                } else {  //to O-seg
                    FCI_TVi.AVStop();
                    FCI_TVi.AVStart(_svcId, FCI_TV.CHSTART_DUAL_O_SEG);
                    if (MainActivity.isMainActivity) {
                        if (MainActivity.ll_black != null) {
                            MainActivity.ll_black.setVisibility(View.VISIBLE);
                        }
                        if (cur_isAudioOnly != 1) {
                        MainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_LIST_AV_STARTED);
                        }
                    } else if (FloatingWindow.isFloating) {
                        if (FloatingWindow.floating_ll_black != null) {
                            FloatingWindow.floating_ll_black.setVisibility(View.INVISIBLE);
                        }
                        if (cur_isAudioOnly != 1) {
                        FloatingWindow.getInstance().sendEvent(TVEVENT.E_CHANNEL_LIST_AV_STARTED_FLOATING);
                        }
                    } else if (ChatMainActivity.isChat) {
                        if (ChatMainActivity.chat_ll_black != null) {
                            ChatMainActivity.chat_ll_black.setVisibility(View.VISIBLE);
                        }
                        if (cur_isAudioOnly != 1) {
                        ChatMainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_LIST_AV_STARTED_CHAT);
                    }
                    }
                    TVlog.i(">>>FCIISDBT>>>", "AV started dual O-seg! 8");
                }*/


                if (MainActivity.isMainActivity) {
                    FCI_TVi.AVStart(_svcId, FCI_TV.CHSTART_SINGLE);
                    if (cur_isAudioOnly != 1) {
                        MainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_LIST_AV_STARTED);
                    }
                    if (cur_isFullseg == 1) {
                        TVlog.i(">>>FCIISDBT>>>", "AV started single F-seg!");
                    } else {
                        TVlog.i(">>>FCIISDBT>>>", "AV started single O-seg!");
                    }
                } else if (FloatingWindow.isFloating) {
                    FCI_TVi.AVStart(_svcId, FCI_TV.CHSTART_SINGLE);
                    if (cur_isAudioOnly != 1) {
                        FloatingWindow.getInstance().sendEvent(TVEVENT.E_CHANNEL_LIST_AV_STARTED_FLOATING);
                    }
                    if (cur_isFullseg == 1) {
                        TVlog.i(">>>FCIISDBT>>>", "AV started single F-seg!");
                    } else {
                        TVlog.i(">>>FCIISDBT>>>", "AV started single O-seg!");
                    }
                } else if (ChatMainActivity.isChat) {
                    FCI_TVi.AVStart(_svcId, FCI_TV.CHSTART_SINGLE);
                    if (cur_isAudioOnly != 1) {
                        ChatMainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_LIST_AV_STARTED_CHAT);
                    }
                    if (cur_isFullseg == 1) {
                        TVlog.i(">>>FCIISDBT>>>", "AV started single F-seg!");
                    } else {
                        TVlog.i(">>>FCIISDBT>>>", "AV started single O-seg!");
                    }
            }
        }
        } else {
            if (MainActivity.isMainActivity) {
            FCI_TVi.AVStart(_svcId, FCI_TV.CHSTART_SINGLE);
                if (cur_isAudioOnly != 1) {
                MainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_LIST_AV_STARTED);
                }
                if (cur_isFullseg == 1) {
                TVlog.i(">>>FCIISDBT>>>", "AV started single F-seg!");
            } else {
                TVlog.i(">>>FCIISDBT>>>", "AV started single O-seg!");
            }
            } else if (FloatingWindow.isFloating) {
                FCI_TVi.AVStart(_svcId, FCI_TV.CHSTART_SINGLE);
                if (cur_isAudioOnly != 1) {
                FloatingWindow.getInstance().sendEvent(TVEVENT.E_CHANNEL_LIST_AV_STARTED_FLOATING);
                }
                if (cur_isFullseg == 1) {
                    TVlog.i(">>>FCIISDBT>>>", "AV started single F-seg!");
                } else {
                    TVlog.i(">>>FCIISDBT>>>", "AV started single O-seg!");
                }
            } else if (ChatMainActivity.isChat) {
                FCI_TVi.AVStart(_svcId, FCI_TV.CHSTART_SINGLE);
                if (cur_isAudioOnly != 1) {
                ChatMainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_LIST_AV_STARTED_CHAT);
                }
                if (cur_isFullseg == 1) {
                    TVlog.i(">>>FCIISDBT>>>", "AV started single F-seg!");
                } else {
                    TVlog.i(">>>FCIISDBT>>>", "AV started single O-seg!");
                }
        }
    }
    }
    //]]dualdecode

    public static void serviceID_start(int _id)
    {
        if(_id <0 || _id >= found  || found==0)
        {
            TVlog.i(TAG, "no channel");
            if (FCI_TVi.initiatedSol) {
                if (MainActivity.isMainActivity) {
                    sendEventToMain(TVEVENT.E_CHANNEL_CHANGE_FAIL);
                } else if (ChatMainActivity.isChat) {
                    ChatMainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_CHANGE_FAIL_CHAT);
                } else if (FloatingWindow.isFloating) {
                    FloatingWindow.getInstance().sendEvent(TVEVENT.E_CHANNEL_CHANGE_FAIL_FLOATING);
                }
            }

            return;
        }
        CommonStaticData.scanningNow = true;    // disable no signal noti

        currentChid = _id;
        TVlog.i(TAG, "========================================================================");
        TVlog.i(TAG, " ID found = " + found + " channel change  " + CommonStaticData.lastCH + " ->  " + currentChid);
        TVlog.i(TAG, "========================================================================");

        //check AV mode & switch
        int[] last_info = FCI_TVi.GetPairNSegInfoOfCHIndex(CommonStaticData.lastCH);
        int last_isPaired = 0;
        int last_pairedIndex = last_info[0];
        int last_sameVideoIndex = last_info[2];
        int last_mainIndex = last_info[3];
        boolean stopNormal;

        int[] cur_info = FCI_TVi.GetPairNSegInfoOfCHIndex(currentChid);
        int cur_mainIndex = cur_info[3];
        int cur_isAudioOnly = cur_info[5];

        if (last_pairedIndex == currentChid) {
            last_isPaired = 1;
        } else {
            last_isPaired = 0;
        }
        stopNormal = dualAV_stop(last_isPaired);
        if (last_mainIndex != cur_mainIndex) {
            CommonStaticData.fromFindFail = false;
        }

        /*if (last_mainIndex == cur_mainIndex) {
            stopNormal = dualAV_stop(1);
        } else {
            stopNormal = dualAV_stop(0);
            MainActivity.fromFindFail = false;
        }*/
        TVlog.i(">>>FCIISDBT>>>", " stopNormal1 = "+stopNormal);

        if (FCI_TVi.initiatedSol) {
            if (MainActivity.isMainActivity) {
                MainActivity.getInstance().removeEvent(TVEVENT.E_CHANNEL_CHANGE_TIMEOVER);
            } else if (ChatMainActivity.isChat) {
                ChatMainActivity.getInstance().removeEvent(TVEVENT.E_CHANNEL_CHANGE_TIMEOVER_CHAT);
            } else if (FloatingWindow.isFloating) {
                FloatingWindow.getInstance().removeEvent(TVEVENT.E_CHANNEL_CHANGE_TIMEOVER_FLOATING);
            }
        }

        // put last channel
        updateChannelInfo(_id);

        //start AV
        dualAV_start(currentChid, stopNormal);

        CommonStaticData.scanningNow = false;    // enalble no signal noti

        //ADD_GINGA_NCL[[
        if (buildOption.ADD_GINGA_NCL==true) {
            if(mContext !=null)  ((MainActivity)mContext).restartNCLDemux();
        }
        //]]ADD_GINGA_NCL
    }


    public static void stop()
    {
        TVlog.i(TAG, " ==> TVBridge.stop()");
        FCI_TVi.AVStop();
        //MainActivity.fromFindFail = false;  //live add
        //ADD_GINGA_NCL[[
        if (buildOption.ADD_GINGA_NCL==true) {
            if(mContext !=null)  ((MainActivity)mContext).stopNCLDemux();
        }
        //]]ADD_GINGA_NCL
    }


    public static void AVStartPlus()
    {
        CommonStaticData.scanningNow = true;    // disable no signal noti
        int[] last_info = FCI_TVi.GetPairNSegInfoOfCHIndex(CommonStaticData.lastCH);
        int last_isPaired = 0;
        int last_pairedIndex = last_info[0];
        int last_sameVideoIndex = last_info[2];
        int last_mainIndex = last_info[3];
        boolean stopNormal;

        int[] cur_info = FCI_TVi.GetPairNSegInfoOfCHIndex(currentChid);
        int cur_mainIndex = cur_info[3];
        int cur_isAudioOnly = cur_info[5];

        if (MainActivity.isMainActivity) {
            ((MainActivity) mContext).removeEvent(TVEVENT.E_CHANNEL_CHANGE_TIMEOVER);
            isScanning = false;
            if (MainActivity.ll_age_limit.getVisibility() == View.VISIBLE) {
                MainActivity.ll_age_limit.setVisibility(View.INVISIBLE);
            }
            if (found == 0) {
                TVlog.i(TAG, "no channel");
                sendEventToMain(TVEVENT.E_CHANNEL_CHANGE_FAIL);
                return;
            }

            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                currentChid = CommonStaticData.lastCH;  //live
                switch (CommonStaticData.receivemode) {
                    case 0:  // 1seg
                        do {
                            currentChid++;
                            if (currentChid == found) {
                                currentChid = 0;
                            }
                            getCursor().moveToPosition(currentChid);
                        }
                        while (getCursor().getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV) != 0);

                        break;
                    case 1:  // fullseg
                        do{
                            currentChid++;
                            if(currentChid == found)
                            {
                                currentChid = 0;
                            }
                            getCursor().moveToPosition(currentChid);
                        }
                        while (getCursor().getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV)!= 1);


                        break;
                    case 2:  // auto
                    case 3:  // off
                        currentChid++;
                        if (currentChid == found) {
                            currentChid = 0;
                        }
                        break;

                }
            } else {
                currentChid++;    // org
                if(currentChid == found) {
                    currentChid = 0;
                }
            }

            //stop AV
            if (last_pairedIndex == currentChid) {
                last_isPaired = 1;
            } else {
                last_isPaired = 0;
            }
            stopNormal = dualAV_stop(last_isPaired);
            if (last_mainIndex != cur_mainIndex) {
                CommonStaticData.fromFindFail = false;
            }

            /*if (last_mainIndex == cur_mainIndex) {
                stopNormal = dualAV_stop(1);
            } else {
                stopNormal = dualAV_stop(0);
                MainActivity.fromFindFail = false;
            }*/
            TVlog.i(">>>FCIISDBT>>>", " stopNormal2 = "+stopNormal);


            TVlog.i(TAG, "========================================================================");
            TVlog.i(TAG, "Plus found = " + found + " channel change  "+CommonStaticData.lastCH+ " ->  "+currentChid );
            TVlog.i(TAG, "========================================================================" );

            updateChannelInfo(currentChid);

            //start AV
            dualAV_start(currentChid, stopNormal);

            CommonStaticData.scanningNow = false;    // enalble no signal noti

            //ADD_GINGA_NCL[[
            if (buildOption.ADD_GINGA_NCL==true) {
                if(mContext !=null)  ((MainActivity)mContext).restartNCLDemux();
            }
            //]]ADD_GINGA_NCL
        } else if (FloatingWindow.isFloating) {
            FloatingWindow.getInstance().removeEvent(TVEVENT.E_CHANNEL_CHANGE_TIMEOVER_FLOATING);
            isScanning = false;
            if (FloatingWindow.getInstance().floating_ll_age_limit.getVisibility() == View.VISIBLE) {
                FloatingWindow.getInstance().floating_ll_age_limit.setVisibility(View.INVISIBLE);
            }
            if (found == 0) {
                TVlog.i(TAG, "no channel");
                FloatingWindow.getInstance().sendEvent(TVEVENT.E_CHANNEL_CHANGE_FAIL_FLOATING);
                return;
            }

            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                currentChid = CommonStaticData.lastCH;  //live
                switch (CommonStaticData.receivemode) {
                    case 0:  // 1seg
                        do {
                            currentChid++;
                            if (currentChid == found) {
                                currentChid = 0;
                            }
                            FloatingWindow.floating_getCursor().moveToPosition(currentChid);
                        }
                        while (FloatingWindow.floating_getCursor().getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV) != 0);

                        break;
                    case 1:  // fullseg
                        do{
                            currentChid++;
                            if(currentChid == found)
                            {
                                currentChid = 0;
                            }
                            FloatingWindow.floating_getCursor().moveToPosition(currentChid);
                        }
                        while (FloatingWindow.floating_getCursor().getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV)!= 1);


                        break;
                    case 2:  // auto
                    case 3:  // off
                        currentChid++;
                        if (currentChid == found) {
                            currentChid = 0;
                        }
                        break;

                }
            } else {
                currentChid++;    // org
                if(currentChid == found) {
                    currentChid = 0;
                }
            }

            //stop AV
            if (last_pairedIndex == currentChid) {
                last_isPaired = 1;
            } else {
                last_isPaired = 0;
            }
            stopNormal = dualAV_stop(last_isPaired);
            if (last_mainIndex != cur_mainIndex) {
                CommonStaticData.fromFindFail = false;
            }

            /*if (last_mainIndex == cur_mainIndex) {
                stopNormal = dualAV_stop(1);
            } else {
                stopNormal = dualAV_stop(0);
                MainActivity.fromFindFail = false;
            }*/
            TVlog.i(">>>FCIISDBT>>>", " stopNormal3 = "+stopNormal);

            TVlog.i(TAG, "========================================================================");
            TVlog.i(TAG, "Plus found = " + found + " channel change  "+CommonStaticData.lastCH+ " ->  "+currentChid);
            TVlog.i(TAG, "========================================================================" );

            updateChannelInfo(currentChid);

            //start AV
            dualAV_start(currentChid, stopNormal);

            CommonStaticData.scanningNow = false;    // enalble no signal noti

            //ADD_GINGA_NCL[[
            if (buildOption.ADD_GINGA_NCL==true) {
                if(mContext !=null)  ((MainActivity)mContext).restartNCLDemux();
            }
            //]]ADD_GINGA_NCL
        } else if (ChatMainActivity.isChat) {
            ChatMainActivity.getInstance().removeEvent(TVEVENT.E_CHANNEL_CHANGE_TIMEOVER_CHAT);
            isScanning = false;
            if (ChatMainActivity.getInstance().chat_ll_age_limit.getVisibility() == View.VISIBLE) {
                ChatMainActivity.getInstance().chat_ll_age_limit.setVisibility(View.INVISIBLE);
            }
            if (found == 0) {
                TVlog.i(TAG, "no channel");
                ChatMainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_CHANGE_FAIL_CHAT);
                return;
            }

            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                currentChid = CommonStaticData.lastCH;  //live
                switch (CommonStaticData.receivemode) {
                    case 0:  // 1seg
                        do {
                            currentChid++;
                            if (currentChid == found) {
                                currentChid = 0;
                            }
                            ChatMainActivity.chat_getCursor().moveToPosition(currentChid);
                        }
                        while (ChatMainActivity.chat_getCursor().getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV) != 0);

                        break;
                    case 1:  // fullseg
                        do{
                            currentChid++;
                            if(currentChid == found)
                            {
                                currentChid = 0;
                            }
                            ChatMainActivity.chat_getCursor().moveToPosition(currentChid);
                        }
                        while (ChatMainActivity.chat_getCursor().getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV)!= 1);


                        break;
                    case 2:  // auto
                    case 3:  // off
                        currentChid++;
                        if (currentChid == found) {
                            currentChid = 0;
                        }
                        break;

                }
            } else {
                currentChid++;    // org
                if(currentChid == found) {
                    currentChid = 0;
                }
            }

            if (last_pairedIndex == currentChid) {
                last_isPaired = 1;
            } else {
                last_isPaired = 0;
            }
            stopNormal = dualAV_stop(last_isPaired);
            if (last_mainIndex != cur_mainIndex) {
                CommonStaticData.fromFindFail = false;
            }

            /*if (last_mainIndex == cur_mainIndex) {
                stopNormal = dualAV_stop(1);
            } else {
                stopNormal = dualAV_stop(0);
                MainActivity.fromFindFail = false;
            }*/
            TVlog.i(">>>FCIISDBT>>>", " stopNormal4 = "+stopNormal);

            TVlog.i(TAG, "========================================================================");
            TVlog.i(TAG, "Plus found = " + found + " channel change  "+CommonStaticData.lastCH+ " ->  "+currentChid);
            TVlog.i(TAG, "========================================================================" );

            updateChannelInfo(currentChid);

            dualAV_start(currentChid, stopNormal);

            CommonStaticData.scanningNow = false;    // enalble no signal noti

            //ADD_GINGA_NCL[[
            if (buildOption.ADD_GINGA_NCL==true) {
                if(mContext !=null)  ((MainActivity)mContext).restartNCLDemux();
            }
            //]]ADD_GINGA_NCL
        }


    }

    public static void AVStartMinus()
    {
        CommonStaticData.scanningNow = true;    // disable no signal noti
        int[] last_info = FCI_TVi.GetPairNSegInfoOfCHIndex(CommonStaticData.lastCH);
        int last_isPaired = 0;
        int last_pairedIndex = last_info[0];
        int last_sameVideoIndex = last_info[2];
        int last_mainIndex = last_info[3];
        boolean stopNormal;

        int[] cur_info = FCI_TVi.GetPairNSegInfoOfCHIndex(currentChid);
        int cur_mainIndex = cur_info[3];
        int cur_isAudioOnly = cur_info[5];

        if (MainActivity.isMainActivity) {
            ((MainActivity) mContext).removeEvent(TVEVENT.E_CHANNEL_CHANGE_TIMEOVER);
            isScanning= false;
            if (MainActivity.ll_age_limit.getVisibility() == View.VISIBLE) {
                MainActivity.ll_age_limit.setVisibility(View.INVISIBLE);
            }
            if (found == 0) {
                TVlog.i(TAG, "no channel");
                sendEventToMain(TVEVENT.E_CHANNEL_CHANGE_FAIL);
                return;
            }

            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                currentChid = CommonStaticData.lastCH;  //live
                switch (CommonStaticData.receivemode) {
                    case 0: // 1seg
                        do {
                            currentChid--;
                            if (currentChid < 0) {
                                currentChid = found - 1;
                            }
                            getCursor().moveToPosition(currentChid);
                        }
                        while (getCursor().getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV) != 0);


                        break;
                    case 1:  // fullseg
                        do{
                            currentChid--;
                            if(currentChid <0)
                            {
                                currentChid=found-1;
                            }
                            getCursor().moveToPosition(currentChid);
                        }
                        while (getCursor().getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV)!=1);


                        break;
                    case 2:  // auto
                    case 3:  // off
                        currentChid--;
                        if(currentChid <0)
                        {
                            currentChid=found-1;
                        }
                        break;

                }
            } else {
                currentChid--;    // org
                if(currentChid < 0)
                {
                    currentChid=found-1;
                }
            }

            if (last_pairedIndex == currentChid) {
                last_isPaired = 1;
            } else {
                last_isPaired = 0;
            }
            stopNormal = dualAV_stop(last_isPaired);
            if (last_mainIndex != cur_mainIndex) {
                CommonStaticData.fromFindFail = false;
            }

            /*if (last_mainIndex == cur_mainIndex) {
                stopNormal = dualAV_stop(1);
            } else {
                stopNormal = dualAV_stop(0);
                MainActivity.fromFindFail = false;
            }*/
            TVlog.i(">>>FCIISDBT>>>", " stopNormal5 = "+stopNormal);

            TVlog.i(TAG, "========================================================================");
            TVlog.i(TAG, " Minus found = " + found + " channel change  "+CommonStaticData.lastCH+ " ->  "+currentChid );
            TVlog.i(TAG, "========================================================================" );
            updateChannelInfo(currentChid);

            dualAV_start(currentChid, stopNormal);

            CommonStaticData.scanningNow = false;    // enalble no signal noti

            //ADD_GINGA_NCL[[
            if (buildOption.ADD_GINGA_NCL==true) {
                if(mContext !=null)  ((MainActivity)mContext).restartNCLDemux();
            }
            //]]ADD_GINGA_NCL
        } else if (FloatingWindow.isFloating) {
            FloatingWindow.getInstance().removeEvent(TVEVENT.E_CHANNEL_CHANGE_TIMEOVER_FLOATING);
            isScanning = false;
            if (FloatingWindow.getInstance().floating_ll_age_limit.getVisibility() == View.VISIBLE) {
                FloatingWindow.getInstance().floating_ll_age_limit.setVisibility(View.INVISIBLE);
            }
            if (found == 0) {
                TVlog.i(TAG, "no channel");
                FloatingWindow.getInstance().sendEvent(TVEVENT.E_CHANNEL_CHANGE_FAIL_FLOATING);
                return;
            }

            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                currentChid = CommonStaticData.lastCH;  //live
                switch (CommonStaticData.receivemode) {
                    case 0: // 1seg
                        do {
                            currentChid--;
                            if (currentChid < 0) {
                                currentChid = found - 1;
                            }
                            FloatingWindow.floating_getCursor().moveToPosition(currentChid);
                        }
                        while (FloatingWindow.floating_getCursor().getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV) != 0);


                        break;
                    case 1:  // fullseg
                        do{
                            currentChid--;
                            if(currentChid < 0)
                            {
                                currentChid = found - 1;
                            }
                            FloatingWindow.floating_getCursor().moveToPosition(currentChid);
                        }
                        while (FloatingWindow.floating_getCursor().getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV)!=1);


                        break;
                    case 2:  // auto
                    case 3:  // off
                        currentChid--;
                        if(currentChid < 0) {
                            currentChid = found - 1;
                        }
                        break;

                }
            } else {
                currentChid--;    // org
                if(currentChid < 0)
                {
                    currentChid = found - 1;
                }
            }

            if (last_pairedIndex == currentChid) {
                last_isPaired = 1;
            } else {
                last_isPaired = 0;
            }
            stopNormal = dualAV_stop(last_isPaired);
            if (last_mainIndex != cur_mainIndex) {
                CommonStaticData.fromFindFail = false;
            }

            /*if (last_mainIndex == cur_mainIndex) {
                stopNormal = dualAV_stop(1);
            } else {
                stopNormal = dualAV_stop(0);
                MainActivity.fromFindFail = false;
            }*/
            TVlog.i(">>>FCIISDBT>>>", " stopNormal6 = "+stopNormal);

            TVlog.i(TAG, "========================================================================");
            TVlog.i(TAG, " Minus found = " + found + " channel change  "+CommonStaticData.lastCH+ " ->  "+currentChid);
            TVlog.i(TAG, "========================================================================" );
            updateChannelInfo(currentChid);

            dualAV_start(currentChid, stopNormal);

            CommonStaticData.scanningNow = false;    // enalble no signal noti

            //ADD_GINGA_NCL[[
            if (buildOption.ADD_GINGA_NCL==true) {
                if(mContext !=null)  ((MainActivity)mContext).restartNCLDemux();
            }
            //]]ADD_GINGA_NCL
        } else if (ChatMainActivity.isChat) {
            ChatMainActivity.getInstance().removeEvent(TVEVENT.E_CHANNEL_CHANGE_TIMEOVER_CHAT);
            isScanning = false;
            if (ChatMainActivity.getInstance().chat_ll_age_limit.getVisibility() == View.VISIBLE) {
                ChatMainActivity.getInstance().chat_ll_age_limit.setVisibility(View.INVISIBLE);
            }
            if (found == 0) {
                TVlog.i(TAG, "no channel");
                ChatMainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_CHANGE_FAIL_CHAT);
                return;
            }

            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                currentChid = CommonStaticData.lastCH;  //live
                switch (CommonStaticData.receivemode) {
                    case 0: // 1seg
                        do {
                            currentChid--;
                            if (currentChid < 0) {
                                currentChid = found - 1;
                            }
                            ChatMainActivity.chat_getCursor().moveToPosition(currentChid);
                        }
                        while (ChatMainActivity.chat_getCursor().getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV) != 0);


                        break;
                    case 1:  // fullseg
                        do{
                            currentChid--;
                            if(currentChid < 0)
                            {
                                currentChid = found - 1;
                            }
                            ChatMainActivity.chat_getCursor().moveToPosition(currentChid);
                        }
                        while (ChatMainActivity.chat_getCursor().getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV)!=1);


                        break;
                    case 2:  // auto
                    case 3:  // off
                        currentChid--;
                        if(currentChid < 0) {
                            currentChid = found - 1;
                        }
                        break;

                }
            } else {
                currentChid--;    // org
                if(currentChid < 0)
                {
                    currentChid = found - 1;
                }
            }

            if (last_pairedIndex == currentChid) {
                last_isPaired = 1;
            } else {
                last_isPaired = 0;
            }
            stopNormal = dualAV_stop(last_isPaired);
            if (last_mainIndex != cur_mainIndex) {
                CommonStaticData.fromFindFail = false;
            }

            /*if (last_mainIndex == cur_mainIndex) {
                stopNormal = dualAV_stop(1);
            } else {
                stopNormal = dualAV_stop(0);
                MainActivity.fromFindFail = false;
            }*/
            TVlog.i(">>>FCIISDBT>>>", " stopNormal7 = "+stopNormal);

            TVlog.i(TAG, "========================================================================");
            TVlog.i(TAG, " Minus found = " + found + " channel change  "+CommonStaticData.lastCH+ " ->  "+currentChid);
            TVlog.i(TAG, "========================================================================" );
            updateChannelInfo(currentChid);

            dualAV_start(currentChid, stopNormal);

            CommonStaticData.scanningNow = false;    // enalble no signal noti

            //ADD_GINGA_NCL[[
            if (buildOption.ADD_GINGA_NCL==true) {
                if(mContext !=null)  ((MainActivity)mContext).restartNCLDemux();
            }
            //]]ADD_GINGA_NCL
        }

    }


    public static String getCurScannedServiceName()
    {
        return scanServiceName;
    }

    public static void handoverProgress(int _progressPercent, int _found, int _freqKHz, String _svcName)
    {

        TVlog.i(TAG, " handoverProgress  percent = " + _progressPercent + ",  found " + _found + ", service name " + _svcName + ", len " + _svcName.length());
        ((MainActivity)mContext).sendEvent(TVEVENT.E_SCAN_HANDOVER_PROCESS, _progressPercent, _found, (int)_freqKHz);
/*
        progressPercent=_progressPercent;
        found = _found;
        scanServiceName = _svcName;
*/
    }

    public static void handoverSuccess(int _index, int _total, int _updateMode)
    {
        TVlog.i(TAG, " handoverSuccess : service index = " + _index + ",  total service count =  " + _total + ", update mode = " + _updateMode);
        ((MainActivity)mContext).sendEvent(TVEVENT.E_SCAN_HANDOVER_SUCCESS, _index, _total, _updateMode);
    }

    public static void scanProgress(int _progressPercent, int _found, int _freqKHz, String _svcName)
    {

        TVlog.i(TAG, " scanProgress  percent = " + _progressPercent + ",  found " + _found + ", service name " + _svcName + ", len " + _svcName.length());
        ((MainActivity)mContext).sendEvent(TVEVENT.E_SCAN_PROCESS, _progressPercent, _found, (int)_freqKHz);
        progressPercent=_progressPercent;
        found = _found;
        scanServiceName = _svcName;

    }

    public static void scanProgress_floating(int _progressPercent, int _found, int _freqKHz, String _svcName)
    {

        TVlog.i(TAG, " scanProgress_floating  percent = " + _progressPercent + ",  found " + _found + ", service name " + _svcName + ", len " + _svcName.length());
        FloatingWindow.getInstance().sendEvent(TVEVENT.E_SCAN_PROCESS_FLOATING, _progressPercent, _found, (int)_freqKHz);
        progressPercent=_progressPercent;
        found = _found;
        scanServiceName = _svcName;

    }

    public static void scanProgress_chat(int _progressPercent, int _found, int _freqKHz, String _svcName)
    {

        TVlog.i(TAG, " scanProgress_chat  percent = " + _progressPercent + ",  found " + _found + ", service name " + _svcName + ", len " + _svcName.length());
        ChatMainActivity.getInstance().sendEvent(TVEVENT.E_SCAN_PROCESS_CHAT, _progressPercent, _found, (int)_freqKHz);
        progressPercent=_progressPercent;
        found = _found;
        scanServiceName = _svcName;

    }

    public static void scanNoti(int _idx,   String _desc, byte _type, byte _vidFormat, byte _audFormat, byte _isFree, int _remoteKey, int _svcNum, int _freqKHz, byte _bLast) {

        if (FCI_TVi.initiatedSol) {

            TVlog.i(TAG, " scanNoti _idx = " + _idx + " _desc = " + _desc + " type  = " + _type +
                    " _vform " + _vidFormat + " _aform" + _audFormat + " _isFree" + _isFree + " _remoteKey" + _remoteKey + " _svcNum" + _svcNum + " _bLast = " + _bLast + "  found channel = " + found);

            if (_bLast == 1) //last
            {

                if (MainActivity.isMainActivity) {
                    if (isScanning) {
                        MainActivity.getInstance().sendEvent(TVEVENT.E_SCAN_PROCESS, 100, found, null);
                    }
                    isScanning = false;
                    sendEventToMain(TVEVENT.E_SCAN_COMPLETED);
                    CommonStaticData.scanCHnum = found;
                    CommonStaticData.settings = mContext.getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                    editor.putInt(CommonStaticData.scanedChannelsKey, CommonStaticData.scanCHnum);
                    editor.commit();
                    MainActivity.getInstance().scanNotify(_idx, _desc, _type, _vidFormat, _audFormat, _isFree, _remoteKey, _svcNum, _freqKHz, _bLast);
                } else if (ChatMainActivity.isChat) {
                    if (ChatMainActivity.getInstance() != null) {
                        if (isScanning) {
                            ChatMainActivity.getInstance().sendEvent(TVEVENT.E_SCAN_PROCESS_CHAT, 100, found, null);
                        }
                        isScanning = false;
                        ChatMainActivity.getInstance().sendEvent(TVEVENT.E_SCAN_COMPLETED_CHAT);
                        CommonStaticData.scanCHnum = found;
                        CommonStaticData.settings = mContext.getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                        editor.putInt(CommonStaticData.scanedChannelsKey, CommonStaticData.scanCHnum);
                        editor.commit();
                        ChatMainActivity.getInstance().scanNotify_chat(_idx, _desc, _type, _vidFormat, _audFormat, _isFree, _remoteKey, _svcNum, _freqKHz, _bLast);
                    }
                } else if (FloatingWindow.isFloating) {
                    if (FloatingWindow.getInstance() != null) {
                        if (isScanning) {
                            FloatingWindow.getInstance().sendEvent(TVEVENT.E_SCAN_PROCESS_FLOATING, 100, found, null);
                        }
                        isScanning = false;
                        FloatingWindow.getInstance().sendEvent(TVEVENT.E_SCAN_COMPLETED_FLOATING);
                        CommonStaticData.scanCHnum = found;
                        CommonStaticData.settings = mContext.getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                        editor.putInt(CommonStaticData.scanedChannelsKey, CommonStaticData.scanCHnum);
                        editor.commit();
                        FloatingWindow.getInstance().scanNotify_floating(_idx, _desc, _type, _vidFormat, _audFormat, _isFree, _remoteKey, _svcNum, _freqKHz, _bLast);
                    }
                }

                //sendEventToMain(TVEVENT.E_SCAN_COMPLETED);


                //start();
            } else //0(on going) or 2(delete)
            {
                if (_bLast == 0) {
                    found = _idx + 1;
                }
                if (MainActivity.isMainActivity) {
                    MainActivity.getInstance().scanNotify(_idx, _desc, _type, _vidFormat, _audFormat, _isFree, _remoteKey, _svcNum, _freqKHz, _bLast);   // justin DB
                } else if (FloatingWindow.isFloating) {
                    if (FloatingWindow.getInstance() != null) {
                        FloatingWindow.getInstance().scanNotify_floating(_idx, _desc, _type, _vidFormat, _audFormat, _isFree, _remoteKey, _svcNum, _freqKHz, _bLast);   // justin DB
                    }
                } else if (ChatMainActivity.isChat) {
                    if (ChatMainActivity.getInstance() != null) {
                        ChatMainActivity.getInstance().scanNotify_chat(_idx, _desc, _type, _vidFormat, _audFormat, _isFree, _remoteKey, _svcNum, _freqKHz, _bLast);   // justin DB
                    }
                }
            }


        }
    }

    public static void viewScanninginfo(TextView tv)
    {
        if(isScanning == false) return;

        tv.setText(Html.fromHtml("<font color=\"#6AFF59\">" + "=============== Scan Info  =================" + "</font>"));
        tv.append("\n" + " progressPercent = " + progressPercent + "     found = " + found );
        tv.append("\n");
    }

    public static void sendEventToMain(TVEVENT _event)
    {
        if(mContext !=null)  ((MainActivity)mContext).sendEvent(_event);
    }
    public static void sendEventToMainWithData(TVEVENT _event,int _data1,int _data2,Object _obj)
    {
        if(mContext !=null)  ((MainActivity)mContext).sendEvent(_event,_data1,_data2,_obj);
    }

    public static void sendEventSubtitle(String _data)
    {
        //TVlog.i(TAG, " sendEventSubtitle ");
        if (MainActivity.isMainActivity) {
            if (mContext != null) {
                ((MainActivity) mContext).sendSubtitle(_data);
            }
        } else if (FloatingWindow.isFloating) {
            if (FloatingWindow.getInstance() != null) {
                FloatingWindow.getInstance().sendSubtitle(_data);
            }
        }
    }

    public static void sendEventSubtitleDirect(byte[] capData, int capLen, byte isClear, byte isEnd, int[] capInfo)
    {
        //TVlog.i(TAG, " sendEventSubtitleDirect ");
        if (MainActivity.isMainActivity) {
            if (mContext != null) {
                ((MainActivity) mContext).sendSubtitleDirect(capData, capLen, isClear, isEnd, capInfo);
            }
        } else if (FloatingWindow.isFloating) {
            if (FloatingWindow.getInstance() != null) {
                FloatingWindow.getInstance().sendSubtitleDirect(capData, capLen, isClear, isEnd, capInfo);
            }
        }
    }

    public static void sendEventSuperimpose(String _data)
    {
        //TVlog.i(TAG, " sendEventSuperimpose ");
        if (MainActivity.isMainActivity) {
            if (mContext != null) {
                ((MainActivity) mContext).sendSuperimpose(_data);
            }
        } else if (FloatingWindow.isFloating) {
            if (FloatingWindow.getInstance() != null) {
                FloatingWindow.getInstance().sendSuperimpose(_data);
            }
        }
    }

    public static void sendEventSuperimposeDirect(byte[] supData, int supLen, byte isClear, byte isEnd, int[] supInfo)
    {
        //TVlog.i(TAG, " sendEventSuperimposeDirect ");
        if (MainActivity.isMainActivity) {
            if (mContext != null) {
                ((MainActivity) mContext).sendSuperimposeDirect(supData, supLen, isClear, isEnd, supInfo);
            }
        } else if (FloatingWindow.isFloating) {
            if (FloatingWindow.getInstance() != null) {
                FloatingWindow.getInstance().sendSuperimposeDirect(supData, supLen, isClear, isEnd, supInfo);
            }
        }
    }

    public static void setContext(Context context)
    {
        mContext = context;
    }


    public static void subSurfaceViewON()
    {
        ((MainActivity) mContext).sendEvent(TVEVENT.E_SURFACE_SUB_ONOFF, 1, 0, 0);
    }

    public static void subSurfaceViewOff()
    {
        ((MainActivity) mContext).sendEvent(TVEVENT.E_SURFACE_SUB_ONOFF, 0, 0, 0);
    }

    public static void subChatSurfaceViewON()
    {
        if (ChatMainActivity.getInstance() != null) {
            ChatMainActivity.getInstance().sendEvent(TVEVENT.E_CHAT_SURFACE_SUB_ONOFF, 1, 0, 0);
        }
    }

    public static void subChatSurfaceViewOff()
    {
        if (ChatMainActivity.getInstance() != null) {
            ChatMainActivity.getInstance().sendEvent(TVEVENT.E_CHAT_SURFACE_SUB_ONOFF, 0, 0, 0);
        }
    }

    public static void subFloatingSurfaceViewON()
    {
        if (FloatingWindow.getInstance() != null) {
            FloatingWindow.getInstance().sendEvent(TVEVENT.E_FLOATING_SURFACE_SUB_ONOFF, 1, 0, 0);
        }
    }

    public static void subFloatingSurfaceViewOff()
    {
        if (FloatingWindow.getInstance() != null) {
            FloatingWindow.getInstance().sendEvent(TVEVENT.E_FLOATING_SURFACE_SUB_ONOFF, 0, 0, 0);
        }
    }

    public static void firstVideoNoti()
    {
        ((MainActivity)mContext).notifyFirstVideo();
        if(buildOption.RECORDING_TYPE_TS == buildOption.RECORDING_TYPE)
        {
            if(MainActivity.isPlayBackActivity==true) {
                PlayBackActivity.getInstance().sendEvent(TVEVENT.E_TSPLAYBACK_FIRSTVIDEO);
            }
        }
    }

    // live add
    public static void firstVideoNotiFloating()
    {
        TVlog.i("live", " ===== firstVideoNotiFloating() =====");
        if (FloatingWindow.getInstance() != null) {
            FloatingWindow.getInstance().notifyFirstVideoFloating();
        }
    }

    public static void firstVideoNotiChat()
    {
        if (ChatMainActivity.getInstance() != null) {
            ChatMainActivity.getInstance().notifyFirstVideoChat();
        }
    }

    public static void firstAudioNoti()
    {
        TVlog.i("live", " ===== firstAudioNoti() =====");
        ((MainActivity)mContext).notifyFirstAudio();
        if(buildOption.RECORDING_TYPE_TS == buildOption.RECORDING_TYPE)
        {
            if(MainActivity.isPlayBackActivity==true) {
                PlayBackActivity.getInstance().sendEvent(TVEVENT.E_TSPLAYBACK_FIRSTAUDIO);
            }
        }
    }

    // live add
    public static void firstAudioNotiFloating()
    {
        TVlog.i("live", " ===== firstAudioNotiFloating() =====");
        if (FloatingWindow.getInstance() != null) {
            FloatingWindow.getInstance().notifyFirstAudioFloating();
        }
    }

    public static void firstAudioNotiChat()
    {
        if (ChatMainActivity.getInstance() != null) {
            ChatMainActivity.getInstance().notifyFirstAudioChat();
        }
    }

    public static void recStartNotify()
    {
        sendEventToMain(TVEVENT.E_RECORDING_START);
    }

    public static void recErrorNotify(int _errorType)
    {
        sendEventToMainWithData(TVEVENT.E_RECORDING_FAIL, _errorType, 0, null);
    }

    public static void recOKNotify()
    {
        sendEventToMainWithData(TVEVENT.E_RECORDING_OK, 0, 0, null);
    }

    public static void noSignalNoti()
    {
        ((MainActivity) mContext).sendEvent(TVEVENT.E_BADSIGNAL_CHECK, 1, 0, null);
    }

    // live add
    public static void noSignalNotiFloating()
    {
        if (FloatingWindow.getInstance() != null) {
            FloatingWindow.getInstance().sendEvent(TVEVENT.E_BADSIGNAL_CHECK_FLOATING, 1, 0, null);
        }
    }

    public static void noSignalNotiChat()
    {
        if (ChatMainActivity.getInstance() != null) {
            ChatMainActivity.getInstance().sendEvent(TVEVENT.E_BADSIGNAL_CHECK_CHAT, 1, 0, null);
        }
    }
    //

    public static void programNotAvailableNoti()
    {
        ((MainActivity) mContext).sendEvent(TVEVENT.E_BADSIGNAL_CHECK, 4, 0, null);
    }

    // live add
    public static void programNotAvailableNotiFloating()
    {
        if (FloatingWindow.getInstance() != null) {
            FloatingWindow.getInstance().sendEvent(TVEVENT.E_BADSIGNAL_CHECK_FLOATING, 4, 0, null);
        }
    }

    public static void programNotAvailableNotiChat()
    {
        if (ChatMainActivity.getInstance() != null) {
            ChatMainActivity.getInstance().sendEvent(TVEVENT.E_BADSIGNAL_CHECK_CHAT, 4, 0, null);
        }
    }
    //

    public static void ewsInfoNoti(int startEndFlag, int signalLevel, int[] areaCodes)
    {
        ((MainActivity) mContext).sendEvent(TVEVENT.E_EWS_RECEIVED, startEndFlag, signalLevel, areaCodes);
    }

    public static void epgUpdateNoti(int epgType)
    {
        ((MainActivity) mContext).removeEvent(TVEVENT.E_EPG_UPDATE);
        ((MainActivity) mContext).postEvent(TVEVENT.E_EPG_UPDATE, 0, epgType);
    }

    public static void tsPlaybackErrorNoti(int errorNum)
    {
        if(buildOption.RECORDING_TYPE_TS == buildOption.RECORDING_TYPE) {
            if(MainActivity.isPlayBackActivity==true) {
                PlayBackActivity.getInstance().sendEvent(TVEVENT.E_TSPLAYBACK_ERROR, errorNum, null);
            }
        }
    }

    public static void bcasCardReady(byte[] cardId)
    {
        ((MainActivity) mContext).sendEvent(TVEVENT.E_BCAS_CARD_READY, 0, 0, cardId);
    }

    public static void bcasCardRemoved()
    {
        ((MainActivity) mContext).removeEvent(TVEVENT.E_BCAS_CARD_REMOVED);
        ((MainActivity) mContext).sendEvent(TVEVENT.E_BCAS_CARD_REMOVED, 0, 0, null);
    }

    public static void noDecoderNotify()
    {
        ((MainActivity) mContext).sendEvent(TVEVENT.E_NO_DECODER_NOTIFY, 0, 0, 0);
    }

    public static void noSupportResolutionNotify()
    {
        ((MainActivity) mContext).sendEvent(TVEVENT.E_NOT_SUPPORT_RESOLUTION, 0, 0, 0);
    }

    //ADD_GINGA_NCL[[
    public static void gingaNCLNoti(byte[] dataGinga)
    {
        ((MainActivity) mContext).processGingaNCL(dataGinga);
    }
    //]]ADD_GINGA_NCL
}
