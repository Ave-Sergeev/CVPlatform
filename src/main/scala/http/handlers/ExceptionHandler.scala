package http.handlers

import exception.Exceptions._
import zio.http.Response

object ExceptionHandler {
  val exceptionHandler: Throwable => Response = {
    case err: UnsupportedFeatureException => Response.internalServerError(s"Exception: $err")
    case err: ResourceNotFoundException   => Response.notFound(s"Exception: $err")
    case err: InternalDatabaseException   => Response.internalServerError(s"Exception: $err")
    case err: BodyParsingException        => Response.badRequest(s"Exception: $err")
    case err: InternalException           => Response.internalServerError(s"Exception: $err")
    case err                              => Response.badRequest(s"Exception: $err")
  }
}
