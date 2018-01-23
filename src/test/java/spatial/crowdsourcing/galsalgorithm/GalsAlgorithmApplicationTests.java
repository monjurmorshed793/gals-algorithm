package spatial.crowdsourcing.galsalgorithm;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import spatial.crowdsourcing.galsalgorithm.model.SpatialData;
import spatial.crowdsourcing.galsalgorithm.repositories.SpatialDataRepository;
import spatial.crowdsourcing.galsalgorithm.shell.TaskCommands;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GalsAlgorithmApplicationTests {

	@Autowired
	SpatialDataRepository spatialDataRepository;

	@Test
	public void contextLoads() {
	}

	@Test
	public void testSpatialDataRepository(){
		List<Long> userIds = spatialDataRepository.findDistinctUserList().subList(0,12);

		List<SpatialData> spatialData = spatialDataRepository.findByUserIdIn(userIds);
		Assert.assertNotEquals(spatialData,0);
	}



}
