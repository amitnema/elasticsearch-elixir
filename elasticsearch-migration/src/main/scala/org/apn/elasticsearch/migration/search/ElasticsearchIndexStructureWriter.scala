package org.apn.elasticsearch.migration.search

import java.io.FileWriter

import org.apn.elasticsearch.migration.factory.IndexStructureWriter
import org.apn.elasticsearch.migration.typ.Field
import org.json.{JSONArray, JSONObject}

class ElasticsearchIndexStructureWriter(val shards: Int, val replicas: Int) extends IndexStructureWriter {

  private val ANALYZER_COMMA = "comma"

  def write(solrConfJson: JSONObject, file: String): Unit = {
    val writer = new FileWriter(file)
    val indexJson = new JSONObject

    indexJson.put("mappings", new JSONObject().put("_doc", new JSONObject()
      .put("properties", getFields(solrConfJson))
      .put("dynamic_templates", getDynamicTemplates(solrConfJson))))

    indexJson
      .put("settings", new JSONObject()
        .put("index", new JSONObject()
          .put("query.default_field", defaultSearchField(solrConfJson))
          .put("similarity.default.type", "BM25")
          .put("number_of_shards", shards)
          .put("number_of_replicas", replicas))
        .put("analysis", new JSONObject()
          .put("analyzer", new JSONObject()
            .put(ANALYZER_COMMA, new JSONObject()
              .put("type", "custom")
              .put("tokenizer", "comma")))
          .put("tokenizer", new JSONObject()
            .put("comma", new JSONObject()
              .put("type", "pattern")
              .put("pattern", ",")))))

    writer.write(indexJson.toString(2))
    writer.flush
    writer.close
  }

  private def defaultSearchField(json: JSONObject) = json.getString("defaultSearchField")
  private def uniqueKey(json: JSONObject) = json.getString("uniqueKey")
  private def fieldType(json: JSONObject) = json.getJSONObject("types").getJSONArray("fieldType")

  private def getFields(solrConfJson: JSONObject): JSONObject = {
    getFieldAttributes(SolrStaticFieldsDefinitionReader.readFields(solrConfJson))
  }

  private def getDynamicTemplates(solrConfJson: JSONObject) = {
    getDynFieldAttributes(SolrStaticFieldsDefinitionReader.readDynamicFields(solrConfJson))
  }

  private def getFieldAttributes(fields: List[Field]): JSONObject = {
    val esField: JSONObject = new JSONObject
    fields.foreach(f => {
      val esFieldAttributes: JSONObject = new JSONObject
      esFieldAttributes
        .put("type", getFieldType(f.fieldType))
        .put("index", f.indexed)
        .put("store", f.stored)
      if (f.multiValued)
        esFieldAttributes.put("analyzer", ANALYZER_COMMA)
      if (f.omitNorms)
        esFieldAttributes.put("norms", f.omitNorms)

      esField.put(f.name, esFieldAttributes)
    })
    esField
  }

  private def getDynFieldAttributes(fields: List[Field]) = {
    var i: Int = 0
    val list = fields.map(f => {
      val esField: JSONObject = new JSONObject
      val esFieldAttributes: JSONObject = new JSONObject
      esFieldAttributes
        .put("match_mapping_type", "*")
        .put("match", f.name)
        .put("mapping", new JSONObject().put("type", getFieldType(f.fieldType)))
      esField.put("dynamic_templates_" + i, esFieldAttributes)
      i += 1
      esField
    })
    new JSONArray(list.toArray)
  }

  private def getFieldType(fieldType: String) = {
    fieldType match {
      case "int" => "integer"
      case "string" => "text"
      case "location" => "geo_point"
      case "location_rpt" => "geo_point"
      case _ => fieldType
    }
  }
}