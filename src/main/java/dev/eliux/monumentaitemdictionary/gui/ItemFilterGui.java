package dev.eliux.monumentaitemdictionary.gui;

import dev.eliux.monumentaitemdictionary.gui.widgets.DropdownWidget;
import dev.eliux.monumentaitemdictionary.gui.widgets.ItemIconButtonWidget;
import dev.eliux.monumentaitemdictionary.util.ItemFilter;
import dev.eliux.monumentaitemdictionary.util.ItemFormatter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;

public class ItemFilterGui extends Screen {
    private final int labelMenuHeight = 30;

    private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

    private ItemIconButtonWidget backButton;
    private ButtonWidget addFilterButton;
    private ArrayList<DropdownWidget> filterListOption;
    private ArrayList<DropdownWidget> filterListValue;
    private ArrayList<ButtonWidget> filterListComparator;
    private ArrayList<TextFieldWidget> filterListConstant;
    private ArrayList<ItemIconButtonWidget> filterListDelete;
    //private ArrayList<ItemIconButtonWidget> filterListDuplicate;

    private ArrayList<ItemFilter> itemFilters = new ArrayList<>();

    private int removeIndex = -1;

    public final DictionaryController controller;

    public ItemFilterGui(Text title, DictionaryController controller) {
        super(title);
        this.controller = controller;

        filterListOption = new ArrayList<>();
        filterListValue = new ArrayList<>();
        filterListComparator = new ArrayList<>();
        filterListConstant = new ArrayList<>();
        filterListDelete = new ArrayList<>();
        //filterListDuplicate = new ArrayList<>();
    }

    public void postInit() {
        backButton = new ItemIconButtonWidget(5, 5, 20, 20, new LiteralText(""), (button) -> {
            controller.setDictionaryScreen();
        }, (button, matrices, mouseX, mouseY) -> {
            renderTooltip(matrices, new LiteralText("Go Back"), mouseX, mouseY);
        }, "arrow", "");

        addFilterButton = new ButtonWidget(5, labelMenuHeight + 5, 80, 20, new LiteralText("Add New Filter"), (button) -> {
            int index = filterListOption.size();

            ItemFilter filter = new ItemFilter();
            itemFilters.add(filter);

            ButtonWidget comparator = new ButtonWidget(280, labelMenuHeight + 5 + index * 25, 60, 20, new LiteralText("Exists"), b -> {
                filter.incrementComparator();
                b.setMessage(new LiteralText(
                        switch (filter.comparator) {
                            case 0: yield "Exists";
                            case 1: yield "Not Exists";
                            case 2: yield ">=";
                            case 3: yield ">";
                            case 4: yield "=";
                            case 5: yield "<=";
                            case 6: yield "<";
                            default: yield "Error";
                        }
                    ));

                updateFilterOutput();
            }, (button1, matrices, mouseX, mouseY) -> {
                renderTooltip(matrices, new LiteralText("Click to cycle"), mouseX, mouseY);
            });
            DropdownWidget value = new DropdownWidget(textRenderer, 125, labelMenuHeight + 7 + index * 25, 150, new LiteralText(""), "", Arrays.asList(), (v) -> {
                filter.value = v;
                updateFilterOutput();
            });
            TextFieldWidget constant = new TextFieldWidget(textRenderer, 345, labelMenuHeight + 8 + index * 25, 30, 14, new LiteralText(""));
            constant.setText("0.0");
            constant.setChangedListener(c -> {
                try {
                    filter.constant = Double.parseDouble(c);
                } catch (Exception e) {
                    // error handling is stupid
                }
                updateFilterOutput();
            });
            DropdownWidget options = new DropdownWidget(textRenderer, 30, labelMenuHeight + 7 + index * 25, 90, new LiteralText(""), "Select Sort Type", Arrays.asList("Tier", "Region", "Location", "Type", "Stat"), (v) -> {
                filter.option = v;

                switch (v) {
                    case "Tier" -> {
                        value.setChoices(controller.getAllTiers());
                        value.setDefaultText("Select Tier");
                        comparator.setMessage(new LiteralText("Exists"));
                    }
                    case "Region" -> {
                        value.setChoices(controller.getAllRegions());
                        value.setDefaultText("Select Region");
                        comparator.setMessage(new LiteralText("Exists"));
                    }
                    case "Location" -> {
                        value.setChoices(controller.getAllLocations());
                        value.setDefaultText("Select Location");
                        comparator.setMessage(new LiteralText("Exists"));
                    }
                    case "Type" -> {
                        value.setChoices(controller.getAllTypes());
                        value.setDefaultText("Select Type");
                        comparator.setMessage(new LiteralText("Exists"));
                    }
                    case "Stat" -> {
                        ArrayList<String> vc = new ArrayList<>();
                        for (String s : controller.getAllStats()) vc.add(ItemFormatter.formatStat(s));
                        value.setChoices(controller.getAllStats(), vc);
                        value.setDefaultText("Select Stat");
                        comparator.setMessage(new LiteralText("Exists"));
                    }
                }

                updateFilterOutput();
            });
            ItemIconButtonWidget delete = new ItemIconButtonWidget(5, labelMenuHeight + 5 + index * 25, 20, 20, new LiteralText(""), b -> {
                removeIndex = filterListOption.indexOf(options);
            }, ((button1, matrices, mouseX, mouseY) -> {
                renderTooltip(matrices, new LiteralText("Delete").setStyle(Style.EMPTY.withColor(0xFF0000)), mouseX, mouseY);
            }), "orange_stained_glass_pane", "Cancel");
            /*
            ItemIconButtonWidget duplicate = new ItemIconButtonWidget(30, labelMenuHeight + 5 + index * 25, 20, 20, new LiteralText(""), b -> {

            }, ((button1, matrices, mouseX, mouseY) -> {
                renderTooltip(matrices, new LiteralText("Duplicate").setStyle(Style.EMPTY.withColor(0x4444FF)), mouseX, mouseY);
            }), "blue_stained_glass_pane", "");
             */

            filterListOption.add(options);
            filterListValue.add(value);
            filterListComparator.add(comparator);
            filterListConstant.add(constant);
            filterListDelete.add(delete);
            //filterListDuplicate.add(duplicate);

            updateFilterListPositions();
        });
    }

