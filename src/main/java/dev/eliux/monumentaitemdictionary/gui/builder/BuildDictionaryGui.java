package dev.eliux.monumentaitemdictionary.gui.builder;

import dev.eliux.monumentaitemdictionary.gui.DictionaryController;
import dev.eliux.monumentaitemdictionary.gui.charm.DictionaryCharm;
import dev.eliux.monumentaitemdictionary.gui.item.DictionaryItem;
import dev.eliux.monumentaitemdictionary.gui.widgets.BuildButtonWidget;
import dev.eliux.monumentaitemdictionary.gui.widgets.ItemIconButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.*;

public class BuildDictionaryGui extends Screen {
    private ItemIconButtonWidget addBuildButton;
    private ItemIconButtonWidget showItemsButton;
    private ItemIconButtonWidget showCharmsButton;
    public final int sideMenuWidth = 40;
    public final int labelMenuHeight = 30;
    public final int itemPadding = 10;
    public final int itemSize = 50;
    public final DictionaryController controller;
    public ArrayList<DictionaryBuild> buildsList;
    public HashMap<DictionaryBuild, BuildButtonWidget> buildsButtons = new HashMap<>();
    public BuildDictionaryGui(Text title, DictionaryController controller) {
        super(title);
        this.controller = controller;
    }

    public void postInit() {
        buildsList = new ArrayList<>();

        addBuildButton = new ItemIconButtonWidget(5, 5, 20, 20, Text.literal(""), (button) -> {
            controller.setBuilderScreen();
        }, Text.literal("Add Build"), "paper", "");

        showItemsButton = new ItemIconButtonWidget(width - sideMenuWidth + 10, labelMenuHeight + 10, 20, 20, Text.literal(""), (button) -> {
            controller.setItemDictionaryScreen();
        }, Text.literal("Item Data").setStyle(Style.EMPTY.withColor(0xFF00FFFF)), "iron_chestplate", "");

        showCharmsButton = new ItemIconButtonWidget(width - sideMenuWidth + 10, labelMenuHeight + 30, 20, 20, Text.literal(""), (button) -> {
            controller.setCharmDictionaryScreen();
        }, Text.literal("Charm Data").setStyle(Style.EMPTY.withColor(0xFFFFFF00)), "glowstone_dust", "");
    }

    public void buildBuildsList() {
        for (DictionaryBuild build : buildsList) {
            int index = buildsList.indexOf(build);
            int row = index / ((width - sideMenuWidth - 5) / (itemSize + itemPadding));
            int col = index % ((width - sideMenuWidth - 5) / (itemSize + itemPadding));

            int x = (col + 1) * itemPadding + col * itemSize;
            int y = labelMenuHeight + (row + 1) * itemPadding + row * itemSize;

            BuildButtonWidget button = new BuildButtonWidget(x, y, itemSize, Text.literal(build.name), (b) -> {
                controller.builderGui.loadItems(build);
                controller.setBuilderScreen();
            }, build, this, () -> getBuildDescription(build));

            buildsButtons.put(build, button);
        }
    }

    private List<Text> getBuildDescription(DictionaryBuild build) {
        List<Text> lines = new ArrayList<>();

        for (DictionaryItem item : build.allItems) {
            lines.add(Text.literal(item.name));
        }

        return lines;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);

        matrices.push();
        matrices.translate(0, 0, 110);
        fill(matrices, 0, 0, width, labelMenuHeight, 0xFF555555);
        drawHorizontalLine(matrices, 0, width, labelMenuHeight, 0xFFFFFFFF);
        drawCenteredTextWithShadow(matrices, textRenderer, Text.literal("Build Dictionary").setStyle(Style.EMPTY.withBold(true)), width / 2, (labelMenuHeight - textRenderer.fontHeight) / 2, 0xFF2ca9d3);
        matrices.pop();
        drawVerticalLine(matrices, width - sideMenuWidth - 1, labelMenuHeight, height, 0x77AAAAAA); // called twice to make the scroll bar render wider (janky, but I don't really care)
        drawVerticalLine(matrices, width - sideMenuWidth - 2, labelMenuHeight, height, 0x77AAAAAA);

        buildsButtons.forEach((build, button) -> {
            button.renderButton(matrices, mouseX, mouseY, delta);
        });

        if (buildsButtons.isEmpty()) {
            drawCenteredTextWithShadow(matrices, textRenderer, "Found No Builds", width / 2, labelMenuHeight + 10, 0xFF2222);
        }

        matrices.push();
        matrices.translate(0, 0, 110);
        addBuildButton.render(matrices, mouseX, mouseY, delta);
        showCharmsButton.render(matrices, mouseX, mouseY, delta);
        showItemsButton.render(matrices, mouseX, mouseY, delta);
        matrices.pop();

        try {
            super.render(matrices, mouseX, mouseY, delta);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        buildsButtons.forEach((build, b) -> {
            b.mouseClicked(mouseX, mouseY, button);
        });

        addBuildButton.mouseClicked(mouseX, mouseY, button);
        showCharmsButton.mouseClicked(mouseX, mouseY, button);
        showItemsButton.mouseClicked(mouseX, mouseY, button);

        return true;
    }

    public void updateGuiPositions() {
    }

    public void addBuild(String name, List<DictionaryItem> items, List<DictionaryCharm> charms) {
        if (name.isEmpty()) name = "No Name";
        DictionaryBuild build = new DictionaryBuild(name, items, charms);
        buildsList.add(build);
        buildBuildsList();
    }
}
