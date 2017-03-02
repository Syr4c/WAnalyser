package de.chris.wanalyser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
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
    private String firstChatPartner = null;
    private String secondChatPartner = null;
    private ArrayList<String> chatAsArray = new ArrayList<String>();

    /* Neue Pattern */
    /* Pattern zum parsen vom Datum aus der aktuellen Zeile */
    private final Pattern datePattern = Pattern.compile("[\\d]{2}[.][\\d]{2}[.][\\d]{2}");
    /* Pattern zum parsen von der aktuellen Uhrzeit (aktuell keine Verwendung */
    private final Pattern clockPattern = Pattern.compile("[\\d]{2}[:]?[\\d]{2}");
    /* Pattern zum parsen vom Namen aus der aktuellen Zeile in Verbindung mit der Uhrzeit um ein falsches parsen aus dem Text zu verhindern */
    private final Pattern clockNamePattern = Pattern.compile("[\\d]{2}[:]?[\\d]{2}[\\p{Blank}][-][\\p{Blank}][\\p{Alpha}]+[:]?");
    /* Pattern zum parsen des Namens aus aus der geparsten Uhrzeit + Name */
    private final Pattern namePattern = Pattern.compile("[\\p{Alpha}]+");

    /* Alter Pattern */
    /* Pattern zum parsen vom Datum aus der aktuellen Zeile */
    //Pattern datePattern = Pattern.compile("[\\d]{2}[.][\\d]{2}[.][\\d]{4}");
    /* Pattern zum parsen von der aktuellen Uhrzeit (aktuell keine Verwendung */
    //Pattern clockPattern = Pattern.compile("[\\d]{2}[:]?[\\d]{2}[:]?[\\d]{2}");
    /* Pattern zum parsen vom Namen aus der aktuellen Zeile in Verbindung mit der Uhrzeit um ein falsches parsen aus dem Text zu verhindern */
    //Pattern clockNamePattern = Pattern.compile("[\\d]{2}[:]?[\\d]{2}[:]?[\\d]{2}[:][\\p{Blank}][\\p{Alpha}]+[:]?");
    /* Pattern zum parsen des Namens aus aus der geparsten Uhrzeit + Name */
    //Pattern namePattern = Pattern.compile("[\\p{Alpha}]+");

    public WAParser(String filePath){
        this.filePath = filePath;
        parseFileToWAChat();
    }

    /**
     * Method opens the chat.txt file and starts the parse cascade.
     */
    private void parseFileToWAChat(){

        try(BufferedReader br = new BufferedReader(new FileReader(filePath))){
            String currentLine;
            while((currentLine = br.readLine()) != null){
                this.chatAsArray.add(currentLine);
            }
        } catch(IOException e) {
            System.out.println("Can't read chat file at " + filePath);
            e.printStackTrace();
        }

        cleanUpChat();
        getChatPartners();
    }

    /**
     * Method to cleanup the 'chatAsArray' Array, remove lines which arent containing any chat information.
     */
    private void cleanUpChat(){
        /* Clean up chatAsArray first line - pretty ugly hard coded */
        this.chatAsArray.remove(0);

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

        getChatPartners();
    }

    /**
     * Method to parse out the chat partners by iterate over the array.
     */
    private void getChatPartners(){

        while(this.firstChatPartner == null || this.secondChatPartner == null){
            for(String line : this.chatAsArray){
                String[] lineAttrArr = getLineAttributes(line);
                String potentiallName = lineAttrArr[1];

                if(this.firstChatPartner == null && potentiallName != null){
                    this.firstChatPartner = potentiallName;
                }

                if(this.firstChatPartner != null && !(this.firstChatPartner.equals(potentiallName))){
                    this.secondChatPartner = potentiallName;
                }
            }
        }

        countMessagesPerDay();
    }

    /**
     * Method iterates over the array and counts the written messages per day. Stores them into a HashMap with the date as key.
     * The Value is another HashMap storing the name as key and the messages per day as value.
     */
    private void countMessagesPerDay(){
        Boolean chatStartDateSet = false;
        LocalDate chatStartDate = null;

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

            fillUpEmptyDays(chatStartDate, chatEndDate, pairMapDates);
        }
    }

    /**
     * @param chatStartDate
     * @param chatEndDate
     * @param pairMapDates
     *
     * Method takes the Start and End Date of a conversation to fill up the days in between with 0 : 0 text messages written.
     */
    private void fillUpEmptyDays(LocalDate chatStartDate, LocalDate chatEndDate, HashMap<LocalDate, HashMap<String, Integer>> pairMapDates){
        LocalDate tempLocalDate = chatStartDate;

        while(!(tempLocalDate.isEqual(chatEndDate))){
            if(!(pairMapDates.containsKey(tempLocalDate))){
                pairMapDates.put(tempLocalDate, new HashMap<String, Integer>());
                pairMapDates.get(tempLocalDate).put(firstChatPartner, 0);
                pairMapDates.get(tempLocalDate).put(secondChatPartner, 0);
            }
            tempLocalDate = tempLocalDate.plusDays(1);
        }

        createWAChat(pairMapDates);
    }

    /**
     * @param pairMapDates
     *
     * Method creates the final WAChat object.
     */
    private void createWAChat(HashMap<LocalDate, HashMap<String, Integer>> pairMapDates){
        chatFile = new WAChat(this.firstChatPartner, this.secondChatPartner, pairMapDates);
        System.out.println("chat object successfully created.");
    }

    /**
     * @param line
     * @return String[2] = {{DATE}, {NAME}}
     *
     * Helper Method to reduce the given line to its meta information such as date and name.
     */
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


    public WAChat getWAChat(){
        return chatFile;
    }

}
