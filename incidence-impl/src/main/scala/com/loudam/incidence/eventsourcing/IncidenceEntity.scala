package com.loudam.incidence.eventsourcing

import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, PersistentEntity}
import play.api.libs.json.{Format, Json}
import akka.Done
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import com.loudam.incidence.api.{IncidenceMessage,Location}
import scala.collection.immutable.Seq
import  scala.collection.mutable.MutableList

class IncidenceEntity extends PersistentEntity{
  override type Command = IncidenceCommand[_]
  override type Event = IncidenceEvent
  override type State = Incidence

  override def initialState: Incidence =  Incidence(title="", description="", Location(0,0), tags= MutableList.empty, files= None)

  override def behavior: Behavior = {

    case Incidence(title, description, Location(longitude,latitude), tags, files) => Actions()
    
    .onCommand[AddIncidence, Done] {

      case (AddIncidence(title, description, Location(longitude,latitude), tags, files), ctx, state) =>
        ctx.thenPersist(
          IncidenceAdded(title, description, Location(longitude,latitude), tags, files)
        ) { _ =>   ctx.reply(Done)     }
     }
    .onReadOnlyCommand[GetIncidence, IncidenceMessage] {
      case (GetIncidence(title), ctx, state) =>
        ctx.reply(IncidenceMessage(title, description, Location(longitude,latitude), tags, files))}

//     .onReadOnlyCommand[showIncidence, Incidence] {
//       case (showIncidence(), ctx, state) =>
//         ctx.reply(Incidence(category, title, description))}

    .onEvent {
      case (IncidenceAdded(title, description, Location(longitude,latitude), tags, files), state) =>
        // update the current state
        Incidence(title, description, Location(longitude,latitude), tags, files)
    }
  }

}




object IncidenceSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[AddIncidence],
    JsonSerializer[GetIncidence],
    JsonSerializer[IncidenceAdded],
    JsonSerializer[Incidence]
  )
}