import auth.AuthService
import http.HTTPServer
import storage.db.ProfileRepository
import zio.Scope

object Layers {

  private val runtime = Scope.default

  val all =
    runtime >+>
      AuthService.live >+>
      HTTPServer.live >+>
      ProfileRepository.live
}
