package com.project.kongdy.monitortoast;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

/**
 *  监测Toast主服务
 * @author kongdy
 *         on 2016/11/29
 */
public class MonitorToastService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if(event.getEventType() != AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED)
            return;
        String sourcePackageName = (String)event.getPackageName();
        Parcelable parcelable = event.getParcelableData();
        if(parcelable instanceof Notification){
        } else {
            String toastMsg = (String) event.getText().get(0);
            Log.e(sourcePackageName,toastMsg);
        }
    }

    @Override
    public void onInterrupt() {

    }
}
