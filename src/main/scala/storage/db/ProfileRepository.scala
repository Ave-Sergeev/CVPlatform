package storage.db

import service.Model.Profile
import zio.macros.accessible
import zio.{Task, TaskLayer}

import java.util.UUID

@accessible
trait ProfileRepository {
  def getAll: Task[List[Profile]]
  def getById(id: UUID): Task[Profile]
  def insert(profile: Profile): Task[UUID]
  def update(profile: Profile): Task[UUID]
  def deleteById(id: UUID): Task[Unit]
}

object ProfileRepository {
  val live: TaskLayer[ProfileRepository] = ProfileRepositoryLive.layer
}
