package reportbuilder;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SOAPRequestObject {
    
    
    
    private Document soapRequest(String xmlInput, String SOAPAction) {
        String wsURL = "https://ams.monitor1.com/stagesgateway/dealer.asmx";
        URL url = null;
        URLConnection connection = null;
        HttpURLConnection httpConn = null;
        String responseString = null;
        String outputString = "";
        ByteArrayOutputStream bout = null;
        OutputStream out = null;
        InputStreamReader isr = null;
        BufferedReader in = null;
        Document document = null;
       
        try {
            url = new URL(wsURL);
            connection = url.openConnection();
            httpConn = (HttpURLConnection) connection;
            
            byte[] buffer = new byte[xmlInput.length()];
            
            buffer = xmlInput.getBytes();
            
            //set appropriate http parameters
            httpConn.setRequestProperty("Content-Length", String.valueOf(buffer.length));
            httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            httpConn.setRequestProperty("SOAPAction", SOAPAction);
            
            httpConn.setRequestMethod("POST");
            
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            
            out = httpConn.getOutputStream();
           
            out.write(buffer);
            out.close();
            
            //read the response and write it to standard out
            isr = new InputStreamReader(httpConn.getInputStream());
            in = new BufferedReader(isr);

            while ((responseString = in.readLine()) != null) {
                outputString = outputString + responseString;
            }
            
            //get response from the web service call
            document = parseXmlFile(outputString);
            
        } catch (Exception e) { e.printStackTrace(); }
        
        return document;
        
    }
    
    private Document parseXmlFile(String in) {
        
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(in));
            return db.parse(is);
        }
        catch (ParserConfigurationException e) { throw new RuntimeException(e);}
        catch (SAXException e) { throw new RuntimeException(e);}
        catch (IOException e) { throw new RuntimeException(e);}
        
    }
    
    public String getUsername(String sn, String pw) {
        
        String returnedUN = "";
        
        String xmlInput = 
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                    "  <soap:Body>\n" +
                    "    <XtLoginInfo xmlns=\"http://tempuri.org/\">\n" +
                    "      <sessionNum>" + sn + "</sessionNum>\n" +
                    "      <sessionPassword>" + pw + "</sessionPassword>\n" +
                    "    </XtLoginInfo>\n" +
                    "  </soap:Body>\n" +
                    "</soap:Envelope>";
        
        String SOAPAction = "http://tempuri.org/XtLoginInfo";
        
        //call the generic soap request method to return a SOAP request document using passed parameters
        Document soapReq = soapRequest(xmlInput, SOAPAction);
        
        //check for successful return
        NodeList success = soapReq.getElementsByTagName("Name");
        String rSuccess = success.item(0).getTextContent();
        returnedUN = rSuccess;
       
        return returnedUN;
        
    }
    
    public String[] login(String un, String pw) {
        
        String[] creds = new String[3];
        
        String xmlInput = 
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                    "  <soap:Body>\n" +
                    "    <Login xmlns=\"http://tempuri.org/\">\n" +
                    "      <userName>" + un + "</userName>\n" +
                    "      <password>" + pw + "</password>\n" +
                    "    </Login>\n" +
                    "  </soap:Body>\n" +
                    "</soap:Envelope>";
        
        String SOAPAction = "http://tempuri.org/Login";
        
        //call the generic soap request method to return a SOAP request document using passed parameters
        Document soapReq = soapRequest(xmlInput, SOAPAction);
        
        //check for successful return
        NodeList success = soapReq.getElementsByTagName("Success");
        String rSuccess = success.item(0).getTextContent();
       
        if (rSuccess.compareTo("true") == 0) {
            
            NodeList sessionNum = soapReq.getElementsByTagName("SessionNum");
            NodeList sessionPassword = soapReq.getElementsByTagName("SessionPassword");
            
            creds[0] = "true";
            creds[1] = sessionNum.item(0).getTextContent();
            creds[2] = sessionPassword.item(0).getTextContent();
            
        }
        
        else {
            creds[0] = "false";
            creds[1] = soapReq.getElementsByTagName("UserErrorMessage").item(0).getTextContent();
        }

        return creds;
        
    }
    
    public void logout(String sn, String pw) {
        
        String xmlInput = 
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                    "  <soap:Body>\n" +
                    "    <Logout xmlns=\"http://tempuri.org/\">\n" +
                    "      <sessionNum>" + sn + "</sessionNum>\n" +
                    "      <sessionPassword>" + pw + "</sessionPassword>\n" +
                    "    </Logout>\n" +
                    "  </soap:Body>\n" +
                    "</soap:Envelope>";
        String SOAPAction = "http://tempuri.org/Logout";
        
        //call the soap request method using passed parameters
        soapRequest(xmlInput, SOAPAction);
          
    }
    
    public void xtAdvancedSearch(String sn, String pw, ProgressBar progressBarPassed, Label statusLabelPassed, Label errorLabelPassed, Button saveButtonPassed, HBox hboxPassed) {
        
        //start a new thread that will run calls in background to allow updating of GUI with progress updates
        Thread taskThread = new Thread(new Runnable() {
            
            @Override
            public void run() {
                
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        progressBarPassed.setVisible(false);
                        saveButtonPassed.setVisible(false);
                        statusLabelPassed.setVisible(true);
                        errorLabelPassed.setVisible(false);
                        hboxPassed.setVisible(true);
                        statusLabelPassed.setText("Compiling account list. This may take awhile...");
                    }
                });
                
                String xmlInput = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                            "<soap:Body>\n" +
                                "<XtAdvancedSearch xmlns=\"http://tempuri.org/\">\n" +
                                    "<sessionNum>" + sn + "</sessionNum>\n" +
                                    "<sessionPassword>" + pw + "</sessionPassword>\n" +
                                    //"<SiteName>%</SiteName>\n" +
                                    "<TransmitterCode>A%00%</TransmitterCode>\n" +
                                    "<IncludeOOSFlag>false</IncludeOOSFlag>\n" +
                                    //"<MaxRows>10</MaxRows>\n" +
                                "</XtAdvancedSearch>\n" +
                            "</soap:Body>\n" +
                        "</soap:Envelope>";

                String SOAPAction = "http://tempuri.org/XtAdvancedSearch";

                //call the generic soap request method to return a SOAP request document using passed parameters
                Document soapReq = soapRequest(xmlInput, SOAPAction);

                //get # of rows returned and store as variable
                NodeList resultSet = soapReq.getElementsByTagName("XtAdvancedSearchResult");
                Integer numRows = resultSet.getLength() - 1;

                if (numRows > 0) {

                    //create array of arrays to store the account data
                    String[][] accountArray = new String[numRows][16];
                    
                    /*********/
                    //create double parameter to be able to pass to progress bar
                    final Double progressInc = 1.0 / numRows;
                    /*********/
                    
                    //get all the transmitter code elements
                    NodeList priXmitF = soapReq.getElementsByTagName("TransmitterCode");
                    
                    //set default progress of progress bar to 0
                    Double progress = 0.0;

                    try {
                        Thread.sleep(1);
                        Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    hboxPassed.setVisible(false);
                                    progressBarPassed.setVisible(true);
                                    statusLabelPassed.setText("" + numRows + " accounts found. Generating report...");
                                }
                        });

                        //cycle through all results
                        for (int i = 0; i < priXmitF.getLength(); i++) {

                            Node parent = priXmitF.item(i).getParentNode();
                            NodeList child = parent.getChildNodes();

                            //cycle through all the child nodes of each xmit
                            for (int j = 0; j < child.getLength(); j++) {
                                switch (child.item(j).getNodeName()) {
                                    case "SiteNum": accountArray[i][0] = child.item(j).getTextContent(); continue;
                                    case "TransmitterCode": accountArray[i][2] = child.item(j).getTextContent(); continue;
                                    case "SiteName" : accountArray[i][3] = child.item(j).getTextContent(); continue;
                                    case "Phone":  accountArray[i][4] = child.item(j).getTextContent(); continue;
                                    case "Address" : accountArray[i][5] = child.item(j).getTextContent(); continue;
                                    case "Address2" : accountArray[i][6] = checkIfNull(child.item(j).getTextContent()); continue;
                                    case "City" : accountArray[i][7] = child.item(j).getTextContent(); continue;
                                    case "State" : accountArray[i][8] = child.item(j).getTextContent(); continue;
                                    case "ZipCode" : accountArray[i][9] = child.item(j).getTextContent(); continue;
                                    case "SiteTypeDescription" : accountArray[i][10] = child.item(j).getTextContent(); continue;
                                    case "DeviceTypeDescription" : accountArray[i][11] = checkIfNull(child.item(j).getTextContent()); continue;
                                    case "XmitList" : accountArray[i][12] = checkIfNull(child.item(j).getTextContent()); continue;
                                    case "SiteID2" : accountArray[i][14] = child.item(j).getTextContent(); 
                                    
                                    break;
                                }
                            }
                            
                            //call the permit list for the specific item
                            accountArray[i][13] = checkIfNull(sitePermitList(sn, pw, accountArray[i][0]));
                            
                            //accountArray[i][14] = siteDetail(sn, pw, accountArray[i][0]);
                            accountArray[i][15] = siteDetail(sn, pw, accountArray[i][0]);

                            //call the site group link list for the specific item
                            accountArray[i][1] = checkIfNull(siteGroupLinkList(sn, pw, accountArray[i][0]));

                            progress += progressInc;
                            final Double reportedProgress = progress; 

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {

                                    progressBarPassed.setProgress(reportedProgress);

                                }
                            });

                        }
                        
                        Platform.runLater(new Runnable() {
                                @Override
                                public void run() {

                                    statusLabelPassed.setText("Report Complete!");
                                    progressBarPassed.setVisible(false);
                                    saveButtonPassed.setVisible(true);
                                    
                                }
                        });

                    } catch (InterruptedException e) {
                      e.printStackTrace();
                    }
                    
                    
                    //call back to internal controller to write the file
                    InternalController ic = new InternalController();
                    ic.setReturnedArray(accountArray);
                    
                }

                else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            
                            statusLabelPassed.setVisible(false);
                            hboxPassed.setVisible(false);
                            errorLabelPassed.setText("No Results Found. Your session has expired.\n"
                                    + "Please logout and back in then try again.\n"
                                    + "If the issue persists, please notify Trey at AMS.");
                            errorLabelPassed.setVisible(true);
                        }
                    });
                }
                
            }
        });
        taskThread.start();
        
    }
    
    public String sitePermitList (String sn, String pw, String sNum) {
        String xmlInput = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
            "  <soap:Body>\n" +
            "    <SitePermitList xmlns=\"http://tempuri.org/\">\n" +
            "      <sessionNum>" + sn + "</sessionNum>\n" +
            "      <sessionPassword>" + pw + "</sessionPassword>\n" +
            "      <SiteNum>" + sNum + "</SiteNum>\n" +
            "      <AgencyNum>30000005</AgencyNum>\n" +
            "    </SitePermitList>\n" +
            "  </soap:Body>\n" +
            "</soap:Envelope>";
        
        String SOAPAction = "http://tempuri.org/SitePermitList";
        
        //call the generic soap request method to return a SOAP request document using passed parameters
        Document soapReq = soapRequest(xmlInput, SOAPAction);
        
        //create array of arrays to store the account data
        String permitResults = "null";

        //get node list of results
        NodeList site = soapReq.getElementsByTagName("Permit");
        
        if (site.getLength() > 0) {
            
            permitResults = site.item(0).getTextContent();
            
        }
        
        
        return permitResults;
    }
    
    public String siteGroupLinkList (String sn, String pw, String sNum) {
        String xmlInput = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
            "  <soap:Body>\n" +
            "    <SiteGroupLinkList xmlns=\"http://tempuri.org/\">\n" +
            "      <sessionNum>" + sn + "</sessionNum>\n" +
            "      <sessionPassword>" + pw + "</sessionPassword>\n" +
            "      <SiteNum>" + sNum + "</SiteNum>\n" +
            "    </SiteGroupLinkList>\n" +
            "  </soap:Body>\n" +
            "</soap:Envelope>";
        
        String SOAPAction = "http://tempuri.org/SiteGroupLinkList";
        
        //call the generic soap request method to return a SOAP request document using passed parameters
        Document soapReq = soapRequest(xmlInput, SOAPAction);
        
        //create array of arrays to store the account data
        String sgllResults = null;

        //get node list of results
        NodeList site = soapReq.getElementsByTagName("SiteGroupSearch");
        
        sgllResults = site.item(0).getTextContent();
        
        return sgllResults;
    }
    
    public String siteDetail (String sn, String pw, String sNum) {
        String xmlInput = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
            "  <soap:Body>\n" +
            "    <SiteDetail xmlns=\"http://tempuri.org/\">\n" +
            "      <sessionNum>" + sn + "</sessionNum>\n" +
            "      <sessionPassword>" + pw + "</sessionPassword>\n" +
            "      <SiteNum>" + sNum + "</SiteNum>\n" +
            "    </SiteDetail>\n" +
            "  </soap:Body>\n" +
            "</soap:Envelope>";
        
        String SOAPAction = "http://tempuri.org/SiteDetail";
        
        //call the generic soap request method to return a SOAP request document using passed parameters
        Document soapReq = soapRequest(xmlInput, SOAPAction);
        
        //create variable to return account data
        String sdResults = null;

        //get node list of results
        NodeList site = soapReq.getElementsByTagName("BillingID");
        
        if (site.getLength() > 0) {
            sdResults = site.item(0).getTextContent();
        }
        
        return sdResults;
        
    }
    
    private String checkIfNull (String value) {
        
        if (value.isEmpty() || value.compareTo(" ") == 0) { return "null"; }
        else {return value;}
    
    }

}