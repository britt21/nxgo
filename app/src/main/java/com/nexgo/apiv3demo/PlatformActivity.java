package com.nexgo.apiv3demo;

import android.content.Context;
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
import com.nexgo.oaf.apiv3.OnAppOperatListener;
import com.nexgo.oaf.apiv3.device.pinpad.AlgorithmModeEnum;
import com.nexgo.oaf.apiv3.device.pinpad.CalcModeEnum;
import com.nexgo.oaf.apiv3.device.pinpad.MacAlgorithmModeEnum;
import com.nexgo.oaf.apiv3.device.pinpad.OnPinPadInputListener;
import com.nexgo.oaf.apiv3.device.pinpad.PinAlgorithmModeEnum;
import com.nexgo.oaf.apiv3.device.pinpad.PinKeyboardModeEnum;
import com.nexgo.oaf.apiv3.device.pinpad.PinPad;
import com.nexgo.oaf.apiv3.device.pinpad.PinPadKeyCode;
import com.nexgo.oaf.apiv3.device.pinpad.WorkKeyTypeEnum;
import com.nexgo.oaf.apiv3.platform.Platform;

import java.util.Arrays;

public class PlatformActivity extends AppCompatActivity {

    private DeviceEngine deviceEngine;
    private Platform platform;
    private boolean homeButtonFlag = true;
    private boolean taskButtonFlag = true;
    private boolean powerButtonFlag = true;
    private boolean controlBarFlag = true;
    private boolean bottomBarFlag = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platform);
        deviceEngine = ((NexgoApplication) getApplication()).deviceEngine;
        platform = deviceEngine.getPlatform();
    }


    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.installAPK:
                //silent install apk, pass the path of the apk
                platform.installApp("/sdcard/app-debug.apk", new OnAppOperatListener() {
                    @Override
                    public void onOperatResult(int i) {
                        LogUtils.debug("installApp ret :{}", i);

                    }
                });
                break;

            case R.id.uninstall:
                //silent uninstall apk,pass the package name of the apk
                platform.uninstallApp("com.example.demo", new OnAppOperatListener() {
                    @Override
                    public void onOperatResult(int i) {
                        LogUtils.debug("uninstallApp ret :{}", i);
                    }
                });
                break;

            case R.id.updateOS:
                platform.updateFirmware("/sdcard/update_new.zip");
                break;

            case R.id.shutdown:
                platform.shutDownDevice();
                break;

            case R.id.reboot:
                platform.rebootDevice();
                break;

            case R.id.home:
                if(homeButtonFlag){
                    platform.enableHomeButton();
                }else{
                    platform.disableHomeButton();
                }
                homeButtonFlag = !homeButtonFlag;
                break;

            case R.id.task:
                if(taskButtonFlag){
                    platform.enableTaskButton();
                }else{
                    platform.disableTaskButton();
                }
                taskButtonFlag = !taskButtonFlag;
                break;

            case R.id.power:
                if(powerButtonFlag){
                    platform.enablePowerButton();
                }else{
                    platform.disablePowerButton();
                }
                powerButtonFlag = !powerButtonFlag;
                break;

            case R.id.controlBar:
                if(controlBarFlag){
                    platform.enableControlBar();
                }else{
                    platform.disableControlBar();
                }
                controlBarFlag = !controlBarFlag;
                break;

            case R.id.bottomBar:
                if(bottomBarFlag){
                    platform.showNavigationBar();
                }else{
                    platform.hideNavigationBar();
                }
                bottomBarFlag = !bottomBarFlag;
                break;

            default:
                break;
        }
    }



}
