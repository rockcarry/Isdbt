package kr.co.fci.tv.channelList;

/**
 * Created by live.kim on 2015-09-14.
 */

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import kr.co.fci.tv.MainActivity;
import kr.co.fci.tv.R;
import kr.co.fci.tv.buildOption;
import kr.co.fci.tv.saves.CommonStaticData;
import kr.co.fci.tv.saves.SharedPreference;
import kr.co.fci.tv.util.CustomToast;

import static kr.co.fci.tv.R.drawable.roundlayout;
import static kr.co.fci.tv.R.drawable.roundlayout_selected;

public class ChannelListAdapter extends ArrayAdapter<Channel> {

    private Context context;
    public List<Channel> channels;

    private Typeface mFont_channelList = MainActivity.getInstance().mFont;
    private  Typeface tf_channelList = MainActivity.getInstance().tf;

    SharedPreference sharedPreference;
    Cursor mCursor;
    Boolean favorEnable;
    private ListView channelListView;

    CustomToast toast = new CustomToast(getContext());

    public static ChannelListAdapter instance;
    public static ChannelListAdapter getInstance()
    {
        return instance;
    }

    //public ChannelListAdapter(Context context, Cursor mCursor, List<Channel> channels) {
    public ChannelListAdapter(Context context, Cursor mCursor, List<Channel> channels, List<Channel> favChannels) {
        super(context, R.layout.channel_list_item, channels);
        this.context = context;
        if(favChannels == null) {
            favorEnable = false;
            this.channels = channels;
        }
        else {
            favorEnable = true;
            this.channels = favChannels;
        }
        sharedPreference = new SharedPreference();
        this.mCursor = mCursor;
        instance = this;
    }

    private class ViewHolder {
        LinearLayout ch_layout_item;
        View chlist_divider;
        RelativeLayout rl_type;
        LinearLayout ll_ch_name;
        LinearLayout ll_imgbtn_favorite;
        TextView phyChNo;
        TextView remoteNo;
        TextView channelNameTxt;
        ImageView favoriteImg;
        //CheckBox favoriteImg;
        ImageView tv_type;
        ImageView tv_free;
        // myphone
        TextView txt_ch;
        TextView txt_dash;
        //
        ListView channelListView;
    }

    @Override
    public int getCount() {
        if (channels != null) {
            return channels.size();
        } else {
            return 0;
        }
    }

