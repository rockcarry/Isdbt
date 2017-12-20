package kr.co.fci.tv.channelList;

/**
 * Created by live.kim on 2015-09-14.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import java.util.ArrayList;

import kr.co.fci.tv.MainActivity;
import kr.co.fci.tv.R;
import kr.co.fci.tv.TVEVENT;
import kr.co.fci.tv.buildOption;
import kr.co.fci.tv.saves.CommonStaticData;
import kr.co.fci.tv.saves.SharedPreference;
import kr.co.fci.tv.tvSolution.FCI_TVi;
import kr.co.fci.tv.tvSolution.TVBridge;

import static kr.co.fci.tv.TVEVENT.E_CAPTION_CLEAR_NOTIFY;

public class FavoriteListFragment extends Fragment {

    public static final String ARG_ITEM_ID = "favorite_list";
    public int fav_id;

    private ListView favoriteList;
    SharedPreference sharedPreference;
    private ArrayList<Channel> favorites;

    /// Gesture
    private boolean chListHide = false;
    private float mTouchX,mTouchY;
    private float mLastMotionX = 0;
    ////

    Activity activity;
    ChannelListAdapter channelListAdapter;

    public static Cursor mCursor = null;

    public static FavoriteListFragment instance;
    public static FavoriteListFragment getInstance()
    {
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_channel_list, container, false);
        // Get favorite items from SharedPreferences.
        sharedPreference = new SharedPreference();
        favorites = sharedPreference.getFavorites(activity);
        mCursor = MainActivity.getCursor();

        favoriteList = (ListView) view.findViewById(R.id.list_channel);
        favoriteList.setOnTouchListener(mTouchListener);

        if (favorites == null) {
            MaterialDialog.Builder builder;
            MaterialDialog alertDialog = null;
            Context mContext = getContext();
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.custom_dialog, null);

            ImageView image1 = (ImageView) layout.findViewById(R.id.image1);
            image1.setImageResource(R.drawable.ic_info_outline_gray_48dp);
            image1.setPadding(24,24,0,0);

            TextView dialog_tilte = (TextView) layout.findViewById(R.id.dialog_title);
            dialog_tilte.setText(R.string.no_favorites_items);
            dialog_tilte.setTextColor(getResources().getColor(R.color.black));
            dialog_tilte.setPadding(0,10,0,0);
            dialog_tilte.setPaintFlags(dialog_tilte.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);

            TextView text1 = (TextView) layout.findViewById(R.id.text1);
            text1.setText(R.string.no_favorites_msg1);
            text1.setTextColor(getResources().getColor(R.color.black));
            text1.setPadding(15,0,0,0);

            ImageView image2 = (ImageView) layout.findViewById(R.id.image2);
            image2.setImageResource(R.drawable.favorite_cancel);

            TextView text2 = (TextView) layout.findViewById(R.id.text2);
            text2.setText(R.string.no_favorites_msg2);
            text2.setTextColor(getResources().getColor(R.color.black));
            text2.setPadding(0,0,20,0);

            builder = new MaterialDialog.Builder(mContext);
            builder.theme(Theme.LIGHT);
            builder.customView(layout, true);
            builder.positiveText("OK");
            builder.positiveColor(getResources().getColor(R.color.blue3));

            alertDialog = builder.build();

            /*alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });*/
            alertDialog.getWindow().setGravity(Gravity.CENTER);
            //alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_box);
            alertDialog.show();

            View decorView = alertDialog.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE;    // justin
            decorView.setSystemUiVisibility(uiOptions);

        } else {

            if (favorites.size() == 0) {
                MaterialDialog.Builder builder;
                MaterialDialog alertDialog = null;
                Context mContext = getContext();
                inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.custom_dialog, null);

                ImageView image1 = (ImageView) layout.findViewById(R.id.image1);
                image1.setImageResource(R.drawable.ic_info_outline_gray_48dp);
                image1.setPadding(24,24,0,0);

                TextView dialog_tilte = (TextView) layout.findViewById(R.id.dialog_title);
                dialog_tilte.setText(R.string.no_favorites_items);
                dialog_tilte.setTextColor(getResources().getColor(R.color.black));
                dialog_tilte.setPadding(0,10,0,0);
                dialog_tilte.setPaintFlags(dialog_tilte.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);

                TextView text1 = (TextView) layout.findViewById(R.id.text1);
                text1.setText(R.string.no_favorites_msg1);
                text1.setTextColor(getResources().getColor(R.color.black));
                text1.setPadding(15,0,0,0);

                ImageView image2 = (ImageView) layout.findViewById(R.id.image2);
                image2.setImageResource(R.drawable.favorite_cancel);

                TextView text2 = (TextView) layout.findViewById(R.id.text2);
                text2.setText(R.string.no_favorites_msg2);
                text2.setTextColor(getResources().getColor(R.color.black));
                text2.setPadding(0,0,20,0);

                builder = new MaterialDialog.Builder(mContext);
                builder.theme(Theme.LIGHT);
                builder.customView(layout, true);
                builder.positiveText("OK");
                builder.positiveColor(getResources().getColor(R.color.blue3));

                alertDialog = builder.build();

            /*alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });*/

                //alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_box);
                alertDialog.getWindow().setGravity(Gravity.CENTER);
                alertDialog.show();

                View decorView = alertDialog.getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE;    // justin
                decorView.setSystemUiVisibility(uiOptions);
            }

            // favoriteList = (ListView) view.findViewById(R.id.list_channel);
            if (favorites != null) {

                channelListAdapter = new ChannelListAdapter(activity, mCursor, null, favorites);

                favoriteList.setAdapter(channelListAdapter);

                favoriteList.setOnItemClickListener(new OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {

                        fav_id = favorites.get(position).getindex();

                        /*int[] cur_info = FCI_TVi.GetPairNSegInfoOfCHIndex(CommonStaticData.lastCH);
                        int isPaired = 0;
                        int pairedIndex = cur_info[0];
                        int mainIndex = cur_info[3];
                        int isAudioOnly = cur_info[5];

                        int[] last_info = FCI_TVi.GetPairNSegInfoOfCHIndex(CommonStaticData.lastCH);
                        int last_mainIndex = last_info[3];

                        if (pairedIndex == fav_id) {
                            isPaired = 1;
                        }
                        else {
                            isPaired = 0;
                        }

                        if (isAudioOnly == 1) {
                            CommonStaticData.isAudioChannel = true;
                        } else {
                            CommonStaticData.isAudioChannel = false;
                        }*/

                        /*if (mainIndex != last_mainIndex) {
                            MainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_LIST_AV_STARTED);
                        }*/

                            MainActivity.getInstance().sendEvent(E_CAPTION_CLEAR_NOTIFY);
                            MainActivity.getInstance().sendEvent(TVEVENT.E_SUPERIMPOSE_CLEAR_NOTIFY);

                        /*if (isPaired == 0 && CommonStaticData.lastCH != fav_id) {
                            MainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_LIST_AV_STARTED);
                            MainActivity.getInstance().sendEvent(E_CAPTION_CLEAR_NOTIFY);
                            MainActivity.getInstance().sendEvent(TVEVENT.E_SUPERIMPOSE_CLEAR_NOTIFY);
                        }*/

                        if (CommonStaticData.lastCH != fav_id) {
                            // live add
                            if (MainActivity.getInstance().sv != null && MainActivity.getInstance().sv.isShown()) {
                                MainActivity.getInstance().sv.setBackgroundColor(getResources().getColor(R.color.transparent));
                            }
                            if(buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
                                if (MainActivity.getInstance().svSub != null && MainActivity.getInstance().svSub.isShown()) {
                                    MainActivity.getInstance().svSub.setBackgroundColor(getResources().getColor(R.color.transparent));
                                }
                            } else if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_MEDIACODEC &&
                                    (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN ||
                                            buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                                            buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE)) {
                                if (MainActivity.getInstance().svSub != null && MainActivity.getInstance().svSub.isShown()) {
                                    MainActivity.getInstance().svSub.setBackgroundColor(getResources().getColor(R.color.transparent));
                                }
                            }
                            if (MainActivity.getInstance().ll_noChannel.getVisibility() == View.VISIBLE) {
                                MainActivity.getInstance().ll_noChannel.setVisibility(View.INVISIBLE);
                            }
                            if (MainActivity.getInstance().ll_noSignal.getVisibility() == View.VISIBLE) {
                                MainActivity.getInstance().ll_noSignal.setVisibility(View.INVISIBLE);
                            }
                            /*if (MainActivity.getInstance().ll_scramble_msg.getVisibility() == View.VISIBLE) {
                                MainActivity.getInstance().ll_scramble_msg.setVisibility(View.INVISIBLE);
                            }*/

                            if (MainActivity.ll_age_limit.getVisibility() == View.VISIBLE) {
                                MainActivity.ll_age_limit.setVisibility(View.INVISIBLE);
                            }
                            TVBridge.serviceID_start(fav_id);
                        }

                        int pos = favoriteList.getFirstVisiblePosition();
                        favoriteList.setAdapter(channelListAdapter);  //text color change when item is clicked in favorites list
                        favoriteList.setSelection(pos);  //selected item is located to top
                    }
                });

                timerDelayRunForScroll(200);
            }
        }
        return view;
    }

    public void timerDelayRunForScroll(int time) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try{
                    int h1 = favoriteList.getHeight();
                    favoriteList.smoothScrollToPositionFromTop(CommonStaticData.lastCH, h1/2, 100);
                }catch (Exception e){}
            }
        },time);
    }

    public void refresh()
    {
        if (favorites != null) {
            favorites = null;
        }
        favorites = sharedPreference.getFavorites(activity);
        if (favorites != null) {
            mCursor = MainActivity.getCursor();
            if (channelListAdapter != null) {
                channelListAdapter = null;
            }
            channelListAdapter = new ChannelListAdapter(activity, mCursor, null, favorites);

            favoriteList.setAdapter(channelListAdapter);
            favoriteList.invalidate();
        }
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

                    mLastMotionX = event.getX();

                    /*if(coefx > 1 && (mLastMotionX < mTouchX) && chListHide == false) {
                        chListHide = true;
                        ChannelMainActivity.getInstance().sendEvent(TVEVENT.E_HIDE_CHANNELLIST);
                    return true;
                    } else {
                        return false;
                    }*/

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

    public void showAlert(String title, String message) {
        if (activity != null && !activity.isFinishing()) {
            if (Build.VERSION.SDK_INT <= 19) {
                AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
                alertDialog.setTitle(title);
                alertDialog.setMessage(message);

                alertDialog.setCancelable(false);

                // setting OK Button
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomDialog).create();
                alertDialog.setTitle(title);
                alertDialog.setMessage(message);

                alertDialog.setCancelable(false);

                // setting OK Button
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}