/*
 * Copyright 2019-2025 52°North Spatial Information Research GmbH
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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Optional;

/**
 * {@link JsonDeserializer} for {@link Geometry}.
 *
 * @author Christian Autermann
 */
public class GeometryDeserializer extends JsonDeserializer<Geometry> {
    private static final int DEFAULT_SRID = 4326;
    private static final GeometryFactory DEFAULT_GEOMETRY_FACTORY = getDefaultGeometryFactory();
    private final GeometryFactory geometryFactory;

    /**
     * Creates a new {@link GeometryDeserializer}.
     */
    public GeometryDeserializer() {
        this(null);
    }

    /**
     * Creates a new {@link GeometryDeserializer}.
     *
     * @param geometryFactory The {@link GeometryFactory} to use to construct geometries.
     */
    public GeometryDeserializer(@Nullable GeometryFactory geometryFactory) {
        this.geometryFactory = Optional.ofNullable(geometryFactory).orElse(DEFAULT_GEOMETRY_FACTORY);
    }

    @Override
    public Geometry deserialize(JsonParser p, DeserializationContext context) throws IOException {
        return deserializeGeometry(p.readValueAs(JsonNode.class), context);
    }

    private Geometry deserializeGeometry(JsonNode node, DeserializationContext context)
            throws JsonMappingException {
        String typeName = node.get(Field.TYPE).asText();

        GeometryType type = GeometryType.fromString(typeName)
                .orElseThrow(() -> invalidGeometryType(context, typeName));

        switch (type) {
            case POINT:
                return deserializePoint(node, context);
            case MULTI_POINT:
                return deserializeMultiPoint(node, context);
            case LINE_STRING:
                return deserializeLineString(node, context);
            case MULTI_LINE_STRING:
                return deserializeMultiLineString(node, context);
            case POLYGON:
                return deserializePolygon(node, context);
            case MULTI_POLYGON:
                return deserializeMultiPolygon(node, context);
            case GEOMETRY_COLLECTION:
                return deserializeGeometryCollection(node, context);
            default:
                throw invalidGeometryType(context, typeName);
        }
    }

    private JsonMappingException invalidGeometryType(DeserializationContext context, String typeName) {
        return JsonMappingException.from(context, "Invalid geometry type: " + typeName);
    }

    private Point deserializePoint(JsonNode node, DeserializationContext context) throws JsonMappingException {
        JsonNode coordinates = getArray(node, context, Field.COORDINATES);
        return this.geometryFactory.createPoint(deserializeCoordinate(coordinates, context));
    }

    private Polygon deserializePolygon(JsonNode node, DeserializationContext context) throws JsonMappingException {
        JsonNode coordinates = getArray(node, context, Field.COORDINATES);
        return deserializeLinearRings(coordinates, context);
    }

    private MultiPolygon deserializeMultiPolygon(JsonNode node, DeserializationContext context)
            throws JsonMappingException {
        JsonNode coordinates = getArray(node, context, Field.COORDINATES);
        Polygon[] polygons = new Polygon[coordinates.size()];
        for (int i = 0; i != coordinates.size(); ++i) {
            polygons[i] = deserializeLinearRings(coordinates.get(i), context);
        }
        return this.geometryFactory.createMultiPolygon(polygons);
    }

    private MultiPoint deserializeMultiPoint(JsonNode node, DeserializationContext context)
            throws JsonMappingException {
        JsonNode coordinates = getArray(node, context, Field.COORDINATES);
        Coordinate[] coords = deserializeCoordinates(coordinates, context);
        return this.geometryFactory.createMultiPointFromCoords(coords);
    }

    private GeometryCollection deserializeGeometryCollection(JsonNode node, DeserializationContext context)
            throws JsonMappingException {
        JsonNode geometries = getArray(node, context, Field.GEOMETRIES);
        Geometry[] geom = new Geometry[geometries.size()];
        for (int i = 0; i != geometries.size(); ++i) {
            geom[i] = deserializeGeometry(geometries.get(i), context);
        }
        return this.geometryFactory.createGeometryCollection(geom);
    }

    private MultiLineString deserializeMultiLineString(JsonNode node, DeserializationContext context)
            throws JsonMappingException {
        JsonNode coordinates = getArray(node, context, Field.COORDINATES);
        LineString[] lineStrings = lineStringsFromJson(coordinates, context);
        return this.geometryFactory.createMultiLineString(lineStrings);
    }

    private LineString[] lineStringsFromJson(JsonNode node, DeserializationContext context)
            throws JsonMappingException {
        LineString[] strings = new LineString[node.size()];
        for (int i = 0; i != node.size(); ++i) {
            Coordinate[] coordinates = deserializeCoordinates(node.get(i), context);
            strings[i] = this.geometryFactory.createLineString(coordinates);
        }
        return strings;
    }

    private LineString deserializeLineString(JsonNode node, DeserializationContext context)
            throws JsonMappingException {
        JsonNode coordinates = getArray(node, context, Field.COORDINATES);
        Coordinate[] coords = deserializeCoordinates(coordinates, context);
        return this.geometryFactory.createLineString(coords);
    }

    private Coordinate[] deserializeCoordinates(JsonNode node, DeserializationContext context)
            throws JsonMappingException {
        Coordinate[] points = new Coordinate[node.size()];
        for (int i = 0; i != node.size(); ++i) {
            points[i] = deserializeCoordinate(node.get(i), context);
        }
        return points;
    }

    private Polygon deserializeLinearRings(JsonNode node, DeserializationContext context)
            throws JsonMappingException {
        LinearRing shell = deserializeLinearRing(node.get(0), context);
        LinearRing[] holes = new LinearRing[node.size() - 1];
        for (int i = 1; i < node.size(); ++i) {
            holes[i - 1] = deserializeLinearRing(node.get(i), context);
        }
        return this.geometryFactory.createPolygon(shell, holes);
    }

    private LinearRing deserializeLinearRing(JsonNode node, DeserializationContext context)
            throws JsonMappingException {
        Coordinate[] coordinates = deserializeCoordinates(node, context);
        return this.geometryFactory.createLinearRing(coordinates);
    }

    private Coordinate deserializeCoordinate(JsonNode node, DeserializationContext context)
            throws JsonMappingException {
        if (node.size() < 2) {
            throw JsonMappingException.from(context, String.format("Invalid number of ordinates: %d", node.size()));
        } else {
            double x = getOrdinate(node, 0, context);
            double y = getOrdinate(node, 1, context);
            if (node.size() < 3) {
                return new Coordinate(x, y);
            } else {
                double z = getOrdinate(node, 2, context);
                return new Coordinate(x, y, z);
            }
        }
    }

    private JsonNode getArray(JsonNode node, DeserializationContext context, String fieldName)
            throws JsonMappingException {
        JsonNode coordinates = node.get(fieldName);
        if (coordinates != null && !coordinates.isArray()) {
            throw JsonMappingException.from(context, "Invalid coordinates, expecting an array but got: "
                    + coordinates.getNodeType().toString());
        }
        return coordinates;
    }

    private double getOrdinate(JsonNode node, int i, DeserializationContext context) throws JsonMappingException {
        JsonNode ordinate = node.get(i);
        if (!ordinate.isNumber()) {
            throw JsonMappingException.from(context, "Invalid coordinates, expecting numbers but got: "
                    + ordinate.getNodeType().toString());
        }
        return ordinate.asDouble();
    }

    private static GeometryFactory getDefaultGeometryFactory() {
        return new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), DEFAULT_SRID);
    }
}
