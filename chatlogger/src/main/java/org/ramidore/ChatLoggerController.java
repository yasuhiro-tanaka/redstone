/*
 * Copyright 2014.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ramidore;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.ramidore.bean.ChatTable;
import org.ramidore.bean.ItemTable;
import org.ramidore.bean.RedStoneChartBean;
import org.ramidore.component.OshiraseJDialog;
import org.ramidore.controller.AbstractMainController;
import org.ramidore.core.PacketAnalyzeService;
import org.ramidore.logic.ChatLoggerLogic;
import org.ramidore.logic.system.RedstoneLogic;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;

/**
 * . JavaFXコントローラクラス
 *
 * @author atmark
 */
public class ChatLoggerController extends AbstractMainController {

    /**
     * 叫び 表示用テーブル.
     */
    @FXML
    private TableView<ChatTable> sakebiChatTable;

    /**
     * 叫び 日付のカラム.
     */
    @FXML
    private TableColumn<ChatTable, String> sakebiChatDate;

    /**
     * 叫び 名前のカラム.
     */
    @FXML
    private TableColumn<ChatTable, String> sakebiChatName;

    /**
     * 叫び 内容のカラム.
     */
    @FXML
    private TableColumn<ChatTable, String> sakebiChatContent;

    /**
     * 一般 表示用テーブル.
     */
    @FXML
    private TableView<ChatTable> normalChatTable;

    /**
     * 一般 日付のカラム.
     */
    @FXML
    private TableColumn<ChatTable, String> normalChatDate;

    /**
     * 一般 名前のカラム.
     */
    @FXML
    private TableColumn<ChatTable, String> normalChatName;

    /**
     * 一般 内容のカラム.
     */
    @FXML
    private TableColumn<ChatTable, String> normalChatContent;

    /**
     * PT 表示用テーブル.
     */
    @FXML
    private TableView<ChatTable> partyChatTable;

    /**
     * PT 日付のカラム.
     */
    @FXML
    private TableColumn<ChatTable, String> partyChatDate;

    /**
     * PT 名前のカラム.
     */
    @FXML
    private TableColumn<ChatTable, String> partyChatName;

    /**
     * PT 内容のカラム.
     */
    @FXML
    private TableColumn<ChatTable, String> partyChatContent;

    /**
     * ギルチャ 表示用テーブル.
     */
    @FXML
    private TableView<ChatTable> guildChatTable;

    /**
     * ギルチャ 日付のカラム.
     */
    @FXML
    private TableColumn<ChatTable, String> guildChatDate;

    /**
     * ギルチャ 名前のカラム.
     */
    @FXML
    private TableColumn<ChatTable, String> guildChatName;

    /**
     * ギルチャ 内容のカラム.
     */
    @FXML
    private TableColumn<ChatTable, String> guildChatContent;

    /**
     * 耳打ち 表示用テーブル.
     */
    @FXML
    private TableView<ChatTable> mimiChatTable;

    /**
     * 耳打ち 日付のカラム.
     */
    @FXML
    private TableColumn<ChatTable, String> mimiChatDate;

    /**
     * 耳打ち 名前のカラム.
     */
    @FXML
    private TableColumn<ChatTable, String> mimiChatName;

    /**
     * 耳打ち 内容のカラム.
     */
    @FXML
    private TableColumn<ChatTable, String> mimiChatContent;

    /**
     * アイテム 表示用テーブル.
     */
    @FXML
    private TableView<ItemTable> itemTable;

    /**
     * アイテム 日付のカラム.
     */
    @FXML
    private TableColumn<ItemTable, String> itemDate;

    /**
     * アイテム 名前のカラム.
     */
    @FXML
    private TableColumn<ItemTable, String> itemName;

    /**
     * ネットワークデバイスのセレクトボックス.
     */
    @FXML
    private ChoiceBox<String> deviceCb;

    /**
     * IPアドレスのセレクトボックス.
     */
    @FXML
    private ChoiceBox<String> addressCb;

    /**
     * 開始/停止ボタン.
     */
    @FXML
    private ToggleButton startTb;

    /**
     * クリアボタン.
     */
    @FXML
    private Button clearButton;

    /**
     * メニュー ウインドウ表示.
     */
    @FXML
    private CheckMenuItem oshiraseWindow;

    /**
     * メニュー 叫びON/OFF.
     */
    @FXML
    private CheckMenuItem sakebiMenu;

    /**
     * メニュー 通常チャットON/OFF.
     */
    @FXML
    private CheckMenuItem normalMenu;

    /**
     * メニュー PTチャットON/OFF.
     */
    @FXML
    private CheckMenuItem partyMenu;

