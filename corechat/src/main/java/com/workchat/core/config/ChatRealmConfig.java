package com.workchat.core.config;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ChatRealmConfig {
    private final RealmConfiguration realmConfig;
    private Realm realm;

    public ChatRealmConfig() {
        realmConfig = new RealmConfiguration.Builder()     // The app is responsible for calling `Realm.init(Context)`
                .name("chat.core.realm")
                .schemaVersion(5)
                .deleteRealmIfMigrationNeeded()// So always use a unique name
                .modules(new ChatLibraryModule())// Always use explicit modules in library projects
                .build();

        // Reset Realm
        try {
            Realm realm = Realm.getDefaultInstance();
            if(!realm.isClosed()){
                realm.close();
            }
            Realm.deleteRealm(realmConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Realm open() {
        // Don't use Realm.setDefaultInstance() in library projects. It is unsafe as app developers can override the
        // default configuration. So always use explicit configurations in library projects.
        return realm = Realm.getInstance(realmConfig);
    }

    public void close() {
        if (realm != null && !realm.isClosed())
            realm.close();
    }
}
