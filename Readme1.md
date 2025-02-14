```
Use Case: Nested and Real-World Complex Object Mapping with MapStruct in Spring Boot

Scenario:

Imagine a user management system where a User entity has nested objects like Address and Role. We need to map this complex entity into a UserDTO that also includes these nested structures properly formatted.


---

1. Project Dependencies (Spring Boot + MapStruct)

Maven Configuration

<dependencies>
    <!-- Spring Boot Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>

    <!-- MapStruct -->
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>1.5.5.Final</version>
    </dependency>

    <!-- MapStruct Processor -->
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct-processor</artifactId>
        <version>1.5.5.Final</version>
        <scope>provided</scope>
    </dependency>

    <!-- Lombok (optional but recommended) -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok-mapstruct-binding</artifactId>
        <version>0.2.0</version>
        <scope>provided</scope>
    </dependency>
</dependencies>


---

2. Define Entity Classes (Nested Structure)

User Entity with Nested Address and Role

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    @Embedded
    private Address address;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;
}

Address as Embedded Object

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    private String street;
    private String city;
    private String state;
    private String zipCode;
}

Role Entity

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String roleName;
}


---

3. Define DTO Classes

UserDTO with Flattened Address and Role List

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String fullName;
    private String email;
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private List<String> roles;
}


---

4. Implement MapStruct Mappers

Address Mapper

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    default String mapAddress(Address address) {
        return address.getStreet() + ", " + address.getCity() + ", " + address.getState() + " " + address.getZipCode();
    }
}

Role Mapper

import org.mapstruct.Mapper;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    default List<String> mapRoles(List<Role> roles) {
        return roles.stream().map(Role::getRoleName).collect(Collectors.toList());
    }
}

User Mapper with Nested Object Handling

import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {AddressMapper.class, RoleMapper.class})
public interface UserMapper {

    @Mapping(source = "firstName", target = "fullName", qualifiedByName = "combineName")
    @Mapping(source = "address.street", target = "street")
    @Mapping(source = "address.city", target = "city")
    @Mapping(source = "address.state", target = "state")
    @Mapping(source = "address.zipCode", target = "zipCode")
    @Mapping(source = "roles", target = "roles")
    UserDTO toUserDTO(User user);

    @Named("combineName")
    default String combineName(String firstName, String lastName) {
        return firstName + " " + lastName;
    }
}


---

5. Implement Service Layer

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                             .stream()
                             .map(userMapper::toUserDTO)
                             .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                                  .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toUserDTO(user);
    }
}


---

6. Implement Controller Layer

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
}


---

7. Example JSON Output

Database Entry (User)

{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "address": {
    "street": "123 Main St",
    "city": "Springfield",
    "state": "IL",
    "zipCode": "62704"
  },
  "roles": [
    { "id": 1, "roleName": "ADMIN" },
    { "id": 2, "roleName": "USER" }
  ]
}

Mapped DTO Output

{
  "id": 1,
  "fullName": "John Doe",
  "email": "john.doe@example.com",
  "street": "123 Main St",
  "city": "Springfield",
  "state": "IL",
  "zipCode": "62704",
  "roles": ["ADMIN", "USER"]
}


---

Key Takeaways

✅ Handles nested objects properly using multiple mappers.
✅ Uses @Mapping for precise field mappings.
✅ Uses @Named methods for custom logic like full name generation.
✅ Easily integrates with Spring Boot (componentModel = "spring").
✅ Optimized for real-world scenarios with address and role lists.

Would you like to add further enhancements, such as bi-directional mapping or DTO-to-Entity conversion?








Using MapStruct in a Spring Boot application is an efficient way to handle object mapping. Below are the best practices and steps to set it up properly:


---

1. Add Dependencies

For Maven:

<dependencies>
    <!-- MapStruct -->
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>1.5.5.Final</version>
    </dependency>
    
    <!-- MapStruct Annotation Processor -->
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct-processor</artifactId>
        <version>1.5.5.Final</version>
        <scope>provided</scope>
    </dependency>
    
    <!-- Lombok (if using Lombok with MapStruct) -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <scope>provided</scope>
    </dependency>

    <!-- Lombok MapStruct Binding -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok-mapstruct-binding</artifactId>
        <version>0.2.0</version>
        <scope>provided</scope>
    </dependency>
</dependencies>

For Gradle:

dependencies {
    implementation 'org.mapstruct:mapstruct:1.5.5.Final'
    annotationProcessor 'org.mapstruct:mapstruct-processor:1.5.5.Final'

    // If using Lombok
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok-mapstruct-binding:0.2.0'
}


---

2. Create DTOs and Entities

Entity Class

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    
    // Getters and Setters (or use Lombok @Data)
}

DTO Class

public class UserDTO {
    private Long id;
    private String fullName;
    private String email;
    
    // Getters and Setters (or use Lombok @Data)
}


---

3. Create a Mapper Interface

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")  // Integrates with Spring
public interface UserMapper {
    
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "firstName", target = "fullName", qualifiedByName = "combineName")
    UserDTO toUserDTO(User user);

    @Named("combineName")
    default String combineName(String firstName, String lastName) {
        return firstName + " " + lastName;
    }
}


---

4. Use Mapper in Service Layer

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toUserDTO(user);
    }
}


---

5. Use in Controller

@RestController
@RequestMapping("/users")
public class UserController {
    
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
}


---

Best Practices

1. Use @Mapper(componentModel = "spring"): This ensures Spring manages the mapper as a bean.


2. Use @Mapping for Custom Mapping: Helps with field renaming and transformations.


3. Use @Named for Custom Methods: Allows reusable custom conversions.


4. Leverage @MappingTarget for Updates: Instead of creating new objects.

@Mapping(target = "id", ignore = true)
void updateUserFromDto(UserDTO dto, @MappingTarget User user);


5. Use List<UserDTO> toUserDTOList(List<User> users); for collections.




---

This setup ensures clean, efficient, and maintainable object mapping in your Spring Boot application. Let me know if you need further clarifications!









import org.apache.spark.sql.{Dataset, Encoder, Encoders, SparkSession}
import org.apache.spark.sql.functions._

val spark = SparkSession.builder()
  .appName("Parallel Processing for ModelWrapper")
  .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
  .getOrCreate()

// Define Encoders
implicit val companyCoreEncoder: Encoder[CompanyCore] = Encoders.bean(classOf[CompanyCore])
implicit val modelWrapperEncoder: Encoder[ModelWrapper] = Encoders.bean(classOf[ModelWrapper])
implicit val partyEncoder: Encoder[Party] = Encoders.bean(classOf[Party])
implicit val apcEncoder: Encoder[Apc] = Encoders.bean(classOf[Apc])
implicit val arEncoder: Encoder[AgreementRole] = Encoders.bean(classOf[AgreementRole])
implicit val prEncoder: Encoder[PartyRelationship] = Encoders.bean(classOf[PartyRelationship])

// Step 1: Read the Parquet data
val companyDataset: Dataset[CompanyCore] = spark.read
  .parquet("/mnt/company/input-data/company-source-data.parquet")
  .as[CompanyCore]

// Initialize RuleExecutor
val ruleExecutor = new RuleExecutor()

// Step 2: Apply transformation in parallel using `mapPartitions`
val modelWrapperDS: Dataset[ModelWrapper] = companyDataset.repartition(100).mapPartitions { iterator =>
  iterator.map { record =>
    ruleExecutor.executeTransformationRule(record) // Parallel transformation
  }
}.as[ModelWrapper]

// Step 3: Extract inner objects **from ModelWrapper**
val companyDF = modelWrapperDS.map(_.getCompany).toDF()
val apcDF = modelWrapperDS.flatMap(_.getApcList).toDF()
val arDF = modelWrapperDS.flatMap(_.getAgreementRoleList).toDF()
val prDF = modelWrapperDS.flatMap(_.getPartyRelationshipList).toDF()

// Step 4: Write all datasets **in parallel**
companyDF.write.mode("overwrite").parquet("/mnt/output/company-parquet")
apcDF.write.mode("overwrite").parquet("/mnt/output/apc-parquet")
arDF.write.mode("overwrite").parquet("/mnt/output/ar-parquet")
prDF.write.mode("overwrite").parquet("/mnt/output/pr-parquet")

// Alternative: Write to Delta Tables
companyDF.write.format("delta").mode("overwrite").save("/mnt/output/company-delta")
apcDF.write.format("delta").mode("overwrite").save("/mnt/output/apc-delta")
arDF.write.format("delta").mode("overwrite").save("/mnt/output/ar-delta")
prDF.write.format("delta").mode("overwrite").save("/mnt/output/pr-delta")

// Show results for validation
companyDF.show()
apcDF.show()
arDF.show()
prDF.show()








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
