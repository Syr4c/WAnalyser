package de.chris.wanalyser;/**
 * Projekt: WAnalyser
 * Package: de.chris.wanalyser
 * Erstellt von cthiele am 02.03.2017 um 09:38.
 */

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.time.LocalDate;

public class WAnalyserFX extends Application {
    @Override
    public void start(Stage primaryStage) {
        BorderPane mainPane = (BorderPane) FXMLLoader.load(Main)

        WAnalyser waAnalyser = new WAnalyser("/home/chris/Dokumente/WAnalyser/Jacky.txt");

        primaryStage.setTitle("WAnalyser r01");

        LineChart<String, Number> lineChart = drawLineChart(waAnalyser);

        Scene scene  = new Scene(lineChart,1400,800);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     *
     * @param waAnalyser
     * @return scene object to integrate in a stage object
     */
    private LineChart<String, Number> drawLineChart(WAnalyser waAnalyser){
        ObservableList<String> categories = FXCollections.observableArrayList();

        for(WAChat.WADay currentDate : waAnalyser.getWaChat().getDays()){
            categories.add(currentDate.getDate().toString());
        }

        final CategoryAxis xAxis = new CategoryAxis(categories);
        final NumberAxis yAxis = new NumberAxis(0, (waAnalyser.getWaChat().getMaxMessagesPerDay() + 15) , 1);

        xAxis.setLabel("Days");
        yAxis.setLabel("Messages");

        //creating the chart
        final LineChart<String,Number> lineChart = new LineChart<String,Number>(xAxis,yAxis);

        lineChart.setTitle(waAnalyser.getWaChat().getFirstChatPartner() + " " + waAnalyser.getWaChat().getSecondChatPartner() + " WAChat");

        XYChart.Series<String, Number> seriesFirstChatPartner = new XYChart.Series<String, Number>();
        XYChart.Series<String, Number> seriesSecondChatPartner = new XYChart.Series<String, Number>();
        XYChart.Series<String, Number> seriesAverageMessages = new XYChart.Series<String, Number>();

        seriesFirstChatPartner.setName(waAnalyser.getWaChat().getFirstChatPartner());
        seriesSecondChatPartner.setName(waAnalyser.getWaChat().getSecondChatPartner());


        for(WAChat.WADay day : waAnalyser.getWaChat().getDays()){

            LocalDate currentDate = day.getDate();
            Integer messagesFirstChatPartner = day.getFirstPartnerMessages();
            Integer messagesSecondChatPartner = day.getSecondPartnerMessages();
            Float averageMessages = day.getAverageMessages();

            seriesFirstChatPartner.getData().add(new XYChart.Data<String, Number>(currentDate.toString(), messagesFirstChatPartner));
            seriesSecondChatPartner.getData().add(new XYChart.Data<String, Number>(currentDate.toString(), messagesSecondChatPartner));
            seriesAverageMessages.getData().add(new XYChart.Data<String, Number>(currentDate.toString(), averageMessages));

        }

        System.out.println(waAnalyser.getWaChat().getDayWithMaxMessages().toString());

        //lineChart.getData().addAll(seriesFirstChatPartner, seriesSecondChatPartner, seriesAverageMessages);
        lineChart.getData().addAll(seriesFirstChatPartner, seriesSecondChatPartner);

        return lineChart;
    }

    public static void main(String[] args){
        launch(args);
    }

}
