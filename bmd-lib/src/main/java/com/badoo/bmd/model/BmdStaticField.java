package com.badoo.bmd.model;

/**
 * Data class for static fields in BMD class definitions.
 * <p/>
 * Created by Erik Andre on 02/11/14.
 */
@SuppressWarnings("UnusedDeclaration")
public class BmdStaticField {

    private final int nameId;
    private final BmdBasicType type;
    private final Object value;

    public BmdStaticField(int nameId, BmdBasicType type, Object value) {
        this.nameId = nameId;
        this.type = type;
        this.value = value;
    }

    /**
     * Returns the string id of the fields name.
     *
     * @return The name string id.
     */
    public int getNameId() {
        return nameId;
    }

    /**
     * Returns the type of the field.
     *
     * @return The field type
     */
    public BmdBasicType getType() {
        return type;
    }

    /**
     * Returns the field value (how to interpret it depends on the type of the field).
     *
     * @return The field value
     */
    public Object getValue() {
        return value;
    }
}