    /**
     * メニュー ギルチャON/OFF.
     */
    @FXML
    private CheckMenuItem guildMenu;

    /**
     * メニュー 耳打ちON/OFF.
     */
    @FXML
    private CheckMenuItem mimiMenu;

    /**
     * お知らせ表示行数変更UI.
     */
    @FXML
    private ChoiceBox<Integer> oshiraseLineCountCb;

    /**
     * 透明度変更用スライダ.
     */
    @FXML
    private Slider opacitySlider;

    /**
     * JFXPanel.
     */
    private JFXPanel jfxPanel;

    /**
     * お知らせウインドウ.
     * <p>
     * SwingのJDialog拡張
     */
    private OshiraseJDialog oshiraseJDialog;

    /**
     * エリアチャート.
     */
    @FXML
    private AreaChart<Number, Number> areaChart;
    
    /**
     * 取得ユニーク数.
     */
    @FXML
    private Label numOfUniqueLabel;
    
    /**
     * 討伐モンスター数.
     */
    @FXML
    private Label numOfKillMobLabel;
    
    /**
     * 取得ゴールド合計.
     */
    @FXML
    private Label numOfGold;
    

    /**
     * 自動起動.
     */
    private boolean autoRun;

    @Override
    public void concreteInitialize() {

        setService(new PacketAnalyzeService(new ChatLoggerLogic(), getConfig()));

        loadConfig();

        initializeDeviceSetting();

        initializeOshirase();

        initializeChart();

        initializeLogic();
    }

    /**
     * パケット処理関連の業務処理の初期化.
     */
    private void initializeLogic() {

        // コントローラーのフィールドとデータクラスのプロパティーを紐付ける

        ChatLoggerLogic logic = (ChatLoggerLogic) getService().getLogic();

        sakebiChatDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        sakebiChatName.setCellValueFactory(new PropertyValueFactory<>("name"));
        sakebiChatContent.setCellValueFactory(new PropertyValueFactory<>("content"));

        logic.getSakebiChatLogic().setTable(sakebiChatTable);

        normalChatDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        normalChatName.setCellValueFactory(new PropertyValueFactory<>("name"));
        normalChatContent.setCellValueFactory(new PropertyValueFactory<>("content"));

        logic.getNormalChatLogic().setTable(normalChatTable);

        partyChatDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        partyChatName.setCellValueFactory(new PropertyValueFactory<>("name"));
        partyChatContent.setCellValueFactory(new PropertyValueFactory<>("content"));

        logic.getPartyChatLogic().setTable(partyChatTable);

        guildChatDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        guildChatName.setCellValueFactory(new PropertyValueFactory<>("name"));
        guildChatContent.setCellValueFactory(new PropertyValueFactory<>("content"));

        logic.getGuildChatLogic().setTable(guildChatTable);

        mimiChatDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        mimiChatName.setCellValueFactory(new PropertyValueFactory<>("name"));
        mimiChatContent.setCellValueFactory(new PropertyValueFactory<>("content"));

        logic.getMimiChatLogic().setTable(mimiChatTable);

        itemDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        itemName.setCellValueFactory(new PropertyValueFactory<>("name"));

        logic.getItemLogic().setItemTable(itemTable);
        logic.getItemLogic().setNumOfGold(numOfGold);

        setUpContextMenu(sakebiChatTable);
        setUpContextMenu(normalChatTable);
        setUpContextMenu(partyChatTable);
        setUpContextMenu(guildChatTable);
        setUpContextMenu(mimiChatTable);
    }
    
    /**
     * ネットワークデバイスの初期化.
     */
    private void initializeDeviceSetting() {

        // デバイス一覧を初期化
    	List<String> deviceList = getService().getDeviceNameList();
        deviceCb.getItems().addAll(deviceList);

        // 初期設定を取得
        deviceCb.getSelectionModel().select(getService().getCurrentDeviceIndex());

        // 使用デバイス選択
        deviceCb.getSelectionModel().selectedIndexProperty().addListener((ov, oldVal, newVal) -> {

            getService().setDevice(newVal.intValue());

            ObservableList<String> addrs = addressCb.getItems();
//            List<String> addressList = getService().getAddressList();
            List<String> addressList = new ArrayList<String>();
            addrs.clear();
            addrs.addAll(addressList);

            addressCb.getSelectionModel().selectFirst();
            getService().setListenAddress(0);
        });

        // IPアドレス一覧を初期化
        addressCb.getItems().addAll(getService().getAddressList());

        // 初期設定を取得
        addressCb.getSelectionModel().select(getService().getCurrentListenAddressIndex());

        // ListenするIPアドレス選択
        addressCb.getSelectionModel().selectedIndexProperty().addListener((ov, oldVal, newVal) -> {

            if (newVal.intValue() != -1) {
                getService().setListenAddress(newVal.intValue());
            }
        });

        // 開始ボタン押下
        startTb.selectedProperty().set(autoRun);
        if (autoRun) {
            getService().start();
        }
        startTb.setOnAction(event -> {
            // イベントの発生元を取得
            ToggleButton toggle = (ToggleButton) event.getSource();

            if (toggle.isSelected()) {
                // 開始
                autoRun = true;

                getService().restart();
            } else {
                // 停止
                autoRun = false;

                getService().stop();
            }
        });
    }

