package com.gadware.tution.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;


@Entity(tableName = "ImageDetails",primaryKeys = {"id","types"})
public class ImageDetails {
    private String id;
    private String type;
    private byte[] bytes;

    public ImageDetails(String id, String type, byte[] bytes) {
        this.id = id;
        this.type = type;
        this.bytes = bytes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
