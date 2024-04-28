package http.api

import auth.AuthService
import http.handlers.ExceptionHandler.exceptionHandler
import service.profile.ProfileService
import storage.postgres.ProfileRepository
import zio.http.Middleware._
import zio.http._

import java.util.UUID

object Profile {
  def routes: HttpApp[AuthService with ProfileRepository] =
    Routes(
      Method.GET / "profile" -> handler {
        ProfileService.getAllProfiles
      },
      Method.GET / "profile" / uuid("id") -> handler { (id: UUID, _: Request) =>
        ProfileService.getProfileById(id)
      },
      Method.POST / "profile" -> handler { req: Request =>
        ProfileService.addProfile(req)
      },
      Method.PUT / "profile" -> handler { req: Request =>
        ProfileService.updateProfile(req)
      },
      Method.DELETE / "profile" / uuid("id") -> handler { (id: UUID, _: Request) =>
        ProfileService.deleteProfileById(id)
      }
    )
      .handleError(exceptionHandler)
      .toHttpApp @@ customAuthZIO(AuthService.validateAuth)
}
