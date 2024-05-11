import zio.Config.Secret

package object util {
  implicit class SecretOps(private val self: Secret) extends AnyVal {
    def secretToString: String = self.value.mkString
  }
}
