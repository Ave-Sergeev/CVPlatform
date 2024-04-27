package storage

import io.getquill.jdbczio.Quill
import zio.TaskLayer

import javax.sql.DataSource

object DB {
  val live: TaskLayer[DataSource] = Quill.DataSource.fromPrefix("database")
}
