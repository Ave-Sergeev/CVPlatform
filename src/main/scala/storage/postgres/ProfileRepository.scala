package storage.postgres

import service.models.Profile
import zio.macros.accessible
import zio.{Task, URLayer}

import java.util.UUID
import javax.sql.DataSource

@accessible
trait ProfileRepository {
  def getAll: Task[List[Profile]]
  def getById(id: UUID): Task[Profile]
  def insert(profile: Profile): Task[UUID]
  def update(profile: Profile): Task[UUID]
  def deleteById(id: UUID): Task[Unit]
}

object ProfileRepository {
  val live: URLayer[DataSource, ProfileRepositoryLive] = ProfileRepositoryLive.layer
}
