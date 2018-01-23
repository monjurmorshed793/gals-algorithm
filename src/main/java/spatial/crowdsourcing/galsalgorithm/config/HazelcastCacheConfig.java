/*
package spatial.crowdsourcing.galsalgorithm.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import spatial.crowdsourcing.galsalgorithm.repositories.SpatialDataMapStore;

@Configuration
public class HazelcastCacheConfig {

    @Autowired
    SpatialDataMapStore spatialDataMapStore;

    @Bean
    public Config hazelcastConfig(){
        Config config = new Config();
        config.setInstanceName("hazelcast-cache");

        MapConfig spatialDataCache = new MapConfig();
        spatialDataCache.setTimeToLiveSeconds(200);
        spatialDataCache.setEvictionPolicy(EvictionPolicy.LFU);
        MapStoreConfig spatialDataStoreConfig = new MapStoreConfig();
        spatialDataStoreConfig.setImplementation(spatialDataMapStore);
        spatialDataCache.setMapStoreConfig(spatialDataStoreConfig);
        config.addMapConfig(spatialDataCache);

        return config;
    }
}
*/
