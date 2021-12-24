package reportbuilder;

import java.io.IOException;
import java.net.URL;
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
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 *
 * @author Trey
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private Pane bkgrndPane;
    
    @FXML
    private Button colorModeButton;
        
    @FXML
    private TextField unField;
    
    @FXML
    private TextField pwField;
    
    @FXML
    private Label unLabel;
    
    @FXML
    private Label pwLabel;
    
    @FXML
    private Label amsLabel;
    
    @FXML
    private Label reportGenLabel;
    
    @FXML
    private Label versionLabel;
    
    @FXML
    private Label errorMessageLabel;
    
    @FXML
    private HBox logoHBox;
    
    public SOAPRequestObject sro = new SOAPRequestObject();
    
    public boolean state = false;
    
    @FXML
    public void onEnter(ActionEvent ae) throws IOException{
       handleLoginButtonAction(ae);
    }
    
    public void acceptPassedState(Boolean stateP){
        state = stateP;
        changeColors(state);
    }
    
    @FXML
    public void colorModeSwitchOnClick (ActionEvent event) {
        
        changeColors(state);
    }
    
    public void changeColors(Boolean stateP) {
        
        if (stateP) {
            amsLabel.getStyleClass().clear(); amsLabel.getStyleClass().add("maroonText");
            unLabel.getStyleClass().clear(); unLabel.getStyleClass().add("blackText");
            pwLabel.getStyleClass().clear(); pwLabel.getStyleClass().add("blackText");
            reportGenLabel.getStyleClass().clear(); reportGenLabel.getStyleClass().add("blackText");
            versionLabel.getStyleClass().clear(); versionLabel.getStyleClass().add("blackText");
            errorMessageLabel.getStyleClass().clear(); errorMessageLabel.getStyleClass().add("redText");
            bkgrndPane.getStyleClass().clear(); bkgrndPane.getStyleClass().add("lightModeBkgrnd");
            logoHBox.getStyleClass().clear(); logoHBox.getStyleClass().add("prioritySystemsLogoHbox");
            colorModeButton.getStyleClass().remove(1); colorModeButton.getStyleClass().add("darkModeOff");
            state = false;
        }
        else {
            amsLabel.getStyleClass().clear(); amsLabel.getStyleClass().add("whiteText");
            unLabel.getStyleClass().clear(); unLabel.getStyleClass().add("whiteText");
            pwLabel.getStyleClass().clear(); pwLabel.getStyleClass().add("whiteText");
            reportGenLabel.getStyleClass().clear(); reportGenLabel.getStyleClass().add("whiteText");
            versionLabel.getStyleClass().clear(); versionLabel.getStyleClass().add("whiteText");
            bkgrndPane.getStyleClass().clear(); bkgrndPane.getStyleClass().add("darkModeBkgrnd");
            errorMessageLabel.getStyleClass().clear(); errorMessageLabel.getStyleClass().add("pinkText");
            logoHBox.getStyleClass().clear(); logoHBox.getStyleClass().add("prioritySystemsLogoHboxDark");
            colorModeButton.getStyleClass().remove(1); colorModeButton.getStyleClass().add("darkModeOn");
            state = true;
        }
        
    }
    
    @FXML
    private void handleLoginButtonAction(ActionEvent event) throws IOException {
        
        String un = unField.getText();
        String pw = pwField.getText();
        String validSN = "";
        String validPW = "";
        String validUN = "";
        
        /**********************************/
        String[] credentials = null;

        
        if (un.isEmpty() || pw.isEmpty()) {
            errorMessageLabel.setText("Please enter a username and password.");
            errorMessageLabel.setVisible(true);
            unField.requestFocus();
        }
        
        else {
            
            errorMessageLabel.setVisible(false);
        
            credentials = sro.login(un, pw);

            //check if valid login
            if (credentials[0].compareTo("true") == 0) {

                validSN = credentials[1];
                validPW = credentials[2];
                validUN = sro.getUsername(validSN, validPW);

                System.out.println("" + validSN + "\n" + validPW + "\nUser: " + validUN);

                //load new scene and pass validated info parameters to be used for future logged in methods
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("internal.fxml"));
                    Parent internalControllerParent = (Parent) fxmlLoader.load();
                    Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
                    window.setScene(new Scene(internalControllerParent));

                    InternalController controller = fxmlLoader.<InternalController>getController();
                    controller.acceptValidatedSessionInfo(validSN, validPW, validUN, state);

                    window.setOnCloseRequest(e -> { controller.manualCloseWindow(); });

                    window.show();

                } catch(Exception e) {
                    e.printStackTrace();
                }

            }

            else {
                if (credentials[1].compareTo("This account is locked. Contact an administrator to unlock.") == 0){
                    
                    errorMessageLabel.setText("Your account is locked.\nPlease contact Dealer Services to unlock your account.");
                    
                }

                else if (credentials[1].compareTo("Your password has expired. Change your password to log in.") == 0) {
                    
                    errorMessageLabel.setText("Your password has expired. Please update your\npassword via the web portal or contact Dealer Services.");
                    
                }

                else {
                    
                    errorMessageLabel.setText("Invalid username or password.  Please try again.");
                    
                }

                unField.requestFocus();
                errorMessageLabel.setVisible(true);

            }
        }

    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        String version = this.getClass().getPackage().getImplementationVersion();
        versionLabel.setText("v" + version);
        
        changeColors(state);
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                unField.requestFocus();
            }
        });
        
        
    }    
    
}
