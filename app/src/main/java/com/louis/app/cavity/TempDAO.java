package com.louis.app.cavity;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface TempDAO {

    @Insert
    void insert(Temp temp);

    @Update
    void update(Temp temp);

    @Delete
    void delete(Temp temp);

    @Query("DELETE FROM temp_table")
    void dropTableTemp();

    @Query("INSERT INTO bouteille_table SELECT * FROM temp_table")
    void copyTable();
}
