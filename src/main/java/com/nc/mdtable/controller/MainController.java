package com.nc.mdtable.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nc.mdtable.Globals;
import static com.nc.mdtable.Globals.ALIGNMENT.CENTER;
import static com.nc.mdtable.Globals.ALIGNMENT.LEFT;
import static com.nc.mdtable.Globals.ALIGNMENT.RIGHT;
import com.nc.mdtable.adapter.ClassTypeAdapter;
import com.nc.mdtable.pojo.Content;
import com.nc.mdtable.pojo.Row;
import com.nc.mdtable.pojo.TableJSON;
import com.nc.mdtable.pojo.UserData;
import com.nc.mdtable.tools.HelperFunctions;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainController implements Initializable {

    @FXML
    private TableView<Row> table;
    @FXML
    private TextField tfCols;
    @FXML
    private TextField tfRows;
    @FXML
    private Button btnGenTable;
    @FXML
    private Button btnGenMarkdown;
    @FXML
    private RadioButton rbLeft;
    @FXML
    private RadioButton rbCenter;
    @FXML
    private RadioButton rbRight;
    @FXML
    private TextArea taMarkdown;
    @FXML
    private MenuItem miOpenJson;
    @FXML
    private MenuItem miSaveAsJson;
    @FXML
    private MenuItem miParams;
    @FXML
    private MenuItem miAbout;
    @FXML
    private MenuItem miClose;
    @FXML
    private TextField tfHeader;
    @FXML
    private TextArea tfCell;
    @FXML
    private HBox hboxMarkdown;
    @FXML
    private HBox hboxBottom;
    @FXML
    private Button btnSaveMarkdown;
    @FXML
    private Label lbProps;
    @FXML
    private Label lbGrid;
    @FXML
    private Label lbCols;
    @FXML
    private Label lbRows;
    @FXML
    private Label lbAlign;
    @FXML
    private Label lbHeader;
    @FXML
    private Label lbEditor;
    @FXML
    private Label lbInfo;
    @FXML
    private Label lbStatus;

    private final Stage stage;
    private TableColumn<Row, ?> activeColumn = null;
    private Property<String> activeCellProperty = null;
    private TableColumn<Row, ?> selColumn = null;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Class.class, new ClassTypeAdapter())
            .setPrettyPrinting()
            .create();

    public MainController(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle bundle) {
        String about = MessageFormat.format(bundle.getString("app.about"), LocalDate.now().getYear());
        lbInfo.setText(about);

        hboxBottom.getStyleClass().add("fg");
        hboxMarkdown.getStyleClass().add("fg");

        lbProps.setText(bundle.getString("lb.props"));
        lbGrid.setText(bundle.getString("lb.grid"));
        lbCols.setText(bundle.getString("lb.cols"));
        lbRows.setText(bundle.getString("lb.rows"));
        lbAlign.setText(bundle.getString("lb.align"));
        lbHeader.setText(bundle.getString("lb.header"));
        lbEditor.setText(bundle.getString("lb.editor"));

        rbLeft.setText(bundle.getString("left"));
        rbCenter.setText(bundle.getString("center"));
        rbRight.setText(bundle.getString("right"));

        btnGenTable.setText(bundle.getString("btn.create.table"));
        btnGenMarkdown.setText(bundle.getString("btn.create.md"));
        btnSaveMarkdown.setText(bundle.getString("btn.saveas.md"));

        ToggleGroup tgAlign = new ToggleGroup();
        rbLeft.setToggleGroup(tgAlign);
        rbCenter.setToggleGroup(tgAlign);
        rbRight.setToggleGroup(tgAlign);

        tfRows.setText("5");
        tfCols.setText("5");

        setupRowFactoryWithContextMenu(bundle);
        setupTableListener();

        miAbout.setText(bundle.getString("mi.about"));
        miParams.setText(bundle.getString("param.editor") + "...");
        miOpenJson.setText(bundle.getString("mi.json.open"));
        miSaveAsJson.setText(bundle.getString("mi.json.saveas"));

        miOpenJson.setOnAction(e -> openJson());
        miSaveAsJson.setOnAction(e -> saveAsJson());
        miParams.setOnAction(e -> editParams(bundle));
        miAbout.setOnAction(e -> showAbout(bundle));
        miClose.setOnAction(e -> System.exit(0));

        btnGenTable.setOnAction(e -> genTable(bundle));
        btnGenMarkdown.setOnAction(e -> genMarkdown());
        btnSaveMarkdown.setOnAction(e -> saveMarkdown());

        rbLeft.setOnAction(e -> updateAlignment(LEFT));
        rbCenter.setOnAction(e -> updateAlignment(CENTER));
        rbRight.setOnAction(e -> updateAlignment(RIGHT));

        tfHeader.textProperty().addListener((obs, oldText, newText) -> {
            if (activeColumn != null) {
                activeColumn.setText(newText);
            }
        });
    }

    private void setupTableListener() {
        table.getFocusModel().focusedCellProperty().addListener((obs, oldCell, newCell) -> {

            if (activeCellProperty != null) {
                tfCell.textProperty().unbindBidirectional(activeCellProperty);
                activeCellProperty = null;
            }

            if (newCell != null && newCell.getTableColumn() != null && newCell.getRow() >= 0) {
                activeColumn = newCell.getTableColumn();
                tfHeader.setText(activeColumn.getText());

                Row selectedRowData = table.getItems().get(newCell.getRow());

                if (selectedRowData != null) {
                    int colIndex = table.getColumns().indexOf(activeColumn);

                    TableColumn<Row, ?> col = activeColumn;

                    if (((UserData) col.getUserData()).getId() >= 0) {

                        activeCellProperty = selectedRowData.propertyAt(((UserData) col.getUserData()).getId());

                        if (activeCellProperty != null) {
                            tfCell.textProperty().bindBidirectional(activeCellProperty);
                        } else {
                            tfCell.clear();
                        }
                    }
                }
            } else {
                activeColumn = null;
                tfHeader.clear();
                tfCell.clear();
            }
        });

        table.getSelectionModel().getSelectedCells().addListener((ListChangeListener<TablePosition>) change -> {
            var selectedCells = table.getSelectionModel().getSelectedCells();

            if (!selectedCells.isEmpty()) {
                TablePosition<?, ?> tablePosition = selectedCells.get(0);

                int row = tablePosition.getRow();
                int col = tablePosition.getColumn();

                Globals.ALIGNMENT align = ((UserData) table.getColumns().get(col).getUserData()).getAlignment();

                if (align != null) {
                    switch (align) {
                        case LEFT:
                            rbLeft.setSelected(true);
                            break;
                        case CENTER:
                            rbCenter.setSelected(true);
                            break;
                        case RIGHT:
                            rbRight.setSelected(true);
                            break;
                        default:
                            throw new AssertionError();
                    }
                }

                lbStatus.setText("Col: " + (col + 1) + " Row: " + (row + 1));
            }
        });

        table.getSelectionModel().getSelectedCells().addListener((ListChangeListener.Change<? extends TablePosition> change) -> {
            if (!table.getSelectionModel().getSelectedCells().isEmpty()) {
                TablePosition<Row, ?> cell = table.getSelectionModel().getSelectedCells().get(0);
                selColumn = cell.getTableColumn();

                System.out.println("User selected column: " + selColumn.getText());
            }
        });
    }

    private void showAbout(ResourceBundle bundle) {
        HelperFunctions.showAlertDialog(bundle, Alert.AlertType.INFORMATION, bundle.getString("app.name") + "\n" + MessageFormat.format(bundle.getString("app.about"), LocalDate.now().getYear()));
    }

    private void saveMarkdown() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Markdownn files (*.md)", "*.md")
        );
        fileChooser.setInitialDirectory(new File(Globals.propman.getProperty(Globals.PATH_MD_SAVE_AS, System.getProperty("user.dir"))));
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                saveToMd(file);
            } catch (IOException ex) {
                System.getLogger(MainController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
            Globals.propman.setProperty(Globals.PATH_MD_SAVE_AS, file.getParent());
            Globals.propman.save();
        }
    }

    public void saveToMd(File file) throws IOException {
        if (!file.getName().toLowerCase().endsWith(".md")) {
            file = new File(file.getAbsolutePath() + ".md");
        }
        try (Writer writer = new FileWriter(file)) {
            writer.write(taMarkdown.getText());
            writer.close();
        }
    }

    private void editParams(ResourceBundle bundle) {
        ParamsController paramsController = new ParamsController(this);
        Node dialogContent = HelperFunctions.loadFxmlNode(bundle, Globals.FXML_PARAMS_PATH, paramsController);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);
        dialog.setTitle(bundle.getString("param.editor"));

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(Globals.CSS_PATH);
        dialogPane.setContent(dialogContent);

        ButtonType saveButtonType = new ButtonType(bundle.getString("btn.save"), ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType(bundle.getString("btn.cancel"), ButtonData.CANCEL_CLOSE);

        if (dialogPane.getButtonTypes().isEmpty()) {
            dialogPane.getButtonTypes().addAll(saveButtonType, cancelButtonType);
        }

        Optional<ButtonType> res = dialog.showAndWait();
        if (res.isPresent() && res.get() == saveButtonType) {
            paramsController.saveParams();
        }
    }

    private void openJson() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json")
        );
        fileChooser.setInitialDirectory(new File(Globals.propman.getProperty(Globals.PATH_JSON_OPEN, System.getProperty("user.dir"))));
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                loadFromJson(file);
            } catch (IOException ex) {
                System.getLogger(MainController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
            Globals.propman.setProperty(Globals.PATH_JSON_OPEN, file.getParent());
            Globals.propman.save();
        }
    }

    private void saveAsJson() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json")
        );
        fileChooser.setInitialDirectory(new File(Globals.propman.getProperty(Globals.PATH_JSON_SAVE_AS, System.getProperty("user.dir"))));
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                saveToJson(file);
            } catch (IOException ex) {
                System.getLogger(MainController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
            Globals.propman.setProperty(Globals.PATH_JSON_SAVE_AS, file.getParent());
            Globals.propman.save();
        }
    }

    public void loadFromJson(File file) throws IOException {
        try (Reader reader = new FileReader(file)) {

            table.getColumns().clear();
            table.getItems().clear();

            TableJSON loaded = gson.fromJson(reader, TableJSON.class);

            List<String> columnNames = loaded.getColumnNames();
            List<String> columnAligns = loaded.getColumnAligns();

            List<TableColumn<Row, String>> cols = new ArrayList<>();
            List<Row> rows = new ArrayList<>();

            for (int i = 0; i < columnNames.size(); i++) {
                String columnName = columnNames.get(i);
                String columnAlign = columnAligns.get(i);
                TableColumn<Row, String> newColumn = new TableColumn<>(columnName);
                newColumn.setUserData(new UserData(Globals.ALIGNMENT.valueOf(columnAlign), i));
                newColumn.setCellFactory(TextFieldTableCell.forTableColumn());
                final int j = i;
                newColumn.setCellValueFactory(param -> param.getValue().propertyAt(j));

                cols.add(newColumn);
            }

            for (int i = 0; i < loaded.getData().size(); i++) {
                List<String> items = loaded.getData().get(i);
                Row dataRow = new Row(items);

                rows.add(dataRow);
            }

            table.getColumns().addAll(cols);
            table.setItems(FXCollections.observableArrayList(rows));
        }
    }

    public void saveToJson(File file) throws IOException {
        if (!file.getName().toLowerCase().endsWith(".json")) {
            file = new File(file.getAbsolutePath() + ".json");
        }
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(getJsonObj(), writer);
        }
    }

    private TableJSON getJsonObj() {
        List<String> columnNames = new ArrayList<>();
        List<String> columnAligns = new ArrayList<>();
        for (TableColumn<Row, ?> column : table.getColumns()) {
            columnNames.add(column.getText());
            columnAligns.add(((UserData) column.getUserData()).getAlignment().toString());
        }

        List<List<String>> dataRows = new ArrayList<>();
        for (Row item : table.getItems()) {
            List<String> rowMap = new ArrayList<>();

            for (TableColumn<Row, ?> column : table.getColumns()) {
                Object cellValue = column.getCellData(item);
                rowMap.add((String) cellValue);
            }
            dataRows.add(rowMap);
        }

        TableJSON tableJSON = new TableJSON(columnNames, columnAligns, dataRows);

        return tableJSON;
    }

    private void setupRowFactoryWithContextMenu(ResourceBundle bundle) {
        table.setRowFactory(tv -> {
            TableRow<Row> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem insertRowItem = new MenuItem(bundle.getString("mi.row.insert"));
            insertRowItem.setOnAction(event -> {

                Row newRow = new Row(table.getColumns().size());
                int index = row.getIndex() + 1;
                table.getItems().add(index, newRow);
            });

            MenuItem deleteRowItem = new MenuItem(bundle.getString("mi.row.del"));
            deleteRowItem.setOnAction(event -> {
                table.getItems().remove(row.getItem());
            });

            MenuItem deleteColumnItem = new MenuItem(bundle.getString("mi.col.del"));
            deleteColumnItem.setOnAction(event -> {
                if (selColumn != null) {

                    int indexToRemove = table.getColumns().indexOf(selColumn);

                    if (indexToRemove != -1) {

                        table.getColumns().remove(selColumn);
                        table.refresh();

                    }
                }
            });

            MenuItem insertColumnItem = new MenuItem(bundle.getString("mi.col.insert"));
            insertColumnItem.setOnAction(event -> {
                if (selColumn != null) {
                    int index = table.getColumns().indexOf(selColumn);
                    int targetIndex = index + 1;
                    int rowCount = table.getItems().size();

                    for (int i = 0; i < rowCount; i++) {
                        Row dataRow = table.getItems().get(i);
                        dataRow.extend(targetIndex, "New");
                    }

                    TableColumn<Row, String> newColumn = new TableColumn<>("New Column");
                    List<TableColumn<Row, ?>> cols = table.getColumns();
                    int max = 0;
                    for (TableColumn<Row, ?> col : cols) {
                        if (((UserData) col.getUserData()).getId() > max) {
                            max = ((UserData) col.getUserData()).getId();
                        }
                    }

                    newColumn.setUserData(new UserData(LEFT, max + 1));
                    newColumn.setCellFactory(TextFieldTableCell.forTableColumn());

                    newColumn.getStyleClass().addAll(selColumn.getStyleClass());
                    newColumn.setStyle(selColumn.getStyle());

                    table.getColumns().add(targetIndex, newColumn);

                    for (int i = targetIndex; i < table.getColumns().size(); i++) {
                        final int modelIndex = i; // Safely capture the correct current index
                        TableColumn<Row, ?> col = table.getColumns().get(i);

                        col.setCellValueFactory(param -> {
                            Row rowD = param.getValue();
                            if (rowD != null) {
                                return (ObservableValue) rowD.propertyAt(modelIndex);
                            }
                            return null;
                        });
                    }

                    table.refresh();
                }
            });

            contextMenu.getItems().addAll(insertRowItem, insertColumnItem, new SeparatorMenuItem(), deleteRowItem, deleteColumnItem);

            row.emptyProperty().addListener((obs, wasEmpty, isEmpty) -> {
                if (isEmpty) {
                    row.setContextMenu(null);
                } else {
                    row.setContextMenu(contextMenu);
                }
            });

            return row;
        });
    }

    public <T> void reorderColumns(TableView<T> table, int fromIndex, int toIndex) {
        int columnCount = table.getColumns().size();

        if (fromIndex >= 0 && fromIndex < columnCount && toIndex >= 0 && toIndex < columnCount) {
            TableColumn<T, ?> column = table.getColumns().remove(fromIndex);
            table.getColumns().add(toIndex, column);
        }
    }

    private void updateAlignment(Globals.ALIGNMENT align) {
        try {
            int col = table.getSelectionModel().getSelectedCells().get(0).getColumn();

            ((UserData) table.getColumns().get(col).getUserData()).setAlignment(align);
        } catch (IndexOutOfBoundsException ex) {
            return;
        }
    }

    private void genTable(ResourceBundle bundle) {
        table.getColumns().clear();
        table.getItems().clear();

        table.setEditable(true);
        table.getSelectionModel().setCellSelectionEnabled(true);

        int numCols;
        int numRows;

        try {
            numCols = Integer.parseInt(tfCols.getText());
            numRows = Integer.parseInt(tfRows.getText());
        } catch (NumberFormatException ex) {
            HelperFunctions.showAlertDialog(bundle, Alert.AlertType.ERROR, ex.getLocalizedMessage());
            return;
        }

        List<TableColumn<Row, String>> tempColumns = new ArrayList<>(numCols);
        for (int j = 0; j < numCols; j++) {
            TableColumn<Row, String> col = new TableColumn<>("Col " + j);
            col.setUserData(new UserData(LEFT, j));
            final int colIndex = j;
            col.setCellValueFactory(param -> {
                TableColumn<Row, String> colT = param.getTableColumn();
                int idx = ((UserData) colT.getUserData()).getId();

                return param.getValue().propertyAt(idx);
            });
            col.setCellFactory(TextFieldTableCell.forTableColumn());

            tempColumns.add(col);
        }

        List<Row> tempRows = new ArrayList<>(numRows);
        for (int i = 0; i < numRows; i++) {
            tempRows.add(new Row(numCols));
        }

        table.getColumns().addAll(tempColumns);
        table.getItems().addAll(tempRows);

    }

    private void genMarkdown() {
        String params = Globals.propman.getProperty(Globals.PARAMS, "");
        String[] rules = params.split(Globals.SEP_ROW, -1);

        Content content = getTableContent();

        List<String> md = new ArrayList<>();
        List<Integer> widths = new ArrayList<>();
        for (int i = 0; i < content.getColWidths().size(); i++) {
            widths.add(0);
        }

        for (int row = 0; row < content.getContent().size(); row++) {
            List<String> line = content.getContent().get(row);
            line.replaceAll(str -> str.replace("\n", " "));
            for (String rule : rules) {
                String[] pair = rule.split(Globals.SEP_COL, -1);
                if (pair.length == 2) {
                    line.replaceAll(str -> str.replace(pair[0], pair[1]));
                }
            }
            for (int col = 0; col < line.size(); col++) {
                int max = line.get(col).length();
                if (max > widths.get(col)) {
                    widths.set(col, max);
                }
            }

        }

        content.setColWidths(widths);

        for (int row = 0; row < content.getContent().size(); row++) {
            List<String> line = content.getContent().get(row);
            if (line.size() > 0) {
                md.add(formatContent(content.getColWidths(), line));
            }
        }

        if (content.getContent().size() > 0 && !md.isEmpty()) {
            md.add(1, formatSeparator(content.getColWidths(), content.getAligns()));
        }

        taMarkdown.setText(String.join("\n", md));
    }

    private String formatContent(List<Integer> widths, List<String> row) {
        if (row == null || row.isEmpty()) {
            return "";
        }

        String format = "";
        for (int i = 0; i < row.size(); i++) {
            int w = widths.get(i);
            if (w <= 3) {
                w = 3;
            }
            format += "|%-" + w + "s";
        }
        format += "|";
        return String.format(format, row.toArray());
    }

    private String formatSeparator(List<Integer> widths, List<Globals.ALIGNMENT> aligns) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < widths.size(); i++) {
            int w = widths.get(i);
            if (w <= 3) {
                w = 3;
            }
            Globals.ALIGNMENT align = aligns.get(i);

            sb.append("|");

            switch (align) {
                case LEFT:
                    sb.append(":");
                    for (int j = 0; j < w - 1; j++) {
                        sb.append("-");
                    }
                    break;

                case CENTER:
                    sb.append(":");
                    for (int j = 0; j < w - 2; j++) {
                        sb.append("-");
                    }
                    sb.append(":");
                    break;

                case RIGHT:
                    for (int j = 0; j < w - 1; j++) {
                        sb.append("-");
                    }
                    sb.append(":");
                    break;

                default:
                    for (int j = 0; j < w; j++) {
                        sb.append("-");
                    }
                    break;
            }
        }

        sb.append("|");
        return sb.toString();
    }

    private Content getTableContent() {
        List<List<String>> content = new ArrayList<>();
        int numColumns = table.getColumns().size();

        int[] widthsArray = new int[numColumns];
        List<Globals.ALIGNMENT> aligns = new ArrayList<>();

        List<String> header = new ArrayList<>(numColumns);
        for (int i = 0; i < numColumns; i++) {
            String headerText = table.getColumns().get(i).getText();
            header.add(headerText);
            widthsArray[i] = headerText != null ? headerText.length() : 0;
            aligns.add(((UserData) table.getColumns().get(i).getUserData()).getAlignment());
        }

        if (table.getItems().size() > 0) {
            content.add(header);
        }

        List<?> items = table.getItems();

        int row = 0;
        for (Object item : items) {
            Row rowProperties = (Row) item;
            List<String> rowData = new ArrayList<>(numColumns);

            for (int colIdx = 0; colIdx < numColumns; colIdx++) {
                TableColumn<Row, ?> col = table.getColumns().get(colIdx);

                StringProperty prop = rowProperties.propertyAt(((UserData) col.getUserData()).getId());
                String value = (prop != null) ? prop.get() : "";
                rowData.add(value);

                int len = (value != null) ? value.length() : 0;
                if (len > widthsArray[colIdx]) {
                    widthsArray[colIdx] = len;
                }
            }
            content.add(rowData);
            row++;
        }

        List<Integer> colWidths = new ArrayList<>(numColumns);
        for (int width : widthsArray) {
            colWidths.add(width);
        }

        return new Content(content, colWidths, aligns);
    }
}
