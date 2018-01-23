/*
package spatial.crowdsourcing.galsalgorithm.repositories;

import com.hazelcast.core.MapStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import spatial.crowdsourcing.galsalgorithm.model.SpatialData;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class SpatialDataMapStore implements MapStore<Long, SpatialData>{
    @Autowired
    SpatialDataRepository spatialDataRepository;

    @Override
    public void store(Long aLong, SpatialData spatialData) {
        spatialDataRepository.save(spatialData);
    }

    @Override
    public void storeAll(Map<Long, SpatialData> map) {
        spatialDataRepository.save(map.values());
    }

    @Override
    public void delete(Long aLong) {
        SpatialData spatialData = load(aLong);
        spatialDataRepository.delete(spatialData);
    }

    @Override
    public void deleteAll(Collection<Long> keys) {
        Iterable<SpatialData> spatialData = spatialDataRepository.findAll(keys);
        spatialDataRepository.delete(spatialData);
    }

    @Override
    public SpatialData load(Long key) {
        return spatialDataRepository.findOne(key);
    }

    @Override
    public Map<Long, SpatialData> loadAll(Collection<Long> keys) {
        Iterable<SpatialData> spatialDataIterable = spatialDataRepository.findAll(keys);
        return StreamSupport.stream(spatialDataIterable.spliterator(), false)
                .collect(Collectors.toMap(SpatialData::getUserId, Function.identity()));
    }

    @Override
    public Iterable<Long> loadAllKeys() {
        Iterable<SpatialData> spatialData = spatialDataRepository.findAll();
        return StreamSupport.stream(spatialData.spliterator(), false)
                .map(SpatialData::getUserId)
                .collect(Collectors.toList());
    }
}
*/
