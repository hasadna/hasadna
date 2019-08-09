package projects.opentrain.israel_railways_pipeline.datahandler;


import com.google.common.collect.ImmutableList;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import projects.opentrain.israel_railways_pipeline.common.RailWayProtos;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

class DataHandler {

    private static final int FILTER_DATE = 20190217;
    private static final String ISRAEL_RAILWAYS_AGENCY_ID = "2";

    private static String getDayOfToday() {
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case Calendar.SUNDAY:
                return "sunday";
            case Calendar.MONDAY:
                return "monday";
            case Calendar.TUESDAY:
                return "tuesday";
            case Calendar.WEDNESDAY:
                return "wednesday";
            case Calendar.THURSDAY:
                return "thursday";
            case Calendar.FRIDAY:
                return "friday";
            case Calendar.SATURDAY:
                return "saturday";
            default:
                return "";
        }
    }

    private static boolean isServiceIdRelevantToday(CSVRecord record) {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        int startDate = Integer.parseInt(record.get("start_date"));
        int endDate = Integer.parseInt(record.get("end_date"));

        // Uncomment to read today's data:
        // int currentDate = Integer.parseInt(dateFormat.format(new Date()));
        // return (startDate <= currentDate && currentDate <= endDate && isRecordDayTrueOnDayOfToday);
        boolean isRecordDayTrueOnDayOfToday = Integer.parseInt(record.get(getDayOfToday())) == 1;
        return (startDate <= FILTER_DATE && FILTER_DATE <= endDate && isRecordDayTrueOnDayOfToday);
    }

//  public static List<Integer> getServiceIds() {
//    List<Protos.Calendar> calendars = getCalendar(calendarsAbsPath);
//    List<Integer> serviceIds = new ArrayList<>();
//    for (Protos.Calendar calendar : calendars) {
//      serviceIds.add(calendar.getServiceId());
//    }
//    return serviceIds;
//  }
//
//  public static List<Protos.Calendar> getCalendar(Path csvPath) {
//    List<Protos.Calendar> calendars = new ArrayList<>();
//    try {
//      CSVParser parser =
//          CSVParser.parse(
//              csvPath,
//              StandardCharsets.UTF_8,
//              CSVFormat.DEFAULT.withHeader(
//                  "service_id",
//                  "sunday",
//                  "monday",
//                  "tuesday",
//                  "wednesday",
//                  "thursday",
//                  "friday",
//                  "saturday",
//                  "start_date",
//                  "end_date"));
//      boolean isHeader = true;
//      for (CSVRecord record : parser) {
//        if (isHeader) {
//          isHeader = false;
//          continue;
//        }
//        if (isServiceIdRelevantToday(record)) {
//          Protos.Calendar.Builder calendar = Protos.Calendar.newBuilder();
//
//          calendars.add(
//              calendar
//                  .setServiceId(Integer.parseInt(record.get("service_id")))
//                  .setSunday(Boolean.parseBoolean(record.get("sunday")))
//                  .setMonday(Boolean.parseBoolean(record.get("monday")))
//                  .setTuesday(Boolean.parseBoolean(record.get("tuesday")))
//                  .setWednesday(Boolean.parseBoolean(record.get("wednesday")))
//                  .setThursday(Boolean.parseBoolean(record.get("thursday")))
//                  .setFriday(Boolean.parseBoolean(record.get("friday")))
//                  .setSaturday(Boolean.parseBoolean(record.get("saturday")))
//                  .setStartDate(Integer.parseInt(record.get("start_date")))
//                  .setEndDate(Integer.parseInt(record.get("end_date")))
//                  .build());
//        }
//      }
//      return calendars;
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
//  }
//
//  public static List<Integer> getRouteIds() {
//    return getRoutes(routesAbsPath).stream().map(Route::getRouteId).collect(Collectors.toList());
//  }
//
//  public static List<Route> getRoutes(Path csvPath) {

