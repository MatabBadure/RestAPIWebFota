package com.hillrom.vest.service;

import java.math.BigDecimal;
import java.util.List;

public interface BenchMarkStrategy {

	public double calculateBenchMark(List<BigDecimal> data);
}
