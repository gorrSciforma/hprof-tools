package com.badoo.hprof.viewer.factory.classdefs;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.viewer.MemoryDump;

import javax.annotation.Nonnull;
import static com.badoo.hprof.viewer.factory.classdefs.ClassUtils.findClassByName;
import static com.badoo.hprof.viewer.factory.classdefs.ClassUtils.findFieldByName;

/**
 * Class definition for accessing data of an instance dump of an Boolean
 *
 * Created by Erik Andre on 05/12/15.
 */
public class IntegerClassDef extends BaseClassDef {

    public final ClassDefinition cls;
    public final InstanceField value;

    public IntegerClassDef(@Nonnull MemoryDump data) {
        cls = findClassByName("java.lang.Integer", data);
        value = findFieldByName("value", BasicType.INT, cls, data);
    }
}
