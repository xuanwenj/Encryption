package application;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {

		try {
			BorderPane root = new BorderPane();
			// VBox for buttons on the left
			VBox vbox = new VBox();
			vbox.setPadding(new Insets(20, 20, 20, 20));
			vbox.setSpacing(10);

			Button btnCaesarE = new Button();
			Button btnModern = new Button();
			btnCaesarE.setText("Caesar Cipher");
			btnModern.setText("DES Cipher");
			vbox.getChildren().addAll(btnCaesarE, btnModern);
			root.setLeft(vbox);
			// action for button btnCaesarE
			
			btnCaesarE.setOnAction(e -> {
				
				VBox inputVBox1 = new VBox();
				inputVBox1.setPadding(new Insets(20, 20, 20, 20));
				inputVBox1.setSpacing(10);
				Label textLabel = new Label("Enter your text:");
				TextField textField = new TextField();
				Label keyLabel = new Label("Enter your key:");
				TextField keyField = new TextField();

				ChoiceBox<String> cb = new ChoiceBox<String>(FXCollections.observableArrayList("Encrypt", "Decrypt"));
				cb.setValue("Encrypt");
				Label resultLable = new Label("Result:");
				TextField resultField = new TextField();
				resultField.setEditable(false);

				Button submitButton = new Button("Submit");
				submitButton.setOnAction(event -> {

					String text = textField.getText();
					int key = Integer.parseInt(keyField.getText());
					Caesar c = new Caesar();
					String result;
					if (cb.getValue().equals("Encrypt")) {
						result = c.encrypt(text.toUpperCase(), key);
					} else {
						result = c.dencrypt(text.toUpperCase(), key);
					}

					resultField.setText(result);
				
				});
				inputVBox1.getChildren().addAll( textLabel, textField, keyLabel, keyField, cb, submitButton, resultLable,resultField);
				root.setCenter(inputVBox1);
	             
			});
			// action for button btnDes
			
			btnModern.setOnAction(e -> {
				VBox inputVBox2 = new VBox();
				inputVBox2.setPadding(new Insets(20, 20, 20, 20));
				inputVBox2.setSpacing(10);
				Label textLabel = new Label("Enter your text:");
				TextField textField = new TextField();
				Label keyLabel = new Label("Enter your key:");
				TextField keyField = new TextField();
				
				HBox row1 = new HBox();
				ToggleGroup group1 = new ToggleGroup();
				RadioButton rb1 = new RadioButton();			
				rb1.setText("DES");
				rb1.setToggleGroup(group1);
				RadioButton rb2 = new RadioButton();
				rb2.setText("AES");
				rb2.setToggleGroup(group1);
				row1.getChildren().addAll(rb1, rb2);
				
				
				HBox row2 = new HBox();
				ToggleGroup group2 = new ToggleGroup();
				RadioButton rb3 = new RadioButton();
				rb3.setText("Encrypt");
				rb3.setToggleGroup(group2);
				RadioButton rb4 = new RadioButton();
				rb4.setText("Decrypt");
				rb4.setToggleGroup(group2);
				row2.getChildren().addAll(rb3, rb4);
				
				HBox row3 = new HBox();
				ToggleGroup group3 = new ToggleGroup();
				RadioButton rb5 = new RadioButton();
				rb5.setText("Generate a Key");
				rb5.setToggleGroup(group3);
				
				RadioButton rb6 = new RadioButton();
				rb6.setText("Use Your Own Key");
				rb6.setToggleGroup(group3);
				row3.getChildren().addAll(rb5, rb6);
				
				row1.setSpacing(80);
				row2.setSpacing(70);
				row3.setSpacing(40);
				
				Label resultLable = new Label("Result:");
				TextField resultField = new TextField();
				resultField.setEditable(false);
				
				Button submitButton = new Button("Submit");
				submitButton.setOnAction(event -> {
					 try {
						String text = textField.getText();
						String key = keyField.getText();
						DESSimple des1 = new DESSimple();
						if (rb1.isSelected()&& rb3.isSelected()) {
							byte[] encryptedData;
							try {
								encryptedData = des1.encrypt(text);
								String encryptedText = Base64.getEncoder().encodeToString(encryptedData);
								resultField.setText(encryptedText);
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
												
						}else if(rb1.isSelected()&& rb4.isSelected()) {
							byte[] decryptedData;
							try {
								decryptedData = Base64.getDecoder().decode(text);
								String decryptedText = des1.decrypt(decryptedData);
								resultField.setText(decryptedText);
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							
						}else if(rb2.isSelected()&& rb3.isSelected()) {
							
						}else if(rb2.isSelected()&& rb4.isSelected()) {
							
						}
						
											
					} catch (NoSuchAlgorithmException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}					 
				});
				
				inputVBox2.getChildren().addAll( textLabel, textField, keyLabel, keyField, row3, row1, row2, submitButton, resultLable, resultField);
				root.setCenter(inputVBox2);
				
				//end of btn
			});
			
			//the main scene
			Scene scene = new Scene(root, 400, 400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
