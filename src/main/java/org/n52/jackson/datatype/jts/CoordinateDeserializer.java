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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.locationtech.jts.geom.Coordinate;

import java.io.IOException;

/**
 * {@link JsonDeserializer} for {@link Coordinate}.
 *
 * @author João Rodrigues
 */
public class CoordinateDeserializer extends JsonDeserializer<Coordinate> {

	/**
     * Creates a new {@link CoordinateDeserializer}.
     */

    @Override
    public Coordinate deserialize(JsonParser p, DeserializationContext context) throws IOException {
        return deserializeCoordinate(p.readValueAs(JsonNode.class), context);
    }

    private Coordinate deserializeCoordinate(JsonNode node, DeserializationContext context)
            throws JsonMappingException {
        if (node.size() < 2) {
            throw JsonMappingException.from(context, String.format("Invalid number of ordinates: %d", node.size()));
        } else {
            if (node.isArray()) {
                double x = node.get(0).asDouble();
                double y = node.get(1).asDouble();
                if (node.size() < 3) {
                    return new Coordinate(x, y);
                } else {
                    double z = node.get(2).asDouble();
                    return new Coordinate(x, y, z);
                }
            } else if (node.isObject()) {
                double x = node.get("x").asDouble();
                double y = node.get("y").asDouble();
                JsonNode z = node.get("z");
                if (z == null) {
                    return new Coordinate(x, y);
                } else {
                    return new Coordinate(x, y, z.asDouble());
                }
            } else {
                 throw JsonMappingException.from(context, String.format("Unknown coordinates format: %s", node.toString()));
            }
        }
    }

}
