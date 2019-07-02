package org.apn.elasticsearch.spark.client

import java.util.concurrent.TimeUnit

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.elasticsearch.hadoop.EsHadoopIllegalStateException
import org.elasticsearch.hadoop.cfg.ConfigurationOptions
import org.scalatest.{BeforeAndAfterAll, FunSuite}

/**
 * @author amit.nema
 */

object ESIndexDataTest {
  private val InPath = "target/test-classes/books.dat"
  private val IndexType = "library/books"
  private[this] val Id = "isbn"
  private[this] val map = Map(ConfigurationOptions.ES_MAPPING_ID -> Id)

  @transient private[this] var conf: SparkConf = null;
  @transient private var spark: Option[SparkSession] = None;

  def beforeAll() = {
    conf = new SparkConf()
      .setAll(map)
      .setAppName(this.getClass.getName)
      .setMaster("local[*]")
    spark = Some(SparkSession.builder().config(conf).getOrCreate())
  }

  def afterAll() {
    spark.foreach((s: SparkSession) => {
      s.close()
      Thread.sleep(TimeUnit.SECONDS.toMillis(3))
    })
  }

}

class ESIndexDataTest extends FunSuite with BeforeAndAfterAll {

  override def beforeAll() = { ESIndexDataTest.beforeAll() }

  override def afterAll() = { ESIndexDataTest.afterAll() }

  test("ESIndexData.saveCsvToEs") {
    val spark: SparkSession = ESIndexDataTest.spark
      .getOrElse(throw new EsHadoopIllegalStateException("Spark not started..."))

    import spark.implicits._
    val esi = new ESIndexData[Books](spark)
    esi.saveCsvToEs(ESIndexDataTest.InPath, ";", ESIndexDataTest.IndexType)
  }
}

private case class Books(isbn: String, bookTitle: Option[String], bookAuthor: Option[String], yearOfPublication: Option[Long],
  publisher: Option[String], imageUrlS: Option[String], imageUrlM: Option[String],
  imageUrlL: Option[String])