/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package orthoFocus;

//import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.animation.PathTransition.OrientationType;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.newdawn.easyogg.OggClip;


/**
 *
 * @author Fred
 */
public class GameFX extends Stage {
   
    static Stage thisStage ;
    static PerceptiveDistraction sideDistractor ;
    static SocialDistraction socialDistractor ;
    static Face face ;
    SearchItems items ;
     
    //Constructor
    public GameFX (Screen s) {
        thisStage = this ;
        sideDistractor = null ;
        items = null ;
        //Position
        Rectangle2D bounds = s.getBounds();
        this.setX(bounds.getMinX());
        this.setY(bounds.getMinY());
        this.setWidth(bounds.getWidth());
        this.setHeight(bounds.getHeight());
        //this.setFullScreen(true);
        
        //Size ?
        setWidth (800) ; setHeight(600) ;
        setTitle ("Game stage") ;
        //On décore
        Group root = new Group();
        Scene scene = new Scene(root);
        scene.setFill(Color.SKYBLUE);
        // création du sol
        Rectangle ground = new Rectangle(0, 2*getHeight()/3, getWidth(), getHeight()/3);
        ground.setFill(Color.GREEN);
        // Listen to resize
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                //Nouvelle largeur
                ground.setWidth( newValue.doubleValue() );
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                //System.out.println("Width: " + newValue);
                double p = ( (double) newValue / 3 ) * 2 ;
                ground.setY(p);
                ground.setHeight((double) newValue - p );
            }
        });
        
        //On ajoute les éléments
        // ajout de tous les éléments de la scène
        root.getChildren().add(ground);
        
        //On teste le visage
        face = new Face (2*this.getHeight()/3) ;
        root.getChildren().add(face) ;
        
        //On affiche
        setScene(scene);
        centerOnScreen () ;
        this.setMaximized(true);
        //this.setFullScreen(true);
        show () ;
        sideDistractor = new PerceptiveDistraction (ground) ;
        sideDistractor.start();
        socialDistractor = new SocialDistraction (ground, face) ;
        socialDistractor.start();
        //Si on ferme la fenêtre, on arrête le thread
        this.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                
                GameFX.itsAllDone(false);
            }
        });
        
        //On écoute le clavier
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override public void handle(KeyEvent event) {
              switch (event.getCode()) {
                case ESCAPE: fireEvent(new WindowEvent(thisStage,WindowEvent.WINDOW_CLOSE_REQUEST)); GameFX.itsAllDone(false); break;
                default:  break;
              }
            }
        });
        
        
        //On teste l'ajout d'items
        items = new SearchItems (OrthoFocus.nbItemsToCreate, this.getWidth(), 2*this.getHeight()/3) ;
        root.getChildren().add(items) ;
        SearchDistractors distracts = new SearchDistractors (OrthoFocus.nbDistractorsToCreate, this.getWidth(), 2*this.getHeight()/3, items) ;
        root.getChildren().add(distracts) ;
        
        
    }
    
    static public void itsAllDone (boolean normalEnd) {
        sideDistractor.interrupt();
        socialDistractor.interrupt();
        OrthoFocus.gameEnded (normalEnd) ;
        //thisStage.fireEvent(new WindowEvent(thisStage,WindowEvent.WINDOW_CLOSE_REQUEST));
    }
}

class PerceptiveDistraction extends Thread {
    
    static Rectangle ground ;
    boolean keepRunning ;
    
    public PerceptiveDistraction (Rectangle ground) {
        this.ground = ground ;
        this.keepRunning = true ;
        this.setName("Sensorial");
    }
    
    public void run(){
        if (!OrthoFocus.doSensorialDistraction) {
            ground.setFill(Color.GREEN);
            return ;
        }
        while (this.keepRunning) {
            for (int i = 0; i < 256; i++) {
                   ground.setFill(Color.hsb(i, 1.0f, 1.0f));
                   //ground.setFill(Color.hsb(i / 256.0f, 1.0f, 1.0f));
                   try {Thread.sleep(12);} catch (InterruptedException ie) {this.keepRunning = false;}
            }
        }
    }
    
}

