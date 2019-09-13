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
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import java.io.IOException;
import java.util.Optional;

/**
 * {@link JsonSerializer} for {@link Geometry}.
 *
 * @author Christian Autermann
 */
public class GeometrySerializer extends JsonSerializer<Geometry> {

    private final IncludeBoundingBox includeBoundingBox;

    /**
     * Creates a new {@link GeometrySerializer}.
     */
    public GeometrySerializer() {
        this(null);
    }

    /**
     * Creates a new {@link GeometrySerializer}.
     *
     * @param includeBoundingBox when to include a bounding box for a {@link Geometry}.
     */
    public GeometrySerializer(IncludeBoundingBox includeBoundingBox) {
        this.includeBoundingBox = Optional.ofNullable(includeBoundingBox)
                .orElseGet(IncludeBoundingBox::never);
    }

    @Override
    public Class<Geometry> handledType() {
        return Geometry.class;
    }

    @Override
    public void serialize(Geometry geometry, JsonGenerator generator, SerializerProvider provider) throws IOException {
        if (geometry == null) {
            generator.writeNull();
        } else if (geometry instanceof Polygon) {
            serialize((Polygon) geometry, generator, provider);
        } else if (geometry instanceof Point) {
            serialize((Point) geometry, generator, provider);
        } else if (geometry instanceof MultiPoint) {
            serialize((MultiPoint) geometry, generator, provider);
        } else if (geometry instanceof MultiPolygon) {
            serialize((MultiPolygon) geometry, generator, provider);
        } else if (geometry instanceof LineString) {
            serialize((LineString) geometry, generator, provider);
        } else if (geometry instanceof MultiLineString) {
            serialize((MultiLineString) geometry, generator, provider);
        } else if (geometry instanceof GeometryCollection) {
            serialize((GeometryCollection) geometry, generator, provider);
        } else {
            throw JsonMappingException.from(generator,
                                            String.format("Geometry type %s is not supported.",
                                                          geometry.getClass().getName()));
        }
    }

    private void serialize(GeometryCollection value, JsonGenerator generator, SerializerProvider provider)
            throws IOException {
        generator.writeStartObject();

        serializeTypeAndBoundingBox(GeometryType.GEOMETRY_COLLECTION, value, generator);

        generator.writeArrayFieldStart(GeoJsonConstants.Fields.GEOMETRIES);

        for (int i = 0; i != value.getNumGeometries(); ++i) {
            serialize(value.getGeometryN(i), generator, provider);
        }

        generator.writeEndArray();
        generator.writeEndObject();
    }

    private void serialize(MultiPoint value, JsonGenerator generator, SerializerProvider provider)
            throws IOException {
        generator.writeStartObject();
        serializeTypeAndBoundingBox(GeometryType.MULTI_POINT, value, generator);
        generator.writeArrayFieldStart(GeoJsonConstants.Fields.COORDINATES);

        for (int i = 0; i < value.getNumGeometries(); ++i) {
            serializeCoordinate((Point) value.getGeometryN(i), generator, provider);
        }

        generator.writeEndArray();
        generator.writeEndObject();
    }

    private void serialize(MultiLineString value, JsonGenerator generator, SerializerProvider provider)
            throws IOException {
        generator.writeStartObject();
        serializeTypeAndBoundingBox(GeometryType.MULTI_LINE_STRING, value, generator);
        generator.writeArrayFieldStart(GeoJsonConstants.Fields.COORDINATES);
        for (int i = 0; i < value.getNumGeometries(); ++i) {
            serializeCoordinates((LineString) value.getGeometryN(i), generator, provider);
        }
        generator.writeEndArray();
        generator.writeEndObject();
    }

    private void serialize(MultiPolygon value, JsonGenerator generator, SerializerProvider provider)
            throws IOException {
        generator.writeStartObject();
        serializeTypeAndBoundingBox(GeometryType.MULTI_POLYGON, value, generator);
        generator.writeArrayFieldStart(GeoJsonConstants.Fields.COORDINATES);
        for (int i = 0; i < value.getNumGeometries(); ++i) {
            serializeCoordinates((Polygon) value.getGeometryN(i), generator, provider);
        }
        generator.writeEndArray();
        generator.writeEndObject();
    }

    private void serialize(Polygon value, JsonGenerator generator, SerializerProvider provider)
            throws IOException {
        generator.writeStartObject();
        serializeTypeAndBoundingBox(GeometryType.POLYGON, value, generator);
        generator.writeFieldName(GeoJsonConstants.Fields.COORDINATES);
        GeometrySerializer.this.serializeCoordinates(value, generator, provider);
        generator.writeEndObject();
    }

    private void serialize(LineString value, JsonGenerator generator, SerializerProvider provider)
            throws IOException {
        generator.writeStartObject();
        serializeTypeAndBoundingBox(GeometryType.LINE_STRING, value, generator);
        generator.writeFieldName(GeoJsonConstants.Fields.COORDINATES);
        serializeCoordinates(value, generator, provider);
        generator.writeEndObject();
    }

    private void serialize(Point value, JsonGenerator generator, SerializerProvider provider)
            throws IOException {
        generator.writeStartObject();
        serializeTypeAndBoundingBox(GeometryType.POINT, value, generator);
        generator.writeFieldName(GeoJsonConstants.Fields.COORDINATES);
        serializeCoordinate(value, generator, provider);
        generator.writeEndObject();
    }

    private void serializeTypeAndBoundingBox(GeometryType type, Geometry geometry, JsonGenerator generator)
            throws IOException {

        generator.writeStringField(GeoJsonConstants.Fields.TYPE, type.toString());

        if (this.includeBoundingBox.shouldIncludeBoundingBoxFor(type) && !geometry.isEmpty()) {
            Envelope envelope = geometry.getEnvelopeInternal();
            generator.writeArrayFieldStart(GeoJsonConstants.Fields.BOUNDING_BOX);
            generator.writeNumber(envelope.getMinX());
            generator.writeNumber(envelope.getMinY());
            generator.writeNumber(envelope.getMaxX());
            generator.writeNumber(envelope.getMaxY());
            generator.writeEndArray();
        }
    }

    private void serializeCoordinates(Polygon value, JsonGenerator generator, SerializerProvider provider)
            throws IOException {
        generator.writeStartArray();

        serializeCoordinates(value.getExteriorRing(), generator, provider);

        for (int i = 0; i < value.getNumInteriorRing(); ++i) {
            serializeCoordinates(value.getInteriorRingN(i), generator, provider);
        }
        generator.writeEndArray();
    }

    private void serializeCoordinates(LineString value, JsonGenerator generator, SerializerProvider provider)
            throws IOException {
        serializeCoordinates(value.getCoordinateSequence(), generator, provider);
    }

    private void serializeCoordinates(CoordinateSequence value, JsonGenerator generator, SerializerProvider provider)
            throws IOException {
        generator.writeStartArray();
        for (int i = 0; i < value.size(); ++i) {
            serializeCoordinate(value.getCoordinate(i), generator, provider);
        }
        generator.writeEndArray();
    }

    private void serializeCoordinate(Point value, JsonGenerator generator, SerializerProvider provider)
            throws IOException {
        serializeCoordinate(value.getCoordinate(), generator, provider);
    }

    private void serializeCoordinate(Coordinate value, JsonGenerator generator, SerializerProvider provider)
            throws IOException {
        generator.writeStartArray();
        generator.writeNumber(value.getX());
        generator.writeNumber(value.getY());
        if (!Double.isNaN(value.getZ()) && Double.isFinite(value.getZ())) {
            generator.writeNumber(value.getZ());
        }
        generator.writeEndArray();
    }

}
