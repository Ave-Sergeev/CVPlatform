package grpc.service

import com.google.protobuf.empty.Empty
import cvpservice.CVPServiceEnv
import profile_service.ZioProfileService.RCProfileService
import profile_service._
import scalapb.zio_grpc.{GIO, RequestContext}
import service.models.Profile
import storage.postgres.ProfileRepository
import zio.{URIO, ZEnvironment, ZIO}

import java.util.UUID

final case class ProfileController(
    repository: ProfileRepository
)(
    implicit env: ZEnvironment[CVPServiceEnv]
) extends RCProfileService {

  override def getAllProfile(request: Empty, context: RequestContext): GIO[AllProfiles] =
    handleRPC(context) {
      repository.getAll
        .map(response => AllProfiles(response.map(profile => UserProfile(profile.id.toString, profile.name, profile.link))))
    }

  override def getProfile(request: ProfileId, context: RequestContext): GIO[UserProfile] =
    handleRPC(context) {
      import util.ulid.ULID.stringToUUID

      repository
        .getById(request.uuid)
        .map(response => UserProfile(response.id.toString, response.name, response.link))
    }

  override def addProfile(request: UserProfile, context: RequestContext): GIO[ProfileId] =
    handleRPC(context) {
      import util.ulid.ULID.stringToUUID

      repository
        .insert(Profile(request.id, request.name, request.link))
        .as(ProfileId(request.id))
    }

  override def updateProfile(request: UserProfile, context: RequestContext): GIO[ProfileId] =
    handleRPC(context) {
      import util.ulid.ULID.stringToUUID

      repository
        .update(Profile(request.id, request.name, request.link))
        .tapError(err => ZIO.logInfo(s"err: $err"))
        .as(ProfileId(request.id))
    }

  override def deleteProfileById(request: ProfileId, context: RequestContext): GIO[Empty] =
    handleRPC(context) {
      repository
        .deleteById(UUID.fromString(request.uuid))
        .as(Empty())
    }
}

object ProfileController {
  def make: URIO[CVPServiceEnv, ProfileController] =
    for {
      environment       <- ZIO.environment[CVPServiceEnv]
      profileRepository <- ZIO.service[ProfileRepository]
    } yield ProfileController(profileRepository)(environment)
}
