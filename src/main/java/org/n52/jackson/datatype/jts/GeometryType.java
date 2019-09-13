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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import java.util.Optional;

public enum GeometryType {
    /**
     * Geometries of type {@link Point}.
     */
    POINT("Point"),
    /**
     * Geometries of type {@link LineString}.
     */
    LINE_STRING("LineString"),
    /**
     * Geometries of type {@link Polygon}.
     */
    POLYGON("Polygon"),
    /**
     * Geometries of type {@link MultiPoint}.
     */
    MULTI_POINT("MultiPoint"),
    /**
     * Geometries of type {@link MultiLineString}.
     */
    MULTI_LINE_STRING("MultiLineString"),
    /**
     * Geometries of type {@link MultiPolygon}.
     */
    MULTI_POLYGON("MultiPolygon"),
    /**
     * Geometries of type {@link GeometryCollection}.
     */
    GEOMETRY_COLLECTION("GeometryCollection");

    private final String type;

    /**
     * Create a new {@link GeometryType}.
     *
     * @param type The string type.
     */
    GeometryType(String type) {
        this.type = type;
    }

    /**
     * The bit mask value of this {@link GeometryType}.
     *
     * @return The bit mask.
     */
    int mask() {
        return 1 << this.ordinal();
    }

    @Override
    @JsonValue
    public String toString() {
        return this.type;
    }

    /**
     * Get the geometry type from the supplied GeoJSON type value.
     *
     * @param value The GeoJSON type.
     * @return The {@link GeometryType}
     */
    @JsonCreator
    public static Optional<GeometryType> fromString(String value) {
        for (GeometryType type : GeometryType.values()) {
            if (type.toString().equals(value)) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }

    /**
     * Get the geometry type from the supplied {@link Geometry}.
     *
     * @param geometry The {@link Geometry}.
     * @return The {@link GeometryType}.
     */
    public static Optional<GeometryType> forGeometry(Geometry geometry) {
        if (geometry == null) {
            return Optional.empty();
        } else if (geometry instanceof Polygon) {
            return Optional.of(POLYGON);
        } else if (geometry instanceof Point) {
            return Optional.of(POINT);
        } else if (geometry instanceof MultiPoint) {
            return Optional.of(MULTI_POINT);
        } else if (geometry instanceof MultiPolygon) {
            return Optional.of(MULTI_POLYGON);
        } else if (geometry instanceof LineString) {
            return Optional.of(LINE_STRING);
        } else if (geometry instanceof MultiLineString) {
            return Optional.of(MULTI_LINE_STRING);
        } else if (geometry instanceof GeometryCollection) {
            return Optional.of(GEOMETRY_COLLECTION);
        } else {
            return Optional.empty();
        }
    }

}
