package under_bridge.sms_server;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import android.os.Handler;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Илья Кокорин on 28.09.2017.
 */

public class SMSMonitor extends BroadcastReceiver {
    private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
    String sms_from;
    String res = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            Object[] pduArray = (Object[]) intent.getExtras().get("pdus");
            SmsMessage[] messages = new SmsMessage[pduArray.length];
            for (int i = 0; i < pduArray.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pduArray[i]);
            }
            StringBuilder bodyText = new StringBuilder();
            for (int i = 0; i < messages.length; i++) {
                bodyText.append(messages[i].getMessageBody());
            }
            System.out.println(bodyText);
            String body = bodyText.toString();
            double strong = 0;
            for (int i = 0, n = body.length()-10; i < n; i++) {
                char c = body.charAt(i);
                if(c == '규'){
                    char d = body.charAt(i + 1);
                    if(d == '모'){
                        //System.out.println(body.charAt(i + 2) + body.charAt(i + 3) + body.charAt(i + 4));
                        //System.out.println(body.substring(i + 2, i + 5));
                        System.out.println(Double.parseDouble(body.substring(i + 2, i + 5)));
                        strong = Double.parseDouble(body.substring(i + 2, i + 5));
                    }
                }
            }
            if(strong > 6.0){
                body = "건물이 지진에 취약합니다.  밖으로 대피하십시오.";
            } else {
                body = "건물에 내진설계가 되어있어 발생한 " + strong + "강도 지진에 대비할 수 있습니다.";
            }
            final Toast bodyToast = Toast.makeText(context, body, Toast.LENGTH_LONG);

            Handler handler = new Handler();

            bodyToast.show();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bodyToast.cancel();
                }
            }, 10000);

            sms_from = messages[0].getDisplayOriginatingAddress();
            /*Intent myIntent = new Intent(context, SmsService.class);
            myIntent.putExtra("sms_body", body);
            myIntent.putExtra("sms_from", sms_from);
            context.startService(myIntent);*/
            //new DownloadWebpageTask().execute(body);

            res = "Message recieved";
            SmsManager.getDefault().sendTextMessage(sms_from, null, res, null, null);


            abortBroadcast();
        }
    }
}
