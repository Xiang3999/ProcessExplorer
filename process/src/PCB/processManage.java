package PCB;

import java.util.*;

public class processManage {
	private PCB runningProcess;                // ����ִ�н���ָ��
	private Vector<PCB> processTable;          // ���̱�
	private int allocation_pid;                 // ����id����(����)

	private Vector<RCB> resourcesTable;        // ��Դ�б�

	// �����������̶���
	private List<PCB> initReadyList;
	private List<PCB> userReadyList;
	private List<PCB> systemReadyList;

	// ��������
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
		if(this.checkpName(pName))//�Ƿ�����
		{
			return 2;
		}
		pid = this.allocation_pid++;
		PCB pcb = new PCB(pid, pName, processPriorities.values()[priority],runningProcess );
		// ������̱�
		this.processTable.add(pcb);
		// init���̴���ֱ����ռ
		if (priority == 0)
		{
			this.runningProcess = pcb;
			runningProcess.changeRunning();
		}
		//�����������
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
		//������ȼ��Ƿ���ȷ
		if (runningProcess.getPriority() < pcb.getPriority())
		{
			runningProcess.changeReady();
			this.runningProcess = pcb;
			runningProcess.changeRunning();
			System.out.println("[warnning]�����ȼ���ռ,�л����� " + runningProcess.getPname() + " ����");
		}

		return 0;
	}
	// ��������
	public int destoryProcess(String pName)
	{
		//�鿴ɾ�������Ƿ��ڽ��̱���
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
	 * �ͷ���Դ
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

		// ��ѯ���� �Ƿ���ռ�е���Դ
		while (pcb.getResourcesEmpty() == false)  // ��Ϊ��
		{
			rcb = pcb.getResourcesFirstRCB();  // ��ȡ��һ��RCB
			// ����Դ rcb ��pcb ���� Resourcesռ����Դ�б����Ƴ�
		    // ����Դ״̬ ����rStatus + number
			temp = pcb.delResource(pcb.getResourcesOwnNum(rcb.getRid()), rcb);
			rcb.releaseR(temp);

			// ������ִ�н����޹�
		    // ��� RCB �������в�Ϊ��, �����������ײ������������Դ�� req С�ڵ��ڿ�����Դ���� u����������������̣������������
			while ((rcb.waitingListEmpty() == false) && (rcb.isWaitingList() == true))
			{
				rcb.requestR(rcb.getWaitingListFirstNum());  // ��ȥ����������Դ
				tempPID = rcb.getWaitingListFirstPID();
				tempNUM = rcb.getWaitingListFirstNum();
				tempPCB = this.findProcessbyID(tempPID);
				PCB changePCB = (tempPCB);  // �ҵ�RCB ��Դ�����ȴ����� ��һ������

				rcb.delWaitingList();   // ����Դ�������������Ƴ� ��һ������

				changePCB.changeReady();
				changePCB.changeREADYLIST();
				// �� rcb ���뵽 changePCB ռ����Դ�б���
				changePCB.addResourse(tempNUM, rcb);         // ��rcb����pcb��ռ����Դ�б�

				// ���� changePCB ���������� 
				// �������ȼ�����ռʽ���Ȳ��ԣ���˵��н��̻����Դʱ����Ҫ�鿴��ǰ�����ȼ���������е���
				switch (changePCB.getPriority())
				{
				case 0: // INIT update
					this.delBlockList(changePCB);
					initReadyList.add(changePCB);
					// �����ж�
					break;

				case 1: // USER 
					// �����ж�
					if (userReadyList.size() == 0)
					{
						this.delBlockList(changePCB);
						userReadyList.add(changePCB);				
						runningProcess = changePCB;  // �����ȼ���ռ����
						changePCB.changeRunning();
					}
					else
					{
						this.delBlockList(changePCB);
						userReadyList.add(changePCB);
					}
					break;

				case 2: // SYSTEM 
					// �����ж�
					if (systemReadyList.size() == 0)
					{
						this.delBlockList(changePCB);
						systemReadyList.add(changePCB);
						runningProcess = changePCB;  // �����ȼ���ռ����
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
	 * ɾ�� userReadyList ָ��������
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
	 * ɾ�� systemReadyList ָ��������
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
	 * ɾ�� �����̵�ָ��������
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
	 * ������Դ
	 * @param rName
	 * @param number
	 * @return
	 */
	public int requestResources(String rName,int number)
	{
		//����Ƿ��д���Դ,û�о��˳�
		if(this.checkResourcesName(rName)==false)
		{
			return 2;
		}
		//�Ƿ񳬹���Դ����
		if(this.checkResourcesInitnum(rName,number))
		{
			return 3;
		}
		// ���� rName �ҵ���Ӧ RCB��
		RCB rcb = this.findResourcesByName(rName);
		
		if (rcb.getNum()>=number)//ʣ����Դ�㹻
		{
			rcb.requestR(number);//ʣ����Դ��һ
			runningProcess.addResourse(number, rcb);//��rcb���뵽pcbռ����Դ�б���
			return 1;
		}
		else//��Դ����������
		{
			runningProcess.changeBLOCKED();// �������� ����״̬
			runningProcess.changeBLOCKLIST();// ���̼��� �����б�
			rcb.addWaitingList(number, runningProcess);// ���� RCB ��Դ�����ȴ�����
			
			switch(runningProcess.getPriority())
			{
			case 0:
				initReadyList.remove(0);//ɾ���������еĵ�һ��Ԫ��
				blockList.add(runningProcess);
				System.out.println("BUG:init��������,�������!" );
				System.exit(0);
			case 1:
				userReadyList.remove(0);
				blockList.add(runningProcess);
				System.out.println( "[warnning]���� " + runningProcess.getPname() + " ����!" );
				// �����ж�
				if (userReadyList.size() != 0)
				{
					runningProcess = userReadyList.get(0);   // ����ִ�н���ָ�� ָ��������е�һ��
					runningProcess.changeRunning();
				}
				else
				{
					runningProcess = initReadyList.get(0);   // ����ִ�н���ָ�� ָ��������е�һ��
					runningProcess.changeRunning();
				}
				break;
			case 2:
				systemReadyList.remove(0);
				blockList.add(runningProcess);
				System.out.println( "[warnning]���� " + runningProcess.getPname() + " ����!" );
				// �����ж�
				if (systemReadyList.size() != 0)
				{
					runningProcess = systemReadyList.get(0);   // ����ִ�н���ָ�� ָ��������е�һ��
					runningProcess.changeRunning();
				}
				else
				{
					runningProcess = userReadyList.get(0);   // ����ִ�н���ָ�� ָ��������е�һ��
					runningProcess.changeRunning();
				}
				break;

			default:
				break;
			}
			// ������̵���
			System.out.println( "[warnning]�л����� " + runningProcess.getPname() + " ִ��!" );
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
	 * �����Դ���Ƿ���ڣ�������ڷ���true
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
	 * ��������Ƿ񳬹�����Դ����,����Ϊ��
	 * @param rName rcb������
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
	 * ɾ����������ָ��������
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
