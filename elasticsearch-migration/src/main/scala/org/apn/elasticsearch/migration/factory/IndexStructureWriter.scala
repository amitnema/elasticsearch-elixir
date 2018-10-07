package org.apn.elasticsearch.migration.factory

import org.json.JSONObject
import java.io.IOException

trait IndexStructureWriter {
  @throws(classOf[IOException])
  def write(solrConfJson: JSONObject, file: String)
}