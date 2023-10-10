package org.example.util;

import reactor.core.publisher.Mono;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class IdGenerator {
    private static IdGenerator instance;
    private final Lock lock = new ReentrantLock(true);
    private long myId = 0;
    private IdGenerator(){

    }
    public synchronized static IdGenerator getInstance(){
        if(instance == null){
            instance = new IdGenerator();
        }
        return instance;
    }
    public Mono<Long> getMyId(){
        return Mono.fromCallable(()->{
            lock.lock();
            try {
                return this.myId++;
            }finally {
                lock.unlock();
            }
        });
    }
}
