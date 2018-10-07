package org.apn.elasticsearch.migration.factory

import org.json.JSONObject
import java.io.IOException

trait IndexStructureReader {
  @throws(classOf[IOException])
  def read(file: String): JSONObject
}