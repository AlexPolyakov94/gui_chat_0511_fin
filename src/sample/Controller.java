package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.WindowEvent;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Controller implements Serializable {
    Socket socket;
    DataOutputStream out;
    Thread thread;
    @FXML
    TextArea textArea;
    @FXML
    TextField textField;
    @FXML
    TextArea textAreaUserList;
    ObjectOutputStream chatList;


    @FXML
    private void onSubmit(){
        String text = textField.getText();
        textArea.appendText(text+"\n");
        textField.clear();
        try {
            out.writeUTF(text);
        } catch (IOException exception) {
            textArea.appendText("Произошла ошибка");
            exception.printStackTrace();
        }
    }

    @FXML
    private void connect() {
        try {
            //Socket socket = new Socket("192.168.0.21",8188);
            //Socket socket = new Socket("45.80.70.161",8188);
            socket = new Socket("localhost",8188);
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            out=new DataOutputStream(socket.getOutputStream());

            try {
                String response = ois.readObject().toString();
                textArea.appendText(response+"\n");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    while (true) {
                        try {
                            Object object = ois.readObject();
                            if (String.class == object.getClass()) {
                                textArea.appendText(object.toString() + "\n");
                            } else if (ArrayList.class == object.getClass()) {
                                ArrayList<String> usersName = new ArrayList<String>();
                                usersName = (ArrayList<String>) object;
                                textAreaUserList.clear();
                                for (String userName : usersName) {
                                    textAreaUserList.appendText(userName + "\n");
                                    out.write(Integer.parseInt(userName));
                                }
                            } else {
                                System.out.println("Класс не определен");
                            }

                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

}