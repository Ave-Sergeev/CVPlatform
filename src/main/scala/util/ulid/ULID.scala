package util.ulid

import wvlet.airframe.ulid.ULID
import zio.{UIO, ZIO}

import java.time.LocalDateTime
import java.util.{TimeZone, UUID}

package object ULID {

  private val tz = TimeZone.getDefault.toZoneId

  def newULID: ULID = ULID.newULID

  def newUUID: UUID = newULID.toUUID

  def newULIDString: String = ULID.newULIDString

  def newUUIDString: String = newUUID.toString

  def newEffectULID: UIO[ULID] = ZIO.succeed(newULID)

  def newEffectUUID: UIO[UUID] = ZIO.succeed(newUUID)

  def newEffectULIDString: UIO[String] = ZIO.succeed(newULIDString)

  def newEffectUUIDString: UIO[String] = ZIO.succeed(newUUIDString)

  def getTimeFromULID(ulid: ULID): LocalDateTime = LocalDateTime.ofInstant(ulid.toInstant, tz)

  def getTimeFromULID(ulid: String): LocalDateTime = getTimeFromULID(ULID.fromString(ulid))

  def getTimeFromUUID(uuid: UUID): LocalDateTime = getTimeFromULID(ULID.fromUUID(uuid))

  def getTimeFromUUID(uuid: String): LocalDateTime = getTimeFromUUID(UUID.fromString(uuid))

  implicit def stringToUUID(self: String): UUID = UUID.fromString(self)
}
