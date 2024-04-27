import zio.Config.Secret

package object util {
  implicit class SecretOps(self: Secret) {
    def secretToString: String = self.value.mkString
  }
}

