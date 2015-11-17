package com.hillrom.vest.domain.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

public class MMDDYYYYLocalDateSerializer extends JsonSerializer<LocalDate> {

    private static DateTimeFormatter formatter = DateTimeFormat
            .forPattern("MM/dd/yyyy");

    @Override
    public void serialize(LocalDate value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeString(formatter.print(value));
    }
}
