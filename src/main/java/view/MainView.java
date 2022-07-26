package view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import logger.Log;
import logger.Logger;
import logger.SingletonLogger;
import logger.Type;

public class MainView {

    private final Logger logger;
    @FXML
    VBox content;
    @FXML
    ScrollPane scrollPane;
    @FXML
    CheckBox errorsButton;
    @FXML
    CheckBox infoButton;
    @FXML
    CheckBox errorsInfoButton;
    @FXML
    CheckBox actionButton;

    public MainView() {
        this.logger = SingletonLogger.getLogger();
    }


    @FXML
    public void initialize() {
        //this.stage = (Stage) this.scrollPane.getScene().getWindow();
        this.content.setFillWidth(true);
        this.logger.observe(l -> Platform.runLater(() -> {
            if (l.getType().equals(Type.ERROR) && this.errorsButton.selectedProperty().get()) {
                this.content.getChildren().add(this.createNode(l));
            } else if (l.getType().equals(Type.INFO) && this.infoButton.selectedProperty().get()) {
                this.content.getChildren().add(this.createNode(l));
            } else if (l.getType().equals(Type.ACTION) && this.actionButton.selectedProperty().get()) {
                this.content.getChildren().add(this.createNode(l));
            } else if (l.getType().equals(Type.ERROR_INFO) && this.errorsInfoButton.selectedProperty().get()) {
                this.content.getChildren().add(this.createNode(l));
            }
        }));
        this.errorsButton.setSelected(false);
        this.infoButton.setSelected(true);
        this.actionButton.setSelected(true);
        this.errorsInfoButton.setSelected(false);
    }


    private Node createNode(final Log l) {
        final HBox box = new HBox();
        final Label tag = new Label("[" + l.getType().getName().toUpperCase() +
                "]: " + l.getTag() + " -> ");
        final Label msg = new Label(l.getMsg());
        tag.setStyle("-fx-font-weight: bold; -fx-text-fill: " +
                l.getType().getColor() + "; -fx-font-size: 15px;");
        msg.setStyle("-fx-font-size: 15px;");
        msg.setWrapText(false);
        tag.setWrapText(false);
        tag.setTextAlignment(TextAlignment.LEFT);
        tag.setMinWidth(this.scrollPane.getWidth() / 3);
        tag.setMaxWidth(this.scrollPane.getWidth() / 3);
        box.setPrefWidth(this.scrollPane.getPrefWidth());
        final Label selectableMsg = this.makeSelectable(msg);
        final Label selectableTag = this.makeSelectable(tag);
        box.getChildren().add(selectableTag);
        box.getChildren().add(selectableMsg);
        box.setSpacing(30);
        return box;
    }

    public void setStageAndSetupListeners(final Stage stage) {
        stage.widthProperty().addListener(event -> {
            //this.content.setPrefWidth(stage.getWidth());
            this.content.getChildren().forEach(x -> {
                final HBox xB = ((HBox) x);
                xB.setPrefWidth(stage.getWidth());
                ((Label) xB.getChildren().get(0)).setMinWidth(this.scrollPane.getWidth() / 3);
                ((Label) xB.getChildren().get(0)).setMaxWidth(this.scrollPane.getWidth() / 3);
            });
        });
    }

    private Label makeSelectable(final Label label) {
        final StackPane textStack = new StackPane();
        final TextField textField = new TextField(label.getText());
        textField.setEditable(false);
        textField.setStyle(label.getStyle() + " -fx-background-color: transparent;" +
                " -fx-background-insets: 0; -fx-background-radius: 0; -fx-padding: 0;");
        final Label invisibleLabel = new Label();
        invisibleLabel.textProperty().bind(label.textProperty());
        invisibleLabel.setVisible(false);
        textStack.getChildren().addAll(invisibleLabel, textField);
        label.textProperty().bindBidirectional(textField.textProperty());
        label.setGraphic(textStack);
        label.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        return label;
    }
}

