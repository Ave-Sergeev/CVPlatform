package storage.postgres

import exception.Exceptions._
import io.getquill.{PostgresZioJdbcContext, SnakeCase}
import service.models.Profile
import zio.{Task, URLayer, ZLayer}

import java.util.UUID
import javax.sql.DataSource

case class ProfileRepositoryLive(ds: DataSource) extends ProfileRepository {

  private val ctx = new PostgresZioJdbcContext(SnakeCase)

  import ctx._

  private val dsLayer = ZLayer.succeed(ds)

  private val profileSchema = quote {
    query[Profile]
  }

  override def getAll: Task[List[Profile]] =
    ctx
      .run(profileSchema)
      .mapError(err => InternalDatabaseException(err.getMessage))
      .provide(dsLayer)

  override def getById(id: UUID): Task[Profile] =
    ctx
      .run(profileSchema.filter(_.id == lift(id)))
      .mapBoth(
        err => InternalDatabaseException(err.getMessage),
        _.headOption
      )
      .some
      .orElseFail(ResourceNotFoundException("Profile with this id was not found"))
      .provide(dsLayer)

  override def insert(profile: Profile): Task[UUID] =
    ctx
      .run(profileSchema.insertValue(lift(profile)).returning(value => value))
      .mapBoth(
        err => InternalDatabaseException(err.getMessage),
        result => result.id
      )
      .provide(dsLayer)

  override def update(profile: Profile): Task[UUID] =
    ctx
      .run(profileSchema.updateValue(lift(profile)).returning(value => value))
      .mapBoth(
        err => InternalDatabaseException(err.getMessage),
        result => result.id
      )
      .provide(dsLayer)

  override def deleteById(id: UUID): Task[Unit] =
    ctx
      .run(profileSchema.filter(_.id == lift(id)).delete)
      .mapError(err => InternalDatabaseException(err.getMessage))
      .unit
      .provide(dsLayer)
}

object ProfileRepositoryLive {
  def layer: URLayer[DataSource, ProfileRepositoryLive] =
    ZLayer.fromFunction[DataSource => ProfileRepositoryLive](ProfileRepositoryLive(_))
}
