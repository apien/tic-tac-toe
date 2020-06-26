package com.github.apien.tictactoe.domain

import com.github.apien.tictactoe.integration.GameInMemoryRepository
import com.github.apien.tictactoe.test.TtcSpec

class GameServiceSpec extends TtcSpec {

  private trait GameServiceContext {
    val repository = new GameInMemoryRepository(scala.collection.mutable.Map())
    val service = new GameService(repository)
  }

  "GameService.create" should "store new game" in new GameServiceContext {
    service.createNew.runSyncUnsafe()

    repository.getAll.runSyncUnsafe() should have size 1
  }

  it should "create game without set up a guest identifier" in new GameServiceContext {
    service.createNew.runSyncUnsafe()

    repository.getAll.runSyncUnsafe().head.guest shouldBe None
  }

  it should "assign new game and player identifiers" in new GameServiceContext {
    service.createNew.runSyncUnsafe()
    service.createNew.runSyncUnsafe()

    repository.getAll.runSyncUnsafe() should have size 2
  }
}
