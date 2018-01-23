package spatial.crowdsourcing.galsalgorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.Transactional;
import spatial.crowdsourcing.galsalgorithm.model.SpatialData;
import spatial.crowdsourcing.galsalgorithm.repositories.SpatialDataRepository;

import javax.annotation.PostConstruct;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.transaction.support.TransactionSynchronizationManager.clear;

@SpringBootApplication
@EnableCaching
public class GalsAlgorithmApplication {


	Logger logger = LoggerFactory.getLogger(GalsAlgorithmApplication.class);


	@Autowired
    SpatialDataRepository spatialDataRepository;


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
        while((readline= bufferedReader.readLine())!=null){
            String[] dataArray = readline.split("\t");
            SpatialData spatialData = new SpatialData();
//                Timestamp timestamp = Timestamp.valueOf(dataArray[1]);
            spatialData.setUserId(Long.parseLong(dataArray[0]) );
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            spatialData.setCheckInTimes(sdf.parse(dataArray[1]));
            spatialData.setLatitude(Double.parseDouble(dataArray[2]));
            spatialData.setLongitude(Double.parseDouble(dataArray[3]));
            spatialData.setLocationId(Long.parseLong(dataArray[4]));
            newSpatialDataList.add(spatialData);
            /*if(newSpatialDataList.size()%100==0){
                spatialDataRepository.save(newSpatialDataList);
                newSpatialDataList.clear();
            }*/
        }
        spatialDataRepository.save(newSpatialDataList);
    }


}
