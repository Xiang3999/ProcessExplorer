package PCB;

import java.util.*;


enum processType{READY, RUNNING, BLOCKED};   //����״̬
enum processList { READYLIST, BLOCKLIST };   // ���̶���
enum processPriorities { INIT, USER, SYSTEM };    // �������ȼ�

class Resource
{
	int ownID;            // ռ�е���Դid
	int ownNum;           // ռ�õ�����
	RCB rcb;             // ָ��RCB��

	public Resource(int ownID, int ownNum, RCB rcb)
	{
		this.ownID = ownID;
		this.ownNum = ownNum;
		this.rcb = rcb;
	}
};
class processStatus
{
	processType pType; // ����״̬
	processList pList; // ���̶���
}
class processCreationTree
{
	PCB parent;
	List<PCB> child;
};
public class PCB {
    private int pid;//�������е�process id
    private String pname;

    processStatus pStatus;         // ����״̬ 
	   //         - Type[READY����̬, RUNNING����̬, BLOCKED����̬]
	   //         - List[READYLIST�����ȴ�����, BLOCKLIST�����ȴ�����]
    private Vector<Resource>Resource;
	processCreationTree pTree;     // ������ 
    private processPriorities prioirty;
	// ���캯��
    public PCB(int pid, String pName, processPriorities priority, PCB parent)
    {
    	this.pid=pid;
    	this.pname=pName;
    	this.prioirty=priority;
    	this.pTree.parent=parent;
    	if (parent != null)
    	{
    		parent.addChild(this);
    	}

    	this.pStatus.pType = processType.READY;
    	this.pStatus.pList = processList.READYLIST;
    	// todo ռ����Դ��
    }
    //����ӽ���
    public void addChild(PCB child) 
    {
    	this.pTree.child.add(child);
    }
    //�ͷ��ӽ���
    public void delChild()
    {
    	this.pTree.child.remove(0);
    }
    //�ͷ�ָ���ӽ���
    public int delChild1(PCB child)
    {
    	for(PCB iter:pTree.child)
    	{
    		if(iter.getPid()==child.getPid())
    		{
    			pTree.child.remove(child);
    			return 0;
    		}
    	}
    	return 0;
    }
    //�ͷŸ�����
    public void deleteFather()
    {
    	this.pTree.parent.delChild1(this);
    }
    //��ӽ�����Դ
    public int addResourse(int num,RCB rcb)
    {
    	for(Resource iter:Resource) {
    		if(iter.rcb==rcb)
    		{
    			iter.ownNum=iter.ownNum+num;
    		}
    		return 0;
    	}
    	Resource newResource=new Resource(rcb.getRid(), num, rcb);  //����pcb����Դ��
    	this.Resource.add(newResource);//��ӵ�Resouce�б�
    	return 0;
    }
    public int delResource(int num, RCB rcb)
    {
    	int number = 0;
    	int i = 0;
    	int temp = 0;
    	boolean flag = false;
    	for(Resource iter:Resource)
    	{
    		if(iter.rcb==rcb)
    		{
    			if(iter.ownNum>num)// ��Ч, �����Ƴ� Resource ��
    			{
    				number = iter.ownNum; // ռ�õ�����
    			}
    			else if(iter.ownNum==num)// ��Ч, ���Ƴ� Resource ��
    			{
    				number = iter.ownNum; // ռ�õ�����
    				temp = i;
    				flag = true;
    			}
    			else//��Ч���ؽ���
    			{
    				return 0;
    			}
    		}
    		i++;
    		
    	}
    	if (i == Resource.size()+1)  // ��Դ�鲻��
    	{
    		return -1;
    	}

    	if (flag == true)  // �Ƴ� Resource ��
    	{
    		Resource.remove(i - 1);
    	}

    	return number;// ����ռ�õ�����,error = 0
    }
    //��������Ϊ����̬
    public int changeBLOCKED()
    {
    	this.pStatus.pType=processType.BLOCKED;
    	return 1;
    }
    //��������Ϊ����̬
    public int changeReady()
    {
    	this.pStatus.pType=processType.READY;
    	return 1;
    }
    //��������Ϊ����̬
    public int changeRunning()
    {
    	this.pStatus.pType=processType.RUNNING;
    	return 1;
    }
    // ��������Ϊ�����б�  
    public int changeBLOCKLIST()
    {
    	this.pStatus.pList = processList.BLOCKLIST;
    	return 0;
    }

    // ��������Ϊ�������� 
    public int changeREADYLIST()
    {
    	this.pStatus.pList = processList.READYLIST;
    	return 0;
    }
    //////////////////////////////////
    //      ���ʳ�Ա��������                   //
    //////////////////////////////////
    public int getPid() 
    {
    	return this.pid;
    }
    public String getPname() 
    {
    	return this.pname;
    }
    public int getPriority()
    {
    	return this.prioirty.ordinal();
    }
    public String getType()
    {
    	return this.pStatus.pType.toString();
    }
    public String getList()
    {
    	return this.pStatus.pList.toString();
    }
    public String getFather()
    {
    	if(this.pTree.parent==null)
    	{
    		System.out.println("û�и�����");
    		return this.pTree.parent.toString();
    	}
    	return this.pTree.parent.toString();
    }
    //����ռ����Դ�б��ռ������
    public int getResourcesOwnNum(int rcbId)
    {
    	for(Resource iter:Resource)
    	{
    		if(iter.ownID==rcbId)
    		{
    			return iter.ownNum;
    		}
    	}
    	return 0;
    }
    
    public void showChilds()
    {
    	for(PCB iter:pTree.child)
    	{
    		System.out.print(iter.getPname()+"  ");
    	}
    }
    //����Ƿ����ӽ���
    public boolean getpTreeEmpty()
    {
    	if(pTree.child.size()>0)
    	{
    		return true;
    	}
    	return false;
    }
    //��һ���ӽ���pid
    public int getpTreeFirstChild()
    {
    	return this.pTree.child.get(0).getPid();
    }
    //��� ��Դ�б��Ƿ�Ϊ��
    public boolean getResourcesEmpty()
    {
    	if(this.Resource.size()>0)
    	{
    		return true;
    	}
    	return false;
    }
    //������Դ�б��һ��rid
    public int getResourcesFirstRCB() 
    {
    	return this.Resource.firstElement().rcb.getRid();  
    }
    public void showThisProcess()
    {
    	System.out.println("------------------------------");
    	System.out.println("PID:\t"+this.getPid());
    	System.out.println("Name:\t"+this.getPname());
    	System.out.println("Status:\t"+this.getType());
    	System.out.println("\t"+this.getList());
    	System.out.println("priority\t"+this.getPriority());
    }
    
    

}
