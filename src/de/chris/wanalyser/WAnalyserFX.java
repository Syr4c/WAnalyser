package de.chris.wanalyser;/**
 * Projekt: WAnalyser
 * Package: de.chris.wanalyser
 * Erstellt von cthiele am 02.03.2017 um 09:38.
 */

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.time.LocalDate;

public class WAnalyserFX extends Application {
    @Override
    public void start(Stage primaryStage) {
        WAnalyser waAnalyser = new WAnalyser("D:\\eclipse neo workspace\\WAnalyser\\src\\de\\chris\\wanalyser\\resources\\Nico.txt");

        primaryStage.setTitle("WAnalyser r01");

        ObservableList<String> categories = FXCollections.observableArrayList();

        for(WAChat.WADay currentDate : waAnalyser.getWaChat().getDays()){
            categories.add(currentDate.getDate().toString());
        }

        final CategoryAxis xAxis = new CategoryAxis(categories);
        final NumberAxis yAxis = new NumberAxis(0, (waAnalyser.getWaChat().getMaxMessagesPerDay() + 2) , 1);

        xAxis.setLabel("Days");
        yAxis.setLabel("Messages");

        //creating the chart
        final LineChart<String,Number> lineChart = new LineChart<String,Number>(xAxis,yAxis);

        lineChart.setTitle(waAnalyser.getWaChat().getFirstChatPartner() + " " + waAnalyser.getWaChat().getSecondChatPartner() + " WAChat");

        XYChart.Series<String, Number> seriesFirstChatPartner = new XYChart.Series<String, Number>();
        XYChart.Series<String, Number> seriesSecondChatPartner = new XYChart.Series<String, Number>();

        seriesFirstChatPartner.setName(waAnalyser.getWaChat().getFirstChatPartner());
        seriesSecondChatPartner.setName(waAnalyser.getWaChat().getSecondChatPartner());

        System.out.println(xAxis.categorySpacingProperty());

        Integer counter = 0;
        for(WAChat.WADay day : waAnalyser.getWaChat().getDays()){
            System.out.println(counter);
            LocalDate currentDate = day.getDate();
            Integer messagesFirstChatPartner = day.getFirstPartnerMessages();
            Integer messagesSecondChatPartner = day.getSecondPartnerMessages();

            seriesFirstChatPartner.getData().add(new XYChart.Data<String, Number>(currentDate.toString(), messagesFirstChatPartner));
            seriesSecondChatPartner.getData().add(new XYChart.Data<String, Number>(currentDate.toString(), messagesSecondChatPartner));
            counter++;
        }

        Scene scene  = new Scene(lineChart,1400,800);
        lineChart.getData().addAll(seriesFirstChatPartner, seriesSecondChatPartner);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args){
        launch(args);
    }

}