package mahbubcseju.bkashverificationsystem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.telephony.SmsMessage;
import android.provider.Telephony;
import android.os.Build;

/**
 * Created by Student on 10/7/2018.
 */

public class SmsReceiver extends BroadcastReceiver {

    //interface
    private static SmsListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        final String tag =  ".onReceive";
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            Log.w(tag, "BroadcastReceiver failed, no intent data to process.");
            return;
        }

        Log.d(tag, "SMS_RECEIVED");

        String smsOriginatingAddress, smsDisplayMessage;

        // API level 19 (KitKat 4.4) getMessagesFromIntent
        if (Build.VERSION.SDK_INT >= 19) {
            for (SmsMessage message : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                Log.d(tag, "KitKat or newer");
                if (message == null) {
                    Log.e(tag, "SMS message is null -- ABORT");
                    break;
                }
                smsOriginatingAddress = message.getDisplayOriginatingAddress();
                //see getMessageBody();
                if(smsOriginatingAddress != null && smsOriginatingAddress.contains("bKash")) {
                    smsDisplayMessage = message.getDisplayMessageBody();
                    if (mListener != null)
                        mListener.messageReceived(smsDisplayMessage);
                }
            }
        } else {
            // Processing SMS messages the OLD way, before KitKat,
            Object[] data = (Object[]) bundle.get("pdus");
            for (Object pdu : data) {
                Log.d(tag, "legacy SMS implementation (before KitKat)");
                SmsMessage message = SmsMessage.createFromPdu((byte[]) pdu);
                if (message == null) {
                    Log.e(tag, "SMS message is null -- ABORT");
                    break;
                }
                smsOriginatingAddress = message.getDisplayOriginatingAddress();
                // see getMessageBody();
                if(smsOriginatingAddress != null && smsOriginatingAddress.contains("bKash")) {
                    smsDisplayMessage = message.getDisplayMessageBody();
                    if (mListener != null)
                        mListener.messageReceived(smsDisplayMessage);
                }
            }
        }
    }

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }
}
