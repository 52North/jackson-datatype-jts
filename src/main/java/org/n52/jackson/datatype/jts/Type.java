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

/**
 * GeoJSON types.
 */
public interface Type {
    /**
     * The type {@value}.
     */
    String POINT = "Point";
    /**
     * The type {@value}.
     */
    String LINE_STRING = "LineString";
    /**
     * The type {@value}.
     */
    String POLYGON = "Polygon";
    /**
     * The type {@value}.
     */
    String MULTI_POINT = "MultiPoint";
    /**
     * The type {@value}.
     */
    String MULTI_LINE_STRING = "MultiLineString";
    /**
     * The type {@value}.
     */
    String MULTI_POLYGON = "MultiPolygon";
    /**
     * The type {@value}.
     */
    String GEOMETRY_COLLECTION = "GeometryCollection";
    /**
     * The type {@value}.
     */
    String FEATURE = "Feature";
    /**
     * The type {@value}.
     */
    String FEATURE_COLLECTION = "FeatureCollection";
}
