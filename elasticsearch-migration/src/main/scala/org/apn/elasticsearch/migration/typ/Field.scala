package org.apn.elasticsearch.migration.typ

import org.json.JSONObject

case class Field(
  val fieldType: String,
  val stored: Boolean,
  val name: String,
  val multiValued: Boolean,
  val indexed: Boolean,
  val required: Boolean,
  /*boost: Float,*/
  val omitNorms: Boolean /*,
  omitTermFrequencyAndPositions: Boolean*/ ) {

  def this(jsonObject: JSONObject) {
    this(
      jsonObject.optString("type"),
      jsonObject.optBoolean("stored"),
      jsonObject.optString("name"), jsonObject.optBoolean("multiValued"),
      jsonObject.optBoolean("indexed"), jsonObject.optBoolean("required"),
      /*jsonObject.optFloat("boost"), */ jsonObject.optBoolean("omitNorms") /*,
      jsonObject.optBoolean("omitTermFrequencyAndPositions")*/ )
  }

  override def toString() =
    " fieldType:" + fieldType +
      ", stored:" + stored +
      ", name	:" + name +
      ", multiValued:" + multiValued +
      ", indexed:" + indexed +
      ", required:" + required +
      ", omitNorms:" + omitNorms

}