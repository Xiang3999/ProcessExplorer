package PCB;

import java.util.*;

public class processManage {
	private PCB runningProcess;                // 正在执行进程指针
	private Vector<PCB> processTable = new Vector<>();          // 进程表
	private int allocation_pid;                 // 进程id分配(自增)

	private Vector<RCB> resourcesTable = new Vector<>();        // 资源列表

	// 三级就绪进程队列
	private List<PCB> initReadyList=new ArrayList<>();
	private List<PCB> userReadyList=new ArrayList<>();
	private List<PCB> systemReadyList=new ArrayList<>();

	// 阻塞队列
	private List<PCB> blockList=new ArrayList<>();  

	

	public processManage()
	{
		this.allocation_pid=1;
		this.runningProcess=null;
	}
/*
 *      PCB
 */
	public int createProcess(String pName, int priority)
	{
		int pid =0;
		if(this.checkpName(pName))//是否重名
		{
			return 2;
		}
		pid = this.allocation_pid++;
		PCB pcb = new PCB(pid, pName, processPriorities.values()[priority],runningProcess );
		// 放入进程表
		this.processTable.add(pcb);
		// init进程处理，直接抢占
		if (priority == 0)
		{
			this.runningProcess = pcb;
			runningProcess.changeRunning();
		}
		//放入就绪队列
		switch (priority)
		{
		case 0:
			initReadyList.add(pcb);
			break;
		case 1:
			userReadyList.add(pcb);
			break;
		case 2:
			systemReadyList.add(pcb);
			break;
		default:
			System.out.print("error");
			break;
		}
		//检查优先级是否正确
		if (runningProcess.getPriority() < pcb.getPriority())
		{
			runningProcess.changeReady();
			this.runningProcess = pcb;
			runningProcess.changeRunning();
			System.out.println("[warnning]高优先级抢占,切换进程 " + runningProcess.getPname() + " 运行");
		}

		return 1;
	}
	// 撤销进程
	public int destoryProcess(String pName)
	{
		//查看删除进程是否在进程表里
		if(this.checkpName(pName)==false)
			return 2;
		PCB deldata=this.findProcessbyName(pName);
		delChildProcess(deldata);
		return 1;
	}
	public void Schedule()
	{
		// 当有2级进程
		if (systemReadyList.size() != 0)
		{
			systemReadyList.remove(0);                // 移除system 就绪列表第一个
			systemReadyList.add(runningProcess);  // 把它放入就绪表队列末尾
			runningProcess.changeReady();
			runningProcess.changeREADYLIST();
			runningProcess = systemReadyList.get(0);   // 正在执行进程指针 指向就绪队列第一个
			runningProcess.changeRunning();
		}
		else  // 当没有2级进程的, 只有1级就绪队列
		{
			userReadyList.remove(0);                  // 移除user 就绪列表第一个
			userReadyList.add(runningProcess);    // 把它放入就绪表队列末尾
			runningProcess.changeReady();
			runningProcess.changeREADYLIST();
			runningProcess = userReadyList.get(0);     // 正在执行进程指针 指向就绪队列第一个
			runningProcess.changeRunning();
		}

	}
	public int delChildProcess(PCB pcb)
	{
		int temp=-1;
		int tempID=0;
		PCB deldata;
		while(pcb.getpTreeEmpty()==true)
		{
			tempID=pcb.getpTreeFirstChild();
			deldata=this.findProcessbyID(tempID);
			PCB tempPCB=deldata;
			pcb.delChild();
			this.delChildProcess(tempPCB);
		}
		RCB rcb;
		temp=this.freeResource(pcb);
		if(temp!=1)
		{
			System.out.println("error");
		}
		//调度
		if(pcb==runningProcess)// 如是执行态进程 第一个就绪态
		{
			if(systemReadyList.size()==1)// 如果高级system就绪进程队列就剩下执行态进程自己一个 降级
			{
				runningProcess=userReadyList.get(userReadyList.size()-1);
				systemReadyList.remove(0);
			}
			else if(userReadyList.size()==1)
			{
				runningProcess=initReadyList.get(initReadyList.size()-1);
				userReadyList.remove(0);
			}
			else
			{
				this.Schedule();
				if (pcb.getPriority() == 1)
				{
					this.delUserReadyList(pcb);
				}
				else if (pcb.getPriority() == 2)
				{
					this.delSystemReadyList(pcb);
				}
				else
				{

				}
			}
				
		}
		else if (pcb.getType() == "READY")  // 就绪
		{
			if (pcb.getPriority() == 1)
			{
				this.delUserReadyList(pcb);
			}
			else if (pcb.getPriority() == 2)
			{
				this.delSystemReadyList(pcb);
			}
			else   
			{

			}
		}  // 前面已经处理完 阻塞态
		System.out.println( "进程 " + pcb.getPname() + "已撤销!" );
		// 释放 PCB 空间
		this.delProcesstable(pcb);
		pcb.deleteFather();
		return 0;
	}
	public PCB findProcessbyName(String pName)
	{
		for (PCB iter:processTable)
		{
			if(pName.equals(iter.getPname()))
			{
				return iter;
			}
		}
		
		return processTable.lastElement();
	}
	
