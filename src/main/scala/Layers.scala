import auth.AuthService
import http.HTTPServer
import service.db.DBService
import zio.Scope

object Layers {

  private val runtime = Scope.default

  val all =
    runtime >+>
      AuthService.live >+>
      HTTPServer.live >+>
      DBService.live
}
