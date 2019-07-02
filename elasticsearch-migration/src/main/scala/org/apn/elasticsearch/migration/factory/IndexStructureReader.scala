package org.apn.elasticsearch.migration.factory

import java.io.IOException

import org.json.JSONObject

trait IndexStructureReader {
  @throws(classOf[IOException])
  def read(file: String): JSONObject
}