package cromwell.pipeline.datastorage.dao.repository

import cromwell.pipeline.datastorage.dto.UserWithCredentials
import cromwell.pipeline.model.wrapper.{ UserEmail, UserId }
import scala.collection.mutable
import scala.concurrent.Future

class UserRepositoryTestImp extends UserRepository {

  private val users: mutable.Map[UserId, UserWithCredentials] = mutable.Map.empty

  def getUserById(userId: UserId): Future[Option[UserWithCredentials]] =
    Future.successful(users.get(userId))

  def getUserByEmail(email: UserEmail): Future[Option[UserWithCredentials]] =
    Future.successful(users.values.find(_.email == email))

  def getUsersByEmail(emailPattern: String): Future[Seq[UserWithCredentials]] =
    Future.successful(users.values.filter(_.email.unwrap.contains(emailPattern)).toSeq)

  def addUser(user: UserWithCredentials): Future[UserId] = {
    users += (user.userId -> user)
    Future.successful(user.userId)
  }

  def deactivateUserByEmail(email: UserEmail): Future[Int] =
    deactivateUser[UserEmail](email, user => user.email)

  def deactivateUserById(userId: UserId): Future[Int] =
    deactivateUser[UserId](userId, user => user.userId)

  private def deactivateUser[A](userParam: A, toUserField: UserWithCredentials => A): Future[Int] = {
    for {
      (_, user) <- users if userParam == toUserField(user)
      deactivatedUser = user.copy(active = false)
    } yield users += (deactivatedUser.userId -> deactivatedUser)
    Future.successful(0)
  }

  def updateUser(updatedUser: UserWithCredentials): Future[Int] = update(updatedUser)

  def updatePassword(userId: UserId, hash: String, salt: String): Future[Int] =
    users.get(userId).fold(Future.successful(0))(u => update(u.copy(passwordHash = hash, passwordSalt = salt)))

  private def update(updatedUser: UserWithCredentials): Future[Int] = {
    if (users.contains(updatedUser.userId)) users += (updatedUser.userId -> updatedUser)
    Future.successful(0)
  }

}

object UserRepositoryTestImp {

  def apply(users: UserWithCredentials*): UserRepositoryTestImp = {
    val userRepositoryTestImp = new UserRepositoryTestImp
    users.foreach(userRepositoryTestImp.addUser)
    userRepositoryTestImp
  }

}
