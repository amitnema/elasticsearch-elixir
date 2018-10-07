package org.apn.elasticsearch.migration.client

import org.apn.elasticsearch.migration.ArgumentsParser
import org.apn.elasticsearch.migration.ConfigurationType
import org.apn.elasticsearch.migration.factory.IndexFileReaderFactory
import org.apn.elasticsearch.migration.factory.IndexFileWriterFactory
import java.nio.file.Path
import java.nio.file.Paths
import java.io.File

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