class SocialDistraction extends Thread {
    
    static Rectangle ground ;
    static Face face ;
    boolean keepRunning ;
    
    public SocialDistraction (Rectangle ground, Face face) {
        this.ground = ground ;
        this.face = face ;
        this.keepRunning = true ;
        this.setName("SocialThrd");
    }
    
    public void run(){
        face.setVisible(OrthoFocus.doSocialDistraction);
        if (!OrthoFocus.doSocialDistraction) return ;
        //face initial position
        face.setTranslateX(0);face.setTranslateY(0);
        face.setLayoutX(ground.getX() + ThreadLocalRandom.current().nextInt(100, 300 ));
        face.setLayoutY(ground.getY() + ThreadLocalRandom.current().nextInt(40, 100 ));
        
        //Trajet
        PathElement[] path = 
        {
            new MoveTo(0, 200),
            //new ArcTo(100, 100, 0, 100, 400, false, false),
            new LineTo(300, 150),
            new ArcTo(100, 100, 0, ThreadLocalRandom.current().nextInt(10, 100 ), 100, false, false),
            new LineTo(ground.getWidth()/2, 100),
            new ArcTo(100, 100, 0, ThreadLocalRandom.current().nextInt(80, 150 ), ThreadLocalRandom.current().nextInt(80, 150 ), false, false),
            new LineTo(ThreadLocalRandom.current().nextInt(100, (int) ground.getWidth()), 80),
            new ArcTo(100, 100, 0, 100, 100, false, false),
            //new LineTo(0, 300),
            new ClosePath()
        };
        Path road = new Path();
        road.getElements().addAll(path);
        PathTransition anim = new PathTransition();
        anim.setNode(face);
        anim.setPath(road);
        anim.setOrientation(OrientationType.ORTHOGONAL_TO_TANGENT);
        anim.setInterpolator(Interpolator.LINEAR);
        anim.setDuration(new Duration(20000));
        //anim.setAutoReverse(true);
        anim.setCycleCount(1);
        anim.play() ;
        
        //On boucle pendant l'exercice
        while (this.keepRunning) {
            if (anim.getStatus() == Animation.Status.STOPPED) {
                //On change le chemin
                System.out.println ("change path") ;
                PathElement[] path2 = {
                    new MoveTo(0, ThreadLocalRandom.current().nextInt(100, (int) ground.getHeight())),
                    new ArcTo(100, 100, 0, 100, 100, false, false),
                    new LineTo(ThreadLocalRandom.current().nextInt(100, (int) ground.getWidth()), ThreadLocalRandom.current().nextInt(100, (int) ground.getHeight())),
                    new ArcTo(100, 100, 0, 100, 100, false, false),
                    new LineTo(300, 150),
                    new ClosePath()
                };
                road.getElements().clear();
                road.getElements().addAll(path2);
                anim.setOrientation(OrientationType.ORTHOGONAL_TO_TANGENT);
                anim.setDuration(new Duration(10000));
                anim.setAutoReverse(true);
                anim.setCycleCount(2);
                anim.play () ;
            }
            try {Thread.sleep(250);} catch (InterruptedException ie) {this.keepRunning = false;}
        }
    }
    
}

class Face extends Group {
    
