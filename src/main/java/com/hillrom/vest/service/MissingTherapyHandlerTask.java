package com.hillrom.vest.service;

import javax.inject.Inject;

public class MissingTherapyHandlerTask extends Thread{

	@Inject
	AdherenceCalculationService adherenceCalculationService;
	
	@Override
	public void run() {
		adherenceCalculationService.processMissedTherapySessions();
	}
	
}
