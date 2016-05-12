package com.hillrom.vest.domain.util;

import java.io.IOException;
import java.text.DecimalFormat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;


public class DecimalNumberSerializer extends JsonSerializer<Number> {

	private static DecimalFormat formatter = new DecimalFormat("#.#");
	@Override
	public void serialize(Number value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {
		jgen.writeNumber(Double.parseDouble(formatter.format(value)));
	}

}
