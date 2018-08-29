package com.loudam.incidence.impl

import com.loudam.incidence.eventsourcing._
import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}
import com.loudam.incidence.api.{IncidenceMessage,Location, IncidenceService}

class IncidenceServiceImpl(persistentEntityRegistry:PersistentEntityRegistry) extends IncidenceService {

  override def getIncidence(title: String): ServiceCall[NotUsed, IncidenceMessage] = ServiceCall { _ =>
    val ref = persistentEntityRegistry.refFor[IncidenceEntity](title)        
    ref.ask(GetIncidence(title))
  }

  // override def showAll : ServiceCall[NotUsed, IncidenceMessage] = ServiceCall { _ =>  
  //   val ref = persistentEntityRegistry.refFor[IncidenceEntity]("title")
  //   ref.ask(showIncidence)
  // }

  override def reportIncidence(): ServiceCall[IncidenceMessage, Done] = ServiceCall { request =>
    val ref = persistentEntityRegistry.refFor[IncidenceEntity](request.title) 

    ref.ask(AddIncidence(request.title, request.description, request.location, request.tags, request.files))
    // ref(request.title).ask(AddIncidence(request.title, request.description, request.location, request.tags, request.files)).map {
    //     case Done => s"Title: ${request.title}! has been Added."
    //   }
  }  
}
