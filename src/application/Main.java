package application;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Scanner;
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
import javafx.scene.layout.VBox;
import java.sql.*;

public class Main extends Application {
	static final String JDBC_URL = "jdbc:mysql://database-1.cxbdcyicswj2.ap-southeast-2.rds.amazonaws.com";
	static final String USERNAME = "admin";
	static final String PASSWORD = "X1122d0610";
	public static void main(String[] args) {

		launch(args);
	}

	DESSimple des1;
	DESSimple aes1;
	ResultSet resultSet;
	TextField usernameField;
	PasswordField passwordField;
	String encyptedpassword;
	int keySize;
	RadioButton aes128 = new RadioButton();
	RadioButton aes192 = new RadioButton();
	RadioButton aes256 = new RadioButton();
	/**
	 * 
	 * @return True, when connecting to workbench database
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws NoSuchPaddingException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	private Boolean connectToDatabase()
			throws SQLException, ClassNotFoundException, NoSuchAlgorithmException, InvalidKeyException,
			NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		String username = usernameField.getText();
		Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
		String query = "SELECT * FROM mydb1.users WHERE userName = ?";
		PreparedStatement preparedStatement = connection.prepareStatement(query);
		preparedStatement.setString(1, username);
		// Executing the SELECT query
		resultSet = preparedStatement.executeQuery();
		if (resultSet.next()) {
			// get the encryptd password from database;
			encyptedpassword = resultSet.getString("password");
			// get the inputed password(wen#pass)
			String enteredPassword = passwordField.getText();
			// decode the encrypted password for decryption;

			byte[] decryptedData = Base64.getDecoder().decode(encyptedpassword);
			System.out.println("the decrypted data is :" + decryptedData);

			DESSimple des2 = new DESSimple("AES", 128);
			// load the key from file
			String filePath = "src\\application\\keytoencryptpassword.txt";
			File keyFile = new File(filePath);
			SecretKey loadedKey;
			try {
				loadedKey = des2.loadKeyFromFile(keyFile);
				des2.setSecretkey(loadedKey);
				String decryptedKey = des2.decrypt(decryptedData);
				if (decryptedKey.equals(enteredPassword)) {
					System.out.println("Database connection successful!");
					return true;
					
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("No password found for user: " + username);

		}
		return false;
	}

	@Override
	public void start(Stage primaryStage) throws NoSuchAlgorithmException {
		des1 = new DESSimple("DES", 56);
		aes1 = new DESSimple("AES", 128);
		// Login layout
		GridPane loginPane = new GridPane();
		loginPane.setPadding(new Insets(20));
		loginPane.setVgap(10);
		loginPane.setHgap(10);

		Label usernameLabel = new Label("Username:");
		usernameField = new TextField();
		Label passwordLabel = new Label("Password:");
		passwordField = new PasswordField();
		Button loginButton = new Button("Login");
		Label loginFeedback = new Label("Invalid password.");
		loginFeedback.setVisible(false);
		

		loginPane.add(usernameLabel, 0, 0);
		loginPane.add(usernameField, 1, 0);
		loginPane.add(passwordLabel, 0, 1);
		loginPane.add(passwordField, 1, 1);
		loginPane.add(loginButton, 1, 2);
		loginPane.add(loginFeedback, 1, 3);
		//login scene
		Scene loginScene = new Scene(loginPane, 300, 300);
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root, 500, 500);
		primaryStage.setScene(loginScene);
		primaryStage.show();
		
		//action on login button
		loginButton.setOnAction(event -> {
			try {
				if (connectToDatabase()) {
					System.out.println("The password entered by user is:" + passwordField.getText());

					System.out.println("The encrypted password saved in database is " + encyptedpassword);
					primaryStage.setScene(scene);

					primaryStage.show();
				} else {
					loginFeedback.setVisible(true);
					System.out.println("Invalid password");
				}
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidAlgorithmParameterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		});
		// load the saved keysize when the project restarted 
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
			//Button btnSaveKeyDB = new Button();
			btnCaesarE.setText("Caesar Cipher");
			btnModern.setText("Modern Cipher");
			btnSaveKeyLocal.setText("Save Key to File");
			//btnSaveKeyDB.setText("Save Key to DB");
			btnLoadKey.setText("Load Key from File");
			// update in git
			Label labelSubmitFeedback = new Label("Please select an option from each row of buttuns to submit");
			labelSubmitFeedback.setVisible(false);
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
				group.getToggles().addAll(aes128, aes192, aes256);

				Button btnsubmitInSetting = new Button();
				btnsubmitInSetting.setText("Submit");
				Label messageLabel = new Label("You need to choose before submit.");
				messageLabel.setVisible(false);
				bitVox.getChildren().addAll(aes128, aes192, aes256, btnsubmitInSetting, messageLabel);
				root.setCenter(bitVox);
				
				//action on submit button in setting menu
				btnsubmitInSetting.setOnAction(event1 -> {
					saveKeySizeToFile((int) group.getSelectedToggle().getUserData());
					if(aes128.isSelected()) {
						try {
							aes1.generateKey((int) aes128.getUserData());
							System.out.println("key size of aes1 is" + aes128.getUserData());
						} catch (NoSuchAlgorithmException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else if(aes192.isSelected()) {
						try {
							aes1.generateKey((int) aes192.getUserData());
							System.out.println("key size of aes1 is" + aes192.getUserData());
						} catch (NoSuchAlgorithmException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else if(aes256.isSelected()) {
						try {
							aes1.generateKey((int) aes256.getUserData());
							System.out.println("key size of aes1 is" + aes256.getUserData());
						} catch (NoSuchAlgorithmException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
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
				submitEncrypt.getChildren().addAll(cb, submitButton);

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
				//Label keyLabel = new Label("Enter your key:");
				Label showKeyLabel = new Label("Key:");
				TextField showKeyField = new TextField();
				// TextField inputKeyField = new TextField();
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

				HBox rowKeyChoice = new HBox();
				ToggleGroup group3 = new ToggleGroup();
				RadioButton generateKey = new RadioButton();
				generateKey.setText("Generate a Key");
				generateKey.setToggleGroup(group3);

				RadioButton loadKeyfromFile = new RadioButton();
				loadKeyfromFile.setText("Load Key from File");
				loadKeyfromFile.setToggleGroup(group3);
				rowKeyChoice.getChildren().addAll(generateKey, loadKeyfromFile);
				// change the items in the layout when generateKey radio button is selected
				generateKey.selectedProperty().addListener(new ChangeListener<Boolean>() {
					public void changed(ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected,
							Boolean isNowSelected) {
						if (isNowSelected) {
							if (!inputVBox2.getChildren().contains(showKeyLabel)) {
								inputVBox2.getChildren().add(5, showKeyLabel);
							}							
							if (!inputVBox2.getChildren().contains(showKeyField)) {
								inputVBox2.getChildren().add(6, showKeyField);
							}
							if (!inputVBox2.getChildren().contains(btnLoadKey)) {
								inputVBox2.getChildren().add(11, btnLoadKey);
							}
							if (!inputVBox2.getChildren().contains(btnSaveKeyLocal)) {
								inputVBox2.getChildren().add(10, btnSaveKeyLocal);//9
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
				// button "load key from file" is selected
				HBox btnBoxinOwnKey = new HBox();
				btnBoxinOwnKey.setSpacing(70);
				btnBoxinOwnKey.getChildren().addAll(btnSaveKeyLocal, btnLoadKey);
				// change the items in the layout when loadKeyfromFile radio button is selected
				loadKeyfromFile.selectedProperty().addListener(new ChangeListener<Boolean>() {
					@Override
					public void changed(ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected,
							Boolean isNowSelected) {
						if (isNowSelected) {
							if (!inputVBox2.getChildren().contains(inputKeyLable)) {
								inputVBox2.getChildren().add(5, inputKeyLable);
							}
							if (!inputVBox2.getChildren().contains(btnSaveKeyLocal)) {
								inputVBox2.getChildren().add(10, btnSaveKeyLocal);
							}
							if (!inputVBox2.getChildren().contains(showKeyField)) {
								inputVBox2.getChildren().add(6, showKeyField);
							}
							if (!inputVBox2.getChildren().contains(btnLoadKey)) {
								inputVBox2.getChildren().add(11,btnLoadKey);
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
				rowKeyChoice.setSpacing(25);

				Label resultLable = new Label("Encrypted/Decrypted Result:");
				TextField resultField = new TextField();
				resultField.setEditable(false);
				FileChooser fileChooser = new FileChooser();
				fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt")

				);
				//action on save key to file
				btnSaveKeyLocal.setOnAction(event -> {
					java.io.File file = fileChooser.showSaveDialog(primaryStage);
					try {
						if (btnDes.isSelected()) {
							des1.saveKeyToFile(file);
						} else if (btnAes.isSelected()) {
							aes1.saveKeyToFile(file);
						}
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				});
				Button submitButton = new Button("Submit");
				// actions on submit button
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
					} else if (btnDes.isSelected() && encrypt.isSelected() && loadKeyfromFile.isSelected()) {
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
					} else if (btnDes.isSelected() && decrypt.isSelected() && loadKeyfromFile.isSelected()) {
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
					} else if (btnAes.isSelected() && encrypt.isSelected() && generateKey.isSelected()) {
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
					} else if (btnAes.isSelected() && decrypt.isSelected() && generateKey.isSelected()) {
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

						// use own key to encrypt in AES
					} else if (btnAes.isSelected() && encrypt.isSelected() && loadKeyfromFile.isSelected()) {
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
					} else if (btnAes.isSelected() && decrypt.isSelected() && loadKeyfromFile.isSelected()) {
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
					}else {
						labelSubmitFeedback.setVisible(true);
					}
				});
				inputVBox2.getChildren().addAll(textLabel, textField, rowDESAES, rowEncryptDecrypt,
						rowKeyChoice, submitButton, resultLable, resultField, labelSubmitFeedback);
				root.setCenter(inputVBox2);
				// action on load key button
				btnLoadKey.setOnAction(event1 -> {
					FileChooser fc = new FileChooser();
					File selectedFile = fc.showOpenDialog(primaryStage);
					if (selectedFile != null) {
						try {
							if (btnDes.isSelected()) {

								SecretKey loadedKey = des1.loadKeyFromFile(selectedFile);
								des1.setSecretkey(loadedKey);
								showKeyField.setText(Base64.getEncoder().encodeToString(loadedKey.getEncoded()));
							} else if (btnAes.isSelected()) {

								SecretKey loadedKey = aes1.loadKeyFromFile(selectedFile);
								aes1.setSecretkey(loadedKey);
								showKeyField.setText(Base64.getEncoder().encodeToString(loadedKey.getEncoded()));
							}
						} catch (ClassNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
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

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	/**
	 * 
	 * @param keySize choosed by user is saved in keySize.txt file.
	 */
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
	/**
	 * 
	 * @return the keysize save in the file.
	 */
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
