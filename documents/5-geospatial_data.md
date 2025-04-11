# Geospatial Data
Geospatial data is data that is associated with a specific location on the Earth's surface. It can be used to represent various types of information, such as population density, climate patterns, and land use.

- GeoJSON: A format for encoding a variety of geographic data structures using JavaScript Object Notation (JSON). It is widely used for representing simple geographical features and their non-spatial attributes.

> https://www.mongodb.com/docs/manual/geospatial-queries/


We will have 2 argumants for the fields:
- `type`: The type of the field. It can be one of the following:
  - `Point`: A single point in space, represented by a pair of coordinates (longitude, latitude).
  - `LineString`: A sequence of points that form a line.
  - `Polygon`: A closed shape defined by a sequence of points.
  - ...
- `coordinates`: The coordinates of the field. It can be a single point, a list of points, or a list of lists of points, depending on the type of the field.
  - `Longitude`: The longitude of the point. (-180 to 180)
  - `Latitude`: The latitude of the point. (-90 to 90)

```bash
db.city.insertOne(
  {
    name: "New York",
    location: {
      type: "Point",
      coordinates: [-74.006, 40.7128]
    }
  }
)
```

---

For search the geospatial data, we should have an index on the field. Because the field is a GeoJSON, we should use the `2dsphere` index.

```bash
db.city.createIndex({ location: "2dsphere" })
```

```bash
# The maxDistance and minDistance are in meters.
db.city.find(
  {
    location: {
      $near: {
        $geometry: {
          type: "Point",
          coordinates: [-74.006, 40.7128]
        },
        $maxDistance: 1000,
        $minDistance: 2
      }
    }
  }
)
```

```bash
# The geoWithin operator is used to find documents that are within a certain distance from a point.
# geoWithin will create a bounding box around the point and return all documents that are within that box.
# I can give multiple points to create a polygon.
db.city.find(
  {
    location: {
      $geoWithin: {
        $geometry: {
          type: "Polygon",
          coordinates: [
            [
              [-74.006, 40.7128],
              [-73.935, 40.7308],
              [-73.935, 40.7128],
              [-74.006, 40.7128]
            ]
          ]
        }
      }
    }
  }
)
```

```bash
db.places.insertOne(
  {
    name: "Central Park",
    area: {
      type: "Polygon",
      coordinates: [
        [
          [-73.9654, 40.7851],
          [-73.9580, 40.7851],
          [-73.9580, 40.7782],
          [-73.9654, 40.7782],
          [-73.9654, 40.7851]
        ]
      ]
    }
  }
)

db.users.insertOne(
  {
    name: "John Doe",
    location: {
      type: "Point",
      coordinates: [-73.9654, 40.7851]
    }
  }
)

db.places.createIndex({ area: "2dsphere" })
db.users.createIndex({ location: "2dsphere" })
```

```bash
# Let's find out if the user is inside the park.

db.area.find(
  {
    area: {
      $geoIntersects: {
        $geometry: {
          type: "Point",
          coordinates: [-73.9654, 40.7851]
        }
      }
    }
  }
)

# This will return the document this location is inside the park.
```

```bash
# Find cities within a certain radius of a point.
# Radius means the distance from the point to the edge of the circle.

db.city.find(
  {
    location: {
      $geoWithin: {
        $centerSphere: [
          [-74.006, 40.7128], # Longitude and latitude of the center point
          10 / 3963.2 # 10 miles in radians (Earth's radius is 3963.2 miles) (This will create a circle with a radius of 10 miles)
        ]
      }
    }
  }
)