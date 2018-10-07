package org.apn.elasticsearch.spark.client

import scala.reflect.runtime.universe.TypeTag

import org.apache.spark.sql.Encoder
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.catalyst.ScalaReflection
import org.apache.spark.sql.types.StructType
import org.elasticsearch.spark.rdd.EsSpark
/**
 * @author amit.nema
 *
 */
class ESIndexData[T: Encoder: TypeTag](spark: SparkSession) {
  /**
   * @param path : path to csv file
   * @param delimiter : field separator
   * @param indexType : index & type in the format as indexName/type (e.g. library/books, library/cds)
   */
  def saveCsvToEs(path: String, delimiter: String, indexType: String) {
    //  create rdd
    val typedRdd = spark.read
      .option("inferSchema", true)
      .option("delimiter", delimiter)
      .schema(caseClassToSchema[T])
      .csv(path).as[T].rdd
    EsSpark.saveToEs(typedRdd, indexType)
  }

  def caseClassToSchema[T: TypeTag] = ScalaReflection.schemaFor[T].dataType.asInstanceOf[StructType]
}