package org.apn.elasticsearch.migration

object ConfigurationType extends Enumeration {
  type ConfigurationType = Value
  val Solr, ES = Value
}