package org.example;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class Main extends Application {

  private static final Map<String, Integer> speed = new HashMap(Map.of(
      String.valueOf(SerialPort.BAUDRATE_110), SerialPort.BAUDRATE_110,
      String.valueOf(SerialPort.BAUDRATE_600), SerialPort.BAUDRATE_600,
      String.valueOf(SerialPort.BAUDRATE_1200), SerialPort.BAUDRATE_1200,
      String.valueOf(SerialPort.BAUDRATE_2400), SerialPort.BAUDRATE_2400,
      String.valueOf(SerialPort.BAUDRATE_4800), SerialPort.BAUDRATE_4800,
      String.valueOf(SerialPort.BAUDRATE_9600), SerialPort.BAUDRATE_9600,
      String.valueOf(SerialPort.BAUDRATE_19200), SerialPort.BAUDRATE_19200
  ));
  private static Map<String, SerialPort[]> ports;

  private static SerialPort[] firstWindowPortsPair;
  private static SerialPort[] secondWindowPortsPair;

  private static int firstPairSpeed;
  private static int secondPairSpeed;

  public static Map<String, SerialPort[]> getPortPairs() {
    List<String> portNames = new ArrayList<>(Arrays.stream(SerialPortList.getPortNames()).toList());
    Map<String, SerialPort[]> portPairs = new HashMap<>();

    int i = 1;

    while (!portNames.isEmpty()) {
      byte checkByte = 1;
      SerialPort port1 = new SerialPort(portNames.get(0));
      SerialPort port2 = new SerialPort(portNames.get(i));
      try {
        port1.openPort();
        port2.openPort();
        port1.writeByte(checkByte);
        byte[] receivedBytes = port2.readBytes();
        if (receivedBytes != null && receivedBytes[0] == checkByte) {
          portPairs.put(portNames.get(0), new SerialPort[] {port1, port2});
          portPairs.put(portNames.get(i), new SerialPort[] {port2, port1});
          portNames.remove(0);
          portNames.remove(i - 1);
          i = 1;
          port1.closePort();
          port2.closePort();
          continue;
        }
        port1.closePort();
        port2.closePort();
        i++;
      } catch (SerialPortException e) {
        e.printStackTrace();
      }
    }
    return portPairs;
  }

  public static void initializeWindows() {
    Dimension displayDimension = Toolkit.getDefaultToolkit().getScreenSize();
    double windowWidth = (double) (displayDimension.width / 2 - 120);
    double windowHeight = (double) (displayDimension.height - 200);

    AnchorPane firstWindowPane = new AnchorPane();
    AnchorPane secondWindowPane = new AnchorPane();

    Label firstWindowsInputFieldLabel = new Label("Input: ");
    firstWindowsInputFieldLabel.setFont(Font.font("Calibri", 16));
    AnchorPane.setLeftAnchor(firstWindowsInputFieldLabel, 10.0);
    AnchorPane.setTopAnchor(firstWindowsInputFieldLabel, 10.0);
    firstWindowPane.getChildren().add(firstWindowsInputFieldLabel);

    TextField firstWindowsPortInputField = new TextField();
    firstWindowsPortInputField.setMinSize((windowWidth - 20) / 2, 50);
    firstWindowsPortInputField.setMaxSize((windowWidth - 20) / 2, 50);
    firstWindowsPortInputField.setStyle("-fx-alignment: top-left;");
    AnchorPane.setLeftAnchor(firstWindowsPortInputField, 10.0);
    AnchorPane.setTopAnchor(firstWindowsPortInputField, 30.0);
    firstWindowPane.getChildren().add(firstWindowsPortInputField);

    Label firstWindowsOutputFieldLabel = new Label("Output: ");
    firstWindowsOutputFieldLabel.setFont(Font.font("Calibri", 16));
    AnchorPane.setLeftAnchor(firstWindowsOutputFieldLabel, 10.0);
    AnchorPane.setTopAnchor(firstWindowsOutputFieldLabel, 90.0);
    firstWindowPane.getChildren().add(firstWindowsOutputFieldLabel);

    TextField firstWindowsOutputField = new TextField();
    firstWindowsOutputField.setMinSize((windowWidth - 20) / 2, 50);
    firstWindowsOutputField.setMaxSize((windowWidth - 20) / 2, 50);
    firstWindowsOutputField.setStyle("-fx-alignment: top-left;");
    firstWindowsOutputField.setEditable(false);
    AnchorPane.setLeftAnchor(firstWindowsOutputField, 10.0);
    AnchorPane.setTopAnchor(firstWindowsOutputField, 110.0);
    firstWindowPane.getChildren().add(firstWindowsOutputField);

    TextField firstWindowOutputPortNameField = new TextField();
    firstWindowOutputPortNameField.setMinSize(150, 30);
    firstWindowOutputPortNameField.setMaxSize(150, 30);
    firstWindowOutputPortNameField.setStyle("-fx-alignment: top-left;");
    firstWindowOutputPortNameField.setEditable(false);
    AnchorPane.setLeftAnchor(firstWindowOutputPortNameField, windowWidth / 2 + 20);
    AnchorPane.setTopAnchor(firstWindowOutputPortNameField, 120.0);
    firstWindowPane.getChildren().add(firstWindowOutputPortNameField);

    ComboBox<String> firstWindowSpeedBox = new ComboBox<>();
    firstWindowSpeedBox.setPrefSize(150, 30);
    firstWindowSpeedBox.getItems().addAll(speed.keySet());
    firstWindowSpeedBox.setValue(speed.keySet().stream().toList().get(0));
    firstWindowSpeedBox.setOnAction(event -> {
      firstPairSpeed = speed.get(firstWindowSpeedBox.getValue());
      System.out.println(firstPairSpeed);
    });
    AnchorPane.setLeftAnchor(firstWindowSpeedBox, 10.0);
    AnchorPane.setTopAnchor(firstWindowSpeedBox, 190.0);
    firstWindowPane.getChildren().add(firstWindowSpeedBox);

    Label firstWindowSpeedLabel = new Label(" <- input port speed");
    firstWindowSpeedLabel.setFont(Font.font("Calibri", 16));
    AnchorPane.setLeftAnchor(firstWindowSpeedLabel, 180.0);
    AnchorPane.setTopAnchor(firstWindowSpeedLabel, 195.0);
    firstWindowPane.getChildren().add(firstWindowSpeedLabel);

    TextField firstWindowBytesReceivedField = new TextField();
    firstWindowBytesReceivedField.setMinSize(150, 30);
    firstWindowBytesReceivedField.setMaxSize(150, 30);
    firstWindowBytesReceivedField.setStyle("-fx-alignment: top-left;");
    firstWindowBytesReceivedField.setEditable(false);
    AnchorPane.setLeftAnchor(firstWindowBytesReceivedField, 10.0);
    AnchorPane.setTopAnchor(firstWindowBytesReceivedField, 250.0);
    firstWindowPane.getChildren().add(firstWindowBytesReceivedField);

    Label firstWindowBytesLabel = new Label(" <- output port bytes received");
    firstWindowBytesLabel.setFont(Font.font("Calibri", 16));
    AnchorPane.setLeftAnchor(firstWindowBytesLabel, 180.0);
    AnchorPane.setTopAnchor(firstWindowBytesLabel, 255.0);
    firstWindowPane.getChildren().add(firstWindowBytesLabel);

    TextArea firstWindowsStatusBar = new TextArea();
    firstWindowsStatusBar.setMinSize((int)(windowWidth - 20), 300);
    firstWindowsStatusBar.setMaxSize((int)(windowWidth - 20), 300);
    firstWindowsStatusBar.setEditable(false);
    AnchorPane.setLeftAnchor(firstWindowsStatusBar, 10.0);
    AnchorPane.setTopAnchor(firstWindowsStatusBar, 300.0);
    firstWindowPane.getChildren().add(firstWindowsStatusBar);

    Label secondInputFieldLabel = new Label("Input: ");
    secondInputFieldLabel.setFont(Font.font("Calibri", 16));
    AnchorPane.setLeftAnchor(secondInputFieldLabel, 10.0);
    AnchorPane.setTopAnchor(secondInputFieldLabel, 10.0);
    secondWindowPane.getChildren().add(secondInputFieldLabel);

    TextField secondWindowsPortInputField = new TextField();
    secondWindowsPortInputField.setMinSize((windowWidth - 20) / 2, 50);
    secondWindowsPortInputField.setMaxSize((windowWidth - 20) / 2, 50);
    secondWindowsPortInputField.setStyle("-fx-alignment: top-left;");
    AnchorPane.setLeftAnchor(secondWindowsPortInputField, 10.0);
    AnchorPane.setTopAnchor(secondWindowsPortInputField, 30.0);
    secondWindowPane.getChildren().add(secondWindowsPortInputField);

    Label secondWindowsOutputFieldLabel = new Label("Output: ");
    secondWindowsOutputFieldLabel.setFont(Font.font("Calibri", 16));
    AnchorPane.setLeftAnchor(secondWindowsOutputFieldLabel, 10.0);
    AnchorPane.setTopAnchor(secondWindowsOutputFieldLabel, 90.0);
    secondWindowPane.getChildren().add(secondWindowsOutputFieldLabel);

    TextField secondWindowsOutputField = new TextField();
    secondWindowsOutputField.setMinSize((windowWidth - 20) / 2, 50);
    secondWindowsOutputField.setMaxSize((windowWidth - 20) / 2, 50);
    secondWindowsOutputField.setStyle("-fx-alignment: top-left;");
    secondWindowsOutputField.setEditable(false);
    AnchorPane.setLeftAnchor(secondWindowsOutputField, 10.0);
    AnchorPane.setTopAnchor(secondWindowsOutputField, 110.0);
    secondWindowPane.getChildren().add(secondWindowsOutputField);

    TextField secondWindowOutputPortNameField = new TextField();
    secondWindowOutputPortNameField.setMinSize(150, 30);
    secondWindowOutputPortNameField.setMaxSize(150, 30);
    secondWindowOutputPortNameField.setStyle("-fx-alignment: top-left;");
    secondWindowOutputPortNameField.setEditable(false);
    AnchorPane.setLeftAnchor(secondWindowOutputPortNameField, windowWidth / 2 + 20);
    AnchorPane.setTopAnchor(secondWindowOutputPortNameField, 120.0);
    secondWindowPane.getChildren().add(secondWindowOutputPortNameField);

    ComboBox<String> secondWindowSpeedBox = new ComboBox<>();
    secondWindowSpeedBox.setPrefSize(150, 30);
    secondWindowSpeedBox.getItems().addAll(speed.keySet());
    secondWindowSpeedBox.setValue(speed.keySet().stream().toList().get(0));
    secondWindowSpeedBox.setOnAction(event -> {
      secondPairSpeed = speed.get(secondWindowSpeedBox.getValue());
      System.out.println(firstPairSpeed);
    });
    AnchorPane.setLeftAnchor(secondWindowSpeedBox, 10.0);
    AnchorPane.setTopAnchor(secondWindowSpeedBox, 190.0);
    secondWindowPane.getChildren().add(secondWindowSpeedBox);

    Label secondWindowSpeedLabel = new Label(" <- input port speed");
    secondWindowSpeedLabel.setFont(Font.font("Calibri", 16));
    AnchorPane.setLeftAnchor(secondWindowSpeedLabel, 180.0);
    AnchorPane.setTopAnchor(secondWindowSpeedLabel, 195.0);
    secondWindowPane.getChildren().add(secondWindowSpeedLabel);

    TextField secondWindowBytesReceivedField = new TextField();
    secondWindowBytesReceivedField.setMinSize(150, 30);
    secondWindowBytesReceivedField.setMaxSize(150, 30);
    secondWindowBytesReceivedField.setStyle("-fx-alignment: top-left;");
    secondWindowBytesReceivedField.setEditable(false);
    AnchorPane.setLeftAnchor(secondWindowBytesReceivedField, 10.0);
    AnchorPane.setTopAnchor(secondWindowBytesReceivedField, 250.0);
    secondWindowPane.getChildren().add(secondWindowBytesReceivedField);

    Label secondWindowBytesLabel = new Label(" <- output port bytes received");
    secondWindowBytesLabel.setFont(Font.font("Calibri", 16));
    AnchorPane.setLeftAnchor(secondWindowBytesLabel, 180.0);
    AnchorPane.setTopAnchor(secondWindowBytesLabel, 255.0);
    secondWindowPane.getChildren().add(secondWindowBytesLabel);

    TextArea secondWindowsStatusBar = new TextArea();
    secondWindowsStatusBar.setMinSize((windowWidth - 20), 300);
    secondWindowsStatusBar.setMaxSize((windowWidth - 20), 300);
    secondWindowsStatusBar.setStyle("-fx-alignment: top-left;");
    secondWindowsStatusBar.setEditable(false);
    AnchorPane.setLeftAnchor(secondWindowsStatusBar, 10.0);
    AnchorPane.setTopAnchor(secondWindowsStatusBar, 300.0);
    secondWindowPane.getChildren().add(secondWindowsStatusBar);



    ComboBox<String> firstWindowInputPortBox = new ComboBox<>();
    firstWindowInputPortBox.setPrefSize(150, 30);
    firstWindowInputPortBox.setValue(ports.keySet().stream().toList().get(0));
    firstWindowPortsPair = ports.get(firstWindowInputPortBox.getValue());
    secondWindowOutputPortNameField.setText(firstWindowPortsPair[1].getPortName());
    firstWindowInputPortBox.getItems().setAll(ports.keySet());
    AnchorPane.setLeftAnchor(firstWindowInputPortBox, windowWidth / 2 + 20);
    AnchorPane.setTopAnchor(firstWindowInputPortBox, 40.0);
    firstWindowPane.getChildren().add(firstWindowInputPortBox);

    ComboBox<String> secondWindowInputPortBox = new ComboBox<>();
    secondWindowInputPortBox.setPrefSize(150, 30);
    for (String port : ports.keySet().stream().toList()) {
      if (!Objects.equals(port, firstWindowPortsPair[0].getPortName()) &&
          !Objects.equals(port, firstWindowPortsPair[1].getPortName())) {
        secondWindowInputPortBox.setValue(port);
      }
    }
    secondWindowPortsPair = ports.get(secondWindowInputPortBox.getValue());
    firstWindowOutputPortNameField.setText(secondWindowPortsPair[1].getPortName());
    secondWindowInputPortBox.getItems().setAll(ports.keySet());
    AnchorPane.setLeftAnchor(secondWindowInputPortBox, windowWidth / 2 + 20);
    AnchorPane.setTopAnchor(secondWindowInputPortBox, 40.0);
    secondWindowPane.getChildren().add(secondWindowInputPortBox);


    firstWindowInputPortBox.setOnAction(event -> {
      if (!Objects.equals(firstWindowInputPortBox.getValue(),
          secondWindowInputPortBox.getValue()) &&
          !Objects.equals(firstWindowInputPortBox.getValue(),
              firstWindowOutputPortNameField.getText())) {
        firstWindowPortsPair = ports.get(firstWindowInputPortBox.getValue());
        secondWindowOutputPortNameField.setText(firstWindowPortsPair[1].getPortName());
      } else {
        firstWindowInputPortBox.setValue(secondWindowPortsPair[0].getPortName());
      }
    });

    secondWindowInputPortBox.setOnAction(event -> {
      if (!Objects.equals(secondWindowInputPortBox.getValue(),
          firstWindowInputPortBox.getValue()) &&
          !Objects.equals(secondWindowInputPortBox.getValue(),
              secondWindowOutputPortNameField.getText())) {
        secondWindowPortsPair = ports.get(secondWindowInputPortBox.getValue());
        firstWindowOutputPortNameField.setText(secondWindowPortsPair[1].getPortName());
      } else {
        secondWindowInputPortBox.setValue(secondWindowPortsPair[0].getPortName());
      }
    });


    Button firstWindowSendButton = new Button("Send");
    firstWindowSendButton.setFont(Font.font("Calibri", 16));
    firstWindowSendButton.setPrefSize(70, 30);
    AnchorPane.setLeftAnchor(firstWindowSendButton, windowWidth / 2 + 190);
    AnchorPane.setTopAnchor(firstWindowSendButton, 40.0);
    firstWindowPane.getChildren().add(firstWindowSendButton);
    firstWindowSendButton.setOnAction(event -> {
      Thread sendData = new Thread(() -> {
        try {
          long[] allBits = new long[0];

          List<long[]> packageToSend =
              BitStuffingClass.listOfLongBitsToSend(firstWindowsPortInputField.getText(),
                  firstWindowPortsPair[0].getPortName());

          secondWindowsStatusBar.setText(Output.listOfLongBitsToSend(firstWindowsPortInputField.getText(),
              firstWindowPortsPair[0].getPortName()));

          for(long[] onePackage : packageToSend) {
            firstWindowPortsPair[0].openPort();
            firstWindowPortsPair[1].openPort();
            firstWindowPortsPair[0].setParams(firstPairSpeed, 8, 1, 0);
            firstWindowPortsPair[1].setParams(firstPairSpeed, 8, 1, 0);

            byte[] longBitsToBytes = BytesBits.fromBitsToByteArray(onePackage);
            firstWindowPortsPair[0].writeBytes(longBitsToBytes);
            byte[] receivedBytes = firstWindowPortsPair[1].readBytes();
            long[] receivedBytesInLong = BytesBits.fromBytesArrayToBits(receivedBytes);

            long[] receivedFcs = BitStuffingClass.fcsFromPackage(receivedBytesInLong);
            long[] receivedData = BitStuffingClass.dataFromPackage(receivedBytesInLong);
            long[] currentFcs = HemmingCodeClass.getFCSHemmingCode(receivedData);
            secondWindowsStatusBar.setText(secondWindowsStatusBar.getText() + "Current FCS: " +
                Arrays.toString(currentFcs) + "\n");
            long[] finalData = HemmingCodeClass.checkHemmingCode(receivedData, receivedFcs);

            allBits = BitStuffingClass.extractDataFromPackage(allBits, finalData);
            BitStuffingClass.outputln(allBits);


            firstWindowPortsPair[0].closePort();
            firstWindowPortsPair[1].closePort();
          }
          long[] cuttedBits = BitStuffingClass.cutting(allBits);
          long[] stuffedBits = BitStuffingClass.bitStuffingDataTo(cuttedBits);
          for(long l : stuffedBits){
            System.out.print(l);
          }
          System.out.println();

          secondWindowBytesReceivedField.setText(String.valueOf(packageToSend.size() * 24));
          secondWindowsOutputField.setText(new String(BytesBits.fromBitsToByteArray(stuffedBits)));
        } catch (SerialPortException e) {
          throw new RuntimeException(e);
        }
      });

      try {
        sendData.start();
        sendData.join();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });

    Button secondWindowSendButton = new Button("Send");
    secondWindowSendButton.setFont(Font.font("Calibri", 16));
    secondWindowSendButton.setPrefSize(70, 30);
    AnchorPane.setLeftAnchor(secondWindowSendButton, windowWidth / 2 + 190);
    AnchorPane.setTopAnchor(secondWindowSendButton, 40.0);
    secondWindowPane.getChildren().add(secondWindowSendButton);
    secondWindowSendButton.setOnAction(event -> {
      Thread sendData = new Thread(() -> {
        try {
          long[] allBits = new long[0];

          List<long[]> packageToSend =
              BitStuffingClass.listOfLongBitsToSend(secondWindowsPortInputField.getText(),
                  secondWindowPortsPair[0].getPortName());

          firstWindowsStatusBar.setText(Output.listOfLongBitsToSend(secondWindowsPortInputField.getText(),
              secondWindowPortsPair[0].getPortName()));

          for(long[] onePackage : packageToSend) {
            secondWindowPortsPair[0].openPort();
            secondWindowPortsPair[1].openPort();
            secondWindowPortsPair[0].setParams(secondPairSpeed, 8, 1, 0);
            secondWindowPortsPair[1].setParams(secondPairSpeed, 8, 1, 0);

            byte[] longBitsToBytes = BytesBits.fromBitsToByteArray(onePackage);
            secondWindowPortsPair[0].writeBytes(longBitsToBytes);
            byte[] receivedBytes = secondWindowPortsPair[1].readBytes();
            long[] receivedBytesInLong = BytesBits.fromBytesArrayToBits(receivedBytes);

            long[] receivedFcs = BitStuffingClass.fcsFromPackage(receivedBytesInLong);
            long[] receivedData = BitStuffingClass.dataFromPackage(receivedBytesInLong);
            long[] currentFcs = HemmingCodeClass.getFCSHemmingCode(receivedData);
            firstWindowsStatusBar.setText(firstWindowsStatusBar.getText() + "Current FCS: " +
                Arrays.toString(currentFcs) + "\n");
            long[] finalData = HemmingCodeClass.checkHemmingCode(receivedData, receivedFcs);

            allBits = BitStuffingClass.extractDataFromPackage(allBits, finalData);
            BitStuffingClass.outputln(allBits);

            secondWindowPortsPair[0].closePort();
            secondWindowPortsPair[1].closePort();
          }
          long[] cuttedBits = BitStuffingClass.cutting(allBits);
          long[] stuffedBits = BitStuffingClass.bitStuffingDataTo(cuttedBits);

          firstWindowBytesReceivedField.setText(String.valueOf(packageToSend.size() * 24));
          firstWindowsOutputField.setText(new String(BytesBits.fromBitsToByteArray(stuffedBits)));
        } catch (SerialPortException e) {
          throw new RuntimeException(e);
        } finally {
          try {
            secondWindowPortsPair[0].closePort();
            secondWindowPortsPair[1].closePort();
          } catch (SerialPortException e) {
            throw new RuntimeException(e);
          }
        }
      });

      try {
        sendData.start();
        sendData.join();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });

    Scene firstWindowScene =
        new Scene(firstWindowPane, windowWidth, windowHeight);
    Stage firstWindow = new Stage();
    firstWindow.setTitle("First Window");
    firstWindow.setScene(firstWindowScene);
    firstWindow.setX(100);
    firstWindow.setY(100);
    firstWindow.show();

    Scene secondWindowScene =
        new Scene(secondWindowPane, windowWidth, windowHeight);
    Stage secondWindow = new Stage();
    secondWindow.setTitle("Second Window");
    secondWindow.setScene(secondWindowScene);
    secondWindow.setX(displayDimension.width / 2 + 100);
    secondWindow.setY(100);
    secondWindow.show();
  }

  @Override
  public void start(Stage stage) {
    ports = getPortPairs();
    System.out.println("Port pairs: ");
    for (String key : ports.keySet()) {
      System.out.println(key);
    }
    if (ports.size() < 2) {
      System.err.println("Not Enough Ports For Work!");
      return;
    }
    initializeWindows();
  }
}