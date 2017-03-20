package com.jmartinez.spark.examples

import scala.util.Random

import org.apache.log4j.{Level, Logger}

import org.apache.spark.{Partitioner, SparkConf, SparkContext}

case class Foo(id: String, id2: String, seq: Int, data: String)

case class FooKey(id: String, id2: String, seq: Int)

object FooKey {
  implicit def orderingByIdAndSeq[A <: FooKey]: Ordering[A] =
    Ordering.by(f => (f.id, f.seq))
}

class CustomPartitioner(partitions: Int) extends Partitioner {
  override def numPartitions: Int = partitions

  override def getPartition(key: Any): Int = {
    val k = key.asInstanceOf[FooKey]
    scala.math.abs((k.id, k.id2).hashCode() % numPartitions)
  }
}

object SortingSpark {

  def main(args: Array[String]): Unit = {
    // Disable INFO Log
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)

    val conf = new SparkConf().setAppName("spark-example").setMaster("local")
    val sc = new SparkContext(conf)

    val list = generateFoo(
      50,
      List(("key1", "2"),
        ("key1", "1"),
        ("key1", "4"),
        ("key2", "3"),
        ("key2", "2"),
        ("key3", "1"),
        ("key3", "zzz"))
    )

    sc.parallelize(list, 5)
      .keyBy(f => FooKey(f.id, f.id2, f.seq))
      .repartitionAndSortWithinPartitions(new CustomPartitioner(5))
      .mapPartitionsWithIndex {
        case (index, it) =>
          it.foreach(f => println(s"$index $f}")) // to see the ordering within each partition
          it
      }
      .count()
  }

  def generateFoo(numItems: Int, keys: List[(String, String)]): List[Foo] =
    keys.flatMap {
      case (id1, id2) =>
        Range(0, numItems).map { item =>
          Foo(id1, id2, Random.nextInt(100), Random.nextInt(1000).toString)
        }
    }

  def randomString(length: Int): String =
    scala.util.Random.alphanumeric.take(length).mkString

}
