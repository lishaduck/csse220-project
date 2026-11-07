module finalproject {
    requires javafx.graphics;
    requires javafx.controls;

    // 2. Open and export your package to JavaFX so it can launch GameApp
    exports finalproject;
    exports finalproject.app;

    opens finalproject.app to javafx.graphics;
}