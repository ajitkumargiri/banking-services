```
import org.apache.spark.sql.{Dataset, Encoder, Encoders, Row, SparkSession}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{StructType, StructField, ArrayType, StringType}

// Initialize Spark Session
val spark = SparkSession.builder()
  .appName("CompanyProcessing")
  .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
  .getOrCreate()

// Define Encoders for CompanyCore and ModelWrapper
implicit val companyCoreEncoder: Encoder[CompanyCore] = Encoders.bean(classOf[CompanyCore])
implicit val modelWrapperEncoder: Encoder[ModelWrapper] = Encoders.bean(classOf[ModelWrapper])

// Step 1: Load the Parquet file into a Dataset[CompanyCore]
val companyDataset: Dataset[CompanyCore] = spark.read
  .parquet("/mnt/yourcompany/input-data/company-source-data.parquet")
  .as[CompanyCore]

// Initialize RuleExecutor
val ruleExecutor = new RuleExecutor()

// Step 2: Repartition Data for Parallel Processing
val numPartitions = 100  // Adjust this based on your cluster and data size
val repartitionedData = companyDataset.repartition(numPartitions)

// Step 3: Process Data in Parallel Using mapPartitions
val transformedRdd: RDD[Row] = repartitionedData.rdd.mapPartitions { iterator =>
  iterator.map { record =>
    val modelWrapper = ruleExecutor.executeTransformationRule(record)  // Apply transformation rule to each record
    val company = modelWrapper.company  // Now it's a single company object
    val apcList = modelWrapper.apcList
    val arList = modelWrapper.agreementRoleList
    val prList = modelWrapper.partyRelationshipList

    // Create Row for each extracted entity
    Row(company, apcList, arList, prList)  // Return a Row containing the company (single), apcList, arList, prList
  }
}

// Step 4: Convert RDD back to DataFrame with the corrected schema
val transformedDf = spark.createDataFrame(transformedRdd, StructType(Seq(
  StructField("company", classOf[Company], nullable = true),  // Single Company object
  StructField("apc", ArrayType(classOf[Apc]), nullable = true),
  StructField("ar", ArrayType(classOf[AgreementRole]), nullable = true),
  StructField("pr", ArrayType(classOf[PartyRelationship]), nullable = true)
)))

// Step 5: Extract each individual dataset and write them

// Extracting individual columns (entities)
val companyDatasetResult = transformedDf.select("company")
val apcDatasetResult = transformedDf.select("apc")
val arDatasetResult = transformedDf.select("ar")
val prDatasetResult = transformedDf.select("pr")

// Step 6: Write Data to Parquet or Delta Tables

// Write Company Dataset to Parquet
companyDatasetResult.write
  .mode("overwrite")
  .parquet("/mnt/output/company-parquet")

// Write APC Dataset to Parquet
apcDatasetResult.write
  .mode("overwrite")
  .parquet("/mnt/output/apc-parquet")

// Write AR Dataset to Parquet
arDatasetResult.write
  .mode("overwrite")
  .parquet("/mnt/output/ar-parquet")

// Write PR Dataset to Parquet
prDatasetResult.write
  .mode("overwrite")
  .parquet("/mnt/output/pr-parquet")

// Alternatively, write to Delta Tables
companyDatasetResult.write
  .format("delta")
  .mode("overwrite")
  .save("/mnt/output/company-delta")

apcDatasetResult.write
  .format("delta")
  .mode("overwrite")
  .save("/mnt/output/apc-delta")

arDatasetResult.write
  .format("delta")
  .mode("overwrite")
  .save("/mnt/output/ar-delta")

prDatasetResult.write
  .format("delta")
  .mode("overwrite")
  .save("/mnt/output/pr-delta")

// Step 7: Show results for validation (Optional)
companyDatasetResult.show()
apcDatasetResult.show()
arDatasetResult.show()
prDatasetResult.show()










import org.apache.spark.sql.{Dataset, Encoder, Encoders, Row, SparkSession}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.StructType

// Initialize Spark Session
val spark = SparkSession.builder()
  .appName("CompanyProcessing")
  .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
  .getOrCreate()

// Define Encoders for CompanyCore and ModelWrapper
implicit val companyCoreEncoder: Encoder[CompanyCore] = Encoders.bean(classOf[CompanyCore])
implicit val modelWrapperEncoder: Encoder[ModelWrapper] = Encoders.bean(classOf[ModelWrapper])

// Step 1: Load the Parquet file into a Dataset[CompanyCore]
val companyDataset: Dataset[CompanyCore] = spark.read
  .parquet("/mnt/yourcompany/input-data/company-source-data.parquet")
  .as[CompanyCore]

// Initialize RuleExecutor
val ruleExecutor = new RuleExecutor()

// Step 2: Repartition Data for Parallel Processing
val numPartitions = 100  // Adjust this based on your cluster and data size
val repartitionedData = companyDataset.repartition(numPartitions)

// Step 3: Process Data in Parallel Using mapPartitions
val transformedRdd: RDD[Row] = repartitionedData.rdd.mapPartitions { iterator =>
  iterator.map { record =>
    val modelWrapper = ruleExecutor.executeTransformationRule(record)  // Apply transformation rule to each record
    val companyList = Option(modelWrapper.company).toList
    val apcList = modelWrapper.apcList
    val arList = modelWrapper.agreementRoleList
    val prList = modelWrapper.partyRelationshipList

    // Create Row for each extracted entity
    Row(companyList, apcList, arList, prList)  // Return a Row containing the lists
  }
}

// Step 4: Convert RDD back to DataFrame
val transformedDf = spark.createDataFrame(transformedRdd, StructType(Seq(
  StructField("company", ArrayType(classOf[Company]), nullable = true),
  StructField("apc", ArrayType(classOf[Apc]), nullable = true),
  StructField("ar", ArrayType(classOf[AgreementRole]), nullable = true),
  StructField("pr", ArrayType(classOf[PartyRelationship]), nullable = true)
)))

// Step 5: Extract each individual dataset and write them

val companyDatasetResult = transformedDf.select("company").withColumnRenamed("company", "company")
val apcDatasetResult = transformedDf.select("apc").withColumnRenamed("apc", "apc")
val arDatasetResult = transformedDf.select("ar").withColumnRenamed("ar", "ar")
val prDatasetResult = transformedDf.select("pr").withColumnRenamed("pr", "pr")

// Write Company Dataset to Parquet
companyDatasetResult.write
  .mode("overwrite")
  .parquet("/mnt/output/company-parquet")

// Write APC Dataset to Parquet
apcDatasetResult.write
  .mode("overwrite")
  .parquet("/mnt/output/apc-parquet")

// Write AR Dataset to Parquet
arDatasetResult.write
  .mode("overwrite")
  .parquet("/mnt/output/ar-parquet")

// Write PR Dataset to Parquet
prDatasetResult.write
  .mode("overwrite")
  .parquet("/mnt/output/pr-parquet")

// Alternatively, write to Delta Tables
companyDatasetResult.write
  .format("delta")
  .mode("overwrite")
  .save("/mnt/output/company-delta")

apcDatasetResult.write
  .format("delta")
  .mode("overwrite")
  .save("/mnt/output/apc-delta")

arDatasetResult.write
  .format("delta")
  .mode("overwrite")
  .save("/mnt/output/ar-delta")

prDatasetResult.write
  .format("delta")
  .mode("overwrite")
  .save("/mnt/output/pr-delta")

// Show results for validation
companyDatasetResult.show()
apcDatasetResult.show()
arDatasetResult.show()
prDatasetResult.show()










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
