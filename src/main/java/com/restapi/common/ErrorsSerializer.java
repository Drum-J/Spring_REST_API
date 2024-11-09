package com.restapi.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.validation.Errors;

import java.io.IOException;

@JsonComponent
public class ErrorsSerializer extends JsonSerializer<Errors> {
    @Override
    public void serialize(Errors errors, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartArray();
        // errors.rejectValue() -> FieldError
        errors.getFieldErrors().forEach(e -> {
            try {
                jsonGenerator.writeStartObject();

                jsonGenerator.writeStringField("field",e.getField());
                jsonGenerator.writeStringField("objectName",e.getObjectName());
                jsonGenerator.writeStringField("code",e.getCode());
                jsonGenerator.writeStringField("defaultMessage",e.getDefaultMessage());
                if (e.getRejectedValue() != null) {
                    jsonGenerator.writeStringField("rejectedValue",e.getRejectedValue().toString());
                }

                jsonGenerator.writeEndObject();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        // errors.reject() -> GlobalError
        errors.getGlobalErrors().forEach(e -> {
            try {
                jsonGenerator.writeStartObject();

                jsonGenerator.writeStringField("objectName",e.getObjectName());
                jsonGenerator.writeStringField("code",e.getCode());
                jsonGenerator.writeStringField("defaultMessage",e.getDefaultMessage());

                jsonGenerator.writeEndObject();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        jsonGenerator.writeEndArray();
    }
}
