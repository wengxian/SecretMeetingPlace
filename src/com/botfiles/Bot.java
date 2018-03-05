package com.botfiles;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import com.Location.Location;

public class Bot extends TelegramLongPollingBot {

    static Vector vecLocationList = new Vector();
    static Vector vecWeeklyLocationList = new Vector();
    static Location lunchLocation = new Location();
    static String noOfPeople = null;
    static String isRaining = null;
    static String dayOfWeek = null;

    @Override
    public void onUpdateReceived(Update update) {

        SendMessage sendMessage = new SendMessage().setChatId(update.getMessage().getChatId());

        String request = update.getMessage().getText();
        String response = update.getMessage().getText();

        if (null != request) {

            response = "";
            System.out.println("request from " + update.getMessage().getFrom().getFirstName() + ":" + update.getMessage().getText());

            boolean isReset = weeklyLocationListReset("./src/com/locationtextfiles/WeeklyLocationList.txt");

            fileReader("./src/com/locationtextfiles/LocationList.txt", "vecLocationList");

            if (!isReset) {
                fileReader("./src/com/locationtextfiles/WeeklyLocationList.txt", "vecWeeklyLocationList");
            }

            if ("new".equalsIgnoreCase(request)) {

                if (getLunchLocation()) {

                    response += "Location Name           => " + lunchLocation.getLocationName() + "\n";
                    response += "Is reachable when rain? => " + lunchLocation.getIsReachableWhenRain() + "\n";
                    response += "Is air-con place?       => " + lunchLocation.getIsAirCon() + "\n";
                    response += "Distance from MOM       => " + lunchLocation.getDistanceFromMom() + "\n";
                    response += "Maximum No of People    => " + lunchLocation.getNoOfPeople() + "\n";

                }

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

    private static boolean weeklyLocationListReset(String fileName) {

        boolean isReset = false;
        BufferedWriter bufferedWriter = null;

        try {

            Date today = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("EEE");
            dayOfWeek = formatter.format(today).toUpperCase();

            if (dayOfWeek.indexOf("MON") >= 0) {

                isReset = true;
                System.out.println("Today is " + dayOfWeek + ". WeeklyLocationList.txt will be reset.");

                bufferedWriter = new BufferedWriter(new FileWriter(fileName));
                bufferedWriter.write("\n");

            }

        } catch (Exception e) {

            e.printStackTrace();
            System.out.println("Exception occurred at weeklyLocationListReset().");
            System.out.println("Program will exit with exit code 1 now.");
            System.exit(1);

        } finally {

            try {

                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }

            } catch (Exception e) {

                e.printStackTrace();
                System.out.println("Exception occurred at weeklyLocationListReset() finally clause.");
                System.out.println("Program will exit with exit code 1 now.");
                System.exit(1);

            }

        }

        return isReset;

    }

    private static void fileReader(String fileName, String vecToAdd) {

        BufferedReader reader = null;

        try {

            reader = new BufferedReader(new FileReader(fileName));
            String line = null;

            while ((line = reader.readLine()) != null) {

                Location location = new Location();
                String[] temp = line.split("\\:");

                location.setLocationName(temp[0]);
                location.setIsReachableWhenRain(temp[1]);
                location.setIsAirCon(temp[2]);
                location.setDistanceFromMom(temp[3]);
                location.setNoOfPeople(Integer.parseInt(temp[4]));

                if ("vecLocationList".equalsIgnoreCase(vecToAdd)) {
                    vecLocationList.add(location);
                } else if ("vecWeeklyLocationList".equalsIgnoreCase(vecToAdd)) {
                    vecWeeklyLocationList.add(location);
                }

            }

        } catch (Exception e) {

            e.printStackTrace();
            System.out.println("Exception occurred at fileReader().");
            System.out.println("Program will exit with exit code 1 now.");
            System.exit(1);

        } finally {

            try {

                if (reader != null) {
                    reader.close();
                }

            } catch (Exception e) {

                e.printStackTrace();
                System.out.println("Exception occurred at fileReader() finally clause.");
                System.out.println("Program will exit with exit code 1 now.");
                System.exit(1);

            }

        }

    }

    private static boolean getLunchLocation() {

        boolean validLocation = true;

        try {

            if (vecWeeklyLocationList.size() == vecLocationList.size()) {
                return false;
            }

            boolean repeat = true;

            while (repeat) {

                lunchLocation = (Location) vecLocationList.elementAt((int) (Math.random() * vecLocationList.size()));

                // System.out.println("lunchLocation.getLocationName(): " + lunchLocation.getLocationName());

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

        return validLocation;

    }

    private static void writeToWeeklyLocationList(String fileName) {

        BufferedWriter bufferedWriter = null;

        try {

            bufferedWriter = new BufferedWriter(new FileWriter(fileName, true));
            bufferedWriter.write(lunchLocation.toString() + ":[" + dayOfWeek + "]" + "\n");

        } catch (Exception e) {

            e.printStackTrace();
            System.out.println("Exception occurred at writeToWeeklyLocationList().");
            System.out.println("Program will exit with exit code 1 now.");
            System.exit(1);

        } finally {

            try {

                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }

            } catch (Exception e) {

                e.printStackTrace();
                System.out.println("Exception occurred at writeToWeeklyLocationList() finally clause.");
                System.out.println("Program will exit with exit code 1 now.");
                System.exit(1);

            }

        }

    }

}
