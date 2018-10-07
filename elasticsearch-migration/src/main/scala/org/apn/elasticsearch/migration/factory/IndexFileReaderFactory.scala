package org.apn.elasticsearch.migration.factory

import org.apn.elasticsearch.migration.ConfigurationType
import org.apn.elasticsearch.migration.ConfigurationType._
import org.apn.elasticsearch.migration.search.SolrIndexStructureReader

object IndexFileReaderFactory {
  def getReader(confType: ConfigurationType): IndexStructureReader = {
    confType match {
      case ConfigurationType.Solr => new SolrIndexStructureReader()
      case _ => throw new IllegalArgumentException("ConfigurationType " + confType + " is not exist.")
    }
  }
}