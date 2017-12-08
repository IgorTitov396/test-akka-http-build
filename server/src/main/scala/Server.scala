import akka.actor.ActorSystem
import akka.event.{LogSource, Logging}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.util.ByteString
import akka.http.scaladsl.server.Directives._
import com.typesafe.config.ConfigFactory
import net.ceedubs.ficus.Ficus._

import scala.io.StdIn
import scala.util.Random

object ServerLogging {
  implicit val logSource: LogSource[Server.type] = new LogSource[Server.type] {
    //def genString(o: Server.type): String = o.getClass.getName
    override def getClazz(o: Server.type): Class[_] = o.getClass
  }
}

object Server extends App {
  import ServerLogging.logSource
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher

  val log = Logging(system, this)
  // streams are re-usable so we can define it here
  // and use it for every request
  val numbers = Source.fromIterator(() =>
    Iterator.continually(Random.nextInt()))

  val route =
    path("random") {
      get {
        log.info("Get request")
        complete(
          HttpEntity(
            ContentTypes.`text/plain(UTF-8)`,
            // transform each number to a chunk of bytes
            numbers.map(n => ByteString(s"$n\n"))
          )
        )
      }
    }

  val config = ConfigFactory.load()
  val serverAdress = config.getOrElse[String]("server.adress", "localhost")
  val serverPort = config.getOrElse[String]("server.port", "8080")

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
  println(s"Server online at http://${serverAdress}:${serverPort}/\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done

}
