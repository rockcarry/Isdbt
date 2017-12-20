package kr.co.fci.tv.tvSolution;

import android.database.Cursor;
import android.widget.ImageView;

import kr.co.fci.tv.FloatingWindow;
import kr.co.fci.tv.MainActivity;
import kr.co.fci.tv.R;
import kr.co.fci.tv.TVEVENT;
import kr.co.fci.tv.buildOption;
import kr.co.fci.tv.chat.ChatMainActivity;
import kr.co.fci.tv.saves.CommonStaticData;
import kr.co.fci.tv.setting.DebugMode;
import kr.co.fci.tv.util.TVlog;

/**
 * Created by eddy.lee on 2015-08-27.
 */
public class SignalMonitor {

    private String TAG = "SignalMonitor";

    int BER;

    int currentSignalVal[];
    int oneSegCN, fullSegCN;
    int oneSegRSSI, fullSegRSSI;
    int oneSegBER, fullSegBER;
    int oneSegPER, fullSegPER;

    public ImageView signalImage;

    private static Cursor mCursor = null;

    int preAntLevel = 1;
    int curAntLevel = 1;
    int preAntLevelF = 1;
    int curAntLevelF = 1;
    int moveFullTh = 0;
    int moveOneTh = 0;

    public boolean event_weakSignal = false;
    public boolean event_noSiganl = false;

