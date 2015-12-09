package com.badoo.hprof.viewer.factory.classdefs;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.viewer.MemoryDump;

import javax.annotation.Nonnull;
import static com.badoo.hprof.viewer.factory.classdefs.ClassUtils.findClassByName;
import static com.badoo.hprof.viewer.factory.classdefs.ClassUtils.findFieldByName;

/**
 * Class definition for accessing data of an instance dump of an Intent
 *
 * Created by Erik Andre on 05/12/15.
 */
public class IntentClassDef extends BaseClassDef {

    public final ClassDefinition cls;
    public final InstanceField extras;
    public final InstanceField action;

    public IntentClassDef(@Nonnull MemoryDump data) {
        cls = findClassByName("android.content.Intent", data);
        extras = findFieldByName("mExtras", BasicType.OBJECT, cls, data);
        action = findFieldByName("mAction", BasicType.OBJECT, cls, data);
    }

}
