package de.chris.wanalyser;

import org.json.simple.JSONObject;

/**
 * Projekt: WAnalyser
 * Package: de.chris.wanalyser.resources
 * Erstellt von cthiele am 02.03.2017 um 15:56.
 *
 *
 */
public class WAJson{

    public static void writeChatToFile(WAChat chat){
        JSONObject jsonChat = new JSONObject();

        jsonChat.put("firstChatPartner", chat.getFirstChatPartner());
        jsonChat.put("secondChatPartner", chat.getSecondChatPartner());

        for(WAChat.WADay currentDay : chat.getDays()){
            JSONObject tempJson = new JSONObject();

            tempJson.put(chat.getFirstChatPartner(), currentDay.getFirstPartnerMessages());
            tempJson.put(chat.getSecondChatPartner(), currentDay.getSecondPartnerMessages());

            jsonChat.put(currentDay.getDate().toString(),tempJson);
        }

        System.out.println(jsonChat.toString());
    }
}
