package net.zigzak.etc;

import android.app.Application;

import io.realm.Realm;

public class EtcApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
    }
}
