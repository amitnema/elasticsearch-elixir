package org.apn.elasticsearch.migration.factory

import org.apn.elasticsearch.migration.ConfigurationType
import org.apn.elasticsearch.migration.ConfigurationType.ConfigurationType
import org.apn.elasticsearch.migration.search.ElasticsearchIndexStructureWriter

object IndexFileWriterFactory {
  def getWriter(confType: ConfigurationType, shards: Int, replicas: Int): IndexStructureWriter = {
    confType match {
      case ConfigurationType.ES => new ElasticsearchIndexStructureWriter(shards, replicas)
      case _ => throw new IllegalArgumentException("ConfigurationType " + confType + " is not exist.")
    }
  }
}