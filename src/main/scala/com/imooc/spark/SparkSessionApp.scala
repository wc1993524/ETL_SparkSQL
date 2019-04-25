package com.imooc.spark

import org.apache.spark.sql.SparkSession

/**
 * spark2.x版本SparkSession的使用
 */
object SparkSessionApp {

  def main(args: Array[String]) {

    val spark = SparkSession.builder().appName("SparkSessionApp")
      .master("local[2]").getOrCreate()

    val people = spark.read.json("file:///home/wangc/software/spark-2.1.0-bin-hadoop2.7/examples/src/main/resources/people.json")
    people.show()

    spark.stop()
  }
}
