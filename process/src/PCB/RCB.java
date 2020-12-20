package PCB;

import java.util.*;

public class RCB {
	private int rid;                      // ��ԴID
	private String rName;                 // ��Դ��
	private int rStatus;                  // ��Դ״̬ ����
	private int initNum;                  // ��ʼ������
	private List<waiting> waitingList;    // ��Դ�����ȴ�����
	// ���캯��
	public RCB(int rid, String rName, int initNum)
	{
		this.rid=rid;
		this.rName=rName;
		this.initNum=initNum;
		this.rStatus=initNum;
	}
	//������Դ
	public int requestR(int num)
	{
		if(this.rStatus>=num)   //ʣ����Դ�㹻
		{
			this.rStatus=this.rStatus-num;  
			return 1;
		}
		else {    //ʣ����Դ����������
			
		}
		return 0;
	}
	//�ͷ���Դ
	public int releaseR(int num)
	{
		this.rStatus=this.rStatus+num;
		if (this.rStatus > this.initNum)
		{
			System.out.print("error: rStatus > initNum");
		}
		return 0;
	}
	// ��������ȴ�����
	public int addWaitingList(int number, PCB pcb)
	{
		//������Դ�����ȴ������Ƿ�����ͬ�� PCB ��
		for (waiting iter:waitingList)
		{
			if (iter.pcb == pcb)
			{
				iter.BlockNum = iter.BlockNum + number;
				return 1;
			}
		}

		// û���ҵ������
		waiting newWaiting = new waiting(pcb.getPid(), number,pcb);   // ������Դ������Դ�� waiting
		this.waitingList.add(newWaiting);         // ��ӵ����̵�ռ����Դ�б�
		return 0;
	}
	
	//�Ƴ������ȴ�����
	public void delWaitingList()
	{
		waitingList.remove(0);
	}
	//�ж����������ײ������������Դ���Ƿ�С�ڵ��ڿ�����Դ����
	
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
	///      ���ʳ�Ա����               		///
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