    //Constructor
    public Face (double height) {
        //invisible par défaut
        this.setVisible(false);
        //position
        setTranslateX(300);
        setTranslateY(height+50);
        //Le visage
        Circle rface = new Circle (0, 0, 60) ;
        rface.setFill (Color.TRANSPARENT) ;
        rface.setStroke(Color.BLACK);
        this.getChildren().add(rface) ;
        //Oeil droit
        Circle rOD = new Circle (-22, -17, 8) ;
        rOD.setFill (Color.BLACK) ;
        this.getChildren().add(rOD) ;
        //Oeil droit
        Circle rOG = new Circle (+22, -17, 8) ;
        rOG.setFill (Color.BLACK) ;
        this.getChildren().add(rOG) ;
        //Le sourire
        Arc a1 = new Arc(0, 25, 25, 15, 180, 180);
        a1.setType(ArcType.OPEN);
        a1.setStroke(Color.BLACK);
        a1.setFill(null);
        a1.setStrokeWidth(2);
        this.getChildren().add(a1) ;
        //Le nez
        Polygon rNose = new Polygon(new double[]{
            0.0, -5.0,
            10.0, 20.0,
            -10.0, 20.0 });
        rNose.setStroke(Color.BLACK);
        rNose.setFill(null);
        this.getChildren().add(rNose) ;
    }
}

class SearchItems extends Group {
    int labelSize, labelFont ;
    Font f ;
    static int nbItems = 20 ;
    public Group items[] ;
    static int reste ;
    
    public SearchItems (int nbItems, double width, double height) {
        reste = nbItems ;
        this.nbItems = nbItems ;
        //Generic event handler
        EventHandler<InputEvent> handler = new EventHandler<InputEvent>() {
            @Override
            public void handle(InputEvent event) {
                ((Group) event.getSource()).setVisible(false);
                ((Group) event.getSource()).removeEventHandler(MouseEvent.MOUSE_CLICKED, this);
                ((Group) event.getSource()).removeEventHandler(TouchEvent.TOUCH_PRESSED, this);
                event.consume();
                reste-- ;
                OggClip audio = null ;
                if (reste == 3) {
                    //On encourage
                    if (audio != null) audio.close();
                    try { audio = new OggClip(this.getClass().getResourceAsStream("plusque3.ogg")); }
                    catch (final IOException e) {System.out.println ("Sound loading pb: " + e.toString()) ;}
                    if (audio != null) audio.play() ;
                }
                if (reste == 1) {
                    //On encourage
                    if (audio != null) audio.close();
                    try { audio = new OggClip(this.getClass().getResourceAsStream("plusque1.ogg")); }
                    catch (final IOException e) {System.out.println ("Sound loading pb: " + e.toString()) ;}
                    audio.play() ;
                }
                if (reste == 0) {
                    //On encourage
                    if (audio != null) audio.close();
                    try { audio = new OggClip(this.getClass().getResourceAsStream("bravo.ogg")); }
                    catch (final IOException e) {System.out.println ("Sound loading pb: " + e.toString()) ;}
                    audio.play() ;
                    GameFX.thisStage.fireEvent(new WindowEvent(GameFX.thisStage,WindowEvent.WINDOW_CLOSE_REQUEST));
                    GameFX.itsAllDone(true);
                }
            }
        };
        
        //Taille ?
        switch (OrthoFocus.itemSize) {
            case 0 :
                //f = Font.font("SansSerif", FontWeight.BOLD, 14);
                f = Font.font("SansSerif", 14);
                labelSize = 35 ; labelFont = 14 ;
                break;
            case 1 :
                //f = Font.font("SansSerif", FontWeight.BOLD, 20);
                f = Font.font("SansSerif", 20);
                labelSize = 45 ; labelFont = 20 ;
                break ;
            default :
                //f = Font.font("SansSerif", FontWeight.BOLD, 24);
                f = Font.font("SansSerif", 24);
                labelSize = 55 ; labelFont = 24 ;
        }
       
        //Char size in pixels
        final Text text = new Text("1");
        text.setFont(f);
        double w = text.getLayoutBounds().getWidth();
        double h = text.getLayoutBounds().getHeight();
        w = (labelSize - w) / 2 ;
        h = ((labelSize + h) / 2 ) - (h/4) ;
        
        // Grid ?
        int nH = (int) (width / labelSize) - 1 ;
        int nV = (int) (height / labelSize) - 2 ;
        int x, y ;
        //On crée les items
        items = new Group[nbItems] ;
        for (int i=0; i< this.nbItems; i++) {
            items[i] = new Group () ;
            //Sa position
            do {
                x = ThreadLocalRandom.current().nextInt(0, nH - 1 ); ;
                y = ThreadLocalRandom.current().nextInt(0, nV );
            } while (this.contains ( labelSize + (x * labelSize), labelSize + (y * labelSize))) ;
            items[i].setTranslateX(labelSize + (x * labelSize));
            items[i].setTranslateY(labelSize + (y * labelSize));
            //Rectangle
            Rectangle r1 = new Rectangle(0,0,labelSize, labelSize);
            //r1.setFill(Color.CYAN);
            r1.setOpacity(0);
            items[i].getChildren().add(r1);
            //Texte
            Text t1 = new Text(w, h, Integer.toString(ThreadLocalRandom.current().nextInt(1, 10 )));
            t1.setFont(f);
            items[i].getChildren().add(t1);
            //ajouté au groupe
            this.getChildren().add(items[i]);
            //On écoute ce qui se passe
            items[i].addEventHandler(MouseEvent.MOUSE_CLICKED, handler);  //setOnMousePressed(handler);
            items[i].addEventHandler(TouchEvent.TOUCH_PRESSED, handler) ; //setOnTouchPressed(handler);
        } 
    }
} //End of class