    /**
     * お知らせ用オブジェクトの初期化.
     */
    private void initializeOshirase() {

        jfxPanel = new JFXPanel();

        oshiraseJDialog = new OshiraseJDialog(getConfig());

        oshiraseJDialog.add(jfxPanel);

        oshiraseJDialog.addLabel();

        Platform.runLater(() -> jfxPanel.setScene(new Scene(new Group())));

        // 別スレッドが持つ値とラベルの値を同期させる
        ChangeListener<String> listener = (ov, oldVal, newVal) -> oshiraseJDialog.getLabel().setText(newVal);

        getService().messageProperty().addListener(listener);

        // 初期設定取得
        boolean noticeable = getService().getLogic().isNoticeable();
        oshiraseWindow.setSelected(noticeable);
        opacitySlider.setDisable(!noticeable);
        oshiraseJDialog.setVisible(noticeable);
        //jfxPanel.setVisible(noticeable);
        oshiraseWindow.setOnAction(e -> {

            ((ChatLoggerLogic) getService().getLogic()).setNoticeable(oshiraseWindow.isSelected());

            // スライダーのenable/disable切り替え
            opacitySlider.setDisable(!oshiraseWindow.isSelected());
            oshiraseJDialog.setVisible(oshiraseWindow.isSelected());
            //jfxPanel.setVisible(oshiraseWindow.isSelected());
        });

        sakebiMenu.setSelected(((ChatLoggerLogic) getService().getLogic()).getSakebiChatLogic().isNoticeable());
        sakebiMenu.setOnAction(e -> ((ChatLoggerLogic) getService().getLogic()).getSakebiChatLogic().setEnabled(sakebiMenu.isSelected()));

        normalMenu.setSelected(((ChatLoggerLogic) getService().getLogic()).getNormalChatLogic().isNoticeable());
        normalMenu.setOnAction(e -> ((ChatLoggerLogic) getService().getLogic()).getNormalChatLogic().setEnabled(normalMenu.isSelected()));

        partyMenu.setSelected(((ChatLoggerLogic) getService().getLogic()).getPartyChatLogic().isNoticeable());
        partyMenu.setOnAction(e -> ((ChatLoggerLogic) getService().getLogic()).getPartyChatLogic().setEnabled(partyMenu.isSelected()));

        guildMenu.setSelected(((ChatLoggerLogic) getService().getLogic()).getGuildChatLogic().isNoticeable());
        guildMenu.setOnAction(e -> ((ChatLoggerLogic) getService().getLogic()).getGuildChatLogic().setEnabled(guildMenu.isSelected()));

        mimiMenu.setSelected(((ChatLoggerLogic) getService().getLogic()).getMimiChatLogic().isNoticeable());
        mimiMenu.setOnAction(e -> ((ChatLoggerLogic) getService().getLogic()).getMimiChatLogic().setEnabled(mimiMenu.isSelected()));

        // クリアボタン押下
        clearButton.setOnAction(event -> {

            sakebiChatTable.getItems().clear();
            normalChatTable.getItems().clear();
            partyChatTable.getItems().clear();
            guildChatTable.getItems().clear();
            mimiChatTable.getItems().clear();
            
            numOfUniqueLabel.setText("0");
            numOfKillMobLabel.setText("0");
            numOfGold.setText("0");
            
        });

        // お知らせ表示行数
        SingleSelectionModel<Integer> selectionModel = oshiraseLineCountCb.getSelectionModel();
        selectionModel.select(((ChatLoggerLogic) getService().getLogic()).getOshiraseLineCount() - 1);
        selectionModel.selectedIndexProperty().addListener((ov, oldVal, newVal) -> ((ChatLoggerLogic) getService().getLogic()).setOshiraseLineCount(newVal.intValue() + 1));

        // お知らせウインドウの透明度
        opacitySlider.setMin(0.0d);
        opacitySlider.setMax(1.0d);
        opacitySlider.setValue((double) oshiraseJDialog.getOpacity());
        opacitySlider.valueProperty().addListener((ov, oldVal, newVal) -> oshiraseJDialog.setOpacity(newVal.floatValue()));
    }

