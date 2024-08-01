package util.ulid

import wvlet.airframe.ulid.ULID
import zio.{IO, UIO, ZIO}

import java.nio.ByteBuffer
import java.time.LocalDateTime
import java.util.{TimeZone, UUID}

package object ULID {

  private lazy val timeZone = TimeZone.getDefault.toZoneId

  def nextULID: IO[Throwable, ULID] = ZIO.succeed(ULID.newULID)

  def nextULIDString: UIO[String] = ZIO.succeed(ULID.newULIDString)

  def nextUUID: UIO[UUID] = ZIO.succeed {
    val ulid                 = ULID.newULID.toBytes
    val mostSignificantBits  = ByteBuffer.wrap(ulid.take(16)).getLong
    val leastSignificantBits = ByteBuffer.wrap(ulid.takeRight(16)).getLong
    new UUID(mostSignificantBits, leastSignificantBits)
  }

  def nextUUIDString: UIO[String] = nextUUID.map(_.toString)

  def getTimeFromULID(ulid: ULID): LocalDateTime = LocalDateTime.ofInstant(ulid.toInstant, timeZone)

  def getTimeFromULID(ulid: String): LocalDateTime = getTimeFromULID(ULID.fromString(ulid))

  def getTimeFromUUID(uuid: UUID): LocalDateTime = getTimeFromULID(toULID(uuid))

  def getTimeFromUUID(uuid: String): LocalDateTime = getTimeFromUUID(UUID.fromString(uuid))

  def toULID(uuid: UUID): ULID = {
    val buffer = ByteBuffer.allocate(16)
    buffer.putLong(uuid.getMostSignificantBits)
    buffer.putLong(uuid.getLeastSignificantBits)
    ULID.fromBytes(buffer.array())
  }

  implicit def stringToUUID(self: String): UUID = UUID.fromString(self)
}
