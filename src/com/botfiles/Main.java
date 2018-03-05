package com.botfiles;

import com.Location.Location;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class Main {

    static Vector vecLocationList = new Vector();
    static Vector vecWeeklyLocationList = new Vector();
    static String dayOfWeek = null;
    static int runTimes = 0;
    static int botCount = 0;

    public static void main(String[] args) {

        try {

            runTimes = runTimes + 1;
            System.out.println("runTimes: " + runTimes);

            boolean isReset = weeklyLocationListReset("./src/com/locationtextfiles/WeeklyLocationList.txt");

            fileReader("./src/com/locationtextfiles/LocationList.txt", "vecLocationList");

            if (!isReset) {
                fileReader("./src/com/locationtextfiles/WeeklyLocationList.txt", "vecWeeklyLocationList");
            }

            ApiContextInitializer.init();

            TelegramBotsApi telegramBotsApi  = new TelegramBotsApi();
            Bot bot = new Bot(vecLocationList, vecWeeklyLocationList);

            telegramBotsApi.registerBot(bot);

        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }

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

}