//  }
//
//  public static List<Integer> getStopsId() {
//    return getStopTimes(stopTimesAbsPath)
//        .stream()
//        .map(StopTime::getStopId)
//        .collect(Collectors.toList());
//  }
//
//  public static List<StopTime> getStopTimes(Path csvPath) {
//    List<String> tripsId = getTripsId();
//    List<StopTime> stopsTime = new ArrayList<>();
//    try {
//      CSVParser parser =
//          CSVParser.parse(
//              csvPath,
//              StandardCharsets.UTF_8,
//              CSVFormat.DEFAULT.withHeader(
//                  "trip_id",
//                  "arrival_time",
//                  "departure_time",
//                  "stop_id",
//                  "stop_sequence",
//                  "pickup_type",
//                  "drop_off_type",
//                  "shape_dist_traveled"));
//      boolean isHeader = true;
//      for (CSVRecord record : parser) {
//        if (isHeader) {
//          isHeader = false;
//          continue;
//        }
//        if (!tripsId.contains(record.get("trip_id"))) {
//          continue;
//        }
//        StopTime.Builder stopTime = StopTime.newBuilder();
//        stopsTime.add(
//            stopTime
//                .setTripId(record.get("trip_id"))
//                .setArrivalTime(record.get("arrival_time"))
//                .setDepartureTime(record.get("departure_time"))
//                .setStopId(Integer.parseInt(record.get("stop_id")))
//                .setStopSequence(record.get("stop_sequence"))
//                .setPickupType(record.get("pickup_type"))
//                .setDropOffType(record.get("drop_off_type"))
//                .setShapeDistTraveled(record.get("shape_dist_traveled"))
//                .build());
//      }
//      return stopsTime;
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
//  }
//
//  public static List<Stop> getStops(Path csvPath) {
//    List<Integer> stopsId = getStopsId();
//    List<Stop> stops = new ArrayList<>();
//    try {
//      CSVParser parser =
//          CSVParser.parse(
//              csvPath,
//              StandardCharsets.UTF_8,
//              CSVFormat.DEFAULT.withHeader(
//                  "stop_id",
//                  "stop_code",
//                  "stop_name",
//                  "stop_desc",
//                  "stop_lat",
//                  "stop_lon",
//                  "location_type",
//                  "parent_station",
//                  "zone_id"));
//      boolean isHeader = true;
//      for (CSVRecord record : parser) {
//        if (isHeader) {
//          isHeader = false;
//          continue;
//        }
//        if (!stopsId.contains(Integer.parseInt(record.get("stop_id")))) {
//          continue;
//        }
//        Stop.Builder stop = Stop.newBuilder();
//        stops.add(
//            stop.setStopId(Integer.parseInt(record.get("stop_id")))
//                .setStopCode(Integer.parseInt(record.get("stop_code")))
//                .setStopName(record.get("stop_name"))
//                .setStopDesc(record.get("stop_desc"))
//                .setStopLat(Double.parseDouble(record.get("stop_lat")))
//                .setStopLon(Double.parseDouble(record.get("stop_lon")))
//                .setLocationType(Integer.parseInt(record.get("location_type")))
//                .setParentStation(record.get("parent_station"))
//                .setZoneId(Integer.parseInt(record.get("zone_id")))
//                .build());
//      }
//      return stops;
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
//  }
//
//  public static List<Translation> getTranslations(Path csvPath) {
//    List<Translation> translations = new ArrayList<>();
//    try {
//      CSVParser parser =
//          CSVParser.parse(
//              csvPath,
//              StandardCharsets.UTF_8,
//              CSVFormat.DEFAULT.withQuote(null).withHeader("trans_id", "lang", "translation"));
//      boolean isHeader = true;
//      for (CSVRecord record : parser) {
//        if (isHeader) {
//          isHeader = false;
//          continue;
//        }
//        Translation.Builder translation = Translation.newBuilder();
//        translations.add(
//            translation
//                .setTransId(record.get("trans_id"))
//                .setLang(record.get("lang"))
//                .setTranslation(record.get("translation"))
//                .build());
//      }
//      return translations;
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
//  }
//
//  public static List<String> getTripsId() {
//    return getTrips(tripsAbsPath).stream().map(Trip::getTripId).collect(Collectors.toList());
//  }
//
//  public static List<Trip> getTrips(Path csvPath) {
//    List<Trip> trips = new ArrayList<>();
//    List<Integer> routesId = getRouteIds();
//    List<Integer> serviceIds = getServiceIds();
//    try {
//      CSVParser parser =
//          CSVParser.parse(
//              csvPath,
//              StandardCharsets.UTF_8,
//              CSVFormat.DEFAULT.withHeader(
//                  "route_id",
//                  "service_id",
//                  "trip_id",
//                  "trip_headsign",
//                  "direction_id",
//                  "shape_id"));
//      boolean isHeader = true;
//      for (CSVRecord record : parser) {
//        if (isHeader) {
//          isHeader = false;
//          continue;
//        }
//        if (!routesId.contains(Integer.parseInt(record.get("route_id")))
//            || !serviceIds.contains(Integer.parseInt(record.get("service_id")))) {
//          continue;
//        }
//        Trip.Builder trip = Trip.newBuilder();
//        trips.add(
//            trip.setRouteId(Integer.parseInt(record.get("route_id")))
//                .setServiceId(Integer.parseInt(record.get("service_id")))
//                .setTripId(record.get("trip_id"))
//                .setTripHeadsign(record.get("trip_headsign"))
//                .setDirectionId(Integer.parseInt(record.get("direction_id")))
//                .setShapeId(record.get("shape_id"))
//                .build());
//      }
//      return trips;
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
//  }
//
//  public static void saveRoutes(){
//    try(FileOutputStream output = new FileOutputStream(calendarsProtoAbsPath)){
//      for(Protos.s)
//    }
//  }
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//


    public static List<RailWayProtos.Routes> getRoutes(Path sampleCsv) {

        List<RailWayProtos.Routes> routesList = new ArrayList<>();
        try {
            CSVParser parser =
                    CSVParser.parse(
                            sampleCsv,
                            StandardCharsets.UTF_8,
                            CSVFormat.DEFAULT.withHeader("id", "stop_ids"
                            ));
            boolean isHeader = true;
            for (CSVRecord record : parser) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }


                RailWayProtos.Routes.Builder routeBuilder = RailWayProtos.Routes.newBuilder();
                routesList.add(
                        routeBuilder
                                .setId(Integer.parseInt(record.get("id")))
                                .setStopIds(record.get("stop_ids"))
                                .build());
            }
            return routesList;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void saveRoutes(Path path, String outputName) {
        try (FileOutputStream output = new FileOutputStream(outputName)) {
            for (RailWayProtos.Routes route : getRoutes(path)) {
//                route.writeDelimitedTo(output);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


//
//  public static void saveRoutes() {
//    try (FileOutputStream output = new FileOutputStream(routesProtoAbsPath)) {
//      for (Route route : getRoutes(routesAbsPath)) {
//        route.writeDelimitedTo(output);
//      }
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
//  }
//
//  public static void saveStopTimes() {
//    try (FileOutputStream output = new FileOutputStream(stopTimesProtoAbsPath)) {
//      for (StopTime stopTime : getStopTimes(stopTimesAbsPath)) {
//        stopTime.writeDelimitedTo(output);
//      }
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
//  }
//
//  public static void saveStops() {
//    try (FileOutputStream output = new FileOutputStream(stopsProtoAbsPath)) {
//      for (Stop stop : getStops(stopsAbsPath)) {
//        stop.writeDelimitedTo(output);
//      }
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
//  }
//
//  public static void saveTranslations() {
//    try (FileOutputStream output = new FileOutputStream(translationsProtoAbsPath)) {
//      for (Translation translation : getTranslations(translationsAbsPath)) {
//        translation.writeDelimitedTo(output);
//      }
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
//  }
//
//  public static void saveTrips() {
//    try (FileOutputStream output = new FileOutputStream(tripsProtoAbsPath)) {
//      for (Trip trip : getTrips(tripsAbsPath)) {
//        trip.writeDelimitedTo(output);
//      }
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
//  }
//
//  // Paths to the CSVs files
//  static Path calendarsAbsPath;
//  static Path routesAbsPath;
//  static Path stopTimesAbsPath;
//  static Path stopsAbsPath;
//  static Path translationsAbsPath;
//  static Path tripsAbsPath;
//
//  // Paths to output files
//  static String calendarsProtoAbsPath;
//  static String routesProtoAbsPath;
//  static String stopTimesProtoAbsPath;
//  static String stopsProtoAbsPath;
//  static String translationsProtoAbsPath;
//  static String tripsProtoAbsPath;

    public static void main(String[] args) {

//    {
////      calendarsAbsPath = Paths.get(args[0]);
//      routesAbsPath = Paths.get(args[0]);
////      stopTimesAbsPath = Paths.get(args[2]);
////      stopsAbsPath = Paths.get(args[3]);
////      translationsAbsPath = Paths.get(args[4]);
////      tripsAbsPath = Paths.get(args[5]);
////
////      calendarsProtoAbsPath = args[6];
//      routesProtoAbsPath = args[1];
////      stopTimesProtoAbsPath = args[8];
////      stopsProtoAbsPath = args[9];
////      translationsProtoAbsPath = args[10];
////      tripsProtoAbsPath = args[11];
//    }
//        System.out.println(args[0]);
//        System.out.println(args[1]);
//    return;
//        getRoutes(Paths.get(args[0]));
//        System.out.println(getRoutes(Paths.get(args[0])));
        saveRoutes(Paths.get(args[0]), args[1]);
//    saveRoutes();
//    saveStopTimes();
//    saveStops();
//    saveTranslations();
//    saveTrips();
    }
}