    @Override
    public Channel getItem(int position) {
        try {
            return channels.get(position);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.channel_list_item, null);
            holder = new ViewHolder();
            holder.ch_layout_item = (LinearLayout) convertView.findViewById(R.id.ch_layout_item);
            holder.chlist_divider = (View) convertView.findViewById(R.id.chlist_divider);

            holder.rl_type = (RelativeLayout) convertView.findViewById(R.id.rl_type);
            holder.ll_ch_name = (LinearLayout) convertView.findViewById(R.id.ll_ch_name);
            holder.ll_imgbtn_favorite = (LinearLayout) convertView.findViewById(R.id.ll_imgbtn_favorite);

            holder.phyChNo = (TextView) convertView.findViewById(R.id.txt_ch_no);
            if (tf_channelList != null) {
                holder.phyChNo.setTypeface(tf_channelList);
            }
            holder.phyChNo.setTextSize(18);

            if (buildOption.VIEW_PHY_CH) {
                holder.phyChNo.setVisibility(View.VISIBLE);
            } else {
                holder.phyChNo.setVisibility(View.GONE);
            }

            if(buildOption.GUI_STYLE == 2 || buildOption.GUI_STYLE == 3) {
                holder.txt_ch = (TextView) convertView.findViewById(R.id.txt_ch);
                holder.txt_dash = (TextView) convertView.findViewById(R.id.txt_dash);
            }

            holder.remoteNo = (TextView) convertView.findViewById(R.id.txt_remote_no);
            holder.channelNameTxt = (TextView) convertView.findViewById(R.id.txt_ch_name);
            holder.channelNameTxt.setSelected(true);
            holder.favoriteImg = (ImageView) convertView.findViewById(R.id.imgbtn_favorite);
            holder.favoriteImg.setId(position);
            holder.tv_type = (ImageView) convertView.findViewById(R.id.tv_type);
            holder.tv_type.setId(position);
            holder.tv_free = (ImageView) convertView.findViewById(R.id.tv_free);
            holder.tv_free.setId(position);

            holder.channelListView = (ListView) convertView.findViewById(R.id.list_channel);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Channel channel = (Channel) getItem(position);

        if (buildOption.GUI_STYLE == 2) {
            if (channel.getindex() == CommonStaticData.lastCH) {
                holder.tv_free.setBackgroundResource(R.drawable.tv_free_sel);
            } else {
                holder.tv_free.setBackgroundResource(R.drawable.tv_free);
            }
        } else {
            holder.tv_free.setBackgroundResource(R.drawable.tv_free);
        }

      //  TVlog.i("ChannelListAdapter", " >>>>> channel.getFreqKHz = " + channel.getFreqKHz());
        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
            int channelNo = 13 + (int)((channel.getFreqKHz()-473143)/6000);
            holder.phyChNo.setText(channelNo+"ch");
        } else {
            if (buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA
                    || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_ONESEG
                    || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                // for Sri Lanka
                int channelNo = 13 + (int)((channel.getFreqKHz()-474000)/8000);
                holder.phyChNo.setText(channelNo+"ch");
            } else {
                int channelNo = 14 + (int)((channel.getFreqKHz()-473143)/6000);
                holder.phyChNo.setText(channelNo+"ch");
            }
        }

        String channelName = channel.getName();
     //   TVlog.i("ChannelListAdapter", " >>>>> channelName = " + channelName);
        String[] split_channelName = channelName.split(" ");

