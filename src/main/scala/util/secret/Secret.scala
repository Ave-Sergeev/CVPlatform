package util.secret

import zio.Config.Secret

package object Secret {
  implicit class SecretOps(private val self: Secret) extends AnyVal {
    def secretToString: String = self.value.mkString
  }
}
