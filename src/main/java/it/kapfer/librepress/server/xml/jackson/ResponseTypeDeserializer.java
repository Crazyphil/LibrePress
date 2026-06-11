package it.kapfer.librepress.server.xml.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.impl.AsDeductionTypeDeserializer;
import com.fasterxml.jackson.databind.util.TokenBuffer;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.BitSet;
import java.util.Collection;
import java.util.Map;

public class ResponseTypeDeserializer extends AsDeductionTypeDeserializer {
    public ResponseTypeDeserializer(JavaType bt, TypeIdResolver idRes, JavaType defaultImpl, DeserializationConfig config, Collection<NamedType> subtypes) {
        super(bt, idRes, defaultImpl, config, subtypes);
    }

    public ResponseTypeDeserializer(ResponseTypeDeserializer src, BeanProperty property) {
        super(src, property);
    }

    @Override
    public com.fasterxml.jackson.databind.jsontype.TypeDeserializer forProperty(BeanProperty prop) {
        return (prop == _property) ? this : new ResponseTypeDeserializer(this, prop);
    }

    /**
     * The parent deserializer failed because it only looks at candidates that contain <em>at least</em> the fields contained in the XML.
     * We give it a try again here by deserializing to a candidate that contains <em>exactly</em> the fields in the XML,
     * even if there are other candidates with more fields.
     */
    @Override
    protected Object _deserializeTypedUsingDefaultImpl(JsonParser p, DeserializationContext ctxt, TokenBuffer tb, String priorFailureMsg) throws IOException {
        JsonParser parser = tb.asParser();
        JsonToken token = parser.nextToken();
        if (token == JsonToken.START_OBJECT) {
            token = parser.nextToken();
        }

        boolean ignoreCase = ctxt.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
        Map<String, Integer> propertyBitIndex = getPropertyBitIndex();
        Map<BitSet, String> subtypeFingerprints = getSubtypeFingerprints();

        BitSet propertyBitSet = new BitSet();
        for (; token == JsonToken.FIELD_NAME; token = parser.nextToken()) {
            String name = parser.currentName();
            if (ignoreCase) name = name.toLowerCase();

            // Skip the value (move past it so nextToken lands on the next PROPERTY_NAME or END_OBJECT)
            parser.nextToken();
            parser.skipChildren();

            Integer bit = propertyBitIndex.get(name);
            if (bit != null) {
                propertyBitSet.set(bit);
            }
        }

        String subtype = subtypeFingerprints.get(propertyBitSet);
        if (subtype != null) {
            // Re-deserialize using the TokenBuffer content (same pattern as parent _deserializeTypedUsingDefaultImpl)
            tb.writeEndObject();
            JsonParser p2 = tb.asParser(p);
            p2.nextToken();
            return _findDeserializer(ctxt, subtype).deserialize(p2, ctxt);
        }
        return super._deserializeTypedUsingDefaultImpl(p, ctxt, tb, priorFailureMsg);
    }

    private Map<BitSet, String> getSubtypeFingerprints() {
        try {
            Class<AsDeductionTypeDeserializer> deserializerClass = AsDeductionTypeDeserializer.class;
            Field subtypeFingerprintsField = deserializerClass.getDeclaredField("subtypeFingerprints");
            subtypeFingerprintsField.setAccessible(true);

            @SuppressWarnings("unchecked")
            Map<BitSet, String> subtypeFingerprints = (Map<BitSet, String>) subtypeFingerprintsField.get(this);
            return subtypeFingerprints;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException("Could not reflectively access subtype fingerprints", e);
        }
    }

    private Map<String, Integer> getPropertyBitIndex() {
        try {
            Class<AsDeductionTypeDeserializer> deserializerClass = AsDeductionTypeDeserializer.class;
            Field propertyBitIndexField = deserializerClass.getDeclaredField("fieldBitIndex");
            propertyBitIndexField.setAccessible(true);

            @SuppressWarnings("unchecked")
            Map<String, Integer> propertyBitIndex = (Map<String, Integer>) propertyBitIndexField.get(this);
            return propertyBitIndex;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException("Could not reflectively access property bit indexes", e);
        }
    }
}
