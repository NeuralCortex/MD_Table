package com.nc.mdtable.controller;

import com.nc.mdtable.Globals;
import com.nc.mdtable.pojo.Parameter;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

public class ParamsController implements Initializable {

    @FXML
    private TableView<Parameter> table;

    private final MainController mainController;

    public ParamsController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL url, ResourceBundle bundle) {
        TableColumn<Parameter, String> colKey = new TableColumn<>(bundle.getString("col.param"));
        TableColumn<Parameter, String> colValue = new TableColumn<>(bundle.getString("col.value"));

        colKey.setCellValueFactory(new PropertyValueFactory<>("key"));
        colValue.setCellValueFactory(new PropertyValueFactory<>("value"));

        table.setEditable(true);
        table.getSelectionModel().setCellSelectionEnabled(true);

        colKey.setEditable(true);
        colKey.setCellFactory(TextFieldTableCell.forTableColumn());
        colKey.setOnEditCommit(e -> {
            Parameter item = e.getRowValue();
            item.setKey(e.getNewValue());
        });

        colValue.setEditable(true);
        colValue.setCellFactory(TextFieldTableCell.forTableColumn());
        colValue.setOnEditCommit(e -> {
            Parameter item = e.getRowValue();
            item.setValue(e.getNewValue());
        });

        table.getColumns().addAll(colKey, colValue);

        ContextMenu contextMenu = new ContextMenu();
        MenuItem miAdd = new MenuItem(bundle.getString("mi.add.row"));
        miAdd.setOnAction(e -> {
            table.getItems().add(new Parameter("Edit", "Edit"));
        });
        contextMenu.getItems().addAll(miAdd);
        table.setContextMenu(contextMenu);

        table.setRowFactory(tv -> {
            TableRow<Parameter> row = new TableRow<>();

            MenuItem miDel = new MenuItem(bundle.getString("mi.del.row"));

            miDel.setOnAction(e -> {
                table.getItems().remove(table.getSelectionModel().getSelectedItem());
            });

            ContextMenu contextRow = new ContextMenu(miDel);

            row.emptyProperty().addListener((obs, wasEmpty, isEmpty) -> {
                if (isEmpty) {
                    row.setContextMenu(null);
                } else {
                    row.setContextMenu(contextRow);
                }
            });

            return row;
        });

        loadParams();
    }

    private void loadParams() {
        String flat = Globals.propman.getProperty(Globals.PARAMS, "");
        if (!flat.isEmpty()) {
            String rows[] = flat.split(Globals.SEP_ROW, -1);
            for (String row : rows) {
                String cols[] = row.split(Globals.SEP_COL, -1);
                table.getItems().add(new Parameter(cols[0], cols[1]));
            }

        }
    }

    public void saveParams() {
        String res = "";
        for (int i = 0; i < table.getItems().size(); i++) {
            Parameter parameter = table.getItems().get(i);
            String row = parameter.getKey() + Globals.SEP_COL + parameter.getValue();
            if (i < table.getItems().size() - 1) {
                res += row + Globals.SEP_ROW;
            } else {
                res += row;
            }
        }
        Globals.propman.setProperty(Globals.PARAMS, res);
        Globals.propman.save();
    }

}
