package ch.hevs.students.raclettedb.database.entity;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ShielingEntity {
    private String id;

    private @NonNull String name;

    private String oldName;

    private String description;

    private String imagepath;

    private float latitude;

    private float longitude;

    public ShielingEntity() {

    }

    public ShielingEntity(@NonNull String name, String description, String imagepath, float latitude, float longitude) {
        this.name = name;
        this.description = description;
        this.imagepath = imagepath;
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

    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
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
        return Objects.hash(id, name, description, imagepath, latitude, longitude);
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", name);
        result.put("description", description);
        result.put("imagepath", imagepath);
        result.put("latitude", latitude);
        result.put("longitude", longitude);

        return result;
    }
}
