package PCB;

import java.util.*;

public class processManage {
	private PCB runningProcess;                // ����ִ�н���ָ��
	private Vector<PCB> processTable = new Vector<>();          // ���̱�
	private int allocation_pid;                 // ����id����(����)

	private Vector<RCB> resourcesTable = new Vector<>();        // ��Դ�б�

	// �����������̶���
	private List<PCB> initReadyList=new ArrayList<>();
	private List<PCB> userReadyList=new ArrayList<>();
	private List<PCB> systemReadyList=new ArrayList<>();

	// ��������
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

		return 1;
	}
	// ��������
	public int destoryProcess(String pName)
	{
		//�鿴ɾ�������Ƿ��ڽ��̱���
		if(this.checkpName(pName)==false)
			return 2;
		PCB deldata=this.findProcessbyName(pName);
		delChildProcess(deldata);
		return 1;
	}
	public void Schedule()
	{
		// ����2������
		if (systemReadyList.size() != 0)
		{
			systemReadyList.remove(0);                // �Ƴ�system �����б��һ��
			systemReadyList.add(runningProcess);  // ����������������ĩβ
			runningProcess.changeReady();
			runningProcess.changeREADYLIST();
			runningProcess = systemReadyList.get(0);   // ����ִ�н���ָ�� ָ��������е�һ��
			runningProcess.changeRunning();
		}
		else  // ��û��2�����̵�, ֻ��1����������
		{
			userReadyList.remove(0);                  // �Ƴ�user �����б��һ��
			userReadyList.add(runningProcess);    // ����������������ĩβ
			runningProcess.changeReady();
			runningProcess.changeREADYLIST();
			runningProcess = userReadyList.get(0);     // ����ִ�н���ָ�� ָ��������е�һ��
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
		//����
		if(pcb==runningProcess)// ����ִ��̬���� ��һ������̬
		{
			if(systemReadyList.size()==1)// ����߼�system�������̶��о�ʣ��ִ��̬�����Լ�һ�� ����
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
		else if (pcb.getType() == "READY")  // ����
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
		}  // ǰ���Ѿ������� ����̬
		System.out.println( "���� " + pcb.getPname() + "�ѳ���!" );
		// �ͷ� PCB �ռ�
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
		while (pcb.getResourcesEmpty() == true)  // ��Ϊ��
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
	public int releaseResources(String rName,int number)
	{
		int operand = 0;  // ������
		int tempPID = -1;
		int tempNUM = 0;
		
		PCB tempPCB;
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
		// release
		
		// ����Դ rcb �Ӵӵ�ǰ���� Resourcesռ����Դ�б����Ƴ�
		// ����Դ״̬ ����rStatus + number
		operand = runningProcess.delResource(number, rcb);

		if (operand == 0)  // �ͷ���Դ������Ч
		{
			return 4;
		} 
		else if (operand == -1) // �ý����޸���Դ
		{
			return 5;
		}
		else
		{
			rcb.releaseR(operand);  // rStatus + number
		}
		// ������ִ�н����޹�
		// ����������в�Ϊ��, �����������ײ������������Դ�� req С�ڵ��ڿ�����Դ���� u����������������̣������������
		while((rcb.waitingListEmpty()==false)&&(rcb.isWaitingList()==true))
		{
			rcb.requestR(rcb.getWaitingListFirstNum());//����������Դ����
			tempPID=rcb.getWaitingListFirstPID();
			tempNUM=rcb.getWaitingListFirstNum();
			tempPCB=this.findProcessbyID(tempPID);
			PCB changePCB=tempPCB;
			rcb.delWaitingList(); // ����Դ�������������Ƴ� ��һ������
			changePCB.changeReady();
			changePCB.changeREADYLIST();
			changePCB.addResourse(tempNUM, rcb);
			// ���� changePCB ���������� 
			// �������ȼ�����ռʽ���Ȳ��ԣ���˵��н��̻����Դʱ����Ҫ�鿴��ǰ�����ȼ���������е���
			switch(changePCB.getPriority())
			{
			case 0:
				this.delBlockList(changePCB);
				initReadyList.add(changePCB);
				System.out.println("���� " + changePCB.getPname() + " ����!" );
				break;
			case 1:
				if(userReadyList.size()==0)
				{
					this.delBlockList(changePCB);
					userReadyList.add(changePCB);
					System.out.println("[warnning]���� " + changePCB.getPname() + " ����!");
					runningProcess = changePCB;  // �����ȼ���ռ����
					changePCB.changeRunning();
					System.out.println("[warnning]�����ȼ����� " + runningProcess.getPname() + " ��ռ" );
				}
				else
				{
					this.delBlockList(changePCB);
					userReadyList.add(changePCB);
					System.out.println("[warnning]���� " + changePCB.getPname() + " ����!");
				}
			case 2:
				if(systemReadyList.size()==0)
				{
					this.delBlockList(changePCB);
					systemReadyList.add(changePCB);
					System.out.println("[warnning]���� " + changePCB.getPname() + " ����!");
					runningProcess = changePCB;  // �����ȼ���ռ����
					changePCB.changeRunning();
					System.out.println("[warnning]�����ȼ����� " + runningProcess.getPname() + " ��ռ" );
				}
				else
				{
					this.delBlockList(changePCB);
					systemReadyList.add(changePCB);
					System.out.println("[warnning]���� " + changePCB.getPname() + " ����!");
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
	 * �����Դ���Ƿ���ڣ�������ڷ���true
	 * @param name
	 * @return
	 */
	public  Boolean checkResourcesName(String name)
	{
		for (RCB data:resourcesTable)
		{
			//�ַ����еȵ�ʱ��ʹ��equals(),��Ϊ==���ж����õĵ�ַ
			if(name.equals(data.getname()))
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
	 * չʾ�����б�
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
	 * չʾ��Դ�б�
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
