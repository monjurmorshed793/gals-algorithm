package spatial.crowdsourcing.galsalgorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import spatial.crowdsourcing.galsalgorithm.model.SpatialData;
import spatial.crowdsourcing.galsalgorithm.repositories.SpatialDataRepository;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableCaching
public class GalsAlgorithmApplication {


	Logger logger = LoggerFactory.getLogger(GalsAlgorithmApplication.class);


	@Autowired
    SpatialDataRepository spatialDataRepository;
  @Autowired
  JdbcTemplate mJdbcTemplate;


	public static void main(String[] args) {
		SpringApplication.run(GalsAlgorithmApplication.class, args);
	}

	@PostConstruct
	public void init() throws Exception{
        if(spatialDataRepository.count()==0){
            readDataFromFile();
        }

        List<Long> userIds = spatialDataRepository.findDistinctUserList().subList(0,12);
        List<SpatialData> spatialData = spatialDataRepository.findByUserIdIn(userIds);
        System.out.println(spatialData.size()+"found---->"+spatialData.size());
	}

    private void readDataFromFile() throws IOException, ParseException {
        File file = new ClassPathResource("Gowalla_totalCheckins.txt").getFile();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String readline="";
        List<SpatialData> newSpatialDataList =new ArrayList<>();
      List<Object[]> splittedFileData = new ArrayList<>();
        while((readline= bufferedReader.readLine())!=null){
          String[] dataArray = readline.split("\t");

//                Timestamp timestamp = Timestamp.valueOf(dataArray[1]);
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
          Object[] objects = new Object[]{Long.parseLong(dataArray[0]), sdf.parse(dataArray[1]), Double.parseDouble(dataArray[2]), Double.parseDouble(dataArray[3]), Long.parseLong(dataArray[4])};
//          spatialData.setUserId(Long.parseLong(dataArray[0]), sdf.parse(dataArray[1]), Double.parseDouble(dataArray[2]), Double.parseDouble(dataArray[3]), Long.parseLong(dataArray[4]) );
          /*dataArray[0]=Long.parseLong(dataArray[0]);
            spatialData.setCheckInTimes(sdf.parse(dataArray[1]));
            spatialData.setLatitude(Double.parseDouble(dataArray[2]));
            spatialData.setLongitude(Double.parseDouble(dataArray[3]));
            spatialData.setLocationId(Long.parseLong(dataArray[4]));
            newSpatialDataList.add(spatialData);
            if(newSpatialDataList.size()%100==0){
                spatialDataRepository.save(newSpatialDataList);
                newSpatialDataList.clear();
            }*/
          splittedFileData.add(objects);
        }
      mJdbcTemplate.batchUpdate("insert into spatial_data(user_id, check_in_times, latitude, longitude,location_id) values(?,?,?,?,?)", splittedFileData);
      //spatialDataRepository.save(newSpatialDataList);
    }


}
