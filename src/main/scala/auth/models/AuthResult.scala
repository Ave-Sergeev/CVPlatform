package auth.models

sealed trait AuthResult {
  def username: String
  def status: Boolean
}

case class ValidAuthResult(username: String = "No name", roles: List[String] = List.empty) extends AuthResult {
  override def status: Boolean = true
}

case class InvalidAuthResult(username: String = "No name") extends AuthResult {
  override def status: Boolean = false
}
