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

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import java.io.Serializable;

/**
 * Class to determine when to include a bounding box for geometries.
 */
public final class IncludeBoundingBox implements Serializable {
    private static final long serialVersionUID = 204690259235746434L;
    private final int mask;

    /**
     * Create a new {@link IncludeBoundingBox}.
     *
     * @param mask The bit mask.
     */
    private IncludeBoundingBox(int mask) {
        this.mask = mask;
    }

    /**
     * @param type The {@link GeometryType}.
     * @return The {@link IncludeBoundingBox}.
     */
    private IncludeBoundingBox include(GeometryType type) {
        return new IncludeBoundingBox(this.mask | type.mask());
    }

    /**
     * Include the bounding box for geometries of type {@link Point}.
     *
     * @return The {@link IncludeBoundingBox}.
     */
    public IncludeBoundingBox forPoint() {
        return include(GeometryType.POINT);
    }

    /**
     * Include the bounding box for geometries of type {@link LineString}.
     *
     * @return The {@link IncludeBoundingBox}.
     */
    public IncludeBoundingBox forLineString() {
        return include(GeometryType.LINE_STRING);
    }

    /**
     * Include the bounding box for geometries of type {@link Polygon}.
     *
     * @return The {@link IncludeBoundingBox}.
     */
    public IncludeBoundingBox forPolygon() {
        return include(GeometryType.POLYGON);
    }

    /**
     * Include the bounding box for geometries of type {@link MultiPoint}.
     *
     * @return The {@link IncludeBoundingBox}.
     */
    public IncludeBoundingBox forMultiPoint() {
        return include(GeometryType.MULTI_POINT);
    }

    /**
     * Include the bounding box for geometries of type {@link MultiLineString}.
     *
     * @return The {@link IncludeBoundingBox}.
     */
    public IncludeBoundingBox forMultiLineString() {
        return include(GeometryType.MULTI_LINE_STRING);
    }

    /**
     * Include the bounding box for geometries of type {@link MultiPolygon}.
     *
     * @return The {@link IncludeBoundingBox}.
     */
    public IncludeBoundingBox forMultiPolygon() {
        return include(GeometryType.MULTI_POLYGON);
    }

    /**
     * Include the bounding box for geometries of type {@link GeometryCollection}.
     *
     * @return The {@link IncludeBoundingBox}.
     */
    public IncludeBoundingBox forGeometryCollection() {
        return include(GeometryType.GEOMETRY_COLLECTION);
    }

    /**
     * Include the bounding box for geometries of type {@link MultiPoint}, {@link MultiLineString}, {@link MultiPolygon}
     * and {@link GeometryCollection}.
     *
     * @return The {@link IncludeBoundingBox}.
     */
    public IncludeBoundingBox forMultiGeometry() {
        return include(GeometryType.MULTI_POINT)
                .include(GeometryType.MULTI_LINE_STRING)
                .include(GeometryType.MULTI_POLYGON)
                .include(GeometryType.GEOMETRY_COLLECTION);
    }

    /**
     * Checks if the bounding box should be included for the {@link Geometry}.
     *
     * @param geometry The {@link Geometry}.
     * @return If the bounding box should be included.
     */
    public boolean shouldIncludeBoundingBoxFor(Geometry geometry) {
        return GeometryType.forGeometry(geometry).map(this::shouldIncludeBoundingBoxFor).orElse(false);
    }

    /**
     * Checks if the bounding box should be included for the {@link GeometryType}
     *
     * @param type The {@link GeometryType}.
     * @return If the bounding box should be included.
     */
    public boolean shouldIncludeBoundingBoxFor(GeometryType type) {
        return (this.mask & type.mask()) != 0;
    }

    /**
     * Never include a bounding box.
     *
     * @return The {@link IncludeBoundingBox}.
     */
    public static IncludeBoundingBox never() {
        return new IncludeBoundingBox(0);
    }

    /**
     * Always include bounding box.
     *
     * @return The {@link IncludeBoundingBox}.
     */
    public static IncludeBoundingBox always() {
        return never()
                .forGeometryCollection()
                .forMultiPolygon()
                .forMultiLineString()
                .forMultiPoint()
                .forPolygon()
                .forLineString()
                .forPoint();
    }

    /**
     * Always include bounding box.
     *
     * @return The {@link IncludeBoundingBox}.
     */
    public static IncludeBoundingBox exceptPoints() {
        return never()
                .forGeometryCollection()
                .forMultiPolygon()
                .forMultiLineString()
                .forMultiPoint()
                .forPolygon()
                .forLineString();
    }

}
