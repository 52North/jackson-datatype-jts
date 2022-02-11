/*
 * Copyright 2019 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.jackson.datatype.jts;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.locationtech.jts.geom.Coordinate;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * {@link JsonSerializer} for {@link Coordinate}.
 *
 * @author João Rodrigues
 */
public class CoordinateSerializer extends JsonSerializer<Coordinate> {
    static final int DEFAULT_DECIMAL_PLACES = 8;
    private final NumberFormat decimalFormat;

    /**
     * Creates a new {@link CoordinateSerializer}.
     */
    public CoordinateSerializer() {
        this(DEFAULT_DECIMAL_PLACES);
    }

    /**
     * Creates a new {@link CoordinateSerializer}.
     *
     * @param decimalPlaces      The number of decimal places for encoded coordinates.
     */
    public CoordinateSerializer(int decimalPlaces) {
        if (decimalPlaces < 0) {
            throw new IllegalArgumentException("decimalPlaces < 0");
        }
        this.decimalFormat = createNumberFormat(decimalPlaces);
    }

    private NumberFormat createNumberFormat(int decimalPlaces) {
        NumberFormat format = DecimalFormat.getInstance(Locale.ROOT);
        format.setRoundingMode(RoundingMode.HALF_UP);
        format.setMinimumFractionDigits(0);
        format.setMaximumFractionDigits(decimalPlaces);
        format.setGroupingUsed(false);
        return format;
    }

    @Override
    public Class<Coordinate> handledType() {
        return Coordinate.class;
    }

    @Override
    public void serialize(Coordinate value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        if (value == null) {
            generator.writeNull();
        } else {
            serializeCoordinate(value, generator, provider);
        }
    }

    private void serializeCoordinate(Coordinate value, JsonGenerator generator, SerializerProvider provider)
            throws IOException {
        generator.writeStartArray();
        generator.writeNumber(decimalFormat.format(value.getX()));
        generator.writeNumber(decimalFormat.format(value.getY()));
        if (!Double.isNaN(value.getZ()) && Double.isFinite(value.getZ())) {
            generator.writeNumber(decimalFormat.format(value.getZ()));
        }
        generator.writeEndArray();
    }

}
