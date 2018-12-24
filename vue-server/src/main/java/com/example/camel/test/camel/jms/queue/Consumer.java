package com.example.camel.test.camel.jms.queue;

import java.util.concurrent.BlockingQueue;

@SuppressWarnings({"rawtypes"})
public class Consumer implements Runnable {
	
	public Consumer(BlockingQueue basket) {
		this.blockingQueue = basket;
	}

	BlockingQueue blockingQueue;
	
	@Override
	public void run() {
		consume();
		try {
			while (true) {
				// 消费苹果
				blockingQueue.take();
				// 休眠1000ms
				Thread.sleep(1000);
			}
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}
	
	private void consume() {
		while(true){
            try {
            	blockingQueue.take();
                System.out.println("从队列取走一个元素，队列剩余:"+blockingQueue.size()+"个元素");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
