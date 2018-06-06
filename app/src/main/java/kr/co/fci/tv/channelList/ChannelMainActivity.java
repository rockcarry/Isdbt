package kr.co.fci.tv.channelList;

/**
 * Created by live.kim on 2015-09-14.
 */

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.List;

import kr.co.fci.tv.MainActivity;
import kr.co.fci.tv.R;
import kr.co.fci.tv.TVEVENT;
import kr.co.fci.tv.buildOption;
import kr.co.fci.tv.saves.CommonStaticData;
import kr.co.fci.tv.setting.InputDialog;
import kr.co.fci.tv.util.TVlog;


public class ChannelMainActivity extends FragmentActivity {

    private String TAG="ChannelMainActivity ";
    private Fragment contentFragment;
    //private ChannelListFragment chListFragment;
    private FavoriteListFragment  favListFragment;
    Button button_channels;
    Button button_favorites;
    private Button ind_channels;
    private Button ind_favorites;
    ChannelListAdapter mChannelListAdapter;
    public static Context channelListContext;
    public static Activity CActivity;

    public static boolean isChannelSelect = true;

    //Runnable mRunnable;
    //Handler mHandler;

    /// Gesture
    private boolean chListHide = false;
    private float mTouchX,mTouchY;
    private float mLastMotionX = 0;
    ////

    public static ChannelMainActivity instance;
    public static ChannelMainActivity getInstance()
    {
        return instance;
    }

    public Handler ChannelMainActivityHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            TVEVENT event = TVEVENT.values()[msg.what];
            switch(event)
            {
                case E_CHANNEL_LIST_ENCRYPTED:
                {
                    //new InputDialog(instance, InputDialog.TYPE_SCRAMBLE_NOTI, null, null, null);
                    if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                        //MainActivity.getInstance().ll_scramble_msg.setVisibility(View.VISIBLE);
                    } else {
                        MainActivity.getInstance().ll_scramble_msg.setVisibility(View.VISIBLE);
                    }
                }
                break;

                case E_EWS_RECEIVED:
                {
                    new InputDialog(instance, InputDialog.TYPE_EWS_NOTIFY, msg.arg1, msg.arg2, msg.obj);
                }
                break;

