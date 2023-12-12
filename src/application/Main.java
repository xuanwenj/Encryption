package application;

import java.beans.Statement;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class Main extends Application {
	static final String JDBC_URL = "jdbc:mysql://127.0.0.1:3306/ebookshop";
	static final String USERNAME = "root";
	static final String PASSWORD = ""; // null password
	List<String> algorithmList = new ArrayList<>();
	

	DESSimple des1;
	
	 public List<String> fetchNames() throws SQLException {
		   try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		
		    // Open a connection
		    Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);		   
		    java.sql.Statement statement = connection.createStatement();
		    String sql = "SELECT * FROM algorithms";
		    ResultSet resultSet = statement.executeQuery(sql);
		    //            1/ Go through the result set to print it
		    while (resultSet.next()) {
		    // Retrieve data by column name
		        String algorithmName = resultSet.getString("name");
		        algorithmList.add(algorithmName);
		        
		    }
		    resultSet.close();
		    statement.close();
		    connection.close();	
		   } catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		//Close external resources
		    
			return algorithmList;
					}
	
	
	
	
	@Override
	public void start(Stage primaryStage) throws NoSuchAlgorithmException {
		des1 = new DESSimple();
		try {
			BorderPane root = new BorderPane();
			// VBox for buttons on the left
			VBox vbox = new VBox();
			vbox.setPadding(new Insets(20, 20, 20, 20));
			vbox.setSpacing(10);
			
			MenuBar menuBar = new MenuBar();
			Menu settingMenu = new Menu("Settings");
		    MenuItem caesar = new MenuItem("Caesar Cipher");
		    MenuItem modern = new MenuItem("Modern Cipher");
		    settingMenu.getItems().addAll(caesar, modern);
		    menuBar.getMenus().add(settingMenu);


			Button btnCaesarE = new Button();
			Button btnModern = new Button();
			btnCaesarE.setText("Caesar Cipher");
			btnModern.setText("DES Cipher");
			vbox.getChildren().addAll(btnCaesarE, btnModern, menuBar);
			root.setLeft(vbox);
			
			caesar.setOnAction(event -> {
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
				submitButton.setOnAction(event1 -> {

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
			
			modern.setOnAction(e -> {
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
				
				rb6.selectedProperty().addListener(new ChangeListener<Boolean>() {
				    @Override
				    public void changed(ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected, Boolean isNowSelected) {
				        if (isNowSelected) { 
				        	inputVBox2.getChildren().add(4,keyField);
				        }else {
				        	inputVBox2.getChildren().remove(4);
				        }
				    }
				    });
				row1.setSpacing(80);
				row2.setSpacing(70);
				row3.setSpacing(40);
				
				Label resultLable = new Label("Result:");
				TextField resultField = new TextField();
				resultField.setEditable(false);
				
				Button submitButton = new Button("Submit");
				submitButton.setOnAction(event -> {
						String text = textField.getText();
						
		
						if (rb1.isSelected()&& rb3.isSelected()&&rb5.isSelected()) {
							//String keyFilePath = "keyFile.txt";
				            try {
								des1.saveKeyToFile();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							byte[] encryptedData;
							try {
								encryptedData = des1.encrypt(text);
								String encryptedText = Base64.getEncoder().encodeToString(encryptedData);
								resultField.setText(encryptedText);
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
												
						}else if(rb1.isSelected()&& rb4.isSelected()&&rb5.isSelected()) {
							byte[] decryptedData;
							try {
								decryptedData = Base64.getDecoder().decode(text);
								String decryptedText = des1.decrypt(decryptedData);
								resultField.setText(decryptedText);
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							
						}else if(rb1.isSelected()&& rb3.isSelected()&&rb6.isSelected()){
							byte[] encryptedData;
							try {
								encryptedData = des1.encrypt(text);
								String encryptedText = Base64.getEncoder().encodeToString(encryptedData);
								String key = keyField.getText();
								
								byte[] keyByte = Base64.getDecoder().decode(key);
								SecretKey givenKey = new SecretKeySpec(keyByte, "DES");
								des1.setSecretkey(givenKey);
								des1.encrypt(text);
								System.out.println(key);
								resultField.setText(encryptedText);
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							
						}else if(rb2.isSelected()&& rb3.isSelected()) {
							
						}else if(rb2.isSelected()&& rb4.isSelected()) {
							
						}
											 
				});
				inputVBox2.getChildren().addAll( textLabel, textField, keyLabel,row3, row1, row2, submitButton, resultLable, resultField);
				root.setCenter(inputVBox2);
				
				//end of btn
			});
		
			// action for button btnCaesarE		
//			btnCaesarE.setOnAction(e -> {
//				
//				VBox inputVBox1 = new VBox();
//				inputVBox1.setPadding(new Insets(20, 20, 20, 20));
//				inputVBox1.setSpacing(10);
//				Label textLabel = new Label("Enter your text:");
//				TextField textField = new TextField();
//				Label keyLabel = new Label("Enter your key:");
//				TextField keyField = new TextField();
//
//				ChoiceBox<String> cb = new ChoiceBox<String>(FXCollections.observableArrayList("Encrypt", "Decrypt"));
//				cb.setValue("Encrypt");
//				Label resultLable = new Label("Result:");
//				TextField resultField = new TextField();
//				resultField.setEditable(false);
//
//				Button submitButton = new Button("Submit");
//				submitButton.setOnAction(event -> {
//
//					String text = textField.getText();
//					int key = Integer.parseInt(keyField.getText());
//					Caesar c = new Caesar();
//					String result;
//					if (cb.getValue().equals("Encrypt")) {
//						result = c.encrypt(text.toUpperCase(), key);
//					} else {
//						result = c.dencrypt(text.toUpperCase(), key);
//					}
//
//					resultField.setText(result);
//				
//				});
//				inputVBox1.getChildren().addAll( textLabel, textField, keyLabel, keyField, cb, submitButton, resultLable,resultField);
//				root.setCenter(inputVBox1);
//	             
//			});
			// action for button btnModern
//			
//			btnModern.setOnAction(e -> {
//				VBox inputVBox2 = new VBox();
//				inputVBox2.setPadding(new Insets(20, 20, 20, 20));
//				inputVBox2.setSpacing(10);
//				Label textLabel = new Label("Enter your text:");
//				TextField textField = new TextField();
//				Label keyLabel = new Label("Enter your key:");
//				TextField keyField = new TextField();
//				
//				HBox row1 = new HBox();
//				ToggleGroup group1 = new ToggleGroup();
//				RadioButton rb1 = new RadioButton();			
//				rb1.setText("DES");
//				rb1.setToggleGroup(group1);
//				RadioButton rb2 = new RadioButton();
//				rb2.setText("AES");
//				rb2.setToggleGroup(group1);
//				row1.getChildren().addAll(rb1, rb2);
//				
//				
//				HBox row2 = new HBox();
//				ToggleGroup group2 = new ToggleGroup();
//				RadioButton rb3 = new RadioButton();
//				rb3.setText("Encrypt");
//				rb3.setToggleGroup(group2);
//				RadioButton rb4 = new RadioButton();
//				rb4.setText("Decrypt");
//				rb4.setToggleGroup(group2);
//				row2.getChildren().addAll(rb3, rb4);
//				
//				HBox row3 = new HBox();
//				ToggleGroup group3 = new ToggleGroup();
//				RadioButton rb5 = new RadioButton();
//				rb5.setText("Generate a Key");
//				rb5.setToggleGroup(group3);
//				
//				RadioButton rb6 = new RadioButton();
//				rb6.setText("Use Your Own Key");
//				rb6.setToggleGroup(group3);
//				row3.getChildren().addAll(rb5, rb6);
//				
//				rb6.selectedProperty().addListener(new ChangeListener<Boolean>() {
//				    @Override
//				    public void changed(ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected, Boolean isNowSelected) {
//				        if (isNowSelected) { 
//				        	inputVBox2.getChildren().add(4,keyField);
//				        }else {
//				        	inputVBox2.getChildren().remove(4);
//				        }
//				    }
//				    });
//				row1.setSpacing(80);
//				row2.setSpacing(70);
//				row3.setSpacing(40);
//				
//				Label resultLable = new Label("Result:");
//				TextField resultField = new TextField();
//				resultField.setEditable(false);
//				
//				Button submitButton = new Button("Submit");
//				submitButton.setOnAction(event -> {
//						String text = textField.getText();
//						String key = keyField.getText();
//		
//						if (rb1.isSelected()&& rb3.isSelected()) {
//							byte[] encryptedData;
//							try {
//								encryptedData = des1.encrypt(text);
//								String encryptedText = Base64.getEncoder().encodeToString(encryptedData);
//								resultField.setText(encryptedText);
//							} catch (Exception e1) {
//								// TODO Auto-generated catch block
//								e1.printStackTrace();
//							}
//												
//						}else if(rb1.isSelected()&& rb4.isSelected()) {
//							byte[] decryptedData;
//							try {
//								decryptedData = Base64.getDecoder().decode(text);
//								String decryptedText = des1.decrypt(decryptedData);
//								resultField.setText(decryptedText);
//							} catch (Exception e1) {
//								// TODO Auto-generated catch block
//								e1.printStackTrace();
//							}
//							
//						}else if(rb2.isSelected()&& rb3.isSelected()) {
//							
//						}else if(rb2.isSelected()&& rb4.isSelected()) {
//							
//						}
//											 
//				});
//				inputVBox2.getChildren().addAll( textLabel, textField, keyLabel,row3, row1, row2, submitButton, resultLable, resultField);
//				root.setCenter(inputVBox2);
//				
//				//end of btn
//			});
			
			
			
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
