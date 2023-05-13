package com.nexgo.apiv3demo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.nexgo.common.ByteUtils;
import com.nexgo.common.LogUtils;
import com.nexgo.oaf.apiv3.DeviceEngine;
import com.nexgo.oaf.apiv3.device.pinpad.AlgorithmModeEnum;
import com.nexgo.oaf.apiv3.device.pinpad.CalcModeEnum;
import com.nexgo.oaf.apiv3.device.pinpad.MacAlgorithmModeEnum;
import com.nexgo.oaf.apiv3.device.pinpad.OnPinPadInputListener;
import com.nexgo.oaf.apiv3.device.pinpad.PinAlgorithmModeEnum;
import com.nexgo.oaf.apiv3.device.pinpad.PinKeyboardModeEnum;
import com.nexgo.oaf.apiv3.device.pinpad.PinPad;
import com.nexgo.oaf.apiv3.device.pinpad.PinPadKeyCode;
import com.nexgo.oaf.apiv3.device.pinpad.WorkKeyTypeEnum;

import java.util.Arrays;

public class PinpadActivity extends AppCompatActivity {

    private DeviceEngine deviceEngine;
    private static final byte[] main_key_data = new byte[16];
    private static final byte[] work_key_data = new byte[16];
    private PinPad pinpad;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinpad);
        mContext = this;
        deviceEngine = ((NexgoApplication) getApplication()).deviceEngine;
        pinpad = deviceEngine.getPinPad();
        pinpad.setAlgorithmMode(AlgorithmModeEnum.DES);
        pinpad.setPinKeyboardMode(PinKeyboardModeEnum.FIXED);
        Arrays.fill(main_key_data, (byte) 0x31);
        Arrays.fill(work_key_data, (byte) 0x31);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_key_inject:
                mainKeyInjectTest();
                break;
            case R.id.work_key_inject:
                workKeyInjectTest();
                break;
            case R.id.work_key_enc:
                workKeyEncTest();
                break;
            case R.id.input_pin:
                inputPinTest();
                break;
            case R.id.pinpad_calMac:
                calcMacValue();
                break;
            case R.id.pinpad_tken:
                tkEnTest();
                break;
            case R.id.pinpad_custom_layout:
                startActivity(new Intent(this, PinpadCustomLayoutActivity.class));
                break;

            case R.id.pinpad_dukpt_layout:
                Intent intent = new Intent(this, DUKPTActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void tkEnTest() {
        String tk = "6212260200103692998D26032209259991693";
        byte[] bTk = ByteUtils.hexString2ByteArray(tk);
        byte[] enData = pinpad.encryptTrackData(10, bTk, bTk.length);
        Toast.makeText(this, enData == null ? getString(R.string.encryption_fail): ByteUtils.byteArray2HexString(enData), Toast.LENGTH_SHORT).show();
    }

    String text = "";

    private void inputPinTest() {
        text = "";
        View dv = getLayoutInflater().inflate(R.layout.dialog_inputpin_layout, null);
        final TextView tv = (TextView) dv.findViewById(R.id.input_pin);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).setView(dv).create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        int[] supperLen = new int[]{0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c};
        byte[] pan = ByteUtils.string2ASCIIByteArray("1234567890123");
        pinpad.inputOnlinePin(supperLen, 60, pan, 10, PinAlgorithmModeEnum.ISO9564FMT1, new OnPinPadInputListener() {
            @Override
            public void onInputResult(final int retCode, byte[] data) {
                alertDialog.dismiss();
                System.out.println("onInputResult->"+retCode+","+ByteUtils.byteArray2HexStringWithSpace(data));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PinpadActivity.this, retCode + "", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onSendKey(byte keyCode) {
                if (keyCode == PinPadKeyCode.KEYCODE_CLEAR) {
                    text = "";
                } else {
                    text += "* ";
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv.setText(text);
                    }
                });
            }
        });
    }

    private void workKeyEncTest() {
        byte[] result = pinpad.calcByWKey(10, WorkKeyTypeEnum.MACKEY, new byte[16], 16, CalcModeEnum.ENCRYPT);
        Toast.makeText(PinpadActivity.this, ByteUtils.byteArray2HexString(result), Toast.LENGTH_SHORT).show();
    }

    private void workKeyInjectTest() {
        int result = pinpad.writeWKey(10, WorkKeyTypeEnum.MACKEY, work_key_data, work_key_data.length);
        Toast.makeText(PinpadActivity.this, "TAK" + result + "", Toast.LENGTH_SHORT).show();
        result = pinpad.writeWKey(10, WorkKeyTypeEnum.PINKEY, work_key_data, work_key_data.length);
        Toast.makeText(PinpadActivity.this, "TPK" + result + "", Toast.LENGTH_SHORT).show();
        result = pinpad.writeWKey(10, WorkKeyTypeEnum.TDKEY, work_key_data, work_key_data.length);
        Toast.makeText(PinpadActivity.this, "TDK" + result + "", Toast.LENGTH_SHORT).show();
        result = pinpad.writeWKey(10, WorkKeyTypeEnum.ENCRYPTIONKEY, work_key_data, work_key_data.length);
        Toast.makeText(PinpadActivity.this, "TEK" + result + "", Toast.LENGTH_SHORT).show();
    }

    private void mainKeyInjectTest() {
        int result = pinpad.writeMKey(10, main_key_data, main_key_data.length);
        Toast.makeText(PinpadActivity.this, getString(R.string.result) + result + "", Toast.LENGTH_SHORT).show();
    }

    public void calcMacValue() {
        String mac = "4984511233489929|000000001000|4984511233489929=21051010000021800001||PTbEBBZDDCivdD9v|";
        byte[] macData = pinpad.calcMac(10, MacAlgorithmModeEnum.X919, mac.getBytes());
        LogUtils.debug("macData:{}", ByteUtils.byteArray2HexString(macData));
        Toast.makeText(PinpadActivity.this, "mac:" + ByteUtils.byteArray2HexString(macData) + "", Toast.LENGTH_SHORT).show();
    }
}
