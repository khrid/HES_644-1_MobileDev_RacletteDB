package ch.hevs.students.raclettedb.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "cheeses",
        foreignKeys =
        @ForeignKey(
                entity = ShielingEntity.class,
                parentColumns = "id",
                childColumns = "shieling",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {
                @Index(
                        value = {"shieling"}
                )})

public class CheeseEntity {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private Long id;

    @NonNull
    @ColumnInfo(name = "shieling")
    private Long shieling;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "description")
    private String description;

    /*@ColumnInfo(name = "ean")
    private int ean;*/

    @ColumnInfo(name = "imagePath")
    private String imagePath;

    public CheeseEntity() {
    }

    public CheeseEntity(@NonNull String name, @NonNull Long shieling) {
        this.name=name;
        this.shieling=shieling;
    }

    public Long getId(){
        return id;
    }

    public Long getShieling() { return  shieling; }

    public String getName(){
        return name;
    }

    public String getType(){
        return type;
    }

    /*public int getEan(){
        return ean;
    }*/

    public String getDescription() {
        return description;
    }

    public String getImagePath() {
        return imagePath;
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

    public void setShieling(Long shieling) {
        this.shieling = shieling;
    }

    /*public void setEan(int ean) {
        this.ean = ean;
    }*/

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof CheeseEntity)) return false;
        CheeseEntity o = (CheeseEntity) obj;
        return o.getId().equals(this.getId());
    }

    @Override
    public String toString() {
        return name;
    }
}
