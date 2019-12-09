package org.n52.jackson.datatype.jts;

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
