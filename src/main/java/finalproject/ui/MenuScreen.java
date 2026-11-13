package finalproject.ui;

import finalproject.app.Screen;
import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/** Shares menu layout, background, and held-fire protection. */
abstract class MenuScreen implements Screen {
    protected final VBox content;
    private final Scene scene;

    protected MenuScreen(String title, String instructions) {
        Label heading = new Label(title);
        heading.setFont(Font.font("sans-serif", 36));
        heading.setTextFill(Color.WHITE);
        Label detail = new Label(instructions);
        detail.setFont(Font.font("sans-serif", 16));
        detail.setTextFill(Color.WHITE);
        detail.setWrapText(true);
        detail.setAlignment(Pos.CENTER);
        content = new VBox(24, heading, detail);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(32));
        ImageView background = new ImageView(GameBackground.IMAGE);
        background.setMouseTransparent(true);
        StackPane layers = new StackPane(background, content);
        background.fitWidthProperty().bind(layers.widthProperty());
        background.fitHeightProperty().bind(layers.heightProperty());
        scene = new Scene(layers, 640, 480);
    }

    protected static Button createButton(String title) {
        Button button = new Button(title);
        button.setFont(Font.font("sans-serif", 16));
        button.setTextFill(Color.WHITE);
        button.setPadding(new Insets(16, 24, 16, 24));
        button.setCursor(Cursor.HAND);
        button.backgroundProperty().bind(Bindings.createObjectBinding(() -> {
            Color color = button.isPressed() ? Color.web("#0b1723")
                    : button.isHover() ? Color.web("#29465e") : Color.web("#152536");
            return new Background(new BackgroundFill(color, new CornerRadii(2), Insets.EMPTY));
        }, button.pressedProperty(), button.hoverProperty()));
        button.borderProperty().bind(Bindings.createObjectBinding(() -> {
            if (!button.isFocused()) return Border.EMPTY;
            return new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID,
                    new CornerRadii(2), new BorderWidths(2), new Insets(-4)));
        }, button.focusedProperty()));
        return button;
    }

    void releaseFireBeforeChoosing(boolean firingHeld) {
        if (!firingHeld) return;
        scene.addEventFilter(KeyEvent.ANY, new EventHandler<>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() != KeyCode.SPACE) return;
                event.consume();
                if (event.getEventType() == KeyEvent.KEY_RELEASED) {
                    scene.removeEventFilter(KeyEvent.ANY, this);
                }
            }
        });
    }

    @Override
    public final Scene getView() {
        return scene;
    }
}
