package http

import auth.AuthService
import auth.models._
import exception.Exceptions._
import metrics.Counters.countRESTRequests
import util.ulid.ULID
import zio.http.{Request, Response}
import zio.{RIO, Scope, ZIO, ZIOAspect}

package object handlers {

  def handleREST[R, E <: Throwable, A](
      path: String,
      request: Request
  )(
      effect: ZIO[R, E, A]
  ): RIO[R with AuthService with Scope, A] = {
    val path = s"${request.method} ${request.url.path}"

    for {
      authResult <- AuthService
        .validateHeader(request)
        .logError("Unauthorized")
      traceId <- ULID.nextULIDString
      annotations = ZIOAspect.annotated(
        "user"    -> authResult.username,
        "method"  -> path,
        "traceId" -> traceId
      )
      result <- authResult match {
        case ValidAuthResult(_, _) =>
          // TODO: You can add role checking if needed.
          effect.tapError(err => ZIO.logError(err.getMessage)) @@ annotations <* ZIO.logInfo("Authorization passed") @@ annotations
        case InvalidAuthResult(_) =>
          ZIO.fail(InvalidCredentialsException(""))
      }
    } yield result
  } @@ countRESTRequests(path)

  val exceptionHandler: Throwable => Response = {
    case err: UnsupportedFeatureException => Response.internalServerError(s"Exception: $err")
    case err: ResourceNotFoundException   => Response.notFound(s"Exception: $err")
    case err: InternalDatabaseException   => Response.internalServerError(s"Exception: $err")
    case err: BodyParsingException        => Response.badRequest(s"Exception: $err")
    case err: InternalException           => Response.internalServerError(s"Exception: $err")
    case err                              => Response.badRequest(s"Exception: $err")
  }
}
