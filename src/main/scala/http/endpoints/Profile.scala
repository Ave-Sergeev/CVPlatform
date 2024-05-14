package http.endpoints

import auth.AuthService
import http.handlers._
import service.profile.ProfileService
import storage.postgres.ProfileRepository
import zio.Scope
import zio.http._

import java.util.UUID

object Profile {
  def routes: HttpApp[AuthService with Scope with ProfileRepository] =
    Routes(
      Method.GET / "profile" -> handler { request: Request =>
        handleREST(request) {
          ProfileService.getAllProfiles
        }
      },
      Method.GET / "profile" / uuid("id") -> handler { (id: UUID, request: Request) =>
        handleREST(request) {
          ProfileService.getProfileById(id)
        }
      },
      Method.POST / "profile" -> handler { request: Request =>
        handleREST(request) {
          ProfileService.addProfile(request)
        }
      },
      Method.PUT / "profile" -> handler { request: Request =>
        handleREST(request) {
          ProfileService.updateProfile(request)
        }
      },
      Method.DELETE / "profile" / uuid("id") -> handler { (id: UUID, request: Request) =>
        handleREST(request) {
          ProfileService.deleteProfileById(id)
        }
      }
    )
      .handleError(exceptionHandler)
      .toHttpApp
}
