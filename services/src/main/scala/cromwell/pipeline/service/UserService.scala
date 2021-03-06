package cromwell.pipeline.service

import cromwell.pipeline.datastorage.dao.repository.UserRepository
import cromwell.pipeline.datastorage.dto.user.{ PasswordUpdateRequest, UserUpdateRequest }
import cromwell.pipeline.datastorage.dto.{ User, UserWithCredentials }
import cromwell.pipeline.model.wrapper.{ Password, UserEmail, UserId }
import cromwell.pipeline.utils.StringUtils._

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.Random

trait UserService {

  def killAllHumans(users: User*): Unit

  def makeAllCatsRobots(users: User*): Unit

  def getUsersByEmail(emailPattern: String): Future[Seq[User]]

  def getUserWithCredentialsByEmail(email: UserEmail): Future[Option[UserWithCredentials]]

  def addUser(user: UserWithCredentials): Future[UserId]

  def deactivateUserById(userId: UserId): Future[Option[User]]

  def updateUser(userId: UserId, request: UserUpdateRequest): Future[Int]

  def updatePassword(
    userId: UserId,
    request: PasswordUpdateRequest,
    salt: String = Random.nextLong().toHexString
  ): Future[Int]

}

object UserService {

  def apply(userRepository: UserRepository)(implicit executionContext: ExecutionContext): UserService =
    new UserService {

      def killAllHumans(users: User*): Unit =
        users.map(user => deactivateUserById(user.userId))

      def makeAllCatsRobots(users: User*): Unit = println("Done")

      def getUsersByEmail(emailPattern: String): Future[Seq[User]] =
        userRepository.getUsersByEmail(emailPattern).map(seq => seq.map(User.fromUserWithCredentials))

      def getUserWithCredentialsByEmail(email: UserEmail): Future[Option[UserWithCredentials]] =
        userRepository.getUserByEmail(email)

      def addUser(user: UserWithCredentials): Future[UserId] = userRepository.addUser(user)

      def deactivateUserById(userId: UserId): Future[Option[User]] =
        for {
          _ <- userRepository.deactivateUserById(userId)
          user <- userRepository.getUserById(userId)
        } yield user.map(User.fromUserWithCredentials)

      def updateUser(userId: UserId, request: UserUpdateRequest): Future[Int] =
        userRepository.getUserById(userId).flatMap {
          case Some(user) =>
            userRepository.updateUser(
              user.copy(email = request.email, firstName = request.firstName, lastName = request.lastName)
            )
          case None => Future.failed(new RuntimeException("user with this id doesn't exist"))
        }

      def updatePassword(
        userId: UserId,
        request: PasswordUpdateRequest,
        salt: String = Random.nextLong().toHexString
      ): Future[Int] = {

        def checkRequestPassword: Future[Unit] =
          if (request.newPassword == request.repeatPassword) {
            Future.unit
          } else {
            Future.failed(new RuntimeException("new password incorrectly duplicated"))
          }

        def checkUserPassword(user: UserWithCredentials): Future[Unit] =
          if (user.passwordHash == calculatePasswordHash(request.currentPassword, user.passwordSalt)) {
            Future.unit
          } else {
            Future.failed(new RuntimeException("user password differs from entered"))
          }

        for {
          _ <- checkRequestPassword
          user <- getUserById(userId)
          _ <- checkUserPassword(user)
          res <- updatePasswordUnsafe(userId, request.newPassword, salt)
        } yield res
      }

      private def getUserById(userId: UserId): Future[UserWithCredentials] =
        userRepository.getUserById(userId).flatMap {
          case Some(user) => Future.successful(user)
          case None       => Future.failed(new RuntimeException("user with this id doesn't exist"))
        }

      private def updatePasswordUnsafe(userId: UserId, newPassword: Password, salt: String): Future[Int] = {
        val passwordHash = calculatePasswordHash(newPassword, salt)
        userRepository.updatePassword(userId = userId, passwordHash = passwordHash, passwordSalt = salt)
      }
    }

}
