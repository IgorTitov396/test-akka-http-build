import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import net.ceedubs.ficus.Ficus._

import scala.concurrent.Future



object Client extends App {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val config = ConfigFactory.load()
  val serverAdress = config.getOrElse[String]("server.adress", "localhost")
  val serverPort = config.getOrElse[String]("server.port", "8080")

  val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = s"http://${serverAdress}:${serverPort}/random"))
  val a = responseFuture
    .map(
      println(_)
    ).recover {
    case e: Exception => println("Something went wrong")
  }.map(_ => system.terminate())
}
