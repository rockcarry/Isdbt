package kr.co.fci.tv.recording;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fci.tv.FCI_TV;

import java.io.File;
import java.io.FilenameFilter;
import java.text.Collator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import kr.co.fci.tv.MainActivity;
import kr.co.fci.tv.R;
import kr.co.fci.tv.TVEVENT;
import kr.co.fci.tv.buildOption;
import kr.co.fci.tv.saves.CommonStaticData;
import kr.co.fci.tv.tvSolution.FCI_TVi;
import kr.co.fci.tv.util.TVlog;

import static kr.co.fci.tv.MainActivity.getExternalMounts;

/**
 * Created by eddy.lee on 2015-09-22.
 */
public class RecordedFileListActivity extends Activity {

    private static String TAG = "RecordedFileListActivity ";
    LinearLayout title_recordedFiles;
    List<RecordedFile> recordedFiles;

    ListView recordedListview;
    RecordedFileListAdapter recordedFileListAdapter;
    TextView noRecordedFiles;
    String deleteFilePath, fileName;

    public static RecordedFileListActivity instance;
    public static RecordedFileListActivity getInstance()
    {
        return instance;
    }
    public static String sendRecordFilename ;


    public Handler RecordedFileHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            TVEVENT event = TVEVENT.values()[msg.what];