    /**
     * チャート初期化.
     */
    private void initializeChart() {

        areaChart.getXAxis().setLabel("起動時からの返却総数");
        areaChart.getYAxis().setLabel("各勢力への返却数");

        XYChart.Series<Number, Number> seriesTenjo = new XYChart.Series<Number, Number>();
        seriesTenjo.setName("天上");

        XYChart.Series<Number, Number> seriesChika = new XYChart.Series<Number, Number>();
        seriesChika.setName("地下");


        XYChart.Series<Number, Number> seriesAkuma = new XYChart.Series<Number, Number>();
        seriesAkuma.setName("悪魔");

        areaChart.getData().add(seriesTenjo);
        areaChart.getData().add(seriesChika);
        areaChart.getData().add(seriesAkuma);

        prepareTimeline();
    }

    /**
     * チャートの更新アニメーションを定義する.
     */
    private void prepareTimeline() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                addDataToSeries();
            }
        }.start();
    }

    /**
     * 別スレッドで更新されているデータを取得し、チャートにデータを追加する.
     */
    private void addDataToSeries() {

        RedstoneLogic redstone = ((ChatLoggerLogic) getService().getLogic()).getRedstoneLogic();

        if (redstone.getChartDataQ().isEmpty()) {
            return;
        }

        RedStoneChartBean bean = redstone.getChartDataQ().remove();

        areaChart.getData().get(0).getData().add(bean.toTenjoData());
        areaChart.getData().get(1).getData().add(bean.toChikaData());
        areaChart.getData().get(2).getData().add(bean.toAkumaData());

        areaChart.getData().get(0).setName("天上(" + bean.getCurrentTenjoCount() + ")");
        areaChart.getData().get(1).setName("地下(" + bean.getCurrentChikaCount() + ")");
        areaChart.getData().get(2).setName("悪魔(" + bean.getCurrentAkumaCount() + ")");
    }

    @Override
    public void loadConfig() {

        super.loadConfig();

        this.loadConfig(getConfig());
    }

    @Override
    public void saveConfig() {

        this.saveConfig(getConfig());

        double w = deviceCb.getScene().getWindow().getWidth();
        double h = deviceCb.getScene().getWindow().getHeight();

        setStageWidth(w);
        setStageHeight(h);

        super.saveConfig();
    }

    @Override
    public void loadConfig(Properties config) {

        // インスタンス生成時に設定を読み込む為不要
        //oshiraseJDialog.loadConfig(config);

        autoRun = Boolean.parseBoolean(config.getProperty("autorun", "false"));
    }

    @Override
    public void saveConfig(Properties config) {

        oshiraseJDialog.saveConfig(config);

//        config.setProperty("autorun", Boolean.toString(autoRun));
    }

    /**
     * getter.
     *
     * @return oshiraseJDialog
     */
    public OshiraseJDialog getOshiraseJDialog() {
        return oshiraseJDialog;
    }

    /**
     * ステージにイベントを追加する.
     *
     * @param stage ステージ
     */
    public void setUpStage(Stage stage) {

        // 閉じた時 Swing on JavaFXな場合Application.stop()が呼ばれない

        stage.setOnCloseRequest(arg0 -> oshiraseJDialog.dispose());
    }

    /**
     * Application.stopが呼び出された際にコールされる処理.
     */
    public void stop() {

        double w = deviceCb.getScene().getWindow().getWidth();
        double h = deviceCb.getScene().getWindow().getHeight();

        setStageWidth(w);
        setStageHeight(h);

        this.saveConfig();

        getService().stop();
    }

    /**
     * チャット関連のテーブルについて、コンテキストメニューを設定する.
     *
     * @param tableView
     */
    private void setUpContextMenu(final TableView<ChatTable> tableView) {

        ContextMenu menu = new ContextMenu();
        MenuItem item1 = new MenuItem("発言者をコピー");
        item1.setOnAction(event -> {
            ChatTable table = tableView.getSelectionModel().getSelectedItem();

            final Clipboard cb = Clipboard.getSystemClipboard();

            final ClipboardContent c = new ClipboardContent();

            c.putString(table.getName());

            cb.setContent(c);

            event.consume();
        });
        menu.getItems().addAll(item1);

        MenuItem item2 = new MenuItem("発言内容をコピー");
        item2.setOnAction(event -> {
            ChatTable table = tableView.getSelectionModel().getSelectedItem();

            final Clipboard cb = Clipboard.getSystemClipboard();

            final ClipboardContent c = new ClipboardContent();

            c.putString(table.getContent());

            cb.setContent(c);

            event.consume();
        });
        menu.getItems().addAll(item2);

        tableView.setContextMenu(menu);
    }
}
