import org.apache.spark.{SparkConf, SparkContext}

object Test {

  def main(args: Array[String]): Unit = {
    //创建SparkConf()并设置App名称
    val conf = new SparkConf().setMaster("120.78.160.135:4040").setAppName("WC")
    //创建SparkContext，该对象是提交spark App的入口
    val sc = new SparkContext(conf)
    //使用sc创建RDD并执行相应的transformation和action
    var row = sc.textFile("hdfs://120.78.160.135:9000/lianjia/sz/20190707/chegongmiao.csv")
    var data = row.map(_.split(","))
      .map(x => (x(4), x(5).split("\\|")(2).trim))
      .map(x => (x._1.substring(0, x._1.length() - 1).toInt, x._2.substring(0, x._2.length() - 2).toDouble))
      .map(x => x._1 / x._2)
    data.mean
    //停止sc，结束该任务
    sc.stop()

  }

}
