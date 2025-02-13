```
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.Dataset

// Initialize the Spark session
val spark = SparkSession.builder()
  .appName("Parallel Processing and Writing to Parquet/Delta")
  .config("spark.sql.shuffle.partitions", "200") // Adjust based on cluster size
  .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
  .getOrCreate()

// Define the number of partitions based on the cluster resources
val numPartitions = 100  // Adjust according to your data size and cluster configuration

// Read the data from Parquet (adjust the path accordingly)
val dataDf = spark.read.parquet("/mnt/data-source/data-source-file.parquet")

// Repartition the DataFrame to optimize parallelism
val dataDfRepartitioned = dataDf.repartition(numPartitions)

// Define the transformation logic (Java method for transformation)
def transformData(dataWrapper: DataWrapper): DataWrapper = {
  // Implement your Java method logic to transform the dataWrapper object
  // This could be any transformation like modifying fields, applying business logic, etc.
  dataWrapper // For simplicity, returning the same object. Modify this logic.
}

// Parallelize the transformation using mapPartitions
val transformedRdd: RDD[Row] = dataDfRepartitioned.rdd.mapPartitions { iterator =>
  iterator.map { record =>
    val dataWrapper = record.getAs[DataWrapper]("dataWrapper")
    // Apply the transformation logic (parallel execution on each partition)
    val transformedDataWrapper = transformData(dataWrapper)
    Row(transformedDataWrapper) // Return transformed row
  }
}

// Convert RDD back to DataFrame
val transformedDf: Dataset[Row] = spark.createDataFrame(transformedRdd, dataDfRepartitioned.schema)

// Write to Parquet or Delta table

// Option 1: Write to Parquet
transformedDf.write
  .mode("overwrite")  // You can choose "append" or "overwrite" based on your need
  .parquet("/mnt/output/parquet-output")

// Option 2: Write to Delta Table
transformedDf.write
  .format("delta")  // Delta format
  .mode("overwrite")  // Choose mode: "overwrite" or "append"
  .save("/mnt/output/delta-output")

// Show the transformed data for validation
transformedDf.show()




```
