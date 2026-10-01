/*
 * Copyright 2019-2026 52°North Spatial Information Research GmbH
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

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

public class EmptyPointTest {
    private final ObjectMapper mapper = JsonMapper.builder().addModule(new JtsModule()).build();
    private final GeometryFactory factory = new GeometryFactory();

    @Test
    public void serializes_empty_point() throws Exception {
        String json = mapper.writeValueAsString(factory.createPoint());
        assertThat(mapper.readTree(json).get("coordinates").isArray()).isTrue();
        assertThat(mapper.readTree(json).get("coordinates").size()).isZero();
    }

    @Test
    public void deserializes_empty_point() throws Exception {
        Point point = mapper.readValue("{\"type\":\"Point\",\"coordinates\":[]}", Point.class);
        assertThat(point.isEmpty()).isTrue();
    }

    @Test
    public void round_trips_empty_points_in_collections() throws Exception {
        Point empty = factory.createPoint();
        Point point = factory.createPoint(new Coordinate(1, 2, 3));
        Geometry[] geometries = {
            factory.createGeometryCollection(new Geometry[] {empty, point}),
            factory.createMultiPoint(new Point[] {empty, point})
        };
        for (Geometry geometry : geometries) {
            String json = mapper.writeValueAsString(geometry);
            Geometry restored = mapper.readValue(json, Geometry.class);
            assertThat(restored.getNumGeometries()).isEqualTo(2);
            assertThat(restored.getGeometryN(0).isEmpty()).isTrue();
            assertThat(restored.getGeometryN(1).getCoordinate().equals3D(point.getCoordinate())).isTrue();
        }
    }
}
