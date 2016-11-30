package com.project.kongdy.monitortoast;

import android.accessibilityservice.AccessibilityService;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.DialogInterface;
import android.os.Parcelable;
import android.util.Log;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

/**
 *  监测Toast主服务
 * @author kongdy
 *         on 2016/11/29
 */
public class MonitorToastService extends AccessibilityService {

    private AlertDialog dialog;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if(event.getEventType() != AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED)
            return;
        String sourcePackageName = (String)event.getPackageName();
        Parcelable parcelable = event.getParcelableData();
        if(parcelable instanceof Notification){
        } else {
            String toastMsg = (String) event.getText().get(0);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(sourcePackageName+":"+toastMsg);
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

            Log.e(sourcePackageName,toastMsg);
        }
    }

    @Override
    public void onInterrupt() {

    }
}