    public void clearFilters() {
        filterListOption.clear();
        filterListValue.clear();
        filterListComparator.clear();
        filterListConstant.clear();
        filterListDelete.clear();
        //filterListDuplicate.clear();
        itemFilters.clear();

        updateFilterOutput();
        updateFilterListPositions();
    }

    private void updateFilterListPositions() {
        addFilterButton.y = labelMenuHeight + 5 + filterListOption.size() * 25;

        filterListOption.forEach(i -> i.y = labelMenuHeight + 8 + filterListOption.indexOf(i) * 25);
        filterListValue.forEach(i -> i.y = labelMenuHeight + 8 + filterListValue.indexOf(i) * 25);
        filterListComparator.forEach(i -> i.y = labelMenuHeight + 5 + filterListComparator.indexOf(i) * 25);
        filterListConstant.forEach(i -> i.y = labelMenuHeight + 8 + filterListConstant.indexOf(i) * 25);
        filterListDelete.forEach(i -> i.y = labelMenuHeight + 5 + filterListDelete.indexOf(i) * 25);
        //filterListDuplicate.forEach(i -> i.y = labelMenuHeight + 5 + filterListDuplicate.indexOf(i) * 25);
    }

    private void updateFilterOutput() {
        controller.updateFilters(itemFilters);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);

        // draw filter buttons and stuff
        boolean anyOpen = false;
        for (DropdownWidget o : filterListOption) if (o.willClick(mouseX, mouseY)) anyOpen = true;
        if (anyOpen) {
            addFilterButton.render(matrices, 0, 0, delta); // funny band-aid fix for rendering white outline while in dropdown menu
        } else {
            addFilterButton.render(matrices, mouseX, mouseY, delta);
        }

        for (DropdownWidget o : filterListOption) {
            o.renderMain(matrices, mouseX, mouseY, delta);
        }
        for (DropdownWidget v : filterListValue) {
            if (!filterListOption.get(filterListValue.indexOf(v)).getLastChoice().equals(""))
                v.renderMain(matrices, mouseX, mouseY, delta);
        }
        for (TextFieldWidget c : filterListConstant) {
            if (filterListOption.get(filterListConstant.indexOf(c)).getLastChoice().equals("Stat") && !filterListComparator.get(filterListConstant.indexOf(c)).getMessage().asString().contains("Exists"))
                c.render(matrices, mouseX, mouseY, delta);
        }
        for (ButtonWidget c : filterListComparator) {
            if (!filterListOption.get(filterListComparator.indexOf(c)).getLastChoice().equals(""))
                c.render(matrices, mouseX, mouseY, delta);
        }
        filterListDelete.forEach(i -> i.render(matrices, mouseX, mouseY, delta));
        //filterListDuplicate.forEach(i -> i.render(matrices, mouseX, mouseY, delta));

