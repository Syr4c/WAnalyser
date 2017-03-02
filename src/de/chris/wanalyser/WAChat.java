package de.chris.wanalyser;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

/**
 * Projekt: WAnalyser
 * Package: de.chris.wanalyser
 * Erstellt von cthiele am 28.02.2017 um 13:06.
 */

public class WAChat {
    private final String firstChatPartner;
    private final String secondChatPartner;
    private Integer maxMessagesPerDay = 0;
    private LocalDate dayWithMaxMessages;
    private ArrayList<WADay> days = new ArrayList<>();

    public WAChat(String firstChatPartner, String secondChatPartner, HashMap<LocalDate, HashMap<String, Integer>> chatHash){
        this.firstChatPartner = firstChatPartner;
        this.secondChatPartner = secondChatPartner;
        generateChat(chatHash);
    }

    private void generateChat(HashMap<LocalDate, HashMap<String, Integer>> chatHash){
        Set<LocalDate> dateSet = chatHash.keySet();
        for(LocalDate currentDate : dateSet){
            Integer firstChatPartnerMessages = chatHash.get(currentDate).get(firstChatPartner);
            Integer secondChatPartnerMessages = chatHash.get(currentDate).get(secondChatPartner);

            if(firstChatPartnerMessages == null) firstChatPartnerMessages = 0;
            if(secondChatPartnerMessages == null) secondChatPartnerMessages = 0;

            days.add(new WADay(currentDate, firstChatPartnerMessages, secondChatPartnerMessages));

            if(firstChatPartnerMessages > maxMessagesPerDay){
                maxMessagesPerDay = firstChatPartnerMessages;
                dayWithMaxMessages = currentDate;

            } else if(secondChatPartnerMessages > maxMessagesPerDay){
                maxMessagesPerDay = secondChatPartnerMessages;
                dayWithMaxMessages = currentDate;
            }
        }

        Collections.sort(days);
    }

    public LocalDate getDayWithMaxMessages(){
        return dayWithMaxMessages;
    }

    public Integer getMaxMessagesPerDay(){
        return maxMessagesPerDay;
    }

    public ArrayList<WADay> getDays(){
        return days;
    }

    public String getFirstChatPartner(){
        return firstChatPartner;
    }

    public String getSecondChatPartner(){
        return secondChatPartner;
    }

    public class WADay implements Comparable<WADay> {
        private final LocalDate date;
        private final Integer firstChatPartnerMessages;
        private final Integer secondChatPartnerMessages;
        private final Float averageMessages;

        public WADay(LocalDate date, Integer firstChatPartnerMessages, Integer secondChatPartnerMessages){
            this.date = date;
            this.firstChatPartnerMessages = firstChatPartnerMessages;
            this.secondChatPartnerMessages = secondChatPartnerMessages;
            this.averageMessages = ((float) firstChatPartnerMessages + (float) secondChatPartnerMessages)/2;
        }

        @Override
        public int compareTo(WADay waDay) {
            return date.compareTo(waDay.getDate());
        }

        public LocalDate getDate(){
            return date;
        }

                public Integer getFirstPartnerMessages(){
            return firstChatPartnerMessages;
        }

        public Integer getSecondPartnerMessages(){
            return secondChatPartnerMessages;
        }

        public Float getAverageMessages(){
            return averageMessages;
        }
    }
}
