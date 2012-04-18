package org.topbraid.jmeter.reader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class JtlToCsvTranslator {
	public static final String IN_FILE = "listener.jtl";
	public static final String OUT_CSV_FILE = "listener.csv";
	public static final String ELEMENT_NAME = "httpSample";
	public static final String LT_ATTRIBUTE = "lt";
	public static final String T_ATTRIBUTE = "t";
	public static final String RC_ATTRIBUTE = "rc";
	public static final String RM_ATTRIBUTE = "rm";
	public static final String LB_ATTRIBUTE = "lb";
	public static final String DELIMITER = ",";
	
	public static final String SMALL_QUERY = "GetSmall";
	public static final String MEDIUM_QUERY = "GetMedium";
	public static final String LARGE_QUERY = "GetLarge";
	public static final String HUGE_QUERY = "GetHuge";
	
	public static final String OK_STATUS_CODE= "200";
	
	Map<String, Set<String>> errorCodesByQuery;
	Set<String> errorCodesForSmallQuery;
	Set<String> errorCodesForMediumQuery;
	Set<String> errorCodesForLargeQuery;
	Set<String> errorCodesForHugeQuery;
	
	Map<String, Integer> errorCountByQuery;
	int errorCountForSmallQuery, errorCountForMediumQuery, errorCountForLargeQuery, errorCountForHugeQuery, totalQueryCount;
	
	
	public JtlToCsvTranslator(){
		errorCodesByQuery = new HashMap<String, Set<String>>();
		errorCodesForSmallQuery = new HashSet<String>();
		errorCodesForMediumQuery = new HashSet<String>();
		errorCodesForLargeQuery = new HashSet<String>();
		errorCodesForHugeQuery = new HashSet<String>();
		errorCountByQuery = new HashMap<String, Integer>();
	}

	private void parseJtlFile(){
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
			writeCsvFile(nodeList);
		else{
			System.err.println("Node List NOT FOUND");
			return;
		}
	}
	
	private void writeCsvFile(NodeList nodeList){
		Node node;
		NamedNodeMap attributeValueMap;
		int queryTime;
		String queryType, statusCode, queryTimeString;
		PrintWriter writer = null;
		try{
			writer = new PrintWriter(new FileWriter(new File(OUT_CSV_FILE)));
		} catch(IOException ioe){
			return;
		}
		
		if(writer != null){
			try{
				for (int i=0; i < 500000; i++){
					++totalQueryCount;
					node = nodeList.item(i);
					attributeValueMap = node.getAttributes();

					statusCode = attributeValueMap.getNamedItem(RC_ATTRIBUTE).getNodeValue();
					queryType = attributeValueMap.getNamedItem(LB_ATTRIBUTE).getNodeValue();
					queryTimeString = attributeValueMap.getNamedItem(T_ATTRIBUTE).getNodeValue();
					queryTime = Integer.parseInt(queryTimeString);

					if(statusCode.equals(OK_STATUS_CODE))
						writer.println(queryType + DELIMITER + queryTime);
					else{
						addErrorStatusCodeToQuery(statusCode, queryType);
					}
				}
			}catch(OutOfMemoryError oome){

			}finally{
				writer.flush();
				writer.close();
				System.out.println("Total Queries:" + totalQueryCount);
			}
		}
		
		updateErrorCodesAndCountsByQueryMap();
		writeErrorCodesToOutput();
	}

	

	private void updateErrorCodesAndCountsByQueryMap() {
		errorCodesByQuery.put(SMALL_QUERY, errorCodesForSmallQuery);
		errorCodesByQuery.put(MEDIUM_QUERY, errorCodesForMediumQuery);
		errorCodesByQuery.put(LARGE_QUERY, errorCodesForLargeQuery);
		errorCodesByQuery.put(HUGE_QUERY, errorCodesForHugeQuery);
		
		errorCountByQuery.put(SMALL_QUERY, errorCountForSmallQuery);
		errorCountByQuery.put(MEDIUM_QUERY, errorCountForMediumQuery);
		errorCountByQuery.put(LARGE_QUERY, errorCountForLargeQuery);
		errorCountByQuery.put(HUGE_QUERY, errorCountForHugeQuery);
	}

	private void addErrorStatusCodeToQuery(String statusCode, String queryType) {
		if(queryType.equals(SMALL_QUERY)){
			errorCodesForSmallQuery.add(statusCode);
			++errorCountForSmallQuery;
		} else if (queryType.equals(MEDIUM_QUERY)){
			errorCodesForMediumQuery.add(statusCode);
			++errorCountForMediumQuery;
		} else if (queryType.equals(LARGE_QUERY)){
			errorCodesForLargeQuery.add(statusCode);
			++errorCountForLargeQuery;
		} else if (queryType.equals(HUGE_QUERY)){
			errorCodesForHugeQuery.add(statusCode);
			++errorCountForHugeQuery;
		} 
	}

	private void writeErrorCodesToOutput() {
		for(String queryName : errorCodesByQuery.keySet()){
			System.out.print(queryName + "\t\t");
			System.out.println(errorCountByQuery.get(queryName));
			Set<String> errorCodeSet = errorCodesByQuery.get(queryName);
			System.out.println(extractErrorCodes(errorCodeSet));
		}
	}
	
	private String extractErrorCodes(Set<String> errorCodeSet){
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
	
	
	public static void main(String[] args){
		JtlToCsvTranslator translator = new JtlToCsvTranslator();
		translator.parseJtlFile();
	}
	
}