        // live modify 20170104
            holder.remoteNo.setText(split_channelName[0]);
        String str = "";
        for (int i = 1; i < split_channelName.length; i++) {
            str += split_channelName[i];
            if (i < split_channelName.length - 1) {
                str += " ";
            }
        }
        holder.channelNameTxt.setText(str);
        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
            //use free font
            if (mFont_channelList != null) {
                holder.channelNameTxt.setTypeface(mFont_channelList);
            }
        }
        //

        if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
            if(CommonStaticData.receivemode == CommonStaticData.RECEIVE_MODE_AUTO || CommonStaticData.receivemode == CommonStaticData.RECEIVE_MODE_OFF) { // Switching : Auto or Off
                convertView.setVisibility(View.VISIBLE);
                holder.ch_layout_item.setVisibility(View.VISIBLE);
                holder.chlist_divider.setVisibility(View.VISIBLE);
            } else if (CommonStaticData.receivemode == CommonStaticData.RECEIVE_MODE_FULLSEG) { //Switching : Fullseg
                if (channel.getType() == 0) { // if 1seg
                    convertView.setVisibility(View.GONE);
                    holder.ch_layout_item.setVisibility(View.GONE);
                    holder.chlist_divider.setVisibility(View.GONE);
                } else if (channel.getType() == 1) { // if Fullseg
                    convertView.setVisibility(View.VISIBLE);
                    holder.ch_layout_item.setVisibility(View.VISIBLE);
                    holder.chlist_divider.setVisibility(View.VISIBLE);
                }
            } else if (CommonStaticData.receivemode == CommonStaticData.RECEIVE_MODE_1SEG) { // Switching : 1seg
                if (channel.getType() == 1) { // if Fullseg
                    convertView.setVisibility(View.GONE);
                    holder.ch_layout_item.setVisibility(View.GONE);
                    holder.chlist_divider.setVisibility(View.GONE);
                } else if (channel.getType() == 0) { // if 1seg
                    convertView.setVisibility(View.VISIBLE);
                    holder.ch_layout_item.setVisibility(View.VISIBLE);
                    holder.chlist_divider.setVisibility(View.VISIBLE);
                }
            }
        } else {
            convertView.setVisibility(View.VISIBLE);
            holder.ch_layout_item.setVisibility(View.VISIBLE);
            holder.chlist_divider.setVisibility(View.VISIBLE);
        }


        if ((channel.getType()) == 0) { // if 1seg
            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                holder.tv_type.setBackgroundResource(R.drawable.jp_1seg);
                holder.tv_free.setVisibility(View.GONE);
            } else {
                if (buildOption.GUI_STYLE == 2) {
                    if (channel.getindex() == CommonStaticData.lastCH) {
                        holder.tv_type.setBackgroundResource(R.drawable.tv_icon_1seg_sel);
                    } else {
                    holder.tv_type.setBackgroundResource(R.drawable.tv_icon_1seg);
                    }
                } else {
                    holder.tv_type.setBackgroundResource(R.drawable.tv_icon_1seg);
                }
                    if ((channel.getFree()) == 0) {
                        holder.tv_free.setVisibility(View.VISIBLE);
                    } else {
                        holder.tv_free.setVisibility(View.GONE);
                    }
            }

        } else {
            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                holder.tv_type.setBackgroundResource(R.drawable.jp_fullseg);
                holder.tv_free.setVisibility(View.GONE);
                } else {
                    if (buildOption.GUI_STYLE == 2) {
                        if (channel.getindex() == CommonStaticData.lastCH) {
                            holder.tv_type.setBackgroundResource(R.drawable.tv_icon_fullseg_sel);
                        } else {
                            holder.tv_type.setBackgroundResource(R.drawable.tv_icon_fullseg);
                        }
                    } else {
                    holder.tv_type.setBackgroundResource(R.drawable.tv_icon_fullseg);
                    }
                    if ((channel.getFree()) == 0) {
                        holder.tv_free.setVisibility(View.VISIBLE);
                    } else {
                        holder.tv_free.setVisibility(View.GONE);
                    }
                }

        }

        /* current channel marking in channel list */
        if (channel.getindex() == MainActivity.getInstance().mChannelIndex) {
            holder.ch_layout_item.setBackgroundResource(roundlayout_selected);
            holder.phyChNo.setTextColor(getContext().getResources().getColorStateList(R.color.blue3));
            //holder.phyChNo.setShadowLayer(10.0f, 0.0f, 0.0f, Color.WHITE);
            holder.remoteNo.setTextColor(getContext().getResources().getColorStateList(R.color.blue3));
            holder.remoteNo.setTypeface(null, Typeface.BOLD);
            //holder.remoteNo.setShadowLayer(10.0f, 0.0f, 0.0f, Color.WHITE);
            holder.channelNameTxt.setTextColor(getContext().getResources().getColorStateList(R.color.blue3));
            holder.channelNameTxt.setTypeface(null, Typeface.BOLD);
            //holder.channelNameTxt.setShadowLayer(10.0f, 0.0f, 0.0f, Color.WHITE);
            if(buildOption.GUI_STYLE == 2 || buildOption.GUI_STYLE == 3) {
                holder.txt_ch.setTextColor(getContext().getResources().getColorStateList(R.color.blue3));
                holder.txt_ch.setTypeface(null, Typeface.BOLD);
                //holder.txt_ch.setShadowLayer(10.0f, 0.0f, 0.0f, Color.WHITE);
                holder.txt_dash.setTextColor(getContext().getResources().getColorStateList(R.color.blue3));
                holder.txt_dash.setTypeface(null, Typeface.BOLD);
                //holder.txt_dash.setShadowLayer(10.0f, 0.0f, 0.0f, Color.WHITE);
            }

        } else {
            holder.ch_layout_item.setBackgroundResource(roundlayout);
            holder.phyChNo.setTextColor(getContext().getResources().getColorStateList(R.color.white));
            //holder.phyChNo.setShadowLayer(0.0f, 0.0f, 0.0f, Color.WHITE);
            holder.remoteNo.setTextColor(getContext().getResources().getColorStateList(R.color.white));
            holder.remoteNo.setTypeface(null, Typeface.NORMAL);
            //holder.remoteNo.setShadowLayer(0.0f, 0.0f, 0.0f, Color.WHITE);
            holder.channelNameTxt.setTextColor(getContext().getResources().getColorStateList(R.color.white));
            holder.channelNameTxt.setTypeface(null, Typeface.NORMAL);
            //holder.channelNameTxt.setShadowLayer(0.0f, 0.0f, 0.0f, Color.WHITE);
            if(buildOption.GUI_STYLE == 2 || buildOption.GUI_STYLE == 3){
                holder.txt_ch.setTextColor(getContext().getResources().getColorStateList(R.color.white));
                holder.txt_ch.setTypeface(null, Typeface.NORMAL);
                //holder.txt_ch.setShadowLayer(0.0f, 0.0f, 0.0f, Color.WHITE);
                holder.txt_dash.setTextColor(getContext().getResources().getColorStateList(R.color.white));
                holder.txt_dash.setTypeface(null, Typeface.NORMAL);
                //holder.txt_dash.setShadowLayer(0.0f, 0.0f, 0.0f, Color.WHITE);
            }
        }

        holder.rl_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewHolder holder = new ViewHolder();
                /*if (holder.rl_type != null) {
                    holder.rl_type.setBackgroundResource(android.R.drawable.btn_default);
                }*/
                if ((channel.getType()) == 0) { // 1seg
                    //CustomToast toast = new CustomToast(getContext());
                    toast.showToast(getContext(), R.string.SD, Toast.LENGTH_SHORT);
                    LinearLayout layout = new LinearLayout(context);
                    LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp1.setMargins(20, 20, 10, 20);
                    lp2.setMargins(0, 22, 10, 0);
                    layout.setBackgroundResource(R.drawable.dialog_box);
                    layout.setOrientation(LinearLayout.HORIZONTAL);
                    ImageView image = new ImageView(context);
                    if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                        image.setImageResource(R.drawable.jp_1seg);
                    } else {
                        if (buildOption.GUI_STYLE == 2) {
                            if (channel.getindex() == CommonStaticData.lastCH) {
                                holder.tv_type.setBackgroundResource(R.drawable.tv_icon_1seg_sel);
                            } else {
                                holder.tv_type.setBackgroundResource(R.drawable.tv_icon_1seg);
                            }
                        } else {
                            holder.tv_type.setBackgroundResource(R.drawable.tv_icon_1seg);
                        }
                    }
                    image.setLayoutParams(lp1);
                    TextView text = new TextView(context);
                    text.setText(R.string.SD);
                    text.setTextSize(15);
                    text.setTextColor(Color.WHITE);
                    text.setLayoutParams(lp2);
                    layout.addView(image);
                    layout.addView(text);
                    toast.setView(layout);
                    toast.show();
                } else if (channel.getType() == 1)  {
                    //CustomToast toast = new CustomToast(getContext());
                    toast.showToast(getContext(), R.string.HD, Toast.LENGTH_SHORT);
                    LinearLayout layout = new LinearLayout(context);
                    LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp1.setMargins(20, 20, 10, 20);
                    lp2.setMargins(0, 22, 10, 0);
                    layout.setBackgroundResource(R.drawable.dialog_box);
                    layout.setOrientation(LinearLayout.HORIZONTAL);
                    ImageView image = new ImageView(context);
                    if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                        image.setImageResource(R.drawable.jp_fullseg);
                    } else {
                        if (buildOption.GUI_STYLE == 2) {
                            if (channel.getindex() == CommonStaticData.lastCH) {
                                holder.tv_type.setBackgroundResource(R.drawable.tv_icon_fullseg_sel);
                            } else {
                                holder.tv_type.setBackgroundResource(R.drawable.tv_icon_fullseg);
                            }
                        } else {
                            holder.tv_type.setBackgroundResource(R.drawable.tv_icon_fullseg);
                        }
                    }
                    image.setLayoutParams(lp1);
                    TextView text = new TextView(context);
                    text.setText(R.string.HD);
                    text.setTextSize(15);
                    text.setTextColor(Color.WHITE);
                    text.setLayoutParams(lp2);
                    layout.addView(image);
                    layout.addView(text);
                    toast.setView(layout);
                    toast.show();
                } else if (channel.getFree() == 0) {
                    //CustomToast toast = new CustomToast(getContext());
                    toast.showToast(getContext(), R.string.Encrypt, Toast.LENGTH_SHORT);
                    LinearLayout layout = new LinearLayout(context);
                    LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp1.setMargins(20, 20, 10, 20);
                    lp2.setMargins(0, 22, 10, 0);
                    layout.setBackgroundResource(R.drawable.dialog_box);
                    layout.setOrientation(LinearLayout.HORIZONTAL);
                    ImageView image = new ImageView(context);
                    if (buildOption.GUI_STYLE == 2) {
                        if (channel.getindex() == CommonStaticData.lastCH) {
                            holder.tv_type.setBackgroundResource(R.drawable.tv_free_sel);
                        } else {
                            holder.tv_type.setBackgroundResource(R.drawable.tv_free);
                        }
                    } else {
                        holder.tv_type.setBackgroundResource(R.drawable.tv_free);
                    }
                    image.setLayoutParams(lp1);
                    TextView text = new TextView(context);
                    text.setText(R.string.Encrypt);
                    text.setTextColor(Color.WHITE);
                    text.setTextSize(15);
                    text.setLayoutParams(lp2);
                    layout.addView(image);
                    layout.addView(text);
                    toast.setView(layout);
                    toast.show();
                }
            }
        });



        holder.tv_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((channel.getType()) == 0) { // 1seg
                    //CustomToast toast = new CustomToast(getContext());
                    toast.showToast(getContext(), R.string.SD, Toast.LENGTH_SHORT);
                    LinearLayout layout = new LinearLayout(context);
                    LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp1.setMargins(20, 20, 10, 20);
                    lp2.setMargins(0, 22, 10, 0);
                    layout.setBackgroundResource(R.drawable.dialog_box);
                    layout.setOrientation(LinearLayout.HORIZONTAL);
                    ImageView image = new ImageView(context);
                    if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                        image.setImageResource(R.drawable.jp_1seg);
                    } else {
                        if (buildOption.GUI_STYLE == 2) {
                            if (channel.getindex() == CommonStaticData.lastCH) {
                                image.setBackgroundResource(R.drawable.tv_icon_1seg_sel);
                            } else {
                                image.setBackgroundResource(R.drawable.tv_icon_1seg);
                            }
                        } else {
                            image.setBackgroundResource(R.drawable.tv_icon_1seg);
                        }
                    }
                    image.setLayoutParams(lp1);
                    TextView text = new TextView(context);
                    text.setText(R.string.SD);
                    text.setTextColor(Color.WHITE);
                    text.setTextSize(15);
                    text.setLayoutParams(lp2);
                    layout.addView(image);
                    layout.addView(text);
                    toast.setView(layout);
                    toast.show();
                }
                else {
                    //CustomToast toast = new CustomToast(getContext());
                    toast.showToast(getContext(), R.string.HD, Toast.LENGTH_SHORT);
                    LinearLayout layout = new LinearLayout(context);
                    LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp1.setMargins(20, 20, 10, 20);
                    lp2.setMargins(0, 22, 10, 0);
                    layout.setBackgroundResource(R.drawable.dialog_box);
                    layout.setOrientation(LinearLayout.HORIZONTAL);
                    ImageView image = new ImageView(context);
                    if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                            || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_FILE) {
                        image.setImageResource(R.drawable.jp_fullseg);
                    } else {
                        if (buildOption.GUI_STYLE == 2) {
                            if (channel.getindex() == CommonStaticData.lastCH) {
                                image.setBackgroundResource(R.drawable.tv_icon_fullseg_sel);
                            } else {
                                image.setBackgroundResource(R.drawable.tv_icon_fullseg);
                            }
                        } else {
                            image.setBackgroundResource(R.drawable.tv_icon_fullseg);
                        }
                    }
                    image.setLayoutParams(lp1);
                    TextView text = new TextView(context);
                    text.setText(R.string.HD);
                    text.setTextColor(Color.WHITE);
                    text.setTextSize(15);
                    text.setLayoutParams(lp2);
                    layout.addView(image);
                    layout.addView(text);
                    toast.setView(layout);
                    toast.show();
                }
            }
        });

        holder.tv_free.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CustomToast toast = new CustomToast(getContext());
                toast.showToast(getContext(), R.string.Encrypt, Toast.LENGTH_SHORT);
                LinearLayout layout = new LinearLayout(context);
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp1.setMargins(20, 20, 10, 20);
                lp2.setMargins(0, 22, 10, 0);
                layout.setBackgroundResource(R.drawable.dialog_box);
                layout.setOrientation(LinearLayout.HORIZONTAL);
                ImageView image = new ImageView(context);
                if (buildOption.GUI_STYLE == 2) {
                    if (channel.getindex() == CommonStaticData.lastCH) {
                        image.setBackgroundResource(R.drawable.tv_free_sel);
                    } else {
                        image.setBackgroundResource(R.drawable.tv_free);
                    }
                } else {
                    image.setBackgroundResource(R.drawable.tv_free);
                }
                image.setLayoutParams(lp1);
                TextView text = new TextView(context);
                text.setText(R.string.Encrypt);
                text.setTextColor(Color.WHITE);
                text.setTextSize(15);
                text.setLayoutParams(lp2);
                layout.addView(image);
                layout.addView(text);
                toast.setView(layout);
                toast.show();
            }
        });


