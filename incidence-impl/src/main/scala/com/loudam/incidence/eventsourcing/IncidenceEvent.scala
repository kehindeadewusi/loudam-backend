package com.loudam.incidence.eventsourcing

import com.loudam.incidence.api.Location
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventShards, AggregateEventTag, AggregateEventTagger}
import play.api.libs.json.{Format, Json}


// sealed trait IncidenceEvent extends AggregateEvent[IncidenceEvent]{
//   def aggregateTag:AggregateEventTag[IncidenceEvent] = IncidenceEvent.Tag
// }

sealed trait IncidenceEvent extends AggregateEvent[IncidenceEvent] {
override def aggregateTag: AggregateEventTagger[IncidenceEvent] = IncidenceEvent.Tag
}


object IncidenceEvent {
  val numberOfShards = 4
  val Tag: AggregateEventShards[IncidenceEvent] = AggregateEventTag.sharded[IncidenceEvent](numberOfShards)
}


// object IncidenceEvent {
//   val Tag = AggregateEventTag[IncidenceEvent]
// }

case class IncidenceAdded(title:String, description:String, location:Location, tags: List[String], files:Option[List[String]]) extends IncidenceEvent

object IncidenceAdded {
  implicit val format:Format[IncidenceAdded] = Json.format[IncidenceAdded]
}
