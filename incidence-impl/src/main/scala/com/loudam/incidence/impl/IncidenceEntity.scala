package com.loudam.incidence.impl

import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, PersistentEntity}
import play.api.libs.json.{Format, Json}
import akka.Done
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import com.loudam.incidence.api.IncidenceMessage
import scala.collection.immutable.Seq

class IncidenceEntity extends PersistentEntity{
  override type Command = IncidenceCommand[_]
  override type Event = IncidenceEvent
  override type State = Incidence

  override def initialState: Incidence = Incidence()

  override def behavior: Behavior = {
    case Incidence(category, title, description) => Actions().onCommand[AddIncidence, Done] {

      case (AddIncidence(c, t, d), ctx, state) =>
        ctx.thenPersist(
          IncidenceAdded(c, t,d)
        ) { _ =>
          // Then once the event is successfully persisted, we respond with done.
          ctx.reply(Done)
        }

    }.onReadOnlyCommand[GetIncidence, IncidenceMessage] {

      // Command handler for the Hello command
      case (GetIncidence(title), ctx, state) =>

        ctx.reply(IncidenceMessage(category, title, description))

    }.onEvent {
      case (IncidenceAdded(c, t, d), state) =>
        // update the current state
        Incidence(c, t, d)

    }
  }

}

case class Incidence(category:String="General", title:String="", description:String="")

object Incidence{
  implicit val format:Format[Incidence] = Json.format[Incidence]
}

sealed trait IncidenceEvent extends AggregateEvent[IncidenceEvent]{
  def aggregateTag:AggregateEventTag[IncidenceEvent] = IncidenceEvent.Tag
}

object IncidenceEvent {
  val Tag = AggregateEventTag[IncidenceEvent]
}

case class IncidenceAdded(category:String, title:String, description:String) extends IncidenceEvent

object IncidenceAdded {
  implicit val format:Format[IncidenceAdded] = Json.format[IncidenceAdded]
}

//commands
sealed trait IncidenceCommand[R] extends ReplyType[R]

case class AddIncidence(category:String, title:String, description:String) extends IncidenceCommand[Done]

object AddIncidence{
  implicit val format:Format[AddIncidence] = Json.format
}

case class GetIncidence(title:String) extends IncidenceCommand[IncidenceMessage]

object  GetIncidence{
  implicit val format:Format[GetIncidence] = Json.format
}

object IncidenceSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[AddIncidence],
    JsonSerializer[GetIncidence],
    JsonSerializer[IncidenceAdded],
    JsonSerializer[Incidence]
  )
}