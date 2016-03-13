package com.hillrom.vest.service;

public class BenchMarkStrategyFactory {

	public static BenchMarkStrategy getBenchMarkStrategy(String type){
		if("AVERAGE".equalsIgnoreCase(type)){
			return new AverageBenchMarkStrategy();
		}else if("MEDIAN".equalsIgnoreCase(type)){
			return new MedianBenchMarkStrategy();
		}else{
			return new AverageBenchMarkStrategy();
		}
	}
}
