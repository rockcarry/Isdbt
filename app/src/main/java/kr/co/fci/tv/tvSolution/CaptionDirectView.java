package kr.co.fci.tv.tvSolution;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import kr.co.fci.tv.FloatingWindow;
import kr.co.fci.tv.MainActivity;
import kr.co.fci.tv.R;
import kr.co.fci.tv.TVEVENT;
import kr.co.fci.tv.chat.ChatMainActivity;

/**
 * Created by elliot.oh on 2016-01-21.
 */
//JAPAN_CAPTION[[
public class CaptionDirectView extends View {
    private final String TAG = "FCIISDBT_JVA";
    private Context mContext;
    private FrameLayout mParentLayout;
    private String mCustomText;
    private Typeface mFont;
    private TextView mTextView[];
    private int[] mTextBlink;
    private int[] mTextViewDelay;
    private ImageView mDRCSView[];
    private int[] mDRCSBlink;
    private int[] mDRCSViewDelay;
    private ImageView mOnesegBackView;
    private int mCountOfTextView;
    private int mCountOfDRCSView;
    private ImageView mPNGView;
    private boolean mIsExistPNG;
    private MediaPlayer mMP;
    private ImageView mRCSBackView;
    private boolean mIsExistRCS;

    private int mSegmentType; //1:fullseg, 0:oneseg

    private boolean mOnDrawing;

    private int mTypeCaption;
    private int mCapViewWidth;
    private int mCapViewHeight;
    private int mDispRealWidth;
    private int mDispRealHeight;
    private int prev_posX_Caption;
    private boolean isWrapUp;
    private int prev_posY_Caption;
    private int prevFontSizeHeight;
    private int capDefaultLeftMargin;
    private int capDefaultTopMargin;

    private final int TYPE_CAPTION_SUBTITLE = 0;
    private final int TYPE_CAPTION_SUPERIMPOSE = 1;

    private final int TYPE_ONESEG = 0;
    private final int TYPE_FULLSEG = 1;

    private final int MAX_TEXT_VIEW_NUM = 256;
    private final int MAX_DRCS_VIEW_NUM = 256;
    private final int CAP_DISPLAY_WIDTH = 960; //960X540 or 720X480 by specification
    private final int CAP_DISPLAY_WIDTH_MARGIN = 40;
    private final int CAP_DISPLAY_WIDTH_960 = 960;
    private final int CAP_DISPLAY_WIDTH_720 = 720;
    private final int CAP_DISPLAY_HEIGHT_540 = 540;
    private final int CAP_DISPLAY_HEIGHT_480 = 480;
    private final int DISP_FORMAT_HOR_960_540 = 7;
    private final int DISP_FORMAT_VER_960_540 = 8;
    private final int DISP_FORMAT_HOR_720_480 = 9;
    private final int DISP_FORMAT_VER_720_480 = 10;
    private final int CAPTION_DEFAULT_HEIGHT_UNIT_LEN = 46;//FONT_36_DOTS + FONT_36_DOTS/2
    // live add
    private final int CAPTION_DEFAULT_HEIGHT_UNIT_LEN_1 = 32;//FONT_24_DOTS + FONT_24_DOTS/2
    private final int CAPTION_DEFAULT_HEIGHT_UNIT_LEN_2 = 22;//FONT_16_DOTS + FONT_16_DOTS/2
    //
    private final int CAPTION_APS_PREDEFINED_HEIGHT_UNIT_LEN = 60;
    private final int MAX_DRCS_UNIT_LEN = 512+12;

    private final int FONT_TYPE_STRING_GENERIC = 0;
    private final int FONT_TYPE_DRCS_PATTERN = 1;
    private final int FONT_TYPE_BMP_PNG = 2;

    private final int FONT_SIZE_SMALL = 0; // (wxh) -> (1/2 x 1/2)
    private final int FONT_SIZE_MIDDLE = 1; // (wxh) -> (1/2 x 1)
    private final int FONT_SIZE_NORMAL = 2; // (wxh) -> (1 x 1)
    private final int FONT_SIZE_DOUBLE_WIDTH = 3; // (wxh) -> (2 x 1)
    private final int FONT_SIZE_DOUBLE_HEIGHT = 4; // (wxh) -> (1 x 2)
    private final int FONT_SIZE_DOUBLE = 5; // (wxh) -> (2 x 2)

    private final float FONT_WIDTH_RATE_ORIGINAL = 1.0f;
    private final float FONT_WIDTH_RATE_HALF = 0.5f;

    private final int FONT_16_DOTS = 16;
    private final int FONT_20_DOTS = 20;
    private final int FONT_24_DOTS = 24;
    private final int FONT_30_DOTS = 30;
    private final int FONT_36_DOTS = 36;

    private final int FLASH_INITIALIZE = 0x00;
    private final int FLASH_START_NORMAL = 0x40;
    private final int FLASH_START_INVERSE = 0x47;
    private final int FLASH_FINISH = 0x4f;

    private final int BCKGND_OPACITY_100 = 0xFFFFFFFF;
    private final int BCKGND_OPACITY_50 = ((int)((float)0xFF*0.5f) << 24) | 0xFFFFFF;
    private final int BCKGND_OPACITY_30 = ((int)((float)0xFF*0.3f) << 24) | 0xFFFFFF;
    private final int BCKGND_OPACITY_20 = ((int)((float)0xFF*0.2f) << 24) | 0xFFFFFF;
    private final int BCKGND_OPACITY_10 = ((int)((float)0xFF*0.1f) << 24) | 0xFFFFFF;
    private final int BCKGND_OPACITY_0 = 0x00000000;
    private final float VIEW_ALPHA_70 = 0.7f; //70%

    private final int PRA_INVALID_INDEX = 255;
    private final int MAX_PRA_INDEX = 13;
    private final int PRA_INDEX_0_CHIME1 = 0;
    private final int PRA_INDEX_1_CHIME2 = 1;
    private final int PRA_INDEX_2_CHIME3 = 2;
    private final int PRA_INDEX_3_CHIME4 = 3;
    private final int PRA_INDEX_4_CHIME5 = 4;
    private final int PRA_INDEX_5_BUTTON1 = 5;
    private final int PRA_INDEX_6_BUTTON2 = 6;
    private final int PRA_INDEX_7_BUTTON3 = 7;
    private final int PRA_INDEX_8_BUTTON4 = 8;
    private final int PRA_INDEX_9_BUTTON5 = 9;
    private final int PRA_INDEX_10_BUTTON6 = 10;
    private final int PRA_INDEX_11_BUTTON7 = 11;
    private final int PRA_INDEX_12_BUTTON8 = 12;
    private final int PRA_INDEX_13_ALERT = 13;

    private final int RCS_INVALID_INDEX = -1;
    private final int SDF_INVALID_HOR = -1;
    private final int SDF_INVALID_VER = -1;


    public CaptionDirectView(Context _context, FrameLayout _layout, int _width, int _height, Typeface _font, int _typeCaption) {
        super(_context);

        mContext = _context;
        mParentLayout = _layout;
        mCustomText = null;
        mFont = null;
        mTextView = null;
        mTextBlink = null;
        mTextViewDelay = null;
        mDRCSView = null;
        mDRCSBlink = null;
        mDRCSViewDelay = null;
        mOnesegBackView = null;
        mCountOfTextView = 0;
        mCountOfDRCSView = 0;
        mPNGView = null;
        mIsExistPNG = false;
        mMP = null;
        mRCSBackView = null;
        mIsExistRCS = false;

        mTypeCaption = TYPE_CAPTION_SUBTITLE;
        prev_posX_Caption = 0;
        prev_posY_Caption = 0;
        prevFontSizeHeight = 0;
        isWrapUp = false;
        capDefaultLeftMargin = 0;
        capDefaultTopMargin = 0;

        //real screen size
        mDispRealWidth = _width;
        mDispRealHeight = _height;

        if (_width > CAP_DISPLAY_WIDTH) {
            mCapViewWidth = CAP_DISPLAY_WIDTH;
            capDefaultLeftMargin = (_width - mCapViewWidth)/2;
        }
        else {
            mCapViewWidth = _width - CAP_DISPLAY_WIDTH_MARGIN;
            capDefaultLeftMargin = CAP_DISPLAY_WIDTH_MARGIN/2;
        }
        mCapViewHeight = _height;

        mFont = _font;
        if (_typeCaption <= TYPE_CAPTION_SUPERIMPOSE) {
            mTypeCaption = _typeCaption;
        }
        else {
            Log.e(TAG, "passed caption type invalid=" + _typeCaption + " => forced to TYPE_CAPTION_SUBTITLE");
            mTypeCaption = TYPE_CAPTION_SUBTITLE;
        }

        mOnDrawing = false;

        mOnesegBackView = new ImageView(mContext);
        mOnesegBackView.setVisibility(View.INVISIBLE);
        mParentLayout.addView(mOnesegBackView);

        mRCSBackView = new ImageView(mContext);
        mRCSBackView.setVisibility(View.INVISIBLE);
        mParentLayout.addView(mRCSBackView);

        mPNGView = new ImageView(mContext);
        mPNGView.setVisibility(View.INVISIBLE);
        mParentLayout.addView(mPNGView);

        mTextView = new TextView[MAX_TEXT_VIEW_NUM];
        mTextBlink = new int[MAX_TEXT_VIEW_NUM];
        mTextViewDelay = new int[MAX_TEXT_VIEW_NUM];
        for (int i=0; i < MAX_TEXT_VIEW_NUM; i++) {
            mTextView[i] = new TextView(mContext);
            mTextView[i].setVisibility(View.INVISIBLE);
            mParentLayout.addView(mTextView[i]);
            mTextBlink[i] = FLASH_INITIALIZE;
            mTextViewDelay[i] = 0;
        }

        mDRCSView = new ImageView[MAX_DRCS_VIEW_NUM];
        mDRCSBlink = new int[MAX_DRCS_VIEW_NUM];
        mDRCSViewDelay = new int[MAX_DRCS_VIEW_NUM];
        for (int i=0; i < MAX_DRCS_VIEW_NUM; i++) {
            mDRCSView[i] = new ImageView(mContext);
            mDRCSView[i].setVisibility(View.INVISIBLE);
            mParentLayout.addView(mDRCSView[i]);
            mDRCSBlink[i] = FLASH_INITIALIZE;
            mDRCSViewDelay[i] = 0;
        }
    }

