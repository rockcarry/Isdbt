package kr.co.fci.tv.setting;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.fci.tv.FCI_TV;

import java.util.Locale;

import kr.co.fci.tv.FloatingWindow;
import kr.co.fci.tv.MainActivity;
import kr.co.fci.tv.R;
import kr.co.fci.tv.TVEVENT;
import kr.co.fci.tv.activity.AboutActivity;
import kr.co.fci.tv.buildOption;
import kr.co.fci.tv.cReleaseOption;
import kr.co.fci.tv.recording.thumbNailUpdate;
import kr.co.fci.tv.saves.CommonStaticData;
import kr.co.fci.tv.tvSolution.FCI_TVi;
import kr.co.fci.tv.tvSolution.TVBridge;
import kr.co.fci.tv.util.CustomToast;
import kr.co.fci.tv.util.TVlog;

public class SettingActivity extends Activity {

    private final static String TAG = "SettingActivity";

    SeekBar seekBar1;
    SeekBar seekBar2;

    Switch switchCaption;
    Switch switchInteractive;
    public Switch switchParental;       // justin 20170526
    Switch switchReset;
    Switch switchSuperimpose;

    Button button_screen;
    Button button_svcmodeswitch;
    Button button_autoSearch;

    Button button_area;
    Button button_prefecture;
    Button button_locality;

    Button button_audio;
    Button button_audiotrack;
    Button button_videotrack;
    Button button_sleep;
    //    Button button_storage;    // not use
    Button button_age;
    Button button_password;
    Button button_battery;
    Button button_captionselect;
    Button button_superimposeselect;

    LinearLayout ll_bcas_id;
    TextView bcas_id_title;
    public TextView tv_bcas_id;
    LinearLayout ll_bcas_test;
    TextView tv_bcas_test;

    Button btn21;
    Button btn22;

    TextView textView_about;

    TextView textView_caption;
    TextView textView_captionselect;
    TextView textView_audiotrack;
    TextView textView_videotrack;
    TextView textView_superimpose;
    TextView textView_superimposeselect;
    TextView textView_interactive;

    String[] arr_screen;
    String[] arr_svcmodeswitch_jp;
    String[] arr_autosearch_jp;

    String[] arr_area;

    String[] arr_prefecture_0;
    String[] arr_prefecture_1;
    String[] arr_prefecture_2;
    String[] arr_prefecture_3;
    String[] arr_prefecture_4;
    String[] arr_prefecture_5;
    String[] arr_prefecture_6;
    String[] arr_prefecture_7;

    String[] arr_locality_00;
    String[] arr_locality_01;
    String[] arr_locality_02;
    String[] arr_locality_03;
    String[] arr_locality_04;
    String[] arr_locality_05;
    String[] arr_locality_06;

    String[] arr_locality_10;
    String[] arr_locality_11;
    String[] arr_locality_12;
    String[] arr_locality_13;
    String[] arr_locality_14;
    String[] arr_locality_15;
    String[] arr_locality_16;

    String[] arr_locality_20;
    String[] arr_locality_21;
    String[] arr_locality_22;
    String[] arr_locality_23;
    String[] arr_locality_24;
    String[] arr_locality_25;

    String[] arr_locality_30;
    String[] arr_locality_31;
    String[] arr_locality_32;
    String[] arr_locality_33;

    String[] arr_locality_40;
    String[] arr_locality_41;
    String[] arr_locality_42;
    String[] arr_locality_43;
    String[] arr_locality_44;
    String[] arr_locality_45;

    String[] arr_locality_50;
    String[] arr_locality_51;
    String[] arr_locality_52;
    String[] arr_locality_53;
    String[] arr_locality_54;

    String[] arr_locality_60;
    String[] arr_locality_61;
    String[] arr_locality_62;
    String[] arr_locality_63;

    String[] arr_locality_70;
    String[] arr_locality_71;
    String[] arr_locality_72;
    String[] arr_locality_73;
    String[] arr_locality_74;
    String[] arr_locality_75;
    String[] arr_locality_76;
    String[] arr_locality_77;

    String[] arr_audio;
    String[] arr_audiotrack;
    String[] arr_videotrack;
    String[] arr_sleep;
    // String[] arr_storage;
    String[] arr_age;
    String[] arr_battery;

    TextView textView_locale;
    ImageView imageView_flag;
    String[] arr_locale;
    int[] arr_localeflag = {R.drawable.flag_of_ar,R.drawable.flag_of_bz,R.drawable.flag_of_bo,R.drawable.flag_of_bw,R.drawable.flag_of_br,
            R.drawable.flag_of_cl,R.drawable.flag_of_cr,R.drawable.flag_of_ec,R.drawable.flag_of_gt,R.drawable.flag_of_hn,
            R.drawable.flag_of_jp,R.drawable.flag_of_mv,R.drawable.flag_of_ni,R.drawable.flag_of_py,R.drawable.flag_of_pe,
            R.drawable.flag_of_ph,R.drawable.flag_of_lk,R.drawable.flag_of_uy,R.drawable.flag_of_ve};



    int dialog_caption_selected;
    int dialog_screen_selected;
    int dialog_svcmodeswitch_selected;
    int dialog_autoSearch_selected;

    int dialog_area_selected;

    int dialog_prefecture0_selected;
    int dialog_prefecture1_selected;
    int dialog_prefecture2_selected;
    int dialog_prefecture3_selected;
    int dialog_prefecture4_selected;
    int dialog_prefecture5_selected;
    int dialog_prefecture6_selected;
    int dialog_prefecture7_selected;

    int dialog_locality00_selected;
    int dialog_locality01_selected;
    int dialog_locality02_selected;
    int dialog_locality03_selected;
    int dialog_locality04_selected;
    int dialog_locality05_selected;
    int dialog_locality06_selected;
    int dialog_locality10_selected;
    int dialog_locality11_selected;
    int dialog_locality12_selected;
    int dialog_locality13_selected;
    int dialog_locality14_selected;
    int dialog_locality15_selected;
    int dialog_locality16_selected;
    int dialog_locality20_selected;
    int dialog_locality21_selected;
    int dialog_locality22_selected;
    int dialog_locality23_selected;
    int dialog_locality24_selected;
    int dialog_locality25_selected;
    int dialog_locality30_selected;
    int dialog_locality31_selected;
    int dialog_locality32_selected;
    int dialog_locality33_selected;
    int dialog_locality40_selected;
    int dialog_locality41_selected;
    int dialog_locality42_selected;
    int dialog_locality43_selected;
    int dialog_locality44_selected;
    int dialog_locality45_selected;
    int dialog_locality50_selected;
    int dialog_locality51_selected;
    int dialog_locality52_selected;
    int dialog_locality53_selected;
    int dialog_locality54_selected;
    int dialog_locality60_selected;
    int dialog_locality61_selected;
    int dialog_locality62_selected;
    int dialog_locality63_selected;
    int dialog_locality70_selected;
    int dialog_locality71_selected;
    int dialog_locality72_selected;
    int dialog_locality73_selected;
    int dialog_locality74_selected;
    int dialog_locality75_selected;
    int dialog_locality76_selected;
    int dialog_locality77_selected;

    int dialog_audiomode_selected;
    int dialog_audiotrack_selected;
    int dialog_videotrack_selected;
    int dialog_sleep_selected;
    //    int dialog_storage_selected;
    int dialog_age_selected;
    int dialog_battery_selected;
    int dialog_locale_selected;
    int dialog_superimpose_selected;

    static final int DIALOG_SCREEN = 0;

    static final int DIALOG_AUDIOMODE = 1;
    static final int DIALOG_SLEEP = 2;
    static final int DIALOG_STORAGE = 3;
    static final int DIALOG_AGE = 4;
    static final int DIALOG_PASSWORD = 5;
    static final int DIALOG_BATTERY = 6;
    static final int DIALOG_RESET = 7;    // justin add
    static final int DIALOG_CHECK_PW_SETAGE = 8;
    static final int DIALOG_SET_PASSWORD = 9;
    static final int DIALOG_RESTORE_PASSWORD = 10;
    static final int DIALOG_SET_LOCALE = 11;
    static final int DIALOG_AUDIOTRACK = 12;
    static final int DIALOG_INTERACTIVE = 13;

    static final int DIALOG_SVCMODE_SWITCH_JP = 14;

    static final int DIALOG_AREA = 15;

    static final int DIALOG_SET_CAPTION = 16;
    static final int DIALOG_SET_SUPERIMPOSE = 17;
    static final int DIALOG_VIDEOTRACK = 18;

    static final int DIALOG_PREFECTURE_0 = 21;
    static final int DIALOG_PREFECTURE_1 = 22;
    static final int DIALOG_PREFECTURE_2 = 23;
    static final int DIALOG_PREFECTURE_3 = 24;
    static final int DIALOG_PREFECTURE_4 = 25;
    static final int DIALOG_PREFECTURE_5 = 26;
    static final int DIALOG_PREFECTURE_6 = 27;
    static final int DIALOG_PREFECTURE_7 = 28;

    static final int DIALOG_LOCALITY_00 = 31;
    static final int DIALOG_LOCALITY_01 = 32;
    static final int DIALOG_LOCALITY_02 = 33;
    static final int DIALOG_LOCALITY_03 = 34;
    static final int DIALOG_LOCALITY_04 = 35;
    static final int DIALOG_LOCALITY_05 = 36;
    static final int DIALOG_LOCALITY_06 = 37;

    static final int DIALOG_LOCALITY_10 = 38;
    static final int DIALOG_LOCALITY_11 = 39;
    static final int DIALOG_LOCALITY_12 = 40;
    static final int DIALOG_LOCALITY_13 = 41;
    static final int DIALOG_LOCALITY_14 = 42;
    static final int DIALOG_LOCALITY_15 = 43;
    static final int DIALOG_LOCALITY_16 = 44;

    static final int DIALOG_LOCALITY_20 = 45;
    static final int DIALOG_LOCALITY_21 = 46;
    static final int DIALOG_LOCALITY_22 = 47;
    static final int DIALOG_LOCALITY_23 = 48;
    static final int DIALOG_LOCALITY_24 = 49;
    static final int DIALOG_LOCALITY_25 = 50;

    static final int DIALOG_LOCALITY_30 = 51;
    static final int DIALOG_LOCALITY_31 = 52;
    static final int DIALOG_LOCALITY_32 = 53;
    static final int DIALOG_LOCALITY_33 = 54;

    static final int DIALOG_LOCALITY_40 = 55;
    static final int DIALOG_LOCALITY_41 = 56;
    static final int DIALOG_LOCALITY_42 = 57;
    static final int DIALOG_LOCALITY_43 = 58;
    static final int DIALOG_LOCALITY_44 = 59;
    static final int DIALOG_LOCALITY_45 = 60;

    static final int DIALOG_LOCALITY_50 = 61;
    static final int DIALOG_LOCALITY_51 = 62;
    static final int DIALOG_LOCALITY_52 = 63;
    static final int DIALOG_LOCALITY_53 = 64;
    static final int DIALOG_LOCALITY_54 = 65;

    static final int DIALOG_LOCALITY_60 = 66;
    static final int DIALOG_LOCALITY_61 = 67;
    static final int DIALOG_LOCALITY_62 = 68;
    static final int DIALOG_LOCALITY_63 = 69;

    static final int DIALOG_LOCALITY_70 = 70;
    static final int DIALOG_LOCALITY_71 = 71;
    static final int DIALOG_LOCALITY_72 = 72;
    static final int DIALOG_LOCALITY_73 = 73;
    static final int DIALOG_LOCALITY_74 = 74;
    static final int DIALOG_LOCALITY_75 = 75;
    static final int DIALOG_LOCALITY_76 = 76;
    static final int DIALOG_LOCALITY_77 = 77;

    static final int MAX_AUDIO_TRACK_NUM = 4;
    static final int MAX_VIDEO_TRACK_NUM = 4;
    static final int MAX_CAPTION_NUM = 2;

    static final int DIALOG_BCAS_CARD_TEST_GOOD = 80;
    static final int DIALOG_BCAS_CARD_TEST_NG = 81;

    static final int DIALOG_AUTO_SEARCH_JP = 82;

    private final static int SCAN_WAIT_TIME = 2000;

    CharSequence[] language;
    CharSequence[] selectableVideo;
    CharSequence[] capLanguage;
    CharSequence[] superimposeLanguage;
    private int audioNum;
    private int videoNum;

    TextView setage;
    TextView password;

    LinearLayout ll_autoSearch;
    Button btn00;

    LinearLayout ll_interactive;
    Button btn7;

    LinearLayout ll_changeArea;
    Button btn01;

    LinearLayout svcmodeswitch;

    MaterialDialog dialog_bcas_tested;

    LinearLayout parental;
    Button btn9;
    LinearLayout age;
    Button btn10;

    LinearLayout lPassword;
    Button btn11;
    LinearLayout lLocale;
    Button btn14;
    LinearLayout lReset;
    Button btn13;

    public static Activity SActivity;

    public static SettingActivity instance;
    public static SettingActivity getInstance()
    {
        return instance;
    }

    public Handler SettingActivityHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            TVEVENT event = TVEVENT.values()[msg.what];
            switch(event)
            {
                case E_CHANNEL_LIST_REMOVED:
                {
                    if (buildOption.ADD_TS_CAPTURE != true) {
                        new InputDialog(instance, InputDialog.TYPE_RESTORE_NOCHANNEL, null, null, null);
                        sendEvent(TVEVENT.E_SETTING_EXIT);
                    }
                }
                break;

                case E_SETTING_EXIT:
                {
                    finish();
                }
                break;

                case E_BCAS_TESTING_DIALOG:
                {
                    if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB) {
                        dialog_bcas_tested = new MaterialDialog.Builder(SettingActivity.this)
                                .theme(Theme.LIGHT)
                                .content(R.string.bcas_tested_msg)
                                .contentColor(getResources().getColor(R.color.black))
                                .show();
                        View decorView = dialog_bcas_tested.getWindow().getDecorView();
                        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE;    // justin
                        decorView.setSystemUiVisibility(uiOptions);

                        postEvent(TVEVENT.E_BCAS_TEST_COMPLETE_DIALOG, 3000);
                    }
                }
                break;

