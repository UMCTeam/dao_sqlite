package com.chendaole.dao_sqlite;

import android.content.Context;
import android.database.Cursor;

import java.io.File;
import java.sql.SQLException;

public class SQLTrait {
    private SQLTraitImpl mSQLTraitImpl;
    private boolean isInited;

    public SQLTrait() {}

    public boolean initialize(Context context) {
        if (isInited) return true;
        synchronized (SQLTrait.class) {
            try {
                String dbPath = context.getExternalCacheDir()+ File.separator + context.getPackageName().replace(".", "_");
                if(!dbPath.endsWith(".db")) {
                    dbPath += ".db";
                }
                mSQLTraitImpl = new SQLTraitImpl(dbPath);
            } catch (SQLException e) {
                return false;
            }
            isInited = true;
            return true;
        }
    }

    public void execSQL(String sql) {
        mSQLTraitImpl.execSQL(sql);
    }

    public Cursor rawQuery(String sql) {
        return mSQLTraitImpl.rawQuery(sql, null, null);
    }

    public static SQLTrait getInstance() {
        return Placeholder.sInstance;
    }

    private static class Placeholder {
        private static SQLTrait sInstance = new SQLTrait();
    }
}
