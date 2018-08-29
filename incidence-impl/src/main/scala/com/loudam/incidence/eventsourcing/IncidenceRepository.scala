// package com.loudam.incidence.eventsourcing

// import akka.Done
// import com.datastax.driver.core.{BoundStatement, PreparedStatement}
// import com.loudam.incidence.api.{IncidenceMessage, Location}
// import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraSession
// import collection.mutable._

// import scala.concurrent.{ExecutionContext, Future}

// class IncidenceRepository(session: CassandraSession)(implicit ec: ExecutionContext) {

//   var IncidenceStatement: PreparedStatement = _

//   def createTable(): Future[Done] = {
//     session.executeCreateTable(
//       """
//         |CREATE TABLE IF NOT EXISTS incidence(
//         |title varchar(25) PRIMARY KEY,
//         |description text,
//         |location map<text,double>,
//         |tags set<text>,
//         |files set<text>
//         |);
//       """.stripMargin)

//       session.executeCreateTable(
//       """
//         |CREATE INDEX IF NOT EXISTS
//         |title_index ON incidence (title);
//       """.stripMargin)
//   }

//   def createPreparedStatements: Future[Done] = {
//     for{
//       incidencePreparedStatement <- session.prepare("INSERT INTO incidence(title,description,location,tags,files) VALUES (?, ?, ?, ?, ?)")
//     } yield{
//       IncidenceStatement = incidencePreparedStatement
//       Done
//     }
//   }  

// // java.util.Map("longitude" -> incidence.location.longitude) 


//   def storeIncidence(incidence: IncidenceMessage): Future[List[BoundStatement]] = {
//     // val m1:Map[String, Double] = new HashMap()
//     // val m2= m1.put("longitude", incidence.location.longitude)


//     val IncidenceBindStatement = IncidenceStatement.bind()
//     IncidenceBindStatement.setString("title", incidence.title)
//     IncidenceBindStatement.setString("description", incidence.title)
//     //IncidenceBindStatement.setMap("location", HashMap("longitude" -> incidence.location.longitude, "latitude" -> incidence.location.latitude))
//     IncidenceBindStatement.setList("tags",  incidence.tags)
//     IncidenceBindStatement.setList("files", incidence.files)    
//     Future.successful(List(IncidenceBindStatement))
//   }

//   def getAnIncidence(Title: String): Future[Option[IncidenceMessage]] =
//     session.selectOne(s"SELECT * FROM incidence WHERE title = '$Title'").map{optRow =>
//       optRow.map{row =>
//         val title = row.getString("title")
//         val description = row.getString("description")
//         val location = row.getMap("location")
//         val tags = row.getList("tags")
//         val files = row.getList("files")
//         IncidenceMessage(title, description, Location, tags, files)
//       }
//     }

// }
