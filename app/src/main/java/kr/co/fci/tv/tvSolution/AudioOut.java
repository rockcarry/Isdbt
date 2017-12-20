package kr.co.fci.tv.tvSolution;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;

import java.lang.reflect.Method;

import kr.co.fci.tv.saves.CommonStaticData;
import kr.co.fci.tv.util.TVlog;


/**
 * Created by eddy.lee on 2015-08-31.
 *
 */

// audioSystem parameters

/*
// device categories config for setForceUse, must match AudioSystem::forced_config
public static final int FORCE_NONE = 0;
public static final int FORCE_SPEAKER = 1;
public static final int FORCE_HEADPHONES = 2;
public static final int FORCE_BT_SCO = 3;
public static final int FORCE_BT_A2DP = 4;
public static final int FORCE_WIRED_ACCESSORY = 5;
public static final int FORCE_BT_CAR_DOCK = 6;
public static final int FORCE_BT_DESK_DOCK = 7;
public static final int FORCE_ANALOG_DOCK = 8;
public static final int FORCE_DIGITAL_DOCK = 9;
public static final int FORCE_NO_BT_A2DP = 10;
public static final int FORCE_SYSTEM_ENFORCED = 11;
public static final int FORCE_HDMI_SYSTEM_AUDIO_ENFORCED = 12;
private static final int NUM_FORCE_CONFIG = 13;
public static final int FORCE_DEFAULT = FORCE_NONE;

// usage for setForceUse, must match AudioSystem::force_use
public static final int FOR_COMMUNICATION = 0;
public static final int FOR_MEDIA = 1;
public static final int FOR_RECORD = 2;
public static final int FOR_DOCK = 3;
public static final int FOR_SYSTEM = 4;
public static final int FOR_HDMI_SYSTEM_AUDIO = 5;
private static final int NUM_FORCE_USE = 6;
*/
public class AudioOut {

    private static final String TAG = "AudioOut";
    private static AudioManager am;
    private Context mContext;
    private boolean mCurSpeakerMode = false; //true - speaker on, false - speaker out

    Class<?> audioSystem;
    public AudioOut(Context _cont) {
        mContext = _cont;
        am= (AudioManager)_cont.getSystemService(Context.AUDIO_SERVICE);

        try {
             audioSystem = Class.forName("android.media.AudioSystem");

        } catch (Exception e) {
            TVlog.i(TAG, "setDeviceConnectionState failed: " + e);
        }

        int result = am.requestAudioFocus(afChangeListener, am.STREAM_MUSIC, am.AUDIOFOCUS_GAIN);
    }

    public void setSpeakerMode( boolean _onOff)
    {
        Method setForceUse;

        try {
            setForceUse = audioSystem.getMethod(
                    "setForceUse", int.class, int.class);
            if(_onOff ==true ) {
				 // justin add for debugging 20161107
                if(isHeadSetConnected()){
                    am.setSpeakerphoneOn(true);
                    am.setMode(AudioManager.MODE_CURRENT);
                }
                setForceUse.invoke(null,1, 1);
            }else
            {
				// justin add for debugging 20161107
                if(isHeadSetConnected()){
                    am.setSpeakerphoneOn(false);
                    am.setMode(AudioManager.MODE_CURRENT);
                }
                setForceUse.invoke(null,1, 2);
            }
            mCurSpeakerMode = _onOff;

        } catch (Exception e) {
            mCurSpeakerMode = false;
            TVlog.i(TAG, "setForceUse failed: " + e);
        }

    }

    public void audioModeReturn()
    {
        Method setForceUse;
        TVlog.i(TAG, "Return   audioMode" );
        try {
            setForceUse = audioSystem.getMethod(
                    "setForceUse", int.class, int.class);

			// justin add for debugging 20161107
 			if(isHeadSetConnected()){
                am.setSpeakerphoneOn(false);
                am.setMode(AudioManager.MODE_CURRENT);
            }
            setForceUse.invoke(null,1, 0);

        } catch (Exception e) {
            TVlog.i(TAG, "setForceUse failed: " + e);
        }
        TVlog.i(TAG, "::abandon focus & focus listener cleared");
        am.abandonAudioFocus(afChangeListener);
        afChangeListener = null;
    }

    public boolean getSpeakerMode () {
        return mCurSpeakerMode;
    }

    public boolean isHeadSetConnected () {
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        Intent iStatus = mContext.registerReceiver(null, iFilter);

        boolean isConnectd = false;
        if (iStatus != null) {
            isConnectd = iStatus.getIntExtra("state", 0) == 1;
        }

        return isConnectd;
    }

    public static AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {


        public void onAudioFocusChange(int focusChange) {

            //TVlog.i(TAG, "=afChangeListener audiofocus result ==  " + focusChange);

            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) { //pause
                // Pause playback
                //TVlog.i(TAG, "AUDIOFOCUS_LOSS_TRANSIENT Pause >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                //TVBridge.stop();
                FCI_TVi.setVolume(0.0f);
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) { //unmute
                // Resume playback
                //TVlog.i(TAG, "AUDIOFOCUS_GAIN Resume >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
                if (CommonStaticData.scanningNow == false && CommonStaticData.scanCHnum > 0) {
                    //    TVBridge.serviceID_start(TVBridge.getCurrentChannel());

					FCI_TVi.setVolume(1.0f);

                    if(am != null) {
                        int result = am.requestAudioFocus(afChangeListener, am.STREAM_MUSIC, am.AUDIOFOCUS_GAIN);
                        
                    }
                }
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) { //mute
                //TVlog.i(TAG, "AUDIOFOCUS_LOSS stop >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
				am.abandonAudioFocus(afChangeListener);
				FCI_TVi.setVolume(1.0f);
                //TVBridge.stop();
                
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {

            }
        }
    };
    //]]elliot
}



