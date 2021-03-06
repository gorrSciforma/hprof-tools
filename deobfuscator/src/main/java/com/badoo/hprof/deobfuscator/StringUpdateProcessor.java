package com.badoo.hprof.deobfuscator;

import com.badoo.hprof.library.HprofReader;
import com.badoo.hprof.library.HprofWriter;
import com.badoo.hprof.library.Tag;
import com.badoo.hprof.library.heap.HeapDumpReader;
import com.badoo.hprof.library.heap.HeapDumpWriter;
import com.badoo.hprof.library.heap.HeapTag;
import com.badoo.hprof.library.heap.processor.HeapDumpBaseProcessor;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.HprofString;
import com.badoo.hprof.library.model.ID;
import com.badoo.hprof.library.processor.CopyProcessor;
import com.badoo.hprof.library.util.StreamUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

import javax.annotation.Nonnull;

import static com.badoo.hprof.library.util.StreamUtil.*;

/**
 * HprofProcessor class for replacing existing strings.
 *
 * Created by Erik Andre on 14/08/2014.
 */
public class StringUpdateProcessor extends CopyProcessor {

    private class ClassDefinitionRemoverProcessor extends HeapDumpBaseProcessor {

        private final OutputStream out;

        public ClassDefinitionRemoverProcessor(OutputStream out) {
            this.out = out;
        }

        @Override
        public void onHeapRecord(int tag, @Nonnull HeapDumpReader reader) throws IOException {
            if (tag == HeapTag.CLASS_DUMP) {
                skipHeapRecord(tag, reader.getInputStream()); // Discard all class definitions since we are writing an updated version instead
            }
            else {
                copyHeapRecord(tag, reader.getInputStream(), out);
            }
        }
    }

    private final Collection<HprofString> strings;
    private final Map<ID, ClassDefinition> classes;
    private boolean writeUpdatedClassDefinitions = true;
    public StringUpdateProcessor(OutputStream out, Map<ID, ClassDefinition> classes, Collection<HprofString> strings) {
        super(out);
        this.strings = strings;
        this.classes = classes;
    }

    @Override
    public void onHeader(@Nonnull String text, int idSize, int timeHigh, int timeLow) throws IOException {
        super.onHeader(text, idSize, timeHigh, timeLow);
        // Write all updated strings
        HprofWriter writer = new HprofWriter(out);
        for (HprofString string : strings) {
            writer.writeStringRecord(string);
        }
    }

    @Override
    public void onRecord(int tag, int timestamp, int length, @Nonnull HprofReader reader) throws IOException {


        if (tag == Tag.STRING) {
            skip(reader.getInputStream(), length); // Discard all the original strings
        }
        else if (tag == Tag.HEAP_DUMP || tag == Tag.HEAP_DUMP_SEGMENT) {


            ByteArrayOutputStream buffer ;

            if (writeUpdatedClassDefinitions) {
                // Write the updated class definitions before continuing with the existing data
                buffer = writeClassesToBAOS(tag, timestamp);
                writeUpdatedClassDefinitions = false;
            }
            else
            {
                buffer = new ByteArrayOutputStream();
            }
            // Filter the heap dump, removing any class definition

            HeapDumpReader heapReader = new HeapDumpReader(reader.getInputStream(), length, new ClassDefinitionRemoverProcessor(buffer));
            while (heapReader.hasNext()) {
                heapReader.next();
            }
            byte[] data = buffer.toByteArray();
            writer.writeRecordHeader(tag, timestamp, data.length);
            out.write(data);
        }
        else {
            super.onRecord(tag, timestamp, length, reader);
        }
    }

    private void writeClasses(int tag, int timestamp) throws IOException {
        // Write all class  definitions to a buffer in order to calculate the size. Uses more memory but avoids an extra pass to calculate size before writing the data
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        HeapDumpWriter bufferWrite = new HeapDumpWriter(buffer);
        for (ClassDefinition cls : classes.values()) {
            bufferWrite.writeClassDumpRecord(cls);
        }
        byte[] data = buffer.toByteArray();
        writer.writeRecordHeader(tag, timestamp, data.length);
        out.write(data);
    }


    private ByteArrayOutputStream writeClassesToBAOS(int tag, int timestamp) throws IOException {
        // Write all class  definitions to a buffer in order to calculate the size. Uses more memory but avoids an extra pass to calculate size before writing the data
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        HeapDumpWriter bufferWrite = new HeapDumpWriter(buffer);
        for (ClassDefinition cls : classes.values()) {
            bufferWrite.writeClassDumpRecord(cls);
        }
        return buffer;
//        byte[] data = buffer.toByteArray();
//        writer.writeRecordHeader(tag, timestamp, data.length);
//        out.write(data);
    }

}
