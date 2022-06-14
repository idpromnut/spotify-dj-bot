package org.unrecoverable.spotifydjbot.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Storable {

    void serialize(OutputStream os) throws IOException;
    
    void deserialze(InputStream is) throws IOException;
}
