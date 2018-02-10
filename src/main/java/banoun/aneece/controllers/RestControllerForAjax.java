package banoun.aneece.controllers;
import static banoun.aneece.services.ServiceUtilities.*;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


import banoun.aneece.model.stockmarket.StockSymbol;

@RestController
public class RestControllerForAjax {


	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMMM/yyyy (HH:mm)");
	
	@PostMapping("/currentStockMarketAjax")
	public Map<String, String> currentStockMarketAjax() {
		StringBuffer updateTime = new StringBuffer();
		Map<String, String> result = new HashMap<>();
		StockSymbol.SYMBOLS_MAP.keySet().stream().forEach(symbol->{
			StockSymbol stockSymbol = StockSymbol.SYMBOLS_MAP.get(symbol);
			if("".equals(updateTime.toString())){
				updateTime.append(stockSymbol.getSortedDayHistory().get(0).getLocalDateTime().format(formatter));
			}
			Double diff = Double.parseDouble(stockSymbol.getCurrentPrice())-Double.parseDouble(stockSymbol.getPreviousPrice());
			String formattedDiff = String.format("%.2f", diff);
			String diffDisplay = "0.00".equals(formattedDiff)?SAME:diff>0?UP+formattedDiff:DOWN+formattedDiff;
			result.put(stockSymbol.getName().replaceAll(" ", ""), OPEN_BRAKET+stockSymbol.getName()+" "+diffDisplay+CLOSE_BRAKET);
			String colour = "0.00".equals(formattedDiff)?"color:blue":diff>0?"color:green":"color:red";
			result.put(stockSymbol.getName().replaceAll(" ", "")+"Color", colour);
		});
		String stockUpdateTimeId = "Updated on: "+updateTime.toString();
		result.put("stockUpdateTimeId", stockUpdateTimeId);
		return result;
	}

}
