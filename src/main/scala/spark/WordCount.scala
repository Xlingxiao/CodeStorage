package spark

import org.apache.spark.{SparkConf, SparkContext}


object WordCount {

  def main(args: Array[String]): Unit = {
    /*val conf = new SparkConf().setMaster("spark://172.16.2.106:7077")
      .setAppName("WordCount")
      .setJars(Array("F:\\lx\\Code\\CodeStorage\\target\\CodeStorage-1.0-SNAPSHOT.jar"))
      .setIfMissing("spark.driver.host","172.16.2.56")*/
    //本地运行
    val conf = new SparkConf().setMaster("local[*]").setAppName("WordCount")
    //实例化spark上下文
    val sc = new SparkContext(conf)
    //读文件
    val textFile = sc.textFile("hdfs://172.16.2.106:8020/zx/spark/wordCount/words.txt")
    val result = textFile.flatMap(_.split(" ")).map((_,1)).reduceByKey(_ + _,1)
    result.collect().foreach(x => print(s"$x "))
    println()
    val sortResult = result.sortBy(x => x._2,ascending = false)
    sortResult.collect().foreach(x => print(s"$x "))
    sortResult.saveAsTextFile("F:\\lx\\Code\\CodeStorage\\target/result")
    sc.stop()
  }
}
