package kr.co.fci.tv.channelList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import java.util.ArrayList;
import java.util.List;

import kr.co.fci.tv.MainActivity;
import kr.co.fci.tv.R;
import kr.co.fci.tv.TVEVENT;
import kr.co.fci.tv.buildOption;
import kr.co.fci.tv.saves.CommonStaticData;
import kr.co.fci.tv.saves.SharedPreference;
import kr.co.fci.tv.saves.TVProgram;
import kr.co.fci.tv.tvSolution.TVBridge;
import kr.co.fci.tv.util.TVlog;

/**
 * Created by live.kim on 2015-09-14.
 */
//public class ChannelListFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
public class ChannelListFragment extends Fragment implements AdapterView.OnItemClickListener {

    public static final String ARG_ITEM_ID = "channel_list";

    Context mContext;

    Activity activity;
    private ListView channelListView;
    private List<Channel> channels = null;
    private ChannelListAdapter channelListAdapter;

    SharedPreference sharedPreference;
    private Uri mUri;
    private Cursor mCursor = null;
    private final static int MAX_FAV_ITEM = 200;

    int id;
    String names;

    Context context;

    private int mChannelType = 0;
    private int mFree = 1;
    private int mRemoteKey = 0;
    private int mSvcNumber = 0;
    private int mFreqKHz = 0;

    private boolean isDialogShown;

