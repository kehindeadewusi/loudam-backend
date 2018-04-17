package com.loudam.incidence.api

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.broker.kafka.{KafkaProperties, PartitionKeyStrategy}
import com.lightbend.lagom.scaladsl.api.transport.Method
import play.api.libs.json.{Format, Json}

object IncidenceService {

  val TOPIC_NAME = "incidence"
}

trait IncidenceService extends Service{

  def reportIncidence(): ServiceCall[IncidenceMessage, Done]

  def getIncidence(title:String): ServiceCall[NotUsed, IncidenceMessage]

  def incidenceTopic(): Topic[IncidenceAdded]

  override final def descriptor: Descriptor = {
    import Service._

    named(name="incidence")
      .withCalls(
        restCall(Method.GET, pathPattern = "/api/incidence/:title", getIncidence _),
        restCall(Method.POST, pathPattern = "/api/incidence/report", reportIncidence())
      )
      .withTopics(
        topic(IncidenceService.TOPIC_NAME, incidenceTopic())
          .addProperty(
            KafkaProperties.partitionKeyStrategy,
            PartitionKeyStrategy[IncidenceAdded](_.category)
          )
      )
      .withAutoAcl(autoAcl = true)
  }
}

case class IncidenceMessage(category:String, title:String, description:String)

object IncidenceMessage{
  implicit val format:Format[IncidenceMessage] = Json.format[IncidenceMessage]
}

case class IncidenceAdded(category:String)

object IncidenceAdded{
  implicit val format:Format[IncidenceAdded] = Json.format[IncidenceAdded]
}