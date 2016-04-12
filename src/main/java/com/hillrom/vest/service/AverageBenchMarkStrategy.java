package com.hillrom.vest.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class AverageBenchMarkStrategy implements BenchMarkStrategy{

	@Override
	public double calculateBenchMark(List<BigDecimal> data) {
		if(Objects.isNull(data) || data.isEmpty()){
			return 0;
		}else{
			double sum = data.stream().mapToDouble(BigDecimal :: doubleValue).sum();
			return sum/data.size();
		}
	}

}
