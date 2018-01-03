package kr.co.fci.tv.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import kr.co.fci.tv.R;
import kr.co.fci.tv.buildOption;
import kr.co.fci.tv.saves.CommonStaticData;
import kr.co.fci.tv.setting.SettingActivity;
import kr.co.fci.tv.tvSolution.FCI_TVi;
import kr.co.fci.tv.tvSolution.TVBridge;
import kr.co.fci.tv.tvSolution.buildInformation;
import kr.co.fci.tv.util.CustomToast;

/**
 * Created by live.kim on 2015-08-15.
 */
public class AboutActivity extends Activity {

    //ActionBar abar;

    ImageButton imageButton_open;
    TextView versionTextView;
    private Handler mSignalHandler = null;
    private Runnable mSignalHandlerTask = null;
    private InputMethodManager imm;
    LinearLayout engineeringModeExt1;
    LinearLayout engineeringModeExt2;
    EditText editTextFreq;
    EditText editTextSize;
    TextView textView_Sig;
    TextView textView_Mem;
    Button btnFreqSet;
    Button btnTSCapture;
    boolean isFreqSet = false;
    boolean isDumpStarted = false;
    String memoryPath;
    int mFreeSize = 0;
    int mStartFreeSize = 0;
    int mSizeToDump = 0;
    String aboutVersion;

