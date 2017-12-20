package kr.co.fci.tv.epgInfo;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import kr.co.fci.tv.MainActivity;
import kr.co.fci.tv.R;
import kr.co.fci.tv.buildOption;
import kr.co.fci.tv.tvSolution.FCI_TVi;
import kr.co.fci.tv.util.TVlog;

import static kr.co.fci.tv.R.drawable.roundlayout;
import static kr.co.fci.tv.R.drawable.roundlayout_selected;


/**
 * Created by justin.song on 2015-07-16.
 */
public class EPGSimpleArrayAdapter extends BaseExpandableListAdapter {

    private static final String TAG= "EPGSimpleArrayAdapter ";

    private ArrayList<String> groupList = null;
    private ArrayList<String> childList = null;
    private LayoutInflater inflater = null;
    private ViewHolder viewHolder = null;
    Cursor mCursor;
    private Context mContext;
    public int mCurWeekDay_EPG = 0;
    public String[] group_split = {, };

    String curTsDate = "";

    public static EPGSimpleArrayAdapter instance;
    public static EPGSimpleArrayAdapter getInstance()
    {
        return instance;
    }


    // public EPGSimpleArrayAdapter(Context act, ArrayList<String> groupList, ArrayList<ArrayList<String>> chilsList) {
    public EPGSimpleArrayAdapter(Context mContext, ArrayList<String> groupList, ArrayList<String> childList) {
        super();
        this.mContext = mContext;
        this.inflater = LayoutInflater.from(mContext);
        this.groupList = groupList;
        this.childList = childList;
    }


    @Override
    public int getGroupCount() {
        //return groups.size();
        return groupList.size();
    }

    public void updateData(ArrayList<String> groupList, ArrayList<String> childList) {
        this.groupList = groupList;
        this.childList = childList;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // return childList.get(groupPosition).size();
        // return childList.size();
        return 1;       // for showing only 1 by justin
    }

    @Override
    public Object getGroup(int groupPosition) {
        //return null;
        return groupList.get(groupPosition);
    }


    @Override
    public Object getChild(int groupPosition, int childPosition) {
        //return childList.get(groupPosition).get(childPosition);
        return childList.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        // return 0;
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        // return childPosition;
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        // return true;
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.epggroup_row, parent, false);
            viewHolder = new ViewHolder();
            //convertView.setTag(viewHolder);

            viewHolder.groupName = (LinearLayout) convertView.findViewById(R.id.groupname);
            viewHolder.tv_header = (TextView) convertView.findViewById(R.id.tv_header);
            viewHolder.expandImg = (ImageView) convertView.findViewById(R.id.expandImg);
            viewHolder.tv_groupName = (TextView) convertView.findViewById(R.id.programText);
            parent.getId();

