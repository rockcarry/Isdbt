package kr.co.fci.tv.recording;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;

import kr.co.fci.tv.MainActivity;
import kr.co.fci.tv.TVEVENT;
import kr.co.fci.tv.buildOption;
import kr.co.fci.tv.util.TVlog;


/**
 * Created by eddy.lee on 2015-09-24.
 */
public class thumbNailUpdate {


    private static String TAG = "thumbNailUpdate";
    private HandlerThread thumbNailUpdateHandleThread;
    private thumbNailUpdateEventHandler thumbNailUpdateEventHandler;

    private static thumbNailUpdate thumbNailUpdateTask =null;
    HashMap cacheBitmap;

    public thumbNailUpdate()
    {
        thumbNailUpdateHandleThread = new HandlerThread("thumbNailUpdate Handler", android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thumbNailUpdateHandleThread.start();
        thumbNailUpdateEventHandler = new thumbNailUpdateEventHandler(thumbNailUpdateHandleThread.getLooper());
        cacheBitmap = new HashMap<String, Bitmap>();
    }

    public static thumbNailUpdate getThhumbNailUpdateTask(){
        if(null == thumbNailUpdateTask){
            synchronized (thumbNailUpdate.class) {
                if(null == thumbNailUpdateTask){
                    thumbNailUpdateTask = new thumbNailUpdate();
                }
            }
        }
        return thumbNailUpdateTask;
    }

    class thumbNailUpdateEventHandler extends Handler {
        public thumbNailUpdateEventHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {

            TVEVENT event = TVEVENT.values()[msg.what];
            switch (event) {

                case E_UPDATE_THUMBNAIL:
                {
                    TVlog.i(TAG, " E_UPDATE_THUMBNAIL");
                    update();
                }
                break;

                default:
                    new AssertionError("no match Event");
            }
        }
    }
   public void sendEvent(TVEVENT _Event , int _time )
    {
        int m;
        m = _Event.ordinal();

        thumbNailUpdateEventHandler.sendEmptyMessageDelayed(m, _time);
    }

    void update()
    {
        //String rootPath= MainActivity.getInstance().getCurrentRecordingPath();
        MainActivity.recordAndCapturePath rootPath = MainActivity.getInstance().getCurrentRecordingPath();

        File rootFolder = new File(rootPath.fullPath);

        File[] recordedList = rootFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (buildOption.RECORDING_TYPE == buildOption.RECORDING_TYPE_TS) {
                    return ((name.endsWith(".tv")));
                } else  { //(buildOption.RECORDING_TYPE == buildOption.RECORDING_TYPE_MP4)
                return ((name.endsWith(".mp4")));
            }
            }
        });

        if(recordedList== null)return;
        if(recordedList.length >0) {
            for (int i = 0; i < recordedList.length; i++) {
                String fileName = recordedList[i].getName();
//            TVlog.i(TAG, " add Thubm nail recorded files  = " + i + " name =" + recordedList[i].getName());
                Bitmap bitmap = null;

                if (cacheBitmap.get(fileName) == null) {
                    bitmap = ThumbnailUtils.createVideoThumbnail(rootPath.fullPath + fileName, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                    if (bitmap != null) {

                        cacheBitmap.put(fileName, bitmap);
                    } else {
                        TVlog.e(TAG, " Failed to get thumbNail from  " + fileName);
                        cacheBitmap.put(fileName, null);
                    }
                } else {
                    //   TVlog.i(TAG, " Already thumbNail  = "+ recordedList[i].getName() );
                }


            }
        }

    }
    Bitmap getBitMap(String _fileName)
    {
       // TVlog.i(TAG, " getBitMap = "+_fileName);
        return (Bitmap)cacheBitmap.get(_fileName);
    }

    void remove(String _fileName)
    {
        cacheBitmap.remove(_fileName);
    }
}
