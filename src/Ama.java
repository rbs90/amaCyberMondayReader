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
import org.jsoup.select.Elements;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private BufferedWriter fileWriter;

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        webView = new WebView();
        //webEngine = webView.getEngine();

        if(getParameters().getRaw().size() != 1){
            System.out.println("Error. Usage: cybermonday.jar <output html file>");
            System.exit(-1);
        }

        start_time = new Date();

        webView.getEngine().getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> observableValue, Worker.State state, Worker.State state2) {
                if (state2 == Worker.State.SUCCEEDED) {
                    //webView.getEngine().executeScript("if (!document.getElementById('FirebugLite')){E = document['createElement' + 'NS'] && document.documentElement.namespaceURI;E = E ? document['createElement' + 'NS'](E, 'script') : document['createElement']('script');E['setAttribute']('id', 'FirebugLite');E['setAttribute']('src', 'https://getfirebug.com/' + 'firebug-lite.js' + '#startOpened');E['setAttribute']('FirebugLite', '4');(document['getElementsByTagName']('head')[0] || document['getElementsByTagName']('body')[0]).appendChild(E);E = new Image;E['setAttribute']('src', 'https://getfirebug.com/' + '#startOpened');}");
                    websiteAnalyserThread = new Thread() {

                        @Override
                        public void run() {

                            File outputFile = new File(getParameters().getRaw().get(0));
                            if(outputFile.exists())
                                outputFile.delete();
                            try {
                                fileWriter = new BufferedWriter(new FileWriter(outputFile));
                                fileWriter.write("<html><head><link rel='stylesheet' href='style.css'>" +
                                        "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js\"></script><script src=\"script.js\"></script></head><body>" +
                                        "<h1>Amazon Cyber Monday &Uuml;bersicht</h1><p>Stand: " + new Date().toString() + "</p><input id=\"searchbar\" placeholder=\"Suchen...\" type=\"search\"></input><br>");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            boolean res = false;
                            while (!res) {
                                res = analyseSite();
                            }

                            try {
                                fileWriter.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            System.exit(0);
                        }
                    };
                    websiteAnalyserThread.start();

                }


            }
        });

        webView.getEngine().load("http://www.amazon.de/gp/angebote/");
        stage.setMinHeight(500);
        stage.setHeight(800);
        stage.setMinWidth(600);
        stage.setWidth(1000);
        stackPane = new StackPane();
        /*
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
         */
    }

    private boolean analyseSite() {

        //100_dealView0
        boolean running = true;

        Boolean waiting = true;
        final String[] html = new String[1];
        Document doc = null;

        int wait_count = 0;
        while (waiting) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    html[0] = (String) webView.getEngine().executeScript("document.documentElement.innerHTML");
                }
            });

            while (html[0] == null) {
                sleep(100);
                wait_count++;
                //System.out.println("waiting1...");
            }

            doc = Jsoup.parse(html[0]);

            if (doc.getElementsByTag("init").size() > 0) {
                doc = Jsoup.parse(doc.getElementsByTag("init").get(0).html());
                if (doc.getElementById("101_dealView0") != null && !doc.getElementById("101_dealView0").hasClass("spinner"))
                    waiting = false;
            } else {
                //System.out.println("waiting2...");
                sleep(100);
                wait_count++;
            }

            if(wait_count > 20){
                System.out.println("RELOADING!!!");
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        webView.getEngine().reload();

                    }
                });
                sleep(1000);
                wait_count = 0;
            }
        }

        final int currentPage = Integer.parseInt(doc.getElementById("dealCurrentPage").text());
        final int maxPages = Integer.parseInt(doc.getElementById("dealTotalPages").text());

        System.out.println("Analysiere Seite " + currentPage + "/" + maxPages);

        int dealNumber = 0;
        while (doc.getElementById("101_dealView" + dealNumber) != null) {
            //System.out.println("found " + dealNumber);
            final AmazonElement amazonElement = new AmazonElement();
            org.jsoup.nodes.Element deal = doc.getElementById("101_dealView" + dealNumber);
            if (deal.getElementById("dealImage") != null) {
                amazonElement.setImg_href(deal.getElementById("dealImage").attr("src"));
                amazonElement.setHref(deal.getElementById("dealImage").parent().attr("href"));
            } else {
                System.out.println("image == null for element " + dealNumber);
                dealNumber++;
                continue;
            }


            if (deal.getElementById("dealDealPrice") != null) {
                amazonElement.setPrice(deal.getElementById("dealDealPrice").child(0).text());
                amazonElement.setName(deal.getElementById("dealTitle").text());
                amazonElement.setStartingTime("Jetzt");
                amazonElement.setPercent_used(Integer.parseInt(deal.getElementById("dealPercentClaimed").text().split("%")[0]));
                //amazonElement.setPercent_used(0);
            } else {
                amazonElement.setPrice("???,?? €");
                if (deal.getElementById("dealTeaser") != null)
                    amazonElement.setName(deal.getElementById("dealTeaser").text());
                else
                    amazonElement.setName("???");

                if (deal.getElementById("timerContent") != null) {
                    Elements tickerElems = deal.getElementById("timerContent").getElementsByClass("ticker");
                    if (tickerElems.size() != 0) {
                        int hours = Integer.parseInt(tickerElems.get(0).text());
                        int mins = Integer.parseInt(tickerElems.get(1).text());
                        LocalDateTime time = LocalDateTime.now().plusHours(hours).plusMinutes(mins);
                        int minute = (int) Math.ceil(time.getMinute() / 5.0) * 5;
                        System.out.println(minute);
                        if (minute == 60) {
                            time = time.withHour(time.getHour() + 1 % 24).withMinute(0);
                        } else
                            time = time.withMinute(minute);
                        String datetimeString = time.format(DateTimeFormatter.ofPattern("HH:mm"));
                        amazonElement.setStartingTime(datetimeString);
                    } else {
                        amazonElement.setStartingTime(deal.getElementById("timerContent").getElementsByTag("span").get(1).text());
                    }
                } else
                    amazonElement.setStartingTime("??:??");
            }
            String htmlString = amazonElement.generateHTML();
            try {
                fileWriter.write(htmlString);
            } catch (IOException e) {
                e.printStackTrace();
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

        if (currentPage == maxPages || cancelJob) {
            //showResult();
            return true;
        } else {
            final boolean[] executed = {false};
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    webView.getEngine().executeScript("jQuery( \"init #rightShovelBg\" ).first().trigger( \"click\" );");
                    executed[0] = true;
                }
            });

            while(!executed[0]) sleep(20);
            return false;
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
