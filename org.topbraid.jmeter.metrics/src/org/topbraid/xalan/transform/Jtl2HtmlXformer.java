package org.topbraid.xalan.transform;

import java.io.FileOutputStream;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


public class Jtl2HtmlXformer {
	public static void main(String[] args){
		TransformerFactory factory = TransformerFactory.newInstance();
		Source xslFile = new StreamSource("Jtl2Html.xsl");
		Source inputXmlFile = new StreamSource("listenerRamWithTrue.jtl");
		try{
			StreamResult outputFile = new StreamResult(new FileOutputStream("listenerRamWithTrue.html"));
			Transformer transformer = factory.newTransformer(xslFile);
			transformer.transform(inputXmlFile, outputFile);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
