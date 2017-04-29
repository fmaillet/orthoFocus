/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package orthoFocus;



import java.io.IOException;
import java.util.Random;
import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.newdawn.easyogg.OggClip;


/**
 *
 * @author fmail
 */
public class OrthoFocus extends Application {
    
    final public static String version = "0.4.0" ;
    
    static boolean TSsupport, MTSsupport ;
    static GameFX game ;
    
    static int itemSize = 1 ;
    static int nbItemsToCreate = 8 ;
    static int nbDistractorsToCreate = 20 ;
    static boolean doSensorialDistraction  = false ;
    static boolean doSocialDistraction = false ;
    static Button launchGame ;
    static Stage mainStage ;
    
    private static int[] numeroEssai = new int[4] ;
    
    @Override
    public void start(Stage stage) {
        Random r = new Random() ;
        this.mainStage = stage ;
        for (int i=0; i<4; i++) numeroEssai[i] = 0 ;
        
        stage.setWidth(800);
        stage.setHeight(600);
        if (TSsupport) stage.setTitle("orthoFocus " + version + " (Touch Screen supported !)");
        else if (MTSsupport) stage.setTitle("orthoFocus " + version + " (MultiTouch Screen supported !)");
        else stage.setTitle("orthoFocus " + version + " (Touch Screen not supported !)");
        
        final Group rootGroup = new Group();
        final Scene scene = new Scene(rootGroup, 800, 400, Color.WHEAT);
        final MenuBar menuBar = MyMenuBar(stage.widthProperty());
        rootGroup.getChildren().add(menuBar);
        
        //Ecrans ?
        Screen primary = Screen.getPrimary();
        Screen secondary ;
        if ( Screen.getScreens().size() > 1 ) secondary = Screen.getScreens().get(1) ;
        else secondary = primary ;
        
        /*// création Cercle 2
        Group sign2 = new Group();
        sign2.setTranslateX(100);
        sign2.setTranslateY(150);
        Circle sun2 = new Circle(60, Color.web("green", 0.8));
        sun2.setCenterX(30);
        sun2.setCenterY(30);
        sign2.getChildren().add(sun2);
        Text text2 = new Text(20, 40, "L");
        text2.setFont(new Font(30));
        sign2.getChildren().add(text2);
        
        // création Carré 1
        Group sign3 = new Group();
        sign3.setTranslateX(150);
        sign3.setTranslateY(150);
        Rectangle sq1 = new Rectangle(120, 120);
        sq1.setFill(Color.YELLOW);
        sign3.getChildren().add(sq1);
        Text text3 = new Text(47, 70, "W");
        text3.setFont(new Font(30));
        sign3.getChildren().add(text3);
        
        // création Carré 2
        Group sign4 = new Group();
        sign4.setTranslateX(200);
        sign4.setTranslateY(200);
        Rectangle sq2 = new Rectangle(120, 120);
        sq2.setFill(Color.GREEN);
        sign4.getChildren().add(sq2);
        Text text4 = new Text(47, 70, "R");
        text4.setFont(new Font(30));
        sign4.getChildren().add(text4);*/
        
        
        
        
        
        
        /*// création d'un élément plus complexe, le panneau
        Group sign = new Group();
        sign.setTranslateX(150);
        sign.setTranslateY(200);
        // Attention les coordonnées sont celles du panneau, pas de la scène
        Text text = new Text(10, 30, "Hello world!");
        text.setFont(new Font(80));
        //text.setFill(Color.WHITE);
        // le repère utilisé est celui du panneau
        Rectangle panel = new Rectangle( 0, -50, 500, 110);
        panel.setFill(Color.DARKBLUE);
        // composer l'élément plus complexe
        sign.getChildren().add(panel);
        sign.getChildren().add(text);*/
        
        /*//Generic event handler
        EventHandler handler = new EventHandler<InputEvent>() {
            @Override
            public void handle(InputEvent event) {
                //System.out.println("Handling event " + event.getEventType()); 
                int x = r.ints (60, (int) scene.getWidth() - 61).findFirst().getAsInt() ;
                int y = r.ints (60, (int) (2*scene.getHeight())/3 - 61).findFirst().getAsInt() ;
                ((Group) event.getSource()).setTranslateX(x);
                ((Group) event.getSource()).setTranslateY(y);
                event.consume();
            }
        };*/
        
        /*// Define mouse event handlers
        sign2.addEventHandler(MouseEvent.MOUSE_CLICKED, handler) ;
        sign3.addEventHandler(MouseEvent.MOUSE_CLICKED, handler) ;
        sign4.addEventHandler(MouseEvent.MOUSE_CLICKED, handler) ;
        // Define touch event handlers
         sign2.addEventHandler(TouchEvent.TOUCH_PRESSED, handler) ;
        sign3.addEventHandler(TouchEvent.TOUCH_PRESSED, handler) ;
        sign4.addEventHandler(TouchEvent.TOUCH_PRESSED, handler) ;
        // ajout de tous les éléments de la scène
        root.getChildren().add(sign2);
        root.getChildren().add(sign3);
        root.getChildren().add(sign4);*/
        
        //Choix de la taille
        Label labelSize = new Label("Taille des items :");
        ChoiceBox cbSize = new ChoiceBox(FXCollections.observableArrayList(
            "Petit", "Moyen", "Grand")
        );
        cbSize.setTooltip(new Tooltip("Taille des caractères"));
        cbSize.setValue("Moyen");
        //final List options = cbSize.getItems();
        cbSize.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener() {
            
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                itemSize = (int) newValue ;
                //System.out.println("New Selected Option: " + itemSize);
            }
        });
        labelSize.setTranslateX(240) ;  labelSize.setTranslateY(85) ;
        cbSize.setTranslateX(350) ;  cbSize.setTranslateY(80) ;
        rootGroup.getChildren().addAll(cbSize, labelSize);
        
        //Nombre d'items
        Label labelNbItems = new Label("Nombre d'items :");
        final Spinner<Integer> nbItems = new Spinner<Integer>();
        SpinnerValueFactory<Integer> valueFactory = 
               new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30, 8);
        nbItems.setValueFactory(valueFactory);
        labelNbItems.setTranslateX(20); labelNbItems.setTranslateY(85);
        nbItems.setTranslateX(120); nbItems.setTranslateY(80);
        nbItems.setMaxWidth(70);
        rootGroup.getChildren().addAll(nbItems, labelNbItems);
        
        //Nombre de distracteurs
        Label labelNbDistracts = new Label("Nb Distracteurs :");
        final Spinner<Integer> nbDistracts = new Spinner<Integer>();
        SpinnerValueFactory<Integer> valueFactory2 = 
               new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 30, 5);
        nbDistracts.setValueFactory(valueFactory2);
        labelNbDistracts.setTranslateX(20); labelNbDistracts.setTranslateY(135);
        nbDistracts.setTranslateX(120); nbDistracts.setTranslateY(130);
        nbDistracts.setMaxWidth(70);
        rootGroup.getChildren().addAll(nbDistracts, labelNbDistracts);
        
        //Distraction sensorielle
        Label labelDistraction = new Label("Distraction :");
        labelDistraction.setTranslateX(20) ;  labelDistraction.setTranslateY(200) ;
        CheckBox dsSensoriel = new CheckBox("Sensorielle");
        dsSensoriel.setTranslateX(120) ;  dsSensoriel.setTranslateY(200) ;
        CheckBox dsSocial = new CheckBox("Sociale");
        dsSocial.setTranslateX(220) ;  dsSocial.setTranslateY(200) ;
        rootGroup.getChildren().addAll(labelDistraction, dsSensoriel, dsSocial);
        
        //Un bouton pour lancer le jeu
        launchGame = new Button("Démarrer le jeu");
        launchGame.setOnAction((ActionEvent e) -> {
            //On donne la consigne
            OggClip consigne = null ;
            try { consigne = new OggClip(this.getClass().getResourceAsStream("consigne.ogg")); }
            catch (final IOException ie) {System.out.println ("Sound loading pb: " + ie.toString()) ;}
            if (consigne != null) consigne.play() ;
            
            nbItemsToCreate = nbItems.getValue() ;
            nbDistractorsToCreate = nbDistracts.getValue() ;
            doSensorialDistraction = dsSensoriel.isSelected() ;
            doSocialDistraction = dsSocial.isSelected() ;
            if (primary.equals(secondary)) stage.setIconified(true);
            game = new GameFX (secondary) ;
            launchGame.setDisable(true);
        });
        //add the button to the scene
        launchGame.setTranslateX(20) ;
        launchGame.setTranslateY(270) ;
        rootGroup.getChildren().add(prepareGraph());
        
        //Prepare graph
        rootGroup.getChildren().add(launchGame);
        
        // ajout de la scène sur l'estrade
        stage.setScene(scene);
        // ouvrir le rideau
        stage.show();
        stage.centerOnScreen();
        
    }
    
    static private XYChart.Series serie1, serie2, serie3, serie4 ;
    
    private LineChart prepareGraph () {
        final CategoryAxis xAxis = new CategoryAxis();
        //xAxis.setTickUnit(1);
        //xAxis.setMaxWidth(35);
        //xAxis.setMinorTickCount(1);
        //xAxis.set
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Temps (sec.)");
        yAxis.setAutoRanging(true);
        yAxis.setForceZeroInRange(false);
        
        //Chart
        final LineChart<String,Number> lineChart = 
                new LineChart<String,Number>(xAxis,yAxis);
        lineChart.setTitle("Résultats");
        lineChart.setLayoutX(300); lineChart.setLayoutY(130);
        lineChart.setMaxWidth(450); lineChart.setMaxHeight(350);
        
        //Datas
        serie1 = new XYChart.Series();
        serie1.setName("Sans");
        //serie1.getData().add(new XYChart.Data("1", 23));
        //serie1.getData().add(new XYChart.Data("2", 14));
        serie2 = new XYChart.Series();
        serie2.setName("Sensoriel");
        //serie2.getData().add(new XYChart.Data("1", 19));
        //serie2.getData().add(new XYChart.Data("2", 12));
        serie3 = new XYChart.Series();
        serie3.setName("Social");
        //serie3.getData().add(new XYChart.Data("1", 10));
        //serie3.getData().add(new XYChart.Data("2", 10));
        serie4 = new XYChart.Series();
        serie4.setName("Les deux");
        //serie4.getData().add(new XYChart.Data("1", 8));
        //serie4.getData().add(new XYChart.Data("2", 9));
        
        lineChart.getData().addAll(serie1, serie2, serie3, serie4);
        //lineChart.setCreateSymbols(false);
        
        return lineChart;
    }
    
    private MenuBar MyMenuBar (final ReadOnlyDoubleProperty menuWidthProperty) {
        
        //On retourne la barre de menu
        final MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(menuWidthProperty);
        
        // File menu - new, save, exit
        Menu fileMenu = new Menu("File");
        MenuItem newMenuItem = new MenuItem("Nouveau");
        newMenuItem.setDisable(true);
        MenuItem saveMenuItem = new MenuItem("Sauver");
        saveMenuItem.setDisable(true);
        MenuItem exitMenuItem = new MenuItem("Quitter");
        exitMenuItem.setOnAction(actionEvent -> Platform.exit());
        exitMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.SHORTCUT_DOWN));
        
        fileMenu.getItems().addAll(newMenuItem, saveMenuItem,
            new SeparatorMenuItem(), exitMenuItem);
        menuBar.getMenus().addAll(fileMenu);
        
        //Return menubar
        return menuBar;
    }
    
    static public void gameEnded (boolean normalEnd, long delay) {
        //On remet le bouton on
        launchGame.setDisable(false);
        //On supprime la fenêtre de jeu
        game = null ;
        mainStage.setIconified(false);
        System.gc();
        //On met à jour le graphe
        if (normalEnd) {
            XYChart.Data<String, Number> data ;
            if (!doSensorialDistraction && !doSocialDistraction) {
                numeroEssai[0]++;
                data = new XYChart.Data<String, Number>(Integer.toString(numeroEssai[0]), delay) ;
                serie1.getData().add(data);
            }
            else if (doSensorialDistraction && !doSocialDistraction) {
                numeroEssai[1]++;
                data = new XYChart.Data<String, Number>(Integer.toString(numeroEssai[1]), delay) ;
                serie2.getData().add(data);
                
            }
            else if (!doSensorialDistraction && doSocialDistraction) {
                numeroEssai[2]++;
                data = new XYChart.Data<String, Number>(Integer.toString(numeroEssai[2]), delay) ;
                serie3.getData().add(data);
            }
            else {
                numeroEssai[3]++;
                data = new XYChart.Data<String, Number>(Integer.toString(numeroEssai[3]), delay) ;
                serie4.getData().add(data);
            }
            //ToolTips sur le graph
            data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    //lbl.setText(data.getYValue()+"");
                    Tooltip t = new Tooltip(data.getYValue()+"");
                    t.setOpacity(0.5);
                    Tooltip.install(data.getNode(), t);

                }
            });
        }
        
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TSsupport = Platform.isSupported(ConditionalFeature.INPUT_TOUCH);
        MTSsupport = Platform.isSupported(ConditionalFeature.INPUT_MULTITOUCH);
        
        /* check for maryTTS here :
        http://lukealderton.com/blog/posts/2013/december/using-marytts-or-openmary-in-java/
        or : video :
        https://www.youtube.com/watch?v=OLKxBorVwk8
        */
        
        //On lance la fenêtre
        
        launch(args);
        
    }
    
}
