package com.pushtorefresh.storio.sample.db.resolvers;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sample.db.entities.Car;
import com.pushtorefresh.storio.sample.db.tables.CarsTable;
import com.pushtorefresh.storio.sqlite.operations.get.DefaultGetResolver;

public final class CarGetResolver extends DefaultGetResolver<Car> {
    @NonNull
    @Override
    public Car mapFromCursor(@NonNull Cursor cursor) {
        return new Car.Builder(cursor.getString(cursor.getColumnIndexOrThrow(CarsTable.COLUMN_MODEL)))
                .id(cursor.getLong(cursor.getColumnIndexOrThrow(CarsTable.COLUMN_ID)))
                .uuid(cursor.getString(cursor.getColumnIndexOrThrow(CarsTable.COLUMN_UUID)))
//                .personId(cursor.getLong(cursor.getColumnIndexOrThrow(CarsTable.COLUMN_PERSON_ID)))
                .personUuid(cursor.getString(cursor.getColumnIndexOrThrow(CarsTable.COLUMN_PERSON_UUID)))
                .build();
    }
}