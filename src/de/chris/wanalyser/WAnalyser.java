package de.chris.wanalyser;

/**
 * Projekt: WAnalyser
 * Package: de.chris.wanalyser
 * Erstellt von cthiele am 28.02.2017 um 13:06.
 */
public class WAnalyser {
    private WAParser waParser = null;
    private WAChat waChat = null;

    public WAnalyser(String path){
        this.waParser = new WAParser(path);
        this.waChat = waParser.getWAChat();
    }

    public WAChat getWaChat(){
        return waChat;
    }
}
