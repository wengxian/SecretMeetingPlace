package com.botfiles;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import com.Location.Location;
import java.util.Vector;

public class Bot extends TelegramLongPollingBot {

    static Vector vecLocationList = new Vector();
    static Vector vecWeeklyLocationList = new Vector();
    static Location lunchLocation = new Location();

    public Bot(Vector v1, Vector v2) {

        this.vecLocationList = v1;
        this.vecWeeklyLocationList = v2;

    }

    @Override
    public void onUpdateReceived(Update update) {

        SendMessage sendMessage = new SendMessage().setChatId(update.getMessage().getChatId());

        String request = update.getMessage().getText();
        String response = update.getMessage().getText();

        if (null != request) {

            response = "";
            System.out.println("request from " + update.getMessage().getFrom().getFirstName() + ":" + update.getMessage().getText());

            if ("new".equalsIgnoreCase(request)) {


                    getLunchLocation();
                    response += "Location Name           => " + lunchLocation.getLocationName() + "\n";
                    response += "Is reachable when rain? => " + lunchLocation.getIsReachableWhenRain() + "\n";
                    response += "Is air-con place?       => " + lunchLocation.getIsAirCon() + "\n";
                    response += "Distance from MOM       => " + lunchLocation.getDistanceFromMom() + "\n";
                    response += "Maximum No of People    => " + lunchLocation.getNoOfPeople() + "\n";



            } else if ("weeklylist".equalsIgnoreCase(request)) {

                for (int i = 0; i < vecLocationList.size(); i++) {
                    response += vecLocationList.elementAt(i) + "\n";
                }

            } else {
                response = "Hello " + update.getMessage().getFrom().getFirstName() + "! You have enter '" + request + "'.";
            }

        } else {
            response = "Hello " + update.getMessage().getFrom().getFirstName() + "! You have enter '" + request + "'.";
        }

        sendMessage.setText(response);

        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getBotUsername() {
        return null;
    }

    @Override
    public String getBotToken() {
        return "470276324:AAH0-6eLyFBVODUHnhuzcXqGAHBT_qMWzUY";
    }

    private static Location getLunchLocation() {

        try {

            boolean repeat = true;

            while (repeat) {

                lunchLocation = (Location) vecLocationList.elementAt((int) (Math.random() * vecLocationList.size()));

                if (vecWeeklyLocationList.size() > 0) {

                    for (int i = 0; i < vecWeeklyLocationList.size(); i++) {

                        Location temp = (Location) vecWeeklyLocationList.elementAt(i);

                        if (lunchLocation.getLocationName().equalsIgnoreCase(temp.getLocationName())) {
                            i = vecWeeklyLocationList.size();
                            repeat = true;
                        } else {
                            repeat = false;
                        }
                    }

                } else {
                    repeat = false;
                }

            }

        } catch (Exception e) {

            e.printStackTrace();
            System.out.println("Exception occurred at getLunchLocation().");
            System.out.println("Program will exit with exit code 1 now.");
            System.exit(1);

        }

        return lunchLocation;

    }

}
