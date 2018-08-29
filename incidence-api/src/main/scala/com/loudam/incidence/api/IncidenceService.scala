package com.loudam.incidence.api

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall, ServiceAcl}
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.broker.kafka.{KafkaProperties, PartitionKeyStrategy}
import com.lightbend.lagom.scaladsl.api.transport.Method
import play.api.libs.json.{Format, Json}
import  scala.collection.mutable.MutableList


trait IncidenceService extends Service{

// curl -H "Content-Type: application/json" -X POST -d  '{"title" : "Tanker","description" : "Fire on water", "location":{"longitude":12.98,"latitude":13.96}, "tags":["Fire","Smoke"], "files" : [] }' http://localhost:9000/api/incidence/report
  def reportIncidence(): ServiceCall[IncidenceMessage, Done]

// curl http://localhost:9000/api/incidence/Tanker
  def getIncidence(title:String): ServiceCall[NotUsed, IncidenceMessage]

// curl http://localhost:9000/api/incidence
  // def showAll : ServiceCall[NotUsed,IncidenceMessage]


  override final def descriptor: Descriptor = {
    import Service._
    named(name="incidence")
      .withCalls(
        restCall(Method.GET, pathPattern = "/api/incidence/:title", getIncidence _),
        // restCall(Method.GET, pathPattern = "/api/incidence", showAll),
        restCall(Method.POST, pathPattern = "/api/incidence/report", reportIncidence _)
      )
      // .withAutoAcl(true)
      .withAcls(         
        ServiceAcl(pathRegex = Some("/api/incidence/file"))      
      )
  }
}

case class Location(longitude:Double, latitude:Double)
object Location{
  implicit val format:Format[Location] = Json.format[Location]
}

case class IncidenceMessage(title:String, description:String, location:Location, tags: MutableList[String], files:Option[MutableList[String]])
object IncidenceMessage{
  implicit val format:Format[IncidenceMessage] = Json.format[IncidenceMessage]
}