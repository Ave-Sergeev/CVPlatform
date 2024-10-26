package util.ulid

import wvlet.airframe.ulid.ULID
import zio.{UIO, ZIO}

import java.time.LocalDateTime
import java.util.{TimeZone, UUID}

package object ULID {

  private val tz = TimeZone.getDefault.toZoneId

  def nextULID: UIO[ULID] = ZIO.succeed(ULID.newULID)

  def nextULIDString: UIO[String] = ZIO.succeed(newULIDString)

  def newULIDString: String = ULID.newULIDString

  def newULID: ULID = ULID.newULID

  def nextUUID: UIO[UUID] = ZIO.succeed(ULID.newULID.toUUID)

  def nextUUIDString: UIO[String] = ZIO.succeed(ULID.newULID.toUUID.toString)

  def getTimeFromULID(ulid: ULID): LocalDateTime = LocalDateTime.ofInstant(ulid.toInstant, tz)

  def getTimeFromULID(ulid: String): LocalDateTime = getTimeFromULID(ULID.fromString(ulid))

  def getTimeFromUUID(uuid: UUID): LocalDateTime = getTimeFromULID(ULID.fromUUID(uuid))

  def getTimeFromUUID(uuid: String): LocalDateTime = getTimeFromUUID(UUID.fromString(uuid))

  implicit def stringToUUID(self: String): UUID = UUID.fromString(self)
}
