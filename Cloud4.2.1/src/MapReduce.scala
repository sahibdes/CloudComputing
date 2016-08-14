import org.apache.spark
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import scala.collection.immutable.Map
import scala.reflect.io.File
import java.io._
import java.io.PrintWriter

object MapReduce {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("Spark Pi")
    val spark = new SparkContext(conf)

    val file = spark.textFile("s3n://s15-p42-part1-easy/data/")

    //title
    val tsv6 = file.map(_.replaceAll("\\<.*\\>", "").replaceAll("\\n", " ").toLowerCase().split("\t")(1))

    //title,text
    val tsv7 = file.map(x => (x.split("\t")(1), x.split("\t")(3).replaceAll("<[^>]+>", "").replace("\\n", " ").toLowerCase().replaceAll("[^a-z]", " ").replaceAll("\\s+", " ")))

    val tsv8 = tsv7.flatMap(line => (line._2.split(" ").map(word => ((word.trim(), line._1.trim()), 1))))

    //reduce by key per document tf
    val tsv9 = tsv8.reduceByKey(_ + _)

    val tsv10 = tsv9.map(value => (value._1._1, 1))

    //reduce by key for all document d_word
    val tsv11 = tsv10.reduceByKey(_ + _)

    val N = tsv7.distinct().count().doubleValue()

    //u is the number of documents word appears;v count of word per document
    val idf = tsv11.join(tsv9.map { case ((t, w), u) => (t, (w, u)) }).map { case (t, (v, (w, u))) => ((t, w), (u, v)) }

    idf.map(f => (println(f._1, f._2)))

    val tfidf = idf.map(f => (f._1, Math.log(N / f._2._2) * f._2._1))

    // tfidf.foreach(f => (println(f._1, f._2)))

    val cloudVal = tfidf.filter(p => (p._1._1.equals("cloud")))
    val finalDs = cloudVal.sortBy { case (left, right) => (-right, left._2) }

    finalDs.take(100).map(f => (println(f._1, f._2)))

    val file1 = "tfidf"
    val writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file1)))

    for (x <- finalDs.toArray()) {
      writer.write(x._1._2 + "\t" + x._2 + "\n")
    }
    writer.close()
  }
}
