package org.unrecoverable.spotifydjbot.storage;

import java.io.IOException;


public interface Store<T> {

    void store(T data, String location) throws IOException;
    
    void load(T data, String location) throws IOException;
}
