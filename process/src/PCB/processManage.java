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
	public int destoryProcess(String pname)
	{
		
		return 0;
	}
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
}
