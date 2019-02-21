package com.cybr406.user.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Component
public class ChangeLogUtil {

  @Autowired
  Environment env;

  public Document document() throws IOException, ParserConfigurationException, SAXException {
    String location = env.getProperty("spring.liquibase.change-log");
    assertNotNull("Change log location must be set.", location);
    assertEquals("classpath:db/changelog/db.changelog-master.xml", location);

    ClassPathResource resource = new ClassPathResource("db/changelog/db.changelog-master.xml");
    assertTrue("Add db.changelog-master.xml to your project.", resource.exists());

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    DocumentBuilder builder = factory.newDocumentBuilder();
    return builder.parse(resource.getInputStream());
  }

  public XPath xpath() {
    XPath xpath = XPathFactory.newInstance().newXPath();
    xpath.setNamespaceContext(new NamespaceContext() {
      public String getNamespaceURI(String prefix) {
        if (prefix == null) throw new NullPointerException("Null prefix");
        else if ("db".equals(prefix)) return "http://www.liquibase.org/xml/ns/dbchangelog";
        else if ("xml".equals(prefix)) return XMLConstants.XML_NS_URI;
        return XMLConstants.NULL_NS_URI;
      }

      // This method isn't necessary for XPath processing.
      public String getPrefix(String uri) {
        throw new UnsupportedOperationException();
      }

      // This method isn't necessary for XPath processing either.
      public Iterator getPrefixes(String uri) {
        throw new UnsupportedOperationException();
      }
    });
    return xpath;
  }
  
}
