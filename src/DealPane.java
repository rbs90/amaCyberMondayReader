import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: rbs
 * Date: 27.11.13
 * Time: 10:07
 * To change this template use File | Settings | File Templates.
 */
public class DealPane extends HBox {

    public DealPane(final AmazonElement deal) {

        setStyle("-fx-background-color: lightgray;");

        setMaxWidth(Double.MAX_VALUE);

        Label start = new Label(deal.getStartingTime());
        start.setMinWidth(100);
        start.setMaxHeight(Double.MAX_VALUE);
        start.setAlignment(Pos.CENTER_LEFT);
        start.setStyle("-fx-font-size: 20px; -fx-text-alignment: right; -fx-alignment: center-right;");
        getChildren().add(start);
        getChildren().add(new ImageView(deal.getImg_href()));

        VBox detail = new VBox();

        Label dealName = new Label(deal.getName());
        dealName.setMaxHeight(Double.MAX_VALUE);
        dealName.setAlignment(Pos.CENTER_LEFT);
        dealName.setStyle("-fx-font-size: 16px;");
        detail.getChildren().add(dealName);

        Label dealPrice = new Label(deal.getPrice());
        dealPrice.setMaxHeight(Double.MAX_VALUE);
        dealPrice.setAlignment(Pos.CENTER_RIGHT);
        dealPrice.setStyle("-fx-font-size: 20px; -fx-text-alignment: right;");
        HBox.setHgrow(dealPrice, Priority.ALWAYS);

        detail.getChildren().add(dealPrice);

        if(deal.getPercent_used() != -1) {
            HBox progressBox = new HBox();
            ProgressBar e = new ProgressBar(100);
            e.setProgress(deal.getPercent_used() / 100d);
            progressBox.getChildren().addAll(e, new Label(deal.getPercent_used() + "%"));
            detail.getChildren().add(progressBox);
        }

        detail.setSpacing(20);

        getChildren().add(detail);


        setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    new BrowserLauncher().openURLinBrowser(deal.getHref());
                } catch (BrowserLaunchingInitializingException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (UnsupportedOperatingSystemException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });
    }

}