    /// Gesture
    private boolean chListHide = false;
    private float mTouchX,mTouchY;
    private float mLastMotionX = 0;
    ////


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        sharedPreference = new SharedPreference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_channel_list, container, false);
        findViewsById(view);

        setChannels();

        channelListAdapter = new ChannelListAdapter(activity, mCursor, channels, null);
        channelListView.setAdapter(channelListAdapter);
        channelListView.setOnTouchListener(mTouchListener);
        channelListView.setOnItemClickListener(this);


        if (channels == null) {
            if (!isDialogShown) {
                MaterialDialog.Builder builder;
                MaterialDialog alertDialog = null;
                Context mContext = getContext();
                inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.custom_dialog, null);

                ImageView image1 = (ImageView) layout.findViewById(R.id.image1);
                image1.setImageResource(R.drawable.ic_info_outline_gray_48dp);
                image1.setPadding(24, 24, 0, 0);

                TextView dialog_tilte = (TextView) layout.findViewById(R.id.dialog_title);
                dialog_tilte.setText(R.string.no_channels_items);
                dialog_tilte.setTextColor(getResources().getColor(R.color.black));
                dialog_tilte.setPadding(0, 10, 0, 0);

                dialog_tilte.setPaintFlags(dialog_tilte.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);

                TextView text1 = (TextView) layout.findViewById(R.id.text1);
                text1.setText(R.string.no_channels_msg1);
                text1.setTextColor(getResources().getColor(R.color.black));
                text1.setPadding(20, 0, 0, 0);

                ImageView image2 = (ImageView) layout.findViewById(R.id.image2);
                image2.setImageResource(R.drawable.ic_search_grey600_48dp);

                TextView text2 = (TextView) layout.findViewById(R.id.text2);
                text2.setText(R.string.no_channels_msg2);
                text2.setTextColor(getResources().getColor(R.color.black));
                text2.setPadding(0, 0, 20, 0);


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
                alertDialog.show();

                Window window = alertDialog.getWindow();
                if (MainActivity.dpiName.equalsIgnoreCase("hdpi")) {
                    window.setLayout(640, 300);
                } else if (MainActivity.dpiName.equalsIgnoreCase("xhdpi")) {
                    window.setLayout(840, 400);
                }
                isDialogShown = true;
            }

        } else {
            if (channels.size() <= 0) {
                if (!isDialogShown) {
                    MaterialDialog.Builder builder;
                    MaterialDialog alertDialog = null;
                    Context mContext = getContext();
                    inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View layout = inflater.inflate(R.layout.custom_dialog, null);

                    ImageView image1 = (ImageView) layout.findViewById(R.id.image1);
                    image1.setImageResource(R.drawable.ic_info_outline_gray_48dp);
                    image1.setPadding(24, 24, 0, 0);

                    TextView dialog_tilte = (TextView) layout.findViewById(R.id.dialog_title);
                    dialog_tilte.setText(R.string.no_channels_items);
                    dialog_tilte.setTextColor(getResources().getColor(R.color.black));
                    dialog_tilte.setPadding(0, 10, 0, 0);
                    dialog_tilte.setPaintFlags(dialog_tilte.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);

                    TextView text1 = (TextView) layout.findViewById(R.id.text1);
                    text1.setText(R.string.no_channels_msg1);
                    text1.setTextColor(getResources().getColor(R.color.black));
                    text1.setPadding(20, 0, 0, 0);

                    ImageView image2 = (ImageView) layout.findViewById(R.id.image2);
                    image2.setImageResource(R.drawable.ic_search_grey600_48dp);

                    TextView text2 = (TextView) layout.findViewById(R.id.text2);
                    text2.setText(R.string.no_channels_msg2);
                    text2.setTextColor(getResources().getColor(R.color.black));
                    text2.setPadding(0,0,20,0);

                    builder = new MaterialDialog.Builder(mContext);
                    builder.theme(Theme.LIGHT);
                    builder.customView(layout, true);
                    builder.positiveText("OK");
                    builder.positiveColor(getResources().getColor(R.color.blue3));

                    //alertDialog = new AlertDialog.Builder(builder.getContext(), R.style.CustomDialog).create();
                    alertDialog = builder.build();


                    /*alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    //activity.finish();
                                    //getFragmentManager().popBackStackImmediate();

                                }
                            });*/
                    alertDialog.getWindow().setGravity(Gravity.CENTER);
                    alertDialog.show();

                    Window window = alertDialog.getWindow();
                    if (MainActivity.dpiName.equalsIgnoreCase("hdpi")) {
                        window.setLayout(640, 300);
                    } else if (MainActivity.dpiName.equalsIgnoreCase("xhdpi")) {
                        window.setLayout(840, 400);
                    }

                    View decorView = alertDialog.getWindow().getDecorView();
                    int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE;    // justin
                    decorView.setSystemUiVisibility(uiOptions);
                    isDialogShown = true;
                }
            }
            else { //check favorite channels
                ArrayList<Channel> favorites;
                Channel favChannel;
                favorites = sharedPreference.getFavorites(activity);

                int favNum = 0;

                if (favorites != null) {
                    favNum = favorites.size();
                }

                if (favNum > 0) {
                    for (int i = 0; i < favNum && favNum < MAX_FAV_ITEM; i++) {
                        int j;
                        favChannel = favorites.get(i);
                        sharedPreference.removeFavorite(activity, favChannel);
                        for (j=0; j < channels.size(); j++) {
                            if ((favChannel.getFreqKHz() == channels.get(j).getFreqKHz())
                                    && (favChannel.getRemoteKey() == channels.get(j).getRemoteKey())
                                    && (favChannel.getSvcID() == channels.get(j).getSvcID())) {
                                sharedPreference.addFavorite(activity, channels.get(j));
                                break;
                            }
                        }
                    }
                }

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
                    int h1 = channelListView.getHeight();
                    channelListView.smoothScrollToPositionFromTop(CommonStaticData.lastCH, h1/2, 100);
                }catch (Exception e){}
            }
        },time);
    }

    public void setChannels() {

        names = new String();

        Intent intent = getActivity().getIntent();
        mUri = intent.getData();
        if (mUri == null) {
            mUri = TVProgram.Programs.CONTENT_URI;
            intent.setData(mUri);

        }

        mCursor = MainActivity.getCursor();
        if (mCursor != null && mCursor.getCount() > 0) {
            mCursor.moveToFirst();
            channels = new ArrayList<Channel>();

            for (int index = 0; index < mCursor.getCount() && index < CommonStaticData.scanCHnum; index++) {
                names = mCursor.getString(CommonStaticData.COLUMN_INDEX_SERVICE_NAME);
                mChannelType =  mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV);
                mFree = mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_FREE);
                mRemoteKey = mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_REMOTE_KEY);
                mSvcNumber = mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_NUMBER);
                mFreqKHz = mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_FREQ);
                TVlog.i("Loading CHANNEL >>> ",index + " " + names + " " + mSvcNumber + " " + mChannelType + " " + mFreqKHz);

                channels.add(new Channel(index, names, mChannelType, mFree, mRemoteKey, mSvcNumber, mFreqKHz));
                mCursor.moveToPosition(index + 1);
            }
            mCursor.moveToPosition(CommonStaticData.lastCH);
        }
    }

    public void UpdateChannelList() {
        if (channels != null && channels.size() > 0) {
            ArrayList<Channel> favorites;
            Channel favChannel;
            favorites = sharedPreference.getFavorites(activity);

            int favNum = 0;

            if (favorites != null) {
                favNum = favorites.size();
            }

            if (!channels.isEmpty()) {
                channels.clear();
            }
            setChannels();
            channelListAdapter = new ChannelListAdapter(activity, mCursor, channels, null);
            channelListView.setAdapter(channelListAdapter);
            channelListAdapter.notifyDataSetChanged();

            //check favorite channels
            if (favNum > 0) {
                for (int i = 0; i < favNum && favNum < MAX_FAV_ITEM; i++) {
                    int j;
                    favChannel = favorites.get(i);
                    sharedPreference.removeFavorite(activity, favChannel);
                    for (j=0; j < channels.size(); j++) {
                        if ((favChannel.getFreqKHz() == channels.get(j).getFreqKHz())
                                && (favChannel.getRemoteKey() == channels.get(j).getRemoteKey())
                                && (favChannel.getSvcID() == channels.get(j).getSvcID())) {
                            sharedPreference.addFavorite(activity, channels.get(j));
                            break;
                        }
                    }
                }
                //fav. list refresh
                if (CommonStaticData.favListFragment != null) {
                    CommonStaticData.favListFragment.refresh();
                }
            }
        }
    }

    private void findViewsById(View view) {
        channelListView = (ListView) view.findViewById(R.id.list_channel);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        // live add

        MainActivity.isMainActivity = true;

        if (MainActivity.getInstance().sv != null) {
            MainActivity.getInstance().sv.setBackgroundColor(getResources().getColor(R.color.black));
        }
        if(buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
            if (MainActivity.getInstance().svSub != null) {
                MainActivity.getInstance().svSub.setBackgroundColor(getResources().getColor(R.color.black));
            }
        }
        if (MainActivity.getInstance().ll_noSignal.getVisibility() == View.VISIBLE) {
            MainActivity.getInstance().ll_noSignal.setVisibility(View.INVISIBLE);
        }
        if (MainActivity.getInstance().ll_scramble_msg.getVisibility() == View.VISIBLE) {
            MainActivity.getInstance().ll_scramble_msg.setVisibility(View.INVISIBLE);
        }

        if (MainActivity.ll_age_limit.getVisibility() == View.VISIBLE) {
            MainActivity.ll_age_limit.setVisibility(View.INVISIBLE);
        }

        MainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_LIST_AV_STARTED);

        TVBridge.serviceID_start(position);
        int pos = channelListView.getFirstVisiblePosition();
        channelListView.setAdapter(channelListAdapter);  //text color change when item is clicked in channel list
        channelListView.setSelection(pos);  //selected item is located to top

        // live add
        if (buildOption.GUI_STYLE == 2 || buildOption.GUI_STYLE == 3) {
            if(chListHide == false) {
                chListHide = true;
                ChannelMainActivity.getInstance().sendEvent(TVEVENT.E_HIDE_CHANNELLIST);
            }
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
                android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(activity).create();
                alertDialog.setTitle(title);
                alertDialog.setMessage(message);
                alertDialog.setCancelable(false);

                // setting OK Button
                alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                //activity.finish();
                                //getFragmentManager().popBackStackImmediate();

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
                                //activity.finish();
                                //getFragmentManager().popBackStackImmediate();

                            }
                        });
                alertDialog.show();
            }

        }
    }

    @Override
    public void onResume() {
        if (mCursor != null) {
            int pos = mCursor.getPosition();
            channelListView.setAdapter(channelListAdapter);  //text color change when item is clicked in channel list
            channelListView.setSelection(pos);  //selected item is located to top
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}