                case E_BCAS_TEST_COMPLETE_DIALOG:
                {
                    if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB) {
                        if (MainActivity.cardStr != null && (MainActivity.is_inserted_card==1)) {
                            TVlog.i(TAG, " >>>>> Card ID = " + MainActivity.cardStr);
                            if (dialog_bcas_tested != null && dialog_bcas_tested.isShowing()) {
                                dialog_bcas_tested.dismiss();
                            }
                            showDialog(DIALOG_BCAS_CARD_TEST_GOOD);

                        } else {
                            TVlog.i(TAG, " >>>>> Card test is failed!!");
                            if (dialog_bcas_tested != null && dialog_bcas_tested.isShowing()) {
                                dialog_bcas_tested.dismiss();
                            }
                            showDialog(DIALOG_BCAS_CARD_TEST_NG);
                        }
                    }
                }
                break;
                // [[ solution switching mode 20170223
                case E_SOLUTION_MODE_SWITCHING:

                    // new InputDialog(instance, InputDialog.TYPE_TV_SYSTEMCHANGE, null, null, null);
                    if (CommonStaticData.settingActivityShow)
                        SettingActivity.getInstance().sendEvent(TVEVENT.E_SETTING_EXIT);
                    MainActivity.getInstance().sendEvent(TVEVENT.E_SCAN_START);

                    break;
                // ]]
            }
            super.handleMessage(msg);
        }};


    public void sendEvent(TVEVENT _Event) {
        int m;
        m = _Event.ordinal();
        Message msg = SettingActivityHandler.obtainMessage(m);
        SettingActivityHandler.sendMessage(msg);
    }

    public void postEvent(TVEVENT _Event,int _time )
    {
        int m;
        m = _Event.ordinal();
        Message msg = SettingActivityHandler.obtainMessage(m);
        SettingActivityHandler.sendEmptyMessageDelayed(m, _time);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //doIntentMessageHook();
        super.onCreate(savedInstanceState);
        /*if (Build.VERSION.SDK_INT >= 19) {
            setTheme(android.R.style.Theme_Material_NoActionBar_Fullscreen);
        }*/
        setContentView(R.layout.activity_setting);

        instance = this;
        SActivity = SettingActivity.this;

        MainActivity.isMainActivity = true;

        DebugMode.getDebugMode().setContext(getApplicationContext());
        CommonStaticData.settingActivityShow = true;

        Locale systemLocale = getResources().getConfiguration().locale;
        String strLanguage = systemLocale.getLanguage();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName,  Context.MODE_PRIVATE);   // justin add for db load

        LinearLayout title_setting = (LinearLayout) findViewById(R.id.title_setting);
        title_setting.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //ADD_GINGA_NCL[[
        ll_interactive = (LinearLayout) findViewById(R.id.interactive);
        btn7 = (Button) findViewById(R.id.btn7);
        if (buildOption.ADD_GINGA_NCL) {
            ll_interactive.setVisibility(View.VISIBLE);
            btn7.setVisibility(View.VISIBLE);
        } else {
            ll_interactive.setVisibility(View.GONE);
            btn7.setVisibility(View.GONE);
        }
        //]]ADD_GINGA_NCL


        ImageButton btn_back_setting = (ImageButton) findViewById(R.id.btn_back_setting);
        btn_back_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        button_screen = (Button) findViewById(R.id.button_screen);
        button_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_SCREEN);
            }
        });

        // live add
        ll_autoSearch = (LinearLayout) findViewById(R.id.ll_autoSearch);
        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG) {
            if (ll_autoSearch != null) {
                ll_autoSearch.setVisibility(View.VISIBLE);
            }
        } else {
            if (ll_autoSearch != null) {
                ll_autoSearch.setVisibility(View.GONE);
            }
        }
        button_autoSearch = (Button) findViewById(R.id.button_autoSearch);
        if (button_autoSearch != null) {
            button_autoSearch.setTextColor(getResources().getColorStateList(R.color.blue3));
            button_autoSearch.setTypeface(null, Typeface.BOLD);
            //button_svcmodeswitch.setShadowLayer(10.0f, 0.0f, 0.0f, Color.WHITE);
            button_autoSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(DIALOG_AUTO_SEARCH_JP);
                }
            });
        }
        //

        // live add
        svcmodeswitch = (LinearLayout) findViewById(R.id.svcmodeswitch);
        if (MainActivity.getInstance().strISDBMode.equalsIgnoreCase("ISDBT Oneseg")) {
            svcmodeswitch.setVisibility(View.GONE);
        } else if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB) {
            svcmodeswitch.setVisibility(View.VISIBLE);
        } else {
            svcmodeswitch.setVisibility(View.GONE);
        }
        //


        button_svcmodeswitch = (Button) findViewById(R.id.button_svcmodeswitch);
        button_svcmodeswitch.setTextColor(getResources().getColorStateList(R.color.blue3));
        button_svcmodeswitch.setTypeface(null, Typeface.BOLD);
        //button_svcmodeswitch.setShadowLayer(10.0f, 0.0f, 0.0f, Color.WHITE);
        button_svcmodeswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_SVCMODE_SWITCH_JP);
            }
        });


        arr_area = getResources().getStringArray(R.array.area);

        arr_prefecture_0 = getResources().getStringArray(R.array.prefecture_0);
        arr_prefecture_1 = getResources().getStringArray(R.array.prefecture_1);
        arr_prefecture_2 = getResources().getStringArray(R.array.prefecture_2);
        arr_prefecture_3 = getResources().getStringArray(R.array.prefecture_3);
        arr_prefecture_4 = getResources().getStringArray(R.array.prefecture_4);
        arr_prefecture_5 = getResources().getStringArray(R.array.prefecture_5);
        arr_prefecture_6 = getResources().getStringArray(R.array.prefecture_6);
        arr_prefecture_7 = getResources().getStringArray(R.array.prefecture_7);

        arr_locality_00 = getResources().getStringArray(R.array.locality_00);
        arr_locality_01 = getResources().getStringArray(R.array.locality_01);
        arr_locality_02 = getResources().getStringArray(R.array.locality_02);
        arr_locality_03 = getResources().getStringArray(R.array.locality_03);
        arr_locality_04 = getResources().getStringArray(R.array.locality_04);
        arr_locality_05 = getResources().getStringArray(R.array.locality_05);
        arr_locality_06 = getResources().getStringArray(R.array.locality_06);

        arr_locality_10 = getResources().getStringArray(R.array.locality_10);
        arr_locality_11 = getResources().getStringArray(R.array.locality_11);
        arr_locality_12 = getResources().getStringArray(R.array.locality_12);
        arr_locality_13 = getResources().getStringArray(R.array.locality_13);
        arr_locality_14 = getResources().getStringArray(R.array.locality_14);
        arr_locality_15 = getResources().getStringArray(R.array.locality_15);
        arr_locality_16 = getResources().getStringArray(R.array.locality_16);

        arr_locality_20 = getResources().getStringArray(R.array.locality_20);
        arr_locality_21 = getResources().getStringArray(R.array.locality_21);
        arr_locality_22 = getResources().getStringArray(R.array.locality_22);
        arr_locality_23 = getResources().getStringArray(R.array.locality_23);
        arr_locality_24 = getResources().getStringArray(R.array.locality_24);
        arr_locality_25 = getResources().getStringArray(R.array.locality_25);
        arr_locality_30 = getResources().getStringArray(R.array.locality_30);
        arr_locality_31 = getResources().getStringArray(R.array.locality_31);
        arr_locality_32 = getResources().getStringArray(R.array.locality_32);
        arr_locality_33 = getResources().getStringArray(R.array.locality_33);

        arr_locality_40 = getResources().getStringArray(R.array.locality_40);
        arr_locality_41 = getResources().getStringArray(R.array.locality_41);
        arr_locality_42 = getResources().getStringArray(R.array.locality_42);
        arr_locality_43 = getResources().getStringArray(R.array.locality_43);
        arr_locality_44 = getResources().getStringArray(R.array.locality_44);
        arr_locality_45 = getResources().getStringArray(R.array.locality_45);

        arr_locality_50 = getResources().getStringArray(R.array.locality_50);
        arr_locality_51 = getResources().getStringArray(R.array.locality_51);
        arr_locality_52 = getResources().getStringArray(R.array.locality_52);
        arr_locality_53 = getResources().getStringArray(R.array.locality_53);
        arr_locality_54 = getResources().getStringArray(R.array.locality_54);

        arr_locality_60 = getResources().getStringArray(R.array.locality_60);
        arr_locality_61 = getResources().getStringArray(R.array.locality_61);
        arr_locality_62 = getResources().getStringArray(R.array.locality_62);
        arr_locality_63 = getResources().getStringArray(R.array.locality_63);

        arr_locality_70 = getResources().getStringArray(R.array.locality_70);
        arr_locality_71 = getResources().getStringArray(R.array.locality_71);
        arr_locality_72 = getResources().getStringArray(R.array.locality_72);
        arr_locality_73 = getResources().getStringArray(R.array.locality_73);
        arr_locality_74 = getResources().getStringArray(R.array.locality_74);
        arr_locality_75 = getResources().getStringArray(R.array.locality_75);
        arr_locality_76 = getResources().getStringArray(R.array.locality_76);
        arr_locality_77 = getResources().getStringArray(R.array.locality_77);

        ll_changeArea = (LinearLayout) findViewById(R.id.ll_changeArea);
        btn01 = (Button) findViewById(R.id.btn01);
        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
            ll_changeArea.setVisibility(View.VISIBLE);
            btn01.setVisibility(View.VISIBLE);
        } else {
            ll_changeArea.setVisibility(View.GONE);
            btn01.setVisibility(View.GONE);
        }

        button_area = (Button) findViewById(R.id.button_area);
        button_area.setTextColor(getResources().getColorStateList(R.color.blue3));
        button_area.setTypeface(null, Typeface.BOLD);
        //button_area.setShadowLayer(10.0f, 0.0f, 0.0f, Color.WHITE);
        //button_area.setText(arr_area[Integer.parseInt(CommonStaticData.areaSet)]);
        button_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_AREA);
            }
        });

        button_prefecture = (Button) findViewById(R.id.button_prefecture);
        button_prefecture.setTextColor(getResources().getColorStateList(R.color.blue3));
        button_prefecture.setTypeface(null, Typeface.BOLD);
        //button_prefecture.setShadowLayer(10.0f, 0.0f, 0.0f, Color.WHITE);
        button_prefecture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((CommonStaticData.areaSet).equalsIgnoreCase("0")) {
                    showDialog(DIALOG_PREFECTURE_0);
                } else if ((CommonStaticData.areaSet).equalsIgnoreCase("1")) {
                    showDialog(DIALOG_PREFECTURE_1);
                } else if ((CommonStaticData.areaSet).equalsIgnoreCase("2")) {
                    showDialog(DIALOG_PREFECTURE_2);
                } else if ((CommonStaticData.areaSet).equalsIgnoreCase("3")) {
                    showDialog(DIALOG_PREFECTURE_3);
                } else if ((CommonStaticData.areaSet).equalsIgnoreCase("4")) {
                    showDialog(DIALOG_PREFECTURE_4);
                } else if ((CommonStaticData.areaSet).equalsIgnoreCase("5")) {
                    showDialog(DIALOG_PREFECTURE_5);
                } else if ((CommonStaticData.areaSet).equalsIgnoreCase("6")) {
                    showDialog(DIALOG_PREFECTURE_6);
                } else if ((CommonStaticData.areaSet).equalsIgnoreCase("7")) {
                    showDialog(DIALOG_PREFECTURE_7);
                }
            }
        });

        button_locality = (Button) findViewById(R.id.button_locality);
        button_locality.setTextColor(getResources().getColorStateList(R.color.blue3));
        button_locality.setTypeface(null, Typeface.BOLD);
        //button_locality.setShadowLayer(10.0f, 0.0f, 0.0f, Color.WHITE);
        button_locality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((CommonStaticData.prefectureSet).equalsIgnoreCase("0/0")) {
                    showDialog(DIALOG_LOCALITY_00);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("0/1")) {
                    showDialog(DIALOG_LOCALITY_01);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("0/2")) {
                    showDialog(DIALOG_LOCALITY_02);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("0/3")) {
                    showDialog(DIALOG_LOCALITY_03);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("0/4")) {
                    showDialog(DIALOG_LOCALITY_04);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("0/5")) {
                    showDialog(DIALOG_LOCALITY_05);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("0/6")) {
                    showDialog(DIALOG_LOCALITY_06);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("1/0")) {
                    showDialog(DIALOG_LOCALITY_10);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("1/1")) {
                    showDialog(DIALOG_LOCALITY_11);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("1/2")) {
                    showDialog(DIALOG_LOCALITY_12);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("1/3")) {
                    showDialog(DIALOG_LOCALITY_13);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("1/4")) {
                    showDialog(DIALOG_LOCALITY_14);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("1/5")) {
                    showDialog(DIALOG_LOCALITY_15);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("1/6")) {
                    showDialog(DIALOG_LOCALITY_16);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("2/0")) {
                    showDialog(DIALOG_LOCALITY_20);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("2/1")) {
                    showDialog(DIALOG_LOCALITY_21);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("2/2")) {
                    showDialog(DIALOG_LOCALITY_22);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("2/3")) {
                    showDialog(DIALOG_LOCALITY_23);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("2/4")) {
                    showDialog(DIALOG_LOCALITY_24);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("2/5")) {
                    showDialog(DIALOG_LOCALITY_25);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("3/0")) {
                    showDialog(DIALOG_LOCALITY_30);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("3/1")) {
                    showDialog(DIALOG_LOCALITY_31);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("3/2")) {
                    showDialog(DIALOG_LOCALITY_32);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("3/3")) {
                    showDialog(DIALOG_LOCALITY_33);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("4/0")) {
                    showDialog(DIALOG_LOCALITY_40);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("4/1")) {
                    showDialog(DIALOG_LOCALITY_41);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("4/2")) {
                    showDialog(DIALOG_LOCALITY_42);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("4/3")) {
                    showDialog(DIALOG_LOCALITY_43);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("4/4")) {
                    showDialog(DIALOG_LOCALITY_44);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("4/5")) {
                    showDialog(DIALOG_LOCALITY_45);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("5/0")) {
                    showDialog(DIALOG_LOCALITY_50);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("5/1")) {
                    showDialog(DIALOG_LOCALITY_51);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("5/2")) {
                    showDialog(DIALOG_LOCALITY_52);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("5/3")) {
                    showDialog(DIALOG_LOCALITY_53);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("5/4")) {
                    showDialog(DIALOG_LOCALITY_54);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("6/0")) {
                    showDialog(DIALOG_LOCALITY_60);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("6/1")) {
                    showDialog(DIALOG_LOCALITY_61);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("6/2")) {
                    showDialog(DIALOG_LOCALITY_62);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("6/3")) {
                    showDialog(DIALOG_LOCALITY_63);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("7/0")) {
                    showDialog(DIALOG_LOCALITY_70);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("7/1")) {
                    showDialog(DIALOG_LOCALITY_71);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("7/2")) {
                    showDialog(DIALOG_LOCALITY_72);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("7/3")) {
                    showDialog(DIALOG_LOCALITY_73);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("7/4")) {
                    showDialog(DIALOG_LOCALITY_74);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("7/5")) {
                    showDialog(DIALOG_LOCALITY_75);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("7/6")) {
                    showDialog(DIALOG_LOCALITY_76);
                } else if ((CommonStaticData.prefectureSet).equalsIgnoreCase("7/7")) {
                    showDialog(DIALOG_LOCALITY_77);
                }
            }
        });

        button_audio = (Button) findViewById(R.id.button_audio);
        button_audio.setTextColor(getResources().getColorStateList(R.color.blue3));
        button_audio.setTypeface(null, Typeface.BOLD);
        //button_audio.setShadowLayer(10.0f, 0.0f, 0.0f, Color.WHITE);
        button_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_AUDIOMODE);
            }
        });

        textView_audiotrack = (TextView)findViewById(R.id.textViewTrack);
        button_audiotrack = (Button) findViewById(R.id.button_audiotrack);
        button_audiotrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_AUDIOTRACK);
            }
        });

        textView_videotrack = (TextView)findViewById(R.id.textViewVideoTrack);
        button_videotrack = (Button) findViewById(R.id.button_videotrack);
        button_videotrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_VIDEOTRACK);
            }
        });

        button_sleep = (Button) findViewById(R.id.button_sleep);
        button_sleep.setTextColor(getResources().getColorStateList(R.color.blue3));
        button_sleep.setTypeface(null, Typeface.BOLD);
        //button_sleep.setShadowLayer(10.0f, 0.0f, 0.0f, Color.WHITE);
        button_sleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_SLEEP);
            }
        });

      /*  button_storage = (Button) findViewById(R.id.button_storage);
        button_storage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_STORAGE);
            }
        });*/

        setage = (TextView)findViewById(R.id.textVie11);
        button_age = (Button) findViewById(R.id.button_age);
        button_age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // showDialog(DIALOG_AGE);
                showDialog(DIALOG_CHECK_PW_SETAGE);
            }
        });

        // justin
        lPassword = (LinearLayout)findViewById(R.id.password);
        btn11 = (Button)findViewById(R.id.btn11);
        if (buildOption.SETTING_PASSWORD_USE==false) {
            lPassword.setVisibility(View.GONE);
            btn11.setVisibility(View.GONE);
        }

        password = (TextView)findViewById(R.id.textView12);
        button_password = (Button) findViewById(R.id.button_password);
        if (Build.VERSION.SDK_INT <= 19) {
            button_password.setPadding(10,10,10,10);
        }
        button_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_SET_PASSWORD);
            }
        }) ;

        button_battery = (Button) findViewById(R.id.button_battery);
        button_battery.setTextColor(getResources().getColorStateList(R.color.blue3));
        button_battery.setTypeface(null, Typeface.BOLD);
        //button_battery.setShadowLayer(10.0f, 0.0f, 0.0f, Color.WHITE);
        button_battery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_BATTERY);
            }
        });

        if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 )  {

            ll_bcas_id = (LinearLayout) findViewById(R.id.ll_bcas_id);
            ll_bcas_test = (LinearLayout) findViewById(R.id.ll_bcas_test);
            ll_bcas_test.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CommonStaticData.scanningNow == false && (MainActivity.is_inserted_card==1)) {

                        sendEvent(TVEVENT.E_BCAS_TESTING_DIALOG);

                        FCI_TVi.bcasTest();
                        bcas_update();
                    }

                }

            });

            btn21 = (Button) findViewById(R.id.btn21);
            btn22 = (Button) findViewById(R.id.btn22);
            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB) {
                ll_bcas_id.setVisibility(View.VISIBLE);
                ll_bcas_test.setVisibility(View.VISIBLE);
                btn21.setVisibility(View.VISIBLE);
                btn22.setVisibility(View.VISIBLE);
            } else {
                ll_bcas_id.setVisibility(View.GONE);
                ll_bcas_test.setVisibility(View.GONE);
                btn21.setVisibility(View.GONE);
                btn22.setVisibility(View.GONE);
            }

            bcas_id_title = (TextView) findViewById(R.id.tv_bcas_id_title);

            tv_bcas_id = (TextView) findViewById(R.id.tv_bcas_id);

            if (MainActivity.cardStr != null && (MainActivity.is_inserted_card==1)) {
                tv_bcas_id.setText(MainActivity.cardStr);
            } else {
                tv_bcas_id.setText("- - - -   - - - -   - - - -   - - - -   - - - -");
            }

            tv_bcas_test = (TextView) findViewById(R.id.tv_bcas_test);

            bcas_update();
        }
        textView_about = (TextView) findViewById(R.id.textView_about);
        textView_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, AboutActivity.class);
                startActivity(intent);
                finish();
            }
        });

        arr_screen = getResources().getStringArray(R.array.screen_ratio);

        arr_autosearch_jp = getResources().getStringArray(R.array.auto_search_jp);

        arr_svcmodeswitch_jp = getResources().getStringArray(R.array.svcmode_switch_jp);

        arr_audio = getResources().getStringArray(R.array.audio_language);
        arr_audiotrack = getResources().getStringArray(R.array.audio_track);
        arr_videotrack = getResources().getStringArray(R.array.video_track);
        arr_sleep = getResources().getStringArray(R.array.sleep_timer);
        //arr_storage = getResources().getStringArray(R.array.storage_location);
        arr_age = getResources().getStringArray(R.array.set_age);
        arr_battery = getResources().getStringArray(R.array.battery_limit);

        arr_locale = getResources().getStringArray(R.array.locale_set);
        textView_locale = (TextView)findViewById(R.id.setLocale);
        textView_locale.setTextColor(getResources().getColorStateList(R.color.blue3));
        textView_locale.setTypeface(null, Typeface.BOLD);
        //textView_locale.setShadowLayer(10.0f, 0.0f, 0.0f, Color.WHITE);
        imageView_flag = (ImageView)findViewById(R.id.Locale_flag);

        // justin
        lLocale = (LinearLayout)findViewById(R.id.locale);
        btn14 = (Button)findViewById(R.id.btn14);
        if (buildOption.SETTING_LOCALE_USE==false) {
            lLocale.setVisibility(View.GONE);
            btn14.setVisibility(View.GONE);
        }
        LinearLayout setLocale = (LinearLayout) findViewById(R.id.locale_range);
        setLocale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_SET_LOCALE);
            }
        });

        textView_caption = (TextView)findViewById(R.id.textView2);
        switchCaption = (Switch) findViewById(R.id.switchCaption);

        textView_caption.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction()==MotionEvent.ACTION_DOWN) {
                    TVlog.i("TEST", " textView_caption Down ");
                    DebugMode.getDebugMode().PressDebugMode();
                }
                return true;
            }
        });

        textView_captionselect = (TextView)findViewById(R.id.textViewCaptionselect);
        button_captionselect = (Button)findViewById(R.id.button_captionselect);
        button_captionselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonStaticData.captionSwitch == true) {
                    showDialog(DIALOG_SET_CAPTION);
                }
            }
        });

        /*
        if (CommonStaticData.captionSwitch == true) {
            switchCaption.setEnabled(true);
            switchCaption.setChecked(true);
            switchCaption.getThumbDrawable().setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
            switchCaption.getTrackDrawable().setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
            MainActivity.getInstance().sendEvent(TVEVENT.E_CAPTION_NOTIFY);
            button_captionselect.setTextColor(getResources().getColorStateList(R.color.cyan));
            button_captionselect.setTypeface(null, Typeface.BOLD);
            button_captionselect.setShadowLayer(10.0f, 0.0f, 0.0f, Color.WHITE);
        } else {
            switchCaption.setEnabled(false);
            switchCaption.setChecked(false);
            switchCaption.getThumbDrawable().setColorFilter(getResources().getColor(R.color.light_gray), PorterDuff.Mode.MULTIPLY);
            switchCaption.getTrackDrawable().setColorFilter(getResources().getColor(R.color.light_gray), PorterDuff.Mode.MULTIPLY);
            MainActivity.getInstance().sendEvent(TVEVENT.E_CAPTION_CLEAR_NOTIFY);
            button_captionselect.setTextColor(Color.GRAY);
            button_captionselect.setTypeface(null, Typeface.NORMAL);
            button_captionselect.setShadowLayer(10.0f, 0.0f, 0.0f, Color.TRANSPARENT);
        }*/

        switchCaption.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int captionNum = FCI_TVi.GetSubtitleNum();
                if (captionNum > 0) {
                    if (isChecked) {
                        switchCaption.getThumbDrawable().setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                        switchCaption.getTrackDrawable().setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                        SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                        CommonStaticData.captionSwitch = true;
                        if (MainActivity.isMainActivity) {
                            MainActivity.getInstance().sendEvent(TVEVENT.E_CAPTION_NOTIFY);
                        } else if (FloatingWindow.isFloating) {
                            FloatingWindow.getInstance().sendEvent(TVEVENT.E_CAPTION_NOTIFY_FLOATING);
                        }
                        button_captionselect.setTextColor(getResources().getColorStateList(R.color.blue3));
                        button_captionselect.setTypeface(null, Typeface.BOLD);
                        //button_captionselect.setShadowLayer(10.0f, 0.0f, 0.0f, Color.WHITE);
                        switchCaption.setChecked(CommonStaticData.captionSwitch);
                    } else {
                        switchCaption.getThumbDrawable().setColorFilter(getResources().getColor(R.color.light_gray), PorterDuff.Mode.MULTIPLY);
                        switchCaption.getTrackDrawable().setColorFilter(getResources().getColor(R.color.light_gray), PorterDuff.Mode.MULTIPLY);
                        SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                        CommonStaticData.captionSwitch = false;
                        if (MainActivity.isMainActivity) {
                            MainActivity.getInstance().sendEvent(TVEVENT.E_CAPTION_CLEAR_NOTIFY);
                        } else if (FloatingWindow.isFloating) {
                            FloatingWindow.getInstance().sendEvent(TVEVENT.E_CAPTION_CLEAR_NOTIFY_FLOATING);
                        }
                        button_captionselect.setTextColor(Color.GRAY);
                        button_captionselect.setTypeface(null, Typeface.NORMAL);
                        //button_captionselect.setShadowLayer(10.0f, 0.0f, 0.0f, Color.TRANSPARENT);
                        switchCaption.setChecked(CommonStaticData.captionSwitch);
                    }
                }

            }
        });

        //ADD_GINGA_NCL[[
        if (buildOption.ADD_GINGA_NCL==true) {
            textView_interactive = (TextView) findViewById(R.id.textView8);
            switchInteractive = (Switch) findViewById(R.id.switchInteractive);
            switchInteractive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        CommonStaticData.interactiveSwitch = true;
                        switchInteractive.getThumbDrawable().setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                        switchInteractive.getTrackDrawable().setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                        MainActivity.getInstance().sendEvent(TVEVENT.E_INTERACTIVE_ENABLE);
                    } else {
                        CommonStaticData.interactiveSwitch=false;
                        switchInteractive.getThumbDrawable().setColorFilter(getResources().getColor(R.color.light_gray), PorterDuff.Mode.MULTIPLY);
                        switchInteractive.getTrackDrawable().setColorFilter(getResources().getColor(R.color.light_gray), PorterDuff.Mode.MULTIPLY);
                        MainActivity.getInstance().sendEvent(TVEVENT.E_INTERACTIVE_DISABLE);
                    }
                }
            });
        }
        //]]ADD_GINGA_NCL

        textView_superimpose = (TextView)findViewById(R.id.textView15);
        switchSuperimpose = (Switch) findViewById(R.id.switchSuperimpose);

        textView_superimpose.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction()==MotionEvent.ACTION_DOWN) {
                    TVlog.i("TEST", " textView_superimpose Down ");
                    DebugMode.getDebugMode().PressLogCaptureMode();
                }
                return true;
            }
        });

        textView_superimposeselect = (TextView)findViewById(R.id.textViewSuperimposeSelect);
        button_superimposeselect = (Button)findViewById(R.id.button_superimposeselect);
        button_superimposeselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonStaticData.superimposeSwitch == true) {
                    showDialog(DIALOG_SET_SUPERIMPOSE);
                }
            }
        });

        /*
        if (CommonStaticData.superimposeSwitch == true) {
            switchSuperimpose.setEnabled(true);
            switchSuperimpose.setChecked(true);
            switchSuperimpose.getThumbDrawable().setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
            switchSuperimpose.getTrackDrawable().setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
            MainActivity.getInstance().sendEvent(TVEVENT.E_SUPERIMPOSE_NOTIFY);
            button_superimposeselect.setTextColor(getResources().getColorStateList(R.color.cyan));
            button_superimposeselect.setTypeface(null, Typeface.BOLD);
            button_superimposeselect.setShadowLayer(10.0f, 0.0f, 0.0f, Color.WHITE);
        } else {
            switchSuperimpose.setEnabled(false);
            switchSuperimpose.setChecked(false);
            switchSuperimpose.getThumbDrawable().setColorFilter(getResources().getColor(R.color.light_gray), PorterDuff.Mode.MULTIPLY);
            switchSuperimpose.getTrackDrawable().setColorFilter(getResources().getColor(R.color.light_gray), PorterDuff.Mode.MULTIPLY);
            MainActivity.getInstance().sendEvent(TVEVENT.E_SUPERIMPOSE_CLEAR_NOTIFY);
            button_superimposeselect.setTextColor(Color.GRAY);
            button_superimposeselect.setTypeface(null, Typeface.NORMAL);
            button_superimposeselect.setShadowLayer(10.0f, 0.0f, 0.0f, Color.TRANSPARENT);
        }*/

        switchSuperimpose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int superimposeNum = FCI_TVi.GetSuperimposeNum();
                if (superimposeNum > 0) {
                    if (isChecked) {
                        switchSuperimpose.getThumbDrawable().setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                        switchSuperimpose.getTrackDrawable().setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                        SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                        CommonStaticData.superimposeSwitch = true;
                        MainActivity.getInstance().sendEvent(TVEVENT.E_SUPERIMPOSE_NOTIFY);
                        button_superimposeselect.setTextColor(getResources().getColorStateList(R.color.blue3));
                        button_superimposeselect.setTypeface(null, Typeface.BOLD);
                        //button_superimposeselect.setShadowLayer(10.0f, 0.0f, 0.0f, Color.WHITE);
                        switchSuperimpose.setChecked(CommonStaticData.superimposeSwitch);
                    } else {
                        switchSuperimpose.getThumbDrawable().setColorFilter(getResources().getColor(R.color.light_gray), PorterDuff.Mode.MULTIPLY);
                        switchSuperimpose.getTrackDrawable().setColorFilter(getResources().getColor(R.color.light_gray), PorterDuff.Mode.MULTIPLY);
                        SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                        CommonStaticData.superimposeSwitch = false;
                        MainActivity.getInstance().sendEvent(TVEVENT.E_SUPERIMPOSE_CLEAR_NOTIFY);
                        button_superimposeselect.setTextColor(Color.GRAY);
                        button_superimposeselect.setTypeface(null, Typeface.NORMAL);
                        //button_superimposeselect.setShadowLayer(10.0f, 0.0f, 0.0f, Color.TRANSPARENT);
                        switchSuperimpose.setChecked(CommonStaticData.superimposeSwitch);
                    }
                }
            }
        });

        // only Brazil, parental control is availble

        parental = (LinearLayout) findViewById(R.id.parental);
        btn9 = (Button) findViewById(R.id.btn9);
        age = (LinearLayout) findViewById(R.id.age);
        btn10 = (Button) findViewById(R.id.btn10);

        if ((buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL
                || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_ONESEG
                || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_FILE
                || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA
                || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_ONESEG
                || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) && buildOption.SETTING_PARENTAL_USE == true) {
            parental.setVisibility(View.VISIBLE);
            btn9.setVisibility(View.VISIBLE);
            age.setVisibility(View.VISIBLE);
            btn10.setVisibility(View.VISIBLE);
        } else {
            parental.setVisibility(View.GONE);
            btn9.setVisibility(View.GONE);
            age.setVisibility(View.GONE);
            btn10.setVisibility(View.GONE);
        }

        //

        switchParental = (Switch) findViewById(R.id.switchParental);
        if (CommonStaticData.ratingsetSwitch == false) {
            button_age.setEnabled(false);
            button_age.setTextColor(Color.GRAY);
            button_age.setTypeface(null, Typeface.NORMAL);
            //button_age.setShadowLayer(10.0f, 0.0f, 0.0f, Color.TRANSPARENT);
            setage.setEnabled(false);
            setage.setTextColor(Color.GRAY);
            button_password.setEnabled(false);
            button_password.setBackgroundResource(R.color.btn_disable);
            button_password.setTextColor(Color.GRAY);
            password.setEnabled(false);
            password.setTextColor(Color.GRAY);
        } else {
            button_age.setEnabled(true);
            button_age.setTextColor(getResources().getColorStateList(R.color.blue3));
            button_age.setTypeface(null, Typeface.BOLD);
            //button_age.setShadowLayer(10.0f, 0.0f, 0.0f, Color.WHITE);
            setage.setEnabled(true);
            setage.setTextColor(Color.WHITE);
            button_password.setEnabled(true);
            button_password.setBackgroundResource(R.color.blue3);
            button_password.setTextColor(Color.WHITE);
            password.setEnabled(true);
            password.setTextColor(Color.WHITE);
        }

        switchParental.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                if (isChecked == true) {
                    switchParental.getThumbDrawable().setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                    switchParental.getTrackDrawable().setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                    switchParental.setChecked(true);
                    button_age.setEnabled(true);
                    button_age.setTextColor(getResources().getColorStateList(R.color.blue3));
                    button_age.setTypeface(null, Typeface.BOLD);
                    //button_age.setShadowLayer(10.0f, 0.0f, 0.0f, Color.WHITE);
                    setage.setEnabled(true);
                    setage.setTextColor(Color.WHITE);
                    button_password.setEnabled(true);
                    button_password.setBackgroundResource(R.color.blue3);
                    button_password.setTextColor(Color.WHITE);
                    password.setEnabled(true);
                    password.setTextColor(Color.WHITE);
                    CommonStaticData.ratingsetSwitch = true;
                    TVlog.i(TAG, " ==> CommonStaticData.passwordVerifyFlag = "+CommonStaticData.passwordVerifyFlag
                            +", CommonStaticData.mainPasswordVerifyFlag = "+CommonStaticData.mainPasswordVerifyFlag
                            +", CommonStaticData.ratingsetSwitch = "+CommonStaticData.ratingsetSwitch);
                    if (!CommonStaticData.mainPasswordVerifyFlag) {
                        if ((CommonStaticData.passwordVerifyFlag == false)
                                && (CommonStaticData.ratingsetSwitch == true)) {
                            CommonStaticData.ageLimitFlag = true;
                        } else {
                            CommonStaticData.ageLimitFlag = false;
                        }
                        MainActivity.getInstance().sendEvent(TVEVENT.E_RATING_MONITOR);
                    } else {
                        CommonStaticData.ageLimitFlag = false;
                        MainActivity.getInstance().sendEvent(TVEVENT.E_RATING_MONITOR);
                    }
                } else {
                    String saved = CommonStaticData.settings.getString(CommonStaticData.passwordKey, null);
                    if (saved == null || saved.length() == 0) {
                        CustomToast customToast = new CustomToast(getApplicationContext());
                        customToast.showToast(SettingActivity.this, R.string.settings_password_needed, Toast.LENGTH_SHORT);
                        switchParental.setChecked(true);
                        CommonStaticData.ratingsetSwitch = true;
                        return;
                    }

                    MaterialDialog dialog = new MaterialDialog.Builder(SettingActivity.this)
                            .theme(Theme.LIGHT)
                            .iconRes(R.drawable.ic_lock_outline_gray_48dp)
                            .title(R.string.settings_password)
                            .titleColor(getResources().getColor(R.color.black))
                            .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                            .positiveText(R.string.submit)
                            .positiveColor(getResources().getColor(R.color.blue3))
                            .negativeText(R.string.cancel)
                            .positiveColor(getResources().getColor(R.color.blue3))
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onNegative(MaterialDialog dialog) {
                                    super.onNegative(dialog);
                                    /*
                                    if ((MainActivity.getInstance().curr_rate >= (CommonStaticData.PG_Rate+1) && (CommonStaticData.PG_Rate!=0))
                                            && (CommonStaticData.passwordVerifyFlag == false)
                                            && (CommonStaticData.ratingsetSwitch == true)) {
                                        CommonStaticData.ageLimitFlag = true;
                                    } else {
                                        CommonStaticData.ageLimitFlag = false;
                                    }
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_RATING_MONITOR);
                                    */
                                    switchParental.setChecked(true);
                                    CommonStaticData.ratingsetSwitch = true;
                                    CommonStaticData.passwordVerifyFlag = false;
                                    CommonStaticData.mainPasswordVerifyFlag = false;
                                    CommonStaticData.ageLimitFlag = true;
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_RATING_MONITOR);
                                }
                            })
                            .input(null, null, new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(MaterialDialog dialog, CharSequence input) {
                                    String save = input.toString();
                                    CommonStaticData.PassWord = CommonStaticData.settings.getString(CommonStaticData.passwordKey, null);
                                    if (CommonStaticData.PassWord == null) {
                                        /*
                                        CommonStaticData.ratingsetSwitch = true;
                                        if ((MainActivity.getInstance().curr_rate >= (CommonStaticData.PG_Rate+1) && (CommonStaticData.PG_Rate!=0))
                                                && (CommonStaticData.passwordVerifyFlag == false)
                                                && (CommonStaticData.ratingsetSwitch == true)) {
                                            CommonStaticData.ageLimitFlag = true;
                                        } else {
                                            CommonStaticData.ageLimitFlag = false;
                                        }
                                        MainActivity.getInstance().sendEvent(TVEVENT.E_RATING_MONITOR);
                                        */
                                        switchParental.setChecked(true);
                                        CommonStaticData.ratingsetSwitch = true;
                                        CommonStaticData.passwordVerifyFlag = false;
                                        CommonStaticData.ageLimitFlag = true;
                                        MainActivity.getInstance().sendEvent(TVEVENT.E_RATING_MONITOR);
                                        showDialog(DIALOG_PASSWORD);
                                        return;
                                    }
                                    if (CommonStaticData.PassWord.equals(save)) {

                                        CommonStaticData.ratingsetSwitch = false;
                                        CommonStaticData.passwordVerifyFlag = false;    // justin 20170523
                                        CommonStaticData.mainPasswordVerifyFlag = false;
                                        CommonStaticData.ageLimitFlag = false;
                                        //MainActivity.getInstance().sendEvent(TVEVENT.E_RATING_MONITOR);
                                        //CommonStaticData.screenBlockFlag = false;
                                        MainActivity.getInstance().sendEvent(TVEVENT.E_CONFIRMED_PASSWORD);
                                        switchParental.getThumbDrawable().setColorFilter(getResources().getColor(R.color.light_gray), PorterDuff.Mode.MULTIPLY);
                                        switchParental.getTrackDrawable().setColorFilter(getResources().getColor(R.color.light_gray), PorterDuff.Mode.MULTIPLY);
                                        //switchParental.setChecked(false);
                                        button_age.setEnabled(false);
                                        button_age.setTextColor(Color.GRAY);
                                        button_age.setTypeface(null, Typeface.NORMAL);
                                        //button_age.setShadowLayer(10.0f, 0.0f, 0.0f, Color.TRANSPARENT);
                                        setage.setEnabled(false);
                                        setage.setTextColor(Color.GRAY);
                                        button_password.setEnabled(false);
                                        button_password.setBackgroundResource(R.color.btn_disable);
                                        button_password.setTextColor(Color.GRAY);
                                        password.setEnabled(false);
                                        password.setTextColor(Color.GRAY);
                                        //switchParental.setChecked(CommonStaticData.ratingsetSwitch);
                                    } else {
                                        CommonStaticData.ratingsetSwitch = true;
                                        CommonStaticData.passwordVerifyFlag = false;
                                        CommonStaticData.ageLimitFlag = true;
                                        MainActivity.getInstance().sendEvent(TVEVENT.E_RATING_MONITOR);
                                        switchParental.getThumbDrawable().setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                                        switchParental.getTrackDrawable().setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                                        switchParental.setChecked(true);
                                        button_age.setEnabled(true);
                                        button_age.setTextColor(getResources().getColorStateList(R.color.blue3));
                                        button_age.setTypeface(null, Typeface.BOLD);
                                        //button_age.setShadowLayer(10.0f, 0.0f, 0.0f, Color.WHITE);
                                        setage.setEnabled(true);
                                        setage.setTextColor(Color.WHITE);
                                        button_password.setEnabled(true);
                                        button_password.setBackgroundResource(R.color.blue3);
                                        button_age.setTextColor(getResources().getColorStateList(R.color.blue3));
                                        button_age.setTypeface(null, Typeface.BOLD);
                                        //button_age.setShadowLayer(10.0f, 0.0f, 0.0f, Color.WHITE);
                                        password.setEnabled(true);
                                        password.setTextColor(Color.WHITE);

                                        //switchParental.setChecked(CommonStaticData.ratingsetSwitch);
                                        CustomToast customToast = new CustomToast(getApplicationContext());
                                        customToast.showToast(SettingActivity.this, R.string.settings_password_wrong, Toast.LENGTH_SHORT);
                                    }
                                }
                            }).show();
                }
            }
        });

        // justin
        lReset = (LinearLayout)findViewById(R.id.reset);
        btn13 = (Button)findViewById(R.id.btn13);
        if (buildOption.SETTING_RESTORE_USE==false) {
            lReset.setVisibility(View.GONE);
            btn13.setVisibility(View.GONE);
        }

        switchReset = (Switch) findViewById(R.id.switchReset);
        switchReset.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    showDialog(DIALOG_RESTORE_PASSWORD);
                } else {

                }
            }
        });

        seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
        seekBar2 = (SeekBar) findViewById(R.id.seekBar2);

        seekBar1.setOnSeekBarChangeListener(new MyBrightnessChangeListener());
        seekBar2.setOnSeekBarChangeListener(new MyTransparentChangeListener());

        Button button_extra = (Button) findViewById(R.id.button_extra);
        button_extra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonStaticData.settingActivityShow = false;
                finish();
            }
        });
    }

    // justin add for init load
    protected void onResume() {
        CommonStaticData.settingActivityShow = true;

        CommonStaticData.currentScaleMode = CommonStaticData.settings.getInt(CommonStaticData.currentScaleModeKey, 0);
        CommonStaticData.captionSwitch = CommonStaticData.settings.getBoolean(CommonStaticData.captionSwitchKey, true);
        int captionNum = FCI_TVi.GetSubtitleNum();
        if (captionNum==0) {    // justin caption selection disable when no caption data
            textView_caption.setTextColor(Color.GRAY);
            switchCaption.setChecked(false);
            switchCaption.setEnabled(false);
            switchCaption.getThumbDrawable().setColorFilter(getResources().getColor(R.color.light_gray), PorterDuff.Mode.MULTIPLY);
            switchCaption.getTrackDrawable().setColorFilter(getResources().getColor(R.color.light_gray), PorterDuff.Mode.MULTIPLY);
            textView_captionselect.setTextColor(Color.GRAY);
            button_captionselect.setEnabled(false);
            button_captionselect.setTextColor(Color.GRAY);
            button_captionselect.setTypeface(null, Typeface.NORMAL);
            //button_captionselect.setShadowLayer(10.0f, 0.0f, 0.0f, Color.TRANSPARENT);
        } else {
            textView_caption.setTextColor(Color.WHITE);
            if (CommonStaticData.captionSwitch == true) {
                switchCaption.setChecked(true);
                switchCaption.setEnabled(true);
                switchCaption.getThumbDrawable().setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                switchCaption.getTrackDrawable().setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                if (MainActivity.isMainActivity) {
                    MainActivity.getInstance().sendEvent(TVEVENT.E_CAPTION_NOTIFY);
                } else if (FloatingWindow.isFloating) {
                    FloatingWindow.getInstance().sendEvent(TVEVENT.E_CAPTION_NOTIFY_FLOATING);
                }
                button_captionselect.setTextColor(getResources().getColorStateList(R.color.blue3));
                button_captionselect.setTypeface(null, Typeface.BOLD);
                //button_captionselect.setShadowLayer(10.0f, 0.0f, 0.0f, Color.WHITE);
            } else {
                switchCaption.setChecked(false);
                switchCaption.setEnabled(true);
                switchCaption.getThumbDrawable().setColorFilter(getResources().getColor(R.color.light_gray), PorterDuff.Mode.MULTIPLY);
                switchCaption.getTrackDrawable().setColorFilter(getResources().getColor(R.color.light_gray), PorterDuff.Mode.MULTIPLY);
                MainActivity.getInstance().sendEvent(TVEVENT.E_CAPTION_CLEAR_NOTIFY);
                button_captionselect.setTextColor(Color.GRAY);
                button_captionselect.setTypeface(null, Typeface.NORMAL);
                //button_captionselect.setShadowLayer(10.0f, 0.0f, 0.0f, Color.TRANSPARENT);
            }
            /*
            switchCaption.setEnabled(true);
            switchCaption.setChecked(CommonStaticData.captionSwitch);
            textView_captionselect.setTextColor(Color.WHITE);
            button_captionselect.setEnabled(true);
            */
            /*
            button_captionselect.setTextColor(getResources().getColorStateList(R.color.cyan));
            button_captionselect.setTypeface(null, Typeface.BOLD);
            button_captionselect.setShadowLayer(10.0f, 0.0f, 0.0f, Color.WHITE);
            */

            TVlog.i(TAG, " >>>>> caption cnt = " + captionNum);
            // max size 3 : eng, por, esp
            if (captionNum > MAX_CAPTION_NUM) {
                captionNum = MAX_CAPTION_NUM;
            }
            capLanguage = new CharSequence[captionNum];

            for (int j=0; j<captionNum; j++) {
                capLanguage[j] = FCI_TVi.GetSubtitleInfo(j);
                if (capLanguage[j].length()==0)
                    capLanguage[j] = getResources().getString(R.string.language) + (j+1);
            }

            button_captionselect.setText(capLanguage[CommonStaticData.captionSelect]);

/*
            Log.e("fcicaption", ">>>> caption track number=" + captionNum);
            if (captionNum > 1) {
                FCI_TVi.SelectCaption(captionNum - 1);
                Log.e("fcicaption", ">>>> forced to caption track to=" + captionNum);
            }
*/
        }
/*
        int superimposeNum = FCI_TVi.GetSuperimposeNum();
        Log.e("fcicaption", ">>>> superimpose track number=" + superimposeNum);
        if (superimposeNum > 1) {
            FCI_TVi.SelectSuperimpose(superimposeNum - 1);
            Log.e("fcicaption", ">>>> forced to superimpose track to=" + superimposeNum);
        }
*/
        CommonStaticData.superimposeSwitch = CommonStaticData.settings.getBoolean(CommonStaticData.superimposeSwitchKey, true);
        int superimposeNum = FCI_TVi.GetSuperimposeNum();
        if (superimposeNum==0) {    // justin caption selection disable when no caption data
            textView_superimpose.setTextColor(Color.GRAY);
            switchSuperimpose.setChecked(false);
            switchSuperimpose.setEnabled(false);
            switchSuperimpose.getThumbDrawable().setColorFilter(getResources().getColor(R.color.light_gray), PorterDuff.Mode.MULTIPLY);
            switchSuperimpose.getTrackDrawable().setColorFilter(getResources().getColor(R.color.light_gray), PorterDuff.Mode.MULTIPLY);
            textView_superimposeselect.setTextColor(Color.GRAY);
            button_superimposeselect.setEnabled(false);
            button_superimposeselect.setTextColor(Color.GRAY);
            button_superimposeselect.setTypeface(null, Typeface.NORMAL);
            //button_superimposeselect.setShadowLayer(10.0f, 0.0f, 0.0f, Color.TRANSPARENT);
        }
        else{
            textView_superimpose.setTextColor(Color.WHITE);
            if (CommonStaticData.superimposeSwitch == true) {
                switchSuperimpose.setChecked(true);
                switchSuperimpose.setEnabled(true);
                switchSuperimpose.getThumbDrawable().setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                switchSuperimpose.getTrackDrawable().setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                MainActivity.getInstance().sendEvent(TVEVENT.E_SUPERIMPOSE_NOTIFY);
                button_superimposeselect.setTextColor(getResources().getColorStateList(R.color.blue3));
                button_superimposeselect.setTypeface(null, Typeface.BOLD);
                //button_superimposeselect.setShadowLayer(10.0f, 0.0f, 0.0f, Color.WHITE);
            } else {
                switchSuperimpose.setChecked(false);
                switchSuperimpose.setEnabled(true);
                switchSuperimpose.getThumbDrawable().setColorFilter(getResources().getColor(R.color.light_gray), PorterDuff.Mode.MULTIPLY);
                switchSuperimpose.getTrackDrawable().setColorFilter(getResources().getColor(R.color.light_gray), PorterDuff.Mode.MULTIPLY);
                MainActivity.getInstance().sendEvent(TVEVENT.E_SUPERIMPOSE_CLEAR_NOTIFY);
                button_superimposeselect.setTextColor(Color.GRAY);
                button_superimposeselect.setTypeface(null, Typeface.NORMAL);
                //button_superimposeselect.setShadowLayer(10.0f, 0.0f, 0.0f, Color.TRANSPARENT);
            }
            /*
            switchSuperimpose.setEnabled(true);
            switchSuperimpose.setChecked(CommonStaticData.superimposeSwitch);
            textView_superimposeselect.setTextColor(Color.WHITE);
            button_superimposeselect.setEnabled(true);
            */

            TVlog.i(TAG, " >>>>> superimposeNum cnt = " + superimposeNum);
            // max size 3 : eng, por, esp
            if (superimposeNum > MAX_CAPTION_NUM) {
                superimposeNum = MAX_CAPTION_NUM;
            }
            superimposeLanguage = new CharSequence[superimposeNum];

            for (int j=0; j<superimposeNum; j++) {
                superimposeLanguage[j] = FCI_TVi.GetSuperimposeInfo(j);
                if (superimposeLanguage[j].length()==0)
                    superimposeLanguage[j] = getResources().getString(R.string.language) + (j+1);
            }

            button_superimposeselect.setText(superimposeLanguage[CommonStaticData.superimposeSelect]);
            /*
            button_superimposeselect.setTextColor(getResources().getColorStateList(R.color.cyan));
            button_superimposeselect.setTypeface(null, Typeface.BOLD);
            button_superimposeselect.setShadowLayer(10.0f, 0.0f, 0.0f, Color.WHITE);
            */
        }

        CommonStaticData.scaleSet= CommonStaticData.settings.getInt(CommonStaticData.scaleSwitchKey, 0);
        button_screen.setText(arr_screen[CommonStaticData.scaleSet]);

        /*
        CommonStaticData.brightness = CommonStaticData.settings.getInt(CommonStaticData.brightnessKey, 50);
        seekBar1.setProgress(CommonStaticData.brightness);

        CommonStaticData.transparent = CommonStaticData.settings.getInt(CommonStaticData.transparentKey, 50);
        seekBar2.setProgress(CommonStaticData.transparent);
        */

        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG) {
            CommonStaticData.autoSearch = CommonStaticData.settings.getInt(CommonStaticData.autoSearchSwitchKey, 0);
            if (button_autoSearch != null) {
                button_autoSearch.setText(arr_autosearch_jp[CommonStaticData.autoSearch]);
            }
        } else {
            CommonStaticData.autoSearch = CommonStaticData.settings.getInt(CommonStaticData.autoSearchSwitchKey, 1);
        }

        // live add
        if (MainActivity.getInstance().strISDBMode.equalsIgnoreCase("ISDBT Oneseg")) {
            CommonStaticData.receivemode = CommonStaticData.settings.getInt(CommonStaticData.receivemodeSwitchKey, CommonStaticData.RECEIVE_MODE_1SEG);; // 1seg
        } else {
            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB) {
                CommonStaticData.receivemode = CommonStaticData.settings.getInt(CommonStaticData.receivemodeSwitchKey, CommonStaticData.RECEIVE_MODE_AUTO);  // auto
            } else {
                CommonStaticData.receivemode = CommonStaticData.settings.getInt(CommonStaticData.receivemodeSwitchKey, CommonStaticData.RECEIVE_MODE_OFF);  // off
            }
        }
        button_svcmodeswitch.setText(arr_svcmodeswitch_jp[CommonStaticData.receivemode]);
        //

        CommonStaticData.areaSet = CommonStaticData.settings.getString(CommonStaticData.areaKey, "1");
        button_area.setText(arr_area[Integer.parseInt(CommonStaticData.areaSet)]);

        CommonStaticData.prefectureSet = CommonStaticData.settings.getString(CommonStaticData.prefectureKey, "1/5");
        String[] splitPrefecture =(CommonStaticData.prefectureSet).split("/");
        if ((splitPrefecture[0]).equalsIgnoreCase("0")) {
            button_prefecture.setText(arr_prefecture_0[Integer.parseInt(splitPrefecture[1])]);
        } else if ((splitPrefecture[0]).equalsIgnoreCase("1")) {
            button_prefecture.setText(arr_prefecture_1[Integer.parseInt(splitPrefecture[1])]);
        } else if ((splitPrefecture[0]).equalsIgnoreCase("2")) {
            button_prefecture.setText(arr_prefecture_2[Integer.parseInt(splitPrefecture[1])]);
        } else if ((splitPrefecture[0]).equalsIgnoreCase("3")) {
            button_prefecture.setText(arr_prefecture_3[Integer.parseInt(splitPrefecture[1])]);
        } else if ((splitPrefecture[0]).equalsIgnoreCase("4")) {
            button_prefecture.setText(arr_prefecture_4[Integer.parseInt(splitPrefecture[1])]);
        } else if ((splitPrefecture[0]).equalsIgnoreCase("5")) {
            button_prefecture.setText(arr_prefecture_5[Integer.parseInt(splitPrefecture[1])]);
        } else if ((splitPrefecture[0]).equalsIgnoreCase("6")) {
            button_prefecture.setText(arr_prefecture_6[Integer.parseInt(splitPrefecture[1])]);
        } else if ((splitPrefecture[0]).equalsIgnoreCase("7")) {
            button_prefecture.setText(arr_prefecture_7[Integer.parseInt(splitPrefecture[1])]);
        }

        CommonStaticData.localitySet = CommonStaticData.settings.getString(CommonStaticData.localityKey, "1/5/0");
        String[] splitLocality = (CommonStaticData.localitySet).split("/");
        if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("00")) {
            button_locality.setText(arr_locality_00[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("01")) {
            button_locality.setText(arr_locality_01[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("02")) {
            button_locality.setText(arr_locality_02[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("03")) {
            button_locality.setText(arr_locality_03[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("04")) {
            button_locality.setText(arr_locality_04[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("05")) {
            button_locality.setText(arr_locality_05[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("06")) {
            button_locality.setText(arr_locality_06[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("10")) {
            button_locality.setText(arr_locality_10[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("11")) {
            button_locality.setText(arr_locality_11[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("12")) {
            button_locality.setText(arr_locality_12[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("13")) {
            button_locality.setText(arr_locality_13[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("14")) {
            button_locality.setText(arr_locality_14[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("15")) {
            button_locality.setText(arr_locality_15[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("16")) {
            button_locality.setText(arr_locality_16[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("20")) {
            button_locality.setText(arr_locality_20[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("21")) {
            button_locality.setText(arr_locality_21[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("22")) {
            button_locality.setText(arr_locality_22[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("23")) {
            button_locality.setText(arr_locality_23[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("24")) {
            button_locality.setText(arr_locality_24[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("25")) {
            button_locality.setText(arr_locality_25[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("30")) {
            button_locality.setText(arr_locality_30[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("31")) {
            button_locality.setText(arr_locality_31[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("32")) {
            button_locality.setText(arr_locality_32[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("33")) {
            button_locality.setText(arr_locality_33[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("40")) {
            button_locality.setText(arr_locality_40[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("41")) {
            button_locality.setText(arr_locality_41[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("42")) {
            button_locality.setText(arr_locality_42[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("43")) {
            button_locality.setText(arr_locality_43[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("44")) {
            button_locality.setText(arr_locality_44[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("45")) {
            button_locality.setText(arr_locality_45[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("50")) {
            button_locality.setText(arr_locality_50[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("51")) {
            button_locality.setText(arr_locality_51[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("52")) {
            button_locality.setText(arr_locality_52[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("53")) {
            button_locality.setText(arr_locality_53[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("54")) {
            button_locality.setText(arr_locality_54[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("60")) {
            button_locality.setText(arr_locality_60[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("61")) {
            button_locality.setText(arr_locality_61[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("62")) {
            button_locality.setText(arr_locality_62[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("63")) {
            button_locality.setText(arr_locality_63[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("70")) {
            button_locality.setText(arr_locality_70[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("71")) {
            button_locality.setText(arr_locality_71[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("72")) {
            button_locality.setText(arr_locality_72[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("73")) {
            button_locality.setText(arr_locality_73[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("74")) {
            button_locality.setText(arr_locality_74[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("75")) {
            button_locality.setText(arr_locality_75[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("76")) {
            button_locality.setText(arr_locality_76[Integer.parseInt(splitLocality[2])]);
        } else if ((splitLocality[0]+splitLocality[1]).equalsIgnoreCase("77")) {
            button_locality.setText(arr_locality_77[Integer.parseInt(splitLocality[2])]);
        }

        CommonStaticData.audiomodeSet = CommonStaticData.settings.getInt(CommonStaticData.audiomodeSwitchKey,1);     // main =1, sub=2, main+sub(stereo)=0
        button_audio.setText(arr_audio[CommonStaticData.audiomodeSet]);
        /*
        CommonStaticData.receivemode=CommonStaticData.settings.getInt(CommonStaticData.receivemodeSwitchKey,0);     // 0=auto, 1=fullseg, 2=1seg
        button_svcmodeswitch.setText(arr_svcmodeswitch[CommonStaticData.receivemode]);
        */

        audioNum = FCI_TVi.GetAudioNum();
        TVlog.i(TAG, " >>>>> setAudiotrack cnt = " + audioNum);
        // max size 3 : eng, por, esp
        if (audioNum > MAX_AUDIO_TRACK_NUM) {
            audioNum = MAX_AUDIO_TRACK_NUM;
        }
        language = new CharSequence[audioNum];
        for (int j=0; j<audioNum; j++) {
            //language[j] = mContext.getResources().getString(R.string.language)+(j+1);
            language[j] = getResources().getString(R.string.language) + (j+1);
        }

        CommonStaticData.audiotrackSet= CommonStaticData.settings.getInt(CommonStaticData.audiotrackSwitchKey,0);
        if (audioNum > 1) {    // justin caption selection disable when no caption data
            textView_audiotrack.setTextColor(Color.WHITE);
            button_audiotrack.setEnabled(true);
            button_audiotrack.setText(arr_audiotrack[CommonStaticData.audiotrackSet]);
            button_audiotrack.setTextColor(getResources().getColorStateList(R.color.blue3));
            button_audiotrack.setTypeface(null, Typeface.BOLD);
            //button_audiotrack.setShadowLayer(10.0f, 0.0f, 0.0f, Color.WHITE);
        }
        else {
            textView_audiotrack.setTextColor(Color.GRAY);
            button_audiotrack.setEnabled(false);
            button_audiotrack.setText(arr_audiotrack[0]);
            button_audiotrack.setTextColor(Color.GRAY);
            button_audiotrack.setTypeface(null, Typeface.NORMAL);
            //button_audiotrack.setShadowLayer(10.0f, 0.0f, 0.0f, Color.TRANSPARENT);
        }

        //video track
        videoNum = FCI_TVi.GetVideoNum();
        if (videoNum > MAX_VIDEO_TRACK_NUM) {
            videoNum = MAX_VIDEO_TRACK_NUM;
        }
        selectableVideo = new CharSequence[videoNum];
        for (int j=0; j < videoNum; j++) {
            selectableVideo[j] = arr_videotrack[j];
        }
        CommonStaticData.videotrackSet= CommonStaticData.settings.getInt(CommonStaticData.videotrackSwitchKey, 0);
        if (videoNum > 1) {
            textView_videotrack.setTextColor(Color.WHITE);
            button_videotrack.setEnabled(true);
            button_videotrack.setText(arr_videotrack[CommonStaticData.videotrackSet]);
            button_videotrack.setTextColor(getResources().getColorStateList(R.color.blue3));
            button_videotrack.setTypeface(null, Typeface.BOLD);
            //button_videotrack.setShadowLayer(10.0f, 0.0f, 0.0f, Color.WHITE);
        } else {
            textView_videotrack.setTextColor(Color.GRAY);
            button_videotrack.setEnabled(false);
            button_videotrack.setText(arr_videotrack[0]);
            button_videotrack.setTextColor(Color.GRAY);
            button_videotrack.setTypeface(null, Typeface.NORMAL);
            //button_videotrack.setShadowLayer(10.0f, 0.0f, 0.0f, Color.TRANSPARENT);
        }

        CommonStaticData.sleeptime=CommonStaticData.settings.getInt(CommonStaticData.sleeptimerSwitchKey,0);
        button_sleep.setText(arr_sleep[CommonStaticData.sleeptime]);

        //ADD_GINGA_NCL[[
        if (buildOption.ADD_GINGA_NCL==true) {
            CommonStaticData.interactiveSwitch = CommonStaticData.settings.getBoolean(CommonStaticData.interactiveKey, true);
            if (CommonStaticData.interactiveSwitch == true) {
                switchInteractive.getThumbDrawable().setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
                switchInteractive.getTrackDrawable().setColorFilter(getResources().getColor(R.color.blue3), PorterDuff.Mode.MULTIPLY);
            }
            else {
                switchInteractive.getThumbDrawable().setColorFilter(getResources().getColor(R.color.light_gray), PorterDuff.Mode.MULTIPLY);
                switchInteractive.getTrackDrawable().setColorFilter(getResources().getColor(R.color.light_gray), PorterDuff.Mode.MULTIPLY);
            }
            switchInteractive.setChecked(CommonStaticData.interactiveSwitch);
        }
        //]]ADD_GINGA_NCL

        /*
        CommonStaticData.storage=CommonStaticData.settings.getInt(CommonStaticData.storageSwitchKey, 0);
        if (getMicroSDCardEnable()) {       // could external read/write from T
            button_storage.setText(arr_storage[CommonStaticData.storage]);
        } else {
            button_storage.setText(arr_storage[0]);     // phone 0
            button_storage.setEnabled(false);
        }*/

        CommonStaticData.ratingsetSwitch = CommonStaticData.settings.getBoolean(CommonStaticData.parentalcontrolSwitchKey, true);
        switchParental.setChecked(CommonStaticData.ratingsetSwitch);

        CommonStaticData.PG_Rate = CommonStaticData.settings.getInt(CommonStaticData.parentalRatingKey, 0);
        button_age.setText(arr_age[CommonStaticData.PG_Rate]);

        CommonStaticData.battMonitorSet = CommonStaticData.settings.getInt(CommonStaticData.definedbattMonitorKey,0);
        button_battery.setText(arr_battery[CommonStaticData.battMonitorSet]);

        // [[ solution switching mode 20170223

        CommonStaticData.localeSet = CommonStaticData.settings.getInt(CommonStaticData.localeSetKey, 4);
        CommonStaticData.solutionMode = CommonStaticData.settings.getInt(CommonStaticData.solutionModeKey, cReleaseOption.FCI_SOLUTION_MODE);

        // TVlog.e("justin ","onResume CommonStaticData.localeSet " + CommonStaticData.localeSet);
        //]]
        textView_locale.setText(arr_locale[CommonStaticData.localeSet]);
        imageView_flag.setImageResource(arr_localeflag[CommonStaticData.localeSet]);

        //eddy add password

        CommonStaticData.PassWord = CommonStaticData.settings.getString(CommonStaticData.passwordKey, null);
        super.onResume();
    }

    // justi add for exit save
    protected void onPause() {
        CommonStaticData.settingActivityShow = false;
        CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = CommonStaticData.settings.edit();
        editor.putBoolean(CommonStaticData.captionSwitchKey, CommonStaticData.captionSwitch);
        editor.putInt(CommonStaticData.scaleSwitchKey, CommonStaticData.scaleSet);
        editor.putInt(CommonStaticData.brightnessKey, CommonStaticData.brightness);
        editor.putInt(CommonStaticData.transparentKey, CommonStaticData.transparent);
        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB) {
            editor.putInt(CommonStaticData.receivemodeSwitchKey, CommonStaticData.receivemode);
            editor.putInt(CommonStaticData.autoSearchSwitchKey, CommonStaticData.autoSearch);
        }
        editor.putInt(CommonStaticData.audiomodeSwitchKey, CommonStaticData.audiomodeSet);
        editor.putInt(CommonStaticData.audiotrackSwitchKey, CommonStaticData.audiotrackSet);
        editor.putInt(CommonStaticData.videotrackSwitchKey, CommonStaticData.videotrackSet);
        editor.putInt(CommonStaticData.sleeptimerSwitchKey, CommonStaticData.sleeptime);
        if (buildOption.ADD_GINGA_NCL == true) {
            editor.putBoolean(CommonStaticData.interactiveKey, CommonStaticData.interactiveSwitch);
        }
        // editor.putInt(CommonStaticData.storageSwitchKey, CommonStaticData.storage);
        editor.putBoolean(CommonStaticData.parentalcontrolSwitchKey, CommonStaticData.ratingsetSwitch);
        editor.putInt(CommonStaticData.parentalRatingKey, CommonStaticData.PG_Rate);
        editor.putString(CommonStaticData.passwordKey, CommonStaticData.PassWord);
        editor.putInt(CommonStaticData.definedbattMonitorKey, CommonStaticData.battMonitorSet);
        editor.putInt(CommonStaticData.localeSetKey, CommonStaticData.localeSet);
        // [[ solution switching mode 20170223
        editor.putInt(CommonStaticData.solutionModeKey, CommonStaticData.solutionMode);     // TCL Brazil Philippine system switching 20161117
        //]]
        editor.putString(CommonStaticData.areaKey, CommonStaticData.areaSet);
        editor.putString(CommonStaticData.prefectureKey, CommonStaticData.prefectureSet);
        editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);

        editor.putBoolean(CommonStaticData.superimposeSwitchKey, CommonStaticData.superimposeSwitch);

        editor.commit();

        if (buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 )  {
            thumbNailUpdate.getThhumbNailUpdateTask().sendEvent(TVEVENT.E_UPDATE_THUMBNAIL, 0);
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        CommonStaticData.settingActivityShow = false;
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {

                TVlog.i(TAG, "onOptionsItemSelected  FLAG_ACTIVITY_CLEAR_TOP ");
                Intent homeIntent = new Intent(this, kr.co.fci.tv.MainActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Dialog onCreateDialog(int id) {

        switch (id) {
            case DIALOG_SET_CAPTION:
                MaterialDialog setCaptionDialog =  new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_caption)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(capLanguage)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(CommonStaticData.captionSelect /*0*/, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_caption_selected = which;
                                button_captionselect.setText(capLanguage[dialog_caption_selected]);
                                CommonStaticData.captionSelect = dialog_caption_selected;
                                FCI_TVi.SelectCaption(CommonStaticData.captionSelect);
                                try {
                                    removeDialog(DIALOG_SET_CAPTION);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                setCaptionDialog.getWindow().setGravity(Gravity.CENTER);
                setCaptionDialog.show();
                break;


            case DIALOG_SET_SUPERIMPOSE:
                MaterialDialog setSuperimposeDialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_superimpose)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(superimposeLanguage)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(CommonStaticData.superimposeSelect /*0*/, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_superimpose_selected = which;
                                button_superimposeselect.setText(superimposeLanguage[dialog_superimpose_selected]);
                                CommonStaticData.superimposeSelect = dialog_superimpose_selected;
                                FCI_TVi.SelectSuperimpose(CommonStaticData.superimposeSelect);
                                try {
                                    removeDialog(DIALOG_SET_SUPERIMPOSE);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                setSuperimposeDialog.getWindow().setGravity(Gravity.CENTER);
                setSuperimposeDialog.show();
                break;

            case DIALOG_SCREEN:
                MaterialDialog screenDialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.screen_ratio)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.screen_ratio)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(CommonStaticData.scaleSet, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_screen_selected = which;
                                button_screen.setText(arr_screen[dialog_screen_selected]);
                                CommonStaticData.scaleSet = dialog_screen_selected;    // justin db save
                                try {
                                    removeDialog(DIALOG_SCREEN);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                screenDialog.getWindow().setGravity(Gravity.CENTER);
                screenDialog.show();
                break;

            case DIALOG_AUTO_SEARCH_JP:
                MaterialDialog autoSearchDialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.set_auto_search)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.auto_search_jp)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(CommonStaticData.autoSearch, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_autoSearch_selected = which;
                                button_autoSearch.setText(arr_autosearch_jp[dialog_autoSearch_selected]);
                                CommonStaticData.autoSearch = dialog_autoSearch_selected;
                                //FCI_TVi.set(CommonStaticData.svcmodeSwitchSet);
                                /*if (dialog_autoSearch_selected == 0) { // Auto
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_AUTO_SEARCH, 0, 0, null);
                                } else if (dialog_autoSearch_selected == 1) {  // Off
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_AUTO_SEARCH, 1, 0, null);
                                }*/
                                try {
                                    removeDialog(DIALOG_AUTO_SEARCH_JP);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                autoSearchDialog.getWindow().setGravity(Gravity.CENTER);
                autoSearchDialog.show();
                break;

            case DIALOG_SVCMODE_SWITCH_JP:
                MaterialDialog svcmodeDialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.set_svcmode_switch)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.svcmode_switch_jp)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(CommonStaticData.receivemode, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                if (CommonStaticData.scanCHnum > 0) {
                                    int isChanged = 0;
                                    int[] info = FCI_TVi.GetPairNSegInfoOfCHIndex(CommonStaticData.lastCH);
                                    int isPaired = 0;
                                    int pairedIndex = info[0];
                                    int segInfo = info[1];
                                    int mainIndex = info[3];
                                    int oneSegIndex = info[4];
                                    if (pairedIndex >= 0) {
                                        isPaired = 1;
                                    }
                                    CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                    dialog_svcmodeswitch_selected = which;
                                    button_svcmodeswitch.setText(arr_svcmodeswitch_jp[dialog_svcmodeswitch_selected]);
                                    if (CommonStaticData.receivemode != dialog_svcmodeswitch_selected) {
                                        isChanged = 1;
                                    }

                                    if (dialog_svcmodeswitch_selected == 1) { // to fullseg
                                        if (isChanged == 1) {
                                            if (mainIndex != -1) {
                                                CommonStaticData.receivemode = CommonStaticData.RECEIVE_MODE_FULLSEG;
                                                MainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_SWITCHING, 1, 0, null);
                                                button_svcmodeswitch.setText(arr_svcmodeswitch_jp[1]);
                                                editor.putInt(CommonStaticData.receivemodeSwitchKey, CommonStaticData.receivemode);
                                                editor.commit();
                                            } else {
                                                TVlog.i("live", " >>>> CommonStaticData.receivemode = "+CommonStaticData.receivemode);
                                                button_svcmodeswitch.setText(arr_svcmodeswitch_jp[CommonStaticData.receivemode]);
                                                CustomToast toast = new CustomToast(getApplicationContext());
                                                toast.showToast(getApplicationContext(), getApplicationContext().getString(R.string.switch_fail_HD), Toast.LENGTH_SHORT);
                                            }
                                        }
                                    } else if (dialog_svcmodeswitch_selected == 0) {  // to 1seg
                                        if (isChanged == 1) {
                                            if (oneSegIndex != -1) {
                                                CommonStaticData.receivemode = CommonStaticData.RECEIVE_MODE_1SEG;
                                                MainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_SWITCHING, 0, 0, null);
                                                button_svcmodeswitch.setText(arr_svcmodeswitch_jp[0]);
                                                editor.putInt(CommonStaticData.receivemodeSwitchKey, CommonStaticData.receivemode);
                                                editor.commit();
                                            } else {
                                                TVlog.i("live", " >>>> CommonStaticData.receivemode = "+CommonStaticData.receivemode);
                                                button_svcmodeswitch.setText(arr_svcmodeswitch_jp[CommonStaticData.receivemode]);
                                                CustomToast toast = new CustomToast(getApplicationContext());
                                                toast.showToast(getApplicationContext(), getApplicationContext().getString(R.string.switch_fail_SD), Toast.LENGTH_SHORT);
                                            }
                                        }
                                    }
                                    else if (dialog_svcmodeswitch_selected == 2) { //auto
                                        if (isChanged == 1) {
                                            CommonStaticData.receivemode = CommonStaticData.RECEIVE_MODE_AUTO;
                                            button_svcmodeswitch.setText(arr_svcmodeswitch_jp[2]);
                                            editor.putInt(CommonStaticData.receivemodeSwitchKey, CommonStaticData.receivemode);
                                            editor.commit();
                                        }
                                    }
                                    else if (dialog_svcmodeswitch_selected == 3) { // to off
                                        if (isChanged == 1) {
                                            CommonStaticData.receivemode = CommonStaticData.RECEIVE_MODE_OFF;
                                            button_svcmodeswitch.setText(arr_svcmodeswitch_jp[3]);
                                            editor.putInt(CommonStaticData.receivemodeSwitchKey, CommonStaticData.receivemode);
                                            editor.commit();
                                        }
                                    }
                                    try {
                                        removeDialog(DIALOG_SVCMODE_SWITCH_JP);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    CustomToast toast = new CustomToast(getApplicationContext());
                                    toast.showToast(getApplicationContext(), getApplicationContext().getString(R.string.no_channel_tip), Toast.LENGTH_SHORT);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                svcmodeDialog.getWindow().setGravity(Gravity.CENTER);
                svcmodeDialog.show();
                break;

            case DIALOG_AREA:
                MaterialDialog areaDialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_area)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.area)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_area_selected = which;
                                button_area.setText(arr_area[dialog_area_selected]);
                                CommonStaticData.areaSet = String.valueOf(dialog_area_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.areaSet = " + CommonStaticData.areaSet);
                                if (dialog_area_selected == 0) {
                                    try {
                                        removeDialog(DIALOG_AREA);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_PREFECTURE_0);
                                } else if (dialog_area_selected == 1) {
                                    try {
                                        removeDialog(DIALOG_AREA);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_PREFECTURE_1);
                                } else if (dialog_area_selected == 2) {
                                    try {
                                        removeDialog(DIALOG_AREA);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_PREFECTURE_2);
                                } else if (dialog_area_selected == 3) {
                                    try {
                                        removeDialog(DIALOG_AREA);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_PREFECTURE_3);
                                } else if (dialog_area_selected == 4) {
                                    try {
                                        removeDialog(DIALOG_AREA);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_PREFECTURE_4);
                                } else if (dialog_area_selected == 5) {
                                    try {
                                        removeDialog(DIALOG_AREA);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_PREFECTURE_5);
                                } else if (dialog_area_selected == 6) {
                                    try {
                                        removeDialog(DIALOG_AREA);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_PREFECTURE_6);
                                } else if (dialog_area_selected == 7) {
                                    try {
                                        removeDialog(DIALOG_AREA);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_PREFECTURE_7);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                areaDialog.getWindow().setGravity(Gravity.CENTER);
                areaDialog.show();
                break;

            case DIALOG_PREFECTURE_0:
                MaterialDialog prefecture0Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_prefecture)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.prefecture_0)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_prefecture0_selected = which;
                                button_prefecture.setText(arr_prefecture_0[dialog_prefecture0_selected]);
                                CommonStaticData.prefectureSet = "0" + "/" + String.valueOf(dialog_prefecture0_selected);    // justin db save
                                if (dialog_prefecture0_selected == 0) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_0);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_00);
                                } else if (dialog_prefecture0_selected == 1) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_0);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_01);
                                } else if (dialog_prefecture0_selected == 2) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_0);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_02);
                                } else if (dialog_prefecture0_selected == 3) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_0);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_03);
                                } else if (dialog_prefecture0_selected == 4) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_0);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_04);
                                } else if (dialog_prefecture0_selected == 5) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_0);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_05);
                                } else if (dialog_prefecture0_selected == 6) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_0);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_06);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                prefecture0Dialog.getWindow().setGravity(Gravity.CENTER);
                prefecture0Dialog.show();
                break;

            case DIALOG_PREFECTURE_1:
                MaterialDialog prefecture1Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_prefecture)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.prefecture_1)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_prefecture1_selected = which;
                                button_prefecture.setText(arr_prefecture_1[dialog_prefecture1_selected]);
                                CommonStaticData.prefectureSet = "1" + "/" + String.valueOf(dialog_prefecture1_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.prefectureSet = " + CommonStaticData.prefectureSet);
                                if (dialog_prefecture1_selected == 0) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_1);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_10);
                                } else if (dialog_prefecture1_selected == 1) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_1);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_11);
                                } else if (dialog_prefecture1_selected == 2) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_1);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_12);
                                } else if (dialog_prefecture1_selected == 3) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_1);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_13);
                                } else if (dialog_prefecture1_selected == 4) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_1);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_14);
                                } else if (dialog_prefecture1_selected == 5) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_1);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_15);
                                } else if (dialog_prefecture1_selected == 6) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_1);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_16);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                prefecture1Dialog.getWindow().setGravity(Gravity.CENTER);
                prefecture1Dialog.show();
                break;

            case DIALOG_PREFECTURE_2:
                MaterialDialog prefecture2Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_prefecture)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.prefecture_2)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_prefecture2_selected = which;
                                button_prefecture.setText(arr_prefecture_2[dialog_prefecture2_selected]);
                                CommonStaticData.prefectureSet = "2" + "/" + String.valueOf(dialog_prefecture2_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.prefectureSet = " + CommonStaticData.prefectureSet);
                                if (dialog_prefecture2_selected == 0) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_2);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_20);
                                } else if (dialog_prefecture2_selected == 1) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_2);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_21);
                                } else if (dialog_prefecture2_selected == 2) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_2);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_22);
                                } else if (dialog_prefecture2_selected == 3) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_2);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_23);
                                } else if (dialog_prefecture2_selected == 4) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_2);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_24);
                                } else if (dialog_prefecture2_selected == 5) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_2);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_25);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                prefecture2Dialog.getWindow().setGravity(Gravity.CENTER);
                prefecture2Dialog.show();
                break;

            case DIALOG_PREFECTURE_3:
                MaterialDialog prefecture3Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_prefecture)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.prefecture_3)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_prefecture3_selected = which;
                                button_prefecture.setText(arr_prefecture_3[dialog_prefecture3_selected]);
                                CommonStaticData.prefectureSet = "3" + "/" + String.valueOf(dialog_prefecture3_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.prefectureSet = " + CommonStaticData.prefectureSet);
                                if (dialog_prefecture3_selected == 0) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_3);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_30);
                                } else if (dialog_prefecture3_selected == 1) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_3);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_31);
                                } else if (dialog_prefecture3_selected == 2) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_3);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_32);
                                } else if (dialog_prefecture3_selected == 3) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_3);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_33);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                prefecture3Dialog.getWindow().setGravity(Gravity.CENTER);
                prefecture3Dialog.show();
                break;

            case DIALOG_PREFECTURE_4:
                MaterialDialog prefecture4Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_prefecture)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.prefecture_4)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_prefecture4_selected = which;
                                button_prefecture.setText(arr_prefecture_4[dialog_prefecture4_selected]);
                                CommonStaticData.prefectureSet = "4" + "/" + String.valueOf(dialog_prefecture4_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.prefectureSet = " + CommonStaticData.prefectureSet);
                                if (dialog_prefecture4_selected == 0) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_4);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_40);
                                } else if (dialog_prefecture4_selected == 1) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_4);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_41);
                                } else if (dialog_prefecture4_selected == 2) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_4);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_42);
                                } else if (dialog_prefecture4_selected == 3) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_4);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_43);
                                } else if (dialog_prefecture4_selected == 4) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_4);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_44);
                                } else if (dialog_prefecture4_selected == 5) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_4);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_45);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                prefecture4Dialog.getWindow().setGravity(Gravity.CENTER);
                prefecture4Dialog.show();
                break;

            case DIALOG_PREFECTURE_5:
                MaterialDialog prefecture5Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_prefecture)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.prefecture_5)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_prefecture5_selected = which;
                                button_prefecture.setText(arr_prefecture_5[dialog_prefecture5_selected]);
                                CommonStaticData.prefectureSet = "5" + "/" + String.valueOf(dialog_prefecture5_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.prefectureSet = " + CommonStaticData.prefectureSet);
                                if (dialog_prefecture5_selected == 0) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_5);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_50);
                                } else if (dialog_prefecture5_selected == 1) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_5);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_51);
                                } else if (dialog_prefecture5_selected == 2) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_5);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_52);
                                } else if (dialog_prefecture5_selected == 3) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_5);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_53);
                                } else if (dialog_prefecture5_selected == 4) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_5);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_54);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                prefecture5Dialog.getWindow().setGravity(Gravity.CENTER);
                prefecture5Dialog.show();
                break;

            case DIALOG_PREFECTURE_6:
                MaterialDialog prefecture6Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_prefecture)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.prefecture_6)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_prefecture6_selected = which;
                                button_prefecture.setText(arr_prefecture_6[dialog_prefecture6_selected]);
                                CommonStaticData.prefectureSet = "6" + "/" + String.valueOf(dialog_prefecture6_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.prefectureSet = " + CommonStaticData.prefectureSet);
                                if (dialog_prefecture6_selected == 0) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_6);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_60);
                                } else if (dialog_prefecture6_selected == 1) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_6);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_61);
                                } else if (dialog_prefecture6_selected == 2) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_6);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_62);
                                } else if (dialog_prefecture6_selected == 3) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_6);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_63);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                prefecture6Dialog.getWindow().setGravity(Gravity.CENTER);
                prefecture6Dialog.show();
                break;

            case DIALOG_PREFECTURE_7:
                MaterialDialog prefecture7Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_prefecture)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.prefecture_7)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_prefecture7_selected = which;
                                button_prefecture.setText(arr_prefecture_7[dialog_prefecture7_selected]);
                                CommonStaticData.prefectureSet = "7" + "/" + String.valueOf(dialog_prefecture7_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.prefectureSet = " + CommonStaticData.prefectureSet);
                                if (dialog_prefecture7_selected == 0) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_7);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_70);
                                } else if (dialog_prefecture7_selected == 1) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_7);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_71);
                                } else if (dialog_prefecture7_selected == 2) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_7);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_72);
                                } else if (dialog_prefecture7_selected == 3) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_7);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_73);
                                } else if (dialog_prefecture7_selected == 4) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_7);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_74);
                                } else if (dialog_prefecture7_selected == 5) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_7);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_75);
                                } else if (dialog_prefecture7_selected == 6) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_7);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_76);
                                } else if (dialog_prefecture7_selected == 7) {
                                    try {
                                        removeDialog(DIALOG_PREFECTURE_7);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                    showDialog(DIALOG_LOCALITY_77);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                prefecture7Dialog.getWindow().setGravity(Gravity.CENTER);
                prefecture7Dialog.show();
                break;

            case DIALOG_LOCALITY_00:
                MaterialDialog locality00Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_00)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality00_selected = which;
                                button_locality.setText(arr_locality_00[dialog_locality00_selected]);
                                CommonStaticData.localitySet = "0/0" + "/" + String.valueOf(dialog_locality00_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_00);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                CommonStaticData.settingActivityShow = false;
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality00_selected == 0) {
                                    int[] regionCh = {13, 14, 15, 19, 21, 23, 25, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality00_selected == 1) {
                                    int[] regionCh = {14, 15, 17, 18, 19, 23, 25, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality00_selected == 2) {
                                    int[] regionCh = {13, 14, 15, 19, 21, 23, 25, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality00_selected == 3) {
                                    int[] regionCh = {16, 20, 22, 24, 26, 31, 33, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality00_selected == 4) {
                                    int[] regionCh = {29, 31, 33, 36, 41, 43, 45, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality00_selected == 5) {
                                    int[] regionCh = {13, 15, 17, 19, 21, 23, 25, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality00_selected == 6) {
                                    int[] regionCh = {13, 14, 16, 18, 20, 22, 24, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality00Dialog.getWindow().setGravity(Gravity.CENTER);
                locality00Dialog.show();
                break;

            case DIALOG_LOCALITY_01:
                MaterialDialog locality01Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_01)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality01_selected = which;
                                button_locality.setText(arr_locality_01[dialog_locality01_selected]);
                                CommonStaticData.localitySet = "0/1" + "/" + String.valueOf(dialog_locality01_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_01);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality01_selected == 0) {
                                    int[] regionCh = {13, 16, 28, 30, 32, 0, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality01_selected == 1) {
                                    int[] regionCh = {14, 18, 20, 22, 24, 0, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality01Dialog.getWindow().setGravity(Gravity.CENTER);
                locality01Dialog.show();
                break;

            case DIALOG_LOCALITY_02:
                MaterialDialog locality02Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_02)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality02_selected = which;
                                button_locality.setText(arr_locality_02[dialog_locality02_selected]);
                                CommonStaticData.localitySet = "0/2" + "/" + String.valueOf(dialog_locality02_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_02);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality02_selected == 0) {
                                    int[] regionCh = {13, 14, 16, 18, 20, 22, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality02_selected == 1) {
                                    int[] regionCh = {15, 17, 19, 21, 24, 30, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality02_selected == 2) {
                                    int[] regionCh = {15, 23, 27, 29, 37, 43, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality02_selected == 3) {
                                    int[] regionCh = {15, 17, 19, 21, 23, 50, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality02_selected == 4) {
                                    int[] regionCh = {13, 14, 15, 16, 17, 18, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality02_selected == 5) {
                                    int[] regionCh = {13, 14, 16, 18, 20, 22, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality02Dialog.getWindow().setGravity(Gravity.CENTER);
                locality02Dialog.show();
                break;

            case DIALOG_LOCALITY_03:
                MaterialDialog locality03Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_03)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality03_selected = which;
                                button_locality.setText(arr_locality_03[dialog_locality03_selected]);
                                CommonStaticData.localitySet = "0/3" + "/" + String.valueOf(dialog_locality03_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_03);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality03_selected == 0) {
                                    int[] regionCh = {13, 17, 19, 21, 24, 28, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality03_selected == 1) {
                                    int[] regionCh = {14, 17, 19, 21, 24, 28, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality03_selected == 2) {
                                    int[] regionCh = {13, 15, 23, 25, 27, 30, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality03_selected == 3) {
                                    int[] regionCh = {16, 18, 20, 22, 26, 30, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality03Dialog.getWindow().setGravity(Gravity.CENTER);
                locality03Dialog.show();
                break;

            case DIALOG_LOCALITY_04:
                MaterialDialog locality04Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_04)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality04_selected = which;
                                button_locality.setText(arr_locality_04[dialog_locality04_selected]);
                                CommonStaticData.localitySet = "0/4" + "/" + String.valueOf(dialog_locality04_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_04);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality04_selected == 0) {
                                    int[] regionCh = {21, 29, 35, 48, 50, 0, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality04_selected == 1) {
                                    int[] regionCh = {19, 23, 25, 26, 33, 0, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality04Dialog.getWindow().setGravity(Gravity.CENTER);
                locality04Dialog.show();
                break;

            case DIALOG_LOCALITY_05:
                MaterialDialog locality05Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_05)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality05_selected = which;
                                button_locality.setText(arr_locality_05[dialog_locality05_selected]);
                                CommonStaticData.localitySet = "0/5" + "/" + String.valueOf(dialog_locality05_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_05);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality05_selected == 0) {
                                    int[] regionCh = {13, 14, 16, 18, 20, 22, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality05_selected == 1) {
                                    int[] regionCh = {24, 28, 32, 34, 37, 40, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality05_selected == 2) {
                                    int[] regionCh = {16, 18, 20, 27, 32, 34, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality05_selected == 3) {
                                    int[] regionCh = {15, 17, 19, 21, 23, 33, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality05Dialog.getWindow().setGravity(Gravity.CENTER);
                locality05Dialog.show();
                break;

            case DIALOG_LOCALITY_06:
                MaterialDialog locality06Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_06)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality06_selected = which;
                                button_locality.setText(arr_locality_06[dialog_locality06_selected]);
                                CommonStaticData.localitySet = "0/6" + "/" + String.valueOf(dialog_locality06_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_06);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality06_selected == 0) {
                                    int[] regionCh = {14, 15, 25, 26, 27, 29, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality06_selected == 1) {
                                    int[] regionCh = {14, 16, 18, 20, 22, 30, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality06_selected == 2) {
                                    int[] regionCh = {13, 16, 17, 21, 26, 28, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality06Dialog.getWindow().setGravity(Gravity.CENTER);
                locality06Dialog.show();
                break;

            case DIALOG_LOCALITY_10:
                MaterialDialog locality10Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_10)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality10_selected = which;
                                button_locality.setText(arr_locality_10[dialog_locality10_selected]);
                                CommonStaticData.localitySet = "1/0" + "/" + String.valueOf(dialog_locality10_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_10);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality10_selected == 0) {
                                    int[] regionCh = {13, 14, 15, 17, 18, 19, 20, 28, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality10Dialog.getWindow().setGravity(Gravity.CENTER);
                locality10Dialog.show();
                break;

            case DIALOG_LOCALITY_11:
                MaterialDialog locality11Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_11)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality11_selected = which;
                                button_locality.setText(arr_locality_11[dialog_locality11_selected]);
                                CommonStaticData.localitySet = "1/1" + "/" + String.valueOf(dialog_locality11_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_11);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality11_selected == 0) {
                                    int[] regionCh = {15, 17, 18, 28, 29, 34, 35, 39, 47, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality11Dialog.getWindow().setGravity(Gravity.CENTER);
                locality11Dialog.show();
                break;

            case DIALOG_LOCALITY_12:
                MaterialDialog locality12Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_12)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality12_selected = which;
                                button_locality.setText(arr_locality_12[dialog_locality12_selected]);
                                CommonStaticData.localitySet = "1/2" + "/" + String.valueOf(dialog_locality12_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_12);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality12_selected == 0) {
                                    int[] regionCh = {19, 28, 33, 36, 37, 39, 42, 43, 45, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality12Dialog.getWindow().setGravity(Gravity.CENTER);
                locality12Dialog.show();
                break;

            case DIALOG_LOCALITY_13:
                MaterialDialog locality13Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_13)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality13_selected = which;
                                button_locality.setText(arr_locality_13[dialog_locality13_selected]);
                                CommonStaticData.localitySet = "1/3" + "/" + String.valueOf(dialog_locality13_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_13);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality13_selected == 0) {
                                    int[] regionCh = {21, 22, 23, 24, 25, 26, 27, 28, 32, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality13Dialog.getWindow().setGravity(Gravity.CENTER);
                locality13Dialog.show();
                break;

            case DIALOG_LOCALITY_14:
                MaterialDialog locality14Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_14)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality14_selected = which;
                                button_locality.setText(arr_locality_14[dialog_locality14_selected]);
                                CommonStaticData.localitySet = "1/4" + "/" + String.valueOf(dialog_locality14_selected);    // justin db save
                                try {
                                    removeDialog(DIALOG_LOCALITY_14);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality14_selected == 0) {
                                    int[] regionCh = {21, 22, 23, 24, 25, 26, 27, 28, 30, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality14Dialog.getWindow().setGravity(Gravity.CENTER);
                locality14Dialog.show();
                break;

            case DIALOG_LOCALITY_15:
                MaterialDialog locality15Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_15)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality15_selected = which;
                                button_locality.setText(arr_locality_15[dialog_locality15_selected]);
                                CommonStaticData.localitySet = "1/5" + "/" + String.valueOf(dialog_locality15_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_15);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality15_selected == 0) {
                                    int[] regionCh = {16, 21, 22, 23, 24, 25, 26, 27, 28, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality15Dialog.getWindow().setGravity(Gravity.CENTER);
                locality15Dialog.show();
                break;

            case DIALOG_LOCALITY_16:
                MaterialDialog locality16Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_16)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality16_selected = which;
                                button_locality.setText(arr_locality_16[dialog_locality16_selected]);
                                CommonStaticData.localitySet = "1/6" + "/" + String.valueOf(dialog_locality16_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_16);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality16_selected == 0) {
                                    int[] regionCh = {18, 21, 22, 23, 24, 25, 26, 27, 28, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality16_selected == 1) {
                                    int[] regionCh = {18, 19, 21, 22, 23, 24, 25, 26, 28, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality16Dialog.getWindow().setGravity(Gravity.CENTER);
                locality16Dialog.show();
                break;

            case DIALOG_LOCALITY_20:
                MaterialDialog locality20Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_20)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality20_selected = which;
                                button_locality.setText(arr_locality_20[dialog_locality20_selected]);
                                CommonStaticData.localitySet = "2/0" + "/" + String.valueOf(dialog_locality20_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_20);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality20_selected == 0) {
                                    int[] regionCh = {13, 15, 17, 19, 23, 26, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality20Dialog.getWindow().setGravity(Gravity.CENTER);
                locality20Dialog.show();
                break;

            case DIALOG_LOCALITY_21:
                MaterialDialog locality21Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_21)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality21_selected = which;
                                button_locality.setText(arr_locality_21[dialog_locality21_selected]);
                                CommonStaticData.localitySet = "2/1" + "/" + String.valueOf(dialog_locality21_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_21);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality21_selected == 0) {
                                    int[] regionCh = {18, 22, 24, 27, 28, 0, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality21Dialog.getWindow().setGravity(Gravity.CENTER);
                locality21Dialog.show();
                break;

            case DIALOG_LOCALITY_22:
                MaterialDialog locality22Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_22)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality22_selected = which;
                                button_locality.setText(arr_locality_22[dialog_locality22_selected]);
                                CommonStaticData.localitySet = "2/2" + "/" + String.valueOf(dialog_locality22_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_22);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality22_selected == 0) {
                                    int[] regionCh = {13, 14, 15, 16, 17, 23, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality22_selected == 1) {
                                    int[] regionCh = {20, 30, 39, 42, 44, 52, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality22_selected == 2) {
                                    int[] regionCh = {14, 16, 25, 31, 33, 37, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality22Dialog.getWindow().setGravity(Gravity.CENTER);
                locality22Dialog.show();
                break;

            case DIALOG_LOCALITY_23:
                MaterialDialog locality23Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_23)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality23_selected = which;
                                button_locality.setText(arr_locality_23[dialog_locality23_selected]);
                                CommonStaticData.localitySet = "2/3" + "/" + String.valueOf(dialog_locality23_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_23);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality23_selected == 0) {
                                    int[] regionCh = {19, 20, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality23Dialog.getWindow().setGravity(Gravity.CENTER);
                locality23Dialog.show();
                break;

            case DIALOG_LOCALITY_24:
                MaterialDialog locality24Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_24)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality24_selected = which;
                                button_locality.setText(arr_locality_24[dialog_locality24_selected]);
                                CommonStaticData.localitySet = "2/4" + "/" + String.valueOf(dialog_locality24_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_24);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality24_selected == 0) {
                                    int[] regionCh = {21, 23, 25, 27, 0, 0, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality24Dialog.getWindow().setGravity(Gravity.CENTER);
                locality24Dialog.show();
                break;

            case DIALOG_LOCALITY_25:
                MaterialDialog locality25Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_25)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality25_selected = which;
                                button_locality.setText(arr_locality_25[dialog_locality25_selected]);
                                CommonStaticData.localitySet = "2/5" + "/" + String.valueOf(dialog_locality24_selected);    // justin db save
                                try {
                                    removeDialog(DIALOG_LOCALITY_25);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality25_selected == 0) {
                                    int[] regionCh = {13, 14, 15, 16, 17, 18, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality25_selected == 1) {
                                    int[] regionCh = {33, 35, 36, 46, 48, 49, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality25_selected == 2) {
                                    int[] regionCh = {38, 41, 47, 49, 51, 53, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality25Dialog.getWindow().setGravity(Gravity.CENTER);
                locality25Dialog.show();
                break;

            case DIALOG_LOCALITY_30:
                MaterialDialog locality30Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_30)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality30_selected = which;
                                button_locality.setText(arr_locality_30[dialog_locality30_selected]);
                                CommonStaticData.localitySet = "3/0" + "/" + String.valueOf(dialog_locality30_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_30);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality30_selected == 0) {
                                    int[] regionCh = {13, 18, 19, 21, 22, 29, 30, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality30_selected == 1) {
                                    int[] regionCh = {14, 15, 16, 17, 29, 30, 31, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality30_selected == 2) {
                                    int[] regionCh = {14, 15, 16, 17, 24, 31, 32, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality30Dialog.getWindow().setGravity(Gravity.CENTER);
                locality30Dialog.show();
                break;

            case DIALOG_LOCALITY_31:
                MaterialDialog locality31Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_31)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality31_selected = which;
                                button_locality.setText(arr_locality_31[dialog_locality31_selected]);
                                CommonStaticData.localitySet = "3/1" + "/" + String.valueOf(dialog_locality31_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_31);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality31_selected == 0) {
                                    int[] regionCh = {13, 15, 17, 18, 19, 20, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality31_selected == 1) {
                                    int[] regionCh = {13, 20, 21, 22, 23, 25, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality31Dialog.getWindow().setGravity(Gravity.CENTER);
                locality31Dialog.show();
                break;

            case DIALOG_LOCALITY_32:
                MaterialDialog locality32Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_32)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality32_selected = which;
                                button_locality.setText(arr_locality_32[dialog_locality32_selected]);
                                CommonStaticData.localitySet = "3/2" + "/" + String.valueOf(dialog_locality32_selected);    // justin db save
                                try {
                                    removeDialog(DIALOG_LOCALITY_32);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality32_selected == 0) {
                                    int[] regionCh = {13, 18, 19, 20, 21, 22, 23, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality32Dialog.getWindow().setGravity(Gravity.CENTER);
                locality32Dialog.show();
                break;

            case DIALOG_LOCALITY_33:
                MaterialDialog locality33Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_33)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality33_selected = which;
                                button_locality.setText(arr_locality_33[dialog_locality33_selected]);
                                CommonStaticData.localitySet = "3/3" + "/" + String.valueOf(dialog_locality33_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_33);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality33_selected == 0) {
                                    int[] regionCh = {18, 19, 21, 22, 27, 28, 44, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality33_selected == 1) {
                                    int[] regionCh = {13, 14, 15, 16, 17, 24, 29, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality33Dialog.getWindow().setGravity(Gravity.CENTER);
                locality33Dialog.show();
                break;

            case DIALOG_LOCALITY_40:
                MaterialDialog locality40Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_40)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality40_selected = which;
                                button_locality.setText(arr_locality_40[dialog_locality40_selected]);
                                CommonStaticData.localitySet = "4/0" + "/" + String.valueOf(dialog_locality40_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_40);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality40_selected == 0) {
                                    int[] regionCh = {13, 15, 16, 17, 20, 26, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality40_selected == 1) {
                                    int[] regionCh = {15, 16, 17, 26, 29, 31, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality40Dialog.getWindow().setGravity(Gravity.CENTER);
                locality40Dialog.show();
                break;

            case DIALOG_LOCALITY_41:
                MaterialDialog locality41Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_41)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality41_selected = which;
                                button_locality.setText(arr_locality_41[dialog_locality41_selected]);
                                CommonStaticData.localitySet = "4/1" + "/" + String.valueOf(dialog_locality41_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_41);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality41_selected == 0) {
                                    int[] regionCh = {13, 14, 15, 16, 17, 23, 25, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality41Dialog.getWindow().setGravity(Gravity.CENTER);
                locality41Dialog.show();
                break;

            case DIALOG_LOCALITY_42:
                MaterialDialog locality42Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_42)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality42_selected = which;
                                button_locality.setText(arr_locality_42[dialog_locality42_selected]);
                                CommonStaticData.localitySet = "4/2" + "/" + String.valueOf(dialog_locality42_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_42);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality42_selected == 0) {
                                    int[] regionCh = {13, 14, 15, 16, 17, 18, 24, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality42Dialog.getWindow().setGravity(Gravity.CENTER);
                locality42Dialog.show();
                break;

            case DIALOG_LOCALITY_43:
                MaterialDialog locality43Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_43)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality43_selected = which;
                                button_locality.setText(arr_locality_43[dialog_locality43_selected]);
                                CommonStaticData.localitySet = "4/3" + "/" + String.valueOf(dialog_locality43_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_43);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality43_selected == 0) {
                                    int[] regionCh = {13, 14, 15, 16, 17, 22, 26, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                    .build();
                locality43Dialog.getWindow().setGravity(Gravity.CENTER);
                locality43Dialog.show();
                break;

            case DIALOG_LOCALITY_44:
                MaterialDialog locality44Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_44)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality44_selected = which;
                                button_locality.setText(arr_locality_44[dialog_locality44_selected]);
                                CommonStaticData.localitySet = "4/4" + "/" + String.valueOf(dialog_locality44_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_44);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality44_selected == 0) {
                                    int[] regionCh = {13, 14, 15, 16, 17, 29, 31, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality44Dialog.getWindow().setGravity(Gravity.CENTER);
                locality44Dialog.show();
                break;

            case DIALOG_LOCALITY_45:
                MaterialDialog locality45Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_45)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality45_selected = which;
                                button_locality.setText(arr_locality_45[dialog_locality45_selected]);
                                CommonStaticData.localitySet = "4/5" + "/" + String.valueOf(dialog_locality45_selected);    // justin db save
                                try {
                                    removeDialog(DIALOG_LOCALITY_45);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality45_selected == 0) {
                                    int[] regionCh = {13, 14, 15, 16, 17, 20, 23, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality45Dialog.getWindow().setGravity(Gravity.CENTER);
                locality45Dialog.show();
                break;

            case DIALOG_LOCALITY_50:
                MaterialDialog locality50Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_50)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality50_selected = which;
                                button_locality.setText(arr_locality_50[dialog_locality50_selected]);
                                CommonStaticData.localitySet = "5/0" + "/" + String.valueOf(dialog_locality50_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_50);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality50_selected == 0) {
                                    int[] regionCh = {20, 29, 31, 36, 38, 0, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality50_selected == 1) {
                                    int[] regionCh = {20, 26, 41, 43, 45, 0, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality50_selected == 2) {
                                    int[] regionCh = {27, 29, 31, 36, 38, 0, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality50Dialog.getWindow().setGravity(Gravity.CENTER);
                locality50Dialog.show();
                break;

            case DIALOG_LOCALITY_51:
                MaterialDialog locality51Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_51)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality51_selected = which;
                                button_locality.setText(arr_locality_51[dialog_locality51_selected]);
                                CommonStaticData.localitySet = "5/1" + "/" + String.valueOf(dialog_locality51_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_51);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality51_selected == 0) {
                                    int[] regionCh = {19, 21, 41, 43, 45, 0, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality51_selected == 1) {
                                    int[] regionCh = {22, 23, 31, 35, 37, 0, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality51_selected == 2) {
                                    int[] regionCh = {20, 21, 33, 38, 44, 0, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality51Dialog.getWindow().setGravity(Gravity.CENTER);
                locality51Dialog.show();
                break;

            case DIALOG_LOCALITY_52:
                MaterialDialog locality52Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_52)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality52_selected = which;
                                button_locality.setText(arr_locality_52[dialog_locality52_selected]);
                                CommonStaticData.localitySet = "5/2" + "/" + String.valueOf(dialog_locality52_selected);    // justin db save
                                try {
                                    removeDialog(DIALOG_LOCALITY_52);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality52_selected == 0) {
                                    int[] regionCh = {18, 20, 21, 27, 30, 32, 45, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality52_selected == 1) {
                                    int[] regionCh = {13, 14, 15, 16, 17, 19, 22, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality52Dialog.getWindow().setGravity(Gravity.CENTER);
                locality52Dialog.show();
                break;

            case DIALOG_LOCALITY_53:
                MaterialDialog locality53Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_53)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality53_selected = which;
                                button_locality.setText(arr_locality_53[dialog_locality53_selected]);
                                CommonStaticData.localitySet = "5/3" + "/" + String.valueOf(dialog_locality53_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_53);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality53_selected == 0) {
                                    int[] regionCh = {14, 15, 18, 19, 22, 23, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality53_selected == 1) {
                                    int[] regionCh = {16, 17, 28, 29, 42, 44, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality53Dialog.getWindow().setGravity(Gravity.CENTER);
                locality53Dialog.show();
                break;

            case DIALOG_LOCALITY_54:
                MaterialDialog locality54Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_54)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality54_selected = which;
                                button_locality.setText(arr_locality_54[dialog_locality54_selected]);
                                CommonStaticData.localitySet = "5/4" + "/" + String.valueOf(dialog_locality54_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_54);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality54_selected == 0) {
                                    int[] regionCh = {13, 16, 18, 20, 26, 0, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality54_selected == 1) {
                                    int[] regionCh = {13, 16, 18, 20, 26, 0, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality54_selected == 2) {
                                    int[] regionCh = {38, 39, 40, 41, 42, 0, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality54Dialog.getWindow().setGravity(Gravity.CENTER);
                locality54Dialog.show();
                break;

            case DIALOG_LOCALITY_60:
                MaterialDialog locality60Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_60)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality60_selected = which;
                                button_locality.setText(arr_locality_60[dialog_locality60_selected]);
                                CommonStaticData.localitySet = "6/0" + "/" + String.valueOf(dialog_locality60_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_60);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality60_selected == 0) {
                                    int[] regionCh = {31, 34, 40, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality60Dialog.getWindow().setGravity(Gravity.CENTER);
                locality60Dialog.show();
                break;

            case DIALOG_LOCALITY_61:
                MaterialDialog locality61Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_61)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality61_selected = which;
                                button_locality.setText(arr_locality_61[dialog_locality61_selected]);
                                CommonStaticData.localitySet = "6/1" + "/" + String.valueOf(dialog_locality61_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_61);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality61_selected == 0) {
                                    int[] regionCh = {13, 15, 17, 18, 21, 24, 27, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality61_selected == 1) {
                                    int[] regionCh = {13, 15, 17, 18, 21, 24, 28, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality61Dialog.getWindow().setGravity(Gravity.CENTER);
                locality61Dialog.show();
                break;

            case DIALOG_LOCALITY_62:
                MaterialDialog locality62Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_62)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality62_selected = which;
                                button_locality.setText(arr_locality_62[dialog_locality62_selected]);
                                CommonStaticData.localitySet = "6/2" + "/" + String.valueOf(dialog_locality62_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_62);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality62_selected == 0) {
                                    int[] regionCh = {13, 16, 17, 20, 21, 27, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality62_selected == 1) {
                                    int[] regionCh = {13, 19, 20, 21, 23, 29, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality62_selected == 2) {
                                    int[] regionCh = {39, 41, 43, 47, 49, 51, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality62Dialog.getWindow().setGravity(Gravity.CENTER);
                locality62Dialog.show();
                break;

            case DIALOG_LOCALITY_63:
                MaterialDialog locality63Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_63)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality63_selected = which;
                                button_locality.setText(arr_locality_63[dialog_locality63_selected]);
                                CommonStaticData.localitySet = "6/3" + "/" + String.valueOf(dialog_locality63_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_63);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality63_selected == 0) {
                                    int[] regionCh = {13, 15, 17, 19, 21, 0, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality63_selected == 1) {
                                    int[] regionCh = {16, 18, 20, 23, 24, 0, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality63_selected == 2) {
                                    int[] regionCh = {25, 26, 27, 28, 29, 0, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality63Dialog.getWindow().setGravity(Gravity.CENTER);
                locality63Dialog.show();
                break;

            case DIALOG_LOCALITY_70:
                MaterialDialog locality70Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_70)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality70_selected = which;
                                button_locality.setText(arr_locality_70[dialog_locality70_selected]);
                                CommonStaticData.localitySet = "7/0" + "/" + String.valueOf(dialog_locality70_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_70);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality70_selected == 0) {
                                    int[] regionCh = {22, 26, 28, 30, 31, 32, 34, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality70_selected == 1) {
                                    int[] regionCh = {27, 29, 30, 31, 32, 40, 42, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality70_selected == 2) {
                                    int[] regionCh = {13, 17, 21, 26, 29, 30, 31, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality70_selected == 3) {
                                    int[] regionCh = {18, 20, 22, 23, 24, 26, 28, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality70Dialog.getWindow().setGravity(Gravity.CENTER);
                locality70Dialog.show();
                break;

            case DIALOG_LOCALITY_71:
                MaterialDialog locality71Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_71)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality71_selected = which;
                                button_locality.setText(arr_locality_71[dialog_locality71_selected]);
                                CommonStaticData.localitySet = "7/1" + "/" + String.valueOf(dialog_locality71_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_71);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality71_selected == 0) {
                                    int[] regionCh = {25, 33, 44, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality71Dialog.getWindow().setGravity(Gravity.CENTER);
                locality71Dialog.show();
                break;

            case DIALOG_LOCALITY_72:
                MaterialDialog locality72Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_72)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality72_selected = which;
                                button_locality.setText(arr_locality_72[dialog_locality72_selected]);
                                CommonStaticData.localitySet = "7/2" + "/" + String.valueOf(dialog_locality72_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_72);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality72_selected == 0) {
                                    int[] regionCh = {13, 14, 15, 18, 19, 20, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality72_selected == 1) {
                                    int[] regionCh = {16, 22, 34, 38, 40, 42, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality72Dialog.getWindow().setGravity(Gravity.CENTER);
                locality72Dialog.show();
                break;

            case DIALOG_LOCALITY_73:
                MaterialDialog locality73Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_73)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality73_selected = which;
                                button_locality.setText(arr_locality_73[dialog_locality73_selected]);
                                CommonStaticData.localitySet = "7/3" + "/" + String.valueOf(dialog_locality73_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_73);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality73_selected == 0) {
                                    int[] regionCh = {24, 28, 41, 42, 47, 49, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality73_selected == 1) {
                                    int[] regionCh = {20, 26, 27, 30, 31, 40, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality73Dialog.getWindow().setGravity(Gravity.CENTER);
                locality73Dialog.show();
                break;

            case DIALOG_LOCALITY_74:
                MaterialDialog locality74Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_74)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality74_selected = which;
                                button_locality.setText(arr_locality_74[dialog_locality74_selected]);
                                CommonStaticData.localitySet = "7/4" + "/" + String.valueOf(dialog_locality74_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_74);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality74_selected == 0) {
                                    int[] regionCh = {14, 15, 22, 32, 34, 0, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality74_selected == 1) {
                                    int[] regionCh = {14, 15, 22, 25, 34, 0, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality74Dialog.getWindow().setGravity(Gravity.CENTER);
                locality74Dialog.show();
                break;

            case DIALOG_LOCALITY_75:
                MaterialDialog locality75Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_75)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality75_selected = which;
                                button_locality.setText(arr_locality_75[dialog_locality75_selected]);
                                CommonStaticData.localitySet = "7/5" + "/" + String.valueOf(dialog_locality75_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_75);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality75_selected == 0) {
                                    int[] regionCh = {13, 14, 15, 16, 0, 0, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality75_selected == 1) {
                                    int[] regionCh = {43, 44, 45, 46, 0, 0, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality75Dialog.getWindow().setGravity(Gravity.CENTER);
                locality75Dialog.show();
                break;

            case DIALOG_LOCALITY_76:
                MaterialDialog locality76Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_76)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality76_selected = which;
                                button_locality.setText(arr_locality_76[dialog_locality76_selected]);
                                CommonStaticData.localitySet = "7/6" + "/" + String.valueOf(dialog_locality76_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_76);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality76_selected == 0) {
                                    int[] regionCh = {18, 29, 34, 36, 40, 42, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality76_selected == 1) {
                                    int[] regionCh = {17, 22, 41, 43, 47, 49, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality76_selected == 2) {
                                    int[] regionCh = {20, 22, 24, 37, 39, 41, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                } else if (dialog_locality76_selected == 3) {
                                    int[] regionCh = {13, 14, 15, 19, 21, 25, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality76Dialog.getWindow().setGravity(Gravity.CENTER);
                locality76Dialog.show();
                break;

            case DIALOG_LOCALITY_77:
                MaterialDialog locality77Dialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .title(R.string.select_locality)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locality_77)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locality77_selected = which;
                                button_locality.setText(arr_locality_77[dialog_locality77_selected]);
                                CommonStaticData.localitySet = "7/7" + "/" + String.valueOf(dialog_locality77_selected);    // justin db save
                                TVlog.i(TAG, " >>>>> CommonStaticData.localitySet = " + CommonStaticData.localitySet);
                                try {
                                    removeDialog(DIALOG_LOCALITY_77);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                finish();
                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putString(CommonStaticData.localityKey, CommonStaticData.localitySet);
                                editor.commit();
                                if (dialog_locality77_selected == 0) {
                                    int[] regionCh = {13, 14, 15, 16, 17, 0, 0, 0, 0, 0, 0, 0};
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_REGION_SCAN_START, regionCh);
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                locality77Dialog.getWindow().setGravity(Gravity.CENTER);
                locality77Dialog.show();
                break;

            case DIALOG_AUDIOMODE:
                MaterialDialog audioModeDialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .icon(getResources().getDrawable(R.drawable.ic_settings_gray_48dp))
                        .title(R.string.audio_language)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.audio_language)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(CommonStaticData.audiomodeSet, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_audiomode_selected = which;
                                button_audio.setText(arr_audio[dialog_audiomode_selected]);
                                CommonStaticData.audiomodeSet = dialog_audiomode_selected;    // justin db save
                                FCI_TVi.setAudioMode(CommonStaticData.audiomodeSet);
                                //removeDialog(DIALOG_AUDIOMODE);
                                dialog.dismiss();
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                super.onNegative(dialog);
                                dialog.dismiss();
                            }
                        }).build();
                audioModeDialog.getWindow().setGravity(Gravity.CENTER);
                audioModeDialog.show();
                break;


            case DIALOG_AUDIOTRACK:
                MaterialDialog audioTrackDialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .icon(getResources().getDrawable(R.drawable.ic_settings_gray_48dp))
                        .title(R.string.audio_language)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(language)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(CommonStaticData.audiotrackSet, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_audiotrack_selected = which;
                                int[] info = FCI_TVi.GetPairNSegInfoOfCHIndex(CommonStaticData.lastCH);
                                int isPaired = 0;
                                int pairedIndex = info[0];
                                int segInfo = info[1];
                                if (pairedIndex >= 0) {
                                    isPaired = 1;
                                }
                                if (FCI_TVi.GetAudioNum() < dialog_audiotrack_selected) {

                                } else {
                                    button_audiotrack.setText(arr_audiotrack[dialog_audiotrack_selected]);
                                    CommonStaticData.audiotrackSet = dialog_audiotrack_selected;    // justin db save
                                    FCI_TVi.AVStop();
                                    //dualdecode[[
                                    TVBridge.dualAV_start(CommonStaticData.lastCH, true);
                                    //]]dualdecode
                                    FCI_TVi.SelectAudioLanguage(CommonStaticData.audiotrackSet);
                                    if (CommonStaticData.videotrackSet < FCI_TVi.GetVideoNum()) {
                                        FCI_TVi.SelectVideoTrack(CommonStaticData.videotrackSet);
                                    }
                                    try {
                                        removeDialog(DIALOG_AUDIOTRACK);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                audioTrackDialog.getWindow().setGravity(Gravity.CENTER);
                audioTrackDialog.show();
                break;


            case DIALOG_VIDEOTRACK:
                MaterialDialog videoTrackDialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .icon(getResources().getDrawable(R.drawable.ic_settings_gray_48dp))
                        .title(R.string.video_track)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(selectableVideo)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(CommonStaticData.videotrackSet, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_videotrack_selected = which;
                                if (FCI_TVi.GetVideoNum() < dialog_videotrack_selected) {

                                } else {
                                    button_videotrack.setText(arr_videotrack[dialog_videotrack_selected]);
                                    CommonStaticData.videotrackSet = dialog_videotrack_selected;    // justin db save
                                    FCI_TVi.AVStop();
                                    //dualdecode[[
                                    TVBridge.dualAV_start(CommonStaticData.lastCH, true);
                                    //]]dualdecode
                                    if (CommonStaticData.audiotrackSet < FCI_TVi.GetAudioNum()) {
                                        FCI_TVi.SelectAudioLanguage(CommonStaticData.audiotrackSet);
                                    }
                                    FCI_TVi.SelectVideoTrack(CommonStaticData.videotrackSet);
                                    try {
                                        removeDialog(DIALOG_VIDEOTRACK);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                videoTrackDialog.getWindow().setGravity(Gravity.CENTER);
                videoTrackDialog.show();
                break;


            case DIALOG_SLEEP:
                MaterialDialog sleepDialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .icon(getResources().getDrawable(R.drawable.ic_settings_gray_48dp))
                        .title(R.string.sleep_timer)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.sleep_timer)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(CommonStaticData.sleeptime, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_sleep_selected = which;
                                int delayTime = 0;
                                button_sleep.setText(arr_sleep[dialog_sleep_selected]);
                                CommonStaticData.sleeptime = dialog_sleep_selected;    // justin db save

                                //  TVlog.i(TAG, "Selected DIALOG_SLEEP = "+ CommonStaticData.sleeptime);
                                MainActivity.getInstance().removeEvent(TVEVENT.E_SLEEP_TIMER);
                                switch (CommonStaticData.sleeptime) {
                                    case 1:
                                        delayTime = 15;
                                        break;
                                    case 2:
                                        delayTime = 30;
                                        break;
                                    case 3:
                                        delayTime = 45;
                                        break;
                                    case 4:
                                        delayTime = 60;
                                        break;
                                    case 5:
                                        delayTime = 90;
                                        break;
                                    case 6:
                                        delayTime = 120;
                                        break;
                                }
                                if (CommonStaticData.sleeptime != 0) {
                                    MainActivity.getInstance().postEvent(TVEVENT.E_SLEEP_TIMER, delayTime * 60 * 1000);
                                    //  MainActivity.getInstance().postEvent(TVEVENT.E_SLEEP_TIMER, 10*1000); // for test
                                }
                                try {
                                    removeDialog(DIALOG_SLEEP);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                sleepDialog.getWindow().setGravity(Gravity.CENTER);
                sleepDialog.show();
                break;

                /*
                case DIALOG_STORAGE:
                return new AlertDialog.Builder(this, R.style.CustomDialog)
                        .setTitle(R.string.storage_location)
                        .setSingleChoiceItems(R.array.storage_location, CommonStaticData.storage, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog_storage_selected = which;
                                button_storage.setText(arr_storage[dialog_storage_selected]);
                                CommonStaticData.storage = dialog_storage_selected;    // justin db save
                                removeDialog(DIALOG_STORAGE);
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .create();
                */

            case DIALOG_AGE:
                MaterialDialog ageDialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .icon(getResources().getDrawable(R.drawable.ic_settings_gray_48dp))
                        .title(R.string.storage_location)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.set_age)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(CommonStaticData.PG_Rate, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_age_selected = which;
                                button_age.setText(arr_age[dialog_age_selected]);
                                CommonStaticData.PG_Rate = dialog_age_selected;    // justin db save

                                // justin 20170523
                                if ((FCI_TVi.GetCurProgramRating() < (CommonStaticData.PG_Rate+1))
                                        && (CommonStaticData.PG_Rate != 0)
                                        && (CommonStaticData.ratingsetSwitch == true)) {
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_CONFIRMED_PASSWORD);
                                    CommonStaticData.ageLimitFlag = false;
                                } else {
                                    //CommonStaticData.screenBlockFlag = false;
                                    CommonStaticData.passwordVerifyFlag = false;
                                    CommonStaticData.ageLimitFlag = false;
                                }
                                MainActivity.getInstance().sendEvent(TVEVENT.E_RATING_MONITOR);

                                CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                editor.putInt(CommonStaticData.parentalRatingKey, CommonStaticData.PG_Rate);
                                editor.putBoolean(CommonStaticData.ageLimitFlagKey, CommonStaticData.ageLimitFlag);
                                editor.putBoolean(CommonStaticData.passwordVerifyFlagKey, CommonStaticData.passwordVerifyFlag);
                                editor.commit();
                                try {
                                    removeDialog(DIALOG_AGE);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                ageDialog.getWindow().setGravity(Gravity.CENTER);
                ageDialog.show();
                break;


            case DIALOG_BATTERY:
                MaterialDialog batteryDialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .icon(getResources().getDrawable(R.drawable.ic_settings_gray_48dp))
                        .title(R.string.battery_limit)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.battery_limit)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(CommonStaticData.battMonitorSet, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_battery_selected = which;
                                button_battery.setText(arr_battery[dialog_battery_selected]);
                                CommonStaticData.battMonitorSet = dialog_battery_selected;

                                TVlog.i(TAG, "Selected battery Level = " + CommonStaticData.battMonitorSet);
                                MainActivity.getInstance().removeEvent(TVEVENT.E_BATTERY_LIMITED_CHECK);
                                if (dialog_battery_selected != 0) {
                                    MainActivity.getInstance().postEvent(TVEVENT.E_BATTERY_LIMITED_CHECK, 5000);
                                }
                                // justin db save
                                try {
                                    removeDialog(DIALOG_BATTERY);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                batteryDialog.getWindow().setGravity(Gravity.CENTER);
                batteryDialog.show();
                break;


            case DIALOG_SET_LOCALE:
                MaterialDialog setLocaleDialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .icon(getResources().getDrawable(R.drawable.ic_settings_gray_48dp))
//                            .title(R.string.locale_title)
                        .title(R.string.system_change_title)
                        .titleColor(getResources().getColor(R.color.black))
                        .items(R.array.locale_set)
                        .itemsColor(getResources().getColor(R.color.black))
                        .itemsCallbackSingleChoice(CommonStaticData.localeSet, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog_locale_selected = which;
                                textView_locale.setText(arr_locale[dialog_locale_selected]);
                                imageView_flag.setImageResource(arr_localeflag[dialog_locale_selected]);
                                //CommonStaticData.localeSet = dialog_locale_selected;
                                // // [[ solution switching mode 20170223
                                if (CommonStaticData.localeSet != dialog_locale_selected)
                                {
                                    CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                                    CommonStaticData.localeSet = dialog_locale_selected;

                                    switch(CommonStaticData.localeSet) {
                                        case 4:     //brazil
                                            //    MainActivity.getInstance().envSet_Normal();
                                            settingRestore();
                                            if (cReleaseOption.FCI_SOLUTION_MODE >= 8 && cReleaseOption.FCI_SOLUTION_MODE <= 11) {
                                                buildOption.FCI_SOLUTION_MODE = buildOption.BRAZIL_USB;
                                                CommonStaticData.solutionMode = buildOption.BRAZIL_USB;
                                            } else if (cReleaseOption.FCI_SOLUTION_MODE >= 0 && cReleaseOption.FCI_SOLUTION_MODE <= 7) {
                                                buildOption.FCI_SOLUTION_MODE = buildOption.BRAZIL;
                                                CommonStaticData.solutionMode = buildOption.BRAZIL;
                                            } else if (cReleaseOption.FCI_SOLUTION_MODE >= 12) {
                                                buildOption.FCI_SOLUTION_MODE = buildOption.BRAZIL_FILE;
                                                CommonStaticData.solutionMode = buildOption.BRAZIL_FILE;
                                            }
                                            editor.putInt(CommonStaticData.localeSetKey, CommonStaticData.localeSet);
                                            editor.putInt(CommonStaticData.solutionModeKey, CommonStaticData.solutionMode);
                                            editor.commit();

                                            MainActivity.getInstance().reStart_TV();
                                            postEvent(TVEVENT.E_SOLUTION_MODE_SWITCHING, SCAN_WAIT_TIME);
                                            break;
                                        case 10:     // japan
                                            //MainActivity.getInstance().envSet_JP();
                                            settingRestore();
                                            if (cReleaseOption.FCI_SOLUTION_MODE >= 8 && cReleaseOption.FCI_SOLUTION_MODE <= 11) {
                                                buildOption.FCI_SOLUTION_MODE = buildOption.JAPAN_USB;
                                                CommonStaticData.solutionMode = buildOption.JAPAN_USB;
                                            } else if (cReleaseOption.FCI_SOLUTION_MODE >= 0 && cReleaseOption.FCI_SOLUTION_MODE <= 7) {
                                                buildOption.FCI_SOLUTION_MODE = buildOption.JAPAN;
                                                CommonStaticData.solutionMode = buildOption.JAPAN;
                                            } else if (cReleaseOption.FCI_SOLUTION_MODE >= 12) {
                                                buildOption.FCI_SOLUTION_MODE = buildOption.JAPAN_FILE;
                                                CommonStaticData.solutionMode = buildOption.JAPAN_FILE;
                                            }
                                            editor.putInt(CommonStaticData.localeSetKey, CommonStaticData.localeSet);
                                            editor.putInt(CommonStaticData.solutionModeKey, CommonStaticData.solutionMode);
                                            editor.commit();

                                            MainActivity.getInstance().reStart_TV();
                                            postEvent(TVEVENT.E_SOLUTION_MODE_SWITCHING, SCAN_WAIT_TIME);
                                            break;

                                        case 15:    // philippines
                                            //   MainActivity.getInstance().envSet_Normal();
                                            if (cReleaseOption.FCI_SOLUTION_MODE >= 8 && cReleaseOption.FCI_SOLUTION_MODE <= 11) {
                                                buildOption.FCI_SOLUTION_MODE = buildOption.PHILIPPINES_USB;
                                                CommonStaticData.solutionMode = buildOption.PHILIPPINES_USB;
                                            } else if (cReleaseOption.FCI_SOLUTION_MODE >= 0 && cReleaseOption.FCI_SOLUTION_MODE <= 7) {
                                                buildOption.FCI_SOLUTION_MODE = buildOption.PHILIPPINES;
                                                CommonStaticData.solutionMode = buildOption.PHILIPPINES;
                                            } else if (cReleaseOption.FCI_SOLUTION_MODE >= 12) {
                                                buildOption.FCI_SOLUTION_MODE = buildOption.PHILIPPINES_FILE;
                                                CommonStaticData.solutionMode = buildOption.PHILIPPINES_FILE;
                                            }
                                            editor.putInt(CommonStaticData.localeSetKey, CommonStaticData.localeSet);
                                            editor.putInt(CommonStaticData.solutionModeKey, CommonStaticData.solutionMode);
                                            editor.commit();

                                            MainActivity.getInstance().reStart_TV();
                                            postEvent(TVEVENT.E_SOLUTION_MODE_SWITCHING, SCAN_WAIT_TIME);
                                            break;

                                        case 16:    // sri_kanka
                                            //    MainActivity.getInstance().envSet_Normal();
                                            if (cReleaseOption.FCI_SOLUTION_MODE >= 8 && cReleaseOption.FCI_SOLUTION_MODE <=11) {
                                                buildOption.FCI_SOLUTION_MODE = buildOption.SRILANKA_USB;
                                                CommonStaticData.solutionMode = buildOption.SRILANKA_USB;
                                            } else if (cReleaseOption.FCI_SOLUTION_MODE >= 12) {
                                                buildOption.FCI_SOLUTION_MODE = buildOption.BRAZIL_FILE;
                                                CommonStaticData.solutionMode = buildOption.BRAZIL_FILE;
                                            } else {
                                                buildOption.FCI_SOLUTION_MODE = buildOption.SRILANKA;
                                                CommonStaticData.solutionMode = buildOption.SRILANKA;
                                            }
                                            editor.putInt(CommonStaticData.localeSetKey, CommonStaticData.localeSet);
                                            editor.putInt(CommonStaticData.solutionModeKey, CommonStaticData.solutionMode);
                                            editor.commit();

                                            MainActivity.getInstance().reStart_TV();
                                            postEvent(TVEVENT.E_SOLUTION_MODE_SWITCHING, SCAN_WAIT_TIME);
                                            break;
                                        default:    // default all Brazil
                                            if (CommonStaticData.solutionMode != buildOption.BRAZIL && CommonStaticData.solutionMode != buildOption.BRAZIL_USB) {
                                                //    MainActivity.getInstance().envSet_Normal();
                                                if (cReleaseOption.FCI_SOLUTION_MODE >= 8 && cReleaseOption.FCI_SOLUTION_MODE <= 11) {
                                                    buildOption.FCI_SOLUTION_MODE = buildOption.BRAZIL_USB;
                                                    CommonStaticData.solutionMode = buildOption.BRAZIL_USB;
                                                } else if (cReleaseOption.FCI_SOLUTION_MODE >= 0 && cReleaseOption.FCI_SOLUTION_MODE <= 7) {
                                                    buildOption.FCI_SOLUTION_MODE = buildOption.BRAZIL;
                                                    CommonStaticData.solutionMode = buildOption.BRAZIL;
                                                } else if (cReleaseOption.FCI_SOLUTION_MODE >= 12) {
                                                    buildOption.FCI_SOLUTION_MODE = buildOption.BRAZIL_FILE;
                                                    CommonStaticData.solutionMode = buildOption.BRAZIL_FILE;
                                                }
                                                editor.putInt(CommonStaticData.localeSetKey, CommonStaticData.localeSet);
                                                editor.putInt(CommonStaticData.solutionModeKey, CommonStaticData.solutionMode);
                                                editor.commit();

                                                MainActivity.getInstance().reStart_TV();
                                                postEvent(TVEVENT.E_SOLUTION_MODE_SWITCHING, SCAN_WAIT_TIME);

                                            }
                                            break;
                                    }
                                }
                                //]]
                                try {
                                    removeDialog(DIALOG_SET_LOCALE);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                                return false;
                            }
                        })
                        .widgetColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .build();
                setLocaleDialog.getWindow().setGravity(Gravity.CENTER);
                setLocaleDialog.show();
                break;


            case DIALOG_RESET:
                MaterialDialog resetDialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .icon(getResources().getDrawable(R.drawable.ic_settings_gray_48dp))
                        .title(R.string.reset_title)
                        .titleColor(getResources().getColor(R.color.black))
                        .content(R.string.reset_msg)
                        .contentColor(getResources().getColor(R.color.black))
                        .positiveText(R.string.ok)
                        .positiveColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                settingRestore();
                                if (MainActivity.isMainActivity) {
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_CAPTION_CLEAR_NOTIFY);
                                } else if (FloatingWindow.isFloating) {
                                    FloatingWindow.getInstance().sendEvent(TVEVENT.E_CAPTION_CLEAR_NOTIFY_FLOATING);
                                }
                                MainActivity.getInstance().sendEvent(TVEVENT.E_CHLIST_REMOVE);
                                MainActivity.getInstance().sendEvent(TVEVENT.E_SUPERIMPOSE_CLEAR_NOTIFY);
                                MainActivity.getInstance().removeEvent(TVEVENT.E_BATTERY_LIMITED_CHECK);
                                MainActivity.getInstance().removeEvent(TVEVENT.E_SLEEP_TIMER);
                                onResume();
                                switchReset.setChecked(false);
                                sendEvent(TVEVENT.E_CHANNEL_LIST_REMOVED);
                            }

                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                super.onNegative(dialog);
                                switchReset.setChecked(false);
                            }
                        })
                        .build();
                resetDialog.getWindow().setGravity(Gravity.CENTER);
                resetDialog.show();
                break;


            case DIALOG_RESTORE_PASSWORD: {
                String saved = CommonStaticData.settings.getString(CommonStaticData.passwordKey, null);
                if (saved == null || saved.length() == 0) {
                    switchReset.setChecked(false);
                    CustomToast customToast = new CustomToast(getApplicationContext());
                    customToast.showToast(SettingActivity.this, R.string.settings_password_needed, Toast.LENGTH_SHORT);
                    break;
                }

                MaterialDialog restorePasswordDialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .icon(getResources().getDrawable(R.drawable.ic_settings_gray_48dp))
                        .title(R.string.settings_password)
                        .titleColor(getResources().getColor(R.color.black))
                        .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                        .positiveText(R.string.submit)
                        .positiveColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                super.onNegative(dialog);
                                switchReset.setChecked(false);
                            }
                        })
                        .input(null, null, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                // TVlog.i(TAG, "ENTER InputDialog");
                                String save = input.toString();
                                CommonStaticData.PassWord = CommonStaticData.settings.getString(CommonStaticData.passwordKey, null);
                                if (CommonStaticData.PassWord == null) {
                                    switchReset.setChecked(false);
                                    showDialog(DIALOG_PASSWORD);
                                    return;
                                }

                                if (CommonStaticData.PassWord.equals(save)) {
                                    switchReset.setChecked(false);
                                    showDialog(DIALOG_RESET);
                                } else {
                                    switchReset.setChecked(false);
                                    CustomToast customToast = new CustomToast(getApplicationContext());
                                    customToast.showToast(SettingActivity.this, R.string.settings_password_wrong, Toast.LENGTH_SHORT);
                                }
                            }
                        })
                        .build();
                restorePasswordDialog.getWindow().setGravity(Gravity.CENTER);
                restorePasswordDialog.show();
            }
            break;

            case DIALOG_PASSWORD: {
                String saved  = CommonStaticData.settings.getString(CommonStaticData.passwordKey, null);
                TVlog.i(TAG, " DIALOG_PASSWORD  String = " + saved);
                if (saved == null || saved.length() == 0) {
                    InputDialog dig = new InputDialog(this, InputDialog.TYPE_NEW_PASSWORD, null, null, null);
                } else {
                    InputDialog dig = new InputDialog(this, InputDialog.TYPE_ENTER_PASSWORD, null, null, null);
                }

            }
            break;


            case DIALOG_SET_PASSWORD: {
                String saved  = CommonStaticData.settings.getString(CommonStaticData.passwordKey, null);
                if (saved == null || saved.length() == 0) {
                    InputDialog dig = new InputDialog(this, InputDialog.TYPE_NEW_PASSWORD, null, null, null);
                } else {
                    InputDialog dig = new InputDialog(this, InputDialog.TYPE_CHECK_N_NEW_PASSWORD, null, null, null);
                }
            }
            break;

            case DIALOG_CHECK_PW_SETAGE: {
                String saved = CommonStaticData.settings.getString(CommonStaticData.passwordKey, null);
                if (saved == null || saved.length() == 0) {
                    CustomToast customToast = new CustomToast(getApplicationContext());
                    customToast.showToast(SettingActivity.this, R.string.settings_password_needed, Toast.LENGTH_SHORT);
                    break;
                }
                MaterialDialog checkPasswordDialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .iconRes(R.drawable.ic_lock_outline_gray_48dp)
                        .title(R.string.settings_password)
                        .titleColor(getResources().getColor(R.color.black))
                        .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                        .positiveText(R.string.submit)
                        .positiveColor(getResources().getColor(R.color.blue3))
                        .negativeText(R.string.cancel)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .input(null, null, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                // TVlog.i(TAG, "ENTER InputDialog");
                                String save = input.toString();
                                CommonStaticData.PassWord = CommonStaticData.settings.getString(CommonStaticData.passwordKey, null);
                                if (CommonStaticData.PassWord == null) {
                                    showDialog(DIALOG_PASSWORD);
                                    return;
                                }

                                if (CommonStaticData.PassWord.equals(save)) {
                                    showDialog(DIALOG_AGE);
                                } else {
                                    CustomToast customToast = new CustomToast(getApplicationContext());
                                    customToast.showToast(SettingActivity.this, R.string.settings_password_wrong, Toast.LENGTH_SHORT);
                                }
                            }
                        }).build();
                checkPasswordDialog.getWindow().setGravity(Gravity.CENTER);
                checkPasswordDialog.show();
            }
            break;

            case DIALOG_BCAS_CARD_TEST_GOOD: {
                MaterialDialog bcasCardTestGoodDialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .content(R.string.bcas_card_test_good_msg)
                        .contentColor(getResources().getColor(R.color.black))
                        .negativeText(R.string.close)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                super.onNegative(dialog);
                                dialog.dismiss();
                            }
                        }).build();
                bcasCardTestGoodDialog.getWindow().setGravity(Gravity.CENTER);
                bcasCardTestGoodDialog.show();
                View decorView = bcasCardTestGoodDialog.getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE;    // justin
                decorView.setSystemUiVisibility(uiOptions);

                TVlog.i(TAG, "== setSystemUiVisibility hideController ==");
            }
            break;

            case DIALOG_BCAS_CARD_TEST_NG: {
                MaterialDialog bcasCardTestNgDialog = new MaterialDialog.Builder(SettingActivity.this)
                        .theme(Theme.LIGHT)
                        .content(R.string.bcas_card_test_ng_msg)
                        .contentColor(getResources().getColor(R.color.black))
                        .negativeText(R.string.close)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                super.onNegative(dialog);
                                dialog.dismiss();
                            }
                        }).build();
                bcasCardTestNgDialog.getWindow().setGravity(Gravity.CENTER);
                bcasCardTestNgDialog.show();
                // live add for hide bar at searching
                View decorView = bcasCardTestNgDialog.getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE;    // justin
                decorView.setSystemUiVisibility(uiOptions);

            }
            break;
        }

        return null;
    }


    public void settingRestore() {
        CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = CommonStaticData.settings.edit();
        editor.putBoolean(CommonStaticData.captionSwitchKey, true);
        editor.putInt(CommonStaticData.scaleSwitchKey, 0);
        editor.putInt(CommonStaticData.brightnessKey, 50);
        editor.putInt(CommonStaticData.transparentKey, 50);
        //live add
        if (MainActivity.getInstance().strISDBMode.equalsIgnoreCase("ISDBT Oneseg")) {
            editor.putInt(CommonStaticData.receivemodeSwitchKey, CommonStaticData.RECEIVE_MODE_1SEG);
        } else {
            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB) {
                editor.putInt(CommonStaticData.receivemodeSwitchKey, CommonStaticData.RECEIVE_MODE_AUTO);
            } else {
                editor.putInt(CommonStaticData.receivemodeSwitchKey, CommonStaticData.RECEIVE_MODE_OFF);
            }
        }
        //

        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG) {
            editor.putInt(CommonStaticData.autoSearchSwitchKey, 0);
        } else {
            editor.putInt(CommonStaticData.autoSearchSwitchKey, 1);
        }

        editor.putString(CommonStaticData.areaKey, "1");
        editor.putString(CommonStaticData.prefectureKey, "1/5");
        editor.putString(CommonStaticData.localityKey, "1/5/0");

        editor.putInt(CommonStaticData.audiomodeSwitchKey, 1);
        editor.putInt(CommonStaticData.sleeptimerSwitchKey, 0);
        if (buildOption.ADD_GINGA_NCL==true) {
            editor.putBoolean(CommonStaticData.interactiveKey, true);
            if (CommonStaticData.interactiveSwitch == false) {
                MainActivity.getInstance().sendEvent(TVEVENT.E_INTERACTIVE_DISABLE);
            }
        }
        //  editor.putInt(CommonStaticData.storageSwitchKey, 0);
        editor.putBoolean(CommonStaticData.parentalcontrolSwitchKey, true);
        editor.putInt(CommonStaticData.parentalRatingKey, 0);
        editor.putInt(CommonStaticData.definedbattMonitorKey,0);

        // [[ solution switching mode 20170223
        editor.putInt(CommonStaticData.solutionModeKey, cReleaseOption.FCI_SOLUTION_MODE);
        editor.putInt(CommonStaticData.localeSetKey, 4);

        // justin add
        editor.putString(CommonStaticData.passwordKey,null);
        editor.putInt(CommonStaticData.audiotrackSwitchKey,0);
        editor.putInt(CommonStaticData.videotrackSwitchKey,0);
        editor.putBoolean(CommonStaticData.superimposeSwitchKey, true);
        CommonStaticData.scanCHnum = 0;
        editor.putInt(CommonStaticData.scanedChannelsKey, CommonStaticData.scanCHnum);
        CommonStaticData.currentScaleMode = 0; // normal ratio
        editor.putInt(CommonStaticData.currentScaleModeKey, CommonStaticData.currentScaleMode);

        /*
        if (cReleaseOption.FCI_SOLUTION_MODE == cReleaseOption.JAPAN
                || cReleaseOption.FCI_SOLUTION_MODE == cReleaseOption.JAPAN_ONESEG
                || cReleaseOption.FCI_SOLUTION_MODE == cReleaseOption.JAPAN_USB
                || cReleaseOption.FCI_SOLUTION_MODE == cReleaseOption.JAPAN_FILE) { // Japan
            editor.putInt(CommonStaticData.localeSetKey, 10);
        } else if (cReleaseOption.FCI_SOLUTION_MODE == cReleaseOption.BRAZIL
                || cReleaseOption.FCI_SOLUTION_MODE == cReleaseOption.BRAZIL_ONESEG
                || cReleaseOption.FCI_SOLUTION_MODE == cReleaseOption.BRAZIL_USB
                || cReleaseOption.FCI_SOLUTION_MODE == cReleaseOption.BRAZIL_FILE) {
            editor.putInt(CommonStaticData.localeSetKey, 4);
        } else if ( cReleaseOption.FCI_SOLUTION_MODE == cReleaseOption.SRILANKA
                || cReleaseOption.FCI_SOLUTION_MODE == cReleaseOption.SRILANKA_ONESEG
                || cReleaseOption.FCI_SOLUTION_MODE == cReleaseOption.SRILANKA_USB) {
            editor.putInt(CommonStaticData.localeSetKey, 16);
        } else if (cReleaseOption.FCI_SOLUTION_MODE == cReleaseOption.PHILIPPINES
                || cReleaseOption.FCI_SOLUTION_MODE == cReleaseOption.PHILIPPINES_ONESEG
                || cReleaseOption.FCI_SOLUTION_MODE == cReleaseOption.PHILIPPINES_USB
                || cReleaseOption.FCI_SOLUTION_MODE == cReleaseOption.PHILIPPINES_FILE) { // Philippines
            editor.putInt(CommonStaticData.localeSetKey, 15);
        } else {
           // CommonStaticData.localeSet=CommonStaticData.settings.getInt(CommonStaticData.localeSetKey, 4);
            editor.putInt(CommonStaticData.localeSetKey, 4);
        }*/

        editor.commit();
    }

    private void setBrightness(int value) {
        if (value < 10) {
            value = 10;
        } else if (value > 100) {
            value = 100;
        }

        //brightness = value;
        CommonStaticData.brightness = value;

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.screenBrightness = (float) value / 100;
        getWindow().setAttributes(params);
    }

    private void setTransparent (int alpha_value) {
        if (alpha_value < 10) {
            alpha_value = 10;
        } else if (alpha_value > 100) {
            alpha_value = 100;
        }

        //transparent = alpha_value;
        CommonStaticData.transparent = alpha_value;

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = (float) alpha_value / 100;
        getWindow().setAttributes(params);
    }

    public class MyBrightnessChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            setBrightness(progress);
            //setTransparent(progress);
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }

    public class MyTransparentChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            setTransparent(progress);

        }

        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }

    public void bcas_update() {
        if (MainActivity.cardStr != null && (MainActivity.is_inserted_card==1)) {
            if (tv_bcas_id != null) {
                tv_bcas_id.setText(MainActivity.cardStr);
            }
        } else {
            if (tv_bcas_id != null) {
                tv_bcas_id.setText("- - - -   - - - -   - - - -   - - - -   - - - -");
            }
        }
    }


    /*
    public static boolean getMicroSDCardEnable() {

        boolean enable = false;

//      String internal_sd_path = Environment.getExternalStorageDirectory().getPath();
        String internal_sd_path = buildOption.PHONE_DRIVE_PATH;
        String micro_include_internal_sd_path = buildOption.SECOND_DRIVE_PATH;

        String temp_folder = "storage_test_1234";
        String temp_path = micro_include_internal_sd_path + temp_folder;

        File dirtemp = new File(temp_path);
        if (!dirtemp.exists()) {
            try {
                dirtemp.mkdirs();
            } catch (Exception e) {
            }
        }

        File dir_exist = new File(temp_path);
        if (!dir_exist.exists()) {
            enable = false;
        } else {
            enable = true;
            dir_exist.delete();
        }
        return enable;
    }
*/

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/

/*
    public void aboutClicked (View v) {
        Intent intent = new Intent(getApplicationContext(), kr.co.fci.fci_player.AboutActivity.class);
        startActivity(intent);
    }
*/

/*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Log.d(TAG, "keyCode:"+keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
*/

    /*
     * USB
     */
    /*
    private void doIntentMessageHook() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
                    System.exit(0);
                    SettingActivity.this.finish();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        intentFilter.addDataScheme("file");
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        Log.d(TAG, "SettingPreferences-----------onDestroy");
        this.unregisterReceiver(mReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        Log.d(TAG, "SettingPreferences-----------onResume");
        super.onResume();
    }
    */
}

