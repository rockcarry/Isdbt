package kr.co.fci.tv.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import java.util.List;

import kr.co.fci.tv.R;
import kr.co.fci.tv.buildOption;
import kr.co.fci.tv.saves.CommonStaticData;

/**
 * Created by live.kim on 2015-08-15.
 */
public class OpenActivity extends Activity {

    //ActionBar abar;

    private static String TAG = "OpenActivity ";

    Context mContext;

    private MaterialDialog ffmpegDialog;
    private MaterialDialog libusbDialog;

    ImageButton button_back;

    LinearLayout ll_libusb;

    ImageButton imageButton_libupnp1;
    ImageButton imageButton_geexbox;
    ImageButton imageButton_ssl;
    ImageButton imageButton_libupnp;
    ImageButton imageButton_ffmpeg;
    ImageButton imageButton_libusb;

    static final int DIALOG_LIBUPNP1 = 0;
    static final int DIALOG_GEEXBOX = 1;
    static final int DIALOG_SSL = 2;
    static final int DIALOG_LIBUPNP = 3;
    static final int DIALOG_FFMPEG = 4;
    static final int DIALOG_LIBUSB = 5;


    public static kr.co.fci.tv.activity.OpenActivity instance;
    public static kr.co.fci.tv.activity.OpenActivity getInstance()
    {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open);
        instance = this;
        CommonStaticData.openActivityShow = true;   // justin add for dongle detached
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

/*

        abar = this.getSupportActionBar();
        abar.setBackgroundDrawable(new ColorDrawable(0xff8800ff));
        abar.setTitle(R.string.open);
        abar.setHomeButtonEnabled(true);
        abar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
*/

        LinearLayout title_open = (LinearLayout) findViewById(R.id.title_open);
        title_open.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonStaticData.openActivityShow = false;   // justin add for dongle detached
                Intent intent = new Intent(kr.co.fci.tv.activity.OpenActivity.this, AboutActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ImageButton btn_back_open = (ImageButton) findViewById(R.id.btn_back_open);
        btn_back_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                CommonStaticData.openActivityShow = false;   // justin add for dongle detached
                Intent intent = new Intent(kr.co.fci.tv.activity.OpenActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

        imageButton_libupnp1 = (ImageButton) findViewById(R.id.imageButton_libupnp1);
        imageButton_libupnp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_LIBUPNP1);
            }
        });

        imageButton_geexbox = (ImageButton) findViewById(R.id.imageButton_geexbox);
        imageButton_geexbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_GEEXBOX);
            }
        });

        imageButton_ssl = (ImageButton) findViewById(R.id.imageButton_ssl);
        imageButton_ssl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_SSL);
            }
        });

        imageButton_libupnp = (ImageButton) findViewById(R.id.imageButton_libupnp);
        imageButton_libupnp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_LIBUPNP);
            }
        });

        imageButton_ffmpeg = (ImageButton) findViewById(R.id.imageButton_ffmpeg);
        imageButton_ffmpeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_FFMPEG);
            }
        });

        ll_libusb = (LinearLayout) findViewById(R.id.ll_libusb);
        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
            ll_libusb.setVisibility(View.VISIBLE);
        } else {
            ll_libusb.setVisibility(View.INVISIBLE);
        }

        imageButton_libusb = (ImageButton) findViewById(R.id.imageButton_libusb);
        imageButton_libusb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_LIBUSB);
            }
        });

        Button button_extra = (Button) findViewById(R.id.button_extra);
        button_extra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonStaticData.openActivityShow = false;   // justin add for dongle detached
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                CommonStaticData.openActivityShow = false;   // justin add for dongle detached
                Intent homeIntent = new Intent(this, AboutActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
        }

        return super.onOptionsItemSelected(item);
    }

      @Override
    public void onBackPressed() {
        CommonStaticData.openActivityShow = false;   // justin add for dongle detached
        finish();
        super.onBackPressed();
    }

