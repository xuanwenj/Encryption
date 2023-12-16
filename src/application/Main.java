package application;

import java.beans.Statement;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.sql.*;

public class Main extends Application {
	static final String JDBC_URL = "jdbc:mysql://database-1.cxbdcyicswj2.ap-southeast-2.rds.amazonaws.com";
	static final String USERNAME = "admin";
	static final String PASSWORD = "X1122d0610"; // null password
	//Connection connection = null;
	
	 private void connectToDatabase() throws SQLException, ClassNotFoundException {
	        
	        	//Class.forName("com.mysql.cj.jdbc.Driver");
				Connection connection = DriverManager.getConnection(
						JDBC_URL, USERNAME, PASSWORD);	
				java.sql.Statement stmt = connection.createStatement();
				System.out.println("Database connection successful!");
				

				 System.out.println("The SQL statement is: " );	
	     	
	    }
	    
	public static void main(String[] args) {
	
		launch(args);
	}
	
	DESSimple des1;
	DESSimple aes1;
	DESSimple des2;
	DESSimple aes2;
	
	int keySize;
	RadioButton aes128 = new RadioButton();
	RadioButton aes192 = new RadioButton();
	RadioButton aes256 = new RadioButton();

	
		

	@Override
	public void start(Stage primaryStage) throws NoSuchAlgorithmException {
		des1 = new DESSimple("DES", 56);
		aes1 = new DESSimple("AES", 128);
		
		    GridPane loginPane = new GridPane();
	        loginPane.setPadding(new Insets(20));
	        loginPane.setVgap(10);
	        loginPane.setHgap(10);

	        Label usernameLabel = new Label("Username:");
	        TextField usernameField = new TextField();
	        Label passwordLabel = new Label("Password:");
	        PasswordField passwordField = new PasswordField();
	        Button loginButton = new Button("Login");

	        loginPane.add(usernameLabel, 0, 0);
	        loginPane.add(usernameField, 1, 0);
	        loginPane.add(passwordLabel, 0, 1);
	        loginPane.add(passwordField, 1, 1);
	        loginPane.add(loginButton, 1, 2);

	        Scene loginScene = new Scene(loginPane, 300, 300);
	        BorderPane root = new BorderPane();
	        Scene scene = new Scene(root, 500, 500);
	        primaryStage.setScene(loginScene);
	        primaryStage.show();
		
		
		loginButton.setOnAction(event ->{
			primaryStage.setScene(scene);
	        primaryStage.show();
	        try {
				connectToDatabase();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		 int loadedKeySize = readKeySize();
		 if (loadedKeySize == 128) {
		        aes128.setSelected(true);
		    } else if (loadedKeySize == 192) {
		        aes192.setSelected(true);
		    } else if (loadedKeySize == 256) {
		        aes256.setSelected(true);
		    }
		try {
			
			// VBox for buttons on the left
			VBox vboxLeft = new VBox();
			vboxLeft.setPadding(new Insets(20, 20, 20, 20));
			vboxLeft.setSpacing(10);

			MenuBar menuBar = new MenuBar();
			Menu settingMenu = new Menu("Settings");
			MenuItem aes = new MenuItem("AES Key Length");
			settingMenu.getItems().addAll(aes);
			menuBar.getMenus().add(settingMenu);

			Button btnCaesarE = new Button();
			Button btnModern = new Button();
			Button btnLoadKey = new Button();
			Button btnSaveKeyLocal = new Button();
			Button btnSaveKeyDB = new Button();
			btnCaesarE.setText("Caesar Cipher");
			btnModern.setText("Modern Cipher");
			btnSaveKeyLocal.setText("Save Key to File");
			btnSaveKeyDB.setText("Save Key to DB");
			btnLoadKey.setText("Load Key from File");
			vboxLeft.getChildren().addAll(btnCaesarE, btnModern, menuBar);
			root.setLeft(vboxLeft);

			// action on aes MenuItem
			aes.setOnAction(event -> {
				VBox bitVox = new VBox();
				bitVox.setPadding(new Insets(20, 20, 20, 20));
				bitVox.setSpacing(10);
				ToggleGroup group = new ToggleGroup();
				aes128.setText("AES-128");
				aes128.setUserData(128);
				aes192.setText("AES-192");
				aes192.setUserData(192);
				aes256.setText("AES-256");
				aes256.setUserData(256);
				group.getToggles().addAll(aes128,aes192,aes256);

				Button submit = new Button();
				submit.setText("Submit");
				Label messageLabel = new Label("You need to choose before submit.");
				messageLabel.setVisible(false);
				bitVox.getChildren().addAll(aes128, aes192, aes256, submit, messageLabel);
				root.setCenter(bitVox);

				submit.setOnAction(event1 -> {
					 saveKeySizeToFile((int)group.getSelectedToggle().getUserData());
				});
			});
			// action on CaesarE button
			btnCaesarE.setOnAction(event -> {
				VBox inputVBox1 = new VBox();
				inputVBox1.setPadding(new Insets(20, 20, 20, 20));
				inputVBox1.setSpacing(10);
				Label textLabel = new Label("Enter your text:");
				TextField textField = new TextField();
				Label keyLabel = new Label("Enter your key:");
				TextField keyField = new TextField();

				ChoiceBox<String> cb = new ChoiceBox<String>(FXCollections.observableArrayList("Encrypt", "Decrypt"));
				cb.setValue("Encrypt");
				HBox submitEncrypt = new HBox();
				submitEncrypt.setPadding(new Insets(0, 20, 20, 0));
				submitEncrypt.setSpacing(10);
				Label resultLable = new Label("Encrypted/Decrypted Result:");
				TextField resultField = new TextField();
				resultField.setEditable(false);				
				Button submitButton = new Button("Submit");
				submitEncrypt.getChildren().addAll(cb,submitButton);
							
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
				inputVBox1.getChildren().addAll(textLabel, textField, keyLabel, keyField, submitEncrypt, resultLable,
						resultField);
				root.setCenter(inputVBox1);
			});

			btnModern.setOnAction(e -> {
				VBox inputVBox2 = new VBox();
				inputVBox2.setPadding(new Insets(20, 20, 20, 20));
				inputVBox2.setSpacing(10);
				Label textLabel = new Label("Enter your text:");
				TextField textField = new TextField();
				Label keyLabel = new Label("Enter your key:");
				Label showKeyLabel = new Label("Key:");
				TextField showKeyField = new TextField();
				//TextField inputKeyField = new TextField();
				Label inputKeyLable = new Label("Loaded key:");
				
				HBox rowDESAES = new HBox();
				ToggleGroup group1 = new ToggleGroup();
				RadioButton btnDes = new RadioButton();
				btnDes.setText("DES");
				btnDes.setToggleGroup(group1);
				RadioButton btnAes = new RadioButton();
				btnAes.setText("AES");
				btnAes.setToggleGroup(group1);
				rowDESAES.getChildren().addAll(btnDes, btnAes);

				HBox rowEncryptDecrypt = new HBox();
				ToggleGroup group2 = new ToggleGroup();
				RadioButton encrypt = new RadioButton();
				encrypt.setText("Encrypt");
				encrypt.setToggleGroup(group2);
				RadioButton decrypt = new RadioButton();
				decrypt.setText("Decrypt");
				decrypt.setToggleGroup(group2);
				rowEncryptDecrypt.getChildren().addAll(encrypt, decrypt);

				HBox row3 = new HBox();
				ToggleGroup group3 = new ToggleGroup();
				RadioButton generateKey = new RadioButton();
				generateKey.setText("Generate a Key");
				generateKey.setToggleGroup(group3);

				RadioButton ownKey = new RadioButton();
				ownKey.setText("Load Key from File");
				ownKey.setToggleGroup(group3);
				row3.getChildren().addAll(generateKey, ownKey);
				
				generateKey.selectedProperty().addListener(new ChangeListener<Boolean>() {
					public void changed(ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected,
							Boolean isNowSelected) {
						 if (isNowSelected) {
							 if (!inputVBox2.getChildren().contains(showKeyLabel)) {
					                inputVBox2.getChildren().add(6, showKeyLabel);
					            }	
					            
					            if (!inputVBox2.getChildren().contains(btnSaveKeyLocal)) {
					                inputVBox2.getChildren().add(10, btnSaveKeyLocal);
					            }
					            if (!inputVBox2.getChildren().contains(showKeyField)) {
					                inputVBox2.getChildren().add(7, showKeyField);
					            }
					            if (!inputVBox2.getChildren().contains(btnSaveKeyDB)) {
					                inputVBox2.getChildren().add(11, btnSaveKeyDB);
					            }
					            if (!inputVBox2.getChildren().contains(btnLoadKey)) {
					                inputVBox2.getChildren().add(12, btnLoadKey);
					            }
					            
					            showKeyField.setEditable(false);
					        } else {
					        	inputVBox2.getChildren().remove(showKeyLabel);
					            inputVBox2.getChildren().remove(showKeyField);
					            inputVBox2.getChildren().remove(btnSaveKeyLocal);
					            inputVBox2.getChildren().remove(btnLoadKey);
					            
					        }
					    
					}
				});
				// button "Use your own Key" is selected
				HBox btnBoxinOwnKey = new HBox();
				btnBoxinOwnKey.setSpacing(70);
				btnBoxinOwnKey.getChildren().addAll(btnSaveKeyLocal, btnLoadKey);
				
				ownKey.selectedProperty().addListener(new ChangeListener<Boolean>() {
					@Override
					public void changed(ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected,
							Boolean isNowSelected) {											
						 if (isNowSelected) {
							 if (!inputVBox2.getChildren().contains(inputKeyLable)) {
					                inputVBox2.getChildren().add(6, inputKeyLable);
					            }	

					            if (!inputVBox2.getChildren().contains(btnSaveKeyLocal)) {
					                inputVBox2.getChildren().add(10, btnSaveKeyLocal);
					            }
					            if (!inputVBox2.getChildren().contains(showKeyField)) {
					                inputVBox2.getChildren().add(7, showKeyField);
					            }
					            if (!inputVBox2.getChildren().contains(btnSaveKeyDB)) {
					                inputVBox2.getChildren().add(11, btnSaveKeyDB);
					            }
					            if (!inputVBox2.getChildren().contains(btnLoadKey)) {
					                inputVBox2.getChildren().add(12, btnLoadKey);
					            }					         
					        } else {					        	
					        	inputVBox2.getChildren().remove(inputKeyLable);
					            inputVBox2.getChildren().remove(showKeyField);
					            inputVBox2.getChildren().remove(btnSaveKeyLocal);
					            inputVBox2.getChildren().remove(btnLoadKey);
					           
					        }						
					}
				});
				rowDESAES.setSpacing(90);
				rowEncryptDecrypt.setSpacing(70);
				row3.setSpacing(25);

				Label resultLable = new Label("Encrypted/Decrypted Result:");
				TextField resultField = new TextField();
				resultField.setEditable(false);
				FileChooser fileChooser = new FileChooser();
				fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt")

				);
				btnSaveKeyLocal.setOnAction(event -> {
					java.io.File file = fileChooser.showSaveDialog(primaryStage);
					try {
						des1.saveKeyToFile(file);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				});
				Button submitButton = new Button("Submit");
				submitButton.setOnAction(event -> {
					String text = textField.getText();
					showKeyField.setText(Base64.getEncoder().encodeToString(des1.getSecretkey().getEncoded()));

					// use generated key to encrypt in DES
					if (btnDes.isSelected() && encrypt.isSelected() && generateKey.isSelected()) {
						showKeyField.setText(Base64.getEncoder().encodeToString(des1.getSecretkey().getEncoded()));
						byte[] encryptedData;
						try {
							encryptedData = des1.encrypt(text);
							String encryptedText = Base64.getEncoder().encodeToString(encryptedData);
							resultField.setText(encryptedText);
							System.out.println(Base64.getEncoder().encodeToString(des1.getSecretkey().getEncoded()));

						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						// use generated key to decrypt in DES
					} else if (btnDes.isSelected() && decrypt.isSelected() && generateKey.isSelected()) {
						showKeyField.setText(Base64.getEncoder().encodeToString(des1.getSecretkey().getEncoded()));
						byte[] decryptedData;
						try {
							decryptedData = Base64.getDecoder().decode(text);
							String decryptedText = des1.decrypt(decryptedData);
							resultField.setText(decryptedText);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						// use own key to encrypt in DES
					} else if (btnDes.isSelected() && encrypt.isSelected() && ownKey.isSelected()) {
						showKeyField.setText(Base64.getEncoder().encodeToString(des1.getSecretkey().getEncoded()));
						byte[] encryptedData;
						try {
							String key = showKeyField.getText();
							byte[] keyByte = Base64.getDecoder().decode(key);
							SecretKey givenKey = new SecretKeySpec(keyByte, "DES");
							des1.setSecretkey(givenKey);
							System.out.println(key);

							encryptedData = des1.encrypt(text);
							String encryptedText = Base64.getEncoder().encodeToString(encryptedData);

							resultField.setText(encryptedText);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

						// use own key to decrypt in DES
					} else if (btnDes.isSelected() && decrypt.isSelected() && ownKey.isSelected()) {
						showKeyField.setText(Base64.getEncoder().encodeToString(des1.getSecretkey().getEncoded()));
						byte[] decryptedData;
						try {
							decryptedData = Base64.getDecoder().decode(text);
							String decryptedText = des1.decrypt(decryptedData);
							resultField.setText(decryptedText);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						// use generated key to encrypt in AES
					}else if(btnAes.isSelected() && encrypt.isSelected()&& generateKey.isSelected()) {
						showKeyField.setText(Base64.getEncoder().encodeToString(aes1.getSecretkey().getEncoded()));
						byte[] encryptedData;
						try {
							encryptedData = aes1.encrypt(text);
							String encryptedText = Base64.getEncoder().encodeToString(encryptedData);
							resultField.setText(encryptedText);
							System.out.println(Base64.getEncoder().encodeToString(aes1.getSecretkey().getEncoded()));

						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						// use generated key to decrypt in AES
					}else if(btnAes.isSelected() && decrypt.isSelected()&& generateKey.isSelected()) {
						showKeyField.setText(Base64.getEncoder().encodeToString(aes1.getSecretkey().getEncoded()));
						byte[] decryptedData;
						try {
							decryptedData = Base64.getDecoder().decode(text);
							String decryptedText = aes1.decrypt(decryptedData);
							resultField.setText(decryptedText);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						//use own key to encrypt in AES 
					}else if (btnAes.isSelected() && encrypt.isSelected()&& ownKey.isSelected()) {
						showKeyField.setText(Base64.getEncoder().encodeToString(aes1.getSecretkey().getEncoded()));
						byte[] encryptedData;
						try {
							String key = showKeyField.getText();
							byte[] keyByte = Base64.getDecoder().decode(key);
							SecretKey givenKey = new SecretKeySpec(keyByte, "AES");
							aes1.setSecretkey(givenKey);
							System.out.println(key);

							encryptedData = aes1.encrypt(text);
							String encryptedText = Base64.getEncoder().encodeToString(encryptedData);

							resultField.setText(encryptedText);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						// use own key to decrypt in AES
					} else if (btnAes.isSelected() && decrypt.isSelected()&& ownKey.isSelected()) {
						showKeyField.setText(Base64.getEncoder().encodeToString(aes1.getSecretkey().getEncoded()));
						byte[] decryptedData;
						try {
							decryptedData = Base64.getDecoder().decode(text);
							String decryptedText = aes1.decrypt(decryptedData);
							
							resultField.setText(decryptedText);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				});
				inputVBox2.getChildren().addAll(textLabel, textField, keyLabel, rowDESAES, rowEncryptDecrypt, row3,
						submitButton, resultLable, resultField);
				root.setCenter(inputVBox2);

				btnLoadKey.setOnAction(event1 -> {
					FileChooser fc = new FileChooser();
					File selectedFile = fc.showOpenDialog(primaryStage);
					if (selectedFile != null) {
						try {
							if (btnDes.isSelected()) {
								des2 = new DESSimple("DES", 56);
								SecretKey loadedKey = des2.loadKeyFromFile(selectedFile);
								des2.setSecretkey(loadedKey);
								showKeyField.setText(Base64.getEncoder().encodeToString(loadedKey.getEncoded()));
							} else if (btnAes.isSelected()) {
								aes2 = new DESSimple("AES", 128);
								SecretKey loadedKey = aes2.loadKeyFromFile(selectedFile);
								aes2.setSecretkey(loadedKey);
								showKeyField.setText(Base64.getEncoder().encodeToString(loadedKey.getEncoded()));
							}
						} catch (ClassNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (NoSuchAlgorithmException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						// Use the loadedKey for decryption or any other operation
						System.out.println("Key loaded successfully.");

					} else {
						System.out.println("No file selected.");
					}
				});

			});// end of btn
			
			// the main scene			
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			//primaryStage.setScene(scene);
			//primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	private void saveKeySizeToFile(int keySize) {
		System.out.println(keySize);
	    try {
	        // Specify the file path where you want to save the key size
	        String filePath = "keySize.txt";

	        // Create a file object
	        File file = new File(filePath);

	        PrintWriter pw = new PrintWriter(file);
	 
	        // Write the key size to the file
	        pw.write(Integer.toString(keySize));
	        pw.close();


	        System.out.println("Key size saved to file successfully.");
	    } catch (IOException e) {
	        System.out.println("Error saving key size to file: " + e.getMessage());
	    }
	}
	private int readKeySize() {
	    int loadedKeySize = -1; // Initialize with a default value
	    try {
	        File file = new File("keySize.txt");

	        if (file.exists()) {
	            // Read the key size from the file
	            Scanner scanner = new Scanner(file);
	            if (scanner.hasNextLine()) {
	                loadedKeySize = Integer.parseInt(scanner.nextLine());
	            }
	            scanner.close();
	        }
	    } catch (IOException e) {
	        System.out.println("Error reading key size from file: " + e.getMessage());
	    }

	    return loadedKeySize;
	}

	
}
// commit test
