package kr.co.fci.tv.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;


/**
 * Created by eddy.lee on 2015-09-08.
 */
final public class TVlog {

    static String TAG = "FCITVAPP :: ";
    private static int level =TVlog.INFO;

    private final static int NOLOG =0;
    private final static int INFO =1;
    private final static int ERROR =2;
    private final static int DEBUG =3;


    public static  void i(String _tag,String _log)
    {
        if(level >= INFO ) {
            Log.w(TAG, _tag + _log) ;

        }
    }

    public static void e(String _tag,String _log)
    {
        if(level >= INFO ) {
            Log.e(TAG, " Error : " + _tag + " " + _log);

        }
    }
    public static void d(String _tag,String _log)
    {
        if(level >= DEBUG ) {
            Log.e(TAG," Debug : "  +_tag + " " + _log);

        }
    }
    public static void DeviceInfo(Context _context)
    {
        Log.e(TAG, "################################################################################");

        Log.e(TAG, " ");
        if( _context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_USB_HOST))
        {
            Log.e(TAG," - OTG Support -");
        }else
        {
            Log.e(TAG," - OTG not Support  - ");
        }

        Log.e(TAG, " ");
        Log.e(TAG,"MODEL: " + android.os.Build.MODEL
                        + "\nDEVICE: " + android.os.Build.DEVICE
                        + "\nMANUFACTURER: " + android.os.Build.MANUFACTURER
                        + "\nPRODUCT: " + android.os.Build.PRODUCT
                        + "\nBRAND: " + android.os.Build.BRAND
                        + "\nDISPLAY: " + android.os.Build.DISPLAY
                        + "\nBOARD: " + android.os.Build.BOARD
                        + "\nHOST: " + android.os.Build.HOST
                        + "\nBOOTLOADER: "+  android.os.Build.BOOTLOADER
                        + "\nFINGERPRINT: "+   android.os.Build.FINGERPRINT
                        + "\nHARDWARE: "+ android.os.Build.HARDWARE
                        + "\nID: "+    android.os.Build.ID
        );
        Log.e(TAG," ");

        Log.e(TAG,"################################################################################");
    }
}
