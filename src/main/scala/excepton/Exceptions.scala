package excepton

object Exceptions {
  sealed abstract class CustomException(message: String) extends Exception(message)

  case class ResourceNotFoundException(message: String) extends CustomException(message)

  case class InternalDatabaseException(message: String) extends CustomException(message)

  case class BodyParsingException(message: String) extends CustomException(message)

  case class UnsupportedFeatureException(message: String) extends CustomException(message)

  case class InternalException(message: String) extends CustomException(message)
}
