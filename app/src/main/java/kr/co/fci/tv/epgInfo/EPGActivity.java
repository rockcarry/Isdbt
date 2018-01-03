package kr.co.fci.tv.epgInfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;

import kr.co.fci.tv.MainActivity;
import kr.co.fci.tv.R;
import kr.co.fci.tv.TVEVENT;
import kr.co.fci.tv.buildOption;
import kr.co.fci.tv.saves.CommonStaticData;
import kr.co.fci.tv.saves.TVProgram;
import kr.co.fci.tv.tvSolution.FCI_TVi;
import kr.co.fci.tv.tvSolution.TVBridge;
import kr.co.fci.tv.util.TVlog;

public class EPGActivity extends Activity {
    public static final String TAG= "EPG_Activity";

    public static Context epgContext;
    public final int EPG_UPDATE_PERIOD = 1000 * 15; //15sec
    public final int EPG_UPDATE_TYPE_PERIODIC = 0xFF;
    public final int EPG_UPDATE_TYPE_CALLBACK_PF = 0;
    public final int EPG_UPDATE_TYPE_CALLBACK_SCHEDULE = 1;
    private String curTsDate = "";
    private String curSysDate = "";

    private ExpandableListView EPGListView;
    private ArrayList<String> mGroupList = null;
    private ArrayList<String> mChildList = null;
    private HashSet<String> mGroupList_hash = null;
    public ArrayList<String> list = null;

    private HashSet<Integer> positionHeaderVisible = null;
    private ArrayList<Integer> position = null;

    int prevSelectedIndex = 0;
    int curSelectedIndex = 0;

    private EPGSimpleArrayAdapter epgSimpleArrayAdapter;

    private int mCurWeekDay;
    private int mLastEPGCount = 0;
    private int mPrePosition;
    private Cursor mCursor = null;
    private Uri mUri;
    private Intent mIntent;
    private int mChannelIndex = -1;
    private static int mCountNoTitle = 0;

    public  TextView tv_phyChNo;
    public  TextView tv_remoteNo;
    public  TextView mTopBarText;

    private ImageView iv_calendar;
    public  TextView tv_curDate;

    private CountDownTimer newTimer;

    public  ScrollView scrollButton;
    private LinearLayout ll_dateBtn;
    public  Button mButton[] = null;

    public  TextView curEpgPosition;
    private TextView lastEPGPosition;
    private int isExpandedGroup = 0;

    public static Activity EActivity;

    public static EPGActivity instance;
    public static EPGActivity getInstance()
    {
        return instance;
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*
        if (buildOption.USE_ANALYTICS_FUNCTION) {
            GoogleAnalytics.getInstance(this).reportActivityStart(this);
        }*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epg);

        instance = this;
        EActivity = EPGActivity.this;
        CommonStaticData.epgActivityShow = true;   // justin add for dongle detached

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        MainActivity.isMainActivity = true;

        mGroupList = new ArrayList<String>();
        mChildList = new ArrayList<String>();
        mGroupList_hash = new HashSet<String>();
        positionHeaderVisible = new HashSet<Integer>();
        list = new ArrayList<String>();
        position = new ArrayList<Integer>();
        mButton = new Button[list.size()];

        curEpgPosition = (TextView) findViewById(R.id.curEpgPosition);
        lastEPGPosition = (TextView) findViewById(R.id.lastEpgPosition);

        init();

