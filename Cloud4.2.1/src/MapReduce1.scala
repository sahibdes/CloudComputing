import org.apache.spark
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import scala.collection.immutable.Map
import scala.reflect.io.File
import java.io._
import java.io.PrintWriter

object MapReduce1 {

  def main(args: Array[String]) {

    val sc = new SparkConf().setAppName("Page Rank")

    val scontext = new SparkContext(sc)
    val lines = scontext.textFile("s3n://s15-p42-part2/wikipedia_arcs")

    val links = lines.map { s =>
      val parts = s.split("\\s+")
      (parts(0), parts(1))
    }.distinct().groupByKey().persist()

    var rankList = links.mapValues(v => 1.0)

    rankList.map(f => (println(f._1, f._2)))

    for (i <- 1 to 10) {
      val contributions = links.join(rankList).values.flatMap {
        case (urls, rank) =>
          val size = urls.size

          urls.map(url => (url, rank / size))
      }

      val danglingRdd = contributions.subtract(rankList)
      // val danglingRddCalc = danglingRdd.map(f => (f._1, (f._2)))
      val countVal = rankList.count().doubleValue()
      val danglingFinal = danglingRdd.map(f => (f._1, f._2 / countVal))

      val danglingVal = danglingFinal.map(f => (f._2))
      val totalDangling = danglingVal.sum()

      rankList = contributions.reduceByKey(_ + _).mapValues((0.15) + 0.85 * _ + (totalDangling.toDouble))
    }

    val output = rankList
    val outputRdd = output

    val names = scontext.textFile("s3n://s15-p42-part2/wikipedia_mapping")
    val namesRdd = names.map { s =>
      val parts = s.split("\\s+")
      (parts(0), parts(1))
    }

    val checkVal = namesRdd.join(outputRdd)

    val finalVal = checkVal.map(f => (f._2))

    val finalDs = finalVal.sortBy { case (left, right) => (-right, left) }

    //finalDs.take(100).foreach(f => (println(f)))

    val file1 = "pagerank"
    val writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file1)))

    for (x <- finalDs.take(100)) {
      writer.write(x._1 + "\t" + x._2 + "\n")
    }
    writer.close()
  }
}