            switch(event)
            {
                case E_RECORDED_FILE_DELETE:
                {
                    int recordedFileIndex =(int) msg.arg1;

                    RecordedFile recordedFile= recordedFiles.get(recordedFileIndex);

                                 // real file remove
                    if(buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 )  {
                        deleteFilePath = recordedFile.getFilePath() + recordedFile.getFileName();
                        thumbNailUpdate.getThhumbNailUpdateTask().remove(recordedFile.getFileName());
                    }else{
                        deleteFilePath = recordedFile.getFilePath()+recordedFile.getFileName();  // live
                    }

                    File deleteFile =  new File(deleteFilePath);
                    boolean b=  deleteFile.delete();


                    //thumbNailUpdate.getThhumbNailUpdateTask().remove(recordedFile.getFileName());

                    recordedFiles.remove(recordedFileIndex);
                    recordedFileListAdapter.notifyDataSetChanged() ;

                    if (recordedFiles != null) {
                        if (recordedFiles.size() == 0)
                            noRecordedFiles.setVisibility(View.VISIBLE);
                        else
                            noRecordedFiles.setVisibility(View.INVISIBLE);
                    }

                    TVlog.i(TAG, "E_RECORDED_FILE_DELETE Index " + recordedFileIndex + " path = " + deleteFilePath + "remove result = " + b);
                }
                break;
                case E_RECORDED_FILE_EDIT:
                {
                    recordedFileListAdapter.notifyDataSetChanged() ;
                }
                break;
                case E_RECORDED_FILE_OK:
                {

                }
                break;

                case E_RECORDED_FILE_MOVIEPLAYER:
                {

                    if (MainActivity.getInstance().sv != null && MainActivity.getInstance().sv.isShown()) {
                    MainActivity.getInstance().sv.setBackgroundColor(getResources().getColor(R.color.transparent));  // live add
                    }
                    if (buildOption.VIDEO_CODEC_TYPE == buildOption.VIDEOCODEC_TYPE_AUTODETECT) {
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

                    int recordedFileIndex =(int) msg.arg1;

                    RecordedFile recordedFile = (RecordedFile)msg.obj;


                         fileName = recordedFile.getFilePath() + recordedFile.getFileName();


                    if (buildOption.RECORDING_TYPE == buildOption.RECORDING_TYPE_TS) { //test

												CommonStaticData.recordedFileActivityShow = false;   // justin add for dongle detached
                        FCI_TVi.AVStop();
                        sendRecordFilename=fileName;
                        Intent myIntent = new Intent(RecordedFileListActivity.this, PlayBackActivity.class);
                        myIntent.putExtra("fileindex",recordedFileIndex);
                        startActivity(myIntent);

                        //MainActivity.getInstance().channelChangeStartView(false);
                        MainActivity.getInstance().channelChangeStartView(true);    // justin
                        MainActivity.getInstance().changeChannelView.setVisibility(View.VISIBLE);

                        finish();

                    }
                    else { //(buildOption.RECORDING_TYPE == buildOption.RECORDING_TYPE_MP4)
                    moveMoviePlayer(fileName);
                }
                }
                break;


            }

            super.handleMessage(msg);
        }};

    public void sendEvent(TVEVENT _Event, int _arg1, Object _obj) {
        int m;
        m = _Event.ordinal();
        Message msg = RecordedFileHandler.obtainMessage(m);
        msg.arg1 = _arg1;
        msg.obj = _obj;
        RecordedFileHandler.sendMessage(msg);
    }
 public List getRecordFileList()
 {
     return recordedFiles;
 }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        TVlog.i(TAG, " = onCreate = ");
        super.onCreate(savedInstanceState);
				CommonStaticData.recordedFileActivityShow = true;   // justin add for dongle detached
        int player_bright = CommonStaticData.brightness;
        //WindowManager.LayoutParams lp = mWindow.getAttributes();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = player_bright;
        getWindow().setAttributes(lp);

        setContentView(R.layout.recordedfilelist);
        instance = this;

        MainActivity.isMainActivity = true;

        noRecordedFiles = (TextView) findViewById(R.id.no_recorded_files);
        if (recordedFiles != null) {
            if (recordedFiles.size() == 0)
                noRecordedFiles.setVisibility(View.VISIBLE);
            else
                noRecordedFiles.setVisibility(View.INVISIBLE);

        }

        LinearLayout title_recordedFiles = (LinearLayout) findViewById(R.id.title_recordedFiles);
        title_recordedFiles.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageButton btn_back_record = (ImageButton) findViewById(R.id.btn_back_record);
        btn_back_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recordedFiles = new ArrayList<RecordedFile>();
        TextView noRecordedFiles = (TextView) findViewById(R.id.no_recorded_files);

        MainActivity.recordAndCapturePath filePath = MainActivity.getInstance().getCurrentRecordingPath();
        TVlog.i(TAG, "recordAndCapturePath filePath = "+ filePath.fullPath);

        //boolean isExternalSdAvailable = MainActivity.getInstance().externalSDAvailable();
        //boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

        String external_rootPath = "";
        String internal_rootPath = "";

        if (buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
            internal_rootPath = filePath.fullPath;
            TVlog.i(TAG, " >>>>> internal_rootPath = "+internal_rootPath);
        } else {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
        if (getExternalMounts().size() != 0) {
            //if (isSDPresent) {
            TVlog.i(TAG, " >>>>> sdcard mounted");
            external_rootPath = MainActivity.getInstance().getExternalSDPath();
            TVlog.i(TAG, " >>>>> external_rootPath = "+external_rootPath);
            internal_rootPath = MainActivity.getInstance().getInternalSDPath();
            TVlog.i(TAG, " >>>>> internal_rootPath = "+internal_rootPath);
        } else {
            TVlog.i(TAG, " >>>>> sdcard unmounted");
            internal_rootPath = filePath.fullPath;
            TVlog.i(TAG, " >>>>> internal_rootPath = "+internal_rootPath);
        }
        } else {
            TVlog.i(TAG, " >>>>> sdcard unmounted");
            internal_rootPath = filePath.fullPath;
            TVlog.i(TAG, " >>>>> internal_rootPath = "+internal_rootPath);
        }
        }


        //if(buildOption.GUI_STYLE == 2 || buildOption.GUI_STYLE == 3){

        //File[] externalDirs = getExternalFilesDirs(null);
        //if(filePath.pathName.equals("SD") && externalDirs.length > 0 && externalDirs[externalDirs.length-1] != null) {
        if (buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB
                || buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
            getRecordedFileFromfolder(filePath.fullPath);
        } else {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
        if (getExternalMounts().size() != 0) {
            getRecordedFileFromfolderTwo(filePath.fullPath, internal_rootPath+buildOption.ROOT_RECORDED_PATH+"/");
            }
            else {
                getRecordedFileFromfolder(filePath.fullPath);
            }
        } else {
            getRecordedFileFromfolder(filePath.fullPath);
        }
        }

            Collections.sort(recordedFiles, nameDescCompre);

        /*}else {
            getRecordedFileFromfolder(filePath.fullPath);
        }*/

        recordedFileListAdapter = new RecordedFileListAdapter(this, recordedFiles);
        recordedListview = (ListView)findViewById(R.id.recordedListView);
        recordedListview.setAdapter(recordedFileListAdapter);

    }

   void  getRecordedFileFromfolder(String _rootPath)
    {
        TVlog.i(TAG, "getRecordedFileFromfolder  path = " + _rootPath);

        File rootFolder = new File(_rootPath);
        File[] recordedList = rootFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (buildOption.RECORDING_TYPE == buildOption.RECORDING_TYPE_TS) {
                    return ((name.endsWith(".tv")));
                } else { //(buildOption.RECORDING_TYPE == buildOption.RECORDING_TYPE_MP4)
                return ((name.endsWith(".mp4")));
            }
            }
        });

        if (recordedList == null) {
            return;
        }

        for(int i=0; i<recordedList.length;i++) {

            // add
            Date lastModDate = new Date(recordedList[i].lastModified());
            //DateFormat formatDate = DateFormat.getDateInstance(DateFormat.SHORT);
            DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");

            String filePath = _rootPath;
            String fileName = recordedList[i].getName();
            String fileDate = formatDate.format(lastModDate);
            long fileSize =recordedList[i].length();
            int fileSizeMB =0;
            if(fileSize >= 1024*1024)
            {
                 fileSizeMB = (int)(fileSize/1024/1024);
            }

            TVlog.i(TAG, " Push recorded files  = " + i + " name =" + recordedList[i].getName() + " fileDate = " + fileDate + " fileSize = " + fileSizeMB);

            recordedFiles.add(new RecordedFile(filePath, fileName, fileDate, fileSizeMB));
            //TVlog.i("TAG", " thumbNailPath   = " + thumbNailPath);

        }
    }

    void  getRecordedFileFromfolderTwo(String _rootPath, String _secondPath)
    {
        TVlog.i(TAG, " >>>>> root_path = " + _rootPath + ", secondPath = " + _secondPath);

        File rootFolder = new File(String.valueOf(_rootPath));
        File[] recordedList = rootFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (buildOption.RECORDING_TYPE == buildOption.RECORDING_TYPE_TS) {
                    return ((name.endsWith(".tv")));
                } else { //(buildOption.RECORDING_TYPE == buildOption.RECORDING_TYPE_MP4)
                return ((name.endsWith(".mp4")));
            }
            }
        });

        if(recordedList != null){
            for(int i=0; i<recordedList.length;i++) {

                // add
                Date lastModDate = new Date(recordedList[i].lastModified());
                //DateFormat formatDate = DateFormat.getDateInstance(DateFormat.SHORT);
                DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");

                String filePath = _rootPath;
                String fileName = recordedList[i].getName();
                String fileDate = formatDate.format(lastModDate);
                long fileSize =recordedList[i].length();
                int fileSizeMB =0;
                if(fileSize >= 1024*1024)
                {
                    fileSizeMB = (int)(fileSize/1024/1024);
                }

                // TVlog.i("TAG", " Push recorded files  = " + i + " path =" + filePath+ " name =" + recordedList[i].getName() + " fileDate = " + fileDate + " fileSize = " + fileSizeMB);
                //recordedFiles.add(new RecordedFile(fileName, fileDate, fileSizeMB));
                recordedFiles.add(new RecordedFile(filePath, fileName, fileDate, fileSizeMB));
                //TVlog.i("TAG", " thumbNailPath   = " + thumbNailPath);
            }
        }

        File secondFolder = new File(String.valueOf(_secondPath));
        File[] recordedListSecond = secondFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (buildOption.RECORDING_TYPE == buildOption.RECORDING_TYPE_TS) {
                    return ((name.endsWith(".tv")));
                } else { //(buildOption.RECORDING_TYPE == buildOption.RECORDING_TYPE_MP4)
                return ((name.endsWith(".mp4")));
            }
            }
        });

        /*if (recordedList == null && recordedListSecond==null) {
            return;
        }*/

        if(recordedListSecond != null){
            for(int i=0; i<recordedListSecond.length;i++) {

                // add
                Date lastModDate = new Date(recordedListSecond[i].lastModified());
                //DateFormat formatDate = DateFormat.getDateInstance(DateFormat.SHORT);
                DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");

                String filePath2 = _secondPath;
                String fileName = recordedListSecond[i].getName();
                String fileDate = formatDate.format(lastModDate);
                long fileSize =recordedListSecond[i].length();
                int fileSizeMB =0;
                if(fileSize >= 1024*1024)
                {
                    fileSizeMB = (int)(fileSize/1024/1024);
                }

                //  TVlog.i("TAG", " Push recordedListSecond files  = " + i + " path =" + filePath2 + " name =" + recordedListSecond[i].getName() + " fileDate = " + fileDate + " fileSize = " + fileSizeMB);
                recordedFiles.add(new RecordedFile(filePath2, fileName, fileDate, fileSizeMB));
                //TVlog.i("TAG", " thumbNailPath   = " + thumbNailPath);
            }
        }
    }


    private void moveMoviePlayer(String _path)
    {
        try
        {
            Intent myIntent = new Intent(android.content.Intent.ACTION_VIEW);
            CommonStaticData.recordedFileActivityShow = false;   // justin add for dongle detached
            File file = new File(_path);
            String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
            String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            myIntent.setDataAndType(Uri.fromFile(file),mimetype);
            startActivity(myIntent);
        }
        catch (Exception e)
        {
            // TODO: handle exception
            String data = e.getMessage();
        }

    }
    @Override
    protected void onPause(){

				CommonStaticData.recordedFileActivityShow = false;   // justin add for dongle detached
        if(buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 )  {
            thumbNailUpdate.getThhumbNailUpdateTask().sendEvent(TVEVENT.E_UPDATE_THUMBNAIL, 0);
        }
		if (buildOption.RECORDING_TYPE == buildOption.RECORDING_TYPE_TS) { //test
            // FCI_TVi.AVStop();
            // TVBridge.serviceID_start(TVBridge.getCurrentChannel());
        }
        super.onPause();

    }

    @Override
    protected void onResume() {

        TVlog.i(TAG, " ===== onResume =====");

        int player_bright = CommonStaticData.brightness;
        //WindowManager.LayoutParams lp = mWindow.getAttributes();
        CommonStaticData.recordedFileActivityShow = true;   // justin add for dongle detached
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = player_bright;
        getWindow().setAttributes(lp);

        if (recordedFiles != null) {
            if (recordedFiles.size() == 0)
                noRecordedFiles.setVisibility(View.VISIBLE);
            else
                noRecordedFiles.setVisibility(View.INVISIBLE);
        }
        MainActivity.isPlayBackActivity = false;
        MainActivity.isMainActivity = true;

        if (!FCI_TVi.initiatedSol) {
            MainActivity.getInstance().changeChannelView.setVisibility(View.INVISIBLE);
            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB ||
                    buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB ||
                    buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB ||
                    buildOption.FCI_SOLUTION_MODE == buildOption.SRILANKA_USB) {
                MainActivity.getInstance().ll_file_play_mode_usb.setVisibility(View.VISIBLE);
            } else {
            MainActivity.getInstance().ll_file_play_mode.setVisibility(View.VISIBLE);
        }

        }

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void sendEvent(TVEVENT _Event) {
        int m;
        m = _Event.ordinal();
        Message msg = RecordedFileHandler.obtainMessage(m);
        RecordedFileHandler.sendMessage(msg);
    }

	   private final static Comparator<RecordedFile> nameDescCompre = new Comparator<RecordedFile>() {
        private final Collator collator = Collator.getInstance();
        @Override
        public int compare(RecordedFile rhs, RecordedFile lhs) {
            return collator.compare(lhs.getFileName(),rhs.getFileName());
        }
    };
}
