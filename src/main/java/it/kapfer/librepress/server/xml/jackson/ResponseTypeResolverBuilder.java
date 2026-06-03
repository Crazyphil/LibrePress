package it.kapfer.librepress.server.xml.jackson;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.jsontype.NamedType;
import tools.jackson.databind.jsontype.TypeIdResolver;
import tools.jackson.databind.jsontype.impl.AsDeductionTypeDeserializer;
import tools.jackson.databind.jsontype.impl.ClassNameIdResolver;
import tools.jackson.databind.jsontype.impl.StdTypeResolverBuilder;

import java.util.Collection;

/**
 * Custom type resolver builder that uses an enhanced {@link AsDeductionTypeDeserializer} to find the correct subtype for responses with similar fields.
 */
public class ResponseTypeResolverBuilder extends StdTypeResolverBuilder {

    @Override
    public tools.jackson.databind.jsontype.TypeDeserializer buildTypeDeserializer(DeserializationContext ctxt, JavaType baseType, Collection<NamedType> subtypes) {
        if (_idType == JsonTypeInfo.Id.CUSTOM) {
            TypeIdResolver idRes = ClassNameIdResolver.construct(baseType, subtypes, subTypeValidator(ctxt));
            JavaType defaultImpl = defineDefaultImpl(ctxt, baseType);
            return new ResponseTypeDeserializer(ctxt, baseType, idRes, defaultImpl, subtypes);
        }
        return super.buildTypeDeserializer(ctxt, baseType, subtypes);
    }
}
