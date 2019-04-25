package com.imooc.spark

import org.apache.spark.sql.SparkSession

/**
 * DataFrame中的操作操作
 */
object DataFrameCase {

  def main(args: Array[String]) {
    val spark = SparkSession.builder().appName("DataFrameRDDApp").master("local[2]").getOrCreate()

    // RDD ==> DataFrame
    val rdd = spark.sparkContext.textFile("file:///home/wangc/software/files/user.txt")

    //注意：需要导入隐式转换
    import spark.implicits._
    val studentDF = rdd.map(_.split("\\|")).map(line => Student(line(0).toLong, line(1), line(2), line(3))).toDF()

    //show默认只显示前20条
    studentDF.show
    studentDF.show(30)
    studentDF.show(30, false)

    studentDF.take(10).foreach(println)
    println(studentDF.first())
    studentDF.head(3).foreach(println)


    studentDF.select("passwd").show(30,false)


    studentDF.filter("name=''").show
    studentDF.filter("name='' OR name='NULL'").show


    //name以王开头的人
    studentDF.filter("SUBSTR(name,0,1)='王'").show

    studentDF.sort(studentDF("name")).show
    studentDF.sort(studentDF("name").desc).show

    studentDF.sort("name","id").show
    studentDF.sort(studentDF("name").asc, studentDF("id").desc).show

    studentDF.select(studentDF("name").as("student_name")).show


    val studentDF2 = rdd.map(_.split("\\|")).map(line => Student(line(0).toLong, line(1), line(2), line(3))).toDF()

    studentDF.join(studentDF2, studentDF.col("id") === studentDF2.col("id")).show

    spark.stop()
  }

  case class Student(id: Long, name: String, loginid: String, passwd: String)

}