		public static AboutActivity instance;
    public static AboutActivity getInstance()
    {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        instance = this;
        CommonStaticData.aboutActivityShow = true;   // justin add for dongle detached
        if(buildOption.GUI_STYLE == 0 ||buildOption.GUI_STYLE == 1 )  {
       aboutVersion = getResources().getString(R.string.ReleaseDate) + " " + buildInformation.RELEASE_DATE + "\n" +
                getResources().getString(R.string.customerName) + " " + getResources().getString(R.string.TVAppVersion) +
                " " + buildInformation.RELEASE_VERSION + "\n" +
                "(" + getResources().getString(R.string.TVSolutionVersion) + " " + buildInformation.RELEASE_SOL_VERSION + ")";

    }else{
        aboutVersion=    getResources().getString(R.string.customerName)+"\n"+
                 getResources().getString(R.string.TVAppVersion)+" "+buildInformation.RELEASE_VERSION;
    }
        versionTextView = (TextView) findViewById(R.id.textView_version);
        versionTextView.setText(aboutVersion);

        LinearLayout title_about = (LinearLayout) findViewById(R.id.title_about);
        title_about.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
            		CommonStaticData.aboutActivityShow = false;   // justin add for dongle detached
                finish();
                Intent intent = new Intent(AboutActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        ImageButton btn_back_about = (ImageButton) findViewById(R.id.btn_back_about);
        btn_back_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            		CommonStaticData.aboutActivityShow = false;   // justin add for dongle detached
                finish();
                Intent intent = new Intent(AboutActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        imageButton_open = (ImageButton) findViewById(R.id.imageButton_open);
        imageButton_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            		CommonStaticData.aboutActivityShow = false;   // justin add for dongle detached
                Intent intent = new Intent(AboutActivity.this, OpenActivity.class);
                startActivity(intent);
                finish();
            }
        });

        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        engineeringModeExt1 = (LinearLayout) findViewById(R.id.engineering_mode_ext1);
        engineeringModeExt2 = (LinearLayout) findViewById(R.id.engineering_mode_ext2);
        if (buildOption.ADD_TS_CAPTURE && buildOption.FCI_SOLUTION_MODE < buildOption.JAPAN_FILE) {
            engineeringModeExt1.setVisibility(View.VISIBLE);
            engineeringModeExt2.setVisibility(View.VISIBLE);
            editTextFreq = (EditText) findViewById(R.id.editTextFreq);
            editTextSize = (EditText) findViewById(R.id.editTextSize);
            textView_Sig = (TextView) findViewById(R.id.textView_Sig);
            textView_Mem = (TextView) findViewById(R.id.textView_Mem);
            btnFreqSet = (Button) findViewById(R.id.buttonFreqSet);
            btnFreqSet.setBackgroundResource(R.drawable.tab_bg_selector);
            btnTSCapture = (Button) findViewById(R.id.buttonTSCapture);
            btnTSCapture.setEnabled(false);
            btnTSCapture.setBackgroundResource(R.color.btn_disable);
            mSignalHandler = new Handler();
            mSignalHandlerTask = new Runnable() {
                public void run() {
                    if (isFreqSet) { //if btn set
                        int signal = FCI_TVi.GetSignal();
                        int more_signal[] = FCI_TVi.GetMoreSignalVal();
                        int valueBER = more_signal[0];
                        int valuePER = more_signal[1];
                        int valueRSSI = more_signal[2];

                        String strtmp = String.format("C/N = %d\nBER = %d\nPER = %d\nRSSI = %d", signal, valueBER, valuePER, valueRSSI);

                        //check signal
                        if (valueBER > 100) {
                            textView_Sig.setTextColor(Color.RED);
                            if (isDumpStarted == false) {
                            btnTSCapture.setEnabled(false);
                                btnTSCapture.setBackgroundResource(R.color.btn_disable);
                        }
                        }
                        else {
                            textView_Sig.setTextColor(Color.WHITE);
                            btnTSCapture.setEnabled(true);
                            btnTSCapture.setBackgroundResource(R.drawable.tab_bg_selector);
                        }
                        textView_Sig.setTextSize(12);
                        textView_Sig.setText(strtmp);

                        long freeSize;
                        String strFreeSize;
                        //memoryPath = Environment.getDataDirectory().getPath();
                        memoryPath = Environment.getExternalStorageDirectory().getPath();
                        StatFs stat = new StatFs(memoryPath);
                        long blockSize = 0;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                            blockSize = stat.getBlockSizeLong();
                        }
                        long availableBlocks = 0;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                            availableBlocks = stat.getAvailableBlocksLong();
                        }

                        freeSize = availableBlocks * blockSize;

                        if (freeSize >= 1024) {
                            freeSize /= 1024;
                            if (freeSize >= 1024) {
                                freeSize /= 1024;
                            }
                        }
                        mFreeSize = (int)freeSize;

                        //check memory size
                        if (mFreeSize < 1024) {
                            textView_Mem.setTextColor(Color.RED);
                            if (mFreeSize < 512) {
                                if (isDumpStarted) {
                                    FCI_TVi.stopDumpTS();
                                    isDumpStarted = false;
                                    CustomToast toast = new CustomToast(getApplicationContext());
                                    toast.showToast(getApplicationContext(), "Memory size is very low", Toast.LENGTH_SHORT);
                                    /*Toast.makeText(getApplicationContext(), "Memory size is very low", Toast.LENGTH_SHORT).show();*/
                                }
                                btnTSCapture.setTextColor(Color.WHITE);
                                btnTSCapture.setText("DUMP");
                                btnTSCapture.setEnabled(false);
                                btnTSCapture.setBackgroundResource(R.color.btn_disable);
                            }
                        }
                        else {
                            textView_Mem.setTextColor(Color.WHITE);
                            if (isDumpStarted) {
                                if ((mStartFreeSize - mFreeSize) >= mSizeToDump) {
                                    FCI_TVi.stopDumpTS();
                                    isDumpStarted = false;
                                    CustomToast toast = new CustomToast(getApplicationContext());
                                    toast.showToast(getApplicationContext(), "Saved TS " + mSizeToDump + "MB to \"" + memoryPath +"\"", Toast.LENGTH_SHORT);
                                    /*Toast.makeText(getApplicationContext(), "Saved TS "+mSizeToDump+"MB to \""+memoryPath+"\"", Toast.LENGTH_SHORT).show();*/
                                    btnTSCapture.setTextColor(Color.WHITE);
                                    btnTSCapture.setText("DUMP");
                                    btnTSCapture.setEnabled(true);
                                }
                        }
                        }

                        strFreeSize = String.format("[Free: %d MB]", mFreeSize);
                        textView_Mem.setTextSize(12);
                        textView_Mem.setText(strFreeSize);

                        if (isDumpStarted) {
                            editTextFreq.setEnabled(false);
                            editTextSize.setEnabled(false);
                            btnFreqSet.setEnabled(false);
                            btnFreqSet.setBackgroundResource(R.color.btn_disable);
                        }
                        else {
                            editTextFreq.setEnabled(true);
                            editTextSize.setEnabled(true);
                            btnFreqSet.setEnabled(true);
                            btnFreqSet.setBackgroundResource(R.drawable.tab_bg_selector);
                        }

                        mSignalHandler.postDelayed(mSignalHandlerTask, 1000);
                    }
                    else {
                        textView_Sig.setText("");
                        textView_Sig.setText("");
                    }
                }
            };
            btnFreqSet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imm.hideSoftInputFromWindow(editTextFreq.getWindowToken(), 0);
                    String strFreq = String.valueOf(editTextFreq.getText());
                    if (strFreq.length() != 6 || strFreq.contains(".")) {
                        isFreqSet = false;
                        CustomToast toast = new CustomToast(getApplicationContext());
                        toast.showToast(getApplicationContext(), "Enter valid value", Toast.LENGTH_SHORT);
                        /*Toast.makeText(getApplicationContext(), "Enter valid value", Toast.LENGTH_SHORT).show();*/
                        return;
                    }
                    else {
                        String strNum = editTextFreq.getText().toString();
                        int rcode = 0;
                        long inputFreq = (long)Integer.parseInt(strNum);
                        if (isFreqSet == true) {
                            FCI_TVi.AVStop();
                        }
                        TVBridge.stop();
                        rcode = FCI_TVi.playFrequency(inputFreq);
                        if (rcode == 0) {
                        isFreqSet = true;
                        }
                        if (isFreqSet == true) {
                            //FCI_TVi.AVStart(0); //play first index :just test
                        }
                        mSignalHandler.postDelayed(mSignalHandlerTask, 1000);
                    }
                }
            });
            btnTSCapture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imm.hideSoftInputFromWindow(editTextSize.getWindowToken(), 0);
                    if (isDumpStarted == false) {
                        String strSize = String.valueOf(editTextSize.getText());
                        if (strSize.length() == 0 || strSize.contains(".")) {
                            CustomToast toast = new CustomToast(getApplicationContext());
                            toast.showToast(getApplicationContext(), "Enter valid value", Toast.LENGTH_SHORT);
                            /*Toast.makeText(getApplicationContext(), "Enter valid value", Toast.LENGTH_SHORT).show();*/
                        } else {
                            String strNum = editTextSize.getText().toString();
                            int inputSize = Integer.parseInt(strNum);
                            if (inputSize >= mFreeSize) {
                                CustomToast toast = new CustomToast(getApplicationContext());
                                toast.showToast(getApplicationContext(), "Enter small value than remained.", Toast.LENGTH_SHORT);
                                /*Toast.makeText(getApplicationContext(), "Enter small value than remained.", Toast.LENGTH_SHORT).show();*/
                            } else if (inputSize < 100) {
                                CustomToast toast = new CustomToast(getApplicationContext());
                                toast.showToast(getApplicationContext(), "Enter large value than 100.", Toast.LENGTH_SHORT);
                                /*Toast.makeText(getApplicationContext(), "Enter large value than 100.", Toast.LENGTH_SHORT).show();*/
                            } else {
                                mStartFreeSize = mFreeSize;
                                mSizeToDump = inputSize;
                                int nRet = FCI_TVi.startDumpTS(mSizeToDump, "");
                                if (nRet == 0) {
                                    btnTSCapture.setTextColor(Color.RED);
                                    btnTSCapture.setText("STOP");
                                    isDumpStarted = true;
                                    return;
                                }
                                else {
                                    CustomToast toast = new CustomToast(getApplicationContext());
                                    toast.showToast(getApplicationContext(), "Dump failed.", Toast.LENGTH_SHORT);
                                    /*Toast.makeText(getApplicationContext(), "Dump failed.", Toast.LENGTH_SHORT).show();*/
                                }
                            }
                        }
                    }
                    else {
                        FCI_TVi.stopDumpTS();
                        isDumpStarted = false;
                        btnTSCapture.setTextColor(Color.WHITE);
                        btnTSCapture.setText("DUMP");
                        int saveTS = mStartFreeSize - mFreeSize;
                        CustomToast toast = new CustomToast(getApplicationContext());
                        toast.showToast(getApplicationContext(), "Saved TS "+saveTS+"MB to \""+memoryPath+"\"", Toast.LENGTH_SHORT);
                        /*Toast.makeText(getApplicationContext(), "Saved TS "+saveTS+"MB to \""+memoryPath+"\"", Toast.LENGTH_SHORT).show();*/
                    }
                }
            });
        }
        else {
            engineeringModeExt1.setVisibility(View.INVISIBLE);
            engineeringModeExt2.setVisibility(View.INVISIBLE);
        }

        Button button_extra = (Button) findViewById(R.id.button_extra);
        button_extra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            		CommonStaticData.aboutActivityShow = false;   // justin add for dongle detached
                finish();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent homeIntent = new Intent(this, SettingActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
        }

        return super.onOptionsItemSelected(item);
    }
    
     @Override
    public void onBackPressed() {
        CommonStaticData.aboutActivityShow = false;   // justin add for dongle detached
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonStaticData.aboutActivityShow = false;   // justin add for dongle detached
        if (buildOption.ADD_TS_CAPTURE && buildOption.FCI_SOLUTION_MODE < buildOption.JAPAN_FILE) {
            mSignalHandler.removeCallbacks(mSignalHandlerTask);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (buildOption.ADD_TS_CAPTURE && buildOption.FCI_SOLUTION_MODE < buildOption.JAPAN_FILE) {
            if (isDumpStarted) {
                FCI_TVi.stopDumpTS();
            }
            if (isFreqSet) {
                TVBridge.serviceID_start(TVBridge.getCurrentChannel());
            }
        }
    }

}
