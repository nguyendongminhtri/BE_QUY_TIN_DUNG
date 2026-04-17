package com.example.demo.config;

public class TextStyle {
    private boolean bold;
    private boolean italic;

    public TextStyle(boolean bold, boolean italic) {
        this.bold = bold;
        this.italic = italic;
    }

    public boolean isBold() {
        return bold;
    }

    public boolean isItalic() {
        return italic;
    }
}
