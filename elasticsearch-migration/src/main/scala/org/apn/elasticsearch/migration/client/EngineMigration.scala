package org.apn.elasticsearch.migration.client

import java.io.File

import org.apn.elasticsearch.migration.factory.{IndexFileReaderFactory, IndexFileWriterFactory}
import org.apn.elasticsearch.migration.{ArgumentsParser, ConfigurationType}

object EngineMigration extends App {

  run(args)

  def run(args: Array[String]) = {
    val arguments = ArgumentsParser.parse(args)
    if (arguments != null) {
      val reader = IndexFileReaderFactory.getReader(ConfigurationType.Solr)
      val writer = IndexFileWriterFactory.getWriter(ConfigurationType.ES, arguments.shards, arguments.replicas)
      writer.write(reader.read(arguments.inputFileName), arguments.inputFileName + ".json")
      println("Please find the ElasticSearch conf file here: " + new File(arguments.inputFileName).getAbsolutePath + ".json")
    }
  }
}