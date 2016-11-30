package com.project.kongdy.monitortoast;

import android.Manifest;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button button_switch;
    private final static int REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent intent = new Intent(this,MonitorToastService.class);
        startService(intent);

        button_switch = (Button) findViewById(R.id.button_switch);
        button_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW)
                != PackageManager.PERMISSION_GRANTED) {
            ((TextView)findViewById(R.id.tv_tip)).setText("如果不弹窗，请手动在权限管理处，向应用开放系统弹窗/悬浮窗权限");
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.SYSTEM_ALERT_WINDOW)){
                Toast.makeText(MainActivity.this, "您已禁止该权限，需要重新开启。", Toast.LENGTH_SHORT).show();
            }else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW}, REQUEST_CODE);
            }

        } else {
            ((TextView)findViewById(R.id.tv_tip)).setText("");
        }
        updateServiceStatus();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

     if(requestCode == REQUEST_CODE) {
         if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
             updateServiceStatus();
         } else {
             Toast.makeText(this,"缺少系统弹框授权，运行中将无法进行弹框",Toast.LENGTH_LONG).show();
         }
     }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void onButtonClick() {
        startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
    }

    private void updateServiceStatus() {
        if(button_switch == null) {
            button_switch = (Button) findViewById(R.id.button_switch);
        }
        boolean ServiceEnabled = false;
        // 循环遍历所有服务，查看是否开启
        AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> accessibilityServices = accessibilityManager
                .getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_SPOKEN);
        for (AccessibilityServiceInfo info : accessibilityServices) {
            if (info.getId().endsWith("/.MonitorToastService")) {
                ServiceEnabled = true;
                break;
            }
        }
        if (ServiceEnabled) {
            button_switch.setText("服务已经开启，点击手动关闭");
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            button_switch.setText("服务已经关闭，点击手动开启");
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }
}