    public void setText(String _text) {
        mCustomText = _text;
    }

    public void renderCaptionDirect(byte[] capData, int capLen, byte isClear, byte isEnd, int[] capInfo, int segType) {
        int fontColor = capInfo[0];
        int fontSize = capInfo[1];
        int fontType = capInfo[2];
        int apsX = capInfo[3];
        int apsY = capInfo[4];

        int flashing = capInfo[5];
        int isApsExist = capInfo[6];
        int charSize = capInfo[7];
        int bgColor = capInfo[8];
        int dispFormat = capInfo[9];
        int sdpX = capInfo[10];
        int sdpY = capInfo[11];

        int timeDelay = capInfo[12];
        int indexPRA = capInfo[13];
        int indexRCS = capInfo[14];
        int horSDF = capInfo[15];
        int verSDF = capInfo[16];
        int lenCap = capInfo[17];

        int posX = apsX + sdpX;
        int posY = apsY + sdpY;

        float sizeText = FONT_36_DOTS;
        int colorDrawing = 0;
        int colorBackground = 10;
        int fontSizeType = FONT_SIZE_NORMAL;

/*
        if (capLen > 0) {
            if (fontType == FONT_TYPE_DRCS_PATTERN) { //DRCS pattern
                Log.e(TAG, "DRCS data: posX=" + posX + ", posY=" + posY + ", size=" + fontSize + ", color=" + fontColor);
                Log.e(TAG, "DRCS data: data len=" + capLen + ", offset=" + offsetCap + ", infoLen=" + lenCap);
            }
            else {
                Log.e(TAG, "string data: posX=" + posX + ", posY=" + posY + ", size=" + fontSize + ", color=" + fontColor);
                Log.e(TAG, "string data: data len=" + capLen + ", offset=" + offsetCap + ", infoLen=" + lenCap);
            }
            if (isClear==1 || isEnd==1) {
                Log.e(TAG, "exception happened: isClear=" + isClear + ", isEnd=" + isEnd);
            }
        }
*/

        if (isClear == 1 || isEnd == 1) {
            //display subtitle & buffer clear if buffer is not empty
            if (isEnd == 1 && isClear == 0) {

                Bundle caption = new Bundle();
                if (mTypeCaption == TYPE_CAPTION_SUPERIMPOSE) {
                    caption.putString("superimpose_info", "japan_superimpose");
                    caption.putString("clear", "");
                    if (MainActivity.isMainActivity) {
                        ((MainActivity) mContext).sendEvent(TVEVENT.E_SUPERIMPOSE_NOTIFY, 0, 0, caption);
                    } else if (FloatingWindow.isFloating) {
                        if (FloatingWindow.getInstance() != null) {
                            FloatingWindow.getInstance().sendEvent(TVEVENT.E_SUPERIMPOSE_NOTIFY_FLOATING, 0, 0, caption);
                        }
                    }
                }
                else {
                    caption.putString("caption_info", "japan_caption");
                    caption.putString("clear", "");
                    if (MainActivity.isMainActivity) {
                        ((MainActivity) mContext).sendEvent(TVEVENT.E_CAPTION_NOTIFY, 0, 0, caption);
                    } else if (FloatingWindow.isFloating) {
                        FloatingWindow.getInstance().sendEvent(TVEVENT.E_CAPTION_NOTIFY_FLOATING, 0, 0, caption);
                    }
                }
            }
            else if (isClear == 1) {
                viewReset();
            }
            prev_posX_Caption = 0;
            prev_posY_Caption = 0;
            prevFontSizeHeight = 0;
            isWrapUp = false;
        }
        else {
            mOnDrawing = true;

/*
            Log.e(TAG, "disp format=" + dispFormat + ", isApsExist=" + isApsExist + ", posX=" + posX + ", posY=" + posY);
            Log.e(TAG, "font size="+charSize+", size type="+fontSize+", font color="+fontColor+", bg color="+colorBackground);
*/
            mSegmentType = segType;
            //live
            if (FloatingWindow.isFloating || ChatMainActivity.isChat) {
                if (mSegmentType != TYPE_ONESEG) {
                    mSegmentType = TYPE_ONESEG;
                }
            }
            //

            if ((mTypeCaption==TYPE_CAPTION_SUBTITLE) && (mSegmentType==TYPE_ONESEG)) {
                if ((MainActivity.dpiName.contains("mdpi")) || (MainActivity.dpiName.contains("ldpi")) || (MainActivity.screenSize.contains("large"))) {
                    if (MainActivity.isMainActivity) {
                        capDefaultTopMargin = mCapViewHeight - (CAPTION_DEFAULT_HEIGHT_UNIT_LEN_1 * 3); //up to 3 rows
                    } else if (FloatingWindow.isFloating || ChatMainActivity.isChat) {
                        capDefaultTopMargin = mCapViewHeight - (CAPTION_DEFAULT_HEIGHT_UNIT_LEN_2 * 3); //up to 3 rows
                    }
                } else {
                    if (MainActivity.isMainActivity) {
                        capDefaultTopMargin = mCapViewHeight - (CAPTION_DEFAULT_HEIGHT_UNIT_LEN * 3); //up to 3 rows
                    } else if (FloatingWindow.isFloating || ChatMainActivity.isChat) {
                        capDefaultTopMargin = mCapViewHeight - (CAPTION_DEFAULT_HEIGHT_UNIT_LEN_1 * 3); //up to 3 rows
                    }
                }
            } else {
                if (mCapViewHeight >= CAP_DISPLAY_HEIGHT_540) {
/*
                    capDefaultTopMargin = (mCapViewHeight - CAP_DISPLAY_HEIGHT_540) / 2;
*/
                    capDefaultTopMargin = (mCapViewHeight - CAP_DISPLAY_HEIGHT_540);
                    if (dispFormat == DISP_FORMAT_HOR_720_480) {
                        capDefaultTopMargin = capDefaultTopMargin + (CAP_DISPLAY_HEIGHT_540 - CAP_DISPLAY_HEIGHT_480) / 2;
                    }
                } else {
                    capDefaultTopMargin = 0;

                    if ((dispFormat == DISP_FORMAT_HOR_720_480) && (mCapViewHeight > CAP_DISPLAY_HEIGHT_480)) {
                        capDefaultTopMargin = capDefaultTopMargin + (mCapViewHeight - CAP_DISPLAY_HEIGHT_480) / 2;
                    }
                }
            }

            //vertical writing not supported yet.
            if (dispFormat == DISP_FORMAT_VER_960_540) {
                dispFormat = DISP_FORMAT_HOR_960_540;
            }
            if (dispFormat == DISP_FORMAT_VER_720_480) {
                dispFormat = DISP_FORMAT_HOR_720_480;
            }

            if (indexPRA != PRA_INVALID_INDEX) {
                //Log.e(TAG, "index of PRA = " + indexPRA);
                sendActionMsg(ACTION_TYPE_PRA_PLAY, indexPRA);
            }

            if (indexRCS != RCS_INVALID_INDEX) {
                if (horSDF != SDF_INVALID_HOR) {
                    if (verSDF != SDF_INVALID_VER) {
                        sendActionMsg(ACTION_TYPE_SET_RCS, indexRCS, sdpX, sdpY+capDefaultTopMargin, horSDF, verSDF, dispFormat);
                        //Log.e(TAG, ">>>>>>>>>>>>>>>>>>> Raster color control:color index="+indexRCS+", sdpX="+sdpX+", sdpY="+sdpY+", hor="+horSDF+", ver="+verSDF);
                    }
                }
            }

            if (isApsExist == 1) {
                if (mCapViewHeight < CAP_DISPLAY_HEIGHT_540) {
                    posY = apsY;
                    if (posY >= sdpY) {
                        posY -= sdpY;
                    }
                    /*
                    if (posY >= CAPTION_APS_PREDEFINED_HEIGHT_UNIT_LEN) {
                        posY -= CAPTION_APS_PREDEFINED_HEIGHT_UNIT_LEN;
                    }*/
                }
                if (dispFormat == DISP_FORMAT_HOR_960_540) {
                    posX = (int) ((float) posX * ((float)mCapViewWidth / (float)CAP_DISPLAY_WIDTH_960));
                } else if (dispFormat == DISP_FORMAT_HOR_720_480) {
                    posX = (int) ((float) posX * ((float)mCapViewWidth / (float)CAP_DISPLAY_WIDTH_720));
                } else { // DISP_FORMAT_VER_960_540, DISP_FORMAT_VER_720_480

                }
            }

            //16, 20, 24, 30, 36
            switch (charSize) {
                case FONT_36_DOTS:
                default:
                    if ((MainActivity.dpiName.contains("mdpi")) || (MainActivity.dpiName.contains("ldpi")) || (MainActivity.screenSize.contains("large"))) {
                        if (MainActivity.isMainActivity) {
                            sizeText = FONT_24_DOTS;
                        } else if (FloatingWindow.isFloating || ChatMainActivity.isChat) {
                            //sizeText = sizeText / 2;
                            sizeText = FONT_16_DOTS;
                        }
                    } else {
                        if (MainActivity.isMainActivity) {
                            sizeText = FONT_36_DOTS;
                        } else if (FloatingWindow.isFloating || ChatMainActivity.isChat) {
                            //sizeText = sizeText / 2;
                            sizeText = FONT_24_DOTS;
                        }
                    }
                    break;

                case FONT_16_DOTS:
                case FONT_20_DOTS:
                case FONT_24_DOTS:
                case FONT_30_DOTS:
                    sizeText = charSize;
                    break;
            }

            switch (fontSize) {
                case FONT_SIZE_SMALL: //small: (wxh) -> (1/2 x 1/2)
                case FONT_SIZE_MIDDLE: //middle: (wxh) -> (1/2 x 1)
                    sizeText = sizeText / 2;
                    fontSizeType = fontSize;
                    break;
                case FONT_SIZE_NORMAL: //normal: (wxh) -> (1x1)
                default:
                    fontSizeType = FONT_SIZE_NORMAL;
                    break;
            }

            //drawing color
            switch (fontColor) {
                case 0:
                case 8:
                default:
                    colorDrawing = Color.WHITE;
                    break;
                case 1:
                    colorDrawing = Color.BLACK;
                    break;
                case 2:
                    colorDrawing = Color.RED;
                    break;
                case 3:
                    colorDrawing = Color.GREEN;
                    break;
                case 4:
                    colorDrawing = Color.YELLOW;
                    break;
                case 5:
                    colorDrawing = Color.BLUE;
                    break;
                case 6:
                    colorDrawing = Color.MAGENTA;
                    break;
                case 7:
                    colorDrawing = Color.CYAN;
                case 9:
                    break;
            }

            //background color
            switch (bgColor) {
                case 1:
                    colorBackground = Color.BLACK;
                    break;
                case 2:
                    colorBackground = Color.RED;
                    break;
                case 3:
                    colorBackground = Color.GREEN;
                    break;
                case 4:
                    colorBackground = Color.YELLOW;
                    break;
                case 5:
                    colorBackground = Color.BLUE;
                    break;
                case 6:
                    colorBackground = Color.MAGENTA;
                    break;
                case 7:
                    colorBackground = Color.CYAN;
                    break;
                case 8:
                    colorBackground = Color.WHITE;
                    break;
                case 9:
                    colorBackground = Color.GRAY;
                    break;
                case 10:
                default:
                    colorBackground = Color.TRANSPARENT;
                    break;
            }

            //setup paint attributes
            Paint pnt = new Paint();

            pnt.setTypeface(mFont);
            //pnt.setAntiAlias(true);
            //pnt.setTextSize(sizeText);
            pnt.setColor(colorDrawing);
            pnt.setStrokeWidth(1);


            try {
                byte[] capRawData = new byte[capLen];
                System.arraycopy(capData, 0, capRawData, 0, capLen);
                String tmpStr = new String(capData, "UTF-16");

                if (tmpStr.length() > 0) {
                    //calibrating Y coordination
                    if (mSegmentType == TYPE_ONESEG) {
                        if ((MainActivity.dpiName.contains("mdpi")) || (MainActivity.dpiName.contains("ldpi")) || (MainActivity.screenSize.contains("large"))) {
                            if (MainActivity.isMainActivity) {
                                if ((posY > prev_posY_Caption) && (mCapViewHeight >= posY + capDefaultTopMargin + CAPTION_DEFAULT_HEIGHT_UNIT_LEN_1)) {
                                    prev_posY_Caption = posY;
                                    prev_posX_Caption = 0;
                                }
                            } else if (FloatingWindow.isFloating || ChatMainActivity.isChat) {
                                if ((posY > prev_posY_Caption) && (mCapViewHeight >= posY + capDefaultTopMargin + CAPTION_DEFAULT_HEIGHT_UNIT_LEN_2)) {
                                    prev_posY_Caption = posY;
                                    prev_posX_Caption = 0;
                                }
                            }
                        } else {
                            if (MainActivity.isMainActivity) {
                                if ((posY > prev_posY_Caption) && (mCapViewHeight >= posY + capDefaultTopMargin + CAPTION_DEFAULT_HEIGHT_UNIT_LEN)) {
                                    prev_posY_Caption = posY;
                                    prev_posX_Caption = 0;
                                }
                            } else if (FloatingWindow.isFloating || ChatMainActivity.isChat) {
                                if ((posY > prev_posY_Caption) && (mCapViewHeight >= posY + capDefaultTopMargin + CAPTION_DEFAULT_HEIGHT_UNIT_LEN_1)) {
                                    prev_posY_Caption = posY;
                                    prev_posX_Caption = 0;
                                }
                            }
                        }
                    }

                    if (fontType == FONT_TYPE_DRCS_PATTERN) {
                        int numOfDRCS = 0;
                        numOfDRCS = capLen / MAX_DRCS_UNIT_LEN;
                        if (numOfDRCS > 0 && (capLen % MAX_DRCS_UNIT_LEN == 0)) {
                            //Log.e(TAG, "valid DRCS parameter: capLen="+capLen+", num of DRCS="+numOfDRCS);
                            int zz;
                            for (zz = 0; zz < numOfDRCS; zz++) {
                                byte[] oneDRCS = new byte[MAX_DRCS_UNIT_LEN];
                                System.arraycopy(capRawData, zz * MAX_DRCS_UNIT_LEN, oneDRCS, 0, MAX_DRCS_UNIT_LEN);
                                //depth:4B, width:4B, height:4B
                                int dcrsDepth = (oneDRCS[0] & 0xFF) | ((oneDRCS[1] << 8) & 0xFF00) | ((oneDRCS[2] << 16) & 0xFF0000) | ((oneDRCS[3] << 24) & 0xFF000000);
                                int drcsWidth = (oneDRCS[4] & 0xFF) | ((oneDRCS[5] << 8) & 0xFF00) | ((oneDRCS[6] << 16) & 0xFF0000) | ((oneDRCS[7] << 24) & 0xFF000000);
                                int drcsHeight = (oneDRCS[8] & 0xFF) | ((oneDRCS[9] << 8) & 0xFF00) | ((oneDRCS[10] << 16) & 0xFF0000) | ((oneDRCS[11] << 24) & 0xFF000000);
                                int bitSize = 0;
                                bitSize = (2 + dcrsDepth) / 2;
                                bitSize += ((2 + dcrsDepth) % 2 == 1) ? 1 : 0;
                                int bytesWidth = bitSize * drcsWidth / 8;
                                int totalDRCSSize = bytesWidth * drcsHeight;
                                //Log.e(TAG, "DRCS: index=" + zz + ", width=" + drcsWidth + ", height=" + drcsHeight + ", bitSize=" + bitSize+": total size="+totalDRCSSize);
                                if (totalDRCSSize > MAX_DRCS_UNIT_LEN-12 || totalDRCSSize <= 0) {
                                    continue;
                                }
                                byte[] buffer = new byte[totalDRCSSize];
                                int i, j, k;
                                int paintPosX, paintPosY, backPaintPosX, backPaintPosY;
                                System.arraycopy(oneDRCS, 12, buffer, 0, totalDRCSSize);


                                //calibrating coordination
                                if (mSegmentType == TYPE_ONESEG) {
                                    if ((MainActivity.dpiName.contains("mdpi")) || (MainActivity.dpiName.contains("ldpi")) || (MainActivity.screenSize.contains("large"))) {
                                        if (MainActivity.isMainActivity) {
                                            if (prev_posX_Caption + sizeText >= mCapViewWidth) {
                                                prev_posY_Caption += CAPTION_DEFAULT_HEIGHT_UNIT_LEN_1;
                                                prev_posX_Caption = 0;
                                            }
                                        } else if (FloatingWindow.isFloating || ChatMainActivity.isChat) {
                                            if (prev_posX_Caption + sizeText >= mCapViewWidth) {
                                                prev_posY_Caption += CAPTION_DEFAULT_HEIGHT_UNIT_LEN_2;
                                                prev_posX_Caption = 0;
                                            }
                                        }
                                    } else {
                                        if (MainActivity.isMainActivity) {
                                            if (prev_posX_Caption + sizeText >= mCapViewWidth) {
                                                prev_posY_Caption += CAPTION_DEFAULT_HEIGHT_UNIT_LEN;
                                                prev_posX_Caption = 0;
                                            }
                                        } else if (FloatingWindow.isFloating || ChatMainActivity.isChat) {
                                            if (prev_posX_Caption + sizeText >= mCapViewWidth) {
                                                prev_posY_Caption += CAPTION_DEFAULT_HEIGHT_UNIT_LEN_1;
                                                prev_posX_Caption = 0;
                                            }
                                        }
                                    }
                                }

                                paintPosX = prev_posX_Caption;
                                backPaintPosX = paintPosX;
                                paintPosY = prev_posY_Caption + capDefaultTopMargin;
                                backPaintPosY = paintPosY;

                                //setup paint attributes
                                Bitmap drcsBmp = Bitmap.createBitmap(drcsWidth, drcsHeight, Bitmap.Config.ARGB_8888);
                                Canvas drcsCanvas = new Canvas(drcsBmp);

                                for (k = 0, j = 0; j < drcsHeight; j++) {
                                    if (bitSize == 1) {
                                        for (i = 0; i < bytesWidth; i++) {
                                            if ((buffer[k + i] & 0x80) == 0x80) {
                                                drcsCanvas.drawPoint((8 * i) + 0, j, pnt);
                                            }
                                            if ((buffer[k + i] & 0x40) == 0x40) {
                                                drcsCanvas.drawPoint((8 * i) + 1, j, pnt);
                                            }
                                            if ((buffer[k + i] & 0x20) == 0x20) {
                                                drcsCanvas.drawPoint((8 * i) + 2, j, pnt);
                                            }
                                            if ((buffer[k + i] & 0x10) == 0x10) {
                                                drcsCanvas.drawPoint((8 * i) + 3, j, pnt);
                                            }
                                            if ((buffer[k + i] & 0x08) == 0x08) {
                                                drcsCanvas.drawPoint((8 * i) + 4, j, pnt);
                                            }
                                            if ((buffer[k + i] & 0x04) == 0x04) {
                                                drcsCanvas.drawPoint((8 * i) + 5, j, pnt);
                                            }
                                            if ((buffer[k + i] & 0x02) == 0x02) {
                                                drcsCanvas.drawPoint((8 * i) + 6, j, pnt);
                                            }
                                            if ((buffer[k + i] & 0x01) == 0x01) {
                                                drcsCanvas.drawPoint((8 * i) + 7, j, pnt);
                                            }
                                        }
                                        k += bytesWidth;
                                    } else if (bitSize == 2) {
                                        for (i = 0; i < bytesWidth; i++) {
                                            if ((buffer[k + i] & 0xC0) != 0) {
                                                drcsCanvas.drawPoint((4 * i) + 0, j, pnt);
                                            }
                                            if ((buffer[k + i] & 0x30) != 0) {
                                                drcsCanvas.drawPoint((4 * i) + 1, j, pnt);
                                            }
                                            if ((buffer[k + i] & 0x0C) != 0) {
                                                drcsCanvas.drawPoint((4 * i) + 2, j, pnt);
                                            }
                                            if ((buffer[k + i] & 0x03) != 0) {
                                                drcsCanvas.drawPoint((4 * i) + 3, j, pnt);
                                            }
                                        }
                                        k += bytesWidth;
                                    }
                                }
                                if (mCountOfDRCSView < MAX_DRCS_VIEW_NUM) {
                                    if (mSegmentType == TYPE_FULLSEG) {
                                        if (posY == prev_posY_Caption) {
                                            backPaintPosX = prev_posX_Caption;
                                        }
                                        else {
                                            backPaintPosX = posX;
                                        }
                                        backPaintPosY = posY + capDefaultTopMargin;
                                    }
                                    sendActionMsg(ACTION_TYPE_SET_DRCS, backPaintPosX, backPaintPosY, sizeText, fontSizeType, mCountOfDRCSView, colorDrawing, colorBackground, flashing, timeDelay, drcsBmp);
                                    mCountOfDRCSView++;
                                }
                                if (mSegmentType == TYPE_ONESEG) {
                                    prev_posX_Caption = prev_posX_Caption + (int)sizeText; //+posX
                                }
                                else { //fullseg
                                    prev_posX_Caption = backPaintPosX + (int)sizeText;
                                    prev_posY_Caption = posY;
                                }
                            }
                        }
                        else {
                            Log.e(TAG, "invalid DRCS parameter: capLen="+capLen+", num of DRCS="+numOfDRCS);
                        }
                    }
                    else if (fontType == FONT_TYPE_STRING_GENERIC){
                        StringBuffer sameLineString = new StringBuffer();
                        int textX, textY = 0;
                        if (mSegmentType == TYPE_ONESEG) {
                            for (int i = 0; i < tmpStr.length(); i++) {
                                if (prev_posX_Caption + sizeText >= mCapViewWidth) {
                                    if (sameLineString != null && mCountOfTextView < MAX_TEXT_VIEW_NUM) {
                                        String dispString = sameLineString.toString();
                                        textX = prev_posX_Caption - dispString.length() * (int) sizeText;
                                        if ((MainActivity.dpiName.contains("mdpi")) || (MainActivity.dpiName.contains("ldpi")) || (MainActivity.screenSize.contains("large"))) {
                                            if (MainActivity.isMainActivity) {
                                                textY = prev_posY_Caption + capDefaultTopMargin + CAPTION_DEFAULT_HEIGHT_UNIT_LEN_1;
                                            } else if (FloatingWindow.isFloating || ChatMainActivity.isChat) {
                                                textY = prev_posY_Caption + capDefaultTopMargin + CAPTION_DEFAULT_HEIGHT_UNIT_LEN_2;
                                            }
                                        } else {
                                            if (MainActivity.isMainActivity) {
                                                textY = prev_posY_Caption + capDefaultTopMargin + CAPTION_DEFAULT_HEIGHT_UNIT_LEN;
                                            } else if (FloatingWindow.isFloating || ChatMainActivity.isChat) {
                                                textY = prev_posY_Caption + capDefaultTopMargin + CAPTION_DEFAULT_HEIGHT_UNIT_LEN_1;
                                            }
                                        }

                                        sendActionMsg(ACTION_TYPE_SET_TEXT, textX, textY, sizeText, fontSizeType, dispString, mCountOfTextView, colorDrawing, colorBackground, flashing, timeDelay, dispFormat);
                                        //Log.e(TAG, "[TIME][1111] index="+mCountOfTextView+", time delay=" + timeDelay);
                                        mCountOfTextView++;
                                        sameLineString = null;
                                        sameLineString = new StringBuffer();
                                    }
                                    prev_posX_Caption = 0;
                                    if ((MainActivity.dpiName.contains("mdpi")) || (MainActivity.dpiName.contains("ldpi")) || (MainActivity.screenSize.contains("large"))) {
                                        if (MainActivity.isMainActivity) {
                                            prev_posY_Caption += CAPTION_DEFAULT_HEIGHT_UNIT_LEN_1;
                                        } else if (FloatingWindow.isFloating || ChatMainActivity.isChat) {
                                            prev_posY_Caption += CAPTION_DEFAULT_HEIGHT_UNIT_LEN_2;
                                        }
                                    } else {
                                        if (MainActivity.isMainActivity) {
                                            prev_posY_Caption += CAPTION_DEFAULT_HEIGHT_UNIT_LEN;
                                        } else if (FloatingWindow.isFloating || ChatMainActivity.isChat) {
                                            prev_posY_Caption += CAPTION_DEFAULT_HEIGHT_UNIT_LEN_1;
                                        }
                                    }
                                }
                                String oneString = String.valueOf(tmpStr.charAt(i));
                                sameLineString.append(oneString);

                                prev_posX_Caption = prev_posX_Caption + (int) sizeText; //+posX

                                //end of index
                                if (i == tmpStr.length() - 1) {
                                    if (sameLineString != null && mCountOfTextView < MAX_TEXT_VIEW_NUM) {
                                        String dispString = sameLineString.toString();
                                        textX = prev_posX_Caption - dispString.length() * (int) sizeText;
                                        if ((MainActivity.dpiName.contains("mdpi")) || (MainActivity.dpiName.contains("ldpi")) || (MainActivity.screenSize.contains("large"))) {
                                            if (MainActivity.isMainActivity) {
                                                textY = prev_posY_Caption + capDefaultTopMargin + CAPTION_DEFAULT_HEIGHT_UNIT_LEN_1;
                                            } else if (FloatingWindow.isFloating || ChatMainActivity.isChat) {
                                                textY = prev_posY_Caption + capDefaultTopMargin + CAPTION_DEFAULT_HEIGHT_UNIT_LEN_2;
                                            }
                                        } else {
                                            if (MainActivity.isMainActivity) {
                                                textY = prev_posY_Caption + capDefaultTopMargin + CAPTION_DEFAULT_HEIGHT_UNIT_LEN;
                                            } else if (FloatingWindow.isFloating || ChatMainActivity.isChat) {
                                                textY = prev_posY_Caption + capDefaultTopMargin + CAPTION_DEFAULT_HEIGHT_UNIT_LEN_1;
                                            }
                                        }

                                        sendActionMsg(ACTION_TYPE_SET_TEXT, textX, textY, sizeText, fontSizeType, dispString, mCountOfTextView, colorDrawing, colorBackground, flashing, timeDelay, dispFormat);
                                        mCountOfTextView++;
                                    }
                                }
                            }
                        }
                        else { //fullseg
                            if (tmpStr.length() > 0 && mCountOfTextView < MAX_TEXT_VIEW_NUM) {
                                if (isWrapUp == true) {
                                    posY = prev_posY_Caption;
                                    isWrapUp = false;
                                }
                                if (posY == prev_posY_Caption) {
                                    textX = prev_posX_Caption;
                                }
                                else if (posY < prev_posY_Caption) {
                                    textX = posX;
                                }
                                else { //posY > prev_posY_Caption
                                    textX = posX;
                                    if (dispFormat==DISP_FORMAT_HOR_960_540 || dispFormat==DISP_FORMAT_HOR_720_480) {
                                        if (posY < prev_posY_Caption + prevFontSizeHeight) {
                                            posY = prev_posY_Caption + prevFontSizeHeight;
                                        }
                                    }
                                    else { ////DISP_FORMAT_VER_960_540, DISP_FORMAT_VER_720_480
                                        Log.e(TAG, ">>> disp format exception >>>0000");
                                    }
                                }
                                if (dispFormat==DISP_FORMAT_HOR_960_540 || dispFormat==DISP_FORMAT_HOR_720_480) {
                                    if ((MainActivity.dpiName.contains("mdpi")) || (MainActivity.dpiName.contains("ldpi")) || (MainActivity.screenSize.contains("large"))) {
                                        if (MainActivity.isMainActivity) {
                                            textY = posY + capDefaultTopMargin + CAPTION_DEFAULT_HEIGHT_UNIT_LEN_1;
                                        } else if (FloatingWindow.isFloating || ChatMainActivity.isChat) {
                                            textY = posY + capDefaultTopMargin + CAPTION_DEFAULT_HEIGHT_UNIT_LEN_2;
                                        }
                                    } else {
                                        if (MainActivity.isMainActivity) {
                                            textY = posY + capDefaultTopMargin + CAPTION_DEFAULT_HEIGHT_UNIT_LEN;
                                        } else if (FloatingWindow.isFloating || ChatMainActivity.isChat) {
                                            textY = posY + capDefaultTopMargin + CAPTION_DEFAULT_HEIGHT_UNIT_LEN_1;
                                        }
                                    }
                                }
                                else { //DISP_FORMAT_VER_960_540, DISP_FORMAT_VER_720_480
                                    if ((MainActivity.dpiName.contains("mdpi")) || (MainActivity.dpiName.contains("ldpi")) || (MainActivity.screenSize.contains("large"))) {
                                        if (MainActivity.isMainActivity) {
                                            textY = posY + capDefaultTopMargin + CAPTION_DEFAULT_HEIGHT_UNIT_LEN_1;
                                        } else if (FloatingWindow.isFloating || ChatMainActivity.isChat) {
                                            textY = posY + capDefaultTopMargin + CAPTION_DEFAULT_HEIGHT_UNIT_LEN_2;
                                        }
                                    } else {
                                        if (MainActivity.isMainActivity) {
                                            textY = posY + capDefaultTopMargin + CAPTION_DEFAULT_HEIGHT_UNIT_LEN;
                                        } else if (FloatingWindow.isFloating || ChatMainActivity.isChat) {
                                            textY = posY + capDefaultTopMargin + CAPTION_DEFAULT_HEIGHT_UNIT_LEN_1;
                                        }
                                    }
                                }
                                if (prev_posY_Caption != 0 && posY != prev_posY_Caption && posY < (prev_posY_Caption + prevFontSizeHeight)) {
                                    //Log.e(TAG, ">>> pos exception >>> prev Y bottom="+(prev_posY_Caption+prevFontSizeHeight)+", new Y="+posY+", prev font height="+prevFontSizeHeight);
                                }
                                int orgTextWidth = (int)(textX + tmpStr.length()*sizeText);
                                int tempFontHeight = 0;
                                if ((MainActivity.dpiName.contains("mdpi")) || (MainActivity.dpiName.contains("ldpi")) || (MainActivity.screenSize.contains("large"))) {
                                    if (MainActivity.isMainActivity) {
                                        tempFontHeight = CAPTION_DEFAULT_HEIGHT_UNIT_LEN_1;
                                    } else if (FloatingWindow.isFloating || ChatMainActivity.isChat) {
                                        tempFontHeight = CAPTION_DEFAULT_HEIGHT_UNIT_LEN_2;
                                    }
                                } else {
                                    if (MainActivity.isMainActivity) {
                                        tempFontHeight = CAPTION_DEFAULT_HEIGHT_UNIT_LEN;
                                    } else if (FloatingWindow.isFloating || ChatMainActivity.isChat) {
                                        tempFontHeight = CAPTION_DEFAULT_HEIGHT_UNIT_LEN_1;
                                    }
                                }

                                if (fontSizeType == FONT_SIZE_MIDDLE) {
                                    tempFontHeight = (int) (sizeText * 2 + (sizeText * 2) / 3);
                                }
                                else {
                                    tempFontHeight = (int) (sizeText + sizeText / 3);
                                }
                                int realCapWidth = capDefaultLeftMargin + mCapViewWidth - FONT_36_DOTS;
                                //check line wraparound
                                if (realCapWidth <= orgTextWidth) {
                                    //Log.e(TAG, "[1111] pos exception >>> apsX="+apsX+ ", sdpX="+sdpX+", apsY="+apsY+", sdpY="+sdpY);
                                    //Log.e(TAG, "[1111] pos exception >>> capDefaultLeftMargin="+capDefaultLeftMargin+", max disp width="+realCapWidth);

                                    for (int i = 0; i < tmpStr.length() && (mCountOfTextView < MAX_TEXT_VIEW_NUM); i++) {
                                        if (realCapWidth <= (textX + sizeText)) {
                                            textX = apsX + sdpX;
                                            posY += tempFontHeight;
                                            if ((MainActivity.dpiName.contains("mdpi")) || (MainActivity.dpiName.contains("ldpi")) || (MainActivity.screenSize.contains("large"))) {
                                                if (MainActivity.isMainActivity) {
                                                    textY = posY + capDefaultTopMargin + CAPTION_DEFAULT_HEIGHT_UNIT_LEN_1;
                                                } else if (FloatingWindow.isFloating || ChatMainActivity.isChat) {
                                                    textY = posY + capDefaultTopMargin + CAPTION_DEFAULT_HEIGHT_UNIT_LEN_2;
                                                }
                                            } else {
                                                if (MainActivity.isMainActivity) {
                                                    textY = posY + capDefaultTopMargin + CAPTION_DEFAULT_HEIGHT_UNIT_LEN;
                                                } else if (FloatingWindow.isFloating || ChatMainActivity.isChat) {
                                                    textY = posY + capDefaultTopMargin + CAPTION_DEFAULT_HEIGHT_UNIT_LEN_1;
                                                }
                                            }

                                            sendActionMsg(ACTION_TYPE_SET_TEXT, textX, textY, sizeText, fontSizeType, Character.toString(tmpStr.charAt(i)), mCountOfTextView, colorDrawing, colorBackground, flashing, timeDelay, dispFormat);
                                            //Log.e(TAG, "out of range >>> textX="+textX+", textY="+textY);
                                        }
                                        else {
                                            sendActionMsg(ACTION_TYPE_SET_TEXT, textX, textY, sizeText, fontSizeType, Character.toString(tmpStr.charAt(i)), mCountOfTextView, colorDrawing, colorBackground, flashing, timeDelay, dispFormat);
                                            //Log.e(TAG, "in range >>> textX="+textX+", textY="+textY);
                                        }
                                        textX += sizeText;
                                        mCountOfTextView++;
                                    }
                                    isWrapUp = true;
                                    prev_posX_Caption = textX;
                                }
                                else {
                                    sendActionMsg(ACTION_TYPE_SET_TEXT, textX, textY, sizeText, fontSizeType, tmpStr, mCountOfTextView, colorDrawing, colorBackground, flashing, timeDelay, dispFormat);
                                    mCountOfTextView++;
                                    prev_posX_Caption = (int)(textX + tmpStr.length()*sizeText);
                                }
                                prev_posY_Caption = posY;
                                prevFontSizeHeight = tempFontHeight;
                            }
                        }
                    }
                    else if (fontType == FONT_TYPE_BMP_PNG) {
//                        Log.e(TAG, "PNG recevied: len="+capLen);
                        sendActionMsg(ACTION_TYPE_SET_PNG, posX+capDefaultLeftMargin, posY+capDefaultTopMargin, capRawData);
                    }
                    else {
                        Log.e(TAG, "not supported data type!!!");
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            mOnDrawing = false;
        }
    }

    @Override
    public void onDraw(Canvas _canvas) {
        if ( mCustomText != null) {
            if (mCustomText.length() > 0) {
                if (mSegmentType == TYPE_ONESEG) {
                    sendActionMsg(ACTION_TYPE_BACKGROUND_VISIBLE);
                }

                if (mIsExistRCS == true) {
                    if (mRCSBackView != null) {
                        sendActionMsg(ACTION_TYPE_RCS_VISIBLE);
                    }
                    //mIsExistRCS = false;
                }

                for (int i=0; i < mCountOfTextView; i++) {
                    if (mTextView[i] != null) {
                        sendActionMsg(ACTION_TYPE_TEXT_VISIBLE, i);
                    }
                }
                for (int i=0; i < mCountOfDRCSView; i++) {
                    if (mDRCSView[i] != null) {
                        sendActionMsg(ACTION_TYPE_DRCS_VISIBLE, i);
                    }
                }
                if (mTypeCaption == TYPE_CAPTION_SUPERIMPOSE && mIsExistPNG == true) {
                    if (mPNGView != null) {
                        sendActionMsg(ACTION_TYPE_PNG_VISIBLE);
                    }
                    mIsExistPNG = false;
                }
            }
            else {
                viewReset();
            }
        }
    }

    private Animation getBlinkAnimation(){
        Animation animation = new AlphaAnimation(1, 0);         // Change alpha from fully visible to invisible
        animation.setDuration(1000);                             // duration - one second
        animation.setInterpolator(new LinearInterpolator());    // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE);                            // Repeat animation infinitely
        animation.setRepeatMode(Animation.RESTART);             // Reverse animation at the end so the button will fade back in

        return animation;
    }

    private void viewReset() {
        if (mOnDrawing == false) {
            sendActionMsg(ACTION_TYPE_BACKGROUND_INVISIBLE);

            for (int i=0; i < mCountOfTextView; i++) {
                sendActionMsg(ACTION_TYPE_TEXT_INVISIBLE, i);
            }
            mCountOfTextView = 0;
            for (int i=0; i < mCountOfDRCSView; i++) {
                sendActionMsg(ACTION_TYPE_DRCS_INVISIBLE, i);
            }
            mCountOfDRCSView = 0;

            sendActionMsg(ACTION_TYPE_PNG_INVISIBLE);
            sendActionMsg(ACTION_TYPE_RCS_INVISIBLE);
        }
        sendActionMsg(ACTION_TYPE_PRA_STOP);
    }

    private static final String ACTION_KEY_TYPE = "ActionKeyType";
    private static final String ACTION_PARAM_TEXT_INDEX = "ActionParamTextIndex";
    private static final String ACTION_PARAM_TEXT_START_X = "ActionParamTextStartX";
    private static final String ACTION_PARAM_TEXT_START_Y = "ActionParamTextStartY";
    private static final String ACTION_PARAM_TEXT_SIZE = "ActionParamTextSize";
    private static final String ACTION_PARAM_TEXT_FONT_SIZE_TYPE = "ActionParamTextFontSizeType";
    private static final String ACTION_PARAM_TEXT_STRING = "ActionParamTextString";
    private static final String ACTION_PARAM_TEXT_COLOR = "ActionParamTextColor";
    private static final String ACTION_PARAM_TEXT_COLOR_BG = "ActionParamTextColorBg";
    private static final String ACTION_PARAM_TEXT_FLASHING = "ActionParamTextFlashing";
    private static final String ACTION_PARAM_TEXT_TIME_DELAY = "ActionParamTextTimeDelay";
    private static final String ACTION_PARAM_DRCS_INDEX = "ActionParamDRCSIndex";
    private static final String ACTION_PARAM_DRCS_SIZE = "ActionParamDRCSSize";
    private static final String ACTION_PARAM_DRCS_START_X = "ActionParamDRCSStartX";
    private static final String ACTION_PARAM_DRCS_START_Y = "ActionParamDRCSStartY";
    private static final String ACTION_PARAM_DRCS_FONT_SIZE_TYPE = "ActionParamDRCSFontSizeType";
    private static final String ACTION_PARAM_DRCS_COLOR = "ActionParamDRCSColor";
    private static final String ACTION_PARAM_DRCS_COLOR_BG = "ActionParamDRCSColorBg";
    private static final String ACTION_PARAM_DRCS_FLASHING = "ActionParamDRCSFlashing";
    private static final String ACTION_PARAM_DRCS_TIME_DELAY = "ActionParamDRCSTimeDelay";
    private static final String ACTION_PARAM_BITMAP = "ActionParamBitmap";
    private static final String ACTION_PARAM_PNG_START_X = "ActionParamPNGStartX";
    private static final String ACTION_PARAM_PNG_START_Y = "ActionParamPNGStartY";
    private static final String ACTION_PARAM_PNG = "ActionParamPNG";
    private static final String ACTION_PARAM_PRA_INDEX = "ActionParamPRAIndex";
    private static final String ACTION_PARAM_RCS_INDEX = "ActionParamRCSIndex";
    private static final String ACTION_PARAM_RCS_START_X = "ActionParamRCSStartX";
    private static final String ACTION_PARAM_RCS_START_Y = "ActionParamRCSStartY";
    private static final String ACTION_PARAM_RCS_HOR = "ActionParamRCSHor";
    private static final String ACTION_PARAM_RCS_VER = "ActionParamRCSVer";
    private static final String ACTION_PARAM_DISP_FORMAT = "ActionParamDispFormat";


    private static final int ACTION_TYPE_SET_TEXT = 0;
    private static final int ACTION_TYPE_TEXT_VISIBLE = 1;
    private static final int ACTION_TYPE_TEXT_INVISIBLE = 2;
    private static final int ACTION_TYPE_SET_DRCS = 3;
    private static final int ACTION_TYPE_DRCS_VISIBLE = 4;
    private static final int ACTION_TYPE_DRCS_INVISIBLE = 5;
    private static final int ACTION_TYPE_BACKGROUND_VISIBLE = 6;
    private static final int ACTION_TYPE_BACKGROUND_INVISIBLE = 7;
    private static final int ACTION_TYPE_SET_PNG = 8;
    private static final int ACTION_TYPE_PNG_VISIBLE = 9;
    private static final int ACTION_TYPE_PNG_INVISIBLE = 10;
    private static final int ACTION_TYPE_PRA_PLAY = 11;
    private static final int ACTION_TYPE_PRA_STOP = 12;
    private static final int ACTION_TYPE_SET_RCS = 13;
    private static final int ACTION_TYPE_RCS_VISIBLE = 14;
    private static final int ACTION_TYPE_RCS_INVISIBLE = 15;

    //ACTION_TYPE_PNG_VISIBLE, ACTION_TYPE_PNG_INVISIBLE, ACTION_TYPE_PRA_STOP
    private void sendActionMsg(int _action) {
        Message msg = mActionHandler.obtainMessage();

        Bundle bundle = new Bundle();
        bundle.putInt(ACTION_KEY_TYPE, _action);
        if (_action == ACTION_TYPE_PNG_VISIBLE || _action == ACTION_TYPE_PNG_INVISIBLE || _action == ACTION_TYPE_PRA_STOP
            || _action == ACTION_TYPE_BACKGROUND_VISIBLE || _action == ACTION_TYPE_BACKGROUND_INVISIBLE
            || _action == ACTION_TYPE_RCS_VISIBLE || _action == ACTION_TYPE_RCS_INVISIBLE) {
            msg.setData(bundle);
            mActionHandler.sendMessage(msg);
        }
    }

    //ACTION_TYPE_TEXT_VISIBLE, ACTION_TYPE_TEXT_INVISIBLE, ACTION_TYPE_DRCS_VISIBLE, ACTION_TYPE_DRCS_INVISIBLE
    private void sendActionMsg(int _action, int _index) {
        Message msg = mActionHandler.obtainMessage();

        Bundle bundle = new Bundle();
        bundle.putInt(ACTION_KEY_TYPE, _action);
        if (_action == ACTION_TYPE_TEXT_VISIBLE || _action == ACTION_TYPE_TEXT_INVISIBLE) {
            bundle.putInt(ACTION_PARAM_TEXT_INDEX, _index);
        }
        else if (_action == ACTION_TYPE_DRCS_VISIBLE || _action == ACTION_TYPE_DRCS_INVISIBLE) {
            bundle.putInt(ACTION_PARAM_DRCS_INDEX, _index);
        }
        else if (_action == ACTION_TYPE_PRA_PLAY) {
            bundle.putInt(ACTION_PARAM_PRA_INDEX, _index);
        }

        msg.setData(bundle);
        mActionHandler.sendMessage(msg);
    }

    //ACTION_TYPE_SET_RCS
    private void sendActionMsg(int _action, int _index, int _x, int _y, int _hor, int _ver, int _dispFormat) {
        Message msg = mActionHandler.obtainMessage();

        Bundle bundle = new Bundle();
        bundle.putInt(ACTION_KEY_TYPE, _action);

        if (_action == ACTION_TYPE_SET_RCS) {
            bundle.putInt(ACTION_PARAM_RCS_INDEX, _index);
            bundle.putInt(ACTION_PARAM_RCS_START_X, _x);
            bundle.putInt(ACTION_PARAM_RCS_START_Y, _y);
            bundle.putInt(ACTION_PARAM_RCS_HOR, _hor);
            bundle.putInt(ACTION_PARAM_RCS_VER, _ver);
            bundle.putInt(ACTION_PARAM_DISP_FORMAT, _dispFormat);
        }

        msg.setData(bundle);
        mActionHandler.sendMessage(msg);
    }

    //ACTION_TYPE_SET_TEXT
    private void sendActionMsg(int _action, int _x, int _y, float _sizeText, int _fontSizeType, String _text, int _index, int _color, int _colorBg, int _flashing, int _timeDelay, int _dispFomrat) {
        Message msg = mActionHandler.obtainMessage();

        Bundle bundle = new Bundle();
        bundle.putInt(ACTION_KEY_TYPE, _action);
        bundle.putInt(ACTION_PARAM_TEXT_START_X, _x);
        bundle.putInt(ACTION_PARAM_TEXT_START_Y, _y);
        bundle.putFloat(ACTION_PARAM_TEXT_SIZE, _sizeText);
        bundle.putInt(ACTION_PARAM_TEXT_FONT_SIZE_TYPE, _fontSizeType);
        bundle.putString(ACTION_PARAM_TEXT_STRING, _text);
        bundle.putInt(ACTION_PARAM_TEXT_INDEX, _index);
        bundle.putInt(ACTION_PARAM_TEXT_COLOR, _color);
        bundle.putInt(ACTION_PARAM_TEXT_COLOR_BG, _colorBg);
        bundle.putInt(ACTION_PARAM_TEXT_FLASHING, _flashing);
        bundle.putInt(ACTION_PARAM_TEXT_TIME_DELAY, _timeDelay);
        bundle.putInt(ACTION_PARAM_DISP_FORMAT, _dispFomrat);

        msg.setData(bundle);
        mActionHandler.sendMessage(msg);
    }

    //ACTION_TYPE_SET_DRCS
    private void sendActionMsg(int _action, int _x, int _y, float _size, int _fontSizeType, int _index, int _color, int _colorBg, int _flashing, int _timeDelay, Bitmap _bmp) {
        Message msg = mActionHandler.obtainMessage();

        Bundle bundle = new Bundle();
        bundle.putInt(ACTION_KEY_TYPE, _action);
        bundle.putInt(ACTION_PARAM_DRCS_START_X, _x);
        bundle.putInt(ACTION_PARAM_DRCS_START_Y, _y);
        bundle.putFloat(ACTION_PARAM_DRCS_SIZE, _size);
        bundle.putInt(ACTION_PARAM_DRCS_FONT_SIZE_TYPE, _fontSizeType);
        bundle.putInt(ACTION_PARAM_DRCS_INDEX, _index);
        bundle.putInt(ACTION_PARAM_DRCS_COLOR, _color);
        bundle.putInt(ACTION_PARAM_DRCS_COLOR_BG, _colorBg);
        bundle.putInt(ACTION_PARAM_DRCS_FLASHING, _flashing);
        bundle.putInt(ACTION_PARAM_DRCS_TIME_DELAY, _timeDelay);
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        _bmp.compress(Bitmap.CompressFormat.PNG, 100, stream) ;
        byte[] byteArray = stream.toByteArray() ;
        bundle.putByteArray(ACTION_PARAM_BITMAP, byteArray);

        msg.setData(bundle);
        mActionHandler.sendMessage(msg);
    }

    //ACTION_TYPE_SET_PNG
    private void sendActionMsg(int _action, int _x, int _y, byte[] _pngArray) {
        Message msg = mActionHandler.obtainMessage();

        Bundle bundle = new Bundle();
        bundle.putInt(ACTION_KEY_TYPE, _action);
        bundle.putInt(ACTION_PARAM_PNG_START_X, _x);
        bundle.putInt(ACTION_PARAM_PNG_START_Y, _y);
        bundle.putByteArray(ACTION_PARAM_PNG, _pngArray);

        msg.setData(bundle);
        mActionHandler.sendMessage(msg);
    }

    private Runnable mRunnableText = new Runnable() {
        @Override
        public void run() {
            for (int i=0; i < MAX_TEXT_VIEW_NUM; i++) {
                if (mTextViewDelay[i] > 0) {
                    mTextView[i].setVisibility(View.VISIBLE);
                    if (mTextBlink[i] == FLASH_START_NORMAL || mTextBlink[i] == FLASH_START_INVERSE) {
                        mTextView[i].startAnimation(getBlinkAnimation());
                    }
                    //Log.e(TAG,"delayed display index="+i+", time="+mTextViewDelay[i]);
                    mTextViewDelay[i] = 0;
                }
            }
        }
    };

    private Runnable mRunnableDRCS = new Runnable() {
        @Override
        public void run() {
            for (int i=0; i < MAX_DRCS_VIEW_NUM; i++) {
                if (mDRCSViewDelay[i] > 0) {
                    mDRCSView[i].setVisibility(View.VISIBLE);
                    if (mDRCSBlink[i] == FLASH_START_NORMAL || mDRCSBlink[i] == FLASH_START_INVERSE) {
                        mDRCSView[i].startAnimation(getBlinkAnimation());
                    }
                    //Log.e(TAG,"[TIME] delayed display index="+i+", time="+mDRCSViewDelay[i]);
                    mDRCSViewDelay[i] = 0;
                }
            }
        }
    };

    private Handler mActionHandler = new Handler() {

        public void handleMessage(Message msg) {
            Bundle data = msg.getData();
            int textX, textY, fontSizeType, color, colorBg, index, flashing, timeDelay, delayedTime, startX, startY, horSDF, verSDF, dispFormat;
            float sizeTextPx, sizeDRCS;
            String dispString;
            ViewGroup.MarginLayoutParams params;
            byte[] bmpArray;

            switch (data.getInt(ACTION_KEY_TYPE)) {
                case ACTION_TYPE_SET_TEXT:
                    textX = data.getInt(ACTION_PARAM_TEXT_START_X);
                    textY = data.getInt(ACTION_PARAM_TEXT_START_Y);
                    sizeTextPx = data.getFloat(ACTION_PARAM_TEXT_SIZE);
                    //live
                    /*
                    if (FloatingWindow.isFloating) {
                        if ((MainActivity.dpiName.contains("mdpi")) || (MainActivity.dpiName.contains("ldpi")) || (MainActivity.screenSize.contains("large"))) {
                            sizeTextPx = (data.getFloat(ACTION_PARAM_TEXT_SIZE))*0.5f;
                        } else {
                            sizeTextPx = (data.getFloat(ACTION_PARAM_TEXT_SIZE))*0.8f;
                        }
                    }*/
                    fontSizeType = data.getInt(ACTION_PARAM_TEXT_FONT_SIZE_TYPE);
                    dispString = data.getString(ACTION_PARAM_TEXT_STRING);
                    color = data.getInt(ACTION_PARAM_TEXT_COLOR);
                    colorBg = data.getInt(ACTION_PARAM_TEXT_COLOR_BG);
                    index = data.getInt(ACTION_PARAM_TEXT_INDEX);
                    flashing = data.getInt(ACTION_PARAM_TEXT_FLASHING);
                    timeDelay = data.getInt(ACTION_PARAM_TEXT_TIME_DELAY);
                    dispFormat = data.getInt(ACTION_PARAM_DISP_FORMAT);

                    params=(ViewGroup.MarginLayoutParams) mTextView[index].getLayoutParams();
                    if ((mSegmentType==TYPE_ONESEG) || (dispFormat==DISP_FORMAT_HOR_960_540) || (dispFormat==DISP_FORMAT_HOR_720_480)) {
                        params.leftMargin = capDefaultLeftMargin + textX;
                        if ((MainActivity.dpiName.contains("mdpi")) || (MainActivity.dpiName.contains("ldpi")) || (MainActivity.screenSize.contains("large"))) {
                            if (MainActivity.isMainActivity) {
                                params.topMargin = textY - CAPTION_DEFAULT_HEIGHT_UNIT_LEN_1;
                            } else if (FloatingWindow.isFloating || ChatMainActivity.isChat) {
                                params.topMargin = textY - CAPTION_DEFAULT_HEIGHT_UNIT_LEN_2;
                            }
                        } else {
                            if (MainActivity.isMainActivity) {
                                params.topMargin = textY - CAPTION_DEFAULT_HEIGHT_UNIT_LEN;
                            } else if (FloatingWindow.isFloating || ChatMainActivity.isChat) {
                                params.topMargin = textY - CAPTION_DEFAULT_HEIGHT_UNIT_LEN_1;
                            }
                        }

                        params.width = (int) sizeTextPx * dispString.length();
                        params.height = params.WRAP_CONTENT;

                        mTextView[index].setLayoutParams(params);

                        mTextView[index].setTypeface(mFont);
                        mTextView[index].setTextColor(color);
                        if (fontSizeType == FONT_SIZE_MIDDLE) {
                            mTextView[index].setTextSize(TypedValue.COMPLEX_UNIT_PX, sizeTextPx * 2);
                            mTextView[index].setTextScaleX(FONT_WIDTH_RATE_HALF);
                        }
                        else {
                            mTextView[index].setTextSize(TypedValue.COMPLEX_UNIT_PX, sizeTextPx);
                            mTextView[index].setTextScaleX(FONT_WIDTH_RATE_ORIGINAL);
                        }

                        mTextView[index].setText(dispString);
                    }
                    else { //DISP_FORMAT_VER_960_540, DISP_FORMAT_VER_720_480, fullseg
                        Log.e(TAG, "disp format exception >>>>> 2222");
                    }
                    mTextBlink[index] = flashing;
                    mTextViewDelay[index] = timeDelay;
                    if ((mTypeCaption == TYPE_CAPTION_SUBTITLE) && (mSegmentType == TYPE_ONESEG)) {
                        mTextView[index].setBackgroundColor(BCKGND_OPACITY_0); //0% opacity
                    }
                    else {
                        if (colorBg == Color.TRANSPARENT) {
                            mTextView[index].setBackgroundColor(BCKGND_OPACITY_0); //0% opacity
                        }
                        else {
                            mTextView[index].setBackgroundColor(colorBg & BCKGND_OPACITY_50);
                        }
                    }
                    break;

                case ACTION_TYPE_TEXT_VISIBLE:
                    index = data.getInt(ACTION_PARAM_TEXT_INDEX);
                    delayedTime = mTextViewDelay[index];
                    if (delayedTime > 0) {
                        //Log.e(TAG, "[TIME] index="+index+", text="+mTextView[index].getText()+", delay="+delayedTime);
                        postDelayed(mRunnableText, delayedTime);
                    }
                    else {
                        mTextView[index].setVisibility(View.VISIBLE);
                        if (mTextBlink[index] == FLASH_START_NORMAL || mTextBlink[index] == FLASH_START_INVERSE) {
                            mTextView[index].startAnimation(getBlinkAnimation());
                            //Log.e(TAG, "text flashing start (index=" + index + ")");
                        }
                    }
                    break;

                case ACTION_TYPE_TEXT_INVISIBLE:
                    index = data.getInt(ACTION_PARAM_TEXT_INDEX);
                    if (mTextBlink[index] == FLASH_START_NORMAL || mTextBlink[index] == FLASH_START_INVERSE) {
                        //Log.e(TAG, "text flashing stop (index="+index+")");
                    }
                    mTextView[index].clearAnimation();
                    mTextBlink[index] = 0;
                    mTextViewDelay[index] = 0;
                    mTextView[index].setVisibility(View.INVISIBLE);
                    break;

                case ACTION_TYPE_SET_DRCS:
                    textX = data.getInt(ACTION_PARAM_DRCS_START_X);
                    textY = data.getInt(ACTION_PARAM_DRCS_START_Y);
                    sizeDRCS = data.getFloat(ACTION_PARAM_DRCS_SIZE);
                    fontSizeType = data.getInt(ACTION_PARAM_DRCS_FONT_SIZE_TYPE);
                    index = data.getInt(ACTION_PARAM_DRCS_INDEX);
                    color = data.getInt(ACTION_PARAM_DRCS_COLOR);
                    colorBg = data.getInt(ACTION_PARAM_DRCS_COLOR_BG);
                    flashing = data.getInt(ACTION_PARAM_DRCS_FLASHING);
                    timeDelay = data.getInt(ACTION_PARAM_DRCS_TIME_DELAY);
                    bmpArray = data.getByteArray(ACTION_PARAM_BITMAP);
                    Bitmap bmpDrcs = BitmapFactory.decodeByteArray(bmpArray, 0, bmpArray.length);

                    params = (ViewGroup.MarginLayoutParams) mDRCSView[index].getLayoutParams();
                    params.leftMargin = capDefaultLeftMargin + textX;
                    params.topMargin = textY;
                    params.width = (int)sizeDRCS;
                    if (mSegmentType == TYPE_FULLSEG) {
                        if (fontSizeType == FONT_SIZE_MIDDLE) {
                            params.height = (int) (sizeDRCS * 2 + (sizeDRCS * 2 / 3));
                        } else {
                            params.height = (int) (sizeDRCS + sizeDRCS / 3);
                        }
                    }
                    else { //oneseg
                        if (fontSizeType == FONT_SIZE_MIDDLE) {
                            params.height = (int) (sizeDRCS * 2);
                            params.topMargin = textY + (int)(sizeDRCS /3);
                        } else {
                            params.height = (int) (sizeDRCS);
                            params.topMargin = textY + (int)(sizeDRCS /6);
                        }
                    }

                    mDRCSView[index].setLayoutParams(params);
                    mDRCSView[index].setImageBitmap(bmpDrcs);
                    mDRCSView[index].setScaleType(ImageView.ScaleType.FIT_XY);
                    mDRCSBlink[index] = flashing;
                    mDRCSViewDelay[index] = timeDelay;
                    if ((mTypeCaption==TYPE_CAPTION_SUBTITLE) && (mSegmentType==TYPE_ONESEG)) {
                        mDRCSView[index].setBackgroundColor(BCKGND_OPACITY_0); //0% opacity
                    }
                    else {
                        if (colorBg == Color.TRANSPARENT) {
                            mDRCSView[index].setBackgroundColor(BCKGND_OPACITY_0); //0% opacity
                        }
                        else {
                            mDRCSView[index].setBackgroundColor(colorBg & BCKGND_OPACITY_50);
                        }
                    }
                    break;

                case ACTION_TYPE_DRCS_VISIBLE:
                    index = data.getInt(ACTION_PARAM_DRCS_INDEX);
                    delayedTime = mDRCSViewDelay[index];
                    if (delayedTime > 0) {
                        //Log.e(TAG, "[TIME] index="+index+", delay="+delayedTime);
                        postDelayed(mRunnableDRCS, delayedTime);
                    }
                    else {
                        mDRCSView[index].setVisibility(View.VISIBLE);
                        if (mDRCSBlink[index] == FLASH_START_NORMAL || mDRCSBlink[index] == FLASH_START_INVERSE) {
                            mDRCSView[index].startAnimation(getBlinkAnimation());
                            //Log.e(TAG, "drcs flashing start (index=" + index + ")");
                        }
                    }
                    break;

                case ACTION_TYPE_DRCS_INVISIBLE:
                    index = data.getInt(ACTION_PARAM_DRCS_INDEX);
                    if (mDRCSBlink[index] == FLASH_START_NORMAL || mDRCSBlink[index] == FLASH_START_INVERSE) {
                        //Log.e(TAG, "drcs flashing stop (index="+index+")");
                    }
                    mDRCSView[index].clearAnimation();
                    mDRCSBlink[index] = 0;
                    mDRCSViewDelay[index] = 0;
                    mDRCSView[index].setVisibility(View.INVISIBLE);
                    break;

                case ACTION_TYPE_BACKGROUND_INVISIBLE:
                    mOnesegBackView.setVisibility(View.INVISIBLE);
                    break;

                case ACTION_TYPE_BACKGROUND_VISIBLE:
                    mOnesegBackView.setBackgroundColor(Color.BLACK & BCKGND_OPACITY_10); //10% opacity
                    params=(ViewGroup.MarginLayoutParams) mOnesegBackView.getLayoutParams();
                    params.leftMargin = 0;
                    params.width = mParentLayout.getWidth();
                    if ((MainActivity.dpiName.contains("mdpi")) || (MainActivity.dpiName.contains("ldpi")) || (MainActivity.screenSize.contains("large"))) {
                        if (MainActivity.isMainActivity) {
                            params.topMargin = mCapViewHeight - CAPTION_DEFAULT_HEIGHT_UNIT_LEN_1*3;
                            params.height = CAPTION_DEFAULT_HEIGHT_UNIT_LEN_1 * 3;
                        } else if (FloatingWindow.isFloating || ChatMainActivity.isChat) {
                            params.topMargin = mCapViewHeight - CAPTION_DEFAULT_HEIGHT_UNIT_LEN_2*3;
                            params.height = CAPTION_DEFAULT_HEIGHT_UNIT_LEN_2 * 3;
                        }
                    } else {
                        if (MainActivity.isMainActivity) {
                            params.topMargin = mCapViewHeight - CAPTION_DEFAULT_HEIGHT_UNIT_LEN*3;
                            params.height = CAPTION_DEFAULT_HEIGHT_UNIT_LEN * 3;
                        } else if (FloatingWindow.isFloating || ChatMainActivity.isChat) {
                            params.topMargin = mCapViewHeight - CAPTION_DEFAULT_HEIGHT_UNIT_LEN_1*3;
                            params.height = CAPTION_DEFAULT_HEIGHT_UNIT_LEN_1 * 3;
                        }
                    }

                    mOnesegBackView.setLayoutParams(params);
                    mOnesegBackView.setVisibility(View.VISIBLE);
                    break;

                case ACTION_TYPE_SET_PNG:
                    startX = data.getInt(ACTION_PARAM_PNG_START_X);
                    startY = data.getInt(ACTION_PARAM_PNG_START_Y);
                    bmpArray = data.getByteArray(ACTION_PARAM_PNG);

                    Bitmap bmpPNG = null;
                    byte[] pngArray = FCI_TVi.getPngFromAribPng(bmpArray);

                    params=(ViewGroup.MarginLayoutParams) mPNGView.getLayoutParams();
                    params.leftMargin = startX;
                    params.topMargin = startY;
                    params.width = params.WRAP_CONTENT;
                    params.height = params.WRAP_CONTENT;

                    if (pngArray != null) {
                        bmpPNG = BitmapFactory.decodeByteArray(pngArray, 0, pngArray.length);
                        if (bmpPNG != null) {
                            mPNGView.setLayoutParams(params);
                            mPNGView.setImageBitmap(bmpPNG);
                            mPNGView.setAlpha(VIEW_ALPHA_70);
                            mIsExistPNG = true;
                        }
                    }
                    break;

                case ACTION_TYPE_PNG_VISIBLE:
                    mPNGView.setVisibility(View.VISIBLE);
                    break;

                case ACTION_TYPE_PNG_INVISIBLE:
                    mPNGView.setVisibility(View.INVISIBLE);
                    break;

                case ACTION_TYPE_PRA_PLAY:
                    index = data.getInt(ACTION_PARAM_PRA_INDEX);
                    if (index >= 0 && index <= MAX_PRA_INDEX) {
                        switch (index) {
                            case PRA_INDEX_0_CHIME1:
                                mMP = MediaPlayer.create(mContext, R.raw.pra00);
                                break;
                            case PRA_INDEX_1_CHIME2:
                                mMP = MediaPlayer.create(mContext, R.raw.pra01);
                                break;
                            case PRA_INDEX_2_CHIME3:
                                mMP = MediaPlayer.create(mContext, R.raw.pra02);
                                break;
                            case PRA_INDEX_3_CHIME4:
                                mMP = MediaPlayer.create(mContext, R.raw.pra03);
                                break;
                            case PRA_INDEX_4_CHIME5:
                                mMP = MediaPlayer.create(mContext, R.raw.pra04);
                                break;
                            case PRA_INDEX_5_BUTTON1:
                                mMP = MediaPlayer.create(mContext, R.raw.pra05);
                                break;
                            case PRA_INDEX_6_BUTTON2:
                                mMP = MediaPlayer.create(mContext, R.raw.pra06);
                                break;
                            case PRA_INDEX_7_BUTTON3:
                                mMP = MediaPlayer.create(mContext, R.raw.pra07);
                                break;
                            case PRA_INDEX_8_BUTTON4:
                                mMP = MediaPlayer.create(mContext, R.raw.pra08);
                                break;
                            case PRA_INDEX_9_BUTTON5:
                                mMP = MediaPlayer.create(mContext, R.raw.pra09);
                                break;
                            case PRA_INDEX_10_BUTTON6:
                                mMP = MediaPlayer.create(mContext, R.raw.pra10);
                                break;
                            case PRA_INDEX_11_BUTTON7:
                                mMP = MediaPlayer.create(mContext, R.raw.pra11);
                                break;
                            case PRA_INDEX_12_BUTTON8:
                                mMP = MediaPlayer.create(mContext, R.raw.pra12);
                                break;
                            case PRA_INDEX_13_ALERT:
                                mMP = MediaPlayer.create(mContext, R.raw.pra13);
                                break;
                        }
                        if (mMP != null) {
                            if (index <= PRA_INDEX_4_CHIME5 || index == PRA_INDEX_13_ALERT) {
                                mMP.setLooping(true);
                            }
                            mMP.start();
                        }
                    }
                    break;

                case ACTION_TYPE_PRA_STOP:
                    if (mMP != null) {
                        mMP.stop();
                    }
                    break;

                case ACTION_TYPE_SET_RCS:
                    index = data.getInt(ACTION_PARAM_RCS_INDEX);
                    startX = data.getInt(ACTION_PARAM_RCS_START_X);
                    startY = data.getInt(ACTION_PARAM_RCS_START_Y);
                    horSDF = data.getInt(ACTION_PARAM_RCS_HOR);
                    verSDF = data.getInt(ACTION_PARAM_RCS_VER);
                    dispFormat = data.getInt(ACTION_PARAM_DISP_FORMAT);
                    switch (index) {
                        case 0:case 65:case 128:case 193:
                            mRCSBackView.setBackgroundColor(Color.BLACK & BCKGND_OPACITY_30);
                            break;
                        case 1:case 66:case 129:case 194:
                            mRCSBackView.setBackgroundColor(Color.RED & BCKGND_OPACITY_30);
                            break;
                        case 2:case 67:case 130:case 195:
                            mRCSBackView.setBackgroundColor(Color.GREEN & BCKGND_OPACITY_30);
                            break;
                        case 3:case 68:case 131:case 196:
                            mRCSBackView.setBackgroundColor(Color.YELLOW & BCKGND_OPACITY_30);
                            break;
                        case 4:case 69:case 132:case 197:
                            mRCSBackView.setBackgroundColor(Color.BLUE & BCKGND_OPACITY_30);
                            break;
                        case 5:case 70:case 133:case 198:
                            mRCSBackView.setBackgroundColor(Color.MAGENTA & BCKGND_OPACITY_30);
                            break;
                        case 6:case 71:case 134:case 199:
                            mRCSBackView.setBackgroundColor(Color.CYAN & BCKGND_OPACITY_30);
                            break;
                        case 7:case 72:case 135:case 200:
                            mRCSBackView.setBackgroundColor(Color.WHITE & BCKGND_OPACITY_30);
                            break;
                        case 8:case 136:
                            mRCSBackView.setBackgroundColor(Color.TRANSPARENT & BCKGND_OPACITY_30);
                            break;
                        case 9:case 73:case 137:case 201: //half red
                            mRCSBackView.setBackgroundColor(Color.rgb(170,0,0) & BCKGND_OPACITY_30);
                            break;
                        case 10:case 74:case 138:case 202: //half green
                            mRCSBackView.setBackgroundColor(Color.rgb(0,170,0) & BCKGND_OPACITY_30);
                            break;
                        case 11:case 75:case 139:case 203: //half yellow
                            mRCSBackView.setBackgroundColor(Color.rgb(170,170,0) & BCKGND_OPACITY_30);
                            break;
                        case 12:case 76:case 140:case 204: //half blue
                            mRCSBackView.setBackgroundColor(Color.rgb(0,0,170) & BCKGND_OPACITY_30);
                            break;
                        case 13:case 77:case 141:case 205: //half magenta
                            mRCSBackView.setBackgroundColor(Color.rgb(170,0,170) & BCKGND_OPACITY_30);
                            break;
                        case 14:case 78:case 142:case 206: //half cyan
                            mRCSBackView.setBackgroundColor(Color.rgb(0,170,170) & BCKGND_OPACITY_30);
                            break;
                        case 15:case 79:case 143:case 207: //half white(gray)
                            mRCSBackView.setBackgroundColor(Color.rgb(170,170,170) & BCKGND_OPACITY_30);
                            break;
                    }
                    params = (ViewGroup.MarginLayoutParams) mRCSBackView.getLayoutParams();
                    if (dispFormat==DISP_FORMAT_HOR_960_540 || dispFormat==DISP_FORMAT_HOR_720_480) {
                        params.leftMargin = startX+capDefaultLeftMargin;
                        params.topMargin = startY;
                        params.width = horSDF;
                        params.height = verSDF;
                    }
                    else { //DISP_FORMAT_VER_960_540, DISP_FORMAT_VER_720_480
                    }
                    mRCSBackView.setLayoutParams(params);
                    mIsExistRCS = true;
                    break;

                case ACTION_TYPE_RCS_VISIBLE:
                    mRCSBackView.setVisibility(View.VISIBLE);
                    mIsExistRCS = false;
                    break;

                case ACTION_TYPE_RCS_INVISIBLE:
                    mRCSBackView.setVisibility(View.INVISIBLE);
                    break;
            }
        }
    };
}
//]]JAPAN_CAPTION
