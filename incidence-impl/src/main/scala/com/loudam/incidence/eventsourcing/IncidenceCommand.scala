package com.loudam.incidence.eventsourcing

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.loudam.incidence.api._
import play.api.libs.json.{Format, Json}

import scala.collection.mutable


//commands
sealed trait IncidenceCommand[R] extends ReplyType[R]

case class AddIncidence(title:String, description:String, location:Location, tags: mutable.MutableList[String], files:Option[mutable.MutableList[String]]) extends IncidenceCommand[Done]

object AddIncidence{
  implicit val format:Format[AddIncidence] = Json.format
}

case class GetIncidence(title:String) extends IncidenceCommand[IncidenceMessage]

object  GetIncidence{
  implicit val format:Format[GetIncidence] = Json.format
}