    // eddy [[160708
    public static String hex(int n) {
        // call toUpperCase() if that's required
        return String.format("0x%2s", Integer.toHexString(n)).replace(' ', '0');
    }
    // ]]eddy 160708
    public SignalMonitor(ImageView _img)
    {
        signalImage = _img;
    }
    public int getSignal()
    {
        int segType = 0;
        mCursor = MainActivity.getCursor();

        //in case of exception
        if (mCursor == null) {
            return -1;
        }

        if (mCursor.isClosed()) {
            return -1;
        }

        if (mCursor != null && (mCursor.getPosition() < 0 || mCursor.getPosition() >= mCursor.getCount()) ) {
            return -1;
        }

        if (mCursor != null && mCursor.getCount() > 0) {
            segType = mCursor.getInt(CommonStaticData.COLUMN_INDEX_SERVICE_MTV);
        }

        // [[ eddy 160708
        String LNA = "LNA = " +hex(FCI_TVi.devRegReadByte(0x0f95));
        String RFVGA = "RFVGA = " +hex(FCI_TVi.devRegReadByte(0x0f96));
        String CSF = "CSF = " +hex(FCI_TVi.devRegReadByte(0x0fd2));
        String CCI = "CCI = " +FCI_TVi.devRegReadByte(0x20fc);
        String cacPgd = "Cac/Cir num = " +hex(FCI_TVi.devRegReadByte(0x2184));
        String mrdPgd = "mrgPgd num = " +hex(FCI_TVi.devRegReadByte(0x2185));
        String xxif = " xxif = " +hex(FCI_TVi.devRegReadByte(0x0f1f));
        String Cacpgdist = " Cacpgdist = " +hex(FCI_TVi.devRegReadByte(0x2505));
        // ]] eddy 160708

        if (segType == 0) { // 1seg
            oneSegCN = FCI_TVi.GetSignal();
            currentSignalVal = FCI_TVi.GetMoreSignalVal2();
            fullSegBER = currentSignalVal[0];
            fullSegPER = currentSignalVal[1];
            oneSegBER = currentSignalVal[2];
            oneSegPER = currentSignalVal[3];
            oneSegRSSI = currentSignalVal[4];

            // live add
            if (MainActivity.getInstance() != null) {
                if (MainActivity.getInstance().strISDBMode.equalsIgnoreCase("ISDBT Oneseg")) {
                    TVlog.e("Air SQ", ">>>>> ISDBT Oneseg ?????? ");
                } else {
                    if (MainActivity.getInstance().is_inserted_card==2) {
                        if(CommonStaticData.receivemode==2 && CommonStaticData.scanCHnum > 0 && CommonStaticData.scanningNow==false && CommonStaticData.handoverMode==0) {
                            //if(currentSignalVal[0]<250 && currentCN>17){
                            //if (currentSignalVal[2] < 250 && currentPER < 100) {
                            if (fullSegBER < 250 && fullSegPER < 100) {
                                moveFullTh++;
                                TVlog.e("Air SQ", ">>>>> 1seg moveFullTh = "+moveFullTh);
                            } else {
                                moveFullTh = 0;
                            }
                            // live add condition
                            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB) {
                                if(moveFullTh == 5) {
                                    TVlog.e("Air SQ", ">>>>> CHANNEL_SWITCHING to Fullseg ");
                                    if (MainActivity.isMainActivity) {
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_SWITCHING, 1, 0, null);
                                    } else if (FloatingWindow.isFloating) {
                                        FloatingWindow.getInstance().sendEvent(TVEVENT.E_CHANNEL_SWITCHING_FLOATING, 1, 0, null);
                                    } else if (ChatMainActivity.isChat) {
                                        ChatMainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_SWITCHING_CHAT, 1, 0, null);
                                    }
                                    moveFullTh = 0;
                                }
                            }
                        }
                    } else {
                        if(CommonStaticData.receivemode==2 && CommonStaticData.scanCHnum > 0 && CommonStaticData.scanningNow==false && (MainActivity.getInstance().is_inserted_card==1) && CommonStaticData.handoverMode==0) {
                            //if(currentSignalVal[0]<250 && currentCN>17){
                            //if (currentSignalVal[2] < 250 && currentPER < 100) {
                            if (fullSegBER < 250 && fullSegPER < 100) {
                                moveFullTh++;
                                TVlog.e("Air SQ", ">>>>> 1seg moveFullTh = "+moveFullTh);
                            } else {
                                moveFullTh = 0;
                            }
                            // live add condition
                            if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB) {
                                if(moveFullTh == 5) {
                                    TVlog.e("Air SQ", ">>>>> CHANNEL_SWITCHING to Fullseg ");
                                    if (MainActivity.isMainActivity) {
                                    MainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_SWITCHING, 1, 0, null);
                                    } else if (FloatingWindow.isFloating) {
                                        FloatingWindow.getInstance().sendEvent(TVEVENT.E_CHANNEL_SWITCHING_FLOATING, 1, 0, null);
                                    } else if (ChatMainActivity.isChat) {
                                        FloatingWindow.getInstance().sendEvent(TVEVENT.E_CHANNEL_SWITCHING_CHAT, 1, 0, null);
                                    }
                                    moveFullTh = 0;
                                }
                            }
                        }
                    }

                }
            }

            // [[ eddy 160708
           /* if (buildOption.FCI_SOLUTION_MODE <buildOption.JAPAN_FILE) {
                TVlog.i("Air SQ", ">>>>> 1-seg Signal Level = "  + "\n"
                        + currentCN + "," + "  BER = " + currentSignalVal[0] + ","+ "  PER = " + currentPER +"," + "  RSSI = " + currentRSSI
                      +" " + LNA+" "+RFVGA+" " +CSF+" " +CCI+" " +cacPgd+" " +mrdPgd);
            }*/
            //]] eddy 160708
            if (buildOption.FCI_SOLUTION_MODE >= buildOption.JAPAN_FILE) {
                oneSegBER = 0;
            }else if(MainActivity.withoutUSB) {     // justin add
                oneSegBER = 1000;
            }else{
                TVlog.i("Air SQ", ">>>>> 1-seg Signal Level = " + "\n"
                        + oneSegCN + "," + "  BER = " + oneSegBER + ","+ "  PER = " + oneSegPER +"," + "  RSSI = " + oneSegRSSI
                        +" " + LNA+" "+RFVGA+" " +CSF+" " +CCI+" " +cacPgd+" " +mrdPgd);
            }

            if (oneSegCN >= 100) {
                oneSegCN = 0;
            }

            // [[ eddy 160708
            /*if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN
                    || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB) {
                if (oneSegBER >= 10000 && currentRSSI < -100) {
                    //oneSegBER = 500;
                    oneSegBER = 1000;//600;
                }

                if(oneSegBER >= 900  || oneSegPER >=10000) {
                    curAntLevel = 0;
                }
                else if((oneSegBER < 900 && oneSegBER >= 700 )||  (oneSegPER < 10000 && oneSegPER >= 5000 ) ){
                    curAntLevel = 1;
                }
                else if((oneSegBER < 700 && oneSegBER >= 500) ||  (oneSegPER < 5000 && oneSegPER >= 1000 ) ){
                    curAntLevel = 2;
                }
                else if(oneSegBER < 500 && oneSegBER >= 350  ){
                    curAntLevel = 3;
                }
                else if(oneSegBER < 350 && oneSegBER >= 200  ){
                    curAntLevel = 4;
                }
                else if(oneSegBER < 200 )
                {
                    curAntLevel = 5;
                }
            } else {*/
            if (oneSegBER >= 10000 && oneSegRSSI < -100) {
                //oneSegBER = 500;
                oneSegBER = 600;
            }

            if(oneSegBER >= 550) {//  || oneSegPER >=5000) {
                curAntLevel = 0;
            }
            else if((oneSegBER < 550 && oneSegBER >= 450 ) ) { //||  (oneSegPER < 5000 && oneSegPER >= 1000 ) ){
                curAntLevel = 1;
            }
            else if((oneSegBER < 450 && oneSegBER >= 350) ) { //||  (oneSegPER < 1000 && oneSegPER >= 100 ) ){
                curAntLevel = 2;
            }
            else if(oneSegBER < 350 && oneSegBER >= 250 ) {
                curAntLevel = 3;
            }
            else if(oneSegBER < 250 && oneSegBER >= 150  ){
                curAntLevel = 4;
            }
            else if(oneSegBER < 150 )
            {
                curAntLevel = 5;
            }
            //}

            if (curAntLevel == 0) {
                signalImage.setImageResource(R.drawable.dtv_signal_bar_0);
            } else if (curAntLevel == 1) {
                signalImage.setImageResource(R.drawable.dtv_signal_bar_1);
            } else if (curAntLevel == 2) {
                signalImage.setImageResource(R.drawable.dtv_signal_bar_2);
            } else if (curAntLevel == 3) {
                signalImage.setImageResource(R.drawable.dtv_signal_bar_3);
            } else if (curAntLevel == 4) {
                signalImage.setImageResource(R.drawable.dtv_signal_bar_4);
            } else if (curAntLevel == 5) {
                signalImage.setImageResource(R.drawable.dtv_signal_bar_5);
            }

            // ]] eddy 160708
            /*if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_ONESEG_USB || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_ONESEG_USB) {
                if (event_weakSignal || event_noSiganl) {
                    signalImage.setImageResource(R.drawable.dtv_signal_bar_0);
                } else {
                    event_weakSignal = false;
                    event_noSiganl = false;
                    if (preAntLevel == 1) {
                        if (curAntLevel == 1) {
                            preAntLevel = 1;
                            signalImage.setImageResource(R.drawable.dtv_signal_bar_1);
                        } else if (curAntLevel == 5) {
                            preAntLevel = 2;
                            signalImage.setImageResource(R.drawable.dtv_signal_bar_2);
                        }
                    } else if (preAntLevel == 2) {
                        if (curAntLevel == 1) {
                            preAntLevel = 1;
                            signalImage.setImageResource(R.drawable.dtv_signal_bar_1);
                        } else if (curAntLevel == 5) {
                            preAntLevel = 3;
                            signalImage.setImageResource(R.drawable.dtv_signal_bar_3);
                        }
                    } else if (preAntLevel == 3) {
                        if (curAntLevel == 1) {
                            preAntLevel = 2;
                            signalImage.setImageResource(R.drawable.dtv_signal_bar_2);
                        } else if (curAntLevel == 5) {
                            preAntLevel = 4;
                            signalImage.setImageResource(R.drawable.dtv_signal_bar_4);
                        }
                    } else if (preAntLevel == 4) {
                        if (curAntLevel == 1) {
                            preAntLevel = 3;
                            signalImage.setImageResource(R.drawable.dtv_signal_bar_3);
                        } else if (curAntLevel == 5) {
                            preAntLevel = 5;
                            signalImage.setImageResource(R.drawable.dtv_signal_bar_5);
                        }
                    } else if (preAntLevel == 5) {
                        if (curAntLevel == 1) {
                            preAntLevel = 4;
                            signalImage.setImageResource(R.drawable.dtv_signal_bar_4);
                        } else if (curAntLevel == 5) {
                            preAntLevel = 5;
                            signalImage.setImageResource(R.drawable.dtv_signal_bar_5);
                        }
                    }
                }
            } else {
            if (curAntLevel == 0) {
                        signalImage.setImageResource(R.drawable.dtv_signal_bar_0);
            } else if (curAntLevel == 1) {
                        signalImage.setImageResource(R.drawable.dtv_signal_bar_1);
            } else if (curAntLevel == 2) {
                        signalImage.setImageResource(R.drawable.dtv_signal_bar_2);
            } else if (curAntLevel == 3) {
                        signalImage.setImageResource(R.drawable.dtv_signal_bar_3);
            } else if (curAntLevel == 4) {
                        signalImage.setImageResource(R.drawable.dtv_signal_bar_4);
            } else if (curAntLevel == 5) {
                        signalImage.setImageResource(R.drawable.dtv_signal_bar_5);
            }
            }*/

        } else { // fullseg
            fullSegCN = FCI_TVi.GetSignal();
            currentSignalVal = FCI_TVi.GetMoreSignalVal2();
            fullSegBER = currentSignalVal[0];
            fullSegPER = currentSignalVal[1];
            oneSegBER = currentSignalVal[2];
            oneSegPER = currentSignalVal[3];
            fullSegRSSI = currentSignalVal[4];
            if (MainActivity.getInstance().is_inserted_card == 2) {
                if(CommonStaticData.receivemode==2 && CommonStaticData.scanCHnum > 0 && CommonStaticData.scanningNow==false && CommonStaticData.handoverMode==0) {

                    //if(currentSignalVal[0]>400 && currentCN<17){
                    //if (currentSignalVal[0] > 350 && currentPER >= 1000) {
                    if (fullSegBER > 350 && fullSegPER >= 1000) {
                        moveOneTh++;
                        TVlog.e("Air SQ", ">>>>> fullseg moveOneTh= "+moveOneTh);
                    } else {
                        moveOneTh = 0;
                    }
                    // live add condition
                    if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB) {
                        if (moveOneTh == 2) {
                            TVlog.e("Air SQ", ">>>>> CHANNEL_SWITCHING to 1seg ");
                            if (MainActivity.isMainActivity) {
                            MainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_SWITCHING, 0, 0, null);
                            } else if (FloatingWindow.isFloating) {
                                FloatingWindow.getInstance().sendEvent(TVEVENT.E_CHANNEL_SWITCHING_FLOATING, 0, 0, null);
                            } else if (ChatMainActivity.isChat) {
                                ChatMainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_SWITCHING_CHAT, 0, 0, null);
                            }
                            moveOneTh = 0;
                        }
                    }
                }
            } else {
                if(CommonStaticData.receivemode==2 && CommonStaticData.scanCHnum > 0 && CommonStaticData.scanningNow==false && MainActivity.getInstance().is_inserted_card==1 && CommonStaticData.handoverMode==0) {

                    //if(currentSignalVal[0]>400 && currentCN<17){
                    //if (currentSignalVal[0] > 350 && currentPER >= 1000) {
                    if (fullSegBER > 350 && fullSegPER >= 1000) {
                        moveOneTh++;
                        TVlog.e("Air SQ", ">>>>> fullseg moveOneTh= " + moveOneTh);
                    } else {
                        moveOneTh = 0;
                    }
                    // live add condition
                    if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB) {
                        if (moveOneTh == 2) {
                            TVlog.e("Air SQ", ">>>>> CHANNEL_SWITCHING to 1seg ");
                            if (MainActivity.isMainActivity) {
                            MainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_SWITCHING, 0, 0, null);
                            } else if (FloatingWindow.isFloating) {
                                FloatingWindow.getInstance().sendEvent(TVEVENT.E_CHANNEL_SWITCHING_FLOATING, 0, 0, null);
                            } else if (ChatMainActivity.isChat) {
                                ChatMainActivity.getInstance().sendEvent(TVEVENT.E_CHANNEL_SWITCHING_CHAT, 0, 0, null);
                            }
                            moveOneTh = 0;
                        }
                    }
                }
            }

            if (buildOption.FCI_SOLUTION_MODE >= buildOption.JAPAN_FILE) {
                fullSegBER = 0;
            }else if(MainActivity.withoutUSB) {     // justin add
                fullSegBER = 1000;
            }
            else
            {
                // [[ eddy 160708
                TVlog.i("Air SQ", ">>>>> Full-seg Signal Level = " + "\n"
                        + fullSegCN + "  BER = " + fullSegBER + ","+ "  PER = " +fullSegPER +"," + "  RSSI = " + fullSegRSSI
                        +" " + LNA+" " +RFVGA+" " +CSF+" " +CCI+" " +cacPgd+" " +mrdPgd);
                // ]] eddy 160708
            }

            if (fullSegCN >= 100) {
                fullSegCN = 0;
            }

            // [[ eddy 160708
            if (fullSegBER >= 10000 && fullSegRSSI < -90) {
                //currentSignalVal[0] = 400;
                fullSegBER = 500;
            }

            if(fullSegBER >= 500 || fullSegPER >=5000) {
                curAntLevelF = 0;
            }
            else if ((fullSegBER < 500 && fullSegBER >= 400) || (fullSegPER < 5000 && fullSegPER >= 1000)){
                curAntLevelF = 1;
            }
            else if (fullSegBER < 400 && fullSegBER >= 300 || (fullSegPER < 1000 && fullSegPER >= 100)){
                curAntLevelF = 2;
            }
            else if (fullSegBER < 300 && fullSegBER >= 200) {
                curAntLevelF = 3;
            }
            else if (fullSegBER < 200 && fullSegBER >= 100) {
                curAntLevelF = 4;
            }
            else if (fullSegBER < 100)
            {
                curAntLevelF = 5;
            }

            if (curAntLevelF == 0) {
                signalImage.setImageResource(R.drawable.dtv_signal_bar_0);
            } else if (curAntLevelF == 1) {
                signalImage.setImageResource(R.drawable.dtv_signal_bar_1);
            } else if (curAntLevelF == 2) {
                signalImage.setImageResource(R.drawable.dtv_signal_bar_2);
            } else if (curAntLevelF == 3) {
                signalImage.setImageResource(R.drawable.dtv_signal_bar_3);
            } else if (curAntLevelF == 4) {
                signalImage.setImageResource(R.drawable.dtv_signal_bar_4);
            } else if (curAntLevelF == 5) {
                signalImage.setImageResource(R.drawable.dtv_signal_bar_5);
            }


            // ]] eddy 160708

            /*if (buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_USB || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_USB || buildOption.FCI_SOLUTION_MODE == buildOption.JAPAN_ONESEG_USB
                    || buildOption.FCI_SOLUTION_MODE == buildOption.BRAZIL_ONESEG_USB || buildOption.FCI_SOLUTION_MODE == buildOption.PHILIPPINES_ONESEG_USB) {
                if (event_weakSignal || event_noSiganl) {
                    signalImage.setImageResource(R.drawable.dtv_signal_bar_0);
                } else {
                    event_weakSignal = false;
                    event_noSiganl = false;

                    if (preAntLevelF == 1) {
                        if (curAntLevelF == 1) {
                            preAntLevelF = 1;
                            signalImage.setImageResource(R.drawable.dtv_signal_bar_1);
                        } else if (curAntLevelF == 5) {
                            preAntLevelF = 2;
                            signalImage.setImageResource(R.drawable.dtv_signal_bar_2);
                        }
                    } else if (preAntLevelF == 2) {
                        if (curAntLevelF == 1) {
                            preAntLevelF = 1;
                            signalImage.setImageResource(R.drawable.dtv_signal_bar_1);
                        } else if (curAntLevelF == 5) {
                            preAntLevelF = 3;
                            signalImage.setImageResource(R.drawable.dtv_signal_bar_3);
                        }
                    } else if (preAntLevelF == 3) {
                        if (curAntLevelF == 1) {
                            preAntLevelF = 2;
                            signalImage.setImageResource(R.drawable.dtv_signal_bar_2);
                        } else if (curAntLevelF == 5) {
                            preAntLevelF = 4;
                            signalImage.setImageResource(R.drawable.dtv_signal_bar_4);
                        }
                    } else if (preAntLevelF == 4) {
                        if (curAntLevelF == 1) {
                            preAntLevelF = 3;
                            signalImage.setImageResource(R.drawable.dtv_signal_bar_3);
                        } else if (curAntLevelF == 5) {
                            preAntLevelF = 5;
                            signalImage.setImageResource(R.drawable.dtv_signal_bar_5);
                        }
                    } else if (preAntLevelF == 5) {
                        if (curAntLevelF == 1) {
                            preAntLevelF = 4;
                            signalImage.setImageResource(R.drawable.dtv_signal_bar_4);
                        } else if (curAntLevelF == 5) {
                            preAntLevelF = 5;
                            signalImage.setImageResource(R.drawable.dtv_signal_bar_5);
                        }
                    }
                }
            } else {
            if (curAntLevelF == 0) {
                        signalImage.setImageResource(R.drawable.dtv_signal_bar_0);
            } else if (curAntLevelF == 1) {
                        signalImage.setImageResource(R.drawable.dtv_signal_bar_1);
            } else if (curAntLevelF == 2) {
                        signalImage.setImageResource(R.drawable.dtv_signal_bar_2);
            } else if (curAntLevelF == 3) {
                        signalImage.setImageResource(R.drawable.dtv_signal_bar_3);
            } else if (curAntLevelF == 4) {
                        signalImage.setImageResource(R.drawable.dtv_signal_bar_4);
            } else if (curAntLevelF == 5) {
                        signalImage.setImageResource(R.drawable.dtv_signal_bar_5);
                    //}
                //}, delay_time);
            }
            }*/

        }

        //  [[ eddy 160708
        if(DebugMode.getDebugMode().checkingDebugOn())
        {
            int AntLevel;
            if (segType == 0) {
                AntLevel = curAntLevel;
                String senddata = "CN = " + oneSegCN + " Ant = " + AntLevel + "\n" +
                        "BER = " + oneSegBER + "\n" +
                        "PER = " + oneSegPER + "\n" +
                        "RSSI = " + oneSegRSSI + "\n" +
                        LNA + "\n" +
                        RFVGA + "\n" +
                        CSF + "\n" +
                        CCI + "\n" +
                        cacPgd + "\n" +
                        mrdPgd + " " + xxif + " " + Cacpgdist;

                MainActivity.getInstance().sendEvent(TVEVENT.E_DEBUG_SCREEN_DISPLAY, 0, 0, senddata);
            } else {
                AntLevel = curAntLevelF;
                String senddata = "CN = " + fullSegCN + " Ant = " + AntLevel + "\n" +
                        "BER = " + fullSegBER + "\n" +
                        "PER = " + fullSegPER + "\n" +
                        "RSSI = " + fullSegRSSI + "\n" +
                        LNA + "\n" +
                        RFVGA + "\n" +
                        CSF + "\n" +
                        CCI + "\n" +
                        cacPgd + "\n" +
                        mrdPgd + " " + xxif + " " + Cacpgdist;

                MainActivity.getInstance().sendEvent(TVEVENT.E_DEBUG_SCREEN_DISPLAY, 0, 0, senddata);
            }
        }
        // ]] eddy 160708

        if (segType == 0) {  //1seg
            BER = oneSegBER;
        } else if (segType == 1) {  //full-seg
            BER = fullSegBER;
        }

        return BER;
    }
}