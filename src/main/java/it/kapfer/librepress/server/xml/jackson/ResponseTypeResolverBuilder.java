package it.kapfer.librepress.server.xml.jackson;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.impl.AsDeductionTypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.impl.ClassNameIdResolver;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;

import java.util.Collection;

/**
 * Custom type resolver builder that uses an enhanced {@link AsDeductionTypeDeserializer} to find the correct subtype for responses with similar fields.
 */
public class ResponseTypeResolverBuilder extends StdTypeResolverBuilder {

    @Override
    public TypeDeserializer buildTypeDeserializer(DeserializationConfig config, JavaType baseType, Collection<NamedType> subtypes) {
        if (_idType == JsonTypeInfo.Id.CUSTOM) {
            TypeIdResolver idRes = ClassNameIdResolver.construct(baseType, config, subtypes, subTypeValidator(config));
            JavaType defaultImpl = defineDefaultImpl(config, baseType);
            return new ResponseTypeDeserializer(baseType, idRes, defaultImpl, config, subtypes);
        }
        return super.buildTypeDeserializer(config, baseType, subtypes);
    }
}
