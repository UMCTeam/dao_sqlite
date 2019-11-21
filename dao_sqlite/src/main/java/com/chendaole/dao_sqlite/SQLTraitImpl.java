package com.chendaole.dao_sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.CancellationSignal;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SQLTraitImpl {
    private final File mDbFile;
    private final ReadWriteLock mReadWriteLock = new ReentrantReadWriteLock();
    private final Lock mReadLock = mReadWriteLock.readLock();
    private final Lock mWriteLock = mReadWriteLock.writeLock();

    public SQLTraitImpl(String persistPath) throws SQLException {
        mDbFile = getDatabaseFile(persistPath);
        if (mDbFile == null)
            throw new SQLException("The database create failed!");
    }

    public void execSQL(String sql) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }


    public Cursor rawQuery(String sql, String[] selectionArgs, CancellationSignal cancellationSignal) {
        return getReadableDatabase().rawQuery(sql, selectionArgs, cancellationSignal);
    }

    private SQLiteDatabase getReadableDatabase() {
        mReadLock.lock();
        try {
            return SQLiteDatabase.openOrCreateDatabase(mDbFile, null);
        } catch (android.database.SQLException se) {
            se.printStackTrace();
        } finally {
            mReadLock.unlock();
        }
        return null;
    }

    private SQLiteDatabase getWritableDatabase() {
        mWriteLock.lock();
        try {
            return SQLiteDatabase.openOrCreateDatabase(mDbFile, null);
        } catch (android.database.SQLException se) {
            se.printStackTrace();
        } finally {
            mWriteLock.unlock();
        }
        return null;
    }

    private File getDatabaseFile(String persistPath) {
        File dbFile = new File(persistPath);
        if(!dbFile.exists()) {
            try {
                dbFile.createNewFile();
            } catch (IOException e) {
                dbFile = null;
            }
        }
        return dbFile;
    }
}
