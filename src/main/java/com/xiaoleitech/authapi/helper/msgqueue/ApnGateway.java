package com.xiaoleitech.authapi.helper.msgqueue;

import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import javapns.devices.Device;
import javapns.devices.implementations.basic.BasicDevice;
import javapns.notification.AppleNotificationServerBasicImpl;
import javapns.notification.PushNotificationManager;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;
import org.json.JSONException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ApnGateway {
    public void sendMessage(String token, String alert, String message) {
        int badge = 3;
        String sound = "default";
        String certificatePath = "D:/apns-dev.pem";
        String certificatePassword = "1111";
        try {
            PushNotificationPayload payload = new PushNotificationPayload();
            payload.addAlert(alert);
            payload.addBadge(badge);
            payload.addCustomAlertBody(message);
            if ( !sound.isEmpty() )
                payload.addSound(sound);
            PushNotificationManager pushNotificationManager = new PushNotificationManager();

            pushNotificationManager.initializeConnection(new AppleNotificationServerBasicImpl(certificatePath, certificatePassword, false));

            List<PushedNotification> notifications = new ArrayList<PushedNotification>();

            Device basicDevice = new BasicDevice();
            basicDevice.setToken(token);
            PushedNotification notification = pushNotificationManager.sendNotification(basicDevice, payload, true);
            notifications.add(notification);

            pushNotificationManager.stopConnection();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (CommunicationException e) {
            e.printStackTrace();
        } catch (KeystoreException e) {
            e.printStackTrace();
        }

    }
}
