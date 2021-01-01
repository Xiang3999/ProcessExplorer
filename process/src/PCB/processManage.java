package PCB;

import java.util.*;

public class processManage {
	private PCB runningProcess;                // 正在执行进程指针
	private Vector<PCB> processTable;          // 进程表
	private int allocation_pid;                 // 进程id分配(自增)

	private Vector<RCB> resourcesTable;        // 资源列表

	// 三级就绪进程队列
	private List<PCB> initReadyList;
	private List<PCB> userReadyList;
	private List<PCB> systemReadyList;

	// 阻塞队列
	private List<PCB> blockList;  

	

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

		return 0;
	}
	// 撤销进程
	public int destoryProcess(String pName)
	{
		//查看删除进程是否在进程表里
		if(this.checkpName(pName)==false)
			return 2;
		PCB deldata=this.findProcessbyName(pName);
		delChildProcess(deldata);
		return 0;
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
		temp=
		return 0;
	}
	public PCB findProcessbyName(String pName)
	{
		for (PCB iter:processTable)
		{
			if(iter.getPname()==pName)
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
			if(iter.getPname()==pName)
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
			if(iter.getPname()==pName)
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
		while (pcb.getResourcesEmpty() == false)  // 不为空
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
		RCB rcb1=new RCB(1,"R1",1);
		RCB rcb2=new RCB(1,"R2",1);
		RCB rcb3=new RCB(1,"R3",1);
		RCB rcb4=new RCB(1,"R4",1);
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
	public  RCB findResourcesByName(String name)
	{
		for (RCB data:resourcesTable)
		{
			if(data.getname()==name)
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
			if(data.getname()==name)
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
			if(data.getname()==rName)
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
}
