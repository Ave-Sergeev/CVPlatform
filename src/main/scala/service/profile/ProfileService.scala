package service.profile

import service.Model.Profile
import service.db.DBService
import zio.http.{Request, Response}
import zio.json.{EncoderOps, JsonDecoder}
import zio.{RIO, ZIO}

import java.util.UUID

object ProfileService {
  def getAllProfiles: RIO[DBService, Response] =
    DBService.getAll.map(result => Response.json(result.toJson))

  def getProfileById(id: UUID): RIO[DBService, Response] =
    DBService.getById(id).map(result => Response.json(result.toJson))

  def addProfile(req: Request): RIO[DBService, Response] =
    for {
      body <- req.body.asString
      profile <- ZIO
        .fromEither(JsonDecoder[Profile].decodeJson(body))
        .orElseFail(new Throwable("Fail to decode json"))
      result <- DBService.insert(profile)
    } yield Response.json(result.toJson)

  def updateProfile(req: Request): RIO[DBService, Response] =
    for {
      body <- req.body.asString
      profile <- ZIO
        .fromEither(JsonDecoder[Profile].decodeJson(body))
        .orElseFail(new Throwable("Fail to decode json"))
      result <- DBService.update(profile)
    } yield Response.json(result.toJson)

  def deleteProfileById(id: UUID): RIO[DBService, Response] =
    DBService.deleteById(id).as(Response.ok)
}
