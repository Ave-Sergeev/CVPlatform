package service.db

import io.getquill.{PostgresZioJdbcContext, SnakeCase}
import service.Model.Profile
import storage.DB
import zio.{Task, TaskLayer, ZLayer}

import java.util.UUID
import javax.sql.DataSource

case class DBServiceLive(ds: DataSource) extends DBService {

  private val ctx = new PostgresZioJdbcContext(SnakeCase)

  import ctx._

  private val dsLayer = ZLayer.succeed(ds)

  private val profileSchema = quote {
    query[Profile]
  }

  override def getAll: Task[List[Profile]] =
    ctx
      .run(profileSchema)
      .mapError(err => new Throwable(err.getMessage))
      .provide(dsLayer)

  override def getById(id: UUID): Task[Profile] =
    ctx
      .run(profileSchema.filter(_.id == lift(id)))
      .mapBoth(
        err => new Throwable(err.getMessage),
        _.headOption
      )
      .some
      .orElseFail(new Throwable("Profile with this id was not found"))
      .provide(dsLayer)

  override def insert(profile: Profile): Task[UUID] =
    ctx
      .run(profileSchema.insertValue(lift(profile)).returning(value => value))
      .mapBoth(
        err => new Throwable(err.getMessage),
        result => result.id
      )
      .provide(dsLayer)

  override def update(profile: Profile): Task[UUID] =
    ctx
      .run(profileSchema.updateValue(lift(profile)).returning(value => value))
      .mapBoth(
        err => new Throwable(err.getMessage),
        result => result.id
      )
      .provide(dsLayer)

  override def deleteById(id: UUID): Task[Unit] =
    ctx
      .run(profileSchema.filter(_.id == lift(id)).delete)
      .mapError(err => new Throwable(err.getMessage))
      .unit
      .provide(dsLayer)
}

object DBServiceLive {
  def layer: TaskLayer[DBService] = DB.live >>> ZLayer.fromFunction[DataSource => DBServiceLive](ds => DBServiceLive(ds))
}