                case E_HIDE_CHANNELLIST:
                {
                    TVlog.i(TAG , "=== E_HIDE_CHANNELLIST *********  ");
                    MainActivity.getInstance().isChannelListViewOn =false;
                    finish();
                    overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);  // live add
                }
                break;
            }
            super.handleMessage(msg);
        }};


    public void sendEvent(TVEVENT _Event) {
        int m;
        m = _Event.ordinal();
        Message msg = ChannelMainActivityHandler.obtainMessage(m);
        ChannelMainActivityHandler.sendMessage(msg);
    }

    public  void sendEvent(TVEVENT _Event, int _arg1, int _arg2, Object _obj) {
        int m;
        m = _Event.ordinal();
        Message msg = ChannelMainActivityHandler.obtainMessage(m);
        msg.arg1 = _arg1;
        msg.arg2 = _arg2;
        msg.obj = _obj;
        ChannelMainActivityHandler.sendMessage(msg);
    }

    @Override
    protected void onStart() {
        super.onStart();
       /* if (buildOption.USE_ANALYTICS_FUNCTION) {
            GoogleAnalytics.getInstance(this).reportActivityStart(this);
        }*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_main);
        instance = this;
        CActivity = ChannelMainActivity.this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        MainActivity.isMainActivity = true;

        final FrameLayout mContent = (FrameLayout) findViewById(R.id.content_frame);

        TVlog.i(TAG, "== onCreate ==");
        MainActivity.getInstance().isChannelListViewOn = true;
        CommonStaticData.channelMainActivityShow = true;   // justin add for dongle detached

        LinearLayout title_channels = (LinearLayout) findViewById(R.id.title_channels);
        title_channels.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.getInstance().isChannelListViewOn = false;
                CommonStaticData.channelMainActivityShow = false;
                finish();
            }
        });

        ImageButton btn_back_channels = (ImageButton) findViewById(R.id.btn_back_channels);
        btn_back_channels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.getInstance().isChannelListViewOn = false;
                CommonStaticData.channelMainActivityShow = false;
                finish();
            }
        });

        button_channels = (Button) findViewById(R.id.button_channels);
        ind_channels = (Button) findViewById(R.id.ind_channels);
        button_favorites = (Button) findViewById(R.id.button_favorites);
        ind_favorites = (Button) findViewById(R.id.ind_favorites);

        if (isChannelSelect == true) {
            button_channels.setBackgroundResource(R.drawable.tab_bg_selected);
            button_channels.setTextColor(getResources().getColor(R.color.white));
            ind_channels.setBackgroundColor(Color.WHITE);
            button_favorites.setBackgroundResource(R.drawable.tab_bg_unselected);
            button_favorites.setTextColor(getResources().getColor(R.color.dark_gray));
            ind_favorites.setBackgroundColor(getResources().getColor(R.color.blue5));
        } else {
            button_channels.setBackgroundResource(R.drawable.tab_bg_unselected);
            button_channels.setTextColor(getResources().getColor(R.color.dark_gray));
            ind_channels.setBackgroundColor(getResources().getColor(R.color.blue5));
            button_favorites.setBackgroundResource(R.drawable.tab_bg_selected);
            button_favorites.setTextColor(getResources().getColor(R.color.white));
            ind_favorites.setBackgroundColor(Color.WHITE);
        }



        button_channels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isChannelSelect = true;
                if (CommonStaticData.chListFragment == null) {
                    CommonStaticData.chListFragment = new ChannelListFragment();
                }
                switchContent(CommonStaticData.chListFragment, ChannelListFragment.ARG_ITEM_ID);
                button_channels.setTextColor(getResources().getColor(R.color.white));
                button_channels.setBackgroundResource(R.drawable.tab_bg_selected);
                button_favorites.setTextColor(getResources().getColor(R.color.dark_gray));
                button_favorites.setBackgroundResource(R.drawable.tab_bg_unselected);
                ind_channels.setBackgroundColor(Color.WHITE);
                ind_favorites.setBackgroundColor(getResources().getColor(R.color.blue5));
            }
        });


        button_favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isChannelSelect = false;
                if (CommonStaticData.favListFragment == null) {
                    CommonStaticData.favListFragment = new FavoriteListFragment();
                }
                switchContent(CommonStaticData.favListFragment, FavoriteListFragment.ARG_ITEM_ID);
                button_channels.setTextColor(getResources().getColor(R.color.dark_gray));
                button_channels.setBackgroundResource(R.drawable.tab_bg_unselected);
                button_favorites.setTextColor(getResources().getColor(R.color.white));
                button_favorites.setBackgroundResource(R.drawable.tab_bg_selected);
                ind_channels.setBackgroundColor(getResources().getColor(R.color.blue5));
                ind_favorites.setBackgroundColor(Color.WHITE);
            }
        });

        FragmentManager fragmentManager = getSupportFragmentManager();

        //finish if not loading completed.
//        if (CommonStaticData.loadingNow) {
//
//            TVlog.i(TAG, "==CommonStaticData.loadingNow finish==");
//            finish();
//        }
		/*
		 * This is called when orientation is changed.
		 */
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("content")) {
                String content = savedInstanceState.getString("content");
                if (content.equals(FavoriteListFragment.ARG_ITEM_ID)) {
                    if (fragmentManager.findFragmentByTag(FavoriteListFragment.ARG_ITEM_ID) != null) {
                        //setFragmentTitle(R.string.favorites);
                        contentFragment = fragmentManager.findFragmentByTag(FavoriteListFragment.ARG_ITEM_ID);
                        CommonStaticData.favListFragment = (FavoriteListFragment) contentFragment;
                    }
                }
            }
            if (fragmentManager.findFragmentByTag(ChannelListFragment.ARG_ITEM_ID) != null) {
                CommonStaticData.chListFragment = (ChannelListFragment) fragmentManager.findFragmentByTag(ChannelListFragment.ARG_ITEM_ID);
                contentFragment = CommonStaticData.chListFragment;
            }
        } else {
            if (isChannelSelect == true) {
                CommonStaticData.chListFragment = new ChannelListFragment();
                switchContent(CommonStaticData.chListFragment, ChannelListFragment.ARG_ITEM_ID);
            } else {
                CommonStaticData.favListFragment = new FavoriteListFragment();
                switchContent(CommonStaticData.favListFragment, FavoriteListFragment.ARG_ITEM_ID);
            }
        }

        channelListContext = this;

        TVlog.i(TAG, "== onCreate  end ==");
    }

    @Override
    public void onResume() {

        TVlog.i(TAG, "== onResume ==");
        CommonStaticData.channelMainActivityShow = true;   // justin add for dongle detached
        MainActivity.getInstance().isChannelListViewOn =true;
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        TVlog.i(TAG, "== onDestroy ==");
        MainActivity.isMainActivity = true;
        MainActivity.getInstance().isChannelListViewOn = false;
        CommonStaticData.channelMainActivityShow = false;   // justin add for dongle detached
        CommonStaticData.chListFragment = null;
        CommonStaticData.favListFragment = null;
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (contentFragment instanceof FavoriteListFragment) {
            outState.putString("content", FavoriteListFragment.ARG_ITEM_ID);
        } else {
            outState.putString("content", ChannelListFragment.ARG_ITEM_ID);
        }
        super.onSaveInstanceState(outState);
    }

    private  View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            float x_changed = mTouchX - event.getRawX();
            float y_changed = mTouchY - event.getRawY();

            float coefx = Math.abs(x_changed / y_changed);

            switch (event.getAction()){

                case MotionEvent.ACTION_DOWN:
                    // TVlog.i("Channellist"," Touch Down");
                    mTouchX = event.getRawX();
                    return true;

                case MotionEvent.ACTION_MOVE:

                    //mLastMotionX = event.getX();
                    mLastMotionX = event.getRawX();

                    if(coefx > 0.5 && (mLastMotionX < mTouchX) && chListHide == false) {
                        chListHide = true;
                        ChannelMainActivity.getInstance().sendEvent(TVEVENT.E_HIDE_CHANNELLIST);
                        return true;
                    } else {
                        return false;
                    }

                case MotionEvent.ACTION_UP:
                    // TVlog.i("Channellist"," Touch Up");
                    if(chListHide == true) {
                        chListHide = false;
                        //return true;
                    }
                    return false;
            }
            return false;
        }


    };
