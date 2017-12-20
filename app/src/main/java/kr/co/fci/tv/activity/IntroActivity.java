package kr.co.fci.tv.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import kr.co.fci.tv.MainActivity;
import kr.co.fci.tv.R;
import kr.co.fci.tv.TVEVENT;
import kr.co.fci.tv.buildOption;
import kr.co.fci.tv.saves.CommonStaticData;
import kr.co.fci.tv.util.TVlog;

/**
 * Created by live.kim on 2015-09-23.
 */


public class IntroActivity extends Activity {

    private String TAG = "IntroActivity";

    Context mContext;

    ImageView imageView;
    AnimationDrawable frameAnimation;
    View decorView;
    int isRunAnimation = buildOption.GUI_STYLE;
    ImageView imgLogo;
    Animation ani;
    Context context;
    FrameLayout intro_bg;

    boolean isScreenOn;

    public Handler IntroActivityHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            TVEVENT event = TVEVENT.values()[msg.what];
            switch(event)
            {
                case E_INTRO_MAIN_ACVITIVY_CALL:
                {
                    TVlog.i(TAG, " Main activity start Call  ~ ");
                    Intent main = new Intent(IntroActivity.this, MainActivity.class);
                    startActivity(main);
                    finish();
                }
                break;
            }
            super.handleMessage(msg);
        }};


    public void postEvent(TVEVENT _Event,int _time )
    {
        int m;
        m = _Event.ordinal();
        Message msg = IntroActivityHandler.obtainMessage(m);
        IntroActivityHandler.sendEmptyMessageDelayed(m, _time);
    }

    public void removeEvent(TVEVENT _Event)
    {
        int m;
        m = _Event.ordinal();
        Message msg = IntroActivityHandler.obtainMessage(m);
        IntroActivityHandler.removeMessages(m);
    }

    @Override
    protected void onDestroy(){
        TVlog.i(TAG, "==== onDestroy ======");
        removeEvent(TVEVENT.E_INTRO_MAIN_ACVITIVY_CALL);
        super.onDestroy();

        TVlog.i(TAG, " >>> Screen stat is  <<< " +isScreenOn);
// usbdongle[[
        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
            if(!isScreenOn){
                CommonStaticData.countIntro = 0;
                SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                editor.putInt(CommonStaticData.countIntroKey, CommonStaticData.countIntro);       // justin check
                editor.commit();
                if(MainActivity.getInstance() != null) {
                    MainActivity.getInstance().TVTerminate();
                }
            }
}
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getIntent().addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);      
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

        if (Build.VERSION.SDK_INT <= 19) {
            isScreenOn = pm.isScreenOn();
        } else {
            isScreenOn = pm.isInteractive();
        }
        TVlog.i(TAG, " >>> Screen stat is  <<< " +isScreenOn);

        if(!isScreenOn){
            finish();
        }

        if (MainActivity.getInstance() != null) {
            if (MainActivity.getInstance().isServiceRunningCheck()) {
                kr.co.fci.tv.util.CustomToast toast = new kr.co.fci.tv.util.CustomToast(getApplicationContext());
                toast.showToast(getApplicationContext(), getApplicationContext().getString(R.string.app_name)+" already running!!", Toast.LENGTH_LONG);
                finish();
            }
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE;    // justin
        decorView.setSystemUiVisibility(uiOptions);
        //TVlog.i(TAG, "== setSystemUiVisibility hideController ==");

        //  super.onCreate(savedInstanceState);
        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB ||
                buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
            CommonStaticData.settings = getSharedPreferences(CommonStaticData.mSharedPreferencesName, Context.MODE_PRIVATE);
            CommonStaticData.countIntro = CommonStaticData.settings.getInt(CommonStaticData.countIntroKey, 0);
        }

        if(buildOption.GUI_STYLE == 0 || buildOption.GUI_STYLE == 1 || (buildOption.GUI_STYLE == 2 && buildOption.CUSTOMER.contains("IRobot")))  {

            // usbdongle[[
            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                    buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB ||
                    buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB ||
                    buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                if (CommonStaticData.countIntro == 0) {
                    setContentView(R.layout.activity_intro);
                    intro_bg = (FrameLayout) findViewById(R.id.intro_bg);

                    imageView = (ImageView) findViewById(R.id.cherry_logo);
                    imgLogo = (ImageView)findViewById(R.id.fci_logo);

                    if(isRunAnimation == 1) {

                        intro_bg.setBackground(getResources().getDrawable(R.color.cherry_red));

                        imageView.setVisibility(View.VISIBLE);
                        imgLogo.setVisibility(View.INVISIBLE);

                        frameAnimation = (AnimationDrawable) imageView.getDrawable();
                        frameAnimation.start();

                        if (buildOption.GUI_STYLE == 2 && buildOption.CUSTOMER.contains("IRobot")) {
                            postEvent(TVEVENT.E_INTRO_MAIN_ACVITIVY_CALL,2000);
                        } else {
                            postEvent(TVEVENT.E_INTRO_MAIN_ACVITIVY_CALL,4000);
                        }

                    } else {
                        intro_bg.setBackground(getResources().getDrawable(R.color.white));

                        imageView.setVisibility(View.INVISIBLE);
                        imgLogo.setVisibility(View.VISIBLE);

                        ani = createAnimation(context);

                        ani.setAnimationListener(new Animation.AnimationListener(){

                            public void onAnimationEnd(Animation animation) {

                                postEvent(TVEVENT.E_INTRO_MAIN_ACVITIVY_CALL,1000);
                            }
                            public void onAnimationRepeat(Animation animation) {;}
                            public void onAnimationStart(Animation animation) {;}

                        });

                    }
                }
                else if (CommonStaticData.countIntro == 2 || CommonStaticData.countIntro == 3) {
                    postEvent(TVEVENT.E_INTRO_MAIN_ACVITIVY_CALL,0);
                    finish();
                }
                else {
                    finish();
                }
                //  SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                CommonStaticData.countIntro = 1;
                //  editor.putInt(CommonStaticData.countIntroKey, CommonStaticData.countIntro);       // justin check
                //  editor.commit();
                // usbdongle]]

            } else {
                setContentView(R.layout.activity_intro);
                intro_bg = (FrameLayout) findViewById(R.id.intro_bg);

                imageView = (ImageView) findViewById(R.id.cherry_logo);
                imgLogo = (ImageView)findViewById(R.id.fci_logo);

                if(isRunAnimation == 1) {


                    intro_bg.setBackground(getResources().getDrawable(R.color.cherry_red));

                    imageView.setVisibility(View.VISIBLE);
                    imgLogo.setVisibility(View.INVISIBLE);

                    frameAnimation = (AnimationDrawable) imageView.getDrawable();
                    frameAnimation.start();

                    if (buildOption.GUI_STYLE == 2 && buildOption.CUSTOMER.contains("IRobot")) {
                        postEvent(TVEVENT.E_INTRO_MAIN_ACVITIVY_CALL,2000);
                    } else {
                        postEvent(TVEVENT.E_INTRO_MAIN_ACVITIVY_CALL,4000);
                    }


                } else {
                    intro_bg.setBackground(getResources().getDrawable(R.color.white));

                    imageView.setVisibility(View.INVISIBLE);
                    imgLogo.setVisibility(View.VISIBLE);

                    ani = createAnimation(context);

                    ani.setAnimationListener(new Animation.AnimationListener(){

                        public void onAnimationEnd(Animation animation) {

                            postEvent(TVEVENT.E_INTRO_MAIN_ACVITIVY_CALL,1000);
                        }
                        public void onAnimationRepeat(Animation animation) {;}
                        public void onAnimationStart(Animation animation) {;}

                    });

                }
            }
        } else {
            if (buildOption.CUSTOMER.contains("Myphone") || buildOption.CUSTOMER.contains("K-FUNG")) {
                // usbdongle[[
                if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                    if (CommonStaticData.countIntro == 0) {
                        setContentView(R.layout.activity_video_intro);
                        postEvent(TVEVENT.E_INTRO_MAIN_ACVITIVY_CALL,5000);
                    }
                    else if (CommonStaticData.countIntro == 2 || CommonStaticData.countIntro == 3) {
                        postEvent(TVEVENT.E_INTRO_MAIN_ACVITIVY_CALL,0);
                        finish();
                    }
                    else {
                        finish();
                    }
                    //    SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                    CommonStaticData.countIntro = 1;
                    //   editor.putInt(CommonStaticData.countIntroKey, CommonStaticData.countIntro);
                    //   editor.commit();
                    // usbdongle]]
                }
                else {
                    setContentView(R.layout.activity_video_intro);
                    postEvent(TVEVENT.E_INTRO_MAIN_ACVITIVY_CALL,5000);
                }
            } else if (buildOption.CUSTOMER.contains("ZHnK") || buildOption.CUSTOMER.contains("Chico")) {  // live add
                // usbdongle[[
                if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB ||
                        buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                    if (CommonStaticData.countIntro == 0) {
                        setContentView(R.layout.activity_intro);
                        intro_bg = (FrameLayout) findViewById(R.id.intro_bg);

                        imageView = (ImageView) findViewById(R.id.cherry_logo);
                        imgLogo = (ImageView)findViewById(R.id.fci_logo);

                        intro_bg.setBackground(getResources().getDrawable(R.color.white));

                        imageView.setVisibility(View.INVISIBLE);
                        imgLogo.setVisibility(View.VISIBLE);

                        ani = createAnimation(context);

                        ani.setAnimationListener(new Animation.AnimationListener(){

                            public void onAnimationEnd(Animation animation) {

                                postEvent(TVEVENT.E_INTRO_MAIN_ACVITIVY_CALL,1000);
                            }
                            public void onAnimationRepeat(Animation animation) {;}
                            public void onAnimationStart(Animation animation) {;}

                        });
                    }
                    else if (CommonStaticData.countIntro == 2 || CommonStaticData.countIntro == 3) {
                        postEvent(TVEVENT.E_INTRO_MAIN_ACVITIVY_CALL,0);
                        finish();
                    }
                    else {
                        finish();
                    }
                    //   SharedPreferences.Editor editor = CommonStaticData.settings.edit();
                    CommonStaticData.countIntro = 1;
                    // editor.putInt(CommonStaticData.countIntroKey, CommonStaticData.countIntro);
                    // editor.commit();
                    // usbdongle]]
                } else {
                    setContentView(R.layout.activity_intro);
                    intro_bg = (FrameLayout) findViewById(R.id.intro_bg);

                    imageView = (ImageView) findViewById(R.id.cherry_logo);
                    imgLogo = (ImageView)findViewById(R.id.fci_logo);

                    intro_bg.setBackground(getResources().getDrawable(R.color.white));

                    imageView.setVisibility(View.INVISIBLE);
                    imgLogo.setVisibility(View.VISIBLE);

                    ani = createAnimation(context);

                    ani.setAnimationListener(new Animation.AnimationListener(){

                        public void onAnimationEnd(Animation animation) {

                            postEvent(TVEVENT.E_INTRO_MAIN_ACVITIVY_CALL,1000);
                        }
                        public void onAnimationRepeat(Animation animation) {;}
                        public void onAnimationStart(Animation animation) {;}

                    });
                }
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if(buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 || (buildOption.GUI_STYLE == 2 && buildOption.CUSTOMER.contains("IRobot")) ) {
            if (isRunAnimation == 1) {
                if (hasFocus) {
                    frameAnimation.start();
                } else {
                    frameAnimation.stop();
                }
            }
        }

    }

    protected void onStart() {
        super.onStart();

        if((buildOption.GUI_STYLE == 0)
                || buildOption.GUI_STYLE == 1
                || (buildOption.GUI_STYLE == 2 && buildOption.CUSTOMER.contains("IRobot"))) {
            if (isRunAnimation == 1) {
                frameAnimation.start();
            } else {
                imgLogo.startAnimation(ani);
            }
        }
        if (buildOption.CUSTOMER.contains("ZHnK") || buildOption.CUSTOMER.contains("Chico")) {
            if(buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 || buildOption.GUI_STYLE == 2) {
                if (isRunAnimation == 1) {
                    frameAnimation.start();
                } else {
                    imgLogo.startAnimation(ani);
                }
            }
        }

    }

    public static Animation createAnimation(Context context) {
        AnimationSet set = new AnimationSet(true);

        RotateAnimation ra = new RotateAnimation(-8, 8,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        ra.setDuration(1000);
        ra.setRepeatMode(Animation.REVERSE);
        ra.setRepeatCount(2);


        AlphaAnimation aa = new AlphaAnimation(0f, 1f);
        aa.setDuration(3000);
        aa.setRepeatMode(Animation.REVERSE);

        if (buildOption.CUSTOMER.contains("ZHnK") || buildOption.CUSTOMER.contains("Chico")) {
            ScaleAnimation sa = new ScaleAnimation(1.0f, 1.0f, 1.0f, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                    0.5f);
            sa.setDuration(3000);
            sa.setRepeatMode(Animation.REVERSE);

            set.addAnimation(aa);
            set.addAnimation(sa);
        } else {
            ScaleAnimation sa = new ScaleAnimation(0.1f, 1.0f, 0.1f, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                    0.5f);
            sa.setDuration(3000);
            sa.setRepeatMode(Animation.REVERSE);

            set.addAnimation(aa);
            set.addAnimation(sa);
        }

        set.setFillAfter(true);

        return set;
    }
}


