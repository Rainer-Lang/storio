package com.pushtorefresh.storio.sample.db.tables;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.queries.InsertQuery;

public final class PersonsTable {

    private PersonsTable() {
        throw new IllegalStateException("No instances please!");
    }

    @NonNull
    public static final String TABLE_NAME = "persons";

    @NonNull
    public static final String COLUMN_ID = "_id";

    @NonNull
    public static final String COLUMN_UUID = "uuid";

    @NonNull
    public static final String COLUMN_NAME = "name";

    @NonNull
    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, "
                + COLUMN_UUID + " TEXT NOT NULL UNIQUE, "
                + COLUMN_NAME + " TEXT NOT NULL"
                + ");";
    }

    @NonNull
    public static String getCreateIndexUuid() {
        return "CREATE INDEX idx_uuid ON " + TABLE_NAME + " (" + COLUMN_UUID + ")";
    }

    public static final InsertQuery INSERT_QUERY_PERSON = InsertQuery.builder().table(PersonsTable.TABLE_NAME).build();
}