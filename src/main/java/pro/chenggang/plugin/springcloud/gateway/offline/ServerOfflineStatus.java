package pro.chenggang.plugin.springcloud.gateway.offline;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * ServerOfflineStatus
 * @author chenggang
 * @date 2019/04/25
 */
@Getter
@Setter
@ToString
public class ServerOfflineStatus {

    private String name;
    private String ip;
    private Integer port;
    private Long offlineTime;

    /**
     * check the server is temporary offline whether or not
     * @param serverUpTime
     * @return
     */
    public boolean isOffline(long serverUpTime){
        if(this.offlineTime == null){
            return false;
        }
        return offlineTime > serverUpTime;
    }

}
