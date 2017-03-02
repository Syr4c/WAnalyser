package de.chris.wanalyser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Projekt: WAnalyser
 * Package: de.chris.wanalyser
 * Erstellt von cthiele am 28.02.2017 um 13:06.
 */

public class WAParser {
    private final String filePath;
    private WAChat chatFile;

    /* Alter Pattern */
    /* Pattern zum parsen vom Datum aus der aktuellen Zeile */
    //Pattern datePattern = Pattern.compile("[\\d]{2}[.][\\d]{2}[.][\\d]{4}");
    /* Pattern zum parsen von der aktuellen Uhrzeit (aktuell keine Verwendung */
    //Pattern clockPattern = Pattern.compile("[\\d]{2}[:]?[\\d]{2}[:]?[\\d]{2}");
    /* Pattern zum parsen vom Namen aus der aktuellen Zeile in Verbindung mit der Uhrzeit um ein falsches parsen aus dem Text zu verhindern */
    //Pattern clockNamePattern = Pattern.compile("[\\d]{2}[:]?[\\d]{2}[:]?[\\d]{2}[:][\\p{Blank}][\\p{Alpha}]+[:]?");
    /* Pattern zum parsen des Namens aus aus der geparsten Uhrzeit + Name */
    //Pattern namePattern = Pattern.compile("[\\p{Alpha}]+");

    /* Neue Pattern */
    /* Pattern zum parsen vom Datum aus der aktuellen Zeile */
    Pattern datePattern = Pattern.compile("[\\d]{2}[.][\\d]{2}[.][\\d]{2}");
    /* Pattern zum parsen von der aktuellen Uhrzeit (aktuell keine Verwendung */
    Pattern clockPattern = Pattern.compile("[\\d]{2}[:]?[\\d]{2}");
    /* Pattern zum parsen vom Namen aus der aktuellen Zeile in Verbindung mit der Uhrzeit um ein falsches parsen aus dem Text zu verhindern */
    Pattern clockNamePattern = Pattern.compile("[\\d]{2}[:]?[\\d]{2}[\\p{Blank}][-][\\p{Blank}][\\p{Alpha}]+[:]?");
    /* Pattern zum parsen des Namens aus aus der geparsten Uhrzeit + Name */
    Pattern namePattern = Pattern.compile("[\\p{Alpha}]+");


    public WAParser(String filePath){
        this.filePath = filePath;
        parseFileToWAChat();
    }

    public WAChat getWAChat(){
        return chatFile;
    }

    private void parseFileToWAChat(){
        ArrayList<String> chatAsArray = new ArrayList<String>();
        String firstChatPartner = null;
        String secondChatPartner = null;
        Boolean chatStartDateSet = false;
        LocalDate chatStartDate = null;

        try(BufferedReader br = new BufferedReader(new FileReader(filePath))){
            String currentLine;
            while((currentLine = br.readLine()) != null){
                chatAsArray.add(currentLine);
            }
        } catch(IOException e) {
            System.out.println("Can't read chat file at " + filePath);
            e.printStackTrace();
        }

        /* Clean up chatAsArray first line - pretty ugly hard coded */
        chatAsArray.remove(0);

        /* Clean up chatAsArray to remove unparseable elements
        ArrayList<String> linesToCleanUp = new ArrayList<String>();

        for(String line : chatAsArray){
            String[] lineAttrArr = getLineAttributes(line);
            if(lineAttrArr[0] == null && lineAttrArr[1] == null){
                linesToCleanUp.add(line);
            }
        }


        for(String line : linesToCleanUp){
            chatAsArray.remove(line);
        } */

        /* Get the names of the chat partners */
        while(firstChatPartner == null || secondChatPartner == null){
            for(String line : chatAsArray){
                String[] lineAttrArr = getLineAttributes(line);
                String potentiallName = lineAttrArr[1];

                if(firstChatPartner == null && potentiallName != null){
                    firstChatPartner = potentiallName;
                }

                if(firstChatPartner != null && !(firstChatPartner.equals(potentiallName))){
                    secondChatPartner = potentiallName;
                }
            }
        }

        HashMap<LocalDate, HashMap<String, Integer>> pairMapDates = new HashMap<>();
        LocalDate chatEndDate = null;

        /* Count Chat Days*/
        for(String line : chatAsArray){
            String[] lineAttrArr = getLineAttributes(line);
            String dateArrayString = lineAttrArr[0];

            /* check if dateArrayString is not null - could happend if WhatsApp groups a chat message in two lines and the second part doesn't contains a date */
            if(dateArrayString != null){
                String[] dateArray = dateArrayString.split("[.]");
                Integer year = Integer.valueOf(dateArray[2]) + 2000;
                Integer month = Integer.valueOf(dateArray[1]);
                Integer day = Integer.valueOf(dateArray[0]);

                LocalDate tempLocaleDate = LocalDate.of(year, month, day);

                if(pairMapDates.containsKey(tempLocaleDate)){
                    if(pairMapDates.get(tempLocaleDate).containsKey(lineAttrArr[1])){
                        Integer messages = pairMapDates.get(tempLocaleDate).get(lineAttrArr[1]);
                        messages = messages + 1;
                        pairMapDates.get(tempLocaleDate).put(lineAttrArr[1], messages);
                    } else {
                        pairMapDates.get(tempLocaleDate).put(lineAttrArr[1], 1);
                    }
                } else {
                    pairMapDates.put(tempLocaleDate, new HashMap<String, Integer>());
                    pairMapDates.get(tempLocaleDate).put(lineAttrArr[1], 1);
                }

                if(!chatStartDateSet){
                    chatStartDate = tempLocaleDate;
                    chatStartDateSet = true;
                }

                chatEndDate = tempLocaleDate;
            }
        }

        LocalDate tempLocalDate = chatStartDate;

        /* fill up empty days between */
        while(!(tempLocalDate.isEqual(chatEndDate))){
            if(!(pairMapDates.containsKey(tempLocalDate))){
                pairMapDates.put(tempLocalDate, new HashMap<String, Integer>());
                pairMapDates.get(tempLocalDate).put(firstChatPartner, 0);
                pairMapDates.get(tempLocalDate).put(secondChatPartner, 0);
            }
            tempLocalDate = tempLocalDate.plusDays(1);
        }


        /* Create WAChat Object */
        chatFile = new WAChat(firstChatPartner, secondChatPartner, pairMapDates);
        System.out.println("chat object successfully created.");
    }

    private String[] getLineAttributes(String line){
        String[] returnArray = new String[2];

        Matcher matcherDate = datePattern.matcher(line);
        Matcher matcherNameClock = clockNamePattern.matcher(line);
        Matcher matcherName;

        String nameClock;

        if(matcherDate.find()){
            returnArray[0] = matcherDate.group();
        }

        if(matcherNameClock.find()){
            nameClock = matcherNameClock.group();
            matcherName = namePattern.matcher(nameClock);

            if(matcherName.find()){
                returnArray[1] = matcherName.group();
            }
        }

        return returnArray;
    }
}
