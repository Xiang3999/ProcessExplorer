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
