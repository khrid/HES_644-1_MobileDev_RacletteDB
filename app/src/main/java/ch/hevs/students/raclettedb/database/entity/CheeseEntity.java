package ch.hevs.students.raclettedb.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cheese")
public class CheeseEntity {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private Long id;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "ean")
    private int ean;

    public CheeseEntity() {
    }

    public CheeseEntity(@NonNull String name) {
        this.name=name;
    }

    public Long getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getType(){
        return type;
    }

    public int getEan(){
        return ean;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name){
        this.name=name;
    }

    public void setType(String type){
        this.type=type;
    }

    public void setId(@NonNull Long id) {
        this.id = id;
    }

    public void setEan(int ean) {
        this.ean = ean;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return name;
    }
}
