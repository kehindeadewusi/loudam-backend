package com.loudamstream.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import com.loudamstream.api.LoudamStreamService
import com.loudam.api.LoudamService
import com.softwaremill.macwire._

class LoudamStreamLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new LoudamStreamApplication(context) {
      override def serviceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new LoudamStreamApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[LoudamStreamService])
}

abstract class LoudamStreamApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer = serverFor[LoudamStreamService](wire[LoudamStreamServiceImpl])

  // Bind the LoudamService client
  lazy val loudamService = serviceClient.implement[LoudamService]
}
