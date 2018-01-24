package spatial.crowdsourcing.galsalgorithm.shell;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import spatial.crowdsourcing.galsalgorithm.model.CrowdSourceStore;
import spatial.crowdsourcing.galsalgorithm.model.Rejection;
import spatial.crowdsourcing.galsalgorithm.model.SpatialData;
import spatial.crowdsourcing.galsalgorithm.repositories.CrowdSourceStoreRepository;
import spatial.crowdsourcing.galsalgorithm.repositories.RejectionRepository;
import spatial.crowdsourcing.galsalgorithm.repositories.SpatialDataRepository;
import spatial.crowdsourcing.galsalgorithm.shell.helper.WorkerCapacity;
import spatial.crowdsourcing.galsalgorithm.shell.helper.WorkerTaskDistance;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@ShellComponent
public class TaskCommands {
    @Autowired
    SpatialDataRepository spatialDataRepository;
  @Autowired
  CrowdSourceStoreRepository mCrowdSourceStoreRepository;
  @Autowired
  RejectionRepository mRejectionRepository;

  @ShellMethod("Show number of distinct of workers and tasks")
  public String getStatistics(
    ){
        String result="";
        List<Long> taskList = spatialDataRepository.findDistinctByLocationId();
        List<Long> userList = spatialDataRepository.findDistinctUserList();
        return "task :"+taskList.size()+" and worker: "+userList.size();
    }

    @ShellMethod("Generate status")
   public String generateStatus(
           @ShellOption(help="worker number") int workerNumber
    ){
      List<Long> userIds = spatialDataRepository.findDistinctUserList().subList(0, workerNumber);
      List<SpatialData> spatialData = spatialDataRepository.findByUserIdIn(userIds);
      spatialData.sort((s1, s2) -> s1.getCheckInTimes().compareTo(s2.getCheckInTimes()));
      Set<String> distinctDates = spatialData.parallelStream().map(s -> new SimpleDateFormat("dd-MM-yyyy").format(s.getCheckInTimes())).collect(Collectors.toSet());
      Map<String, List<SpatialData>> userAndDateMap = new HashMap<>();
      Map<String, List<SpatialData>> dateMap = new HashMap<>();
      spatialData.forEach(s -> createUserAndDateMap(userAndDateMap, dateMap, s));
      mCrowdSourceStoreRepository.deleteAll();
      distinctDates.forEach(date -> {
        createDateBasedCrowdSource(userAndDateMap, dateMap, date);
      });
      return spatialData.size() + "";
   }

  private void createDateBasedCrowdSource(Map<String, List<SpatialData>> pUserAndDateMap, Map<String, List<SpatialData>> pDateMap, String date) {
    System.out.println("For date--->" + date);
    List<SpatialData> dataSpatialDataList = pDateMap.get(date);
    List<WorkerCapacity> workerCapacityList = getUserCapacityOfTheDay(pUserAndDateMap, date, dataSpatialDataList);
    List<Integer> capacity = workerCapacityList.stream().map(s -> s.getCapacity()).collect(Collectors.toList());
    capacity.sort((c1, c2) -> c2.compareTo(c1));
    Map<Integer, Long> workerCapacityMap = new HashMap<>();
    workerCapacityList.forEach(w -> workerCapacityMap.put(w.getCapacity(), w.getWorkerId()));
    Map<Long, SpatialData> locationMap = new HashMap<>();
    dataSpatialDataList.forEach(d -> {
      if (!locationMap.containsKey(d.getLocationId()))
        locationMap.put(d.getLocationId(), d);
    });
    List<CrowdSourceStore> crowdSourceStoreList = new ArrayList<>();
    capacity.forEach(c -> {
      storeCrowdSource(date, dataSpatialDataList, workerCapacityMap, locationMap, crowdSourceStoreList, c);
    });
    mCrowdSourceStoreRepository.save(crowdSourceStoreList);

  }

  private void storeCrowdSource(String date, List<SpatialData> pDataSpatialDataList, Map<Integer, Long> pWorkerCapacityMap, Map<Long, SpatialData> pLocationMap, List<CrowdSourceStore> pCrowdSourceStoreList, Integer c) {
    SpatialData workerInitialLocation = pDataSpatialDataList.remove(0);
    pLocationMap.remove(workerInitialLocation.getLocationId());
    for (int i = 0; i < c; i++) {
      if (pLocationMap.isEmpty())
        break;
      List<WorkerTaskDistance> workerTaskDistances = new ArrayList<>();
      getDistanceFromLocation(pLocationMap, workerInitialLocation, workerTaskDistances, date, pWorkerCapacityMap.get(c));
      workerTaskDistances.sort((w1, w2) -> w1.getDistance().compareTo(w2.getDistance()));
      if (workerTaskDistances.size() == 0)
        break;
      workerInitialLocation = pLocationMap.get(workerTaskDistances.get(0).getLocationId());
      CrowdSourceStore crowdSourceStore = new CrowdSourceStore();
      crowdSourceStore.setDate(date);
      crowdSourceStore.setUserId(pWorkerCapacityMap.get(c));
      crowdSourceStore.setTaskId(workerInitialLocation.getLocationId());
      pCrowdSourceStoreList.add(crowdSourceStore);
      pLocationMap.remove(workerTaskDistances.get(0).getLocationId());
    }
  }

