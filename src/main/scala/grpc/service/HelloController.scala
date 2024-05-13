package grpc.service

import com.google.protobuf.empty.Empty
import cvpservice.CVPServiceEnv
import hello.ZioHello.RCProfileService
import hello.{ProfileResponse, UserProfile}
import io.grpc.StatusException
import scalapb.zio_grpc.RequestContext
import storage.postgres.ProfileRepository
import zio.{IO, URIO, ZEnvironment, ZIO}

final case class HelloController(
    repository: ProfileRepository
)(
    implicit env: ZEnvironment[CVPServiceEnv]
) extends RCProfileService {
  override def getAllProfile(request: Empty, context: RequestContext): IO[StatusException, ProfileResponse] =
    handleRPC(context) {
      repository.getAll
        .map { response =>
          ProfileResponse(
            response.map(profile => UserProfile(profile.id.toString, profile.name, profile.link))
          )
        }
    }
}

object HelloController {
  def make: URIO[CVPServiceEnv, HelloController] =
    for {
      environment       <- ZIO.environment[CVPServiceEnv]
      profileRepository <- ZIO.service[ProfileRepository]
    } yield HelloController(profileRepository)(environment)
}