/*

    public void openClicked1(View v) {
        switch (v.getId()) {
            case R.id.imageButton_libupnp1:
                showDialog(DIALOG_LIBUPNP1);
                break;
            case R.id.imageButton_geexbox:
                showDialog(DIALOG_GEEXBOX);
                break;
            case R.id.imageButton_ssl:
                showDialog(DIALOG_SSL);
                break;
            case R.id.imageButton_libupnp:
                showDialog(DIALOG_LIBUPNP);
                break;
        }
    }
*/


    @Override
    public Dialog onCreateDialog(int id) {

        switch (id) {
            case DIALOG_LIBUPNP1:
                new MaterialDialog.Builder(kr.co.fci.tv.activity.OpenActivity.this)
                        .theme(Theme.LIGHT)
                        .iconRes(R.drawable.ic_info_outline_gray_48dp)
                            .title(R.string.libupnp1_title)
                        .titleColor(getResources().getColor(R.color.black))
                            .content(R.string.libupnp1)
                            .negativeText(R.string.ok)
                        .negativeColor(getResources().getColor(R.color.blue3))
                            .show();
                break;

            case DIALOG_GEEXBOX:
                new MaterialDialog.Builder(kr.co.fci.tv.activity.OpenActivity.this)
                        .theme(Theme.LIGHT)
                        .iconRes(R.drawable.ic_info_outline_gray_48dp)
                            .title(R.string.geexbox_title)
                        .titleColor(getResources().getColor(R.color.black))
                            .content(R.string.geexbox)
                            .negativeText(R.string.ok)
                        .negativeColor(getResources().getColor(R.color.blue3))
                            .show();
                break;

            case DIALOG_SSL:
                new MaterialDialog.Builder(kr.co.fci.tv.activity.OpenActivity.this)
                        .theme(Theme.LIGHT)
                        .iconRes(R.drawable.ic_info_outline_gray_48dp)
                            .title(R.string.ssl_title)
                        .titleColor(getResources().getColor(R.color.black))
                            .content(R.string.ssl)
                            .negativeText(R.string.ok)
                        .negativeColor(getResources().getColor(R.color.blue3))
                            .show();
                break;

            case DIALOG_LIBUPNP:
                new MaterialDialog.Builder(kr.co.fci.tv.activity.OpenActivity.this)
                        .theme(Theme.LIGHT)
                        .iconRes(R.drawable.ic_info_outline_gray_48dp)
                            .title(R.string.libupnp_title)
                        .titleColor(getResources().getColor(R.color.black))
                            .content(R.string.libupnp)
                            .negativeText(R.string.ok)
                        .negativeColor(getResources().getColor(R.color.blue3))
                            .show();
                break;

            case DIALOG_FFMPEG:
            {
                ffmpegDialog = new MaterialDialog.Builder(kr.co.fci.tv.activity.OpenActivity.this)
                        .theme(Theme.LIGHT)
                        .iconRes(R.drawable.ic_info_outline_gray_48dp)
                            .title(R.string.ffmpeg_title)
                        .titleColor(getResources().getColor(R.color.black))
                            .content(R.string.ffmpeg)
                        .contentColor(getResources().getColor(R.color.black))
                            .negativeText(R.string.ok)
                        .negativeColor(getResources().getColor(R.color.blue3))
                            .show();
                    View decorView = ffmpegDialog.getWindow().getDecorView();
                    int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE;    // justin
                    decorView.setSystemUiVisibility(uiOptions);
                }
            break;

            case DIALOG_LIBUSB:
            {
                libusbDialog = new MaterialDialog.Builder(kr.co.fci.tv.activity.OpenActivity.this)
                        .theme(Theme.LIGHT)
                        .iconRes(R.drawable.ic_info_outline_gray_48dp)
                        .title(R.string.libusb_title)
                        .titleColor(getResources().getColor(R.color.black))
                        .content(R.string.libusb)
                        .contentColor(getResources().getColor(R.color.black))
                        .negativeText(R.string.ok)
                        .negativeColor(getResources().getColor(R.color.blue3))
                        .show();
                View decorView = libusbDialog.getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE;    // justin
                decorView.setSystemUiVisibility(uiOptions);
            }
            break;

                /*return new AlertDialog.Builder(this, R.style.CustomDialog)
                        .setTitle(R.string.ffmpeg_title)
                        .setMessage(R.string.ffmpeg)
                        .setNegativeButton(R.string.ok, null)
                        .create();*/
        }

        return null;
        //return super.onCreateDialog(id);
    }

    protected boolean isRunningInForeground() {
        ActivityManager manager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = manager.getRunningTasks(1);
        if (tasks.isEmpty()) {
            return false;
        }
        String topActivityName = tasks.get(0).topActivity.getPackageName();
        return topActivityName.equalsIgnoreCase(getPackageName());
    }
}
