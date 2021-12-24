package reportbuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;


/**
 * FXML Controller class
 *
 * @author Trey
 */
public class InternalController implements Initializable {
    
    private FileChooser fileChooser = new FileChooser();
    
    private String VALID_SN = "";
    private String VALID_PW = "";
    private String userName = "";
    public static boolean state = false;
    
    public static String[][] returnedArray = new String[0][0];
    
    public SOAPRequestObject sroInternal = new SOAPRequestObject();
    
    @FXML
    private Label reportStatusLabel;
    
    @FXML
    private Label errorStatusLabel;
    
    @FXML
    private Label userNameLabel;
    
    @FXML
    private Label amsLabel;
    
    @FXML
    private Label reportGenLabel;
    
    @FXML
    private Label versionLabel;
    
    @FXML
    private Button logoutButton;
    
    @FXML
    private Button getReportButton;
    
    @FXML
    private Button saveReportButton;
    
    @FXML
    private HBox progressSpinner;
    
    @FXML
    private HBox logoHBox;

    @FXML
    private ProgressBar reportProgressBar = new ProgressBar(0);
    
    @FXML
    private Button colorModeButton;
    
    @FXML
    private Pane bkgrndPane;
    
    @FXML
    public void colorModeSwitchOnClick (ActionEvent event) {
        changeColorMode(state);
    }
    
    @FXML
    public void logoutButtonHandleAction(ActionEvent event) throws IOException{
        
        reportStatusLabel.setVisible(false);
        reportProgressBar.setVisible(false);
        saveReportButton.setVisible(false);
        progressSpinner.setVisible(false);
        reportStatusLabel.setText("Generating report...");
        
        sroInternal.logout(VALID_SN, VALID_PW);
        
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
        Parent mainControllerParent = (Parent) fxmlLoader.load();
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(new Scene(mainControllerParent));
        
        FXMLDocumentController mainController = fxmlLoader.<FXMLDocumentController>getController();
        mainController.acceptPassedState(state);

        window.show();
    }
 
    @FXML
    private void getResultsButtonHandleAction(ActionEvent event) {
        sroInternal.xtAdvancedSearch(VALID_SN, VALID_PW, reportProgressBar, reportStatusLabel, errorStatusLabel, saveReportButton, progressSpinner);
    }
    
    @FXML
    public void setReturnedArray(String[][] data) {
        returnedArray = data;
    }
    
    @FXML
    public void saveButton() {
        writeToFile(returnedArray);
    }
    
    public void writeToFile(String[][] data) {
        
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
        String date = sdf.format(d);
        
        fileChooser.setTitle("Save file...");
        fileChooser.setInitialFileName(date + "_ReportOutput");
        fileChooser.getExtensionFilters().add( new ExtensionFilter("CSV (Comma Delimited)", "*.csv"));
        
        
        //choose where to save the file
        File file = fileChooser.showSaveDialog(null);
        
        //if a valid location was chosen
        if (file != null) {

            try {
            
                PrintWriter writer = new PrintWriter(file);

                StringBuilder sb = new StringBuilder();

                sb.append("SiteGroupNum,");
                sb.append("Primary Xmit,");
                sb.append("SiteName,");
                sb.append("Phone,");
                sb.append("Address,");
                sb.append("Address2,");
                sb.append("City,");
                sb.append("State,");
                sb.append("ZipCode,");
                sb.append("SiteType,");
                sb.append("PanelType,");
                sb.append("Secondary Xmit,");
                sb.append("Permit,");
                sb.append("Legacy_No,");
                sb.append("BillingID\n");


                for (int i = 0; i < data.length; i++) {

                    for (int j = 1; j < data[i].length; j++) {
                        sb.append("\"");
                        sb.append(data[i][j]);
                        sb.append("\"");
                        sb.append(",");
                    }
                    if (sb.length() > 0) {
                       sb.setLength(sb.length() - 1);
                       sb.append("\n");
                    }

                }


                writer.write(sb.toString());
                writer.flush();
                writer.close();


            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
            }
            
            reportStatusLabel.setText("Report saved!");
            
            
        }
        
        //code to handle what happens if they cancelled the save dialog box
        else {}

    }

    public void acceptValidatedSessionInfo (String vSN, String vPW, String vUN, Boolean vState) {
        VALID_SN = vSN;
        VALID_PW = vPW;
        userName = vUN;
        userNameLabel.setText("Hello, " + vUN + "!");
        state = vState;
        changeColorMode(state);
    }
    
    public void changeColorMode(Boolean stateP) {
        
        if (stateP) {
            amsLabel.getStyleClass().clear(); amsLabel.getStyleClass().add("whiteText");
            userNameLabel.getStyleClass().clear(); userNameLabel.getStyleClass().add("whiteText");
            reportGenLabel.getStyleClass().clear(); reportGenLabel.getStyleClass().add("whiteText");
            versionLabel.getStyleClass().clear(); versionLabel.getStyleClass().add("whiteText");
            reportStatusLabel.getStyleClass().clear(); reportStatusLabel.getStyleClass().add("whiteText");
            errorStatusLabel.getStyleClass().clear(); errorStatusLabel.getStyleClass().add("pinkText");
            bkgrndPane.getStyleClass().clear(); bkgrndPane.getStyleClass().add("darkModeBkgrnd");
            logoHBox.getStyleClass().clear(); logoHBox.getStyleClass().add("prioritySystemsLogoHboxDark");
            colorModeButton.getStyleClass().remove(1); colorModeButton.getStyleClass().add("darkModeOn");
            state = false;
        }
        else {
            amsLabel.getStyleClass().clear(); amsLabel.getStyleClass().add("maroonText");
            userNameLabel.getStyleClass().clear(); userNameLabel.getStyleClass().add("blackText");
            reportGenLabel.getStyleClass().clear(); reportGenLabel.getStyleClass().add("blackText");
            versionLabel.getStyleClass().clear(); versionLabel.getStyleClass().add("blackText");
            reportStatusLabel.getStyleClass().clear(); reportStatusLabel.getStyleClass().add("blueText");
            errorStatusLabel.getStyleClass().clear(); errorStatusLabel.getStyleClass().add("redText");
            bkgrndPane.getStyleClass().clear(); bkgrndPane.getStyleClass().add("lightModeBkgrnd");
            logoHBox.getStyleClass().clear(); logoHBox.getStyleClass().add("prioritySystemsLogoHbox");
            colorModeButton.getStyleClass().remove(1); colorModeButton.getStyleClass().add("darkModeOff");
            state = true;
        }
        
    }
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        String version = this.getClass().getPackage().getImplementationVersion();
        versionLabel.setText("v" + version);
          
    }    
    
    public void manualCloseWindow() {

        sroInternal.logout(VALID_SN, VALID_PW);
        Platform.exit();
    
    }


}