/*

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_favorites:
                //setFragmentTitle(R.string.favorites);
                favListFragment = new FavoriteListFragment();
                switchContent(favListFragment, FavoriteListFragment.ARG_ITEM_ID);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
*/

    public void switchContent(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        while (fragmentManager.popBackStackImmediate());

        if (fragment != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.content_frame, fragment, tag);
            //Only FavoriteListFragment is added to the back stack.
            if (!(fragment instanceof ChannelListFragment)) {
                transaction.addToBackStack(tag);
            }
            transaction.commit();
            contentFragment = fragment;
        }
    }

    /*protected void setFragmentTitle(int resourseId) {
        setTitle(resourseId);
        getActionBar().setTitle(resourseId);

    }*/

    /*
     * We call super.onBackPressed(); when the stack entry count is > 0. if it
     * is instanceof ProductListFragment or if the stack entry count is == 0, then
     * we finish the activity.
     * In other words, from ProductListFragment on back press it quits the app.
     */
    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            super.onBackPressed();
            ind_channels.setBackgroundColor(Color.WHITE);
            ind_favorites.setBackgroundColor(getResources().getColor(R.color.blue5));

        } else if (contentFragment instanceof ChannelListFragment
                || fm.getBackStackEntryCount() == 0) {
            MainActivity.getInstance().isChannelListViewOn =false;
            CommonStaticData.channelMainActivityShow = false;   // justin add for dongle detached
            finish();
        }
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
