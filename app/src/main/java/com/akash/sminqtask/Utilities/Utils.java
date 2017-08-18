package com.akash.sminqtask.Utilities;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by akash on 8/12/2017.
 */

public class Utils {

    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            //mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }
}
