package com.hillrom.vest.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class MedianBenchMarkStrategy implements BenchMarkStrategy{

	@Override
	public double calculateBenchMark(List<BigDecimal> data) {
		if(Objects.isNull(data) || data.isEmpty()){
			return 0;
		}else{
			double min = data.stream().min(BigDecimal :: compareTo).get().doubleValue();
			double max = data.stream().max(BigDecimal :: compareTo).get().doubleValue();
			return (min + max)/2;
		}
	}

}
