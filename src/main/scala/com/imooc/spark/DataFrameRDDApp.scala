package com.imooc.spark

import org.apache.spark.sql.types.{StringType, IntegerType, StructField, StructType}
import org.apache.spark.sql.{Row, SparkSession}

/**
 * DataFrame和RDD的互操作
 */
object DataFrameRDDApp {

  def main(args: Array[String]) {

    val spark = SparkSession.builder().appName("DataFrameRDDApp").master("local[2]").getOrCreate()

    //inferReflection(spark)

    program(spark)

    spark.stop()
  }

  /**
    * Programmatically Specifying the Schema
    * 利用编程方式进行转换(此方式实在不知道列名和类型时使用)
    *
    * Create an RDD of Rows from the original RDD;
    * Create the schema represented by a StructType matching the structure of Rows in the RDD created in Step 1.
    * Apply the schema to the RDD of Rows via createDataFrame method provided by SparkSession.
    * @param spark
    */
  def program(spark: SparkSession): Unit = {
    // RDD ==> DataFrame
    val rdd = spark.sparkContext.textFile("file:///home/wangc/software/files/infos.txt")

    val infoRDD = rdd.map(_.split(",")).map(line => Row(line(0).toInt, line(1), line(2).toInt))

    val structType = StructType(Array(StructField("id", IntegerType, true),
      StructField("name", StringType, true),
      StructField("age", IntegerType, true)))

    val infoDF = spark.createDataFrame(infoRDD,structType)
    infoDF.printSchema()
    infoDF.show()


    //通过df的api进行操作
    infoDF.filter(infoDF.col("age") > 30).show

    //通过sql的方式进行操作
    infoDF.createOrReplaceTempView("infos")
    spark.sql("select * from infos where age > 30").show()
  }

  /**
    * Inferring the Schema Using Reflection
    * 利用反射进行转换(使用反射来推断包含特定数据类型RDD的元数据，即case class，事先需要知道字段名及类型)
    * @param spark
    */
  def inferReflection(spark: SparkSession) {
    // RDD ==> DataFrame
    val rdd = spark.sparkContext.textFile("file:///home/wangc/software/files/infos.txt")

    //注意：需要导入隐式转换
    import spark.implicits._
    val infoDF = rdd.map(_.split(",")).map(line => Info(line(0).toInt, line(1), line(2).toInt)).toDF()

    infoDF.show()

    infoDF.filter(infoDF.col("age") > 30).show

    infoDF.createOrReplaceTempView("infos")
    spark.sql("select * from infos where age > 30").show()
  }

  case class Info(id: Int, name: String, age: Int)

}
