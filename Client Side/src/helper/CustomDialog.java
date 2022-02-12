package helper;

import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
/**
 *
 * @author moham
 */
public class CustomDialog {
    
   private String name = "";
   private ButtonType buttonTypeOk,buttonTypeCancel;
    public CustomDialog(){}
    
    public CustomDialog(ButtonType buttonTypeOk,ButtonType buttonTypeCancel){
        this.buttonTypeOk = buttonTypeOk;
        this.buttonTypeCancel = buttonTypeCancel;
    }
     public String getName()
     {
      return name;
         
     }
   
    public boolean displayDialog(String message){
        boolean isCancled = false;
               
       Alert alert = new Alert(Alert.AlertType.NONE);
       TextField content = new TextField();
       alert.setTitle("Player Name");
       alert.setHeaderText(message);
       alert.getDialogPane().setContent(content);
    
        ButtonType buttonTypeOk = new ButtonType("Ok");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", 
        ButtonBar.ButtonData.CANCEL_CLOSE);
            
        alert.getButtonTypes().setAll(buttonTypeOk,
                buttonTypeCancel);
   
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
             getClass().getResource("/style/EnterName.css").toExternalForm());
             dialogPane.getStyleClass().add("myDialog");
        
        Optional<ButtonType> result = alert.showAndWait();
       if(result.get() == buttonTypeOk){
         name = content.getText().trim();
          if(name.isEmpty()){
            displayDialog("Name is required");
            isCancled = true;
          }else{
              isCancled = false;
           System.out.println(name);
          }
       }else if(result.get() == buttonTypeCancel){
           isCancled = true;
       }
 
       if(!name.isEmpty() &&isCancled ){
               isCancled = false;
           }
       return isCancled;
    
    }   

    
}
