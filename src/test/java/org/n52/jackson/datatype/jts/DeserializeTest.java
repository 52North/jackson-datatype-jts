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

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DeserializeTest {
    private final ObjectMapper mapper;

    public DeserializeTest() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JtsModule());
    }

    @Test
    public void throws_for_invalid_coordinates() {
        // GIVEN
        String json = "{\"multiPolygon\":{\"type\":\"MultiPolygon\","
                + "\"coordinates\":\"[[["
                + "[102.01234567890,2.01234567890],"
                + "[103.01234567890,2.01234567890],"
                + "[103.01234567890,3.01234567890],"
                + "[102.01234567890,3.01234567890],"
                + "[102.01234567890,2.01234567890]]]]\"}}}";

        // WHEN / THEN
        assertThatThrownBy(() -> mapper.readValue(json, TestObject.class))
                .isInstanceOf(JsonMappingException.class)
                .hasMessageStartingWith("Invalid coordinates, expecting an array but got: STRING");
    }

    @Test
    public void throws_for_invalid_coordinate_elements() {
        // GIVEN
        String json = "{\"point\":{\"type\":\"Point\",\"coordinates\":[[1,2],[3,4]]}}}";

        // WHEN / THEN
        assertThatThrownBy(() -> mapper.readValue(json, TestObject.class))
                .isInstanceOf(JsonMappingException.class)
                .hasMessageStartingWith("Invalid coordinates, expecting numbers but got: ARRAY");
    }

    public static class TestObject {
        private Point point;
        private MultiPolygon multiPolygon;

        public MultiPolygon getMultiPolygon() {
            return multiPolygon;
        }

        public void setMultiPolygon(MultiPolygon multiPolygon) {
            this.multiPolygon = multiPolygon;
        }

        public Point getPoint() {
            return point;
        }

        public void setPoint(Point point) {
            this.point = point;
        }
    }
}
