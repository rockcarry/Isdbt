package kr.co.fci.tv.setting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import java.io.IOException;
import java.util.Locale;

import kr.co.fci.tv.MainActivity;
import kr.co.fci.tv.R;
import kr.co.fci.tv.TVEVENT;
import kr.co.fci.tv.buildOption;
import kr.co.fci.tv.chat.ChatMainActivity;
import kr.co.fci.tv.recording.RecordedFileListActivity;
import kr.co.fci.tv.saves.CommonStaticData;
import kr.co.fci.tv.util.CustomToast;
import kr.co.fci.tv.util.TVlog;

import static kr.co.fci.tv.MainActivity.DIALOG_SCANMODE;
import static kr.co.fci.tv.MainActivity.DIALOG_SCAN_RESTORE;

/**
 * Created by eddy.lee on 2015-08-27.
 */
public class InputDialog {
    public static MediaPlayer mMediaPlayer = null;
    public Context inputDialog_mContext;
    public static MaterialDialog inputDialog = null;
    private MaterialDialog batteryNotifyDialog;
    private MaterialDialog sleepNotifyDialog;
    private MaterialDialog notSupportRecordDialog;
    private MaterialDialog noChannelListDialog;
    public static MaterialDialog signalStatNotiDialog;
    private MaterialDialog scrambleNotiDialog;
    private MaterialDialog recordDeleteFileDialog;
    private static MaterialDialog ewsNotiDialog;
    private MaterialDialog terminateDialog;
    private String TAG= "InputDialog";

    public static final int TYPE_NEW_PASSWORD = 1;
    public static final int TYPE_ENTER_PASSWORD = 2;
    public static final int TYPE_CONFIRM_PASSWORD = 3;
    public static final int TYPE_BATTERY_NOTIFY = 4;
    public static final int TYPE_TV_TERMINATE = 5;
    public static final int TYPE_TV_CHECK_PASSWORD = 6;
    public static final int TYPE_SLEEP_NOTIFY = 7;
    public static final int TYPE_NOT_SUPPORT_RECORD = 8;
    public static final int TYPE_TV_NOCHANNELLIST = 9;
    public static final int TYPE_SIGNALSTAT_NOTI = 10;
    public static final int TYPE_SCRAMBLE_NOTI = 11;
    public static final int TYPE_RECORD_DELETEFILE = 12;
    public static final int TYPE_RECORD_EDIT = 13;
    public static final int TYPE_CHECK_N_NEW_PASSWORD = 14;
    public static final int TYPE_EWS_NOTIFY = 15;
    public static final int TYPE_RECOVER_FOCUS = 16;
    public static final int TYPE_RESTORE_NOCHANNEL = 17;    // justin add

    private static int countOfAlertNoChannel = 0;
    private static int countOfAlertBadSignal = 0;
    private static int countOfAlertScrambled = 0;

    // public static Dialog sigAlertDig;
    public static DialogInterface sigAlertDig;
    public static Boolean sigDig = false;

    Locale_of_EWBS locale_of_ewbs;

