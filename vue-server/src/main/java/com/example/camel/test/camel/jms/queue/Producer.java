package com.example.camel.test.camel.jms.queue;

import java.util.concurrent.BlockingQueue;

@SuppressWarnings({"rawtypes", "unchecked"})
public class Producer implements Runnable {
	
	public Producer(BlockingQueue basket, Object obj) {
		this.blockingQueue = basket;
		this.obj = obj;
	}

	BlockingQueue blockingQueue;
	
	Object obj;

	@Override
	public void run() {
		produce();
	}
	
	private void produce() {
        while(true){
        	 try {
        		 blockingQueue.put(1);
                 System.out.println("向队列取中插入一个元素，队列剩余元素个数："+blockingQueue.size());
             } catch (InterruptedException e) {
                 e.printStackTrace();
             }
        }
    }

}