        LinearLayout title_epg = (LinearLayout) findViewById(R.id.title_epg);
        title_epg.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.getInstance().removeEvent(TVEVENT.E_EPG_UPDATE);
                finish();
            }
        });

        ImageButton btn_back_epg = (ImageButton) findViewById(R.id.btn_back_epg);
        btn_back_epg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.getInstance().removeEvent(TVEVENT.E_EPG_UPDATE);
                CommonStaticData.epgActivityShow = false;
                finish();
            }
        });

        Button button_extra = (Button) findViewById(R.id.button_extra);
        button_extra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.getInstance().removeEvent(TVEVENT.E_EPG_UPDATE);
                finish();
            }
        });

        epgContext = this;

        /*
        if (buildOption.USE_ANALYTICS_FUNCTION) {
            // google analytics
            Tracker mTracker = ((AnalyticsApplication)getApplication()).getTracker(AnalyticsApplication.TrackerName.APP_TRACKER);
            mTracker.setScreenName("EPG");
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        CommonStaticData.epgActivityShow = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        /*
        if (buildOption.USE_ANALYTICS_FUNCTION) {
            GoogleAnalytics.getInstance(this).reportActivityStop(this);
        }*/
    }

    private void init() {
        tv_phyChNo = (TextView) findViewById(R.id.tv_phyChNo);
        tv_phyChNo.setTypeface(MainActivity.getInstance().tf);
        tv_phyChNo.setTextSize(18);

        if (buildOption.VIEW_PHY_CH) {
            tv_phyChNo.setVisibility(View.VISIBLE);
        } else {
            tv_phyChNo.setVisibility(View.GONE);
        }

        tv_remoteNo = (TextView) findViewById(R.id.tv_remoteNo);
        mTopBarText = (TextView) findViewById(R.id.epg_program);
        mTopBarText.setSelected(true);

        iv_calendar = (ImageView) findViewById(R.id.iv_calendar);
        tv_curDate = (TextView) findViewById(R.id.tv_curDate);

        EPGListView = (ExpandableListView) findViewById(R.id.programList);

        mIntent = getIntent();

        mUri = mIntent.getData();
        if (mUri == null) {
            mUri = TVProgram.Programs.CONTENT_URI;
            mIntent.setData(mUri);
        }

        mChannelIndex = TVBridge.getCurrentChannel();

        if (mCursor == null || mCursor.isClosed()) {
            mCursor = getContentResolver().query(mUri, CommonStaticData.PROJECTION, null, null, TVProgram.Programs.SERVICEID);
        }

        if (mCursor.getCount() > 0) {
            if (mChannelIndex != 0) {
                mCursor.moveToPosition(mChannelIndex);
            } else {
                mCursor.moveToFirst();
            }
            int freq = Integer.parseInt(mCursor.getString(CommonStaticData.COLUMN_INDEX_SERVICE_FREQ));
            TVlog.i(TAG, " >>>>> current freq = " + freq);

            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                int channelNo = 13 + (int)((freq-473143)/6000);
                tv_phyChNo.setText(channelNo + "ch");
            } else {
                if (buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA
                        || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_ONESEG
                        || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                    // for Sri Lanka
                    int channelNo = 13 + (int)((freq-474000)/8000);
                    tv_phyChNo.setText(channelNo + "ch");
                } else {
                    int channelNo = 14 + (int)((freq-473143)/6000);
                    tv_phyChNo.setText(channelNo + "ch");
                }
            }

            String channelName = mCursor.getString(CommonStaticData.COLUMN_INDEX_SERVICE_NAME);
            String[] split_channelName = channelName.split(" ");

            // live modify 20170104
            tv_remoteNo.setText(split_channelName[0]);
            String str = "";
            for (int i = 1; i < split_channelName.length; i++) {
                str += split_channelName[i];
                if (i < split_channelName.length - 1) {
                    str += " ";
                }
            }
            mTopBarText.setText(str);
            //

        } else {
            tv_phyChNo.setText("- -ch");
            tv_remoteNo.setText("- - -");
            mTopBarText.setText(R.string.no_channel_title);
        }

        if (buildOption.USE_REF_TIME) {
            newTimer = new CountDownTimer(1000000000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy, h:mm a");
                    tv_curDate.setText(sdf.format(cal.getTime()));
                }
                @Override
                public void onFinish() {

                }
            };
            newTimer.start();
        } else {
            newTimer = new CountDownTimer(100000000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mm a");

                    int mCurWeekDay = 0;
                    int date[] = FCI_TVi.GetTSNetTime();
                    mCurWeekDay = MainActivity.DayFun(date[0], date[1], date[2]);
                    int curYear = date[0];
                    int curMonth = date[1];
                    String monthStr = "";
                    if (curMonth == 1) {
                        monthStr = "Jan";
                    }
                    else if (curMonth == 2) {
                        monthStr = "Feb";
                    }
                    else if (curMonth == 3) {
                        monthStr = "Mar";
                    }
                    else if (curMonth == 4) {
                        monthStr = "Apr";
                    }
                    else if (curMonth == 5) {
                        monthStr = "May";
                    }
                    else if (curMonth == 6) {
                        monthStr = "Jun";
                    }
                    else if (curMonth == 7) {
                        monthStr = "Jul";
                    }
                    else if (curMonth == 8) {
                        monthStr = "Aug";
                    }
                    else if (curMonth == 9) {
                        monthStr = "Sep";
                    }
                    else if (curMonth == 10) {
                        monthStr = "Oct";
                    }
                    else if (curMonth == 11) {
                        monthStr = "Nov";
                    }
                    else if (curMonth == 12) {
                        monthStr = "Dec";
                    }
                    int curDay, curHour, curMin = 0;
                    curDay = date[2];
                    curHour = date[3];
                    curMin = date[4];
                    int dispCurHour = 0;
                    String curAMPM = "";
                    String curPrecede = "";
                    dispCurHour = curHour;

                    if (dispCurHour > 12) {
                        dispCurHour = dispCurHour - 12;
                        curAMPM = "PM";
                    } else if (dispCurHour == 0) {
                        dispCurHour = 12;
                        curAMPM = "AM";
                    } else if (dispCurHour == 12)
                        curAMPM = "PM";
                    else {
                        curAMPM = "AM";
                    }

                    if (curMin < 10) {
                        curPrecede = "0";
                    } else {
                        curPrecede = "";
                    }

                    if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                        curTsDate = (curYear+getApplicationContext().getString(R.string.year)+
                                +curMonth+getApplicationContext().getString(R.string.month)
                                +curDay+getApplicationContext().getString(R.string.day)+", "+dispCurHour+":"+curPrecede+curMin+" "+curAMPM);
                        //curTsDate = (monthStr+" "+curDay+", "+curYear+", "+curHour+":"+curPrecede+curMin);
                        curSysDate = sdf.format((cal.getTime()));
                    } else {
                        curTsDate = (monthStr+" "+curDay+", "+curYear+", "+dispCurHour+":"+curPrecede+curMin+" "+curAMPM);
                        //curTsDate = (monthStr+" "+curDay+", "+curYear+", "+curHour+":"+curPrecede+curMin);
                        curSysDate = sdf.format((cal.getTime()));
                        /*
                        if (curTsDate != null) {
                            tv_curDate.setText(curTsDate);
                        } else {
                            tv_curDate.setText(String.format(curSysDate));
                        }*/
                    }

                    if (curYear==0 || curMonth==0 || curDay==0) {
                        tv_curDate.setText(curSysDate);
                    } else {
                        tv_curDate.setText(curTsDate);
                    }
                }

                @Override
                public void onFinish() {
                }
            };
            newTimer.start();
        }

        EPGListView.setAdapter(new EPGSimpleArrayAdapter(this, mGroupList, mChildList));

        EPGListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (curEpgPosition == null) {
                    curEpgPosition.setText("-");
                } else {
                    curEpgPosition.setText(Integer.toString(groupPosition+1));
                }
                return false;
            }
        });

        EPGListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (mPrePosition != groupPosition) {
                    EPGListView.collapseGroup(mPrePosition);
                    mPrePosition = groupPosition;
                }
            }
        });

        UpdateEPGList(EPG_UPDATE_TYPE_PERIODIC);

        //UpdateEPGTitle();  //live

        MainActivity.getInstance().postEvent(TVEVENT.E_EPG_UPDATE, EPG_UPDATE_PERIOD, EPG_UPDATE_TYPE_PERIODIC);  // 15 sec
        MainActivity.getInstance().postEvent(TVEVENT.E_EPGTITLE_UPDATE, 0);  //live
    }

    //live
    public void UpdateEPGTitle() {
        TVlog.i(TAG, " >>>>>>>>>>>>>>>>> UpdateEPGTitle() is called");
        //TextView mTopBarText = (TextView) findViewById(R.id.epg_program);
        mCursor = MainActivity.getCursor();
        if (CommonStaticData.scanCHnum > 0 && mCursor.getCount() > 0) {
            //mCursor.moveToFirst();
            //mCursor = getContentResolver().query(mUri, CommonStaticData.PROJECTION, TVProgram.Programs.TYPE + "=?", CommonStaticData.selectionArgsTV, null);
            mCursor.moveToPosition(CommonStaticData.lastCH);

            int freq = Integer.parseInt(mCursor.getString(CommonStaticData.COLUMN_INDEX_SERVICE_FREQ));
            TVlog.i(TAG, " >>>>> current freq = " + freq);

            if (buildOption.VIEW_PHY_CH) {
                tv_phyChNo.setVisibility(View.VISIBLE);
            } else {
                tv_phyChNo.setVisibility(View.GONE);
            }

            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                int channelNo = 13 + (int)((freq-473143)/6000);
                tv_phyChNo.setText(channelNo+"ch");
            } else {
                if (buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA
                        || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_ONESEG
                        || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                    // for Sri Lanka
                    int channelNo = 13 + (int)((freq-474000)/8000);
                    tv_phyChNo.setText(channelNo + "ch");
                } else {
                    int channelNo = 14 + (int)((freq-473143)/6000);
                    tv_phyChNo.setText(channelNo + "ch");
                }
            }

            String channelName = mCursor.getString(CommonStaticData.COLUMN_INDEX_SERVICE_NAME);
            String[] split_channelName = channelName.split(" ");

            // live modify 20170104
            tv_remoteNo.setText(split_channelName[0]);
            String str = "";
            for (int i = 1; i < split_channelName.length; i++) {
                str += split_channelName[i];
                if (i < split_channelName.length - 1) {
                    str += " ";
                }
            }
            mTopBarText.setText(str);
            //

            //mTopBarText.setText(mCursor.getString(CommonStaticData.COLUMN_INDEX_SERVICE_NAME));
        }
    }

    private final int EPG_TIME_NOT_DEFINED = 0xFF;
    private final String EPG_TIME_STR_NOT_DEFINED = "??:??";
    private final String EPG_TIME_STR_FORMAT_SPECIFIER = "%02d:%02d";
    private final String EPG_TIME_STR_SEPARATOR = "-";
    private final String EPG_TIME_STR_END_INTERVAL_SPACE = "   ";

    public void UpdateEPGList(int _epgType) {

        // live add
        mCursor = MainActivity.getCursor();
        //
        int mScrollY = 0;
        int pos = EPGListView.getFirstVisiblePosition();
        View v = EPGListView.getChildAt(0);
        if (v != null) {
            mScrollY = (int) v.getTop();
        } else {
            mScrollY = 0;
        }
        //

        TVlog.i(TAG, " >>>>> UpdateEPGList!!!!!");

        TextView noepg_text = (TextView)findViewById(R.id.no_egp_msg);

        if (buildOption.VIEW_PHY_CH) {
            tv_phyChNo.setVisibility(View.VISIBLE);
        } else {
            tv_phyChNo.setVisibility(View.GONE);
        }

        if (mCursor != null && mCursor.getCount() > 0) {
            int freq = Integer.parseInt(mCursor.getString(CommonStaticData.COLUMN_INDEX_SERVICE_FREQ));
            TVlog.i(TAG, " >>>>> current freq = " + freq);

            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                int channelNo = 13 + (int)((freq-473143)/6000);
                tv_phyChNo.setText(channelNo+"ch");
            } else {
                int channelNo = 14 + (int)((freq-473143)/6000);
                tv_phyChNo.setText(channelNo+"ch");
            }

            String channelName = mCursor.getString(CommonStaticData.COLUMN_INDEX_SERVICE_NAME);
            String[] split_channelName = channelName.split(" ");

            // live modify 20170104
            tv_remoteNo.setText(split_channelName[0]);
            String str = "";
            for (int i = 1; i < split_channelName.length; i++) {
                str += split_channelName[i];
                if (i < split_channelName.length - 1) {
                    str += " ";
                }
            }
            mTopBarText.setText(str);
            //

            //mTopBarText.setText(mCursor.getString(CommonStaticData.COLUMN_INDEX_SERVICE_NAME));  // live
        } else {
            tv_phyChNo.setText("- -ch");
            tv_remoteNo.setText("- - -");
            mTopBarText.setText(R.string.no_channel_title);
        }

        int date[] = FCI_TVi.GetTSNetTime();
        mCurWeekDay = MainActivity.DayFun(date[0], date[1], date[2]);
        /*
        if (EPGSimpleArrayAdapter.getInstance() != null) {
            mCurWeekDay = EPGSimpleArrayAdapter.getInstance().mCurWeekDay_EPG;
        }*/

        int EPGCount = FCI_TVi.GetEPGCount(mCurWeekDay, CommonStaticData.lastCH);

        if (EPGCount==0) {
            noepg_text.setVisibility(View.VISIBLE);
            curEpgPosition.setText("0");
        } else {  // justin no epg text clear
            noepg_text.setVisibility(View.INVISIBLE);
            curEpgPosition.setText("1");
        }

        if ((EPGCount != mLastEPGCount) || (mCountNoTitle > 0) || (_epgType != EPG_UPDATE_TYPE_PERIODIC)) {
            //TVlog.e(TAG, "::epg update start==> counNoTitle=" + mCountNoTitle + ", epgCount=" + EPGCount + ", lastEPGcount=" + mLastEPGCount+", _epgType="+_epgType);
            mGroupList.clear();
            mChildList.clear();
            mGroupList_hash.clear();
            positionHeaderVisible.clear();

            mCountNoTitle = 0;

            for (int EPGIndex = 0; EPGIndex < EPGCount; EPGIndex++) {
                String epgts = FCI_TVi.GetEPGTS(mCurWeekDay, EPGIndex);
                String[] epgts_split = epgts.split("  ");

                //TVlog.i(TAG, " >>>>>> epgts = "+epgts);
                //invalid time stamp
                if (epgts == null) {
                    continue;
                }

                String epgtitle = FCI_TVi.GetEPGTitle(mCurWeekDay, EPGIndex);
                String epgdetail = FCI_TVi.GetEPGDesc(mCurWeekDay, EPGIndex);

                //invalid title
                if (epgtitle.length() <= 2) {
                    mCountNoTitle++;
                    //TVlog.e(TAG, "::epg title len invalid(" + epgtitle.length() + ")=====================================>epg counNoTitle=" + mCountNoTitle);
                    continue;
                }

                if (epgdetail.length() == 0) {
                    epgdetail = getString(R.string.epg_no_desc);
                }

                String epgStartNDuration = null;
                String epgStartTime = null;
                String epgEndTime = null;
                int startHour, startMin, endHour, endMin, tmpHour, tmpMin;
                int[] startNDuration = FCI_TVi.GetEPGStartTimeNDuration(mCurWeekDay, EPGIndex);
                startHour = startNDuration[3];
                startMin = startNDuration[4];
                tmpHour = startNDuration[6];
                tmpMin = startNDuration[7];
                //start time
                if (startHour == EPG_TIME_NOT_DEFINED || startMin == EPG_TIME_NOT_DEFINED) {
                    epgStartTime = EPG_TIME_STR_NOT_DEFINED;
                } else {
                    epgStartTime = String.format(EPG_TIME_STR_FORMAT_SPECIFIER, startHour, startMin);
                }
                //end time
                if (tmpHour == EPG_TIME_NOT_DEFINED || tmpMin == EPG_TIME_NOT_DEFINED) { //duration not defined.
                    epgEndTime = EPG_TIME_STR_NOT_DEFINED;
                } else {
                    endMin = startMin + startNDuration[7];
                    if (endMin >= 60) {
                        endHour = 1;
                        endMin = endMin - 60;
                    } else {
                        endHour = 0;
                    }
                    endHour = endHour + startHour + startNDuration[6];
                    if (endHour >= 24) {
                        endHour = endHour % 24;
                    }
                    epgEndTime = String.format(EPG_TIME_STR_FORMAT_SPECIFIER, endHour, endMin);
                }

                String epgDate = epgts_split[0];
                mGroupList_hash.add(epgDate);
                TVlog.i(TAG, " >>>>> mGroupList_hash = " + mGroupList_hash);


                epgStartNDuration = epgStartTime + EPG_TIME_STR_SEPARATOR + epgEndTime + EPG_TIME_STR_END_INTERVAL_SPACE;
                String epgdisplay = epgDate + "  " + epgStartNDuration + epgtitle;
                //String epgdisplay = epgStartNDuration + epgtitle;

                mGroupList.add(epgdisplay);
                mChildList.add(epgdetail);
            }

            String[] group_split = {,};
            String[] prev_group_split = {,};
            for (int EPGIndex = 0; EPGIndex < mGroupList.size(); EPGIndex++) {
                group_split = ((String) mGroupList.get(EPGIndex)).split("  ");
                if (EPGIndex == 0) {
                    positionHeaderVisible.add(EPGIndex);
                } else {
                    prev_group_split = ((String) mGroupList.get(EPGIndex - 1)).split("  ");
                    if (group_split[0].equals(prev_group_split[0])) {
                        positionHeaderVisible.remove(EPGIndex);
                    } else {
                        positionHeaderVisible.add(EPGIndex);
                    }
                }
            }
            position = new ArrayList<Integer>(positionHeaderVisible);
            Collections.sort(position);
            TVlog.i(TAG, " >>>>> position = "+position);

        }

        list = new ArrayList<String>(mGroupList_hash);
        Collections.sort(list);

        /*
        EPGListView.setAdapter(new EPGSimpleArrayAdapter(this, mGroupList, mChildList));
        EPGListView.deferNotifyDataSetChanged();
        mLastEPGCount = EPGCount;
        */

        scrollButton = (ScrollView) findViewById(R.id.scrollButton);

        ll_dateBtn = (LinearLayout) findViewById(R.id.ll_dateBtn);
        if (ll_dateBtn != null) {
            ll_dateBtn.removeAllViews();
        }

        mButton = new Button[list.size()];
        for (int i = 0; i < list.size(); i++) {
            mButton[i] = new Button(this);
            if (mButton[i] != null) {
                LinearLayout.LayoutParams pm = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                pm.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                pm.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                pm.gravity = Gravity.CENTER;
                mButton[i].setId(i);
                mButton[i].setTag(list.get(i));
                String[] list_split  = list.get(i).split("/");
                if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                        || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                    mButton[i].setText(" "+list_split[0]+getApplicationContext().getString(R.string.month)
                            +list_split[1]+getApplicationContext().getString(R.string.day));
                } else {
                    mButton[i].setText(" "+list.get(i));
                }

                mButton[i].setTextColor(ContextCompat.getColor(this, R.color.white));
                mButton[i].setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_live_tv_white_48dp,0,0,0);
                mButton[i].setBackgroundResource(R.drawable.btn_selector);
                mButton[i].setPadding(10,20,10,20);
                mButton[i].setLayoutParams(pm);
                ll_dateBtn.addView(mButton[i]);
                //live remove
                //TVlog.i(TAG, " >>>>> current mButton.getId() = "+mButton[curSelectedIndex].getId());

                if (i == curSelectedIndex && mButton[curSelectedIndex] != null) {
                    mButton[curSelectedIndex].setBackgroundResource(R.drawable.btn_selected);
                    EPGListView.requestFocusFromTouch();
                    if (epgSimpleArrayAdapter != null) {
                        epgSimpleArrayAdapter.notifyDataSetChanged();
                    }
                    EPGListView.setSelection(curSelectedIndex);
                } else {
                    mButton[i].setBackgroundResource(R.drawable.btn_unselected);
                }
                /*
                if (mButton[curSelectedIndex] != null) {
                    //mButton[curSelectedIndex].setSelected(true);
                    mButton[curSelectedIndex].setBackgroundResource(R.drawable.btn_selected);
                    EPGListView.requestFocusFromTouch();
                    if (epgSimpleArrayAdapter != null) {
                        epgSimpleArrayAdapter.notifyDataSetChanged();
                    }
                    EPGListView.setSelection(curSelectedIndex);

                }*/
                //prevSelectedIndex = mButton[0].getId();

                mButton[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        curSelectedIndex = v.getId();
                        if (prevSelectedIndex != curSelectedIndex) {
                            mButton[curSelectedIndex].setBackgroundResource(R.drawable.btn_selected);
                            mButton[prevSelectedIndex].setBackgroundResource(R.drawable.btn_unselected);
                        }
                        TVlog.i(TAG, " >>>>> previous mButton.getId() = "+mButton[prevSelectedIndex].getId());
                        TVlog.i(TAG, " >>>>> current mButton.getId() = "+mButton[curSelectedIndex].getId());
                        prevSelectedIndex = mButton[curSelectedIndex].getId();
                        EPGListView.requestFocusFromTouch();
                        epgSimpleArrayAdapter.notifyDataSetChanged();
                        EPGListView.setSelection((position.get(mButton[curSelectedIndex].getId())));
                    }
                });
            }
        }
        // live modify
        TVlog.i(TAG, " >>>>> mGroupList = "+mGroupList);
        epgSimpleArrayAdapter = new EPGSimpleArrayAdapter(this, mGroupList, mChildList);
        EPGListView.setAdapter(epgSimpleArrayAdapter);

        //live add
        EPGListView.setSelectionFromTop(pos, mScrollY);
        /*
        if (mButton.length > curSelectedIndex) {
            EPGListView.setSelection((position.get(mButton[curSelectedIndex].getId())));
        }*/
        EPGListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener()
        {
            @Override
            public void onGroupExpand(int groupPosition)
            {
                isExpandedGroup = (int) epgSimpleArrayAdapter.getGroupId(groupPosition);
                if (mPrePosition != groupPosition) {
                    EPGListView.collapseGroup(mPrePosition);
                    mPrePosition = groupPosition;
                }
            }
        });
        EPGListView.expandGroup(isExpandedGroup);
        //
        epgSimpleArrayAdapter.notifyDataSetChanged();
        mLastEPGCount = EPGCount;
        //lastEPGPosition.setText(Integer.toString(EPGCount));
        lastEPGPosition.setText(Integer.toString(EPGCount-mCountNoTitle));
    }

    @Override
    protected void onDestroy() {
        TVlog.i(TAG, " ===== onDestroy() =====");
        super.onDestroy();
        CommonStaticData.epgActivityShow = false;   // justin add for dongle detached
        epgContext = null;
        newTimer.cancel();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        CommonStaticData.epgActivityShow = false;
        finish();
    }

    public static String getDayOfWeek(String date) {
        int year = 0;
        int month = 0;
        int day = 0;
        if (date.length() > 7) {
            year = Integer.parseInt(date.substring(0, 4));
            month = Integer.parseInt(date.substring(4, 6));
            day = Integer.parseInt(date.substring(6, 8));
        }
        String dayOfWeek = "";
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DATE, day);
        int day_of_week = cal.get(Calendar.DAY_OF_WEEK);
        switch (day_of_week) {
            case 1:
                dayOfWeek = "Sunday";
                break;
            case 2:
                dayOfWeek = "Monday";
                break;
            case 3:
                dayOfWeek = "Tuesday";
                break;
            case 4:
                dayOfWeek = "Wednesday";
                break;
            case 5:
                dayOfWeek = "Thursday";
                break;
            case 6:
                dayOfWeek = "Friday";
                break;
            case 7:
                dayOfWeek = "Saturday";
                break;
        }
        return dayOfWeek;
    }

}

