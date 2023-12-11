package application;

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
			Button btnDes = new Button();
			btnCaesarE.setText("Caesar Cipher");
			btnDes.setText("DES Cipher");
			vbox.getChildren().addAll(btnCaesarE, btnDes);
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
				inputVBox1.getChildren().addAll( textLabel, textField, keyLabel, keyField, cb, submitButton, resultField);
				root.setCenter(inputVBox1);
	             
			});
			// action for button btnDes
			
			btnDes.setOnAction(e -> {
				VBox inputVBox2 = new VBox();
				inputVBox2.setPadding(new Insets(20, 20, 20, 20));
				inputVBox2.setSpacing(10);
				Label textLabel = new Label("Enter your text:");
				TextField textField = new TextField();
				Label keyLabel = new Label("Enter your key:");
				TextField keyField = new TextField();
				RadioButton rb1 = new RadioButton();			
				rb1.setText("DES");
				RadioButton rb2 = new RadioButton();
				rb2.setText("AES");
				TextField resultField = new TextField();
				resultField.setEditable(false);
				
				Button submitButton = new Button("Submit");
				submitButton.setOnAction(event -> {					
				});
				
				inputVBox2.getChildren().addAll( textLabel, textField, keyLabel, keyField, rb1, rb2, submitButton, resultField);
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
