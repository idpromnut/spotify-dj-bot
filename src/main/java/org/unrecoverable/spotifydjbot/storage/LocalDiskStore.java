package org.unrecoverable.spotifydjbot.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unrecoverable.spotifydjbot.utils.JsonUtils;

import lombok.Getter;
import lombok.Setter;

@Component
public class LocalDiskStore implements Store<Object> {

    private static final String STORE_EXTENSION = ".json";
    private static final String STORE_DIR_NAME = "store";

    @Autowired
    @Setter
    private File homeDirectory;

    private File storeDir;

    @Getter
    private boolean storeAvailable = false;

    @PostConstruct
    public void initialize() {
        
        if (homeDirectory.exists()) {
            storeDir = new File(homeDirectory, STORE_DIR_NAME);
            if (storeDir.exists() && !storeDir.isDirectory()) {
                throw new IllegalStateException("Can't use store directory: there is a file in the way: " + storeDir.getAbsolutePath());
            }
            else if (!storeDir.exists()) {
                if (!storeDir.mkdirs()) {
                    throw new IllegalStateException("Unable to create store directory: " + storeDir.getAbsolutePath());
                }
            }
            storeAvailable = true;
        }
    }

    private File dottedPathToFile(String dottedPath, File parent) {
        File location = parent;
        for(String path: StringUtils.split(dottedPath, ".")) {
            location = new File(location, path);
        }
        return location;
    }

    /**
     * 
     * @param data
     * @param location A dotted path with the last component being the name where store the object under (file name)
     * @throws IOException
     */
    @Override
    public void store(Object data, String location) throws IOException {
        File locationFile = dottedPathToFile(location, storeDir);
        locationFile = new File(locationFile.getParent(), locationFile.getName() + STORE_EXTENSION);
        locationFile.getParentFile().mkdirs();
        if (locationFile.getParentFile().exists() && locationFile.getParentFile().isDirectory()) {
            try (OutputStream os = new FileOutputStream(locationFile)) {

                if (data instanceof Storable) {
                    Storable dataStorable = (Storable)data;
                    dataStorable.serialize(os);
                }
                else {
                    JsonUtils.convertObjectToStream(this, os, false, true);
                }
            }
        }
        else {
            throw new IOException("Can't create/access location in store: " + locationFile.getAbsolutePath());
        }
    }

    @Override
    public void load(Object data, String location) throws IOException {
        File locationFile = dottedPathToFile(location, storeDir);
        locationFile = new File(locationFile.getParent(), locationFile.getName() + STORE_EXTENSION);
        locationFile.getParentFile().mkdirs();
        if (locationFile.exists() && !locationFile.isDirectory()) {
            try (InputStream is = new FileInputStream(locationFile)) {
                if (data instanceof Storable) {
                    Storable dataStorable = (Storable)data;
                    dataStorable.deserialze(is);
                }
                else {
                    JsonUtils.convertStreamToObject(is, this, false);
                }
            }
        }
        else {
            throw new IOException("Can't access location in store: " + locationFile.getAbsolutePath());
        }
    }
}
