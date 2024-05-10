import zio.Config.Secret
import zio.ZIO
import zio.json.{DecoderOps, EncoderOps, JsonDecoder, JsonEncoder}
import zio.kafka.serde.Serde

package object util {
  implicit class SecretOps(private val self: Secret) extends AnyVal {
    def secretToString: String = self.value.mkString
  }

  implicit def serde[M: JsonEncoder: JsonDecoder]: Serde[Any, M] =
    Serde.string.inmapM { string =>
      ZIO
        .fromEither(string.fromJson[M])
        .mapError(err => new RuntimeException(err))
    }(profile => ZIO.succeed(profile.toJson))
}
