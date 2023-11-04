package dev.heliosclient.ui.clickgui;

import dev.heliosclient.HeliosClient;
import dev.heliosclient.managers.ColorManager;
import dev.heliosclient.managers.ModuleManager;
import dev.heliosclient.module.Categories;
import dev.heliosclient.module.Category;
import dev.heliosclient.module.Module_;
import dev.heliosclient.module.settings.Setting;
import dev.heliosclient.module.settings.SettingGroup;
import dev.heliosclient.module.sysmodules.ClickGUI;
import dev.heliosclient.util.ColorUtils;
import dev.heliosclient.util.FileUtils;
import dev.heliosclient.util.Renderer2D;
import me.x150.renderer.render.SVGFile;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryPane {
    public Category category;
    public int x;
    public int y;
    public int height;
    public static int width = 83;
    public boolean collapsed = false;
    int startX, startY;
    boolean dragging = false;
    ArrayList<ModuleButton> moduleButtons;
    private final Screen parentScreen;
    public static int MAX_HEIGHT = 150;
    private int scrollOffset = 0;
    public int iconWidth = 10;
    public int iconHeight = 10;
    private SVGFile svgFile;
    float delay = 0, delay2 = 0;
    private final float delayBetweenButtons = 0.2f;
    private final float delayBetweenSettings = 0.2f;


    public CategoryPane(Category category, int initialX, int initialY, boolean collapsed, Screen parentScreen) {
        this.category = category;
        this.x = initialX;
        this.y = initialY;
        this.collapsed = collapsed;
        this.parentScreen = parentScreen;
        moduleButtons = new ArrayList<ModuleButton>();
        for (Module_ m : ModuleManager.INSTANCE.getModulesByCategory(category)) {
            moduleButtons.add(new ModuleButton(m, parentScreen));
        }
        if (moduleButtons.size() == 0) collapsed = true;
        height = Math.round((moduleButtons.size() * (5 + Renderer2D.getStringHeight())));

        if (FileUtils.doesFileInPathExist(category.iconSrc)) {
            svgFile = new SVGFile(category.iconSrc, iconWidth, iconHeight);
        } else {
            HeliosClient.LOGGER.info("SVG File for " + category.name + "does not exist");
        }
    }

    public void addModule(List<Module_> moduleS) {
        for (Module_ module : moduleS) {
            boolean exists = false;
            for (ModuleButton button : moduleButtons) {
                if (button.module == module) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                ModuleButton moduleButton = new ModuleButton(module, parentScreen);
                moduleButtons.add(moduleButton);
            }
        }
    }

    public void removeModule(Module_ module) {
        moduleButtons.removeIf(button -> button.module == module);
    }

    public void keepOnlyModule(Module_ module) {
        moduleButtons.removeIf(button -> button.module != module);
    }

    public void removeModules() {
        moduleButtons.clear();
    }

    public void update() {
        float prevbuttonY = y;
        for (ModuleButton button : moduleButtons) {
            button.update(prevbuttonY);
            if (!button.isAnimationDone()) {
                if (delay <= 0) {
                    delay = delayBetweenButtons;
                }
                delay -= button.animationSpeed;
            }
            prevbuttonY = button.y;
        }
    }

    public void updateSetting() {
        for (ModuleButton button : moduleButtons) {
            for (SettingGroup settingBuilder : button.module.quickSettingGroups) {
                if (!settingBuilder.shouldRender()) continue;
                for (Setting setting : settingBuilder.getSettings()) {
                    if (!setting.shouldRender()) continue;
                    setting.update(button.getY());
                    if (!setting.isAnimationDone()) {
                        if (delay2 <= 0) {
                            delay2 = delayBetweenSettings;
                        }
                        delay2 -= setting.animationSpeed;
                    }
                }
            }
        }
    }

    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta, TextRenderer textRenderer) {
        int categoryNameHeight = (int) Renderer2D.getFxStringHeight(category.name);

        int maxWidth = 0;
        int maxHeight = 4;
        for (ModuleButton m : moduleButtons) {
            maxWidth = Math.max(maxWidth, m.width);
            maxHeight += categoryNameHeight + 10;
        }
        if (maxWidth < getWidth() - 3) {
            maxWidth = getWidth() - 3;
        }

        height = maxHeight;

        if (ClickGUI.ScrollTypes.values()[ClickGUI.ScrollType.value] == ClickGUI.ScrollTypes.OLD) {
            MAX_HEIGHT = height;
        } else {
            MAX_HEIGHT = (int) Math.round(ClickGUI.CategoryHeight.value);
            if (MAX_HEIGHT > height) {
                MAX_HEIGHT = height;
            }
        }
        if (!collapsed && height >= 10) {
            if (category == Categories.SEARCH) {
                Renderer2D.drawRoundedRectangle(drawContext.getMatrices().peek().getPositionMatrix(), x - 2, y + categoryNameHeight + 25, false, false, true, true, width + 4.5f, MAX_HEIGHT, 3, ColorUtils.changeAlpha(new Color(ColorManager.INSTANCE.ClickGuiPrimary()), 100).getRGB());
            } else {
                Renderer2D.drawRoundedRectangle(drawContext.getMatrices().peek().getPositionMatrix(), x - 2, y + categoryNameHeight + 6, false, false, true, true, width + 4.5f, MAX_HEIGHT, 3, ColorUtils.changeAlpha(new Color(ColorManager.INSTANCE.ClickGuiPrimary()), 100).getRGB());
            }
        }

        if (dragging) {
            x = mouseX - startX;
            y = mouseY - startY;
        }
        if (!collapsed) {
            update();

            int buttonYOffset = y + 10 + categoryNameHeight - scrollOffset;
            if (category == Categories.SEARCH) {
                buttonYOffset = y + 27 + categoryNameHeight - scrollOffset;
            }
            int boxHeight = 0;


            for (ModuleButton m : moduleButtons) {
                if (m.hasFaded()) {
                    m.startFading();
                }

                m.setFaded(false);
                if (buttonYOffset >= y + categoryNameHeight && buttonYOffset < y + MAX_HEIGHT) {
                    // Update the y position of the button based on its animation progress
                    int animatedY = Math.round(m.getY() + (buttonYOffset - m.getY()) * m.getAnimationProgress());
                    m.render(drawContext, mouseX, mouseY, x, animatedY, maxWidth);
                }

                buttonYOffset += categoryNameHeight + 10;
                // Draw the settings for this module if they are open
                if (m.settingsOpen) {
                    updateSetting();
                    for (SettingGroup settingBuilder : m.module.quickSettingGroups) {
                        buttonYOffset += Math.round(settingBuilder.getGroupNameHeight() + 3);
                        boxHeight += Math.round(settingBuilder.getGroupNameHeight() + 3);
                        settingBuilder.renderBuilder(drawContext, x - 1, buttonYOffset, width);

                        if (!settingBuilder.shouldRender()) continue;

                        for (Setting setting : settingBuilder.getSettings()) {
                            if (!setting.shouldRender()) continue;

                            setting.quickSettings = m.settingsOpen;
                            // Update the y position of the setting based on its animation progress
                            int animatedY = Math.round(setting.getY() + (buttonYOffset - setting.getY()) * setting.getAnimationProgress());
                            setting.renderCompact(drawContext, x, animatedY + 3, mouseX, mouseY, textRenderer);

                            buttonYOffset += setting.heightCompact;
                            boxHeight += setting.heightCompact;
                        }
                        buttonYOffset += Math.round(settingBuilder.getGroupNameHeight());
                        boxHeight += Math.round(settingBuilder.getGroupNameHeight());

                    }
                    if (!m.module.quickSettingGroups.isEmpty()) {
                        buttonYOffset += 2;
                    }
                    m.setBoxHeight(boxHeight);
                } else {
                    for (SettingGroup settingBuilder : m.module.quickSettingGroups) {
                        for (Setting setting : settingBuilder.getSettings()) {
                            setting.animationDone = false;
                            delay2 = 0;
                            setting.setAnimationProgress(0.0f);
                        }
                    }
                }
            }
        }
        if (collapsed) {
            for (ModuleButton m : moduleButtons) {
                m.setFaded(true);
                m.animationDone = false;
                delay = 0;
                m.setAnimationProgress(0.0f);
            }
        }
        Renderer2D.drawRoundedRectangle(drawContext.getMatrices().peek().getPositionMatrix(), x - 2, y, width + 4.5f, categoryNameHeight + 8, 3, ColorUtils.changeAlpha(new Color(ColorManager.INSTANCE.ClickGuiPrimary()), 255).getRGB());

        Renderer2D.drawFixedString(drawContext.getMatrices(), category.name, x + (float) (CategoryPane.getWidth() - 4) / 2 - Renderer2D.getFxStringWidth(category.name) / 2, (float) (y + 4), ColorManager.INSTANCE.clickGuiPaneText());
        if (svgFile != null) {
            // Gives a very shitty error idk why. Not fixable
            //svgFile.render(drawContext.getMatrices(), x, y,iconWidth,iconHeight);
        }
    }

    public boolean hovered(double mouseX, double mouseY) {
        int categoryNameHeight = (int) Renderer2D.getFxStringHeight(category.name);
        return mouseX > x + 2 && mouseX < x + (width - 2) && mouseY > y + 2 && mouseY < y + categoryNameHeight + 14;
    }

    public boolean hoveredOverModules(double mouseX, double mouseY) {
        int categoryNameHeight = (int) Renderer2D.getFxStringHeight(category.name);
        return mouseX > x + 2 && mouseX < x + (width - 2) && mouseY > y + categoryNameHeight + 14 && mouseY < y + height;
    }


    public void mouseClicked(int mouseX, int mouseY, int button) {
        for (ModuleButton moduleButton : moduleButtons) {
            if (moduleButton.mouseClicked(mouseX, mouseY, button, collapsed)) return;
            if (moduleButton.settingsOpen) {
                for (SettingGroup settingGroup : moduleButton.module.quickSettingGroups) {
                    settingGroup.mouseClickedBuilder(mouseX, mouseY);
                    if (!settingGroup.shouldRender()) continue;
                    settingGroup.mouseClicked(mouseX, mouseY, button);
                }
            }
        }
        if (hovered(mouseX, mouseY) && button == 1) collapsed = !collapsed;
        else if (hovered(mouseX, mouseY) && button == 0) {
            startX = mouseX - x;
            startY = mouseY - y;
            dragging = true;
        }
        if (button == 2) {
            startX = mouseX - x;
            startY = mouseY - y;
            dragging = true;
        }
    }

    public void mouseScrolled(int mouseX, int mouseY, double amount) {
        if (hoveredOverModules(mouseX, mouseY) && ClickGUI.ScrollTypes.values()[ClickGUI.ScrollType.value] == ClickGUI.ScrollTypes.NEW) {
            int categoryNameHeight = (int) Renderer2D.getFxStringHeight(category.name);
            // Scroll this pane by changing the scroll offset
            scrollOffset += (int) (amount * ClickGUI.ScrollSpeed.value);

            // Clamp the scroll offset to prevent scrolling past the start or end of the modules
            int maxScroll = Math.max(0, moduleButtons.size() * (categoryNameHeight + 10));
            scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset));
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int button) {
        for (ModuleButton moduleButton : moduleButtons) {
            if (moduleButton.settingsOpen) {
                for (SettingGroup settingGroup : moduleButton.module.quickSettingGroups) {
                    if (!settingGroup.shouldRender()) continue;
                    settingGroup.mouseReleased(mouseX, mouseY, button);
                }
            }
        }
        dragging = false;
    }

    public void charTyped(char chr, int modifiers) {
        for (ModuleButton moduleButton : moduleButtons) {
            if (moduleButton.settingsOpen) {
                for (SettingGroup settingGroup : moduleButton.module.quickSettingGroups) {
                    if (!settingGroup.shouldRender()) continue;
                    settingGroup.charTyped(chr, modifiers);
                }
            }
        }
    }

    public static int getWidth() {
        return width;
    }
}
