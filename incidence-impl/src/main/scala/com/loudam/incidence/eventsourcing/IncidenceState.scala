package com.loudam.incidence.eventsourcing

import com.loudam.incidence.api.Location
import play.api.libs.json.{Format, Json}

case class Incidence(title:String, description:String, location:Location, tags: List[String], files:Option[List[String]])
object Incidence{
  implicit val format:Format[Incidence] = Json.format[Incidence]
}
