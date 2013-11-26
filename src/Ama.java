import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: rbs
 * Date: 26.11.13
 * Time: 19:19
 * To change this template use File | Settings | File Templates.
 */
public class Ama extends Application{

    public boolean cyberSeite = false;

    public ArrayList<Element> deals = new ArrayList();
    public int seite = 1;

    @Override
    public void start(final Stage stage) throws Exception {
        final WebView webView = new WebView();
        final WebEngine engine = webView.getEngine();

        engine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> observableValue, Worker.State state, Worker.State state2) {
                if(state2 == Worker.State.SUCCEEDED){
                    if(!cyberSeite){
                        System.out.println("Fertsch");
                        String href = engine.getDocument().getElementById("nav-swm-holiday-map").getElementsByTagName("area").item(0).getAttributes().getNamedItem("href").getTextContent();
                        engine.load("http://amazon.de" + href);
                        cyberSeite = true;
                    } else {


                        while(true){
                            System.out.println("Analysiere Seite " + seite);
                            seite ++;
                            boolean waiting = true;
                            int wait = 0;
                            while(waiting) {
                                if(engine.getDocument().getElementById("100_dealView0") != null)
                                    waiting = false;
                                else {
                                    wait ++;
                                    System.out.println("Waiting for amazon.de..." + wait);
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                    }
                                }
                            }

                            //100_dealView0
                            boolean running = true;
                            int dealNumber = 0;
                            while(running){
                                Element deal = engine.getDocument().getElementById("100_dealView" + dealNumber);

                                if(deal != null){

                                    while(deal.getElementsByTagName("div").getLength() < 6){
                                        try {
                                            Thread.sleep(1000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                        }
                                        System.out.println("Wait for site load...");
                                    }

                                    deals.add(deal);
                                    dealNumber++;
                                    System.out.println("Angebot " + dealNumber + " gefunden");
                                    System.out.println(deal.getElementsByTagName("div").item(5).getTextContent().trim());
                                } else {
                                    running = false;
                                }
                            }
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            }
                            engine.executeScript("$( \"#rightShovelBg\" ).trigger( \"click\" );");

                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            }
                        }
                    }
                }


            }
        });

        engine.load("http://www.amazon.de");
        stage.setMinHeight(700);
        stage.setMinWidth(1000);
        stage.setScene(new Scene(webView));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
