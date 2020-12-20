package PCB;

import java.util.*;


enum processType{READY, RUNNING, BLOCKED};   //进程状态
enum processList { READYLIST, BLOCKLIST };   // 进程队列
enum processPriorities { INIT, USER, SYSTEM };    // 进程优先级

class Resource
{
	private int ownID;            // 占有的资源id
	private int ownNum;           // 占用的数量
	RCB rcb=new RCB;             // 指向RCB块

	Resource(int ownID, int ownNum, RCB rcb)
	{
		this.ownID = ownID;
		this.ownNum = ownNum;
		this.rcb = rcb;
	}
};
public class PCB {

	
    private int pid;//正在运行的process id
    private int prioirty;
    private String pname;
    private String childName=null;
    private Map<String ,Integer> requestRes=new HashMap<>();
    private int parentId;
    private int childId;
    private int state;
}
