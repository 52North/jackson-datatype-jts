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

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import javax.annotation.Nullable;

/**
 * {@link com.fasterxml.jackson.databind.Module} to add serialization for JTS geometries.
 *
 * @author Christian Autermann
 */
public class JtsModule extends SimpleModule {
    private static final long serialVersionUID = 1L;
    private final GeometryFactory geometryFactory;
    private final IncludeBoundingBox includeBoundingBox;
    private final int decimalPlaces;

    /**
     * Creates a new {@link JtsModule}.
     */
    public JtsModule() {
        this(null, null, GeometrySerializer.DEFAULT_DECIMAL_PLACES);
    }

    /**
     * Creates a new {@link JtsModule}.
     *
     * @param decimalPlaces The number of decimal places for encoded coordinates.
     */
    public JtsModule(int decimalPlaces) {
        this(null, null, decimalPlaces);
    }

    /**
     * Creates a new {@link JtsModule}.
     *
     * @param geometryFactory The {@link GeometryFactory} to use to construct geometries.
     */
    public JtsModule(@Nullable GeometryFactory geometryFactory) {
        this(geometryFactory, null, GeometrySerializer.DEFAULT_DECIMAL_PLACES);
    }

    /**
     * Creates a new {@link JtsModule}.
     *
     * @param geometryFactory The {@link GeometryFactory} to use to construct geometries.
     * @param decimalPlaces   The number of decimal places for encoded coordinates.
     */
    public JtsModule(@Nullable GeometryFactory geometryFactory, int decimalPlaces) {
        this(geometryFactory, null, decimalPlaces);
    }

    /**
     * Creates a new {@link JtsModule}.
     *
     * @param includeBoundingBox The {@link IncludeBoundingBox} to use to serialize geometries.
     */
    public JtsModule(@Nullable IncludeBoundingBox includeBoundingBox) {
        this(null, includeBoundingBox, GeometrySerializer.DEFAULT_DECIMAL_PLACES);
    }

    /**
     * Creates a new {@link JtsModule}.
     *
     * @param includeBoundingBox The {@link IncludeBoundingBox} to use to serialize geometries.
     * @param decimalPlaces      The number of decimal places for encoded coordinates.
     */
    public JtsModule(@Nullable IncludeBoundingBox includeBoundingBox, int decimalPlaces) {
        this(null, includeBoundingBox, decimalPlaces);
    }

    /**
     * Creates a new {@link JtsModule}.
     *
     * @param geometryFactory    The {@link GeometryFactory} to use to construct geometries.
     * @param includeBoundingBox The {@link IncludeBoundingBox} to use to serialize geometries.
     */
    public JtsModule(@Nullable GeometryFactory geometryFactory, @Nullable IncludeBoundingBox includeBoundingBox) {
        this(geometryFactory, includeBoundingBox, GeometrySerializer.DEFAULT_DECIMAL_PLACES);
    }

    /**
     * Creates a new {@link JtsModule}.
     *
     * @param geometryFactory    The {@link GeometryFactory} to use to construct geometries.
     * @param includeBoundingBox The {@link IncludeBoundingBox} to use to serialize geometries.
     */
    public JtsModule(@Nullable GeometryFactory geometryFactory, @Nullable IncludeBoundingBox includeBoundingBox,
                     int decimalPlaces) {
        super(VersionInfo.getVersion());
        this.geometryFactory = geometryFactory;
        this.includeBoundingBox = includeBoundingBox;
        if (decimalPlaces < 0) {
            throw new IllegalArgumentException("decimalPlaces < 0");
        }
        this.decimalPlaces = decimalPlaces;
    }

    @Override
    public void setupModule(SetupContext context) {
        var deserializer = getDeserializer();
        addSerializer(Geometry.class, getSerializer());
        addDeserializer(Geometry.class, deserializer);
        addDeserializer(Point.class, new TypeSafeJsonDeserializer<>(Point.class, deserializer));
        addDeserializer(LineString.class, new TypeSafeJsonDeserializer<>(LineString.class, deserializer));
        addDeserializer(Polygon.class, new TypeSafeJsonDeserializer<>(Polygon.class, deserializer));
        addDeserializer(MultiPoint.class, new TypeSafeJsonDeserializer<>(MultiPoint.class, deserializer));
        addDeserializer(MultiLineString.class, new TypeSafeJsonDeserializer<>(MultiLineString.class, deserializer));
        addDeserializer(MultiPolygon.class, new TypeSafeJsonDeserializer<>(MultiPolygon.class, deserializer));
        addDeserializer(GeometryCollection.class,
                        new TypeSafeJsonDeserializer<>(GeometryCollection.class, deserializer));
        super.setupModule(context);
    }

    private JsonSerializer<Geometry> getSerializer() {
        return new GeometrySerializer(this.includeBoundingBox, this.decimalPlaces);
    }

    private JsonDeserializer<Geometry> getDeserializer() {
        return new GeometryDeserializer(geometryFactory);
    }

}
