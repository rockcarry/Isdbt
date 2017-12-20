package kr.co.fci.tv;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by live.kim on 2016-03-30.
 */
public class IntroVideoView extends SurfaceView implements
        SurfaceHolder.Callback {

    private static final String TAG = "INTRO_VIDEO_CALLBACK";
    private MediaPlayer mp;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public IntroVideoView(Context context, AttributeSet attrs,
                          int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public IntroVideoView(Context context, AttributeSet attrs,
                          int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public IntroVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IntroVideoView(Context context) {
        super(context);
        init();
    }

    private void init() {
        mp = new MediaPlayer();
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        AssetFileDescriptor afd = getResources().openRawResourceFd(R.raw.intro); // your intro video file placed in raw folder named as intro.mp4
        try {
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),
                    afd.getDeclaredLength());
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        android.view.ViewGroup.LayoutParams lp = getLayoutParams();

        int screenHeight = getHeight();
        int screenWidth = getWidth();

        // this plays in full screen video
        lp.height = screenHeight;
        lp.width = screenWidth;

        setLayoutParams(lp);
        mp.setDisplay(getHolder());
        mp.setLooping(false);
        mp.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mp.stop();
    }

}
