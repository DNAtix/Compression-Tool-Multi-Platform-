/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication1;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 *
 * @author HP
 */
public class JavaFXApplication1 extends Application {
    
    
     // DNAtix compressed format extension
        private static final String DNATIX_EXTENSION = ".dtix";
        
        // About text
        private static final String ABOUT =
            "DNAtix - DNA Compression Tool - v0.1b\n\n" +
            "(c)2018 DNAtix Ltd. - All rights reserved. - www.dnatix.com\n\n" +
            "This tool is a free tool developed by DNAtix for compressing DNA sequence files.\n\n" +
            "Developed by the DNAtix Development Team for the DNAtix Genetics Eco-system.\n\n" +
            "Contact us at: support@dnatix.com";

    
    
    
    
    Button browse, compress, decompress;
    TextField filePathTextField;
    ImageView imageViewLogo;
    
    File file;
    private MenuBar menuBar;
    
    
    
    
    
    
    
    
    
    
    
    
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        menuBar = new MenuBar();
       Label menuLabel = new Label("About us");
        menuLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            Alert alert = new Alert(AlertType.INFORMATION, ABOUT);
            alert.setTitle("About us");
            alert.show();
        }
    });
    Menu fileMenuButton = new Menu();
    fileMenuButton.setGraphic(menuLabel);
    menuBar.getMenus().add(fileMenuButton);
        
        browse = new Button();
        browse.setLayoutX(503);
        browse.setLayoutY(116);
        browse.setPrefHeight(26);
        browse.setPrefWidth(100);
        browse.setStyle("-fx-text-base-color: #ffffff;"
                + "-fx-background-color: #008b8b;");

        browse.setText("Browse");
        browse.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Browse");
                List<File> fileNameList = fileChooser.showOpenMultipleDialog(primaryStage);
                
                if (fileNameList == null || fileNameList.size() < 1)
                    return;
                
                file = fileNameList.get(0);
                filePathTextField.setText(file.getPath());
            }
            private Window name;
        });
        
        
        filePathTextField = new TextField();
        filePathTextField.setLayoutX(33);
        filePathTextField.setLayoutY(116);
        filePathTextField.setPrefHeight(26);
        filePathTextField.setPrefWidth(450);
        filePathTextField.setEditable(false);
        
        
        compress = new Button();
        compress.setLayoutX(33);
        compress.setLayoutY(229);
        compress.setPrefHeight(75);
        compress.setPrefWidth(258);
        Image newGame = new Image("File:Resources\\CompressDNA1.png");
        ImageView compressImage = new ImageView(newGame);
        compressImage.setFitHeight(75);
        compressImage.setFitWidth(258);
        compress.setGraphic(compressImage);
        
        compress.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if (filePathTextField.getText().equals("")){
                    Alert alert = new Alert(AlertType.ERROR, "No file to compress");
                    alert.show();
                    return;
                }
                
                
                File f = new File(filePathTextField.getText());
                if(!f.exists() || f.isDirectory()) { 
                    Alert alert = new Alert(AlertType.ERROR, "File doesn't exist anymore");
                    alert.show();
                    return;
                }
                        
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Compress");
                File dest = fileChooser.showSaveDialog(primaryStage);
                
                if (dest == null)
                    return;
                
                if (Compress.CompressFile(file.getPath(), dest.getPath()) != 0){
                    Alert alert = new Alert(AlertType.ERROR, "Failed to compress " + filePathTextField.getText());
                    alert.show();
                    return;
                }
                
                Alert alert = new Alert(AlertType.INFORMATION, "File Compressed Successfully");
                alert.show();
            }
        });
        
        
        decompress = new Button();
        decompress.setLayoutX(329);
        decompress.setLayoutY(229);
        decompress.setPrefHeight(75);
        decompress.setPrefWidth(258);
        Image newGame2 = new Image("File:Resources\\DecompressDNA1.png");
        ImageView decompressImage = new ImageView(newGame2);
        decompressImage.setFitHeight(75);
        decompressImage.setFitWidth(258);
        decompress.setGraphic(decompressImage);

        
        decompress.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event){
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Decompress");
                File dest = fileChooser.showSaveDialog(primaryStage);
                
                try {
                    if (filePathTextField.getText().equals("")){
                        Alert alert = new Alert(AlertType.ERROR, "No file to decompress");
                        alert.show();
                        return;
                    }
                    
                    if (Compress.DecompressFile(file.getPath(), dest.getPath()) != 0){
                        Alert alert = new Alert(AlertType.ERROR, "Failed to decompress " + filePathTextField.getText());
                        alert.show();
                        return;
                    }
                } catch (IOException ex) {
                    Logger.getLogger(JavaFXApplication1.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        
        
        Image image = new Image("File:Resources\\DNATIX_logo.png");
        imageViewLogo = new ImageView(image);
        imageViewLogo.setLayoutX(163);
        imageViewLogo.setLayoutY(28);    
        imageViewLogo.setFitHeight(75);
        imageViewLogo.setFitWidth(258);
        
        Pane root = new Pane();
        root.getChildren().add(browse);
        root.getChildren().add(filePathTextField);
        root.getChildren().add(compress);
        root.getChildren().add(decompress);
        root.getChildren().add(imageViewLogo);
        root.getChildren().add(menuBar);
        
        Scene scene = new Scene(root, 630, 325);
        
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image("File:Resources\\fav_icon.png"));
        primaryStage.setTitle("DNAtix Compression Tool");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        launch(args);
    }
    
}
