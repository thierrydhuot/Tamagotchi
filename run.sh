#!/bin/bash

# JavaFX path - pointing to your actual JavaFX location
JAVAFX_PATH="/Users/hamzakhamissa/Downloads/javafx-sdk-23.0.2"

# Check if JavaFX path exists
if [ ! -d "$JAVAFX_PATH" ]; then
    echo "Error: JavaFX SDK not found at $JAVAFX_PATH"
    echo "Please download JavaFX SDK from https://gluonhq.com/products/javafx/"
    echo "Extract it and update JAVAFX_PATH in this script"
    exit 1
fi

# Create bin directory if it doesn't exist
mkdir -p bin

# Compile
echo "Compiling..."
javac --module-path "$JAVAFX_PATH/lib" \
      --add-modules javafx.controls,javafx.graphics,javafx.base,javafx.fxml \
      -d bin \
      Main.java Pet.java Player.java GameState.java

if [ $? -eq 0 ]; then
    # Run using system Java with additional flags for macOS
    echo "Running..."
    java --module-path "$JAVAFX_PATH/lib" \
         --add-modules javafx.controls,javafx.graphics,javafx.base,javafx.fxml \
         --add-opens javafx.graphics/com.sun.javafx.sg.prism=ALL-UNNAMED \
         --add-opens javafx.graphics/com.sun.javafx.tk=ALL-UNNAMED \
         --add-opens javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED \
         -Dprism.order=sw \
         -Dprism.verbose=true \
         -Djava.library.path="$JAVAFX_PATH/lib" \
         -cp bin \
         Main
else
    echo "Compilation failed!"
fi 