  private void getDistanceFromLocation(Map<Long, SpatialData> pLocationMap, SpatialData pWorkerInitialLocation, List<WorkerTaskDistance> pWorkerTaskDistances, String pDate, Long userId) {
    for (Map.Entry<Long, SpatialData> entry : pLocationMap.entrySet()) {
      SpatialData spatialData = pLocationMap.get(entry.getKey());
      double latDistance = Math.toRadians(spatialData.getLatitude() - pWorkerInitialLocation.getLatitude());
      double longDistance = Math.toRadians(spatialData.getLongitude() - pWorkerInitialLocation.getLongitude());
      Double distance = distance(pWorkerInitialLocation.getLatitude(), pWorkerInitialLocation.getLongitude(), spatialData.getLatitude(), spatialData.getLongitude(), 'N');
      if (spatialData.getCheckInTimes().after(pWorkerInitialLocation.getCheckInTimes()))
        pWorkerTaskDistances.add(new WorkerTaskDistance(pWorkerInitialLocation.getUserId(), spatialData.getLocationId(), distance));
      else {
        if (spatialData.getLocationId() != null)
          mRejectionRepository.save(new Rejection(pDate, userId, spatialData.getLocationId()));
      }
    }
  }

  private List<WorkerCapacity> getUserCapacityOfTheDay(Map<String, List<SpatialData>> pUserAndDateMap, String date, List<SpatialData> pDataSpatialDataList) {
    List<WorkerCapacity> pWorkerCapacityList = new ArrayList<>();
    pDataSpatialDataList.forEach(s -> {
      WorkerCapacity workerCapacity = new WorkerCapacity();
      workerCapacity.setWorkerId(s.getUserId());
      workerCapacity.setCapacity(pUserAndDateMap.get(date + s.getUserId()).size());
      pWorkerCapacityList.add(workerCapacity);
    });
    return pWorkerCapacityList;
  }

  private void createUserAndDateMap(Map<String, List<SpatialData>> pUserAndDateMap, Map<String, List<SpatialData>> pDateMap, SpatialData spatialData) {
    String dateAndUserKey = new SimpleDateFormat("dd-MM-yyyy").format(spatialData.getCheckInTimes()) + spatialData.getUserId();
    String dateKey = new SimpleDateFormat("dd-MM-yyyy").format(spatialData.getCheckInTimes());
    List<SpatialData> spatialDataListForUserAndDate = new ArrayList<>();
    List<SpatialData> spatialDataListForDate = new ArrayList<>();
    createOrUpdateMap(pUserAndDateMap, spatialData, dateAndUserKey, spatialDataListForUserAndDate);
    createOrUpdateMap(pDateMap, spatialData, dateKey, spatialDataListForDate);
  }

  private void createOrUpdateMap(Map<String, List<SpatialData>> pUserAndDateMap, SpatialData spatialData, String pDateAndUserKey, List<SpatialData> pSpatialDataListForUserAndDate) {
    if (pUserAndDateMap.containsKey(pDateAndUserKey)) {
      pSpatialDataListForUserAndDate = pUserAndDateMap.get(pDateAndUserKey);
      pSpatialDataListForUserAndDate.add(spatialData);
      pUserAndDateMap.put(pDateAndUserKey, pSpatialDataListForUserAndDate);
    } else {
      pSpatialDataListForUserAndDate.add(spatialData);
      pUserAndDateMap.put(pDateAndUserKey, pSpatialDataListForUserAndDate);
    }
  }


  private double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
    double theta = lon1 - lon2;
    double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
    dist = Math.acos(dist);
    dist = rad2deg(dist);
    dist = dist * 60 * 1.1515;
    if (unit == 'K') {
      dist = dist * 1.609344;
    } else if (unit == 'N') {
      dist = dist * 0.8684;
    }
    return (dist);
  }

  /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
  /*::  This function converts decimal degrees to radians             :*/
  /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
  private double deg2rad(double deg) {
    return (deg * Math.PI / 180.0);
  }

  /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
  /*::  This function converts radians to decimal degrees             :*/
  /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
  private double rad2deg(double rad) {
    return (rad * 180.0 / Math.PI);
  }


}