        for (DropdownWidget o : filterListOption) {
            o.renderDropdown(matrices, mouseX, mouseY, delta);
        }
        for (DropdownWidget v : filterListValue) {
            if (!filterListOption.get(filterListValue.indexOf(v)).getLastChoice().equals(""))
                v.renderDropdown(matrices, mouseX, mouseY, delta);
        }

        // draw the label at the top
        matrices.push();
        matrices.translate(0, 0, 110);
        fill(matrices, 0, 0, width, labelMenuHeight, 0xFF555555);
        drawHorizontalLine(matrices, 0, width, labelMenuHeight, 0xFFFFFFFF);
        drawCenteredText(matrices, textRenderer, new LiteralText("Item Filters").setStyle(Style.EMPTY.withBold(true)), width / 2, (labelMenuHeight - textRenderer.fontHeight) / 2, 0xFFFFAA00);
        matrices.pop();

        // draw gui elements
        matrices.push();
        matrices.translate(0, 0, 110);
        backButton.render(matrices, mouseX, mouseY, delta);
        matrices.pop();

        try {
            super.render(matrices, mouseX, mouseY, delta);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        backButton.mouseClicked(mouseX, mouseY, button);

        for (DropdownWidget o : filterListOption) {
            if (o.willClick(mouseX, mouseY)) {
                o.mouseClicked(mouseX, mouseY, button);
                return false;
            }
            o.mouseClicked(mouseX, mouseY, button);
        }
        for (DropdownWidget v : filterListValue) {
            if (v.willClick(mouseX, mouseY)) {
                if (!filterListOption.get(filterListValue.indexOf(v)).getLastChoice().equals(""))
                    v.mouseClicked(mouseX, mouseY, button);
                return false;
            }
            if (!filterListOption.get(filterListValue.indexOf(v)).getLastChoice().equals(""))
                v.mouseClicked(mouseX, mouseY, button);
        }
        for (ButtonWidget c : filterListComparator) {
            if (!filterListOption.get(filterListComparator.indexOf(c)).getLastChoice().equals(""))
                c.mouseClicked(mouseX, mouseY, button);
        }
        for (TextFieldWidget c : filterListConstant) {
            if (filterListOption.get(filterListConstant.indexOf(c)).getLastChoice().equals("Stat") && !filterListComparator.get(filterListConstant.indexOf(c)).getMessage().asString().contains("Exists"))
                c.mouseClicked(mouseX, mouseY, button);
        }
        filterListDelete.forEach(i -> i.mouseClicked(mouseX, mouseY, button));
        //filterListDuplicate.forEach(i -> i.mouseClicked(mouseX, mouseY, button));

        addFilterButton.mouseClicked(mouseX, mouseY, button);

        if (removeIndex != -1) {
            filterListOption.remove(removeIndex);
            filterListValue.remove(removeIndex);
            filterListComparator.remove(removeIndex);
            filterListConstant.remove(removeIndex);
            filterListDelete.remove(removeIndex);
            //filterListDuplicate.remove(removeIndex);
            itemFilters.remove(removeIndex);
            removeIndex = -1;

            updateFilterListPositions();
            updateFilterOutput();
        }

        return true;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        super.charTyped(chr, modifiers);

        filterListOption.forEach(i -> i.charTyped(chr, modifiers));
        filterListValue.forEach(i -> i.charTyped(chr, modifiers));
        filterListConstant.forEach(i -> i.charTyped(chr, modifiers));

        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        super.keyPressed(keyCode, scanCode, modifiers);

        filterListOption.forEach(i -> i.keyPressed(keyCode, scanCode, modifiers));
        filterListValue.forEach(i -> i.keyPressed(keyCode, scanCode, modifiers));
        filterListConstant.forEach(i -> i.keyPressed(keyCode, scanCode, modifiers));

        return true;
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);


    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        super.mouseScrolled(mouseX, mouseY, amount);

        filterListOption.forEach(i -> i.mouseScrolled(mouseX, mouseY, amount));
        filterListValue.forEach(i -> i.mouseScrolled(mouseX, mouseY, amount));

        return true;
    }
}