	public PCB findProcessbyID(int pid)
	{
		for (PCB iter:processTable)
		{
			if(iter.getPid()==pid)
			{
				return iter;
			}
		}
		
		return processTable.lastElement();
	}
	public Boolean checkProcessName(String pName)
	{
		for(PCB iter:processTable)
		{
			if(pName.equals(iter.getPname()))
			{
				return true;
			}
		}
		return false;
	}
	public Boolean checkProcessPid(int pid)
	{
		for(PCB iter:processTable)
		{
			if(iter.getPid()==pid)
			{
				return true;
			}
		}
		return false;
	}
	/**
	 * @param pName
	 * @return
	 */
	public boolean checkpName(String pName)
	{
		for(PCB iter:processTable)
		{
			if(pName.equals(iter.getPname()))
			{
				return true;
			}
		}
		return false;
	}
	/**
	 * 释放资源
	 * @param pcb
	 * @return
	 */
	public int freeResource(PCB pcb)
	{
		int tempPID = -1;
		int temp = 0;
		int tempNUM = 0;
		RCB rcb;
		PCB tempPCB;

		// 查询进程 是否有占有的资源
		while (pcb.getResourcesEmpty() == true)  // 不为空
		{
			rcb = pcb.getResourcesFirstRCB();  // 获取第一个RCB
			// 将资源 rcb 从pcb 进程 Resources占有资源列表中移除
		    // 并资源状态 数量rStatus + number
			temp = pcb.delResource(pcb.getResourcesOwnNum(rcb.getRid()), rcb);
			rcb.releaseR(temp);

			// 跟正在执行进程无关
		    // 如果 RCB 阻塞队列不为空, 且阻塞队列首部进程需求的资源数 req 小于等于可用资源数量 u，则唤醒这个阻塞进程，放入就绪队列
			while ((rcb.waitingListEmpty() == false) && (rcb.isWaitingList() == true))
			{
				rcb.requestR(rcb.getWaitingListFirstNum());  // 减去请求数量资源
				tempPID = rcb.getWaitingListFirstPID();
				tempNUM = rcb.getWaitingListFirstNum();
				tempPCB = this.findProcessbyID(tempPID);
				PCB changePCB = (tempPCB);  // 找到RCB 资源阻塞等待队列 第一个进程

				rcb.delWaitingList();   // 从资源的阻塞队列中移除 第一个进程

				changePCB.changeReady();
				changePCB.changeREADYLIST();
				// 把 rcb 插入到 changePCB 占有资源列表中
				changePCB.addResourse(tempNUM, rcb);         // 把rcb插入pcb的占有资源列表

				// 插入 changePCB 到就绪队列 
				// 基于优先级的抢占式调度策略，因此当有进程获得资源时，需要查看当前的优先级情况并进行调度
				switch (changePCB.getPriority())
				{
				case 0: // INIT update
					this.delBlockList(changePCB);
					initReadyList.add(changePCB);
					// 降级判断
					break;

				case 1: // USER 
					// 升级判定
					if (userReadyList.size() == 0)
					{
						this.delBlockList(changePCB);
						userReadyList.add(changePCB);				
						runningProcess = changePCB;  // 高优先级抢占运行
						changePCB.changeRunning();
					}
					else
					{
						this.delBlockList(changePCB);
						userReadyList.add(changePCB);
					}
					break;

				case 2: // SYSTEM 
					// 升级判定
					if (systemReadyList.size() == 0)
					{
						this.delBlockList(changePCB);
						systemReadyList.add(changePCB);
						runningProcess = changePCB;  // 高优先级抢占运行
						changePCB.changeRunning();
					}
					else
					{
						this.delBlockList(changePCB);
						systemReadyList.add(changePCB);
					}
					break;

				default:
					break;
				}

			}
		}
		return 1;
	}
	/**
	 * 删除 userReadyList 指定进程项
	 * @param pcb
	 * @return 
	 */
	public int delUserReadyList(PCB pcb)
	{
		for (PCB data:userReadyList)
		{
			if(data.getPid()==pcb.getPid())
			{
				userReadyList.remove(data);
				return 1;
			}
		}
		return 0;
	}
	/**
	 * 删除 systemReadyList 指定进程项
	 * @param pcb
	 * @return 0/1
	 */
	public int delSystemReadyList(PCB pcb)
	{
		for (PCB data:systemReadyList)
		{
			if(data.getPid()==pcb.getPid())
			{
				systemReadyList.remove(data);
				return 1;
			}
		}
		return 0;
	}
	/**
	 * 删除 主进程的指定进程项
	 * @param pcb
	 * @return 0/1
	 */
	public int delProcesstable(PCB pcb)
	{
		for (PCB data:processTable)
		{
			if(data.getPid()==pcb.getPid())
			{
				processTable.remove(data);
				return 1;
			}
		}
		return 0;
	}
/*********************************************
*              processManager
*              RCB
**********************************************/
	public void createResources()
	{
		RCB rcb1=new RCB(1,"R1",3);
		RCB rcb2=new RCB(2,"R2",1);
		RCB rcb3=new RCB(3,"R3",2);
		RCB rcb4=new RCB(4,"R4",1);
		this.resourcesTable.add(rcb1);
		this.resourcesTable.add(rcb2);
		this.resourcesTable.add(rcb3);
		this.resourcesTable.add(rcb4);
	}
	/**
	 * 请求资源
	 * @param rName
	 * @param number
	 * @return
	 */
	public int requestResources(String rName,int number)
	{
		//检查是否有此资源,没有就退出
		if(this.checkResourcesName(rName)==false)
		{
			return 2;
		}
		//是否超过资源总量
		if(this.checkResourcesInitnum(rName,number))
		{
			return 3;
		}
		// 根据 rName 找到相应 RCB块
		RCB rcb = this.findResourcesByName(rName);
		
		if (rcb.getNum()>=number)//剩余资源足够
		{
			rcb.requestR(number);//剩余资源减一
			runningProcess.addResourse(number, rcb);//把rcb插入到pcb占有资源列表中
			return 1;
		}
		else//资源不够，阻塞
		{
			runningProcess.changeBLOCKED();// 进程设置 阻塞状态
			runningProcess.changeBLOCKLIST();// 进程加入 阻塞列表
			rcb.addWaitingList(number, runningProcess);// 插入 RCB 资源阻塞等待队列
			
			switch(runningProcess.getPriority())
			{
			case 0:
				initReadyList.remove(0);//删除就绪队列的第一个元素
				blockList.add(runningProcess);
				System.out.println("BUG:init进程阻塞,程序崩溃!" );
				System.exit(0);
			case 1:
				userReadyList.remove(0);
				blockList.add(runningProcess);
				System.out.println( "[warnning]进程 " + runningProcess.getPname() + " 阻塞!" );
				// 降级判断
				if (userReadyList.size() != 0)
				{
					runningProcess = userReadyList.get(0);   // 正在执行进程指针 指向就绪队列第一个
					runningProcess.changeRunning();
				}
				else
				{
					runningProcess = initReadyList.get(0);   // 正在执行进程指针 指向就绪队列第一个
					runningProcess.changeRunning();
				}
				break;
			case 2:
				systemReadyList.remove(0);
				blockList.add(runningProcess);
				System.out.println( "[warnning]进程 " + runningProcess.getPname() + " 阻塞!" );
				// 降级判断
				if (systemReadyList.size() != 0)
				{
					runningProcess = systemReadyList.get(0);   // 正在执行进程指针 指向就绪队列第一个
					runningProcess.changeRunning();
				}
				else
				{
					runningProcess = userReadyList.get(0);   // 正在执行进程指针 指向就绪队列第一个
					runningProcess.changeRunning();
				}
				break;

			default:
				break;
			}
			// 输出进程调度
			System.out.println( "[warnning]切换进程 " + runningProcess.getPname() + " 执行!" );
		}
		return 4;
		
	}
	public int releaseResources(String rName,int number)
	{
		int operand = 0;  // 操作数
		int tempPID = -1;
		int tempNUM = 0;
		
		PCB tempPCB;
		//检查是否有此资源,没有就退出
		if(this.checkResourcesName(rName)==false)
		{
			return 2;
		}
		//是否超过资源总量
		if(this.checkResourcesInitnum(rName,number))
		{
			return 3;
		}
		// 根据 rName 找到相应 RCB块
		RCB rcb = this.findResourcesByName(rName);		
		// release
		
		// 将资源 rcb 从从当前进程 Resources占有资源列表中移除
		// 并资源状态 数量rStatus + number
		operand = runningProcess.delResource(number, rcb);

		if (operand == 0)  // 释放资源数量无效
		{
			return 4;
		} 
		else if (operand == -1) // 该进程无该资源
		{
			return 5;
		}
		else
		{
			rcb.releaseR(operand);  // rStatus + number
		}
		// 跟正在执行进程无关
		// 如果阻塞队列不为空, 且阻塞队列首部进程需求的资源数 req 小于等于可用资源数量 u，则唤醒这个阻塞进程，放入就绪队列
		while((rcb.waitingListEmpty()==false)&&(rcb.isWaitingList()==true))
		{
			rcb.requestR(rcb.getWaitingListFirstNum());//减少请求资源数量
			tempPID=rcb.getWaitingListFirstPID();
			tempNUM=rcb.getWaitingListFirstNum();
			tempPCB=this.findProcessbyID(tempPID);
			PCB changePCB=tempPCB;
			rcb.delWaitingList(); // 从资源的阻塞队列中移除 第一个进程
			changePCB.changeReady();
			changePCB.changeREADYLIST();
			changePCB.addResourse(tempNUM, rcb);
			// 插入 changePCB 到就绪队列 
			// 基于优先级的抢占式调度策略，因此当有进程获得资源时，需要查看当前的优先级情况并进行调度
			switch(changePCB.getPriority())
			{
			case 0:
				this.delBlockList(changePCB);
				initReadyList.add(changePCB);
				System.out.println("进程 " + changePCB.getPname() + " 就绪!" );
				break;
			case 1:
				if(userReadyList.size()==0)
				{
					this.delBlockList(changePCB);
					userReadyList.add(changePCB);
					System.out.println("[warnning]进程 " + changePCB.getPname() + " 就绪!");
					runningProcess = changePCB;  // 高优先级抢占运行
					changePCB.changeRunning();
					System.out.println("[warnning]高优先级进程 " + runningProcess.getPname() + " 抢占" );
				}
				else
				{
					this.delBlockList(changePCB);
					userReadyList.add(changePCB);
					System.out.println("[warnning]进程 " + changePCB.getPname() + " 就绪!");
				}
			case 2:
				if(systemReadyList.size()==0)
				{
					this.delBlockList(changePCB);
					systemReadyList.add(changePCB);
					System.out.println("[warnning]进程 " + changePCB.getPname() + " 就绪!");
					runningProcess = changePCB;  // 高优先级抢占运行
					changePCB.changeRunning();
					System.out.println("[warnning]高优先级进程 " + runningProcess.getPname() + " 抢占" );
				}
				else
				{
					this.delBlockList(changePCB);
					systemReadyList.add(changePCB);
					System.out.println("[warnning]进程 " + changePCB.getPname() + " 就绪!");
				}
			default:
				break;	
			}
			
		}
		return 1;
	}
	public  RCB findResourcesByName(String name)
	{
		for (RCB data:resourcesTable)
		{
			if(name.equals(data.getname()))
			{
				return data;
			}
		}
		return resourcesTable.lastElement();
	}
	/**
	 * 检查资源名是否存在，如果存在返回true
	 * @param name
	 * @return
	 */
	public  Boolean checkResourcesName(String name)
	{
		for (RCB data:resourcesTable)
		{
			//字符串判等的时候使用equals(),因为==是判断引用的地址
			if(name.equals(data.getname()))
			{
				return true;
			}
		}
		return false;
	}
	/**
	 * 检查请求是否超过此资源总量,超过为真
	 * @param rName rcb的名字
	 * @param num  rid
	 * @return
	 */
	public Boolean checkResourcesInitnum(String rName,int num)
	{
		for (RCB data:resourcesTable)
		{
			if(rName.equals(data.getname()))
			{
				if(data.getInitNum()>=num)
				{
					return false;
				}
			}
		}
		return true;
	}
	
	
	/**
	 * 删除阻塞队列指定进程项
	 * @param pcb
	 * @return 0/1 
	 */
	public int delBlockList(PCB pcb)
	{
		for (PCB iter:blockList)
		{
			if(iter.getPid()==pcb.getPid())
			{
				blockList.remove(iter);
				return 1;
			}
		}
		return 0;
	}
	/*************************************************************
	 *  processManager
	 *  get() - show()
	 *************************************************************/
	public int getRunningProcess()
	{
		return this.runningProcess.getPid();
	}
	public void showReadyList()
	{
		System.out.println("===>ReadyList");
		System.out.print("System Ready List:");
		for(PCB iter:systemReadyList)
		{
			if(runningProcess.getPid()==iter.getPid())
				System.out.print("=>");
			System.out.print(" "+iter.getPname());
		}
		System.out.print("\n");
		System.out.print("User   Ready List:");
		for(PCB iter:userReadyList)
		{
			if(runningProcess.getPid()==iter.getPid())
				System.out.print("=>");
			System.out.print(" "+iter.getPname());
		}
		System.out.print("\n");
		System.out.print("Init   Ready List:");
		for(PCB iter:initReadyList)
		{
			if(runningProcess.getPid()==iter.getPid())
				System.out.print("=>");
			System.out.print(" "+iter.getPname());
		}
		System.out.print("\n");
	}
	/**
	 * 展示进程列表
	 */
	public void showProcessTable()
	{
		System.out.println("===> ProcessTable");
		System.out.println("PID\t NAME\t PRIORITY\t TYPE\t LIST\t FATHER\t CHILD");
		for(PCB iter:processTable)
		{
			System.out.print(iter.getPid()+"\t"+iter.getPname()+"\t"
		+iter.getPriority()+"\t"+iter.getType()+"\t"+iter.getList()+"\t"+iter.getFather()+"\t");
			iter.showChilds();
			System.out.print("\n");
			
		}
	}
	/**
	 * 展示资源列表
	 */
	public void showResourcessTable()
	{
		System.out.println("===> ProcessTable");
		System.out.println("NAME\t NUMBER");
		for(RCB iter:resourcesTable)
		{
			System.out.print(iter.getname()+"\t"+iter.getNum());
			System.out.print("\n");
			
		}
	}
	public void showBlockList()
	{
		int n=0;
		System.out.println("===> BlockList");
		for (RCB iter:resourcesTable)
		{
			System.out.print("*R");
			n++;
			System.out.print(n+"\t");
			iter.showWaitingListEach();
			System.out.print("\n");	
		}

	}
	
}
