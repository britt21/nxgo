package com.nexgo.apiv3demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.nexgo.common.ByteUtils;
import com.nexgo.oaf.apiv3.DeviceEngine;
import com.nexgo.oaf.apiv3.SdkResult;
import com.nexgo.oaf.apiv3.device.pinpad.AlgorithmModeEnum;
import com.nexgo.oaf.apiv3.device.pinpad.DesAlgorithmModeEnum;
import com.nexgo.oaf.apiv3.device.pinpad.DukptKeyModeEnum;
import com.nexgo.oaf.apiv3.device.pinpad.DukptKeyTypeEnum;
import com.nexgo.oaf.apiv3.device.pinpad.MacAlgorithmModeEnum;
import com.nexgo.oaf.apiv3.device.pinpad.PinPad;

public class DUKPTActivity extends AppCompatActivity {

    private DeviceEngine deviceEngine;
    private PinPad pinpad;
    private final int KEYINDEX = 6;
    private final byte[] BDK = new byte[]{1,2,3,4,5,6,7,8,1,2,3,4,5,6,7,8};
    private final byte[] KSN = new byte[]{1,2,3,4,5,6,7,0,0,0};
    private final byte[] needCryDatas = new byte[]{1,2,3,4,5,6,7,8,1,2,3,4,5,6,7,8,1,2,3,4,5,6,7,8};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dukpt);

        initView();

        initDUPKT();
    }

    // initView
    private TextView log_txt;
    private void initView(){
        log_txt = (TextView) findViewById(R.id.log_txt);
    }

    // init DUKPT
    private void initDUPKT(){
        deviceEngine = ((NexgoApplication) getApplication()).deviceEngine;
        pinpad = deviceEngine.getPinPad();

        pinpad.setAlgorithmMode(AlgorithmModeEnum.DUKPT);
    }

    // inject BDK and KSN
    private void injectBDKAndKSN(){
        int result = pinpad.dukptKeyInject(KEYINDEX, DukptKeyTypeEnum.BDK, BDK, BDK.length, KSN);
        Toast.makeText(this, getString(R.string.result)+result, Toast.LENGTH_SHORT).show();
    }

    // encry datas
    private void encryDatas(){
        byte[] cryDatas = pinpad.dukptEncrypt(KEYINDEX, DukptKeyModeEnum.REQUEST, needCryDatas, needCryDatas.length, DesAlgorithmModeEnum.CBC, new byte[]{0,0,0,0,0,0,0,0});
        if(cryDatas == null){
            Toast.makeText(this, getString(R.string.dukptencryptfail), Toast.LENGTH_SHORT).show();
            return;
        }
        log_txt.setText("encryDatas:"+ByteUtils.byteArray2HexStringWithSpace(cryDatas));

        byte[] mac = pinpad.calcMac(KEYINDEX, MacAlgorithmModeEnum.X919, DukptKeyModeEnum.REQUEST, new byte[]{1,2,3,4,5,6,7,8,1,2,3,4,5,6,7,8,1,2,3,4,5,6,7,8});

        System.out.println("mac->"+ByteUtils.byteArray2HexStringWithSpace(mac));

    }
    // EC increase
    private void ECIncrease(){
        System.out.println("ECIncrease");
        pinpad.dukptKsnIncrease(KEYINDEX);
        Toast.makeText(this, "EC Increased", Toast.LENGTH_SHORT).show();
    }

    // calcMAC
    private void calcDUKPTMAC(){

    }

    // current KSN
    private void currentKSN(){
        byte[] nowKsn = pinpad.dukptCurrentKsn(KEYINDEX);
        if(nowKsn == null){
            Toast.makeText(this, getString(R.string.dukptencryptfail), Toast.LENGTH_SHORT).show();
            return;
        }
        log_txt.setText("KSN:"+ByteUtils.byteArray2HexStringWithSpace(nowKsn));
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.injectkey_btn:
                injectBDKAndKSN();
                break;
            case R.id.encrypdatas_btn:
                encryDatas();
                break;
            case R.id.ecincrease_btn:
                ECIncrease();
                break;
            case R.id.currentksn_btn:
                currentKSN();
                break;
        }
    }
}
