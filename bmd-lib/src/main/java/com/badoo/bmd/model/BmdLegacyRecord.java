package com.badoo.bmd.model;

/**
 * Class containing a legacy (HPROF) BMD record.
 * <p/>
 * Created by Erik Andre on 09/11/14.
 */
public class BmdLegacyRecord {

    private final int originalTag;
    private final byte[] data;

    public BmdLegacyRecord(int originalTag, byte[] data) {
        this.originalTag = originalTag;
        this.data = data;
    }

    /**
     * Returns the original tag that this record had.
     *
     * @return The original tag
     */
    public int getOriginalTag() {
        return originalTag;
    }

    /**
     * Returns the data if the record.
     *
     * @return The record data
     */
    public byte[] getData() {
        return data;
    }
}
