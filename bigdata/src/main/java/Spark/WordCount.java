package Spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;
import utils.FileUtil;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @Author: LX
 * @Date: 2019/3/27 19:31
 * @Version: 1.0
 */
public class WordCount {

    private static final Pattern SPACE = Pattern.compile(" ");

    public static void main(String[] args) {
        // 1：创建SparkConf对象,设置相关配置信息
        SparkConf sparkConf = new SparkConf().setAppName("JavaWordCount");
        sparkConf.setMaster("local");

        // 2：创建JavaSparkContext对象，SparkContext是Spark的所有功能的入口
        JavaSparkContext ctx = new JavaSparkContext(sparkConf);

        // 3：创建一个初始的RDD
        // SparkContext中，用于根据文件类型的输入源创建RDD的方法，叫做textFile()方法
        JavaRDD<String> lines = ctx.textFile("D:\\Software\\spark-2.4.0-bin-hadoop2.7\\README.md", 1);
        // 4：对初始的RDD进行transformation操作，也就是一些计算操作
        StringBuilder sb = new StringBuilder();

        JavaRDD<String> words = lines.flatMap(
                (FlatMapFunction<String, String>) s -> Arrays.asList(SPACE.split(s)).iterator());

        JavaPairRDD<String, Integer> ones = words.mapToPair(
                (PairFunction<String, String, Integer>) s -> new Tuple2<>(s, 1));
        JavaPairRDD<String, Integer> counts = ones.reduceByKey(
                (Function2<Integer, Integer, Integer>) (i1, i2) -> i1 + i2);

        List<Tuple2<String, Integer>> output = counts.collect();

        // 5:将结果输出文件或者打印
        FileUtil util = new FileUtil();

        for (Tuple2<?, ?> tuple : output) {
//            System.out.println(tuple._1() + ": " + tuple._2());
            sb.append(tuple._1).append(':').append(tuple._2).append('\n');
        }
        util.writeString(sb.toString(),"d:/tmp/output/wordCount.txt");
        ctx.close();
    }
}
