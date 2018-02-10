package banoun.aneece.services.realtime;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import banoun.aneece.services.StockExchangeService;

@Service
public class StockMarketRealtimerService implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(StockMarketRealtimerService.class);
	private static Long WAIT_TIME = 14*60*1000L;

	@Autowired
	StockExchangeService stock;

	public StockMarketRealtimerService(){
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		log.info("StockMarketRealtimerService STARTING...");
		while(stock==null){sleep(3000);}
		log.info("StockMarketRealtimerService STARTED");
		while(true){
			try{
				stock.updateStockRecords();
				sleep(WAIT_TIME);
			}catch(Throwable t){
				log.error("StockMarketRealtimerService Crashed on: "+ LocalDateTime.now()+t.getMessage()+t.toString());
				sleep(WAIT_TIME);
			}
		}
	}

	public static void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
