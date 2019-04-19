package utils;



/**
 * @Author: LX
 * @Date: 2019/4/19 17:41
 * @Version: 1.0
 */

public class IDUtil {

    public long getId(String addr, String machineRoom, String rack, String machineId, String time, String currentId) {
        String id = addr + machineRoom + rack + machineId + time + currentId;
        return Long.parseLong(id);
    }



}
