package dev.heliosclient.module.settings;

import dev.heliosclient.HeliosClient;
import dev.heliosclient.util.Renderer2D;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class ButtonSetting extends Setting {
    private List<Button> buttons;
    private String ButtonCategoryText;

    public ButtonSetting(String ButtonCategoryText) {
        buttons = new ArrayList<>();
        this.ButtonCategoryText = ButtonCategoryText;
    }

    public void addButton(String buttonText, Runnable task) {
        Button button = new Button(buttonText, task,this.x,this.y);
        buttons.add(button);
        adjustButtonLayout();
    }

    private void adjustButtonLayout() {
        height = (buttons.size() * 18) + 20;
    }

    @Override
    public void render(DrawContext drawContext, int x, int y, int mouseX, int mouseY, TextRenderer textRenderer) {
      //  super.render(drawContext, x, y, mouseX, mouseY, textRenderer);
        drawContext.drawText(textRenderer,ButtonCategoryText, HeliosClient.MC.getWindow().getScaledWidth()/2 - textRenderer.getWidth(ButtonCategoryText)/2 + 1, y + 2,Color.WHITE.getRGB(),true);

        int buttonX = x + 2;
        int buttonY = y + textRenderer.fontHeight + 10;

        for (Button button : buttons) {
            button.render(drawContext, buttonX, buttonY, mouseX, mouseY, textRenderer);
            buttonY += 20;
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        for (Button button1 : buttons) {
           button1.mouseClicked(mouseX,mouseY);
        }
    }

    private class Button {
        private String text;
        private Runnable task;
        private int x, y;

        public Button(String text, Runnable task, int x, int y) {
            this.text = text;
            this.task = task;
            this.x = x;
            this.y = y;
        }

        public void render(DrawContext drawContext, int x, int y, int mouseX, int mouseY, TextRenderer textRenderer) {
            this.x = x;
            this.y = y;
            int fillColor = Color.DARK_GRAY.getRGB();
            int borderColor = hovered(mouseX, mouseY) ? Color.WHITE.getRGB() : Color.BLACK.getRGB();
            Renderer2D.drawRoundedRectangle(drawContext, x, y, width - 4, 18 - 4, 2, fillColor);
            Renderer2D.drawOutlineRoundedBox(drawContext, x, y, width - 4, 18 - 4,2, 0.7f,borderColor);

            // Render button text
            int textX = x + (width - textRenderer.getWidth(text)) / 2;
            int textY = y + 3;
            drawContext.drawText(textRenderer,text,textX,textY,Color.WHITE.getRGB(),false);
        }

        private boolean hovered(int mouseX, int mouseY) {
            return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + 18;
        }
        public boolean mouseClicked(double mouseX, double mouseY){
            if (hovered((int) mouseX, (int) mouseY)){
                task.run();
                return true;
            }
            return false;
        }
    }
}
