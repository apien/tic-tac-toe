package com.github.apien.tictactoe.domain

import com.github.apien.tictactoe.test.TtcSpec
class GameServiceSpec extends TtcSpec {

  //TODO Bring this tests to the life. I have some trouble to instantiate the fake Transactor.
//  private trait GameServiceContext {
//    val interpreter = KleisliInterpreter[Task](
//      Blocker.liftExecutionContext(ExecutionContexts.synchronous)
//    ).ConnectionInterpreter
//
//    val transactor = Transactor(
//      (),
//      (_: Unit) => Resource.pure(null),
//      KleisliInterpreter[Task](
//        Blocker.liftExecutionContext(ExecutionContexts.synchronous)
//      ).ConnectionInterpreter,
//      Strategy.void
//    )
//
//    val repository = new GameInMemoryRepository(scala.collection.mutable.Map())
//    val service = new GameService(repository, null)
//  }
//
//  "GameService.create" should "store new game" in new GameServiceContext {
//    service.createNew.runSyncUnsafe()
//
//    repository.getAll.transact(transactor).runSyncUnsafe() should have size 1
//  }
//
//  it should "create game without set up a guest identifier" in new GameServiceContext {
//    service.createNew.runSyncUnsafe()
//
//    repository.getAll
//      .transact(transactor)
//      .runSyncUnsafe()
//      .head
//      .guest shouldBe None
//  }
//
//  it should "assign new game and player identifiers" in new GameServiceContext {
//    service.createNew.runSyncUnsafe()
//    service.createNew.runSyncUnsafe()
//
//    repository.getAll.transact(transactor).runSyncUnsafe() should have size 2
//  }
}
