package ch.hevs.students.raclettedb.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "shielings")
public class ShielingEntity {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private Long id;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "imagePath")
    private String imagePath;

    @ColumnInfo(name = "latitude")
    private float latitude;

    @ColumnInfo(name = "longitude")
    private float longitude;

    public ShielingEntity() {
    }

    public ShielingEntity(@NonNull String name) {
        this.name=name;
    }

    public Long getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setId(@NonNull Long id) {
        this.id = id;
    }

    public void setName(String name){
        this.name=name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof ShielingEntity)) return false;
        ShielingEntity o = (ShielingEntity) obj;
        return o.getId().equals(this.getId());
    }

    @Override
    public String toString() {
        return name;
    }
}

