package ru.sergeykozhukhov.currencyconverter.presentation;

import java.util.concurrent.Executor;


/**
 * Используется для того чтобы в юнит тесте все операции выполнялись синхронно
 **/
class SynchronousExecutor implements Executor {

    @Override
    public void execute(Runnable command) {
        command.run();
    }

}