/*

public class IntroActivity extends Activity {

    ImageView imgLogo;
    Animation ani;
    Context context;

    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        imgLogo = (ImageView)findViewById(R.id.animationImage);

        //ani = new AlphaAnimation(0.0f, 2.0f);
        ani = createAnimation(context);
        //ani = new ScaleAnimation(0,1,1,1);
        //ani.setDuration(3000);

        ani.setAnimationListener(new Animation.AnimationListener(){

            public void onAnimationEnd(Animation animation) {
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        Intent main = new Intent(IntroActivity.this, MainActivity.class);
                        startActivity(main);
                        finish();
                    }
                }, 1000);
            }
            public void onAnimationRepeat(Animation animation) {;}
            public void onAnimationStart(Animation animation) {;}

        });
    }

    public static Animation createAnimation(Context context) {
        AnimationSet set = new AnimationSet(true);


        RotateAnimation ra = new RotateAnimation(-8, 8,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        ra.setDuration(1000);
        ra.setRepeatMode(Animation.REVERSE);
        ra.setRepeatCount(2);


        AlphaAnimation aa = new AlphaAnimation(0f, 1f);
        aa.setDuration(3000);
        aa.setRepeatMode(Animation.REVERSE);

        ScaleAnimation sa = new ScaleAnimation(0.1f, 1.0f, 0.1f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        sa.setDuration(3000);
        sa.setRepeatMode(Animation.REVERSE);
        //sa.setRepeatCount(2);

        //set.addAnimation(ra);
        set.addAnimation(aa);
        set.addAnimation(sa);
        set.setFillAfter(true);

        return set;
    }

    protected void onStart() {
        super.onStart();
        imgLogo.startAnimation(ani);
    }

}
*/
