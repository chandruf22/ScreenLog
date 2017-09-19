package com.f22labs.screenlog.storage;

/**
 * As it sounds, anything that can stored and represented as byte array.
 */
public interface Storable {
    byte[] getBytes();
}
