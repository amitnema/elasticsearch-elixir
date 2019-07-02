package org.apn.elasticsearch.migration

import org.apache.commons.cli.{DefaultParser, HelpFormatter, Option, Options}
import org.apn.elasticsearch.migration.bean.Arguments

object ArgumentsParser {

  val OPTIONS = new Options()

  val INPUT_FILE = "inputFile"

  val INDEX_NAME = "indexName"
  val SHARDS = "shards"
  val REPLICAS = "replicas"

  OPTIONS.addOption(Option.builder("inputFile").argName(INPUT_FILE).hasArg()
    .desc("Configuration file to read").required().build())
  OPTIONS.addOption(Option.builder(INDEX_NAME).argName(INDEX_NAME).hasArg()
    .desc("Index name to be created.").build())
  OPTIONS.addOption(Option.builder(SHARDS).argName(SHARDS).hasArg()
    .desc("Number of shards to be created.").build())
  OPTIONS.addOption(Option.builder(REPLICAS).argName(REPLICAS).hasArg()
    .desc("Number of replicas to be created.").build())

  def parse(args: Array[String]): Arguments = {
    var arguments: Arguments = null
    val parser = new DefaultParser()
    try {
      val commandLine = parser.parse(OPTIONS, args)
      arguments = new Arguments(
        commandLine.getOptionValue(INPUT_FILE),
        commandLine.getOptionValue(SHARDS, "5").toInt,
        commandLine.getOptionValue(REPLICAS, "1").toInt)
    } catch {
      case e: Throwable => {
        System.err.println("Error during starup: " + e.getMessage())
        printHelp()
      }
    }
    arguments
  }

  /**
   * Prints usage.
   */
  def printHelp() {
    val helpFormater = new HelpFormatter()
    helpFormater.printHelp("java -jar search-engine-migration.jar", OPTIONS, true)
  }

}