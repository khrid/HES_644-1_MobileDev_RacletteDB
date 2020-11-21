package ch.hevs.students.raclettedb.database.entity;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CheeseEntity {
    private String id;

    private Long shieling;

    private @NonNull
    String name;

    private String type;

    private String description;

    private String imagePath;

    public CheeseEntity() {

    }

    public CheeseEntity(@NonNull String name, String description, String type, String imagePath) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.imagePath = imagePath;
    }

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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Long getShieling() {
        return shieling;
    }

    public void setShieling(Long shieling) {
        this.shieling = shieling;
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
        return Objects.hash(id, shieling, name, type, description, imagePath);
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", name);
        result.put("description", description);
        result.put("type", type);
        result.put("imagepath", imagePath);
        result.put("shieling", shieling);

        return result;
    }
}
