package com.mageddo.common.jackson.converters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.mageddo.common.monetary.Monetary;

import java.io.IOException;

public interface MonetaryConverter {

	class MonetaryJsonDeserializer extends JsonDeserializer<Monetary> {
		@Override
		public Monetary deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
			final String value = p.getValueAsString();
			if (value == null) {
				return null;
			}
			return new Monetary(value);
		}
	}

	class MonetaryJsonSerializer extends JsonSerializer<Monetary> {
		@Override
		public void serialize(Monetary value, JsonGenerator gen, SerializerProvider p) throws IOException {
			if (value == null) {
				gen.writeNull();
			} else {
				gen.writeString(String.valueOf(value));
			}
		}
	}

	class MonetaryDisplaySerializer extends JsonSerializer<Monetary> {
		@Override
		public void serialize(Monetary value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
			if (value == null) {
				gen.writeNull();
			} else {
				gen.writeString(value.toDisplayString());
			}
		}
	}
}
