package PCB;

import java.util.*;

public class RCB {
	private int rid;                      // 资源ID
	private String rName;                 // 资源名
	private int rStatus;                  // 资源状态 数量
	private int initNum;                  // 初始化数量
	private List<waiting> waitingList;    // 资源阻塞等待队列
	// 构造函数
	public RCB(int rid, String rName, int initNum)
	{
		this.rid=rid;
		this.rName=rName;
		this.initNum=initNum;
		this.rStatus=initNum;
	}
	//请求资源
	public int requestR(int num)
	{
		if(this.rStatus>=num)   //剩余资源足够
		{
			this.rStatus=this.rStatus-num;  
			return 1;
		}
		else {    //剩余资源不够，阻塞
			
		}
		return 0;
	}
	//释放资源
	public int releaseR(int num)
	{
		this.rStatus=this.rStatus+num;
		if (this.rStatus > this.initNum)
		{
			System.out.print("error: rStatus > initNum");
		}
		return 0;
	}
	// 添加阻塞等待队列
	public int addWaitingList(int number, PCB pcb)
	{
		//遍历资源阻塞等待队列是否有相同的 PCB 块
		for (waiting iter:waitingList)
		{
			if (iter.pcb == pcb)
			{
				iter.BlockNum = iter.BlockNum + number;
				return 1;
			}
		}

		// 没有找到则添加
		waiting newWaiting = new waiting(pcb.getPid(), number,pcb);   // 创建资源阻塞资源块 waiting
		this.waitingList.add(newWaiting);         // 添加到进程的占有资源列表
		return 0;
	}
	
	//移除阻塞等待队列
	public void delWaitingList()
	{
		waitingList.remove(0);
	}
	//判断阻塞队列首部进程需求的资源数是否小于等于可用资源数量
	
	public boolean isWaitingList()
	{
		if (waitingList.get(0).pcb.getResourcesOwnNum(this.getRid()) <= this.rStatus)
		{
			return true;
		}
		else
		{
			return false;
		}
		
	}
	///////////////////////////////////
	///      访问成员变量               		///
	///////////////////////////////////
	
	public int getRid()
	{
		return this.rid;
	}
	public String getname() 
	{
		return this.rName;
	}
	public int getNum()
	{
		return this.rStatus;
	}
	public int getInitNum()
	{
		return this.initNum;
	}
	public boolean waitingListEmpty()
	{
		if(this.waitingList.size()>0)
		{
			return true;
		}
		return false;
	}
	public int getWaitingListFirstNum()
	{
		return this.waitingList.get(0).BlockNum;
	}
	public int getWaitingListFirstPID()
	{
		return this.waitingList.get(0).BlockPID;
	}
	public void showWaitingListEach()
	{
		for(waiting iter:waitingList)
		{
			System.out.print(iter.pcb.getPname()+"\t");
		}
	}
}
