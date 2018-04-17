package com.loudamstream.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.loudamstream.api.LoudamStreamService
import com.loudam.api.LoudamService

import scala.concurrent.Future

/**
  * Implementation of the LoudamStreamService.
  */
class LoudamStreamServiceImpl(loudamService: LoudamService) extends LoudamStreamService {
  def stream = ServiceCall { hellos =>
    Future.successful(hellos.mapAsync(8)(loudamService.hello(_).invoke()))
  }
}
