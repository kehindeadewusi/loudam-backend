package com.loudam.incidence.controller

import java.io.File

import akka.stream.scaladsl.{ FileIO, Sink }
import akka.stream.{ IOResult, Materializer }
import akka.util.ByteString
import play.api.Logger
import play.api.libs.streams.Accumulator
import play.api.mvc.MultipartFormData.FilePart
import play.api.mvc._
import play.core.parsers.Multipart.{ FileInfo, FilePartHandler }

import scala.concurrent.{ ExecutionContext, Future }

// Controller Class
class ImageUpdateController(controllerComponents: play.api.mvc.ControllerComponents)(implicit mat: Materializer,
                            exCtx: ExecutionContext) extends AbstractController(controllerComponents) {
  private val logger = Logger(this.getClass)

  // this is Play's Action
  def uploadFile(): Action[MultipartFormData[File]] = 
    Action.async(parse.multipartFormData(fileHandler)) { request: Request[MultipartFormData[File]] =>
      val files = request.body.files     
//      println(request.image)
     Future.successful(Ok(files.map(_.ref.getAbsolutePath) mkString("Uploaded[", ", ", "]")))      
    }
  
  private def fileHandler: FilePartHandler[File] = {
    case FileInfo(partName, filename, contentType) => {
      val tempFile = {
        // create a temp file in the `target` folder       
        val f = new java.io.File("./target/file-upload-data/uploads", filename).getAbsoluteFile
        // make sure the subfolders inside `target` exist.
        f.getParentFile.mkdirs()                
        f        
      }
      val sink: Sink[ByteString, Future[IOResult]] = FileIO.toPath(tempFile.toPath)
      val acc: Accumulator[ByteString, IOResult] = Accumulator(sink)
      acc.map {
        case akka.stream.IOResult(bytesWriten, status) =>
          FilePart(partName, filename, contentType, tempFile)          
      }
    }
  }
}