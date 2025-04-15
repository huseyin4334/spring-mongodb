# Aggregation On MongoDB
It enables you to perform operations such as filtering, grouping, sorting, and transforming data. 

> [Aggregation](https://www.mongodb.com/docs/manual/aggregation/)

Aggregation have stages. And every stage returns the result to the next stage. The result of the last stage is the final result.

`db.products.aggregate([ stage1, stage2, ... ])`

Stages are defined in an array. The stages are executed in the order they are defined.
- [Stages](https://www.mongodb.com/docs/manual/reference/operator/aggregation-pipeline/)
- `$match`: Filters the documents to pass only the documents that match the specified condition(s) to the next pipeline stage.
- `$group`: Groups documents by the specified identifier expression and applies the accumulator expression(s), if specified, to each group.
- `$sort`: Sorts all input documents and returns them to the pipeline in sorted order.

## Group And Match And Sort

```bash
# Example: Find the total sales for each category of products with a price greater than 10, sorted by total sales in descending order.
# If we group something, we should use the _id field. It will be unique for each group.
# We can map the different fields on the 
db.products.aggregate([
  # https://www.mongodb.com/docs/manual/reference/operator/aggregation/match/
  { $match: { price: { $gt: 10 } } },
  # https://www.mongodb.com/docs/manual/reference/operator/aggregation/group/
  { $group: { _id: "$category", totalSales: { $sum: "$price" } , avarageOfPrice: { $avg: "$price" } } },
  { $sort: { totalSales: -1 } }
])

# Example: Find the total sales for each category of products with a price greater than 10 after that show the results if the total sales are greater than 200.
db.products.aggregate([
  { $match: { price: { $gt: 10 } } },
  { $group: { _id: "$category", totalSales: { $sum: "$price" } , avarageOfPrice: { $avg: "$price" } } },
  { $match: { totalSales: { $gt: 200 } } },
  { $sort: { totalSales: -1 } }
])

# I can use group how many times I want. For example, I can group by category and then by subcategory.
db.products.aggregate([
  { $match: { price: { $gt: 10 } } },
  { $group: { _id: { category: "$category"}, totalSalesByRootCategory: { $sum: "$price" } , avarageOfPrice: { $avg: "$price" } } },
  { $group: { _id: { category: "$_id.category", subcategory: "$subcategory"}, totalSalesBySubCategory: { $sum: "$price" } , avarageOfPrice: { $avg: "$price" } } },
  { $sort: { totalSales: -1 } }
])

# I don't have the group by any field. I can get result for all documents too.
# $sum: 1 means that I will count the number of documents. I can also use $count operator. (productCount: $count: {})
db.products.aggregate([
  { $group: { _id: null, totalSales: { $sum: "$price" } , avarageOfPrice: { $avg: "$price" }, productCount: { $sum: 1 } } },
  { $sort: { totalSales: -1 } }
])
```

## Projection

```bash
db.products.aggregate([
    {
        $project: {
            name: 1,
            price: 1,
            category: 1,
            image: {
                $concat: [
                    "<url>/",
                    "$image",
                    { $toUpper: "#$imageStage" }
                ]
            },
            parentCategory: {
                $toUpper: {
                    $substrCP: [ # substrCP is used to get the substring of a string.
                        "$category", # 222-3344-5555
                        0,
                        {$indexOfCP: [ "$category", "-" ] } # indexOfCP is used to get the index of a string.
                        # Also, $strlenCP is used to get the length of a string.
                    ]
                }
            },
            stockQty: {
                $subtract: [
                    "$loadedQty",
                    { 
                        $cond: [ 
                            { 
                                $exist: { 
                                "$orderedQty" 
                                } 
                            }, "$orderedQty", 0 
                        ] 
                    } # If orderedQty is not exist, set it to 0.
                ]
            },
            warehouseLocation: {
                type: "Point",
                coordinates: [
                    { $toDouble: "$location.coordinates.longitude" },
                    { $convert: { input: "$location.coordinates.latitude", to: "double",  onError: 0.0, onNull: 0.0 } }
                ]
            },
            createdDate: {
                $dateToString: {
                    format: "%Y-%m-%d",
                    date: "$createdDate", # createdDate is a date.
                    timezone: "UTC"
                }
            },
            updatedDate: {
                $convert: { # $toDate is used to convert a string to date.
                    input: "$updatedDate", # updatedDate is a string.
                    to: "date",
                    onError: new Date(),
                    onNull: new Date()
                }
            }
        }
    },
])
```

## Pushing Data To Array And Unwind Usage

```bash
db.products.aggregate([
    {
        $group: {
            _id: "$category",
            products: { $push: "$$ROOT" }, # $$ROOT is used to get the whole document.
            totalSales: { $sum: "$price" },
            averageOfPrice: { $avg: "$price" },
            allTechDetails: { $push: "$techs" }, # We will see the array of array. 
        }
    },
    { $sort: { totalSales: -1 } }
])


db.products.aggregate([
    { 
        $unwind: "$techs" 
    }, # Unwind is used to flatten the array. This will return one document for each element in the array.
    { 
        $group: {
            _id: "$category",
            # I deleted the other calculated fields. Because they will be duplicated.
            allTechDetails: { $push: "$techs" },
        }
    },
    
    { $sort: { totalSales: -1 } }
])

# Eliminate the duplicates
db.products.aggregate([
    { 
        $unwind: "$techs" 
    }, # Unwind is used to flatten the array. This will return one document for each element in the array.
    { 
        $group: {
            _id: "$category",
            allTechDetails: { $addToSet: "$techs" }, # $addToSet is used to eliminate the duplicates.
        }
    },
    
    { $sort: { totalSales: -1 } }
])
```

## Slice And Filter

```bash
db.products.aggregate([
    { 
        $project: {
            name: 1,
            techs: { $slice: [ "$techs", 3 ] } # Get the first 3 elements of the array.
            otherTechs: { $slice: [ "$techs", -3 ] } # Get the last 3 elements of the array.
            # [ "$techs", 2, 1 ] # Get the 2nd element of the array.
            # [ "$techs", 0, 2 ] # Get the first 2 elements of the array.
            sizeOfTechs: { $size: "$techs" }, # Get the size of the array.
        }
    }
])

db.products.aggregate([
    { 
        $project: {
            name: 1,
            longTechs: { $filter: { input: "$techs", as: "tech", cond: { $gt: [ {$strLenCP: "$tech.description"}, 10 ] } } }, # Get the techs that have description length greater than 10.
            lessTechs: { $filter: { input: "$techs", as: "tech", cond: { $lt: [ {$strLenCP: "$tech.description"}, 10 ] } } }, # Get the techs that have description length less than 10.
        }
    }
])


db.products.aggregate([
    { $unwind: "$techs" }, # Unwind is used to flatten the array. This will return one document for each element in the array.
    { 
        $project: {
            name: 1,
            description: "$techs.description",
            price: 1,
            category: 1,
        },
    },
    { 
        $sort: { price: -1 } 
    }, # Sort by price in descending order.
    { 
        $group: {
            _id: "$category",
            totalSales: { $sum: "$price" },
            averageOfPrice: { $avg: "$price" },
            allTechDetails: { $push: "$techs" }, # We will see the array of array. 
        }
    }
])
```


## Bucket and BucketAuto
Buckets are used to group the documents into ranges. For example, I can group the documents by price ranges.
The difference from group is that I can define the ranges.

BucketAuto is used to automatically create the buckets based on the data.

```bash
db.products.aggregate([
    { 
        $bucket: {
            groupBy: "$price", # The field to group by.
            boundaries: [ 0, 10, 20, 30, 40, 50 ], # The boundaries of the buckets.
            # boundaries works like a range. The first bucket will be [0, 10), the second [10, 20), and so on.
            default: "Other", # The default bucket if the value is not in the boundaries.
            output: {
                name: { $push: "$name" }, # The name of the product.
                count: { $sum: 1 }, # The number of documents in the bucket.
                totalSales: { $sum: "$price" }, # The total sales in the bucket.
                averageOfPrice: { $avg: "$price" }, # The average price in the bucket.
            }
        }
    }
])


db.products.aggregate([
    { 
        $bucketAuto: {
            groupBy: "$price", # The field to group by.
            buckets: 5, # The number of buckets. I said I want 5 buckets. Mongo create buckets with equal sizes.
            output: {
                name: { $push: "$name" }, # The name of the product.
                count: { $sum: 1 }, # The number of documents in the bucket.
                totalSales: { $sum: "$price" }, # The total sales in the bucket.
                averageOfPrice: { $avg: "$price" }, # The average price in the bucket.
            }
        }
    }
])
```


## Pipeline Optimization
https://www.mongodb.com/docs/manual/core/aggregation-pipeline-optimization/


## Write Outputs To A Collection

```bash
db.products.aggregate([
    { 
        $group: {
            _id: "$category",
            products: { $push: "$$ROOT" }, # $$ROOT is used to get the whole document.
            totalSales: { $sum: "$price" },
            averageOfPrice: { $avg: "$price" },
            allTechDetails: { $push: "$techs" }, # We will see the array of array. 
        }
    },
    { $sort: { totalSales: -1 } },
    {
        $out: "productsByCategory" # Write the output to a collection.
    }
])
```
