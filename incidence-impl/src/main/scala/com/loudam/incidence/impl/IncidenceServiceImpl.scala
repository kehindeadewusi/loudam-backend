package com.loudam.incidence.impl

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}
import com.loudam.incidence.api
import com.loudam.incidence.api.{IncidenceMessage, IncidenceService}

class IncidenceServiceImpl(persistentEntityRegistry:PersistentEntityRegistry) extends IncidenceService {

  override def getIncidence(title: String): ServiceCall[NotUsed, IncidenceMessage] = ServiceCall { _ =>
    val ref = persistentEntityRegistry.refFor[IncidenceEntity](title)
    ref.ask(GetIncidence(title))
  }

  override def reportIncidence(): ServiceCall[IncidenceMessage, Done] = ServiceCall { request =>
    val ref = persistentEntityRegistry.refFor[IncidenceEntity](request.title)
    ref.ask(AddIncidence(request.category, request.title, request.description))
  }

  override def incidenceTopic(): Topic[api.IncidenceAdded] =
    TopicProducer.singleStreamWithOffset({
      fromOffset =>
        persistentEntityRegistry.eventStream(IncidenceEvent.Tag, fromOffset)
          .map(ev => (convertEvent(ev), ev.offset))
    })

  private def convertEvent(event: EventStreamElement[IncidenceEvent]): api.IncidenceAdded = {
    event.event match {
      case IncidenceAdded(c, t, d) => api.IncidenceAdded(c)
    }
  }
}