/*

        holder.ll_ch_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewHolder holder = new ViewHolder();
                if (holder.ch_layout_item != null) {
                    holder.ch_layout_item.setBackgroundResource(android.R.drawable.list_selector_background);
                }

                MainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_LIST_AV_STARTED);
                TVBridge.serviceID_start(position);
                notifyDataSetChanged();
            }
        });

        holder.channelNameTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewHolder holder = new ViewHolder();
                if (holder.ch_layout_item != null) {
                    holder.ch_layout_item.setBackgroundResource(android.R.drawable.list_selector_background);
                }

                MainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_LIST_AV_STARTED);
                TVBridge.serviceID_start(position);
                notifyDataSetChanged();
            }
        });
*/

        holder.ll_imgbtn_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = v.getTag().toString();
                ViewHolder holder = new ViewHolder();

                if (tag.equalsIgnoreCase("gray")) {
                    //Channel channel = channels.get(v.getId());
                    Channel channel = channels.get(position);
                    sharedPreference.addFavorite(context, channels.get(position));
                    //CustomToast toast = new CustomToast(getContext());
                    toast.showToast(getContext(), R.string.add_favr, Toast.LENGTH_SHORT);
                    //Toast.makeText(context, context.getResources().getString(R.string.add_favr), Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                    v.setTag("yellow");
                    if (holder.favoriteImg != null) {
                        holder.favoriteImg.setImageResource(R.drawable.favorite_set);
                    }

                } else {
                    if(favorEnable == false) {  // On channellist viewing
                        sharedPreference.removeFavorite(context, channels.get(position));
                        v.setTag("gray");
                        if (holder.favoriteImg != null) {
                            holder.favoriteImg.setImageResource(R.drawable.favorite_cancel);
                        }
                        //CustomToast toast = new CustomToast(getContext());
                        toast.showToast(getContext(), R.string.remove_favr, Toast.LENGTH_SHORT);
                        //Toast.makeText(context, context.getResources().getString(R.string.remove_favr), Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                    } else {                      // On favoritelist viewing
                        sharedPreference.removeFavorite(context, channels.get(position));
                        v.setTag("gray");
                        if (holder.favoriteImg != null) {
                            holder.favoriteImg.setImageResource(R.drawable.favorite_cancel);
                        }
                        //CustomToast toast = new CustomToast(getContext());
                        toast.showToast(getContext(), R.string.remove_favr, Toast.LENGTH_SHORT);
                        //Toast.makeText(context, context.getResources().getString(R.string.remove_favr), Toast.LENGTH_SHORT).show();
                        channels.remove(channels.get(position));
                        notifyDataSetChanged();
                    }
                }
            }
        });


        holder.favoriteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = v.getTag().toString();
                if (tag.equalsIgnoreCase("gray")) {
                    //Channel channel = channels.get(v.getId());
                    Channel channel = channels.get(position);
                    sharedPreference.addFavorite(context, channels.get(position));
                    //CustomToast toast = new CustomToast(getContext());
                    toast.showToast(getContext(), R.string.add_favr, Toast.LENGTH_SHORT);
                    //Toast.makeText(context, context.getResources().getString(R.string.add_favr), Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                    v.setTag("yellow");
                    ((ImageView)v).setImageResource(R.drawable.favorite_set);

                } else {
                    if(favorEnable == false) {  // On channellist viewing
                        sharedPreference.removeFavorite(context, channels.get(position));
                        v.setTag("gray");
                        ((ImageView) v).setImageResource(R.drawable.favorite_cancel);
                        //CustomToast toast = new CustomToast(getContext());
                        toast.showToast(getContext(), R.string.remove_favr, Toast.LENGTH_SHORT);
                        //Toast.makeText(context, context.getResources().getString(R.string.remove_favr), Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                    }else {                      // On favoritelist viewing
                        sharedPreference.removeFavorite(context, channels.get(position));
                        v.setTag("gray");
                        ((ImageView) v).setImageResource(R.drawable.favorite_cancel);
                        //CustomToast toast = new CustomToast(getContext());
                        toast.showToast(getContext(), R.string.remove_favr, Toast.LENGTH_SHORT);
                        //Toast.makeText(context, context.getResources().getString(R.string.remove_favr), Toast.LENGTH_SHORT).show();
                        channels.remove(channels.get(position));
                        notifyDataSetChanged();
                    }
                }
            }
        });


        /*If a channel exists in shared preferences then set favorite_set drawable
         * and set a tag*/
        if (checkFavoriteItem(channel)) {
            holder.favoriteImg.setImageResource(R.drawable.favorite_set);
            holder.ll_imgbtn_favorite.setTag("yellow");
            holder.favoriteImg.setTag("yellow");

        } else {
            holder.favoriteImg.setImageResource(R.drawable.favorite_cancel);
            holder.ll_imgbtn_favorite.setTag("gray");
            holder.favoriteImg.setTag("gray");
        }

        return convertView;
    }

    /*Checks whether a particular channel exists in SharedPreferences*/
    public boolean checkFavoriteItem(Channel checkChannel) {
        boolean check = false;
        List<Channel> favorites = sharedPreference.getFavorites(context);
        if (favorites != null) {
            for (Channel channel : favorites) {
                if (channel.equals(checkChannel)) {
                    check = true;
                    break;
                }
            }
        }
        return check;
    }

    @Override
    public void add(Channel channel) {
        //super.add(channel);
        channels.add(channel);
        notifyDataSetChanged();
    }

    @Override
    public void remove(Channel channel) {
        super.remove(channel);
        channels.remove(channel);
        notifyDataSetChanged();
    }

    @Override
    protected void finalize() throws Throwable {
        free();
        super.finalize();
    }

    private void free() {
        context = null;
        channels = null;
        sharedPreference = null;
        mCursor = null;
    }

}