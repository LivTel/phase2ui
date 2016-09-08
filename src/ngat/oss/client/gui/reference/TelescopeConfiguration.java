/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.reference;

import ngat.oss.client.gui.wrapper.SlideArrangementsContainer;
import ngat.oss.client.gui.wrapper.SlideArrangement;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import ngat.oss.client.gui.frame.LoginFrame;
import ngat.oss.client.gui.wrapper.Clear;
import ngat.oss.client.gui.wrapper.Dichroic;
import ngat.oss.client.gui.wrapper.Mirror;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author nrc
 */
public class TelescopeConfiguration {

    static Logger logger = Logger.getLogger(TelescopeConfiguration.class);

    private static TelescopeConfiguration instance;

    private SlideArrangementsContainer slideArrangementContainer = new SlideArrangementsContainer();

    public static TelescopeConfiguration getInstance() {
        if (instance == null) {
            instance = new TelescopeConfiguration();
        }
        return instance;
    }

    private TelescopeConfiguration() {
        /*
        .
        .
        .
        .
         */
        loadSlideArrangements();
        //debugShowArrangements();
    }

    public SlideArrangementsContainer getSlideArrangementsContainer() {
        return slideArrangementContainer;
    }

    private void debugShowArrangements() {
        System.err.println(slideArrangementContainer);
    }

