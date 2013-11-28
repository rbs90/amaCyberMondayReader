import com.javafx.experiments.scenicview.ScenicView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: rbs
 * Date: 26.11.13
 * Time: 19:19
 * To change this template use File | Settings | File Templates.
 */
public class Ama extends Application{

    public boolean cyberSeite = false;

    private ArrayList<AmazonElement> deals = new ArrayList();
    private int seite = 1;
    //private static WebEngine webEngine;
    private WebView webView;
    private Stage stage;

    private Date start_time;
    private StackPane stackPane;
    private VBox dealBox = new VBox();
    private Thread websiteAnalyserThread;
    private boolean cancelJob;
    private Button reloadButton;

    @Override
    public void start(final Stage stage) throws Exception {
        webView = new WebView();
        //webEngine = webView.getEngine();

        webView.getEngine().getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> observableValue, Worker.State state, Worker.State state2) {
                if(state2 == Worker.State.SUCCEEDED){
                    if(!cyberSeite){
                        System.out.println("amazon.de fertig geladen");
                        String html = (String) webView.getEngine().executeScript("document.documentElement.innerHTML");
                        Document doc = Jsoup.parse(html);
                        String href = doc.getElementsByAttributeValue("name", "cm13").get(0).children().get(0).attr("href");
                        webView.getEngine().load("http://amazon.de" + href);
                        cyberSeite = true;
                        start_time = new Date();
                    } else {

                        //webView.getEngine().executeScript("if (!document.getElementById('FirebugLite')){E = document['createElement' + 'NS'] && document.documentElement.namespaceURI;E = E ? document['createElement' + 'NS'](E, 'script') : document['createElement']('script');E['setAttribute']('id', 'FirebugLite');E['setAttribute']('src', 'https://getfirebug.com/' + 'firebug-lite.js' + '#startOpened');E['setAttribute']('FirebugLite', '4');(document['getElementsByTagName']('head')[0] || document['getElementsByTagName']('body')[0]).appendChild(E);E = new Image;E['setAttribute']('src', 'https://getfirebug.com/' + '#startOpened');}");
                        websiteAnalyserThread = new Thread() {

                            @Override
                            public void run() {
                                analyseSite();
                            }

                        };
                        websiteAnalyserThread.start();
                    }
                }


            }
        });

        webView.getEngine().load("http://www.amazon.de");
        stage.setMinHeight(500);
        stage.setHeight(800);
        stage.setMinWidth(600);
        stage.setWidth(1000);
        stackPane = new StackPane();
        dealBox.setStyle("-fx-background-color: #010229;");
        dealBox.setSpacing(10);
        stackPane.getChildren().add(webView);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(dealBox);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToHeight(true);
        BorderPane borderPane = new BorderPane();
        borderPane.setMaxWidth(Double.MAX_VALUE);

        stage.setTitle("AmazonCyberDealCollector v1.0 by rbs90.de");

        reloadButton = new Button("Bitte einen Moment Geduld ;) Die Weltherschaft wird vorbereitet...");
        reloadButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                reload();
            }
        });
        reloadButton.setMaxWidth(Double.MAX_VALUE);
        borderPane.setBottom(reloadButton);

        dealBox.setMaxWidth(Double.MAX_VALUE);
        dealBox.setFillWidth(true);
        dealBox.minWidthProperty().bind(scrollPane.widthProperty().subtract(10));

        scrollPane.setMaxWidth(Double.MAX_VALUE);
        borderPane.setCenter(scrollPane);
        stackPane.getChildren().add(borderPane);
        stage.setScene(new Scene(stackPane));
        this.stage = stage;
        this.stage.show();

        //ScenicView.show(stage.getScene());
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
    }

    private void analyseSite() {

        //100_dealView0
        boolean running = true;

        Boolean waiting = true;
        final String[] html = new String[1];
        Document doc = null;
        while(waiting) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    html[0] = (String) webView.getEngine().executeScript("document.documentElement.innerHTML");
                }
            });

            while(html[0] == null) {
                sleep(100);
                System.out.println("waiting1...");
            }

            doc = Jsoup.parse(html[0]);
            if(doc.getElementById("100_dealView0") != null && !doc.getElementById("100_dealView0").hasClass("spinner"))
                waiting = false;
            else {
                System.out.println("waiting2...");
                sleep(100);
            }

        }

        final int currentPage = Integer.parseInt(doc.getElementById("dealCurrentPage").text());
        final int maxPages = Integer.parseInt(doc.getElementById("dealTotalPages").text());

        long ms = new Date().getTime() - start_time.getTime();
        System.out.println("Analysiere Seite " + currentPage + "/" + maxPages + "(nach " + ms + " ms)");

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                reloadButton.setText("Bitte warten, lade neuste Artikel vom Server...(" + currentPage + "/" + maxPages + ")");
            }
        });

        int dealNumber = 0;
        while(doc.getElementById("100_dealView" + dealNumber) != null){
            //System.out.println("found " + dealNumber);
            final AmazonElement amazonElement = new AmazonElement();
            org.jsoup.nodes.Element deal = doc.getElementById("100_dealView" + dealNumber);
            if(deal.getElementById("dealImage") != null){
                amazonElement.setImg_href(deal.getElementById("dealImage").attr("src"));
                amazonElement.setHref(deal.getElementById("dealImage").parent().attr("href"));
            } else {
                System.out.println("image == null for element " + dealNumber);
                dealNumber++;
                continue;
            }


            if(deal.getElementById("dealDealPrice") != null){
                amazonElement.setPrice(deal.getElementById("dealDealPrice").child(0).text());
                amazonElement.setName(deal.getElementById("dealTitleLink").text());
                amazonElement.setStartingTime("Jetzt");
                amazonElement.setPercent_used(Integer.parseInt(deal.getElementById("dealPercentClaimed").text().split("%")[0]));
            } else {
                amazonElement.setPrice("???,?? €");
                if(deal.getElementById("dealTeaser") != null)
                    amazonElement.setName(deal.getElementById("dealTeaser").text());
                else
                    amazonElement.setName("???");

                if(deal.getElementsByClass("ldupcomingtxt").size() > 0)
                    amazonElement.setStartingTime(deal.getElementsByClass("ldupcomingtxt").get(0).getElementsByTag("b").get(0).text());
                else
                    amazonElement.setStartingTime("??:??");
            }
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    dealBox.getChildren().add(new DealPane(amazonElement));
                }
            });
            deals.add(amazonElement);
            dealNumber++;
        }

        if(currentPage == maxPages || cancelJob)
            showResult();
        else {
            final boolean[] executed = {false};
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    webView.getEngine().executeScript("jQuery( \"#rightShovelBg\" ).trigger( \"click\" );");
                    executed[0] = true;
                }
            });

            while(!executed[0]) sleep(20);
            analyseSite();
        }
    }

    private void showResult() {
        stage.setMinWidth(500);
        stage.setWidth(900);
        cancelJob = false; //finished cancel
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                reloadButton.setText(dealBox.getChildren().size() + " Artikel geladen. Aktualisieren?");
            }
        });
        /*
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                dealBox = new VBox();
                ScrollPane scrollPane = new ScrollPane();
                scrollPane.setContent(dealBox);
                scrollPane.setFitToHeight(true);
                scrollPane.setFitToHeight(true);
                BorderPane borderPane = new BorderPane();
                borderPane.setCenter(scrollPane);
                stackPane.
                //ScenicView.show(stage.getScene());
                for(AmazonElement deal : deals){
                    dealBox.getChildren().add(new DealPane(deal));
                }

                dealBox.setSpacing(10);
                dealBox.setPadding(new Insets(10, 10, 10, 10));
                dealBox.setStyle("-fx-background: linear-gradient(to bottom right, #010229, black);");
            }


        }); */
    }

    public static void main(String[] args) {
        launch(args);
    }


    private void sleep(int ms){
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void reload(){

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                reloadButton.setText("Warte auf alten Job...");
            }
        });

        new Thread(){
            @Override
            public void run() {
                if(websiteAnalyserThread.isAlive()){
                    cancelJob = true;
                    while(cancelJob) try {
                        sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }


                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        dealBox.getChildren().clear();
                        cancelJob = false;
                        cyberSeite = false;
                        webView.getEngine().load("http://www.amazon.de");

                        reloadButton.setText("Bitte warten, lade neuste Artikel vom Server...");
                    }
                });
            }
        }.start();
    }

}
