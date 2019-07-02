package org.apn.elasticsearch.migration.search

import java.io.File

import org.apn.elasticsearch.migration.factory.IndexStructureReader
import org.json.JSONObject

class SolrIndexStructureReader extends IndexStructureReader {
  def read(file: String): JSONObject = {
    val reader = new SolrStaticFieldsDefinitionReader(new File(file));
    reader.readSchema();
  }
}