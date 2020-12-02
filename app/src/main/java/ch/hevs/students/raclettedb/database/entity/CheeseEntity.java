package ch.hevs.students.raclettedb.database.entity;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CheeseEntity {
    private String id;

    private String shieling;

    private String oldShieling;

    private @NonNull
    String name;

    private String type;

    private String description;

    private String imagepath;

    public CheeseEntity() {

    }

    public CheeseEntity(@NonNull String name, String description, String type, String imagePath) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.imagepath = imagePath;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    public String getShieling() {
        return shieling;
    }

    public void setShieling(String shieling) {
        this.shieling = shieling;
    }

    public String getOldShieling() {
        return oldShieling;
    }

    public void setOldShieling(String oldShieling) {
        this.oldShieling = oldShieling;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof CheeseEntity)) return false;
        CheeseEntity obj = (CheeseEntity) o;
        return obj.getId().equals(this.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, shieling, name, type, description, imagepath);
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", name);
        result.put("description", description);
        result.put("type", type);
        result.put("imagepath", imagepath);
        result.put("shieling", shieling);

        return result;
    }

    @Override
    public String toString() {
        return "CheeseEntity{" +
                "id='" + id + '\'' +
                ", shieling='" + shieling + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", imagepath='" + imagepath + '\'' +
                '}';
    }
}
