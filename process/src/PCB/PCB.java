package PCB;

import java.util.*;


enum processType{READY, RUNNING, BLOCKED};   //进程状态
enum processList { READYLIST, BLOCKLIST };   // 进程队列
enum processPriorities { INIT, USER, SYSTEM };    // 进程优先级

class Resource
{
	int ownID;            // 占有的资源id
	int ownNum;           // 占用的数量
	RCB rcb;             // 指向RCB块

	public Resource(int ownID, int ownNum, RCB rcb)
	{
		this.ownID = ownID;
		this.ownNum = ownNum;
		this.rcb = rcb;
	}
};
class processStatus
{
	processType pType; // 进程状态
	processList pList; // 进程队列
	public processStatus()
	{
		
	}
}
class processCreationTree
{
	PCB parent;
	List<PCB> child ;
	public processCreationTree()
	{
		this.parent=null;
		this.child= new ArrayList<>();
	}
};
public class PCB {
    private int pid;//正在运行的process id
    private String pname;

    processStatus pStatus =new processStatus();         // 进程状态 
	   //         - Type[READY就绪态, RUNNING运行态, BLOCKED阻塞态]
	   //         - List[READYLIST就绪等待队列, BLOCKLIST阻塞等待队列]
    private Vector<Resource>Resource=new Vector<>();
	processCreationTree pTree=new processCreationTree();     // 进程树 
    private processPriorities prioirty;
	// 构造函数
    public PCB(int pid, String pName, processPriorities priority, PCB parent)
    {
    	this.pid=pid;
    	this.pname=pName;
    	this.prioirty=priority;
    	if(pName.equals("init"))
    	{
    		this.pStatus.pType = processType.READY;
        	this.pStatus.pList = processList.READYLIST;
        	return;
    	}
    	if(parent==null)
    	{
    		System.out.print("error:parent==NULL");
    	}
    	else
    	{
    		this.pTree.parent=parent;
    		parent.addChild(this);
    	}

    	this.pStatus.pType = processType.READY;
    	this.pStatus.pList = processList.READYLIST;
    	// todo 占用资源表
    }
    //添加子进程
    public void addChild(PCB child) 
    {
    	this.pTree.child.add(child);
    }
    //释放子进程
    public void delChild()
    {
    	this.pTree.child.remove(0);
    }
    //释放指定子进程
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
    //释放父进程
    public void deleteFather()
    {
    	this.pTree.parent.delChild1(this);
    }
    //添加进程资源
    public int addResourse(int num,RCB rcb)
    {
    	for(Resource iter:Resource) {
    		if(iter.rcb==rcb)
    		{
    			iter.ownNum=iter.ownNum+num;
    		}
    		return 0;
    	}
    	Resource newResource=new Resource(rcb.getRid(), num, rcb);  //创建pcb的资源块
    	this.Resource.add(newResource);//添加到Resouce列表
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
    			if(iter.ownNum>num)// 有效, 但不移除 Resource 块
    			{
    				number = iter.ownNum; // 占用的数量
    			}
    			else if(iter.ownNum==num)// 有效, 但移除 Resource 块
    			{
    				number = iter.ownNum; // 占用的数量
    				temp = i;
    				flag = true;
    			}
    			else//无效返回结束
    			{
    				return 0;
    			}
    		}
    		i++;
    		
    	}
    	if (i == Resource.size()+1)  // 资源块不在
    	{
    		return -1;
    	}

    	if (flag == true)  // 移除 Resource 块
    	{
    		Resource.remove(i - 1);
    	}

    	return number;// 返回占用的数量,error = 0
    }
    //进程设置为阻塞态
    public int changeBLOCKED()
    {
    	this.pStatus.pType=processType.BLOCKED;
    	return 1;
    }
    //进程设置为就绪态
    public int changeReady()
    {
    	this.pStatus.pType=processType.READY;
    	return 1;
    }
    //进程设置为运行态
    public int changeRunning()
    {
    	this.pStatus.pType=processType.RUNNING;
    	return 1;
    }
    // 进程设置为阻塞列表  
    public int changeBLOCKLIST()
    {
    	this.pStatus.pList = processList.BLOCKLIST;
    	return 0;
    }

    // 进程设置为就绪队列 
    public int changeREADYLIST()
    {
    	this.pStatus.pList = processList.READYLIST;
    	return 0;
    }
    //////////////////////////////////
    //      访问成员变量方法                   //
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
    		return "null";
    	}
    	return this.pTree.parent.toString();
    }
    /**
     * 返回占有资源列表的占有数量
     * @param rcbId
     * @return
     */
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
    /**
     * 检查是否有子进程,有子进程返回true
     * @return
     */
    public boolean getpTreeEmpty()
    {
    	if(pTree.child.size()>0)
    	{
    		return true;
    	}
    	return false;
    }
    /**
     * 返回第一个子进程pid
     * @return
     */
    public int getpTreeFirstChild()
    {
    	return this.pTree.child.get(0).getPid();
    }
    /**
     * 检查 资源列表是否为空,为空返回false
     * @return
     */
    public boolean getResourcesEmpty()
    {
    	if(this.Resource.size()>0)
    	{
    		return true;
    	}
    	return false;
    }
    //返回资源列表第一个RCB
    public RCB getResourcesFirstRCB() 
    {
    	
    	return this.Resource.firstElement().rcb;  
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
