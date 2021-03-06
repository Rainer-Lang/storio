package com.pushtorefresh.storio.sample.db.resolvers;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sample.db.entities.Car;
import com.pushtorefresh.storio.sample.db.entities.Person;
import com.pushtorefresh.storio.sample.db.tables.CarsTable;
import com.pushtorefresh.storio.sample.db.tables.PersonsTable;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.put.PutResolver;
import com.pushtorefresh.storio.sqlite.operations.put.PutResult;
import com.pushtorefresh.storio.sqlite.queries.UpdateQuery;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class PersonPutResolver extends PutResolver<Person> {
    @NonNull
    @Override
    public PutResult performPut(@NonNull StorIOSQLite storIOSQLite, @NonNull Person person) {
        // For consistency and performance (we are going to affect two tables) we will open transaction
        storIOSQLite.internal().beginTransaction();

        try {
            final ContentValues contentValues = new ContentValues(3);

            contentValues.put(PersonsTable.COLUMN_ID,   person.id());
            contentValues.put(PersonsTable.COLUMN_UUID, person.uuid());
            contentValues.put(PersonsTable.COLUMN_NAME, person.name());

            final Set<String> affectedTables = new HashSet<>(2);
            affectedTables.add(PersonsTable.TABLE_NAME);

//            // need _id of inserted object -> can't get now
//            // _id
//            storIOSQLite
//                    .put()
//                    .object(person)
//                    .prepare()
//                    .executeAsBlocking();
//
//            List<Car> cars = person.cars();
//            int count = cars.size();
//            for (int i=0; i<count; i++) {
//                storIOSQLite
//                        .put()
//                        .object(new Car.Builder(cars.get(i).model()).personUuid(person.uuid()).build())
//                        .prepare()
//                        .executeAsBlocking();
//            }
//            if (0<count) {
//                affectedTables.add(CarsTable.TABLE_NAME);
//            }

            // If person already has an Id — it was inserted into the db
            // Otherwise, we will insert person and then get his/her id!
            final long personId;

            if (person.id() != null) {
                personId = person.id();

                storIOSQLite
                        .internal()
                        .update(UpdateQuery.builder()
                                        .table(PersonsTable.TABLE_NAME)
                                        .where(PersonsTable.COLUMN_ID + "=?")
                                        .whereArgs(person.id())
                                        .build(),
                                contentValues);

                if (!person.cars().isEmpty()) {
                    storIOSQLite
                            .put()
                            .objects(person.cars())
                            .prepare()
                            .executeAsBlocking();

                    affectedTables.add(CarsTable.TABLE_NAME);
                }
            }
            else {
                personId = storIOSQLite
                        .internal()
                        .insert(PersonsTable.INSERT_QUERY_PERSON, contentValues);

                List<Car> cars = person.cars();
                int count = cars.size();
                for (int i=0; i<count; i++) {
                    storIOSQLite
                            .put()
                            .object(new Car.Builder(cars.get(i).model()).personUuid(person.uuid()).build())
                            .prepare()
                            .executeAsBlocking();
                }
                if (0<count) {
                    affectedTables.add(CarsTable.TABLE_NAME);
                }
            }

            storIOSQLite.internal().setTransactionSuccessful();

            return person.id() != null
                    ? PutResult.newUpdateResult(1, affectedTables)
                    : PutResult.newInsertResult(personId, affectedTables);
        } finally {
            storIOSQLite.internal().endTransaction();
        }
    }
}