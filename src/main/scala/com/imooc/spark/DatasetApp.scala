package com.imooc.spark

import org.apache.spark.sql.SparkSession

/**
 * Dataset操作
 */
object DatasetApp {

  def main(args: Array[String]) {
    val spark = SparkSession.builder().appName("DatasetApp")
      .master("local[2]").getOrCreate()

    //注意：需要导入隐式转换
    import spark.implicits._

    val path = "file:///home/wangc/software/files/student.csv"

    //spark如何解析csv文件？
    val df = spark.read.option("header","true").option("inferSchema","true").csv(path)
    df.show

    val ds = df.as[Student]
    ds.map(line => line.xm).show

    spark.stop()
  }

  case class Student(id:Long,xm:String,xsbh:String,xb:Int,nj:String)
}
