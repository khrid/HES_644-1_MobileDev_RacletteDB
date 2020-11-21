package ch.hevs.students.raclettedb.database.entity;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ShielingEntity {
    private String id;

    private @NonNull String name;

    private String description;

    private String imagePath;

    private float latitude;

    private float longitude;

    public ShielingEntity() {

    }

    public ShielingEntity(@NonNull String name, String description, String imagePath, float latitude, float longitude) {
        this.name = name;
        this.description = description;
        this.imagePath = imagePath;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @NonNull
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof ShielingEntity)) return false;
        ShielingEntity obj = (ShielingEntity) o;
        return obj.getId().equals(this.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, imagePath, latitude, longitude);
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", name);
        result.put("description", description);
        result.put("imagepath", imagePath);
        result.put("latitude", latitude);
        result.put("longitude", longitude);

        return result;
    }
}
