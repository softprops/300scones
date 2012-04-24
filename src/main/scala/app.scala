object App {
  import unfiltered._
  import unfiltered.response.{ ResponseString, JsonContent }
  import unfiltered.request.{ Path, Seg }
  import netty._

  def main(args: Array[String]) {
    lazy val f = Env("FLICKR_KEY") match {
      case Some(k) => Flickr(k)
      case _ => sys.error("FLICKR_KEY could not be resolved")
    }
    Http(Env("PORT").getOrElse("8080").toInt)
      .resources(getClass.getResource("/www/index.html"))
      .handler(cycle.Planify {
        case Path(Seg("scones.json" :: Nil)) =>
          JsonContent ~> ResponseString(f.scones)
      }).beforeStop {
        f.shutdown
      }
      .run()
  }
}

object Env {
  def apply(name: String) = Option(System.getenv(name))
}

case class Flickr(key: String) {
  import dispatch._
  private lazy val http = new Http
  val JSONWrap = """jsonFlickrApi\((.+)\)""".r
  def unwrap(raw: String) = raw match {
    case JSONWrap(json) => json
    case json => json
  }
    
  private def api = url("http://api.flickr.com/services/rest/") <<? Map(
    "api_key"-> key,
    "format" -> "json"
  )
  def search(term: String, pp: Int = 300) =
    api <<? Map("method"   -> "flickr.photos.search",
                "text"     -> term,
                "per_page" -> pp.toString)
  def scones =
    unwrap(http(search("scone") > As.string)())

  def shutdown = http.shutdown
}
