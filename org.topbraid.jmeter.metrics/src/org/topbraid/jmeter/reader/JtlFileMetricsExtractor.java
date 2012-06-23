package org.topbraid.jmeter.reader;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class JtlFileMetricsExtractor {

	public static final String IN_FILE = "listener.jtl";
	public static final String ELEMENT_NAME = "httpSample";
	public static final String LT_ATTRIBUTE = "lt";
	public static final String T_ATTRIBUTE = "t";
	public static final String RC_ATTRIBUTE = "rc";
	public static final String RM_ATTRIBUTE = "rm";
	public static final String LB_ATTRIBUTE = "lb";
	public static final String DELIMITER = "\t\t";
	
	public static final String SMALL_QUERY = "GetSmall";
	public static final String MEDIUM_QUERY = "GetMedium";
	public static final String LARGE_QUERY = "GetLarge";
	public static final String HUGE_QUERY = "GetHuge";
	
	public static final String OK_STATUS_CODE= "200";
	
	public static void main(String[] args){
		parseJtlFile();
	}
	
	private static void parseJtlFile(){
		File jtlFile = new File(IN_FILE);
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		NodeList nodeList = null;
		try {
			DocumentBuilder builder = dbFactory.newDocumentBuilder();
			Document document = builder.parse(jtlFile);
			nodeList = document.getElementsByTagName(ELEMENT_NAME);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(nodeList != null)
			calculatePerformanceMetrics(nodeList);
		else{
			System.err.println("Node List NOT FOUND");
			return;
		}
	}
	
	private static void calculatePerformanceMetrics(NodeList nodeList){
		Node node;
		NamedNodeMap attributeValueMap;
		int queryTime;
		String queryType, statusCode, queryTimeString;
		
		
		int smallQueryCount = 0, largeQueryCount = 0, mediumQueryCount = 0, hugeQueryCount = 0;
		int smallQueryFastestTime = Integer.MAX_VALUE, mediumQueryFastestTime = Integer.MAX_VALUE, 
				largeQueryFastestTime = Integer.MAX_VALUE, hugeQueryFastestTime = Integer.MAX_VALUE;  
		int smallQuerySlowestTime = 0, mediumQuerySlowestTime = 0, largeQuerySlowestTime = 0, hugeQuerySlowestTime = 0;  
		long smallQueryTotalTime = 0, mediumQueryTotalTime = 0, largeQueryTotalTime = 0, hugeQueryTotalTime = 0;
		int smallQueryErrorCount = 0, mediumQueryErrorCount = 0, largeQueryErrorCount = 0, hugeQueryErrorCount = 0;
		float smallQueryAvgTime = 0.0f, mediumQueryAvgTime = 0.0f, largeQueryAvgTime = 0.0f, hugeQueryAvgTime = 0.0f;
		float smallQueryErrorRate = 0.0f, mediumQueryErrorRate = 0.0f, largeQueryErrorRate = 0.0f, hugeQueryErrorRate = 0.0f;

		Set<String> smallQueryErrorStatusCodes = new HashSet<String>();
		Set<String> mediumQueryErrorStatusCodes = new HashSet<String>();
		Set<String> largeQueryErrorStatusCodes = new HashSet<String>();
		Set<String> hugeQueryErrorStatusCodes = new HashSet<String>();

		
	//	System.out.println("Total queries executed: " + nodeList.getLength());
		
		for (int i=0; i < 27600; i++){
			node = nodeList.item(i);
			attributeValueMap = node.getAttributes();
			
			statusCode = attributeValueMap.getNamedItem(RC_ATTRIBUTE).getNodeValue();
			queryType = attributeValueMap.getNamedItem(LB_ATTRIBUTE).getNodeValue();
			queryTimeString = attributeValueMap.getNamedItem(T_ATTRIBUTE).getNodeValue();
			queryTime = Integer.parseInt(queryTimeString);
			
			if(queryType.equals(SMALL_QUERY)){
				if(statusCode.equals(OK_STATUS_CODE)){
					++smallQueryCount;
					smallQueryTotalTime += queryTime;
					smallQueryFastestTime = queryTime <= smallQueryFastestTime? queryTime : smallQueryFastestTime;
					smallQuerySlowestTime = queryTime >= smallQuerySlowestTime? queryTime : smallQuerySlowestTime;
				}else{
					++smallQueryErrorCount;
					smallQueryErrorStatusCodes.add(statusCode);
				}
			}else if(queryType.equals(MEDIUM_QUERY)){
				if(statusCode.equals(OK_STATUS_CODE)){
					++mediumQueryCount;
					mediumQueryTotalTime += queryTime;
					mediumQueryFastestTime = queryTime <= mediumQueryFastestTime? queryTime : mediumQueryFastestTime;
					mediumQuerySlowestTime = queryTime >= mediumQuerySlowestTime? queryTime : mediumQuerySlowestTime;
				}else{
					++mediumQueryErrorCount;
					mediumQueryErrorStatusCodes.add(statusCode);
				}
			}else if(queryType.equals(LARGE_QUERY)){
				if(statusCode.equals(OK_STATUS_CODE)){
					++largeQueryCount;
					largeQueryTotalTime += queryTime;
					largeQueryFastestTime = queryTime <= largeQueryFastestTime? queryTime : largeQueryFastestTime;
					largeQuerySlowestTime = queryTime >= largeQuerySlowestTime? queryTime : largeQuerySlowestTime;
				}else{
					++largeQueryErrorCount;
					largeQueryErrorStatusCodes.add(statusCode);
				}
			}else if(queryType.equals(HUGE_QUERY)){
				if(statusCode.equals(OK_STATUS_CODE)){
					++hugeQueryCount;
					hugeQueryTotalTime += queryTime;
					hugeQueryFastestTime = queryTime <= hugeQueryFastestTime? queryTime : hugeQueryFastestTime;
					hugeQuerySlowestTime = queryTime >= hugeQuerySlowestTime? queryTime : hugeQuerySlowestTime;
				}else{
					++hugeQueryErrorCount;
					hugeQueryErrorStatusCodes.add(statusCode);
				}
			}else {
				System.err.println("UNKNOWN QUERY");
				continue;
			}
		}
		
		smallQueryAvgTime = smallQueryCount > 0? smallQueryTotalTime/smallQueryCount : 0;
		smallQueryErrorRate = smallQueryCount > 0 ?(smallQueryErrorCount/smallQueryCount)*100 : 0;
		
		mediumQueryAvgTime = mediumQueryCount > 0? mediumQueryTotalTime/mediumQueryCount : 0;
		mediumQueryErrorRate =  mediumQueryCount > 0? (mediumQueryErrorCount/mediumQueryCount)*100 : 0;
		
		largeQueryAvgTime =  largeQueryCount > 0?largeQueryTotalTime/largeQueryCount : 0;
		largeQueryErrorRate =  largeQueryCount > 0?(largeQueryErrorCount/largeQueryCount)*100 : 0;
		
		hugeQueryAvgTime =  hugeQueryCount > 0? hugeQueryTotalTime/hugeQueryCount : 0;
		hugeQueryErrorRate =  hugeQueryCount > 0? (hugeQueryErrorCount/hugeQueryCount)*100 : 0;
		
		System.out.println("SMALL QUERIES" + DELIMITER + "COUNT: " + smallQueryCount + "\n");
		System.out.println("FASTEST TIME" + DELIMITER + "SLOWEST TIME" + DELIMITER + "AVERAGE_TIME" + DELIMITER + 
					"ERRORS" + DELIMITER + "ERROR_STATUS_CODES");
		System.out.println(smallQueryFastestTime + DELIMITER + 
							smallQuerySlowestTime + DELIMITER + 
							smallQueryAvgTime + DELIMITER + 
							smallQueryErrorCount + DELIMITER + 
							extractErrorCodes(smallQueryErrorStatusCodes) + "\n\n");
		
		System.out.println("MEDIUM QUERIES"  + DELIMITER + "COUNT: " + mediumQueryCount + "\n");
		System.out.println("FASTEST TIME" + DELIMITER + "SLOWEST TIME" + DELIMITER + "AVERAGE_TIME" + DELIMITER + 
				"ERRORS" + DELIMITER + "ERROR_STATUS_CODES");
		System.out.println(mediumQueryFastestTime + DELIMITER + 
							mediumQuerySlowestTime + DELIMITER + 
							mediumQueryAvgTime + DELIMITER + 
							mediumQueryErrorCount + DELIMITER + 
							extractErrorCodes(mediumQueryErrorStatusCodes) + "\n\n");
		
		System.out.println("LARGE QUERIES"  + DELIMITER + "COUNT: " + largeQueryCount +  "\n");
		System.out.println("FASTEST TIME" + DELIMITER + "SLOWEST TIME" + DELIMITER + "AVERAGE_TIME" + DELIMITER + 
				"ERRORS" + DELIMITER + "ERROR_STATUS_CODES");
		System.out.println(largeQueryFastestTime + DELIMITER + 
							largeQuerySlowestTime + DELIMITER + 
							largeQueryAvgTime + DELIMITER + 
							largeQueryErrorCount + DELIMITER + 
							extractErrorCodes(largeQueryErrorStatusCodes) + "\n\n");
		
		System.out.println("HUGE QUERIES"  + DELIMITER + "COUNT: " + hugeQueryCount +  "\n");
		System.out.println("FASTEST TIME" + DELIMITER + "SLOWEST TIME" + DELIMITER + "AVERAGE_TIME" + DELIMITER + 
				"ERRORS" + DELIMITER + "ERROR_STATUS_CODES");
		System.out.println(hugeQueryFastestTime + DELIMITER + 
							hugeQuerySlowestTime + DELIMITER + 
							hugeQueryAvgTime + DELIMITER +
							hugeQueryErrorCount + DELIMITER + 
							extractErrorCodes(hugeQueryErrorStatusCodes) + "\n\n");
		
	}
	
	private static String extractErrorCodes(Set<String> errorCodeSet){
		String errorCodes = "";
		for(String errorCode : errorCodeSet){
			errorCodes += errorCode + ", ";
		}
		
		if(errorCodes.length() > 0){
			errorCodes.trim();
			errorCodes = errorCodes.substring(0, errorCodes.lastIndexOf(","));
			return errorCodes;
		}

		return "N/A";
	}
}