            //viewHolder.scheduleImg.setImageResource(R.drawable.clock_gray);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (viewHolder.expandImg != null) {
            if (isExpanded) {
                viewHolder.expandImg.setImageResource(R.drawable.collapsible_white);
                viewHolder.groupName.setBackgroundResource(roundlayout_selected);
            } else {
                viewHolder.expandImg.setImageResource(R.drawable.expandable_white);
                viewHolder.groupName.setBackgroundResource(roundlayout);
            }
        }

        group_split = ((String) getGroup(groupPosition)).split("  ");

        int date[] = FCI_TVi.GetTSNetTime();
        String date1[] = group_split[0].split("/");
        date[1] = Integer.parseInt(date1[0]);  //month
        date[2] = Integer.parseInt(date1[1]);  //day
        TVlog.i(TAG, " >>>>> date[0] = "+date[0]+", date[1] = "+date[1]+", date[2] = "+date[2]);
        mCurWeekDay_EPG = MainActivity.DayFun(date[0], date[1], date[2]);
        //mCurWeekDay_EPG = EPGActivity.DayofDate(date[0], date[1], date[2]);
        String epgts = FCI_TVi.GetEPGTS(mCurWeekDay_EPG, groupPosition);
        TVlog.i(TAG, " >>>>> epgts = "+epgts);
        String[] epgts_split1 = epgts.split("  ");

        if (EPGActivity.getInstance().list.size() >= 0) {
            for (int i = 0; i < EPGActivity.getInstance().list.size(); i++) {
                if (EPGActivity.getInstance().mButton[i] != null && epgts_split1[0] != null) {
                    if (EPGActivity.getInstance().mButton[i].getTag().equals(epgts_split1[0])) {
                        EPGActivity.getInstance().mButton[i].setSelected(true);
                        EPGActivity.getInstance().scrollButton.smoothScrollTo(0, EPGActivity.getInstance().mButton[i].getTop());

                    } else {
                        EPGActivity.getInstance().mButton[i].setSelected(false);
                    }
                }
            }
        }

        String[] epgts_split2 = epgts_split1[0].split("/");

        int curYear = date[0];
        //int curMonth = Integer.parseInt(epgts_split2[0]);
        //int curDay = Integer.parseInt(epgts_split2[1]);
        int curMonth = date[1];
        int curDay = date[2];
        String monthStr = "";
        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
            if (curMonth == 1) {
                monthStr = "1"+getContext().getString(R.string.month);
            } else if (curMonth == 2) {
                monthStr = "2"+getContext().getString(R.string.month);
            } else if (curMonth == 3) {
                monthStr = "3"+getContext().getString(R.string.month);
            } else if (curMonth == 4) {
                monthStr = "4"+getContext().getString(R.string.month);
            } else if (curMonth == 5) {
                monthStr = "5"+getContext().getString(R.string.month);
            } else if (curMonth == 6) {
                monthStr = "6"+getContext().getString(R.string.month);
            } else if (curMonth == 7) {
                monthStr = "7"+getContext().getString(R.string.month);
            } else if (curMonth == 8) {
                monthStr = "8"+getContext().getString(R.string.month);
            } else if (curMonth == 9) {
                monthStr = "9"+getContext().getString(R.string.month);
            } else if (curMonth == 10) {
                monthStr = "10"+getContext().getString(R.string.month);
            } else if (curMonth == 11) {
                monthStr = "11"+getContext().getString(R.string.month);
            } else if (curMonth == 12) {
                monthStr = "12"+getContext().getString(R.string.month);
            }
        } else {
            if (curMonth == 1) {
                monthStr = "Jan";
            } else if (curMonth == 2) {
                monthStr = "Feb";
            } else if (curMonth == 3) {
                monthStr = "Mar";
            } else if (curMonth == 4) {
                monthStr = "Apr";
            } else if (curMonth == 5) {
                monthStr = "May";
            } else if (curMonth == 6) {
                monthStr = "Jun";
            } else if (curMonth == 7) {
                monthStr = "Jul";
            } else if (curMonth == 8) {
                monthStr = "Aug";
            } else if (curMonth == 9) {
                monthStr = "Sep";
            } else if (curMonth == 10) {
                monthStr = "Oct";
            } else if (curMonth == 11) {
                monthStr = "Nov";
            } else if (curMonth == 12) {
                monthStr = "Dec";
            }
        }

        String dayOfWeek = "";
        String dayOfWeek1 = "";
        try {
            dayOfWeek = EPGActivity.getDayOfWeek(String.valueOf(date[0])+epgts_split2[0]+epgts_split2[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        TVlog.i (TAG , " >>>>> dayOfWeek = "+dayOfWeek);

        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
            if (dayOfWeek.equals("Monday")) {
                dayOfWeek1 = getContext().getString(R.string.monday);
            } else if (dayOfWeek.equals("Tuesday")) {
                dayOfWeek1 = getContext().getString(R.string.tuesday);
            } else if (dayOfWeek.equals("Wednesday")) {
                dayOfWeek1 = getContext().getString(R.string.wednesday);
            } else if (dayOfWeek.equals("Thursday")) {
                dayOfWeek1 = getContext().getString(R.string.thursday);
            } else if (dayOfWeek.equals("Friday")) {
                dayOfWeek1 = getContext().getString(R.string.friday);
            } else if (dayOfWeek.equals("Saturday")) {
                dayOfWeek1 = getContext().getString(R.string.saturday);
            } else if (dayOfWeek.equals("Sunday")) {
                dayOfWeek1 = getContext().getString(R.string.sunday);
            }
            curTsDate = (curYear+getContext().getString(R.string.year)+curMonth+getContext().getString(R.string.month)+curDay+getContext().getString(R.string.day)+dayOfWeek1);
        } else {
            curTsDate = (dayOfWeek+", "+monthStr+" "+curDay +", "+curYear);
        }

        //viewHolder.tv_header.setText(group_split[0]);
        TVlog.i (TAG , " >>>>> curTsDate1 = "+curTsDate);
        viewHolder.tv_header.setText(curTsDate);
        viewHolder.tv_header.setTag(group_split[0]);

        String[] prev_group_split = {, };
        if (0 < groupPosition && groupPosition <= getGroupCount()) {
            prev_group_split = ((String) getGroup(groupPosition-1)).split("  ");
        }

        if (group_split.length == 3) {
            viewHolder.tv_groupName.setText((String) group_split[1]+"  "+group_split[2]);
        } else if (group_split.length == 4) {
            viewHolder.tv_groupName.setText((String) group_split[1]+"  "+group_split[2]+"  "+group_split[3]);
        } else if (group_split.length == 5) {
            viewHolder.tv_groupName.setText((String) group_split[1]+"  "+group_split[2]+"  "+group_split[3]+"  "+group_split[4]);
        } else if (group_split.length == 6) {
            viewHolder.tv_groupName.setText((String) group_split[1]+"  "+group_split[2]+"  "+group_split[3]+"  "+group_split[4]+"  "+group_split[5]);
        } else if (group_split.length == 7) {
            viewHolder.tv_groupName.setText((String) group_split[1]+"  "+group_split[2]+"  "+group_split[3]+"  "+group_split[4]+"  "+group_split[5]+"  "+group_split[6]);
        } else if (group_split.length == 8) {
            viewHolder.tv_groupName.setText((String) group_split[1]+"  "+group_split[2]+"  "+group_split[3]+"  "+group_split[4]+"  "+group_split[5]+"  "+group_split[6]+"  "+group_split[7]);
        } else if (group_split.length == 9) {
            viewHolder.tv_groupName.setText((String) group_split[1]+"  "+group_split[2]+"  "+group_split[3]+"  "+group_split[4]+"  "+group_split[5]+"  "+group_split[6]+"  "+group_split[7]+"  "+group_split[8]);
        }


        TVlog.i (TAG, " >>>>> group_split[0] = "+group_split[0]);
        TVlog.i (TAG, " >>>>> prev_group_split = "+ Arrays.toString(prev_group_split));

        if (groupPosition == 0) {
            viewHolder.tv_header.setVisibility(View.VISIBLE);
        } else if (0 < groupPosition && groupPosition < getGroupCount()) {
            if (group_split[0].equals(prev_group_split[0])) {
                viewHolder.tv_header.setVisibility(View.GONE);
            } else {
                viewHolder.tv_header.setVisibility(View.VISIBLE);
            }
        }

        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
            //use free font
            viewHolder.tv_groupName.setTypeface(MainActivity.getInstance().mFont);
        }

        return convertView;
    }



    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String children = (String) getChild(groupPosition, childPosition);

        if(convertView==null) {

              /*  viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.epgprogramlist_item, null);
                viewHolder.tv_groupName = (TextView)convertView.findViewById(R.id.programText);
                viewHolder.tv_childName = (TextView) convertView.findViewById(R.id.programDesc);
                convertView.setTag(viewHolder);*/
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.epgchild_row, null);
            viewHolder.tv_childName = (TextView) convertView.findViewById(R.id.textView_detail);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.tv_childName.setText((String) getChild(groupPosition, childPosition));
        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
            viewHolder.tv_childName.setTypeface(MainActivity.getInstance().mFont);
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {return false;}

    static class ViewHolder {
        //public ImageView iv_image;
        LinearLayout groupName;
        TextView tv_header;
        TextView tv_groupName;
        TextView tv_childName;
        ImageView expandImg;
    }



    protected Context getContext() {
        return mContext;
    }


    @Override
    protected void finalize() throws Throwable {
        free();
        super.finalize();
    }

    private void free() {
        mContext = null;
        groupList = null;
        childList = null;
        mCursor = null;
    }

}
