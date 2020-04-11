package org.fordes.subview.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main
{
	public static void main(String[] args)
	{
		long start = System.currentTimeMillis();
		
		// 创建一个同时允许两个线程并发执行的线程池
		ExecutorService executor = Executors.newFixedThreadPool(2);
		for(int i = 0; i < 5; i++)
		{
			Thread thread = new TestThread();
			executor.execute(thread);
		}
		executor.shutdown();
		
		try
		{
			// awaitTermination返回false即超时会继续循环，返回true即线程池中的线程执行完成主线程跳出循环往下执行，每隔10秒循环一次
			while (!executor.awaitTermination(10, TimeUnit.SECONDS));
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		long end = System.currentTimeMillis();
		System.out.println("子线程执行时长：" + (end - start));
	}
}