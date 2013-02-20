package org.glassfish.json;

import javax.json.*;
import javax.json.stream.JsonGenerator;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;

class JsonWriterImpl implements JsonWriter {
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private final JsonGenerator generator;
    private boolean writeDone;

    JsonWriterImpl(Writer writer) {
        this(writer, Collections.<String, Object>emptyMap());
    }

    JsonWriterImpl(Writer writer, Map<String, ?> config) {
        boolean prettyPrinting = JsonProviderImpl.isPrettyPrintingEnabled(config);
        generator = prettyPrinting
                ? new JsonPrettyGeneratorImpl(writer)
                : new JsonGeneratorImpl(writer);
    }

    JsonWriterImpl(OutputStream out) {
        this(out, UTF_8, Collections.<String, Object>emptyMap());
    }

    JsonWriterImpl(OutputStream out, Map<String, ?> config) {
        this(out, UTF_8, config);
    }

    JsonWriterImpl(OutputStream out, Charset charset, Map<String, ?> config) {
        boolean prettyPrinting = JsonProviderImpl.isPrettyPrintingEnabled(config);
        generator = prettyPrinting
                ? new JsonPrettyGeneratorImpl(out, charset)
                : new JsonGeneratorImpl(out, charset);
    }

    @Override
    public void writeArray(JsonArray array) {
        if (writeDone) {
            throw new IllegalStateException("write/writeObject/writeArray/close method is already called.");
        }
        writeDone = true;
        generator.writeStartArray();
        for(JsonValue value : array) {
            generator.write(value);
        }
        generator.writeEnd();
    }

    @Override
    public void writeObject(JsonObject object) {
        if (writeDone) {
            throw new IllegalStateException("write/writeObject/writeArray/close method is already called.");
        }
        writeDone = true;
        generator.writeStartObject();
        for(Map.Entry<String, JsonValue> e : object.entrySet()) {
            generator.write(e.getKey(), e.getValue());
        }
        generator.writeEnd();
    }

    @Override
    public void write(JsonStructure value) {
        if (value instanceof JsonArray) {
            writeArray((JsonArray)value);
        } else {
            writeObject((JsonObject)value);
        }
    }

    @Override
    public void close() {
        writeDone = true;
        generator.close();
    }

}