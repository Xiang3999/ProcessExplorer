package PCB;

public class waiting {
	int BlockPID;           // �����Ľ���ID
	int BlockNum;           // ��������Դ����
	PCB pcb;             // ָ��PCB��

	waiting(int BlockPID, int BlockNum, PCB* pcb)
	{
		this.BlockPID = BlockPID;
		this.BlockNum = BlockNum;
		this.pcb = pcb;
	}
}
