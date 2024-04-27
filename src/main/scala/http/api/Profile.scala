package http.api

import auth.AuthService
import service.db.DBService
import service.profile.ProfileService
import zio.http.Middleware._
import zio.http._

import java.util.UUID

object Profile {
  def routes: HttpApp[AuthService with DBService] =
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
      .handleError(err => Response.badRequest(err.getMessage))
      .toHttpApp @@ customAuthZIO(AuthService.validateAuth)
}