    private void loadSlideArrangements() {

        logger.info("loadSlideArrangements()");

        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

            //load the document
            Document doc;
            File localXMLConfigFile = new File(CONST.DEFAULT_OPTICAL_CONFIG_LOCAL_FILE_LOCATION);
            if (localXMLConfigFile.exists()) {
                //use the local file if it's there
                logger.info("using " + CONST.DEFAULT_OPTICAL_CONFIG_LOCAL_FILE_LOCATION);
                doc = docBuilder.parse(localXMLConfigFile);
            } else {
                //otherwise use the file on the webserver
                //URI uri = new URI(CONST.DEFAULT_OPTICAL_CONFIG_WEB_FILE_LOCATION);
                URL url = new URL(CONST.DEFAULT_OPTICAL_CONFIG_WEB_FILE_LOCATION);
                logger.info("using " + url.getPath());
                doc = docBuilder.parse(url.openStream());
                //System.err.println(doc.getInputEncoding() + " " + doc.getXmlVersion());
            }
            
            // normalize text representation
            doc.getDocumentElement().normalize();
            //System.out.println ("Root element of the doc is " +doc.getDocumentElement().getNodeName()); //should be 'services'
            NodeList opticalElementsList = doc.getElementsByTagName("optical-elements");

            //System.err.println("opticalElementsList.getLength()=" + opticalElementsList.getLength());

            for (int s = 0; s < opticalElementsList.getLength(); s++) {
                Node opticalElementNode = opticalElementsList.item(s);

                if (opticalElementNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element opticalElementElement = (Element) opticalElementNode;
                    NodeList slideArrangementList = opticalElementElement.getElementsByTagName("slide-arrangement");

                    //System.err.println("slideArrangementList.getLength()=" + slideArrangementList.getLength());

                    for (int i=0; i<slideArrangementList.getLength(); i++) {
                        Element slideArrangementElement = (Element) slideArrangementList.item(i);
                        SlideArrangement sa = getSlideArrangementFromElement(slideArrangementElement);
                        slideArrangementContainer.addSlideArrangement(sa);
                    }
                }
            }
            logger.info("loadSlideArrangements() SUCCESSFUL");
        } catch (SAXParseException err) {
            String m = "Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId() + " " + err.getMessage();
            logger.error(m);
        } catch (SAXException e) {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();
            logger.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    private SlideArrangement getSlideArrangementFromElement(Element slideArrangementElement) throws Exception {
        
        String nameText;
        try {
            Node nameNode = slideArrangementElement.getElementsByTagName("name").item(0);
            NodeList nodesInNameNode = nameNode.getChildNodes();
            nameText = nodesInNameNode.item(0).getNodeValue().trim();
        }catch (Exception e) {
            throw new Exception("Problem with name node in slide-arrangement (may not exist)");
        }

        String positionText;
        try {
            Node positionNode = slideArrangementElement.getElementsByTagName("position").item(0);
            NodeList nodesInPositionNode = positionNode.getChildNodes();
            positionText = nodesInPositionNode.item(0).getNodeValue().trim();
        } catch (Exception e) {
            throw new Exception("Problem with position node in slide-arrangement (may not exist)");
        }

        int position;
        try {
            position = Integer.parseInt(positionText);
        } catch (Exception e) {
            throw new Exception("Position is not a numeric");
        }
        
        Node opticalElementsNode = slideArrangementElement.getElementsByTagName("elements").item(0);
        List opticalElements = getOpticalElements((Element)opticalElementsNode);
        
        SlideArrangement slideArrangement = new SlideArrangement(nameText, position);
        slideArrangement.setOpticalSlideElements(opticalElements);
        return slideArrangement;
    }

    private List getOpticalElements(Element elementsNode) throws Exception {

        NodeList dichroicChildNodeList = elementsNode.getElementsByTagName("dichroic");
        NodeList mirrorChildNodeList = elementsNode.getElementsByTagName("mirror");
        NodeList clearChildNodeList = elementsNode.getElementsByTagName("clear");

        List dichroicElementsList = getDichroicsList(dichroicChildNodeList);
        List mirrorElementsList = getMirrorsList(mirrorChildNodeList);
        List clearElementsList = getClearsList(clearChildNodeList);

        List allElementsList = new ArrayList();
        allElementsList.addAll(dichroicElementsList);
        allElementsList.addAll(mirrorElementsList);
        allElementsList.addAll(clearElementsList);
        
        return allElementsList;
    }

    private List getDichroicsList(NodeList dichroicChildNodeList) throws Exception {
        ArrayList dichroicsList = new ArrayList();

        for (int i=0; i<dichroicChildNodeList.getLength(); i++) {
            Element dichroicElement = (Element)dichroicChildNodeList.item(i);

            //get position
            Node positionNode = dichroicElement.getElementsByTagName("position").item(0);
            if (positionNode ==null) {
                throw new Exception("unable to find position node");
            }
            NodeList nodesInPositionNode = positionNode.getChildNodes();
            String positionText = nodesInPositionNode.item(0).getNodeValue().trim();
            int position;
            try {
                position = Integer.parseInt(positionText);
            } catch (Exception e) {
                throw new Exception("position value is not a numeric");
            }

            //get name
            Node nameNode = dichroicElement.getElementsByTagName("name").item(0);
            if (nameNode ==null) {
                throw new Exception("unable to find name node");
            }
            NodeList nodesInNameNode = nameNode.getChildNodes();
            String name = nodesInNameNode.item(0).getNodeValue().trim();

            //get first-colour
            Node firstColourName = dichroicElement.getElementsByTagName("first-colour").item(0);
            if (firstColourName ==null) {
                throw new Exception("unable to find first-colour node");
            }
            NodeList nodesInFirstColourNode = firstColourName.getChildNodes();
            String firstColour = nodesInFirstColourNode.item(0).getNodeValue().trim();

            //get second-colour
            Node secondColourName = dichroicElement.getElementsByTagName("second-colour").item(0);
            if (secondColourName ==null) {
                throw new Exception("unable to find second-colour node");
            }
            NodeList nodesInSecondColourNode = secondColourName.getChildNodes();
            String secondColour = nodesInSecondColourNode.item(0).getNodeValue().trim();

            Dichroic dichroic = new Dichroic(position, name, firstColour, secondColour);
            dichroicsList.add(dichroic);
        }

        return dichroicsList;
    }

    private List getMirrorsList(NodeList mirrorChildNodeList) throws Exception {
        ArrayList mirrorsList = new ArrayList();

        for (int i=0; i<mirrorChildNodeList.getLength(); i++) {
            Element mirrorElement = (Element)mirrorChildNodeList.item(i);

            //get position
            Node positionNode = mirrorElement.getElementsByTagName("position").item(0);
            if (positionNode ==null) {
                throw new Exception("unable to find position node in mirror element");
            }
            NodeList nodesInPositionNode = positionNode.getChildNodes();
            String positionText = nodesInPositionNode.item(0).getNodeValue().trim();
            int position;
            try {
                position = Integer.parseInt(positionText);
            } catch (Exception e) {
                throw new Exception("position value is not a numeric");
            }

            //get name
            Node nameNode = mirrorElement.getElementsByTagName("name").item(0);
            if (positionNode ==null) {
                throw new Exception("unable to find name node in mirror element");
            }
            NodeList nodesInNameNode = nameNode.getChildNodes();
            String name = nodesInNameNode.item(0).getNodeValue().trim();

            Mirror mirror = new Mirror(position, name);
            mirrorsList.add(mirror);
        }
        return mirrorsList;
    }

    private List getClearsList(NodeList clearElementsList) throws Exception {
        ArrayList clearsList = new ArrayList();

        for (int i=0; i<clearElementsList.getLength(); i++) {
            Element mirrorElement = (Element)clearElementsList.item(i);

            //get position
            Node positionNode = mirrorElement.getElementsByTagName("position").item(0);
            if (positionNode ==null) {
                throw new Exception("unable to find position node in clear element");
            }
            NodeList nodesInPositionNode = positionNode.getChildNodes();
            String positionText = nodesInPositionNode.item(0).getNodeValue().trim();
            int position;
            try {
                position = Integer.parseInt(positionText);
            } catch (Exception e) {
                throw new Exception("position value is not a numeric");
            }
            Clear clear = new Clear(position);
            clearsList.add(clear);
        }
        return clearsList;
    }

}
