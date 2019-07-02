package org.apn.elasticsearch.migration.factory

import java.io.IOException

import org.json.JSONObject

trait IndexStructureWriter {
  @throws(classOf[IOException])
  def write(solrConfJson: JSONObject, file: String)
}