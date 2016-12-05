package com.project.kongdy.monitortoast;

import android.accessibilityservice.AccessibilityService;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

/**
 * 监测Toast主服务
 *
 * @author kongdy
 *         on 2016/11/29
 */
public class MonitorToastService extends AccessibilityService {

    private AlertDialog dialog;
    /**
     * 最后一次记录的时间
     */
    private long lastRecordTime;
    /**
     * 待过滤字符
     */
    private String filterStr ="";


    private BroadcastReceiver timeBlockReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                if (action.equals(getString(R.string.param))) {
                    String extraStr = intent.getStringExtra(getString(R.string.time_block));
                    filterStr = intent.getStringExtra(getString(R.string.filter_str));
                    long extra;
                    try {
                        extra = Long.parseLong(extraStr);
                    } catch (NumberFormatException e) {
                        return;
                    }
                    lastRecordTime = System.currentTimeMillis() + extra;
                    Log.e("timeBlockReceiver", "receive time:" + extra + ",filter str:" + filterStr);
                }
            }
        }
    };

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (lastRecordTime < System.currentTimeMillis())
            return;
        if (event.getEventType() != AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED)
            return;
        String sourcePackageName = (String) event.getPackageName();
        Parcelable parcelable = event.getParcelableData();
        if (parcelable instanceof Notification) {
        } else {
            String toastMsg = (String) event.getText().get(0);

            if (!toastMsg.contains(filterStr)) {
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(sourcePackageName + ":" + toastMsg);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setCancelable(true);

            dialog = builder.create();
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            dialog.show();

            Log.e(sourcePackageName, toastMsg);
        }
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        lastRecordTime = System.currentTimeMillis();

        IntentFilter intentFilter = new IntentFilter(getResources().getString(R.string.param));

        registerReceiver(timeBlockReceiver, intentFilter);

    }

    @Override
    public void onDestroy() {

        unregisterReceiver(timeBlockReceiver);

        super.onDestroy();
    }

    @Override
    public void onInterrupt() {
    }
}
