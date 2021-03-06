package cromwell.pipeline.datastorage.dao.repository

import cromwell.pipeline.datastorage.dao.utils.ArbitraryUtils._
import cromwell.pipeline.datastorage.dao.utils.FormatUtils._
import cromwell.pipeline.datastorage.dto._
import cromwell.pipeline.datastorage.dto.auth.{ SignInRequest, SignUpRequest }
import cromwell.pipeline.datastorage.dto.user.{ PasswordUpdateRequest, UserUpdateRequest }
import org.scalatest.{ AsyncWordSpec, Matchers }
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import play.api.libs.json.{ JsError, JsSuccess }

class MarshallerTests extends AsyncWordSpec with Matchers with ScalaCheckDrivenPropertyChecks {

  "MarshallerFormat" when {
    "format UserUpdateRequest" in {
      forAll { (a: UserUpdateRequest) =>
        val parseResult: UserUpdateRequest = userUpdateRequestFormat.reads(userUpdateRequestFormat.writes(a)) match {
          case JsSuccess(value, _) => value
          case JsError(_)          => fail("Could not parse request")
        }
        parseResult should equal(a)
      }
    }
    "format PasswordUpdateRequest" in {
      forAll { (a: PasswordUpdateRequest) =>
        val parseResult: PasswordUpdateRequest =
          passwordUpdateRequestFormat.reads(passwordUpdateRequestFormat.writes(a)) match {
            case JsSuccess(value, _) => value
            case JsError(_)          => fail("Could not parse request")
          }
        parseResult should equal(a)
      }
    }
    "format SignUpRequest" in {
      forAll { (a: SignUpRequest) =>
        val parseResult: SignUpRequest = signUpRequestFormat.reads(signUpRequestFormat.writes(a)) match {
          case JsSuccess(value, _) => value
          case JsError(_)          => fail("Could not parse request")
        }
        parseResult should equal(a)
      }
    }
    "format SignInRequest" in {
      forAll { (a: SignInRequest) =>
        val parseResult: SignInRequest = signInRequestFormat.reads(signInRequestFormat.writes(a)) match {
          case JsSuccess(value, _) => value
          case JsError(_)          => fail("Could not parse request")
        }
        parseResult should equal(a)
      }
    }
    "format ProjectAdditionalRequest" in {
      forAll { (a: ProjectAdditionRequest) =>
        val parseResult: ProjectAdditionRequest =
          projectAdditionRequestFormat.reads(projectAdditionRequestFormat.writes(a)) match {
            case JsSuccess(value, _) => value
            case JsError(_)          => fail("Could not parse request")
          }
        parseResult should equal(a)
      }
    }
    "format ProjectUpdateRequest" in {
      forAll { (a: ProjectUpdateNameRequest) =>
        val parseResult: ProjectUpdateNameRequest =
          projectUpdateNameRequestFormat.reads(projectUpdateNameRequestFormat.writes(a)) match {
            case JsSuccess(value, _) => value
            case JsError(_)          => fail("Could not parse request")
          }
        parseResult should equal(a)
      }
    }
    "format ProjectFileContent" in {
      forAll { (a: ProjectFileContent) =>
        val parseResult: ProjectFileContent = projectFileContentFormat.reads(projectFileContentFormat.writes(a)) match {
          case JsSuccess(value, _) => value
          case JsError(_)          => fail("Could not parse request")
        }
        parseResult should equal(a)
      }
    }
    "format GitLabFileContent" in {
      forAll { (a: GitLabFileContent) =>
        val parseResult: GitLabFileContent = gitLabFileContentFormat.reads(gitLabFileContentFormat.writes(a)) match {
          case JsSuccess(value, _) => value
          case JsError(_)          => fail("Could not parse request")
        }
        parseResult should equal(a)
      }
    }
    "format ProjectUpdateFileRequest" in {
      forAll { (a: ProjectUpdateFileRequest) =>
        val parseResult: ProjectUpdateFileRequest =
          projectUpdateFileRequestFormat.reads(projectUpdateFileRequestFormat.writes(a)) match {
            case JsSuccess(value, _) => value
            case JsError(_)          => fail("Could not parse request")
          }
        parseResult should equal(a)
      }
    }
  }
}
