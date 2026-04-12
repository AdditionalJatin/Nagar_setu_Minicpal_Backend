package com.NagarSetu.Backend.MasterAdmin.DTOs;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.*;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GeometryUtil {

    private final ObjectMapper mapper; // ✅ inject (IMPORTANT)

    private final GeometryFactory geometryFactory =
            new GeometryFactory(new PrecisionModel(), 4326);

    // ✅ GeoJSON → Polygon
    public Polygon convertGeoJsonToPolygon(JsonNode geoJson) {

        JsonNode coordinates = geoJson.get("coordinates").get(0);

        Coordinate[] coords = new Coordinate[coordinates.size()];

        for (int i = 0; i < coordinates.size(); i++) {
            JsonNode point = coordinates.get(i);

            double lon = point.get(0).asDouble();
            double lat = point.get(1).asDouble();

            coords[i] = new Coordinate(lon, lat);
        }

        // 🔥 Ensure polygon is closed
        if (!coords[0].equals2D(coords[coords.length - 1])) {
            Coordinate[] newCoords = new Coordinate[coords.length + 1];
            System.arraycopy(coords, 0, newCoords, 0, coords.length);
            newCoords[newCoords.length - 1] = coords[0];
            coords = newCoords;
        }

        LinearRing shell = geometryFactory.createLinearRing(coords);
        Polygon polygon = geometryFactory.createPolygon(shell);

        // 🔥 Validate
        if (!polygon.isValid()) {
            throw new RuntimeException("Invalid polygon geometry");
        }

        return polygon;
    }

    // ✅ Center
    public Point calculateCenter(Polygon polygon) {
        return polygon.getCentroid();
    }

    // ✅ Polygon → GeoJSON
    public ObjectNode convertPolygonToGeoJson(Polygon polygon) {

        ObjectNode geoJson = mapper.createObjectNode();
        geoJson.put("type", "Polygon");

        var coordinatesArray = mapper.createArrayNode();
        var ringArray = mapper.createArrayNode();

        for (Coordinate coord : polygon.getCoordinates()) {
            var point = mapper.createArrayNode();
            point.add(coord.x);
            point.add(coord.y);
            ringArray.add(point);
        }

        coordinatesArray.add(ringArray);
        geoJson.set("coordinates", coordinatesArray);

        return geoJson;
    }

    // ✅ Point → GeoJSON
    public ObjectNode convertPointToGeoJson(Point point) {

        ObjectNode geoJson = mapper.createObjectNode();
        geoJson.put("type", "Point");

        var coord = mapper.createArrayNode();
        coord.add(point.getX());
        coord.add(point.getY());

        geoJson.set("coordinates", coord);

        return geoJson;
    }
}