    public InputDialog(Context _con, int _type, Object _obj1, Object _obj2, Object _obj3)
    {

        inputDialog_mContext= _con;
        //editor = CommonStaticData.settings.edit();
        locale_of_ewbs = (Locale_of_EWBS) new Locale_of_EWBS();
        switch (_type) {

            case TYPE_NEW_PASSWORD:
            {
                inputDialog = new MaterialDialog.Builder(inputDialog_mContext)
                        .theme(Theme.LIGHT)
                        .iconRes(R.drawable.ic_lock_outline_gray_48dp)
                        .title(R.string.new_pw)
                        .titleColor(inputDialog_mContext.getResources().getColor(R.color.black))
                        .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                        .positiveText(R.string.submit)
                        .positiveColor(inputDialog_mContext.getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(inputDialog_mContext.getResources().getColor(R.color.blue3))
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                inputDialog = null;
                            }
                        })
                        .input(null, null, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                String savedata = input.toString();
                                if (savedata != null) {
                                    if (savedata.length() > 0) {
                                        CommonStaticData.PassWord = savedata;
                                        CommonStaticData.settings = inputDialog_mContext.getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                        editor.putString(CommonStaticData.passwordKey, savedata);
                                        editor.commit();
                                        TVlog.i(TAG, " new DIALOG_PASSWORD  String = " + savedata);
                                        CustomToast customToast = new CustomToast(inputDialog_mContext);
                                        customToast.showToast(inputDialog_mContext, R.string.saved, Toast.LENGTH_SHORT);
                                    } else {
                                        CustomToast customToast = new CustomToast(inputDialog_mContext);
                                        customToast.showToast(inputDialog_mContext, R.string.invalid_password, Toast.LENGTH_SHORT);
                                    }
                                } else {
                                    CustomToast customToast = new CustomToast(inputDialog_mContext);
                                    customToast.showToast(inputDialog_mContext, R.string.settings_password_wrong, Toast.LENGTH_SHORT);
                                }
                            }
                        }).build();
                inputDialog.getWindow().setGravity(Gravity.CENTER);
                inputDialog.show();
                View decorView = inputDialog.getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE ;
                decorView.setSystemUiVisibility(uiOptions);
            }
            break;

            case TYPE_ENTER_PASSWORD:
            {
                inputDialog = new MaterialDialog.Builder(inputDialog_mContext)
                        .theme(Theme.LIGHT)
                        .iconRes(R.drawable.ic_lock_outline_gray_48dp)
                        .title(R.string.settings_password)
                        .titleColor(inputDialog_mContext.getResources().getColor(R.color.black))
                        .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                        .positiveText(R.string.submit)
                        .positiveColor(inputDialog_mContext.getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .onNegative(new MaterialDialog.SingleButtonCallback() { // justin 20170523
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                /*
                                if (MainActivity.getInstance().screenbl_enable.equals(true)){
                                        MainActivity.getInstance().screenbl_enable=false;
                                }
                                */
                                // justin 20150526
                                //CommonStaticData.screenBlockFlag = false;
                                //CommonStaticData.ageLimitFlag = false;
                                //MainActivity.getInstance().postEvent(TVEVENT.E_RATING_MONITOR,2000);
                            }
                        })
                        .negativeColor(inputDialog_mContext.getResources().getColor(R.color.blue3))
                        .input(null, null, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                TVlog.i(TAG, "ENTER InputDialog");
                                String save = input.toString();
                                CommonStaticData.PassWord = CommonStaticData.settings.getString(CommonStaticData.passwordKey, null);
                                if (CommonStaticData.PassWord.equals(save)) {
                                    CommonStaticData.passwordVerifyFlag = true;
                                    CommonStaticData.mainPasswordVerifyFlag = true;
                                    CommonStaticData.ageLimitFlag = false;
                                    if (ChatMainActivity.isChat == true){
                                        ChatMainActivity.getInstance().sendEvent(TVEVENT.E_CONFIRMED_PASSWORD_CHAT);
                                    } else {
                                        MainActivity.getInstance().sendEvent(TVEVENT.E_CONFIRMED_PASSWORD);
                                    }
                                } else {
                                    CommonStaticData.passwordVerifyFlag = false;
                                    CommonStaticData.mainPasswordVerifyFlag = false;
                                    CommonStaticData.ageLimitFlag = true;
                                    if (ChatMainActivity.isChat == true){
                                        ChatMainActivity.getInstance().sendEvent(TVEVENT.E_RATING_MONITOR_CHAT);
                                    } else {
                                        MainActivity.getInstance().sendEvent(TVEVENT.E_RATING_MONITOR);
                                    }

                                    CustomToast customToast = new CustomToast(inputDialog_mContext);
                                    customToast.showToast(inputDialog_mContext, R.string.settings_password_wrong, Toast.LENGTH_SHORT);
                                }
                            }
                        }).dismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                /*
                                if (MainActivity.getInstance().screenbl_enable.equals(true)){
                                        MainActivity.getInstance().screenbl_enable = false;
                                }*/
                                // justin 20150526
                                //CommonStaticData.screenBlockFlag = false;
                                //CommonStaticData.ageLimitFlag = false;
                                //MainActivity.getInstance().postEvent(TVEVENT.E_RATING_MONITOR,2000);
                            }
                        }).build();
                inputDialog.getWindow().setGravity(Gravity.CENTER);
                inputDialog.show();
                View decorView = inputDialog.getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE ;
                decorView.setSystemUiVisibility(uiOptions);
            }
            break;

            case TYPE_CONFIRM_PASSWORD:
            {
                inputDialog = new MaterialDialog.Builder(inputDialog_mContext)
                        .theme(Theme.LIGHT)
                        .iconRes(R.drawable.ic_lock_outline_gray_48dp)
                        .title(R.string.settings_password)
                        .titleColor(inputDialog_mContext.getResources().getColor(R.color.black))
                        .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                        .content(R.string.program_blocked)
                        .contentColor(inputDialog_mContext.getResources().getColor(R.color.black))
                        .positiveText(R.string.submit)
                        .positiveColor(inputDialog_mContext.getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(inputDialog_mContext.getResources().getColor(R.color.blue3))
                        .input(null, null, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                TVlog.i(TAG, "CONFIRM InputDialog");
                                String save = input.toString();
                                CommonStaticData.PassWord = CommonStaticData.settings.getString(CommonStaticData.passwordKey, null);
                                if (CommonStaticData.PassWord.equals(save)) {
                                    CustomToast customToast = new CustomToast(inputDialog_mContext);
                                    customToast.showToast(inputDialog_mContext, R.string.confirm_password, Toast.LENGTH_SHORT);

                                } else {
                                    CustomToast customToast = new CustomToast(inputDialog_mContext);
                                    customToast.showToast(inputDialog_mContext, R.string.settings_password_wrong, Toast.LENGTH_SHORT);

                                }
                            }
                        }).build();
                inputDialog.getWindow().setGravity(Gravity.CENTER);
                inputDialog.show();
                View decorView = inputDialog.getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE ;
                decorView.setSystemUiVisibility(uiOptions);
            }
            break;

            case TYPE_CHECK_N_NEW_PASSWORD:
            {
                inputDialog = new MaterialDialog.Builder(inputDialog_mContext)
                        .theme(Theme.LIGHT)
                        .iconRes(R.drawable.ic_lock_outline_gray_48dp)
                        .title(R.string.settings_password)
                        .titleColor(inputDialog_mContext.getResources().getColor(R.color.black))
                        .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                        .positiveText(R.string.submit)
                        .positiveColor(inputDialog_mContext.getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(inputDialog_mContext.getResources().getColor(R.color.blue3))
                        .input(null, null, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                TVlog.i(TAG, "ENTER InputDialog");
                                String save = input.toString();
                                CommonStaticData.PassWord = CommonStaticData.settings.getString(CommonStaticData.passwordKey, null);
                                if (CommonStaticData.PassWord.equals(save)) {
                                    InputDialog dig = new InputDialog(inputDialog_mContext, TYPE_NEW_PASSWORD, null, null, null);
                                } else {
                                    CustomToast customToast = new CustomToast(inputDialog_mContext);
                                    customToast.showToast(inputDialog_mContext, R.string.settings_password_wrong, Toast.LENGTH_SHORT);
                                }}
                        })
                        .build();
                inputDialog.getWindow().setGravity(Gravity.CENTER);
                inputDialog.show();
                View decorView = inputDialog.getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE ;
                decorView.setSystemUiVisibility(uiOptions);
            }
            break;

            case TYPE_TV_CHECK_PASSWORD:
            {
                String rateText = null;
                CharSequence rText = null;

                switch((int)_obj1){
                    case 2:
                        //rateText = inputDialog_mContext.getString(R.string.block_10age);
                        rateText = inputDialog_mContext.getString(R.string.age_limit_10);
                        rText = rateText;
                        break;
                    case 3:
                        //rateText = inputDialog_mContext.getString(R.string.block_12age);
                        rateText = inputDialog_mContext.getString(R.string.age_limit_12);
                        rText = rateText;
                        break;
                    case 4:
                        //rateText = inputDialog_mContext.getString(R.string.block_14age);
                        rateText = inputDialog_mContext.getString(R.string.age_limit_14);
                        rText = rateText;
                        break;
                    case 5:
                        //rateText = inputDialog_mContext.getString(R.string.block_16age);
                        rateText = inputDialog_mContext.getString(R.string.age_limit_14);
                        rText = rateText;
                        break;
                    case 6:
                        //rateText = inputDialog_mContext.getString(R.string.block_18age);
                        rateText = inputDialog_mContext.getString(R.string.age_limit_14);
                        rText = rateText;
                        break;
                }

                inputDialog = new MaterialDialog.Builder(inputDialog_mContext)
                        .theme(Theme.LIGHT)
                        .title(R.string.age_limit_title)
                        .titleColor(inputDialog_mContext.getResources().getColor(R.color.black))
                        .content(rText)
                        .contentColor(inputDialog_mContext.getResources().getColor(R.color.black))
                        .positiveText(R.string.ok)
                        .positiveColor(inputDialog_mContext.getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(inputDialog_mContext.getResources().getColor(R.color.blue3))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                //TerminateTV();
                                new InputDialog(inputDialog_mContext, InputDialog.TYPE_ENTER_PASSWORD, null, null, null);
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                inputDialog = null;
                            }
                        }).build();
                inputDialog.getWindow().setGravity(Gravity.CENTER);
                inputDialog.show();
                // justin 20170525
                inputDialog.setCanceledOnTouchOutside(false);
                View decorView = inputDialog.getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE ;
                decorView.setSystemUiVisibility(uiOptions);
            }
            break;


            case TYPE_BATTERY_NOTIFY:
            {
                int remaining = (int)_obj1;
                View decorView;

                String batteryMSG = inputDialog_mContext.getString(R.string.battery_remaining)+ " "+Integer.toString(remaining)+ " %";
                CharSequence cs = batteryMSG;
                if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 )  {
                    batteryNotifyDialog = new MaterialDialog.Builder(inputDialog_mContext)
                            .theme(Theme.LIGHT)
                            .iconRes(R.drawable.ic_battery_alert_grey600_48dp)
                            .title(R.string.battery_alarm)
                            .titleColor(inputDialog_mContext.getResources().getColor(R.color.black))
                            .content(cs)
                            .contentColor(inputDialog_mContext.getResources().getColor(R.color.black))
                            .negativeText(R.string.cancel)
                            .negativeColor(inputDialog_mContext.getResources().getColor(R.color.blue3))
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    batteryNotifyDialog.dismiss();
                                }
                            })
                            .positiveText(R.string.exit)
                            .positiveColor(inputDialog_mContext.getResources().getColor(R.color.blue3))
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    TerminateTV();
                                }
                            })
                            .build();
                    batteryNotifyDialog.getWindow().setGravity(Gravity.CENTER);
                    batteryNotifyDialog.show();
                    decorView = batteryNotifyDialog.getWindow().getDecorView();
                } else {
                    MaterialDialog dialog = new MaterialDialog.Builder(inputDialog_mContext)
                            .theme(Theme.LIGHT)
                            .iconRes(R.drawable.ic_battery_alert_grey600_48dp)
                            .title(R.string.battery_alarm)
                            .titleColor(inputDialog_mContext.getResources().getColor(R.color.black))
                            .content(cs)
                            .contentColor(inputDialog_mContext.getResources().getColor(R.color.black))
                            .negativeText(R.string.cancel)
                            .negativeColor(inputDialog_mContext.getResources().getColor(R.color.blue3))
                            .positiveText(R.string.exit)
                            .positiveColor(inputDialog_mContext.getResources().getColor(R.color.blue3))
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onNegative(MaterialDialog dialog) {
                                    super.onNegative(dialog);
                                    dialog.dismiss();
                                }
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    super.onPositive(dialog);
                                    TerminateTV();
                                }
                            }).build();
                    dialog.getWindow().setGravity(Gravity.CENTER);
                    dialog.show();
                    decorView = dialog.getWindow().getDecorView();

                }
                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE ;
                decorView.setSystemUiVisibility(uiOptions);

            }
            break;

            case TYPE_TV_TERMINATE:
            {
                View decorView;
                if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 )  {
                    terminateDialog = new MaterialDialog.Builder(inputDialog_mContext)
                            .theme(Theme.LIGHT)
                            .title(R.string.confirm)
                            .titleColor(inputDialog_mContext.getResources().getColor(R.color.black))
                            .content(R.string.terminatemsg)
                            .contentColor(inputDialog_mContext.getResources().getColor(R.color.black))
                            .negativeText(R.string.cancel)
                            .negativeColor(inputDialog_mContext.getResources().getColor(R.color.blue3))
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    terminateDialog.dismiss();
                                }
                            })
                            .positiveText(R.string.exit)
                            .positiveColor(inputDialog_mContext.getResources().getColor(R.color.blue3))
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    TerminateTV();
                                }
                            })
                            .build();
                    terminateDialog.getWindow().setGravity(Gravity.CENTER);
                    terminateDialog.show();
                    decorView = terminateDialog.getWindow().getDecorView();
                } else {
                    MaterialDialog dialog = new MaterialDialog.Builder(inputDialog_mContext)
                            .theme(Theme.LIGHT)
                            .title(R.string.confirm)
                            .titleColor(inputDialog_mContext.getResources().getColor(R.color.black))
                            .content(R.string.terminatemsg)
                            .contentColor(inputDialog_mContext.getResources().getColor(R.color.black))
                            .positiveText(R.string.exit)
                            .positiveColor(inputDialog_mContext.getResources().getColor(R.color.blue3))
                            .negativeText(R.string.cancel)
                            .negativeColor(inputDialog_mContext.getResources().getColor(R.color.blue3))
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    TerminateTV();
                                }
                            })
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                }
                            }).build();
                    dialog.getWindow().setGravity(Gravity.CENTER);
                    dialog.show();
                    decorView = dialog.getWindow().getDecorView();
                }
                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE ;
                decorView.setSystemUiVisibility(uiOptions);
            }
            break;

            case TYPE_SLEEP_NOTIFY:
            {
                String sleepMSG = inputDialog_mContext.getString(R.string.terminatemsg);
                CharSequence cs = sleepMSG;
                View decorView;
                MainActivity.getInstance().postEvent(TVEVENT.E_SLEEP_TIMER_EXPIRED, 10 * 1000);

                if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 )  {
                    sleepNotifyDialog = new MaterialDialog.Builder(inputDialog_mContext)
                            .theme(Theme.LIGHT)
                            .iconRes(R.drawable.ic_schedule_grey600_48dp)
                            .title(R.string.sleep_alarm)
                            .titleColor(inputDialog_mContext.getResources().getColor(R.color.black))
                            .content(cs)
                            .contentColor(inputDialog_mContext.getResources().getColor(R.color.black))
                            .negativeText(R.string.cancel)
                            .negativeColor(inputDialog_mContext.getResources().getColor(R.color.blue3))
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    MainActivity.getInstance().removeEvent(TVEVENT.E_SLEEP_TIMER_EXPIRED);
                                }
                            })
                            .positiveText(R.string.exit)
                            .positiveColor(inputDialog_mContext.getResources().getColor(R.color.blue3))
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    MainActivity.getInstance().removeEvent(TVEVENT.E_SLEEP_TIMER_EXPIRED);
                                    TerminateTV();
                                }
                            })
                            .build();
                    sleepNotifyDialog.getWindow().setGravity(Gravity.CENTER);
                    sleepNotifyDialog.show();
                    decorView = sleepNotifyDialog.getWindow().getDecorView();
                } else {
                    MaterialDialog dialog = new MaterialDialog.Builder(inputDialog_mContext)
                            .theme(Theme.LIGHT)
                            .iconRes(R.drawable.ic_schedule_grey600_48dp)
                            .title(R.string.sleep_alarm)
                            .titleColor(inputDialog_mContext.getResources().getColor(R.color.black))
                            .content(cs)
                            .contentColor(inputDialog_mContext.getResources().getColor(R.color.black))
                            .negativeText(R.string.cancel)
                            .negativeColor(inputDialog_mContext.getResources().getColor(R.color.blue3))
                            .positiveText(R.string.exit)
                            .positiveColor(inputDialog_mContext.getResources().getColor(R.color.blue3))
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onNegative(MaterialDialog dialog) {
                                    super.onNegative(dialog);
                                    MainActivity.getInstance().removeEvent(TVEVENT.E_SLEEP_TIMER_EXPIRED);
                                }
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    super.onPositive(dialog);
                                    MainActivity.getInstance().removeEvent(TVEVENT.E_SLEEP_TIMER_EXPIRED);
                                    TerminateTV();
                                }
                            }).build();
                    dialog.getWindow().setGravity(Gravity.CENTER);
                    dialog.show();
                    decorView = dialog.getWindow().getDecorView();
                }
                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE ;
                decorView.setSystemUiVisibility(uiOptions);
            }
            break;

            case TYPE_NOT_SUPPORT_RECORD:
            {
                View decorView;

                if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 )  {
                    notSupportRecordDialog = new MaterialDialog.Builder(inputDialog_mContext)
                            .theme(Theme.LIGHT)
                            .iconRes(R.drawable.ic_info_outline_gray_48dp)
                            .title(R.string.alert)
                            .titleColor(inputDialog_mContext.getResources().getColor(R.color.black))
                            .content(R.string.recordwrongformat)
                            .contentColor(inputDialog_mContext.getResources().getColor(R.color.black))
                            .positiveText(R.string.ok)
                            .positiveColor(inputDialog_mContext.getResources().getColor(R.color.blue3))
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                }
                            })
                            .build();
                    notSupportRecordDialog.getWindow().setGravity(Gravity.CENTER);
                    notSupportRecordDialog.show();
                    decorView = notSupportRecordDialog.getWindow().getDecorView();
                } else {
                    MaterialDialog dialog = new MaterialDialog.Builder(inputDialog_mContext)
                            .theme(Theme.LIGHT)
                            .iconRes(R.drawable.ic_info_outline_gray_48dp)
                            .title(R.string.alert)
                            .titleColor(inputDialog_mContext.getResources().getColor(R.color.black))
                            .content(R.string.recordwrongformat)
                            .contentColor(inputDialog_mContext.getResources().getColor(R.color.black))
                            .positiveText(R.string.ok)
                            .positiveColor(inputDialog_mContext.getResources().getColor(R.color.blue3))
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    super.onPositive(dialog);

                                }
                            }).build();
                    dialog.getWindow().setGravity(Gravity.CENTER);
                    dialog.show();
                    decorView = dialog.getWindow().getDecorView();
                }
                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE ;
                decorView.setSystemUiVisibility(uiOptions);
            }
            break;

            case TYPE_RESTORE_NOCHANNEL:    // justin add
            {
                View decorView;
                MainActivity.getInstance().removeEvent(TVEVENT.E_SCAN_MONITOR);
                if (MainActivity.isBBFail) {
                    break;
                }
                if (countOfAlertNoChannel > 0) {
                    break;
                }
                countOfAlertNoChannel++;

                if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE
                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG) {
                    countOfAlertNoChannel--;    // justin add
                    MainActivity.getInstance().showDialog(DIALOG_SCAN_RESTORE);
                } else {
                    noChannelListDialog = new MaterialDialog.Builder(inputDialog_mContext)
                            .theme(Theme.LIGHT)
                            .iconRes(R.drawable.ic_signal_cellular_connected_no_internet_4_bar_black_48dp)
                            .title(R.string.no_channel_tip)
                            .titleColor(inputDialog_mContext.getResources().getColor(R.color.black))
                            .content(R.string.scan_tip)
                            .contentColor(inputDialog_mContext.getResources().getColor(R.color.black))
                            .positiveText(R.string.ok)
                            .positiveColor(inputDialog_mContext.getResources().getColor(R.color.blue3))
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    countOfAlertNoChannel--;
                                    if (CommonStaticData.settingActivityShow)
                                        SettingActivity.getInstance().sendEvent(TVEVENT.E_SETTING_EXIT);
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_SCAN_START);
                                }
                            })
                            .build();
                    noChannelListDialog.getWindow().setGravity(Gravity.CENTER);
                    noChannelListDialog.show();
                    noChannelListDialog.setCanceledOnTouchOutside(false);
                    decorView = noChannelListDialog.getWindow().getDecorView();
                    /*
                    if (MainActivity.isBBFail) {
                        noChannelListDialog.dismiss();
                    }*/
                    int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE ;
                    decorView.setSystemUiVisibility(uiOptions);
                }
            }
            break;

            case TYPE_TV_NOCHANNELLIST:
            {
                View decorView;
                MainActivity.getInstance().removeEvent(TVEVENT.E_SCAN_MONITOR);
                if (MainActivity.isBBFail) {
                    break;
                }
                if (countOfAlertNoChannel > 0) {
                    break;
                }
                countOfAlertNoChannel++;

                if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE
                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG) {
                    countOfAlertNoChannel--;    // justin add
                    MainActivity.getInstance().showDialog(DIALOG_SCANMODE);
                } else {
                    noChannelListDialog = new MaterialDialog.Builder(inputDialog_mContext)
                            .theme(Theme.LIGHT)
                            .iconRes(R.drawable.ic_signal_cellular_connected_no_internet_4_bar_black_48dp)
                            .title(R.string.no_channel_tip)
                            .titleColor(inputDialog_mContext.getResources().getColor(R.color.black))
                            .content(R.string.scan_tip)
                            .contentColor(inputDialog_mContext.getResources().getColor(R.color.black))
                            .negativeText(R.string.cancel)
                            .negativeColor(inputDialog_mContext.getResources().getColor(R.color.blue3))
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    countOfAlertNoChannel--;
                                    MainActivity.getInstance().changeChannelView.setVisibility(View.INVISIBLE);
                                    if (MainActivity.getInstance().ll_scramble_msg.getVisibility() == View.VISIBLE) {
                                        MainActivity.getInstance().ll_scramble_msg.setVisibility(View.INVISIBLE);
                                    }
                                    if (MainActivity.getInstance().ll_noSignal.getVisibility() == View.VISIBLE) {
                                        MainActivity.getInstance().ll_noSignal.setVisibility(View.INVISIBLE);
                                    }
                                    MainActivity.ll_noChannel.setVisibility(View.VISIBLE);
                                    if (MainActivity.getInstance().currChNo != null && MainActivity.getInstance().currCH != null && MainActivity.getInstance().currRemoteNo != null) {
                                        MainActivity.getInstance().currChNo.setText("- -ch");
                                        MainActivity.getInstance().currRemoteNo.setText("- - -");
                                        MainActivity.getInstance().currCH.setText(R.string.no_channel_title);
                                    }
                                    if (MainActivity.getInstance().rl_ChType != null) {
                                        MainActivity.getInstance().rl_ChType.setVisibility(View.GONE);
                                    }
                                    if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 )  {
                                           MainActivity.getInstance().currProgram.setText("- - -");
                                        MainActivity.getInstance().currDuration.setText("--:--~--:--");
                                    }
                                }
                            })
                            .positiveText(R.string.ok)
                            .positiveColor(inputDialog_mContext.getResources().getColor(R.color.blue3))
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    countOfAlertNoChannel--;
                                    if (CommonStaticData.settingActivityShow)
                                        SettingActivity.getInstance().sendEvent(TVEVENT.E_SETTING_EXIT);
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_SCAN_START);
                                }
                            })
                            .build();
                    noChannelListDialog.getWindow().setGravity(Gravity.CENTER);
                    noChannelListDialog.show();
                    noChannelListDialog.setCanceledOnTouchOutside(false);
                    decorView = noChannelListDialog.getWindow().getDecorView();

                    int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE ;
                    decorView.setSystemUiVisibility(uiOptions);
                }
            }
            break;

            case TYPE_SIGNALSTAT_NOTI:
            {
                MainActivity.getInstance().removeEvent(TVEVENT.E_BADSIGNAL_CHECK);
                if (countOfAlertBadSignal > 0) {
                    break;
                }
                countOfAlertBadSignal++;

                signalStatNotiDialog = new MaterialDialog.Builder(inputDialog_mContext)
                        .theme(Theme.LIGHT)
                        .iconRes(R.drawable.ic_signal_cellular_connected_no_internet_4_bar_black_48dp)
                        .title(R.string.no_signal)
                        .titleColor(inputDialog_mContext.getResources().getColor(R.color.black))
                        .content(R.string.no_signal_tip)
                        .contentColor(inputDialog_mContext.getResources().getColor(R.color.black))
                        .positiveText(R.string.ok)
                        .positiveColor(inputDialog_mContext.getResources().getColor(R.color.blue3))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                countOfAlertBadSignal--;
                                sigDig = false;
                                MainActivity.getInstance().sendEvent(TVEVENT.E_BADSIGNAL_CHECK, 1, 0, null);
                            }
                        })
                        .build();
                signalStatNotiDialog.getWindow().setGravity(Gravity.CENTER);
                signalStatNotiDialog.show();

                View decorView = signalStatNotiDialog.getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE ;
                decorView.setSystemUiVisibility(uiOptions);
                sigDig = true;
            }
            break;

            case TYPE_SCRAMBLE_NOTI:
            {
                View decorView;
                if (buildOption.FCI_SOLUTION_MODE != buildOption.JAPAN && buildOption.FCI_SOLUTION_MODE != buildOption.JAPAN_ONESEG && buildOption.FCI_SOLUTION_MODE != buildOption.JAPAN_FILE) {
                    MainActivity.getInstance().removeEvent(TVEVENT.E_CHANNEL_LIST_ENCRYPTED);
                    if (countOfAlertScrambled > 0) {
                        break;
                    }
                    countOfAlertScrambled++;

                    /*
                    if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 )  {
                        scrambleNotiDialog = new MaterialDialog.Builder(inputDialog_mContext)
                                .iconRes(R.drawable.tv_free)
                                .title(R.string.scrambl_ch)
                                .content(R.string.scrambl_ch_tip)
                                .positiveText(R.string.ok)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        countOfAlertScrambled--;
                                    }
                                })
                                .show();
                        scrambleNotiDialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_box);
                        decorView = scrambleNotiDialog.getWindow().getDecorView();
                    } else {
                        MaterialDialog dialog = new MaterialDialog.Builder(inputDialog_mContext)
                                .cancelable(false)
                                .iconRes(R.drawable.tv_free)
                                .title(R.string.scrambl_ch)
                                .content(R.string.scrambl_ch_tip)
                                .positiveText(R.string.ok)
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        super.onPositive(dialog);
                                        countOfAlertScrambled--;
                                    }
                                }).show();
                        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_box);
                        decorView = dialog.getWindow().getDecorView();
                    }
                    int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE ;
                    decorView.setSystemUiVisibility(uiOptions);
                    */

                    if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
//                      MainActivity.getInstance().ll_scramble_msg.setVisibility(View.VISIBLE);
                    } else {
                        MainActivity.getInstance().ll_scramble_msg.setVisibility(View.VISIBLE);
                    }
                }

            }
            break;

            case TYPE_RECORD_DELETEFILE: {
                View decorView;
                final int index = (int) _obj1;
                if (Locale.getDefault().getLanguage().equalsIgnoreCase("ja")) {
                    if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 )  {
                        recordDeleteFileDialog = new MaterialDialog.Builder(inputDialog_mContext)
                                .theme(Theme.LIGHT)
                                .iconRes(R.drawable.ic_delete_grey600_48dp)
                                .title(R.string.delete_file)
                                .titleColor(inputDialog_mContext.getResources().getColor(R.color.black))
                                .content(R.string.delete_file_msg)
                                .contentColor(inputDialog_mContext.getResources().getColor(R.color.black))
                                .negativeText(R.string.cancel)
                                .negativeColor(inputDialog_mContext.getResources().getColor(R.color.blue3))
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        recordDeleteFileDialog.dismiss();
                                    }
                                })
                                .positiveText(R.string.yes)
                                .positiveColor(inputDialog_mContext.getResources().getColor(R.color.blue3))
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        TVlog.i(TAG, "Delete File yes");

                                        RecordedFileListActivity.getInstance().sendEvent(TVEVENT.E_RECORDED_FILE_DELETE, index, null);
                                    }
                                })
                                .build();
                        recordDeleteFileDialog.getWindow().setGravity(Gravity.CENTER);
                        recordDeleteFileDialog.show();
                        decorView = recordDeleteFileDialog.getWindow().getDecorView();
                    } else {
                        MaterialDialog dialog = new MaterialDialog.Builder(inputDialog_mContext)
                                .theme(Theme.LIGHT)
                                .iconRes(R.drawable.ic_delete_grey600_48dp)
                                .title(R.string.delete_file)
                                .titleColor(inputDialog_mContext.getResources().getColor(R.color.black))
                                .content(R.string.delete_file_msg)
                                .contentColor(inputDialog_mContext.getResources().getColor(R.color.black))
                                .negativeText(R.string.cancel)
                                .negativeColor(inputDialog_mContext.getResources().getColor(R.color.blue3))
                                .positiveText(R.string.yes)
                                .positiveColor(inputDialog_mContext.getResources().getColor(R.color.blue3))
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onNegative(MaterialDialog dialog) {
                                        super.onNegative(dialog);
                                        dialog.dismiss();
                                    }
                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        super.onPositive(dialog);
                                        TVlog.i(TAG, "Delete File yes");
                                        RecordedFileListActivity.getInstance().sendEvent(TVEVENT.E_RECORDED_FILE_DELETE, index, null);
                                    }
                                }).build();
                        dialog.getWindow().setGravity(Gravity.CENTER);
                        dialog.show();
                        decorView = dialog.getWindow().getDecorView();

                    }
                    int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE ;
                    decorView.setSystemUiVisibility(uiOptions);
                } else {
                    if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 )  {
                        recordDeleteFileDialog = new MaterialDialog.Builder(inputDialog_mContext)
                                .theme(Theme.LIGHT)
                                .iconRes(R.drawable.ic_delete_grey600_48dp)
                                .title(R.string.delete_file)
                                .titleColor(inputDialog_mContext.getResources().getColor(R.color.black))
                                .content(R.string.delete_file_msg)
                                .contentColor(inputDialog_mContext.getResources().getColor(R.color.black))
                                .negativeText(R.string.cancel)
                                .negativeColor(inputDialog_mContext.getResources().getColor(R.color.blue3))
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        recordDeleteFileDialog.dismiss();
                                    }
                                })
                                .positiveText(R.string.yes)
                                .positiveColor(inputDialog_mContext.getResources().getColor(R.color.blue3))
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        TVlog.i(TAG, "Delete File yes");

                                        RecordedFileListActivity.getInstance().sendEvent(TVEVENT.E_RECORDED_FILE_DELETE, index, null);
                                    }
                                })
                                .build();
                        recordDeleteFileDialog.getWindow().setGravity(Gravity.CENTER);
                        recordDeleteFileDialog.show();

                        decorView = recordDeleteFileDialog.getWindow().getDecorView();
                    } else {
                        MaterialDialog dialog = new MaterialDialog.Builder(inputDialog_mContext)
                                .theme(Theme.LIGHT)
                                .iconRes(R.drawable.ic_delete_grey600_48dp)
                                .title(R.string.delete_file)
                                .titleColor(inputDialog_mContext.getResources().getColor(R.color.black))
                                .content(R.string.delete_file_msg)
                                .contentColor(inputDialog_mContext.getResources().getColor(R.color.black))
                                .negativeText(R.string.cancel)
                                .negativeColor(inputDialog_mContext.getResources().getColor(R.color.blue3))
                                .positiveText(R.string.yes)
                                .positiveColor(inputDialog_mContext.getResources().getColor(R.color.blue3))
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onNegative(MaterialDialog dialog) {
                                        super.onNegative(dialog);
                                        dialog.dismiss();
                                    }
                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        super.onPositive(dialog);
                                        TVlog.i(TAG, "Delete File yes");
                                        RecordedFileListActivity.getInstance().sendEvent(TVEVENT.E_RECORDED_FILE_DELETE, index, null);
                                    }
                                }).build();
                        dialog.getWindow().setGravity(Gravity.CENTER);
                        dialog.show();
                        decorView = dialog.getWindow().getDecorView();
                    }
                    int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE ;
                    decorView.setSystemUiVisibility(uiOptions);
                }
            }
            break;

            case TYPE_RECORD_EDIT:
            {
                inputDialog = new MaterialDialog.Builder(inputDialog_mContext)
                        .theme(Theme.LIGHT)
                        .title(R.string.new_pw)
                        .titleColor(inputDialog_mContext.getResources().getColor(R.color.black))
                        .input(null, null, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                            }
                        }).build();
                inputDialog.getWindow().setGravity(Gravity.CENTER);
                inputDialog.show();
                View decorView = inputDialog.getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE ;
                decorView.setSystemUiVisibility(uiOptions);
            }
            break;

            case TYPE_EWS_NOTIFY:
            {
                View decorView;
                MainActivity.getInstance().removeEvent(TVEVENT.E_EWS_RECEIVED);

                int startEndFlag = (int)_obj1;
                int signalLevel = (int)_obj2;
                int[] areaCodes = (int[])_obj3;
                String strTitle = "";
                String strMessage = "";
                String strMessage_1 = "";
                String strMessage_2 = "" ;
                StringBuilder tMsg= new StringBuilder(" ");

                Uri alert =  RingtoneManager.getActualDefaultRingtoneUri(inputDialog_mContext, RingtoneManager.TYPE_ALARM);
                mMediaPlayer = new MediaPlayer();
                try {
                    mMediaPlayer.setDataSource(inputDialog_mContext, alert);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                final AudioManager audioManager = (AudioManager) inputDialog_mContext.getSystemService(Context.AUDIO_SERVICE);

                //start/end flag
                if (startEndFlag == 1) {
                    strTitle = inputDialog_mContext.getResources().getString(R.string.ewbs_title_start);
                }
                else if (startEndFlag == 0){
                    strTitle = inputDialog_mContext.getResources().getString(R.string.ewbs_title_end);
                }

                //signal level (type)
                if (signalLevel == 0) {
                    strMessage_1 = inputDialog_mContext.getResources().getString(R.string.ewbs_siglevel_0);
                }
                else if (signalLevel == 1) {
                    strMessage_1 = inputDialog_mContext.getResources().getString(R.string.ewbs_siglevel_1);
                }

                int cntr = CommonStaticData.localeSet;
                if (areaCodes.length >= 1){

                    for(int j=0; j < areaCodes.length ; j++) {
                        tMsg.append(locale_of_ewbs.findArea(cntr, areaCodes.length , String.format("%03x", areaCodes[j]))+" ");     // use small letters
                    }

                    strMessage_2 = tMsg.toString();
                    strMessage = strMessage_1 + " in " + strMessage_2;
                }

                if (ewsNotiDialog != null && ewsNotiDialog.isShowing()) {
                    if (mMediaPlayer.isPlaying()) {
                        mMediaPlayer.stop();
                        mMediaPlayer.release();
                    }
                    ewsNotiDialog.dismiss();
                }

                if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 )  {
                    ewsNotiDialog = new MaterialDialog.Builder(inputDialog_mContext)
                            .theme(Theme.LIGHT)
                            .title(strTitle)
                            .titleColor(inputDialog_mContext.getResources().getColor(R.color.black))
                            .content(strMessage)
                            .contentColor(inputDialog_mContext.getResources().getColor(R.color.black))
                            .positiveText(R.string.ok)
                            .positiveColor(inputDialog_mContext.getResources().getColor(R.color.blue3))
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    mMediaPlayer.stop();
                                    mMediaPlayer.release();
                                    dialog.dismiss();
                                }
                            })
                            .build();
                    ewsNotiDialog.getWindow().setGravity(Gravity.CENTER);
                    ewsNotiDialog.show();
                    ewsNotiDialog.setCanceledOnTouchOutside(false);
                    decorView = ewsNotiDialog.getWindow().getDecorView();
                } else {
                    ewsNotiDialog = new MaterialDialog.Builder(inputDialog_mContext)
                            .theme(Theme.LIGHT)
                            .iconRes(R.drawable.ic_delete_grey600_48dp)
                            .title(strTitle)
                            .titleColor(inputDialog_mContext.getResources().getColor(R.color.black))
                            .content(strMessage)
                            .contentColor(inputDialog_mContext.getResources().getColor(R.color.black))
                            .positiveText(R.string.ok)
                            .positiveColor(inputDialog_mContext.getResources().getColor(R.color.blue3))
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    super.onPositive(dialog);
                                    mMediaPlayer.stop();
                                    mMediaPlayer.release();
                                    dialog.dismiss();
                                }
                            }).build();
                    ewsNotiDialog.getWindow().setGravity(Gravity.CENTER);
                    ewsNotiDialog.show();
                    ewsNotiDialog.setCanceledOnTouchOutside(false);
                    decorView = ewsNotiDialog.getWindow().getDecorView();

                }
                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE ;
                decorView.setSystemUiVisibility(uiOptions);

                if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                    mMediaPlayer.setLooping(true);
                    try {
                        mMediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mMediaPlayer.start();
                    TVlog.i(TAG, " >>> mMediaPlayer.getTrackInfo() = "+String.valueOf(mMediaPlayer.getTrackInfo()));
                } else {
                    TVlog.i(TAG, " >>> audioManager.getStreamVolume(AudioManager.STREAM_RING) = "+String.valueOf(audioManager.getStreamVolume(AudioManager.STREAM_ALARM)));
                }
            }
            break;
            case TYPE_RECOVER_FOCUS:
            {
                DialogInterface mPopDlg = null;
                MaterialDialog.Builder ald =  new MaterialDialog.Builder(inputDialog_mContext);
                ald.theme(Theme.LIGHT);
                ald.build();
                mPopDlg = ald.show();
                mPopDlg.dismiss();
            }
            break;
        }
    }


    void TerminateTV()
    {
        MainActivity.getInstance().sendEvent(TVEVENT.E_TERMINATE);
    }

    public static void nosignalNotiClear(){
        if (sigDig) {
            signalStatNotiDialog.dismiss();
            countOfAlertBadSignal--;
            sigDig = false;
        }
    }
}
