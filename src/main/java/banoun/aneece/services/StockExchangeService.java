package banoun.aneece.services;
import static banoun.aneece.services.ServiceUtilities.*;

import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import banoun.aneece.model.stockmarket.StockEntry;
import banoun.aneece.model.stockmarket.StockSymbol;

@Service
public class StockExchangeService {

	private static final String STOCK_DAY_HISTORY_URL = "https://api.iextrading.com/1.0/stock/STOCKSYMBOL/chart/1d";
	private static final String STOCK_SYMBOL = "STOCKSYMBOL";

	public void updateStockRecords() {
		update();
	}

	private void update() {
		LocalDateTime currentLocalDateTime = LocalDateTime.now();
		String currentTime = currentLocalDateTime.format(DateTimeFormatter.ofPattern(StockSymbol.DATE_PATTERN));
		StockSymbol.SYMBOLS_MAP.keySet().stream().forEach(symbol -> {
			StockSymbol stockSymbol = StockSymbol.SYMBOLS_MAP.get(symbol);
			try {
				ObjectMapper mapper = new ObjectMapper();
				stockSymbol.setLastUpdatTime(currentTime);
				stockSymbol.setDayHistoryLastUpdatTime(currentTime);
				String stockDayHistory = getJson(STOCK_DAY_HISTORY_URL.replace(STOCK_SYMBOL, symbol), 3000);
				List<StockEntry> dayHistory = new ArrayList<>();
				stockSymbol.setDayHistory(dayHistory);
				List<Map<String, String>> list = mapper.readValue(stockDayHistory, new TypeReference<List<Map<String, String>>>() {});
				list.stream().forEach(e -> {
					if(!isNull(e.get("date"), e.get("minute"), e.get("average"))&& Double.parseDouble(e.get("average"))!=0.0){
						String time = e.get("date") + e.get("minute");
						StockEntry stockEntry = new StockEntry();
						stockEntry.setDateTime(time);
						stockEntry.setLocalDateTime(LocalDateTime.parse(time, DateTimeFormatter.ofPattern(StockSymbol.DATE_PATTERN)));
						stockEntry.setAverage(Double.parseDouble(e.get("average")));
						stockEntry.setMarketAverage(Double.parseDouble(e.get("marketAverage")));
						dayHistory.add(stockEntry);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
			List<StockEntry> sortedDayHistory = stockSymbol.getSortedDayHistory();
			stockSymbol.setCurrentPrice(sortedDayHistory.get(0).getAverage().toString()); 
			stockSymbol.setPreviousPrice(sortedDayHistory.get(1).getAverage().toString());
			
		});
	}

	private String getJson(String url, int timeout) {
		HttpURLConnection httpURLConnection = null;
		try {
			httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
			httpURLConnection.setRequestMethod("GET");
			httpURLConnection.setRequestProperty("Content-length", "0");
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setAllowUserInteraction(false);
			httpURLConnection.setConnectTimeout(timeout);
			httpURLConnection.setReadTimeout(timeout);
			httpURLConnection.connect();
			int status = httpURLConnection.getResponseCode();
			switch (status) {
			case 200:
			case 201:
				BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				br.close();
				return sb.toString();
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (httpURLConnection != null) {
				try {
					httpURLConnection.disconnect();
					httpURLConnection = null;
					
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return null;
	}
}