class SearchDistractors extends Group {
    int labelSize, labelFont ;
    Font f ;
    int nbItems = 50 ;
    Group items[] ;
    static int reste ;
    
    public SearchDistractors (int nbItems, double width, double height, SearchItems s) {
        reste = nbItems ;
        this.nbItems = nbItems ;
        
        //Taille ?
        switch (OrthoFocus.itemSize) {
            case 0 :
                //f = Font.font("SansSerif", FontWeight.BOLD, 14);
                f = Font.font("SansSerif", 14);
                labelSize = 35 ; labelFont = 14 ;
                break;
            case 1 :
                //f = Font.font("SansSerif", FontWeight.BOLD, 20);
                f = Font.font("SansSerif", 20);
                labelSize = 45 ; labelFont = 20 ;
                break ;
            default :
                //f = Font.font("SansSerif", FontWeight.BOLD, 24);
                f = Font.font("SansSerif", 24);
                labelSize = 55 ; labelFont = 24 ;
        }
       
        //Char size in pixels
        final Text text = new Text("1");
        text.setFont(f);
        double w = text.getLayoutBounds().getWidth();
        double h = text.getLayoutBounds().getHeight();
        w = (labelSize - w) / 2 ;
        h = ((labelSize + h) / 2 ) - (h/4) ;
        
        // Grid ?
        int nH = (int) (width / labelSize) - 1 ;
        int nV = (int) (height / labelSize) - 2 ;
        int x, y ;
        //On crée les items
        items = new Group[nbItems] ;
        for (int i=0; i< this.nbItems; i++) {
            items[i] = new Group () ;
            //Sa position
            do {
                x = ThreadLocalRandom.current().nextInt(0, nH - 1 ); ;
                y = ThreadLocalRandom.current().nextInt(0, nV );
            } while (this.contains (labelSize + (x * labelSize), labelSize + (y * labelSize))
                    || s.contains (labelSize + (x * labelSize), labelSize + (y * labelSize))) ;
            items[i].setTranslateX(labelSize + (x * labelSize));
            items[i].setTranslateY(labelSize + (y * labelSize));
            //Rectangle
            Rectangle r1 = new Rectangle(0,0,labelSize, labelSize);
            //r1.setFill(Color.CYAN);
            r1.setOpacity(0);
            items[i].getChildren().add(r1);
            //Texte
            char c = (char)(ThreadLocalRandom.current().nextInt(0, 26 ) + 'A') ;
            Text t1 = new Text(w, h, String.valueOf( c ));
            t1.setFont(f);
            items[i].getChildren().add(t1);
            //ajouté au groupe
            this.getChildren().add(items[i]);
        } 
    }
} //End of class 