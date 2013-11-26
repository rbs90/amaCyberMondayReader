import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Created with IntelliJ IDEA.
 * User: rbs
 * Date: 31.08.13
 * Time: 17:17
 * To change this template use File | Settings | File Templates.
 */
public class GuiTest extends Application{


    @Override
    public void start(Stage stage) throws Exception {

        BorderPane borderPane = new BorderPane();
        stage.setScene(new Scene(borderPane));
        stage.setMinWidth(300);
        stage.setMinHeight(300);
        stage.show();

        stage.getScene().getStylesheets().add(GuiTest.class.getResource("s.css").toExternalForm());


        Label label = new Label("TEST123");
        label.getStyleClass().add("test");
        label.setFont(new Font(30));

        Thread.sleep(1000);
        label.setTextFill(Color.BLUE);


        borderPane.setCenter(label);

    }

    public static void main(String[] args) {
        launch(args);
    }
}
