package http.handlers

import zio.http.Response

object ExceptionHandler {
  val exceptionHandler: Throwable => Response = {
    case err: Throwable => Response.internalServerError(s"Exception: $err")
    case err => Response.badRequest(s"Error: $err")
  }
}
