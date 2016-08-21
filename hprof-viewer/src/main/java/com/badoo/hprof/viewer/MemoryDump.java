package com.badoo.hprof.viewer;

import com.badoo.hprof.library.model.*;

import java.util.Collections;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Data class for the information read from the HPROF file
 */
public class MemoryDump {

    public final Map<ID, HprofString> strings;
    public final Map<ID, ClassDefinition> classes;
    public final Map<ID, Instance> instances;
    public final Map<ID, ObjectArray> objArrays;
    public final Map<ID, PrimitiveArray> primitiveArrays;

    public MemoryDump(Map<ID, ClassDefinition> classes, Map<ID, HprofString> strings, Map<ID, Instance> instances,
                      Map<ID, ObjectArray> objArrays, Map<ID, PrimitiveArray> primitiveArrays) {
        this.strings = Collections.unmodifiableMap(strings);
        this.classes = Collections.unmodifiableMap(classes);
        this.instances = Collections.unmodifiableMap(instances);
        this.objArrays = Collections.unmodifiableMap(objArrays);
        this.primitiveArrays = Collections.unmodifiableMap(primitiveArrays);
    }

    @Nonnull
    public ClassDefinition findClassByName(@Nonnull String name) {
        ClassDefinition cls = tryFindClassByName(name);
        if (cls == null) {
            throw new IllegalArgumentException("No class with name " + name + " found!");
        }
        return cls;
    }

    @Nullable
    public ClassDefinition tryFindClassByName(@Nonnull String name) {
        for (ClassDefinition cls : classes.values()) {
            if (name.equals(strings.get(cls.getNameStringId()).getValue())) {
                return cls;
            }
        }
        return null;
    }

    @Nonnull
    public InstanceField findFieldByName(@Nonnull String name, @Nonnull BasicType type, @Nonnull ClassDefinition cls) {
        InstanceField foundField = tryFindFieldByName(name, type, cls);
        if (foundField == null) {
            // Error reporting
            StringBuilder error = new StringBuilder();
            for (InstanceField field : cls.getInstanceFields()) {
                error.append(strings.get(field.getFieldNameId())).append(" ").append(field.getType()).append("\n");
            }
            throw new IllegalArgumentException("Field " + name + " not found in " + strings.get(cls.getNameStringId()) + "\n" + error.toString());
        }
        return foundField;
    }

    @Nullable
    public InstanceField tryFindFieldByName(@Nonnull String name, @Nonnull BasicType type, @Nonnull ClassDefinition cls) {
        for (InstanceField field : cls.getInstanceFields()) {
            if (field.getType() == type && name.equals(strings.get(field.getFieldNameId()).getValue())) {
                return field;
            }
        }
        return null;
    }

    @Nonnull
    public StaticField findStaticFieldByName(@Nonnull String name, @Nonnull BasicType type, @Nonnull ClassDefinition cls) {
        for (StaticField field : cls.getStaticFields()) {
            String fieldName = strings.get(field.getFieldNameId()).getValue();
            if (field.getType() == type && name.equals(fieldName)) {
                return field;
            }
        }
        // Error reporting
        StringBuilder error = new StringBuilder();
        for (StaticField field : cls.getStaticFields()) {
            error.append(strings.get(field.getFieldNameId())).append(" ").append(field.getType()).append("\n");
        }
        throw new IllegalArgumentException("field " + name + " not found in " + getClassName(cls));
    }

    public String getClassName(@Nonnull Instance instance) {
        ClassDefinition cls = classes.get(instance.getClassId());
        return strings.get(cls.getNameStringId()).getValue();
    }

    public String getClassName(@Nonnull ClassDefinition cls) {
        return strings.get(cls.getNameStringId()).getValue();
    }

    public boolean isInstanceOf(Instance instance, ClassDefinition of) {
        if (instance == null || of == null) {
            return false;
        }
        ClassDefinition cls = classes.get(instance.getClassId());
        while (cls != null) {
            if (cls == of) {
                return true;
            }
            cls = classes.get(cls.getSuperClassObjectId());
        }
        return false;
    }

}
