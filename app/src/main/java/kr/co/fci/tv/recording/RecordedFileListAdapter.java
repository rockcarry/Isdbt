package kr.co.fci.tv.recording;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import kr.co.fci.tv.R;
import kr.co.fci.tv.TVEVENT;
import kr.co.fci.tv.buildOption;
import kr.co.fci.tv.setting.InputDialog;
import kr.co.fci.tv.tvSolution.FCI_TVi;
import kr.co.fci.tv.util.TVlog;

/**
 * Created by eddy.lee on 2015-09-22.
 */
public class RecordedFileListAdapter extends ArrayAdapter<RecordedFile> {

    private static String TAG = "RecordedFileListAdapter";
    private Context context;
    List<RecordedFile> recordedFiles;
    LayoutInflater inflater;

    public RecordedFileListAdapter(Context _context, List<RecordedFile> _recordedFiles) {
        super(_context, R.layout.recorded_file_item, _recordedFiles);
        context = _context;
        recordedFiles = _recordedFiles;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return recordedFiles.size();
    }

    @Override
    public RecordedFile getItem(int position) {
        return recordedFiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        ViewHolder holder =null;
        RecordedFile recfile = (RecordedFile) getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView= inflater.inflate(R.layout.recorded_file_item,parent, false);
            holder.titleview =(TextView) convertView.findViewById(R.id.titleTextView);
            holder.dateview = (TextView) convertView.findViewById(R.id.dateTextView);
            holder.sizeView = (TextView) convertView.findViewById(R.id.sizeTextview);
            holder.thubNailview =(ImageView)convertView.findViewById(R.id.thumbNailImageView);

            if (buildOption.RECORDING_TYPE == buildOption.RECORDING_TYPE_TS) {
                holder.timeview = (TextView) convertView.findViewById(R.id.timeTextView);
                holder.timeview.setBackgroundResource(R.drawable.roundlayout);
            }

            if(buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 )  {
                holder.ll_playButton = (LinearLayout) convertView.findViewById(R.id.ll_playButton);
                holder.playButton = (ImageButton) convertView.findViewById(R.id.playButton);
            } else {
                holder.thubNailview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int getPosition = (Integer) v.getTag();
                        TVlog.i(TAG, "Click Image  = " + getPosition);

                        RecordedFileListActivity.getInstance().sendEvent(TVEVENT.E_RECORDED_FILE_MOVIEPLAYER, getPosition, recordedFiles.get(getPosition));

                    }
                });
            }
            holder.ll_file_info = (LinearLayout) convertView.findViewById(R.id.ll_file_info);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 )  {
            holder.ll_playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int getPosition = (Integer) v.getTag();
                    TVlog.i(TAG, " >>>>> [1] Click Image pos  = " + getPosition);
                    RecordedFileListActivity.getInstance().sendEvent(TVEVENT.E_RECORDED_FILE_MOVIEPLAYER, getPosition, recordedFiles.get(getPosition));

                }
            });

            holder.playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int getPosition = (Integer) v.getTag();
                    TVlog.i(TAG, " >>>>> [2] Click Image pos  = " + getPosition);

                    RecordedFileListActivity.getInstance().sendEvent(TVEVENT.E_RECORDED_FILE_MOVIEPLAYER, getPosition, recordedFiles.get(getPosition));

                }
            });
        }


        holder.delete = (LinearLayout)convertView.findViewById(R.id.deleteLinearLayout);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int getPosition = (Integer) v.getTag();
                TVlog.i(TAG, " >>>>> Click delete = " + getPosition);
                RecordedFile recFile = recordedFiles.get(getPosition);
                new InputDialog(context, InputDialog.TYPE_RECORD_DELETEFILE, getPosition, null, null);

            }
        });


        if(buildOption.GUI_STYLE == 2 || buildOption.GUI_STYLE == 3) {
            holder.ll_file_info.setTag(position);
        }else{
            holder.ll_file_info.setTag(position);
            holder.ll_playButton.setTag(position);
            holder.playButton.setTag(position);
        }
        holder.delete.setTag(position);
        holder.thubNailview.setTag(position);

        holder.titleview.setText(recfile.getFileName());
        holder.dateview.setText(recfile.getDate());

        if (buildOption.RECORDING_TYPE == buildOption.RECORDING_TYPE_TS) {
            long duration = (long) FCI_TVi.TSPlayBackGetDuration(recfile.getFilePath()+recfile.getFileName());
            String displayEnd = String.format("%02d:%02d:%02d", ((duration / (1000 * 60 * 60)) % 24), ((duration / (1000 * 60)) % 60), ((duration / 1000) % 60));
            holder.timeview.setText(displayEnd);
        }

        holder.sizeView.setText(Integer.toString(recfile.getfileSize()));
        if(buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 )  {
            Bitmap thumbNailImage = thumbNailUpdate.getThhumbNailUpdateTask().getBitMap(recfile.getFileName());
            TVlog.i(TAG, " >>>>> recfile.getFileName() = "+recfile.getFileName());
            if (thumbNailImage != null) {
                holder.thubNailview.setImageBitmap(thumbNailImage);
            } else {
                holder.thubNailview.setImageResource(0);
            }
        } else {
            holder.thubNailview.setImageResource(R.drawable.ic_video);
        }

        holder.ll_file_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int getPosition = (Integer) v.getTag();
                TVlog.i(TAG, "Click Image  = " + getPosition);

                RecordedFileListActivity.getInstance().sendEvent(TVEVENT.E_RECORDED_FILE_MOVIEPLAYER, getPosition, recordedFiles.get(getPosition));

            }
        });

        return convertView;
    }


    @Override
    public void add(RecordedFile _recordedFile) {
        //super.add(channel);
        recordedFiles.add(_recordedFile);
        notifyDataSetChanged();
    }

    @Override
    public void remove(RecordedFile _recordedFile) {

        recordedFiles.remove(_recordedFile);
        notifyDataSetChanged();
    }

    class ViewHolder
    {
        EditText fileNameEdit;
        TextView titleview;

        TextView dateview ;
        TextView sizeView;
        TextView timeview;
        ImageView thubNailview;
        LinearLayout ll_playButton;
        ImageButton playButton;

        RelativeLayout ImageLayout;
        LinearLayout delete;

        LinearLayout ll_file_info;

    }

    public class CustomTextWatcher implements TextWatcher {
        private EditText mEditText;
        private int position;
        String fileName;

        public CustomTextWatcher( EditText _editText,int _position ,String _fileName) {
            mEditText = _editText;
            position = _position;
            fileName = _fileName;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            // TVlog.i(TAG, "Click beforeTextChanged = " + position + " Change = "+s);
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

            // TVlog.i(TAG, "Click onTextChanged = " + position + " Change = "+s);
        }

        public void afterTextChanged(Editable s) {

            TVlog.i(TAG, " >>>>> Click afterTextChanged = " + position + " Change = "+s);
            fileName = s.toString();
            RecordedFile recfile = (RecordedFile) getItem(position);


//            mEditText.setText(fileName);
//            recfile.setFileName(fileName);
//            notifyDataSetChanged();
        }
    }


}
