package to.bs.bruningseriesmeterial.Utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import to.bs.bruningseriesmeterial.service.HolidayManager;

/**
 * Created by Phillipp on 26.05.2017.
 */

public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);

            Intent startServiceIntent = new Intent(context, HolidayManager.class);
            PendingIntent startServicePendingIntent = PendingIntent.getService(context,0,startServiceIntent,0);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis() + 1000*60);

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), 1000*60, startServicePendingIntent);

        }

    }
}
