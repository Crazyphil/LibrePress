package it.kapfer.librepress.server.xml.jackson;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.jsontype.NamedType;
import tools.jackson.databind.jsontype.TypeIdResolver;
import tools.jackson.databind.jsontype.impl.AsDeductionTypeDeserializer;
import tools.jackson.databind.util.TokenBuffer;

import java.lang.reflect.Field;
import java.util.BitSet;
import java.util.Collection;
import java.util.Map;

public class ResponseTypeDeserializer extends AsDeductionTypeDeserializer {
    public ResponseTypeDeserializer(DeserializationContext ctxt, JavaType bt, TypeIdResolver idRes, JavaType defaultImpl, Collection<NamedType> subtypes) {
        super(ctxt, bt, idRes, defaultImpl, subtypes);
    }

    public ResponseTypeDeserializer(ResponseTypeDeserializer src, BeanProperty property) {
        super(src, property);
    }

    public tools.jackson.databind.jsontype.TypeDeserializer forProperty(BeanProperty prop) {
        return (prop == _property) ? this : new ResponseTypeDeserializer(this, prop);
    }

    /**
     * The parent deserializer failed because it only looks at candidates that contain <em>at least</em> the fields contained in the XML.
     * We give it a try again here by deserializing to a candidate that contains <em>exactly</em> the fields in the XML,
     * even if there are other candidates with more fields.
     */
    @Override
    protected Object _deserializeTypedUsingDefaultImpl(JsonParser p, DeserializationContext ctxt, TokenBuffer tb, String priorFailureMsg) throws JacksonException {
        JsonParser parser = tb.asParser();
        JsonToken token = parser.nextToken();
        if (token == JsonToken.START_OBJECT) {
            token = parser.nextToken();
        }

        boolean ignoreCase = ctxt.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
        Map<String, Integer> propertyBitIndex = getPropertyBitIndex();
        Map<BitSet, String> subtypeFingerprints = getSubtypeFingerprints();

        BitSet propertyBitSet = new BitSet();
        for (; token == JsonToken.PROPERTY_NAME; token = parser.nextToken()) {
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
            JsonParser p2 = tb.asParser(ctxt, p);
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
            return (Map<BitSet, String>) subtypeFingerprintsField.get(this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return Map.of();
        }
    }

    private Map<String, Integer> getPropertyBitIndex() {
        try {
            Class<AsDeductionTypeDeserializer> deserializerClass = AsDeductionTypeDeserializer.class;
            Field propertyBitIndexField = deserializerClass.getDeclaredField("propertyBitIndex");
            propertyBitIndexField.setAccessible(true);
            return (Map<String, Integer>) propertyBitIndexField.get(this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return Map.of();
        }
    }
}
