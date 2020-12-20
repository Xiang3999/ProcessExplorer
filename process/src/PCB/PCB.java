package PCB;

import java.util.*;


enum processType{READY, RUNNING, BLOCKED};   //����״̬
enum processList { READYLIST, BLOCKLIST };   // ���̶���
enum processPriorities { INIT, USER, SYSTEM };    // �������ȼ�

class Resource
{
	private int ownID;            // ռ�е���Դid
	private int ownNum;           // ռ�õ�����
	RCB rcb=new RCB;             // ָ��RCB��

	Resource(int ownID, int ownNum, RCB rcb)
	{
		this.ownID = ownID;
		this.ownNum = ownNum;
		this.rcb = rcb;
	}
};
public class PCB {

	
    private int pid;//�������е�process id
    private int prioirty;
    private String pname;
    private String childName=null;
    private Map<String ,Integer> requestRes=new HashMap<>();
    private int parentId;
    private int childId;
    private int state;
}
