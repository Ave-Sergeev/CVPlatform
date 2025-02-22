package http.endpoints

import auth.AuthService
import http.handlers._
import service.profile.ProfileService
import storage.postgres.ProfileRepository
import zio.Scope
import zio.http._

import java.util.UUID

object Profile {

  val routes: Routes[ProfileRepository with AuthService with Scope, Nothing] =
    Routes(
      Method.GET / "profile" -> handler { request: Request =>
        handleREST("GET /profile", request) {
          ProfileService.getAllProfiles
        }
      },
      Method.GET / "profile" / uuid("id") -> handler { (id: UUID, request: Request) =>
        handleREST("GET /profile/:id", request) {
          ProfileService.getProfileById(id)
        }
      },
      Method.POST / "profile" -> handler { request: Request =>
        handleREST("POST /profile", request) {
          ProfileService.addProfile(request)
        }
      },
      Method.PUT / "profile" -> handler { request: Request =>
        handleREST("PUT /profile", request) {
          ProfileService.updateProfile(request)
        }
      },
      Method.DELETE / "profile" / uuid("id") -> handler { (id: UUID, request: Request) =>
        handleREST("DELETE /profile/:id", request) {
          ProfileService.deleteProfileById(id)
        }
      }
    )
      .handleError(exceptionHandler)
}
