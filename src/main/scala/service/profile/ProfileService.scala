package service.profile

import service.Model.Profile
import storage.db.ProfileRepository
import zio.http.{Request, Response}
import zio.json.{EncoderOps, JsonDecoder}
import zio.{RIO, ZIO}

import java.util.UUID

object ProfileService {
  def getAllProfiles: RIO[ProfileRepository, Response] =
    ProfileRepository.getAll.map(result => Response.json(result.toJson))

  def getProfileById(id: UUID): RIO[ProfileRepository, Response] =
    ProfileRepository.getById(id).map(result => Response.json(result.toJson))

  def addProfile(req: Request): RIO[ProfileRepository, Response] =
    for {
      body <- req.body.asString
      profile <- ZIO
        .fromEither(JsonDecoder[Profile].decodeJson(body))
        .orElseFail(new Throwable("Fail to decode json"))
      result <- ProfileRepository.insert(profile)
    } yield Response.json(result.toJson)

  def updateProfile(req: Request): RIO[ProfileRepository, Response] =
    for {
      body <- req.body.asString
      profile <- ZIO
        .fromEither(JsonDecoder[Profile].decodeJson(body))
        .orElseFail(new Throwable("Fail to decode json"))
      result <- ProfileRepository.update(profile)
    } yield Response.json(result.toJson)

  def deleteProfileById(id: UUID): RIO[ProfileRepository, Response] =
    ProfileRepository.deleteById(id).as(Response.ok)
}
