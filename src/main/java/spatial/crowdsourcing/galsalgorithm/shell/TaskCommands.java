package spatial.crowdsourcing.galsalgorithm.shell;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import spatial.crowdsourcing.galsalgorithm.model.SpatialData;
import spatial.crowdsourcing.galsalgorithm.repositories.SpatialDataRepository;

import java.util.List;

@ShellComponent
public class TaskCommands {
    @Autowired
    SpatialDataRepository spatialDataRepository;

    @ShellMethod("Show number of distinct of workers and tasks: (wt)")
    public String process(
            @ShellOption(help = "Type")  String worker
    ){
        String result="";
        List<Long> taskList = spatialDataRepository.findDistinctByLocationId();
        List<Long> userList = spatialDataRepository.findDistinctUserList();
        return "task :"+taskList.size()+" and worker: "+userList.size();
    }

    @ShellMethod("Generate status")
   public String generateStatus(
           @ShellOption(help="worker number") Integer workerNumber
    ){
        List<Long> workerList = spatialDataRepository.findDistinctByLocationId().subList(0, 12);
        List<SpatialData> spatialDataList = spatialDataRepository.findByUserIdIn(workerList);
        return spatialDataList.size()+"";
   }


}
