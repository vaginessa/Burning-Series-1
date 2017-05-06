package to.bs.bruningseriesmeterial;

import android.app.Application;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

/**
 * Created by Phillipp on 02.05.2017.
 */

@ReportsCrashes(mailTo = "meinerlpserver@gmail.com",
        customReportContent = { ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT },
        resToastText = R.string.crash_toast_text,
        mode = ReportingInteractionMode.DIALOG,
        resDialogIcon = android.R.drawable.ic_dialog_info, //optional. default is a warning sign
        resDialogTitle = R.string.crash_dialog_title, // optional. default is your application name
        resDialogOkToast = R.string.crash_dialog_ok_toast, // optional. displays a Toast message when the user accepts to send a report.
        resDialogTheme = android.R.style.Theme_DeviceDefault_Dialog)
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
    }
}
